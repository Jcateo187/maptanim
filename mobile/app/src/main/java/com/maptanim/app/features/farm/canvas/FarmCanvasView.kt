package com.maptanim.app.features.farm.canvas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.draw.clip
import com.maptanim.app.features.farm.companion.CompanionProximityAlert
import com.maptanim.app.features.farm.companion.FarmCompanionManager
import com.maptanim.app.features.farm.companion.NeighborStatus
import kotlinx.coroutines.delay
import com.maptanim.app.features.farm.components.CropCircleActionOverlay
import com.maptanim.app.features.farm.components.VegetableSelectionOverlay
import com.maptanim.app.features.farm.renderer.canvas.CropSvgRenderer
import com.maptanim.app.features.farm.renderer.canvas.TopDownCamera
import com.maptanim.app.features.farm.renderer.canvas.TopDownFarmCanvas
import com.maptanim.app.features.farm.renderer.canvas.TopDownProjection
import com.maptanim.app.features.farm.renderer.model.FOOT_IN_METERS
import com.maptanim.app.features.farm.renderer.model.SIX_INCHES_IN_METERS
import com.maptanim.app.features.farm.renderer.model.realLifeCropDiameterM
import com.maptanim.app.features.farm.viewmodel.EditUiState
import com.maptanim.app.features.farm.viewmodel.EditViewModel

private val LushGreen = Color(0xFF2E7D32)
private val SoilBrownCanvas = Color(0xFF261814)

data class SelectedCropData(
    val zoneId: String,
    val plotId: String,
    val cropName: String,
    val cropId: String,
    val varietyName: String? = null,
    val worldCenterX: Float,
    val worldCenterY: Float,
    val worldTopY: Float,
    val widthM: Float,
    val heightM: Float,
    val gardenLabel: String
)

/**
 * FarmCanvasView — Interactive 2D Top-Down Centered Garden Viewport with:
 * - Centered brown soil grid and crisp white boxes (Picture 1 style)
 * - Metric yard numbers (height on left, width on top)
 * - Direct planting: vegetable drag-and-drop straight into 2D grid, no bed
 * - Slight black tint when a crop is clicked (map drag disabled while active, tap tint to dismiss)
 * - 5-icon radial floating overlay around selected crop (Picture 2 style)
 * - Single [+] ADD VEGETABLES button in the bottom right corner
 */
@Composable
fun FarmCanvasView(
    editUiState: EditUiState,
    editViewModel: EditViewModel,
    canvasLayer: CanvasLayer = CanvasLayer.CROPS,
    showYardRulers: Boolean = true,
    showMeasurement: Boolean = true,
    showCompanion: Boolean = false,
    showVariety: Boolean = false,
    showCropName: Boolean = true,
    yardWidthM: Float = 3.048f,
    yardHeightM: Float = 2.4384f,
    isExpanded: Boolean = true,
    isVegetableOverlayVisible: Boolean = false,
    onToggleVegetableOverlay: () -> Unit = {},
    onToggleExpand: () -> Unit = {},
    onOpenYardGuide: () -> Unit = {},
    onSelectLayer: (CanvasLayer) -> Unit = {},
    onDeletePlot: (String) -> Unit = {},
    onOpenCropInfo: (cropName: String) -> Unit = {},
    onOpenCareScreen: (cropName: String, variety: String, bedLabel: String, plotId: String) -> Unit = { _, _, _, _ -> },
    onOpenInspect: () -> Unit = {},
    onOpenTimeline: () -> Unit = {},
    onOpenCalendar: (cropName: String, variety: String?, gardenLabel: String, plotId: String, zoneId: String) -> Unit = { _, _, _, _, _ -> },
    onNavigateToGuide: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var liveCamera by remember { mutableStateOf(TopDownCamera(zoom = 0.5f)) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    val selectedPlot = editUiState.plots.firstOrNull { it.id == editUiState.selectedPlotId } ?: editUiState.plots.firstOrNull()
    val activePlotId = selectedPlot?.id
    val selectedZone = editUiState.cropZones.firstOrNull { it.id == editUiState.selectedZoneId && (activePlotId == null || it.plotId == activePlotId) }

    // ── Active Garden Crops: ONLY crops belonging to this active garden! ─────
    val activeGardenCrops = remember(editUiState.cropZones, activePlotId) {
        if (activePlotId != null) {
            editUiState.cropZones.filter {
                it.plotId == activePlotId &&
                !it.cropName.isNullOrBlank() &&
                !it.cropName.equals("Bed", ignoreCase = true)
            }
        } else emptyList()
    }

    // ── Copy Workflow State ──────────────────────────────────────────────────
    var pendingCopyCrop by remember { mutableStateOf<SelectedCropData?>(null) }

    // ── Drag-and-Drop State ──────────────────────────────────────────────────
    var isDraggingCrop by remember { mutableStateOf(false) }
    var dragCropName by remember { mutableStateOf("Tomato") }
    var dragCropId by remember { mutableStateOf("tomato") }
    var dragVariety by remember { mutableStateOf<String?>(null) }
    var dragTouchPos by remember { mutableStateOf(Offset.Zero) }

    // Hover position: convert screen drag position → world coordinate on centered grid
    val hoverWorldPos = remember(isDraggingCrop, dragCropName, dragTouchPos, liveCamera, yardWidthM, yardHeightM) {
        if (isDraggingCrop) {
            val diam = realLifeCropDiameterM(dragCropName)
            val rawWorld = TopDownProjection.screenToWorld(dragTouchPos.x, dragTouchPos.y, liveCamera)
            val centered = Offset(rawWorld.x - diam / 2f, rawWorld.y - diam / 2f)
            val snapStep = SIX_INCHES_IN_METERS // 6-inch small crop box snap
            val snappedX = (Math.round(centered.x / snapStep) * snapStep).coerceIn(0f, (yardWidthM - diam).coerceAtLeast(0f))
            val snappedY = (Math.round(centered.y / snapStep) * snapStep).coerceIn(0f, (yardHeightM - diam).coerceAtLeast(0f))
            Offset(snappedX, snappedY)
        } else null
    }

    val isValidPlacement = remember(isDraggingCrop, dragCropName, hoverWorldPos, yardWidthM, yardHeightM) {
        if (isDraggingCrop && hoverWorldPos != null) {
            val diam = realLifeCropDiameterM(dragCropName)
            val hx = hoverWorldPos.x; val hy = hoverWorldPos.y
            hx >= 0f && hy >= 0f && (hx + diam) <= (yardWidthM + 0.05f) && (hy + diam) <= (yardHeightM + 0.05f)
        } else true
    }

    // ── Real-Time Companion Proximity Analysis during Drag ─────────────────────
    val dragCompanionAnalysis = remember(isDraggingCrop, dragCropName, hoverWorldPos, activeGardenCrops) {
        if (isDraggingCrop && hoverWorldPos != null) {
            FarmCompanionManager.analyzeDragHover(dragCropName, hoverWorldPos, activeGardenCrops)
        } else null
    }

    // 5-Second Companion Alert state
    var activeCompanionAlert by remember { mutableStateOf<CompanionProximityAlert?>(null) }
    var alertTimerKey by remember { mutableIntStateOf(0) }

    // Trigger 5-second pop-up alert when dragging near a companion
    LaunchedEffect(dragCompanionAnalysis?.neighborCrop, dragCompanionAnalysis?.status) {
        val analysis = dragCompanionAnalysis
        if (analysis != null && analysis.status != NeighborStatus.NEUTRAL) {
            activeCompanionAlert = CompanionProximityAlert(
                id = "${analysis.primaryCrop}_${analysis.neighborCrop}",
                status = analysis.status,
                headline = if (analysis.status == NeighborStatus.ANTAGONIST) "BAD NEIGHBOR CONFLICT" else "BENEFICIAL COMPANION",
                cropsSubtitle = "${analysis.primaryCrop} ${if (analysis.status == NeighborStatus.ANTAGONIST) "⚔️" else "🤝"} ${analysis.neighborCrop} (${String.format("%.2fm", analysis.distanceM)})",
                explanation = analysis.reason,
                distanceM = analysis.distanceM
            )
            alertTimerKey++
        }
    }

    // 5-second auto-dismiss countdown timer
    LaunchedEffect(alertTimerKey) {
        if (activeCompanionAlert != null) {
            delay(5000L)
            activeCompanionAlert = null
        }
    }

    // ── Resolve Selected Crop on Direct Grid ─────────────────────────────────
    val selectedCrop = remember(selectedZone, selectedPlot, activeGardenCrops) {
        if (selectedZone != null && selectedZone.plotId == activePlotId &&
            !selectedZone.cropName.isNullOrBlank() && !selectedZone.cropName.equals("Bed", ignoreCase = true)
        ) {
            val realDiamW = if (selectedZone.widthM > 0.05f) selectedZone.widthM else realLifeCropDiameterM(selectedZone.cropName)
            val realDiamH = if (selectedZone.heightM > 0.05f) selectedZone.heightM else realLifeCropDiameterM(selectedZone.cropName)
            val worldX = selectedZone.offsetX
            val worldY = selectedZone.offsetY
            SelectedCropData(
                zoneId = selectedZone.id,
                plotId = selectedPlot?.id ?: selectedZone.plotId,
                cropName = selectedZone.cropName ?: "Vegetable",
                cropId = (selectedZone.cropName ?: "crop").lowercase(),
                varietyName = selectedPlot?.cropVariety?.ifBlank { null },
                worldCenterX = worldX + realDiamW / 2f,
                worldCenterY = worldY + realDiamH / 2f,
                worldTopY = worldY,
                widthM = realDiamW,
                heightM = realDiamH,
                gardenLabel = selectedPlot?.plotLabel ?: "Garden"
            )
        } else null
    }

    // Dynamic screen position of selectedCrop that moves with camera pan & zoom
    val cropScreenCenter = remember(selectedCrop, liveCamera) {
        if (selectedCrop != null) {
            TopDownProjection.worldToScreen(selectedCrop.worldCenterX, selectedCrop.worldCenterY, liveCamera)
        } else Offset.Zero
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SoilBrownCanvas)
            .onSizeChanged { canvasSize = it }
    ) {
        // ── 1. The 2D Centered Canvas with Brown Soil, White Grid & Metric Rulers ─
        TopDownFarmCanvas(
            modifier = Modifier.fillMaxSize(),
            uiState = editUiState,
            editViewModel = editViewModel,
            activeCropName = pendingCopyCrop?.cropName ?: (if (isDraggingCrop) dragCropName else ""),
            activeCropId = pendingCopyCrop?.cropId ?: (if (isDraggingCrop) dragCropId else ""),
            activeVariety = pendingCopyCrop?.varietyName ?: dragVariety,
            activeCropDiameterM = pendingCopyCrop?.widthM,
            hoverWorldPos = hoverWorldPos,
            isValidPlacement = isValidPlacement,
            isDraggingCrop = isDraggingCrop,
            dragCropName = dragCropName,
            showYardRulers = showMeasurement,
            showMeasurement = showMeasurement,
            showCompanion = showCompanion,
            showVariety = showVariety,
            showCropName = showCropName,
            yardWidthM = yardWidthM,
            yardHeightM = yardHeightM,
            initialZoom = 0.5f,
            onCameraChanged = { liveCamera = it },
            onCropPlaced = {
                pendingCopyCrop = null
            },
            onCropTapped = {
                pendingCopyCrop = null
                val tappedZone = selectedZone
                if (tappedZone != null) {
                    val analysis = FarmCompanionManager.analyzePlacedCrop(tappedZone, activeGardenCrops)
                    if (analysis.status != NeighborStatus.NEUTRAL) {
                        activeCompanionAlert = CompanionProximityAlert(
                            id = "${analysis.primaryCrop}_${analysis.neighborCrop}",
                            status = analysis.status,
                            headline = if (analysis.status == NeighborStatus.ANTAGONIST) "BAD NEIGHBOR CONFLICT" else "BENEFICIAL COMPANION",
                            cropsSubtitle = "${analysis.primaryCrop} ${if (analysis.status == NeighborStatus.ANTAGONIST) "⚔️" else "🤝"} ${analysis.neighborCrop} (${String.format("%.2fm", analysis.distanceM)})",
                            explanation = analysis.reason,
                            distanceM = analysis.distanceM
                        )
                        alertTimerKey++
                    }
                }
            },
            onTapOutsideCrop = {
                if (pendingCopyCrop == null) {
                    editViewModel.selectCropZone(null)
                }
            }
        )

        // ── 5-Second Companion Proximity Alert Pop-Up ───────────────────────
        AnimatedVisibility(
            visible = activeCompanionAlert != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            activeCompanionAlert?.let { alert ->
                val isAntagonist = alert.status == NeighborStatus.ANTAGONIST
                val headerBg = if (isAntagonist) Color(0xFF2E1214) else Color(0xFF0F2B14)
                val borderColor = if (isAntagonist) Color(0xFFFF5252) else Color(0xFF69F0AE)
                val iconText = if (isAntagonist) "⚠️" else "🛡️"
                val titleColor = if (isAntagonist) Color(0xFFFF8A80) else Color(0xFFB9F6CA)

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = headerBg.copy(alpha = 0.96f),
                    border = BorderStroke(1.5.dp, borderColor),
                    shadowElevation = 10.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = iconText, fontSize = 20.sp)
                                Column {
                                    Text(
                                        text = alert.headline,
                                        color = titleColor,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.4.sp
                                    )
                                    Text(
                                        text = alert.cropsSubtitle,
                                        color = Color.White,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            IconButton(
                                onClick = { activeCompanionAlert = null },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = alert.explanation,
                            color = Color(0xFFECEFF1),
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = borderColor,
                            trackColor = Color.White.copy(alpha = 0.15f)
                        )
                    }
                }
            }
        }

        // ── Drop Feedback Banner ─────────────────────────────────────────────
        editUiState.dropFeedbackMessage?.let { msg ->
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
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
                    IconButton(
                        onClick = { editViewModel.clearDropFeedbackMessage() },
                        modifier = Modifier.size(16.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color.Gray)
                    }
                }
            }
        }

        // ── Copy Placement Guidance Banner ───────────────────────────────────
        pendingCopyCrop?.let { copied ->
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xF21B2E1D),
                border = BorderStroke(1.2.dp, Color(0xFF81C784)),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        tint = Color(0xFF81C784),
                        modifier = Modifier.size(20.dp)
                    )
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = "Copied ${copied.cropName}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tap anywhere on the garden grid to plant",
                            color = Color(0xFFC8E6C9),
                            fontSize = 10.sp
                        )
                    }
                    TextButton(
                        onClick = { pendingCopyCrop = null },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFFCC80))
                    ) {
                        Text("Cancel", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // ── 2. BLACK TINT & 5-ICON FLOATING OVERLAY AROUND CLICKED CROP ───────
        val isCropVisibleOnScreen = remember(canvasSize, cropScreenCenter) {
            canvasSize.width > 0 && canvasSize.height > 0 &&
            cropScreenCenter.x >= -120f && cropScreenCenter.x <= (canvasSize.width + 120f) &&
            cropScreenCenter.y >= -120f && cropScreenCenter.y <= (canvasSize.height + 120f)
        }

        if (selectedCrop != null && !isDraggingCrop && !isVegetableOverlayVisible && pendingCopyCrop == null) {
            // Slight black tint overlay that intercepts touches so the map cannot be dragged
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.38f))
                    .pointerInput(Unit) {
                        detectTapGestures {
                            editViewModel.selectCropZone(null)
                        }
                    }
            )

            // Radial floating circular action buttons centered around the crop (Picture 2)
            if (isCropVisibleOnScreen) {
                val overlaySizePx = with(density) { 240.dp.roundToPx() }
                val overlayHalfPx = overlaySizePx / 2
                val cropCenterX = cropScreenCenter.x.toInt()
                val cropCenterY = cropScreenCenter.y.toInt()

                val rawX = cropCenterX - overlayHalfPx
                val rawY = cropCenterY - overlayHalfPx

                val maxX = (canvasSize.width - overlaySizePx - 8).coerceAtLeast(8)
                val maxY = (canvasSize.height - overlaySizePx - 8).coerceAtLeast(8)
                val clampedX = rawX.coerceIn(8, maxX)
                val clampedY = rawY.coerceIn(8, maxY)

                CropCircleActionOverlay(
                    cropName = selectedCrop.cropName,
                    varietyName = selectedCrop.varietyName,
                    isResizeActive = editUiState.isResizeMode,
                    cropWidthM = selectedCrop.widthM,
                    onOpenInfo = {
                        onOpenCropInfo(selectedCrop.cropName)
                    },
                    onOpenCheckUp = {
                        onOpenCareScreen(
                            selectedCrop.cropName,
                            selectedCrop.varietyName ?: "",
                            selectedCrop.gardenLabel,
                            selectedCrop.plotId
                        )
                    },
                    onOpenCalendar = {
                        onOpenCalendar(
                            selectedCrop.cropName,
                            selectedCrop.varietyName,
                            selectedCrop.gardenLabel,
                            selectedCrop.plotId,
                            selectedCrop.zoneId
                        )
                    },
                    onResizeCrop = {
                        editViewModel.toggleResizeMode()
                    },
                    onUpdateSize = { newDiameterM ->
                        editViewModel.setCropZoneDimensions(selectedCrop.zoneId, newDiameterM)
                    },
                    onCopyCrop = {
                        pendingCopyCrop = selectedCrop
                        editViewModel.selectCropZone(null)
                    },
                    onDeleteCrop = {
                        editViewModel.removeCropFromBed(selectedCrop.zoneId)
                        editViewModel.selectCropZone(null)
                    },
                    onDismiss = {
                        editViewModel.selectCropZone(null)
                    },
                    modifier = Modifier.offset { IntOffset(clampedX, clampedY) }
                )
            }
        }

        // ── 3. BUTTON AT THE BOTTOM RIGHT: [+] ADD VEGETABLES (Single +) ──────
        if (!isVegetableOverlayVisible && !isDraggingCrop && pendingCopyCrop == null && selectedCrop == null) {
            Surface(
                onClick = onToggleVegetableOverlay,
                shape = RoundedCornerShape(24.dp),
                color = LushGreen,
                shadowElevation = 8.dp,
                border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.85f)),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(end = 16.dp, bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Vegetables",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "ADD VEGETABLES",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // ── 4. THE VEGETABLE SELECTION OVERLAY (Direct Planting) ─────────────
        VegetableSelectionOverlay(
            isVisible = isVegetableOverlayVisible,
            onDismiss = onToggleVegetableOverlay,
            onOpenCropInfo = onOpenCropInfo,
            onCropDragStart = { cropName, cropId, variety, screenOffset ->
                isDraggingCrop = true
                dragCropName = cropName
                dragCropId = cropId
                dragVariety = variety
                dragTouchPos = screenOffset
                if (isVegetableOverlayVisible) {
                    onToggleVegetableOverlay()
                }
            },
            onCropDragging = { screenOffset ->
                dragTouchPos = screenOffset
            },
            onCropDragEnd = { dropOffset ->
                if (isDraggingCrop) {
                    val dropWorld = TopDownProjection.screenToWorld(dropOffset.x, dropOffset.y, liveCamera)
                    isDraggingCrop = false

                    // Direct planting on centered grid within garden bounds
                    if (dropWorld.x >= 0f && dropWorld.x <= yardWidthM &&
                        dropWorld.y >= 0f && dropWorld.y <= yardHeightM
                    ) {
                        val targetBed = selectedPlot ?: editUiState.plots.firstOrNull()
                        if (targetBed != null) {
                            editViewModel.plantCropInBed(
                                bedPlotId = targetBed.id,
                                newCropName = dragCropName,
                                newCropId = dragCropId,
                                atWorldX = dropWorld.x,
                                atWorldY = dropWorld.y,
                                variety = dragVariety
                            )
                        }
                    } else {
                        editViewModel.reportInvalidDropLocation("Paki-drop ang pananim sa loob ng garden grid.")
                    }
                }
            },
            onPlantDirectly = { cropName, cropId, variety ->
                val target = selectedPlot ?: editUiState.plots.firstOrNull()
                if (target != null) {
                    editViewModel.plantCropInBed(
                        bedPlotId = target.id,
                        newCropName = cropName,
                        newCropId = cropId,
                        variety = variety
                    )
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // ── 5. FLOATING DRAG PREVIEW BUBBLE (With Dynamic Companion Glow & Badges) ────
        if (isDraggingCrop) {
            val halfSizePx = with(density) { 38.dp.roundToPx() }
            val borderColor = when (dragCompanionAnalysis?.status) {
                NeighborStatus.ANTAGONIST -> Color(0xFFFF1744)
                NeighborStatus.BENEFICIAL -> Color(0xFF00E676)
                else -> if (isValidPlacement) Color(0xFF4CAF50) else Color(0xFFF44336)
            }
            val haloColor = when (dragCompanionAnalysis?.status) {
                NeighborStatus.ANTAGONIST -> Color(0xFFFF1744).copy(alpha = 0.35f)
                NeighborStatus.BENEFICIAL -> Color(0xFF00E676).copy(alpha = 0.35f)
                else -> Color.Transparent
            }

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = dragTouchPos.x.toInt() - halfSizePx,
                            y = dragTouchPos.y.toInt() - halfSizePx
                        )
                    }
                    .size(76.dp),
                contentAlignment = Alignment.Center
            ) {
                if (haloColor != Color.Transparent) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(haloColor, CircleShape)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .shadow(12.dp, CircleShape)
                        .background(Color(0xFF1A1F16).copy(alpha = 0.94f), CircleShape)
                        .border(2.5.dp, borderColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(44.dp)) {
                        CropSvgRenderer.drawCropSvg(
                            drawScope = this,
                            cropName = dragCropName,
                            center = Offset(size.width / 2f, size.height / 2f),
                            sizePx = size.width * 0.85f
                        )
                    }

                    if (dragCompanionAnalysis?.status == NeighborStatus.ANTAGONIST) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFD32F2F),
                            border = BorderStroke(1.5.dp, Color.White),
                            modifier = Modifier
                                .size(22.dp)
                                .align(Alignment.TopEnd)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "⚠️", fontSize = 11.sp)
                            }
                        }
                    } else if (dragCompanionAnalysis?.status == NeighborStatus.BENEFICIAL) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF2E7D32),
                            border = BorderStroke(1.5.dp, Color.White),
                            modifier = Modifier
                                .size(22.dp)
                                .align(Alignment.TopEnd)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "🛡️", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
