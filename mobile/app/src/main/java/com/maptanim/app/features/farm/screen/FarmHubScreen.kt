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
import com.maptanim.app.features.farm.dialogs.AddBedDialog
import com.maptanim.app.features.farm.dialogs.FarmSetupDialog
import com.maptanim.app.features.farm.tabs.CheckUpTab
import com.maptanim.app.features.farm.tabs.GuideTab
import com.maptanim.app.features.farm.tabs.HarvestTab
import com.maptanim.app.features.farm.tabs.PlanTab
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
 *    8-point handles, pan/zoom, Basketball Court Scale benchmark, toolbar actions).
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

    var sheetState by remember { mutableStateOf(SheetExpandState.HALF) }
    var showAddBedDialog by remember { mutableStateOf(false) }
    var showHarvestDialog by remember { mutableStateOf(false) }
    var harvestYieldInput by remember { mutableStateOf("") }
    var harvestNotesInput by remember { mutableStateOf("") }
    var isFinalHarvestChecked by remember { mutableStateOf(true) }

    val animatedWeight by animateFloatAsState(
        targetValue = when (sheetState) {
            SheetExpandState.PEEK -> 0.16f
            SheetExpandState.HALF -> 0.52f
            SheetExpandState.FULL -> 0.88f
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
                    showCourtScale = uiState.planState.showBasketballScale,
                    isExpanded = sheetState == SheetExpandState.PEEK,
                    onToggleExpand = {
                        sheetState = if (sheetState == SheetExpandState.PEEK) SheetExpandState.HALF else SheetExpandState.PEEK
                    },
                    onToggleCourtScale = { viewModel.toggleBasketballScale() },
                    onSelectLayer = { viewModel.setCanvasLayer(it) },
                    onRequestAddBed = { showAddBedDialog = true },
                    onDeletePlot = { viewModel.deletePlot(it) },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // ── 2. Drag Handle & Expand/Collapse Button Bar ──────────────────
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        sheetState = when (sheetState) {
                            SheetExpandState.PEEK -> SheetExpandState.HALF
                            SheetExpandState.HALF -> SheetExpandState.PEEK
                            SheetExpandState.FULL -> SheetExpandState.HALF
                        }
                    },
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .background(Color(0xFFBDBDBD), RoundedCornerShape(2.dp))
                    )
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (sheetState == SheetExpandState.PEEK) Icons.Default.VerticalAlignBottom else Icons.Default.VerticalAlignTop,
                            contentDescription = null,
                            tint = LushGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (sheetState == SheetExpandState.PEEK) "Show Form" else "Expand Canvas",
                            color = LushGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // ── 3. Bottom Zone: Expandable Workspace Tabs ────────────────────
            Box(
                modifier = Modifier
                    .weight(animatedWeight.coerceAtLeast(0.12f))
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    TabRow(
                        selectedTabIndex = uiState.selectedTopTab.ordinal,
                        containerColor = Color.White,
                        contentColor = LushGreen,
                        divider = { HorizontalDivider(color = CardBorderColor) }
                    ) {
                        Tab(
                            selected = uiState.selectedTopTab == TopTab.PLAN,
                            onClick = { viewModel.selectTopTab(TopTab.PLAN) },
                            icon = { Icon(Icons.Default.DashboardCustomize, contentDescription = null) },
                            text = {
                                Text(
                                    text = "Plan",
                                    fontWeight = if (uiState.selectedTopTab == TopTab.PLAN) FontWeight.Bold else FontWeight.Normal,
                                    color = if (uiState.selectedTopTab == TopTab.PLAN) LushGreen else DeepBlack
                                )
                            }
                        )
                        Tab(
                            selected = uiState.selectedTopTab == TopTab.GUIDE,
                            onClick = { viewModel.selectTopTab(TopTab.GUIDE) },
                            icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null) },
                            text = {
                                Text(
                                    text = "Guide",
                                    fontWeight = if (uiState.selectedTopTab == TopTab.GUIDE) FontWeight.Bold else FontWeight.Normal,
                                    color = if (uiState.selectedTopTab == TopTab.GUIDE) LushGreen else DeepBlack
                                )
                            }
                        )
                        Tab(
                            selected = uiState.selectedTopTab == TopTab.CHECKUP,
                            onClick = { viewModel.selectTopTab(TopTab.CHECKUP) },
                            icon = { Icon(Icons.Default.Healing, contentDescription = null) },
                            text = {
                                Text(
                                    text = "Check",
                                    fontWeight = if (uiState.selectedTopTab == TopTab.CHECKUP) FontWeight.Bold else FontWeight.Normal,
                                    color = if (uiState.selectedTopTab == TopTab.CHECKUP) LushGreen else DeepBlack
                                )
                            }
                        )
                        Tab(
                            selected = uiState.selectedTopTab == TopTab.HARVEST,
                            onClick = { viewModel.selectTopTab(TopTab.HARVEST) },
                            icon = { Icon(Icons.Default.Agriculture, contentDescription = null) },
                            text = {
                                Text(
                                    text = "Harvest",
                                    fontWeight = if (uiState.selectedTopTab == TopTab.HARVEST) FontWeight.Bold else FontWeight.Normal,
                                    color = if (uiState.selectedTopTab == TopTab.HARVEST) LushGreen else DeepBlack
                                )
                            }
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        when (uiState.selectedTopTab) {
                            TopTab.PLAN -> {
                                PlanTab(
                                    state = uiState.planState,
                                    onSelectPlot = {
                                        viewModel.selectPlot(it)
                                        editViewModel.selectPlot(it)
                                    },
                                    onAddNewBed = { showAddBedDialog = true },
                                    onDeleteBed = {
                                        viewModel.deletePlot(it)
                                        editViewModel.deletePlot(it)
                                    },
                                    onResizeBed = { id, w, h ->
                                        viewModel.resizePlot(id, w, h)
                                        editViewModel.resizePlot(id, w, h)
                                    },
                                    onAssignCrop = { id, cId, cName ->
                                        viewModel.assignCropToPlot(id, cId, cName)
                                    },
                                    onSetCanvasLayer = { viewModel.setCanvasLayer(it) },
                                    onSetTool = {
                                        viewModel.setEditTool(it)
                                        editViewModel.selectTool(it)
                                    },
                                    onUndo = {
                                        viewModel.undo()
                                        editViewModel.undo()
                                    },
                                    onRedo = {
                                        viewModel.redo()
                                    },
                                    onOpenSetupDialog = {
                                        viewModel.openFarmSetupDialog()
                                    },
                                    onNavigateToGuide = {
                                        viewModel.selectTopTab(TopTab.GUIDE)
                                    }
                                )
                            }
                            TopTab.GUIDE -> {
                                GuideTab(
                                    state = uiState.guideState,
                                    onSelectDssTab = { viewModel.selectDssTab(it) },
                                    onCompleteTask = { viewModel.completeTask(it) },
                                    onNavigateToCheckUp = { viewModel.selectTopTab(TopTab.CHECKUP) },
                                    onNavigateToHarvest = { viewModel.selectTopTab(TopTab.HARVEST) }
                                )
                            }
                            TopTab.CHECKUP -> {
                                CheckUpTab(
                                    state = uiState.checkUpState,
                                    onOpenAddLog = { viewModel.setAddLogOpen(true) },
                                    onNavigateToGuide = { viewModel.selectTopTab(TopTab.GUIDE) }
                                )
                            }
                            TopTab.HARVEST -> {
                                HarvestTab(
                                    state = uiState.harvestState,
                                    onOpenHarvestModal = { showHarvestDialog = true },
                                    onNavigateToPlan = { viewModel.selectTopTab(TopTab.PLAN) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Add Bed Preset Dialog ────────────────────────────────────────────────
    if (showAddBedDialog) {
        AddBedDialog(
            existingCount = uiState.planState.rawPlots.size,
            onDismiss = { showAddBedDialog = false },
            onConfirm = { label, widthM, heightM, soilType ->
                showAddBedDialog = false
                viewModel.addPlot(null, widthM, heightM, soilType)
                editViewModel.addDirectPlantingPlot(
                    atWorldX = 2f,
                    atWorldY = (uiState.planState.rawPlots.size * 4.5f) + 1f,
                    cropName = "Bed",
                    cropId = "bed",
                    initialW = widthM,
                    initialH = heightM
                )
            }
        )
    }

    // ── Harvest Yield Recording Modal ────────────────────────────────────────
    if (showHarvestDialog) {
        val selectedPlot = uiState.planState.rawPlots.firstOrNull { it.id == uiState.planState.selectedPlotId }
            ?: uiState.planState.rawPlots.firstOrNull()

        AlertDialog(
            onDismissRequest = { showHarvestDialog = false },
            title = {
                Text(
                    text = "Record Harvest Yield",
                    fontWeight = FontWeight.Bold,
                    color = DeepBlack
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Plot: ${selectedPlot?.plotLabel ?: "Bed #1"} • ${selectedPlot?.cropName ?: "Produce"}",
                        fontSize = 13.sp,
                        color = LushGreen,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = harvestYieldInput,
                        onValueChange = { harvestYieldInput = it },
                        label = { Text("Yield (kg)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LushGreen,
                            focusedLabelColor = LushGreen
                        )
                    )

                    OutlinedTextField(
                        value = harvestNotesInput,
                        onValueChange = { harvestNotesInput = it },
                        label = { Text("Notes (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LushGreen,
                            focusedLabelColor = LushGreen
                        )
                    )

                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isFinalHarvestChecked,
                            onCheckedChange = { isFinalHarvestChecked = it },
                            colors = CheckboxDefaults.colors(checkedColor = LushGreen)
                        )
                        Text(
                            text = "Reset bed for next crop rotation",
                            fontSize = 12.sp,
                            color = DeepBlack
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val yield = harvestYieldInput.toFloatOrNull() ?: 0f
                        selectedPlot?.let {
                            viewModel.recordHarvest(
                                plotId = it.id,
                                yieldKg = yield,
                                notes = harvestNotesInput,
                                isFinalHarvest = isFinalHarvestChecked
                            )
                        }
                        showHarvestDialog = false
                        harvestYieldInput = ""
                        harvestNotesInput = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LushGreen)
                ) {
                    Text("Save Record", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showHarvestDialog = false }) {
                    Text("Cancel", color = DeepBlack)
                }
            },
            containerColor = Color.White
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
}
