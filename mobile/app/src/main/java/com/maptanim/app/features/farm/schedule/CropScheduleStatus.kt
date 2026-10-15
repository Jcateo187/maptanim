package com.maptanim.app.features.farm.schedule

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.maptanim.app.domain.model.ManagementStage
import com.maptanim.app.features.farm.components.BackyardCropAgroData
import com.maptanim.app.features.farm.components.getCropHeroDrawable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * Single source of truth for "has this vegetable's schedule started?".
 * Every Vegetables Check Up entry point (Home, Growing, Canvas overlay, Farm Hub, Calendar)
 * resolves the same status so the lock/unlock rule is identical everywhere.
 */
sealed interface CropScheduleStatus {
    /** No planting date set, or the schedule was never started. */
    object NoSchedule : CropScheduleStatus

    /** Planting date is in the future. */
    data class Upcoming(val startDate: LocalDate, val daysUntil: Int) : CropScheduleStatus

    /** Planted today or on a previous day and not yet at harvest age. */
    data class Active(
        val plantedDate: LocalDate,
        val dayNumber: Int,
        val stage: ManagementStage,
        val totalDays: Int
    ) : CropScheduleStatus

    /** Reached (or passed) the expected days-to-harvest. */
    data class HarvestWindow(
        val plantedDate: LocalDate,
        val dayNumber: Int,
        val totalDays: Int
    ) : CropScheduleStatus

    /** Check Up is only meaningful once the plant is actually in the ground. */
    val canCheckUp: Boolean get() = this is Active || this is HarvestWindow

    /** Stage to record on a check-up log, or null when locked. */
    val stageOrNull: ManagementStage?
        get() = when (this) {
            is Active -> stage
            is HarvestWindow -> ManagementStage.HARVEST
            else -> null
        }

    val dayNumberOrNull: Int?
        get() = when (this) {
            is Active -> dayNumber
            is HarvestWindow -> dayNumber
            else -> null
        }
}

fun parsePlantedDate(raw: String?): LocalDate? = try {
    if (raw.isNullOrBlank()) null else LocalDate.parse(raw.trim().take(10))
} catch (_: Exception) {
    null
}

/** Day 0 = planting day. Never negative. */
fun dayNumberOn(date: LocalDate, plantedDate: LocalDate): Int =
    ChronoUnit.DAYS.between(plantedDate, date).toInt().coerceAtLeast(0)

/** Time-based management stage for a given day, scaled to each vegetable's days-to-harvest. */
fun stageForDay(dayNumber: Int, totalDays: Int): ManagementStage {
    if (dayNumber <= 0) return ManagementStage.PLANTING
    val ratio = dayNumber.toFloat() / totalDays.coerceAtLeast(1).toFloat()
    return when {
        ratio < 0.20f -> ManagementStage.EARLY_GROWTH
        ratio < 0.50f -> ManagementStage.VEGETATIVE_GROWTH
        ratio < 1.00f -> ManagementStage.FLOWERING_FRUIT_DEVELOPMENT
        else -> ManagementStage.HARVEST
    }
}

/**
 * Resolves the schedule status for one vegetable.
 * @param isMonitoringStarted false when the user only placed the crop on the canvas without starting its calendar.
 */
fun resolveScheduleStatus(
    plantedDate: String?,
    cropName: String,
    varietyName: String? = null,
    isMonitoringStarted: Boolean = true,
    today: LocalDate = LocalDate.now()
): CropScheduleStatus {
    val planted = parsePlantedDate(plantedDate) ?: return CropScheduleStatus.NoSchedule
    if (planted.isAfter(today)) {
        return CropScheduleStatus.Upcoming(
            startDate = planted,
            daysUntil = ChronoUnit.DAYS.between(today, planted).toInt()
        )
    }
    if (!isMonitoringStarted) return CropScheduleStatus.NoSchedule

    val matchedVariety = if (!varietyName.isNullOrBlank()) {
        com.maptanim.app.data.local.seeds.CropVarietySeeds.list.firstOrNull {
            it.varietyName.equals(varietyName, ignoreCase = true) ||
            it.localNamePh?.contains(varietyName, ignoreCase = true) == true
        }
    } else {
        com.maptanim.app.data.local.seeds.CropVarietySeeds.list.firstOrNull {
            it.cropName.equals(cropName, ignoreCase = true)
        }
    }

    val totalDays = matchedVariety?.growthDurationDays
        ?: BackyardCropAgroData.getProfile(cropName).baseDaysToHarvest
    val day = dayNumberOn(today, planted)
    return if (day >= totalDays) {
        CropScheduleStatus.HarvestWindow(planted, day, totalDays)
    } else {
        CropScheduleStatus.Active(planted, day, stageForDay(day, totalDays), totalDays)
    }
}

private val LockForest = Color(0xFF1B5E20)
private val LockAmber = Color(0xFFE65100)
private val LockText = Color(0xFF28302A)
private val LockMuted = Color(0xFF4C564D)

/**
 * Shown instead of the Check Up form when the schedule has not started.
 * Explains why and offers exactly one way forward.
 */
@Composable
fun CheckUpLockedDialog(
    cropName: String,
    status: CropScheduleStatus,
    onOpenSchedule: (() -> Unit)?,
    onDismiss: () -> Unit
) {
    val dateFmt = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH)
    val (title, message, actionLabel) = when (status) {
        is CropScheduleStatus.Upcoming -> Triple(
            "Hindi pa nagsisimula ang $cropName",
            "Magsisimula ang schedule sa ${status.startDate.format(dateFmt)} " +
                "(${status.daysUntil} araw pa). Mabubuksan ang Vegetables Check Up sa araw ng pagtatanim.",
            "Tingnan ang Calendar"
        )
        else -> Triple(
            "Wala pang schedule ang $cropName",
            "I-set muna ang petsa ng pagtatanim sa Calendar. Kapag nakatanim na ang gulay " +
                "(ngayon o sa mga nakaraang araw), mabubuksan ang Vegetables Check Up.",
            "I-set ang Petsa ng Pagtatanim"
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Image(
                        painter = painterResource(id = getCropHeroDrawable(cropName)),
                        contentDescription = cropName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (status is CropScheduleStatus.Upcoming) LockAmber else LockForest),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }

                Text(
                    text = "Naka-lock ang Vegetables Check Up",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (status is CropScheduleStatus.Upcoming) LockAmber else LockForest,
                    letterSpacing = 0.3.sp
                )
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = LockText,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = message,
                    fontSize = 13.sp,
                    color = LockMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (onOpenSchedule != null) {
                    Button(
                        onClick = onOpenSchedule,
                        colors = ButtonDefaults.buttonColors(containerColor = LockForest),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(actionLabel, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Isara", color = LockMuted, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

/** Small reusable pill for cards: shows lock reason in one line. */
@Composable
fun ScheduleStatusPill(status: CropScheduleStatus, modifier: Modifier = Modifier) {
    val (label, color) = when (status) {
        is CropScheduleStatus.NoSchedule -> "Wala pang schedule" to Color(0xFF546E7A)
        is CropScheduleStatus.Upcoming -> "Magsisimula sa ${status.daysUntil} araw" to LockAmber
        is CropScheduleStatus.Active -> "Araw ${status.dayNumber} • ${status.stage.label}" to LockForest
        is CropScheduleStatus.HarvestWindow -> "Handa nang anihin • Araw ${status.dayNumber}" to Color(0xFFB8860B)
    }
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.10f),
        border = BorderStroke(0.6.dp, color.copy(alpha = 0.45f)),
        modifier = modifier
    ) {
        Text(
            text = label,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}
