package com.maptanim.app.dss.evaluator

import com.maptanim.app.domain.model.Crop
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.HarvestRecord
import com.maptanim.app.domain.model.TaskType
import com.maptanim.app.dss.model.DssCategory
import com.maptanim.app.dss.model.DssDecision
import com.maptanim.app.dss.model.DssDecisionType
import com.maptanim.app.dss.model.DssInput
import com.maptanim.app.dss.model.DssPriority
import com.maptanim.app.dss.rules.DssRuleCatalog

/**
 * Evaluates crop rotation patterns to disrupt monoculture disease cycles.
 */
class RotationEvaluator {

    fun evaluate(input: DssInput): List<DssDecision> {
        val decisions = mutableListOf<DssDecision>()
        val today = input.farmerData.currentDate
        val farmId = input.session.farmId

        input.farmerData.plots.forEach { plot ->
            val crop = input.referenceData.crops.firstOrNull { it.name.equals(plot.cropName, ignoreCase = true) }
                ?: return@forEach

            // Check harvest history for this plot to see what was planted previously
            val pastHarvests = input.farmerData.harvestHistory.filter { it.plotId == plot.id }
            val lastHarvest = pastHarvests.maxByOrNull { it.harvestedAt } ?: return@forEach

            val prevCropName = lastHarvest.cropName
            val isSolanaceaeRepeat = isSameFamily(crop.name, prevCropName, "Solanaceae")
            val isCucurbitRepeat = isSameFamily(crop.name, prevCropName, "Cucurbitaceae")

            if (isSolanaceaeRepeat || isCucurbitRepeat) {
                val familyName = if (isSolanaceaeRepeat) "Solanaceae (Nightshade)" else "Cucurbitaceae (Gourd)"
                val rule = DssRuleCatalog.findRuleByCode("ROTATION_FAMILY_DISEASE_BREAK")
                decisions.add(
                    DssDecision(
                        id = "dss_rotation_repeat_${plot.id}",
                        farmId = farmId,
                        plotId = plot.id,
                        plotLabel = plot.plotLabel,
                        cropName = crop.name,
                        decisionType = DssDecisionType.ALERT,
                        category = DssCategory.CROP_ROTATION_FALLOW,
                        priority = DssPriority.HIGH,
                        title = "Consecutive $familyName Planting Detected on ${plot.plotLabel}",
                        summary = "Previous harvest was $prevCropName. Current planting is ${crop.name}. Rotate to Fabaceae (Beans) next cycle.",
                        explanation = rule?.explanation ?: "Monoculture of the same family causes exponential multiplication of soil-borne pathogens and nutrient exhaustion.",
                        source = rule?.source ?: "BPI Crop Rotation Protocol 2025: Disease Cycle Disruption",
                        actionText = "Plan Legume Rotation",
                        actionTaskType = TaskType.ROTATION_ALERT,
                        ruleId = rule?.id,
                        evaluatedAt = today.toString()
                    )
                )
            }
        }

        return decisions
    }

    private fun isSameFamily(cropA: String, cropB: String, family: String): Boolean {
        val solanaceae = listOf("Tomato", "Eggplant", "Chili Pepper")
        val cucurbits = listOf("Cucumber", "Squash", "Bitter Gourd")
        return when (family) {
            "Solanaceae" -> cropA in solanaceae && cropB in solanaceae
            "Cucurbitaceae" -> cropA in cucurbits && cropB in cucurbits
            else -> false
        }
    }
}

typealias DssRotationEvaluator = RotationEvaluator
