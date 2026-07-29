package com.maptanim.app.renderer.gesture

import androidx.compose.ui.geometry.Offset
import com.maptanim.app.domain.model.EditTool
import com.maptanim.app.renderer.canvas.FarmCanvasRenderer
import com.maptanim.app.renderer.model.*

/**
 * CanvasGestureHandler — translates raw Compose pointer events into
 * typed canvas actions dispatched to EditViewModel.
 *
 * Streamlined for 100% responsive, effortless crop drag re-positioning.
 */
class CanvasGestureHandler(
    private val onPlotTapped: (plotId: String) -> Unit,
    private val onCanvasTapped: (worldPos: Offset) -> Unit,
    private val onPlotDragStart: (plotId: String) -> Unit,
    private val onPlotDragging: (plotId: String, worldDelta: Offset) -> Unit,
    private val onPlotDragEnd: (plotId: String) -> Unit,
    private val onHandleDragStart: (handle: HandleType, plotId: String) -> Unit,
    private val onHandleDragging: (handle: HandleType, worldDelta: Offset) -> Unit,
    private val onHandleDragEnd: (handle: HandleType) -> Unit,
    private val onDeleteQuickTapped: (plotId: String) -> Unit,
    private val onActionBtnTapped: (plotId: String) -> Unit,
    private val onCameraPan: (dx: Float, dy: Float) -> Unit,
    private val onCameraZoom: (scaleFactor: Float, centroid: Offset) -> Unit,
    private val onLongPress: (plotId: String) -> Unit,
    private val onCropZoneTapped: ((zoneId: String) -> Unit)? = null,
    private val onAddTrellisTapped: ((plotId: String) -> Unit)? = null
) {

    private var activeHandleDrag: HandleType? = null
    private var activePlotDrag: String? = null

    private var initialPlotWorldPos: Offset? = null
    private var initialTouchWorldPos: Offset? = null

    /**
     * Called on each pointer tap event.
     */
    fun onTap(
        screenPos: Offset,
        plots: List<PlotRenderData>,
        cropZones: List<CropZoneRenderData> = emptyList(),
        camera: CameraState,
        handles: HandlePositions?,
        activeTool: EditTool,
        selectedPlotId: String?
    ) {
        val tappedPlot = hitTestPlots(screenPos, plots, camera)
        val worldPos = IsometricProjection.toWorld(screenPos.x, screenPos.y, camera)

        if (tappedPlot != null) {
            when (activeTool) {
                EditTool.SELECT_MOVE -> onPlotTapped(tappedPlot.id)
                EditTool.ADD_PLANT   -> onPlotTapped(tappedPlot.id)
                EditTool.DELETE      -> onDeleteQuickTapped(tappedPlot.id)
                EditTool.ADD_PLOT    -> onCanvasTapped(worldPos)
            }
        } else {
            onCanvasTapped(worldPos)  // Deselect
        }
    }

    /**
     * Called during drag events.
     */
    fun onDrag(
        currentPos: Offset,
        previousPos: Offset,
        plots: List<PlotRenderData>,
        camera: CameraState,
        handles: HandlePositions?,
        selectedPlotId: String?,
        activeTool: EditTool
    ) {
        val screenDelta = currentPos - previousPos

        // ── Plot body drag (re-positioning placed crop) ───────────────────
        activePlotDrag?.let { plotId ->
            val currentTouchWorldPos = IsometricProjection.toWorld(currentPos.x, currentPos.y, camera)
            val startTouchWorld = initialTouchWorldPos ?: currentTouchWorldPos
            val worldDelta = currentTouchWorldPos - startTouchWorld

            onPlotDragging(plotId, worldDelta)
            initialTouchWorldPos = currentTouchWorldPos
            return
        }

        // ── Camera pan (no plot drag active) ──────────────────────────────
        onCameraPan(screenDelta.x, screenDelta.y)
    }

    fun onDragStart(
        startPos: Offset,
        plots: List<PlotRenderData>,
        camera: CameraState,
        handles: HandlePositions?,
        selectedPlotId: String?,
        activeTool: EditTool
    ) {
        // Direct hold & drag on any placed crop on farm area with expanded hit radius
        val tappedPlot = hitTestPlots(startPos, plots, camera)
        if (tappedPlot != null) {
            onPlotTapped(tappedPlot.id)
            activePlotDrag = tappedPlot.id
            initialPlotWorldPos = Offset(tappedPlot.posX, tappedPlot.posY)
            initialTouchWorldPos = IsometricProjection.toWorld(startPos.x, startPos.y, camera)
            onPlotDragStart(tappedPlot.id)
            return
        }
    }

    fun onDragEnd() {
        activePlotDrag?.let { plotId ->
            onPlotDragEnd(plotId)
            activePlotDrag = null
            initialPlotWorldPos = null
            initialTouchWorldPos = null
        }
    }

    fun onPinchZoom(scaleFactor: Float, centroid: Offset) =
        onCameraZoom(scaleFactor, centroid)

    fun onLongPress(screenPos: Offset, plots: List<PlotRenderData>, camera: CameraState) {
        hitTestPlots(screenPos, plots, camera)?.let { onLongPress(it.id) }
    }

    // ── Hit Testing ──────────────────────────────────────────────────────

    private fun hitTestPlots(
        screenPos: Offset,
        plots: List<PlotRenderData>,
        camera: CameraState
    ): PlotRenderData? {
        val worldPos = IsometricProjection.toWorld(screenPos.x, screenPos.y, camera)
        val pad = 0.6f // Expanded hit buffer around plot bounds for effortless holding
        return plots
            .sortedByDescending { it.posX + it.posY }
            .firstOrNull { plot ->
                worldPos.x >= (plot.posX - pad) && worldPos.x <= (plot.posX + plot.widthM + pad) &&
                worldPos.y >= (plot.posY - pad) && worldPos.y <= (plot.posY + plot.heightM + pad)
            }
    }

    private fun screenDeltaToWorldDelta(screenDelta: Offset, camera: CameraState): Offset {
        val worldDx = screenDelta.x / (camera.zoom * IsometricProjection.TILE_W / 2f)
        val worldDy = screenDelta.y / (camera.zoom * IsometricProjection.TILE_H / 2f)
        return Offset(
            x = (worldDx + worldDy) / 2f,
            y = (worldDy - worldDx) / 2f
        )
    }
}

enum class HandleType {
    DRAG,
    DELETE_QUICK,
    CORNER_TL,
    CORNER_TR,
    CORNER_BL,
    CORNER_BR,
    MID_TOP,
    MID_BOTTOM,
    MID_LEFT,
    MID_RIGHT,
    ACTION_BTN
}
