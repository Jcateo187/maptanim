package com.maptanim.app.features.farm.renderer.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.CompanionRelation
import com.maptanim.app.domain.model.EditTool
import com.maptanim.app.dss.knowledgebase.CompanionDataProvider
import com.maptanim.app.features.farm.renderer.gesture.HandleType
import com.maptanim.app.features.farm.renderer.model.CropZoneRenderData
import com.maptanim.app.features.farm.renderer.model.FOOT_IN_METERS
import com.maptanim.app.features.farm.renderer.model.INCH_IN_METERS
import com.maptanim.app.features.farm.renderer.model.PlotRenderData
import com.maptanim.app.features.farm.renderer.model.SIX_INCHES_IN_METERS
import com.maptanim.app.features.farm.renderer.model.YARD_IN_METERS
import com.maptanim.app.features.farm.renderer.model.cropSinglePlantSpacingM
import com.maptanim.app.features.farm.renderer.model.realLifeCropDiameterM
import com.maptanim.app.features.farm.viewmodel.EditUiState
import com.maptanim.app.features.farm.viewmodel.EditViewModel
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import com.maptanim.app.features.farm.companion.FarmCompanionManager
import com.maptanim.app.features.farm.companion.NeighborStatus
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

// ═══════════════════════════════════════════════════════════════════════════════
// TopDownCamera — Simple orthographic 2D camera state
// ═══════════════════════════════════════════════════════════════════════════════

data class TopDownCamera(
    val panX: Float = 0f,
    val panY: Float = 0f,
    val zoom: Float = 0.5f
)

// ═══════════════════════════════════════════════════════════════════════════════
// TopDownProjection — Flat 2D orthographic coordinate conversion
// ═══════════════════════════════════════════════════════════════════════════════

object TopDownProjection {
    /** Pixels per meter at zoom = 1.0 */
    const val PPM = 40f

    fun worldToScreen(worldX: Float, worldY: Float, camera: TopDownCamera): Offset =
        Offset(worldX * PPM * camera.zoom + camera.panX, worldY * PPM * camera.zoom + camera.panY)

    fun screenToWorld(screenX: Float, screenY: Float, camera: TopDownCamera): Offset =
        Offset((screenX - camera.panX) / (PPM * camera.zoom), (screenY - camera.panY) / (PPM * camera.zoom))

    fun worldSizeToScreen(meters: Float, camera: TopDownCamera): Float = meters * PPM * camera.zoom

    fun snapToGrid(worldPos: Offset, snap: Float = SIX_INCHES_IN_METERS): Offset =
        Offset(Math.round(worldPos.x / snap) * snap, Math.round(worldPos.y / snap) * snap)

    fun fitCamera(
        farmW: Float = 45f,
        farmH: Float = 45f,
        screenW: Float,
        screenH: Float,
        topPadding: Float = 140f,
        bottomPadding: Float = 180f,
        sidePadding: Float = 30f,
        leftPadding: Float = sidePadding,
        rightPadding: Float = sidePadding
    ): TopDownCamera {
        val availW = (screenW - leftPadding - rightPadding).coerceAtLeast(100f)
        val availH = (screenH - topPadding - bottomPadding).coerceAtLeast(100f)
        val zoom = minOf(availW / (farmW * PPM), availH / (farmH * PPM)).coerceIn(0.15f, 3.5f)
        val farmScreenW = farmW * PPM * zoom
        val farmScreenH = farmH * PPM * zoom
        val panX = leftPadding + (availW - farmScreenW) / 2f
        val panY = topPadding + (availH - farmScreenH) / 2f
        return TopDownCamera(panX = panX, panY = panY, zoom = zoom)
    }

    /**
     * Clamps camera pan and zoom so the garden grid is comfortably visible.
     * Maximum zoom in allows zooming in to 10 inches (~0.254m).
     * Maximum zoom out fits the grid with only a little gap around it.
     */
    fun clampCamera(
        camera: TopDownCamera,
        farmW: Float = 10f,
        farmH: Float = 8f,
        screenW: Float,
        screenH: Float,
        topPadding: Float = 100f,
        bottomPadding: Float = 140f,
        sidePadding: Float = 30f,
        leftPadding: Float = sidePadding,
        rightPadding: Float = sidePadding
    ): TopDownCamera {
        if (screenW <= 0f || screenH <= 0f) return camera

        val availW = (screenW - leftPadding - rightPadding).coerceAtLeast(100f)
        val availH = (screenH - topPadding - bottomPadding).coerceAtLeast(100f)
        val safeW = farmW.coerceAtLeast(1.0f)
        val safeH = farmH.coerceAtLeast(1.0f)

        // 2 inches in world meters: 2 * 0.0254m = 0.0508m (permits zooming deeply down to 1-inch box resolution)
        val minVisibleM = 0.0508f
        val fitZoom = minOf(availW / (safeW * PPM), availH / (safeH * PPM))
        val minZoom = (fitZoom * 0.85f).coerceAtLeast(0.05f)
        val maxZoom = (availW / (minVisibleM * PPM)).coerceAtLeast(fitZoom)
        val clampedZoom = camera.zoom.coerceIn(minZoom, maxZoom)

        val farmScreenW = safeW * PPM * clampedZoom
        val farmScreenH = safeH * PPM * clampedZoom

        // Only have a little comfortable gap to drag a grid in normal ways
        val panGapX = (availW * 0.25f).coerceAtLeast(60f)
        val panGapY = (availH * 0.25f).coerceAtLeast(60f)

        val minPanX = screenW - rightPadding - farmScreenW - panGapX
        val maxPanX = leftPadding + panGapX
        val clampedPanX = camera.panX.coerceIn(minOf(minPanX, maxPanX), maxOf(minPanX, maxPanX))

        val minPanY = (screenH - bottomPadding) - farmScreenH - panGapY
        val maxPanY = topPadding + panGapY
        val clampedPanY = camera.panY.coerceIn(minOf(minPanY, maxPanY), maxOf(minPanY, maxPanY))

        return TopDownCamera(panX = clampedPanX, panY = clampedPanY, zoom = clampedZoom)
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Design Tokens
// ═══════════════════════════════════════════════════════════════════════════════

internal val CanvasBg         = Color(0xFF261814) // Dark neutral background outside garden
internal val FarmInnerBg      = Color(0xFF4E342E) // Rich brown agricultural soil for garden grid
internal val GridSubBoxLineColor = Color(0x40FFFFFF) // Subtle white lines for small 6-inch crop boxes
internal val GridBoxLineColor = Color(0x85FFFFFF)    // Clear white lines for 1-foot (12-inch) zones
internal val GridMajorLineColor = Color(0xF5FFFFFF)  // Solid white major lines for 1-yard (36-inch) boundaries
internal val GridDotColor     = Color(0x38D7CCC8) // Grid dot / indicator color
internal val FarmBorderColor  = Color.White       // Crisp white farm perimeter
internal val SelectionBlue    = Color(0xFF2979FF)
internal val HandleFill       = Color.White
internal val HandleStroke     = Color(0xFF1565C0)
internal val ValidHover       = Color(0xFF4CAF50)
internal val InvalidHover     = Color(0xFFF44336)
internal val LabelBg          = Color(0xFF3E2723)
internal val BedFill          = Color(0xFF5D4037)
internal val BedBorder        = Color(0xFF8D6E63)
internal val PlantedBedFill   = Color(0xFF3E2723)

// ═══════════════════════════════════════════════════════════════════════════════
// Gesture state tracking
// ═══════════════════════════════════════════════════════════════════════════════

private enum class DragMode { IDLE, PAN, MOVE_CROP, RESIZE_HANDLE }

// ═══════════════════════════════════════════════════════════════════════════════
// TopDownFarmCanvas — Professional 2D orthographic top-down farm canvas
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * TopDownFarmCanvas — Canva/Miro-style flat 2D farm canvas with rectangular beds,
 * crop icons, free-form positioning, snapping, resize handles, and professional
 * design-editor interactions.
 *
 * Renders entirely in a flat orthographic 2D top-down view with rectangular beds and vector SVG crops.
 */
@Composable
fun TopDownFarmCanvas(
    modifier: Modifier = Modifier,
    uiState: EditUiState,
    editViewModel: EditViewModel,
    activeCropName: String = "",
    activeCropId: String = "",
    activeVariety: String? = null,
    activeCropDiameterM: Float? = null,
    hoverWorldPos: Offset? = null,
    isValidPlacement: Boolean = true,
    isDraggingCrop: Boolean = false,
    dragCropName: String = "",
    showYardRulers: Boolean = true,
    showMeasurement: Boolean = true,
    showBoundary: Boolean = false,
    yardWidthM: Float = 3.048f,
    yardHeightM: Float = 2.4384f,
    showCompanion: Boolean = false,
    showVariety: Boolean = false,
    showCropName: Boolean = true,
    initialZoom: Float = 0.5f,
    onCameraChanged: (TopDownCamera) -> Unit = {},
    onCropPlaced: () -> Unit = {},
    onCropTapped: (CropZoneRenderData) -> Unit = {},
    onTapOutsideCrop: () -> Unit = {}
) {
    val effectiveShowRulers = (showYardRulers || showBoundary) && showMeasurement
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    var camera by remember { mutableStateOf(TopDownCamera(zoom = initialZoom)) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var hasAnimated by remember { mutableStateOf(false) }

    // Gesture tracking
    var dragMode by remember { mutableStateOf(DragMode.IDLE) }
    var dragStartScreenPos by remember { mutableStateOf(Offset.Zero) }
    var dragAccumWorld by remember { mutableStateOf(Offset.Zero) }
    var activeHandle by remember { mutableStateOf<HandleType?>(null) }
    var activeDragCropId by remember { mutableStateOf<String?>(null) }
    var lastPointerCount by remember { mutableIntStateOf(0) }
    var lastPinchDist by remember { mutableFloatStateOf(0f) }

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val topPaddingPx = with(density) { 56.dp.toPx() }
    val bottomPaddingPx = with(density) { if (isLandscape) 20.dp.toPx() else 60.dp.toPx() }
    val leftPaddingPx = with(density) { if (isLandscape) 20.dp.toPx() else 16.dp.toPx() }
    val rightPaddingPx = with(density) { if (isLandscape) 20.dp.toPx() else 16.dp.toPx() }

    // Entrance: Center specifically on the garden grid with yardWidthM and yardHeightM
    LaunchedEffect(canvasSize, yardWidthM, yardHeightM, isLandscape) {
        if (canvasSize.width > 0 && canvasSize.height > 0) {
            val safeW = yardWidthM.coerceAtLeast(1.0f)
            val safeH = yardHeightM.coerceAtLeast(1.0f)
            val availW = (canvasSize.width.toFloat() - leftPaddingPx - rightPaddingPx).coerceAtLeast(100f)
            val availH = (canvasSize.height.toFloat() - topPaddingPx - bottomPaddingPx).coerceAtLeast(100f)
            val fitZoom = minOf(availW / (safeW * TopDownProjection.PPM), availH / (safeH * TopDownProjection.PPM))
            val targetZoom = fitZoom.coerceAtLeast(0.05f)
            val farmScreenW = safeW * TopDownProjection.PPM * targetZoom
            val farmScreenH = safeH * TopDownProjection.PPM * targetZoom
            val panX = leftPaddingPx + (availW - farmScreenW) / 2f
            val panY = topPaddingPx + (availH - farmScreenH) / 2f
            val initCam = TopDownCamera(panX = panX, panY = panY, zoom = targetZoom)
            camera = initCam
            onCameraChanged(initCam)
            hasAnimated = true
        }
    }

    // Report camera changes
    LaunchedEffect(camera) { onCameraChanged(camera) }

    // Snapshot current state for gesture handlers
    val currentPlots by rememberUpdatedState(uiState.plots)
    val currentCropZones by rememberUpdatedState(uiState.cropZones)
    val currentSelectedPlotId by rememberUpdatedState(uiState.selectedPlotId)
    val currentSelectedZoneId by rememberUpdatedState(uiState.selectedZoneId)
    val currentIsResizeMode by rememberUpdatedState(uiState.isResizeMode)
    val currentIsSnapEnabled by rememberUpdatedState(uiState.isSnapEnabled)
    val currentActiveTool by rememberUpdatedState(uiState.activeTool)
    val currentIsGridEnabled by rememberUpdatedState(uiState.isGridEnabled)

    val touchSlop = with(density) { 8f * this.density }
    val handleHitRadius = 0.6f // world meters

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { canvasSize = it }
            .pointerInput(
                uiState.selectedPlotId, uiState.selectedZoneId, uiState.isResizeMode,
                uiState.activeTool, activeCropName, activeCropId
            ) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val changes = event.changes
                        val pointerCount = changes.count { it.pressed }

                        when (event.type) {
                            PointerEventType.Press -> {
                                if (pointerCount == 1 && lastPointerCount == 0) {
                                    val pos = changes.first().position
                                    dragStartScreenPos = pos
                                    dragAccumWorld = Offset.Zero
                                    val world = TopDownProjection.screenToWorld(pos.x, pos.y, camera)

                                    // Hit test: handle → crop on grid (hold to drag) → pan
                                    val activePlotId = currentSelectedPlotId ?: currentPlots.firstOrNull()?.id
                                    val selectedZone = currentCropZones.firstOrNull { it.id == currentSelectedZoneId && (activePlotId == null || it.plotId == activePlotId) }
                                    val hitHandle = when {
                                        currentIsResizeMode && selectedZone != null ->
                                            hitTestZoneHandle(world, selectedZone, handleHitRadius)
                                        else -> null
                                    }

                                    val hitCrop = hitTestCropZone(world, currentCropZones, activePlotId)

                                    when {
                                        hitHandle != null -> {
                                            dragMode = DragMode.RESIZE_HANDLE
                                            activeHandle = hitHandle
                                            if (selectedZone != null) {
                                                editViewModel.onZoneHandleDragStart(selectedZone.id)
                                            }
                                        }
                                        hitCrop != null -> {
                                            // Directly allow holding and dragging any crop on the grid!
                                            dragMode = DragMode.MOVE_CROP
                                            activeDragCropId = hitCrop.id
                                            editViewModel.onCropZoneDragStart(hitCrop.id)
                                        }
                                        else -> {
                                            dragMode = DragMode.PAN
                                        }
                                    }
                                }
                                if (pointerCount >= 2 && lastPointerCount < 2) {
                                    // Start pinch zoom
                                    val p1 = changes[0].position
                                    val p2 = changes[1].position
                                    lastPinchDist = dist(p1, p2)
                                    dragMode = DragMode.PAN // Switch to pan mode during pinch
                                }
                                lastPointerCount = pointerCount
                            }
                            PointerEventType.Move -> {
                                if (pointerCount >= 2) {
                                    // Pinch zoom
                                    val p1 = changes[0].position
                                    val p2 = changes[1].position
                                    val curDist = dist(p1, p2)
                                    if (lastPinchDist > 10f) {
                                        val factor = curDist / lastPinchDist
                                        val availW = (canvasSize.width.toFloat() - leftPaddingPx - rightPaddingPx).coerceAtLeast(100f)
                                        val availH = (canvasSize.height.toFloat() - topPaddingPx - bottomPaddingPx).coerceAtLeast(100f)
                                        val safeW = yardWidthM.coerceAtLeast(1.0f)
                                        val safeH = yardHeightM.coerceAtLeast(1.0f)
                                        val minVisibleM = 0.0508f
                                        val fitZoom = minOf(availW / (safeW * TopDownProjection.PPM), availH / (safeH * TopDownProjection.PPM))
                                        val minZoom = (fitZoom * 0.85f).coerceAtLeast(0.05f)
                                        val maxZoom = (availW / (minVisibleM * TopDownProjection.PPM)).coerceAtLeast(fitZoom)
                                        val targetZoom = (camera.zoom * factor).coerceIn(minZoom, maxZoom)
                                        val actualScale = if (camera.zoom > 0f) targetZoom / camera.zoom else 1f

                                        val center = Offset((p1.x + p2.x) / 2f, (p1.y + p2.y) / 2f)
                                        val newPanX = center.x - (center.x - camera.panX) * actualScale
                                        val newPanY = center.y - (center.y - camera.panY) * actualScale
                                        val rawCam = camera.copy(panX = newPanX, panY = newPanY, zoom = targetZoom)
                                        camera = TopDownProjection.clampCamera(
                                            rawCam, farmW = yardWidthM, farmH = yardHeightM,
                                            screenW = canvasSize.width.toFloat(), screenH = canvasSize.height.toFloat(),
                                            topPadding = topPaddingPx, bottomPadding = bottomPaddingPx,
                                            leftPadding = leftPaddingPx, rightPadding = rightPaddingPx
                                        )
                                    }
                                    lastPinchDist = curDist
                                    changes.forEach { it.consume() }
                                } else if (pointerCount == 1) {
                                    val change = changes.firstOrNull { it.pressed } ?: continue
                                    val delta = change.position - dragStartScreenPos
                                    val totalScreenDist = dist(change.position, dragStartScreenPos)

                                    if (totalScreenDist > touchSlop) {
                                        val screenDelta = change.position - change.previousPosition
                                        val incrementalWorldDelta = Offset(
                                            screenDelta.x / (TopDownProjection.PPM * camera.zoom),
                                            screenDelta.y / (TopDownProjection.PPM * camera.zoom)
                                        )

                                        when (dragMode) {
                                            DragMode.MOVE_CROP -> {
                                                dragAccumWorld += incrementalWorldDelta
                                                val zid = activeDragCropId ?: currentSelectedZoneId
                                                if (zid != null) {
                                                    editViewModel.moveCropZone(zid, dragAccumWorld)
                                                }
                                                change.consume()
                                            }
                                            DragMode.RESIZE_HANDLE -> {
                                                dragAccumWorld += incrementalWorldDelta
                                                val h = activeHandle
                                                val zid = currentSelectedZoneId
                                                if (h != null && zid != null) {
                                                    editViewModel.resizeCropZoneByHandle(zid, h, dragAccumWorld)
                                                }
                                                change.consume()
                                            }
                                            DragMode.PAN -> {
                                                val rawCam = camera.copy(
                                                    panX = camera.panX + screenDelta.x,
                                                    panY = camera.panY + screenDelta.y
                                                )
                                                camera = TopDownProjection.clampCamera(
                                                    rawCam, farmW = yardWidthM, farmH = yardHeightM,
                                                    screenW = canvasSize.width.toFloat(), screenH = canvasSize.height.toFloat(),
                                                    topPadding = topPaddingPx, bottomPadding = bottomPaddingPx,
                                                    leftPadding = leftPaddingPx, rightPadding = rightPaddingPx
                                                )
                                                change.consume()
                                            }
                                            DragMode.IDLE -> {}
                                        }
                                    }
                                }
                                lastPointerCount = pointerCount
                            }
                            PointerEventType.Release -> {
                                if (pointerCount == 0) {
                                    val releasePos = changes.firstOrNull()?.position ?: dragStartScreenPos
                                    val totalDist = dist(releasePos, dragStartScreenPos)

                                    if (totalDist < touchSlop && dragMode != DragMode.IDLE) {
                                        // TAP — no significant drag
                                        val worldPos = TopDownProjection.screenToWorld(
                                            dragStartScreenPos.x, dragStartScreenPos.y, camera
                                        )
                                        val activePlotId = currentSelectedPlotId ?: currentPlots.firstOrNull()?.id
                                        val hitCrop = hitTestCropZone(worldPos, currentCropZones, activePlotId)
                                        val hitPlot = hitTestPlot(worldPos, currentPlots)
                                        handleTap(
                                            worldPos = worldPos,
                                            hitCrop = hitCrop,
                                            hitPlot = hitPlot,
                                            activeTool = currentActiveTool,
                                            activeCropName = activeCropName,
                                            activeCropId = activeCropId,
                                            activeVariety = activeVariety,
                                            activeCropDiameterM = activeCropDiameterM,
                                            isSnapEnabled = currentIsSnapEnabled,
                                            plots = currentPlots,
                                            currentSelectedPlotId = currentSelectedPlotId,
                                            editViewModel = editViewModel,
                                            onCropPlaced = onCropPlaced,
                                            onCropTapped = onCropTapped,
                                            onTapOutsideCrop = onTapOutsideCrop
                                        )
                                    }

                                    // Finalize drag
                                    when (dragMode) {
                                        DragMode.MOVE_CROP -> {
                                            val zid = activeDragCropId ?: currentSelectedZoneId
                                            zid?.let { editViewModel.onCropZoneDragEnd(it) }
                                        }
                                        DragMode.RESIZE_HANDLE -> {
                                            if (currentSelectedZoneId != null) {
                                                editViewModel.onZoneHandleDragEnd()
                                            }
                                        }
                                        else -> {}
                                    }

                                    dragMode = DragMode.IDLE
                                    activeHandle = null
                                    activeDragCropId = null
                                    dragAccumWorld = Offset.Zero
                                    lastPointerCount = 0
                                    lastPinchDist = 0f
                                }
                                lastPointerCount = pointerCount
                            }
                            else -> { lastPointerCount = pointerCount }
                        }
                    }
                }
            }
    ) {
        // ── 1. Outside Background ──────────────────────────────────────
        drawRect(CanvasBg, Offset.Zero, size)

        // ── 2. Centered Garden Grid with White Boxes & Yard Numbers ────
        drawYardPerimeterAndRulers(camera, yardWidthM, yardHeightM, effectiveShowRulers, textMeasurer)

        // ── 3. Crops Planted Directly on Grid (No Bed) ─────────────────
        val activePlotId = currentSelectedPlotId ?: currentPlots.firstOrNull()?.id
        val gardenCrops = if (activePlotId != null) {
            uiState.cropZones.filter {
                it.plotId == activePlotId &&
                !it.cropName.isNullOrBlank() &&
                !it.cropName.equals("Bed", ignoreCase = true)
            }
        } else emptyList()

        for (zone in gardenCrops) {
            drawDirectCrop(
                zone = zone,
                allZones = gardenCrops,
                isSelected = (zone.id == currentSelectedZoneId),
                isResizeMode = currentIsResizeMode,
                showCompanion = showCompanion,
                showVariety = showVariety,
                showMeasurement = showMeasurement,
                showCropName = showCropName,
                varietyName = currentPlots.firstOrNull { it.id == zone.plotId }?.cropVariety,
                camera = camera,
                textMeasurer = textMeasurer
            )
        }

        // ── 4. Drag Hover Tile Preview ─────────────────────────────────
        if (isDraggingCrop && hoverWorldPos != null) {
            val hoverDiam = realLifeCropDiameterM(activeCropName)
            val dragAnalysis = FarmCompanionManager.analyzeDragHover(activeCropName, hoverWorldPos, gardenCrops)
            drawHoverTile(
                worldPos = hoverWorldPos,
                isValid = isValidPlacement,
                neighborStatus = dragAnalysis?.status ?: NeighborStatus.NEUTRAL,
                camera = camera,
                w = hoverDiam,
                h = hoverDiam
            )
            if (activeCropName.isNotBlank()) {
                val center = TopDownProjection.worldToScreen(hoverWorldPos.x + hoverDiam / 2f, hoverWorldPos.y + hoverDiam / 2f, camera)
                val hoverSvgSize = TopDownProjection.worldSizeToScreen(hoverDiam, camera).coerceAtLeast(10f)
                CropSvgRenderer.drawCropSvg(
                    drawScope = this,
                    cropName = activeCropName,
                    center = center,
                    sizePx = hoverSvgSize
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Drawing helpers
// ═══════════════════════════════════════════════════════════════════════════════


private fun DrawScope.drawYardPerimeterAndRulers(
    camera: TopDownCamera,
    yardWidthM: Float = 3.048f,
    yardHeightM: Float = 2.4384f,
    showRulers: Boolean = true,
    textMeasurer: TextMeasurer
) {
    val tl = TopDownProjection.worldToScreen(0f, 0f, camera)
    val yardScreenW = TopDownProjection.worldSizeToScreen(yardWidthM, camera)
    val yardScreenH = TopDownProjection.worldSizeToScreen(yardHeightM, camera)
    val yardSize = Size(yardScreenW, yardScreenH)

    // 1. Brown agricultural soil ground surface of the centered garden grid
    drawRoundRect(
        color = FarmInnerBg,
        topLeft = tl,
        size = yardSize,
        cornerRadius = CornerRadius(4f)
    )

    // Metric & inch screen measurements
    val pixelPerInch = TopDownProjection.worldSizeToScreen(INCH_IN_METERS, camera)
    val pixelPer6Inch = pixelPerInch * 6f
    val pixelPerFoot = pixelPerInch * 12f

    // ── Level-of-Detail Line Alphas (smooth fading for boxes & lines) ──
    // 1-Inch lines: fade in smoothly between 10px and 26px per inch
    val inchLineAlpha = ((pixelPerInch - 10f) / 16f).coerceIn(0f, 1f) * 0.28f

    // 6-Inch lines: fade in smoothly between 12px and 24px per 6-inch
    val sixInchLineAlpha = ((pixelPer6Inch - 12f) / 12f).coerceIn(0f, 1f) * 0.45f

    // 1-Foot lines: visible once foot is > 15px
    val footLineAlpha = ((pixelPerFoot - 15f) / 15f).coerceIn(0f, 1f) * 0.65f

    // 1-Yard lines: solid when zoomed out (0.95f), softens to 0.35f guide when zoom is max to inches
    val yardMaxZoomFade = ((pixelPerInch - 20f) / 25f).coerceIn(0f, 1f)
    val yardLineAlpha = (0.95f - (0.60f * yardMaxZoomFade)).coerceIn(0.35f, 0.95f)

    // ── Screen bounds culling ──
    val totalInchesX = (yardWidthM / INCH_IN_METERS).roundToInt().coerceAtLeast(1)
    val totalInchesY = (yardHeightM / INCH_IN_METERS).roundToInt().coerceAtLeast(1)
    val totalSixInchesX = (yardWidthM / SIX_INCHES_IN_METERS).roundToInt().coerceAtLeast(1)
    val totalSixInchesY = (yardHeightM / SIX_INCHES_IN_METERS).roundToInt().coerceAtLeast(1)
    val totalFeetX = (yardWidthM / FOOT_IN_METERS).roundToInt().coerceAtLeast(1)
    val totalFeetY = (yardHeightM / FOOT_IN_METERS).roundToInt().coerceAtLeast(1)
    val totalYardsX = (yardWidthM / YARD_IN_METERS).roundToInt().coerceAtLeast(1)
    val totalYardsY = (yardHeightM / YARD_IN_METERS).roundToInt().coerceAtLeast(1)

    val minWorldX = maxOf(0f, TopDownProjection.screenToWorld(0f, 0f, camera).x)
    val maxWorldX = minOf(yardWidthM, TopDownProjection.screenToWorld(size.width, 0f, camera).x)
    val startInchX = maxOf(0, (minWorldX / INCH_IN_METERS).toInt() - 1)
    val endInchX = minOf(totalInchesX, (maxWorldX / INCH_IN_METERS).toInt() + 1)

    val minWorldY = maxOf(0f, TopDownProjection.screenToWorld(0f, 0f, camera).y)
    val maxWorldY = minOf(yardHeightM, TopDownProjection.screenToWorld(0f, size.height, camera).y)
    val startInchY = maxOf(0, (minWorldY / INCH_IN_METERS).toInt() - 1)
    val endInchY = minOf(totalInchesY, (maxWorldY / INCH_IN_METERS).toInt() + 1)

    // 2. White grid lines with smooth Level-of-Detail alpha blending inside garden bounds
    clipRect(left = tl.x, top = tl.y, right = tl.x + yardScreenW, bottom = tl.y + yardScreenH) {
        // A. 1-Inch Grid Lines (subtle 1" boxes)
        if (inchLineAlpha > 0.01f) {
            val inchColor = Color.White.copy(alpha = inchLineAlpha)
            for (i in startInchX..endInchX) {
                if (i % 6 != 0) {
                    val gx = (i * INCH_IN_METERS).coerceAtMost(yardWidthM)
                    val sx = TopDownProjection.worldToScreen(gx, 0f, camera).x
                    drawLine(inchColor, Offset(sx, tl.y), Offset(sx, tl.y + yardScreenH), strokeWidth = 0.5f)
                }
            }
            for (i in startInchY..endInchY) {
                if (i % 6 != 0) {
                    val gy = (i * INCH_IN_METERS).coerceAtMost(yardHeightM)
                    val sy = TopDownProjection.worldToScreen(0f, gy, camera).y
                    drawLine(inchColor, Offset(tl.x, sy), Offset(tl.x + yardScreenW, sy), strokeWidth = 0.5f)
                }
            }
        }

        // B. 6-Inch Grid Lines (medium crop zone boxes)
        if (sixInchLineAlpha > 0.01f) {
            val sixColor = Color.White.copy(alpha = sixInchLineAlpha)
            val start6X = (startInchX / 6).coerceAtLeast(0)
            val end6X = (endInchX / 6 + 1).coerceAtMost(totalSixInchesX)
            for (si in start6X..end6X) {
                if (si % 2 != 0) {
                    val gx = (si * SIX_INCHES_IN_METERS).coerceAtMost(yardWidthM)
                    val sx = TopDownProjection.worldToScreen(gx, 0f, camera).x
                    drawLine(sixColor, Offset(sx, tl.y), Offset(sx, tl.y + yardScreenH), strokeWidth = 0.8f)
                }
            }
            val start6Y = (startInchY / 6).coerceAtLeast(0)
            val end6Y = (endInchY / 6 + 1).coerceAtMost(totalSixInchesY)
            for (si in start6Y..end6Y) {
                if (si % 2 != 0) {
                    val gy = (si * SIX_INCHES_IN_METERS).coerceAtMost(yardHeightM)
                    val sy = TopDownProjection.worldToScreen(0f, gy, camera).y
                    drawLine(sixColor, Offset(tl.x, sy), Offset(tl.x + yardScreenW, sy), strokeWidth = 0.8f)
                }
            }
        }

        // C. 1-Foot Grid Lines (12-inch boundaries)
        if (footLineAlpha > 0.01f) {
            val footColor = Color.White.copy(alpha = footLineAlpha)
            val startFtX = (startInchX / 12).coerceAtLeast(0)
            val endFtX = (endInchX / 12 + 1).coerceAtMost(totalFeetX)
            for (fi in startFtX..endFtX) {
                if (fi % 3 != 0) {
                    val gx = (fi * FOOT_IN_METERS).coerceAtMost(yardWidthM)
                    val sx = TopDownProjection.worldToScreen(gx, 0f, camera).x
                    drawLine(footColor, Offset(sx, tl.y), Offset(sx, tl.y + yardScreenH), strokeWidth = 1.1f)
                }
            }
            val startFtY = (startInchY / 12).coerceAtLeast(0)
            val endFtY = (endInchY / 12 + 1).coerceAtMost(totalFeetY)
            for (fi in startFtY..endFtY) {
                if (fi % 3 != 0) {
                    val gy = (fi * FOOT_IN_METERS).coerceAtMost(yardHeightM)
                    val sy = TopDownProjection.worldToScreen(0f, gy, camera).y
                    drawLine(footColor, Offset(tl.x, sy), Offset(tl.x + yardScreenW, sy), strokeWidth = 1.1f)
                }
            }
        }

        // D. 1-Yard Major Lines (36-inch boundaries)
        val yardColor = Color.White.copy(alpha = yardLineAlpha)
        val startYdX = (startInchX / 36).coerceAtLeast(0)
        val endYdX = (endInchX / 36 + 1).coerceAtMost(totalYardsX)
        for (yi in startYdX..endYdX) {
            val gx = (yi * YARD_IN_METERS).coerceAtMost(yardWidthM)
            val sx = TopDownProjection.worldToScreen(gx, 0f, camera).x
            drawLine(yardColor, Offset(sx, tl.y), Offset(sx, tl.y + yardScreenH), strokeWidth = 1.8f)
        }
        val startYdY = (startInchY / 36).coerceAtLeast(0)
        val endYdY = (endInchY / 36 + 1).coerceAtMost(totalYardsY)
        for (yi in startYdY..endYdY) {
            val gy = (yi * YARD_IN_METERS).coerceAtMost(yardHeightM)
            val sy = TopDownProjection.worldToScreen(0f, gy, camera).y
            drawLine(yardColor, Offset(tl.x, sy), Offset(tl.x + yardScreenW, sy), strokeWidth = 1.8f)
        }
    }

    // 3. Crisp white perimeter boundary border
    drawRoundRect(
        color = Color.White.copy(alpha = 0.95f),
        topLeft = tl,
        size = yardSize,
        cornerRadius = CornerRadius(4f),
        style = Stroke(width = 2.5f)
    )

    // 4. Metric / Inch Rulers with Dynamic Fading
    if (showRulers) {
        // ── Number & Label Alphas ──
        // Big numbers (Yards): 1.0 when zoomed out, fades to 0.0 as zoom reaches max to inches (pixelPerInch 20 -> 36)
        val bigYardLabelAlpha = (1.0f - ((pixelPerInch - 20f) / 16f)).coerceIn(0f, 1f)

        // Foot numbers: visible at mid-zoom, fades as zoom reaches max to inches
        val footLabelAlpha = ((pixelPerFoot - 30f) / 25f).coerceIn(0f, 1f) * (1.0f - ((pixelPerInch - 24f) / 16f)).coerceIn(0f, 1f)

        // 6-inch numbers: fades in when 6-inch has comfortable space (>= 28px)
        val sixInchLabelAlpha = ((pixelPer6Inch - 28f) / 20f).coerceIn(0f, 1f)

        // 1-inch numbers: fades in when pixelPerInch >= 22px
        val inchLabelAlpha = ((pixelPerInch - 22f) / 14f).coerceIn(0f, 1f)

        val fontSize = when {
            inchLabelAlpha > 0.4f -> (pixelPerInch * 0.28f).coerceIn(6.5f, 9.5f).sp
            else -> (8.5f * camera.zoom).coerceIn(7f, 10f).sp
        }

        // ── Ruler Numbers Step with Gap (0, 2, 4, 6, 8, ...) ──
        // Tick and grid lines are there for every single 1 inch without number!
        val labelStep = when {
            pixelPerInch >= 12f -> 2 // Gap in numbers: 0", 2", 4", 6", 8", 10", 12"... (lines are there for 1", 3", 5"... without number)
            pixelPer6Inch >= 28f -> 6 // Every 6 inches (0", 6", 12", 18", 24"...)
            pixelPerFoot >= 30f -> 12 // Every 12 inches (0", 12", 24", 36"...)
            else -> 36 // Only yards (0", 36", 72"...)
        }

        // ── Top Axis (Width Rulers) ──
        for (i in startInchX..endInchX) {
            val gx = (i * INCH_IN_METERS).coerceAtMost(yardWidthM)
            val sx = TopDownProjection.worldToScreen(gx, 0f, camera).x
            val isYard = (i % 36 == 0)
            val isFoot = (i % 12 == 0)
            val is6Inch = (i % 6 == 0)
            val isEvenInch = (i % 2 == 0)

            // Tick lines are drawn for every inch:
            // Odd inches (1, 3, 5, 7, 9...) have tick lines without number!
            val (tickH, tickStroke, tickAlpha) = when {
                isYard -> Triple(11f, 1.8f, maxOf(0.6f, yardLineAlpha))
                isFoot -> Triple(8f, 1.2f, maxOf(0.5f, footLineAlpha))
                is6Inch -> Triple(6.5f, 1.0f, sixInchLineAlpha.coerceAtLeast(0.35f))
                isEvenInch -> Triple(4.5f, 0.7f, inchLineAlpha)
                else -> Triple(3.0f, 0.45f, inchLineAlpha) // Tick line is there for odd inches without number!
            }

            if (tickAlpha > 0.02f) {
                drawLine(
                    color = Color.White.copy(alpha = tickAlpha),
                    start = Offset(sx, tl.y - tickH),
                    end = Offset(sx, tl.y),
                    strokeWidth = tickStroke
                )
            }

            // Draw label with gap (0, 2, 4, 6, 8, 10, ...)
            val shouldDrawLabel = (i % labelStep == 0)
            if (shouldDrawLabel) {
                val (labelText, labelAlpha) = when {
                    i == 0 -> Pair("0\"", 1.0f)
                    isYard -> {
                        if (bigYardLabelAlpha > 0.25f) Pair("${i}\" (${i / 36}yd)", bigYardLabelAlpha)
                        else Pair("${i}\"", (1f - bigYardLabelAlpha).coerceIn(0f, 1f))
                    }
                    isFoot -> {
                        if (footLabelAlpha > 0.25f) Pair("${i}\" (${i / 12}ft)", footLabelAlpha)
                        else Pair("${i}\"", maxOf(sixInchLabelAlpha, inchLabelAlpha, 0.7f))
                    }
                    is6Inch -> Pair("${i}\"", maxOf(sixInchLabelAlpha, inchLabelAlpha))
                    else -> Pair("${i}\"", inchLabelAlpha)
                }

                drawRulerLabel(
                    text = labelText,
                    x = sx,
                    y = tl.y - tickH - 2f,
                    alpha = labelAlpha,
                    textMeasurer = textMeasurer,
                    fontSize = fontSize,
                    isTopAxis = true
                )
            }
        }

        // ── Left Axis (Height Rulers) ──
        for (i in startInchY..endInchY) {
            val gy = (i * INCH_IN_METERS).coerceAtMost(yardHeightM)
            val sy = TopDownProjection.worldToScreen(0f, gy, camera).y
            val isYard = (i % 36 == 0)
            val isFoot = (i % 12 == 0)
            val is6Inch = (i % 6 == 0)
            val isEvenInch = (i % 2 == 0)

            val (tickW, tickStroke, tickAlpha) = when {
                isYard -> Triple(11f, 1.8f, maxOf(0.6f, yardLineAlpha))
                isFoot -> Triple(8f, 1.2f, maxOf(0.5f, footLineAlpha))
                is6Inch -> Triple(6.5f, 1.0f, sixInchLineAlpha.coerceAtLeast(0.35f))
                isEvenInch -> Triple(4.5f, 0.7f, inchLineAlpha)
                else -> Triple(3.0f, 0.45f, inchLineAlpha) // Tick line is there for odd inches without number!
            }

            if (tickAlpha > 0.02f) {
                drawLine(
                    color = Color.White.copy(alpha = tickAlpha),
                    start = Offset(tl.x - tickW, sy),
                    end = Offset(tl.x, sy),
                    strokeWidth = tickStroke
                )
            }

            val shouldDrawLabel = (i % labelStep == 0)
            if (shouldDrawLabel) {
                val (labelText, labelAlpha) = when {
                    i == 0 -> Pair("0\"", 1.0f)
                    isYard -> {
                        if (bigYardLabelAlpha > 0.25f) Pair("${i}\" (${i / 36}yd)", bigYardLabelAlpha)
                        else Pair("${i}\"", (1f - bigYardLabelAlpha).coerceIn(0f, 1f))
                    }
                    isFoot -> {
                        if (footLabelAlpha > 0.25f) Pair("${i}\" (${i / 12}ft)", footLabelAlpha)
                        else Pair("${i}\"", maxOf(sixInchLabelAlpha, inchLabelAlpha, 0.7f))
                    }
                    is6Inch -> Pair("${i}\"", maxOf(sixInchLabelAlpha, inchLabelAlpha))
                    else -> Pair("${i}\"", inchLabelAlpha)
                }

                drawRulerLabel(
                    text = labelText,
                    x = tl.x - tickW - 2f,
                    y = sy,
                    alpha = labelAlpha,
                    textMeasurer = textMeasurer,
                    fontSize = fontSize,
                    isTopAxis = false
                )
            }
        }
    }
}

private fun DrawScope.drawRulerLabel(
    text: String,
    x: Float,
    y: Float,
    alpha: Float,
    textMeasurer: TextMeasurer,
    fontSize: androidx.compose.ui.unit.TextUnit,
    isTopAxis: Boolean
) {
    if (alpha <= 0.03f) return
    val style = TextStyle(
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
        color = Color.White.copy(alpha = alpha)
    )
    val m = textMeasurer.measure(text, style, maxLines = 1)
    val pW = m.size.width.toFloat() + 4f
    val pH = m.size.height.toFloat() + 2f
    val (bX, bY) = if (isTopAxis) {
        Pair(x - pW / 2f, y - pH)
    } else {
        Pair(x - pW, y - pH / 2f)
    }
    drawRoundRect(
        color = Color(0xD9212121).copy(alpha = (0.85f * alpha).coerceIn(0f, 0.85f)),
        topLeft = Offset(bX, bY),
        size = Size(pW, pH),
        cornerRadius = CornerRadius(3f)
    )
    drawText(m, topLeft = Offset(bX + 2f, bY + 1f))
}

private fun DrawScope.drawShieldIcon(center: Offset, radius: Float) {
    val path = Path().apply {
        val top = center.y - radius * 0.55f
        val bottom = center.y + radius * 0.60f
        val left = center.x - radius * 0.55f
        val right = center.x + radius * 0.55f
        moveTo(center.x, top)
        lineTo(right, top + radius * 0.15f)
        lineTo(right, center.y + radius * 0.15f)
        cubicTo(
            right, center.y + radius * 0.45f,
            center.x + radius * 0.2f, bottom - radius * 0.1f,
            center.x, bottom
        )
        cubicTo(
            center.x - radius * 0.2f, bottom - radius * 0.1f,
            left, center.y + radius * 0.45f,
            left, center.y + radius * 0.15f
        )
        lineTo(left, top + radius * 0.15f)
        close()
    }
    drawPath(path, color = Color.White)
    val checkPath = Path().apply {
        moveTo(center.x - radius * 0.28f, center.y)
        lineTo(center.x - radius * 0.08f, center.y + radius * 0.20f)
        lineTo(center.x + radius * 0.28f, center.y - radius * 0.18f)
    }
    drawPath(checkPath, color = Color(0xFF2E7D32), style = Stroke(width = 1.6f, cap = StrokeCap.Round))
}

private fun DrawScope.drawWarningIcon(center: Offset, radius: Float) {
    val barTop = center.y - radius * 0.50f
    val barBottom = center.y + radius * 0.12f
    val dotY = center.y + radius * 0.45f
    drawLine(
        color = Color.White,
        start = Offset(center.x, barTop),
        end = Offset(center.x, barBottom),
        strokeWidth = (radius * 0.26f).coerceAtLeast(1.5f),
        cap = StrokeCap.Round
    )
    drawCircle(
        color = Color.White,
        radius = (radius * 0.14f).coerceAtLeast(1.2f),
        center = Offset(center.x, dotY)
    )
}

private fun DrawScope.drawHoverTile(
    worldPos: Offset,
    isValid: Boolean,
    neighborStatus: NeighborStatus = NeighborStatus.NEUTRAL,
    camera: TopDownCamera,
    w: Float = 1f,
    h: Float = 1f
) {
    val tl = TopDownProjection.worldToScreen(worldPos.x, worldPos.y, camera)
    val sw = TopDownProjection.worldSizeToScreen(w, camera)
    val sh = TopDownProjection.worldSizeToScreen(h, camera)
    val cornerRadius = CornerRadius(6f * camera.zoom.coerceIn(0.5f, 2f))

    when (neighborStatus) {
        NeighborStatus.ANTAGONIST -> {
            // Bad neighbor glowing danger zone: vivid coral red with outer halo
            val glowColor = Color(0xFFFF1744)
            drawRoundRect(
                color = glowColor.copy(alpha = 0.18f),
                topLeft = Offset(tl.x - 6f, tl.y - 6f),
                size = Size(sw + 12f, sh + 12f),
                cornerRadius = CornerRadius(8f * camera.zoom.coerceIn(0.5f, 2f))
            )
            drawRoundRect(glowColor.copy(alpha = 0.38f), tl, Size(sw, sh), cornerRadius)
            drawRoundRect(glowColor, tl, Size(sw, sh), cornerRadius, style = Stroke(2.5f))
        }
        NeighborStatus.BENEFICIAL -> {
            // Beneficial companion glowing synergy zone: luminous emerald green with outer halo
            val glowColor = Color(0xFF00E676)
            drawRoundRect(
                color = glowColor.copy(alpha = 0.18f),
                topLeft = Offset(tl.x - 6f, tl.y - 6f),
                size = Size(sw + 12f, sh + 12f),
                cornerRadius = CornerRadius(8f * camera.zoom.coerceIn(0.5f, 2f))
            )
            drawRoundRect(glowColor.copy(alpha = 0.38f), tl, Size(sw, sh), cornerRadius)
            drawRoundRect(glowColor, tl, Size(sw, sh), cornerRadius, style = Stroke(2.5f))
        }
        NeighborStatus.NEUTRAL -> {
            val color = if (isValid) ValidHover.copy(alpha = 0.35f) else InvalidHover.copy(alpha = 0.35f)
            val borderColor = if (isValid) ValidHover else InvalidHover
            drawRoundRect(color, tl, Size(sw, sh), cornerRadius)
            drawRoundRect(borderColor, tl, Size(sw, sh), cornerRadius, style = Stroke(2f))
        }
    }
}

private fun DrawScope.drawDirectCrop(
    zone: CropZoneRenderData,
    allZones: List<CropZoneRenderData>,
    isSelected: Boolean,
    isResizeMode: Boolean = false,
    showCompanion: Boolean,
    showVariety: Boolean,
    showMeasurement: Boolean = true,
    showCropName: Boolean = true,
    varietyName: String?,
    camera: TopDownCamera,
    textMeasurer: TextMeasurer
) {
    val zW = if (zone.widthM > 0.05f) zone.widthM else realLifeCropDiameterM(zone.cropName)
    val zH = if (zone.heightM > 0.05f) zone.heightM else realLifeCropDiameterM(zone.cropName)
    val center = TopDownProjection.worldToScreen(zone.offsetX + zW / 2f, zone.offsetY + zH / 2f, camera)
    val zWPx = TopDownProjection.worldSizeToScreen(zW, camera)
    val zHPx = TopDownProjection.worldSizeToScreen(zH, camera)
    val maxRadiusPx = maxOf(zWPx, zHPx) / 2f

    // Analyze neighbor proximity (Good vs Bad neighbors)
    val analysis = FarmCompanionManager.analyzePlacedCrop(zone, allZones)

    // ── GLOWING ZONE AURA AROUND CROP BODY ─────────────────────────
    if (showCompanion) {
        if (analysis.status == NeighborStatus.ANTAGONIST) {
            // Bad neighbor glowing danger zone (Coral red aura)
            drawCircle(
                color = Color(0x33FF1744),
                radius = maxRadiusPx * 1.35f,
                center = center
            )
            drawCircle(
                color = Color(0x99FF1744),
                radius = maxRadiusPx * 1.15f,
                center = center,
                style = Stroke(width = 1.8f)
            )
        } else if (analysis.status == NeighborStatus.BENEFICIAL) {
            // Beneficial companion synergy glowing zone (Emerald green aura)
            drawCircle(
                color = Color(0x3300E676),
                radius = maxRadiusPx * 1.35f,
                center = center
            )
            drawCircle(
                color = Color(0x9900E676),
                radius = maxRadiusPx * 1.15f,
                center = center,
                style = Stroke(width = 1.8f)
            )
        }
    }

    // Selection highlight: highlights the exact physical zone box occupied on the grid
    if (isSelected) {
        val tlScreen = TopDownProjection.worldToScreen(zone.offsetX, zone.offsetY, camera)
        val zoneSizePx = Size(zWPx, zHPx)
        val cornerRad = (3f * camera.zoom).coerceIn(2f, 6f)
        drawRoundRect(
            color = Color(0x33FFB300),
            topLeft = tlScreen,
            size = zoneSizePx,
            cornerRadius = CornerRadius(cornerRad)
        )
        drawRoundRect(
            color = Color(0xFFFFB300),
            topLeft = tlScreen,
            size = zoneSizePx,
            cornerRadius = CornerRadius(cornerRad),
            style = Stroke(width = 2.0f)
        )

        // If in resize mode, render 4 corner resize handles
        if (isResizeMode) {
            val trScreen = TopDownProjection.worldToScreen(zone.offsetX + zW, zone.offsetY, camera)
            val blScreen = TopDownProjection.worldToScreen(zone.offsetX, zone.offsetY + zH, camera)
            val brScreen = TopDownProjection.worldToScreen(zone.offsetX + zW, zone.offsetY + zH, camera)
            val handleRadius = (7.5f * camera.zoom).coerceIn(6f, 13f)
            val corners = listOf(tlScreen, trScreen, blScreen, brScreen)
            for (corner in corners) {
                drawCircle(color = Color(0xFFCCFF90), radius = handleRadius, center = corner)
                drawCircle(color = Color(0xFF2E7D32), radius = handleRadius, center = corner, style = Stroke(2.2f))
            }
        }
    }

    // ── MULTI-CROP GENERATION WITHIN 1 UNIFIED ZONE ───────────────
    // When a zone is resized larger, it populates multiple individual crops
    // according to the vegetable's natural spacing, while remaining 1 single selectable zone.
    val plantSpacing = cropSinglePlantSpacingM(zone.cropName).coerceAtLeast(0.06f)
    val cols = maxOf(1, kotlin.math.round(zW / plantSpacing).toInt())
    val rows = maxOf(1, kotlin.math.round(zH / plantSpacing).toInt())
    val totalPlants = cols * rows
    val cellW = zW / cols
    val cellH = zH / rows
    val cellWPx = TopDownProjection.worldSizeToScreen(cellW, camera)
    val cellHPx = TopDownProjection.worldSizeToScreen(cellH, camera)
    val plantIconSizePx = (minOf(cellWPx, cellHPx) * 0.88f).coerceAtLeast(6f)

    for (r in 0 until rows) {
        for (c in 0 until cols) {
            val px = zone.offsetX + (c + 0.5f) * cellW
            val py = zone.offsetY + (r + 0.5f) * cellH
            val plantCenterPx = TopDownProjection.worldToScreen(px, py, camera)
            CropSvgRenderer.drawCropSvg(
                drawScope = this,
                cropName = zone.cropName ?: "",
                center = plantCenterPx,
                sizePx = plantIconSizePx
            )
        }
    }

    // ── CROP BODY BADGE (Shield Icon if Good, Warning Icon if Bad) ─
    if (showCompanion) {
        val badgeR = (maxRadiusPx * 0.35f).coerceIn(8f, 16f)
        val badgeCenter = Offset(center.x + (zWPx / 2f) * 0.72f, center.y - (zHPx / 2f) * 0.72f)

        if (analysis.status == NeighborStatus.ANTAGONIST) {
            // Warning Badge (Red circle with white border and warning icon)
            drawCircle(Color(0xFFD32F2F), radius = badgeR, center = badgeCenter)
            drawCircle(Color.White, radius = badgeR, center = badgeCenter, style = Stroke(1.5f))
            drawWarningIcon(badgeCenter, badgeR)
        } else if (analysis.status == NeighborStatus.BENEFICIAL) {
            // Shield Badge (Green circle with white border and shield icon)
            drawCircle(Color(0xFF2E7D32), radius = badgeR, center = badgeCenter)
            drawCircle(Color.White, radius = badgeR, center = badgeCenter, style = Stroke(1.5f))
            drawShieldIcon(badgeCenter, badgeR)
        }
    }

    // Crop label displaying name, plant count, and physical zone size
    val shouldShowLabel = (showCropName || showMeasurement) && (camera.zoom >= 1.2f || isSelected)
    if (shouldShowLabel) {
        val cName = if (showCropName) (zone.cropName ?: "Crop") else null
        val vName = if (showCropName && showVariety && !varietyName.isNullOrBlank()) varietyName else null
        val namePart = when {
            cName != null && vName != null -> "$cName • $vName"
            cName != null -> cName
            else -> null
        }

        val countPart = if (totalPlants > 1) "$totalPlants plants" else null

        val inchesW = (zW / INCH_IN_METERS).roundToInt()
        val inchesH = (zH / INCH_IN_METERS).roundToInt()
        val measurementPart = if (showMeasurement) {
            if (inchesW == inchesH) {
                when {
                    inchesW >= 36 && inchesW % 36 == 0 -> "${inchesW / 36}yd (${inchesW}\")"
                    inchesW >= 12 && inchesW % 12 == 0 -> "${inchesW / 12}ft (${inchesW}\")"
                    else -> "${inchesW}\""
                }
            } else {
                "${inchesW}\" × ${inchesH}\""
            }
        } else null

        val labelText = when {
            namePart != null && countPart != null && measurementPart != null ->
                "$namePart ($countPart • $measurementPart)"
            namePart != null && countPart != null ->
                "$namePart ($countPart)"
            namePart != null && measurementPart != null ->
                "$namePart ($measurementPart)"
            namePart != null ->
                namePart
            countPart != null && measurementPart != null ->
                "$countPart ($measurementPart)"
            measurementPart != null ->
                measurementPart
            countPart != null ->
                countPart
            else -> ""
        }

        if (labelText.isNotBlank()) {
            val zFontSize = (8f * camera.zoom).coerceIn(6.5f, 11f)
            val zMeasured = textMeasurer.measure(
                text = labelText,
                style = TextStyle(fontSize = zFontSize.sp, fontWeight = FontWeight.SemiBold, color = Color.White),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val pW = zMeasured.size.width.toFloat() + 6f
            val pH = zMeasured.size.height.toFloat() + 2f
            val pX = center.x - pW / 2f
            val pY = center.y + (zHPx / 2f) + 4f
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.75f),
                topLeft = Offset(pX, pY),
                size = Size(pW, pH),
                cornerRadius = CornerRadius(2f)
            )
            drawText(zMeasured, topLeft = Offset(pX + 3f, pY + 1f))
        }
    }
}

private fun DrawScope.drawPlotBed(
    plot: PlotRenderData,
    cropZones: List<CropZoneRenderData>,
    selectedZoneId: String?,
    isSelected: Boolean,
    isDraggingCrop: Boolean = false,
    dragCropName: String = "",
    showCompanion: Boolean = false,
    showVariety: Boolean = false,
    camera: TopDownCamera,
    textMeasurer: TextMeasurer
) {
    val tl = TopDownProjection.worldToScreen(plot.posX, plot.posY, camera)
    val bedW = TopDownProjection.worldSizeToScreen(plot.widthM, camera)
    val bedH = TopDownProjection.worldSizeToScreen(plot.heightM, camera)
    val bedSize = Size(bedW, bedH)
    val cornerR = CornerRadius(4f * camera.zoom.coerceIn(0.5f, 2f))

    val isBed = plot.cropName?.startsWith("Bed", ignoreCase = true) == true ||
            plot.cropId?.startsWith("bed", ignoreCase = true) == true ||
            plot.plotLabel.startsWith("Bed", ignoreCase = true) ||
            plot.cropName.isNullOrBlank() ||
            plot.cropId.isNullOrBlank() ||
            plot.cropName.equals("Bed", ignoreCase = true) ||
            plot.cropId.equals("bed", ignoreCase = true)
    val bedCrops = cropZones.filter { it.plotId == plot.id && !it.cropName.isNullOrBlank() && !it.cropName.equals("Bed", ignoreCase = true) }

    if (isBed) {
        if (bedCrops.isEmpty()) {
            // Plain brown bed — empty bed ready for planting
            drawRoundRect(BedFill, tl, bedSize, cornerR)
            drawRoundRect(BedBorder, tl, bedSize, cornerR, style = Stroke(1.5f * camera.zoom.coerceIn(0.5f, 2f)))
            // Subtle horizontal furrow lines
            val lineCount = (plot.heightM * 2).toInt().coerceIn(1, 12)
            val lineSpacing = bedH / (lineCount + 1)
            for (i in 1..lineCount) {
                val y = tl.y + i * lineSpacing
                drawLine(
                    BedBorder.copy(alpha = 0.3f),
                    Offset(tl.x + 4f, y),
                    Offset(tl.x + bedW - 4f, y),
                    strokeWidth = 0.8f
                )
            }

            // Visual Guidance Cue: "+ Empty Bed" or "+ Drop Crop"
            if (bedW > 35f && bedH > 20f) {
                val cueText = if (isDraggingCrop && dragCropName.isNotBlank() && !dragCropName.contains("Bed", ignoreCase = true)) {
                    "+ Drop $dragCropName"
                } else {
                    "+ Empty Bed"
                }
                val cueStyle = TextStyle(
                    fontSize = (9f * camera.zoom).coerceIn(6f, 11f).sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFA5D6A7)
                )
                val cueMeasured = textMeasurer.measure(cueText, cueStyle, maxLines = 1)
                val cueX = tl.x + (bedW - cueMeasured.size.width) / 2f
                val cueY = tl.y + (bedH - cueMeasured.size.height) / 2f
                drawRoundRect(
                    Color(0xD91E281C),
                    Offset(cueX - 4f, cueY - 2f),
                    Size(cueMeasured.size.width.toFloat() + 8f, cueMeasured.size.height.toFloat() + 4f),
                    CornerRadius(4f)
                )
                drawText(cueMeasured, topLeft = Offset(cueX, cueY))
            }

            // Welcoming dashed green highlight when dragging a crop over the canvas
            if (isDraggingCrop && dragCropName.isNotBlank() && !dragCropName.contains("Bed", ignoreCase = true)) {
                drawRoundRect(
                    Color(0xFF81C784),
                    Offset(tl.x - 1f, tl.y - 1f),
                    Size(bedW + 2f, bedH + 2f),
                    cornerR,
                    style = Stroke(
                        width = (1.8f * camera.zoom).coerceIn(1.5f, 3f),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)
                    )
                )
            }
        } else {
            // Planted bed — dark brown base with crop zones
            drawRoundRect(PlantedBedFill, tl, bedSize, cornerR)
            drawRoundRect(BedBorder, tl, bedSize, cornerR, style = Stroke(1.5f * camera.zoom.coerceIn(0.5f, 2f)))

            // Live Companion Planting Visual Guidance during drag
            if (isDraggingCrop && dragCropName.isNotBlank() && !dragCropName.contains("Bed", ignoreCase = true)) {
                val companionStatuses = bedCrops.mapNotNull { zone ->
                    zone.cropName?.let { cName ->
                        try {
                            CompanionDataProvider.getRelationship(dragCropName, cName)?.relationship
                        } catch (_: Exception) { null }
                    }
                }
                val hasBeneficial = companionStatuses.any { it == CompanionRelation.BENEFICIAL }
                val hasAntagonist = companionStatuses.any { it == CompanionRelation.ANTAGONIST }

                if (hasBeneficial) {
                    // Glowing emerald companion border
                    drawRoundRect(
                        Color(0xFF4CAF50),
                        Offset(tl.x - 2f, tl.y - 2f),
                        Size(bedW + 4f, bedH + 4f),
                        cornerR,
                        style = Stroke(width = (2.5f * camera.zoom).coerceIn(2f, 4f))
                    )
                    if (bedW > 40f) {
                        val boostStyle = TextStyle(
                            fontSize = (8.5f * camera.zoom).coerceIn(6f, 10f).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE8F5E9)
                        )
                        val boostMeasured = textMeasurer.measure("Boost!", boostStyle, maxLines = 1)
                        val bX = tl.x + bedW - boostMeasured.size.width - 6f
                        val bY = tl.y + 3f
                        drawRoundRect(
                            Color(0xEE2E7D32),
                            Offset(bX - 3f, bY - 1f),
                            Size(boostMeasured.size.width.toFloat() + 6f, boostMeasured.size.height.toFloat() + 2f),
                            CornerRadius(3f)
                        )
                        drawText(boostMeasured, topLeft = Offset(bX, bY))
                    }
                } else if (hasAntagonist) {
                    // Amber warning border for incompatible companion
                    drawRoundRect(
                        Color(0xFFFFB300),
                        Offset(tl.x - 2f, tl.y - 2f),
                        Size(bedW + 4f, bedH + 4f),
                        cornerR,
                        style = Stroke(
                            width = (2f * camera.zoom).coerceIn(1.5f, 3f),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)
                        )
                    )
                    if (bedW > 40f) {
                        val warnStyle = TextStyle(
                            fontSize = (8.5f * camera.zoom).coerceIn(6f, 10f).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFF8E1)
                        )
                        val warnMeasured = textMeasurer.measure("Conflict!", warnStyle, maxLines = 1)
                        val wX = tl.x + bedW - warnMeasured.size.width - 6f
                        val wY = tl.y + 3f
                        drawRoundRect(
                            Color(0xEEFF8F00),
                            Offset(wX - 3f, wY - 1f),
                            Size(warnMeasured.size.width.toFloat() + 6f, warnMeasured.size.height.toFloat() + 2f),
                            CornerRadius(3f)
                        )
                        drawText(warnMeasured, topLeft = Offset(wX, wY))
                    }
                }
            }

            val iconRadius = (4f * camera.zoom).coerceIn(2.5f, 10f)
            bedCrops.forEach { zone ->
                val cropCol = cropColor(zone.cropName ?: "")
                val zoneWorldX = plot.posX + zone.offsetX
                val zoneWorldY = plot.posY + zone.offsetY
                val ztl = TopDownProjection.worldToScreen(zoneWorldX, zoneWorldY, camera)
                val zW = TopDownProjection.worldSizeToScreen(zone.widthM, camera)
                val zH = TopDownProjection.worldSizeToScreen(zone.heightM, camera)
                val isZoneSelected = (zone.id == selectedZoneId)

                // Distinct crop zone container box inside the bed
                val zoneCornerR = CornerRadius(3f * camera.zoom.coerceIn(0.5f, 1.5f))
                drawRoundRect(
                    color = cropCol.copy(alpha = if (isZoneSelected) 0.35f else 0.20f),
                    topLeft = ztl,
                    size = Size(zW, zH),
                    cornerRadius = zoneCornerR
                )
                drawRoundRect(
                    color = if (isZoneSelected) Color(0xFFFFB300) else cropCol.copy(alpha = 0.55f),
                    topLeft = ztl,
                    size = Size(zW, zH),
                    cornerRadius = zoneCornerR,
                    style = Stroke(
                        width = (if (isZoneSelected) 1.8f else 1f) * camera.zoom.coerceIn(0.5f, 1.5f),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 3f), 0f)
                    )
                )

                // Crop zone label if zoomed in enough
                if (zW > 35f && zH > 16f) {
                    val cName = zone.cropName ?: "Crop"
                    val vName = plot.cropVariety
                    val labelText = if (showVariety && !vName.isNullOrBlank()) "$cName • $vName" else cName
                    val zFontSize = (8.5f * camera.zoom).coerceIn(5.5f, 10.5f)
                    val zMeasured = textMeasurer.measure(
                        text = labelText,
                        style = TextStyle(fontSize = zFontSize.sp, fontWeight = FontWeight.Bold, color = Color.White),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    drawRoundRect(
                        color = Color.Black.copy(alpha = 0.65f),
                        topLeft = Offset(ztl.x + 2f, ztl.y + 2f),
                        size = Size(zMeasured.size.width.toFloat() + 6f, zMeasured.size.height.toFloat() + 2f),
                        cornerRadius = CornerRadius(2f)
                    )
                    drawText(zMeasured, topLeft = Offset(ztl.x + 5f, ztl.y + 3f))

                    // Companion indicator if enabled
                    if (showCompanion && !zone.cropName.isNullOrBlank()) {
                        val compStyle = TextStyle(fontSize = (7f * camera.zoom).coerceIn(5f, 9f).sp, fontWeight = FontWeight.Bold, color = Color(0xFFC8E6C9))
                        val compText = textMeasurer.measure("🌿 Companion", compStyle, maxLines = 1)
                        val badgeX = ztl.x + zW - compText.size.width - 6f
                        if (badgeX > ztl.x + zMeasured.size.width + 10f) {
                            drawRoundRect(
                                color = Color(0xDD1B5E20),
                                topLeft = Offset(badgeX - 2f, ztl.y + 2f),
                                size = Size(compText.size.width + 4f, compText.size.height + 2f),
                                cornerRadius = CornerRadius(2f)
                            )
                            drawText(compText, topLeft = Offset(badgeX, ztl.y + 3f))
                        }
                    }
                }

                // Render SVG Crop inside the crop zone
                val unitCols = (zone.widthM / 0.8f).toInt().coerceAtLeast(1)
                val unitRows = (zone.heightM / 0.8f).toInt().coerceAtLeast(1)
                if (unitCols <= 1 && unitRows <= 1) {
                    val svgSize = minOf(zW, zH) * 0.72f
                    CropSvgRenderer.drawCropSvg(
                        drawScope = this,
                        cropName = zone.cropName ?: "",
                        center = Offset(ztl.x + zW / 2f, ztl.y + zH / 2f),
                        sizePx = svgSize
                    )
                } else {
                    val cellW = zW / unitCols
                    val cellH = zH / unitRows
                    val svgSize = minOf(cellW, cellH) * 0.72f
                    for (r in 0 until unitRows) {
                        for (c in 0 until unitCols) {
                            val itemCx = ztl.x + (c + 0.5f) * cellW
                            val itemCy = ztl.y + (r + 0.5f) * cellH
                            if (itemCx > tl.x + 2 && itemCx < tl.x + bedW - 2 &&
                                itemCy > tl.y + 2 && itemCy < tl.y + bedH - 2) {
                                CropSvgRenderer.drawCropSvg(
                                    drawScope = this,
                                    cropName = zone.cropName ?: "",
                                    center = Offset(itemCx, itemCy),
                                    sizePx = svgSize
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
        // Non-bed plot (legacy fallback only if plot has a real crop assigned)
        if (!plot.cropName.isNullOrBlank() && !plot.cropName.equals("Bed", ignoreCase = true)) {
            drawRoundRect(PlantedBedFill, tl, bedSize, cornerR)
            drawRoundRect(BedBorder, tl, bedSize, cornerR, style = Stroke(1.5f * camera.zoom.coerceIn(0.5f, 2f)))
            val unitCols = (plot.widthM / 1.0f).toInt().coerceAtLeast(1)
            val unitRows = (plot.heightM / 1.0f).toInt().coerceAtLeast(1)
            val cellW = bedW / unitCols
            val cellH = bedH / unitRows
            val svgSize = minOf(cellW, cellH) * 0.72f
            for (r in 0 until unitRows) {
                for (c in 0 until unitCols) {
                    val itemCx = tl.x + (c + 0.5f) * cellW
                    val itemCy = tl.y + (r + 0.5f) * cellH
                    CropSvgRenderer.drawCropSvg(
                        drawScope = this,
                        cropName = plot.cropName ?: "",
                        center = Offset(itemCx, itemCy),
                        sizePx = svgSize
                    )
                }
            }
        } else {
            drawRoundRect(BedFill, tl, bedSize, cornerR)
            drawRoundRect(BedBorder, tl, bedSize, cornerR, style = Stroke(1.5f * camera.zoom.coerceIn(0.5f, 2f)))
        }
    }

    // ── Label ──────────────────────────────────────────────────────────
    val labelText = when {
        isBed && bedCrops.isNotEmpty() -> {
            val cropsSummary = bedCrops.mapNotNull { it.cropName }.distinct().joinToString(", ")
            "${plot.plotLabel.ifBlank { "Bed" }} • $cropsSummary"
        }
        isBed -> plot.plotLabel.ifBlank { "Bed" }
        else -> plot.cropName ?: plot.plotLabel
    }
    if (labelText.isNotBlank() && bedW > 30f && bedH > 18f) {
        val fontSize = (11f * camera.zoom).coerceIn(7f, 16f)
        val textStyle = TextStyle(
            fontSize = fontSize.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        val measured = textMeasurer.measure(
            text = labelText,
            style = textStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            constraints = androidx.compose.ui.unit.Constraints(maxWidth = bedW.toInt().coerceAtLeast(1))
        )
        // Draw label background pill at bottom of bed
        val labelW = measured.size.width.toFloat() + 8f
        val labelH = measured.size.height.toFloat() + 4f
        val labelX = tl.x + (bedW - labelW) / 2f
        val labelY = tl.y + bedH - labelH - 3f

        if (labelY > tl.y + 2f) {
            drawRoundRect(
                LabelBg.copy(alpha = 0.75f),
                Offset(labelX, labelY),
                Size(labelW, labelH),
                CornerRadius(3f)
            )
            drawText(
                measured,
                topLeft = Offset(labelX + 4f, labelY + 2f)
            )
        }
    }

    // Dimensions badge (when selected)
    if (isSelected && bedW > 20f) {
        val dimText = "${plot.widthM.toInt()}m × ${plot.heightM.toInt()}m"
        val dimStyle = TextStyle(
            fontSize = (9f * camera.zoom).coerceIn(6f, 12f).sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFB0BEC5)
        )
        val dimMeasured = textMeasurer.measure(dimText, dimStyle, maxLines = 1)
        val dimX = tl.x + (bedW - dimMeasured.size.width) / 2f
        val dimY = tl.y - dimMeasured.size.height - 4f
        if (dimY > 0f) {
            drawRoundRect(
                Color.Black.copy(alpha = 0.55f),
                Offset(dimX - 4f, dimY - 2f),
                Size(dimMeasured.size.width.toFloat() + 8f, dimMeasured.size.height.toFloat() + 4f),
                CornerRadius(3f)
            )
            drawText(dimMeasured, topLeft = Offset(dimX, dimY))
        }
    }
}

private fun DrawScope.drawSelectionBorder(plot: PlotRenderData, camera: TopDownCamera) {
    val tl = TopDownProjection.worldToScreen(plot.posX, plot.posY, camera)
    val bedW = TopDownProjection.worldSizeToScreen(plot.widthM, camera)
    val bedH = TopDownProjection.worldSizeToScreen(plot.heightM, camera)
    val strokeW = (2f * camera.zoom).coerceIn(1.5f, 3f)

    drawRoundRect(
        SelectionBlue,
        Offset(tl.x - 2f, tl.y - 2f),
        Size(bedW + 4f, bedH + 4f),
        CornerRadius(5f),
        style = Stroke(
            width = strokeW,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 4f), 0f)
        )
    )
}

private fun DrawScope.drawResizeHandles(plot: PlotRenderData, camera: TopDownCamera) {
    val handles = plotHandlePositions(plot)
    val handleRadius = (5f * camera.zoom).coerceIn(4f, 8f)
    val strokeW = (1.5f * camera.zoom).coerceIn(1f, 2.5f)

    for ((_, worldPos) in handles) {
        val screenPos = TopDownProjection.worldToScreen(worldPos.x, worldPos.y, camera)
        drawCircle(HandleFill, handleRadius, screenPos)
        drawCircle(HandleStroke, handleRadius, screenPos, style = Stroke(strokeW))
    }
}

private fun DrawScope.drawZoneSelectionBorder(
    zone: CropZoneRenderData,
    camera: TopDownCamera
) {
    val zx = zone.offsetX
    val zy = zone.offsetY
    val tl = TopDownProjection.worldToScreen(zx, zy, camera)
    val zW = TopDownProjection.worldSizeToScreen(zone.widthM, camera)
    val zH = TopDownProjection.worldSizeToScreen(zone.heightM, camera)
    val strokeW = (2f * camera.zoom).coerceIn(1.5f, 3f)

    drawRoundRect(
        Color(0xFFFFB300),
        Offset(tl.x - 2f, tl.y - 2f),
        Size(zW + 4f, zH + 4f),
        CornerRadius(4f),
        style = Stroke(
            width = strokeW,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)
        )
    )
}

private fun DrawScope.drawZoneResizeHandles(
    zone: CropZoneRenderData,
    camera: TopDownCamera
) {
    val handles = zoneHandlePositions(zone)
    val handleRadius = (5f * camera.zoom).coerceIn(4f, 8f)
    val strokeW = (1.5f * camera.zoom).coerceIn(1f, 2.5f)

    for ((_, worldPos) in handles) {
        val screenPos = TopDownProjection.worldToScreen(worldPos.x, worldPos.y, camera)
        drawCircle(Color(0xFFFFE082), handleRadius, screenPos)
        drawCircle(Color(0xFFFF8F00), handleRadius, screenPos, style = Stroke(strokeW))
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Hit testing & helpers
// ═══════════════════════════════════════════════════════════════════════════════

private fun plotHandlePositions(plot: PlotRenderData): Map<HandleType, Offset> {
    val x = plot.posX; val y = plot.posY; val w = plot.widthM; val h = plot.heightM
    return mapOf(
        HandleType.CORNER_TL  to Offset(x, y),
        HandleType.MID_TOP    to Offset(x + w / 2f, y),
        HandleType.CORNER_TR  to Offset(x + w, y),
        HandleType.MID_LEFT   to Offset(x, y + h / 2f),
        HandleType.MID_RIGHT  to Offset(x + w, y + h / 2f),
        HandleType.CORNER_BL  to Offset(x, y + h),
        HandleType.MID_BOTTOM to Offset(x + w / 2f, y + h),
        HandleType.CORNER_BR  to Offset(x + w, y + h)
    )
}

private fun hitTestHandle(worldPos: Offset, plot: PlotRenderData, hitRadius: Float): HandleType? {
    val handles = plotHandlePositions(plot)
    return handles.entries.firstOrNull { (_, pos) ->
        dist(worldPos, pos) < hitRadius
    }?.key
}

private fun zoneHandlePositions(zone: CropZoneRenderData): Map<HandleType, Offset> {
    val x = zone.offsetX
    val y = zone.offsetY
    val w = if (zone.widthM > 0.05f) zone.widthM else realLifeCropDiameterM(zone.cropName)
    val h = if (zone.heightM > 0.05f) zone.heightM else realLifeCropDiameterM(zone.cropName)
    return mapOf(
        HandleType.CORNER_TL  to Offset(x, y),
        HandleType.MID_TOP    to Offset(x + w / 2f, y),
        HandleType.CORNER_TR  to Offset(x + w, y),
        HandleType.MID_LEFT   to Offset(x, y + h / 2f),
        HandleType.MID_RIGHT  to Offset(x + w, y + h / 2f),
        HandleType.CORNER_BL  to Offset(x, y + h),
        HandleType.MID_BOTTOM to Offset(x + w / 2f, y + h),
        HandleType.CORNER_BR  to Offset(x + w, y + h)
    )
}

private fun hitTestZoneHandle(
    worldPos: Offset,
    zone: CropZoneRenderData,
    hitRadius: Float
): HandleType? {
    val handles = zoneHandlePositions(zone)
    return handles.entries.firstOrNull { (_, pos) ->
        dist(worldPos, pos) < hitRadius
    }?.key
}

private fun hitTestCropZone(
    worldPos: Offset,
    cropZones: List<CropZoneRenderData>,
    activePlotId: String? = null
): CropZoneRenderData? {
    val gardenZones = if (activePlotId != null) {
        cropZones.filter { it.plotId == activePlotId }
    } else cropZones
    return gardenZones.lastOrNull { zone ->
        if (zone.cropName.isNullOrBlank() || zone.cropName.equals("Bed", ignoreCase = true)) return@lastOrNull false
        val zW = if (zone.widthM > 0.05f) zone.widthM else realLifeCropDiameterM(zone.cropName)
        val zH = if (zone.heightM > 0.05f) zone.heightM else realLifeCropDiameterM(zone.cropName)
        val inBoundingBox = worldPos.x >= (zone.offsetX - 0.06f) &&
                            worldPos.x <= (zone.offsetX + zW + 0.06f) &&
                            worldPos.y >= (zone.offsetY - 0.06f) &&
                            worldPos.y <= (zone.offsetY + zH + 0.06f)
        val cx = zone.offsetX + zW / 2f
        val cy = zone.offsetY + zH / 2f
        val dx = worldPos.x - cx
        val dy = worldPos.y - cy
        val dist = sqrt(dx * dx + dy * dy)
        val hitRadius = maxOf(maxOf(zW, zH) / 2f, 0.22f)
        inBoundingBox || (dist <= hitRadius)
    }
}

private fun hitTestPlot(worldPos: Offset, plots: List<PlotRenderData>): PlotRenderData? {
    return null
}

private fun handleTap(
    worldPos: Offset,
    hitCrop: CropZoneRenderData?,
    hitPlot: PlotRenderData?,
    activeTool: EditTool,
    activeCropName: String,
    activeCropId: String,
    activeVariety: String?,
    activeCropDiameterM: Float? = null,
    isSnapEnabled: Boolean,
    plots: List<PlotRenderData>,
    currentSelectedPlotId: String?,
    editViewModel: EditViewModel,
    onCropPlaced: () -> Unit,
    onCropTapped: (CropZoneRenderData) -> Unit,
    onTapOutsideCrop: () -> Unit
) {
    if (activeCropName.isNotEmpty() && !activeCropName.equals("Bed", ignoreCase = true)) {
        var tx = worldPos.x; var ty = worldPos.y
        if (isSnapEnabled) {
            val snapped = TopDownProjection.snapToGrid(worldPos)
            tx = snapped.x; ty = snapped.y
        }
        val targetGarden = plots.firstOrNull { it.id == currentSelectedPlotId } ?: plots.firstOrNull()
        if (targetGarden != null) {
            val success = editViewModel.plantCropInBed(
                bedPlotId = targetGarden.id,
                newCropName = activeCropName,
                newCropId = activeCropId,
                atWorldX = tx,
                atWorldY = ty,
                variety = activeVariety,
                initialDiameterM = activeCropDiameterM
            )
            if (success) {
                onCropPlaced()
            }
        }
    } else if (hitCrop != null) {
        // User tapped a crop on the grid
        editViewModel.selectCropZone(hitCrop.id)
        onCropTapped(hitCrop)
    } else {
        // Tapped outside any crop -> deselect all
        editViewModel.selectCropZone(null)
        onTapOutsideCrop()
    }
}

private fun DrawScope.visibleWorldBounds(camera: TopDownCamera): VisibleBounds {
    val tl = TopDownProjection.screenToWorld(0f, 0f, camera)
    val br = TopDownProjection.screenToWorld(size.width, size.height, camera)
    return VisibleBounds(
        left = minOf(tl.x, br.x) - 2f,
        top = minOf(tl.y, br.y) - 2f,
        right = maxOf(tl.x, br.x) + 2f,
        bottom = maxOf(tl.y, br.y) + 2f
    )
}

private data class VisibleBounds(val left: Float, val top: Float, val right: Float, val bottom: Float)

private fun dist(a: Offset, b: Offset): Float =
    sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y))


/** Maps crop names to category-appropriate colors for the 2D crop dots. */
internal fun cropColor(cropName: String): Color = when (cropName.lowercase()) {
    "carrot", "karot" -> Color(0xFFFF8A65)
    "tomato", "kamatis" -> Color(0xFFEF5350)
    "eggplant", "talong" -> Color(0xFF7E57C2)
    "pechay" -> Color(0xFF66BB6A)
    "corn", "mais" -> Color(0xFFFFCA28)
    "onion", "sibuyas" -> Color(0xFFFFAB91)
    "cabbage", "repolyo" -> Color(0xFF81C784)
    "lettuce", "litsugas" -> Color(0xFFA5D6A7)
    "ampalaya" -> Color(0xFF43A047)
    "okra" -> Color(0xFF66BB6A)
    "kangkong" -> Color(0xFF26A69A)
    "squash", "pumpkin", "kalabasa" -> Color(0xFFFFB74D)
    "sili", "chili" -> Color(0xFFE53935)
    "cucumber", "pipino" -> Color(0xFF26A69A)
    "sitaw", "stringbeans" -> Color(0xFF66BB6A)
    "bed" -> Color(0xFF8D6E63)
    else -> Color(0xFF81C784)
}
