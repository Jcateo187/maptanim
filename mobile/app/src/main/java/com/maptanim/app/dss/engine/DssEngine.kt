package com.maptanim.app.dss.engine

import com.maptanim.app.domain.model.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit

// ─── GrowthStageCalculator ─────────────────────────────────────────────────

/**
 * Calculates the current growth stage for a plot's crop.
 *
 * Input:  crop_plots.planted_date (Room DB) + crops.days_to_harvest (Room DB)
 * Output: GrowthStage enum value
 */
class GrowthStageCalculator {

    fun calculate(plantedDate: LocalDate, daysToHarvest: Int, today: LocalDate): GrowthStage {
        val daysSincePlanting = ChronoUnit.DAYS.between(plantedDate, today).toInt()
            .coerceAtLeast(0)

        return when {
            daysSincePlanting < 7              -> GrowthStage.GERMINATION
            daysSincePlanting < 21             -> GrowthStage.EARLY_VEGETATIVE
            daysSincePlanting < 35             -> GrowthStage.MID_VEGETATIVE
            daysSincePlanting < 50             -> GrowthStage.FLOWERING
            daysSincePlanting < daysToHarvest  -> GrowthStage.FRUITING
            daysSincePlanting < daysToHarvest + 7 -> GrowthStage.HARVEST_READY
            else                               -> GrowthStage.OVERDUE
        }
    }
}

// ─── SoilSuitabilityScorer ─────────────────────────────────────────────────

/**
 * Scores how well a crop's soil requirements match the plot's actual soil type.
 * Returns a float 0.0–1.0 (0% = poor match, 100% = optimal).
 *
 * Input:  crop_plots.soil_type + crops.ideal_soils / suitable_soils / tolerated_soils (Room DB)
 */
class SoilSuitabilityScorer {

    fun score(soilType: SoilType, crop: Crop): Float = when {
        soilType in crop.idealSoils     -> 1.00f   // Optimal
        soilType in crop.suitableSoils  -> 0.75f   // Good
        soilType in crop.toleratedSoils -> 0.50f   // Marginal
        else                            -> 0.25f   // Poor match
    }
}

// ─── CompanionPlantsMatrix ─────────────────────────────────────────────────

/**
 * Evaluates companion planting relationships between all adjacent plots.
 *
 * Data source: dss_rules table in Room DB (synced from Supabase on first launch).
 * Adjacent plots = plots whose bounding boxes are within 1.5m of each other.
 */
class CompanionPlantsMatrix {

    fun evaluate(plots: List<CropPlot>, rules: List<DssRule>): List<CompanionAlert> {
        val alerts = mutableListOf<CompanionAlert>()

        val adjacentPairs = findAdjacentPlots(plots)

        adjacentPairs.forEach { (plotA, plotB) ->
            val cropA = plotA.cropName ?: return@forEach
            val cropB = plotB.cropName ?: return@forEach

            val rule = rules.firstOrNull { r ->
                (r.cropA == cropA && r.cropB == cropB) ||
                (r.cropA == cropB && r.cropB == cropA)
            } ?: return@forEach

            if (rule.relationship == CompanionRelation.ANTAGONIST) {
                alerts.add(
                    CompanionAlert(
                        plotALabel = plotA.plotLabel,
                        plotBLabel = plotB.plotLabel,
                        cropA = cropA,
                        cropB = cropB,
                        relationship = CompanionRelation.ANTAGONIST,
                        message = rule.notes ?: "$cropA and $cropB should not be planted adjacent."
                    )
                )
            }
        }

        return alerts
    }

    /** Plots within 1.5m of each other are considered adjacent. */
    private fun findAdjacentPlots(plots: List<CropPlot>): List<Pair<CropPlot, CropPlot>> {
        val pairs = mutableListOf<Pair<CropPlot, CropPlot>>()
        for (i in plots.indices) {
            for (j in i + 1 until plots.size) {
                val a = plots[i]
                val b = plots[j]
                val gapX = maxOf(0f, b.posX - (a.posX + a.widthM))
                    .coerceAtLeast(maxOf(0f, a.posX - (b.posX + b.widthM)))
                val gapY = maxOf(0f, b.posY - (a.posY + a.heightM))
                    .coerceAtLeast(maxOf(0f, a.posY - (b.posY + b.heightM)))
                if (gapX <= 1.5f && gapY <= 1.5f) {
                    pairs.add(Pair(a, b))
                }
            }
        }
        return pairs
    }
}

// ─── DSS Rule Domain Model ─────────────────────────────────────────────────

data class DssRule(
    val id: String,
    val cropA: String,
    val cropB: String,
    val relationship: CompanionRelation,
    val notes: String?
)

data class CompanionAlert(
    val plotALabel: String,
    val plotBLabel: String,
    val cropA: String,
    val cropB: String,
    val relationship: CompanionRelation,
    val message: String
)

data class SoilScore(
    val plotLabel: String,
    val soilType: SoilType,
    val score: Float?
)

// ─── DssEngine ────────────────────────────────────────────────────────────

class DssEngine(
    private val growthCalculator: GrowthStageCalculator = GrowthStageCalculator(),
    private val companionMatrix: CompanionPlantsMatrix = CompanionPlantsMatrix(),
    private val soilScorer: SoilSuitabilityScorer = SoilSuitabilityScorer()
) {
    data class DssResult(
        val tasks: List<GeneratedTask>,
        val companionAlerts: List<CompanionAlert>,
        val soilScores: List<SoilScore>
    )

    data class GeneratedTask(
        val plotId: String,
        val plotLabel: String,
        val cropName: String,
        val taskType: TaskType,
        val title: String,
        val subLabel: String,
        val dueDate: String
    )

    fun evaluate(
        plots: List<CropPlot>,
        crops: List<Crop>,
        rules: List<DssRule>,
        activities: List<Activity>,
        today: LocalDate
    ): DssResult {
        val tasks = plots.flatMap { plot ->
            val crop = crops.firstOrNull { it.name == plot.cropName } ?: return@flatMap emptyList()
            val plantedDate = plot.plantedDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: return@flatMap emptyList()
            val stage = growthCalculator.calculate(plantedDate, crop.daysToHarvest, today)
            generateTasksForPlot(plot, crop, stage, activities, today)
        }

        val companionAlerts = companionMatrix.evaluate(plots, rules)

        val soilScores = plots.map { plot ->
            val crop = crops.firstOrNull { it.name == plot.cropName }
            SoilScore(
                plotLabel = plot.plotLabel,
                soilType = plot.soilType,
                score = crop?.let { soilScorer.score(plot.soilType, it) }
            )
        }

        return DssResult(tasks, companionAlerts, soilScores)
    }

    private fun generateTasksForPlot(
        plot: CropPlot,
        crop: Crop,
        stage: GrowthStage,
        activities: List<Activity>,
        today: LocalDate
    ): List<GeneratedTask> {
        val generated = mutableListOf<GeneratedTask>()

        // ── WATER task ────────────────────────────────────────────────────
        val lastWatered = activities
            .filter { it.plotId == plot.id && it.type == TaskType.WATER }
            .mapNotNull { runCatching { LocalDate.parse(it.performedAt.take(10)) }.getOrNull() }
            .maxOrNull()

        val daysSinceWater = lastWatered?.let { ChronoUnit.DAYS.between(it, today).toInt() }
            ?: crop.wateringIntervalDays

        if (daysSinceWater >= crop.wateringIntervalDays) {
            generated.add(GeneratedTask(
                plotId = plot.id, plotLabel = plot.plotLabel, cropName = crop.name,
                taskType = TaskType.WATER,
                title = "Water ${plot.plotLabel}",
                subLabel = crop.name,
                dueDate = today.toString()
            ))
        }

        // ── FERTILIZE task ────────────────────────────────────────────────
        if (stage in listOf(GrowthStage.EARLY_VEGETATIVE, GrowthStage.MID_VEGETATIVE, GrowthStage.FLOWERING)) {
            val lastFertilized = activities
                .filter { it.plotId == plot.id && it.type == TaskType.FERTILIZE }
                .mapNotNull { runCatching { LocalDate.parse(it.performedAt.take(10)) }.getOrNull() }
                .maxOrNull()

            val daysSinceFert = lastFertilized?.let { ChronoUnit.DAYS.between(it, today).toInt() }
                ?: crop.fertilizeIntervalDays

            if (daysSinceFert >= crop.fertilizeIntervalDays) {
                generated.add(GeneratedTask(
                    plotId = plot.id, plotLabel = plot.plotLabel, cropName = crop.name,
                    taskType = TaskType.FERTILIZE,
                    title = "Fertilize ${crop.name}",
                    subLabel = plot.plotLabel,
                    dueDate = today.toString()
                ))
            }
        }

        // ── HARVEST task ──────────────────────────────────────────────────
        if (stage == GrowthStage.HARVEST_READY || stage == GrowthStage.OVERDUE) {
            generated.add(GeneratedTask(
                plotId = plot.id, plotLabel = plot.plotLabel, cropName = crop.name,
                taskType = TaskType.HARVEST,
                title = "Harvest ${crop.name}",
                subLabel = plot.plotLabel,
                dueDate = today.toString()
            ))
        }

        // ── PEST_ALERT task ───────────────────────────────────────────────
        val currentSeason = deriveSeason(today)
        val lastPestCheck = activities
            .filter { it.plotId == plot.id && it.type == TaskType.PEST_ALERT }
            .mapNotNull { runCatching { LocalDate.parse(it.performedAt.take(10)) }.getOrNull() }
            .maxOrNull()
        val daysSincePestCheck = lastPestCheck?.let { ChronoUnit.DAYS.between(it, today).toInt() } ?: 8

        if (currentSeason.name in crop.pestRiskSeason && daysSincePestCheck >= 7) {
            generated.add(GeneratedTask(
                plotId = plot.id, plotLabel = plot.plotLabel, cropName = crop.name,
                taskType = TaskType.PEST_ALERT,
                title = "Check Pest Alert",
                subLabel = crop.name,
                dueDate = today.toString()
            ))
        }

        return generated
    }

    private fun deriveSeason(date: LocalDate): Season {
        return when (date.monthValue) {
            in 5..9  -> Season.WET
            else     -> Season.DRY
        }
    }
}
