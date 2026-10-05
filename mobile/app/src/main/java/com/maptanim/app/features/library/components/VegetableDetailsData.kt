package com.maptanim.app.features.library.components

import android.content.Context
import com.maptanim.app.data.datasource.CropMetadataAssetDataSource
import com.maptanim.app.domain.model.Crop

data class OverviewInfo(
    val summary: String,
    val botanicalName: String,
    val family: String,
    val culinaryUses: String,
    val regionalSuitability: String,
    val agriculturalImportance: String
)

data class VarietyDetail(
    val name: String,
    val localName: String,
    val daysToHarvest: Int,
    val characteristics: String,
    val diseaseResistance: String,
    val optimalSeason: String,
    val stageDays: Map<String, Int>? = null
)

data class GrowingSeasonInfo(
    val drySeasonStatus: String,
    val wetSeasonStatus: String,
    val optimalTemperature: String,
    val peakMonths: String,
    val climateRisks: String,
    val weatherTips: List<String>
)

data class SoilInfo(
    val idealSoilTypes: String,
    val optimalPh: String,
    val drainage: String,
    val landPrep: String,
    val organicMatter: String,
    val recommendations: List<String>
)

data class PlantingInfo(
    val method: String,
    val germinationDays: String,
    val transplantAge: String,
    val plantSpacing: String,
    val rowSpacing: String,
    val plantingDepth: String,
    val trellisingNeeded: Boolean,
    val trellisingAdvice: String?,
    val tips: List<String>
)

data class WateringInfo(
    val frequency: String,
    val bestTime: String,
    val criticalStages: String,
    val irrigationType: String,
    val moistureConservation: String,
    val warnings: List<String>
)

data class FertilizationInfo(
    val npkRatio: String,
    val basalApplication: String,
    val sideDressing: String,
    val organicOptions: String,
    val micronutrients: String,
    val schedule: List<String>
)

data class GrowthStageItem(
    val stageNumber: Int,
    val stageName: String,
    val durationDays: String,
    val description: String,
    val farmerAction: String
)

data class PestDiseaseItem(
    val name: String,
    val type: String, // "Insect Pest" or "Disease"
    val symptoms: String,
    val organicControl: String,
    val prevention: String,
    val chemicalControl: String? = null,
    val imageAsset: String? = null
)

object AgronomicAssetHelper {
    fun resolvePestImage(pestName: String, explicitAsset: String? = null): String? {
        if (!explicitAsset.isNullOrBlank()) return explicitAsset
        val lower = pestName.lowercase()
        return when {
            lower.contains("fruit borer") || (lower.contains("borer") && !lower.contains("shoot")) -> "file:///android_asset/metadata/pest/Fruit_borer.png"
            lower.contains("leaf curl") || lower.contains("tylcv") -> "file:///android_asset/metadata/pest/Tomato_leaf_curlvirus.png"
            lower.contains("bacterial wilt") || lower.contains("ralstonia") -> "file:///android_asset/metadata/pest/Bacterial_wilt.png"
            lower.contains("leafminer") || lower.contains("miner") -> "file:///android_asset/metadata/pest/Vegetable_leafminer.png"
            lower.contains("flea beetle") || lower.contains("fleabeetle") -> "file:///android_asset/metadata/pest/Eggplant_and_brassica_fleabeetle.png"
            lower.contains("diamondback") || lower.contains("dbm") -> "file:///android_asset/metadata/pest/Diamondback_moth.png"
            lower.contains("aphid") -> "file:///android_asset/metadata/pest/Melon_and_cotton_aphids.png"
            lower.contains("anthracnose") -> "file:///android_asset/metadata/pest/Chilli_anthracnose_fruit_rot.png"
            lower.contains("downy mildew") || lower.contains("downy") -> "file:///android_asset/metadata/pest/Cucurbit_downy_mildew.png"
            lower.contains("powdery mildew") || lower.contains("powdery") -> "file:///android_asset/metadata/pest/Powdery_mildew.png"
            lower.contains("armyworm") || lower.contains("spodoptera") -> "file:///android_asset/metadata/pest/Fall_armyworm.png"
            lower.contains("thrip") -> "file:///android_asset/metadata/pest/Onion_thrips.png"
            else -> null
        }
    }

    fun resolveSoilImage(soilTypes: String): String {
        val lower = soilTypes.lowercase()
        return when {
            lower.contains("sandy loam") || lower.contains("sandy") -> "file:///android_asset/metadata/soil_images/Sandy_soil.png"
            lower.contains("clay loam") || lower.contains("clay") -> "file:///android_asset/metadata/soil_images/Clay_soil.png"
            lower.contains("silt") -> "file:///android_asset/metadata/soil_images/Silty_soil.png"
            lower.contains("peat") -> "file:///android_asset/metadata/soil_images/Peaty_soil.png"
            lower.contains("chalk") -> "file:///android_asset/metadata/soil_images/Chalky_soil.png"
            else -> "file:///android_asset/metadata/soil_images/Loam_soil.png"
        }
    }

    fun resolveCompanionCropImage(plantName: String): String? {
        val lower = plantName.lowercase()
        return when {
            lower.contains("tomato") || lower.contains("kamatis") -> "file:///android_asset/metadata/crops_images/tomato.png"
            lower.contains("eggplant") || lower.contains("talong") -> "file:///android_asset/metadata/crops_images/eggplant.png"
            lower.contains("pechay") || lower.contains("petsay") -> "file:///android_asset/metadata/crops_images/pechay.png"
            lower.contains("carrot") || lower.contains("karot") -> "file:///android_asset/metadata/crops_images/carrot.png"
            lower.contains("corn") || lower.contains("mais") -> "file:///android_asset/metadata/crops_images/corn.png"
            lower.contains("onion") || lower.contains("sibuyas") || lower.contains("garlic") -> "file:///android_asset/metadata/crops_images/onion.png"
            lower.contains("pipino") || lower.contains("cucumber") -> "file:///android_asset/metadata/crops_images/pipino.png"
            lower.contains("sitaw") || lower.contains("bean") -> "file:///android_asset/metadata/crops_images/sitaw.png"
            lower.contains("cabbage") || lower.contains("repolyo") -> "file:///android_asset/metadata/crops_images/cabbage.png"
            lower.contains("pepper") || lower.contains("sili") -> "file:///android_asset/metadata/crops_images/sili.png"
            lower.contains("ampalaya") || lower.contains("bitter") -> "file:///android_asset/metadata/crops_images/ampalaya.png"
            lower.contains("okra") -> "file:///android_asset/metadata/crops_images/okra.png"
            lower.contains("lettuce") || lower.contains("litsugas") -> "file:///android_asset/metadata/crops_images/lettuce.png"
            lower.contains("kangkong") -> "file:///android_asset/metadata/crops_images/kangkong.png"
            lower.contains("pumpkin") || lower.contains("kalabasa") || lower.contains("squash") -> "file:///android_asset/metadata/crops_images/pumpkin.png"
            else -> null
        }
    }
}

data class CompanionInfo(
    val beneficialCompanions: List<String>,
    val companionBenefits: String,
    val plantsToAvoid: List<String>,
    val avoidReasons: String
)

data class IntercroppingInfo(
    val recommendedCrops: List<String>,
    val spatialLayout: String,
    val benefits: String,
    val managementAdvice: String
)

data class HarvestInfo(
    val maturityIndicators: String,
    val daysRange: String,
    val harvestingMethod: String,
    val timeOfDay: String,
    val frequency: String,
    val indicatorsList: List<String>
)

data class PostHarvestInfo(
    val sortingGrading: String,
    val washingCleaning: String,
    val storageConditions: String,
    val optimalTemperature: String,
    val relativeHumidity: String,
    val packagingTransport: String,
    val shelfLife: String
)

data class VegetableAgronomicGuide(
    val crop: Crop,
    val overview: OverviewInfo,
    val varieties: List<VarietyDetail>,
    val growingSeason: GrowingSeasonInfo,
    val soil: SoilInfo,
    val planting: PlantingInfo,
    val watering: WateringInfo,
    val fertilization: FertilizationInfo,
    val growthStages: List<GrowthStageItem>,
    val pestsAndDiseases: List<PestDiseaseItem>,
    val companionPlants: CompanionInfo,
    val intercropping: IntercroppingInfo,
    val harvest: HarvestInfo,
    val postHarvest: PostHarvestInfo
)

object VegetableGuideProvider {

    fun getGuideForCrop(crop: Crop, context: Context?): VegetableAgronomicGuide {
        val meta = CropMetadataAssetDataSource.getCropMetadataByName(context, crop.name)
        val nameLower = crop.name.lowercase()

        return when {
            nameLower.contains("tomato") || nameLower.contains("kamatis") -> createTomatoGuide(crop, meta)
            nameLower.contains("eggplant") || nameLower.contains("talong") -> createEggplantGuide(crop, meta)
            nameLower.contains("pechay") || nameLower.contains("petsay") -> createPechayGuide(crop, meta)
            nameLower.contains("carrot") || nameLower.contains("karot") -> createCarrotGuide(crop, meta)
            nameLower.contains("ampalaya") || nameLower.contains("bitter") -> createAmpalayaGuide(crop, meta)
            nameLower.contains("sitaw") || nameLower.contains("string") || nameLower.contains("yardlong") -> createSitawGuide(crop, meta)
            nameLower.contains("okra") -> createOkraGuide(crop, meta)
            nameLower.contains("cabbage") || nameLower.contains("repolyo") -> createCabbageGuide(crop, meta)
            nameLower.contains("pepper") || nameLower.contains("sili") -> createChiliGuide(crop, meta)
            nameLower.contains("corn") || nameLower.contains("mais") -> createCornGuide(crop, meta)
            nameLower.contains("kangkong") -> createKangkongGuide(crop, meta)
            nameLower.contains("lettuce") || nameLower.contains("litsugas") -> createLettuceGuide(crop, meta)
            nameLower.contains("onion") || nameLower.contains("sibuyas") -> createOnionGuide(crop, meta)
            nameLower.contains("pipino") || nameLower.contains("cucumber") -> createCucumberGuide(crop, meta)
            nameLower.contains("pumpkin") || nameLower.contains("kalabasa") || nameLower.contains("squash") -> createSquashGuide(crop, meta)
            else -> createGenericGuide(crop, meta)
        }
    }

    private fun createTomatoGuide(crop: Crop, meta: com.maptanim.app.data.datasource.CropMetadataInfo?): VegetableAgronomicGuide {
        return VegetableAgronomicGuide(
            crop = crop,
            overview = OverviewInfo(
                summary = "High-demand culinary Solanaceous fruit vegetable grown extensively in Ilocos, Bukidnon, and Central Luzon. Rich in lycopene, Vitamin C, and potassium.",
                botanicalName = "Solanum lycopersicum",
                family = "Solanaceae (Nightshade Family)",
                culinaryUses = "Essential base for Sinigang, Pinakbet, Menudo, Afritada, salads, and sawsawan dips.",
                regionalSuitability = "Central Luzon, Ilocos Region, Bukidnon, Southern Tagalog.",
                agriculturalImportance = "Top commercial vegetable in the Philippines with year-round market demand and high return per square meter."
            ),
            varieties = listOf(
                VarietyDetail(
                    name = "Diamante Max F1",
                    localName = "Kamatis Diamante Max F1",
                    daysToHarvest = 60,
                    characteristics = "Thick-walled, firm oval fruits, high shipping tolerance, deep red when ripe.",
                    diseaseResistance = "High resistance to Tomato Yellow Leaf Curl Virus (TyLCV) and Bacterial Wilt.",
                    optimalSeason = "Year-Round (Wet & Dry)",
                    stageDays = mapOf(
                        "Sprout" to 5,
                        "Seedling" to 13,
                        "Vegetative" to 20,
                        "Flowering" to 16,
                        "Harvest" to 6
                    )
                ),
                VarietyDetail(
                    name = "Apollo",
                    localName = "Kamatis Apollo",
                    daysToHarvest = 72,
                    characteristics = "Open-pollinated lowland variety producing fleshy, sweet-tart fruits.",
                    diseaseResistance = "Moderate tolerance to Early Blight and Fusarium Wilt.",
                    optimalSeason = "Dry Season (Oct – March)",
                    stageDays = mapOf(
                        "Sprout" to 6,
                        "Seedling" to 15,
                        "Vegetative" to 24,
                        "Flowering" to 20,
                        "Harvest" to 7
                    )
                ),
                VarietyDetail(
                    name = "Rosas F1",
                    localName = "Kamatis Rosas",
                    daysToHarvest = 65,
                    characteristics = "High-yielding determinate bush variety with uniform round-to-oblong fruit sets.",
                    diseaseResistance = "TyLCV tolerant and heat stress resilient.",
                    optimalSeason = "Year-Round",
                    stageDays = mapOf(
                        "Sprout" to 5,
                        "Seedling" to 14,
                        "Vegetative" to 22,
                        "Flowering" to 18,
                        "Harvest" to 6
                    )
                )
            ),
            growingSeason = GrowingSeasonInfo(
                drySeasonStatus = "OPTIMAL (Peak Yield & Quality)",
                wetSeasonStatus = "CHALLENGING (Requires raised beds & rain shelters)",
                optimalTemperature = "20°C – 30°C",
                peakMonths = "November to April (Dry Season Lowlands)",
                climateRisks = "Continuous rain causes foliar blight, bacterial wilt, and blossom drop. Temperatures >35°C reduce fruit pollen viability.",
                weatherTips = listOf(
                    "Plant in raised beds (25–30 cm) with plastic mulch during rainy periods to prevent water stagnation.",
                    "Provide overhead rain shelters or net tunnels during heavy monsoon rains.",
                    "Ensure adequate field drainage ditches between furrow rows."
                )
            ),
            soil = SoilInfo(
                idealSoilTypes = "Sandy Loam, Loam with high organic matter",
                optimalPh = "6.0 – 6.8 (Slightly Acidic to Neutral)",
                drainage = "Excellent internal drainage required; cannot tolerate waterlogged root zones.",
                landPrep = "Plow and harrow twice to 20–30 cm depth. Construct 1-meter wide raised beds with drainage furrows.",
                organicMatter = "Incorporate 5–10 tons/ha of well-decomposed cow/chicken manure or vermicast during final bed harrowing.",
                recommendations = listOf(
                    "Test soil pH before planting; apply agricultural lime if pH is below 5.5 to prevent calcium deficiency.",
                    "Use silver-black plastic mulch to retain soil moisture, suppress weeds, and deter aphids."
                )
            ),
            planting = PlantingInfo(
                method = "Transplanting (Seedlings raised in 104-hole seedling trays)",
                germinationDays = "5 – 7 days in nursery trays",
                transplantAge = "21 – 25 days after sowing (with 4–5 true leaves)",
                plantSpacing = "50 cm between plants",
                rowSpacing = "75 – 100 cm between rows",
                plantingDepth = "Transplant seedling up to its first true leaf level to encourage adventitious root development.",
                trellisingNeeded = true,
                trellisingAdvice = "Erect bamboo stakes or an A-trellis 2–3 weeks after transplanting. Tie main stem with soft twine to keep foliage and fruits off the damp ground.",
                tips = listOf(
                    "Harden seedlings 5 days before transplanting by reducing watering and exposing to full sunlight.",
                    "Transplant in the late afternoon (3:00 PM – 5:00 PM) to minimize transplant shock."
                )
            ),
            watering = WateringInfo(
                frequency = "Every 2 days during dry season; daily during initial transplant establishment",
                bestTime = "Early morning (6:00 AM – 8:00 AM) or late afternoon",
                criticalStages = "Flowering and Fruit Expansion (moisture stress causes blossom end rot and fruit splitting)",
                irrigationType = "Drip irrigation or furrow watering at the base",
                moistureConservation = "Apply rice straw or silver-black plastic mulch over beds.",
                warnings = listOf(
                    "Avoid overhead spraying on leaves to prevent fungal blights and bacterial spot.",
                    "Inconsistent watering (dry followed by heavy soaking) causes severe fruit cracking."
                )
            ),
            fertilization = FertilizationInfo(
                npkRatio = "1.5 : 1.0 : 2.0 (High Potassium for fruit firmness and sweetness)",
                basalApplication = "Apply 10–15g of Complete (14-14-14) plus 1 handful vermicast per planting hole.",
                sideDressing = "15 DAT: 5g Urea (46-0-0) per plant. 30 DAT & 45 DAT: 10g 14-14-14 + 5g Muriate of Potash (0-0-60).",
                organicOptions = "Foliar spray with Fermented Plant Juice (FPJ) and Fish Amino Acids (FAA) every 10 days.",
                micronutrients = "Calcium nitrate spray during flowering to completely prevent Blossom End Rot.",
                schedule = listOf(
                    "Day 0: Basal 14-14-14 + compost in hole",
                    "Day 15: Nitrogen side-dress for vegetative growth",
                    "Day 30: Balanced NPK + Potassium at first flower cluster",
                    "Day 45–60: Potassium booster for fruit expansion and color"
                )
            ),
            growthStages = listOf(
                GrowthStageItem(1, "Germination & Sprout", "Days 1–7", "Seeds absorb moisture, radical emerges, and cotyledon leaves unfold.", "Maintain moist seedling tray mix in shaded nursery."),
                GrowthStageItem(2, "Seedling Nursery", "Days 8–25", "True leaves develop; root network anchors in plug tray.", "Harden seedlings in full sun 5 days before field transplanting."),
                GrowthStageItem(3, "Vegetative & Trellising", "Days 26–45", "Rapid stem elongation, lateral branching, and canopy establishment.", "Install bamboo stakes and prune lower suckers up to first flower cluster."),
                GrowthStageItem(4, "Flowering & Fruit Set", "Days 46–60", "Yellow flower clusters bloom; pollinated ovaries swell into green fruitlets.", "Side-dress potassium and maintain consistent 2-day irrigation."),
                GrowthStageItem(5, "Ripening & Harvest", "Days 61–75+", "Fruit color changes from breaker green to vibrant red; sugars peak.", "Harvest early morning at breaker or turning stage for market.")
            ),
            pestsAndDiseases = listOf(
                PestDiseaseItem(
                    name = "Fruit Borer (Helicoverpa armigera)",
                    type = "Insect Pest",
                    symptoms = "Caterpillars bore circular entry holes into developing fruits with visible dark frass, causing premature rotting and fruit drop.",
                    organicControl = "Spray Bacillus thuringiensis (Bt) or 5% Neem extract early morning. Install pheromone lures (4 traps/ha). Handpick infested fruits.",
                    prevention = "Plant African Marigold border rows as trap crop. Rotate with non-host crops. Avoid planting adjacent to sweet corn.",
                    chemicalControl = "Chlorantraniliprole (Prevathon) or Emamectin benzoate at egg-hatch stage.",
                    imageAsset = "file:///android_asset/metadata/pest/Fruit_borer.png"
                ),
                PestDiseaseItem(
                    name = "Tomato Yellow Leaf Curl Virus (TyLCV)",
                    type = "Disease (Viral)",
                    symptoms = "Severe upward cupping and curling of leaflets, marginal chlorosis, stunted terminal growth, and blossom drop with no fruit set.",
                    organicControl = "Vector control: Whitefly (Bemisia tabaci) eradication using yellow sticky boards (20/ha) and neem oil soap. Rogue out infected plants immediately.",
                    prevention = "Plant certified TyLCV-resistant varieties like Diamante Max F1 and Rosas F1. Install 40-mesh insect nets in seedling beds.",
                    chemicalControl = "Dinotefuran or Imidacloprid drench at transplanting.",
                    imageAsset = "file:///android_asset/metadata/pest/Tomato_leaf_curlvirus.png"
                ),
                PestDiseaseItem(
                    name = "Bacterial Wilt (Ralstonia solanacearum)",
                    type = "Disease (Bacterial)",
                    symptoms = "Rapid daytime wilting of entire green foliage without prior yellowing. Stem vascular browning; white bacterial ooze in clear water suspension test.",
                    organicControl = "No chemical cure once infected. Rogue out diseased plants with root ball. Drench soil perimeter with Trichoderma bio-control agent.",
                    prevention = "Strict 3-year crop rotation with wetland paddy rice or corn. Construct 25–30 cm raised beds to eliminate stagnant root moisture.",
                    chemicalControl = "Copper hydroxide soil drench barrier around infection perimeter.",
                    imageAsset = "file:///android_asset/metadata/pest/Bacterial_wilt.png"
                ),
                PestDiseaseItem(
                    name = "Vegetable Leafminer (Liriomyza spp.)",
                    type = "Insect Pest",
                    symptoms = "Serpentine, winding white or translucent trails mined through leaf mesophyll. Heavily mined leaves dry up, dropping and exposing fruit to sunscald.",
                    organicControl = "Place yellow sticky cards 15 cm above crop canopy. Spray botanical insecticidal soap or neem oil extract. Protect Diglyphus parasitoids.",
                    prevention = "Deep plowing to bury pupae. Prune and compost infested lower foliage. Eradicate surrounding weed hosts.",
                    chemicalControl = "Cyromazine or Abamectin when active mines exceed 5 per leaf.",
                    imageAsset = "file:///android_asset/metadata/pest/Vegetable_leafminer.png"
                ),
                PestDiseaseItem(
                    name = "Early Blight (Alternaria solani)",
                    type = "Disease (Fungal)",
                    symptoms = "Concentric target-like brown spots on older lower leaves, yellow halo margin, leading to premature defoliation from ground level up.",
                    organicControl = "Copper-based fungicide spray (Bordeaux mixture). Prune lower leaves up to 20 cm from ground level to halt soil-splash inoculation.",
                    prevention = "Apply silver-black plastic mulch or thick rice straw mulch. Avoid overhead sprinkler watering; irrigate strictly at root base.",
                    chemicalControl = "Mancozeb or Azoxystrobin spray every 7–10 days during rainy weather.",
                    imageAsset = null
                )
            ),
            companionPlants = CompanionInfo(
                beneficialCompanions = listOf("Marigold", "Basil", "Onion / Garlic", "Carrot", "Pechay"),
                companionBenefits = "Marigolds exude alpha-terthienyl from roots which destroys root-knot nematodes and repels thrips. Basil repels hornworms and attracts pollinators. Alliums repel aphids with strong sulfur aromas.",
                plantsToAvoid = listOf("Corn", "Eggplant", "Potato", "Fennel"),
                avoidReasons = "Corn shares the destructive Tomato Fruit Borer (Corn Earworm). Potato and Eggplant share late blight and flea beetles. Fennel produces allelopathic chemicals that stunt tomato roots."
            ),
            intercropping = IntercroppingInfo(
                recommendedCrops = listOf("Pechay / Brassica greens", "Bush Sitaw", "Radish (Labanos)"),
                spatialLayout = "Plant 2 rows of quick-harvest Pechay on the bed edges between tomato stakes. Pechay matures in 28 days before tomato foliage canopies over.",
                benefits = "Generates early cash flow while tomato crop develops; provides ground cover to prevent weed germination.",
                managementAdvice = "Harvest leafy intercrop cleanly before tomato flowering stage (Day 40) so nutrient competition is eliminated."
            ),
            harvest = HarvestInfo(
                maturityIndicators = "Color break (breaker stage: pink blush at blossom end) for distance shipping; firm deep-red for local immediate sales.",
                daysRange = "60 – 72 days from transplanting",
                harvestingMethod = "Gently twist fruit upward at the natural stem joint (abscission zone) or clip with pruning shears leaving the calyx intact.",
                timeOfDay = "Early morning (6:00 AM – 9:00 AM) while ambient temperature is cool.",
                frequency = "Every 3 to 4 days during peak productive flush.",
                indicatorsList = listOf(
                    "Breaker stage: green fruit shows first noticeable pink/red blush at bottom",
                    "Turning stage: 10% to 30% of surface shows pink/red coloration",
                    "Pink/Light Red stage: 30% to 90% colored; ideal for local wet markets",
                    "Fruit feels solid and heavy in the palm with glossy unwrinkled skin"
                )
            ),
            postHarvest = PostHarvestInfo(
                sortingGrading = "Grade by size into Class A (>100g, defect-free), Class B (70–100g), and Class C (<70g or slight misshape). Discard cracked or borer-damaged fruits.",
                washingCleaning = "Wipe with a clean damp cloth or rinse in chlorinated water (50 ppm) and air-dry thoroughly in a shaded shed.",
                storageConditions = "Store in a well-ventilated room at 12°C – 15°C with 85–90% relative humidity.",
                optimalTemperature = "12°C – 15°C (Avoid domestic refrigerators below 10°C as chilling injury halts ripening)",
                relativeHumidity = "85% – 90%",
                packagingTransport = "Pack in ventilated plastic crates lined with newspaper or clean banana leaves, max 20 kg per crate to prevent bottom fruit crush.",
                shelfLife = "7 – 14 days depending on harvest maturity stage."
            )
        )
    }

    private fun createEggplantGuide(crop: Crop, meta: com.maptanim.app.data.datasource.CropMetadataInfo?): VegetableAgronomicGuide {
        return VegetableAgronomicGuide(
            crop = crop,
            overview = OverviewInfo(
                summary = "The leading vegetable crop in the Philippines by production volume and hectarage. Warm-season, heavy cropper cultivated across lowland areas.",
                botanicalName = "Solanum melongena",
                family = "Solanaceae (Nightshade Family)",
                culinaryUses = "Essential for Tortang Talong, Pinakbet, Kare-Kare, Ensaladang Talong, and Sinigang.",
                regionalSuitability = "Ilocos Region, Pangasinan, Central Luzon, CALABARZON, and Davao.",
                agriculturalImportance = "Reliable cash crop providing continuous weekly harvests for 3 to 6 months once mature."
            ),
            varieties = listOf(
                VarietyDetail(
                    name = "Casino 901 F1",
                    localName = "Talong Casino",
                    daysToHarvest = 75,
                    characteristics = "Long, shiny deep-purple cylindrical fruit with green calyx and firm white flesh.",
                    diseaseResistance = "High field tolerance to Bacterial Wilt and Phomopsis Fruit Rot.",
                    optimalSeason = "Year-Round"
                ),
                VarietyDetail(
                    name = "Dumaguete Long Purple",
                    localName = "Talong DLP",
                    daysToHarvest = 80,
                    characteristics = "Popular open-pollinated variety with slender, tender dark purple fruits.",
                    diseaseResistance = "Moderate tolerance to shoot borer and root rot.",
                    optimalSeason = "Dry Season"
                ),
                VarietyDetail(
                    name = "Banate King F1",
                    localName = "Talong Banate King",
                    daysToHarvest = 70,
                    characteristics = "Early-maturing hybrid with high continuous fruiting flush and long shelf life.",
                    diseaseResistance = "Strong tolerance to heat stress and wet soil conditions.",
                    optimalSeason = "Year-Round"
                )
            ),
            growingSeason = GrowingSeasonInfo(
                drySeasonStatus = "EXCELLENT (Maximum fruit glossiness and yield)",
                wetSeasonStatus = "GOOD (Requires raised beds and good drainage)",
                optimalTemperature = "24°C – 32°C",
                peakMonths = "October to May",
                climateRisks = "Prolonged waterlogging causes rapid bacterial wilt and root rot. Cold temperatures (<18°C) stunt growth.",
                weatherTips = listOf(
                    "Elevate beds to 30 cm during rainy season to avoid stagnant water.",
                    "Mulch beds with rice hull or straw during dry season to conserve moisture."
                )
            ),
            soil = SoilInfo(
                idealSoilTypes = "Deep, fertile Sandy Loam or Clay Loam",
                optimalPh = "5.5 – 6.8",
                drainage = "Moderate to rapid drainage required; roots are sensitive to standing water.",
                landPrep = "Plow 2 to 3 times to 30 cm depth followed by harrowing. Form furrow ridges 100 cm apart.",
                organicMatter = "Add 10 tons/ha of composted manure during land preparation.",
                recommendations = listOf(
                    "Incorporate carbonized rice hull (CRH) to loosen heavy clay soils and improve aeration.",
                    "Maintain soil pH between 6.0 and 6.5 for maximum phosphorus uptake."
                )
            ),
            planting = PlantingInfo(
                method = "Transplanting (Raised in seedling trays)",
                germinationDays = "7 – 10 days",
                transplantAge = "30 – 35 days after sowing (5–6 true leaves)",
                plantSpacing = "60 – 75 cm between plants",
                rowSpacing = "100 cm between rows",
                plantingDepth = "Set seedling root ball flush with soil surface.",
                trellisingNeeded = true,
                trellisingAdvice = "Stake plants with bamboo sticks (1 meter tall) when fruits start forming to prevent lodging under fruit weight.",
                tips = listOf(
                    "Water trays thoroughly 1 hour before transplanting for easy root ball extraction.",
                    "Plant in late afternoon and irrigate immediately."
                )
            ),
            watering = WateringInfo(
                frequency = "Every 2 days in dry season; furrow irrigate weekly",
                bestTime = "Early morning (6:00 AM – 8:00 AM)",
                criticalStages = "Flowering, fruit set, and continuous picking flush",
                irrigationType = "Furrow irrigation or drip lines",
                moistureConservation = "Plastic mulch or thick rice straw layer",
                warnings = listOf(
                    "Moisture deficiency causes fruit bitterness, dull skin color, and blossom drop.",
                    "Overwatering promotes fungal fruit rot and root decay."
                )
            ),
            fertilization = FertilizationInfo(
                npkRatio = "1.5 : 1.0 : 2.5 (High Potassium for glossy skin and elongated fruit)",
                basalApplication = "15g Complete 14-14-14 plus 200g compost per planting hill.",
                sideDressing = "Every 14 days after first harvest: alternate 10g Urea (46-0-0) and 10g Muriate of Potash (0-0-60).",
                organicOptions = "Apply fermented fruit juice (FFJ) and vermitea every 2 weeks.",
                micronutrients = "Boron and zinc foliar sprays to enhance flower retention.",
                schedule = listOf(
                    "Day 0: Basal NPK + organic matter",
                    "Day 20: First side-dress with Urea",
                    "Day 40: Balanced NPK + Potassium at early blooming",
                    "Every 14 days after first pick: NPK maintenance"
                )
            ),
            growthStages = listOf(
                GrowthStageItem(1, "Germination", "Days 1–10", "Seeds germinate and cotyledons emerge.", "Keep seedling nursery humid and warm."),
                GrowthStageItem(2, "Seedling Tray Phase", "Days 11–35", "True leaves expand and root plug forms.", "Harden in full sun 7 days before field planting."),
                GrowthStageItem(3, "Vegetative Establishment", "Days 36–55", "Bush canopy develops thick woody stem and large leaves.", "Install bamboo stake support and weed furrow bases."),
                GrowthStageItem(4, "Flowering & Fruit Setting", "Days 56–70", "Purple flowers bloom and self-pollinate.", "Boost potassium side-dressing and maintain regular watering."),
                GrowthStageItem(5, "Continuous Harvest Flush", "Days 75–180+", "Fruits reach full length and deep glossy sheen.", "Harvest twice weekly and side-dress fertilizer every 2 weeks.")
            ),
            pestsAndDiseases = listOf(
                PestDiseaseItem("Fruit and Shoot Borer (Leucinodes orbonalis)", "Insect Pest", "Larvae bore into tender shoots causing wilting, then enter fruits leaving feeding holes.", "Prune and bury wilted shoots promptly. Spray neem extract or BT bio-insecticide.", "Cover young fruits with paper bags or use sex pheromone traps."),
                PestDiseaseItem("Flea Beetle (Phyllotreta striolata)", "Insect Pest", "Shot-hole feeding marks on young leaves leading to stunted growth.", "Dust with diatomaceous earth or spray botanical chili-garlic extract.", "Use yellow sticky cards and maintain weed-free surroundings."),
                PestDiseaseItem("Bacterial Wilt (Ralstonia solanacearum)", "Disease (Bacterial)", "Sudden wilting of healthy green plants without yellowing.", "No cure once infected; rogue and burn infected plants.", "Rotate with rice, corn, or legumes for 3 seasons.")
            ),
            companionPlants = CompanionInfo(
                beneficialCompanions = listOf("String Beans", "Chili Pepper", "Marigold", "Basil"),
                companionBenefits = "Beans supply fixed atmospheric nitrogen. Marigold roots repel nematodes and reduce beetle populations.",
                plantsToAvoid = listOf("Tomato", "Potato"),
                avoidReasons = "Shares common insect pests (borers, flea beetles) and soil-borne fungal pathogens."
            ),
            intercropping = IntercroppingInfo(
                recommendedCrops = listOf("Bush Sitaw", "Pechay", "Radish"),
                spatialLayout = "Plant quick leafy vegetables between wide eggplant rows (1 meter spacing) during the first 30 days.",
                benefits = "Maximizes land utilization before eggplant canopy branches out.",
                managementAdvice = "Clear intercrop completely when eggplant reaches flowering stage."
            ),
            harvest = HarvestInfo(
                maturityIndicators = "Skin is deep glossy purple, firm yet yielding slightly to thumb pressure; calyx spines are green and fresh.",
                daysRange = "70 – 85 days from transplanting",
                harvestingMethod = "Cut fruit stalk 2 cm above calyx using sharp pruning shears.",
                timeOfDay = "Early morning or late afternoon",
                frequency = "Every 4 to 5 days continuously for 3 to 6 months",
                indicatorsList = listOf(
                    "Fruit has reached variety standard length (20–30 cm)",
                    "Exterior has brilliant glossy sheen; dull skin indicates over-ripeness",
                    "Seeds inside are still white and soft, not brown and hard"
                )
            ),
            postHarvest = PostHarvestInfo(
                sortingGrading = "Sort into Class A (straight, glossy, >20cm), Class B (slight curve, 15-20cm), and Class C (short/blemished).",
                washingCleaning = "Wipe gently with clean soft dry cloth. Do not soak in water.",
                storageConditions = "Store in cool shaded packing area at 10°C – 12°C with 90% humidity.",
                optimalTemperature = "10°C – 12°C (Do not chill below 8°C)",
                relativeHumidity = "90% – 95%",
                packagingTransport = "Pack in 20-kg slotted plastic crates lined with banana leaves or kraft paper.",
                shelfLife = "5 – 8 days under ambient tropical conditions."
            )
        )
    }

    private fun createPechayGuide(crop: Crop, meta: com.maptanim.app.data.datasource.CropMetadataInfo?): VegetableAgronomicGuide {
        return VegetableAgronomicGuide(
            crop = crop,
            overview = OverviewInfo(
                summary = "Fast-growing, nutrient-packed Brassica leafy green beloved across the Philippines. Quick turnaround cash crop yielding high profit per unit area in under a month.",
                botanicalName = "Brassica rapa subsp. chinensis",
                family = "Brassicaceae (Mustard Family)",
                culinaryUses = "Essential for Nilagang Baka/Baboy, Pochero, Ginisang Pechay, and fresh soup broths.",
                regionalSuitability = "Nationwide (both lowlands and uplands; urban garden friendly).",
                agriculturalImportance = "Fastest cash return crop for smallholders; harvestable in 25–30 days from transplanting."
            ),
            varieties = listOf(
                VarietyDetail("Black Behi", "Pechay Black Behi", 28, "Broad, dark green tender leaves with thick, crisp white petioles.", "High heat tolerance and slow bolting.", "Year-Round"),
                VarietyDetail("Pavito", "Pechay Pavito", 25, "Compact, upright growth habit with uniform succulent stalks.", "High field tolerance to downy mildew.", "Year-Round"),
                VarietyDetail("Ching Chiang", "Pechay Baby Pak Choi", 30, "Spoon-shaped leaves with dense succulent green stems.", "Excellent culinary quality and crispness.", "Cool Season / Upland")
            ),
            growingSeason = GrowingSeasonInfo(
                drySeasonStatus = "EXCELLENT (Requires regular watering)",
                wetSeasonStatus = "GOOD (Requires raised beds and shelter from heavy pounding rain)",
                optimalTemperature = "18°C – 30°C",
                peakMonths = "Year-Round cultivation",
                climateRisks = "Pounding typhoon rain tears tender leaves. Prolonged intense heat causes early bolting (premature flowering).",
                weatherTips = listOf(
                    "Use 30% shade netting or agro-net tunnels during intense summer heat.",
                    "Elevate garden beds 20–25 cm with good drainage channels."
                )
            ),
            soil = SoilInfo(
                idealSoilTypes = "Sandy Loam, Loam rich in organic compost",
                optimalPh = "6.0 – 7.0",
                drainage = "Fast surface and internal drainage.",
                landPrep = "Pulverize soil finely to 15–20 cm depth. Form 1-meter wide raised beds.",
                organicMatter = "Mix 2 kg vermicast or composted manure per square meter.",
                recommendations = listOf(
                    "Incorporate carbonized rice hull (CRH) to maintain loose, crumbly soil texture.",
                    "Ensure adequate calcium in soil to prevent tipburn."
                )
            ),
            planting = PlantingInfo(
                method = "Direct seeding or transplanting from seedling trays",
                germinationDays = "3 – 5 days",
                transplantAge = "12 – 14 days after sowing (3 true leaves)",
                plantSpacing = "15 – 20 cm between plants",
                rowSpacing = "20 – 25 cm between rows",
                plantingDepth = "1 cm depth for direct seeds; set root plug level for transplants.",
                trellisingNeeded = false,
                trellisingAdvice = null,
                tips = listOf(
                    "Thin direct-seeded stands 10 days after germination leaving single robust seedlings.",
                    "Transplant in late afternoon and water thoroughly."
                )
            ),
            watering = WateringInfo(
                frequency = "Daily or twice daily during hot dry spells",
                bestTime = "Early morning and late afternoon",
                criticalStages = "Throughout the entire 30-day cycle due to shallow root system",
                irrigationType = "Fine sprinkler, watering can with rose head, or drip tape",
                moistureConservation = "Light rice hull mulch between rows",
                warnings = listOf(
                    "Water stress causes tough fibrous leaves and premature flowering (bolting).",
                    "Avoid strong high-pressure water jets that flatten young leaves."
                )
            ),
            fertilization = FertilizationInfo(
                npkRatio = "2.0 : 1.0 : 1.0 (High Nitrogen for lush green foliage expansion)",
                basalApplication = "Incorporate Complete 14-14-14 (15g/sqm) + vermicompost during bed prep.",
                sideDressing = "Day 10 & Day 18: Drench with Urea (10g/10L water) or Fermented Plant Juice (FPJ).",
                organicOptions = "Vermitea foliar drench and fermented manure extract weekly.",
                micronutrients = "Foliar calcium to prevent leaf margin tipburn.",
                schedule = listOf(
                    "Day 0: Basal compost + 14-14-14",
                    "Day 10: Liquid nitrogen drench",
                    "Day 18: FPJ foliar booster"
                )
            ),
            growthStages = listOf(
                GrowthStageItem(1, "Germination", "Days 1–5", "Tiny seeds sprout rapidly with heart-shaped cotyledons.", "Keep soil evenly moist with fine mist."),
                GrowthStageItem(2, "Seedling Establishment", "Days 6–14", "First 3 true leaves appear; root system anchors.", "Thin direct-seeded beds or transplant tray seedlings."),
                GrowthStageItem(3, "Active Rosette Growth", "Days 15–22", "Leaves expand rapidly with thick white fleshy petiole stalks.", "Apply liquid nitrogen drench and monitor for cutworms."),
                GrowthStageItem(4, "Harvest Maturity", "Days 25–30", "Crisp, broad green leaves form full open rosette.", "Harvest entire plant or cut outer leaves.")
            ),
            pestsAndDiseases = listOf(
                PestDiseaseItem("Diamondback Moth (Plutella xylostella)", "Insect Pest", "Tiny green caterpillars chewing small holes ('windowpaning') in leaves.", "Spray Bacillus thuringiensis (BT) or Neem extract.", "Net barriers and intercropping with alliums."),
                PestDiseaseItem("Flea Beetle", "Insect Pest", "Small black jumping beetles eating numerous tiny holes in leaves.", "Dust with wood ash or diatomaceous earth.", "Sticky yellow insect cards."),
                PestDiseaseItem("Damping-Off", "Disease (Fungal)", "Seedling stems rot at soil line and collapse.", "Use sterilized potting mix; avoid overwatering.", "Trichoderma biological fungicide.")
            ),
            companionPlants = CompanionInfo(
                beneficialCompanions = listOf("Tomato", "Onion", "Garlic", "Mint"),
                companionBenefits = "Alliums and mint repel diamondback moths with pungent scent.",
                plantsToAvoid = listOf("String Beans", "Corn"),
                avoidReasons = "Taller plants cast heavy shade that slows down pechay vegetative growth."
            ),
            intercropping = IntercroppingInfo(
                recommendedCrops = listOf("Tomato", "Eggplant", "Sweet Pepper"),
                spatialLayout = "Plant pechay along the outer edges of tomato/eggplant beds.",
                benefits = "Harvest pechay before solanaceous crops develop large canopies.",
                managementAdvice = "Harvest pechay cleanly at Day 28 so long-term crops have full root space."
            ),
            harvest = HarvestInfo(
                maturityIndicators = "Leaves are large, crisp, deep green, and stalks are fleshy and tender.",
                daysRange = "25 – 30 days from transplanting",
                harvestingMethod = "Pull entire plant with roots or cut cleanly at base with a sharp knife.",
                timeOfDay = "Early morning or late afternoon",
                frequency = "Single harvest per bed batch",
                indicatorsList = listOf(
                    "Plant has 6 to 8 large fully expanded crisp leaves",
                    "Petiole bases are thick, succulent, and white",
                    "Harvest before central flower stalk emerges"
                )
            ),
            postHarvest = PostHarvestInfo(
                sortingGrading = "Remove yellowing or torn outer leaves. Trim excess root tips.",
                washingCleaning = "Wash root base in clean cold water to remove soil; bundle in 500g or 1kg units.",
                storageConditions = "Cool, shaded area at 4°C – 8°C with high humidity.",
                optimalTemperature = "4°C – 8°C",
                relativeHumidity = "95%",
                packagingTransport = "Pack loosely in perforated plastic bags or bamboo baskets lined with moist banana leaves.",
                shelfLife = "3 – 5 days under refrigeration; 1 – 2 days at room temperature."
            )
        )
    }

    private fun createCarrotGuide(crop: Crop, meta: com.maptanim.app.data.datasource.CropMetadataInfo?): VegetableAgronomicGuide {
        return VegetableAgronomicGuide(
            crop = crop,
            overview = OverviewInfo(
                summary = "High-value root vegetable packed with beta-carotene, Vitamin A, and dietary fiber. Thrives in cool highland climates and sandy lowland loam soils.",
                botanicalName = "Daucus carota subsp. sativus",
                family = "Apiaceae (Carrot / Parsley Family)",
                culinaryUses = "Essential ingredient in Pancit, Menudo, Afritada, Chop Suey, lumpia, and salads.",
                regionalSuitability = "Benguet, Mountain Province, Bukidnon, and sandy lowland soils.",
                agriculturalImportance = "High retail value and excellent storage capability, providing steady income."
            ),
            varieties = listOf(
                VarietyDetail("Kuroda", "Karot Kuroda", 90, "Thick, conical orange roots with blunt tips; sweet and tender.", "Heat tolerant and adaptable to lowlands.", "Year-Round"),
                VarietyDetail("New Kuroda", "Karot Bagong Kuroda", 85, "Uniform cylindrical roots with smooth skin and deep orange core.", "High resistance to leaf blight.", "Dry Season / Highland"),
                VarietyDetail("Terracotta F1", "Karot Terracotta", 95, "High-yielding commercial hybrid with crisp flesh and small core.", "Excellent shipping durability.", "Upland / Highland")
            ),
            growingSeason = GrowingSeasonInfo(
                drySeasonStatus = "OPTIMAL (Sunny days and cool nights produce sweetest roots)",
                wetSeasonStatus = "MODERATE (Requires raised beds to avoid root rotting and cracking)",
                optimalTemperature = "16°C – 24°C",
                peakMonths = "October to March",
                climateRisks = "Heavy rains cause root splitting and soil compaction. High temperatures (>28°C) cause pale color and bitterness.",
                weatherTips = listOf(
                    "Construct high raised beds (30 cm) with deep, stone-free loose soil.",
                    "Maintain continuous light moisture during germination."
                )
            ),
            soil = SoilInfo(
                idealSoilTypes = "Deep Sandy Loam, Silt Loam free of rocks and hardpan",
                optimalPh = "6.0 – 6.8",
                drainage = "Very deep, rapid drainage. Heavy clay causes deformed, forked roots.",
                landPrep = "Plow and till deeply to 35 cm. Remove all stones, clods, and debris. Rake bed surface smooth.",
                organicMatter = "Use only fully decomposed compost; fresh unaged manure causes root forking.",
                recommendations = listOf(
                    "Avoid fresh animal manure which causes hairy, multi-legged roots.",
                    "Ensure deep loose soil to allow straight taproot elongation."
                )
            ),
            planting = PlantingInfo(
                method = "Direct Seeding only (Never transplant carrots as taproot will break)",
                germinationDays = "7 – 14 days",
                transplantAge = "Not Applicable (Direct Seeded)",
                plantSpacing = "5 – 8 cm between plants (after thinning)",
                rowSpacing = "20 – 25 cm between rows",
                plantingDepth = "0.5 – 1.0 cm shallow depth",
                trellisingNeeded = false,
                trellisingAdvice = null,
                tips = listOf(
                    "Mix tiny seeds with dry sand or fine wood ash for even scattering.",
                    "Thin seedlings in 2 stages: first at 15 days, second at 30 days."
                )
            ),
            watering = WateringInfo(
                frequency = "Every 2 days; keep seedbed moist until germination",
                bestTime = "Early morning with gentle fine spray",
                criticalStages = "Germination (first 2 weeks) and root enlargement (Days 45–75)",
                irrigationType = "Micro-sprinkler or fine rose can",
                moistureConservation = "Light burlap cloth or rice straw covering seed furrow until sprout",
                warnings = listOf(
                    "Dry spells followed by heavy water cause severe lengthwise root splitting.",
                    "Waterlogged soil causes root rotting and cavity spot disease."
                )
            ),
            fertilization = FertilizationInfo(
                npkRatio = "1.0 : 1.5 : 2.0 (High Potassium and Phosphorus; Low Nitrogen)",
                basalApplication = "Apply Complete 14-14-14 (20g/sqm) + well-cured compost during bed preparation.",
                sideDressing = "Day 30 & Day 50: Side-dress with Muriate of Potash (0-0-60) and Solophos (0-20-0).",
                organicOptions = "Wood ash incorporation for potassium and vermicast tea drenches.",
                micronutrients = "Boron application to prevent internal core browning and hollow root.",
                schedule = listOf(
                    "Day 0: Basal P and K incorporated deep",
                    "Day 30: Potassium side-dress after second thinning",
                    "Day 50: Final potassium boost for root thickening"
                )
            ),
            growthStages = listOf(
                GrowthStageItem(1, "Germination", "Days 1–14", "Tiny seeds absorb water; taproot emerges downward.", "Keep soil surface continuously moist."),
                GrowthStageItem(2, "Feathery Foliage", "Days 15–35", "Lacy, fern-like leaves expand; taproot extends deep.", "Thin seedlings to 5–8 cm spacing."),
                GrowthStageItem(3, "Root Thickening", "Days 36–65", "Taproot expands radially; orange carotene accumulates.", "Side-dress potassium and hill up soil around root shoulders."),
                GrowthStageItem(4, "Harvest Maturity", "Days 70–95", "Root reaches full diameter (3–5 cm) with blunt tip.", "Harvest by loosening soil with a digging fork.")
            ),
            pestsAndDiseases = listOf(
                PestDiseaseItem("Cutworms & Armyworms", "Insect Pest", "Caterpillars sever young carrot seedlings at soil level at night.", "Handpick at night with flashlight; apply neem extract.", "Clean weeding and tilling."),
                PestDiseaseItem("Root-Knot Nematodes", "Microscopic Pest", "Galls and swellings on roots causing severe stunted, forked, hairy roots.", "Incorporate marigold plants or neem cake into soil.", "Rotate with non-susceptible crops like corn."),
                PestDiseaseItem("Alternaria Leaf Blight", "Disease (Fungal)", "Dark brown necrotic spots on leaf margins curling and dying back.", "Spray copper fungicide; avoid overhead watering.", "Wide row spacing for air circulation.")
            ),
            companionPlants = CompanionInfo(
                beneficialCompanions = listOf("Onion", "Leek", "Rosemary", "Lettuce"),
                companionBenefits = "Onion scent masks carrot odor from rust flies and aphids.",
                plantsToAvoid = listOf("Fennel", "Dill", "Celery"),
                avoidReasons = "Attracts common Apiaceae pests and can cross-pollinate."
            ),
            intercropping = IntercroppingInfo(
                recommendedCrops = listOf("Radish", "Lettuce", "Green Onions"),
                spatialLayout = "Sow quick-maturing radish seeds mixed with carrot seeds. Radish is pulled in 25 days, naturally thinning the carrot row.",
                benefits = "Solves the slow carrot germination weeding problem and breaks soil crust.",
                managementAdvice = "Gently pull companion radish so carrot taproots remain undisturbed."
            ),
            harvest = HarvestInfo(
                maturityIndicators = "Carrot root shoulder is 3–4 cm wide; foliage starts to mature and yellow slightly.",
                daysRange = "85 – 100 days from seeding",
                harvestingMethod = "Loosen bed soil beside row with a spading fork, then pull gently by foliage base.",
                timeOfDay = "Early morning or late afternoon",
                frequency = "Harvest as needed or full bed harvest",
                indicatorsList = listOf(
                    "Root top shoulder shows deep orange color at soil line",
                    "Root has attained firm conical or cylindrical variety shape",
                    "Sweet taste and crisp texture without woody core"
                )
            ),
            postHarvest = PostHarvestInfo(
                sortingGrading = "Grade into Class A (straight, smooth, >15cm), Class B (small/curved), and rejects (split/forked).",
                washingCleaning = "Wash in clean running water to remove clinging soil. Remove green leafy tops to prevent root dehydration.",
                storageConditions = "Store in perforated bags in cool storage at 0°C – 4°C with 95% humidity.",
                optimalTemperature = "0°C – 4°C",
                relativeHumidity = "95% – 98%",
                packagingTransport = "Pack in 20-kg polyethylene bags with ventilation holes.",
                shelfLife = "2 – 4 weeks under refrigeration; 4 – 7 days at room temperature."
            )
        )
    }

    private fun createAmpalayaGuide(crop: Crop, meta: com.maptanim.app.data.datasource.CropMetadataInfo?): VegetableAgronomicGuide {
        return VegetableAgronomicGuide(
            crop = crop,
            overview = OverviewInfo(
                summary = "High-value medicinal and culinary climbing vine famous for its crisp, bitter green fruit containing charantin and polypeptide-p. Staple commercial crop across the Philippines grown on bamboo trellises.",
                botanicalName = "Momordica charantia",
                family = "Cucurbitaceae (Gourd Family)",
                culinaryUses = "Famous for Pinakbet, Ginisang Ampalaya with egg, Dinengdeng, and medicinal herbal tea.",
                regionalSuitability = "Central Luzon, CALABARZON, Ilocos, and Davao.",
                agriculturalImportance = "Consistently high farmgate price; continuous harvests for 2 to 3 months once trellised."
            ),
            varieties = listOf(
                VarietyDetail("Jade Star XL F1", "Ampalaya Jade Star XL", 55, "Extra-long dark green fruits (30–35 cm) with prominent ridges.", "Tolerant to Papaya Ringspot Virus and Leaf Spot.", "Year-Round"),
                VarietyDetail("Galaxy F1", "Ampalaya Galaxy", 58, "Heavy yielder with thick, firm flesh and bright glossy green ribs.", "High resistance to Downy Mildew and Anthracnose.", "Year-Round / Wet"),
                VarietyDetail("Sta. Rita", "Ampalaya Sta. Rita", 60, "Classic open-pollinated variety with moderate bitterness and tender skin.", "Adapted to lowland dry season.", "Dry Season")
            ),
            growingSeason = GrowingSeasonInfo(
                drySeasonStatus = "EXCELLENT (Low disease pressure under irrigation)",
                wetSeasonStatus = "GOOD (Requires high overhead trellis to keep fruits off moist ground)",
                optimalTemperature = "24°C – 35°C",
                peakMonths = "November to May",
                climateRisks = "Prolonged wet ground causes fruit rotting. Temperatures <20°C inhibit flowering.",
                weatherTips = listOf(
                    "Always train vines on 2-meter overhead bamboo/net trellis.",
                    "Wrap individual young fruits in newspaper or blue plastic sleeves to protect from fruit flies."
                )
            ),
            soil = SoilInfo(
                idealSoilTypes = "Loam, Sandy Loam with high organic matter",
                optimalPh = "6.0 – 6.7",
                drainage = "Well-drained; roots cannot tolerate standing water.",
                landPrep = "Plow and harrow twice. Form raised beds 1.5 to 2.0 meters apart.",
                organicMatter = "Add 5 tons/ha composted manure in planting hills.",
                recommendations = listOf(
                    "Construct furrows with good runoff gradients.",
                    "Mulch beds to conserve moisture and suppress weeds."
                )
            ),
            planting = PlantingInfo(
                method = "Direct seeding or seedling tray transplanting",
                germinationDays = "5 – 7 days (clip seed tip to accelerate)",
                transplantAge = "12 – 15 days after sowing (2 true leaves)",
                plantSpacing = "50 – 75 cm between hills",
                rowSpacing = "200 – 250 cm between trellis rows",
                plantingDepth = "2 cm seed depth",
                trellisingNeeded = true,
                trellisingAdvice = "Construct an overhead bamboo/wire trellis (Balag) or A-frame trellis 2 meters high. Guide climbing vines with strings.",
                tips = listOf(
                    "Prune lower lateral shoots up to 1 meter high to concentrate vine growth onto the overhead trellis canopy.",
                    "Allow bees and natural pollinators access to yellow flowers."
                )
            ),
            watering = WateringInfo(
                frequency = "Every 2 days; daily during flowering and fruit sizing",
                bestTime = "Early morning",
                criticalStages = "Vine climbing, flowering, and continuous fruit development",
                irrigationType = "Furrow irrigation or drip lines",
                moistureConservation = "Plastic mulch or thick straw mulch",
                warnings = listOf(
                    "Moisture stress causes stunted, curved, excessively bitter gourds.",
                    "Waterlogging causes Fusarium root rot and damping-off."
                )
            ),
            fertilization = FertilizationInfo(
                npkRatio = "1.5 : 1.0 : 2.0",
                basalApplication = "15g Complete 14-14-14 + 1 shovel compost per hill.",
                sideDressing = "Every 10 days: Alternate Urea (46-0-0) and Muriate of Potash (0-0-60).",
                organicOptions = "Drench with fermented fruit juice (FFJ) and vermicast tea.",
                micronutrients = "Boron and zinc foliar sprays for enhanced flower setting.",
                schedule = listOf(
                    "Day 0: Basal fertilizer in planting hill",
                    "Day 20: Nitrogen boost when vine starts climbing",
                    "Day 40: Balanced NPK at first female flowering",
                    "Every 10 days during harvest: Potash and nitrogen"
                )
            ),
            growthStages = listOf(
                GrowthStageItem(1, "Germination", "Days 1–7", "Seed coat cracks and radical emerges.", "Soak seeds in warm water for 12 hours before planting."),
                GrowthStageItem(2, "Seedling & Tendril Emergence", "Days 8–20", "First tendrils emerge and look for support.", "Guide main vine onto bamboo climbing stakes."),
                GrowthStageItem(3, "Overhead Trellis Canopy", "Days 21–40", "Vines branch profusely across overhead trellis netting.", "Prune lower secondary shoots below 1 meter."),
                GrowthStageItem(4, "Flowering & Pollination", "Days 41–55", "Yellow male and female flowers bloom; bees pollinate.", "Wrap young 5-cm fruitlets with paper to stop fruit flies."),
                GrowthStageItem(5, "Harvest Flush", "Days 55–120+", "Fruits expand to 30 cm with prominent crisp ribs.", "Harvest every 3 days before yellowing occurs.")
            ),
            pestsAndDiseases = listOf(
                PestDiseaseItem("Melon Fruit Fly (Bactrocera cucurbitae)", "Insect Pest", "Adult flies oviposit eggs under fruit skin; maggots feed inside causing rot and curvature.", "Bag individual fruitlets with paper or perforated plastic bags when 5 cm long.", "Hang methyl eugenol pheromone traps around perimeter."),
                PestDiseaseItem("Downy Mildew (Pseudoperonospora cubensis)", "Disease (Fungal)", "Angular yellow spots on upper leaf surface with purplish-gray mold underneath.", "Spray copper fungicide or metalaxyl during wet humid spells.", "Prune dense inner canopy for sunlight and air circulation."),
                PestDiseaseItem("Bacterial Wilt", "Disease (Bacterial)", "Sudden wilting of vine leaves in daytime.", "Pull and burn affected vines immediately.", "Crop rotation and soil drench with bio-control.")
            ),
            companionPlants = CompanionInfo(
                beneficialCompanions = listOf("Corn", "Radish", "Marigold", "Bush Sitaw"),
                companionBenefits = "Corn acts as a windbreak and supports climbing vines. Marigolds repel nematodes.",
                plantsToAvoid = listOf("Potato", "Tomato"),
                avoidReasons = "Share fungal blights and compete for heavy potassium nutrients."
            ),
            intercropping = IntercroppingInfo(
                recommendedCrops = listOf("Corn", "Radish", "Pechay"),
                spatialLayout = "Plant corn rows along the windward perimeter of ampalaya balag trellis.",
                benefits = "Optimizes vertical space and creates microclimate protection.",
                managementAdvice = "Keep trellis base free of weeds and competing ground vines."
            ),
            harvest = HarvestInfo(
                maturityIndicators = "Fruits are light green to dark green, firm, ridges are rounded and prominent; blossom end is still green (not yellow).",
                daysRange = "55 – 65 days from transplanting",
                harvestingMethod = "Cut fruit stalk with sharp knife or shears leaving 2 cm of peduncle attached.",
                timeOfDay = "Early morning (6:00 AM – 8:00 AM)",
                frequency = "Every 3 to 4 days",
                indicatorsList = listOf(
                    "Fruit has reached full variety length (25–35 cm)",
                    "Exterior ribs are plump and rounded",
                    "Harvest before fruit turns yellow or orange (over-ripe seeds turn red and flesh softens)"
                )
            ),
            postHarvest = PostHarvestInfo(
                sortingGrading = "Class A (straight, uniform green, >30cm, no punctures), Class B (slight curve, 20-30cm), Class C (small/twisted).",
                washingCleaning = "Wipe clean with cloth; do not wash with water if storing.",
                storageConditions = "Cool, ventilated storage at 12°C – 14°C with 85–90% humidity.",
                optimalTemperature = "12°C – 14°C (Sensitive to chilling injury below 10°C)",
                relativeHumidity = "85% – 90%",
                packagingTransport = "Pack in shallow 10-kg to 15-kg crates lined with banana leaves; avoid deep piling.",
                shelfLife = "4 – 7 days under ambient conditions."
            )
        )
    }

    private fun createSitawGuide(crop: Crop, meta: com.maptanim.app.data.datasource.CropMetadataInfo?): VegetableAgronomicGuide {
        return VegetableAgronomicGuide(
            crop = crop,
            overview = OverviewInfo(
                summary = "Popular legume vegetable rich in protein, iron, and fiber. Climbing pole bean grown throughout the country that naturally enriches soil with nitrogen.",
                botanicalName = "Vigna unguiculata subsp. sesquipedalis",
                family = "Fabaceae (Legume Family)",
                culinaryUses = "Essential for Adobong Sitaw, Kare-Kare, Sinigang, Ginisang Sitaw, and Pinakbet.",
                regionalSuitability = "Nationwide (Ilocos, Central Luzon, Southern Tagalog, Visayas, Mindanao).",
                agriculturalImportance = "Continuous 2-month harvest cycle; fixes atmospheric nitrogen to regenerate soil fertility."
            ),
            varieties = listOf(
                VarietyDetail("Sandigan", "Sitaw Sandigan", 50, "Dark green, smooth, firm pods (55–60 cm long) with slow seed swelling.", "High resistance to Bean Rust and Mosaic Virus.", "Year-Round"),
                VarietyDetail("Negros Green", "Sitaw Negros Green", 52, "Long, tender deep green pods with high yield flushes.", "Tolerant to heat and drought.", "Dry Season"),
                VarietyDetail("Morelos", "Sitaw Morelos", 48, "Light green, tender, sweet pods with vigorous climbing vines.", "Fast-maturing commercial hybrid.", "Year-Round")
            ),
            growingSeason = GrowingSeasonInfo(
                drySeasonStatus = "EXCELLENT (Continuous heavy pod flush under irrigation)",
                wetSeasonStatus = "GOOD (Requires strong trellis and fungal protection)",
                optimalTemperature = "22°C – 32°C",
                peakMonths = "October to May",
                climateRisks = "Continuous rain causes flower drop, pod rotting, and fungal rust.",
                weatherTips = listOf(
                    "Construct an A-frame or vertical pole trellis before vines start trailing.",
                    "Ensure furrow drainage is clear during heavy downpours."
                )
            ),
            soil = SoilInfo(
                idealSoilTypes = "Sandy Loam, Loam with good aeration",
                optimalPh = "5.5 – 6.8",
                drainage = "Good drainage; legumes cannot withstand standing water.",
                landPrep = "Plow and harrow twice. Form raised beds 100 cm apart.",
                organicMatter = "Add 3 to 5 tons/ha composted organic matter.",
                recommendations = listOf(
                    "Inoculate soil with Rhizobium bacteria to maximize root nodule nitrogen fixation.",
                    "Avoid excessive chemical nitrogen fertilizer which encourages foliage at the expense of pods."
                )
            ),
            planting = PlantingInfo(
                method = "Direct Seeding (2–3 seeds per hill)",
                germinationDays = "3 – 5 days",
                transplantAge = "Not Recommended (Direct seeded)",
                plantSpacing = "30 – 40 cm between hills",
                rowSpacing = "100 cm between trellis rows",
                plantingDepth = "2 – 3 cm deep",
                trellisingNeeded = true,
                trellisingAdvice = "Erect bamboo poles (2 meters high) in an A-frame structure or trellis net 2 weeks after seeding.",
                tips = listOf(
                    "Thin to 2 strongest plants per hill 10 days after germination.",
                    "Guide young vines counter-clockwise around bamboo poles."
                )
            ),
            watering = WateringInfo(
                frequency = "Every 2 to 3 days",
                bestTime = "Early morning",
                criticalStages = "Flowering and pod elongation",
                irrigationType = "Furrow or drip irrigation",
                moistureConservation = "Rice straw mulch around vine base",
                warnings = listOf(
                    "Water stress during flowering causes blossoms to abort.",
                    "Excessive moisture encourages root rot and anthracnose."
                )
            ),
            fertilization = FertilizationInfo(
                npkRatio = "1.0 : 2.0 : 2.0 (High Phosphorus and Potassium; Low Nitrogen)",
                basalApplication = "Apply Complete 14-14-14 (15g/hill) at planting.",
                sideDressing = "At flowering (Day 35): Side-dress 10g Solophos (0-20-0) + 10g Muriate of Potash (0-0-60).",
                organicOptions = "Foliar spray with fermented fruit juice (FFJ) and seaweed extract.",
                micronutrients = "Molybdenum and boron to support nitrogen fixation nodules.",
                schedule = listOf(
                    "Day 0: Basal NPK in planting hill",
                    "Day 20: Organic foliar drench",
                    "Day 35: Phosphorus & potassium boost for flowering",
                    "Every 12 days during harvest: Potassium replenishment"
                )
            ),
            growthStages = listOf(
                GrowthStageItem(1, "Germination", "Days 1–5", "Fast seed emergence with stout green hypocotyl.", "Keep soil moist until seedlings stand upright."),
                GrowthStageItem(2, "Vining & Climbing", "Days 6–25", "Vigorous vines produce tendrils seeking support.", "Erect bamboo poles and guide vines upward."),
                GrowthStageItem(3, "Flowering", "Days 26–40", "Purple/white flowers bloom in pairs at nodes.", "Side-dress potassium and avoid overhead watering."),
                GrowthStageItem(4, "Pod Elongation", "Days 41–50", "Slender green pods grow rapidly (up to 60 cm).", "Protect from pod borers and maintain soil moisture."),
                GrowthStageItem(5, "Harvest Flush", "Days 50–90+", "Pods reach full length with tender, succulent flesh.", "Harvest every 2 to 3 days.")
            ),
            pestsAndDiseases = listOf(
                PestDiseaseItem("Bean Pod Borer (Maruca testulalis)", "Insect Pest", "Larvae web flowers and bore into young pods, causing frass and decay.", "Spray Bacillus thuringiensis (BT) or Neem extract at early flowering.", "Handpick infested pods and destroy."),
                PestDiseaseItem("Black Bean Aphids (Aphis craccivora)", "Insect Pest", "Clusters of black insects sucking sap from tender shoot tips and pods.", "Spray with soap solution or neem oil; encourage ladybugs.", "Yellow sticky cards."),
                PestDiseaseItem("Bean Rust (Uromyces appendiculatus)", "Disease (Fungal)", "Reddish-brown rust pustules on leaves leading to premature drying.", "Spray wettable sulfur or copper fungicide.", "Plant resistant varieties like Sandigan.")
            ),
            companionPlants = CompanionInfo(
                beneficialCompanions = listOf("Corn", "Eggplant", "Radish", "Cucumber"),
                companionBenefits = "Corn stalks provide natural climbing trellises. Beans fix nitrogen for companion crops.",
                plantsToAvoid = listOf("Onion", "Garlic", "Chives"),
                avoidReasons = "Alliums inhibit the beneficial nitrogen-fixing bacteria on legume roots."
            ),
            intercropping = IntercroppingInfo(
                recommendedCrops = listOf("Corn", "Pechay", "Squash"),
                spatialLayout = "Traditional 'Three Sisters' polyculture or alternating rows with corn.",
                benefits = "Maximum nitrogen utilization and natural physical support.",
                managementAdvice = "Ensure climbing beans do not overwhelm slower companion crops."
            ),
            harvest = HarvestInfo(
                maturityIndicators = "Pods are long, tender, pliable, and green; seeds inside have not yet bulged visibly.",
                daysRange = "48 – 55 days from seeding",
                harvestingMethod = "Snap pod stem gently at the node by hand without tearing the plant vine.",
                timeOfDay = "Early morning (6:00 AM – 8:30 AM)",
                frequency = "Every 2 to 3 days continuously for 6 to 8 weeks",
                indicatorsList = listOf(
                    "Pods have reached 50–60 cm length",
                    "Pods snap crisply when bent",
                    "Seeds inside are tender and immature, not hard and protruding"
                )
            ),
            postHarvest = PostHarvestInfo(
                sortingGrading = "Grade by length and straightness into Class A (>50cm, straight), Class B (40-50cm), and Class C (curved/short).",
                washingCleaning = "Bundle in 1-kg or 500g tie bundles with rubber bands or straw; do not wash with water.",
                storageConditions = "Store in cool, humid shade at 8°C – 10°C with 90% humidity.",
                optimalTemperature = "8°C – 10°C",
                relativeHumidity = "90% – 95%",
                packagingTransport = "Pack bundles neatly into ventilated plastic crates or woven bamboo baskets.",
                shelfLife = "3 – 5 days under ambient conditions; 7 – 10 days refrigerated."
            )
        )
    }

    private fun createOkraGuide(crop: Crop, meta: com.maptanim.app.data.datasource.CropMetadataInfo?): VegetableAgronomicGuide {
        return VegetableAgronomicGuide(
            crop = crop,
            overview = OverviewInfo(
                summary = "Hardy, heat-loving Malvaceous vegetable renowned for its tender mucilaginous pods. High in soluble dietary fiber, folate, and Vitamin C with low pest pressure.",
                botanicalName = "Abelmoschus esculentus",
                family = "Malvaceae (Mallow / Hibiscus Family)",
                culinaryUses = "Essential for Pinakbet, Sinigang, Dinengdeng, boiled with bagoong sawsawan, and tempura.",
                regionalSuitability = "Nationwide (Thrives in all Philippine lowland and coastal regions).",
                agriculturalImportance = "Extremely resilient to heat, drought, and typhoons; export quality crop to Japan and regional markets."
            ),
            varieties = listOf(
                VarietyDetail("Smooth Green", "Okra Smooth Green", 45, "Spineless, deep green, 5-ridged tender pods with high market acceptance.", "Heat and drought tolerant.", "Year-Round"),
                VarietyDetail("Kamiling Green", "Okra Kamiling", 48, "Vigorous, branching bush producing abundant slender dark green pods.", "Resistant to Yellow Vein Mosaic Virus.", "Year-Round"),
                VarietyDetail("Green Soft F1", "Okra Green Soft", 43, "Early-maturing hybrid with high continuous daily harvest flush.", "High export grade quality.", "Year-Round")
            ),
            growingSeason = GrowingSeasonInfo(
                drySeasonStatus = "EXCELLENT (Loves tropical sunshine and high temperatures)",
                wetSeasonStatus = "GOOD (Very sturdy deep taproot resists strong winds)",
                optimalTemperature = "25°C – 35°C",
                peakMonths = "Year-Round cultivation",
                climateRisks = "Cold temperatures (<20°C) severely slow growth. Frost or chilling stops pod development.",
                weatherTips = listOf(
                    "Thrives in hot summer sun when other vegetables suffer heat stress.",
                    "Tolerates typhoon winds better than climbing vines due to woody upright stem."
                )
            ),
            soil = SoilInfo(
                idealSoilTypes = "Sandy Loam, Clay Loam, Alluvial soil",
                optimalPh = "6.0 – 6.8",
                drainage = "Moderate to good drainage.",
                landPrep = "Plow and harrow 2 times. Form ridges 75 cm apart.",
                organicMatter = "Incorporate 3 to 5 tons/ha compost or vermicast.",
                recommendations = listOf(
                    "Adaptable to wide range of soils including heavy clay.",
                    "Deep taproot can penetrate subsoil layers."
                )
            ),
            planting = PlantingInfo(
                method = "Direct Seeding (2–3 seeds per hill)",
                germinationDays = "4 – 6 days (soak seeds in water overnight to soften hard seed coat)",
                transplantAge = "Not Applicable (Direct seeded)",
                plantSpacing = "30 – 40 cm between plants",
                rowSpacing = "75 – 90 cm between rows",
                plantingDepth = "2 – 3 cm deep",
                trellisingNeeded = false,
                trellisingAdvice = null,
                tips = listOf(
                    "Soak seeds in water for 12 hours before planting for uniform germination.",
                    "Thin to 1 strongest plant per hill at 10 days."
                )
            ),
            watering = WateringInfo(
                frequency = "Every 3 to 4 days (Highly drought tolerant once established)",
                bestTime = "Early morning or late afternoon",
                criticalStages = "Germination and continuous pod development flush",
                irrigationType = "Furrow irrigation or drip",
                moistureConservation = "Mulch with rice straw around base",
                warnings = listOf(
                    "Avoid standing water for more than 24 hours.",
                    "Regular watering produces significantly more tender pods."
                )
            ),
            fertilization = FertilizationInfo(
                npkRatio = "1.5 : 1.0 : 1.5",
                basalApplication = "10g Complete 14-14-14 + 1 handful compost per hill.",
                sideDressing = "Every 15 days: Side-dress 10g Urea (46-0-0) and 10g Complete (14-14-14).",
                organicOptions = "Compost tea and vermicast drench monthly.",
                micronutrients = "General micronutrient foliar spray during flowering.",
                schedule = listOf(
                    "Day 0: Basal NPK in hill",
                    "Day 20: Side-dress nitrogen for vegetative bush branching",
                    "Day 40: Balanced NPK at first flower bud",
                    "Every 15 days during harvest: Nitrogen maintenance"
                )
            ),
            growthStages = listOf(
                GrowthStageItem(1, "Germination", "Days 1–6", "Seeds sprout with broad rounded cotyledons.", "Keep soil moist until emergence."),
                GrowthStageItem(2, "Bush Branching", "Days 7–30", "Strong woody central stem develops with palmate leaves.", "Thin plants and weed ridge furrows."),
                GrowthStageItem(3, "Hibiscus-Like Flowering", "Days 31–45", "Large, beautiful pale yellow flowers with purple centers bloom.", "Side-dress balanced fertilizer."),
                GrowthStageItem(4, "Rapid Pod Formation", "Days 46–50", "Flowers drop and pods elongate rapidly in 4 to 6 days.", "Inspect daily for harvest readiness."),
                GrowthStageItem(5, "Daily Harvest Flush", "Days 50–120+", "Continuous flower-to-pod cycle for 2 to 4 months.", "Harvest every 2 days to stimulate continuous blooming.")
            ),
            pestsAndDiseases = listOf(
                PestDiseaseItem("Cotton Aphids & Leafhoppers", "Insect Pest", "Sap-sucking insects causing leaf curling and transmitting mosaic virus.", "Spray with neem oil extract or soap solution.", "Yellow sticky insect traps."),
                PestDiseaseItem("Corn Earworm / Pod Borer", "Insect Pest", "Caterpillar chewing holes into young pods.", "Handpick or spray Bacillus thuringiensis (BT).", "Intercropping with aromatic herbs."),
                PestDiseaseItem("Yellow Vein Mosaic Virus (YVMV)", "Disease (Viral)", "Bright yellow vein clearing on leaves; stunted yellow pods.", "Control whitefly vectors using neem spray. Rogue infected plants.", "Plant resistant varieties like Kamiling Green.")
            ),
            companionPlants = CompanionInfo(
                beneficialCompanions = listOf("Chili Pepper", "Eggplant", "Cucumber", "Melon"),
                companionBenefits = "Okra attracts beneficial pollinators and traps aphids away from other crops.",
                plantsToAvoid = listOf("Fennel"),
                avoidReasons = "Inhibits general vegetable root development."
            ),
            intercropping = IntercroppingInfo(
                recommendedCrops = listOf("Pechay", "Mustasa", "Radish"),
                spatialLayout = "Plant quick leafy greens between wide okra rows during the first 3 weeks.",
                benefits = "Weed suppression and extra income before okra bushes canopy over.",
                managementAdvice = "Harvest greens before okra reaches knee height."
            ),
            harvest = HarvestInfo(
                maturityIndicators = "Pods are tender, 7–10 cm long, green, and the tip snaps cleanly when bent with finger.",
                daysRange = "45 – 55 days from seeding",
                harvestingMethod = "Cut pod stem with sharp knife or shears wearing gloves to avoid skin itch from pod hairs.",
                timeOfDay = "Early morning or late afternoon",
                frequency = "Every 2 days (Critical: mature pods turn woody and stop plant from flowering)",
                indicatorsList = listOf(
                    "Pod tip snaps off crisply when bent; if it bends without snapping, it is too fibrous",
                    "Pod length is 7 to 10 cm",
                    "Color is bright glossy green without brown streaks"
                )
            ),
            postHarvest = PostHarvestInfo(
                sortingGrading = "Grade into Class A (7-10cm, tender, straight), Class B (10-12cm), and reject woody pods.",
                washingCleaning = "Do not wash in water; wipe clean with dry cloth.",
                storageConditions = "Store in cool humid room at 7°C – 10°C with 90% humidity.",
                optimalTemperature = "7°C – 10°C",
                relativeHumidity = "90% – 95%",
                packagingTransport = "Pack in perforated cartons or woven plastic crates lined with paper, max 10 kg.",
                shelfLife = "3 – 5 days under ambient conditions; 7 – 10 days refrigerated."
            )
        )
    }

    private fun createCabbageGuide(crop: Crop, meta: com.maptanim.app.data.datasource.CropMetadataInfo?): VegetableAgronomicGuide = createGenericGuide(crop, meta)
    private fun createChiliGuide(crop: Crop, meta: com.maptanim.app.data.datasource.CropMetadataInfo?): VegetableAgronomicGuide = createGenericGuide(crop, meta)
    private fun createCornGuide(crop: Crop, meta: com.maptanim.app.data.datasource.CropMetadataInfo?): VegetableAgronomicGuide = createGenericGuide(crop, meta)
    private fun createKangkongGuide(crop: Crop, meta: com.maptanim.app.data.datasource.CropMetadataInfo?): VegetableAgronomicGuide = createGenericGuide(crop, meta)
    private fun createLettuceGuide(crop: Crop, meta: com.maptanim.app.data.datasource.CropMetadataInfo?): VegetableAgronomicGuide = createGenericGuide(crop, meta)
    private fun createOnionGuide(crop: Crop, meta: com.maptanim.app.data.datasource.CropMetadataInfo?): VegetableAgronomicGuide = createGenericGuide(crop, meta)
    private fun createCucumberGuide(crop: Crop, meta: com.maptanim.app.data.datasource.CropMetadataInfo?): VegetableAgronomicGuide = createGenericGuide(crop, meta)
    private fun createSquashGuide(crop: Crop, meta: com.maptanim.app.data.datasource.CropMetadataInfo?): VegetableAgronomicGuide = createGenericGuide(crop, meta)

    private fun createGenericGuide(crop: Crop, meta: com.maptanim.app.data.datasource.CropMetadataInfo?): VegetableAgronomicGuide {
        val days = crop.daysToHarvest.takeIf { it > 0 } ?: 60
        val waterDays = crop.wateringIntervalDays.takeIf { it > 0 } ?: 2
        val phMin = crop.optimalPhMin
        val phMax = crop.optimalPhMax
        val category = crop.category

        return VegetableAgronomicGuide(
            crop = crop,
            overview = OverviewInfo(
                summary = crop.description ?: "${crop.name} (${crop.localName ?: ""}) is a nutritious vegetable crop adapted to Philippine conditions.",
                botanicalName = crop.botanicalName ?: "Brassica / Solanum sp.",
                family = "${crop.category} Family",
                culinaryUses = "Used in diverse Filipino dishes, soups, stir-fries, and fresh table preparations.",
                regionalSuitability = "Cultivated across major Philippine agricultural regions.",
                agriculturalImportance = "Key source of dietary nutrition, vitamins, and stable income for vegetable farmers."
            ),
            varieties = meta?.varieties?.map {
                VarietyDetail(
                    name = it.varietyName,
                    localName = it.localNamePh,
                    daysToHarvest = it.growthDurationDays,
                    characteristics = it.description,
                    diseaseResistance = it.diseaseResistance ?: "General disease tolerance",
                    optimalSeason = it.optimalSeasons.joinToString(", ")
                )
            } ?: listOf(
                VarietyDetail(
                    name = "Standard Commercial Variety",
                    localName = crop.localName ?: crop.name,
                    daysToHarvest = days,
                    characteristics = "High-yielding, vigorous Philippine lowland cultivar.",
                    diseaseResistance = "Standard disease tolerance.",
                    optimalSeason = "Year-Round"
                )
            ),
            growingSeason = GrowingSeasonInfo(
                drySeasonStatus = "OPTIMAL (Under consistent irrigation)",
                wetSeasonStatus = "FAVORABLE (Ensure raised beds and proper drainage)",
                optimalTemperature = "20°C – 32°C",
                peakMonths = "November to May",
                climateRisks = "Prolonged waterlogging and unshaded heat stress.",
                weatherTips = listOf(
                    "Construct raised beds (20–30 cm) to ensure roots remain aerated.",
                    "Use organic mulch to conserve moisture and suppress weeds."
                )
            ),
            soil = SoilInfo(
                idealSoilTypes = "Loam, Sandy Loam with good organic content",
                optimalPh = "$phMin – $phMax",
                drainage = "Well-drained; avoid stagnant water.",
                landPrep = "Plow and harrow twice to 20–30 cm depth. Form smooth raised beds.",
                organicMatter = "Incorporate 5 tons/ha vermicast or composted manure.",
                recommendations = listOf(
                    "Maintain soil pH between $phMin and $phMax for peak nutrient solubility.",
                    "Incorporate carbonized rice hull to enhance drainage."
                )
            ),
            planting = PlantingInfo(
                method = "Transplanting or Direct Seeding based on seed size",
                germinationDays = "5 – 8 days",
                transplantAge = "15 – 25 days after sowing",
                plantSpacing = "30 – 50 cm between hills",
                rowSpacing = "60 – 80 cm between rows",
                plantingDepth = "1 – 2 cm depth",
                trellisingNeeded = crop.category.contains("CUCURBIT", ignoreCase = true) || crop.name.contains("Ampalaya", ignoreCase = true),
                trellisingAdvice = if (crop.category.contains("CUCURBIT", ignoreCase = true)) "Erect bamboo stakes or trellis net." else null,
                tips = listOf(
                    "Water planting holes thoroughly prior to seedling placement.",
                    "Transplant during late afternoon to prevent wilting."
                )
            ),
            watering = WateringInfo(
                frequency = "Every $waterDays day(s) depending on soil moisture",
                bestTime = "Early morning (6:00 AM – 8:00 AM)",
                criticalStages = "Vegetative canopy establishment and flowering/fruiting",
                irrigationType = "Drip, furrow, or fine sprinkler",
                moistureConservation = "Apply organic mulch around root base",
                warnings = listOf(
                    "Avoid overwatering to prevent root rot.",
                    "Do not allow soil to completely dry out during flowering."
                )
            ),
            fertilization = FertilizationInfo(
                npkRatio = "${crop.nRatio} : ${crop.pRatio} : ${crop.kRatio}",
                basalApplication = "Complete 14-14-14 (15g/hill) + compost at planting.",
                sideDressing = "Every 14 days: Alternate Nitrogen (Urea) and Potassium (Muriate of Potash).",
                organicOptions = "Fermented Plant Juice (FPJ) and vermitea foliar drenches.",
                micronutrients = "Calcium and magnesium foliar supplements.",
                schedule = listOf(
                    "Day 0: Basal compost and NPK",
                    "Day 15: Nitrogen vegetative booster",
                    "Day 30: Balanced NPK and potassium for development"
                )
            ),
            growthStages = listOf(
                GrowthStageItem(1, "Germination & Sprout", "Days 1–7", "Seeds germinate and cotyledon leaves emerge.", "Maintain moist seedbed."),
                GrowthStageItem(2, "Seedling Establishment", "Days 8–25", "True leaves develop and roots anchor firmly.", "Harden seedlings before transplanting."),
                GrowthStageItem(3, "Vegetative Growth", "Days 26–45", "Rapid canopy and stem expansion.", "Side-dress fertilizer and weed rows."),
                GrowthStageItem(4, "Flowering / Maturation", "Days 46–60", "Flowers bloom and harvestable organs develop.", "Maintain consistent watering and pest monitoring."),
                GrowthStageItem(5, "Harvest Maturity", "Days 60–$days+", "Reaches peak market maturity and flavor.", "Harvest during cool morning hours.")
            ),
            pestsAndDiseases = listOf(
                PestDiseaseItem("Aphids & Sucking Insects", "Insect Pest", "Sap-sucking insects on underside of leaves causing curling and stunting.", "Spray with neem oil extract or soap solution.", "Yellow sticky cards and clean weeding."),
                PestDiseaseItem("Caterpillars & Borers", "Insect Pest", "Holes chewed in leaves or developing produce.", "Spray Bacillus thuringiensis (BT) or handpick.", "Maintain companion border plants."),
                PestDiseaseItem("Fungal Leaf Blight / Spot", "Disease (Fungal)", "Brown or black necrotic spots on foliage.", "Spray copper fungicide; avoid wetting leaves.", "Ensure good plant spacing and aeration.")
            ),
            companionPlants = CompanionInfo(
                beneficialCompanions = crop.companionPlants.ifEmpty { listOf("Marigold", "Onion", "Basil") },
                companionBenefits = "Repels harmful insect pests with aromatic essential oils and attracts pollinators.",
                plantsToAvoid = crop.avoidPlants.ifEmpty { listOf("Fennel") },
                avoidReasons = "Shared disease susceptibility or allelopathic competition."
            ),
            intercropping = IntercroppingInfo(
                recommendedCrops = listOf("Pechay", "Radish", "Bush Beans"),
                spatialLayout = "Plant quick-growing leafy greens in furrow shoulders between primary crop rows.",
                benefits = "Increases total yield per area and suppresses weeds.",
                managementAdvice = "Harvest intercrops before main crop canopies close."
            ),
            harvest = HarvestInfo(
                maturityIndicators = crop.harvestIndicators ?: "Reaches full variety size, deep characteristic color, and firm texture.",
                daysRange = "$days days from planting",
                harvestingMethod = "Harvest with clean, sharp shears or gentle hand picking.",
                timeOfDay = "Early morning (6:00 AM – 9:00 AM)",
                frequency = "Every 3 to 5 days during productive flush",
                indicatorsList = listOf(
                    "Attains full commercial size and shape",
                    "Firm texture and vibrant coloration",
                    "Harvest before over-maturation or fiber hardening"
                )
            ),
            postHarvest = PostHarvestInfo(
                sortingGrading = "Grade by size, uniformity, and absence of cracks or borer damage into Class A, B, and C.",
                washingCleaning = "Wipe clean or wash with chlorinated water if applicable; remove field heat.",
                storageConditions = "Store in clean, shaded, well-ventilated packing shed.",
                optimalTemperature = "12°C – 15°C",
                relativeHumidity = "85% – 90%",
                packagingTransport = "Pack in ventilated plastic crates lined with banana leaves or paper.",
                shelfLife = "5 – 10 days depending on storage conditions."
            )
        )
    }
}
