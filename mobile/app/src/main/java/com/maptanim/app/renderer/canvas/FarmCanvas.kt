package com.maptanim.app.renderer.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.maptanim.app.domain.model.CanvasMode
import com.maptanim.app.renderer.gesture.CanvasGestureHandler
import com.maptanim.app.renderer.gesture.HandleType
import com.maptanim.app.renderer.model.CameraState
import com.maptanim.app.renderer.model.HandlePositions
import com.maptanim.app.ui.screens.edit.EditUiState
import com.maptanim.app.ui.screens.edit.EditViewModel

/**
 * FarmCanvas — High-performance 2D Isometric Farm Canvas Composable.
 */
@Composable
fun FarmCanvas(
    modifier: Modifier = Modifier,
    uiState: EditUiState,
    editViewModel: EditViewModel,
    canvasMode: CanvasMode = CanvasMode.EDIT,
    activeCropName: String = "Carrot",
    activeCropId: String = "carrot",
    hoverWorldPos: Offset? = null,
    isValidPlacement: Boolean = true,
    isDraggingCrop: Boolean = false,
    onCameraStateChanged: (CameraState) -> Unit = {},
    onCanvasTouchPosChanged: ((Offset?) -> Unit)? = null,
    onOpenCropPicker: (() -> Unit)? = null,
    onOpenMonitoring: (() -> Unit)? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val resources = context.resources
    var cameraState by remember { mutableStateOf(CameraState()) }
    var currentHandles by remember { mutableStateOf<HandlePositions?>(null) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    val currentPlots by rememberUpdatedState(uiState.plots)
    val currentSelectedPlotId by rememberUpdatedState(uiState.selectedPlotId)
    val currentActiveTool by rememberUpdatedState(uiState.activeTool)
    val currentCropZones by rememberUpdatedState(uiState.cropZones)
    val currentIsResizeMode by rememberUpdatedState(uiState.isResizeMode)
    val currentIsSnapEnabled by rememberUpdatedState(uiState.isSnapEnabled)

    LaunchedEffect(cameraState) {
        onCameraStateChanged(cameraState)
    }

    val gestureHandler = remember(editViewModel, activeCropName, activeCropId, onOpenCropPicker, onOpenMonitoring, canvasSize) {
        CanvasGestureHandler(
            onPlotTapped = { plotId ->
                editViewModel.selectPlot(plotId)
            },
            onCanvasTapped = { worldPos ->
                var targetX = worldPos.x
                var targetY = worldPos.y
                if (currentIsSnapEnabled) {
                    val snapped = FarmCanvasRenderer.snapToGrid(worldPos)
                    targetX = snapped.x
                    targetY = snapped.y
                }
                if (activeCropName.isNotEmpty() && 
                    (currentActiveTool == com.maptanim.app.domain.model.EditTool.ADD_PLANT ||
                     currentActiveTool == com.maptanim.app.domain.model.EditTool.ADD_PLOT)) {
                    if (isValidPlacement) {
                        editViewModel.addDirectPlantingPlot(targetX.coerceIn(0f, 44f), targetY.coerceIn(0f, 44f), activeCropName, activeCropId)
                    }
                } else {
                    editViewModel.deselect()
                }
            },
            onPlotDragStart = { plotId -> editViewModel.onPlotDragStart(plotId) },
            onPlotDragging = { plotId, worldDelta ->
                editViewModel.movePlot(plotId, worldDelta)
            },
            onPlotDragEnd = { plotId, isValid -> editViewModel.onPlotDragEnd(plotId, isValid) },
            onHandleDragStart = { _, plotId -> editViewModel.onHandleDragStart(plotId) },
            onHandleDragging = { handle, worldDelta ->
                currentSelectedPlotId?.let { plotId ->
                    editViewModel.resizePlotByHandle(plotId, handle, worldDelta)
                }
            },
            onHandleDragEnd = { _ -> editViewModel.onHandleDragEnd() },
            onDeleteQuickTapped = { plotId -> editViewModel.deletePlot(plotId) },
            onActionBtnTapped = { plotId -> editViewModel.selectPlot(plotId) },
            onCameraPan = { dx, dy ->
                cameraState = cameraState.pan(dx, dy, canvasSize.width.toFloat(), canvasSize.height.toFloat())
            },
            onCameraZoom = { scaleFactor, _ ->
                cameraState = cameraState.clampZoom(cameraState.zoom * scaleFactor, canvasSize.width.toFloat(), canvasSize.height.toFloat())
                editViewModel.updateZoom(cameraState.zoom)
            },
            onLongPress = { plotId -> editViewModel.selectPlot(plotId) },
            onAddTrellisTapped = { plotId -> editViewModel.addTrellis(plotId) },
            onCropZoneTapped = { zoneId -> editViewModel.selectCropZone(zoneId) }
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { canvasSize = it }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    var downPos = Offset.Zero
                    var totalDragDistance = 0f

                    while (true) {
                        val event = awaitPointerEvent()
                        val changes = event.changes

                        // ── 1. Multi-touch (2+ pointers -> Pinch Zoom & 2-finger Pan) ─
                        if (changes.size >= 2) {
                            onCanvasTouchPosChanged?.invoke(null)
                            val p1 = changes[0]
                            val p2 = changes[1]
                            if (p1.pressed && p2.pressed) {
                                val prevP1 = p1.previousPosition
                                val prevP2 = p2.previousPosition
                                val currentP1 = p1.position
                                val currentP2 = p2.position

                                val prevDist = (prevP1 - prevP2).getDistance()
                                val currentDist = (currentP1 - currentP2).getDistance()

                                if (prevDist > 0f && currentDist > 0f) {
                                    val scaleFactor = currentDist / prevDist
                                    val centroid = (currentP1 + currentP2) / 2f
                                    gestureHandler.onPinchZoom(scaleFactor, centroid)
                                }

                                val prevCentroid = (prevP1 + prevP2) / 2f
                                val currentCentroid = (currentP1 + currentP2) / 2f
                                val panDelta = currentCentroid - prevCentroid
                                if (panDelta.getDistance() > 0.5f) {
                                    gestureHandler.onDrag(
                                        currentPos = currentCentroid,
                                        previousPos = prevCentroid,
                                        plots = currentPlots,
                                        camera = cameraState,
                                        handles = currentHandles,
                                        selectedPlotId = currentSelectedPlotId,
                                        activeTool = currentActiveTool
                                    )
                                }
                                p1.consume()
                                p2.consume()
                            }
                        }
                        // ── 2. Single-touch (1 pointer -> Crop drag / Camera pan) ──────
                        else if (changes.size == 1) {
                            val change = changes[0]

                            if (change.changedToDown()) {
                                downPos = change.position
                                totalDragDistance = 0f
                                onCanvasTouchPosChanged?.invoke(change.position)
                                gestureHandler.onDragStart(
                                    startPos = change.position,
                                    plots = currentPlots,
                                    camera = cameraState,
                                    handles = currentHandles,
                                    selectedPlotId = currentSelectedPlotId,
                                    activeTool = currentActiveTool,
                                    isResizeMode = currentIsResizeMode
                                )
                            } else if (change.pressed && change.positionChange() != Offset.Zero) {
                                val delta = change.positionChange()
                                totalDragDistance += delta.getDistance()
                                onCanvasTouchPosChanged?.invoke(change.position)
                                gestureHandler.onDrag(
                                    currentPos = change.position,
                                    previousPos = change.previousPosition,
                                    plots = currentPlots,
                                    camera = cameraState,
                                    handles = currentHandles,
                                    selectedPlotId = currentSelectedPlotId,
                                    activeTool = currentActiveTool
                                )
                                change.consume()
                            } else if (change.changedToUp()) {
                                onCanvasTouchPosChanged?.invoke(null)
                                if (totalDragDistance < 8f) {
                                    // Tap event (minimal movement)
                                    gestureHandler.onTap(
                                        screenPos = downPos,
                                        plots = currentPlots,
                                        cropZones = currentCropZones,
                                        camera = cameraState,
                                        handles = currentHandles,
                                        activeTool = currentActiveTool,
                                        selectedPlotId = currentSelectedPlotId
                                    )
                                }
                                gestureHandler.onDragEnd(isValidPlacement)
                            }
                        }
                    }
                }
            }
    ) {
        if (cameraState.panX == 0f && size.width > 0f) {
            cameraState = cameraState.centered(size.width, size.height)
        }

        with(FarmCanvasRenderer) {
            render(
                plots = uiState.plots,
                cropZones = uiState.cropZones,
                farmObjects = uiState.farmObjects,
                camera = cameraState,
                canvasMode = canvasMode,
                selectedPlotId = uiState.selectedPlotId,
                selectedZoneId = uiState.selectedZoneId,
                hoverWorldPos = hoverWorldPos,
                isValidPlacement = isValidPlacement,
                isDraggingCrop = isDraggingCrop,
                isGridEnabled = uiState.isGridEnabled,
                isSnapEnabled = uiState.isSnapEnabled,
                isResizeMode = uiState.isResizeMode,
                resources = resources,
                context = context,
                onHandlesReady = { _, handles ->
                    currentHandles = handles
                }
            )
        }
    }
}
