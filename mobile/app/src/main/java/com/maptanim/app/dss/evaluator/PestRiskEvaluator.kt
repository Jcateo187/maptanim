package com.maptanim.app.dss.evaluator

import com.maptanim.app.domain.model.Crop
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.TaskType
import com.maptanim.app.dss.model.DssCategory
import com.maptanim.app.dss.model.DssDecision
import com.maptanim.app.dss.model.DssDecisionType
import com.maptanim.app.dss.model.DssInput
import com.maptanim.app.dss.model.DssPriority
import com.maptanim.app.dss.rules.DssRuleCatalog

/**
 * Evaluates pest risk indicators and biological/botanical pest interventions.
 */
class PestRiskEvaluator {

    fun evaluate(input: DssInput): List<DssDecision> {
        val decisions = mutableListOf<DssDecision>()
        val today = input.farmerData.currentDate
        val farmId = input.session.farmId
        val currentSeason = if (today.monthValue in 5..10) "WET" else "DRY"

        input.farmerData.plots.forEach { plot ->
            val crop = input.referenceData.crops.firstOrNull { it.name.equals(plot.cropName, ignoreCase = true) }
                ?: return@forEach

            // Check if active pest observations were logged by farmer in activity logs
            val plotActivities = input.farmerData.recentActivities.filter { it.plotId == plot.id }
            val latestPestActivity = plotActivities.firstOrNull { act ->
                !act.notes.isNullOrBlank() &&
                    (act.notes.contains("pest", ignoreCase = true) ||
                     act.notes.contains("insekto", ignoreCase = true) ||
                     act.notes.contains("aphid", ignoreCase = true) ||
                     act.notes.contains("borer", ignoreCase = true) ||
                     act.notes.contains("uod", ignoreCase = true) ||
                     act.notes.contains("dilaw", ignoreCase = true))
            }

            if (latestPestActivity != null) {
                val rule = DssRuleCatalog.findRuleByCode("PEST_APHID_CONTROL")
                decisions.add(
                    DssDecision(
                        id = "dss_pest_active_${plot.id}",
                        farmId = farmId,
                        plotId = plot.id,
                        plotLabel = plot.plotLabel,
                        cropName = crop.name,
                        decisionType = DssDecisionType.ALERT,
                        category = DssCategory.PEST_DISEASE,
                        priority = DssPriority.CRITICAL,
                        title = "Active Pest Symptom Reported on ${plot.plotLabel}",
                        summary = "Observation note: \"${latestPestActivity.notes}\". Prepare biological control immediately.",
                        explanation = rule?.explanation ?: "Early physical or botanical control (Neem spray, botanical extract) stops the pest exponential reproductive cycle before severe defoliation.",
                        source = rule?.source ?: "DA-BPI National Organic Agriculture Program Technical Guide",
                        actionText = "Apply Organic Spray",
                        actionTaskType = TaskType.APPLY_PESTICIDE,
                        ruleId = rule?.id,
                        evaluatedAt = today.toString()
                    )
                )
            } else if (currentSeason in crop.pestRiskSeason) {
                // Seasonal high-risk check
                decisions.add(
                    DssDecision(
                        id = "dss_pest_risk_${plot.id}",
                        farmId = farmId,
                        plotId = plot.id,
                        plotLabel = plot.plotLabel,
                        cropName = crop.name,
                        decisionType = DssDecisionType.RECOMMENDATION,
                        category = DssCategory.PEST_DISEASE,
                        priority = DssPriority.MEDIUM,
                        title = "${crop.name} Seasonal Pest Watch ($currentSeason)",
                        summary = "Common pests during $currentSeason: ${crop.commonPests.take(3).joinToString(", ")}.",
                        explanation = "Warmer or wetter microclimates create ideal incubation conditions for piercing-sucking insect populations.",
                        source = "DA-BPI Pest & Disease Early Warning Bulletin 2026",
                        actionText = "Perform Field Inspection",
                        actionTaskType = TaskType.PEST_ALERT,
                        evaluatedAt = today.toString()
                    )
                )
            }
        }

        return decisions
    }
}

typealias DssPestDiseaseEvaluator = PestRiskEvaluator
