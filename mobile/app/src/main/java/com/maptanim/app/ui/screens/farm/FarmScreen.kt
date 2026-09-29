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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.maptanim.app.domain.model.FarmTask
import com.maptanim.app.domain.model.TaskType
import com.maptanim.app.navigation.Routes
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmScreen(
    navController: NavController,
    viewModel: FarmViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isMenuExpanded by remember { mutableStateOf(false) }

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
                onBack = { navController.popBackStack() },
                onMenuClick = { isMenuExpanded = true }
            )

            // Optional Farm Actions Menu
            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false },
                modifier = Modifier.background(Color(0xFF1F291A))
            ) {
                DropdownMenuItem(
                    text = { Text("✏️ Edit Farm Layout", color = White) },
                    onClick = {
                        isMenuExpanded = false
                        navController.navigate(Routes.EDIT)
                    }
                )
                DropdownMenuItem(
                    text = { Text("🔄 Refresh Farm Data", color = White) },
                    onClick = {
                        isMenuExpanded = false
                        viewModel.refreshData()
                    }
                )
            }

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
                        FarmOverviewTab(
                            uiState = uiState,
                            onEditFarm = { navController.navigate(Routes.EDIT) },
                            onCompleteTask = { viewModel.completeTask(it) },
                            onOpenPlantDetails = { viewModel.openCropDetails(it) },
                            onOpenPlotDetails = { viewModel.openPlotDetails(it) },
                            onViewActivities = { viewModel.selectTab(FarmTab.ACTIVITY) }
                        )
                    }
                    FarmTab.CROPS -> {
                        FarmCropsTab(
                            uiState = uiState,
                            onFilterSelected = { viewModel.selectCropsFilter(it) },
                            onOpenPlantDetails = { viewModel.openCropDetails(it) },
                            onOpenPlotDetails = { viewModel.openPlotDetails(it) },
                            onStartPlanting = { plotId -> viewModel.startPlantingNow(plotId) }
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
                    FarmTab.MONITOR -> {
                        FarmMonitorTab(
                            uiState = uiState,
                            onRecordObservationClick = { plot -> viewModel.openObservationDialog(plot) }
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

    // ── MODALS ────────────────────────────────────────────────────────────────
    // 1. Crop Details Dialog
    if (uiState.selectedPlantForDetails != null || uiState.selectedPlotForDetails != null) {
        CropDetailsDialog(
            plant = uiState.selectedPlantForDetails,
            plot = uiState.selectedPlotForDetails,
            onDismiss = { viewModel.closeCropDetails() },
            onRecordObservation = { plot ->
                viewModel.closeCropDetails()
                viewModel.openObservationDialog(plot)
            },
            onStartPlanting = { plotId ->
                viewModel.startPlantingNow(plotId)
            },
            onHarvest = { plotId ->
                viewModel.recordHarvest(plotId)
            }
        )
    }

    // 2. Record Observation Dialog
    if (uiState.isObservationModalOpen) {
        RecordObservationDialog(
            initialPlot = uiState.observationTargetPlot,
            availablePlots = uiState.plots.filter { it.cropName != null },
            onDismiss = { viewModel.closeObservationDialog() },
            onSubmit = { plotId, stage, condition, pestNotes, notes ->
                viewModel.recordObservation(plotId, stage, condition, pestNotes, notes)
            }
        )
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
    onBack: () -> Unit,
    onMenuClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
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
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = farmName.uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = White,
                letterSpacing = 1.sp,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Farm Actions",
                    tint = Color(0xFFA0B09A)
                )
            }
        }
        HorizontalDivider(color = Color(0xFF243021), thickness = 1.dp)
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

@Composable
private fun FarmOverviewTab(
    uiState: FarmUiState,
    onEditFarm: () -> Unit,
    onCompleteTask: (String) -> Unit,
    onOpenPlantDetails: (MonitoredPlant) -> Unit,
    onOpenPlotDetails: (CropPlot) -> Unit,
    onViewActivities: () -> Unit
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ── 1. FARM LAYOUT ───────────────────────────────────────────────────
        Text(
            text = "FARM LAYOUT",
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )

        ActualFarmLayoutCard(
            plots = uiState.plots,
            onEditFarm = onEditFarm,
            onOpenPlantDetails = onOpenPlantDetails,
            onOpenPlotDetails = onOpenPlotDetails,
            plantedPlants = uiState.plantedPlants
        )

        HorizontalDivider(color = Color(0xFF222C1F), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

        // ── 2. FARM ATTENTION ────────────────────────────────────────────────
        Text(
            text = "FARM ATTENTION",
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )

        if (uiState.attentionItems.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "✅", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (uiState.plots.isEmpty()) "No active crops to monitor" else "All crops healthy • No immediate attention required",
                    color = Color(0xFFA0B09A),
                    fontSize = 13.sp
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                uiState.attentionItems.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                val plant = uiState.plantedPlants.firstOrNull {
                                    it.plotLabel.contains(item.plotLabel ?: "", true) ||
                                    item.plotLabel?.contains(it.cropName, true) == true
                                }
                                if (plant != null) onOpenPlantDetails(plant)
                            }
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (item.severity == AttentionSeverity.HIGH) "🔴" else "🟡",
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = item.plotLabel ?: item.title,
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.width(105.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item.description,
                            color = Color(0xFFBAC7B6),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "›",
                            color = Color(0xFFA0B09A),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = Color(0xFF222C1F), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

        // ── 3. TODAY ─────────────────────────────────────────────────────────
        Text(
            text = "TODAY",
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )

        if (uiState.todayTasks.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "○", fontSize = 16.sp, color = Color(0xFF6B7C66))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (uiState.plots.isEmpty()) "No tasks scheduled for today" else "All scheduled tasks completed",
                    color = Color(0xFFA0B09A),
                    fontSize = 13.sp
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                uiState.todayTasks.forEach { task ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onCompleteTask(task.id) }
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (task.isCompleted) "●" else "○",
                            color = if (task.isCompleted) Color(0xFF81C784) else Color(0xFFA0B09A),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = task.title,
                            color = if (task.isCompleted) Color(0xFF8A9A84) else White,
                            fontSize = 13.sp,
                            fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = task.subLabel ?: "08:00 AM",
                            color = Color(0xFFA0B09A),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActualFarmLayoutCard(
    plots: List<CropPlot>,
    onEditFarm: () -> Unit,
    onOpenPlantDetails: (MonitoredPlant) -> Unit,
    onOpenPlotDetails: (CropPlot) -> Unit,
    plantedPlants: List<MonitoredPlant>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141C12)),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF2B3825)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
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
                    onPlotClick = { plot ->
                        val plant = plantedPlants.firstOrNull { it.id == plot.id }
                        if (plant != null) {
                            onOpenPlantDetails(plant)
                        } else {
                            onOpenPlotDetails(plot)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // [ Edit Farm ] button at bottom right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onEditFarm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32),
                        contentColor = White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Edit Farm",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
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
// TAB 2: CROPS (Separates Planted and Planned/Unplanted)
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun FarmCropsTab(
    uiState: FarmUiState,
    onFilterSelected: (CropsFilter) -> Unit,
    onOpenPlantDetails: (MonitoredPlant) -> Unit,
    onOpenPlotDetails: (CropPlot) -> Unit,
    onStartPlanting: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        // Segmented Control: Planted vs Planned/Unplanted
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

        Spacer(modifier = Modifier.height(14.dp))

        // Crop List
        if (uiState.cropsFilter == CropsFilter.PLANTED) {
            if (uiState.plantedPlants.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No planted crops recorded yet.\nCheck the Planned tab to start planting.",
                        color = Color(0xFFA0B09A),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.plantedPlants, key = { it.id }) { plant ->
                        PlantedCropCard(
                            plant = plant,
                            onOpenDetails = { onOpenPlantDetails(plant) }
                        )
                    }
                }
            }
        } else {
            if (uiState.plannedPlots.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No planned or unplanted beds.\nAll configured beds currently have active plantings.",
                        color = Color(0xFFA0B09A),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.plannedPlots, key = { it.id }) { plot ->
                        PlannedPlotCard(
                            plot = plot,
                            onOpenDetails = { onOpenPlotDetails(plot) },
                            onStartPlanting = { onStartPlanting(plot.id) }
                        )
                    }
                }
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
// TAB 4: MONITOR (Observations & Tracking Feeding DSS)
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun FarmMonitorTab(
    uiState: FarmUiState,
    onRecordObservationClick: (CropPlot?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        // Header with "+ Record Observation" button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CROP OBSERVATIONS",
                    color = White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Directly feeds the DSS Engine",
                    color = Color(0xFFA0B09A),
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = { onRecordObservationClick(null) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Record",
                    tint = White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Record Observation", fontSize = 12.sp, color = White)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (uiState.monitorItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No active plantings to monitor.\nPlant crops first to track conditions.",
                    color = Color(0xFFA0B09A),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.monitorItems, key = { it.plot.id }) { item ->
                    MonitorCard(
                        item = item,
                        onRecord = { onRecordObservationClick(item.plot) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MonitorCard(
    item: CropMonitorCardData,
    onRecord: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF182216)),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF2C3927)))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = item.crop?.name ?: item.plot.cropName ?: "Vegetable",
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "${item.plot.plotLabel} • Variety: ${item.plot.cropVariety ?: "Standard"}",
                        color = Color(0xFFA0B09A),
                        fontSize = 11.sp
                    )
                }

                // Stage pill
                Box(
                    modifier = Modifier
                        .background(Color(0xFF2E7D32).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .border(1.dp, Color(0xFF4CAF50).copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.currentStageName,
                        color = Color(0xFF81C784),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Observation Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF202A1E), RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row {
                    Text("Condition: ", color = Color(0xFFA0B09A), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(item.plantCondition, color = White, fontSize = 11.sp)
                }
                Row {
                    Text("Pest/Disease: ", color = Color(0xFFA0B09A), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(item.pestObservation, color = Color(0xFFFFCC80), fontSize = 11.sp)
                }
                Row {
                    Text("Observation: ", color = Color(0xFFA0B09A), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(item.growthObservation, color = Color(0xFFD0DFCC), fontSize = 11.sp)
                }
                if (item.lastRecordedAt != null) {
                    Text(
                        text = "Last recorded: ${item.lastRecordedAt}",
                        color = Color(0xFF8A9A84),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onRecord,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ForestGreen),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = "Log Observation",
                        tint = ForestGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Record Observation", fontSize = 11.sp, color = ForestGreen)
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// TAB 5: ACTIVITY (Historical Farm Records)
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

        Spacer(modifier = Modifier.height(12.dp))

        val filteredActivities = uiState.activityHistory.filter { item ->
            uiState.selectedActivityFilter == ActivityCategory.ALL || item.category == uiState.selectedActivityFilter
        }

        if (filteredActivities.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No farm activity records match this filter.",
                    color = Color(0xFFA0B09A),
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredActivities, key = { it.id }) { act ->
                    ActivityHistoryCard(act = act)
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

// ═══════════════════════════════════════════════════════════════════════════════
// MODAL 1: CROP DETAILS DIALOG
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun CropDetailsDialog(
    plant: MonitoredPlant?,
    plot: CropPlot?,
    onDismiss: () -> Unit,
    onRecordObservation: (CropPlot) -> Unit,
    onStartPlanting: (String) -> Unit,
    onHarvest: (String) -> Unit
) {
    val cropName = plant?.cropName ?: plot?.cropName ?: "Vegetable"
    val plotLabel = plant?.plotLabel ?: plot?.plotLabel ?: "Bed"
    val variety = plant?.cropVariety ?: plot?.cropVariety ?: "Standard Variety"
    val soil = plant?.soilType ?: plot?.soilType
    val isPlanted = plant != null || plot?.plantedDate != null
    val plotId = plant?.id ?: plot?.id ?: ""

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161E14)),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF2E3E29)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = cropName,
                            color = White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$plotLabel • $variety",
                            color = Color(0xFFA0B09A),
                            fontSize = 12.sp
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Image Hero
                val imageModel = CropMetadataAssetDataSource.resolveCropImage(plant?.cropId ?: plot?.cropId, cropName, plant?.imageUrl)
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageModel)
                        .crossfade(true)
                        .build(),
                    contentDescription = cropName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF222C1F)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quick stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OverviewMetricCard("Soil", soil?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Loam", Color(0xFF4CAF50), Modifier.weight(1f))
                    OverviewMetricCard("Status", if (isPlanted) "Planted" else "Planned", if (isPlanted) Color(0xFF4CAF50) else Color(0xFFFFA000), Modifier.weight(1f))
                    if (plant != null) {
                        OverviewMetricCard("Days", "${plant.daysPlanted}/${plant.daysToHarvest}d", Color(0xFF42A5F5), Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Companion & Care Guidance
                if (plant != null) {
                    Text("COMPANION ANALYSIS", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(plant.companionStatus, color = Color(0xFFC0D0BA), fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("GROWING TIP", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(plant.growingTip, color = Color(0xFFC0D0BA), fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("PEST SURVEILLANCE", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(plant.pestInfo, color = Color(0xFFFFCC80), fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                if (isPlanted) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (plot != null) onRecordObservation(plot)
                                else if (plant != null) {
                                    // Construct dummy CropPlot to pass
                                    val dummyPlot = CropPlot(
                                        id = plant.id,
                                        farmId = plant.farmId,
                                        plotLabel = plant.plotLabel,
                                        cropName = plant.cropName,
                                        cropId = plant.cropId,
                                        cropVariety = plant.cropVariety,
                                        soilType = plant.soilType,
                                        posX = 0f,
                                        posY = 0f,
                                        widthM = 10f,
                                        heightM = 5f,
                                        plantedDate = plant.rawPlantedDate
                                    )
                                    onRecordObservation(dummyPlot)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E7D32))
                        ) {
                            Text("📝 Observation", fontSize = 12.sp, color = ForestGreen)
                        }

                        Button(
                            onClick = { onHarvest(plotId) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("🌾 Harvest", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Button(
                        onClick = { onStartPlanting(plotId) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("🌱 Start Planting Now", fontSize = 13.sp, color = White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// MODAL 2: RECORD OBSERVATION DIALOG (Feeds directly to DSS)
// ═══════════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecordObservationDialog(
    initialPlot: CropPlot?,
    availablePlots: List<CropPlot>,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String, String) -> Unit
) {
    var selectedPlotId by remember { mutableStateOf(initialPlot?.id ?: availablePlots.firstOrNull()?.id ?: "") }
    var selectedStage by remember { mutableStateOf("Vegetative") }
    var selectedCondition by remember { mutableStateOf("Healthy & Vigorously Growing") }
    var pestNotes by remember { mutableStateOf("No pests observed") }
    var generalNotes by remember { mutableStateOf("") }

    val stages = listOf("Sprout", "Seedling", "Vegetative", "Flowering", "Fruiting", "Mature")
    val conditions = listOf(
        "Healthy & Vigorously Growing",
        "Mild Water Stress",
        "Nutrient Deficiency",
        "Pest / Disease Detected",
        "Wilting / Stunted"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161E14)),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF2E3E29)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Record Crop Observation",
                            color = White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Updates DSS rules & alerts immediately",
                            color = Color(0xFFA0B09A),
                            fontSize = 11.sp
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bed selection if multiple
                Text("Select Bed / Crop:", color = Color(0xFFC0D0BA), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availablePlots.forEach { plot ->
                        val isSelected = plot.id == selectedPlotId
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedPlotId = plot.id },
                            label = { Text("${plot.plotLabel} (${plot.cropName})", fontSize = 11.sp) },
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

                // Growth Stage
                Text("Observed Growth Stage:", color = Color(0xFFC0D0BA), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    stages.forEach { stage ->
                        FilterChip(
                            selected = stage == selectedStage,
                            onClick = { selectedStage = stage },
                            label = { Text(stage, fontSize = 11.sp) },
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

                // Condition
                Text("Plant Condition:", color = Color(0xFFC0D0BA), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    conditions.forEach { cond ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (cond == selectedCondition) Color(0xFF202E1D) else Color.Transparent)
                                .clickable { selectedCondition = cond }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            RadioButton(
                                selected = cond == selectedCondition,
                                onClick = { selectedCondition = cond },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF4CAF50), unselectedColor = Color(0xFF8A9A84))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = cond, color = White, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Pest observations
                OutlinedTextField(
                    value = pestNotes,
                    onValueChange = { pestNotes = it },
                    label = { Text("Pest / Disease Presence", color = Color(0xFFA0B09A)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = White,
                        focusedBorderColor = Color(0xFF4CAF50),
                        unfocusedBorderColor = Color(0xFF2A3824)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Additional notes
                OutlinedTextField(
                    value = generalNotes,
                    onValueChange = { generalNotes = it },
                    label = { Text("Detailed Observation Notes", color = Color(0xFFA0B09A)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = White,
                        focusedBorderColor = Color(0xFF4CAF50),
                        unfocusedBorderColor = Color(0xFF2A3824)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (selectedPlotId.isNotBlank()) {
                            onSubmit(selectedPlotId, selectedStage, selectedCondition, pestNotes, generalNotes)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save Observation & Feed DSS", color = White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
