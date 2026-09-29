package com.maptanim.app.data.local

import com.maptanim.app.data.local.entity.*

object CropKnowledgeSeed {

    val tomatoYieldStudies: List<CropYieldStudyEntity> = listOf(
        // Study A — Camiguin, 2015–2016 (Translated to Practical Backyard/Small Farm Terms)
        CropYieldStudyEntity(
            id = "yield_study_tomato_camiguin_2016",
            cropName = "Tomato",
            varietyName = "Diamante Max F1",
            studyCode = "STUDY_A_CAMIGUIN_2016",
            studyTitle = "Utilization of Indigenous Mulches on the Growth and Yield of Different Tomato Varieties in Catarman, Camiguin, Philippines",
            location = "Catarman, Camiguin, Northern Mindanao, Philippines",
            studyPeriod = "October 2015 – February 2016",
            cultivationCondition = "Open Field / Backyard Bed with Organic Mulch Blanket",
            treatmentDescription = "Covering the soil surface around tomato plants with a 2-inch layer of clean wood sawdust, dried rice straw, or dry grass (cogon) compared to bare exposed soil.",
            baselineYieldTPerHa = 4.28f,
            reportedYieldTPerHa = 5.08f,
            yieldIncreasePercent = 18.69f,
            keyFindings = "Covering the ground around tomato plants with free sawdust or dry grass keeps the soil cool and moist under hot sun, prevents weeds, and yields up to 25–35 firm tomatoes per plant (an 18.7% boost without costly inputs).",
            authors = "Erecson Sipin Solis, Larry Dionio, Ruth Duran, Jeanny Dacup",
            institution = "Camiguin Polytechnic State College",
            publicationReference = "Camiguin Agronomic Field Trial 2015–2016 (Solis et al., ResearchGate)"
        ),
        // Study B — Bacnotan, La Union, 2025 (Translated to Practical Smallholder / Rain-Shelter Terms)
        CropYieldStudyEntity(
            id = "yield_study_tomato_bacnotan_2025",
            cropName = "Tomato",
            varietyName = "Off-Season Hybrid Cultivar",
            studyCode = "STUDY_B_BACNOTAN_2025",
            studyTitle = "Yield and Growth Response of Off-Season Tomato to Trehalose Foliar Fertilizer under Protected Cultivation in Bacnotan, La Union",
            location = "Bacnotan, La Union, Ilocos Region, Philippines",
            studyPeriod = "2024 – 2025 Off-Season",
            cultivationCondition = "Simple Protective Canopy / Backyard Rain Shelter",
            treatmentDescription = "Natural trehalose plant sugar spray (or 1 tsp brown sugar per liter of clean water) misted gently on tomato flowers 3 times during blooming.",
            baselineYieldTPerHa = null,
            reportedYieldTPerHa = 4.75f,
            yieldIncreasePercent = null,
            keyFindings = "Under hot or rainy backyard conditions, spraying a mild sugar-water solution on blossoms prevents flower drop, helping almost every tomato flower develop into a full, sweet fruit.",
            authors = "Agronomic Research Team, DMMMSU-NLUC",
            institution = "Don Mariano Marcos Memorial State University, Bacnotan, La Union",
            publicationReference = "International Journal of Environment, Agriculture and Biotechnology (Bacnotan Study 2025)"
        )
    )

    val tomatoVarieties: List<CropVarietyEntity> = listOf(
        CropVarietyEntity(
            id = "var_tomato_diamante_max_f1",
            cropName = "Tomato",
            varietyName = "Diamante Max F1",
            localNamePh = "Kamatis Diamante Max F1",
            breederOrganization = "East-West Seed Philippines / NSIC Registered",
            growthDurationDays = 60,
            stage1SproutDays = 5,
            stage2SeedlingDays = 13,
            stage3VegetativeDays = 20,
            stage4FloweringDays = 16,
            stage5HarvestDays = 6,
            wateringIntervalDays = 2,
            fertilizeIntervalDays = 10,
            optimalSeasonsCsv = "YEAR_ROUND,WET,DRY",
            diseaseResistance = "Tough variety resistant to yellow leaf curling virus and bacterial wilting.",
            description = "The most popular, beginner-friendly hybrid tomato in the Philippines. Produces thick, firm oval fruits that don't easily rot or spoil.",
            sourceCitation = "DA-BPI National Seed Industry Council (NSIC) & East-West Seed 2024"
        ),
        CropVarietyEntity(
            id = "var_tomato_apollo",
            cropName = "Tomato",
            varietyName = "Apollo",
            localNamePh = "Kamatis Apollo",
            breederOrganization = "UPLB-IPB / DA-BPI Lowland Release",
            growthDurationDays = 72,
            stage1SproutDays = 6,
            stage2SeedlingDays = 15,
            stage3VegetativeDays = 24,
            stage4FloweringDays = 20,
            stage5HarvestDays = 7,
            wateringIntervalDays = 2,
            fertilizeIntervalDays = 12,
            optimalSeasonsCsv = "DRY",
            diseaseResistance = "Good natural resistance to leaf spots and summer heat.",
            description = "Traditional lowland tomato variety producing juicy, deep-red fruits best planted during dry, sunny months.",
            sourceCitation = "Institute of Plant Breeding (IPB) UPLB & DA-BPI Solanaceous Bulletin"
        )
    )

    val tomatoGrowthStages: List<CropGrowthStageEntity> = listOf(
        CropGrowthStageEntity(
            id = "stage_tomato_0_sprout",
            cropName = "Tomato",
            stageIndex = 0,
            stageName = "SPROUT / GERMINATION",
            dayStart = 1,
            dayEnd = 5,
            primaryFarmerAction = "Keep seed tray in a warm, shaded spot; mist with a spray bottle every morning.",
            careRecommendation = "Plant seeds shallow (half an inch) in recycled egg cartons, cups, or seed trays with soft soil mixed with compost.",
            irrigationAdvice = "Gentle morning misting only. Do not pour heavy water with a cup so tiny seeds do not wash away.",
            nutritionAdvice = "No fertilizer needed yet; the baby seed has its own food stored inside.",
            criticalRisks = "Over-watering causes baby stems to rot and fall over (damping-off). Keep tray ventilated.",
            sourceCitation = "DA-BPI Lowland Vegetable Production Guide 2024"
        ),
        CropGrowthStageEntity(
            id = "stage_tomato_1_seedling",
            cropName = "Tomato",
            stageIndex = 1,
            stageName = "SEEDLING & TRANSPLANTING",
            dayStart = 6,
            dayEnd = 18,
            primaryFarmerAction = "Expose seedlings to morning sun for 3 days, then transplant into your garden bed in the late afternoon.",
            careRecommendation = "Dig small holes spaced 1 arm-length (50 cm) apart. Bury the stem up to the first leaves for strong roots.",
            irrigationAdvice = "Pour 1 cup of water around each plant immediately after transplanting, then water 1 tabo every 2 days.",
            nutritionAdvice = "Put 1 to 2 handfuls of vermicast, well-rotted compost, or dried kitchen compost in each hole before planting.",
            criticalRisks = "Midday heat can wilt fresh transplants; always transplant when the sun is going down.",
            sourceCitation = "DA-BPI Technical Bulletin No. 14"
        ),
        CropGrowthStageEntity(
            id = "stage_tomato_2_vegetative",
            cropName = "Tomato",
            stageIndex = 2,
            stageName = "RAPID GROWING (VEGETATIVE)",
            dayStart = 19,
            dayEnd = 38,
            primaryFarmerAction = "Push a bamboo stick (tulos) next to each plant and tie the main stem gently with a strip of cloth.",
            careRecommendation = "Pinch off the tiny extra shoots ('suckers') growing between the main stem and leaf branches so the plant grows tall.",
            irrigationAdvice = "Water 1 tabo (1–2 liters) per plant every 2 days in the morning. Always water the soil, never splash the leaves.",
            nutritionAdvice = "Spread 1 handful of vermicast or kitchen compost around the base, or water with rice-wash water (hugas-bigas).",
            criticalRisks = "Letting vines crawl on wet ground invites leaf rot; keep stems tied upright on bamboo sticks.",
            sourceCitation = "UPLB College of Agriculture Vegetable Management Compendium"
        ),
        CropGrowthStageEntity(
            id = "stage_tomato_3_flowering",
            cropName = "Tomato",
            stageIndex = 3,
            stageName = "FLOWERING & FRUIT SETTING",
            dayStart = 39,
            dayEnd = 54,
            primaryFarmerAction = "Inspect flower clusters for tiny green caterpillars and support heavy fruiting branches.",
            careRecommendation = "Remove yellowed lower leaves near the ground to let air circulate and sun reach the tomatoes.",
            irrigationAdvice = "Water steadily every 2 days. Skipping watering then flooding will cause the tomatoes to split and crack.",
            nutritionAdvice = "Sprinkle a spoonful of crushed eggshells (for calcium) or a pinch of wood ash around the plant to prevent bottom-rot.",
            criticalRisks = "Fruit borer caterpillars boring into green tomatoes; bottom end of fruit turning black from dry soil.",
            sourceCitation = "DA-BPI Solanaceous Production Standards"
        ),
        CropGrowthStageEntity(
            id = "stage_tomato_4_harvest",
            cropName = "Tomato",
            stageIndex = 4,
            stageName = "HARVEST TIME",
            dayStart = 55,
            dayEnd = 65,
            primaryFarmerAction = "Gently twist or clip tomatoes when they turn light-pink or orange-red in the cool morning.",
            careRecommendation = "Leave the green cap on top of the tomato so it stays fresh for up to two weeks on the counter.",
            irrigationAdvice = "Water a little less now so the ripe tomatoes are sweeter and do not split.",
            nutritionAdvice = "No more compost or fertilizer needed. Enjoy your fresh homegrown tomatoes!",
            criticalRisks = "Overripe tomatoes falling and attracting ants or birds. Pick every 2 to 3 days.",
            sourceCitation = "DA-BPI Post-Harvest Horticulture Guide"
        )
    )

    val tomatoSoilCompatibilities: List<CropSoilCompatibilityEntity> = listOf(
        CropSoilCompatibilityEntity(
            id = "soil_compat_tomato_loam",
            cropName = "Tomato",
            soilType = "LOAM",
            suitabilityRating = "OPTIMAL",
            suitabilityScore = 1.00f,
            agronomicRationale = "Loam is the perfect garden soil—soft, dark, crumbly, and drains water easily without drying out.",
            amendmentAction = "Mix in 1 to 2 handfuls of compost or vermicast per plant before planting.",
            alertWarning = null,
            sourceCitation = "BSWM Soil Fertility and Management Guidelines & DA-BPI Standards"
        ),
        CropSoilCompatibilityEntity(
            id = "soil_compat_tomato_clay",
            cropName = "Tomato",
            soilType = "CLAY",
            suitabilityRating = "MARGINAL",
            suitabilityScore = 0.50f,
            agronomicRationale = "Heavy clay soil holds too much water like sticky mud. Tomato roots cannot breathe and rot quickly if flooded.",
            amendmentAction = "Build your planting bed at least 1 foot high (raised bed) and mix burnt rice hull (CRH) or dry compost to make the soil crumbly.",
            alertWarning = "DANGER OF ROOT ROT: Heavy clay soil stays wet too long after rain. Make sure your garden bed is raised so water drains away.",
            sourceCitation = "DA-BPI Lowland Vegetable Production Bulletin 2024"
        ),
        CropSoilCompatibilityEntity(
            id = "soil_compat_tomato_sandy",
            cropName = "Tomato",
            soilType = "SANDY",
            suitabilityRating = "SUITABLE",
            suitabilityScore = 0.75f,
            agronomicRationale = "Sandy soil is loose and drains water very fast, but dries out quickly and loses plant food under hot sun.",
            amendmentAction = "Cover the soil with a 2-inch blanket of dried leaves, grass, or sawdust (Camiguin method) to keep moisture in.",
            alertWarning = "Dries out fast: Water more frequently in small amounts rather than drowning it all at once.",
            sourceCitation = "PCARRD Philippine Recommends for Soil & Water Conservation & Camiguin Study 2016"
        ),
        CropSoilCompatibilityEntity(
            id = "soil_compat_tomato_silty",
            cropName = "Tomato",
            soilType = "SILTY",
            suitabilityRating = "SUITABLE",
            suitabilityScore = 0.75f,
            agronomicRationale = "Smooth silt soil holds nutrients well but the top layer can form a hard crust after strong rain.",
            amendmentAction = "Gently scratch the topsoil with a hand trowel after heavy rains and cover with dried leaves.",
            alertWarning = null,
            sourceCitation = "DA-BAR Lowland Vegetables Production Handbook"
        ),
        CropSoilCompatibilityEntity(
            id = "soil_compat_tomato_peaty",
            cropName = "Tomato",
            soilType = "PEATY",
            suitabilityRating = "MARGINAL",
            suitabilityScore = 0.50f,
            agronomicRationale = "Very dark peaty soil can be naturally sour (acidic), which prevents the tomato plant from absorbing calcium.",
            amendmentAction = "Mix crushed eggshells or agricultural lime into the soil 2 weeks before planting to sweeten the soil.",
            alertWarning = "Sour soil risk: Acidic peat causes the bottoms of tomatoes to turn black (blossom-end rot). Add crushed eggshells.",
            sourceCitation = "BSWM Lime Application Technical Guide"
        ),
        CropSoilCompatibilityEntity(
            id = "soil_compat_tomato_chalky",
            cropName = "Tomato",
            soilType = "CHALKY",
            suitabilityRating = "POOR",
            suitabilityScore = 0.25f,
            agronomicRationale = "Chalky alkaline soil (with white limestone) causes tomato leaves to turn pale yellow and stunts growth.",
            amendmentAction = "Add lots of rich dark organic compost, coffee grounds, and dried leaves to condition the soil.",
            alertWarning = "Yellow leaf risk: Plants struggle in chalky soil without lots of dark organic compost added.",
            sourceCitation = "Philippine Agricultural Reference Guide"
        )
    )

    val tomatoPestDiseaseGuides: List<CropPestDiseaseGuideEntity> = listOf(
        CropPestDiseaseGuideEntity(
            id = "pest_tomato_bacterial_wilt",
            cropName = "Tomato",
            pestDiseaseName = "Bacterial Wilt",
            localNamePh = "Lanta ng Kamatis",
            scientificName = "Ralstonia solanacearum",
            category = "BACTERIAL",
            riskSeason = "WET",
            criticalStageIndex = 2,
            symptoms = "The entire tomato plant suddenly wilts and droops at midday while the leaves are still completely green.",
            organicBiocontrol = "Water the soil with bio-fungicide (Trichoderma) or sprinkle carbonized rice hull around the base.",
            culturalPrevention = "Plant in raised garden beds so rainwater drains away quickly. Never plant tomatoes where eggplants or bell peppers just died.",
            sourceCitation = "DA-BPI Integrated Pest Management (IPM) Technical Guide"
        ),
        CropPestDiseaseGuideEntity(
            id = "pest_tomato_fruit_borer",
            cropName = "Tomato",
            pestDiseaseName = "Tomato Fruit Borer",
            localNamePh = "Harabas / Uod sa Bunga",
            scientificName = "Helicoverpa armigera",
            category = "INSECT",
            riskSeason = "YEAR_ROUND",
            criticalStageIndex = 3,
            symptoms = "Small round holes drilled into green and red tomatoes with worm dirt inside; affected fruits rot and drop off.",
            organicBiocontrol = "Spray safe organic Bt (Bacillus thuringiensis) or neem oil spray directly on flower clusters.",
            culturalPrevention = "Check plants in the early morning and handpick any green caterpillars. Plant bright marigold flowers around your beds to repel moths.",
            sourceCitation = "DA-BPI Lowland Pest Management Compendium"
        ),
        CropPestDiseaseGuideEntity(
            id = "pest_tomato_whitefly_tylcv",
            cropName = "Tomato",
            pestDiseaseName = "Whitefly / Leaf Curl",
            localNamePh = "Puting Langaw / Kulot ng Kamatis",
            scientificName = "Bemisia tabaci / TyLCV",
            category = "VIRAL",
            riskSeason = "DRY",
            criticalStageIndex = 1,
            symptoms = "Leaves curl upwards like cups and turn yellow; tiny white powdery insects flutter when you shake the plant.",
            organicBiocontrol = "Hang yellow plastic cups/boards coated with cooking oil to catch them; spray mild neem or garlic-soap water under leaves.",
            culturalPrevention = "Choose resistant seeds like Diamante Max F1; cover the surrounding soil with dried straw or reflective foil.",
            sourceCitation = "DA-BPI Virus & Vector Management Bulletin"
        )
    )
}

