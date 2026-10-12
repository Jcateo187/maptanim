package com.maptanim.app.dss.evaluator

import com.maptanim.app.domain.model.AgroZone
import com.maptanim.app.domain.model.FarmEnvironment
import com.maptanim.app.domain.model.Season
import com.maptanim.app.domain.model.SiteConstraint
import com.maptanim.app.domain.model.SoilType

enum class SuitabilityTier(val label: String, val colorHex: Long) {
    OPTIMAL("Optimal", 0xFF2E7D32),       // Lush Green
    MANAGEABLE("Manageable", 0xFFF57C00), // Warm Amber
    CHALLENGING("Challenging", 0xFFD32F2F) // Deep Red
}

data class AlternativeMethod(
    val title: String,
    val challengeDescription: String,
    val stepByStepRemediation: List<String>,
    val materialList: List<String>,
    val agronomicRationale: String,
    val expectedOutcome: String
)

data class PlaceCropSuitability(
    val cropName: String,
    val tier: SuitabilityTier,
    val scorePct: Int,
    val summaryReason: String,
    val positiveFactors: List<String>,
    val challengeFactors: List<String>,
    val alternativeMethods: List<AlternativeMethod>
)

/**
 * PlaceBasedCropEvaluator — Evaluates crop compatibility based on the farm's
 * geographic agro-zone (Highland vs Lowland), base soil type, physical dimensions,
 * active season, and site constraints.
 *
 * If a crop is suboptimal or challenging in that setting, this evaluator provides
 * concrete, actionable compensatory alternative agricultural methods based on
 * published DA-BPI, BSWM, and PhilRice technical guides.
 */
class PlaceBasedCropEvaluator {

    fun evaluate(
        cropName: String,
        environment: FarmEnvironment,
        season: Season = Season.DRY
    ): PlaceCropSuitability {
        val cleanName = cropName.trim().lowercase()
        val zone = environment.zone
        val soil = environment.defaultSoil
        val constraints = environment.constraints

        var score = 100
        val positives = mutableListOf<String>()
        val challenges = mutableListOf<String>()
        val methods = mutableListOf<AlternativeMethod>()

        val isCoolSeason = cleanName in listOf("lettuce", "litsugas", "cabbage", "repolyo", "carrot", "karot")
        val isWarmFruiting = cleanName in listOf("tomato", "kamatis", "eggplant", "talong", "chili", "sili", "okra", "corn", "mais", "sitaw", "cucumber", "pipino", "squash", "kalabasa", "ampalaya")
        val isLeafyFast = cleanName in listOf("pechay", "kangkong", "water_spinach")

        // ── 1. Agro-Zone Evaluation ──────────────────────────────────────────
        if (zone == AgroZone.HIGHLAND) {
            if (isCoolSeason) {
                positives.add("Highland microclimate (>500m) provides the cool temperatures ideal for crisp head and taproot development.")
            } else if (isWarmFruiting) {
                score -= 15
                challenges.add("Cool highland nights (<16°C) can extend days-to-maturity by 15–20 days and slow fruit set.")
                methods.add(
                    AlternativeMethod(
                        title = "Thermal Heat Retention & Selective Pruning",
                        challengeDescription = "Cool nighttime temperatures slow metabolism and fruit ripening for warm-season vegetables.",
                        stepByStepRemediation = listOf(
                            "Line bed perimeters with dark river rocks or dark plastic mulch to absorb heat during the day and radiate warmth at night.",
                            "Pinch auxiliary sucker shoots weekly to concentrate photosynthetic energy into primary fruiting clusters.",
                            "Avoid evening overhead watering to protect root zone from cold shocks."
                        ),
                        materialList = listOf("Dark thermal stones or black plastic mulch", "Pruning shears"),
                        agronomicRationale = "DA-BPI Baguio trials show that root-zone heat retention cushions highland temperature drops, maintaining enzymatic activity.",
                        expectedOutcome = "Prevents delayed blossom set and normalizes harvest timeline."
                    )
                )
            } else {
                positives.add("Highland conditions support healthy, rapid vegetative growth.")
            }
        } else {
            // LOWLAND
            if (isCoolSeason) {
                score -= 45
                challenges.add("Lowland tropical heat (>30°C) triggers heat stress, premature bolting, bitter taste, or head failure.")
                if (cleanName in listOf("lettuce", "litsugas")) {
                    methods.add(
                        AlternativeMethod(
                            title = "Midday 40% Shade Netting & Morning Irrigation",
                            challengeDescription = "High tropical ambient solar radiation causes lettuce to bolt prematurely, turn bitter, and exhibit tipburn.",
                            stepByStepRemediation = listOf(
                                "Install a 40%–50% black monofilament shade net canopy 1.5m above the bed from 10:00 AM to 3:00 PM.",
                                "Select heat-tolerant loose-leaf varieties (e.g. Tropicana, Green Towers) rather than heading iceberg types.",
                                "Water deeply before 7:00 AM with cool water, and apply a light canopy misting at 1:30 PM to suppress leaf temperature.",
                                "Harvest early in the morning before 7:30 AM while leaves retain maximum crispness."
                            ),
                            materialList = listOf("40%–50% Shade net", "Bamboo or PVC frame poles", "Rice-straw mulch"),
                            agronomicRationale = "BSWM microclimate studies confirm that midday 40% shading lowers canopy temperature by 3.5°C, preventing bitter lactucarium synthesis.",
                            expectedOutcome = "Crisp, sweet leaves with zero premature bolting under lowland tropical heat."
                        )
                    )
                } else if (cleanName in listOf("cabbage", "repolyo")) {
                    methods.add(
                        AlternativeMethod(
                            title = "Tropical Hybrid Selection & Insect Exclusion Netting",
                            challengeDescription = "Heading cabbage requires cool nights for firm head compaction and suffers intense Diamondback Moth pressure in lowlands.",
                            stepByStepRemediation = listOf(
                                "Plant exclusively certified heat-tolerant lowland F1 hybrids (e.g. K-S Cross F1 or KK Cross).",
                                "Cover the bed with 32-mesh fine insect netting to exclude Diamondback Moths without pesticides.",
                                "Plant taller companion borders (Okra or Sweet Corn) on the western edge to provide natural afternoon shade."
                            ),
                            materialList = listOf("Lowland hybrid seeds (K-S Cross F1)", "32-mesh insect barrier netting", "Bamboo hoops"),
                            agronomicRationale = "DA-BPI trials demonstrate that K-S Cross F1 initiates head firming even at 32°C when protected from moth defoliation.",
                            expectedOutcome = "Tight, marketable cabbage heads even in lowland summer conditions."
                        )
                    )
                } else if (cleanName in listOf("carrot", "karot")) {
                    methods.add(
                        AlternativeMethod(
                            title = "Deep CRH Bed Aeration & Rice-Straw Insulating Mulch",
                            challengeDescription = "Warm lowland soil causes carrots to develop stunted, fibrous, or branched roots.",
                            stepByStepRemediation = listOf(
                                "Cultivate soil to a minimum depth of 35 cm, blending 50% sand/CRH and 50% mature compost.",
                                "Apply a continuous 5 cm layer of dry rice straw over the soil surface to insulate the root zone from solar heat.",
                                "Sow heat-adapted Chantenay or Terracotta F1 varieties."
                            ),
                            materialList = listOf("Carbonized Rice Hull (CRH)", "Clean river sand", "Rice straw mulch"),
                            agronomicRationale = "BSWM soil temperature monitoring indicates that 5 cm organic mulch maintains root-zone temperatures 4°C cooler than bare soil.",
                            expectedOutcome = "Smooth, straight, tender orange taproots with minimal root branching."
                        )
                    )
                }
            } else if (isWarmFruiting) {
                positives.add("Lowland tropical sunshine and warmth (24–34°C) match the optimal photosynthesis curve for fruiting crops.")
            } else {
                positives.add("Fast-growing greens thrive in lowland morning sunshine.")
            }
        }

        // ── 2. Base Soil Type Evaluation ──────────────────────────────────────
        when (soil) {
            SoilType.LOAM -> {
                positives.add("Loam soil offers a balanced 40-40-20 texture with ideal aeration, root anchorage, and nutrient holding capacity.")
            }
            SoilType.CLAY -> {
                if (cleanName in listOf("tomato", "kamatis", "eggplant", "talong", "chili", "sili") || isCoolSeason) {
                    score -= 25
                    challenges.add("Heavy clay retains excess water during rains, suffocating roots and predisposing crops to Bacterial Wilt (Ralstonia).")
                    methods.add(
                        AlternativeMethod(
                            title = "25–30 cm Elevated Raised Bed & Carbonized Rice Hull Amendment",
                            challengeDescription = "Saturated heavy clay compacts easily, cutting off root oxygen within 48 hours of heavy rain.",
                            stepByStepRemediation = listOf(
                                "Construct 25–30 cm high raised beds enclosed with bamboo splits, wood planks, or hollow blocks.",
                                "Thoroughly mix Carbonized Rice Hull (CRH / Uling na Ipa) and compost at a 1:1 ratio into the top 20 cm of clay soil.",
                                "Excavate 15 cm deep drainage ditches along bed perimeters to divert standing water away from the plot."
                            ),
                            materialList = listOf("Carbonized Rice Hull (CRH)", "Well-rotted compost / vermicast", "Bamboo border stakes"),
                            agronomicRationale = "DA-BPI Technical Bulletin No. 14 confirms that CRH incorporates permanent macropores into heavy clay, preventing Ralstonia bacterial wilt.",
                            expectedOutcome = "Superior drainage, rapid root respiration, and healthy root systems during monsoonal rains."
                        )
                    )
                } else {
                    positives.add("Clay soil provides strong mineral cation exchange capacity, holding nutrients securely once amended.")
                }
            }
            SoilType.SANDY -> {
                if (cleanName in listOf("tomato", "kamatis", "corn", "mais", "pechay", "kangkong")) {
                    score -= 20
                    challenges.add("Sandy soil drains rapidly, resulting in frequent water stress and rapid nutrient leaching.")
                    methods.add(
                        AlternativeMethod(
                            title = "5 cm Organic Mulch Blanket & Inverted Bottle Drip Irrigation",
                            challengeDescription = "Porosity of sandy soil causes rapid moisture loss and leaches essential nitrogen and potassium.",
                            stepByStepRemediation = listOf(
                                "Lay a 5 cm thick blanket of dried rice straw or clean sawdust over the entire bed surface.",
                                "Insert inverted 1.5L PET bottles with perforated caps next to each plant base for steady sub-surface root hydration.",
                                "Split fertilizer dressings into smaller weekly doses of fermented plant juice (FPJ) rather than single heavy applications."
                            ),
                            materialList = listOf("Rice straw or dried leaves (5 cm depth)", "Recycled 1.5L plastic bottles", "Organic compost / vermitea"),
                            agronomicRationale = "Camiguin research trials (Solis et al., 2016) demonstrated that organic mulch significantly conserves soil moisture and increases tomato yield by 18.7%.",
                            expectedOutcome = "Moisture retention increased by up to 60%, preventing blossom end rot and nutrient deficiency."
                        )
                    )
                } else {
                    positives.add("Sandy soil offers effortless root penetration and zero danger of standing-water root rot.")
                }
            }
            else -> {
                positives.add("Soil condition is manageable with regular compost additions.")
            }
        }

        // ── 3. Site Constraints Evaluation ───────────────────────────────────
        if (SiteConstraint.FLOODING in constraints) {
            score -= 20
            challenges.add("Site is prone to water pooling during intense rain events.")
            if (methods.none { it.title.contains("Raised Bed") }) {
                methods.add(
                    AlternativeMethod(
                        title = "Raised Mound Planting & Runoff Trenching",
                        challengeDescription = "Temporary surface flooding drowns roots and rots crowns.",
                        stepByStepRemediation = listOf(
                            "Mound planting ridges 30 cm above ground level.",
                            "Direct perimeter furrows into a downhill soakaway or drainage basin."
                        ),
                        materialList = listOf("Shovel / Hoe for trenching", "Gravel / Sand for drainage base"),
                        agronomicRationale = "Elevating the root crown above surface water level keeps root hairs aerobic even during flash storms.",
                        expectedOutcome = "Zero plant loss from short-term backyard flooding."
                    )
                )
            }
        }

        if (SiteConstraint.WIND_EXPOSURE in constraints) {
            if (cleanName in listOf("corn", "mais", "sitaw", "tomato", "kamatis")) {
                score -= 15
                challenges.add("High wind gusts cause stalk lodging and trellis displacement.")
                methods.add(
                    AlternativeMethod(
                        title = "Bamboo A-Frame Trellis with Guy-Wire Bracing",
                        challengeDescription = "Strong winds can topple tall corn stalks or rip heavy vining plants from their supports.",
                        stepByStepRemediation = listOf(
                            "Build an A-frame structure using seasoned bamboo poles sunk at least 40 cm into the ground.",
                            "Fasten diagonal guy-wires tied to corner ground pegs.",
                            "For corn, hill-up soil around the base at Day 25 to stimulate anchoring prop roots."
                        ),
                        materialList = listOf("Bamboo poles (2.5m)", "Sturdy nylon tie wire", "Wooden ground pegs"),
                        agronomicRationale = "Triangular A-frame geometry distributes wind loads evenly, preventing stem fracturing.",
                        expectedOutcome = "Trellis and tall stalks remain securely upright through seasonal gusty winds."
                    )
                )
            }
        }

        if (SiteConstraint.SHADY in constraints) {
            if (isWarmFruiting || cleanName in listOf("corn", "mais")) {
                score -= 30
                challenges.add("Fruiting crops require at least 6 hours of full direct sunlight to produce flowers and fruit.")
                methods.add(
                    AlternativeMethod(
                        title = "Reflective White Ground Mulch & South-East Orientation",
                        challengeDescription = "Insufficient sunlight leads to leggy stems, blossom drop, and minimal fruit set.",
                        stepByStepRemediation = listOf(
                            "Position this bed on the southeastern edge of your yard to maximize morning sun exposure.",
                            "Spread reflective white plastic mulch or paint nearby perimeter walls white to bounce ambient light into the crop canopy.",
                            "Prune surrounding tree branches to open canopy gaps."
                        ),
                        materialList = listOf("Reflective white mulch or white paint", "Pruning loppers"),
                        agronomicRationale = "Reflective ground covers bounce photosynthetically active radiation (PAR) onto lower foliage, boosting sugar synthesis.",
                        expectedOutcome = "Boosts light availability by 25–35%, sustaining flowering in partial shade."
                    )
                )
            } else if (cleanName in listOf("pechay", "kangkong", "lettuce", "litsugas")) {
                positives.add("Leafy greens tolerate partial shade (4–5 hours sun) with less heat stress.")
            }
        }

        if (SiteConstraint.WATER_SCARCITY in constraints) {
            score -= 15
            challenges.add("Limited irrigation water requires strict conservation measures.")
        }

        if (SiteConstraint.CHICKENS_ANIMALS in constraints) {
            score -= 10
            challenges.add("Free-range chickens or stray animals can scratch and uproot young seedlings.")
            methods.add(
                AlternativeMethod(
                    title = "Thorny Branch Perimeter Barrier & Stick Cages",
                    challengeDescription = "Chickens scratch topsoil for grubs, destroying tender root systems within minutes.",
                    stepByStepRemediation = listOf(
                        "Line the outer perimeter of your bed with cut thorny branches (Bougainvillea or Citrus).",
                        "Drive 4 bamboo sticks around each young plant and wrap with recycled netting or rice sack cloth up to 40 cm height.",
                        "Ensure soil is covered with mulch so bare dirt is not exposed to foraging birds."
                    ),
                    materialList = listOf("Cut thorny branches (Bougainvillea/Calamansi)", "Bamboo sticks", "Discarded netting/rice sacks"),
                    agronomicRationale = "Fowl possess sensitive foot pads and avoid stepping on thorny or physically obstructed surfaces.",
                    expectedOutcome = "Protects seedling root zones completely without purchasing commercial wire fencing."
                )
            )
        }

        if (SiteConstraint.SLOPING_WELL_DRAINED in constraints) {
            positives.add("Natural ground slope ensures rapid surface runoff, drastically lowering waterlogged root rot risk.")
        }

        if (environment.season.contains("Rainy", ignoreCase = true) || environment.season.contains("Wet", ignoreCase = true)) {
            if (cleanName in listOf("tomato", "kamatis")) {
                challenges.add("Rainy conditions splash soil pathogens onto foliage and promote fungal leaf spot.")
                methods.add(
                    AlternativeMethod(
                        title = "Dried Straw Splash Mulch & Lower Leaf Elevation",
                        challengeDescription = "Raindrop impacts splash fungal microbes from wet mud onto lower leaves, initiating blight.",
                        stepByStepRemediation = listOf(
                            "Spread a 5 cm thick blanket of dried rice straw (dayami) or dried leaves over the entire bed surface.",
                            "Prune off all bottom leaves touching the ground or within 15 cm of the soil surface.",
                            "Stake the plant upright with a bamboo pole and tie loosely in a figure-8 knot to keep foliage elevated."
                        ),
                        materialList = listOf("Dried rice straw or dried grass mulch", "Bamboo stake (1.5m)", "Soft cloth strips"),
                        agronomicRationale = "Straw mulch absorbs falling raindrops, neutralizing soil-to-leaf splash inoculations.",
                        expectedOutcome = "Prevents soil-borne fungal leaf spot and keeps stems dry and elevated."
                    )
                )
            }
        }

        val clampedScore = score.coerceIn(15, 100)
        val tier = when {
            clampedScore >= 80 -> SuitabilityTier.OPTIMAL
            clampedScore >= 50 -> SuitabilityTier.MANAGEABLE
            else -> SuitabilityTier.CHALLENGING
        }

        val summary = when (tier) {
            SuitabilityTier.OPTIMAL -> "Thrives naturally in your ${zone.label} ${soil.name.lowercase()} environment with standard care."
            SuitabilityTier.MANAGEABLE -> "Grows well in your ${zone.label} setting with minor routine cultural adjustments."
            SuitabilityTier.CHALLENGING -> "Challenging in this environment without applying recommended compensatory methods."
        }

        return PlaceCropSuitability(
            cropName = cropName,
            tier = tier,
            scorePct = clampedScore,
            summaryReason = summary,
            positiveFactors = positives,
            challengeFactors = challenges,
            alternativeMethods = methods
        )
    }
}
