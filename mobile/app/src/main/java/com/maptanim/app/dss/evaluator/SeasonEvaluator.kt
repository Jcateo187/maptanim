package com.maptanim.app.dss.evaluator

import com.maptanim.app.domain.model.Crop
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.Season
import com.maptanim.app.domain.model.TaskType
import com.maptanim.app.dss.model.DssCategory
import com.maptanim.app.dss.model.DssDecision
import com.maptanim.app.dss.model.DssDecisionType
import com.maptanim.app.dss.model.DssInput
import com.maptanim.app.dss.model.DssPriority
import com.maptanim.app.dss.rules.DssRuleCatalog

/**
 * Evaluates season suitability and planting windows against DA-BPI recommendations.
 */
class SeasonEvaluator {

    fun evaluate(input: DssInput): List<DssDecision> {
        val decisions = mutableListOf<DssDecision>()
        val today = input.farmerData.currentDate
        val currentSeason = if (today.monthValue in 5..10) Season.WET else Season.DRY
        val farmId = input.session.farmId

        input.farmerData.plots.forEach { plot ->
            val crop = input.referenceData.crops.firstOrNull { it.name.equals(plot.cropName, ignoreCase = true) }
                ?: return@forEach

            // Check if crop seasonality matches
            if (crop.seasonality.isNotEmpty() && !crop.seasonality.contains("YEAR_ROUND") && !crop.seasonality.contains(currentSeason.name)) {
                decisions.add(
                    DssDecision(
                        id = "dss_season_${plot.id}_${currentSeason.name}",
                        farmId = farmId,
                        plotId = plot.id,
                        plotLabel = plot.plotLabel,
                        cropName = crop.name,
                        decisionType = DssDecisionType.ALERT,
                        category = DssCategory.SEASON_WINDOW,
                        priority = DssPriority.HIGH,
                        title = "${crop.name} Off-Season Warning on ${plot.plotLabel}",
                        summary = "${crop.name} prefers ${crop.seasonality.joinToString("/")} season. Currently in ${currentSeason.name} season.",
                        explanation = "Cultivating ${crop.name} outside its optimal climate window increases susceptibility to climatic stress, disease proliferation, and suboptimal yield.",
                        source = "DA-BPI Philippine Crop Calendar & Agro-Climatic Zones",
                        actionText = "Implement microclimate protection",
                        actionTaskType = TaskType.OBSERVATION,
                        evaluatedAt = today.toString()
                    )
                )
            }

            // Wet season fungal and drainage check for Solanaceae
            if (currentSeason == Season.WET && crop.category.equals("FRUIT", ignoreCase = true) && crop.name in listOf("Tomato", "Eggplant", "Chili Pepper")) {
                val rule = DssRuleCatalog.findRuleByCode("SEASON_TOMATO_WET")
                decisions.add(
                    DssDecision(
                        id = "dss_wet_solanaceae_${plot.id}",
                        farmId = farmId,
                        plotId = plot.id,
                        plotLabel = plot.plotLabel,
                        cropName = crop.name,
                        decisionType = DssDecisionType.ALERT,
                        category = DssCategory.SEASON_WINDOW,
                        priority = DssPriority.MEDIUM,
                        title = "Monsoon Waterlogging & Bacterial Wilt Risk",
                        summary = "Elevate drainage furrows around ${plot.plotLabel} to prevent root saturation.",
                        explanation = rule?.explanation ?: "Excess soil moisture combined with tropical heat accelerates Ralstonia solanacearum (bacterial wilt) infection in Solanaceous vegetables.",
                        source = rule?.source ?: "DA-BPI Technical Bulletin No. 14: Solanaceous Crops Management",
                        actionText = "Inspect Drainage Furrows",
                        actionTaskType = TaskType.OBSERVATION,
                        ruleId = rule?.id,
                        evaluatedAt = today.toString()
                    )
                )
            }
        }

        return decisions
    }
}

typealias DssSeasonEvaluator = SeasonEvaluator
