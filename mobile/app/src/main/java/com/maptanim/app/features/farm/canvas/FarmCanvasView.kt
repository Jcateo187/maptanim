package com.maptanim.app.features.farm.canvas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.features.farm.components.BedFloatingActionRing
import com.maptanim.app.features.farm.components.CanvasSmartGuidanceHud
import com.maptanim.app.features.farm.components.CanvasTopToolbar
import com.maptanim.app.features.farm.components.EditBottomLayout
import com.maptanim.app.features.farm.dialogs.DirectionalBedSizingDialog
import com.maptanim.app.features.farm.renderer.canvas.CropSvgRenderer
import com.maptanim.app.features.farm.renderer.canvas.TopDownCamera
import com.maptanim.app.features.farm.renderer.canvas.TopDownFarmCanvas
import com.maptanim.app.features.farm.renderer.canvas.TopDownProjection
import com.maptanim.app.features.farm.tabs.plan.CropTray
import com.maptanim.app.features.farm.viewmodel.EditUiState
import com.maptanim.app.features.farm.viewmodel.EditViewModel

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val MeasureOrange = Color(0xFFE65100)
private val CardBorderColor = Color(0xFFE0E0E0)

/**
 * FarmCanvasView — Interactive 2D Top-Down Farm Viewport with visual CropTray.
 * Features:
 * 1. TopDownFarmCanvas with multi-touch gestures, 8-point handles, and multi-crop support.
 * 2. Visual CropTray with drag-and-drop & tap-to-plant directly into beds.
 * 3. Contextual Bed Action Card showing all planted crops with 1-tap Daily Guide CTA.
 * 4. Responsive floating toolbar for Add Bed, Crops Tray, Zoom, Scale, and Layers.
 */
@Composable
fun FarmCanvasView(
    editUiState: EditUiState,
    editViewModel: EditViewModel,
    canvasLayer: CanvasLayer,
    showYardRulers: Boolean = true,
    yardWidthM: Float = 15f,
    yardHeightM: Float = 10f,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onOpenYardGuide: () -> Unit,
    onSelectLayer: (CanvasLayer) -> Unit,
    onRequestAddBed: () -> Unit,
    onDeletePlot: (String) -> Unit,
    onOpenInspect: () -> Unit = {},
    onOpenTimeline: () -> Unit = {},
    onNavigateToGuide: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var liveCamera by remember { mutableStateOf(TopDownCamera(zoom = 0.5f)) }
    val selectedPlot = editUiState.plots.firstOrNull { it.id == editUiState.selectedPlotId }
    var showDirectionalSizingDialog by remember { mutableStateOf(false) }

    // ── CropTray & Drag-and-Drop State ───────────────────────────────────────
    var isCropTrayVisible by remember { mutableStateOf(false) }
    var activeCropName by remember { mutableStateOf("") }
    var activeCropId by remember { mutableStateOf("") }

    var isDraggingCrop by remember { mutableStateOf(false) }
    var dragCropName by remember { mutableStateOf("Tomato") }
    var dragCropId by remember { mutableStateOf("tomato") }
    var dragCropImageUrl by remember { mutableStateOf<String?>(null) }
    var dragTouchPos by remember { mutableStateOf(Offset.Zero) }

    // Hover position: convert screen drag position → world coordinate
    val hoverWorldPos = remember(isDraggingCrop, dragTouchPos, liveCamera, dragCropId, dragCropName) {
        if (isDraggingCrop) {
            val isBedDrag = dragCropId.startsWith("bed", ignoreCase = true) || dragCropName.contains("Bed", ignoreCase = true)
            val w = if (isBedDrag) 2.0f else 1.0f
            val h = 1.0f
            val rawWorld = TopDownProjection.screenToWorld(dragTouchPos.x, dragTouchPos.y, liveCamera)
            val centered = Offset(rawWorld.x - w / 2f, rawWorld.y - h / 2f)
            val snapped = TopDownProjection.snapToGrid(centered)
            Offset(snapped.x.coerceIn(0f, 45.0f - w), snapped.y.coerceIn(0f, 45.0f - h))
        } else null
    }

    val isValidPlacement = remember(isDraggingCrop, dragCropId, dragCropName, hoverWorldPos, editUiState.plots) {
        if (isDraggingCrop && hoverWorldPos != null) {
            val hx = hoverWorldPos.x; val hy = hoverWorldPos.y
            val isBedDrag = dragCropId.startsWith("bed", ignoreCase = true) || dragCropName.contains("Bed", ignoreCase = true)
            val w = if (isBedDrag) 2.0f else 1.0f
            val h = 1.0f
            val inBounds = hx >= 0f && hy >= 0f && (hx + w) <= 45.0f && (hy + h) <= 45.0f

            if (!isBedDrag) {
                val cropCenterX = hx + w / 2f
                val cropCenterY = hy + h / 2f
                val existingBed = editUiState.plots.firstOrNull { plot ->
                    cropCenterX >= plot.posX && cropCenterX < (plot.posX + plot.widthM) &&
                    cropCenterY >= plot.posY && cropCenterY < (plot.posY + plot.heightM)
                }
                inBounds && existingBed != null
            } else {
                val overlaps = editUiState.plots.any { plot ->
                    hx < (plot.posX + plot.widthM) && (hx + w) > plot.posX &&
                    hy < (plot.posY + plot.heightM) && (hy + h) > plot.posY
                }
                inBounds && !overlaps
            }
        } else true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1B221A))
    ) {
        // ── 1. The Full 2D Top-Down Interactive Canvas Engine ───────────────
        TopDownFarmCanvas(
            modifier = Modifier.fillMaxSize(),
            uiState = editUiState,
            editViewModel = editViewModel,
            activeCropName = activeCropName,
            activeCropId = activeCropId,
            hoverWorldPos = hoverWorldPos,
            isValidPlacement = isValidPlacement,
            isDraggingCrop = isDraggingCrop,
            dragCropName = dragCropName,
            showYardRulers = showYardRulers,
            yardWidthM = yardWidthM,
            yardHeightM = yardHeightM,
            initialZoom = 0.5f,
            onCameraChanged = { liveCamera = it }
        )

        // ── 2. Top Canvas Floating Toolbar ──────────────────────────────────
        CanvasTopToolbar(
            isCropTrayVisible = isCropTrayVisible,
            onToggleCropTray = { isCropTrayVisible = !isCropTrayVisible },
            onRequestAddBed = onRequestAddBed,
            isSaving = editUiState.isSaving,
            showYardRulers = showYardRulers,
            onOpenYardGuide = onOpenYardGuide,
            onZoomIn = {
                val newZoom = (liveCamera.zoom * 1.3f).coerceIn(0.2f, 3.5f)
                liveCamera = liveCamera.copy(zoom = newZoom)
            },
            onZoomOut = {
                val newZoom = (liveCamera.zoom * 0.75f).coerceIn(0.2f, 3.5f)
                liveCamera = liveCamera.copy(zoom = newZoom)
            },
            isExpanded = isExpanded,
            onToggleExpand = onToggleExpand,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // ── Drop Feedback Toast Banner ──────────────────────────────────────
        editUiState.dropFeedbackMessage?.let { msg ->
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 50.dp, start = 16.dp, end = 16.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xF2212121),
                border = BorderStroke(1.dp, Color(0xFFFFB300))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = msg, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // ── 2b. Smart Agricultural Guidance HUD (Canva/Miro Contextual Assistant) ────
        CanvasSmartGuidanceHud(
            plots = editUiState.plots,
            cropZones = editUiState.cropZones,
            selectedPlotId = editUiState.selectedPlotId,
            isDraggingCrop = isDraggingCrop,
            dragCropName = dragCropName,
            onOpenAddBed = onRequestAddBed,
            onOpenCropTray = { isCropTrayVisible = true },
            onOpenGuide = onNavigateToGuide,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = if (editUiState.dropFeedbackMessage != null) 84.dp else 48.dp, start = 10.dp, end = 10.dp)
        )

        // ── 3. Planter-Style Floating Circular Action Ring ──────────────────
        if (selectedPlot != null && !isDraggingCrop && !isCropTrayVisible) {
            val primaryCrop = remember(selectedPlot.id, editUiState.cropZones) {
                editUiState.cropZones.firstOrNull {
                    it.plotId == selectedPlot.id && !it.cropName.isNullOrBlank() && !it.cropName.equals("Bed", ignoreCase = true)
                }?.cropName ?: selectedPlot.cropName
            }

            BedFloatingActionRing(
                selectedPlot = selectedPlot,
                primaryCropName = primaryCrop,
                onOpenDossier = onNavigateToGuide,
                onOpenCropTray = { isCropTrayVisible = true },
                onOpenSizingDialog = { showDirectionalSizingDialog = true },
                onDeletePlot = { onDeletePlot(selectedPlot.id) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }

        if (showDirectionalSizingDialog && selectedPlot != null) {
            DirectionalBedSizingDialog(
                initialWidthM = selectedPlot.widthM,
                initialHeightM = selectedPlot.heightM,
                plotLabel = selectedPlot.plotLabel,
                onApplyDimensions = { newW, newH ->
                    editViewModel.setPlotDimensions(selectedPlot.id, newW, newH)
                    showDirectionalSizingDialog = false
                },
                onDismiss = { showDirectionalSizingDialog = false }
            )
        }

        // ── 4. The Visual CropTray (Bottom Panel) ───────────────────────────
        if (isCropTrayVisible) {
            CropTray(
                modifier = Modifier.align(Alignment.BottomCenter),
                selectedCropName = activeCropName,
                availableCrops = editUiState.availableCrops,
                isSyncing = editUiState.isSyncingCrops,
                onSyncRequested = { editViewModel.refreshCropsFromRemote() },
                onCropSelected = { newCropName, newCropId ->
                    if (activeCropName.equals(newCropName, ignoreCase = true)) {
                        activeCropName = ""; activeCropId = ""
                        editViewModel.selectTool(com.maptanim.app.domain.model.EditTool.SELECT_MOVE)
                    } else {
                        activeCropName = newCropName; activeCropId = newCropId
                        editViewModel.selectTool(com.maptanim.app.domain.model.EditTool.ADD_PLANT)
                    }
                },
                onCropDragStart = { cropName, cropId, imageUrl, startOffset ->
                    isDraggingCrop = true
                    dragCropName = cropName; dragCropId = cropId
                    dragCropImageUrl = imageUrl; dragTouchPos = startOffset
                },
                onCropDragging = { currentOffset -> dragTouchPos = currentOffset },
                onCropDragEnd = { dropOffset ->
                    if (isDraggingCrop) {
                        val isBedDrag = dragCropId.startsWith("bed", ignoreCase = true) || dragCropName.contains("Bed", ignoreCase = true)
                        val bedW = 2.0f
                        val bedH = 1.0f
                        val dropWorld = TopDownProjection.screenToWorld(dropOffset.x, dropOffset.y, liveCamera)

                        isDraggingCrop = false

                        if (isBedDrag) {
                            val centered = Offset(dropWorld.x - bedW / 2f, dropWorld.y - bedH / 2f)
                            val snapped = TopDownProjection.snapToGrid(centered)
                            val safeX = snapped.x.coerceIn(0f, 45.0f - bedW)
                            val safeY = snapped.y.coerceIn(0f, 45.0f - bedH)
                            val canDrop = safeX >= 0f && safeY >= 0f && (safeX + bedW) <= 45.0f && (safeY + bedH) <= 45.0f &&
                                    !editUiState.plots.any { plot ->
                                        safeX < (plot.posX + plot.widthM) && (safeX + bedW) > plot.posX &&
                                        safeY < (plot.posY + plot.heightM) && (safeY + bedH) > plot.posY
                                    }
                            if (canDrop) {
                                editViewModel.addDirectPlantingPlot(safeX, safeY, "Bed", "bed", initialW = bedW, initialH = bedH)
                            } else {
                                editViewModel.reportInvalidDropLocation("Hindi maaaring maglagay ng kama dito: May nakaharang.")
                            }
                        } else {
                            val targetBed = editUiState.plots.firstOrNull { plot ->
                                dropWorld.x >= plot.posX && dropWorld.x <= (plot.posX + plot.widthM) &&
                                dropWorld.y >= plot.posY && dropWorld.y <= (plot.posY + plot.heightM)
                            }
                            if (targetBed != null) {
                                editViewModel.plantCropInBed(targetBed.id, dragCropName, dragCropId, dropWorld.x, dropWorld.y)
                            } else {
                                editViewModel.reportInvalidDropLocation("I-drop ang pananim sa loob ng isang Garden Bed.")
                            }
                        }
                    }
                },
                onClose = { isCropTrayVisible = false }
            )
        }

        // ── 5. Floating Drag Preview Bubble ──────────────────────────────────
        if (isDraggingCrop) {
            val isBedDrag = dragCropId.startsWith("bed", ignoreCase = true) || dragCropName.contains("Bed", ignoreCase = true)
            val density = androidx.compose.ui.platform.LocalDensity.current
            val halfSizePx = with(density) { 36.dp.roundToPx() }

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = dragTouchPos.x.toInt() - halfSizePx,
                            y = dragTouchPos.y.toInt() - halfSizePx
                        )
                    }
                    .size(72.dp)
                    .shadow(10.dp, CircleShape)
                    .background(Color(0xFF1A1F16).copy(alpha = 0.92f), CircleShape)
                    .border(2.dp, if (isValidPlacement) Color(0xFF4CAF50) else Color(0xFFF44336), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isBedDrag) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF6D4C41))
                            .border(1.5.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Bed", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                } else {
                    Canvas(modifier = Modifier.size(46.dp)) {
                        CropSvgRenderer.drawCropSvg(
                            drawScope = this,
                            cropName = dragCropName,
                            center = Offset(size.width / 2f, size.height / 2f),
                            sizePx = size.width * 0.85f
                        )
                    }
                }
            }
        }
    }
}
