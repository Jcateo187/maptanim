package com.maptanim.app.ui.screens.farm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.maptanim.app.data.datasource.CropMetadataAssetDataSource
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.CropZone
import com.maptanim.app.domain.model.ManagementStage
import com.maptanim.app.domain.model.TaskType
import com.maptanim.app.domain.model.VegetableCategory
import com.maptanim.app.navigation.Routes
import com.maptanim.app.ui.dialogs.AddLogDialog
import com.maptanim.app.ui.dialogs.CropDssManagementDialog
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.White
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.LocalConfiguration
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmScreen(
    navController: NavController,
    viewModel: FarmViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Toast feedback
    LaunchedEffect(uiState.toastMessage) {
        if (uiState.toastMessage != null) {
            kotlinx.coroutines.delay(2500)
            viewModel.clearToast()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        color = Color(0xFF10160F)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── TOP BAR ───────────────────────────────────────────────────────
            FarmTopBar(
                farmName = uiState.farmName,
                isLoading = uiState.isLoading,
                onBack = { navController.popBackStack() },
                onGardenLayoutClick = { viewModel.selectTab(FarmTab.OVERVIEW) }
            )

            // ── CLEAN TAB NAVIGATION ──────────────────────────────────────────
            FarmTabNavigation(
                selectedTab = uiState.selectedTab,
                onTabSelected = { viewModel.selectTab(it) }
            )

            // Toast banner
            AnimatedVisibility(
                visible = uiState.toastMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .background(Color(0xFF2E7D32), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.toastMessage ?: "",
                        color = White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // ── TAB CONTENT ───────────────────────────────────────────────────
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (uiState.selectedTab) {
                    FarmTab.OVERVIEW -> {
                        SingleScreenFarmHub(
                            uiState = uiState,
                            onAddNewBed = { viewModel.addNewBed() },
                            onAddNewBedWithDetails = { label, w, h, soil -> viewModel.addNewBed(label, w, h, soil) },
                            onDeleteBed = { viewModel.deleteBed(it) },
                            onAssignCrop = { plotId, cropName, variety, method, isPlanted ->
                                viewModel.assignCropToBed(plotId, cropName, variety, method, isPlanted)
                            },
                            onCompleteTask = { viewModel.completeTask(it) },
                            onQuickMaintain = { plotId, action -> viewModel.quickLogMaintenance(plotId, action) },
                            onRecordHarvest = { plotId, yieldKg, notes, isFinal ->
                                viewModel.recordHarvest(plotId, yieldKg, notes, isFinal)
                            },
                            onAdvanceStage = { plotId, stage -> viewModel.advancePlotStage(plotId, stage) },
                            onLogObservationTask = { plotId, taskTitle ->
                                viewModel.quickLogMaintenance(plotId, "weed")
                            }
                        )
                    }
                    FarmTab.MONITORING -> {
                        FarmMonitoringTab(
                            uiState = uiState,
                            onFilterSelected = { viewModel.selectCropsFilter(it) },
                            onOpenPlantDetails = { viewModel.openCropDetails(it) },
                            onOpenPlotDetails = { viewModel.openPlotDetails(it) },
                            onStartPlanting = { plotId -> viewModel.startPlantingNow(plotId) },
                            onDismissAlert = { alert, note -> viewModel.dismissAlertAndRecordHistory(alert, note) },
                            onDismissAllAlerts = { alerts -> viewModel.dismissAllAlertsAndRecordHistory(alerts) }
                        )
                    }
                    FarmTab.CALENDAR -> {
                        FarmCalendarTab(
                            uiState = uiState,
                            onDateSelected = { viewModel.selectCalendarDate(it) },
                            onClearDateFilter = { viewModel.clearCalendarDateFilter() },
                            onPreviousMonth = { viewModel.previousCalendarMonth() },
                            onNextMonth = { viewModel.nextCalendarMonth() },
                            onFilterSelected = { viewModel.filterCalendar(it) },
                            onEventClick = { event ->
                                when (event.type) {
                                    CalendarEventType.MONITORING -> {
                                        val plot = uiState.plots.firstOrNull { it.id == event.plotId }
                                            ?: uiState.plots.firstOrNull { it.plotLabel == event.plotLabel }
                                        if (plot != null) {
                                            viewModel.openObservationDialog(plot)
                                        }
                                    }
                                    CalendarEventType.PLANTING,
                                    CalendarEventType.GROWTH_STAGE,
                                    CalendarEventType.HARVEST -> {
                                        val plant = uiState.plantedPlants.firstOrNull { it.id == event.plotId }
                                            ?: uiState.plantedPlants.firstOrNull { it.plotLabel == event.plotLabel }
                                        if (plant != null) {
                                            viewModel.openCropDetails(plant)
                                        } else {
                                            val plot = uiState.plots.firstOrNull { it.id == event.plotId }
                                                ?: uiState.plots.firstOrNull { it.plotLabel == event.plotLabel }
                                            if (plot != null) {
                                                viewModel.openPlotDetails(plot)
                                            }
                                        }
                                    }
                                    CalendarEventType.CROP_CARE,
                                    CalendarEventType.DSS_TASK,
                                    CalendarEventType.COMPLETED -> {
                                        if (!event.taskId.isNullOrBlank()) {
                                            viewModel.completeTask(event.taskId)
                                        } else if (!event.plotId.isNullOrBlank()) {
                                            val plot = uiState.plots.firstOrNull { it.id == event.plotId }
                                            if (plot != null) viewModel.openPlotDetails(plot)
                                        }
                                    }
                                }
                            },
                            onCompleteTask = { viewModel.completeTask(it) }
                        )
                    }
                    FarmTab.ACTIVITY -> {
                        FarmActivityTab(
                            uiState = uiState,
                            onFilterSelected = { viewModel.filterActivities(it) }
                        )
                    }
                }
            }
        }
    }

    // ── MODALS (For secondary tabs only; Overview uses SingleScreenFarmHub directly) ──
    // 1. Crop DSS Management Dialog
    if (uiState.selectedTab != FarmTab.OVERVIEW && (uiState.selectedPlantForDetails != null || uiState.selectedPlotForDetails != null)) {
        CropDssManagementDialog(
            plant = uiState.selectedPlantForDetails,
            plot = uiState.selectedPlotForDetails,
            onDismiss = { viewModel.closeCropDetails() },
            onReadInLibrary = { cropName ->
                viewModel.closeCropDetails()
                navController.navigate(Routes.libraryRoute(cropName))
            },
            onRecordObservation = {
                // Handled internally by CropDssManagementDialog
            },
            onStartPlanting = { plotId ->
                viewModel.startPlantingNow(plotId)
            },
            onHarvest = {
                viewModel.closeCropDetails()
            }
        )
    }

    // 2. Interactive Record Observation Dialog (The Question, ABC Choices, and Selection Checkboxes)
    if (uiState.isObservationModalOpen) {
        val targetPlot = uiState.observationTargetPlot
            ?: uiState.plots.firstOrNull { !it.cropName.isNullOrBlank() }
        if (targetPlot != null) {
            val monitoredPlant = uiState.plantedPlants.firstOrNull { it.id == targetPlot.id || it.plotLabel == targetPlot.plotLabel }
            val rawDate = targetPlot.plantedDate ?: monitoredPlant?.rawPlantedDate
            val stage = if (targetPlot.currentStage != ManagementStage.PREPARATION) {
                targetPlot.currentStage
            } else if (!rawDate.isNullOrBlank()) {
                val dateOnly = rawDate.take(10)
                val pDate = try { LocalDate.parse(dateOnly) } catch (_: Exception) { LocalDate.now() }
                val days = java.time.temporal.ChronoUnit.DAYS.between(pDate, LocalDate.now()).toInt().coerceAtLeast(0)
                val dth = (monitoredPlant?.daysToHarvest ?: 60).coerceAtLeast(20)
                val progress = (days.toFloat() / dth.toFloat()).coerceIn(0f, 1f)
                when {
                    progress < 0.15f -> ManagementStage.PLANTING
                    progress < 0.35f -> ManagementStage.EARLY_GROWTH
                    progress < 0.65f -> ManagementStage.VEGETATIVE_GROWTH
                    progress < 0.90f -> ManagementStage.FLOWERING_FRUIT_DEVELOPMENT
                    else -> ManagementStage.HARVEST
                }
            } else {
                ManagementStage.PREPARATION
            }
            AddLogDialog(
                cropPlantingId = targetPlot.id,
                bedId = targetPlot.plotLabel,
                cropId = targetPlot.cropId,
                varietyId = targetPlot.cropVariety,
                cropName = targetPlot.cropName ?: "Crop",
                varietyName = targetPlot.cropVariety ?: "Standard Variety",
                currentStage = stage,
                plantingMethod = "Transplanting",
                onDismiss = { viewModel.closeObservationDialog() },
                onSubmitLog = { cropLog ->
                    viewModel.submitCropLog(cropLog)
                }
            )
        } else {
            viewModel.closeObservationDialog()
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// TOP BAR
// ═══════════════════════════════════════════════════════════════════════════════

// ═══════════════════════════════════════════════════════════════════════════════
// TOP BAR
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun FarmTopBar(
    farmName: String,
    isLoading: Boolean,
    onBack: () -> Unit,
    onGardenLayoutClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .background(Color(0xFF10160F))
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = farmName.uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = White,
                letterSpacing = 0.8.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onGardenLayoutClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32),
                    contentColor = White
                ),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Yard,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = Color(0xFFC8E6C9)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Garden Layout",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }
        }
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = Color(0xFF4CAF50),
                trackColor = Color(0xFF1B2419)
            )
        } else {
            HorizontalDivider(color = Color(0xFF243021), thickness = 1.dp)
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// CLEAN TAB NAVIGATION
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun FarmTabNavigation(
    selectedTab: FarmTab,
    onTabSelected: (FarmTab) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SecondaryScrollableTabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = Color(0xFF10160F),
            contentColor = Color(0xFF4CAF50),
            edgePadding = 12.dp,
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(selectedTabIndex = selectedTab.ordinal),
                    height = 2.5.dp,
                    color = Color(0xFF4CAF50)
                )
            },
            divider = {}
        ) {
            FarmTab.values().forEach { tab ->
                val isSelected = tab == selectedTab
                Tab(
                    selected = isSelected,
                    onClick = { onTabSelected(tab) },
                    text = {
                        Text(
                            text = tab.title.uppercase(),
                            color = if (isSelected) White else Color(0xFFA0B09A),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                )
            }
        }
        HorizontalDivider(color = Color(0xFF243021), thickness = 1.dp)
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// TAB 1: OVERVIEW
// ═══════════════════════════════════════════════════════════════════════════════

// ═══════════════════════════════════════════════════════════════════════════════
// VEGETABLE CATEGORIES (DA/PSA Standard 8-category classification)
// ═══════════════════════════════════════════════════════════════════════════════

private data class VegCategoryItem(
    val category: VegetableCategory?,
    val icon: String,
    val shortName: String,
    val displayName: String,
    val examples: String
)

private val VEGETABLE_CATEGORY_ITEMS = listOf(
    VegCategoryItem(null, "🌿", "All", "All Vegetables", "All 8 categories"),
    VegCategoryItem(VegetableCategory.FRUIT, "🍅", "Fruit", "Fruit Vegetables", "Tomato, Eggplant, Squash"),
    VegCategoryItem(VegetableCategory.LEAFY, "🥬", "Leafy", "Leafy Vegetables", "Pechay, Kangkong, Lettuce"),
    VegCategoryItem(VegetableCategory.ROOT, "🥕", "Root", "Root Vegetables", "Carrot, Radish"),
    VegCategoryItem(VegetableCategory.BULB, "🧅", "Bulb", "Bulb Vegetables", "Onion, Garlic"),
    VegCategoryItem(VegetableCategory.STEM, "🎋", "Stem", "Stem Vegetables", "Celery, Asparagus"),
    VegCategoryItem(VegetableCategory.SHOOT, "🌱", "Shoot", "Shoot Vegetables", "Bamboo Shoots, Togue"),
    VegCategoryItem(VegetableCategory.FLOWER, "🥦", "Flower", "Flower Vegetables", "Broccoli, Cauliflower"),
    VegCategoryItem(VegetableCategory.TUBER, "🥔", "Tuber", "Tuber Vegetables", "Potato, Sweet Potato")
)

private fun getCropVegetableCategory(cropName: String): VegetableCategory {
    val clean = cropName.lowercase().replace(" ", "").replace("_", "").replace("-", "")
    return when {
        clean.contains("onion") || clean.contains("sibuyas") || clean.contains("garlic") || clean.contains("bawang") || clean.contains("bulb") -> VegetableCategory.BULB
        clean.contains("celery") || clean.contains("asparagus") || clean.contains("kintsay") || clean.contains("stem") -> VegetableCategory.STEM
        clean.contains("labong") || clean.contains("shoot") || clean.contains("sprout") || clean.contains("togue") -> VegetableCategory.SHOOT
        clean.contains("pechay") || clean.contains("bokchoy") || clean.contains("pakchoi") || clean.contains("kangkong") || clean.contains("spinach") || clean.contains("lettuce") || clean.contains("litsugas") || clean.contains("cabbage") || clean.contains("repolyo") || clean.contains("mustasa") || clean.contains("leafy") -> VegetableCategory.LEAFY
        clean.contains("broccoli") || clean.contains("cauliflower") || clean.contains("flower") -> VegetableCategory.FLOWER
        clean.contains("carrot") || clean.contains("karot") || clean.contains("radish") || clean.contains("labanos") || clean.contains("root") -> VegetableCategory.ROOT
        clean.contains("potato") || clean.contains("patatas") || clean.contains("kamote") || clean.contains("sweetpotato") || clean.contains("cassava") || clean.contains("tuber") -> VegetableCategory.TUBER
        clean.contains("tomato") || clean.contains("kamatis") || clean.contains("eggplant") || clean.contains("talong") || clean.contains("squash") || clean.contains("pumpkin") || clean.contains("kalabasa") || clean.contains("okra") || clean.contains("cucumber") || clean.contains("pipino") || clean.contains("sili") || clean.contains("pepper") || clean.contains("chili") || clean.contains("corn") || clean.contains("mais") || clean.contains("ampalaya") || clean.contains("bittergourd") || clean.contains("sitaw") || clean.contains("bean") || clean.contains("fruit") -> VegetableCategory.FRUIT
        else -> VegetableCategory.FRUIT
    }
}

@Composable
private fun FarmOverviewTab(
    uiState: FarmUiState,
    onEditFarm: () -> Unit,
    onCompleteTask: (String) -> Unit,
    onOpenPlantDetails: (MonitoredPlant) -> Unit,
    onOpenPlotDetails: (CropPlot) -> Unit,
    onViewActivities: () -> Unit,
    onQuickMaintain: (String, String) -> Unit = { _, _ -> }
) {
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF4CAF50))
        }
        return
    }

    var selectedPlotId by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf<VegetableCategory?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Aggregate all crops: planted and planned
    val allCrops = remember(uiState.plantedPlants, uiState.plannedPlots) {
        val list = uiState.plantedPlants.toMutableList()
        uiState.plannedPlots.filter { !it.cropName.isNullOrBlank() && !it.cropName.equals("Bed", ignoreCase = true) }.forEach { plot ->
            if (list.none { it.id == plot.id }) {
                list.add(
                    MonitoredPlant(
                        id = plot.id,
                        farmId = plot.farmId,
                        cropId = plot.cropId,
                        cropName = plot.cropName ?: "Vegetable",
                        localName = plot.cropName ?: "Gulay",
                        cropVariety = plot.cropVariety ?: "Standard Variety",
                        plotLabel = plot.plotLabel,
                        currentStageIndex = 0,
                        stageName = "Stage 1: Planned",
                        daysPlanted = 0,
                        daysToHarvest = 60,
                        healthStatus = "Planned Crop",
                        soilType = plot.soilType
                    )
                )
            }
        }
        list
    }

    val selectedPlot = uiState.plots.firstOrNull { it.id == selectedPlotId }

    // Crops for currently selected bed
    val allCropsForBed = remember(selectedPlotId, allCrops) {
        if (selectedPlotId == null) {
            allCrops
        } else {
            val label = selectedPlot?.plotLabel ?: ""
            allCrops.filter {
                it.plotLabel.equals(label, ignoreCase = true) ||
                it.plotLabel.contains(label, ignoreCase = true) ||
                it.id == selectedPlotId
            }
        }
    }

    // Filtered by selected vegetable category
    val filteredCrops = remember(allCropsForBed, selectedCategory) {
        if (selectedCategory == null) {
            allCropsForBed
        } else {
            allCropsForBed.filter { crop ->
                getCropVegetableCategory(crop.cropName) == selectedCategory
            }
        }
    }

    val cropPages = remember(filteredCrops) {
        filteredCrops.chunked(4)
    }

    val pageCount = cropPages.size.coerceAtLeast(1)
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pageCount })

    LaunchedEffect(selectedPlotId, selectedCategory) {
        if (pagerState.currentPage != 0) {
            pagerState.scrollToPage(0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ── 1. FARM LAYOUT (TOP) ─────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "FARM LAYOUT",
                color = White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "${uiState.plots.size} Beds • ${uiState.cropZones.size} Crops",
                color = Color(0xFFA0B09A),
                fontSize = 11.sp
            )
        }

        ActualFarmLayoutCard(
            plots = uiState.plots,
            zones = uiState.cropZones,
            onOpenPlantDetails = onOpenPlantDetails,
            onOpenPlotDetails = onOpenPlotDetails,
            plantedPlants = uiState.plantedPlants,
            onEditFarm = onEditFarm,
            onQuickMaintain = onQuickMaintain
        )

        HorizontalDivider(color = Color(0xFF222C1F), thickness = 1.dp, modifier = Modifier.padding(vertical = 2.dp))

        // ── 2. FARM BEDS (MIDDLE) ────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "FARM BEDS",
                color = White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "${uiState.plots.size} Beds Active",
                color = Color(0xFFA0B09A),
                fontSize = 11.sp
            )
        }

        // Horizontal scrollable Bed Cards (1 row, 4 visible columns per view)
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val bedCardWidth = ((screenWidth - 32.dp - 24.dp) / 4).coerceAtLeast(78.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // "ALL" bed card
            val isAllSelected = selectedPlotId == null
            SmallBedCard(
                plotLabel = "ALL",
                soilType = "All Beds",
                cropCount = allCrops.size,
                isSelected = isAllSelected,
                onClick = { selectedPlotId = null },
                modifier = Modifier.width(bedCardWidth)
            )

            if (uiState.plots.isEmpty()) {
                listOf("Bed 1", "Bed 2", "Bed 3").forEach { label ->
                    SmallBedCard(
                        plotLabel = label,
                        soilType = "Loam",
                        cropCount = 0,
                        isSelected = false,
                        onClick = onEditFarm,
                        modifier = Modifier.width(bedCardWidth)
                    )
                }
            } else {
                uiState.plots.forEach { plot ->
                    val isSelected = selectedPlotId == plot.id
                    val bedCropCount = allCrops.count {
                        it.plotLabel.equals(plot.plotLabel, ignoreCase = true) || it.id == plot.id
                    }
                    SmallBedCard(
                        plotLabel = plot.plotLabel,
                        soilType = plot.soilType.name.lowercase().replaceFirstChar { it.uppercase() },
                        cropCount = bedCropCount,
                        isSelected = isSelected,
                        onClick = {
                            selectedPlotId = if (selectedPlotId == plot.id) null else plot.id
                        },
                        modifier = Modifier.width(bedCardWidth)
                    )
                }
            }
        }

        HorizontalDivider(color = Color(0xFF222C1F), thickness = 1.dp, modifier = Modifier.padding(vertical = 2.dp))

        // ── 3. CROPS SECTION (BOTTOM) ────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (selectedPlot != null) "${selectedPlot.plotLabel.uppercase()} CROPS" else "FARM CROPS",
                    color = White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                if (filteredCrops.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1E2A1B),
                        border = BorderStroke(1.dp, Color(0xFF2E3E29))
                    ) {
                        Text(
                            text = "${filteredCrops.size}",
                            color = Color(0xFF81C784),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Category Icon Dropdown for 8 Types of Vegetables
            Box {
                var isCategoryMenuExpanded by remember { mutableStateOf(false) }
                val currentCategoryItem = VEGETABLE_CATEGORY_ITEMS.firstOrNull { it.category == selectedCategory }
                    ?: VEGETABLE_CATEGORY_ITEMS.first()

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (selectedCategory != null) Color(0xFF2E7D32).copy(alpha = 0.25f) else Color(0xFF1B2419),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (selectedCategory != null) Color(0xFF4CAF50) else Color(0xFF2E3E29)
                    ),
                    modifier = Modifier.clickable { isCategoryMenuExpanded = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = currentCategoryItem.icon,
                            fontSize = 13.sp
                        )
                        Text(
                            text = currentCategoryItem.shortName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selectedCategory != null) Color(0xFF81C784) else White
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Category Filter",
                            tint = Color(0xFFA0B09A),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Small vertical display dropdown menu
                DropdownMenu(
                    expanded = isCategoryMenuExpanded,
                    onDismissRequest = { isCategoryMenuExpanded = false },
                    modifier = Modifier
                        .background(Color(0xFF161E14))
                        .border(1.dp, Color(0xFF2B3A27), RoundedCornerShape(8.dp))
                        .widthIn(min = 180.dp, max = 230.dp)
                ) {
                    Text(
                        text = "VEGETABLE CATEGORY",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA0B09A),
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                    HorizontalDivider(color = Color(0xFF243021), thickness = 0.8.dp)

                    VEGETABLE_CATEGORY_ITEMS.forEach { item ->
                        val isSelected = (item.category == selectedCategory)
                        val count = if (item.category == null) {
                            allCropsForBed.size
                        } else {
                            allCropsForBed.count { getCropVegetableCategory(it.cropName) == item.category }
                        }

                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(text = item.icon, fontSize = 14.sp)
                                        Column {
                                            Text(
                                                text = item.displayName,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color(0xFF81C784) else White
                                            )
                                            Text(
                                                text = item.examples,
                                                fontSize = 9.sp,
                                                color = Color(0xFF7E8F7A)
                                            )
                                        }
                                    }
                                    if (count > 0) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isSelected) Color(0xFF2E7D32) else Color(0xFF222D1F)
                                        ) {
                                            Text(
                                                text = "$count",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) White else Color(0xFFA0B09A),
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                            },
                            onClick = {
                                selectedCategory = item.category
                                isCategoryMenuExpanded = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .background(if (isSelected) Color(0xFF1E2B1B) else Color.Transparent),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        if (filteredCrops.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onEditFarm() },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141C12)),
                border = BorderStroke(1.dp, Color(0xFF2B3825))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "🌱", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = when {
                            selectedCategory != null -> "No ${selectedCategory?.name?.lowercase() ?: ""} vegetables found"
                            selectedPlot != null -> "No crops planted in ${selectedPlot.plotLabel} yet"
                            else -> "No crops planted in farm yet"
                        },
                        color = White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Tap to open Farm Editor and plant crops",
                        color = Color(0xFFA0B09A),
                        fontSize = 11.sp
                    )
                }
            }
        } else {
            // Horizontal Pager: 2 rows x 2 columns per page
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { pageIdx ->
                val pageItems = cropPages.getOrNull(pageIdx) ?: emptyList()
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Row 1: items 0 and 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val item0 = pageItems.getOrNull(0)
                        if (item0 != null) {
                            SmallCropCard(
                                plant = item0,
                                modifier = Modifier.weight(1f),
                                onClick = { onOpenPlantDetails(item0) }
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }

                        val item1 = pageItems.getOrNull(1)
                        if (item1 != null) {
                            SmallCropCard(
                                plant = item1,
                                modifier = Modifier.weight(1f),
                                onClick = { onOpenPlantDetails(item1) }
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }

                    // Row 2: items 2 and 3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val item2 = pageItems.getOrNull(2)
                        if (item2 != null) {
                            SmallCropCard(
                                plant = item2,
                                modifier = Modifier.weight(1f),
                                onClick = { onOpenPlantDetails(item2) }
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }

                        val item3 = pageItems.getOrNull(3)
                        if (item3 != null) {
                            SmallCropCard(
                                plant = item3,
                                modifier = Modifier.weight(1f),
                                onClick = { onOpenPlantDetails(item3) }
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // ── Number tabs below 1 2 3 --- if more ──────────────────────────
            if (cropPages.size > 1) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF141C12),
                    border = BorderStroke(1.dp, Color(0xFF222C1F)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Page ${pagerState.currentPage + 1} of ${cropPages.size} (${filteredCrops.size} Crops)",
                            color = Color(0xFFA0B09A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )

                        // Tabs: [ 1 ] [ 2 ] [ 3 ] ...
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            cropPages.indices.forEach { pageIdx ->
                                val isPageActive = pagerState.currentPage == pageIdx
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isPageActive) Color(0xFF4CAF50) else Color(0xFF1E281B),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = if (isPageActive) Color(0xFF81C784) else Color(0xFF2E3E29)
                                    ),
                                    modifier = Modifier.clickable {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(pageIdx)
                                        }
                                    }
                                ) {
                                    Box(
                                        modifier = Modifier.size(width = 30.dp, height = 28.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${pageIdx + 1}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isPageActive) White else Color(0xFFA0B09A)
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
}

@Composable
private fun SmallBedCard(
    plotLabel: String,
    soilType: String,
    cropCount: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF1E2F1C) else Color(0xFF141C12)
        ),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) Color(0xFF4CAF50) else Color(0xFF2B3825)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 6.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = plotLabel.uppercase(),
                color = if (isSelected) Color(0xFF81C784) else White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = soilType,
                color = Color(0xFFA0B09A),
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isSelected) Color(0xFF2E442B) else Color(0xFF1F291B))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (cropCount > 0) "$cropCount ${if (cropCount == 1) "crop" else "crops"}" else "Empty",
                    color = if (cropCount > 0) Color(0xFF81C784) else Color(0xFF758570),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun SmallCropCard(
    plant: MonitoredPlant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161E14)),
        border = BorderStroke(1.dp, Color(0xFF2B3825))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val imageModel = CropMetadataAssetDataSource.resolveCropImage(plant.cropId, plant.cropName, plant.imageUrl)
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageModel)
                        .crossfade(true)
                        .build(),
                    contentDescription = plant.cropName,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF222C1F)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = plant.cropName,
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = plant.plotLabel,
                        color = Color(0xFF81C784),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = plant.cropVariety ?: "Standard",
                        color = Color(0xFFA0B09A),
                        fontSize = 9.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            val isOverdue = plant.healthStatus.contains("Overdue", true)
            val isReady = plant.healthStatus.contains("Ready", true)
            val badgeColor = when {
                isOverdue -> Color(0xFFE53935)
                isReady -> Color(0xFFFFA000)
                else -> Color(0xFF4CAF50)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = plant.stageName.take(18),
                    color = badgeColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "D${plant.daysPlanted}",
                    color = Color(0xFFA0B09A),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ActualFarmLayoutCard(
    plots: List<CropPlot>,
    zones: List<CropZone> = emptyList(),
    onOpenPlantDetails: (MonitoredPlant) -> Unit,
    onOpenPlotDetails: (CropPlot) -> Unit,
    plantedPlants: List<MonitoredPlant>,
    onEditFarm: () -> Unit = {},
    onQuickMaintain: (plotId: String, actionType: String) -> Unit = { _, _ -> }
) {
    var selectedPlot by remember { mutableStateOf<CropPlot?>(null) }
    var isPanelCollapsed by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141C12)),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF2B3825)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header with layout stats & Add Bed button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "FARM LAYOUT CANVAS",
                        color = White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "• Tap bed to manage",
                        color = Color(0xFFA0B09A),
                        fontSize = 10.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF202E1C),
                    border = BorderStroke(1.dp, Color(0xFF384F31)),
                    modifier = Modifier.clickable { onEditFarm() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "＋", color = Color(0xFF81C784), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Add / Edit Beds", color = Color(0xFF81C784), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // ── Real 2D Top-Down Farm Layout Preview Canvas ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF2B3825), RoundedCornerShape(8.dp))
            ) {
                FarmLayoutPreviewCanvas(
                    plots = plots,
                    zones = zones,
                    onPlotClick = { plot ->
                        selectedPlot = plot
                        isPanelCollapsed = false
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // ── Collapsed Floating Handle on Canvas (< handle) ──
                if (selectedPlot != null && isPanelCollapsed) {
                    val activePlot = selectedPlot!!
                    val activePlant = plantedPlants.firstOrNull { it.id == activePlot.id }
                    val cropDisplay = activePlant?.cropName ?: activePlot.cropName ?: "Unplanted"
                    Surface(
                        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
                        color = Color(0xF0182415),
                        border = BorderStroke(1.dp, Color(0xFF4C6B42)),
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clickable { isPanelCollapsed = false }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "<",
                                color = Color(0xFF81C784),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Column {
                                Text(
                                    text = activePlot.plotLabel.uppercase(),
                                    color = Color(0xFF81C784),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = cropDisplay,
                                    color = White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // ── Interactive Bed Maintenance Panel (with < collapse handle) ──
            if (selectedPlot != null && !isPanelCollapsed) {
                val activePlot = selectedPlot!!
                val activePlant = plantedPlants.firstOrNull { it.id == activePlot.id }
                val cropDisplay = activePlant?.cropName ?: activePlot.cropName ?: "Unplanted Bed"
                val bedArea = (activePlot.widthM * activePlot.heightM).coerceAtLeast(0.1f)
                val fractionOfKey = (28.4f / bedArea).coerceAtLeast(1f).roundToInt()

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF192316),
                    border = BorderStroke(1.dp, Color(0xFF33462D)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Title row with < collapse button and ✕ close
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // The < collapse handle button
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF273822),
                                    border = BorderStroke(1.dp, Color(0xFF4C6B42)),
                                    modifier = Modifier.clickable { isPanelCollapsed = true }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Text(text = "<", color = Color(0xFF81C784), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Hide", color = Color(0xFFA0B09A), fontSize = 10.sp)
                                    }
                                }

                                Text(
                                    text = "${activePlot.plotLabel.uppercase()} • $cropDisplay",
                                    color = White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "🏀 %.1fm×%.1fm (1/%d key)".format(activePlot.widthM, activePlot.heightM, fractionOfKey),
                                    color = Color(0xFFFFD54F),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    text = "✕",
                                    color = Color(0xFFA0B09A),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clickable { selectedPlot = null }
                                        .padding(horizontal = 4.dp)
                                )
                            }
                        }

                        // 1-Tap Quick Action Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Water
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF132A38),
                                border = BorderStroke(1.dp, Color(0xFF265D7D)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onQuickMaintain(activePlot.id, "Water") }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 7.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "💧 Water", color = Color(0xFF80D8FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Weed
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF23331C),
                                border = BorderStroke(1.dp, Color(0xFF486E38)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onQuickMaintain(activePlot.id, "Weed") }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 7.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "🌿 Weed", color = Color(0xFFA5D6A7), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Full Details / Guide
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF332918),
                                border = BorderStroke(1.dp, Color(0xFF7A5C22)),
                                modifier = Modifier
                                    .weight(1.3f)
                                    .clickable {
                                        if (activePlant != null) {
                                            onOpenPlantDetails(activePlant)
                                        } else {
                                            onOpenPlotDetails(activePlot)
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 7.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "⚙️ Full Guide →", color = Color(0xFFFFD54F), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewMetricCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xFF202A1E), RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                color = color,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = Color(0xFFA0B09A),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun FarmLayoutMiniCanvas(
    plots: List<CropPlot>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        if (w <= 0 || h <= 0) return@Canvas

        // Draw background soil
        drawRect(Color(0xFF151D13), Offset.Zero, size)

        // Draw farm boundary
        drawRoundRect(
            color = Color(0xFF2A3824),
            topLeft = Offset(8f, 8f),
            size = Size(w - 16f, h - 16f),
            cornerRadius = CornerRadius(6f, 6f),
            style = Stroke(width = 1.5f)
        )

        // Find plot bounds
        val maxX = (plots.maxOfOrNull { it.posX + it.widthM } ?: 40f).coerceAtLeast(10f)
        val maxY = (plots.maxOfOrNull { it.posY + it.heightM } ?: 30f).coerceAtLeast(10f)

        val scaleX = (w - 32f) / maxX
        val scaleY = (h - 32f) / maxY
        val scale = minOf(scaleX, scaleY)

        val offsetX = (w - maxX * scale) / 2f
        val offsetY = (h - maxY * scale) / 2f

        // Draw each plot bed
        plots.forEach { plot ->
            val rectLeft = offsetX + plot.posX * scale
            val rectTop = offsetY + plot.posY * scale
            val rectW = (plot.widthM * scale).coerceAtLeast(12f)
            val rectH = (plot.heightM * scale).coerceAtLeast(12f)

            val isPlanted = plot.plantedDate != null
            val isPlanned = plot.cropName != null && !isPlanted

            val fillColor = when {
                isPlanted -> Color(0xFF1E381C)
                isPlanned -> Color(0xFF2B2516)
                else -> Color(0xFF211D19)
            }
            val strokeColor = when {
                isPlanted -> Color(0xFF4CAF50)
                isPlanned -> Color(0xFFFFA000)
                else -> Color(0xFF6D4C41)
            }

            drawRoundRect(
                color = fillColor,
                topLeft = Offset(rectLeft, rectTop),
                size = Size(rectW, rectH),
                cornerRadius = CornerRadius(4f, 4f)
            )

            drawRoundRect(
                color = strokeColor,
                topLeft = Offset(rectLeft, rectTop),
                size = Size(rectW, rectH),
                cornerRadius = CornerRadius(4f, 4f),
                style = if (isPlanned || !isPlanted) Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))) else Stroke(width = 1.5f)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// TAB 2: MONITORING (Separates Planted and Planned/Unplanted)
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun FarmMonitoringTab(
    uiState: FarmUiState,
    onFilterSelected: (CropsFilter) -> Unit,
    onOpenPlantDetails: (MonitoredPlant) -> Unit,
    onOpenPlotDetails: (CropPlot) -> Unit,
    onStartPlanting: (String) -> Unit,
    onDismissAlert: (FarmAttentionItem, String) -> Unit = { _, _ -> },
    onDismissAllAlerts: (List<FarmAttentionItem>) -> Unit = {}
) {
    var selectedPlotId by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf<VegetableCategory?>(null) }
    var recommendationAlertForModal by remember { mutableStateOf<FarmAttentionItem?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Base crops list driven by segmented control: Planted vs Planned
    val baseCrops = remember(uiState.cropsFilter, uiState.plantedPlants, uiState.plannedPlots) {
        if (uiState.cropsFilter == CropsFilter.PLANTED) {
            uiState.plantedPlants
        } else {
            uiState.plannedPlots.map { plot ->
                MonitoredPlant(
                    id = plot.id,
                    farmId = plot.farmId,
                    cropId = plot.cropId,
                    cropName = plot.cropName ?: "Vegetable",
                    localName = plot.cropName ?: "Gulay",
                    cropVariety = plot.cropVariety ?: "Standard Variety",
                    plotLabel = plot.plotLabel,
                    currentStageIndex = 0,
                    stageName = "Planned",
                    daysPlanted = 0,
                    daysToHarvest = 60,
                    healthStatus = "Planned Crop",
                    soilType = plot.soilType
                )
            }
        }
    }

    val selectedPlot = uiState.plots.firstOrNull { it.id == selectedPlotId }

    // Filter by selected bed
    val cropsForBed = remember(selectedPlotId, baseCrops) {
        if (selectedPlotId == null) {
            baseCrops
        } else {
            val label = selectedPlot?.plotLabel ?: ""
            baseCrops.filter {
                it.plotLabel.equals(label, ignoreCase = true) ||
                it.plotLabel.contains(label, ignoreCase = true) ||
                it.id == selectedPlotId
            }
        }
    }

    // Filter by selected vegetable category
    val filteredCrops = remember(cropsForBed, selectedCategory) {
        if (selectedCategory == null) {
            cropsForBed
        } else {
            cropsForBed.filter { crop ->
                getCropVegetableCategory(crop.cropName) == selectedCategory
            }
        }
    }

    // 4 rows x 4 columns = 16 crops per page
    val cropPages = remember(filteredCrops) {
        filteredCrops.chunked(16)
    }

    val pageCount = cropPages.size.coerceAtLeast(1)
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pageCount })

    LaunchedEffect(selectedPlotId, selectedCategory, uiState.cropsFilter) {
        if (pagerState.currentPage != 0) {
            pagerState.scrollToPage(0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── 1. SEGMENTED CONTROL: Planted vs Planned/Unplanted ────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A2317), RoundedCornerShape(10.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val plantedCount = uiState.plantedPlants.size
            val plannedCount = uiState.plannedPlots.size

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (uiState.cropsFilter == CropsFilter.PLANTED) Color(0xFF2E7D32) else Color.Transparent)
                    .clickable { onFilterSelected(CropsFilter.PLANTED) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Planted Crops ($plantedCount)",
                    color = if (uiState.cropsFilter == CropsFilter.PLANTED) White else Color(0xFFA0B09A),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (uiState.cropsFilter == CropsFilter.PLANNED) Color(0xFF2E7D32) else Color.Transparent)
                    .clickable { onFilterSelected(CropsFilter.PLANNED) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Planned / Unplanted ($plannedCount)",
                    color = if (uiState.cropsFilter == CropsFilter.PLANNED) White else Color(0xFFA0B09A),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        // ── 1.5. COMPANION & FARM ALERTS (Swipeable Carousel with Fixed Height) ─
        val relevantAlerts = remember(uiState.attentionItems, selectedPlotId, uiState.cropsFilter) {
            uiState.attentionItems.filter { alert ->
                if (selectedPlotId == null) {
                    true
                } else {
                    val label = selectedPlot?.plotLabel ?: ""
                    alert.plotLabel?.contains(label, ignoreCase = true) == true
                }
            }
        }

        if (relevantAlerts.isNotEmpty()) {
            val alertsPagerState = rememberPagerState(
                initialPage = 0,
                pageCount = { relevantAlerts.size }
            )

            LaunchedEffect(relevantAlerts.size) {
                if (alertsPagerState.currentPage >= relevantAlerts.size && relevantAlerts.isNotEmpty()) {
                    alertsPagerState.scrollToPage(relevantAlerts.size - 1)
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Header: Title + Dots indicator + Mark All Read button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "FARM ALERTS",
                            color = Color(0xFFA0B09A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        if (relevantAlerts.size > 1) {
                            Text(
                                text = "${alertsPagerState.currentPage + 1}/${relevantAlerts.size}",
                                color = Color(0xFF81C784),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // Pagination Dots
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(relevantAlerts.size.coerceAtMost(6)) { dotIndex ->
                                    val isDotSelected = alertsPagerState.currentPage == dotIndex
                                    Box(
                                        modifier = Modifier
                                            .size(if (isDotSelected) 6.dp else 4.dp)
                                            .clip(CircleShape)
                                            .background(if (isDotSelected) Color(0xFF81C784) else Color(0xFF384A33))
                                    )
                                }
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF1E2D1A)
                            ) {
                                Text(
                                    text = "1 Active",
                                    color = Color(0xFF81C784),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // Mark All as Read button
                    Text(
                        text = if (relevantAlerts.size > 1) "Mark All as Read" else "Mark Read",
                        color = Color(0xFF81C784),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onDismissAllAlerts(relevantAlerts) }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }

                // Horizontal Carousel: shows exactly 1 card at a time with fixed height
                HorizontalPager(
                    state = alertsPagerState,
                    modifier = Modifier.fillMaxWidth()
                ) { pageIdx ->
                    val alert = relevantAlerts.getOrNull(pageIdx)
                    if (alert != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 136.dp, max = 158.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF161F14),
                            border = BorderStroke(
                                1.dp,
                                if (alert.severity == AttentionSeverity.HIGH) Color(0xFF7A3333) else Color(0xFF2E3E28)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Top row: Bed badge + Severity tag + Date
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (alert.severity == AttentionSeverity.HIGH) Color(0xFF421D1D) else Color(0xFF1E2D1A)
                                        ) {
                                            Text(
                                                text = alert.plotLabel ?: "FARM BED",
                                                color = if (alert.severity == AttentionSeverity.HIGH) Color(0xFFFF8A80) else Color(0xFF81C784),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                            )
                                        }

                                        if (alert.severity == AttentionSeverity.HIGH) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color(0xFF5C1D1D)
                                            ) {
                                                Text(
                                                    text = "HIGH RISK",
                                                    color = Color(0xFFFFCDD2),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = alert.date,
                                        color = Color(0xFF8B9B85),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                // Title and concise description (max 2 lines)
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = alert.title,
                                        color = White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = alert.description,
                                        color = Color(0xFFC0CDC0),
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                // Action buttons: "Mark as Read" & "View Recommendations"
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            onDismissAlert(alert, "Marked as read")
                                        },
                                        modifier = Modifier.weight(1f).height(34.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color(0xFF384A33)),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = Color(0xFFA0B09A)
                                        ),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Mark as Read",
                                            modifier = Modifier.size(14.dp),
                                            tint = Color(0xFFA0B09A)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Mark Read",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            recommendationAlertForModal = alert
                                        },
                                        modifier = Modifier.weight(1.3f).height(34.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF2E7D32),
                                            contentColor = White
                                        ),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lightbulb,
                                            contentDescription = "View Recommendations",
                                            modifier = Modifier.size(14.dp),
                                            tint = White
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Recommendations",
                                            fontSize = 11.sp,
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

        // ── 2. BEDS CARDS (1 row, 5 columns, horizontally scrollable) ────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "FARM BEDS",
                color = White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "${uiState.plots.size} Beds",
                color = Color(0xFFA0B09A),
                fontSize = 11.sp
            )
        }

        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        // 5 columns visible per view
        val bedCardWidth = ((screenWidth - 28.dp - 24.dp) / 5).coerceAtLeast(62.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // "ALL" bed card
            val isAllSelected = selectedPlotId == null
            CompactBedCard(
                plotLabel = "ALL",
                subtitle = "All Beds",
                cropCount = baseCrops.size,
                isSelected = isAllSelected,
                onClick = { selectedPlotId = null },
                modifier = Modifier.width(bedCardWidth)
            )

            uiState.plots.forEach { plot ->
                val isSelected = selectedPlotId == plot.id
                val bedCropCount = baseCrops.count {
                    it.plotLabel.equals(plot.plotLabel, ignoreCase = true) || it.id == plot.id
                }
                CompactBedCard(
                    plotLabel = plot.plotLabel,
                    subtitle = plot.soilType.name.lowercase().replaceFirstChar { it.uppercase() },
                    cropCount = bedCropCount,
                    isSelected = isSelected,
                    onClick = {
                        selectedPlotId = if (selectedPlotId == plot.id) null else plot.id
                    },
                    modifier = Modifier.width(bedCardWidth)
                )
            }
        }

        HorizontalDivider(color = Color(0xFF222C1F), thickness = 1.dp, modifier = Modifier.padding(vertical = 2.dp))

        // ── 3. CROPS SECTION (4 rows x 4 columns with category dropdown) ───────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (selectedPlot != null) "${selectedPlot.plotLabel.uppercase()} CROPS" else "CROPS",
                    color = White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                if (filteredCrops.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1E2A1B),
                        border = BorderStroke(1.dp, Color(0xFF2E3E29))
                    ) {
                        Text(
                            text = "${filteredCrops.size}",
                            color = Color(0xFF81C784),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Category Icon Dropdown for 8 Types of Vegetables
            Box {
                var isCategoryMenuExpanded by remember { mutableStateOf(false) }
                val currentCategoryItem = VEGETABLE_CATEGORY_ITEMS.firstOrNull { it.category == selectedCategory }
                    ?: VEGETABLE_CATEGORY_ITEMS.first()

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (selectedCategory != null) Color(0xFF2E7D32).copy(alpha = 0.25f) else Color(0xFF1B2419),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (selectedCategory != null) Color(0xFF4CAF50) else Color(0xFF2E3E29)
                    ),
                    modifier = Modifier.clickable { isCategoryMenuExpanded = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = currentCategoryItem.icon,
                            fontSize = 13.sp
                        )
                        Text(
                            text = currentCategoryItem.shortName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selectedCategory != null) Color(0xFF81C784) else White
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Category Filter",
                            tint = Color(0xFFA0B09A),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Small vertical display dropdown menu
                DropdownMenu(
                    expanded = isCategoryMenuExpanded,
                    onDismissRequest = { isCategoryMenuExpanded = false },
                    modifier = Modifier
                        .background(Color(0xFF161E14))
                        .border(1.dp, Color(0xFF2B3A27), RoundedCornerShape(8.dp))
                        .widthIn(min = 180.dp, max = 230.dp)
                ) {
                    Text(
                        text = "VEGETABLE CATEGORY",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA0B09A),
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                    HorizontalDivider(color = Color(0xFF243021), thickness = 0.8.dp)

                    VEGETABLE_CATEGORY_ITEMS.forEach { item ->
                        val isSelected = (item.category == selectedCategory)
                        val count = if (item.category == null) {
                            cropsForBed.size
                        } else {
                            cropsForBed.count { getCropVegetableCategory(it.cropName) == item.category }
                        }

                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(text = item.icon, fontSize = 14.sp)
                                        Column {
                                            Text(
                                                text = item.displayName,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color(0xFF81C784) else White
                                            )
                                            Text(
                                                text = item.examples,
                                                fontSize = 9.sp,
                                                color = Color(0xFF7E8F7A)
                                            )
                                        }
                                    }
                                    if (count > 0) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isSelected) Color(0xFF2E7D32) else Color(0xFF222D1F)
                                        ) {
                                            Text(
                                                text = "$count",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) White else Color(0xFFA0B09A),
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                            },
                            onClick = {
                                selectedCategory = item.category
                                isCategoryMenuExpanded = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .background(if (isSelected) Color(0xFF1E2B1B) else Color.Transparent),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        if (filteredCrops.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141C12)),
                border = BorderStroke(1.dp, Color(0xFF2B3825))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "🌱", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = when {
                            selectedCategory != null -> "No ${selectedCategory?.name?.lowercase() ?: ""} vegetables in this selection"
                            selectedPlot != null -> "No ${if (uiState.cropsFilter == CropsFilter.PLANTED) "planted" else "planned"} crops in ${selectedPlot.plotLabel}"
                            else -> "No ${if (uiState.cropsFilter == CropsFilter.PLANTED) "planted" else "planned"} crops found"
                        },
                        color = White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            // Horizontal Pager: 4 rows x 4 columns = 16 crops per page
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { pageIdx ->
                val pageItems = cropPages.getOrNull(pageIdx) ?: emptyList()
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (rowIdx in 0 until 4) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (colIdx in 0 until 4) {
                                val itemIndex = rowIdx * 4 + colIdx
                                val item = pageItems.getOrNull(itemIndex)
                                if (item != null) {
                                    GridCropCard(
                                        plant = item,
                                        onClick = {
                                            if (uiState.cropsFilter == CropsFilter.PLANTED) {
                                                onOpenPlantDetails(item)
                                            } else {
                                                val plot = uiState.plots.firstOrNull { it.id == item.id }
                                                if (plot != null) onOpenPlotDetails(plot) else onOpenPlantDetails(item)
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            // ── Number tabs below 1 2 3 ... ─────────────────────────────────
            if (cropPages.size > 1) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF141C12),
                    border = BorderStroke(1.dp, Color(0xFF222C1F)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Page ${pagerState.currentPage + 1} of ${cropPages.size} (${filteredCrops.size} Crops)",
                            color = Color(0xFFA0B09A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            cropPages.indices.forEach { pageIdx ->
                                val isPageActive = pagerState.currentPage == pageIdx
                                Surface(
                                    shape = RoundedCornerShape(5.dp),
                                    color = if (isPageActive) Color(0xFF4CAF50) else Color(0xFF1E281B),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = if (isPageActive) Color(0xFF81C784) else Color(0xFF2E3E29)
                                    ),
                                    modifier = Modifier.clickable {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(pageIdx)
                                        }
                                    }
                                ) {
                                    Box(
                                        modifier = Modifier.size(width = 28.dp, height = 26.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${pageIdx + 1}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isPageActive) White else Color(0xFFA0B09A)
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

    val currentModalAlert = recommendationAlertForModal
    if (currentModalAlert != null) {
        CompanionRecommendationDialog(
            alert = currentModalAlert,
            onDismiss = { recommendationAlertForModal = null },
            onAcknowledge = {
                onDismissAlert(currentModalAlert, "Recommendation Handled")
                recommendationAlertForModal = null
            }
        )
    }
}

@Composable
private fun CompanionRecommendationDialog(
    alert: FarmAttentionItem,
    onDismiss: () -> Unit,
    onAcknowledge: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF141C13),
            border = BorderStroke(1.dp, Color(0xFF2B3A26))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header with Bed and Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF1E2D1A)
                    ) {
                        Text(
                            text = alert.plotLabel ?: "FARM BED",
                            color = Color(0xFF81C784),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = alert.date,
                        color = Color(0xFF8B9B85),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = alert.title,
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1A2418),
                    border = BorderStroke(1.dp, Color(0xFF283624)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "OBSERVATION & CONTEXT",
                            color = Color(0xFF81C784),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = alert.description,
                            color = Color(0xFFC0CDC0),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1F2B1B),
                    border = BorderStroke(1.dp, Color(0xFF33472C)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "RECOMMENDED ACTION & COMPANIONS",
                            color = Color(0xFFA5D6A7),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = alert.companionRecommendation
                                ?: "Intercrop with aromatic herbs like Basil, Marigold, or Green Onion to deter insects and improve soil biology.",
                            color = White,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF384A33)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFA0B09A))
                    ) {
                        Text("Close", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onAcknowledge,
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32),
                            contentColor = White
                        )
                    ) {
                        Text("Acknowledge & Record", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactBedCard(
    plotLabel: String,
    subtitle: String,
    cropCount: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF1E2F1C) else Color(0xFF141C12)
        ),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) Color(0xFF4CAF50) else Color(0xFF2B3825)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 6.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = plotLabel.uppercase(),
                color = if (isSelected) Color(0xFF81C784) else White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = subtitle,
                color = Color(0xFFA0B09A),
                fontSize = 8.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(3.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (isSelected) Color(0xFF2E442B) else Color(0xFF1F291B))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = if (cropCount > 0) "$cropCount" else "0",
                    color = if (cropCount > 0) Color(0xFF81C784) else Color(0xFF758570),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun GridCropCard(
    plant: MonitoredPlant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161E14)),
        border = BorderStroke(1.dp, Color(0xFF2B3825))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val imageModel = CropMetadataAssetDataSource.resolveCropImage(plant.cropId, plant.cropName, plant.imageUrl)
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageModel)
                    .crossfade(true)
                    .build(),
                contentDescription = plant.cropName,
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF222C1F)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = plant.cropName,
                color = White,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Text(
                text = plant.plotLabel,
                color = Color(0xFF81C784),
                fontSize = 8.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(3.dp))

            val isPlanned = plant.healthStatus.contains("Planned", true)
            val isOverdue = plant.healthStatus.contains("Overdue", true)
            val isReady = plant.healthStatus.contains("Ready", true)
            val badgeColor = when {
                isPlanned -> Color(0xFFFFA000)
                isOverdue -> Color(0xFFE53935)
                isReady -> Color(0xFFFFB300)
                else -> Color(0xFF4CAF50)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(3.dp))
                    .padding(vertical = 1.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isPlanned) "Planned" else "D${plant.daysPlanted}",
                    color = badgeColor,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun PlantedCropCard(
    plant: MonitoredPlant,
    onOpenDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenDetails() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF182216)),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF2C3927)))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Crop Thumbnail Image
                val imageModel = CropMetadataAssetDataSource.resolveCropImage(plant.cropId, plant.cropName, plant.imageUrl)
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageModel)
                        .crossfade(true)
                        .build(),
                    contentDescription = plant.cropName,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF232D20)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = plant.cropName,
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )

                        // Status Badge
                        val isOverdue = plant.healthStatus.contains("Overdue", true)
                        val isReady = plant.healthStatus.contains("Ready", true)
                        val statusColor = when {
                            isOverdue -> Color(0xFFE53935)
                            isReady -> Color(0xFFFFA000)
                            else -> Color(0xFF81C784)
                        }
                        Box(
                            modifier = Modifier
                                .background(statusColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .border(1.dp, statusColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isOverdue) "Overdue" else if (isReady) "Harvest Ready" else "Active",
                                color = statusColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "Variety: ${plant.cropVariety ?: "Standard"} • ${plant.plotLabel}",
                        color = Color(0xFFA0B09A),
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Planted: ${plant.rawPlantedDate?.take(10) ?: "N/A"} (${plant.daysPlanted} days)",
                        color = Color(0xFFC0D0BA),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar & Growth Stage
            val progressRatio = if (plant.daysToHarvest > 0) (plant.daysPlanted.toFloat() / plant.daysToHarvest).coerceIn(0f, 1f) else 0f
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = plant.stageName,
                    color = Color(0xFF81C784),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${(progressRatio * 100).toInt()}% Growth",
                    color = Color(0xFFA0B09A),
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { progressRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Color(0xFF4CAF50),
                trackColor = Color(0xFF263322),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action: Open Crop Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Open Crop Details",
                    color = ForestGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Details",
                    tint = ForestGreen,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun PlannedPlotCard(
    plot: CropPlot,
    onOpenDetails: () -> Unit,
    onStartPlanting: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenDetails() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF182216)),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF3A3120)))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = plot.cropName ?: "Unassigned Bed",
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "${plot.plotLabel} • Soil: ${plot.soilType.name.lowercase().replaceFirstChar { it.uppercase() }}",
                        color = Color(0xFFA0B09A),
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .background(Color(0xFFFFA000).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .border(1.dp, Color(0xFFFFA000).copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (plot.cropName != null) "Planned" else "Empty Bed",
                        color = Color(0xFFFFA000),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Variety: ${plot.cropVariety ?: "Not Specified"}",
                    color = Color(0xFFC0D0BA),
                    fontSize = 11.sp
                )

                Button(
                    onClick = onStartPlanting,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("🌱 Start Planting", fontSize = 11.sp, color = White)
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// TAB 3: CALENDAR (Farming-Focused Timeline)
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun FarmCalendarTab(
    uiState: FarmUiState,
    onDateSelected: (LocalDate) -> Unit,
    onClearDateFilter: () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onFilterSelected: (CalendarEventType?) -> Unit,
    onEventClick: (FarmTimelineEvent) -> Unit,
    onCompleteTask: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE || configuration.screenWidthDp >= 680

    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Left pane: Monthly calendar overview
            Box(
                modifier = Modifier
                    .weight(0.48f)
                    .fillMaxHeight()
            ) {
                MonthlyCalendarCard(
                    month = uiState.calendarMonth,
                    selectedDate = uiState.selectedCalendarDate,
                    isDateFilterActive = uiState.isCalendarDateFilterActive,
                    timelineEvents = uiState.timelineEvents,
                    onDateSelected = onDateSelected,
                    onPreviousMonth = onPreviousMonth,
                    onNextMonth = onNextMonth
                )
            }

            // Right pane: Schedule timeline and activity list
            Box(
                modifier = Modifier
                    .weight(0.52f)
                    .fillMaxHeight()
            ) {
                CalendarEventsSection(
                    uiState = uiState,
                    onClearDateFilter = onClearDateFilter,
                    onFilterSelected = onFilterSelected,
                    onEventClick = onEventClick,
                    onCompleteTask = onCompleteTask
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            MonthlyCalendarCard(
                month = uiState.calendarMonth,
                selectedDate = uiState.selectedCalendarDate,
                isDateFilterActive = uiState.isCalendarDateFilterActive,
                timelineEvents = uiState.timelineEvents,
                onDateSelected = onDateSelected,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(modifier = Modifier.weight(1f)) {
                CalendarEventsSection(
                    uiState = uiState,
                    onClearDateFilter = onClearDateFilter,
                    onFilterSelected = onFilterSelected,
                    onEventClick = onEventClick,
                    onCompleteTask = onCompleteTask
                )
            }
        }
    }
}

@Composable
private fun MonthlyCalendarCard(
    month: YearMonth,
    selectedDate: LocalDate,
    isDateFilterActive: Boolean,
    timelineEvents: List<FarmTimelineEvent>,
    onDateSelected: (LocalDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161F14)),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFF2C3927))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Month navigation header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onPreviousMonth,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Month",
                        tint = Color(0xFF81C784),
                        modifier = Modifier.size(18.dp)
                    )
                }

                val monthTitle = month.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))
                Text(
                    text = monthTitle.uppercase(),
                    color = White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                IconButton(
                    onClick = onNextMonth,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Month",
                        tint = Color(0xFF81C784),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Weekday labels (SUN - SAT)
            val weekDays = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weekDays.forEach { dayName ->
                    Text(
                        text = dayName,
                        color = Color(0xFFA0B09A),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Calendar days grid
            val firstDayOfMonth = month.atDay(1)
            val startDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 0 = Sunday
            val totalDays = month.lengthOfMonth()
            val totalCells = startDayOfWeek + totalDays
            val totalRows = (totalCells + 6) / 7

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (row in 0 until totalRows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (col in 0 until 7) {
                            val cellIndex = row * 7 + col
                            val dayNumber = cellIndex - startDayOfWeek + 1
                            if (dayNumber in 1..totalDays) {
                                val cellDate = month.atDay(dayNumber)
                                val isSelected = isDateFilterActive && cellDate == selectedDate
                                val isToday = cellDate == LocalDate.now()
                                val dayEvents = timelineEvents.filter { it.date == cellDate }

                                CalendarDayCell(
                                    dayNumber = dayNumber,
                                    isSelected = isSelected,
                                    isToday = isToday,
                                    events = dayEvents,
                                    onClick = { onDateSelected(cellDate) },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    dayNumber: Int,
    isSelected: Boolean,
    isToday: Boolean,
    events: List<FarmTimelineEvent>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isSelected -> Color(0xFF2E7D32)
        isToday -> Color(0xFF23361E)
        events.isNotEmpty() -> Color(0xFF1C2719)
        else -> Color.Transparent
    }

    val border = when {
        isSelected -> BorderStroke(1.5.dp, Color(0xFF81C784))
        isToday -> BorderStroke(1.dp, Color(0xFF66BB6A))
        events.isNotEmpty() -> BorderStroke(0.5.dp, Color(0xFF2E3D29))
        else -> null
    }

    Box(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .then(if (border != null) Modifier.border(border, RoundedCornerShape(8.dp)) else Modifier)
            .clickable { onClick() }
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$dayNumber",
                color = when {
                    isSelected -> White
                    isToday -> Color(0xFF81C784)
                    events.isNotEmpty() -> Color(0xFFE2EBE0)
                    else -> Color(0xFF7A8C74)
                },
                fontSize = 12.sp,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Medium
            )

            if (events.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val displayEvents = events.take(3)
                    displayEvents.forEach { event ->
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(
                                    if (isSelected) White else getEventColor(event.type),
                                    CircleShape
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarEventsSection(
    uiState: FarmUiState,
    onClearDateFilter: () -> Unit,
    onFilterSelected: (CalendarEventType?) -> Unit,
    onEventClick: (FarmTimelineEvent) -> Unit,
    onCompleteTask: (String) -> Unit
) {
    val filteredEvents = uiState.timelineEvents.filter { event ->
        val matchesDate = !uiState.isCalendarDateFilterActive || event.date == uiState.selectedCalendarDate
        val matchesType = uiState.calendarFilter == null || event.type == uiState.calendarFilter
        matchesDate && matchesType
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Section Header with Date Context and Clear Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (uiState.isCalendarDateFilterActive) {
                    Text(
                        text = "SCHEDULE FOR ${uiState.selectedCalendarDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())).uppercase()}",
                        color = Color(0xFF81C784),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                } else {
                    Text(
                        text = "ALL SCHEDULED ACTIVITIES",
                        color = White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = "${filteredEvents.size} event(s)",
                    color = Color(0xFFA0B09A),
                    fontSize = 11.sp
                )
            }

            if (uiState.isCalendarDateFilterActive) {
                TextButton(
                    onClick = onClearDateFilter,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Show All",
                        tint = Color(0xFF81C784),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Show All Dates",
                        color = Color(0xFF81C784),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal filter chips: All, Planting, Growth, Monitoring, Crop Care, Tasks, Harvest
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = uiState.calendarFilter == null,
                onClick = { onFilterSelected(null) },
                label = { Text("All (${uiState.timelineEvents.size})", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF2E7D32),
                    selectedLabelColor = White,
                    containerColor = Color(0xFF1A2317),
                    labelColor = Color(0xFFA0B09A)
                )
            )

            CalendarEventType.values().forEach { type ->
                val typeCount = uiState.timelineEvents.count { it.type == type }
                FilterChip(
                    selected = uiState.calendarFilter == type,
                    onClick = { onFilterSelected(if (uiState.calendarFilter == type) null else type) },
                    label = { Text("${type.icon} ${type.label} ($typeCount)", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF2E7D32),
                        selectedLabelColor = White,
                        containerColor = Color(0xFF1A2317),
                        labelColor = Color(0xFFA0B09A)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Events list or empty placeholder
        if (filteredEvents.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF182216)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF273523))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = "No events",
                        tint = Color(0xFF6B7C66),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (uiState.isCalendarDateFilterActive)
                            "No scheduled activities for this date."
                        else
                            "No activities matching the selected filter.",
                        color = Color(0xFFA0B09A),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    if (uiState.isCalendarDateFilterActive) {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = onClearDateFilter) {
                            Text(
                                text = "View All Scheduled Dates",
                                color = Color(0xFF81C784),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredEvents, key = { it.id }) { event ->
                    CalendarEventCard(
                        event = event,
                        onEventClick = onEventClick,
                        onCompleteTask = onCompleteTask
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarEventCard(
    event: FarmTimelineEvent,
    onEventClick: (FarmTimelineEvent) -> Unit,
    onCompleteTask: (String) -> Unit
) {
    val borderColor = getEventColor(event.type)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEventClick(event) },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF182216)),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFF2C3927))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Visual indicator left pill
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(42.dp)
                    .background(borderColor, RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Emoji type badge
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF233020)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = event.type.icon, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "${event.subtitle} • 📅 ${event.date.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault()))}",
                    color = Color(0xFFA0B09A),
                    fontSize = 11.sp
                )
                if (!event.extraInfo.isNullOrBlank()) {
                    Text(
                        text = event.extraInfo,
                        color = Color(0xFF81C784),
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Quick task action if DSS_TASK or CROP_CARE with task id
            if ((event.type == CalendarEventType.DSS_TASK || event.type == CalendarEventType.CROP_CARE) && !event.isCompleted && !event.taskId.isNullOrBlank()) {
                IconButton(
                    onClick = { onCompleteTask(event.taskId) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircleOutline,
                        contentDescription = "Complete Task",
                        tint = Color(0xFF81C784)
                    )
                }
            } else if (event.isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = Color(0xFF81C784),
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "View Details",
                    tint = Color(0xFF6B7C66),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

private fun getEventColor(type: CalendarEventType): Color = when (type) {
    CalendarEventType.PLANTING -> Color(0xFF4CAF50)
    CalendarEventType.GROWTH_STAGE -> Color(0xFF2E7D32)
    CalendarEventType.MONITORING -> Color(0xFF00B0FF)
    CalendarEventType.CROP_CARE -> Color(0xFF26A69A)
    CalendarEventType.DSS_TASK -> Color(0xFFFFA000)
    CalendarEventType.HARVEST -> Color(0xFFFFD54F)
    CalendarEventType.COMPLETED -> Color(0xFF757575)
}

// ═══════════════════════════════════════════════════════════════════════════════
// TAB 4: ACTIVITY HISTORY (Historical Farm Records)
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun FarmActivityTab(
    uiState: FarmUiState,
    onFilterSelected: (ActivityCategory) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        Text(
            text = "HISTORICAL FARM RECORDS",
            color = White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter chips: All, Crop Added, Crop Planted, Monitored, Task Done, Harvested
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ActivityCategory.values().forEach { cat ->
                FilterChip(
                    selected = uiState.selectedActivityFilter == cat,
                    onClick = { onFilterSelected(cat) },
                    label = { Text(cat.label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF2E7D32),
                        selectedLabelColor = White,
                        containerColor = Color(0xFF1A2317),
                        labelColor = Color(0xFFA0B09A)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val filteredActivities = uiState.activityHistory.filter { item ->
            uiState.selectedActivityFilter == ActivityCategory.ALL || item.category == uiState.selectedActivityFilter
        }

        var activityLimit by remember(uiState.selectedActivityFilter) { mutableIntStateOf(20) }

        if (filteredActivities.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No farm activity records match this filter.",
                    color = Color(0xFFA0B09A),
                    fontSize = 13.sp
                )
            }
        } else {
            val pagedActivities = filteredActivities.take(activityLimit)
            val grouped = pagedActivities.groupBy { act ->
                try {
                    val dateOnly = act.timestamp.take(10)
                    val actDate = LocalDate.parse(dateOnly)
                    val today = LocalDate.now()
                    when {
                        actDate == today -> "Today"
                        actDate == today.minusDays(1) -> "Yesterday"
                        actDate.isAfter(today.minusDays(7)) -> "This Week"
                        actDate.isAfter(today.minusDays(30)) -> "Earlier this Month"
                        else -> "Earlier Seasons"
                    }
                } catch (_: Exception) {
                    "Recent Activity"
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                grouped.forEach { (header, items) ->
                    item(key = "header_$header") {
                        Text(
                            text = header.uppercase(),
                            color = Color(0xFF81C784),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                        )
                    }
                    items(items, key = { it.id }) { act ->
                        ActivityHistoryCard(act = act)
                    }
                }

                if (filteredActivities.size > activityLimit) {
                    item(key = "load_older_activities") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            OutlinedButton(
                                onClick = { activityLimit += 20 },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFF2E7D32)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFF81C784)
                                )
                            ) {
                                Text(
                                    text = "Load Older Activities (+20 of ${filteredActivities.size - activityLimit} remaining)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
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
private fun ActivityHistoryCard(act: FarmHistoryItem) {
    val (icon, badgeColor) = when (act.category) {
        ActivityCategory.CROP_ADDED -> Pair("➕", Color(0xFF00897B))
        ActivityCategory.CROP_PLANTED -> Pair("🌱", Color(0xFF4CAF50))
        ActivityCategory.MONITORING_RECORDED -> Pair("🔍", Color(0xFF2196F3))
        ActivityCategory.ACTIVITY_COMPLETED -> Pair("✓", Color(0xFF8BC34A))
        ActivityCategory.HARVEST_RECORDED -> Pair("🌾", Color(0xFFFFB300))
        else -> Pair("📋", Color(0xFF9E9E9E))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF182216)),
        shape = RoundedCornerShape(10.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF2C3927)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(badgeColor.copy(alpha = 0.2f), CircleShape)
                    .border(1.dp, badgeColor.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = act.title,
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = act.timestamp.take(10),
                        color = Color(0xFFA0B09A),
                        fontSize = 10.sp
                    )
                }

                Text(
                    text = act.subtitle,
                    color = Color(0xFFB0BFAC),
                    fontSize = 11.sp
                )

                if (!act.details.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = act.details,
                        color = Color(0xFF8A9A84),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// MODAL 1: CROP DSS MANAGEMENT DIALOG — Moved to reusable CropDssManagementDialog

// ═══════════════════════════════════════════════════════════════════════════════
// MODAL 2: INTERACTIVE RECORD OBSERVATION DIALOG (The Question, ABC, Selection)
// Handled by reusable AddLogDialog
// ═══════════════════════════════════════════════════════════════════════════════
