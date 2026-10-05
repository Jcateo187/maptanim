package com.maptanim.app.features.farm.renderer.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.SoilType



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
    val plantedDate: String? = null,
    val daysPlanted: Int = 0,
    val daysToHarvest: Int = 60,
    val stageProgressRatio: Float = 0f,
    val plantedTimestampMs: Long = 0L,
    val activeTasks: List<TaskPinData> = emptyList()
) {
    val currentStageProgressRatio: Float get() = stageProgressRatio

    val growthStage: Int get() {
        val ratio = currentStageProgressRatio
        return when {
            ratio < 0.20f -> 1
            ratio < 0.40f -> 2
            ratio < 0.60f -> 3
            ratio < 0.80f -> 4
            else -> 5
        }
    }

    val isHarvestReady: Boolean get() =
        isMonitoringStarted && (growthStage == 5 || currentStageProgressRatio >= 0.90f || (daysToHarvest > 0 && daysPlanted >= daysToHarvest))

    val isHarvestOverdue: Boolean get() =
        isMonitoringStarted && daysToHarvest > 0 && daysPlanted > daysToHarvest

    val worldCenter: Offset get() = Offset(posX + widthM / 2f, posY + heightM / 2f)
}

fun CropPlot.toRenderData(activeTasks: List<TaskPinData> = emptyList()): PlotRenderData {
    val isStarted = !plantedDate.isNullOrBlank()
    var elapsedDays = 0
    var plantedMs = 0L

    if (isStarted) {
        try {
            val dateTime = java.time.ZonedDateTime.parse(plantedDate)
            plantedMs = dateTime.toInstant().toEpochMilli()
        } catch (e: Exception) {
            try {
                val date = java.time.LocalDate.parse(plantedDate!!.take(10))
                plantedMs = date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            } catch (e2: Exception) {
                plantedMs = System.currentTimeMillis()
            }
        }
        try {
            val date = java.time.LocalDate.parse(plantedDate!!.take(10))
            elapsedDays = java.time.temporal.ChronoUnit.DAYS.between(date, java.time.LocalDate.now()).toInt().coerceAtLeast(0)
        } catch (e: Exception) {
            elapsedDays = 0
        }
    } else {
        plantedMs = System.currentTimeMillis()
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
        plantedDate = plantedDate,
        daysPlanted = elapsedDays,
        daysToHarvest = defaultDays,
        stageProgressRatio = progressRatio,
        plantedTimestampMs = plantedMs,
        activeTasks = activeTasks
    )
}



// ─── TaskPinData ──────────────────────────────────────────────────────────

data class TaskPinData(
    val taskId: String,
    val taskType: TaskType,
    val plotId: String
)

typealias CropGrowthStage = com.maptanim.app.domain.model.CropGrowthStage
typealias TaskType    = com.maptanim.app.domain.model.TaskType

// ─── CropZoneRenderData ──────────────────────────────────────────────────

data class CropZoneRenderData(
    val id: String,
    val plotId: String,
    val cropName: String?,
    val cropId: String? = null,
    val offsetX: Float,
    val offsetY: Float,
    val widthM: Float = 1.0f,
    val heightM: Float = 1.0f,
    val spacingM: Float = 0.5f,
    val growthStage: Int = 1,
    val plantInstances: List<PlantInstanceRender> = emptyList()
)

data class PlantInstanceRender(
    val worldX: Float = 0f,
    val worldY: Float = 0f,
    val scaleFactor: Float = 1f,
    val cropName: String = "",
    val growthStage: Int = 1
)

