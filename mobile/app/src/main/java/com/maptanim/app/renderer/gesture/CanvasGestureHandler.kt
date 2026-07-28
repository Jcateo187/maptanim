package com.maptanim.app.renderer.gesture

import androidx.compose.ui.geometry.Offset
import com.maptanim.app.domain.model.EditTool
import com.maptanim.app.renderer.canvas.FarmCanvasRenderer
import com.maptanim.app.renderer.model.*

/**
 * CanvasGestureHandler — translates raw Compose pointer events into
 * typed canvas actions dispatched to EditViewModel.
 *
 * Handles:
 *   - TAP: bed selection, tool-specific tap actions
 *   - DRAG: bed move (SELECT_MOVE), fence drawing (ADD_FENCE), pan (two-finger)
 *   - PINCH: camera zoom
 *   - LONG PRESS: shortcut for Change Crop dialog
 *   - HANDLE HIT-TEST: determines which handle was touched in Edit Mode
 *
 * No hardcoded bed IDs or positions — all hit-testing uses live BedRenderData
 * from the current ViewModel uiState.
 */
class CanvasGestureHandler(
    private val onBedTapped: (bedId: String) -> Unit,
    private val onCanvasTapped: (worldPos: Offset) -> Unit,            // empty space tap = deselect or place bed
    private val onBedDragStart: (bedId: String) -> Unit,
    private val onBedDragging: (bedId: String, worldDelta: Offset) -> Unit,
    private val onBedDragEnd: (bedId: String) -> Unit,
    private val onHandleDragStart: (handle: HandleType, bedId: String) -> Unit,
    private val onHandleDragging: (handle: HandleType, worldDelta: Offset) -> Unit,
    private val onHandleDragEnd: (handle: HandleType) -> Unit,
    private val onDeleteQuickTapped: (bedId: String) -> Unit,
    private val onActionBtnTapped: (bedId: String) -> Unit,   // green ⊕ → Change Crop
    private val onCameraPan: (dx: Float, dy: Float) -> Unit,
    private val onCameraZoom: (scaleFactor: Float, centroid: Offset) -> Unit,
    private val onLongPress: (bedId: String) -> Unit,
    private val onCropZoneTapped: ((zoneId: String) -> Unit)? = null,
    private val onAddTrellisTapped: ((bedId: String) -> Unit)? = null
) {


    private var activeHandleDrag: HandleType? = null
    private var activeBedDrag: String? = null

    /**
     * Called on each pointer tap event.
     * 1. Check if any handle was tapped (hit radius: 24dp)
     * 2. If not, check if any bed was tapped (point-in-rhombus test)
     * 3. If not, it's an empty canvas tap → deselect
     *
     * @param screenPos  Tap position in screen pixels
     * @param beds       Current beds from ViewModel uiState (sourced from Room)
     * @param camera     Current camera state
     * @param handles    Current handle positions (null if no bed selected)
     * @param activeTool Current edit tool selection
     * @param selectedBedId Current selected bed ID
     */
    fun onTap(
        screenPos: Offset,
        beds: List<BedRenderData>,
        cropZones: List<CropZoneRenderData> = emptyList(),
        camera: CameraState,
        handles: HandlePositions?,
        activeTool: EditTool,
        selectedBedId: String?
    ) {
        // ── 1. Check handles first ───────────────────────────────────────
        if (handles != null && selectedBedId != null) {
            val hitHandle = hitTestHandles(screenPos, handles, camera)
            if (hitHandle != null) {
                when (hitHandle) {
                    HandleType.DELETE_QUICK -> onDeleteQuickTapped(selectedBedId)
                    HandleType.ACTION_BTN   -> onActionBtnTapped(selectedBedId)
                    else                    -> { /* handled by drag flow */ }
                }
                return
            }
        }

        // ── 2. Check beds ────────────────────────────────────────────────
        val tappedBed = hitTestBeds(screenPos, beds, camera)
        val worldPos = IsometricProjection.toWorld(screenPos.x, screenPos.y, camera)

        if (tappedBed != null) {
            // ── 2.5. Check crop zones within the tapped bed ──────────────
            if (tappedBed.id == selectedBedId && onCropZoneTapped != null) {
                val hitZone = hitTestCropZones(screenPos, tappedBed, cropZones.filter { it.bedId == tappedBed.id }, camera)
                if (hitZone != null) {
                    onCropZoneTapped.invoke(hitZone.id)
                    return
                }
            }

            when (activeTool) {
                EditTool.SELECT_MOVE -> onBedTapped(tappedBed.id)
                EditTool.ADD_PLANT   -> onBedTapped(tappedBed.id)
                EditTool.DELETE      -> onDeleteQuickTapped(tappedBed.id)
                EditTool.ADD_BED     -> onCanvasTapped(worldPos)
            }
        } else {
            onCanvasTapped(worldPos)  // Deselect
        }

    }

    /**
     * Called during drag events.
     * Differentiates between: handle drag, bed body drag, camera pan.
     */
    fun onDrag(
        currentPos: Offset,
        previousPos: Offset,
        beds: List<BedRenderData>,
        camera: CameraState,
        handles: HandlePositions?,
        selectedBedId: String?,
        activeTool: EditTool
    ) {
        val screenDelta = currentPos - previousPos

        // ── Handle drag ─────────────────────────────────────────────────
        activeHandleDrag?.let { handle ->
            val worldDelta = screenDeltaToWorldDelta(screenDelta, camera)
            onHandleDragging(handle, worldDelta)
            return
        }

        // ── Bed body drag (SELECT_MOVE tool, bed selected) ───────────────
        activeBedDrag?.let { bedId ->
            val worldDelta = screenDeltaToWorldDelta(screenDelta, camera)
            onBedDragging(bedId, worldDelta)
            return
        }

        // ── Camera pan (no bed/handle drag active) ───────────────────────
        onCameraPan(screenDelta.x, screenDelta.y)
    }

    fun onDragStart(
        startPos: Offset,
        beds: List<BedRenderData>,
        camera: CameraState,
        handles: HandlePositions?,
        selectedBedId: String?,
        activeTool: EditTool
    ) {
        if (handles != null && selectedBedId != null) {
            val hitHandle = hitTestHandles(startPos, handles, camera)
            if (hitHandle != null && hitHandle !in listOf(HandleType.DELETE_QUICK, HandleType.ACTION_BTN)) {
                activeHandleDrag = hitHandle
                onHandleDragStart(hitHandle, selectedBedId)
                return
            }
        }

        if (activeTool == EditTool.SELECT_MOVE) {
            val tappedBed = hitTestBeds(startPos, beds, camera)
            if (tappedBed != null && tappedBed.id == selectedBedId) {
                activeBedDrag = tappedBed.id
                onBedDragStart(tappedBed.id)
            }
        }
    }

    fun onDragEnd() {
        activeHandleDrag?.let { handle ->
            onHandleDragEnd(handle)
            activeHandleDrag = null
            return
        }
        activeBedDrag?.let { bedId ->
            onBedDragEnd(bedId)
            activeBedDrag = null
        }
    }

    fun onPinchZoom(scaleFactor: Float, centroid: Offset) =
        onCameraZoom(scaleFactor, centroid)

    fun onLongPress(screenPos: Offset, beds: List<BedRenderData>, camera: CameraState) {
        hitTestBeds(screenPos, beds, camera)?.let { onLongPress(it.id) }
    }

    // ── Hit Testing ──────────────────────────────────────────────────────

    /**
     * Point-in-rhombus hit test for isometric bed shapes.
     * Tests from back-to-front (reverse painter order) so topmost bed wins.
     */
    private fun hitTestBeds(
        screenPos: Offset,
        beds: List<BedRenderData>,
        camera: CameraState
    ): BedRenderData? {
        // Convert screen tap to world position, then check which bed contains it
        val worldPos = IsometricProjection.toWorld(screenPos.x, screenPos.y, camera)
        return beds
            .sortedByDescending { it.posX + it.posY }  // front beds tested first
            .firstOrNull { bed ->
                worldPos.x >= bed.posX && worldPos.x <= bed.posX + bed.widthM &&
                worldPos.y >= bed.posY && worldPos.y <= bed.posY + bed.heightM
            }
    }

    /** Handle hit test — checks if screenPos is within HIT_RADIUS of any handle center. */
    private fun hitTestHandles(
        screenPos: Offset,
        handles: HandlePositions,
        camera: CameraState
    ): HandleType? {
        val hitRadius = HIT_RADIUS_PX * camera.zoom
        val handleMap = mapOf(
            HandleType.DRAG       to handles.dragHandle,
            HandleType.DELETE_QUICK to handles.deleteQuick,
            HandleType.CORNER_TL  to handles.cornerTL,
            HandleType.CORNER_TR  to handles.cornerTR,
            HandleType.CORNER_BL  to handles.cornerBL,
            HandleType.CORNER_BR  to handles.cornerBR,
            HandleType.MID_TOP    to handles.midTop,
            HandleType.MID_BOTTOM to handles.midBottom,
            HandleType.MID_LEFT   to handles.midLeft,
            HandleType.MID_RIGHT  to handles.midRight,
            HandleType.ACTION_BTN to handles.actionBtn
        )
        return handleMap.entries
            .firstOrNull { (_, pos) -> (screenPos - pos).getDistance() <= hitRadius }
            ?.key
    }

    /**
     * Point-in-rect hit test for crop zones within a selected bed.
     * Converts screen tap to world coordinates and tests against zone bounds.
     */
    private fun hitTestCropZones(
        screenPos: Offset,
        parentBed: BedRenderData,
        zones: List<CropZoneRenderData>,
        camera: CameraState
    ): CropZoneRenderData? {
        val worldPos = IsometricProjection.toWorld(screenPos.x, screenPos.y, camera)
        // Zone coordinates are relative to bed origin
        val relX = worldPos.x - parentBed.posX
        val relY = worldPos.y - parentBed.posY
        return zones.firstOrNull { zone ->
            relX >= zone.offsetX && relX <= zone.offsetX + zone.widthM &&
            relY >= zone.offsetY && relY <= zone.offsetY + zone.heightM
        }
    }

    private fun screenDeltaToWorldDelta(screenDelta: Offset, camera: CameraState): Offset {
        // Screen pixel delta → world meter delta (accounting for zoom)
        val worldDx = screenDelta.x / (camera.zoom * IsometricProjection.TILE_W / 2f)
        val worldDy = screenDelta.y / (camera.zoom * IsometricProjection.TILE_H / 2f)
        // Invert isometric transform for move
        return Offset(
            x = (worldDx + worldDy) / 2f,
            y = (worldDy - worldDx) / 2f
        )
    }

    companion object {
        private const val HIT_RADIUS_PX = 24f   // 24px hit radius for handle touch targets
    }
}

// ─── HandleType ──────────────────────────────────────────────────────────

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
