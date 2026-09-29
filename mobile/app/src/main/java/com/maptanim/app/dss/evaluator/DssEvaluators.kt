package com.maptanim.app.dss.evaluator

import com.maptanim.app.domain.model.Activity
import com.maptanim.app.domain.model.CompanionRelation
import com.maptanim.app.domain.model.Crop
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.GrowthStage
import com.maptanim.app.domain.model.HarvestRecord
import com.maptanim.app.domain.model.Season
import com.maptanim.app.domain.model.TaskType
import com.maptanim.app.dss.engine.GrowthStageCalculator
import com.maptanim.app.dss.engine.SoilSuitabilityScorer
import com.maptanim.app.dss.knowledgebase.CompanionDataProvider
import com.maptanim.app.dss.model.DssCategory
import com.maptanim.app.dss.model.DssDecision
import com.maptanim.app.dss.model.DssDecisionType
import com.maptanim.app.dss.model.DssInput
import com.maptanim.app.dss.model.DssPriority
import com.maptanim.app.dss.rules.DssRuleCatalog
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID

// ─── 1. Season & Planting Window Evaluator ───────────────────────────────────

class DssSeasonEvaluator {

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

// ─── 2. Soil Compatibility Evaluator ────────────────────────────────────────

class DssSoilEvaluator(
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
                    com.maptanim.app.domain.model.SoilType.CLAY -> {
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
                    com.maptanim.app.domain.model.SoilType.SANDY -> {
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
                    com.maptanim.app.domain.model.SoilType.LOAM -> {
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
                val rule = if (plot.soilType == com.maptanim.app.domain.model.SoilType.CLAY) {
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

// ─── 3. Growth Stage & Crop Care Scheduling Evaluator ───────────────────────

class DssGrowthCareEvaluator(
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
                GrowthStage.SEEDLING, GrowthStage.VEGETATIVE, GrowthStage.FLOWERING,
                GrowthStage.EARLY_VEGETATIVE, GrowthStage.MID_VEGETATIVE
            )
            if (isFertilizeStage) {
                val lastFertilized = input.farmerData.recentActivities
                    .filter { it.plotId == plot.id && it.type == TaskType.FERTILIZE }
                    .mapNotNull { runCatching { LocalDate.parse(it.performedAt.take(10)) }.getOrNull() }
                    .maxOrNull()

                val daysSinceFert = lastFertilized?.let { ChronoUnit.DAYS.between(it, today).toInt() }
                    ?: crop.fertilizeIntervalDays

                if (daysSinceFert >= crop.fertilizeIntervalDays) {
                    val isFlowering = stage == GrowthStage.FLOWERING || stage == GrowthStage.FRUITING
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
                if (stage == GrowthStage.VEGETATIVE || stage == GrowthStage.MID_VEGETATIVE || daysElapsed in 15..35) {
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
                if (stage == GrowthStage.FLOWERING || stage == GrowthStage.FRUITING || daysElapsed in 35..55) {
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
                if (stage == GrowthStage.FLOWERING || stage == GrowthStage.FRUITING) {
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

// ─── 4. Pest & Disease Organic Interventions Evaluator ──────────────────────

class DssPestDiseaseEvaluator {

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

// ─── 5. Companion Planting & Spatial Intercropping Evaluator ────────────────

class DssCompanionEvaluator {

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

// ─── 6. Crop Rotation & Fallow Evaluator ─────────────────────────────────────

class DssRotationEvaluator {

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

// ─── 7. Harvest Readiness Evaluator ─────────────────────────────────────────

class DssHarvestEvaluator(
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

            if (stage == GrowthStage.OVERDUE) {
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
            } else if (stage == GrowthStage.HARVEST_READY) {
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
