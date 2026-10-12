package com.maptanim.app.features.farm.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.maptanim.app.features.farm.components.BackyardCropAgroData
import com.maptanim.app.features.farm.renderer.canvas.CropSvgRenderer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// ═══════════════════════════════════════════════════════════════════════════════
// Anti-Glare Eye-Friendly Palette
// ═══════════════════════════════════════════════════════════════════════════════
private val ForestGreen = Color(0xFF1B5E20)
private val LushGreen = Color(0xFF2E7D32)
private val LightGreenBg = Color(0xFFE8F5E9)
private val DeepBlack = Color(0xFF141915)
private val Charcoal = Color(0xFF28302A)
private val MutedText = Color(0xFF4C564D)
private val CardBorderColor = Color(0xFFD0D8CC)
private val CardBg = Color(0xFFFFFFFF)
private val SurfaceBg = Color(0xFFF2F5F0)
private val AmberAccent = Color(0xFFE65100)

/**
 * SetPlantingDateDialog — Floating modal overlay to choose when the crop was or will be planted.
 * Shown one-time when the user first clicks Calendar on a crop without a schedule,
 * or when they click the header Reschedule icon on the Calendar Screen.
 */
@Composable
fun SetPlantingDateDialog(
    cropName: String,
    varietyName: String? = null,
    gardenLabel: String = "Garden",
    initialDate: String? = null,
    isReschedule: Boolean = false,
    onDismiss: () -> Unit,
    onSaveDate: (String) -> Unit
) {
    val initialLocalDate = remember(initialDate) {
        if (!initialDate.isNullOrBlank()) {
            try {
                LocalDate.parse(initialDate.take(10))
            } catch (_: Exception) {
                LocalDate.now()
            }
        } else {
            LocalDate.now()
        }
    }

    var selectedDate by remember { mutableStateOf(initialLocalDate) }
    var viewingMonth by remember { mutableStateOf(YearMonth.from(initialLocalDate)) }

    val maturityProfile = remember(cropName) { BackyardCropAgroData.getProfile(cropName) }

    val fullDateFormatter = remember {
        DateTimeFormatter.ofPattern("MMMM d, yyyy (EEEE)", Locale.forLanguageTag("fil-PH"))
    }
    val fallbackFormatter = remember {
        DateTimeFormatter.ofPattern("MMMM d, yyyy (EEEE)", Locale.ENGLISH)
    }

    val formattedDateText = remember(selectedDate) {
        try {
            selectedDate.format(fullDateFormatter)
        } catch (_: Exception) {
            selectedDate.format(fallbackFormatter)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .padding(horizontal = 16.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(20.dp)),
                color = CardBg,
                border = BorderStroke(1.2.dp, CardBorderColor),
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // ── Header: Crop Icon, Title & Close Button ───────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = LightGreenBg,
                                border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.35f)),
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Canvas(modifier = Modifier.size(32.dp)) {
                                        CropSvgRenderer.drawCropSvg(
                                            drawScope = this,
                                            cropName = cropName,
                                            center = Offset(size.width / 2f, size.height / 2f),
                                            sizePx = size.width * 0.95f
                                        )
                                    }
                                }
                            }

                            Column {
                                Text(
                                    text = if (isReschedule) "I-reschedule ang Pagtatanim" else "Itakda ang Petsa ng Tanim",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepBlack
                                )
                                Text(
                                    text = "$cropName${if (!varietyName.isNullOrBlank()) " ($varietyName)" else ""} • $gardenLabel",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MutedText
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .background(SurfaceBg, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Isara",
                                tint = Charcoal,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = CardBorderColor.copy(alpha = 0.7f))

                    // ── Sub-heading ───────────────────────────────────────────
                    Text(
                        text = "Piliin kung kailan mo itatanim o kailan itinanim ang iyong $cropName upang makabuo ng tamang iskedyul ng pagdidilig, pagpapataba, at pag-aani.",
                        fontSize = 12.5.sp,
                        lineHeight = 17.sp,
                        color = Charcoal
                    )

                    // ── Quick Preset Chips ────────────────────────────────────
                    val today = remember { LocalDate.now() }
                    val tomorrow = remember { today.plusDays(1) }
                    val yesterday = remember { today.minusDays(1) }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickPresetChip(
                            label = "Ngayon (Today)",
                            isSelected = selectedDate == today,
                            onClick = {
                                selectedDate = today
                                viewingMonth = YearMonth.from(today)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        QuickPresetChip(
                            label = "Bukas (Tomorrow)",
                            isSelected = selectedDate == tomorrow,
                            onClick = {
                                selectedDate = tomorrow
                                viewingMonth = YearMonth.from(tomorrow)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        QuickPresetChip(
                            label = "Kahapon (Yesterday)",
                            isSelected = selectedDate == yesterday,
                            onClick = {
                                selectedDate = yesterday
                                viewingMonth = YearMonth.from(yesterday)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // ── Month Navigation & Day Grid Picker ────────────────────
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SurfaceBg,
                        border = BorderStroke(1.dp, CardBorderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Month / Year Switcher
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(
                                    onClick = { viewingMonth = viewingMonth.minusMonths(1) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                        contentDescription = "Nakaraang Buwan",
                                        tint = ForestGreen
                                    )
                                }

                                val monthName = viewingMonth.month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("fil-PH"))
                                    .ifBlank { viewingMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) }
                                    .replaceFirstChar { it.uppercase() }

                                Text(
                                    text = "$monthName ${viewingMonth.year}",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepBlack
                                )

                                IconButton(
                                    onClick = { viewingMonth = viewingMonth.plusMonths(1) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = "Susunod na Buwan",
                                        tint = ForestGreen
                                    )
                                }
                            }

                            // Day of Week Header Row (Sun to Sat)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                val dayHeaders = listOf("Lin", "Lun", "Mar", "Miy", "Huw", "Biy", "Sab")
                                dayHeaders.forEach { name ->
                                    Text(
                                        text = name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (name == "Lin" || name == "Sab") AmberAccent else MutedText,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.width(36.dp)
                                    )
                                }
                            }

                            // Days Grid
                            MonthCalendarGrid(
                                viewingMonth = viewingMonth,
                                selectedDate = selectedDate,
                                onSelectDate = { selectedDate = it }
                            )
                        }
                    }

                    // ── Active Selected Date Display ──────────────────────────
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = LightGreenBg,
                        border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EventAvailable,
                                contentDescription = null,
                                tint = ForestGreen,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = "Napiling Petsa ng Tanim:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ForestGreen
                                )
                                Text(
                                    text = formattedDateText,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DeepBlack
                                )
                            }
                        }
                    }

                    // ── Philippine Agronomic Context Card ─────────────────────
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = CardBg,
                        border = BorderStroke(1.dp, CardBorderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = AmberAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Paalala sa Paglilipat-tanim (BPI-DA)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberAccent
                                )
                            }
                            Text(
                                text = "Mas mainam ilipat-tanim sa dakong 4:00 PM - 5:30 PM upang hindi malanta sa tindi ng sikat ng araw. Tinatayang magsisimula ang pag-aani sa loob ng ${maturityProfile.baseDaysToHarvest} araw.",
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = Charcoal
                            )
                        }
                    }

                    // ── Action Buttons ────────────────────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, CardBorderColor),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Charcoal),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        ) {
                            Text("Kanselahin", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                onSaveDate(selectedDate.toString())
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ForestGreen,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .weight(1.5f)
                                .height(46.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (isReschedule) "I-save ang Pagbabago" else "I-save at Kalendaryo",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickPresetChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) ForestGreen else SurfaceBg,
        border = BorderStroke(1.dp, if (isSelected) ForestGreen else CardBorderColor),
        modifier = modifier.height(34.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else DeepBlack
            )
        }
    }
}

@Composable
private fun MonthCalendarGrid(
    viewingMonth: YearMonth,
    selectedDate: LocalDate,
    onSelectDate: (LocalDate) -> Unit
) {
    val firstDayOfMonth = viewingMonth.atDay(1)
    val daysInMonth = viewingMonth.lengthOfMonth()
    val startDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 0 = Sunday, 1 = Monday, ...

    val totalCells = ((startDayOfWeek + daysInMonth + 6) / 7) * 7
    val today = LocalDate.now()

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        for (week in 0 until (totalCells / 7)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                for (dayCol in 0..6) {
                    val cellIndex = week * 7 + dayCol
                    val dayNumber = cellIndex - startDayOfWeek + 1

                    if (dayNumber in 1..daysInMonth) {
                        val cellDate = viewingMonth.atDay(dayNumber)
                        val isSelected = cellDate == selectedDate
                        val isToday = cellDate == today

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isSelected -> ForestGreen
                                        isToday -> ForestGreen.copy(alpha = 0.15f)
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable { onSelectDate(cellDate) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayNumber.toString(),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                color = when {
                                    isSelected -> Color.White
                                    isToday -> ForestGreen
                                    else -> DeepBlack
                                }
                            )
                        }
                    } else {
                        // Empty spacer cell
                        Spacer(modifier = Modifier.size(36.dp))
                    }
                }
            }
        }
    }
}
