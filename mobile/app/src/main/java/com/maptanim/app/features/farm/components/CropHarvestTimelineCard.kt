package com.maptanim.app.features.farm.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.CropGrowthStage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val LightSurface = Color(0xFFF9FAF8)
private val CardBorderColor = Color(0xFFE0E0E0)
private val TimelineOrange = Color(0xFFF57C00)

/**
 * Agronomic metadata for backyard vegetable crops (DA-BPI standards).
 */
data class CropMaturityProfile(
    val baseDaysToHarvest: Int,
    val pickingIntervalDays: Int,
    val harvestLongevityDays: Int,
    val yieldKgPerPlant: Float,
    val isMultiPick: Boolean
)

object BackyardCropAgroData {
    fun getProfile(cropName: String): CropMaturityProfile {
        val lower = cropName.lowercase().trim()
        return when {
            lower.contains("tomato") || lower.contains("kamatis") ->
                CropMaturityProfile(75, 3, 45, 2.0f, true)
            lower.contains("eggplant") || lower.contains("talong") ->
                CropMaturityProfile(85, 4, 90, 1.5f, true)
            lower.contains("chili") || lower.contains("sili") ->
                CropMaturityProfile(85, 6, 150, 0.5f, true)
            lower.contains("okra") ->
                CropMaturityProfile(55, 2, 90, 0.8f, true)
            lower.contains("pechay") || lower.contains("bok choy") ->
                CropMaturityProfile(30, 0, 0, 0.15f, false)
            lower.contains("lettuce") || lower.contains("litsugas") ->
                CropMaturityProfile(45, 0, 0, 0.20f, false)
            lower.contains("kangkong") ->
                CropMaturityProfile(28, 12, 120, 0.20f, true)
            lower.contains("cucumber") || lower.contains("pipino") ->
                CropMaturityProfile(50, 2, 30, 1.5f, true)
            lower.contains("sitaw") || lower.contains("yardlong") || lower.contains("beans") ->
                CropMaturityProfile(55, 3, 40, 0.6f, true)
            lower.contains("corn") || lower.contains("mais") ->
                CropMaturityProfile(75, 0, 0, 0.4f, false)
            else ->
                CropMaturityProfile(60, 3, 30, 1.0f, true)
        }
    }
}

/**
 * CropHarvestTimelineCard — Visual harvest timeline, starting date, growth stage progress bar,
 * expected harvest window countdown, and picking calendar.
 */
@Composable
fun CropHarvestTimelineCard(
    cropName: String,
    plantedDateMillis: Long,
    plantCount: Int,
    modifier: Modifier = Modifier
) {
    val profile = remember(cropName) { BackyardCropAgroData.getProfile(cropName) }
    val now = System.currentTimeMillis()
    val daysPlanted = remember(plantedDateMillis, now) {
        val diff = (now - plantedDateMillis).coerceAtLeast(0)
        (diff / (1000 * 60 * 60 * 24)).toInt().coerceIn(0, 200)
    }

    val totalDays = profile.baseDaysToHarvest
    val daysRemaining = (totalDays - daysPlanted).coerceAtLeast(0)
    val progress = (daysPlanted.toFloat() / totalDays.toFloat()).coerceIn(0f, 1f)

    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    val plantedDateStr = remember(plantedDateMillis) {
        dateFormat.format(Date(if (plantedDateMillis > 0) plantedDateMillis else now))
    }

    val expectedHarvestDateStr = remember(plantedDateMillis, totalDays) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = if (plantedDateMillis > 0) plantedDateMillis else now
        cal.add(Calendar.DAY_OF_YEAR, totalDays)
        dateFormat.format(cal.time)
    }

    val totalEstimatedYieldKg = remember(profile, plantCount) {
        val count = plantCount.coerceAtLeast(1)
        count * profile.yieldKgPerPlant
    }

    val currentStage = remember(progress) {
        when {
            progress < 0.20f -> CropGrowthStage.GERMINATION
            progress < 0.40f -> CropGrowthStage.SEEDLING
            progress < 0.70f -> CropGrowthStage.VEGETATIVE
            progress < 0.90f -> CropGrowthStage.FLOWERING
            else -> CropGrowthStage.HARVEST
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Card Header ─────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE8F5E9),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Eco, contentDescription = null, tint = LushGreen, modifier = Modifier.size(16.dp))
                        }
                    }
                    Column {
                        Text(
                            text = "$cropName Harvest Timeline",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = DeepBlack
                        )
                        Text(
                            text = "Stage: ${currentStage.name.lowercase().replaceFirstChar { it.uppercase() }} • Day $daysPlanted of $totalDays",
                            fontSize = 10.5.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (daysRemaining == 0) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                    border = BorderStroke(1.dp, if (daysRemaining == 0) Color(0xFFA5D6A7) else Color(0xFFFFB74D))
                ) {
                    Text(
                        text = if (daysRemaining == 0) "Harvest Ready" else "$daysRemaining days left",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (daysRemaining == 0) LushGreen else TimelineOrange,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            // ── Visual Milestone Progress Bar ───────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = LushGreen,
                    trackColor = Color(0xFFE0E0E0),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Sown / Planted", fontSize = 9.sp, color = Color(0xFF888888))
                    Text("Vegetative", fontSize = 9.sp, color = Color(0xFF888888))
                    Text("Flowering", fontSize = 9.sp, color = Color(0xFF888888))
                    Text("Harvest", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = LushGreen)
                }
            }

            // ── Starting Point & Expected Calendar Dates ────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Starting Point
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF666666), modifier = Modifier.size(12.dp))
                            Text("STARTING DATE", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF777777))
                        }
                        Text(plantedDateStr, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = DeepBlack)
                    }
                }

                // Expected First Harvest
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = LushGreen, modifier = Modifier.size(12.dp))
                            Text("EXPECTED HARVEST", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = LushGreen)
                        }
                        Text(expectedHarvestDateStr, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DeepBlack)
                    }
                }
            }

            // ── Picking Interval & Yield Forecast ───────────────────────────
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF1F8E9),
                border = BorderStroke(1.dp, LushGreen.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (profile.isMultiPick)
                                "Pick every ${profile.pickingIntervalDays} days over ${profile.harvestLongevityDays}d window"
                            else
                                "Single-harvest crop: clear bed after picking",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF2E7D32)
                        )
                    }
                    Text(
                        text = "Est. ${String.format("%.1f", totalEstimatedYieldKg)} kg",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LushGreen
                    )
                }
            }
        }
    }
}
