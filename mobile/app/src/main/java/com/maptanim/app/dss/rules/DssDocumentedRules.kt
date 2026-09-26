package com.maptanim.app.dss.rules

import com.maptanim.app.domain.model.TaskType
import com.maptanim.app.dss.model.DssCategory
import com.maptanim.app.dss.model.DssPriority

/**
 * Representation of a research-based documented rule stored in the agricultural reference repository.
 * Contains condition logic, agronomic rationale (explanation), and published citations (source).
 */
data class DssDocumentedRule(
    val id: String,
    val ruleCode: String,
    val category: DssCategory,
    val targetCrop: String? = null,
    val targetFamily: String? = null,
    val conditionDescription: String,
    val recommendation: String,
    val explanation: String,
    val source: String,
    val defaultPriority: DssPriority = DssPriority.MEDIUM,
    val actionTaskType: TaskType? = null
)

/**
 * Verified knowledge base catalog containing Philippine national agricultural standards,
 * Bureau of Plant Industry (DA-BPI), DA-BAR, and BSWM documented rules.
 */
object DssRuleCatalog {

    val documentedRules: List<DssDocumentedRule> = listOf(
        // ── 1. Soil Suitability & Amendments ──────────────────────────────────
        DssDocumentedRule(
            id = "rule_soil_loam_fertility",
            ruleCode = "SOIL_LOAM_OPTIMAL",
            category = DssCategory.SOIL_COMPATIBILITY,
            conditionDescription = "Crop planted in Loam soil with balanced drainage and organic matter.",
            recommendation = "Maintain soil structure with light vermicompost topdressing.",
            explanation = "Loam provides an optimal 40-40-20 sand-silt-clay proportion with high cation exchange capacity, allowing unimpeded root aeration and steady moisture retention.",
            source = "BSWM Soil Fertility & Management Manual (Philippine Agricultural Reference)",
            defaultPriority = DssPriority.INFO
        ),
        DssDocumentedRule(
            id = "rule_soil_heavy_clay_drainage",
            ruleCode = "SOIL_CLAY_AERATION",
            category = DssCategory.SOIL_COMPATIBILITY,
            conditionDescription = "Solanaceae or bulb crop planted in heavy Clay soil.",
            recommendation = "Incorporate rice hull ash (carbonized rice hull) and compost to improve aeration.",
            explanation = "Heavy clay soil has high water retention but low porosity when saturated, risking root rot (Pythium/Rhizoctonia) and poor root penetration for Solanaceae and bulb crops.",
            source = "DA-BPI Lowland Vegetable Production Guide 2024",
            defaultPriority = DssPriority.HIGH,
            actionTaskType = TaskType.SOIL_AMENDMENT
        ),
        DssDocumentedRule(
            id = "rule_soil_sandy_leaching",
            ruleCode = "SOIL_SANDY_MULCH",
            category = DssCategory.SOIL_COMPATIBILITY,
            conditionDescription = "Crops planted in Sandy soil during warm or dry conditions.",
            recommendation = "Apply 5cm organic straw mulch and increase irrigation frequency.",
            explanation = "Sandy soils have low water holding capacity and rapid nutrient leaching. Mulch reduces evaporative moisture loss by up to 45% and buffers root temperature.",
            source = "PCARRD Philippine Recommends for Soil and Water Conservation",
            defaultPriority = DssPriority.MEDIUM,
            actionTaskType = TaskType.WATER
        ),

        // ── 2. Seasonal Planting Windows ──────────────────────────────────────
        DssDocumentedRule(
            id = "rule_season_tomato_wet_risk",
            ruleCode = "SEASON_TOMATO_WET",
            category = DssCategory.SEASON_WINDOW,
            targetCrop = "Tomato",
            conditionDescription = "Tomato cultivated during the Philippine Wet/Monsoon season (June–October).",
            recommendation = "Provide rain shelters or elevated beds with drainage furrows to avert bacterial wilt (Ralstonia).",
            explanation = "Tomato foliage and root systems are highly vulnerable to waterlogging and bacterial wilt under high humidity and prolonged rainfall above 28°C.",
            source = "DA-BPI Technical Bulletin No. 14: Solanaceous Crops Management",
            defaultPriority = DssPriority.HIGH,
            actionTaskType = TaskType.OBSERVATION
        ),
        DssDocumentedRule(
            id = "rule_season_pechay_year_round",
            ruleCode = "SEASON_PECHAY_VERSATILITY",
            category = DssCategory.SEASON_WINDOW,
            targetCrop = "Pechay",
            conditionDescription = "Pechay grown across diverse seasonal windows.",
            recommendation = "Protect shallow roots from heavy downpours with raised mulch beds in the rainy season.",
            explanation = "Pechay (Brassica rapa) has a rapid 25-30 day maturity cycle making it suitable year-round, but torrential rains can physically batter delicate leaves without mulch protection.",
            source = "DA-BAR Lowland Vegetables Production Handbook",
            defaultPriority = DssPriority.LOW
        ),

        // ── 3. Growth Stage & Crop Care Scheduling ────────────────────────────
        DssDocumentedRule(
            id = "rule_care_vegetative_fertilize",
            ruleCode = "CARE_VEG_NITROGEN",
            category = DssCategory.GROWTH_CARE,
            conditionDescription = "Crops entering the vegetative expansion stage.",
            recommendation = "Apply nitrogen-rich side-dressing (vermicast or 14-14-14 / 46-0-0) around the dripline.",
            explanation = "During the vegetative stage, leaf area index and stem elongation surge, requiring active nitrogen uptake to synthesize chlorophyll and support biomass accumulation.",
            source = "DA-BPI Philippine National Standards for Organic & Conventional Soil Amendments",
            defaultPriority = DssPriority.MEDIUM,
            actionTaskType = TaskType.FERTILIZE
        ),
        DssDocumentedRule(
            id = "rule_care_flowering_potassium",
            ruleCode = "CARE_FLOWER_POTASSIUM",
            category = DssCategory.GROWTH_CARE,
            conditionDescription = "Fruit vegetables (Tomato, Eggplant, Cucumber, Bitter Gourd) entering flowering/fruiting.",
            recommendation = "Transition to high Potassium (K) and Phosphorus (P) nutrition; avoid excessive nitrogen.",
            explanation = "Excess nitrogen at flowering stimulates excessive vegetative suckers while inducing blossom drop. Potassium is essential for carbohydrate translocation into developing fruits.",
            source = "UPLB College of Agriculture Horticulture Compendium",
            defaultPriority = DssPriority.MEDIUM,
            actionTaskType = TaskType.FERTILIZE
        ),
        DssDocumentedRule(
            id = "rule_care_weeding_early",
            ruleCode = "CARE_CRITICAL_WEED_PERIOD",
            category = DssCategory.GROWTH_CARE,
            conditionDescription = "Seedling and early vegetative crops within first 30 days.",
            recommendation = "Perform manual or shallow hoe weeding to keep the root zone clear.",
            explanation = "The first 3-4 weeks represent the Critical Period of Weed Competition (CPWC). Unchecked weeds reduce crop yield by over 40% through direct competition for sunlight and nutrients.",
            source = "IRRI / DA-BAR Crop Protection & Weed Science Compendium",
            defaultPriority = DssPriority.HIGH,
            actionTaskType = TaskType.WEED
        ),
        DssDocumentedRule(
            id = "rule_care_trellis_vines",
            ruleCode = "CARE_TRELLIS_INSTALL",
            category = DssCategory.GROWTH_CARE,
            conditionDescription = "Vining crops (Bitter Gourd, Cucumber, String Bean) reaching 20cm height without support.",
            recommendation = "Erect bamboo balag or A-frame trellises with climbing twine.",
            explanation = "Ground contact encourages soil-borne fungal pathogens (Anthracnose, Phytophthora) on fruits. Trellising increases sunlight exposure, improves air circulation, and produces straight, marketable pods.",
            source = "DA-BPI Guidelines on Indigenous Trellising Systems (Tulos & Balag)",
            defaultPriority = DssPriority.HIGH,
            actionTaskType = TaskType.TRELLIS
        ),

        // ── 4. Pest & Disease Organic Interventions ───────────────────────────
        DssDocumentedRule(
            id = "rule_pest_aphids_neem",
            ruleCode = "PEST_APHID_CONTROL",
            category = DssCategory.PEST_DISEASE,
            conditionDescription = "Aphids or whitefly clusters observed on leaf undersides or shoot tips.",
            recommendation = "Apply 2% Neem oil extract (Azadirachtin) or botanical chili-soap spray during late afternoon.",
            explanation = "Azadirachtin acts as an insect growth regulator and feeding deterrent against soft-bodied piercing-sucking insects, without destroying beneficial pollinators when applied after sunset.",
            source = "DA-BPI National Organic Agriculture Program Technical Guide",
            defaultPriority = DssPriority.CRITICAL,
            actionTaskType = TaskType.APPLY_PESTICIDE
        ),
        DssDocumentedRule(
            id = "rule_pest_fruit_borer_traps",
            ruleCode = "PEST_BORER_INTERVENTION",
            category = DssCategory.PEST_DISEASE,
            conditionDescription = "Fruit borer signs or moth activity observed in Solanaceae or Cucurbit beds.",
            recommendation = "Install yellow sticky traps, pheromone lures, and manually pick/destroy bore-infested fruits.",
            explanation = "Once fruit borer larvae penetrate the fruit wall, contact sprays become ineffective. Pheromone lures disrupt mating while immediate sanitary culling breaks the reproductive cycle.",
            source = "DA-BAR Integrated Pest Management (IPM) Field Protocol",
            defaultPriority = DssPriority.CRITICAL,
            actionTaskType = TaskType.PEST_ALERT
        ),

        // ── 5. Companion Planting & Spatial Intercropping ─────────────────────
        DssDocumentedRule(
            id = "rule_companion_antagonist_isolation",
            ruleCode = "COMPANION_ANTAGONIST_ALERT",
            category = DssCategory.COMPANION_INTERCROPPING,
            conditionDescription = "Adjacent plantings of antagonistic crops within 1.5 meters (e.g. Tomato adjacent to Eggplant or Cabbage).",
            recommendation = "Re-space or insert a barrier crop (e.g. Allium/Onion or Tagetes/Marigold) between beds.",
            explanation = "Antagonistic crop pairings share identical pest and fungal spectra (promoting cross-epidemics) or exude allelopathic root biochemicals that suppress neighboring plant vigor.",
            source = "DA-BPI Companion Bulletin 2026 (58 Approved Companion Pairs)",
            defaultPriority = DssPriority.CRITICAL
        ),
        DssDocumentedRule(
            id = "rule_companion_beneficial_synergy",
            ruleCode = "COMPANION_BENEFICIAL_SYNERGY",
            category = DssCategory.COMPANION_INTERCROPPING,
            conditionDescription = "Beneficial pairings planted within close proximity (e.g. Tomato + Onion, Corn + Bean).",
            recommendation = "Maintain companion planting layout for natural pest deterrence and soil health enhancement.",
            explanation = "Onion sulfur volatiles confuse host-seeking pests of Tomato, while legumes (String Bean) symbiotically fix atmospheric nitrogen that supplements heavy-feeding crops.",
            source = "DA-BPI Companion Bulletin 2026 / DA-BAR Intercropping Manual",
            defaultPriority = DssPriority.LOW
        ),

        // ── 6. Crop Rotation & Fallow Management ──────────────────────────────
        DssDocumentedRule(
            id = "rule_rotation_family_break",
            ruleCode = "ROTATION_FAMILY_DISEASE_BREAK",
            category = DssCategory.CROP_ROTATION_FALLOW,
            conditionDescription = "Bed previously planted with a Solanaceae crop (Tomato, Eggplant, Pepper) slated for replanting.",
            recommendation = "Do NOT replant Solanaceae. Rotate to a Legume (Sitaw) or Root crop (Carrot) for at least 1 cycle.",
            explanation = "Continuous monoculture of the same botanical family builds up soil-borne bacterial wilt (Ralstonia solanacearum) and root-knot nematodes (Meloidogyne spp.) in the soil profile.",
            source = "BPI Crop Rotation Protocol 2025: Disease Cycle Disruption",
            defaultPriority = DssPriority.HIGH,
            actionTaskType = TaskType.ROTATION_ALERT
        ),
        DssDocumentedRule(
            id = "rule_rotation_fallow_solarization",
            ruleCode = "ROTATION_FALLOW_SOLARIZATION",
            category = DssCategory.CROP_ROTATION_FALLOW,
            conditionDescription = "Bed harvested with severe fungal or bacterial history.",
            recommendation = "Allow a 2–3 week fallow period with deep soil tilling and solarization prior to next planting.",
            explanation = "Deep exposure to tropical sunlight and dry aeration depletes anaerobic pathogen inoculum and allows beneficial soil saprophytes to decompose infected residual root tissues.",
            source = "BSWM Integrated Soil Health & Solarization Protocol",
            defaultPriority = DssPriority.MEDIUM,
            actionTaskType = TaskType.SOIL_AMENDMENT
        ),

        // ── 7. Harvest Readiness & Indices ────────────────────────────────────
        DssDocumentedRule(
            id = "rule_harvest_maturity_signs",
            ruleCode = "HARVEST_OPTIMAL_WINDOW",
            category = DssCategory.HARVEST_READINESS,
            conditionDescription = "Crop has reached or exceeded 90% of documented days-to-harvest.",
            recommendation = "Inspect for physical harvest indicators and harvest in the early morning.",
            explanation = "Harvesting at optimal physiological maturity ensures peak sugar/nutrient content and maximum shelf life. Early morning harvest minimizes field heat and post-harvest respiration loss.",
            source = "Philippine National Standards (PNS) for Fresh Fruit & Leafy Vegetables",
            defaultPriority = DssPriority.HIGH,
            actionTaskType = TaskType.HARVEST
        ),
        DssDocumentedRule(
            id = "rule_harvest_overdue_warning",
            ruleCode = "HARVEST_OVERDUE_DEPRECIATION",
            category = DssCategory.HARVEST_READINESS,
            conditionDescription = "Crop has exceeded maximum days-to-harvest.",
            recommendation = "Harvest immediately to prevent fruit cracking, bolting, or woody fiber accumulation.",
            explanation = "Overdue crops bolt to seed (in brassicas and lettuce) or develop lignified fibrous tissue (in okra and string beans), drastically reducing commercial value and edibility.",
            source = "DA-BPI Post-Harvest Handling & Quality Preservation Guidelines",
            defaultPriority = DssPriority.CRITICAL,
            actionTaskType = TaskType.HARVEST
        )
    )

    fun getRulesForCategory(category: DssCategory): List<DssDocumentedRule> =
        documentedRules.filter { it.category == category }

    fun findRuleByCode(code: String): DssDocumentedRule? =
        documentedRules.firstOrNull { it.ruleCode == code }
}
