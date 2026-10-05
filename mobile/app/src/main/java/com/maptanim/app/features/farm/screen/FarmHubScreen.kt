package com.maptanim.app.features.farm.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maptanim.app.navigation.MainBottomNavBar
import com.maptanim.app.features.farm.tabs.CheckUpTab
import com.maptanim.app.features.farm.tabs.GuideTab
import com.maptanim.app.features.farm.tabs.HarvestTab
import com.maptanim.app.features.farm.tabs.PlanTab
import com.maptanim.app.features.farm.viewmodel.FarmHubViewModel
import com.maptanim.app.features.farm.viewmodel.TopTab
import com.maptanim.app.navigation.Routes

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)

/**
 * FarmHubScreen — Consolidated Farm Management Hub.
 * Houses the 4 modular pillars:
 * 1. PlanTab (spatial layout, bed sizing, basketball court benchmark)
 * 2. GuideTab (agronomic care, dynamic DA-BPI tasks, stage progression)
 * 3. CheckUpTab (field observations, pest & disease logging)
 * 4. HarvestTab (readiness tracking, yield records)
 *
 * Adheres strictly to the Daylight High-Contrast Theme (Pure White background, Lush Green buttons, Deep Black text).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmHubScreen(
    navController: NavController,
    viewModel: FarmHubViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showHarvestDialog by remember { mutableStateOf(false) }
    var harvestYieldInput by remember { mutableStateOf("") }
    var harvestNotesInput by remember { mutableStateOf("") }
    var isFinalHarvestChecked by remember { mutableStateOf(true) }

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
            // ── 4 Modular Hub Tabs ───────────────────────────────────────────
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

            // ── Active Tab Display ───────────────────────────────────────────
            Box(modifier = Modifier.weight(1f)) {
                when (uiState.selectedTopTab) {
                    TopTab.PLAN -> {
                        PlanTab(
                            state = uiState.planState,
                            onSelectPlot = { viewModel.selectPlot(it) },
                            onAddNewBed = { viewModel.addPlot() },
                            onDeleteBed = { viewModel.deletePlot(it) },
                            onSetCanvasLayer = { viewModel.setCanvasLayer(it) },
                            onSetTool = { viewModel.setEditTool(it) },
                            onUndo = { viewModel.undo() },
                            onRedo = { viewModel.redo() }
                        )
                    }
                    TopTab.GUIDE -> {
                        GuideTab(
                            state = uiState.guideState,
                            onSelectDssTab = { viewModel.selectDssTab(it) },
                            onCompleteTask = { viewModel.completeTask(it) }
                        )
                    }
                    TopTab.CHECKUP -> {
                        CheckUpTab(
                            state = uiState.checkUpState,
                            onOpenAddLog = { viewModel.setAddLogOpen(true) }
                        )
                    }
                    TopTab.HARVEST -> {
                        HarvestTab(
                            state = uiState.harvestState,
                            onOpenHarvestModal = { showHarvestDialog = true }
                        )
                    }
                }
            }
        }
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
}
