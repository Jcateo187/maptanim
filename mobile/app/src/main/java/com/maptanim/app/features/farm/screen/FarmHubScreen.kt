package com.maptanim.app.features.farm.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.maptanim.app.domain.model.SoilType
import com.maptanim.app.features.farm.canvas.FarmCanvasView
import com.maptanim.app.features.farm.components.FarmEditTopHeader
import com.maptanim.app.features.farm.dialogs.AdjustGardenSizeDialog
import com.maptanim.app.features.farm.dialogs.BedTimelineDialog
import com.maptanim.app.features.farm.dialogs.CropCareDssDialog
import com.maptanim.app.features.farm.dialogs.CropInformationDialog
import com.maptanim.app.features.farm.dialogs.GardenSummaryDialog
import com.maptanim.app.features.farm.dialogs.NewGardenDialog
import com.maptanim.app.features.farm.dialogs.RenameGardenDialog
import com.maptanim.app.features.farm.dialogs.SetPlantingDateDialog
import com.maptanim.app.features.farm.dialogs.CropScheduleCalendarScreen
import com.maptanim.app.features.farm.renderer.model.PlotRenderData
import com.maptanim.app.features.farm.viewmodel.EditViewModel
import com.maptanim.app.features.farm.viewmodel.FarmHubViewModel
import com.maptanim.app.navigation.MainBottomNavBar
import com.maptanim.app.navigation.Routes
import java.util.Locale

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val LightSurface = Color(0xFFF9FAF8)

data class CareDialogData(
    val cropName: String,
    val varietyName: String = "",
    val gardenLabel: String = "Garden",
    val plotId: String = ""
)

data class CropCalendarTarget(
    val cropName: String,
    val varietyName: String? = null,
    val gardenLabel: String = "Garden",
    val plotId: String = "",
    val zoneId: String? = null,
    val initialPlantedDate: String? = null
)

/**
 * FarmHubScreen — Garden Management Hub.
 * Features:
 * 1. Header: Left side displays "Garden", right side displays Archive icon.
 *    Clicking Archive icon opens "Show Archive" checkbox toggle.
 * 2. Main Area: List of gardens. Left side has Garden Name and (Width X Height ft) under name.
 *    Right side has More (⋮) icon with small overlay for Rename, Archive, and Delete.
 * 3. Bottom Right Floating Overlay: "+ NEW GARDEN" button. Clicking opens "Create new garden"
 *    form with Garden name input, Width and Height Card with scrollable size selection,
 *    and Cancel (left) / Continue (right) buttons.
 * 4. 2D Canvas Edit Screen with brown soil background, grid lines boxes, add vegetables tray,
 *    and 3 circular overlays for crops.
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

    // ── Navigation & View Modes ──────────────────────────────────────────────
    var isEditingMode by remember { mutableStateOf(false) }

    // ── Archive & Header State ───────────────────────────────────────────────
    var showArchived by remember { mutableStateOf(false) }
    var archiveMenuExpanded by remember { mutableStateOf(false) }

    // ── Header & Canvas Feature Toggles ──────────────────────────────────────
    var showCompanion by remember { mutableStateOf(false) }
    var showVariety by remember { mutableStateOf(false) }
    var showMeasurement by remember { mutableStateOf(true) }
    var showCropName by remember { mutableStateOf(true) }
    var isVegetableOverlayVisible by remember { mutableStateOf(false) }

    // ── Dialog Visibility States ─────────────────────────────────────────────
    var showNewGardenDialog by remember { mutableStateOf(false) }
    var showAdjustGardenDialog by remember { mutableStateOf(false) }
    var showGardenSummaryDialog by remember { mutableStateOf(false) }
    var showCropInfoDialog by remember { mutableStateOf<String?>(null) }
    var showCareDssDialog by remember { mutableStateOf<CareDialogData?>(null) }
    var showCropCalendarData by remember { mutableStateOf<Pair<PlotRenderData, String>?>(null) }
    var showSetPlantingDateForTarget by remember { mutableStateOf<CropCalendarTarget?>(null) }
    var activeCalendarTarget by remember { mutableStateOf<CropCalendarTarget?>(null) }
    var gardenToRename by remember { mutableStateOf<PlotRenderData?>(null) }

    // Active selected plot for sizing & rotation
    val selectedPlot = remember(editUiState.plots, editUiState.selectedPlotId) {
        editUiState.plots.firstOrNull { it.id == editUiState.selectedPlotId }
            ?: editUiState.plots.firstOrNull()
    }

    // Filter plots based on "Show Archive" checkbox toggle
    val displayedPlots = remember(editUiState.plots, showArchived) {
        if (showArchived) {
            editUiState.plots
        } else {
            editUiState.plots.filter { it.isActive }
        }
    }

    // Ensure selected plot ID is set whenever in editing mode
    LaunchedEffect(isEditingMode, editUiState.selectedPlotId, selectedPlot?.id) {
        if (isEditingMode && editUiState.selectedPlotId == null && selectedPlot != null) {
            editViewModel.selectPlot(selectedPlot.id)
        }
    }

    Scaffold(
        topBar = {
            if (isEditingMode) {
                // ── EDIT SCREEN HEADER ───────────────────────────────────────
                FarmEditTopHeader(
                    gardenName = selectedPlot?.plotLabel?.ifBlank { null } ?: "Garden",
                    canUndo = editUiState.canUndo,
                    canRedo = editUiState.canRedo,
                    showCompanion = showCompanion,
                    showVariety = showVariety,
                    showMeasurement = showMeasurement,
                    showCropName = showCropName,
                    onBack = {
                        isEditingMode = false
                        isVegetableOverlayVisible = false
                    },
                    onUndo = { editViewModel.undo() },
                    onRedo = { editViewModel.redo() },
                    onAdjustSize = { showAdjustGardenDialog = true },
                    onRotateGarden = {
                        selectedPlot?.let { editViewModel.rotatePlot(it.id) }
                    },
                    onGardenSummary = { showGardenSummaryDialog = true },
                    onToggleCompanion = { showCompanion = it },
                    onToggleVariety = { showVariety = it },
                    onToggleMeasurement = { showMeasurement = it },
                    onToggleCropName = { showCropName = it }
                )
            } else {
                // ── GARDEN TOP BAR ───────────────────────────────────────────
                // Left: "Garden", Right: Archive icon with "Show Archive" checkbox popover
                Column {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Garden",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = LushGreen
                            )
                        },
                        actions = {
                            // Archive Icon Button & "Show Archive" Dropdown Overlay
                            Box {
                                IconButton(onClick = { archiveMenuExpanded = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Archive,
                                        contentDescription = "Archive Options",
                                        tint = LushGreen
                                    )
                                }

                                DropdownMenu(
                                    expanded = archiveMenuExpanded,
                                    onDismissRequest = { archiveMenuExpanded = false },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = "Show Archive",
                                                    fontSize = 13.5.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = DeepBlack
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Checkbox(
                                                    checked = showArchived,
                                                    onCheckedChange = {
                                                        showArchived = it
                                                        archiveMenuExpanded = false
                                                    },
                                                    colors = CheckboxDefaults.colors(checkedColor = LushGreen)
                                                )
                                            }
                                        },
                                        onClick = {
                                            showArchived = !showArchived
                                            archiveMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.White,
                            titleContentColor = LushGreen
                        )
                    )
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Color.Black
                    )
                }
            }
        },
        bottomBar = {
            if (!isEditingMode) {
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
            }
        },
        contentWindowInsets = if (isEditingMode) WindowInsets(0, 0, 0, 0) else ScaffoldDefaults.contentWindowInsets,
        containerColor = if (isEditingMode) Color(0xFF261814) else Color(0xFFFBFBFA)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isEditingMode) {
                        Modifier.padding(top = innerPadding.calculateTopPadding())
                    } else {
                        Modifier.padding(innerPadding)
                    }
                )
        ) {
            if (!isEditingMode) {
                // ═════════════════════════════════════════════════════════════
                // 1. MAIN AREA: LIST OF GARDENS
                // ═════════════════════════════════════════════════════════════
                if (displayedPlots.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFE8F5E9),
                                modifier = Modifier.size(76.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Yard,
                                        contentDescription = null,
                                        tint = LushGreen,
                                        modifier = Modifier.size(38.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (showArchived) "No Archived Gardens" else "No Gardens Yet",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepBlack
                            )
                            Text(
                                text = if (showArchived)
                                    "There are no archived gardens. Uncheck 'Show Archive' to see active gardens."
                                else
                                    "Tap 'NEW GARDEN' in the bottom right to create your first garden.",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(displayedPlots, key = { it.id }) { plot ->
                            GardenListItemCard(
                                plot = plot,
                                onCardClick = {
                                    editViewModel.selectPlot(plot.id)
                                    isEditingMode = true
                                },
                                onRename = {
                                    gardenToRename = plot
                                },
                                onToggleArchive = {
                                    editViewModel.setPlotArchived(plot.id, isArchived = plot.isActive)
                                },
                                onDelete = {
                                    editViewModel.deletePlot(plot.id)
                                }
                            )
                        }
                    }
                }

                // ── Floating Overlay in Bottom Right: NEW GARDEN ─────────────
                ExtendedFloatingActionButton(
                    onClick = { showNewGardenDialog = true },
                    containerColor = LushGreen,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "NEW GARDEN",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    )
                }

            } else {
                // ═════════════════════════════════════════════════════════════
                // 2. 2D EDIT SCREEN (Brown Soil Canvas, Grid Boxes, Add Vegs, Overlay)
                // ═════════════════════════════════════════════════════════════
                FarmCanvasView(
                    editUiState = editUiState,
                    editViewModel = editViewModel,
                    yardWidthM = selectedPlot?.widthM ?: (10f * 0.3048f),
                    yardHeightM = selectedPlot?.heightM ?: (8f * 0.3048f),
                    showYardRulers = showMeasurement,
                    showMeasurement = showMeasurement,
                    showCompanion = showCompanion,
                    showVariety = showVariety,
                    showCropName = showCropName,
                    isVegetableOverlayVisible = isVegetableOverlayVisible,
                    onToggleVegetableOverlay = {
                        isVegetableOverlayVisible = !isVegetableOverlayVisible
                    },
                    onOpenCropInfo = { cropName ->
                        showCropInfoDialog = cropName
                    },
                    onOpenCareScreen = { cName, vName, gLabel, pId ->
                        showCareDssDialog = CareDialogData(
                            cropName = cName,
                            varietyName = vName,
                            gardenLabel = gLabel,
                            plotId = pId
                        )
                    },
                    onOpenCalendar = { cropName, variety, gardenLabel, plotId, zoneId ->
                        val cropKey = zoneId ?: "${plotId}_$cropName"
                        val existingDate = editViewModel.getCropPlantedDate(cropKey, zoneId, plotId)
                        val target = CropCalendarTarget(
                            cropName = cropName,
                            varietyName = variety,
                            gardenLabel = gardenLabel,
                            plotId = plotId,
                            zoneId = zoneId,
                            initialPlantedDate = existingDate
                        )
                        if (existingDate.isNullOrBlank()) {
                            // First time per crop: prompt overlay to set date when to plant
                            showSetPlantingDateForTarget = target
                        } else {
                            // If crop already done create schedule: redirect directly to Calendar screen
                            activeCalendarTarget = target
                        }
                    },
                    onDeletePlot = { plotId ->
                        editViewModel.deletePlot(plotId)
                    },
                    onNavigateToGuide = {
                        isEditingMode = false
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // ── 1. Create New Garden Dialog ──────────────────────────────────────────
    if (showNewGardenDialog) {
        NewGardenDialog(
            existingCount = editUiState.plots.size,
            onDismiss = { showNewGardenDialog = false },
            onSave = { label, widthFt, heightFt ->
                showNewGardenDialog = false
                val widthM = widthFt * 0.3048f
                val heightM = heightFt * 0.3048f

                editViewModel.addDirectPlantingPlot(
                    atWorldX = 0f,
                    atWorldY = 0f,
                    cropName = label,
                    cropId = label.lowercase(),
                    initialW = widthM,
                    initialH = heightM
                )
                // Transition directly to the 2D Edit Screen for THIS garden
                isEditingMode = true
            }
        )
    }

    // ── 2. Rename Garden Dialog ──────────────────────────────────────────────
    gardenToRename?.let { targetPlot ->
        RenameGardenDialog(
            initialName = targetPlot.plotLabel,
            onDismiss = { gardenToRename = null },
            onConfirm = { newName ->
                editViewModel.renamePlot(targetPlot.id, newName)
                gardenToRename = null
            }
        )
    }

    // ── 3. Adjust Garden Size Dialog (Max 40 ft) ─────────────────────────────
    if (showAdjustGardenDialog) {
        val targetPlot = selectedPlot ?: editUiState.plots.firstOrNull()
        val currentWidthFt = (targetPlot?.widthM ?: 3.0f) / 0.3048f
        val currentHeightFt = (targetPlot?.heightM ?: 1.5f) / 0.3048f

        AdjustGardenSizeDialog(
            initialWidthFt = currentWidthFt,
            initialHeightFt = currentHeightFt,
            gardenLabel = targetPlot?.plotLabel ?: "Garden",
            onDismiss = { showAdjustGardenDialog = false },
            onApply = { widthFt, heightFt ->
                targetPlot?.let {
                    val wM = widthFt * 0.3048f
                    val hM = heightFt * 0.3048f
                    editViewModel.setPlotDimensions(it.id, wM, hM)
                }
                showAdjustGardenDialog = false
            }
        )
    }

    // ── 4. Garden Summary Dialog ─────────────────────────────────────────────
    if (showGardenSummaryDialog) {
        GardenSummaryDialog(
            farmName = selectedPlot?.plotLabel?.ifBlank { null } ?: "Garden",
            plots = editUiState.plots,
            cropZones = editUiState.cropZones,
            onDismiss = { showGardenSummaryDialog = false }
        )
    }

    // ── 5. Crop Information Dialog ───────────────────────────────────────────
    showCropInfoDialog?.let { cropName ->
        CropInformationDialog(
            cropName = cropName,
            onDismiss = { showCropInfoDialog = null }
        )
    }

    // ── 6. Crop Care & DSS Evaluation Dialog ─────────────────────────────────
    showCareDssDialog?.let { careData ->
        CropCareDssDialog(
            cropName = careData.cropName,
            varietyName = careData.varietyName,
            gardenLabel = careData.gardenLabel,
            plotId = careData.plotId,
            onDismiss = { showCareDssDialog = null }
        )
    }

    // ── 7. Crop Set Planting Date Overlay (One-Time Prompt & Reschedule) ─────
    showSetPlantingDateForTarget?.let { target ->
        SetPlantingDateDialog(
            cropName = target.cropName,
            varietyName = target.varietyName,
            gardenLabel = target.gardenLabel,
            initialDate = target.initialPlantedDate,
            isReschedule = !target.initialPlantedDate.isNullOrBlank(),
            onDismiss = { showSetPlantingDateForTarget = null },
            onSaveDate = { chosenDate ->
                val cropKey = target.zoneId ?: "${target.plotId}_${target.cropName}"
                editViewModel.saveCropPlantedDate(cropKey, target.zoneId, target.plotId, chosenDate)
                showSetPlantingDateForTarget = null
                // Redirect directly to Calendar screen
                activeCalendarTarget = target.copy(initialPlantedDate = chosenDate)
            }
        )
    }

    // ── 8. Dedicated Full-Screen Crop Growing Calendar & Timeline ────────────
    activeCalendarTarget?.let { target ->
        CropScheduleCalendarScreen(
            cropName = target.cropName,
            varietyName = target.varietyName,
            gardenLabel = target.gardenLabel,
            plotId = target.plotId,
            zoneId = target.zoneId,
            plantedDateStr = target.initialPlantedDate ?: java.time.LocalDate.now().toString(),
            onDismiss = { activeCalendarTarget = null },
            onReschedule = {
                // Top header right icon clicked: re-open date overlay to reschedule
                showSetPlantingDateForTarget = target
            }
        )
    }

    // ── Legacy Bed Timeline Dialog Fallback ──────────────────────────────────
    showCropCalendarData?.let { (targetPlot, cropName) ->
        BedTimelineDialog(
            plot = targetPlot,
            cropZones = editUiState.cropZones,
            focusedCropName = cropName,
            onDismiss = { showCropCalendarData = null }
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Garden Card Item Component
// ═══════════════════════════════════════════════════════════════════════════════
/**
 * GardenListItemCard:
 * - Left side: Garden name, with (Width X Height ft) directly below name (e.g. Likod Balay (40.0 X 40.0ft))
 * - Right side: More (⋮) icon opening small overlay with: Rename, Archive, Delete
 */
@Composable
private fun GardenListItemCard(
    plot: PlotRenderData,
    onCardClick: () -> Unit,
    onRename: () -> Unit,
    onToggleArchive: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val widthFt = plot.widthM / 0.3048f
    val heightFt = plot.heightM / 0.3048f

    Surface(
        onClick = onCardClick,
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Left Side: Garden Name & (Width X Height ft) ─────────────
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = plot.plotLabel.ifBlank { "Garden" },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlack
                    )

                    if (!plot.isActive) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFEEEEEE)
                        ) {
                            Text(
                                text = "Archived",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Dimension in feet directly below the name: (40.0 X 40.0ft)
                Text(
                    text = "(${String.format(Locale.US, "%.1f", widthFt)} X ${String.format(Locale.US, "%.1f", heightFt)}ft)",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            // ── Right Side: More Icon & Overlay with Rename, Archive, Delete ─
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options",
                        tint = DeepBlack,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    // 1. Rename
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Rename",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = DeepBlack
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = LushGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onRename()
                        }
                    )

                    // 2. Archive / Unarchive
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (plot.isActive) "Archive" else "Unarchive",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = DeepBlack
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (plot.isActive) Icons.Default.Archive else Icons.Default.Unarchive,
                                contentDescription = null,
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onToggleArchive()
                        }
                    )

                    HorizontalDivider(color = CardBorderColor.copy(alpha = 0.5f))

                    // 3. Delete
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Delete",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFD32F2F)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = null,
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}
