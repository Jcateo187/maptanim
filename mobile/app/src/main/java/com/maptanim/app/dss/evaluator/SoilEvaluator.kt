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
                it.soilType.equals(plot.soilType.name, ignoreCase = true)
            }

            if (isTomato) {
                when (plot.soilType) {
                    SoilType.CLAY -> {
                        decisions.add(
                            DssDecision(
                                id = "dss_soil_clay_alert_${plot.id}",
                                farmId = farmId,
                                plotId = plot.id,
                                plotLabel = plot.plotLabel,
                                cropName = crop.name,
                                decisionType = DssDecisionType.ALERT,
                                category = DssCategory.SOIL_COMPATIBILITY,
                                priority = DssPriority.CRITICAL,
                                title = "Heavy Clay Waterlogging & Bacterial Wilt Alert (${plot.plotLabel})",
                                summary = dbSoil?.alertWarning ?: "Tomato in heavy clay soil risks rapid bacterial wilting (Ralstonia). Elevate beds by 25–30 cm and mix carbonized rice hull.",
                                explanation = dbSoil?.agronomicRationale ?: "Published DA-BPI trials demonstrate that saturated clay suffocates solanaceous roots within 48 hours, facilitating vascular infection. Incorporating carbonized rice hull (CRH) at 1:1 with compost restores internal porosity.",
                                source = dbSoil?.sourceCitation ?: "DA-BPI Technical Bulletin No. 14 & BSWM Soil Management",
                                actionText = "Build Raised Furrows & Add CRH",
                                actionTaskType = TaskType.SOIL_AMENDMENT,
                                evaluatedAt = today.toString()
                            )
                        )
                    }
                    SoilType.SANDY -> {
                        decisions.add(
                            DssDecision(
                                id = "dss_soil_sandy_mulch_${plot.id}",
                                farmId = farmId,
                                plotId = plot.id,
                                plotLabel = plot.plotLabel,
                                cropName = crop.name,
                                decisionType = DssDecisionType.RECOMMENDATION,
                                category = DssCategory.SOIL_COMPATIBILITY,
                                priority = DssPriority.MEDIUM,
                                title = "Organic Mulch Recommendation (Camiguin Study Citation)",
                                summary = dbSoil?.amendmentAction ?: "Apply 5 cm sawdust or rice straw mulch to retain moisture and prevent nutrient leaching in sandy soil.",
                                explanation = dbSoil?.agronomicRationale ?: "Camiguin research trials (Solis et al., 2016) demonstrated that sawdust mulch significantly conserves soil moisture and increases Diamante Max F1 tomato yield to 5.08 t/ha compared to unmulched bare soil (4.28 t/ha).",
                                source = dbSoil?.sourceCitation ?: "Camiguin Agronomic Field Trial 2015-2016 (Solis et al.)",
                                actionText = "Apply Organic Mulch",
                                actionTaskType = TaskType.SOIL_AMENDMENT,
                                evaluatedAt = today.toString()
                            )
                        )
                    }
                    SoilType.LOAM -> {
                        decisions.add(
                            DssDecision(
                                id = "dss_soil_loam_opt_${plot.id}",
                                farmId = farmId,
                                plotId = plot.id,
                                plotLabel = plot.plotLabel,
                                cropName = crop.name,
                                decisionType = DssDecisionType.RECOMMENDATION,
                                category = DssCategory.SOIL_COMPATIBILITY,
                                priority = DssPriority.INFO,
                                title = "Optimal Soil Match: Loam on ${plot.plotLabel}",
                                summary = dbSoil?.amendmentAction ?: "Loam soil provides an optimal 40-40-20 balance of drainage and nutrients for Tomato root expansion.",
                                explanation = dbSoil?.agronomicRationale ?: "Natural loam texture maintains continuous root aeration without waterlogging, supporting steady blossom development and vigorous fruit set.",
                                source = dbSoil?.sourceCitation ?: "BSWM Soil Fertility Standards 2024",
                                actionText = "Maintain Organic Topdressing",
                                actionTaskType = TaskType.SOIL_AMENDMENT,
                                evaluatedAt = today.toString()
                            )
                        )
                    }
                    else -> {
                        if (score <= 0.50f) {
                            decisions.add(
                                DssDecision(
                                    id = "dss_soil_${plot.id}",
                                    farmId = farmId,
                                    plotId = plot.id,
                                    plotLabel = plot.plotLabel,
                                    cropName = crop.name,
                                    decisionType = DssDecisionType.RECOMMENDATION,
                                    category = DssCategory.SOIL_COMPATIBILITY,
                                    priority = DssPriority.MEDIUM,
                                    title = "Soil Conditioning Advised: ${plot.soilType.name} (${(score * 100).toInt()}%)",
                                    summary = "Apply organic compost and agricultural lime if soil is acidic.",
                                    explanation = "Tomato requires pH 6.0–6.8 for proper calcium uptake to avoid blossom end rot.",
                                    source = "DA-BPI Solanaceous Production Standards",
                                    actionText = "Apply Soil Amendment",
                                    actionTaskType = TaskType.SOIL_AMENDMENT,
                                    evaluatedAt = today.toString()
                                )
                            )
                        }
                    }
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
