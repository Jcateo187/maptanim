package com.maptanim.app.features.farm.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maptanim.app.navigation.MainBottomNavBar
import com.maptanim.app.features.farm.canvas.FarmCanvasView
import com.maptanim.app.features.farm.components.FarmDrawerDragBar
import com.maptanim.app.features.farm.components.InterconnectedWorkflowHeader
import com.maptanim.app.features.farm.components.SheetExpandState
import com.maptanim.app.features.farm.components.WorkflowStep
import com.maptanim.app.domain.model.ManagementStage
import com.maptanim.app.features.farm.components.UnifiedBedSummaryDossier
import com.maptanim.app.features.farm.dialogs.AddBedDialog
import com.maptanim.app.features.farm.dialogs.AddLogDialog
import com.maptanim.app.features.farm.dialogs.BedTimelineDialog
import com.maptanim.app.features.farm.dialogs.FarmSetupDialog
import com.maptanim.app.features.farm.dialogs.HarvestRecordDialog
import com.maptanim.app.features.farm.dialogs.YardMeasurementGuideDialog
import com.maptanim.app.features.farm.viewmodel.EditViewModel
import com.maptanim.app.features.farm.viewmodel.FarmHubViewModel
import com.maptanim.app.features.farm.viewmodel.TopTab
import com.maptanim.app.navigation.Routes

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)

/**
 * FarmHubScreen — Consolidated Farm Management Hub.
 * Features:
 * 1. Expandable 2D Top-Down Canvas Viewport (TopDownFarmCanvas multi-touch gestures,
 *    8-point handles, pan/zoom, Yard Measurement Guide, toolbar actions).
 * 2. Expand/Minimize toggle button (minimizes the form below so canvas gets 84% height).
 * 3. 4 Modular Pillars: PlanTab, GuideTab, CheckUpTab, HarvestTab.
 *
 * Adheres strictly to the Daylight High-Contrast Theme (Pure White background, Lush Green buttons, Deep Black text).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmHubScreen(
    navController: NavController,
    viewModel: FarmHubViewModel = viewModel()
) {
    val editViewModel: EditViewModel = viewModel()
    val editUiState by editViewModel.uiState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var sheetState by remember { mutableStateOf(SheetExpandState.HIDDEN) }
    var showAddBedDialog by remember { mutableStateOf(false) }
    var showHarvestDialog by remember { mutableStateOf(false) }
    var showYardGuideDialog by remember { mutableStateOf(false) }
    var showTimelineDialog by remember { mutableStateOf(false) }
    var showAddLogDialog by remember { mutableStateOf(false) }
    var yardWidthM by remember { mutableFloatStateOf(15f) }
    var yardHeightM by remember { mutableFloatStateOf(10f) }
    var showYardRulers by remember { mutableStateOf(true) }

    val animatedWeight by animateFloatAsState(
        targetValue = when (sheetState) {
            SheetExpandState.HIDDEN -> 0.0f
            SheetExpandState.PEEK -> 0.14f
            SheetExpandState.HALF -> 0.60f
            SheetExpandState.FULL -> 0.94f
        },
        label = "sheetWeight"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "FARM HUB",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = LushGreen,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = uiState.farmName,
                            fontSize = 12.sp,
                            color = DeepBlack
                        )
                    }
                },
                actions = {
                    Surface(
                        onClick = { viewModel.openFarmSetupDialog() },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F8E9),
                        border = BorderStroke(1.dp, LushGreen.copy(alpha = 0.5f)),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Tune,
                                contentDescription = "Farm Setup",
                                tint = LushGreen,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "${uiState.farmEnvironment.zone.label} • ${uiState.farmEnvironment.defaultSoil.name}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = LushGreen
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = DeepBlack
                )
            )
        },
        bottomBar = {
            MainBottomNavBar(
                selectedRoute = Routes.FARM,
                onNavigate = { route ->
                    if (route != Routes.FARM) {
                        navController.navigate(route) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ── 1. Top Zone: Interactive 2D Canvas (Expandable) ──────────────
            Box(
                modifier = Modifier
                    .weight((1f - animatedWeight).coerceAtLeast(0.10f))
                    .fillMaxWidth()
            ) {
                FarmCanvasView(
                    editUiState = editUiState,
                    editViewModel = editViewModel,
                    canvasLayer = uiState.planState.canvasLayer,
                    showYardRulers = showYardRulers,
                    yardWidthM = yardWidthM,
                    yardHeightM = yardHeightM,
                    isExpanded = sheetState == SheetExpandState.HIDDEN,
                    onToggleExpand = {
                        sheetState = if (sheetState == SheetExpandState.HIDDEN) SheetExpandState.HALF else SheetExpandState.HIDDEN
                    },
                    onOpenYardGuide = { showYardGuideDialog = true },
                    onOpenInspect = { showAddLogDialog = true },
                    onOpenTimeline = { showTimelineDialog = true },
                    onSelectLayer = { viewModel.setCanvasLayer(it) },
                    onRequestAddBed = { showAddBedDialog = true },
                    onDeletePlot = { viewModel.deletePlot(it) },
                    onNavigateToGuide = {
                        sheetState = SheetExpandState.HALF
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // ── 2. Drag Handle & Expand/Collapse Control Bar ──────────────────
            FarmDrawerDragBar(
                sheetState = sheetState,
                onSetState = { sheetState = it }
            )

            // ── 3. Bottom Zone: Unified Case Dossier Summary Form ────────────
            if (sheetState != SheetExpandState.HIDDEN) {
                Box(
                    modifier = Modifier
                        .weight(animatedWeight.coerceAtLeast(0.12f))
                        .fillMaxWidth()
                ) {
                    val activeRenderPlot = editUiState.plots.firstOrNull { it.id == editUiState.selectedPlotId }
                        ?: editUiState.plots.firstOrNull()

                    UnifiedBedSummaryDossier(
                        selectedPlot = activeRenderPlot,
                        allPlots = editUiState.plots,
                        cropZones = editUiState.cropZones,
                        onOpenCropTray = {
                            sheetState = SheetExpandState.HIDDEN
                        },
                        onOpenInspect = { showAddLogDialog = true },
                        onOpenHarvestModal = { showHarvestDialog = true },
                        onHideDrawer = { sheetState = SheetExpandState.HIDDEN },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    // ── Real-World Yard Measurement Guide Dialog ─────────────────────────────
    if (showYardGuideDialog) {
        YardMeasurementGuideDialog(
            currentWidthM = yardWidthM,
            currentHeightM = yardHeightM,
            showRulers = showYardRulers,
            onApplyDimensions = { w, h, showR ->
                yardWidthM = w
                yardHeightM = h
                showYardRulers = showR
                showYardGuideDialog = false
            },
            onDismiss = { showYardGuideDialog = false }
        )
    }

    // ── Add Bed Preset Dialog ────────────────────────────────────────────────
    if (showAddBedDialog) {
        AddBedDialog(
            existingCount = uiState.planState.rawPlots.size,
            defaultSoil = uiState.farmEnvironment.defaultSoil,
            onDismiss = { showAddBedDialog = false },
            onConfirm = { label, widthM, heightM, soilType, cropName ->
                showAddBedDialog = false
                viewModel.addPlot(cropName, widthM, heightM, soilType)
                editViewModel.addDirectPlantingPlot(
                    atWorldX = 2f,
                    atWorldY = (uiState.planState.rawPlots.size * 4.5f) + 1f,
                    cropName = cropName ?: "Bed",
                    cropId = cropName?.lowercase() ?: "bed",
                    initialW = widthM,
                    initialH = heightM
                )
            }
        )
    }

    // ── Harvest Yield Recording Modal ────────────────────────────────────────
    if (showHarvestDialog) {
        val selectedPlot = uiState.activePlot
        HarvestRecordDialog(
            selectedPlot = selectedPlot,
            onDismiss = { showHarvestDialog = false },
            onConfirm = { yieldKg, notes, isFinalHarvest ->
                selectedPlot?.let {
                    viewModel.recordHarvest(
                        plotId = it.id,
                        yieldKg = yieldKg,
                        notes = notes,
                        isFinalHarvest = isFinalHarvest
                    )
                }
                showHarvestDialog = false
            }
        )
    }

    // ── Farm Setup & Agro-Zone Environment Calibration Dialog ────────────────
    if (uiState.showFarmSetupDialog) {
        FarmSetupDialog(
            initialName = uiState.farmName,
            initialEnvironment = uiState.farmEnvironment,
            onConfirm = { name, env ->
                viewModel.updateFarmSetup(name, env)
            },
            onDismiss = { viewModel.closeFarmSetupDialog() }
        )
    }

    // ── Direct Bed Harvest Timeline & Calendar Modal ─────────────────────────
    if (showTimelineDialog) {
        val selectedRenderPlot = editUiState.plots.firstOrNull { it.id == editUiState.selectedPlotId }
            ?: editUiState.plots.firstOrNull()
        if (selectedRenderPlot != null) {
            BedTimelineDialog(
                plot = selectedRenderPlot,
                cropZones = editUiState.cropZones,
                onDismiss = { showTimelineDialog = false }
            )
        }
    }

    // ── Direct Field Observation / Crop Inspection Dialog ────────────────────
    if (showAddLogDialog) {
        val selectedPlot = uiState.activePlot ?: uiState.planState.rawPlots.firstOrNull { it.id == editUiState.selectedPlotId }
        val targetCropName = selectedPlot?.cropName ?: "Vegetable"
        val targetCropVariety = selectedPlot?.cropVariety ?: "Standard Variety"
        AddLogDialog(
            cropPlantingId = selectedPlot?.id ?: "",
            bedId = selectedPlot?.plotLabel ?: "Bed",
            cropId = selectedPlot?.cropId,
            varietyId = selectedPlot?.cropVariety,
            cropName = targetCropName,
            varietyName = targetCropVariety,
            currentStage = ManagementStage.VEGETATIVE_GROWTH,
            plantingMethod = "Transplanting",
            onDismiss = { showAddLogDialog = false },
            onSubmitLog = { cropLog ->
                viewModel.submitCropLog(cropLog)
                showAddLogDialog = false
            }
        )
    }
}
