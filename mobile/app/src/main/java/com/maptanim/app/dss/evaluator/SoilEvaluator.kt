package com.maptanim.app.dss.evaluator

import com.maptanim.app.domain.model.Crop
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.SoilType
import com.maptanim.app.domain.model.TaskType
import com.maptanim.app.dss.engine.SoilSuitabilityScorer
import com.maptanim.app.dss.model.DssCategory
import com.maptanim.app.dss.model.DssDecision
import com.maptanim.app.dss.model.DssDecisionType
import com.maptanim.app.dss.model.DssInput
import com.maptanim.app.dss.model.DssPriority
import com.maptanim.app.dss.rules.DssRuleCatalog

/**
 * Evaluates soil compatibility with crops based on soil type and published BSWM / DA-BPI trials.
 */
class SoilEvaluator(
    private val scorer: SoilSuitabilityScorer = SoilSuitabilityScorer()
) {
    fun evaluate(input: DssInput): List<DssDecision> {
        val decisions = mutableListOf<DssDecision>()
        val today = input.farmerData.currentDate
        val farmId = input.session.farmId

        input.farmerData.plots.forEach { plot ->
            val crop = input.referenceData.crops.firstOrNull { it.name.equals(plot.cropName, ignoreCase = true) }
                ?: return@forEach

            val score = scorer.score(plot.soilType, crop)
            val isTomato = crop.name.equals("Tomato", ignoreCase = true) || crop.name.equals("Kamatis", ignoreCase = true)

            val dbSoil = input.referenceData.soilCompatibilities.firstOrNull {
                it.cropName.equals(crop.name, ignoreCase = true) &&
                (it.soilType.equals(plot.soilType.name, ignoreCase = true) ||
                 it.soilType.replace(" ", "_").equals(plot.soilType.name, ignoreCase = true))
            }

            if (dbSoil != null) {
                if (dbSoil.suitabilityRating == "POOR" || dbSoil.suitabilityRating == "MARGINAL" || dbSoil.suitabilityScore < 0.65f) {
                    val isCritical = plot.soilType == SoilType.CLAY && dbSoil.alertWarning != null
                    decisions.add(
                        DssDecision(
                            id = "dss_soil_alert_${plot.id}",
                            farmId = farmId,
                            plotId = plot.id,
                            plotLabel = plot.plotLabel,
                            cropName = crop.name,
                            decisionType = if (isCritical) DssDecisionType.ALERT else DssDecisionType.RECOMMENDATION,
                            category = DssCategory.SOIL_COMPATIBILITY,
                            priority = if (isCritical) DssPriority.CRITICAL else DssPriority.HIGH,
                            title = if (isCritical) "Soil Hazard: ${plot.soilType.name} Alert for ${crop.name}" else "Soil Amendment Needed for ${crop.name} on ${plot.plotLabel}",
                            summary = dbSoil.alertWarning ?: dbSoil.amendmentAction ?: "Amend ${plot.soilType.name} soil with compost to meet ${crop.name} root aeration requirements.",
                            explanation = dbSoil.agronomicRationale,
                            source = dbSoil.sourceCitation,
                            actionText = "Apply Soil Amendment",
                            actionTaskType = TaskType.SOIL_AMENDMENT,
                            evaluatedAt = today.toString()
                        )
                    )
                } else if (dbSoil.suitabilityRating == "OPTIMAL" || dbSoil.suitabilityScore >= 0.85f) {
                    decisions.add(
                        DssDecision(
                            id = "dss_soil_opt_${plot.id}",
                            farmId = farmId,
                            plotId = plot.id,
                            plotLabel = plot.plotLabel,
                            cropName = crop.name,
                            decisionType = DssDecisionType.RECOMMENDATION,
                            category = DssCategory.SOIL_COMPATIBILITY,
                            priority = DssPriority.INFO,
                            title = "Optimal Soil Match: ${plot.soilType.name} on ${plot.plotLabel}",
                            summary = dbSoil.amendmentAction ?: "${plot.soilType.name} provides optimal aeration and moisture balance for ${crop.name}.",
                            explanation = dbSoil.agronomicRationale ?: "Natural ${plot.soilType.name} texture supports healthy root expansion and steady nutrient uptake.",
                            source = dbSoil.sourceCitation ?: "Bureau of Soils and Water Management (BSWM) Standards 2026",
                            actionText = "Maintain Organic Topdressing",
                            actionTaskType = TaskType.SOIL_AMENDMENT,
                            evaluatedAt = today.toString()
                        )
                    )
                }
            } else if (score <= 0.50f) {
                val rule = if (plot.soilType == SoilType.CLAY) {
                    DssRuleCatalog.findRuleByCode("SOIL_CLAY_AERATION")
                } else {
                    DssRuleCatalog.findRuleByCode("SOIL_SANDY_MULCH")
                }

                decisions.add(
                    DssDecision(
                        id = "dss_soil_${plot.id}",
                        farmId = farmId,
                        plotId = plot.id,
                        plotLabel = plot.plotLabel,
                        cropName = crop.name,
                        decisionType = DssDecisionType.RECOMMENDATION,
                        category = DssCategory.SOIL_COMPATIBILITY,
                        priority = if (score < 0.3f) DssPriority.HIGH else DssPriority.MEDIUM,
                        title = "Soil Amendment Prescribed: ${plot.soilType.name} Match (${(score * 100).toInt()}%)",
                        summary = rule?.recommendation ?: "Apply organic compost and mulch to adjust soil drainage and aeration.",
                        explanation = rule?.explanation ?: "The plot's soil physical properties deviate from ${crop.name}'s optimal root aeration and moisture parameters.",
                        source = rule?.source ?: "Bureau of Soils and Water Management (BSWM) Guidelines",
                        actionText = "Apply Soil Amendment",
                        actionTaskType = TaskType.SOIL_AMENDMENT,
                        ruleId = rule?.id,
                        evaluatedAt = today.toString()
                    )
                )
            }
        }

        return decisions
    }
}

typealias DssSoilEvaluator = SoilEvaluator
