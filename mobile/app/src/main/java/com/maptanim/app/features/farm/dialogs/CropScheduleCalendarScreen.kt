package com.maptanim.app.features.farm.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import kotlinx.coroutines.launch
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.maptanim.app.data.local.entity.CropVarietyEntity
import com.maptanim.app.data.local.seeds.CropVarietySeeds
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.domain.model.CropGrowthStage
import com.maptanim.app.domain.model.CropLog
import com.maptanim.app.domain.model.ManagementStage
import com.maptanim.app.features.farm.components.BackyardCropAgroData
import com.maptanim.app.features.farm.dialogs.CropCareDssDialog
import com.maptanim.app.features.farm.renderer.canvas.CropSvgRenderer
import com.maptanim.app.features.farm.schedule.CheckUpRecorder
import com.maptanim.app.features.farm.schedule.CheckUpSeverity
import com.maptanim.app.features.farm.schedule.CropScheduleStatus
import com.maptanim.app.features.farm.schedule.TaskCompletionStore
import com.maptanim.app.features.farm.schedule.dayNumberOn
import com.maptanim.app.features.farm.schedule.parsePlantedDate
import com.maptanim.app.features.farm.schedule.resolveScheduleStatus
import com.maptanim.app.features.farm.schedule.AgendaScheduleItem
import com.maptanim.app.features.farm.schedule.AgendaTaskType
import com.maptanim.app.features.farm.schedule.BottomScheduleViewMode
import com.maptanim.app.features.farm.schedule.CalendarScreenTab
import com.maptanim.app.features.farm.schedule.CropAgronomicCategory
import com.maptanim.app.features.farm.schedule.StageBannerInfo
import com.maptanim.app.features.farm.schedule.buildCropSpecificStages
import com.maptanim.app.features.farm.schedule.detectCropCategory
import com.maptanim.app.features.farm.schedule.findStageName
import com.maptanim.app.features.farm.schedule.generateCropAgendaSchedule
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

// Typealiases for backward compatibility
typealias CalendarScreenTab = com.maptanim.app.features.farm.schedule.CalendarScreenTab
typealias AgendaTaskType = com.maptanim.app.features.farm.schedule.AgendaTaskType
typealias BottomScheduleViewMode = com.maptanim.app.features.farm.schedule.BottomScheduleViewMode
typealias AgendaScheduleItem = com.maptanim.app.features.farm.schedule.AgendaScheduleItem
typealias StageBannerInfo = com.maptanim.app.features.farm.schedule.StageBannerInfo
typealias CropAgronomicCategory = com.maptanim.app.features.farm.schedule.CropAgronomicCategory

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
    initialTab: CalendarScreenTab = CalendarScreenTab.CALENDAR,
    isPlanningMode: Boolean = false,
    onStartSchedule: (() -> Unit)? = null,
    onNavigateToCanvas: ((plotId: String) -> Unit)? = null,
    onDismiss: () -> Unit,
    onReschedule: () -> Unit
) {
    val today = remember { LocalDate.now() }
    val effectivePlanningMode = isPlanningMode || plantedDateStr.isBlank() || remember(plantedDateStr, today) {
        try {
            val parsed = LocalDate.parse(plantedDateStr.take(10))
            parsed.isAfter(today)
        } catch (_: Exception) {
            true
        }
    }

    val plantedDate = remember(plantedDateStr) {
        try {
            LocalDate.parse(plantedDateStr.take(10))
        } catch (_: Exception) {
            LocalDate.now()
        }
    }

    val matchedVariety = remember(cropName, varietyName) {
        if (!varietyName.isNullOrBlank()) {
            CropVarietySeeds.list.firstOrNull {
                it.varietyName.equals(varietyName, ignoreCase = true) ||
                it.localNamePh?.contains(varietyName, ignoreCase = true) == true
            }
        } else {
            CropVarietySeeds.list.firstOrNull {
                it.cropName.equals(cropName, ignoreCase = true)
            }
        }
    }

    val maturityProfile = remember(cropName) { BackyardCropAgroData.getProfile(cropName) }
    val totalDays = remember(maturityProfile, matchedVariety) {
        matchedVariety?.growthDurationDays ?: maturityProfile.baseDaysToHarvest
    }
    val harvestDate = remember(plantedDate, totalDays) { plantedDate.plusDays(totalDays.toLong()) }

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

    // Floating navigation tab & scroll synchronization
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var activeNavTab by remember { mutableStateOf(initialTab) }
    var isProgrammaticScroll by remember { mutableStateOf(false) }
    var showCheckUpDssDialog by remember { mutableStateOf(false) }
    var showPostHarvestDialog by remember { mutableStateOf(false) }

    // Scroll to initial tab on launch if requested
    LaunchedEffect(initialTab) {
        if (initialTab != CalendarScreenTab.CALENDAR) {
            val targetIndex = if (effectivePlanningMode) {
                1
            } else {
                when (initialTab) {
                    CalendarScreenTab.CALENDAR -> 0
                    CalendarScreenTab.GROWTH_TIMELINE -> 1
                    CalendarScreenTab.TODAYS_TASK -> 2
                }
            }
            lazyListState.scrollToItem(targetIndex)
        }
    }

    // Automatically follow scroll position and highlight the visible section
    LaunchedEffect(lazyListState.firstVisibleItemIndex) {
        if (!isProgrammaticScroll) {
            activeNavTab = if (effectivePlanningMode) {
                if (lazyListState.firstVisibleItemIndex == 0) CalendarScreenTab.CALENDAR
                else CalendarScreenTab.TODAYS_TASK
            } else {
                when {
                    lazyListState.firstVisibleItemIndex == 0 -> CalendarScreenTab.CALENDAR
                    lazyListState.firstVisibleItemIndex == 1 -> CalendarScreenTab.GROWTH_TIMELINE
                    else -> CalendarScreenTab.TODAYS_TASK
                }
            }
        }
    }

    // Reactive observation of check-up reports for this crop/planting
    val checkUpLogsFlow = remember(plotId, gardenLabel) {
        RepositoryProvider.cropLogRepository.observeLogsForPlanting(plotId)
    }
    val logsFromPlanting by checkUpLogsFlow.collectAsState(initial = emptyList())

    val bedLogsFlow = remember(gardenLabel) {
        RepositoryProvider.cropLogRepository.observeLogsForBed(gardenLabel)
    }
    val logsFromBed by bedLogsFlow.collectAsState(initial = emptyList())

    val checkUpLogs = remember(logsFromPlanting, logsFromBed, cropName, plotId) {
        val combined = (logsFromPlanting + logsFromBed).distinctBy { it.id }
        combined.filter {
            it.cropName.equals(cropName, ignoreCase = true) || it.cropPlantingId == plotId
        }.sortedByDescending { it.createdAt.ifBlank { it.date } }
    }

    val stageInfoList = remember(cropName, varietyName, totalDays, matchedVariety) {
        buildCropSpecificStages(
            cropName = cropName,
            varietyName = varietyName,
            totalDays = totalDays,
            variety = matchedVariety
        )
    }

    // Generate chronological agenda events
    val scheduleItems = remember(plantedDate, cropName, varietyName, maturityProfile, matchedVariety, stageInfoList) {
        generateCropAgendaSchedule(
            cropName = cropName,
            varietyName = varietyName,
            plantedDate = plantedDate,
            profile = maturityProfile,
            variety = matchedVariety,
            stages = stageInfoList
        ).sortedBy { it.date }
    }

    // State for task completion toggles — persisted per plot and vegetable
    val context = LocalContext.current
    val scheduleKey = remember(plotId, cropName) { "${plotId.ifBlank { "plot-1" }}_${cropName}" }
    var completedTaskIds by remember(scheduleKey) {
        mutableStateOf(TaskCompletionStore.load(context, scheduleKey))
    }

    // Set of dates with recorded check-up evaluations
    val checkUpDates = remember(checkUpLogs) {
        checkUpLogs.mapNotNull { parsePlantedDate(it.date) }.toSet()
    }

    // Single source of truth for schedule status
    val scheduleStatus = remember(plantedDateStr, cropName, varietyName, effectivePlanningMode, today) {
        resolveScheduleStatus(
            plantedDate = plantedDateStr,
            cropName = cropName,
            varietyName = varietyName,
            isMonitoringStarted = !effectivePlanningMode,
            today = today
        )
    }

    // Group items by date for monthly calendar indicator dots (empty in planning mode so color coding is hidden)
    val tasksByDate = remember(scheduleItems, effectivePlanningMode) {
        if (effectivePlanningMode) emptyMap() else scheduleItems.groupBy { it.date }
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
        ConfigureDialogSystemBars(isLightStatusBar = true, isLightNavigationBar = true)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceBg)
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
                            .statusBarsPadding()
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
                                    text = if (effectivePlanningMode) {
                                        val vInfo = if (!varietyName.isNullOrBlank()) " • $varietyName" else ""
                                        "$gardenLabel$vInfo • Pagpaplano (Hindi pa Nagsisimula)"
                                    } else {
                                        val vInfo = if (!varietyName.isNullOrBlank()) " • $varietyName" else ""
                                        "$gardenLabel$vInfo • Itinanim: $formattedPlantedDate"
                                    },
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MutedText
                                )
                            }
                        }

                        // Right side: RESCHEDULE ICON BUTTON
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
                // 3 Floating Tabs (Sticky navigation that follows user when scrolling down):
                // 1. Calendar
                // 2. Growth Timeline
                // 3. Today's task (Daily Agenda, Timeline Schedule)
                // ═══════════════════════════════════════════════════════════════
                FloatingCalendarNavigationTabs(
                    activeTab = activeNavTab,
                    isPlanningMode = effectivePlanningMode,
                    onTabSelected = { targetTab ->
                        activeNavTab = targetTab
                        coroutineScope.launch {
                            isProgrammaticScroll = true
                            try {
                                val targetIndex = if (effectivePlanningMode) {
                                    if (targetTab == CalendarScreenTab.CALENDAR) 0 else 1
                                } else {
                                    when (targetTab) {
                                        CalendarScreenTab.CALENDAR -> 0
                                        CalendarScreenTab.GROWTH_TIMELINE -> 1
                                        CalendarScreenTab.TODAYS_TASK -> 2
                                    }
                                }
                                lazyListState.animateScrollToItem(targetIndex)
                            } finally {
                                isProgrammaticScroll = false
                            }
                        }
                    }
                )

                // ═══════════════════════════════════════════════════════════════
                // Main Scrollable Body:
                // 1. Monthly Calendar
                // 2. Growth Timeline
                // 3. Agenda Schedule of Events
                // ═══════════════════════════════════════════════════════════════
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp),
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
                            checkUpDates = checkUpDates,
                            plantedDate = plantedDate,
                            harvestDate = harvestDate,
                            isPlanningMode = effectivePlanningMode
                        )
                    }

                    // ── 2. GROWTH TIMELINE CARD (Hidden in Planning Mode) ────
                    if (!effectivePlanningMode) {
                        item {
                            GrowthTimelineCard(
                                cropName = cropName,
                                varietyName = varietyName,
                                plantedDate = plantedDate,
                                totalDays = totalDays,
                                daysPlanted = daysPlanted,
                                daysRemaining = daysRemaining,
                                progressRatio = stageProgressRatio,
                                formattedPlantedDate = formattedPlantedDate,
                                formattedHarvestDate = formattedHarvestDate,
                                maturityProfile = maturityProfile,
                                stages = stageInfoList
                            )
                        }
                    }

                    // ── 3. BOTTOM SPLIT PANE AGENDA VIEW / TIMELINE LIST ──────
                    if (effectivePlanningMode) {
                        item(key = "locked-schedule-and-history") {
                            LockedScheduleAndHistoryCard(
                                cropName = cropName,
                                onStartSchedule = onStartSchedule
                            )
                        }
                    } else {
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
                                            "Today's Task • Daily Agenda"
                                        else
                                            "Today's Task • Check Up history",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = DeepBlack
                                    )
                                    Text(
                                        text = if (bottomViewMode == BottomScheduleViewMode.CHECKUP_HISTORY)
                                            "Talaan ng mga nasuring problema at rekomendasyon (${checkUpLogs.size})"
                                        else if (showAllTasksInAgenda)
                                            "Lahat (${scheduleItems.size}) ng gawain mula pagtanim hanggang ani"
                                        else if (selectedCalendarDate != null)
                                            "Gawain para sa linggo ng ${selectedCalendarDate?.format(fallbackDateFormatter)}"
                                        else "Pumili ng araw sa kalendaryo sa itaas",
                                        fontSize = 11.sp,
                                        color = MutedText
                                    )
                                }

                                // Segmented Control Toggle between Daily Agenda and Check Up history
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = SurfaceBg,
                                    border = BorderStroke(1.dp, CardBorderColor)
                                ) {
                                    Row(modifier = Modifier.padding(3.dp)) {
                                        listOf(
                                            BottomScheduleViewMode.SPLIT_AGENDA,
                                            BottomScheduleViewMode.CHECKUP_HISTORY
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
                                        isPast = item.date.isBefore(today),
                                        today = today,
                                        onToggleCheck = {
                                            if (item.date.isAfter(today)) {
                                                android.widget.Toast.makeText(
                                                    context,
                                                    "Hindi pa takdang panahon para sa gawaing ito (Nakatakda sa ${item.date}). Hintayin ang takdang araw.",
                                                    android.widget.Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                val newIds = if (isChecked) completedTaskIds - item.id else completedTaskIds + item.id
                                                completedTaskIds = newIds
                                                TaskCompletionStore.save(context, scheduleKey, newIds)
                                            }
                                        },
                                        onHarvestClick = if (item.type == AgendaTaskType.HARVEST) { { showPostHarvestDialog = true } } else null
                                    )
                                }
                            }
                        }
                    } else {
                        // ── MODE 2: CHECK UP HISTORY VIEW (Replaces Timeline Schedule) ────────
                        if (checkUpLogs.isEmpty()) {
                            item(key = "checkup-history-empty") {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = CardBg,
                                    border = BorderStroke(1.dp, CardBorderColor),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = LightGreenBg,
                                            modifier = Modifier.size(54.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.History,
                                                    contentDescription = null,
                                                    tint = ForestGreen,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "Wala Pang Check Up History",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DeepBlack
                                        )
                                        Text(
                                            text = "Wala pang naitalang ulat sa pagsusuri para sa $cropName. Magsagawa ng Vegetables Check Up upang maitala ang mga problema at solusyon dito.",
                                            fontSize = 12.sp,
                                            color = MutedText,
                                            textAlign = TextAlign.Center
                                        )
                                        FilledTonalButton(
                                            onClick = { showCheckUpDssDialog = true },
                                            colors = ButtonDefaults.filledTonalButtonColors(
                                                containerColor = LightGreenBg,
                                                contentColor = ForestGreen
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Icon(Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Text("Magsagawa ng Check Up Ngayon", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            items(checkUpLogs, key = { it.id }) { log ->
                                CheckUpHistoryLogCard(log = log, plantedDate = plantedDate)
                            }
                        }
                    }
                }
            }

            if (showCheckUpDssDialog) {
                CropCareDssDialog(
                    cropName = cropName,
                    varietyName = varietyName ?: "",
                    gardenLabel = gardenLabel,
                    plotId = plotId,
                    currentStage = scheduleStatus.stageOrNull ?: ManagementStage.VEGETATIVE_GROWTH,
                    scheduleStatus = scheduleStatus,
                    onOpenSchedule = { showCheckUpDssDialog = false },
                    onDismiss = { showCheckUpDssDialog = false }
                )
            }

            if (showPostHarvestDialog) {
                PostHarvestRotationDialog(
                    cropName = cropName,
                    varietyName = varietyName,
                    gardenLabel = gardenLabel,
                    plotId = plotId,
                    daysPlanted = daysPlanted,
                    onDismiss = { showPostHarvestDialog = false },
                    onHarvestRecorded = { isFinal ->
                        showPostHarvestDialog = false
                        if (isFinal) {
                            onDismiss()
                        }
                    },
                    onNavigateToCanvas = { targetPlotId ->
                        showPostHarvestDialog = false
                        onNavigateToCanvas?.invoke(targetPlotId) ?: onDismiss()
                    }
                )
            }
        }
    }
}
}

// ═══════════════════════════════════════════════════════════════════════════════
// Component 1: Monthly Calendar Card
// ═══════════════════════════════════════════════════════════════════════════════
@Composable
private fun LockedScheduleAndHistoryCard(
    cropName: String,
    onStartSchedule: (() -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CardBg,
        border = BorderStroke(1.2.dp, CardBorderColor),
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFFFF3E0),
                border = BorderStroke(1.dp, AmberAlert.copy(alpha = 0.4f)),
                modifier = Modifier.size(58.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Naka-lock",
                        tint = AmberAlert,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Naka-lock ang Daily Agenda at Check Up history",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DeepBlack,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Nakatago ang mga pang-araw-araw na gawain, paalala (pagdidilig, pagpapataba), at talaan ng check-up dahil hindi pa nasisimulan ang iskedyul ng pananim para sa $cropName.",
                    fontSize = 12.sp,
                    color = MutedText,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SurfaceBg,
                border = BorderStroke(0.8.dp, CardBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "I-click ang button sa ibaba upang simulan ang kalendaryo ng pagtatanim at i-unlock ang lahat ng aktibidad.",
                        fontSize = 11.5.sp,
                        color = Charcoal,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (onStartSchedule != null) {
                Button(
                    onClick = onStartSchedule,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestGreen,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Simulan ang Kalendaryo Ngayon",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingCalendarNavigationTabs(
    activeTab: CalendarScreenTab,
    onTabSelected: (CalendarScreenTab) -> Unit,
    isPlanningMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val availableTabs = if (isPlanningMode) {
        listOf(CalendarScreenTab.CALENDAR, CalendarScreenTab.TODAYS_TASK)
    } else {
        CalendarScreenTab.values().toList()
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = CardBg,
        border = BorderStroke(1.dp, CardBorderColor),
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            availableTabs.forEach { tab ->
                val isSelected = activeTab == tab
                val tabBgColor by animateColorAsState(
                    targetValue = if (isSelected) ForestGreen else Color.Transparent,
                    label = "calendarTabBg"
                )
                val iconTint by animateColorAsState(
                    targetValue = if (isSelected) Color.White else MutedText,
                    label = "calendarTabIcon"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else Charcoal,
                    label = "calendarTabText"
                )
                val displayIcon = if (isPlanningMode && tab == CalendarScreenTab.TODAYS_TASK) Icons.Default.Lock else tab.icon
                val displayLabel = if (isPlanningMode && tab == CalendarScreenTab.TODAYS_TASK) "Today's Task (Naka-lock)" else tab.label

                Surface(
                    onClick = { onTabSelected(tab) },
                    shape = RoundedCornerShape(11.dp),
                    color = tabBgColor,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = displayIcon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = displayLabel,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                            color = textColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthlyCalendarCard(
    viewingMonth: YearMonth,
    onMonthChanged: (YearMonth) -> Unit,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    tasksByDate: Map<LocalDate, List<AgendaScheduleItem>>,
    checkUpDates: Set<LocalDate> = emptySet(),
    plantedDate: LocalDate,
    harvestDate: LocalDate,
    isPlanningMode: Boolean = false
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
                                val isPlantedDay = !isPlanningMode && cellDate == plantedDate
                                val isHarvestDay = !isPlanningMode && cellDate == harvestDate
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

                                        // Task Indicator Dots + Check-Up Dot
                                        val hasCheckUp = cellDate in checkUpDates
                                        if ((tasksOnDay.isNotEmpty() || hasCheckUp) && !isSelected) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                                modifier = Modifier.padding(top = 1.dp)
                                            ) {
                                                if (hasCheckUp) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(4.5.dp)
                                                            .clip(CircleShape)
                                                            .background(Color(0xFFE65100))
                                                    )
                                                }
                                                val distinctTypes = tasksOnDay.map { it.type }.distinct().take(if (hasCheckUp) 2 else 3)
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

            if (!isPlanningMode) {
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
            } else {
                HorizontalDivider(color = CardBorderColor.copy(alpha = 0.6f))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF1F5F0),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = ForestGreen.copy(alpha = 0.8f),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "Walang naka-iskedyul na aktibidad habang nasa pagpaplano pa ang gulay na ito.",
                            fontSize = 11.sp,
                            color = Charcoal,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
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
    varietyName: String? = null,
    plantedDate: LocalDate,
    totalDays: Int,
    daysPlanted: Int,
    daysRemaining: Int,
    progressRatio: Float,
    formattedPlantedDate: String,
    formattedHarvestDate: String,
    maturityProfile: com.maptanim.app.features.farm.components.CropMaturityProfile,
    stages: List<StageBannerInfo> = emptyList()
) {
    val activeStage = remember(daysPlanted, stages) {
        stages.firstOrNull { daysPlanted in it.startDay..it.endDay }
            ?: if (daysPlanted <= 0) stages.firstOrNull() else stages.lastOrNull()
    }

    val stageDisplayName = activeStage?.stageTitle ?: "Yugto ng Paglago"
    val stageAdvice = activeStage?.description
        ?: "Panatilihing regular ang pagdidilig at pag-aalaga ayon sa kasalukuyang yugto ng tanim."

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
            // Header: Title, Variety Tag, and Countdown Badge
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
                    Column {
                        Text(
                            text = "Yugto ng Paglago (Growth Timeline)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBlack
                        )
                        if (!varietyName.isNullOrBlank()) {
                            Text(
                                text = "Barayti: $varietyName",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ForestGreen
                            )
                        }
                    }
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

                // Dynamic Stage Labels based on the crop's actual stages
                if (stages.isNotEmpty()) {
                    val displayStages = stages.filter { it.startDay > 0 || stages.size <= 4 }.ifEmpty { stages }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        displayStages.forEachIndexed { index, stage ->
                            val isCur = activeStage?.stageKey == stage.stageKey
                            Text(
                                text = if (index == 0) "${stage.stageKey} (1d)" else if (index == displayStages.lastIndex) "Ani (${totalDays}d)" else stage.stageKey,
                                fontSize = 8.5.sp,
                                fontWeight = if (isCur) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isCur) ForestGreen else MutedText
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Punla (Day 1)", fontSize = 9.sp, color = MutedText)
                        Text("Paglago", fontSize = 9.sp, color = MutedText)
                        Text("Ani (Day $totalDays)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                    }
                }
            }

            // Active Stage Scenic / Highlight Box
            if (activeStage != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Brush.horizontalGradient(activeStage.gradientColors))
                            .padding(12.dp)
                    ) {
                        Icon(
                            imageVector = activeStage.icon,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.16f),
                            modifier = Modifier
                                .size(56.dp)
                                .align(Alignment.CenterEnd)
                                .offset(x = 6.dp, y = 2.dp)
                        )

                        Column(
                            modifier = Modifier.align(Alignment.CenterStart),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.White.copy(alpha = 0.25f)
                                ) {
                                    Text(
                                        text = activeStage.dayRange,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = "Araw $daysPlanted ng $totalDays (${(progressRatio * 100).toInt()}%)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }

                            Text(
                                text = activeStage.stageTitle,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )

                            Text(
                                text = activeStage.description,
                                fontSize = 11.sp,
                                lineHeight = 14.sp,
                                color = Color.White.copy(alpha = 0.95f)
                            )
                        }
                    }
                }
            } else {
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
                        Text(
                            text = "Kasalukuyan: $stageDisplayName",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        Text(
                            text = stageAdvice,
                            fontSize = 11.5.sp,
                            lineHeight = 15.sp,
                            color = Charcoal
                        )
                    }
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
    isPast: Boolean = false,
    today: LocalDate = LocalDate.now(),
    onToggleCheck: () -> Unit,
    onHarvestClick: (() -> Unit)? = null
) {
    val isMissed = isPast && !isChecked
    val isRightTimeDone = isChecked
    val isFuture = item.date.isAfter(today)

    val itemBg = when {
        isRightTimeDone -> Color(0xFFF4FAF4)
        isMissed -> Color(0xFFFFF5F5)
        isFuture -> Color(0xFFFAFAFA)
        else -> CardBg
    }

    val itemBorder = when {
        isRightTimeDone -> ForestGreen.copy(alpha = 0.35f)
        isMissed -> Color(0xFFEF5350).copy(alpha = 0.45f)
        isFuture -> CardBorderColor.copy(alpha = 0.5f)
        else -> CardBorderColor
    }

    val stripeColor = when {
        isRightTimeDone -> ForestGreen
        isMissed -> Color(0xFFE53935)
        isFuture -> Color(0xFFBDBDBD)
        else -> item.type.color
    }

    val titleColor = when {
        isRightTimeDone -> ForestGreen
        isMissed -> Color(0xFFD32F2F)
        isFuture -> MutedText.copy(alpha = 0.7f)
        else -> DeepBlack
    }

    val titleDecoration = when {
        isRightTimeDone -> TextDecoration.LineThrough
        isMissed -> TextDecoration.LineThrough
        else -> TextDecoration.None
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = itemBg,
        border = BorderStroke(1.dp, itemBorder),
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
                    color = when {
                        isRightTimeDone -> ForestGreen
                        isMissed -> Color(0xFFD32F2F)
                        isFuture -> MutedText.copy(alpha = 0.6f)
                        else -> DeepBlack
                    }
                )
                Text(
                    text = item.type.tagalogLabel,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = when {
                        isRightTimeDone -> ForestGreen.copy(alpha = 0.8f)
                        isMissed -> Color(0xFFD32F2F).copy(alpha = 0.8f)
                        isFuture -> MutedText.copy(alpha = 0.5f)
                        else -> item.type.color
                    },
                    maxLines = 1
                )
            }

            // Vertical Colored Stripe (Teams style thick indicator bar)
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(38.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(stripeColor)
            )

            // Task Details Body
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    if (isMissed) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFFFEBEE)
                        ) {
                            Text(
                                text = "Hindi Nagawa",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC62828),
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    } else if (isRightTimeDone) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Text(
                                text = "Tapos Na",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    } else if (isFuture) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFF5F5F5)
                        ) {
                            Text(
                                text = "Paparating",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF757575),
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }

                    Text(
                        text = item.title,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor,
                        textDecoration = titleDecoration
                    )
                }
                Text(
                    text = item.tagalogInstruction,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    color = when {
                        isRightTimeDone -> ForestGreen.copy(alpha = 0.7f)
                        isMissed -> Color(0xFFC62828).copy(alpha = 0.75f)
                        isFuture -> MutedText.copy(alpha = 0.6f)
                        else -> Charcoal
                    },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (item.type == AgendaTaskType.HARVEST && onHarvestClick != null) {
                    FilledTonalButton(
                        onClick = onHarvestClick,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFFFFF3E0),
                            contentColor = AmberAlert
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.padding(top = 4.dp).height(28.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Eco, contentDescription = null, tint = AmberAlert, modifier = Modifier.size(13.dp))
                            Text("Mag-ani at Ihanda ang Lupa", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = AmberAlert)
                        }
                    }
                }
            }

            // Status icon / Action button
            IconButton(
                onClick = onToggleCheck,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = when {
                        isRightTimeDone -> Icons.Default.CheckCircle
                        isMissed -> Icons.Default.Cancel
                        isFuture -> Icons.Default.Lock
                        else -> Icons.Default.RadioButtonUnchecked
                    },
                    contentDescription = when {
                        isRightTimeDone -> "Tapos na"
                        isMissed -> "Hindi Nagawa"
                        isFuture -> "Paparating (Naka-lock)"
                        else -> "Gawin"
                    },
                    tint = when {
                        isRightTimeDone -> ForestGreen
                        isMissed -> Color(0xFFE53935)
                        isFuture -> Color(0xFFBDBDBD)
                        else -> CardBorderColor
                    },
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
private fun CheckUpHistoryLogCard(log: CropLog, plantedDate: LocalDate? = null) {
    val summary = remember(log) { CheckUpRecorder.summarize(log) }
    val logDate = remember(log.date) { parsePlantedDate(log.date) }
    val dayNumber = remember(logDate, plantedDate) {
        if (logDate != null && plantedDate != null) dayNumberOn(logDate, plantedDate) else null
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CardBg,
        border = BorderStroke(1.dp, CardBorderColor),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Inspection Icon + Category/Title + Severity + Stage Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = summary.severity.color.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, summary.severity.color.copy(alpha = 0.35f)),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = summary.severity.color,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = summary.category,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBlack
                        )
                        val dateLine = buildString {
                            append(log.date)
                            if (dayNumber != null) append(" • Araw $dayNumber")
                        }
                        Text(
                            text = dateLine,
                            fontSize = 11.sp,
                            color = MutedText
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (summary.severity != CheckUpSeverity.UNKNOWN) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = summary.severity.color.copy(alpha = 0.12f),
                            border = BorderStroke(0.8.dp, summary.severity.color.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = summary.severity.label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = summary.severity.color,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = LightGreenBg
                    ) {
                        Text(
                            text = log.currentStage.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = CardBorderColor.copy(alpha = 0.6f), thickness = 0.8.dp)

            // Problem & Symptoms Identified Section
            if (log.selectedCheckboxes.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(
                        text = "Mga Nasuring Problema / Sintomas:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Charcoal
                    )
                    log.selectedCheckboxes.forEach { symptom ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFFF3E0),
                            border = BorderStroke(0.8.dp, AmberAlert.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .background(AmberAlert, CircleShape)
                                )
                                Text(
                                    text = symptom,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFBF360C)
                                )
                            }
                        }
                    }
                }
            }

            // Prescribed Solution / Notes Section
            val resultTitle = summary.resultTitle
            val details = summary.details
            if (!resultTitle.isNullOrBlank() || !details.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceBg,
                    border = BorderStroke(0.8.dp, CardBorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (!resultTitle.isNullOrBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = ForestGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = resultTitle,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreen
                                )
                            }
                        }
                        if (!details.isNullOrBlank()) {
                            Text(
                                text = details,
                                fontSize = 11.5.sp,
                                color = Charcoal,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
