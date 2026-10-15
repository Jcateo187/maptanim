package com.maptanim.app.features.farm.schedule

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Yard
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import java.time.LocalDate

import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Timeline

// Theme Palette Tokens
private val LushGreen = Color(0xFF2E7D32)
private val ForestGreen = Color(0xFF1B5E20)
private val AmberAlert = Color(0xFFE65100)
private val RedAlert = Color(0xFFC62828)
private val PurpleAccent = Color(0xFF6A1B9A)
private val GoldHarvest = Color(0xFFF57F17)

/** Navigation tabs for the calendar screen */
enum class CalendarScreenTab(val label: String, val icon: ImageVector) {
    CALENDAR("Calendar", Icons.Default.CalendarMonth),
    GROWTH_TIMELINE("Growth Timeline", Icons.Default.Timeline),
    TODAYS_TASK("Today's Task", Icons.Default.Checklist)
}

/** Segmented toggle for bottom split agenda vs check-up history */
enum class BottomScheduleViewMode(val label: String, val icon: ImageVector) {
    SPLIT_AGENDA("Daily Agenda", Icons.Default.Checklist),
    CHECKUP_HISTORY("Check Up history", Icons.Default.History)
}

/** Types of agricultural tasks displayed in the agenda */
enum class AgendaTaskType(
    val tagalogLabel: String,
    val icon: ImageVector,
    val color: Color,
    val dotColor: Color = color
) {
    WATERING("Pagdidilig", Icons.Default.InvertColors, LushGreen, LushGreen),
    FERTILIZER("Pagpapataba", Icons.Default.Spa, AmberAlert, AmberAlert),
    PEST_SCOUT("Pagsusuri sa Peste", Icons.Default.BugReport, RedAlert, RedAlert),
    PRUNING_TRELLIS("Pruning at Trellis", Icons.Default.Agriculture, PurpleAccent, PurpleAccent),
    HARVEST("Pag-aani", Icons.Default.Stars, GoldHarvest, GoldHarvest),
    MILESTONE("Mahalagang Yugto", Icons.Default.Eco, ForestGreen, ForestGreen)
}

/** Agronomic category classification for crops */
enum class CropAgronomicCategory {
    LEAFY_BRASSICA,
    TUBER_ROOT,
    ALLIUM,
    CLIMBING_VINE,
    SOLANACEOUS_FRUIT,
    GRAIN_CORN
}

/** High-level visual stage info card */
data class StageBannerInfo(
    val stageKey: String,
    val stageTitle: String,
    val dayRange: String,
    val description: String,
    val gradientColors: List<Color>,
    val icon: ImageVector,
    val startDay: Int = 0,
    val endDay: Int = 0
)

/** Single agenda task event item */
data class AgendaScheduleItem(
    val id: String,
    val date: LocalDate,
    val timeStr: String,
    val dayNumber: Int,
    val type: AgendaTaskType,
    val title: String,
    val tagalogInstruction: String,
    val stageName: String,
    val isKeyMilestone: Boolean = false,
    var isDone: Boolean = false
)
