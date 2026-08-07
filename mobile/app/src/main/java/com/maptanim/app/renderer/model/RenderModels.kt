package com.maptanim.app.renderer.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.maptanim.app.domain.model.CropPlot
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
    val zoom: Float = 0.52f,     // Scale factor — default 0.52f provides a clear, zoomed-in view
    val minZoom: Float = 0.30f,  // Dynamic minZoom floor allows smooth zoom out
    val maxZoom: Float = 4.0f    // Max zoom in (400%)
) {
    /**
     * Clamps zoom and ensures camera pan offset stays within valid map boundaries.
     * Calculates dynamic minZoom based on screen width/height so the entire 45x45 farm
     * diamond (2880px x 1440px at 1.0 zoom) fits without edge clipping on any screen orientation.
     */
    fun clampZoom(newZoom: Float, screenWidth: Float = 1920f, screenHeight: Float = 1080f): CameraState {
        val effectiveMinZoom = if (screenWidth > 0f && screenHeight > 0f) {
            val fitW = screenWidth / (45f * IsometricProjection.TILE_W)
            val fitH = screenHeight / (45f * IsometricProjection.TILE_H)
            minOf(fitW, fitH).coerceIn(0.25f, 0.52f)
        } else {
            minZoom
        }

        val clampedZ = newZoom.coerceIn(effectiveMinZoom, maxZoom)
        val centerPanX = if (screenWidth > 0f) screenWidth / 2f else 960f
        val centerPanY = if (screenHeight > 0f) (screenHeight / 2f) - (45f * (IsometricProjection.TILE_H / 2f) * clampedZ) else 150f

        if (clampedZ <= effectiveMinZoom * 1.001f) {
            return copy(zoom = clampedZ, panX = centerPanX, panY = centerPanY, minZoom = effectiveMinZoom)
        }

        val zoomRatio = ((clampedZ / effectiveMinZoom) - 1.0f).coerceAtLeast(0f)
        val halfMapW = 65f * (IsometricProjection.TILE_W / 2f) * effectiveMinZoom
        val halfMapH = 65f * (IsometricProjection.TILE_H / 2f) * effectiveMinZoom

        val maxPanH = halfMapW * zoomRatio
        val maxPanV = halfMapH * zoomRatio

        val clampedPanX = panX.coerceIn(centerPanX - maxPanH, centerPanX + maxPanH)
        val clampedPanY = panY.coerceIn(centerPanY - maxPanV, centerPanY + maxPanV)

        return copy(zoom = clampedZ, panX = clampedPanX, panY = clampedPanY, minZoom = effectiveMinZoom)
    }

    /**
     * Strict Bounded Camera Panning:
     * - At max zoom out (zoom <= minZoom * 1.001f), panning is 100% locked to center (cannot pan past edges).
     * - When zoomed in (zoom > minZoom), panning is allowed but strictly clamped so the camera
     *   cannot pan outside the outer limits visible at max zoom out.
     */
    fun pan(dx: Float, dy: Float, screenWidth: Float = 1920f, screenHeight: Float = 1080f): CameraState {
        val centerPanX = if (screenWidth > 0f) screenWidth / 2f else 960f
        val centerPanY = if (screenHeight > 0f) (screenHeight / 2f) - (45f * (IsometricProjection.TILE_H / 2f) * zoom) else 150f

        // At max zoom out, lock camera completely to center
        if (zoom <= minZoom * 1.001f) {
            return copy(panX = centerPanX, panY = centerPanY)
        }

        // When zoomed in, allow panning strictly within the max-zoom-out bounding frame
        val zoomRatio = ((zoom / minZoom) - 1.0f).coerceAtLeast(0f)
        val halfMapW = 65f * (IsometricProjection.TILE_W / 2f) * minZoom
        val halfMapH = 65f * (IsometricProjection.TILE_H / 2f) * minZoom

        val maxPanH = halfMapW * zoomRatio
        val maxPanV = halfMapH * zoomRatio

        val minPanX = centerPanX - maxPanH
        val maxPanX = centerPanX + maxPanH
        val minPanY = centerPanY - maxPanV
        val maxPanY = centerPanY + maxPanV

        return copy(
            panX = (panX + dx).coerceIn(minPanX, maxPanX),
            panY = (panY + dy).coerceIn(minPanY, maxPanY)
        )
    }

    /** Centers world coordinate (22.5, 22.5) dead-center on screen. */
    fun centered(screenWidth: Float, screenHeight: Float): CameraState {
        val effectiveMinZoom = if (screenWidth > 0f && screenHeight > 0f) {
            val fitW = screenWidth / (45f * IsometricProjection.TILE_W)
            val fitH = screenHeight / (45f * IsometricProjection.TILE_H)
            minOf(fitW, fitH).coerceIn(0.25f, 0.52f)
        } else {
            minZoom
        }
        val targetZoom = zoom.coerceIn(effectiveMinZoom, maxZoom)
        val centerPanX = screenWidth / 2f
        val centerPanY = (screenHeight / 2f) - (45f * (IsometricProjection.TILE_H / 2f) * targetZoom)
        return copy(zoom = targetZoom, panX = centerPanX, panY = centerPanY, minZoom = effectiveMinZoom)
    }

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
 */
object IsometricProjection {
    const val TILE_W = 64f
    const val TILE_H = 32f

    fun toScreen(worldX: Float, worldY: Float, camera: CameraState): Offset {
        val screenX = (worldX - worldY) * (TILE_W / 2f) * camera.zoom + camera.panX
        val screenY = (worldX + worldY) * (TILE_H / 2f) * camera.zoom + camera.panY
        return Offset(screenX, screenY)
    }

    fun toWorld(screenX: Float, screenY: Float, camera: CameraState): Offset {
        val unpannedX = (screenX - camera.panX) / camera.zoom
        val unpannedY = (screenY - camera.panY) / camera.zoom
        val worldX = (unpannedX / TILE_W) + (unpannedY / TILE_H)
        val worldY = (unpannedY / TILE_H) - (unpannedX / TILE_W)
        return Offset(worldX, worldY)
    }
}

// ─── PlotRenderData ───────────────────────────────────────────────────────

data class PlotRenderData(
    val id: String,
    val farmId: String,
    val plotLabel: String,
    val cropName: String?,
    val cropId: String?,
    val cropVariety: String? = null,
    val soilType: SoilType,
    val posX: Float,
    val posY: Float,
    val widthM: Float,
    val heightM: Float,
    val rotationDeg: Float = 0f,
    val isMonitoringStarted: Boolean = false,
    val daysPlanted: Int = 0,
    val daysToHarvest: Int = 60,
    val stageProgressRatio: Float = 0f,
    val activeTasks: List<TaskPinData> = emptyList()
) {
    val worldCenter: Offset get() = Offset(posX + widthM / 2f, posY + heightM / 2f)

    fun worldCorners(): PlotWorldCorners = PlotWorldCorners(
        topLeft     = Offset(posX,          posY),
        topRight    = Offset(posX + widthM, posY),
        bottomLeft  = Offset(posX,          posY + heightM),
        bottomRight = Offset(posX + widthM, posY + heightM)
    )

    fun screenCorners(camera: CameraState): PlotScreenCorners {
        val w = worldCorners()
        return PlotScreenCorners(
            topLeft     = IsometricProjection.toScreen(w.topLeft.x,     w.topLeft.y,     camera),
            topRight    = IsometricProjection.toScreen(w.topRight.x,    w.topRight.y,    camera),
            bottomLeft  = IsometricProjection.toScreen(w.bottomLeft.x,  w.bottomLeft.y,  camera),
            bottomRight = IsometricProjection.toScreen(w.bottomRight.x, w.bottomRight.y, camera)
        )
    }

    fun topEdgeCenter(camera: CameraState): Offset {
        val sc = screenCorners(camera)
        return Offset(
            x = (sc.topLeft.x + sc.topRight.x) / 2f,
            y = (sc.topLeft.y + sc.topRight.y) / 2f
        )
    }

    fun centerScreen(camera: CameraState): Offset =
        IsometricProjection.toScreen(worldCenter.x, worldCenter.y, camera)

    fun labelAnchor(camera: CameraState): Offset =
        IsometricProjection.toScreen(posX + widthM / 2f, posY + heightM, camera)

    fun pinAnchor(camera: CameraState): Offset {
        val top = topEdgeCenter(camera)
        return top.copy(y = top.y - PIN_FLOAT_OFFSET_DP * camera.zoom)
    }

    companion object {
        const val PIN_FLOAT_OFFSET_DP = 32f
    }
}

fun CropPlot.toRenderData(activeTasks: List<TaskPinData> = emptyList()): PlotRenderData {
    val isStarted = !plantedDate.isNullOrBlank()
    var elapsedDays = 0
    if (isStarted) {
        try {
            val date = java.time.LocalDate.parse(plantedDate!!.take(10))
            elapsedDays = java.time.temporal.ChronoUnit.DAYS.between(date, java.time.LocalDate.now()).toInt().coerceAtLeast(0)
        } catch (e: Exception) {
            elapsedDays = 0
        }
    }
    val defaultDays = when (cropName?.lowercase() ?: "") {
        "pechay" -> 28
        "okra" -> 45
        "sitaw", "stringbeans" -> 50
        "ampalaya" -> 55
        "kamatis", "tomato" -> 60
        "repolyo", "cabbage" -> 60
        "mais", "corn" -> 65
        "sili", "chili" -> 65
        "talong", "eggplant" -> 75
        "kalabasa", "pumpkin" -> 80
        "karots", "carrot" -> 85
        "sibuyas", "onion" -> 100
        else -> 60
    }
    val progressRatio = if (isStarted && defaultDays > 0) (elapsedDays.toFloat() / defaultDays.toFloat()).coerceIn(0f, 1f) else 0f

    return PlotRenderData(
        id = id,
        farmId = farmId,
        plotLabel = plotLabel,
        cropName = cropName,
        cropId = cropId,
        cropVariety = cropVariety,
        soilType = soilType,
        posX = posX,
        posY = posY,
        widthM = widthM,
        heightM = heightM,
        rotationDeg = rotationDeg,
        isMonitoringStarted = isStarted,
        daysPlanted = elapsedDays,
        daysToHarvest = defaultDays,
        stageProgressRatio = progressRatio,
        activeTasks = activeTasks
    )
}

data class HandlePositions(
    val dragHandle: Offset = Offset.Zero,
    val deleteQuick: Offset = Offset.Zero,
    val cornerTL: Offset = Offset.Zero,
    val cornerTR: Offset = Offset.Zero,
    val cornerBL: Offset = Offset.Zero,
    val cornerBR: Offset = Offset.Zero,
    val midTop: Offset = Offset.Zero,
    val midBottom: Offset = Offset.Zero,
    val midLeft: Offset = Offset.Zero,
    val midRight: Offset = Offset.Zero,
    val actionBtn: Offset = Offset.Zero
)

data class PlotWorldCorners(
    val topLeft: Offset,
    val topRight: Offset,
    val bottomLeft: Offset,
    val bottomRight: Offset
)

data class PlotScreenCorners(
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

data class TaskPinData(
    val taskId: String,
    val taskType: TaskType,
    val plotId: String
)

typealias GrowthStage = com.maptanim.app.domain.model.GrowthStage
typealias TaskType    = com.maptanim.app.domain.model.TaskType

// ─── CropZoneRenderData ──────────────────────────────────────────────────

data class CropZoneRenderData(
    val id: String,
    val plotId: String,
    val cropName: String?,
    val offsetX: Float,
    val offsetY: Float,
    val widthM: Float,
    val heightM: Float,
    val spacingM: Float,
    val growthStage: Int = 1,
    val plantInstances: List<PlantInstanceRender> = emptyList()
)

data class PlantInstanceRender(
    val worldX: Float,
    val worldY: Float,
    val scaleFactor: Float,
    val cropName: String,
    val growthStage: Int = 1
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
    val attachedPlotId: String? = null
)
