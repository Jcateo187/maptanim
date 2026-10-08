package com.maptanim.app.features.farm.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.maptanim.app.domain.model.CropGrowthStage
import com.maptanim.app.features.farm.components.BackyardCropAgroData
import com.maptanim.app.features.farm.renderer.canvas.CropSvgRenderer
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

// ═══════════════════════════════════════════════════════════════════════════════
// Eye-Friendly, Anti-Glare High-Contrast Palette
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
private val AmberAlert = Color(0xFFE65100)
private val BlueAccent = Color(0xFF1565C0)
private val PurpleAccent = Color(0xFF6A1B9A)
private val GoldHarvest = Color(0xFFF9A825)
private val RedAlert = Color(0xFFC62828)

// ═══════════════════════════════════════════════════════════════════════════════
// Agenda Task Models & View Modes
// ═══════════════════════════════════════════════════════════════════════════════
enum class AgendaTaskType(
    val tagalogLabel: String,
    val icon: ImageVector,
    val color: Color,
    val dotColor: Color
) {
    WATERING("Pagdidilig", Icons.Default.WaterDrop, LushGreen, LushGreen),
    FERTILIZER("Pagpapataba", Icons.Default.Science, AmberAlert, AmberAlert),
    PEST_SCOUT("Pagsusuri sa Peste", Icons.Default.BugReport, RedAlert, RedAlert),
    PRUNING_TRELLIS("Pruning at Trellis", Icons.Default.ContentCut, PurpleAccent, PurpleAccent),
    HARVEST("Pag-aani", Icons.Default.Eco, GoldHarvest, GoldHarvest),
    MILESTONE("Mahalagang Yugto", Icons.Default.Stars, BlueAccent, BlueAccent)
}

enum class BottomScheduleViewMode(val label: String, val icon: ImageVector) {
    SPLIT_AGENDA("Daily Agenda", Icons.Default.Checklist),
    TIMELINE_LIST("Timeline Schedule", Icons.Default.Timeline)
}

data class StageBannerInfo(
    val stageKey: String,
    val stageTitle: String,
    val dayRange: String,
    val description: String,
    val gradientColors: List<Color>,
    val icon: ImageVector
)

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

/**
 * CropScheduleCalendarScreen — Full-screen dedicated view for:
 * 1. Top Section: Interactive Monthly Calendar with date task dots
 * 2. Middle Section: Growth Timeline (Seedling -> Vegetative -> Flowering -> Fruiting -> Harvest)
 * 3. Bottom Section: Agenda Schedule of Events (Date, Time, and Day Task on that time)
 * 4. Header Right Side: Reschedule Icon to re-open the planting date selector overlay.
 */
@Composable
fun CropScheduleCalendarScreen(
    cropName: String,
    varietyName: String? = null,
    gardenLabel: String = "Garden",
    plotId: String,
    zoneId: String? = null,
    plantedDateStr: String,
    onDismiss: () -> Unit,
    onReschedule: () -> Unit
) {
    val plantedDate = remember(plantedDateStr) {
        try {
            LocalDate.parse(plantedDateStr.take(10))
        } catch (_: Exception) {
            LocalDate.now()
        }
    }

    val maturityProfile = remember(cropName) { BackyardCropAgroData.getProfile(cropName) }
    val totalDays = maturityProfile.baseDaysToHarvest
    val harvestDate = remember(plantedDate, totalDays) { plantedDate.plusDays(totalDays.toLong()) }

    val today = remember { LocalDate.now() }
    val daysPlanted = remember(plantedDate, today) {
        ChronoUnit.DAYS.between(plantedDate, today).toInt().coerceAtLeast(0)
    }
    val daysRemaining = remember(totalDays, daysPlanted) {
        (totalDays - daysPlanted).coerceAtLeast(0)
    }
    val stageProgressRatio = remember(daysPlanted, totalDays) {
        (daysPlanted.toFloat() / totalDays.toFloat()).coerceIn(0f, 1f)
    }

    // Active calendar navigation
    var viewingMonth by remember { mutableStateOf(YearMonth.from(today.coerceAtLeast(plantedDate))) }
    var selectedCalendarDate by remember { mutableStateOf<LocalDate?>(today) }
    var showAllTasksInAgenda by remember { mutableStateOf(false) }
    var bottomViewMode by remember { mutableStateOf(BottomScheduleViewMode.SPLIT_AGENDA) }
    var selectedCategoryFilter by remember { mutableStateOf<AgendaTaskType?>(null) }
    var showAddCustomTaskDialog by remember { mutableStateOf(false) }
    var customTasks by remember { mutableStateOf(listOf<AgendaScheduleItem>()) }

    // Generate chronological agenda events
    val baseScheduleItems = remember(plantedDate, cropName, maturityProfile) {
        generateCropAgendaSchedule(cropName, plantedDate, maturityProfile)
    }

    val scheduleItems = remember(baseScheduleItems, customTasks) {
        (baseScheduleItems + customTasks).sortedBy { it.date }
    }

    // State for task completion toggles
    var completedTaskIds by remember { mutableStateOf(setOf<String>()) }

    // Group items by date for monthly calendar indicator dots
    val tasksByDate = remember(scheduleItems) {
        scheduleItems.groupBy { it.date }
    }

    // List of dates for Daily Agenda View (Teams Style, Screenshot 1)
    val agendaDateList = remember(showAllTasksInAgenda, selectedCalendarDate, plantedDate, harvestDate) {
        val targetDate = selectedCalendarDate
        if (!showAllTasksInAgenda && targetDate != null) {
            // 7-day contiguous window from selected calendar date (matching Screenshot 1)
            (0L..6L).map { targetDate.plusDays(it) }
        } else {
            // Full planting duration from plantedDate to harvestDate + 7 days
            val dates = mutableListOf<LocalDate>()
            var cur = plantedDate
            val limit = harvestDate.plusDays(7)
            while (!cur.isAfter(limit)) {
                dates.add(cur)
                cur = cur.plusDays(1)
            }
            dates
        }
    }

    val stageInfoList = remember {
        listOf(
            StageBannerInfo(
                stageKey = "Pagtatanim",
                stageTitle = "Pagtatanim at Paghahanda",
                dayRange = "Araw 0 • Paghahanda ng Kama",
                description = "Buhaghag na lupa, compost, at starter drench",
                gradientColors = listOf(Color(0xFF1B3B2B), Color(0xFF2E5D43), Color(0xFF437A5B)),
                icon = Icons.Default.Spa
            ),
            StageBannerInfo(
                stageKey = "Punla",
                stageTitle = "Yugto 1: Punla / Seedling",
                dayRange = "Araw 1 – 14 • Pagsibol at Ugat",
                description = "Pagsibol ng unang dahon at regular na pagdidilig",
                gradientColors = listOf(Color(0xFF143D1A), Color(0xFF235A29), Color(0xFF387A3E)),
                icon = Icons.Default.Yard
            ),
            StageBannerInfo(
                stageKey = "Paglago",
                stageTitle = "Yugto 2: Paglago / Vegetative",
                dayRange = "Araw 15 – 34 • Paglaki ng Tangkay",
                description = "Trellis, mulching, at foliar feeding (FPJ)",
                gradientColors = listOf(Color(0xFF0F3818), Color(0xFF1C5225), Color(0xFF2E6B39)),
                icon = Icons.Default.Agriculture
            ),
            StageBannerInfo(
                stageKey = "Pamumulaklak",
                stageTitle = "Yugto 3: Pamumulaklak / Flowering",
                dayRange = "Araw 35 – 49 • Pagsulpot ng Bulaklak",
                description = "Calcium boost, maingat na pagdidilig sa ugat",
                gradientColors = listOf(Color(0xFF2C1947), Color(0xFF4A2B6F), Color(0xFF70459E)),
                icon = Icons.Default.LocalFlorist
            ),
            StageBannerInfo(
                stageKey = "Pamumunga",
                stageTitle = "Yugto 4: Pamumunga at Pagkahinog",
                dayRange = "Araw 50 – 74 • Paglaki ng Bunga",
                description = "Potassium feed (FFJ) at suporta sa mabigat na sanga",
                gradientColors = listOf(Color(0xFF4A2010), Color(0xFF7B3B18), Color(0xFFA65825)),
                icon = Icons.Default.Eco
            ),
            StageBannerInfo(
                stageKey = "Pag-aani",
                stageTitle = "Yugto 5: Panahon ng Pag-aani",
                dayRange = "Araw 75+ • Regular na Pagpitas",
                description = "Pitasin sa umaga gamit ang malinis na gunting",
                gradientColors = listOf(Color(0xFF1C2833), Color(0xFF2C3E50), Color(0xFFD35400)),
                icon = Icons.Default.Stars
            )
        )
    }

    // Filter agenda items based on user selection
    val displayedAgendaItems = remember(scheduleItems, selectedCalendarDate, showAllTasksInAgenda) {
        if (showAllTasksInAgenda || selectedCalendarDate == null) {
            scheduleItems.sortedBy { it.date }
        } else {
            scheduleItems.filter { it.date == selectedCalendarDate }.sortedBy { it.timeStr }
        }
    }

    val filteredAgendaItems = remember(displayedAgendaItems, selectedCategoryFilter) {
        if (selectedCategoryFilter == null) displayedAgendaItems
        else displayedAgendaItems.filter { it.type == selectedCategoryFilter }
    }

    val dateMonthFormatter = remember {
        DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.forLanguageTag("fil-PH"))
    }
    val fallbackDateFormatter = remember {
        DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH)
    }
    val formattedPlantedDate = remember(plantedDate) {
        try { plantedDate.format(dateMonthFormatter) } catch (_: Exception) { plantedDate.format(fallbackDateFormatter) }
    }
    val formattedHarvestDate = remember(harvestDate) {
        try { harvestDate.format(dateMonthFormatter) } catch (_: Exception) { harvestDate.format(fallbackDateFormatter) }
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
                .background(SurfaceBg)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ═══════════════════════════════════════════════════════════════
                // Top App Bar: Back Arrow, Title, and Right Reschedule Icon Button
                // ═══════════════════════════════════════════════════════════════
                Surface(
                    color = CardBg,
                    border = BorderStroke(1.dp, CardBorderColor),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            IconButton(onClick = onDismiss, modifier = Modifier.size(38.dp)) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Bumalik sa Canvas",
                                    tint = ForestGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Surface(
                                shape = CircleShape,
                                color = LightGreenBg,
                                border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.35f)),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Canvas(modifier = Modifier.size(26.dp)) {
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
                                    text = "$cropName Kalendaryo",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DeepBlack,
                                    letterSpacing = (-0.2).sp
                                )
                                Text(
                                    text = "$gardenLabel • Itinanim: $formattedPlantedDate",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MutedText
                                )
                            }
                        }

                        // Right side: RESCHEDULE ICON BUTTON (as requested by user)
                        FilledTonalButton(
                            onClick = onReschedule,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = LightGreenBg,
                                contentColor = ForestGreen
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EditCalendar,
                                    contentDescription = "I-reschedule",
                                    modifier = Modifier.size(17.dp),
                                    tint = ForestGreen
                                )
                                Text(
                                    text = "Reschedule",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreen
                                )
                            }
                        }
                    }
                }

                // ═══════════════════════════════════════════════════════════════
                // Main Scrollable Body:
                // 1. Monthly Calendar
                // 2. Growth Timeline
                // 3. Agenda Schedule of Events
                // ═══════════════════════════════════════════════════════════════
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    contentPadding = PaddingValues(vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // ── 1. MONTHLY CALENDAR CARD ──────────────────────────────
                    item {
                        MonthlyCalendarCard(
                            viewingMonth = viewingMonth,
                            onMonthChanged = { viewingMonth = it },
                            selectedDate = selectedCalendarDate,
                            onDateSelected = { date ->
                                selectedCalendarDate = date
                                showAllTasksInAgenda = false
                            },
                            tasksByDate = tasksByDate,
                            plantedDate = plantedDate,
                            harvestDate = harvestDate
                        )
                    }

                    // ── 2. GROWTH TIMELINE CARD ───────────────────────────────
                    item {
                        GrowthTimelineCard(
                            cropName = cropName,
                            plantedDate = plantedDate,
                            totalDays = totalDays,
                            daysPlanted = daysPlanted,
                            daysRemaining = daysRemaining,
                            progressRatio = stageProgressRatio,
                            formattedPlantedDate = formattedPlantedDate,
                            formattedHarvestDate = formattedHarvestDate,
                            maturityProfile = maturityProfile
                        )
                    }

                    // ── 3. BOTTOM SPLIT PANE AGENDA VIEW / TIMELINE LIST ──────
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Section Header and Mode Switcher
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f, fill = false)) {
                                    Text(
                                        text = if (bottomViewMode == BottomScheduleViewMode.SPLIT_AGENDA)
                                            "Daily Agenda View"
                                        else
                                            "Timeline Schedule View",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = DeepBlack
                                    )
                                    Text(
                                        text = if (showAllTasksInAgenda)
                                            "Lahat (${scheduleItems.size}) ng gawain mula pagtanim hanggang ani"
                                        else if (selectedCalendarDate != null)
                                            "Gawain para sa linggo ng ${selectedCalendarDate?.format(fallbackDateFormatter)}"
                                        else "Pumili ng araw sa kalendaryo sa itaas",
                                        fontSize = 11.sp,
                                        color = MutedText
                                    )
                                }

                                // Segmented Control Toggle between Daily Agenda (Teams) and Timeline Schedule (Google Calendar)
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = SurfaceBg,
                                    border = BorderStroke(1.dp, CardBorderColor)
                                ) {
                                    Row(modifier = Modifier.padding(3.dp)) {
                                        listOf(
                                            BottomScheduleViewMode.SPLIT_AGENDA,
                                            BottomScheduleViewMode.TIMELINE_LIST
                                        ).forEach { mode ->
                                            val isSelected = bottomViewMode == mode
                                            Surface(
                                                onClick = { bottomViewMode = mode },
                                                shape = RoundedCornerShape(7.dp),
                                                color = if (isSelected) ForestGreen else Color.Transparent,
                                                modifier = Modifier.height(30.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 9.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = mode.icon,
                                                        contentDescription = null,
                                                        tint = if (isSelected) Color.White else Charcoal,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                    Text(
                                                        text = mode.label,
                                                        fontSize = 11.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                        color = if (isSelected) Color.White else DeepBlack
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Secondary Control Row: Date Scope Toggle + Category Filter Chips
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FilterChip(
                                    selected = showAllTasksInAgenda,
                                    onClick = { showAllTasksInAgenda = !showAllTasksInAgenda },
                                    label = {
                                        Text(
                                            text = if (showAllTasksInAgenda) "Lahat (${scheduleItems.size})" else "Napiling Araw",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (showAllTasksInAgenda) Icons.Default.CalendarToday else Icons.Default.Checklist,
                                            contentDescription = null,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ForestGreen,
                                        selectedLabelColor = Color.White,
                                        selectedLeadingIconColor = Color.White
                                    )
                                )

                                FilterChip(
                                    selected = selectedCategoryFilter == null,
                                    onClick = { selectedCategoryFilter = null },
                                    label = { Text("Lahat ng Kategorya", fontSize = 10.5.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = LightGreenBg,
                                        selectedLabelColor = ForestGreen
                                    )
                                )

                                listOf(
                                    AgendaTaskType.WATERING,
                                    AgendaTaskType.FERTILIZER,
                                    AgendaTaskType.PEST_SCOUT,
                                    AgendaTaskType.PRUNING_TRELLIS,
                                    AgendaTaskType.HARVEST,
                                    AgendaTaskType.MILESTONE
                                ).forEach { type ->
                                    FilterChip(
                                        selected = selectedCategoryFilter == type,
                                        onClick = {
                                            selectedCategoryFilter = if (selectedCategoryFilter == type) null else type
                                        },
                                        label = { Text(type.tagalogLabel, fontSize = 10.5.sp) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = type.icon,
                                                contentDescription = null,
                                                tint = if (selectedCategoryFilter == type) Color.White else type.color,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = type.color,
                                            selectedLabelColor = Color.White,
                                            selectedLeadingIconColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // ── ITEMS LIST ────────────────────────────────────────────
                    if (scheduleItems.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = CardBg,
                                border = BorderStroke(1.dp, CardBorderColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EventAvailable,
                                        contentDescription = null,
                                        tint = LushGreen,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Text(
                                        text = "Walang Aktibidad sa Napiling Filter",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = DeepBlack
                                    )
                                    Text(
                                        text = "I-tap ang 'Lahat' o pumili ng ibang kategorya upang makita ang buong talaan ng gawain.",
                                        fontSize = 11.5.sp,
                                        textAlign = TextAlign.Center,
                                        color = MutedText
                                    )
                                    TextButton(onClick = {
                                        showAllTasksInAgenda = true
                                        selectedCategoryFilter = null
                                    }) {
                                        Text("Ipakita ang Lahat ng Iskedyul", fontWeight = FontWeight.Bold, color = ForestGreen, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    } else if (bottomViewMode == BottomScheduleViewMode.SPLIT_AGENDA) {
                        // ── MODE 1: DAILY AGENDA VIEW (Microsoft Teams Calendar Style - Screenshot 1)
                        agendaDateList.forEach { curDate ->
                            val tasksOnDate = (tasksByDate[curDate] ?: emptyList())
                                .filter { selectedCategoryFilter == null || it.type == selectedCategoryFilter }

                            item(key = "agenda-date-hdr-$curDate") {
                                TeamsStyleDateHeader(
                                    date = curDate,
                                    today = today,
                                    plantedDate = plantedDate
                                )
                            }

                            if (tasksOnDate.isEmpty()) {
                                item(key = "agenda-no-events-$curDate") {
                                    TeamsStyleNoEventsRow()
                                }
                            } else {
                                items(tasksOnDate, key = { it.id }) { item ->
                                    val isChecked = item.id in completedTaskIds
                                    TeamsStyleAgendaTaskItem(
                                        item = item,
                                        isChecked = isChecked,
                                        onToggleCheck = {
                                            completedTaskIds = if (isChecked) completedTaskIds - item.id else completedTaskIds + item.id
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        // ── MODE 2: TIMELINE SCHEDULE VIEW (Google Calendar Schedule Style - Screenshot 2)
                        stageInfoList.forEach { stageInfo ->
                            val stageTasks = scheduleItems
                                .filter { it.stageName == stageInfo.stageKey && (selectedCategoryFilter == null || it.type == selectedCategoryFilter) }

                            if (stageTasks.isNotEmpty()) {
                                item(key = "scenic-banner-${stageInfo.stageKey}") {
                                    GoogleCalendarStageScenicBanner(stageInfo = stageInfo)
                                }

                                val groupedByDate = stageTasks.groupBy { it.date }.toList()
                                groupedByDate.forEachIndexed { dateIdx, (date, tasksOnDate) ->
                                    tasksOnDate.forEach { item ->
                                        item(key = "gcal-item-${item.id}") {
                                            val isChecked = item.id in completedTaskIds
                                            GoogleCalendarScheduleDateRow(
                                                item = item,
                                                isChecked = isChecked,
                                                onToggleCheck = {
                                                    completedTaskIds = if (isChecked) completedTaskIds - item.id else completedTaskIds + item.id
                                                }
                                            )
                                        }
                                    }

                                    // Intervening gap days in stage (Matches "Sep 10 - 16, 2028" in Screenshot 2)
                                    if (dateIdx < groupedByDate.size - 1) {
                                        val nextDate = groupedByDate[dateIdx + 1].first
                                        val gapDays = ChronoUnit.DAYS.between(date, nextDate)
                                        if (gapDays >= 3) {
                                            item(key = "gcal-interval-$date-$nextDate") {
                                                val startDayStr = date.plusDays(1).dayOfMonth
                                                val endDayStr = nextDate.minusDays(1).dayOfMonth
                                                val monthName = date.month.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("fil-PH"))
                                                    .ifBlank { date.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH) }
                                                    .replaceFirstChar { it.uppercase() }
                                                GoogleCalendarIntervalRow(
                                                    label = "$monthName $startDayStr – $endDayStr • Patuloy na pag-aalaga at pagpapanatili ng moisture"
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ═══════════════════════════════════════════════════════════════
            // Floating Action Button (+) matching Screenshot 1 & 2
            // ═══════════════════════════════════════════════════════════════
            FloatingActionButton(
                onClick = { showAddCustomTaskDialog = true },
                containerColor = ForestGreen,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .size(54.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Magdagdag ng Gawain",
                    modifier = Modifier.size(26.dp)
                )
            }

            if (showAddCustomTaskDialog) {
                AddCustomFarmTaskDialog(
                    initialDate = selectedCalendarDate ?: today,
                    onDismiss = { showAddCustomTaskDialog = false },
                    onAddTask = { newTask ->
                        customTasks = customTasks + newTask
                    }
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Component 1: Monthly Calendar Card
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun MonthlyCalendarCard(
    viewingMonth: YearMonth,
    onMonthChanged: (YearMonth) -> Unit,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    tasksByDate: Map<LocalDate, List<AgendaScheduleItem>>,
    plantedDate: LocalDate,
    harvestDate: LocalDate
) {
    val today = remember { LocalDate.now() }
    var isWeekMode by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CardBg,
        border = BorderStroke(1.2.dp, CardBorderColor),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Month Switcher Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { onMonthChanged(viewingMonth.minusMonths(1)) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous Month",
                        tint = ForestGreen
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val monthName = viewingMonth.month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("fil-PH"))
                        .ifBlank { viewingMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) }
                        .replaceFirstChar { it.uppercase() }

                    Text(
                        text = "$monthName ${viewingMonth.year}",
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlack
                    )

                    Surface(
                        onClick = {
                            onMonthChanged(YearMonth.now())
                            onDateSelected(today)
                        },
                        shape = RoundedCornerShape(6.dp),
                        color = LightGreenBg,
                        border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "Ngayon",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Week / Month View Switcher Button (Screenshot 1 & 2 affordance)
                    Surface(
                        onClick = { isWeekMode = !isWeekMode },
                        shape = RoundedCornerShape(6.dp),
                        color = SurfaceBg,
                        border = BorderStroke(1.dp, CardBorderColor)
                    ) {
                        Text(
                            text = if (isWeekMode) "Buwan" else "Linggo",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBlack,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                IconButton(
                    onClick = { onMonthChanged(viewingMonth.plusMonths(1)) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next Month",
                        tint = ForestGreen
                    )
                }
            }

            // 7 Weekday Headers (Sun to Sat)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                val dayNames = listOf("Lin", "Lun", "Mar", "Miy", "Huw", "Biy", "Sab")
                dayNames.forEach { name ->
                    Text(
                        text = name,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (name == "Lin" || name == "Sab") AmberAlert else MutedText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(36.dp)
                    )
                }
            }

            // Days Grid
            val firstDayOfMonth = viewingMonth.atDay(1)
            val daysInMonth = viewingMonth.lengthOfMonth()
            val startDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 0 = Sunday

            val totalCells = ((startDayOfWeek + daysInMonth + 6) / 7) * 7
            val totalWeeks = totalCells / 7

            val targetDate = when {
                selectedDate != null && selectedDate.year == viewingMonth.year && selectedDate.month == viewingMonth.month -> selectedDate
                today.year == viewingMonth.year && today.month == viewingMonth.month -> today
                else -> viewingMonth.atDay(1)
            }
            val targetWeekRow = ((startDayOfWeek + targetDate.dayOfMonth - 1) / 7).coerceIn(0, totalWeeks - 1)
            val weeksToRender = if (isWeekMode) listOf(targetWeekRow) else (0 until totalWeeks).toList()

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                for (week in weeksToRender) {
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
                                val isPlantedDay = cellDate == plantedDate
                                val isHarvestDay = cellDate == harvestDate
                                val tasksOnDay = tasksByDate[cellDate] ?: emptyList()

                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when {
                                                isSelected -> ForestGreen
                                                isToday -> ForestGreen.copy(alpha = 0.12f)
                                                tasksOnDay.isNotEmpty() -> SurfaceBg
                                                else -> Color.Transparent
                                            }
                                        )
                                        .clickable { onDateSelected(cellDate) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = dayNumber.toString(),
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected || isToday || isHarvestDay) FontWeight.Bold else FontWeight.Medium,
                                            color = when {
                                                isSelected -> Color.White
                                                isHarvestDay -> GoldHarvest
                                                isToday -> ForestGreen
                                                else -> DeepBlack
                                            }
                                        )

                                        // Task Indicator Dots
                                        if (tasksOnDay.isNotEmpty() && !isSelected) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                                modifier = Modifier.padding(top = 1.dp)
                                            ) {
                                                val distinctTypes = tasksOnDay.map { it.type }.distinct().take(3)
                                                distinctTypes.forEach { type ->
                                                    Box(
                                                        modifier = Modifier
                                                            .size(4.dp)
                                                            .clip(CircleShape)
                                                            .background(type.dotColor)
                                                    )
                                                }
                                            }
                                        } else if (isHarvestDay && !isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .clip(CircleShape)
                                                    .background(GoldHarvest)
                                            )
                                        }
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.size(38.dp))
                            }
                        }
                    }
                }
            }

            // Drag Handle to toggle Week Strip vs Full Month Grid (Matches Screenshot 1)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { isWeekMode = !isWeekMode }
                    .padding(vertical = 3.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MutedText.copy(alpha = 0.35f))
                )
            }

            // Legend of Calendar Indicators
            HorizontalDivider(color = CardBorderColor.copy(alpha = 0.6f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendDot(color = LushGreen, label = "Pagdidilig")
                LegendDot(color = AmberAlert, label = "Pataba")
                LegendDot(color = RedAlert, label = "Peste")
                LegendDot(color = PurpleAccent, label = "Trellis")
                LegendDot(color = GoldHarvest, label = "Pag-aani")
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(text = label, fontSize = 9.5.sp, color = MutedText, fontWeight = FontWeight.Medium)
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Component 2: Growth Timeline Card (Seedling -> Vegetative -> Flowering -> Fruiting -> Harvest)
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun GrowthTimelineCard(
    cropName: String,
    plantedDate: LocalDate,
    totalDays: Int,
    daysPlanted: Int,
    daysRemaining: Int,
    progressRatio: Float,
    formattedPlantedDate: String,
    formattedHarvestDate: String,
    maturityProfile: com.maptanim.app.features.farm.components.CropMaturityProfile
) {
    val currentStage = remember(progressRatio) {
        when {
            progressRatio < 0.20f -> CropGrowthStage.SEEDLING
            progressRatio < 0.48f -> CropGrowthStage.VEGETATIVE
            progressRatio < 0.70f -> CropGrowthStage.FLOWERING
            progressRatio < 0.90f -> CropGrowthStage.RIPENING
            else -> CropGrowthStage.HARVEST
        }
    }

    val stageTagalogTitle = remember(currentStage) {
        when (currentStage) {
            CropGrowthStage.GERMINATION -> "Pagsibol (Germination)"
            CropGrowthStage.SEEDLING -> "Punla / Pagsibol (Seedling)"
            CropGrowthStage.VEGETATIVE -> "Paglago ng Dahon at Sangay (Vegetative)"
            CropGrowthStage.FLOWERING -> "Pamumulaklak (Flowering)"
            CropGrowthStage.RIPENING -> "Pamumunga at Pagkahinog (Fruiting / Ripening)"
            CropGrowthStage.HARVEST -> "Handa nang Anihin (Harvest)"
        }
    }

    val stageAdvice = remember(currentStage) {
        when (currentStage) {
            CropGrowthStage.SEEDLING, CropGrowthStage.GERMINATION ->
                "Siguraduhing protektado ang punla sa malalakas na ulan. Magdilig sa umaga lamang upang hindi mababad ang ugat."
            CropGrowthStage.VEGETATIVE ->
                "Maglagay ng rice straw mulch at patatagin ang bamboo stake. Lagyan ng organikong pataba (vermicast o FPJ)."
            CropGrowthStage.FLOWERING ->
                "Iwasang basain ang bulaklak tuwing nagdidilig upang hindi malaglag ang pollen. Panatilihing katamtaman ang basa sa ugat."
            CropGrowthStage.RIPENING ->
                "Palakasin ang alalay ng trellis sa mga sangang may mabibigat na bunga. Mag-spray ng calcium phosphate / FFJ."
            CropGrowthStage.HARVEST ->
                "Pitasin sa umaga habang sariwa gamit ang malinis na gunting. Iwanan ang peduncle upang tumagal ang shelf-life."
        }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CardBg,
        border = BorderStroke(1.2.dp, CardBorderColor),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: Title and Countdown Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timeline,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Yugto ng Paglago (Growth Timeline)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlack
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (daysRemaining == 0) LightGreenBg else Color(0xFFFFF3E0),
                    border = BorderStroke(1.dp, if (daysRemaining == 0) LushGreen else AmberAlert)
                ) {
                    Text(
                        text = if (daysRemaining == 0) "Handa nang Anihen!" else "$daysRemaining araw bago mag-ani",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (daysRemaining == 0) ForestGreen else AmberAlert,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Visual Progress Bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LinearProgressIndicator(
                    progress = { progressRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(9.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = ForestGreen,
                    trackColor = SurfaceBg
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Punla (Day 1)", fontSize = 9.sp, color = MutedText)
                    Text("Paglago", fontSize = 9.sp, color = MutedText)
                    Text("Bulaklak", fontSize = 9.sp, color = MutedText)
                    Text("Bunga", fontSize = 9.sp, color = MutedText)
                    Text("Ani (Day $totalDays)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                }
            }

            // Current Stage Highlight Box
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SurfaceBg,
                border = BorderStroke(1.dp, CardBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Kasalukuyan: $stageTagalogTitle",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        Text(
                            text = "Araw $daysPlanted ng $totalDays (${(progressRatio * 100).toInt()}%)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepBlack
                        )
                    }

                    Text(
                        text = stageAdvice,
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp,
                        color = Charcoal
                    )
                }
            }

            // Dates Summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Event, contentDescription = null, tint = MutedText, modifier = Modifier.size(13.dp))
                    Text("Itinanim: $formattedPlantedDate", fontSize = 10.sp, color = MutedText)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = GoldHarvest, modifier = Modifier.size(13.dp))
                    Text("Tinatayang Ani: $formattedHarvestDate", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DeepBlack)
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Component 3: Agenda Schedule of Events Card (Date, Time, Day Task on that time)
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun AgendaScheduleItemCard(
    item: AgendaScheduleItem,
    isChecked: Boolean,
    onToggleCheck: () -> Unit
) {
    val dateDisplayFormatter = remember {
        DateTimeFormatter.ofPattern("MMM d (EEE)", Locale.forLanguageTag("fil-PH"))
    }
    val fallbackDisplay = remember {
        DateTimeFormatter.ofPattern("MMM d (EEE)", Locale.ENGLISH)
    }
    val formattedDate = remember(item.date) {
        try { item.date.format(dateDisplayFormatter) } catch (_: Exception) { item.date.format(fallbackDisplay) }
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isChecked) Color(0xFFF9FAF8) else CardBg,
        border = BorderStroke(1.dp, if (isChecked) CardBorderColor.copy(alpha = 0.6f) else item.type.color.copy(alpha = 0.35f)),
        shadowElevation = if (isChecked) 0.dp else 1.5.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Checkbox for Task Completion
            IconButton(
                onClick = onToggleCheck,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (isChecked) "Tapos na" else "Hindi pa tapos",
                    tint = if (isChecked) ForestGreen else item.type.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Task Details Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                // Header Row: Category Badge + Date & Time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = item.type.color.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = item.type.tagalogLabel,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = item.type.color,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MutedText,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "${item.timeStr} • $formattedDate",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MutedText
                        )
                    }
                }

                // Day Task Title on that time
                Text(
                    text = item.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isChecked) MutedText else DeepBlack,
                    textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
                )

                // Detailed Tagalog Agricultural Instruction
                Text(
                    text = item.tagalogInstruction,
                    fontSize = 11.5.sp,
                    lineHeight = 15.sp,
                    color = if (isChecked) MutedText.copy(alpha = 0.8f) else Charcoal
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Agronomic Schedule Generator Engine (Philippine Climate & BPI Standards)
// ═══════════════════════════════════════════════════════════════════════════════
private fun generateCropAgendaSchedule(
    cropName: String,
    plantedDate: LocalDate,
    profile: com.maptanim.app.features.farm.components.CropMaturityProfile
): List<AgendaScheduleItem> {
    val items = mutableListOf<AgendaScheduleItem>()
    val totalDays = profile.baseDaysToHarvest

    // Day 0: Planting / Transplanting Day
    items.add(
        AgendaScheduleItem(
            id = "task-0-morning",
            date = plantedDate,
            timeStr = "6:30 AM",
            dayNumber = 0,
            type = AgendaTaskType.WATERING,
            title = "Paghahanda ng Lupa at Kamang Taniman",
            tagalogInstruction = "Basain ang kama o paso nang banayad. Siguraduhing buhaghag at may sapat na drainage ang lupa.",
            stageName = "Pagtatanim",
            isKeyMilestone = true
        )
    )
    items.add(
        AgendaScheduleItem(
            id = "task-0-afternoon",
            date = plantedDate,
            timeStr = "4:30 PM",
            dayNumber = 0,
            type = AgendaTaskType.MILESTONE,
            title = "Pagtatanim / Paglilipat-tanim sa Hapon",
            tagalogInstruction = "Itanim ang punla sa dakong hapon upang maiwasan ang init ng araw. Diligan ng banayad sa ugat (Starter Drench).",
            stageName = "Pagtatanim",
            isKeyMilestone = true
        )
    )

    // Routine Morning Watering: Every 2 days
    for (day in 1..totalDays + 20 step 2) {
        val taskDate = plantedDate.plusDays(day.toLong())
        items.add(
            AgendaScheduleItem(
                id = "water-$day",
                date = taskDate,
                timeStr = "6:30 AM",
                dayNumber = day,
                type = AgendaTaskType.WATERING,
                title = "Masinsing Pagdidilig sa Umaga",
                tagalogInstruction = "Idilig sa base ng lupa bago mag-init ang araw. Huwag basain ang mga dahon upang maiwasan ang fungal blight.",
                stageName = if (day < 14) "Punla" else if (day < 35) "Paglago" else "Pamumunga"
            )
        )
    }

    // Routine Pest & Leaf Scout: Every 3 days starting Day 3
    for (day in 3..totalDays + 20 step 3) {
        val taskDate = plantedDate.plusDays(day.toLong())
        items.add(
            AgendaScheduleItem(
                id = "pest-$day",
                date = taskDate,
                timeStr = "7:30 AM",
                dayNumber = day,
                type = AgendaTaskType.PEST_SCOUT,
                title = "Pagsusuri sa Dahon at Peste sa Umaga",
                tagalogInstruction = "Suriin ang ilalim ng dahon habang malamig pa ang panahon. Maghanap ng aphids, whitefly, o uod ng dahon.",
                stageName = if (day < 14) "Punla" else if (day < 35) "Paglago" else "Pamumunga"
            )
        )
    }

    // Key Agronomic Milestones & Interventions
    // Day 7: First Organic Fertilizer
    items.add(
        AgendaScheduleItem(
            id = "fert-7",
            date = plantedDate.plusDays(7),
            timeStr = "4:30 PM",
            dayNumber = 7,
            type = AgendaTaskType.FERTILIZER,
            title = "Unang Pagpapataba: Basal / Vermicast",
            tagalogInstruction = "Maglagay ng 1 dakot ng vermicast o organikong compost sa paligid ng drip line (5cm layo sa tangkay).",
            stageName = "Punla"
        )
    )

    // Day 14: Mulching & Soil Aeration
    items.add(
        AgendaScheduleItem(
            id = "mulch-14",
            date = plantedDate.plusDays(14),
            timeStr = "5:00 PM",
            dayNumber = 14,
            type = AgendaTaskType.MILESTONE,
            title = "Paglalagay ng Rice Straw Mulch",
            tagalogInstruction = "Lagyan ng 2 pulgadang tuyong dayami o damo sa ibabaw ng lupa upang mapanatili ang moisture at pigilan ang damo.",
            stageName = "Paglago"
        )
    )

    // Day 21: Trellis & Staking Setup
    items.add(
        AgendaScheduleItem(
            id = "trellis-21",
            date = plantedDate.plusDays(21),
            timeStr = "4:30 PM",
            dayNumber = 21,
            type = AgendaTaskType.PRUNING_TRELLIS,
            title = "Pagtatayo ng Bamboo Stakes / Trellis",
            tagalogInstruction = "Ibaon ang tukod na kawayan at maluwag na itali ang tangkay gamit ang malambot na panali sa hugis-figure-8.",
            stageName = "Paglago"
        )
    )

    // Day 28: Vegetative Booster (FPJ / Fish Amino)
    items.add(
        AgendaScheduleItem(
            id = "fert-28",
            date = plantedDate.plusDays(28),
            timeStr = "4:30 PM",
            dayNumber = 28,
            type = AgendaTaskType.FERTILIZER,
            title = "Pag-spray ng FPJ / Organikong Foliar Feed",
            tagalogInstruction = "Magtimpla ng 2 kutsarang Fermented Plant Juice sa 1 litrong tubig at i-spray sa dahon sa dakong hapon.",
            stageName = "Paglago"
        )
    )

    // Day 35: Sucker Pruning & Airflow Grooming
    items.add(
        AgendaScheduleItem(
            id = "prune-35",
            date = plantedDate.plusDays(35),
            timeStr = "7:30 AM",
            dayNumber = 35,
            type = AgendaTaskType.PRUNING_TRELLIS,
            title = "Pagpupungos ng Mabababang Dahon at Suckers",
            tagalogInstruction = "Alisin ang mga dahong sumasayad sa lupa at maliliit na suckers sa kili-kili ng sangay para sa magandang sirkulasyon ng hangin.",
            stageName = "Paglago"
        )
    )

    // Day 45: Flowering Boost & Blossom Drop Prevention
    items.add(
        AgendaScheduleItem(
            id = "flower-45",
            date = plantedDate.plusDays(45),
            timeStr = "6:30 AM",
            dayNumber = 45,
            type = AgendaTaskType.MILESTONE,
            title = "Pagsusuri sa Unang Pamumulaklak",
            tagalogInstruction = "Tiyaking pantay ang pagdidilig upang maiwasan ang blossom-end rot. Mag-spray ng eggshell vinegar tea (calcium).",
            stageName = "Pamumulaklak"
        )
    )

    // Day 55: Fruit Support & Potassium Feed (FFJ)
    items.add(
        AgendaScheduleItem(
            id = "fruit-55",
            date = plantedDate.plusDays(55),
            timeStr = "5:00 PM",
            dayNumber = 55,
            type = AgendaTaskType.FERTILIZER,
            title = "Pagsuporta sa Bunga at Pagpapataba ng Potassium",
            tagalogInstruction = "Lagyan ng tali ang mabibigat na kumpol ng bunga. Mag-spray ng Fermented Fruit Juice (FFJ) para sa tamis at laki ng bunga.",
            stageName = "Pamumunga"
        )
    )

    // First Harvest Day ($totalDays)
    val harvestDate = plantedDate.plusDays(totalDays.toLong())
    items.add(
        AgendaScheduleItem(
            id = "harvest-first",
            date = harvestDate,
            timeStr = "6:30 AM",
            dayNumber = totalDays,
            type = AgendaTaskType.HARVEST,
            title = "UNANG ARAW NG PAG-AANI (First Harvest Window)",
            tagalogInstruction = "Pitasin ang mga hinog na bunga sa umaga gamit ang malinis na gunting. Iwanan ang calyx/tangkay para tumagal ang sariwa.",
            stageName = "Pag-aani",
            isKeyMilestone = true
        )
    )

    // Multi-pick intervals if applicable
    if (profile.isMultiPick) {
        val interval = profile.pickingIntervalDays.coerceAtLeast(2)
        for (extraDay in (totalDays + interval)..(totalDays + profile.harvestLongevityDays.coerceAtMost(30)) step interval) {
            val pickDate = plantedDate.plusDays(extraDay.toLong())
            items.add(
                AgendaScheduleItem(
                    id = "pick-$extraDay",
                    date = pickDate,
                    timeStr = "6:30 AM",
                    dayNumber = extraDay,
                    type = AgendaTaskType.HARVEST,
                    title = "Regular na Pagpitas / Harvest Interval",
                    tagalogInstruction = "Pitasin ang mga sumunod na hinog na bunga. Alisin ang anumang may sira o inuod na prutas upang hindi mahawa ang iba.",
                    stageName = "Pag-aani"
                )
            )
        }
    }

    return items.sortedBy { it.date }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Microsoft Teams Style Daily Agenda Helpers (Screenshot 1)
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun TeamsStyleDateHeader(
    date: LocalDate,
    today: LocalDate,
    plantedDate: LocalDate
) {
    val monthDayStr = remember(date) {
        val monthName = date.month.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("fil-PH"))
            .ifBlank { date.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH) }
            .replaceFirstChar { it.uppercase() }
        "$monthName ${date.dayOfMonth}"
    }

    val relativeTag = remember(date, today) {
        when (date) {
            today -> "Ngayon"
            today.plusDays(1) -> "Bukas"
            today.minusDays(1) -> "Kahapon"
            else -> date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("fil-PH"))
                .ifBlank { date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH) }
                .replaceFirstChar { it.uppercase() }
        }
    }

    val dayDiff = ChronoUnit.DAYS.between(plantedDate, date).toInt()
    val dayTag = if (dayDiff >= 0) " (Araw $dayDiff)" else ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = monthDayStr,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = DeepBlack
        )
        Text(
            text = "$relativeTag$dayTag",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (date == today) ForestGreen else MutedText
        )
    }
}

@Composable
private fun TeamsStyleNoEventsRow() {
    Text(
        text = "Walang nakatakdang gawain",
        fontSize = 11.5.sp,
        color = MutedText.copy(alpha = 0.65f),
        modifier = Modifier.padding(start = 8.dp, top = 2.dp, bottom = 6.dp)
    )
}

@Composable
private fun TeamsStyleAgendaTaskItem(
    item: AgendaScheduleItem,
    isChecked: Boolean,
    onToggleCheck: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isChecked) Color(0xFFF9FAF8) else CardBg,
        border = BorderStroke(1.dp, if (isChecked) CardBorderColor.copy(alpha = 0.5f) else CardBorderColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable { onToggleCheck() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Time & Subtitle Column on Left (Teams style)
            Column(
                modifier = Modifier.width(62.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.timeStr,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isChecked) MutedText else DeepBlack
                )
                Text(
                    text = item.type.tagalogLabel,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isChecked) MutedText.copy(alpha = 0.7f) else item.type.color,
                    maxLines = 1
                )
            }

            // Vertical Colored Stripe (Teams style thick indicator bar)
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(38.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isChecked) CardBorderColor else item.type.color)
            )

            // Task Details Body
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = item.title,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isChecked) MutedText else DeepBlack,
                    textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
                )
                Text(
                    text = item.tagalogInstruction,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    color = if (isChecked) MutedText.copy(alpha = 0.75f) else Charcoal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Checkbox for task completion
            IconButton(
                onClick = onToggleCheck,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (isChecked) "Tapos na" else "Gawin",
                    tint = if (isChecked) ForestGreen else CardBorderColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Google Calendar Schedule View Style Helpers (Screenshot 2)
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun GoogleCalendarStageScenicBanner(
    stageInfo: StageBannerInfo
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
                .background(Brush.horizontalGradient(stageInfo.gradientColors))
                .padding(14.dp)
        ) {
            // Silhouette & Motif Icon in Background
            Icon(
                imageVector = stageInfo.icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.18f),
                modifier = Modifier
                    .size(68.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = 8.dp, y = 4.dp)
            )

            Column(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.White.copy(alpha = 0.22f)
                ) {
                    Text(
                        text = stageInfo.dayRange,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = stageInfo.stageTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = (-0.2).sp
                )

                Text(
                    text = stageInfo.description,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun GoogleCalendarScheduleDateRow(
    item: AgendaScheduleItem,
    isChecked: Boolean,
    onToggleCheck: () -> Unit
) {
    val dayOfWeekName = remember(item.date) {
        item.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("fil-PH"))
            .ifBlank { item.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH) }
            .replaceFirstChar { it.uppercase() }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Left Column (Google Calendar Style Date: "Sun" / "3")
        Column(
            modifier = Modifier.width(44.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dayOfWeekName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MutedText
            )
            Text(
                text = item.date.dayOfMonth.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DeepBlack
            )
            Text(
                text = "Araw ${item.dayNumber}",
                fontSize = 9.sp,
                color = ForestGreen,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Right Column: Full-width colored event pill card (Screenshot 2 style)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isChecked) Color(0xFFF9FAF8) else item.type.color.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, if (isChecked) CardBorderColor else item.type.color.copy(alpha = 0.35f)),
            modifier = Modifier
                .weight(1f)
                .clickable { onToggleCheck() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = item.type.color
                        ) {
                            Text(
                                text = item.timeStr,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }

                        Text(
                            text = item.type.tagalogLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = item.type.color
                        )
                    }

                    Text(
                        text = item.title,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isChecked) MutedText else DeepBlack,
                        textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
                    )

                    Text(
                        text = item.tagalogInstruction,
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        color = if (isChecked) MutedText.copy(alpha = 0.7f) else Charcoal,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onToggleCheck,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (isChecked) ForestGreen else item.type.color,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GoogleCalendarIntervalRow(label: String) {
    Text(
        text = label,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = MutedText.copy(alpha = 0.7f),
        modifier = Modifier.padding(start = 54.dp, top = 2.dp, bottom = 4.dp)
    )
}

// ═══════════════════════════════════════════════════════════════════════════════
// Add Custom Task Dialog (FAB (+) action)
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun AddCustomFarmTaskDialog(
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onAddTask: (AgendaScheduleItem) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var instruction by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(AgendaTaskType.WATERING) }
    var timeStr by remember { mutableStateOf("7:00 AM") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = null, tint = ForestGreen)
                Text("Magdagdag ng Paalala / Tala", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Pangalan ng Gawain") },
                    placeholder = { Text("Hal. Maglagay ng mulch, mag-spray ng CalPhos...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = instruction,
                    onValueChange = { instruction = it },
                    label = { Text("Detalye o Tagubilin (Opsiyonal)") },
                    placeholder = { Text("Hal. 2 kutsara bawat litrong tubig...") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Kategorya:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepBlack)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AgendaTaskType.values().forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type.tagalogLabel, fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = type.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val newItem = AgendaScheduleItem(
                            id = "custom-${System.currentTimeMillis()}",
                            date = initialDate,
                            timeStr = timeStr,
                            dayNumber = 0,
                            type = selectedType,
                            title = title.trim(),
                            tagalogInstruction = instruction.ifBlank { "Personal na paalala para sa pananim." },
                            stageName = "Gawain"
                        )
                        onAddTask(newItem)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                enabled = title.isNotBlank()
            ) {
                Text("I-save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Kanselahin", color = MutedText)
            }
        }
    )
}
