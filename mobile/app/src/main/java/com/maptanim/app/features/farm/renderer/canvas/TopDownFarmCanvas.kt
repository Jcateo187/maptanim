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
import com.maptanim.app.domain.model.EditTool
import com.maptanim.app.features.farm.renderer.gesture.HandleType
import com.maptanim.app.features.farm.renderer.model.CropZoneRenderData
import com.maptanim.app.features.farm.renderer.model.PlotRenderData
import com.maptanim.app.features.farm.viewmodel.EditUiState
import com.maptanim.app.features.farm.viewmodel.EditViewModel
import kotlin.math.abs
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

    fun snapToGrid(worldPos: Offset, snap: Float = 0.5f): Offset =
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
     * Clamps camera pan and zoom so the 45m x 45m farm box NEVER goes outside the visible screen.
     * Limits the height within the available viewport and guarantees compatibility with any device.
     */
    fun clampCamera(
        camera: TopDownCamera,
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
        if (screenW <= 0f || screenH <= 0f) return camera

        val availW = (screenW - leftPadding - rightPadding).coerceAtLeast(100f)
        val availH = (screenH - topPadding - bottomPadding).coerceAtLeast(100f)
        val fitZoom = minOf(availW / (farmW * PPM), availH / (farmH * PPM)).coerceIn(0.15f, 3.5f)
        val clampedZoom = camera.zoom.coerceIn(fitZoom, 3.5f)

        val farmScreenW = farmW * PPM * clampedZoom
        val farmScreenH = farmH * PPM * clampedZoom

        val clampedPanX = if (farmScreenW <= availW) {
            leftPadding + (availW - farmScreenW) / 2f
        } else {
            camera.panX.coerceIn(screenW - rightPadding - farmScreenW, leftPadding)
        }

        val clampedPanY = if (farmScreenH <= availH) {
            topPadding + (availH - farmScreenH) / 2f
        } else {
            camera.panY.coerceIn((screenH - bottomPadding) - farmScreenH, topPadding)
        }

        return TopDownCamera(panX = clampedPanX, panY = clampedPanY, zoom = clampedZoom)
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Design Tokens
// ═══════════════════════════════════════════════════════════════════════════════

internal val CanvasBg         = Color(0xFF1B221A) // Dark agricultural soil background
internal val GridDotColor     = Color(0xFF384535) // Subtle gray-green dot grid
internal val FarmBorderColor  = Color(0xFF4CAF50) // Crisp green farm boundary
internal val FarmInnerBg      = Color(0xFF1B221A) // Seamless farm surface inside boundary
internal val BedFill          = Color(0xFF8D6E63) // Brown garden bed
internal val BedBorder        = Color(0xFF5D4037)
internal val PlantedBedFill   = Color(0xFF6D4C41)
internal val SelectionBlue    = Color(0xFF2979FF)
internal val HandleFill       = Color.White
internal val HandleStroke     = Color(0xFF1565C0)
internal val ValidHover       = Color(0xFF4CAF50)
internal val InvalidHover     = Color(0xFFF44336)
internal val LabelBg          = Color(0xFF37474F)

// ═══════════════════════════════════════════════════════════════════════════════
// Gesture state tracking
// ═══════════════════════════════════════════════════════════════════════════════

private enum class DragMode { IDLE, PAN, MOVE_PLOT, MOVE_CROP, RESIZE_HANDLE }

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
    hoverWorldPos: Offset? = null,
    isValidPlacement: Boolean = true,
    isDraggingCrop: Boolean = false,
    showBoundary: Boolean = false,
    initialZoom: Float = 0.5f,
    onCameraChanged: (TopDownCamera) -> Unit = {}
) {
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
    var activeDragPlotId by remember { mutableStateOf<String?>(null) }
    var lastPointerCount by remember { mutableIntStateOf(0) }
    var lastPinchDist by remember { mutableFloatStateOf(0f) }

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val topPaddingPx = with(density) { 56.dp.toPx() }
    val bottomPaddingPx = with(density) { if (isLandscape) 20.dp.toPx() else 60.dp.toPx() }
    val leftPaddingPx = with(density) { if (isLandscape) 20.dp.toPx() else 16.dp.toPx() }
    val rightPaddingPx = with(density) { if (isLandscape) 20.dp.toPx() else 16.dp.toPx() }

    // Entrance: Start at initialZoom (default 0.5f = zoom 5) centered on beds with NO visible canvas edge
    LaunchedEffect(canvasSize, isLandscape) {
        if (canvasSize.width > 0 && canvasSize.height > 0) {
            val targetZoom = initialZoom.coerceIn(0.2f, 3.5f)
            val centerX = if (uiState.plots.isNotEmpty()) {
                (uiState.plots.minOf { it.posX } + uiState.plots.maxOf { it.posX + it.widthM }) / 2f
            } else 22.5f
            val centerY = if (uiState.plots.isNotEmpty()) {
                (uiState.plots.minOf { it.posY } + uiState.plots.maxOf { it.posY + it.heightM }) / 2f
            } else 22.5f
            val panX = canvasSize.width / 2f - centerX * TopDownProjection.PPM * targetZoom
            val panY = canvasSize.height / 2f - centerY * TopDownProjection.PPM * targetZoom
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

                                    // Hit test: handle (crop or bed) → selected crop → selected bed → pan
                                    val selectedPlot = currentPlots.firstOrNull { it.id == currentSelectedPlotId }
                                    val selectedZone = currentCropZones.firstOrNull { it.id == currentSelectedZoneId }
                                    val hitHandle = when {
                                        currentIsResizeMode && selectedZone != null && selectedPlot != null ->
                                            hitTestZoneHandle(world, selectedZone, selectedPlot, handleHitRadius)
                                        currentIsResizeMode && selectedPlot != null && selectedZone == null ->
                                            hitTestHandle(world, selectedPlot, handleHitRadius)
                                        else -> null
                                    }

                                    val hitCrop = hitTestCropZone(world, currentCropZones, currentPlots)
                                    val hitPlot = hitTestPlot(world, currentPlots)

                                    when {
                                        hitHandle != null -> {
                                            dragMode = DragMode.RESIZE_HANDLE
                                            activeHandle = hitHandle
                                            if (selectedZone != null) {
                                                editViewModel.onZoneHandleDragStart(selectedZone.id)
                                            } else if (selectedPlot != null) {
                                                activeDragPlotId = currentSelectedPlotId
                                                editViewModel.onHandleDragStart(currentSelectedPlotId!!)
                                            }
                                        }
                                        hitCrop != null && hitCrop.id == currentSelectedZoneId -> {
                                            dragMode = DragMode.MOVE_CROP
                                            editViewModel.onCropZoneDragStart(hitCrop.id)
                                        }
                                        hitPlot != null && hitPlot.id == currentSelectedPlotId && currentSelectedZoneId == null -> {
                                            dragMode = DragMode.MOVE_PLOT
                                            activeDragPlotId = hitPlot.id
                                            editViewModel.onPlotDragStart(hitPlot.id)
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
                                        val fitZoom = minOf(availW / (45f * TopDownProjection.PPM), availH / (45f * TopDownProjection.PPM)).coerceIn(0.15f, 3.5f)
                                        val targetZoom = (camera.zoom * factor).coerceIn(fitZoom, 3.5f)
                                        val actualScale = if (camera.zoom > 0f) targetZoom / camera.zoom else 1f

                                        val center = Offset((p1.x + p2.x) / 2f, (p1.y + p2.y) / 2f)
                                        val newPanX = center.x - (center.x - camera.panX) * actualScale
                                        val newPanY = center.y - (center.y - camera.panY) * actualScale
                                        val rawCam = camera.copy(panX = newPanX, panY = newPanY, zoom = targetZoom)
                                        camera = TopDownProjection.clampCamera(
                                            rawCam, farmW = 45f, farmH = 45f,
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
                                            DragMode.MOVE_PLOT -> {
                                                dragAccumWorld += incrementalWorldDelta
                                                activeDragPlotId?.let { plotId ->
                                                    editViewModel.movePlot(plotId, dragAccumWorld)
                                                }
                                                change.consume()
                                            }
                                            DragMode.MOVE_CROP -> {
                                                dragAccumWorld += incrementalWorldDelta
                                                val zid = currentSelectedZoneId
                                                if (zid != null) {
                                                    editViewModel.moveCropZone(zid, dragAccumWorld)
                                                }
                                                change.consume()
                                            }
                                            DragMode.RESIZE_HANDLE -> {
                                                dragAccumWorld += incrementalWorldDelta
                                                val h = activeHandle
                                                val zid = currentSelectedZoneId
                                                val pid = activeDragPlotId
                                                if (h != null) {
                                                    if (zid != null) {
                                                        editViewModel.resizeCropZoneByHandle(zid, h, dragAccumWorld)
                                                    } else if (pid != null) {
                                                        editViewModel.resizePlotByHandle(pid, h, dragAccumWorld)
                                                    }
                                                }
                                                change.consume()
                                            }
                                            DragMode.PAN -> {
                                                val rawCam = camera.copy(
                                                    panX = camera.panX + screenDelta.x,
                                                    panY = camera.panY + screenDelta.y
                                                )
                                                camera = TopDownProjection.clampCamera(
                                                    rawCam, farmW = 45f, farmH = 45f,
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
                                        val hitCrop = hitTestCropZone(worldPos, currentCropZones, currentPlots)
                                        val hitPlot = hitTestPlot(worldPos, currentPlots)
                                        handleTap(
                                            worldPos, hitCrop, hitPlot, currentActiveTool,
                                            activeCropName, activeCropId,
                                            currentIsSnapEnabled, currentPlots, editViewModel
                                        )
                                    }

                                    // Finalize drag
                                    when (dragMode) {
                                        DragMode.MOVE_PLOT -> {
                                            activeDragPlotId?.let { editViewModel.onPlotDragEnd(it) }
                                        }
                                        DragMode.MOVE_CROP -> {
                                            currentSelectedZoneId?.let { editViewModel.onCropZoneDragEnd(it) }
                                        }
                                        DragMode.RESIZE_HANDLE -> {
                                            if (currentSelectedZoneId != null) {
                                                editViewModel.onZoneHandleDragEnd()
                                            } else {
                                                editViewModel.onHandleDragEnd()
                                            }
                                        }
                                        else -> {}
                                    }

                                    dragMode = DragMode.IDLE
                                    activeHandle = null
                                    activeDragPlotId = null
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
        // ── Background ─────────────────────────────────────────────────
        drawRect(CanvasBg, Offset.Zero, size)

        // ── Dot Grid ───────────────────────────────────────────────────
        if (currentIsGridEnabled) {
            drawDotGrid(camera)
        }

        // ── Farm Boundary (45m × 45m) ──────────────────────────────────
        drawFarmBoundary(camera, showBoundary)

        // ── Plot Beds & Crops ──────────────────────────────────────────
        for (plot in currentPlots) {
            drawPlotBed(
                plot = plot,
                cropZones = uiState.cropZones,
                selectedZoneId = currentSelectedZoneId,
                isSelected = (plot.id == currentSelectedPlotId && currentSelectedZoneId == null),
                camera = camera,
                textMeasurer = textMeasurer
            )
        }

        // ── Selection Handles ──────────────────────────────────────────
        val selectedZone = currentCropZones.firstOrNull { it.id == currentSelectedZoneId }
        val selectedPlot = currentPlots.firstOrNull { it.id == currentSelectedPlotId }

        if (selectedZone != null && selectedPlot != null) {
            drawZoneSelectionBorder(selectedZone, selectedPlot, camera)
            if (currentIsResizeMode) {
                drawZoneResizeHandles(selectedZone, selectedPlot, camera)
            }
        } else if (selectedPlot != null) {
            drawSelectionBorder(selectedPlot, camera)
            if (currentIsResizeMode) {
                drawResizeHandles(selectedPlot, camera)
            }
        }

        // ── Drag Hover Tile Preview ────────────────────────────────────
        if (isDraggingCrop && hoverWorldPos != null) {
            val isBedDrag = activeCropId.startsWith("bed", ignoreCase = true) || activeCropName.contains("Bed", ignoreCase = true)
            val hoverW = if (isBedDrag) 2.0f else 1.0f
            val hoverH = 1.0f
            drawHoverTile(hoverWorldPos, isValidPlacement, camera, hoverW, hoverH)
            if (!isBedDrag && activeCropName.isNotBlank()) {
                val htl = TopDownProjection.worldToScreen(hoverWorldPos.x, hoverWorldPos.y, camera)
                val hsw = TopDownProjection.worldSizeToScreen(hoverW, camera)
                val hsh = TopDownProjection.worldSizeToScreen(hoverH, camera)
                CropSvgRenderer.drawCropSvg(
                    drawScope = this,
                    cropName = activeCropName,
                    center = Offset(htl.x + hsw / 2f, htl.y + hsh / 2f),
                    sizePx = minOf(hsw, hsh) * 0.72f
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Drawing helpers
// ═══════════════════════════════════════════════════════════════════════════════

private fun DrawScope.drawDotGrid(camera: TopDownCamera) {
    val worldBounds = visibleWorldBounds(camera)
    val gridStep = when {
        camera.zoom > 1.5f -> 0.5f
        camera.zoom > 0.6f -> 1f
        camera.zoom > 0.25f -> 2f
        else -> 5f
    }
    val startX = (worldBounds.left / gridStep).toInt() * gridStep
    val startY = (worldBounds.top / gridStep).toInt() * gridStep
    val dotRadius = (1.2f * camera.zoom).coerceIn(0.5f, 2.5f)

    var gx = startX
    while (gx <= worldBounds.right) {
        var gy = startY
        while (gy <= worldBounds.bottom) {
            val screen = TopDownProjection.worldToScreen(gx, gy, camera)
            drawCircle(GridDotColor, dotRadius, screen)
            gy += gridStep
        }
        gx += gridStep
    }
}

private fun DrawScope.drawFarmBoundary(camera: TopDownCamera, showBoundary: Boolean = false) {
    if (!showBoundary) return
    val tl = TopDownProjection.worldToScreen(0f, 0f, camera)
    val farmSize = Size(
        TopDownProjection.worldSizeToScreen(45f, camera),
        TopDownProjection.worldSizeToScreen(45f, camera)
    )
    // Distinct plot surface inside the farm box
    drawRoundRect(
        color = FarmInnerBg,
        topLeft = tl,
        size = farmSize,
        cornerRadius = CornerRadius(4f)
    )
    // Green boundary stroke
    drawRoundRect(
        color = FarmBorderColor.copy(alpha = 0.6f),
        topLeft = tl,
        size = farmSize,
        cornerRadius = CornerRadius(4f),
        style = Stroke(
            width = 2.5f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
        )
    )
}

private fun DrawScope.drawHoverTile(
    worldPos: Offset,
    isValid: Boolean,
    camera: TopDownCamera,
    w: Float = 1f,
    h: Float = 1f
) {
    val tl = TopDownProjection.worldToScreen(worldPos.x, worldPos.y, camera)
    val sw = TopDownProjection.worldSizeToScreen(w, camera)
    val sh = TopDownProjection.worldSizeToScreen(h, camera)
    val color = if (isValid) ValidHover.copy(alpha = 0.35f) else InvalidHover.copy(alpha = 0.35f)
    val borderColor = if (isValid) ValidHover else InvalidHover
    drawRoundRect(color, tl, Size(sw, sh), CornerRadius(4f * camera.zoom.coerceIn(0.5f, 2f)))
    drawRoundRect(borderColor, tl, Size(sw, sh), CornerRadius(4f * camera.zoom.coerceIn(0.5f, 2f)), style = Stroke(2f))
}

private fun DrawScope.drawPlotBed(
    plot: PlotRenderData,
    cropZones: List<CropZoneRenderData>,
    selectedZoneId: String?,
    isSelected: Boolean,
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
            // Plain brown bed — no crops
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
        } else {
            // Planted bed — dark brown base with crop zones
            drawRoundRect(PlantedBedFill, tl, bedSize, cornerR)
            drawRoundRect(BedBorder, tl, bedSize, cornerR, style = Stroke(1.5f * camera.zoom.coerceIn(0.5f, 2f)))

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
                    val zFontSize = (9f * camera.zoom).coerceIn(6f, 11f)
                    val zMeasured = textMeasurer.measure(
                        text = cName,
                        style = TextStyle(fontSize = zFontSize.sp, fontWeight = FontWeight.Bold, color = Color.White),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    drawRoundRect(
                        color = Color.Black.copy(alpha = 0.6f),
                        topLeft = Offset(ztl.x + 2f, ztl.y + 2f),
                        size = Size(zMeasured.size.width.toFloat() + 6f, zMeasured.size.height.toFloat() + 2f),
                        cornerRadius = CornerRadius(2f)
                    )
                    drawText(zMeasured, topLeft = Offset(ztl.x + 5f, ztl.y + 3f))
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
    parentBed: PlotRenderData,
    camera: TopDownCamera
) {
    val zx = parentBed.posX + zone.offsetX
    val zy = parentBed.posY + zone.offsetY
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
    parentBed: PlotRenderData,
    camera: TopDownCamera
) {
    val handles = zoneHandlePositions(zone, parentBed)
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

private fun zoneHandlePositions(zone: CropZoneRenderData, parentBed: PlotRenderData): Map<HandleType, Offset> {
    val x = parentBed.posX + zone.offsetX
    val y = parentBed.posY + zone.offsetY
    val w = zone.widthM
    val h = zone.heightM
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
    parentBed: PlotRenderData,
    hitRadius: Float
): HandleType? {
    val handles = zoneHandlePositions(zone, parentBed)
    return handles.entries.firstOrNull { (_, pos) ->
        dist(worldPos, pos) < hitRadius
    }?.key
}

private fun hitTestCropZone(
    worldPos: Offset,
    cropZones: List<CropZoneRenderData>,
    plots: List<PlotRenderData>
): CropZoneRenderData? {
    // Return topmost (last) crop zone containing worldPos
    return cropZones.lastOrNull { zone ->
        val parent = plots.firstOrNull { it.id == zone.plotId }
        if (parent != null) {
            val zx = parent.posX + zone.offsetX
            val zy = parent.posY + zone.offsetY
            worldPos.x >= zx && worldPos.x <= zx + zone.widthM &&
            worldPos.y >= zy && worldPos.y <= zy + zone.heightM
        } else false
    }
}

private fun hitTestPlot(worldPos: Offset, plots: List<PlotRenderData>): PlotRenderData? {
    // Return the topmost (last) plot that contains the point
    return plots.lastOrNull { plot ->
        worldPos.x >= plot.posX && worldPos.x <= plot.posX + plot.widthM &&
        worldPos.y >= plot.posY && worldPos.y <= plot.posY + plot.heightM
    }
}

private fun handleTap(
    worldPos: Offset,
    hitCrop: CropZoneRenderData?,
    hitPlot: PlotRenderData?,
    activeTool: EditTool,
    activeCropName: String,
    activeCropId: String,
    isSnapEnabled: Boolean,
    plots: List<PlotRenderData>,
    editViewModel: EditViewModel
) {
    val isBed = activeCropId.equals("bed", ignoreCase = true) || activeCropName.equals("Bed", ignoreCase = true)

    if (activeCropName.isNotEmpty() &&
        (activeTool == EditTool.ADD_PLANT || activeTool == EditTool.ADD_PLOT)
    ) {
        var tx = worldPos.x; var ty = worldPos.y
        if (isSnapEnabled) {
            val snapped = TopDownProjection.snapToGrid(worldPos)
            tx = snapped.x; ty = snapped.y
        }

        if (isBed) {
            editViewModel.addDirectPlantingPlot(tx.coerceIn(0f, 44f), ty.coerceIn(0f, 44f), "Bed", "bed")
        } else {
            // Crops can only be placed on existing beds
            val targetBed = plots.firstOrNull { plot ->
                tx >= plot.posX && tx < (plot.posX + plot.widthM) &&
                ty >= plot.posY && ty < (plot.posY + plot.heightM) &&
                (plot.cropName == "Bed" || plot.cropId == "bed")
            }
            if (targetBed != null) {
                editViewModel.plantCropInBed(targetBed.id, activeCropName, activeCropId, tx, ty)
            } else {
                editViewModel.reportInvalidDropLocation("⚠️ Hindi wasto ang lokasyon: Paki-lagay ang pananim sa loob ng isang Garden Bed.")
            }
        }
    } else if (hitCrop != null) {
        editViewModel.selectCropZone(hitCrop.id)
    } else if (hitPlot != null) {
        editViewModel.selectPlot(hitPlot.id)
    } else {
        editViewModel.deselect()
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
