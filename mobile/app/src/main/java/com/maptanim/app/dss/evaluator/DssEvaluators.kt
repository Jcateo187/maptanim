package com.maptanim.app.dss.evaluator

import com.maptanim.app.dss.engine.GrowthStageCalculator
import com.maptanim.app.dss.engine.SoilSuitabilityScorer
import com.maptanim.app.dss.model.DssDecision
import com.maptanim.app.dss.model.DssInput

/**
 * Lightweight facade coordinating all modular agricultural evaluators.
 *
 * Decomposed into:
 * - [SeasonEvaluator] (Planting window & off-season alerts)
 * - [SoilEvaluator] (Soil texture match & conditioning amendments)
 * - [GrowthCareEvaluator] (Irrigation, organic feeding, trellising, weeding)
 * - [PestRiskEvaluator] (Early warning & organic IPM remedies)
 * - [CompanionEvaluator] (Spatial synergy & antagonist alerts)
 * - [RotationEvaluator] (Monoculture disease cycle breaking)
 * - [HarvestEvaluator] (Readiness & overdue warnings)
 */
class DssEvaluatorFacade(
    val seasonEvaluator: SeasonEvaluator = SeasonEvaluator(),
    val soilEvaluator: SoilEvaluator = SoilEvaluator(),
    val growthCareEvaluator: GrowthCareEvaluator = GrowthCareEvaluator(),
    val pestRiskEvaluator: PestRiskEvaluator = PestRiskEvaluator(),
    val companionEvaluator: CompanionEvaluator = CompanionEvaluator(),
    val rotationEvaluator: RotationEvaluator = RotationEvaluator(),
    val harvestEvaluator: HarvestEvaluator = HarvestEvaluator()
) {
    fun evaluateAll(input: DssInput): List<DssDecision> {
        val decisions = mutableListOf<DssDecision>()
        decisions.addAll(seasonEvaluator.evaluate(input))
        decisions.addAll(soilEvaluator.evaluate(input))
        decisions.addAll(growthCareEvaluator.evaluate(input))
        decisions.addAll(pestRiskEvaluator.evaluate(input))
        decisions.addAll(companionEvaluator.evaluate(input))
        decisions.addAll(rotationEvaluator.evaluate(input))
        decisions.addAll(harvestEvaluator.evaluate(input))
        return decisions
    }
}
