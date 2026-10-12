package com.maptanim.app.dss.evaluator

import com.maptanim.app.domain.model.CompanionRelation
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.TaskType
import com.maptanim.app.dss.knowledgebase.CompanionDataProvider
import com.maptanim.app.dss.model.DssCategory
import com.maptanim.app.dss.model.DssDecision
import com.maptanim.app.dss.model.DssDecisionType
import com.maptanim.app.dss.model.DssInput
import com.maptanim.app.dss.model.DssPriority

/**
 * Evaluates companion planting synergies and antagonist conflicts for adjacent plots.
 */
class CompanionEvaluator {

    fun evaluate(input: DssInput): List<DssDecision> {
        val decisions = mutableListOf<DssDecision>()
        val today = input.farmerData.currentDate
        val farmId = input.session.farmId
        val plots = input.farmerData.plots

        val adjacentPairs = findAdjacentPairs(plots)
        val processedKeys = mutableSetOf<String>()

        adjacentPairs.forEach { (plotA, plotB) ->
            val cropA = plotA.cropName ?: return@forEach
            val cropB = plotB.cropName ?: return@forEach
            val pairKey = if (plotA.id < plotB.id) "${plotA.id}_${plotB.id}" else "${plotB.id}_${plotA.id}"
            if (processedKeys.contains(pairKey)) return@forEach
            processedKeys.add(pairKey)

            val companionEntry = CompanionDataProvider.getRelationship(cropA, cropB) ?: return@forEach

            if (companionEntry.relationship == CompanionRelation.ANTAGONIST) {
                decisions.add(
                    DssDecision(
                        id = "dss_companion_antagonist_${plotA.id}_${plotB.id}",
                        farmId = farmId,
                        plotId = plotA.id,
                        plotLabel = "${plotA.plotLabel} & ${plotB.plotLabel}",
                        cropName = "$cropA + $cropB",
                        decisionType = DssDecisionType.ALERT,
                        category = DssCategory.COMPANION_INTERCROPPING,
                        priority = DssPriority.CRITICAL,
                        title = "Antagonist Plant Warning: $cropA & $cropB Adjacent",
                        summary = companionEntry.reason,
                        explanation = "Scientific documentation confirms negative interactions through shared disease vectors or allelopathic root exudates that stunt adjacent growth.",
                        source = "DA-BPI Companion Bulletin 2026 (58 Approved Companion Pairs)",
                        actionText = "Re-space Beds",
                        actionTaskType = TaskType.OBSERVATION,
                        evaluatedAt = today.toString()
                    )
                )
            } else if (companionEntry.relationship == CompanionRelation.BENEFICIAL) {
                decisions.add(
                    DssDecision(
                        id = "dss_companion_beneficial_${plotA.id}_${plotB.id}",
                        farmId = farmId,
                        plotId = plotA.id,
                        plotLabel = "${plotA.plotLabel} & ${plotB.plotLabel}",
                        cropName = "$cropA + $cropB",
                        decisionType = DssDecisionType.RECOMMENDATION,
                        category = DssCategory.COMPANION_INTERCROPPING,
                        priority = DssPriority.LOW,
                        title = "Beneficial Companion Synergy: $cropA + $cropB",
                        summary = companionEntry.reason,
                        explanation = "Natural companion pairing enhances pest repulsion, optimizes root depth distribution, or improves atmospheric nitrogen fixation.",
                        source = "DA-BPI Companion Bulletin 2026 / DA-BAR Intercropping Manual",
                        actionText = "Maintain Layout",
                        evaluatedAt = today.toString()
                    )
                )
            }
        }

        return decisions
    }

    private fun findAdjacentPairs(plots: List<CropPlot>): List<Pair<CropPlot, CropPlot>> {
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

typealias DssCompanionEvaluator = CompanionEvaluator
