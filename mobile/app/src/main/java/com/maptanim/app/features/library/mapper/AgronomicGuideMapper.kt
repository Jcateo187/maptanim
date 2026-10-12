package com.maptanim.app.features.library.mapper

import com.maptanim.app.data.local.entity.*
import com.maptanim.app.domain.model.Crop
import com.maptanim.app.features.library.model.*

/**
 * AgronomicGuideMapper — Transforms Room database entities into rich, structured
 * VegetableAgronomicGuide representations for UI consumption.
 */
object AgronomicGuideMapper {

    fun mapToGuide(
        crop: Crop,
        varieties: List<CropVarietyEntity>,
        growthStages: List<CropGrowthStageEntity>,
        soilCompatibilities: List<CropSoilCompatibilityEntity>,
        pestGuides: List<CropPestDiseaseGuideEntity>,
        yieldStudies: List<CropYieldStudyEntity>
    ): VegetableAgronomicGuide {
        val cropName = crop.name

        // 1. Overview Info
        val overview = OverviewInfo(
            summary = crop.description?.ifBlank {
                "${crop.name} is a high-value vegetable well-suited for Philippine backyard and smallholder cultivation."
            } ?: "${crop.name} is a high-value vegetable well-suited for Philippine backyard and smallholder cultivation.",
            botanicalName = resolveBotanicalName(cropName),
            family = resolveFamily(cropName),
            culinaryUses = resolveCulinaryUses(cropName),
            regionalSuitability = "Nationwide (Ilocos, Central Luzon, Southern Tagalog, Bicol, Visayas, Northern Mindanao)",
            agriculturalImportance = "Key nutrition and cash crop highlighted under the Department of Agriculture Urban and Peri-Urban Agriculture Program (DA-UPIP)."
        )

        // 2. Varieties
        val mappedVarieties = varieties.map { v ->
            VarietyDetail(
                name = v.varietyName,
                localName = v.localNamePh ?: v.varietyName,
                daysToHarvest = v.growthDurationDays,
                characteristics = v.description ?: "Recommended Philippine commercial cultivar.",
                diseaseResistance = v.diseaseResistance ?: "General tolerance to tropical pests.",
                optimalSeason = v.optimalSeasonsCsv.replace(",", ", "),
                stageDays = mapOf(
                    "Sprout" to v.stage1SproutDays,
                    "Seedling" to v.stage2SeedlingDays,
                    "Vegetative" to v.stage3VegetativeDays,
                    "Flowering" to v.stage4FloweringDays,
                    "Harvest" to v.stage5HarvestDays
                )
            )
        }.ifEmpty {
            listOf(
                VarietyDetail(
                    name = "${crop.name} Standard",
                    localName = crop.name,
                    daysToHarvest = crop.daysToHarvest,
                    characteristics = "Adapted to lowland tropical backyard beds.",
                    diseaseResistance = "Standard tropical vigor.",
                    optimalSeason = "YEAR_ROUND"
                )
            )
        }

        // 3. Growing Season Info
        val growingSeason = GrowingSeasonInfo(
            drySeasonStatus = "Optimal Growth with Regular Irrigation (November – April)",
            wetSeasonStatus = "Requires Raised Beds to Prevent Waterlogging (May – October)",
            optimalTemperature = "24°C – 32°C (Warm Tropical Lowlands)",
            peakMonths = "November to April (Dry Season Peak)",
            climateRisks = "Prolonged monsoon rainfall and high humidity encourage fungal and bacterial pathogens.",
            weatherTips = listOf(
                "Elevate beds 20–30 cm during rainy season for rapid surface drainage.",
                "Mulch with rice straw or carbonized rice hulls to preserve root zone moisture in summer.",
                "Erect temporary rain shelters or plastic tunnels during heavy typhoon months."
            )
        )

        // 4. Soil Info
        val optimalSoils = soilCompatibilities
            .filter { it.suitabilityRating == "OPTIMAL" }
            .joinToString(", ") { it.soilType }
            .ifBlank { "Loam, Sandy Loam" }

        val soil = SoilInfo(
            idealSoilTypes = optimalSoils,
            optimalPh = "6.0 – 6.8 (Slightly Acidic to Neutral)",
            drainage = "Well-drained with high organic percolation capacity",
            landPrep = "Thorough tilling to 20 cm depth, incorporate decomposed compost and carbonized rice hull (CRH).",
            organicMatter = "At least 3–5% organic matter; mix 1 shovel of vermicompost per square meter.",
            recommendations = soilCompatibilities.mapNotNull { compat ->
                compat.amendmentAction?.let { "${compat.soilType}: $it" }
            }.ifEmpty {
                listOf(
                    "Loam: Incorporate 1 bucket well-rotted compost per 3 square meters.",
                    "Clay: Construct 25 cm raised beds and mix 30% organic mulch and CRH.",
                    "Sand: Add abundant vermicompost to retain moisture and soluble nutrients."
                )
            }
        )

        // 5. Planting Info
        val planting = PlantingInfo(
            method = if (isDirectSeeded(cropName)) "Direct Seeding" else "Seedbed / Seedling Tray Transplanting",
            germinationDays = "${crop.daysToHarvest / 12}–${crop.daysToHarvest / 8} Days",
            transplantAge = if (isDirectSeeded(cropName)) "N/A (Direct Sown)" else "18–25 Days (with 4–5 true leaves)",
            plantSpacing = "${resolvePlantSpacing(cropName)} cm between plants",
            rowSpacing = "${(resolvePlantSpacing(cropName) * 1.5).toInt()} cm between rows",
            plantingDepth = if (isDirectSeeded(cropName)) "2.0 – 3.0 cm" else "0.5 cm in seedling cells",
            trellisingNeeded = needsTrellis(cropName),
            trellisingAdvice = if (needsTrellis(cropName)) {
                "Erect sturdy 1.8m–2.0m bamboo trellis (A-frame or vertical teepee) before vines begin active running."
            } else null,
            tips = listOf(
                "Transplant seedlings in late afternoon (4:00 PM) to minimize heat stress.",
                "Water seedlings thoroughly 1 hour before pulling to keep root ball intact.",
                "Gently press soil around the root crown without compressing the stem."
            )
        )

        // 6. Watering Info
        val watering = WateringInfo(
            frequency = if (cropName.contains("Kangkong") || cropName.contains("Pechay") || cropName.contains("Lettuce")) {
                "Daily (1–2 times per day in dry season)"
            } else {
                "Every 2–3 Days (Deep root soaking)"
            },
            bestTime = "Early morning (6:00 AM – 8:00 AM)",
            criticalStages = "Germination, transplanting establishment, and flowering/fruit setting.",
            irrigationType = "Drip irrigation, watering can rose at soil level, or furrow irrigation.",
            moistureConservation = "Mulch with clean dried rice straw, cogon grass, or wood shavings.",
            warnings = listOf(
                "Never wet leaves during late afternoon; damp night foliage invites fungal blight.",
                "Avoid standing puddles around stem collars to prevent bacterial wilt.",
                "Inconsistent moisture during fruit swelling causes fruit cracking and blossom end rot."
            )
        )

        // 7. Fertilization Info
        val fertilization = FertilizationInfo(
            npkRatio = resolveNpkRatio(cropName),
            basalApplication = "Mix 2 shovels of aged compost + 1 handful complete organic fertilizer per bed meter.",
            sideDressing = "Apply compost tea or fermented plant juice (FPJ) every 10–14 days.",
            organicOptions = "Vermicompost, fermented fruit juice (FFJ), carbonized rice hull (CRH), bone meal.",
            micronutrients = "Calcium and boron foliar spray during blooming to ensure flower retention.",
            schedule = listOf(
                "Basal (Day 0): Incorporate rich compost into planting beds.",
                "Vegetative (Day 15–20): Nitrogen-rich compost tea or fish amino acid (FAA).",
                "Flowering/Fruiting (Day 35+): Potassium-rich fermented fruit juice (FFJ) and wood ash."
            )
        )

        // 8. Growth Stages
        val mappedGrowthStages = growthStages.map { s ->
            GrowthStageItem(
                stageNumber = s.stageIndex,
                stageName = s.stageName,
                durationDays = "${s.dayStart}–${s.dayEnd} Days",
                description = s.careRecommendation,
                farmerAction = s.primaryFarmerAction
            )
        }.ifEmpty {
            listOf(
                GrowthStageItem(0, "Germination", "1–5 Days", "Keep seedbed consistently moist in shade.", "Morning fine misting."),
                GrowthStageItem(1, "Seedling", "6–20 Days", "Provide bright gentle morning sunlight.", "Thin out weak seedlings."),
                GrowthStageItem(2, "Vegetative", "21–45 Days", "Rapid stem and leaf expansion.", "Side-dress organic compost."),
                GrowthStageItem(3, "Flowering / Fruiting", "46–60 Days", "Bud development and fruit set.", "Apply potassium fertilizer."),
                GrowthStageItem(4, "Harvest", "61+ Days", "Pick mature crops in early morning.", "Store in cool shade.")
            )
        }

        // 9. Pests and Diseases
        val mappedPests = pestGuides.map { p ->
            PestDiseaseItem(
                name = p.pestDiseaseName,
                type = if (p.category == "INSECT") "Insect Pest" else "Plant Disease",
                symptoms = p.symptoms,
                organicControl = p.organicBiocontrol,
                prevention = p.culturalPrevention,
                imageAsset = AgronomicAssetHelper.resolvePestImage(p.pestDiseaseName)
            )
        }.ifEmpty {
            listOf(
                PestDiseaseItem(
                    name = "Common Vegetable Aphids",
                    type = "Insect Pest",
                    symptoms = "Clusters of tiny green/black sap-sucking insects curling tender young leaf tips.",
                    organicControl = "Spray neem oil extract (5ml/L) or mild dish soap solution directly on colonies.",
                    prevention = "Encourage natural predators (ladybird beetles); plant marigolds and basil nearby."
                )
            )
        }

        // 10. Companion Info
        val companionPlants = CompanionInfo(
            beneficialCompanions = resolveCompanions(cropName),
            companionBenefits = "Repels insect pests, enhances soil microbial biodiversity, and improves space utilization.",
            plantsToAvoid = resolveAntagonists(cropName),
            avoidReasons = "Compete for identical root nutrients or attract shared soil-borne bacterial pathogens."
        )

        // 11. Intercropping Info
        val intercropping = IntercroppingInfo(
            recommendedCrops = resolveCompanions(cropName).take(3),
            spatialLayout = "Alternate rows or plant companion herbs along bed perimeters.",
            benefits = "Maximizes vertical canopy and ground surface space while confounding insect pests.",
            managementAdvice = "Ensure companion crops do not shade out slow-growing base vegetable crops."
        )

        // 12. Harvest Info
        val harvest = HarvestInfo(
            maturityIndicators = "Firm fruit color development, crisp snapping pods, or mature leafy head rosettes.",
            daysRange = "${crop.daysToHarvest - 5}–${crop.daysToHarvest + 10} Days after sowing",
            harvestingMethod = "Use clean, sharp pruning shears or harvest knife. Avoid twisting or pulling stems.",
            timeOfDay = "Early morning (6:00 AM – 8:30 AM) when plants are fully hydrated.",
            frequency = "Every 2–3 days for indeterminate fruiting vegetables; single cut for leafy greens.",
            indicatorsList = listOf(
                "Fruits or heads have reached standard varietal size and glossy skin tone.",
                "Petioles snap crisply without fibrous stringiness.",
                "Seeds inside fruits are still soft and tender (not hard and brown)."
            )
        )

        // 13. Post-Harvest Info
        val postHarvest = PostHarvestInfo(
            sortingGrading = "Sort out bruised, damaged, or insect-punctured produce immediately to protect clean batch.",
            washingCleaning = "Rinse gently in clean potable water; air dry in shaded, well-ventilated breeze.",
            storageConditions = "Store in perforated crates or clean baskets in cool, dry indoor storage.",
            optimalTemperature = if (cropName.contains("Tomato") || cropName.contains("Eggplant")) "12°C – 15°C (Cool Pantry)" else "4°C – 8°C (Refrigerated)",
            relativeHumidity = "85% – 90% RH to prevent wilting and moisture shrinkage.",
            packagingTransport = "Cushion crates with clean banana leaves; avoid stacking heavier crates directly on produce.",
            shelfLife = "4–7 days fresh room temperature; up to 14 days under proper refrigeration."
        )

        return VegetableAgronomicGuide(
            crop = crop,
            overview = overview,
            varieties = mappedVarieties,
            growingSeason = growingSeason,
            soil = soil,
            planting = planting,
            watering = watering,
            fertilization = fertilization,
            growthStages = mappedGrowthStages,
            pestsAndDiseases = mappedPests,
            companionPlants = companionPlants,
            intercropping = intercropping,
            harvest = harvest,
            postHarvest = postHarvest
        )
    }

    private fun resolveBotanicalName(crop: String): String = when {
        crop.contains("Tomato", true) -> "Solanum lycopersicum"
        crop.contains("Eggplant", true) -> "Solanum melongena"
        crop.contains("Chili", true) || crop.contains("Sili", true) -> "Capsicum annuum / C. frutescens"
        crop.contains("Okra", true) -> "Abelmoschus esculentus"
        crop.contains("Pechay", true) -> "Brassica rapa subsp. chinensis"
        crop.contains("Lettuce", true) -> "Lactuca sativa"
        crop.contains("Kangkong", true) -> "Ipomoea aquatica"
        crop.contains("Cucumber", true) || crop.contains("Pipino", true) -> "Cucumis sativus"
        crop.contains("Yardlong", true) || crop.contains("Sitaw", true) -> "Vigna unguiculata subsp. sesquipedalis"
        crop.contains("Corn", true) || crop.contains("Mais", true) -> "Zea mays var. saccharata"
        else -> "Horticultural Cultivar"
    }

    private fun resolveFamily(crop: String): String = when {
        crop.contains("Tomato", true) || crop.contains("Eggplant", true) || crop.contains("Chili", true) -> "Solanaceae (Nightshade Family)"
        crop.contains("Okra", true) -> "Malvaceae (Mallow Family)"
        crop.contains("Pechay", true) -> "Brassicaceae (Mustard Family)"
        crop.contains("Lettuce", true) -> "Asteraceae (Daisy / Lettuce Family)"
        crop.contains("Kangkong", true) -> "Convolvulaceae (Morning Glory Family)"
        crop.contains("Cucumber", true) || crop.contains("Pipino", true) -> "Cucurbitaceae (Gourd Family)"
        crop.contains("Yardlong", true) || crop.contains("Sitaw", true) -> "Fabaceae (Legume Family)"
        crop.contains("Corn", true) || crop.contains("Mais", true) -> "Poaceae (Grass Family)"
        else -> "Vegetable Crop"
    }

    private fun resolveCulinaryUses(crop: String): String = when {
        crop.contains("Tomato", true) -> "Fresh in ensaladas, sauteed in sinigang, pinakbet, and traditional sauces."
        crop.contains("Eggplant", true) -> "Tortang talong, pinakbet, ensaladang talong, and grilled dishes."
        crop.contains("Chili", true) -> "Sinigang, sawsawan, Bicol express, and fresh culinary spicing."
        crop.contains("Okra", true) -> "Sinigang, pinakbet, steamed with bagoong, and healthy broths."
        crop.contains("Pechay", true) -> "Nilaga, bulalo, stir-fries, and quick nutritious vegetable soups."
        crop.contains("Lettuce", true) -> "Fresh garden salads, samgyupsal wraps, burgers, and spring rolls."
        crop.contains("Kangkong", true) -> "Adobong kangkong, sinigang sa sampalok, and crispy deep-fried leaves."
        crop.contains("Cucumber", true) -> "Refreshing salads, atsara pickling, fresh juicing, and cold dips."
        crop.contains("Yardlong", true) -> "Adobong sitaw, pinakbet, sinigang, and sauteed with ground meat."
        crop.contains("Corn", true) -> "Boiled sweet corn on the cob, corn soup with moringa, and fresh snacks."
        else -> "Nutritious Philippine home-cooked dishes."
    }

    private fun resolveNpkRatio(crop: String): String = when {
        crop.contains("Pechay", true) || crop.contains("Kangkong", true) || crop.contains("Lettuce", true) -> "3-1-2 (High Nitrogen for foliar leaf growth)"
        crop.contains("Tomato", true) || crop.contains("Eggplant", true) || crop.contains("Chili", true) || crop.contains("Cucumber", true) -> "1-2-2 (High Phosphorus & Potassium for flowering and fruiting)"
        crop.contains("Yardlong", true) -> "1-2-1 (Low Nitrogen; legumes fix own nitrogen via root nodules)"
        crop.contains("Corn", true) -> "3-1-2 (High Nitrogen & Potassium for strong stalks and full ears)"
        else -> "2-1-2 (Balanced Complete Nutrition)"
    }

    private fun isDirectSeeded(crop: String): Boolean =
        crop.contains("Corn", true) || crop.contains("Okra", true) ||
        crop.contains("Yardlong", true) || crop.contains("Kangkong", true) ||
        crop.contains("Cucumber", true)

    private fun resolvePlantSpacing(crop: String): Int = when {
        crop.contains("Corn", true) -> 25
        crop.contains("Eggplant", true) || crop.contains("Tomato", true) -> 50
        crop.contains("Chili", true) || crop.contains("Sili", true) -> 40
        crop.contains("Okra", true) || crop.contains("Cucumber", true) || crop.contains("Yardlong", true) || crop.contains("Sitaw", true) -> 30
        crop.contains("Pechay", true) || crop.contains("Lettuce", true) -> 15
        crop.contains("Kangkong", true) -> 10
        else -> 30
    }

    private fun needsTrellis(crop: String): Boolean =
        crop.contains("Yardlong", true) || crop.contains("Cucumber", true)

    private fun resolveCompanions(crop: String): List<String> = when {
        crop.contains("Tomato", true) -> listOf("Sweet Basil", "Marigold", "Garlic", "Onion")
        crop.contains("Eggplant", true) -> listOf("Marigold", "Bush Beans", "Basil")
        crop.contains("Chili", true) -> listOf("Onion", "Garlic", "Basil", "Carrot")
        crop.contains("Okra", true) -> listOf("Sweet Pepper", "Eggplant", "Melon")
        crop.contains("Pechay", true) -> listOf("Mint", "Lemongrass", "Garlic", "Celery")
        crop.contains("Lettuce", true) -> listOf("Chives", "Carrot", "Radish", "Cucumber")
        crop.contains("Kangkong", true) -> listOf("Pechay", "Mustard", "Spring Onion")
        crop.contains("Cucumber", true) -> listOf("Corn", "Radish", "Sunflower", "Marigold")
        crop.contains("Yardlong", true) -> listOf("Corn", "Cucumber", "Radish", "Eggplant")
        crop.contains("Corn", true) -> listOf("Yardlong Bean", "Squash", "Cucumber", "Melon")
        else -> listOf("Marigold", "Basil", "Green Onion")
    }

    private fun resolveAntagonists(crop: String): List<String> = when {
        crop.contains("Tomato", true) -> listOf("Corn (shares earworm)", "Fennel", "Potato")
        crop.contains("Eggplant", true) -> listOf("Fennel")
        crop.contains("Chili", true) -> listOf("Fennel")
        crop.contains("Pechay", true) -> listOf("Tomato (flea beetles)", "Strawberry")
        crop.contains("Yardlong", true) -> listOf("Onion (inhibits rhizobia)", "Garlic")
        crop.contains("Corn", true) -> listOf("Tomato (shared corn earworm / fruit borer)")
        else -> listOf("Fennel")
    }
}
