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
 * Evaluates irrigation, organic fertilization, trellising, and weeding based on crop stage.
 */
class GrowthCareEvaluator(
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
            val isTomato = crop.name.contains("Tomato", ignoreCase = true) || crop.name.contains("Kamatis", ignoreCase = true)

            // 1. Irrigation Evaluation
            val lastWatered = input.farmerData.recentActivities
                .filter { it.plotId == plot.id && it.type == TaskType.WATER }
                .mapNotNull { runCatching { LocalDate.parse(it.performedAt.take(10)) }.getOrNull() }
                .maxOrNull()

            val daysSinceWater = lastWatered?.let { ChronoUnit.DAYS.between(it, today).toInt() }
                ?: crop.wateringIntervalDays

            if (daysSinceWater >= crop.wateringIntervalDays) {
                val waterSummary = if (isTomato) {
                    "Water 1 tabo (1–2 liters) gently at the base of each plant in the cool morning. Never splash water on the leaves."
                } else {
                    "Last watered $daysSinceWater days ago. Water gently around the plant base."
                }

                decisions.add(
                    DssDecision(
                        id = "dss_task_water_${plot.id}",
                        farmId = farmId,
                        plotId = plot.id,
                        plotLabel = plot.plotLabel,
                        cropName = crop.name,
                        decisionType = DssDecisionType.TASK,
                        category = DssCategory.GROWTH_CARE,
                        priority = if (daysSinceWater > crop.wateringIntervalDays + 1) DssPriority.CRITICAL else DssPriority.HIGH,
                        title = "Water ${crop.name} on ${plot.plotLabel}",
                        summary = waterSummary,
                        explanation = "Consistent root-zone moisture prevents tomatoes from cracking and keeps flowers from dropping.",
                        source = "DA-BAR Lowland Vegetables Production Handbook (Smallholder Guide)",
                        actionText = "Diligan ang Halaman",
                        actionTaskType = TaskType.WATER,
                        evaluatedAt = today.toString()
                    )
                )
            }

            // 2. Fertilization / Organic Nutrition Scheduling by Stage
            val isFertilizeStage = stage in listOf(
                CropGrowthStage.SEEDLING, CropGrowthStage.VEGETATIVE, CropGrowthStage.FLOWERING,
                CropGrowthStage.RIPENING
            )
            if (isFertilizeStage) {
                val lastFertilized = input.farmerData.recentActivities
                    .filter { it.plotId == plot.id && it.type == TaskType.FERTILIZE }
                    .mapNotNull { runCatching { LocalDate.parse(it.performedAt.take(10)) }.getOrNull() }
                    .maxOrNull()

                val daysSinceFert = lastFertilized?.let { ChronoUnit.DAYS.between(it, today).toInt() }
                    ?: crop.fertilizeIntervalDays

                if (daysSinceFert >= crop.fertilizeIntervalDays) {
                    val isFlowering = stage == CropGrowthStage.FLOWERING || stage == CropGrowthStage.RIPENING
                    val nutritionSummary = if (isTomato) {
                        if (isFlowering) {
                            "Add 1 handful of vermicast and sprinkle a spoonful of crushed eggshells (for calcium) or a pinch of wood ash around the plant base."
                        } else {
                            "Spread 1 to 2 handfuls of rich compost or vermicast around each plant, or water with rice-wash water (hugas-bigas)."
                        }
                    } else {
                        "Apply 1-2 handfuls of compost or balanced organic amendment around plant base."
                    }

                    decisions.add(
                        DssDecision(
                            id = "dss_task_fert_${plot.id}_${stage.name}",
                            farmId = farmId,
                            plotId = plot.id,
                            plotLabel = plot.plotLabel,
                            cropName = crop.name,
                            decisionType = DssDecisionType.TASK,
                            category = DssCategory.GROWTH_CARE,
                            priority = DssPriority.MEDIUM,
                            title = "Add Compost / Natural Plant Food for ${crop.name}",
                            summary = nutritionSummary,
                            explanation = "Natural organic compost feeds soil microorganisms and slowly releases nutrients without burning roots.",
                            source = "DA-BPI National Organic Agriculture Program (NOAP)",
                            actionText = "Maglagay ng Pataba",
                            actionTaskType = TaskType.FERTILIZE,
                            evaluatedAt = today.toString()
                        )
                    )
                }
            }

            // 3. Trellising / Bamboo Staking Check
            val needsTrellis = crop.name in listOf("Bitter Gourd", "Cucumber", "Yardlong String Bean", "Tomato")
            if (needsTrellis && daysElapsed in 15..35) {
                val hasTrellised = input.farmerData.recentActivities.any { it.plotId == plot.id && it.type == TaskType.TRELLIS }
                if (!hasTrellised) {
                    decisions.add(
                        DssDecision(
                            id = "dss_task_trellis_${plot.id}",
                            farmId = farmId,
                            plotId = plot.id,
                            plotLabel = plot.plotLabel,
                            cropName = crop.name,
                            decisionType = DssDecisionType.TASK,
                            category = DssCategory.GROWTH_CARE,
                            priority = DssPriority.HIGH,
                            title = "Push Bamboo Stake (Tulos) for ${crop.name}",
                            summary = "Plants are growing tall (~${daysElapsed} days). Push a 1.5m bamboo stick next to each plant and tie the stem gently with a strip of cloth.",
                            explanation = "Keeping tomato leaves and fruits off wet soil prevents fungal rot and keeps fruits clean.",
                            source = "DA-BPI Guidelines on Indigenous Trellising Systems",
                            actionText = "Lagyan ng Tulos / Tali",
                            actionTaskType = TaskType.TRELLIS,
                            evaluatedAt = today.toString()
                        )
                    )
                }
            }

            // 4. Critical Weeding Period (First 30 days)
            if (daysElapsed in 7..30) {
                val lastWeed = input.farmerData.recentActivities
                    .filter { it.plotId == plot.id && it.type == TaskType.WEED }
                    .mapNotNull { runCatching { LocalDate.parse(it.performedAt.take(10)) }.getOrNull() }
                    .maxOrNull()
                val daysSinceWeed = lastWeed?.let { ChronoUnit.DAYS.between(it, today).toInt() } ?: 10

                if (daysSinceWeed >= 8) {
                    val rule = DssRuleCatalog.findRuleByCode("CARE_CRITICAL_WEED_PERIOD")
                    decisions.add(
                        DssDecision(
                            id = "dss_task_weed_${plot.id}",
                            farmId = farmId,
                            plotId = plot.id,
                            plotLabel = plot.plotLabel,
                            cropName = crop.name,
                            decisionType = DssDecisionType.TASK,
                            category = DssCategory.GROWTH_CARE,
                            priority = DssPriority.MEDIUM,
                            title = "Weed Clearing due on ${plot.plotLabel}",
                            summary = "Maintain 0.5m weed-free circle around crop base.",
                            explanation = rule?.explanation ?: "The first 30 days are the Critical Period of Weed Competition (CPWC). Unchecked weeds absorb vital moisture and nutrients.",
                            source = rule?.source ?: "IRRI / DA-BAR Crop Protection & Weed Science Compendium",
                            actionText = "Log Weed Activity",
                            actionTaskType = TaskType.WEED,
                            ruleId = rule?.id,
                            evaluatedAt = today.toString()
                        )
                    )
                }
            }

            // 5. Research Yield Studies & IPM Protocol for Tomato
            if (isTomato) {
                val studyA = input.referenceData.yieldStudies.firstOrNull { it.studyCode == "STUDY_A_CAMIGUIN_2016" }
                val studyB = input.referenceData.yieldStudies.firstOrNull { it.studyCode == "STUDY_B_BACNOTAN_2025" }

                // Study A: Camiguin (2015-2016) Mulching Protocol
                if (stage == CropGrowthStage.VEGETATIVE || daysElapsed in 15..35) {
                    decisions.add(
                        DssDecision(
                            id = "dss_rec_mulch_camiguin_${plot.id}",
                            farmId = farmId,
                            plotId = plot.id,
                            plotLabel = plot.plotLabel,
                            cropName = crop.name,
                            decisionType = DssDecisionType.RECOMMENDATION,
                            category = DssCategory.GROWTH_CARE,
                            priority = DssPriority.MEDIUM,
                            title = "Sawdust / Straw Mulch Protocol (Camiguin Study)",
                            summary = studyA?.treatmentDescription ?: "Apply 5cm organic sawdust mulch to achieve peak yield of 5.08 t/ha for Diamante Max F1.",
                            explanation = studyA?.keyFindings ?: "Field research in Catarman, Camiguin (Solis et al., 2016) proved that sawdust mulch (V1M4) on Diamante Max F1 elevated yield from 4.28 t/ha to 5.08 t/ha by reducing soil water evaporation, cooling root zones, and suppressing weed competition.",
                            source = studyA?.publicationReference ?: "Camiguin Polytechnic State College Agronomic Field Trial (2015–2016)",
                            actionText = "Apply Sawdust Mulch",
                            actionTaskType = TaskType.SOIL_AMENDMENT,
                            evaluatedAt = today.toString()
                        )
                    )
                }

                // Study B: Bacnotan (2025) Trehalose Foliar Spray Protocol
                if (stage == CropGrowthStage.FLOWERING || stage == CropGrowthStage.RIPENING || daysElapsed in 35..55) {
                    decisions.add(
                        DssDecision(
                            id = "dss_rec_trehalose_bacnotan_${plot.id}",
                            farmId = farmId,
                            plotId = plot.id,
                            plotLabel = plot.plotLabel,
                            cropName = crop.name,
                            decisionType = DssDecisionType.RECOMMENDATION,
                            category = DssCategory.GROWTH_CARE,
                            priority = DssPriority.MEDIUM,
                            title = "Trehalose Foliar Protocol (Bacnotan 2025 Study)",
                            summary = studyB?.treatmentDescription ?: "Apply trehalose foliar spray (2 tbsp / 16L water) thrice during flowering to maximize fruit count and harvest frequency.",
                            explanation = studyB?.keyFindings ?: "Bacnotan, La Union research under protected cultivation (IJEAB, 2025) demonstrated that 3x foliar trehalose applications significantly enhanced marketable fruit count and sustained continuous harvest without excess input cost.",
                            source = studyB?.publicationReference ?: "International Journal of Environment, Agriculture and Biotechnology (Bacnotan Study 2025)",
                            actionText = "Schedule Foliar Spray",
                            actionTaskType = TaskType.FERTILIZE,
                            evaluatedAt = today.toString()
                        )
                    )
                }

                // DA-BPI IPM Fruit Borer Scout Task
                if (stage == CropGrowthStage.FLOWERING || stage == CropGrowthStage.RIPENING) {
                    decisions.add(
                        DssDecision(
                            id = "dss_task_borer_scout_${plot.id}",
                            farmId = farmId,
                            plotId = plot.id,
                            plotLabel = plot.plotLabel,
                            cropName = crop.name,
                            decisionType = DssDecisionType.TASK,
                            category = DssCategory.PEST_DISEASE,
                            priority = DssPriority.HIGH,
                            title = "Check for Caterpillars (Uod sa Bunga) on ${plot.plotLabel}",
                            summary = "Inspect flower clusters and baby green tomatoes in the morning. Pick off caterpillars by hand and spray mild garlic-chili or neem water.",
                            explanation = "Catching caterpillars early prevents them from drilling holes into developing tomatoes.",
                            source = "DA-BPI Lowland Vegetable IPM Guide",
                            actionText = "Tingnan ang Halaman",
                            actionTaskType = TaskType.OBSERVATION,
                            evaluatedAt = today.toString()
                        )
                    )
                }
            }
        }

        return decisions
    }
}

typealias DssGrowthCareEvaluator = GrowthCareEvaluator
