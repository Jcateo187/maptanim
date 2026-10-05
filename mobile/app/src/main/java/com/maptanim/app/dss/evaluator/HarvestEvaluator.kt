package com.maptanim.app.dss.evaluator

import com.maptanim.app.domain.model.Crop
import com.maptanim.app.domain.model.CropGrowthStage
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.TaskType
import com.maptanim.app.dss.engine.GrowthStageCalculator
import com.maptanim.app.dss.model.DssCategory
import com.maptanim.app.dss.model.DssDecision
import com.maptanim.app.dss.model.DssDecisionType
import com.maptanim.app.dss.model.DssInput
import com.maptanim.app.dss.model.DssPriority
import com.maptanim.app.dss.rules.DssRuleCatalog
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Evaluates harvest readiness and maturity window based on days to harvest and indicators.
 */
class HarvestEvaluator(
    private val calculator: GrowthStageCalculator = GrowthStageCalculator()
) {
    fun evaluate(input: DssInput): List<DssDecision> {
        val decisions = mutableListOf<DssDecision>()
        val today = input.farmerData.currentDate
        val farmId = input.session.farmId

        input.farmerData.plots.forEach { plot ->
            val crop = input.referenceData.crops.firstOrNull { it.name.equals(plot.cropName, ignoreCase = true) }
                ?: return@forEach
            val plantedDateStr = plot.plantedDate ?: return@forEach
            val plantedDate = runCatching { LocalDate.parse(plantedDateStr.take(10)) }.getOrNull()
                ?: return@forEach

            val stage = calculator.calculate(plantedDate, crop.daysToHarvest, today)
            val daysElapsed = ChronoUnit.DAYS.between(plantedDate, today).toInt().coerceAtLeast(0)

            val isOverdue = crop.daysToHarvest > 0 && daysElapsed > crop.daysToHarvest
            if (isOverdue) {
                val rule = DssRuleCatalog.findRuleByCode("HARVEST_OVERDUE_DEPRECIATION")
                decisions.add(
                    DssDecision(
                        id = "dss_harvest_overdue_${plot.id}",
                        farmId = farmId,
                        plotId = plot.id,
                        plotLabel = plot.plotLabel,
                        cropName = crop.name,
                        decisionType = DssDecisionType.ALERT,
                        category = DssCategory.HARVEST_READINESS,
                        priority = DssPriority.CRITICAL,
                        title = "URGENT: ${crop.name} Overdue for Harvest on ${plot.plotLabel}",
                        summary = "${crop.name} is at day $daysElapsed (target maturity: ${crop.daysToHarvest} days). Harvest immediately.",
                        explanation = rule?.explanation ?: "Over-mature crops undergo rapid lignification, bitterness development, and fruit cracking under field heat.",
                        source = rule?.source ?: "DA-BPI Post-Harvest Handling & Quality Preservation Guidelines",
                        actionText = "Harvest Now",
                        actionTaskType = TaskType.HARVEST,
                        ruleId = rule?.id,
                        evaluatedAt = today.toString()
                    )
                )
            } else if (stage == CropGrowthStage.HARVEST) {
                val rule = DssRuleCatalog.findRuleByCode("HARVEST_OPTIMAL_WINDOW")
                val indicators = crop.harvestIndicators ?: "Fruit reaches standard size and firm coloration"
                decisions.add(
                    DssDecision(
                        id = "dss_harvest_ready_${plot.id}",
                        farmId = farmId,
                        plotId = plot.id,
                        plotLabel = plot.plotLabel,
                        cropName = crop.name,
                        decisionType = DssDecisionType.TASK,
                        category = DssCategory.HARVEST_READINESS,
                        priority = DssPriority.HIGH,
                        title = "Harvest Ready: ${crop.name} on ${plot.plotLabel}",
                        summary = "Days elapsed: $daysElapsed / ${crop.daysToHarvest} days. Check indicators: $indicators.",
                        explanation = rule?.explanation ?: "Early morning harvest minimizes post-harvest respiration and retains peak sweetness and market quality.",
                        source = rule?.source ?: "Philippine National Standards (PNS) for Fresh Produce",
                        actionText = "Record Harvest",
                        actionTaskType = TaskType.HARVEST,
                        ruleId = rule?.id,
                        evaluatedAt = today.toString()
                    )
                )
            }
        }

        return decisions
    }
}

typealias DssHarvestEvaluator = HarvestEvaluator
