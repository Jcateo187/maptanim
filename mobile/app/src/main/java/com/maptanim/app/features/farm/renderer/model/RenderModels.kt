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
    val isActive: Boolean = true,
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
        isActive = isActive,
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
    val plantInstances: List<PlantInstanceRender> = emptyList(),
    val plantedDate: String? = null
)

data class PlantInstanceRender(
    val worldX: Float = 0f,
    val worldY: Float = 0f,
    val scaleFactor: Float = 1f,
    val cropName: String = "",
    val growthStage: Int = 1
)

const val INCH_IN_METERS = 0.0254f
const val SIX_INCHES_IN_METERS = 0.1524f // 6 inches = 0.5 foot (box size for small crops)
const val FOOT_IN_METERS = 0.3048f       // 12 inches = 1 foot
const val YARD_IN_METERS = 0.9144f       // 36 inches = 1 yard = 3 feet

/**
 * Real-life physical foliage/body diameter and occupied space of vegetables in meters.
 * Calibrated in inches, feet, and yards:
 * - Carrot / Radish / Garlic: 1 inch (0.0254m) = 1 box of 1" grid
 * - Onion: 3 inches (0.0762m) = 3 boxes
 * - Pechay / Bok Choy / Mustasa / Kangkong: 6 inches (0.1524m) = 6 boxes
 * - Lettuce / Spinach: 8 inches (0.2032m) = 8 boxes
 * - Chili / Pepper / Sitaw / Beans: 12 inches / 1 foot (0.3048m) = 12 boxes
 * - Cabbage / Broccoli / Cauliflower: 15 inches (0.381m) = 15 boxes
 * - Okra / Eggplant / Tomato / Cucumber / Bittergourd / Corn: 18 inches / 1.5 ft (0.4572m)
 * - Pumpkin / Squash / Kalabasa / Watermelon: 36 inches / 1 yard / 3 ft (0.9144m)
 */
fun realLifeCropDiameterM(cropName: String?): Float {
    val clean = cropName?.lowercase()?.replace(" ", "")?.replace("_", "")?.replace("-", "") ?: return FOOT_IN_METERS
    return when {
        clean.contains("carrot") || clean.contains("karot") -> INCH_IN_METERS // 1 inch (1 box of 1" grid)
        clean.contains("radish") || clean.contains("labanos") ||
        clean.contains("garlic") || clean.contains("bawang") -> INCH_IN_METERS // 1 inch (1 box of 1" grid)
        clean.contains("onion") || clean.contains("sibuyas") -> INCH_IN_METERS * 3f // 3 inches

        clean.contains("pechay") || clean.contains("bokchoy") || clean.contains("pakchoi") ||
        clean.contains("mustasa") || clean.contains("mustard") ||
        clean.contains("kangkong") || clean.contains("waterspinach") -> SIX_INCHES_IN_METERS // 6 inches (0.1524m)

        clean.contains("lettuce") || clean.contains("litsugas") ||
        clean.contains("spinach") -> INCH_IN_METERS * 8f // 8 inches (0.2032m)

        clean.contains("sili") || clean.contains("chili") || clean.contains("pepper") ||
        clean.contains("sitaw") || clean.contains("stringbean") || clean.contains("beans") -> FOOT_IN_METERS // 12 inches / 1 foot (0.3048m)

        clean.contains("cabbage") || clean.contains("repolyo") ||
        clean.contains("broccoli") || clean.contains("cauliflower") -> INCH_IN_METERS * 15f // 15 inches (0.381m)

        clean.contains("okra") ||
        clean.contains("eggplant") || clean.contains("talong") ||
        clean.contains("tomato") || clean.contains("kamatis") ||
        clean.contains("pipino") || clean.contains("cucumber") ||
        clean.contains("ampalaya") || clean.contains("bittergourd") ||
        clean.contains("corn") || clean.contains("mais") -> INCH_IN_METERS * 18f // 18 inches / 1.5 ft (0.4572m)

        clean.contains("kalabasa") || clean.contains("squash") ||
        clean.contains("pumpkin") || clean.contains("watermelon") ||
        clean.contains("pakwan") || clean.contains("melon") -> YARD_IN_METERS // 1 yard = 36 inches = 3 ft (0.9144m)

        else -> FOOT_IN_METERS // 1 foot (12 inches) default
    }
}

/**
 * Individual plant spacing for vegetable crop multi-plant generation within a zone.
 * Square Foot Gardening / DA-BPI recommended planting densities:
 * - Carrot, Radish, Garlic, Onion: 3 inches (16 plants per 1-foot zone)
 * - Pechay, Bok Choy, Mustard, Kangkong: 6 inches (4 plants per 1-foot zone)
 * - Lettuce, Spinach: 8 inches
 * - Chili, Pepper, Bush Beans: 12 inches / 1 foot
 * - Cabbage, Broccoli: 15 inches
 * - Tomato, Eggplant, Okra, Cucumber, Bittergourd, Corn: 18 inches / 1.5 ft
 * - Squash, Kalabasa, Pumpkin, Watermelon: 36 inches / 1 yard
 */
fun cropSinglePlantSpacingM(cropName: String?): Float {
    val clean = cropName?.lowercase()?.replace(" ", "")?.replace("_", "")?.replace("-", "") ?: return FOOT_IN_METERS
    return when {
        clean.contains("carrot") || clean.contains("karot") ||
        clean.contains("radish") || clean.contains("labanos") ||
        clean.contains("garlic") || clean.contains("bawang") ||
        clean.contains("onion") || clean.contains("sibuyas") -> INCH_IN_METERS * 3f // 3 inches

        clean.contains("pechay") || clean.contains("bokchoy") || clean.contains("pakchoi") ||
        clean.contains("mustasa") || clean.contains("mustard") ||
        clean.contains("kangkong") || clean.contains("waterspinach") -> SIX_INCHES_IN_METERS // 6 inches

        clean.contains("lettuce") || clean.contains("litsugas") ||
        clean.contains("spinach") -> INCH_IN_METERS * 8f // 8 inches

        clean.contains("sili") || clean.contains("chili") || clean.contains("pepper") ||
        clean.contains("sitaw") || clean.contains("stringbean") || clean.contains("beans") -> FOOT_IN_METERS // 12 inches / 1 foot

        clean.contains("cabbage") || clean.contains("repolyo") ||
        clean.contains("broccoli") || clean.contains("cauliflower") -> INCH_IN_METERS * 15f // 15 inches

        clean.contains("okra") ||
        clean.contains("eggplant") || clean.contains("talong") ||
        clean.contains("tomato") || clean.contains("kamatis") ||
        clean.contains("pipino") || clean.contains("cucumber") ||
        clean.contains("ampalaya") || clean.contains("bittergourd") ||
        clean.contains("corn") || clean.contains("mais") -> INCH_IN_METERS * 18f // 18 inches (1.5 ft)

        clean.contains("kalabasa") || clean.contains("squash") ||
        clean.contains("pumpkin") || clean.contains("watermelon") ||
        clean.contains("pakwan") || clean.contains("melon") -> YARD_IN_METERS // 36 inches (3 ft / 1 yd)

        else -> FOOT_IN_METERS
    }
}


