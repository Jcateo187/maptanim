package com.maptanim.app.renderer.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import com.maptanim.app.domain.model.CanvasMode
import com.maptanim.app.renderer.gesture.CanvasGestureHandler
import com.maptanim.app.renderer.gesture.HandleType
import com.maptanim.app.renderer.model.CameraState
import com.maptanim.app.renderer.model.HandlePositions
import com.maptanim.app.ui.screens.edit.EditUiState
import com.maptanim.app.ui.screens.edit.EditViewModel

/**
 * FarmCanvas — High-performance 2D Isometric Farm Canvas Composable.
 *
 * Connected to float-based CameraState and CanvasGestureHandler.
 * Renders back-to-front: Grass ground -> Paths -> Beds -> Crop Zones & Plants -> Labels -> Trellises/Fences -> Handles/Pins -> Grid.
 */
@Composable
fun FarmCanvas(
    modifier: Modifier = Modifier,
    uiState: EditUiState,
    editViewModel: EditViewModel,
    canvasMode: CanvasMode = CanvasMode.EDIT,
    activeCropName: String = "Carrot",
    activeCropId: String = "carrot",
    onOpenCropPicker: (() -> Unit)? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val resources = context.resources
    var cameraState by remember { mutableStateOf(CameraState()) }
    var currentHandles by remember { mutableStateOf<HandlePositions?>(null) }

    val gestureHandler = remember(editViewModel, activeCropName, activeCropId, onOpenCropPicker) {
        CanvasGestureHandler(
            onBedTapped = { bedId ->
                when (uiState.activeTool) {
                    com.maptanim.app.domain.model.EditTool.SELECT_MOVE -> editViewModel.selectBed(bedId)
                    com.maptanim.app.domain.model.EditTool.ADD_PLANT   -> {
                        editViewModel.selectBed(bedId)
                        onOpenCropPicker?.invoke()
                    }
                    com.maptanim.app.domain.model.EditTool.DELETE      -> editViewModel.deleteBed(bedId)
                    else -> editViewModel.selectBed(bedId)
                }
            },
            onCanvasTapped = { worldPos ->
                var targetX = worldPos.x
                var targetY = worldPos.y
                if (uiState.isSnapEnabled) {
                    val snapped = FarmCanvasRenderer.snapToGrid(worldPos)
                    targetX = snapped.x
                    targetY = snapped.y
                }
                if (uiState.activeTool == com.maptanim.app.domain.model.EditTool.ADD_PLANT ||
                    uiState.activeTool == com.maptanim.app.domain.model.EditTool.ADD_BED) {
                    editViewModel.addDirectPlantingPlot(targetX, targetY, activeCropName, activeCropId)
                } else {
                    editViewModel.deselect()
                }
            },
            onBedDragStart = { },
            onBedDragging = { bedId, worldDelta ->
                editViewModel.moveBed(bedId, worldDelta)
            },
            onBedDragEnd = { },
            onHandleDragStart = { _, _ -> },
            onHandleDragging = { handle, worldDelta ->
                uiState.selectedBedId?.let { bedId ->
                    val bed = uiState.editedBeds.firstOrNull { it.id == bedId }
                    if (bed != null) {
                        when (handle) {
                            // Mid-edge handles: resize in one axis only
                            HandleType.MID_RIGHT -> editViewModel.resizeBed(bedId, bed.widthM + worldDelta.x, bed.heightM)
                            HandleType.MID_LEFT  -> editViewModel.resizeBed(bedId, bed.widthM - worldDelta.x, bed.heightM)
                            HandleType.MID_BOTTOM -> editViewModel.resizeBed(bedId, bed.widthM, bed.heightM + worldDelta.y)
                            HandleType.MID_TOP   -> editViewModel.resizeBed(bedId, bed.widthM, bed.heightM - worldDelta.y)
                            // Corner handles: resize both axes
                            HandleType.CORNER_BR -> editViewModel.resizeBed(bedId, bed.widthM + worldDelta.x, bed.heightM + worldDelta.y)
                            HandleType.CORNER_BL -> editViewModel.resizeBed(bedId, bed.widthM - worldDelta.x, bed.heightM + worldDelta.y)
                            HandleType.CORNER_TR -> editViewModel.resizeBed(bedId, bed.widthM + worldDelta.x, bed.heightM - worldDelta.y)
                            HandleType.CORNER_TL -> editViewModel.resizeBed(bedId, bed.widthM - worldDelta.x, bed.heightM - worldDelta.y)
                            // Drag handle: move
                            HandleType.DRAG -> editViewModel.moveBed(bedId, worldDelta)
                            else -> {}
                        }
                    }
                }
            },
            onHandleDragEnd = { },
            onDeleteQuickTapped = { bedId -> editViewModel.deleteBed(bedId) },
            onActionBtnTapped = { bedId ->
                editViewModel.selectBed(bedId)
                onOpenCropPicker?.invoke()
            },
            onCameraPan = { dx, dy ->
                cameraState = cameraState.pan(dx, dy)
            },
            onCameraZoom = { scaleFactor, _ ->
                cameraState = cameraState.clampZoom(cameraState.zoom * scaleFactor)
                editViewModel.updateZoom(cameraState.zoom)
            },
            onLongPress = { bedId ->
                editViewModel.selectBed(bedId)
                onOpenCropPicker?.invoke()
            },
            onAddTrellisTapped = { bedId -> editViewModel.addTrellis(bedId) },
            onCropZoneTapped = { zoneId -> editViewModel.selectCropZone(zoneId) }
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(gestureHandler) {
                detectTapGestures(
                    onTap = { offset ->
                        gestureHandler.onTap(
                            screenPos = offset,
                            beds = uiState.beds,
                            cropZones = uiState.cropZones,
                            camera = cameraState,
                            handles = currentHandles,
                            activeTool = uiState.activeTool,
                            selectedBedId = uiState.selectedBedId
                        )
                    },
                    onLongPress = { offset ->
                        gestureHandler.onLongPress(offset, uiState.beds, cameraState)
                    }
                )
            }
            .pointerInput(gestureHandler) {
                detectDragGestures(
                    onDragStart = { startOffset ->
                        gestureHandler.onDragStart(
                            startPos = startOffset,
                            beds = uiState.beds,
                            camera = cameraState,
                            handles = currentHandles,
                            selectedBedId = uiState.selectedBedId,
                            activeTool = uiState.activeTool
                        )
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        gestureHandler.onDrag(
                            currentPos = change.position,
                            previousPos = change.previousPosition,
                            beds = uiState.beds,
                            camera = cameraState,
                            handles = currentHandles,
                            selectedBedId = uiState.selectedBedId,
                            activeTool = uiState.activeTool
                        )
                    },
                    onDragEnd = {
                        gestureHandler.onDragEnd()
                    }
                )
            }
    ) {
        if (cameraState.panX == 0f && size.width > 0f) {
            cameraState = cameraState.centered(size.width, size.height)
        }

        with(FarmCanvasRenderer) {
            render(
                beds = uiState.beds,
                cropZones = uiState.cropZones,
                farmObjects = uiState.farmObjects,
                camera = cameraState,
                canvasMode = canvasMode,
                selectedBedId = uiState.selectedBedId,
                selectedZoneId = uiState.selectedZoneId,
                isGridEnabled = uiState.isGridEnabled,
                isSnapEnabled = uiState.isSnapEnabled,
                resources = resources,
                context = context,
                onHandlesReady = { _, handles ->
                    currentHandles = handles
                }
            )
        }
    }
}
