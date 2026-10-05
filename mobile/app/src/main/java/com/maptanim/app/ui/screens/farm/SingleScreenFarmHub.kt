package com.maptanim.app.ui.screens.farm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maptanim.app.domain.model.*
import com.maptanim.app.renderer.canvas.TopDownCamera
import com.maptanim.app.renderer.canvas.TopDownFarmCanvas
import com.maptanim.app.renderer.canvas.TopDownProjection
import com.maptanim.app.ui.dialogs.DssTab
import com.maptanim.app.ui.dialogs.TopTab
import com.maptanim.app.ui.dialogs.components.BasketballCourtScaleCard
import com.maptanim.app.ui.screens.edit.EditUiState
import com.maptanim.app.ui.screens.edit.EditViewModel
import com.maptanim.app.ui.theme.White
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale

enum class SheetExpandState {
    PEEK,
    HALF,
    FULL
}

enum class HubLayoutMode(val label: String) {
    SPLIT("⬌ Split"),
    MAP("🗺 Map"),
    PANEL("📋 Panel")
}

data class SymptomOption(val id: String, val label: String)

val SoilType.label: String
    get() = when (this) {
        SoilType.LOAM -> "Loam"
        SoilType.CLAY -> "Clay"
        SoilType.SANDY -> "Sandy"
        SoilType.SILTY -> "Silty"
        SoilType.PEATY -> "Peaty"
        SoilType.CHALKY -> "Chalky"
    }

/**
 * SingleScreenFarmHub
 *
 * Implements the Unified 1-Screen Farm Workspace inspired by media_1791094749670.html.
 * Integrates interactive Canvas Map on top and 4-Tab Workspace Panel on bottom
 * with zero screen-hopping between edit, summary, farm, and management dialogs.
 */
@Composable
fun SingleScreenFarmHub(
    uiState: FarmUiState,
    editViewModel: EditViewModel = viewModel(),
    onAddNewBed: () -> Unit,
    onAddNewBedWithDetails: ((String?, Float, Float, SoilType) -> Unit)? = null,
    onDeleteBed: (String) -> Unit,
    onAssignCrop: (plotId: String, cropName: String, variety: String?, method: String, isPlanted: Boolean) -> Unit,
    onCompleteTask: (String) -> Unit,
    onQuickMaintain: (plotId: String, actionType: String) -> Unit,
    onRecordHarvest: (plotId: String, yieldKg: Float, notes: String?, isFinalHarvest: Boolean) -> Unit,
    onAdvanceStage: (plotId: String, stage: ManagementStage) -> Unit,
    onLogObservationTask: (plotId: String, taskTitle: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val editUiState by editViewModel.uiState.collectAsState()

    var selectedPlotId by remember(uiState.plots, editUiState.selectedPlotId) {
        mutableStateOf(editUiState.selectedPlotId ?: uiState.plots.firstOrNull()?.id)
    }
    var selectedTopTab by remember { mutableStateOf(TopTab.PLAN) }
    var canvasLayer by remember { mutableStateOf(CanvasLayer.CROPS) }
    var sheetState by remember { mutableStateOf(SheetExpandState.PEEK) }
    var showAddBedDialog by remember { mutableStateOf(false) }
    var plotPendingDelete by remember { mutableStateOf<CropPlot?>(null) }
    var previousPlotIds by remember { mutableStateOf(uiState.plots.map { it.id }.toSet()) }

    LaunchedEffect(Unit) {
        editViewModel.refresh()
    }

    LaunchedEffect(editUiState.selectedPlotId) {
        if (editUiState.selectedPlotId != null && editUiState.selectedPlotId != selectedPlotId) {
            selectedPlotId = editUiState.selectedPlotId
        }
    }

    // Auto-select and display newly added bed immediately in Plan tab
    LaunchedEffect(uiState.plots) {
        val currentIds = uiState.plots.map { it.id }.toSet()
        val newPlotId = currentIds.subtract(previousPlotIds).firstOrNull()
        if (newPlotId != null) {
            selectedPlotId = newPlotId
            editViewModel.selectPlot(newPlotId)
            selectedTopTab = TopTab.PLAN
            sheetState = SheetExpandState.HALF
        } else if (selectedPlotId == null || !currentIds.contains(selectedPlotId)) {
            val fallbackId = uiState.plots.firstOrNull()?.id
            selectedPlotId = fallbackId
            if (fallbackId != null) {
                editViewModel.selectPlot(fallbackId)
            }
        }
        previousPlotIds = currentIds
    }

    val activePlot = uiState.plots.firstOrNull { it.id == selectedPlotId } ?: uiState.plots.firstOrNull()

    val currentMonth = LocalDate.now().monthValue
    val isWetSeason = currentMonth in 5..10
    val seasonLabel = if (isWetSeason) "🌧 Wet Season" else "☀️ Dry Season"

    val highRiskPlot = uiState.plots.firstOrNull { it.cropName in listOf("Tomato", "Eggplant", "Pepper") && isWetSeason }
    val readyHarvestPlot = uiState.plots.firstOrNull { it.currentStage == ManagementStage.HARVEST }

    val animatedWeight by androidx.compose.animation.core.animateFloatAsState(
        targetValue = when (sheetState) {
            SheetExpandState.PEEK -> 0.14f
            SheetExpandState.HALF -> 0.52f
            SheetExpandState.FULL -> 0.90f
        },
        label = "sheetWeight"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF10160F))
    ) {

        // ── SMART ALERT BANNER ────────────────────────────────────────────────
        if (highRiskPlot != null) {
            Surface(
                color = Color(0xFF2D1B1B),
                border = BorderStroke(1.dp, Color(0xFF5C2D2D)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "⚠️", fontSize = 12.sp)
                        Text(
                            text = "${highRiskPlot.cropName ?: "Solanaceae"} (${highRiskPlot.plotLabel}): Bacterial wilt risk in wet season.",
                            color = Color(0xFFFF8A80),
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                    Surface(
                        onClick = {
                            selectedPlotId = highRiskPlot.id
                            selectedTopTab = TopTab.PLAN
                            sheetState = SheetExpandState.HALF
                        },
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFD32F2F)
                    ) {
                        Text(
                            text = "Fix",
                            color = White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        } else if (readyHarvestPlot != null) {
            Surface(
                color = Color(0xFF1B2E1D),
                border = BorderStroke(1.dp, Color(0xFF385E3B)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "🧺", fontSize = 12.sp)
                        Text(
                            text = "${readyHarvestPlot.cropName ?: "Crop"} (${readyHarvestPlot.plotLabel}) is ready for harvest!",
                            color = Color(0xFFA5D6A7),
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                    Surface(
                        onClick = {
                            selectedPlotId = readyHarvestPlot.id
                            selectedTopTab = TopTab.HARVEST
                            sheetState = SheetExpandState.HALF
                        },
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF2E7D32)
                    ) {
                        Text(
                            text = "Pick",
                            color = White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // ── DUAL REGION WORKSPACE (SPACIOUS CANVAS + EXPANDABLE SHEET) ────────
        Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
            // TOP ZONE: Spacious Canvas (Maximized Placement Area)
            Box(
                modifier = Modifier
                    .weight((1f - animatedWeight).coerceAtLeast(0.08f))
                    .fillMaxWidth()
            ) {
                CanvasSection(
                    plots = uiState.plots,
                    zones = uiState.cropZones,
                    selectedPlotId = selectedPlotId,
                    canvasLayer = canvasLayer,
                    seasonLabel = seasonLabel,
                    isWetSeason = isWetSeason,
                    plantedCount = uiState.plantedPlants.size,
                    editUiState = editUiState,
                    editViewModel = editViewModel,
                    onSelectLayer = { canvasLayer = it },
                    onSelectPlot = { plotId ->
                        selectedPlotId = plotId
                        editViewModel.selectPlot(plotId)
                    },
                    onRequestAddBed = { showAddBedDialog = true },
                    onRequestDeleteBed = { plot -> plotPendingDelete = plot },
                    onOpenPlanTab = { plot ->
                        selectedPlotId = plot.id
                        editViewModel.selectPlot(plot.id)
                        selectedTopTab = TopTab.PLAN
                        sheetState = SheetExpandState.HALF
                    },
                    onQuickMaintain = onQuickMaintain,
                    modifier = Modifier.fillMaxSize()
                )
            }

            HorizontalDivider(color = Color(0xFF2B3825), thickness = 1.dp)

            // BOTTOM ZONE: Expandable Workspace Sheet with Drag Handle Button "────────"
            Box(
                modifier = Modifier
                    .weight(animatedWeight.coerceAtLeast(0.12f))
                    .fillMaxWidth()
            ) {
                WorkspacePanel(
                    activePlot = activePlot,
                    allPlots = uiState.plots,
                    crops = uiState.crops,
                    todayTasks = uiState.todayTasks,
                    selectedTopTab = selectedTopTab,
                    sheetState = sheetState,
                    onToggleExpand = {
                        sheetState = when (sheetState) {
                            SheetExpandState.PEEK -> SheetExpandState.HALF
                            SheetExpandState.HALF -> SheetExpandState.FULL
                            SheetExpandState.FULL -> SheetExpandState.PEEK
                        }
                    },
                    onExpandHalf = { sheetState = SheetExpandState.HALF },
                    onCollapse = { sheetState = SheetExpandState.PEEK },
                    onSelectTopTab = { selectedTopTab = it },
                    onSelectPlot = { plotId ->
                        selectedPlotId = plotId
                        editViewModel.selectPlot(plotId)
                    },
                    onAddNewBed = { showAddBedDialog = true },
                    onRequestDeleteBed = { plot -> plotPendingDelete = plot },
                    onAssignCrop = onAssignCrop,
                    onCompleteTask = onCompleteTask,
                    onRecordHarvest = onRecordHarvest,
                    onAdvanceStage = onAdvanceStage,
                    onLogObservationTask = onLogObservationTask,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Add Bed Modal Dialog
        if (showAddBedDialog) {
            AddBedDialog(
                existingCount = uiState.plots.size,
                onDismiss = { showAddBedDialog = false },
                onConfirm = { label, widthM, heightM, soilType ->
                    showAddBedDialog = false
                    if (onAddNewBedWithDetails != null) {
                        onAddNewBedWithDetails(label, widthM, heightM, soilType)
                    } else {
                        onAddNewBed()
                    }
                }
            )
        }

        // Delete Bed Confirmation Dialog
        if (plotPendingDelete != null) {
            DeleteBedConfirmDialog(
                plot = plotPendingDelete!!,
                onDismiss = { plotPendingDelete = null },
                onConfirm = {
                    val plot = plotPendingDelete!!
                    val plotId = plot.id
                    onDeleteBed(plotId)
                    editViewModel.deletePlot(plotId)
                    val remaining = uiState.plots.filter { it.id != plotId }
                    val nextId = remaining.firstOrNull()?.id
                    selectedPlotId = nextId
                    if (nextId != null) {
                        editViewModel.selectPlot(nextId)
                    }
                    plotPendingDelete = null
                }
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// REGION 1: CANVASS SECTION
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun CanvasSection(
    plots: List<CropPlot>,
    zones: List<CropZone>,
    selectedPlotId: String?,
    canvasLayer: CanvasLayer,
    seasonLabel: String,
    isWetSeason: Boolean,
    plantedCount: Int,
    editUiState: EditUiState,
    editViewModel: EditViewModel,
    onSelectLayer: (CanvasLayer) -> Unit,
    onSelectPlot: (String) -> Unit,
    onRequestAddBed: () -> Unit,
    onRequestDeleteBed: (CropPlot) -> Unit,
    onOpenPlanTab: (CropPlot) -> Unit,
    onQuickMaintain: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val activePlot = plots.firstOrNull { it.id == selectedPlotId }
    var liveCamera by remember { mutableStateOf(TopDownCamera(zoom = 0.5f)) }

    Column(modifier = modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 4.dp)) {
        // Toolbar: Add Bed + Season Badge + Layer Selector
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    onClick = onRequestAddBed,
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF2E7D32),
                    border = BorderStroke(1.dp, Color(0xFF4CAF50))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "＋", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Add Bed", color = White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (isWetSeason) Color(0xFF1A382A) else Color(0xFF382E1A),
                    border = BorderStroke(1.dp, if (isWetSeason) Color(0xFF2A5941) else Color(0xFF59482A))
                ) {
                    Text(
                        text = seasonLabel,
                        color = if (isWetSeason) Color(0xFF81C784) else Color(0xFFFFD54F),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            // Layer Selector: Crops / Risk / Harvest
            Row(
                modifier = Modifier
                    .background(Color(0xFF192318), RoundedCornerShape(8.dp))
                    .padding(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                CanvasLayer.values().forEach { layer ->
                    val isSel = layer == canvasLayer
                    Surface(
                        onClick = { onSelectLayer(layer) },
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSel) Color(0xFF385532) else Color.Transparent
                    ) {
                        Text(
                            text = layer.name.lowercase(Locale.ROOT).replaceFirstChar { it.uppercase() },
                            color = if (isSel) Color(0xFF81C784) else Color(0xFFA0B09A),
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // ── TOP-DOWN FARM CANVAS RENDERER (NO EDGES, ZOOM 5 = 0.5f) ──────────
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            TopDownFarmCanvas(
                modifier = Modifier.fillMaxSize(),
                uiState = editUiState,
                editViewModel = editViewModel,
                showBoundary = false,
                initialZoom = 0.5f,
                onCameraChanged = { liveCamera = it }
            )

            // Floating Zoom & Reset Controls in Top-Right
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    onClick = {
                        val newZoom = (liveCamera.zoom * 1.3f).coerceIn(0.2f, 3.5f)
                        liveCamera = liveCamera.copy(zoom = newZoom)
                    },
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xD91E281C),
                    border = BorderStroke(1.dp, Color(0xFF385532))
                ) {
                    Text(
                        text = "＋",
                        color = White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                    )
                }
                Surface(
                    onClick = {
                        val newZoom = (liveCamera.zoom * 0.75f).coerceIn(0.2f, 3.5f)
                        liveCamera = liveCamera.copy(zoom = newZoom)
                    },
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xD91E281C),
                    border = BorderStroke(1.dp, Color(0xFF385532))
                ) {
                    Text(
                        text = "−",
                        color = White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Surface(
                    onClick = {
                        // Reset to Zoom 0.5 (Zoom 5) centered on beds
                        val targetZoom = 0.5f
                        val centerX = if (plots.isNotEmpty()) {
                            (plots.minOf { it.posX } + plots.maxOf { it.posX + it.widthM }) / 2f
                        } else 22.5f
                        val centerY = if (plots.isNotEmpty()) {
                            (plots.minOf { it.posY } + plots.maxOf { it.posY + it.heightM }) / 2f
                        } else 22.5f
                        val panX = 400f - centerX * TopDownProjection.PPM * targetZoom
                        val panY = 300f - centerY * TopDownProjection.PPM * targetZoom
                        liveCamera = TopDownCamera(panX = panX, panY = panY, zoom = targetZoom)
                    },
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xD91E281C),
                    border = BorderStroke(1.dp, Color(0xFF385532))
                ) {
                    Text(
                        text = "⌂ 5x",
                        color = Color(0xFF81C784),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp)
                    )
                }
            }

            // Canvas Quick-Action Bar for Selected Bed
            if (activePlot != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xF2162215),
                    border = BorderStroke(1.dp, Color(0xFF385532)),
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp)
                        .fillMaxWidth(0.96f)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
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
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF283B25),
                                    border = BorderStroke(1.dp, Color(0xFF4CAF50))
                                ) {
                                    Text(
                                        text = activePlot.plotLabel,
                                        color = Color(0xFF81C784),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = if (!activePlot.cropName.isNullOrBlank()) {
                                        "${activePlot.cropName} (${activePlot.currentStage.label})"
                                    } else {
                                        "Unplanted Bed"
                                    },
                                    color = White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            val areaSqM = activePlot.widthM * activePlot.heightM
                            val keyFrac = (28.4f / areaSqM.coerceAtLeast(0.1f)).toInt().coerceAtLeast(1)
                            Text(
                                text = "🏀 %.1fm×%.1fm (%.1fm² • 1/%d key)".format(activePlot.widthM, activePlot.heightM, areaSqM, keyFrac),
                                color = Color(0xFFFFD54F),
                                fontSize = 10.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF1B281B)
                            ) {
                                Text(
                                    text = "🌾 ${activePlot.soilType.label}",
                                    color = Color(0xFFA5D6A7),
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Surface(
                                    onClick = { onOpenPlanTab(activePlot) },
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF223820),
                                    border = BorderStroke(1.dp, Color(0xFF388E3C))
                                ) {
                                    Text(
                                        text = "🌱 Plan",
                                        color = Color(0xFF81C784),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                    )
                                }
                                Surface(
                                    onClick = { onQuickMaintain(activePlot.id, "Water") },
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF132A38),
                                    border = BorderStroke(1.dp, Color(0xFF265D7D))
                                ) {
                                    Text(
                                        text = "💧 Water",
                                        color = Color(0xFF80D8FF),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                    )
                                }
                                Surface(
                                    onClick = { onQuickMaintain(activePlot.id, "Weed") },
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF23331C),
                                    border = BorderStroke(1.dp, Color(0xFF486E38))
                                ) {
                                    Text(
                                        text = "🌿 Weed",
                                        color = Color(0xFFA5D6A7),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                    )
                                }
                                Surface(
                                    onClick = { onRequestDeleteBed(activePlot) },
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF381A1A),
                                    border = BorderStroke(1.dp, Color(0xFF6B2828))
                                ) {
                                    Text(
                                        text = "🗑 Delete",
                                        color = Color(0xFFFF8A80),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
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

// ═══════════════════════════════════════════════════════════════════════════════
// REGION 2: WORKSPACE PANEL (THE 4 TABS)
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun WorkspacePanel(
    activePlot: CropPlot?,
    allPlots: List<CropPlot>,
    crops: List<Crop>,
    todayTasks: List<FarmTask>,
    selectedTopTab: TopTab,
    sheetState: SheetExpandState,
    onToggleExpand: () -> Unit,
    onExpandHalf: () -> Unit,
    onCollapse: () -> Unit,
    onSelectTopTab: (TopTab) -> Unit,
    onSelectPlot: (String) -> Unit,
    onAddNewBed: () -> Unit,
    onRequestDeleteBed: (CropPlot) -> Unit,
    onAssignCrop: (plotId: String, cropName: String, variety: String?, method: String, isPlanted: Boolean) -> Unit,
    onCompleteTask: (String) -> Unit,
    onRecordHarvest: (plotId: String, yieldKg: Float, notes: String?, isFinalHarvest: Boolean) -> Unit,
    onAdvanceStage: (plotId: String, stage: ManagementStage) -> Unit,
    onLogObservationTask: (plotId: String, taskTitle: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(Color(0xFF10160F))) {
        // ── DRAGGABLE EXPAND / COLLAPSE HANDLE BUTTON "────────" ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleExpand() }
                .padding(vertical = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(5.dp)
                    .background(Color(0xFF6B8065), RoundedCornerShape(2.5.dp))
            )
        }

        if (sheetState == SheetExpandState.PEEK) {
            // PEEK MODE: Compact 1-row summary leaving ~86% canvas for spacious bed placement
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandHalf() }
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (activePlot != null) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF1B2E1C),
                            border = BorderStroke(1.dp, Color(0xFF388E3C))
                        ) {
                            Text(
                                text = activePlot.plotLabel,
                                color = Color(0xFF81C784),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                        Column {
                            Text(
                                text = activePlot.cropName ?: "Unplanted Bed",
                                color = White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            val areaSqM = activePlot.widthM * activePlot.heightM
                            Text(
                                text = "%.1fm × %.1fm (%.1f m²) • %s".format(
                                    activePlot.widthM,
                                    activePlot.heightM,
                                    areaSqM,
                                    activePlot.currentStage.label
                                ),
                                color = Color(0xFFA0B09A),
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    } else {
                        Text(
                            text = "No Bed Selected • Tap bed on canvas",
                            color = Color(0xFFA0B09A),
                            fontSize = 12.sp
                        )
                    }
                }

                Surface(
                    onClick = onExpandHalf,
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF2E7D32)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "▲", color = White, fontSize = 10.sp)
                        Text(
                            text = "Expand Workspace",
                            color = White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            // EXPANDED MODE (HALF or FULL): Show controls row + 4 Sticky Tabs + Content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (activePlot != null) {
                        Text(
                            text = "${activePlot.plotLabel}: ${activePlot.cropName ?: "Unplanted"}",
                            color = White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF1E281C)
                        ) {
                            Text(
                                text = activePlot.currentStage.label,
                                color = Color(0xFF81C784),
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Text(
                            text = "All Beds Workspace",
                            color = Color(0xFFA0B09A),
                            fontSize = 12.sp
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (sheetState == SheetExpandState.HALF) {
                        Surface(
                            onClick = onToggleExpand,
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF1E2B1A)
                        ) {
                            Text(
                                text = "▲ Full",
                                color = Color(0xFFA5D6A7),
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Surface(
                        onClick = onCollapse,
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF2B2020)
                    ) {
                        Text(
                            text = "▼ Canvas View",
                            color = Color(0xFFFFAB91),
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Bed Quick-Switcher Chips Row
            if (allPlots.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    allPlots.forEach { plot ->
                        val isSel = plot.id == activePlot?.id
                        val cropEmoji = when (plot.cropName?.lowercase()) {
                            "tomato" -> "🍅"
                            "eggplant" -> "🍆"
                            "pepper", "chili" -> "🌶"
                            "carrot" -> "🥕"
                            "lettuce", "pechay", "mustard", "cabbage" -> "🥬"
                            "cucumber" -> "🥒"
                            "squash" -> "🎃"
                            "corn" -> "🌽"
                            "bean", "string beans" -> "🫘"
                            "okra" -> "🌱"
                            null -> "🟫"
                            else -> "🌱"
                        }
                        Surface(
                            onClick = { onSelectPlot(plot.id) },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSel) Color(0xFF283B25) else Color(0xFF162215),
                            border = BorderStroke(1.dp, if (isSel) Color(0xFF4CAF50) else Color(0xFF2A3A28))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = cropEmoji, fontSize = 11.sp)
                                Text(
                                    text = plot.plotLabel,
                                    color = if (isSel) Color(0xFF81C784) else Color(0xFFA0B09A),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                )
                                if (!plot.cropName.isNullOrBlank()) {
                                    Text(
                                        text = "• ${plot.cropName}",
                                        color = if (isSel) White else Color(0xFF7E8F7A),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    // Add Bed Quick Chip
                    Surface(
                        onClick = onAddNewBed,
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF1E351E),
                        border = BorderStroke(1.dp, Color(0xFF388E3C))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(text = "＋", color = Color(0xFF81C784), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = "New Bed", color = Color(0xFF81C784), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Sticky 4-Tab Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF141C12))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TopTab.values().forEach { tab ->
                    val isSel = tab == selectedTopTab
                    Surface(
                        onClick = { onSelectTopTab(tab) },
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSel) Color(0xFF2E7D32) else Color.Transparent,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab.label,
                                color = if (isSel) White else Color(0xFFA0B09A),
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFF222C1F), thickness = 1.dp)

            // Tab Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp)
            ) {
            if (activePlot == null) {
                // Empty state if no plot exists
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162014)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "🌱", fontSize = 32.sp)
                        Text(
                            text = "No Bed Selected",
                            color = White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tap any bed on the farm layout above, or add a new bed to start planning.",
                            color = Color(0xFFA0B09A),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = onAddNewBed,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            Text(text = "＋ Add First Bed")
                        }
                    }
                }
            } else {
                when (selectedTopTab) {
                    TopTab.PLAN -> {
                        HubPlanTab(
                            plot = activePlot,
                            crops = crops,
                            onAssignCrop = { cropName, variety, method ->
                                onAssignCrop(activePlot.id, cropName, variety, method, true)
                            },
                            onRequestDeleteBed = { onRequestDeleteBed(activePlot) }
                        )
                    }
                    TopTab.GUIDE -> {
                        HubGuideTab(
                            plot = activePlot,
                            todayTasks = todayTasks.filter { it.plotId == activePlot.id },
                            onCompleteTask = onCompleteTask,
                            onAdvanceStage = { onAdvanceStage(activePlot.id, it) }
                        )
                    }
                    TopTab.CHECKUP -> {
                        HubCheckupTab(
                            plot = activePlot,
                            onGenerateTask = { taskTitle -> onLogObservationTask(activePlot.id, taskTitle) }
                        )
                    }
                    TopTab.HARVEST -> {
                        HubHarvestTab(
                            plot = activePlot,
                            onRecordHarvest = { yieldKg, isFinal ->
                                onRecordHarvest(activePlot.id, yieldKg, "Harvested from Hub", isFinal)
                            },
                            onStartRotation = { nextCropName ->
                                onAssignCrop(activePlot.id, nextCropName, "Standard", "Direct Seed", true)
                            }
                        )
                    }
                }
            }
        }
    }
}
}

// ─────────────────────────────────────────────────────────────────────────────
// TAB 1: PLAN (Bed Setup + Basketball Scale + Crop Selection)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HubPlanTab(
    plot: CropPlot,
    crops: List<Crop>,
    onAssignCrop: (cropName: String, variety: String?, method: String) -> Unit,
    onRequestDeleteBed: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf("Direct Seed") }
    var selectedStyle by remember { mutableStateOf("Organic") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Bed Specs Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141C12)),
            border = BorderStroke(1.dp, Color(0xFF2B3825)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${plot.plotLabel.uppercase()} SETUP",
                        color = Color(0xFFA5D6A7),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF22331E),
                        border = BorderStroke(1.dp, Color(0xFF385532))
                    ) {
                        Text(
                            text = "${plot.soilType.name.lowercase(Locale.ROOT).replaceFirstChar { it.uppercase() }} Soil",
                            color = Color(0xFF81C784),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                // Basketball Court Real-World Scale Benchmark
                BasketballCourtScaleCard(
                    widthM = plot.widthM,
                    heightM = plot.heightM,
                    cropName = plot.cropName ?: "Vegetables",
                    plotLabel = plot.plotLabel
                )

                // Method and Style Selectors
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Method:", color = Color(0xFFA0B09A), fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("Direct Seed", "Transplant").forEach { method ->
                            val isSel = method == selectedMethod
                            Surface(
                                onClick = { selectedMethod = method },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) Color(0xFF2E7D32) else Color(0xFF1A2418),
                                border = BorderStroke(1.dp, if (isSel) Color(0xFF4CAF50) else Color(0xFF2D3C2A))
                            ) {
                                Text(
                                    text = method,
                                    color = if (isSel) White else Color(0xFFA0B09A),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Approach:", color = Color(0xFFA0B09A), fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("🌿 Organic" to "Organic", "🧪 Conventional" to "Conventional").forEach { (label, style) ->
                            val isSel = style == selectedStyle
                            Surface(
                                onClick = { selectedStyle = style },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) Color(0xFF2E7D32) else Color(0xFF1A2418),
                                border = BorderStroke(1.dp, if (isSel) Color(0xFF4CAF50) else Color(0xFF2D3C2A))
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSel) White else Color(0xFFA0B09A),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Crop Selection Catalog
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141C12)),
            border = BorderStroke(1.dp, Color(0xFF2B3825)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "🌱 CHOOSE / REPLANT CROP FOR ${plot.plotLabel.uppercase()}",
                    color = Color(0xFFA5D6A7),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Tap a crop to assign it to this bed with automatic season & companion checks.",
                    color = Color(0xFFA0B09A),
                    fontSize = 11.sp
                )

                val catalogCrops = listOf(
                    Triple("Tomato", "🍅", 75),
                    Triple("Eggplant", "🍆", 85),
                    Triple("Pechay", "🥬", 30),
                    Triple("Okra", "🫛", 55),
                    Triple("Chili", "🌶️", 85),
                    Triple("Lettuce", "🥗", 45),
                    Triple("Kangkong", "🌿", 28),
                    Triple("Cucumber", "🥒", 50),
                    Triple("Sitaw", "🫘", 55),
                    Triple("Sweet Corn", "🌽", 75)
                )

                catalogCrops.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { (name, emoji, dth) ->
                            val isCurrent = plot.cropName?.equals(name, ignoreCase = true) == true
                            Surface(
                                onClick = { onAssignCrop(name, "Standard", selectedMethod) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isCurrent) Color(0xFF2E7D32) else Color(0xFF192318),
                                border = BorderStroke(1.dp, if (isCurrent) Color(0xFF4CAF50) else Color(0xFF2D3C2A)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = emoji, fontSize = 20.sp)
                                    Column {
                                        Text(text = name, color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "$dth days", color = Color(0xFFA0B09A), fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Delete Bed Action
        Surface(
            onClick = onRequestDeleteBed,
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF221616),
            border = BorderStroke(1.dp, Color(0xFF4D2626)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🗑 Remove ${plot.plotLabel}", color = Color(0xFFFF8A80), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TAB 2: GUIDE (Stage Progress + Actionable Tasks)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HubGuideTab(
    plot: CropPlot,
    todayTasks: List<FarmTask>,
    onCompleteTask: (String) -> Unit,
    onAdvanceStage: (ManagementStage) -> Unit
) {
    val currentStage = plot.currentStage

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Stage Tracker Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141C12)),
            border = BorderStroke(1.dp, Color(0xFF2B3825)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GROWTH STAGE: ${currentStage.label.uppercase()}",
                        color = Color(0xFFA5D6A7),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "Stage ${currentStage.stageNumber}/5", color = Color(0xFFA0B09A), fontSize = 10.sp)
                }

                // Progress Bar
                val progress = currentStage.stageNumber / 5f
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF4CAF50),
                    trackColor = Color(0xFF222C1F)
                )

                // Stage Steppers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val stages = ManagementStage.values()
                    val prevIdx = (currentStage.ordinal - 1).coerceAtLeast(0)
                    val nextIdx = (currentStage.ordinal + 1).coerceAtMost(stages.size - 1)

                    Surface(
                        onClick = { onAdvanceStage(stages[prevIdx]) },
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF1D281B),
                        border = BorderStroke(1.dp, Color(0xFF384F31)),
                        enabled = currentStage.ordinal > 0
                    ) {
                        Text(
                            text = "◀ Prev Stage",
                            color = if (currentStage.ordinal > 0) White else Color(0xFF556050),
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        onClick = { onAdvanceStage(stages[nextIdx]) },
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF2E7D32),
                        enabled = currentStage.ordinal < stages.size - 1
                    ) {
                        Text(
                            text = "Next Stage ▶",
                            color = White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Today's Bed Tasks
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141C12)),
            border = BorderStroke(1.dp, Color(0xFF2B3825)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "TODAY'S CHORES FOR ${plot.plotLabel.uppercase()} (${todayTasks.size})",
                    color = Color(0xFFA5D6A7),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                if (todayTasks.isEmpty()) {
                    Text(
                        text = "No pending chores for today. Watering and weeding on schedule! ✨",
                        color = Color(0xFF81C784),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                } else {
                    todayTasks.forEach { task ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF192318),
                            border = BorderStroke(1.dp, Color(0xFF2B3825)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = task.isCompleted,
                                    onCheckedChange = { onCompleteTask(task.id) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFF2E7D32),
                                        checkmarkColor = White,
                                        uncheckedColor = Color(0xFF4C5D47)
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(text = task.title, color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "Due today • ${task.taskType.name}", color = Color(0xFFA0B09A), fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TAB 3: CHECK-UP (Field Scouting & Diagnosis)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HubCheckupTab(
    plot: CropPlot,
    onGenerateTask: (String) -> Unit
) {
    val selectedSymptoms = remember { mutableStateMapOf<String, Boolean>() }

    val symptomCategories = remember {
        listOf(
            "Leaves" to listOf(
                SymptomOption("yl", "Older leaves yellow"),
                SymptomOption("yn", "New leaves yellow, green veins"),
                SymptomOption("sp", "Brown/black spots"),
                SymptomOption("cu", "Leaves curl / twist"),
                SymptomOption("wl", "Wilting in daytime"),
                SymptomOption("ho", "Chewed holes")
            ),
            "Pests" to listOf(
                SymptomOption("ap", "Aphids (sticky insects)"),
                SymptomOption("wf", "Whiteflies"),
                SymptomOption("ca", "Caterpillars"),
                SymptomOption("fb", "Holes inside fruit")
            ),
            "Site" to listOf(
                SymptomOption("wt", "Soil soggy / standing water"),
                SymptomOption("dr", "Soil dry and cracked"),
                SymptomOption("wd", "Heavy weeds")
            )
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141C12)),
            border = BorderStroke(1.dp, Color(0xFF2B3825)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "🩺 FIELD SCOUTING & DIAGNOSIS (${plot.cropName ?: "Plant"})",
                    color = Color(0xFFA5D6A7),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Tap symptoms you observe on this bed to diagnose causes and generate care tasks.",
                    color = Color(0xFFA0B09A),
                    fontSize = 11.sp
                )

                symptomCategories.forEach { (category, options) ->
                    Text(
                        text = category.uppercase(),
                        color = Color(0xFFFFD54F),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        options.forEach { opt ->
                            val isChecked = selectedSymptoms[opt.id] == true
                            Surface(
                                onClick = { selectedSymptoms[opt.id] = !isChecked },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isChecked) Color(0xFF2E7D32) else Color(0xFF192318),
                                border = BorderStroke(1.dp, if (isChecked) Color(0xFF4CAF50) else Color(0xFF2D3C2A))
                            ) {
                                Text(
                                    text = opt.label,
                                    color = if (isChecked) White else Color(0xFFA0B09A),
                                    fontSize = 11.sp,
                                    fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Diagnosis Results Card
        val activeCount = selectedSymptoms.count { it.value }
        if (activeCount > 0) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF182215)),
                border = BorderStroke(1.dp, Color(0xFF385532)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "LIKELY CAUSES & REMEDIES",
                        color = Color(0xFFA5D6A7),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (selectedSymptoms["wt"] == true || selectedSymptoms["wl"] == true) {
                        DiagnosisItem(
                            title = "Root Suffocation / Poor Drainage",
                            remedy = "Stop watering immediately. Loosen compacted soil 20cm around bed, raise bed 25cm, and add Trichoderma compost.",
                            onAddTask = { onGenerateTask("Loosen soil & raise bed for drainage") }
                        )
                    }
                    if (selectedSymptoms["yl"] == true) {
                        DiagnosisItem(
                            title = "Nitrogen Hunger",
                            remedy = "Side-dress 2 kg vermicast or spray diluted fermented plant juice (FPJ) in the late afternoon.",
                            onAddTask = { onGenerateTask("Side-dress vermicast / organic FPJ") }
                        )
                    }
                    if (selectedSymptoms["ap"] == true || selectedSymptoms["wf"] == true) {
                        DiagnosisItem(
                            title = "Sucking Insects (Aphids / Whiteflies)",
                            remedy = "Spray neem extract or mild soapy water at dusk. Install yellow sticky traps near foliage.",
                            onAddTask = { onGenerateTask("Apply neem spray & place sticky traps") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DiagnosisItem(
    title: String,
    remedy: String,
    onAddTask: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF141C12),
        border = BorderStroke(1.dp, Color(0xFF2E4029)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "• $title", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text = remedy, color = Color(0xFFC0CDC0), fontSize = 11.sp, lineHeight = 15.sp)
            Surface(
                onClick = onAddTask,
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFF2E7D32)
            ) {
                Text(
                    text = "＋ Add Chore to Today's Tasks",
                    color = White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TAB 4: HARVEST & ROTATION (Yield Logging + Rotation Loop)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HubHarvestTab(
    plot: CropPlot,
    onRecordHarvest: (yieldKg: Float, isFinal: Boolean) -> Unit,
    onStartRotation: (nextCropName: String) -> Unit
) {
    var yieldText by remember { mutableStateOf("1.5") }
    var isFinalHarvest by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Yield Logging Form
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141C12)),
            border = BorderStroke(1.dp, Color(0xFF2B3825)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "🌾 RECORD HARVEST FOR ${plot.plotLabel.uppercase()}",
                    color = Color(0xFFA5D6A7),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                // Picking Type Segmented Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF192318), RoundedCornerShape(8.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(false to "🌱 Ongoing Picking", true to "🌾 Final Harvest").forEach { (finalChoice, label) ->
                        val isSel = isFinalHarvest == finalChoice
                        Surface(
                            onClick = { isFinalHarvest = finalChoice },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSel) Color(0xFF2E7D32) else Color.Transparent,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = label,
                                color = if (isSel) White else Color(0xFFA0B09A),
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 7.dp)
                            )
                        }
                    }
                }

                // Yield Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Harvested Quantity (kg):", color = Color(0xFFA0B09A), fontSize = 12.sp)
                    OutlinedTextField(
                        value = yieldText,
                        onValueChange = { yieldText = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.width(110.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = White,
                            unfocusedTextColor = White,
                            focusedBorderColor = Color(0xFF81C784),
                            unfocusedBorderColor = Color(0xFF2E4029)
                        )
                    )
                }

                Button(
                    onClick = {
                        val yield = yieldText.toFloatOrNull() ?: 1.0f
                        onRecordHarvest(yield, isFinalHarvest)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isFinalHarvest) "🌾 Record Final Harvest & Clear Bed" else "🌱 Record Ongoing Pick",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Closed-Loop Rotation Engine
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141C12)),
            border = BorderStroke(1.dp, Color(0xFF2B3825)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "🔄 NEXT CROP ROTATION (PREVENT MONOCULTURE)",
                    color = Color(0xFFA5D6A7),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "After this cycle, rotate to a different botanical family to break pest cycles and replenish nitrogen.",
                    color = Color(0xFFA0B09A),
                    fontSize = 11.sp
                )

                val suggestions = listOf(
                    Triple("Pechay (Brassica)", "🥬", "Fast 30-day leafy crop, consumes remaining nitrogen"),
                    Triple("Sitaw (Legume)", "🫘", "Fixes atmospheric nitrogen into soil for future crops"),
                    Triple("Cucumber (Cucurbit)", "🥒", "Deep rooting, breaks Solanaceae pest cycles")
                )

                suggestions.forEach { (cropName, emoji, why) ->
                    Surface(
                        onClick = { onStartRotation(cropName.substringBefore(" ")) },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF192318),
                        border = BorderStroke(1.dp, Color(0xFF2B3825)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = emoji, fontSize = 20.sp)
                                Column {
                                    Text(text = cropName, color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(text = why, color = Color(0xFFA0B09A), fontSize = 10.sp)
                                }
                            }
                            Text(text = "▶ Start", color = Color(0xFF81C784), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// DIALOGS: ADD BED & DELETE BED CONFIRMATION
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun AddBedDialog(
    existingCount: Int,
    onDismiss: () -> Unit,
    onConfirm: (label: String, widthM: Float, heightM: Float, soilType: SoilType) -> Unit
) {
    var label by remember { mutableStateOf("Bed #${existingCount + 1}") }
    var selectedPresetIndex by remember { mutableStateOf(0) }
    var selectedSoil by remember { mutableStateOf(SoilType.LOAM) }

    // Presets: (Name, Width to Height, Description)
    val presets = remember {
        listOf(
            Triple("Standard Bed (4.0m × 1.2m)", 4.0f to 1.2f, "Optimal bio-intensive double reach"),
            Triple("Long Field Bed (6.0m × 1.2m)", 6.0f to 1.2f, "High-yield market garden row"),
            Triple("Compact Bed (2.0m × 1.0m)", 2.0f to 1.0f, "Herbs, nursery, or tight space"),
            Triple("Raised Bed (3.0m × 0.8m)", 3.0f to 0.8f, "Timber / masonry raised garden")
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1B241A),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "🟫", fontSize = 20.sp)
                Text(
                    text = "Add New Planting Bed",
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Bed Label
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Bed Name / Label", color = Color(0xFFA0B09A)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = White,
                        focusedBorderColor = Color(0xFF4CAF50),
                        unfocusedBorderColor = Color(0xFF385532),
                        focusedContainerColor = Color(0xFF141C12),
                        unfocusedContainerColor = Color(0xFF141C12)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Bed Dimension Presets
                Text(
                    text = "Bed Dimensions & Style",
                    color = Color(0xFF81C784),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    presets.forEachIndexed { index, (presetName, dims, desc) ->
                        val isSel = index == selectedPresetIndex
                        Surface(
                            onClick = { selectedPresetIndex = index },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) Color(0xFF233820) else Color(0xFF141D13),
                            border = BorderStroke(1.dp, if (isSel) Color(0xFF4CAF50) else Color(0xFF2C3E2A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = presetName,
                                        color = if (isSel) White else Color(0xFFD0DDD0),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = desc,
                                        color = Color(0xFFA0B09A),
                                        fontSize = 10.sp
                                    )
                                }
                                val areaSqM = dims.first * dims.second
                                Text(
                                    text = "%.1f m²".format(areaSqM),
                                    color = Color(0xFFFFD54F),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Soil Type
                Text(
                    text = "Soil Type",
                    color = Color(0xFF81C784),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SoilType.values().forEach { soil ->
                        val isSel = soil == selectedSoil
                        Surface(
                            onClick = { selectedSoil = soil },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSel) Color(0xFF2E7D32) else Color(0xFF141D13),
                            border = BorderStroke(1.dp, if (isSel) Color(0xFF4CAF50) else Color(0xFF2B3A29))
                        ) {
                            Text(
                                text = soil.label,
                                color = if (isSel) White else Color(0xFFA0B09A),
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val dims = presets[selectedPresetIndex].second
                    onConfirm(label, dims.first, dims.second, selectedSoil)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text(text = "＋ Create Bed", color = White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = Color(0xFFA0B09A))
            }
        }
    )
}

@Composable
private fun DeleteBedConfirmDialog(
    plot: CropPlot,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val isPlanted = !plot.cropName.isNullOrBlank()
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF201515),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "🗑", fontSize = 22.sp)
                Text(
                    text = "Delete ${plot.plotLabel}?",
                    color = Color(0xFFFF8A80),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (isPlanted) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF381C1C),
                        border = BorderStroke(1.dp, Color(0xFF6B2828)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "⚠️ Active Crop Warning",
                                color = Color(0xFFFF8A80),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "This bed has planted ${plot.cropName} (${plot.currentStage.label}).",
                                color = White,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Deleting this bed will permanently remove it from your farm map and erase its crop cycle history and maintenance records.",
                                color = Color(0xFFFFCDD2),
                                fontSize = 11.sp
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Are you sure you want to remove ${plot.plotLabel} (%.1fm × %.1fm) from the farm layout? This action cannot be undone.".format(plot.widthM, plot.heightM),
                        color = Color(0xFFE0E0E0),
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Text(text = "🗑 Delete Bed", color = White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = Color(0xFFA0B09A))
            }
        }
    )
}

