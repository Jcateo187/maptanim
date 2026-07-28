package com.maptanim.app.renderer.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.maptanim.app.domain.model.FarmObjectType
import com.maptanim.app.domain.model.SoilType


// ─── CameraState ──────────────────────────────────────────────────────────

/**
 * Immutable camera state for the isometric farm canvas.
 * Stored in EditViewModel / HomeViewModel uiState — NOT persisted to Room.
 * Session-only: resets to default on screen re-entry.
 */
data class CameraState(
    val panX: Float = 0f,       // Horizontal pan offset in screen pixels
    val panY: Float = 0f,       // Vertical pan offset in screen pixels
    val zoom: Float = 1.0f,     // Scale factor — 70% min zoom (0.70f)
    val minZoom: Float = 0.70f, // 70% minimum zoom limit as requested
    val maxZoom: Float = 4.0f   // 400% maximum zoom
) {
    fun clampZoom(newZoom: Float) = copy(zoom = newZoom.coerceIn(minZoom, maxZoom))

    /** Pans the camera with expanded boundaries to reach the left side and all corners of the map. */
    fun pan(dx: Float, dy: Float): CameraState {
        val maxPanX = 2500f * zoom
        val maxPanY = 2500f * zoom
        return copy(
            panX = (panX + dx).coerceIn(-maxPanX, maxPanX),
            panY = (panY + dy).coerceIn(-maxPanY, maxPanY)
        )
    }

    fun centered(screenWidth: Float, screenHeight: Float) = copy(
        panX = screenWidth / 2f,
        panY = screenHeight * 0.15f
    )

    /** Returns the farm world bounds visible at current pan/zoom with buffer padding. */
    fun getVisibleWorldBounds(screenWidth: Float, screenHeight: Float): Rect {
        val c1 = IsometricProjection.toWorld(0f, 0f, this)
        val c2 = IsometricProjection.toWorld(screenWidth, 0f, this)
        val c3 = IsometricProjection.toWorld(0f, screenHeight, this)
        val c4 = IsometricProjection.toWorld(screenWidth, screenHeight, this)

        val minX = minOf(c1.x, c2.x, c3.x, c4.x) - 6f
        val maxX = maxOf(c1.x, c2.x, c3.x, c4.x) + 6f
        val minY = minOf(c1.y, c2.y, c3.y, c4.y) - 6f
        val maxY = maxOf(c1.y, c2.y, c3.y, c4.y) + 6f

        return Rect(minX, minY, maxX, maxY)
    }
}

// ─── IsometricProjection ──────────────────────────────────────────────────

/**
 * Converts between world coordinates (meters, from farm origin) and
 * screen coordinates (pixels, from canvas top-left).
 *
 * Projection type: Cabinet isometric, 2:1 pixel ratio, ~30° elevation.
 *
 * World coordinate system:
 *   - Origin (0, 0) = top-left corner of farm
 *   - X axis = goes right-and-down in screen space
 *   - Y axis = goes right-and-up in screen space
 *
 * Formula:
 *   screenX = (worldX - worldY) * (TILE_W / 2)
 *   screenY = (worldX + worldY) * (TILE_H / 2)
 */
object IsometricProjection {
    const val TILE_W = 128f   // 1 world meter → 128px wide tile
    const val TILE_H = 64f    // 1 world meter → 64px tall tile (2:1 ratio)

    fun toScreen(worldX: Float, worldY: Float, camera: CameraState): Offset {
        val rawX = (worldX - worldY) * (TILE_W / 2f)
        val rawY = (worldX + worldY) * (TILE_H / 2f)
        return Offset(
            x = rawX * camera.zoom + camera.panX,
            y = rawY * camera.zoom + camera.panY
        )
    }

    fun toWorld(screenX: Float, screenY: Float, camera: CameraState): Offset {
        val rawX = (screenX - camera.panX) / camera.zoom
        val rawY = (screenY - camera.panY) / camera.zoom
        return Offset(
            x = (rawX / (TILE_W / 2f) + rawY / (TILE_H / 2f)) / 2f,
            y = (rawY / (TILE_H / 2f) - rawX / (TILE_W / 2f)) / 2f
        )
    }
}

// ─── BedRenderData ────────────────────────────────────────────────────────

/**
 * Render-ready view of a Bed.
 * Built from Bed domain model + computed screen coordinates.
 * Populated by BedRenderMapper from Room data — no hardcoded positions.
 */
data class BedRenderData(
    val id: String,
    val bedLabel: String,       // From beds.bed_label (Room)
    val cropName: String?,      // From beds.crop_name (Room) — null shows + placeholder
    val soilType: SoilType,     // From beds.soil_type (Room) — drives soil texture tile
    val growthStage: GrowthStage? = null,  // Computed by GrowthStageCalculator
    val posX: Float,            // From beds.pos_x (Room)
    val posY: Float,            // From beds.pos_y (Room)
    val widthM: Float,          // From beds.width_m (Room)
    val heightM: Float,         // From beds.height_m (Room)
    val rotationDeg: Float,     // From beds.rotation_deg (Room)
    val hasActiveTasks: Boolean,  // Derived from today's tasks list
    val activeTasks: List<TaskPinData> = emptyList()
) {
    /** 4 isometric corners of this bed in world space. */
    val worldCorners: BedWorldCorners get() = BedWorldCorners(
        topLeft     = Offset(posX, posY),
        topRight    = Offset(posX + widthM, posY),
        bottomLeft  = Offset(posX, posY + heightM),
        bottomRight = Offset(posX + widthM, posY + heightM)
    )

    /** Center of bed in world space. */
    val worldCenter: Offset get() = Offset(posX + widthM / 2f, posY + heightM / 2f)

    /** All 4 corners in screen space. Used for hit testing and selection drawing. */
    fun screenCorners(camera: CameraState): BedScreenCorners {
        val w = worldCorners
        return BedScreenCorners(
            topLeft     = IsometricProjection.toScreen(w.topLeft.x,     w.topLeft.y,     camera),
            topRight    = IsometricProjection.toScreen(w.topRight.x,    w.topRight.y,    camera),
            bottomLeft  = IsometricProjection.toScreen(w.bottomLeft.x,  w.bottomLeft.y,  camera),
            bottomRight = IsometricProjection.toScreen(w.bottomRight.x, w.bottomRight.y, camera)
        )
    }

    /** Screen-space center of top edge — anchor for drag handle and status pins. */
    fun topEdgeCenter(camera: CameraState): Offset {
        val sc = screenCorners(camera)
        return Offset(
            x = (sc.topLeft.x + sc.topRight.x) / 2f,
            y = (sc.topLeft.y + sc.topRight.y) / 2f
        )
    }

    /** Screen-space center of the bed. Anchor for the green ⊕ action button. */
    fun centerScreen(camera: CameraState): Offset =
        IsometricProjection.toScreen(worldCenter.x, worldCenter.y, camera)

    /** Front-bottom center of the isometric bed. Anchor for the bed label chip. */
    fun labelAnchor(camera: CameraState): Offset =
        IsometricProjection.toScreen(posX + widthM / 2f, posY + heightM, camera)

    /** Status pin anchor — floats above the top edge of the bed. */
    fun pinAnchor(camera: CameraState): Offset {
        val top = topEdgeCenter(camera)
        return top.copy(y = top.y - PIN_FLOAT_OFFSET_DP * camera.zoom)
    }

    companion object {
        const val PIN_FLOAT_OFFSET_DP = 32f   // How high pins float above the bed top edge
    }
}

data class BedWorldCorners(
    val topLeft: Offset,
    val topRight: Offset,
    val bottomLeft: Offset,
    val bottomRight: Offset
)

data class BedScreenCorners(
    val topLeft: Offset,
    val topRight: Offset,
    val bottomLeft: Offset,
    val bottomRight: Offset
) {
    val topCenter: Offset    get() = Offset((topLeft.x + topRight.x) / 2f,       (topLeft.y + topRight.y) / 2f)
    val bottomCenter: Offset get() = Offset((bottomLeft.x + bottomRight.x) / 2f, (bottomLeft.y + bottomRight.y) / 2f)
    val leftMid: Offset      get() = Offset((topLeft.x + bottomLeft.x) / 2f,     (topLeft.y + bottomLeft.y) / 2f)
    val rightMid: Offset     get() = Offset((topRight.x + bottomRight.x) / 2f,   (topRight.y + bottomRight.y) / 2f)
}

// ─── TaskPinData ──────────────────────────────────────────────────────────

/**
 * Lightweight model for a status badge pin rendered above a bed.
 * Populated from TaskRepository.observeTodayTasks() — no static pins.
 */
data class TaskPinData(
    val taskId: String,
    val taskType: TaskType,   // Determines pin color and icon
    val bedId: String
)

// ─── HandlePositions ──────────────────────────────────────────────────────

/**
 * Screen-space positions of all selection handles for a selected bed.
 * Reported back to CanvasGestureHandler so gesture hit-tests know
 * which handle was touched.
 */
data class HandlePositions(
    val dragHandle: Offset,
    val deleteQuick: Offset,
    val cornerTL: Offset,
    val cornerTR: Offset,
    val cornerBL: Offset,
    val cornerBR: Offset,
    val midTop: Offset,
    val midBottom: Offset,
    val midLeft: Offset,
    val midRight: Offset,
    val actionBtn: Offset
)

// ─── GrowthStage import alias ─────────────────────────────────────────────
// (imported from domain.model — re-exported here for renderer package convenience)
typealias GrowthStage     = com.maptanim.app.domain.model.GrowthStage
typealias TaskType        = com.maptanim.app.domain.model.TaskType

// ─── CropZoneRenderData ──────────────────────────────────────────────────


data class CropZoneRenderData(
    val id: String,
    val bedId: String,
    val cropName: String?,
    val offsetX: Float,
    val offsetY: Float,
    val widthM: Float,
    val heightM: Float,
    val spacingM: Float,
    val plantInstances: List<PlantInstanceRender> = emptyList()
)

data class PlantInstanceRender(
    val worldX: Float,
    val worldY: Float,
    val scaleFactor: Float,
    val cropName: String
)

// ─── FarmObjectRenderData (trellis, fence, tree) ─────────────────────────

data class FarmObjectRenderData(
    val id: String,
    val objectType: FarmObjectType,
    val worldX: Float,
    val worldY: Float,
    val widthM: Float,
    val heightM: Float,
    val rotationDeg: Float = 0f,
    val attachedBedId: String? = null
)

