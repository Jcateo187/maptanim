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
import com.maptanim.app.features.farm.schedule.SpecificVegetable
import com.maptanim.app.features.farm.schedule.detectSpecificVegetable
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

            val stage = calculator.calculateForCrop(crop.name, plantedDate, crop.daysToHarvest, today)
            val daysElapsed = ChronoUnit.DAYS.between(plantedDate, today).toInt().coerceAtLeast(0)
            val specificVeg = detectSpecificVegetable(crop.name)
            val isTomato = specificVeg == SpecificVegetable.TOMATO

            // 1. Irrigation Evaluation (Crop-Specific Water Management)
            val lastWatered = input.farmerData.recentActivities
                .filter { it.plotId == plot.id && it.type == TaskType.WATER }
                .mapNotNull { runCatching { LocalDate.parse(it.performedAt.take(10)) }.getOrNull() }
                .maxOrNull()

            val effectiveInterval = when (specificVeg) {
                SpecificVegetable.PECHAY, SpecificVegetable.LETTUCE -> 1
                SpecificVegetable.CARROT, SpecificVegetable.POTATO -> 3
                SpecificVegetable.SQUASH -> 4
                SpecificVegetable.CABBAGE -> 2
                else -> crop.wateringIntervalDays.coerceAtLeast(1)
            }

            val daysSinceWater = lastWatered?.let { ChronoUnit.DAYS.between(it, today).toInt() }
                ?: effectiveInterval

            if (daysSinceWater >= effectiveInterval) {
                val (waterSummary, waterExplanation) = when (specificVeg) {
                    SpecificVegetable.PECHAY -> Pair(
                        "Drench 1-2 liters gently at soil base at 6:00 AM. Never water the center of the leafy rosette to avoid bacterial soft rot.",
                        "Pechay shallow fibrous roots require high surface moisture; avoiding wet leaves prevents bacterial soft rot (Pectobacterium)."
                    )
                    SpecificVegetable.LETTUCE -> Pair(
                        "Water base gently at 6:00 AM. Maintain consistent root zone coolness and hydration to prevent tipburn and premature bolting.",
                        "Erratic watering or hot dry root zones induce bitter latex and calcium-deficiency tipburn in crisphead/looseleaf lettuce."
                    )
                    SpecificVegetable.CARROT -> Pair(
                        "Water evenly between rows (1-2 liters/meter). Maintain even moisture throughout root profile to prevent taproot cracking.",
                        "Alternating severe dry and wet spells causes carrot taproot splitting and rough hairy lateral root development."
                    )
                    SpecificVegetable.POTATO -> Pair(
                        "Provide moderate base watering around ridges every 3 days. Keep soil moist but never saturated to protect developing tubers.",
                        "Excess stagnant water suffocates underground stolons and induces blackleg or soft rot, while drought stalls tuber expansion."
                    )
                    SpecificVegetable.SQUASH -> Pair(
                        "Deep soak 2–3 liters around hill base. Keep large broad leaves completely dry to prevent powdery mildew spores.",
                        "Squash deep taproots thrive on deep, infrequent watering. Wet foliage rapidly develops powdery and downy mildew under humid heat."
                    )
                    SpecificVegetable.BITTER_GOURD -> Pair(
                        "Deep water 1–2 liters at root base under trellis every 2 days in the cool morning to support continuous juicy vine growth.",
                        "Ampalaya requires steady hydration during vine elongation and fruit development to ensure firm, crisp fruit ridges."
                    )
                    SpecificVegetable.CUCUMBER -> Pair(
                        "Water 1–2 liters at base at 6:30 AM. Maintain steady moisture so cucumbers grow straight and avoid cucurbitacin bitterness.",
                        "Moisture stress causes bitter chemical (cucurbitacin) buildup and crooked, misshapen cucumber fruits."
                    )
                    SpecificVegetable.STRING_BEANS -> Pair(
                        "Water at root base under trellis. Keep foliage dry to prevent bean rust (Uromyces) and anthracnose from spreading.",
                        "Yardlong beans need root-zone moisture during flowering and pod setting; wet foliage encourages fungal rust lesions."
                    )
                    SpecificVegetable.BELL_PEPPER -> Pair(
                        "Water 1–2 liters at plant base under mulch. Avoid leaf splashing to protect flower clusters and stop blossom-end rot.",
                        "Fluctuating moisture halts calcium uptake in sweet pepper fruit tips, causing sunken leathery blossom-end rot."
                    )
                    SpecificVegetable.EGGPLANT -> Pair(
                        "Water 2 liters deeply at base. Moist soil supports continuous branching and high glossy fruit production.",
                        "Eggplant is a heavy feeder and drinker; consistent soil moisture sustains prolonged harvesting flushes."
                    )
                    SpecificVegetable.CABBAGE -> Pair(
                        "Water base evenly every 2 days. Avoid irregular heavy waterings during head formation to stop heads from splitting open.",
                        "Sudden moisture spikes after dry spells cause mature, compact cabbage heads to burst and split."
                    )
                    SpecificVegetable.GREEN_ONION -> Pair(
                        "Water base under stem at 6:00 AM. Keep soil damp but ensure good drainage around the basal plate.",
                        "Green onions thrive with steady moisture but rot if standing water accumulates around the bulb plate."
                    )
                    else -> Pair(
                        "Water 1 tabo (1–2 liters) gently at the base of each ${crop.name} in the cool morning. Never splash water directly on leaves to prevent fungal infection.",
                        "Consistent root-zone moisture prevents wilting, supports steady development, and protects blossoms for ${crop.name}."
                    )
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
                        priority = if (daysSinceWater > effectiveInterval + 1) DssPriority.CRITICAL else DssPriority.HIGH,
                        title = "Water ${crop.name} on ${plot.plotLabel}",
                        summary = waterSummary,
                        explanation = waterExplanation,
                        source = "DA-BAR Lowland Vegetables Production Handbook (Smallholder Guide)",
                        actionText = "Diligan ang Halaman",
                        actionTaskType = TaskType.WATER,
                        evaluatedAt = today.toString()
                    )
                )
            }

            // 2. Fertilization / Organic Nutrition Scheduling by Vegetable & Stage
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
                    val (nutritionTitle, nutritionSummary, nutritionExpl) = when (specificVeg) {
                        SpecificVegetable.TOMATO -> {
                            if (stage == CropGrowthStage.FLOWERING || stage == CropGrowthStage.RIPENING) {
                                Triple(
                                    "Calcium & Potassium Boost for Flowering Tomato",
                                    "Add 1 handful of vermicast and sprinkle a spoonful of crushed eggshells (for calcium) or a pinch of wood ash around the plant base.",
                                    "Calcium stops blossom-end rot in developing tomatoes, while potassium maximizes fruit sweetness and solid wall development."
                                )
                            } else {
                                Triple(
                                    "Organic Nitrogen & Compost for Vegetative Tomato",
                                    "Spread 1 to 2 handfuls of rich compost or vermicast around each plant, or water with rice-wash water (hugas-bigas).",
                                    "Feeds active vegetative branch development and establishes a dense fibrous root system."
                                )
                            }
                        }
                        SpecificVegetable.EGGPLANT -> {
                            if (stage == CropGrowthStage.FLOWERING || stage == CropGrowthStage.RIPENING) {
                                Triple(
                                    "Flower Retention & Fruit Set Feed for Eggplant",
                                    "Spray Fermented Fruit Juice (FFJ, 2 tbsp/1L) and apply 1 handful of compost mixed with eggshell calcium 10cm from base.",
                                    "Maintains prolific purple flowering and prevents flower drop (abscission) during hot weather."
                                )
                            } else {
                                Triple(
                                    "Basal Vermicast & Growth Booster for Eggplant",
                                    "Spread 1 handful of vermicast around drip line (5cm from stem) and drench with fermented plant juice (FPJ).",
                                    "Stimulates vigorous stem branching and thick leather foliage capable of holding heavy fruit clusters."
                                )
                            }
                        }
                        SpecificVegetable.PECHAY -> {
                            if (daysElapsed >= 14) {
                                Triple(
                                    "Foliar Nitrogen & Petiole Boost for Pechay",
                                    "Foliar spray with Fermented Plant Juice (FPJ, 2 tbsp/1L water) and drench soil with vermicast tea in late afternoon.",
                                    "Accelerates vegetative expansion for broad, tender green leaves and succulent, crunchy white petioles before harvest."
                                )
                            } else {
                                Triple(
                                    "Starter Vermicast Drench for Pechay Seedlings",
                                    "Drench soil with vermicompost tea extract around seedlings to stimulate rapid white fibrous root initiation.",
                                    "Organic compost extracts establish rapid root networks critical for Pechay's 28–30 day growth cycle."
                                )
                            }
                        }
                        SpecificVegetable.LETTUCE -> {
                            Triple(
                                "Organic Tea & Calcium Drench for Crisp Lettuce",
                                "Drench with diluted compost tea and spray steeped eggshell solution around root perimeter; avoid heavy chemical nitrogen.",
                                "Excess nitrogen turns lettuce leaves bitter, while eggshell calcium prevents leaf tipburn and promotes crisp, sweet texture."
                            )
                        }
                        SpecificVegetable.CARROT -> {
                            if (daysElapsed >= 40) {
                                Triple(
                                    "Potassium Side-dress for Carrot Root Bulking",
                                    "Side-dress with dry wood ash (abo ng kahoy, rich in potassium) between rows and mulch lightly. Avoid high-nitrogen fertilizers.",
                                    "Potassium fuels taproot bulking, sweetness, and beta-carotene synthesis. High nitrogen causes misshapen, hairy, and forked taproots."
                                )
                            } else {
                                Triple(
                                    "Starter Phosphorus & Compost for Carrot Taproot",
                                    "Drench between seedling lines with vermitea and bone meal extract to encourage deep taproot downward elongation.",
                                    "Phosphorus drives downward vertical taproot growth into loose soil before root thickening begins."
                                )
                            }
                        }
                        SpecificVegetable.POTATO -> {
                            if (daysElapsed >= 30) {
                                Triple(
                                    "Potassium & Tuber Bulking Feed for Potato",
                                    "Apply wood ash and rich compost prior to hilling up; spray Fermented Fruit Juice (FFJ) in late afternoon.",
                                    "Fuels underground stolon enlargement into heavy, nutrient-dense potato tubers without excess foliage overgrowth."
                                )
                            } else {
                                Triple(
                                    "Starter Vermicast for Potato Stems",
                                    "Side-dress 1 handful of vermicast at the base of emerging stems to promote strong canopy emergence.",
                                    "Vigorous initial stem growth provides the photosynthetic capacity needed for later tuber initiation."
                                )
                            }
                        }
                        SpecificVegetable.SQUASH -> {
                            if (stage == CropGrowthStage.FLOWERING || stage == CropGrowthStage.RIPENING || daysElapsed >= 40) {
                                Triple(
                                    "Potassium & Calcium Feed for Bulking Squash",
                                    "Side-dress 1 cup of wood ash and compost around hill perimeter; spray eggshell solution for female flower retention.",
                                    "High potassium thickens squash flesh, raises carotene content, and hardens the outer rind for longer post-harvest shelf life."
                                )
                            } else {
                                Triple(
                                    "Compost Hill Booster for Squash Vines",
                                    "Apply 2 handfuls of rich compost around each hill and spray FPJ to promote vigorous runner expansion.",
                                    "Builds dense, expansive leaf canopies that shade soil, suppress weeds, and harvest maximum sunlight."
                                )
                            }
                        }
                        SpecificVegetable.BITTER_GOURD -> {
                            if (stage == CropGrowthStage.FLOWERING || stage == CropGrowthStage.RIPENING || daysElapsed >= 35) {
                                Triple(
                                    "Fruit Setting & Trellis Feed for Ampalaya",
                                    "Foliar spray with Fermented Fruit Juice (FFJ) and eggshell calcium; side-dress organic compost at hill base.",
                                    "Supplies potassium and calcium to prevent blossom drop and develop deep green, warty, heavy gourds."
                                )
                            } else {
                                Triple(
                                    "Vine Vigor Organic Booster for Ampalaya",
                                    "Apply 1 handful of vermicast at base and spray FPJ to stimulate vigorous climbing tendrils up the overhead trellis.",
                                    "Builds strong primary vines that branch prolifically across the bamboo trellis canopy."
                                )
                            }
                        }
                        SpecificVegetable.CUCUMBER -> {
                            if (stage == CropGrowthStage.FLOWERING || stage == CropGrowthStage.RIPENING || daysElapsed >= 25) {
                                Triple(
                                    "Continuous Harvest Potassium Feed for Pipino",
                                    "Apply wood ash tea and compost at drip line every 7 days; spray FFJ to promote steady fruit elongation.",
                                    "Supports non-stop formation of crisp, sweet, straight green cucumbers during extended picking flushes."
                                )
                            } else {
                                Triple(
                                    "Starter Compost & Trellis Guide Feed for Pipino",
                                    "Drench base with compost tea to establish vigorous root systems and strong climbing stems.",
                                    "Rapid early growth allows cucumber vines to ascend trellis netting before flowering begins."
                                )
                            }
                        }
                        SpecificVegetable.STRING_BEANS -> {
                            if (stage == CropGrowthStage.FLOWERING || stage == CropGrowthStage.RIPENING || daysElapsed >= 30) {
                                Triple(
                                    "Pod Setting Potassium & Calcium Feed for Sitaw",
                                    "Spray eggshell calcium tea and wood ash extract; avoid high nitrogen as beans fix their own nitrogen via rhizobia.",
                                    "Excess nitrogen causes excessive foliage at the expense of bean pods; calcium and potassium maximize pod length and tenderness."
                                )
                            } else {
                                Triple(
                                    "Light Compost & Trellis Booster for Sitaw",
                                    "Apply light vermicompost drench around hill; beans utilize symbiotic nitrogen-fixing root nodules.",
                                    "Gentle organic amendment promotes healthy nodule colonization on legume roots."
                                )
                            }
                        }
                        SpecificVegetable.BELL_PEPPER -> {
                            if (stage == CropGrowthStage.FLOWERING || stage == CropGrowthStage.RIPENING || daysElapsed >= 35) {
                                Triple(
                                    "Calcium Phosphate & FFJ Feed for Bell Pepper",
                                    "Foliar spray fermented eggshell calcium vinegar solution and FFJ; side-dress compost at drip line.",
                                    "Prevents sunken blossom-end rot and produces thick-walled, heavy, glossy sweet peppers."
                                )
                            } else {
                                Triple(
                                    "Basal Vermicast & Growth Feed for Bell Pepper",
                                    "Apply 1 handful of vermicast 5cm from stem base and spray FPJ for thick, sturdy main stems.",
                                    "Creates strong main stem caliper required to support heavy fruit sets without snapping."
                                )
                            }
                        }
                        SpecificVegetable.CABBAGE -> {
                            if (daysElapsed >= 35) {
                                Triple(
                                    "Head Packing Potassium & Calcium Feed for Cabbage",
                                    "Side-dress wood ash and compost; spray eggshell solution to ensure dense, solid head formation without internal tipburn.",
                                    "Potassium packs outer wrapper leaves tightly around the heart, while calcium prevents internal tipburn."
                                )
                            } else {
                                Triple(
                                    "Frame Leaf Vegetative Drench for Cabbage",
                                    "Drench with vermicompost tea and FPJ to expand wide, healthy outer wrapper leaves.",
                                    "Outer frame leaves serve as solar power plants that later funnel sugars into the forming cabbage head."
                                )
                            }
                        }
                        SpecificVegetable.GREEN_ONION -> {
                            Triple(
                                "Organic Compost & Wood Ash for Green Onion",
                                "Side-dress a thin band of dry wood ash and compost between rows; drench with compost tea.",
                                "Potassium strengthens tubular cell walls, giving scallions their crisp texture and pest resistance."
                            )
                        }
                        else -> {
                            Triple(
                                "Add Compost / Natural Plant Food for ${crop.name}",
                                "Apply 1-2 handfuls of compost or balanced organic amendment around plant base.",
                                "Natural organic compost feeds soil microorganisms and slowly releases nutrients without burning roots."
                            )
                        }
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
                            title = nutritionTitle,
                            summary = nutritionSummary,
                            explanation = nutritionExpl,
                            source = "DA-BPI National Organic Agriculture Program (NOAP)",
                            actionText = "Maglagay ng Pataba",
                            actionTaskType = TaskType.FERTILIZE,
                            evaluatedAt = today.toString()
                        )
                    )
                }
            }

            // 3. Vegetable-Specific Structural & Agronomic Care (Staking, Trellis, Hilling, Thinning, Blanching)
            when (specificVeg) {
                SpecificVegetable.TOMATO, SpecificVegetable.EGGPLANT, SpecificVegetable.BELL_PEPPER -> {
                    if (daysElapsed in 20..40) {
                        val hasStaked = input.farmerData.recentActivities.any { it.plotId == plot.id && it.type == TaskType.TRELLIS }
                        if (!hasStaked) {
                            decisions.add(
                                DssDecision(
                                    id = "dss_task_stake_${plot.id}",
                                    farmId = farmId,
                                    plotId = plot.id,
                                    plotLabel = plot.plotLabel,
                                    cropName = crop.name,
                                    decisionType = DssDecisionType.TASK,
                                    category = DssCategory.GROWTH_CARE,
                                    priority = DssPriority.HIGH,
                                    title = "Push Bamboo Stake (Tulos) & Figure-8 Tie for ${crop.name}",
                                    summary = "Plants are growing tall (~$daysElapsed days). Push a sturdy bamboo stake (1.2–1.5m) beside each plant and tie the stem loosely in a figure-8 knot.",
                                    explanation = "Staking prevents heavy fruit-laden stems from lodging onto wet soil, stopping fungal fruit rot and bacterial wilt.",
                                    source = "DA-BPI National Vegetable Production Technical Guide",
                                    actionText = "Lagyan ng Tulos / Tukod",
                                    actionTaskType = TaskType.TRELLIS,
                                    evaluatedAt = today.toString()
                                )
                            )
                        }
                    }
                }
                SpecificVegetable.BITTER_GOURD -> {
                    if (daysElapsed in 14..30) {
                        val hasTrellised = input.farmerData.recentActivities.any { it.plotId == plot.id && it.type == TaskType.TRELLIS }
                        if (!hasTrellised) {
                            decisions.add(
                                DssDecision(
                                    id = "dss_task_trellis_ampalaya_${plot.id}",
                                    farmId = farmId,
                                    plotId = plot.id,
                                    plotLabel = plot.plotLabel,
                                    cropName = crop.name,
                                    decisionType = DssDecisionType.TASK,
                                    category = DssCategory.GROWTH_CARE,
                                    priority = DssPriority.HIGH,
                                    title = "Erect Overhead Trellis (Balag) for Ampalaya",
                                    summary = "Vines are sending out climbing tendrils (~$daysElapsed days). Erect a 1.8m bamboo overhead arbor (balag) and guide vines upward.",
                                    explanation = "Overhead arbor provides full sun exposure, prevents ground contact rot, and simplifies pest monitoring and fruit bagging.",
                                    source = "DA-BAR Cucurbit Production & Trellising Manual",
                                    actionText = "Itayo ang Balag",
                                    actionTaskType = TaskType.TRELLIS,
                                    evaluatedAt = today.toString()
                                )
                            )
                        }
                    }
                }
                SpecificVegetable.CUCUMBER -> {
                    if (daysElapsed in 10..25) {
                        val hasTrellised = input.farmerData.recentActivities.any { it.plotId == plot.id && it.type == TaskType.TRELLIS }
                        if (!hasTrellised) {
                            decisions.add(
                                DssDecision(
                                    id = "dss_task_trellis_cucumber_${plot.id}",
                                    farmId = farmId,
                                    plotId = plot.id,
                                    plotLabel = plot.plotLabel,
                                    cropName = crop.name,
                                    decisionType = DssDecisionType.TASK,
                                    category = DssCategory.GROWTH_CARE,
                                    priority = DssPriority.HIGH,
                                    title = "Install Vertical Trellis Netting for Cucumber",
                                    summary = "Cucumber vines are expanding (~$daysElapsed days). Install vertical trellis netting or bamboo frames and guide vines upward.",
                                    explanation = "Vertical trellising keeps cucumbers off damp soil, ensuring straight fruits and reducing downy mildew by 70%.",
                                    source = "DA-BPI Protected Cropping Guidelines for Cucurbits",
                                    actionText = "Ayusin ang Lambat / Trellis",
                                    actionTaskType = TaskType.TRELLIS,
                                    evaluatedAt = today.toString()
                                )
                            )
                        }
                    }
                }
                SpecificVegetable.STRING_BEANS -> {
                    if (daysElapsed in 12..28) {
                        val hasTrellised = input.farmerData.recentActivities.any { it.plotId == plot.id && it.type == TaskType.TRELLIS }
                        if (!hasTrellised) {
                            decisions.add(
                                DssDecision(
                                    id = "dss_task_trellis_sitaw_${plot.id}",
                                    farmId = farmId,
                                    plotId = plot.id,
                                    plotLabel = plot.plotLabel,
                                    cropName = crop.name,
                                    decisionType = DssDecisionType.TASK,
                                    category = DssCategory.GROWTH_CARE,
                                    priority = DssPriority.HIGH,
                                    title = "Erect A-Frame Bamboo Trellis for Sitaw",
                                    summary = "Yardlong bean vines are twining (~$daysElapsed days). Erect 1.8m A-frame bamboo poles and guide twining tips in a clockwise direction.",
                                    explanation = "Sitaw twines clockwise naturally. Trellising keeps pods hanging straight, prevents pod borer infestation, and eases harvesting.",
                                    source = "DA-BPI Legume Technical Manual",
                                    actionText = "Itayo ang A-Frame Trellis",
                                    actionTaskType = TaskType.TRELLIS,
                                    evaluatedAt = today.toString()
                                )
                            )
                        }
                    }
                }
                SpecificVegetable.POTATO -> {
                    if (daysElapsed in 18..35) {
                        val hasHilled = input.farmerData.recentActivities.any { it.plotId == plot.id && it.type == TaskType.SOIL_AMENDMENT }
                        if (!hasHilled) {
                            decisions.add(
                                DssDecision(
                                    id = "dss_task_hill_potato_${plot.id}",
                                    farmId = farmId,
                                    plotId = plot.id,
                                    plotLabel = plot.plotLabel,
                                    cropName = crop.name,
                                    decisionType = DssDecisionType.TASK,
                                    category = DssCategory.GROWTH_CARE,
                                    priority = DssPriority.HIGH,
                                    title = "Perform 1st Hilling-Up (Tambak Lupa) for Potato",
                                    summary = "Emerging stems are ~15cm tall (~$daysElapsed days). Mound 7cm of loose loam soil and compost around stem bases.",
                                    explanation = "Hilling up stimulates stolon formation where tubers develop and blocks sunlight that turns potatoes green and toxic (solanine).",
                                    source = "DA-BPI Highland & Lowland Potato Production Compendium",
                                    actionText = "Magtabon ng Lupa (Hilling)",
                                    actionTaskType = TaskType.SOIL_AMENDMENT,
                                    evaluatedAt = today.toString()
                                )
                            )
                        }
                    }
                }
                SpecificVegetable.CARROT -> {
                    if (daysElapsed in 20..35) {
                        val hasThinned = input.farmerData.recentActivities.any { it.plotId == plot.id && it.type == TaskType.WEED }
                        if (!hasThinned) {
                            decisions.add(
                                DssDecision(
                                    id = "dss_task_thin_carrot_${plot.id}",
                                    farmId = farmId,
                                    plotId = plot.id,
                                    plotLabel = plot.plotLabel,
                                    cropName = crop.name,
                                    decisionType = DssDecisionType.TASK,
                                    category = DssCategory.GROWTH_CARE,
                                    priority = DssPriority.HIGH,
                                    title = "Crucial Seedling Thinning to 5–8cm for Carrot",
                                    summary = "Feathery seedlings are crowded (~$daysElapsed days). Thin seedlings to 5–8cm spacing. Cook thinned greens as microgreens.",
                                    explanation = "Overcrowded carrot seedlings compete intensely underground, resulting in twisted, skinny, forked, unmarketable taproots.",
                                    source = "DA-BAR Root Vegetable Production Bulletin",
                                    actionText = "Magpanipis ng Punla (Thinning)",
                                    actionTaskType = TaskType.WEED,
                                    evaluatedAt = today.toString()
                                )
                            )
                        }
                    }
                }
                SpecificVegetable.PECHAY -> {
                    if (daysElapsed in 6..12) {
                        decisions.add(
                            DssDecision(
                                id = "dss_task_thin_pechay_${plot.id}",
                                farmId = farmId,
                                plotId = plot.id,
                                plotLabel = plot.plotLabel,
                                cropName = crop.name,
                                decisionType = DssDecisionType.TASK,
                                category = DssCategory.GROWTH_CARE,
                                priority = DssPriority.HIGH,
                                title = "Thin Pechay to 10–15cm & Apply Rice Straw Mulch",
                                summary = "Seedlings are established (~$daysElapsed days). Thin to 10–15cm spacing and mulch bed with clean dried rice straw.",
                                explanation = "Proper spacing allows full petiole rosette expansion. Rice straw mulch prevents soil mud from splashing onto leaves during rain.",
                                source = "DA-BPI Leafy Brassica Production Guide",
                                actionText = "Magpanipis at Mag-mulch",
                                actionTaskType = TaskType.WEED,
                                evaluatedAt = today.toString()
                            )
                        )
                    }
                }
                SpecificVegetable.LETTUCE -> {
                    if (daysElapsed in 12..25) {
                        decisions.add(
                            DssDecision(
                                id = "dss_task_shade_lettuce_${plot.id}",
                                farmId = farmId,
                                plotId = plot.id,
                                plotLabel = plot.plotLabel,
                                cropName = crop.name,
                                decisionType = DssDecisionType.TASK,
                                category = DssCategory.GROWTH_CARE,
                                priority = DssPriority.MEDIUM,
                                title = "Erect 30% Shade Netting for Lettuce (11 AM – 2 PM)",
                                summary = "Midday tropical sun is intense. Erect temporary 30% black netting or light canopy to shield lettuce between 11:00 AM and 2:00 PM.",
                                explanation = "Direct afternoon heat scorches tender lettuce leaf margins, triggers bitter latex production, and causes premature bolting.",
                                source = "DA-BPI Urban Agriculture & Protected Cropping Protocol",
                                actionText = "Maglagay ng Shade Net",
                                actionTaskType = TaskType.OBSERVATION,
                                evaluatedAt = today.toString()
                            )
                        )
                    }
                }
                SpecificVegetable.GREEN_ONION -> {
                    if (daysElapsed in 22..40) {
                        decisions.add(
                            DssDecision(
                                id = "dss_task_blanch_onion_${plot.id}",
                                farmId = farmId,
                                plotId = plot.id,
                                plotLabel = plot.plotLabel,
                                cropName = crop.name,
                                decisionType = DssDecisionType.TASK,
                                category = DssCategory.GROWTH_CARE,
                                priority = DssPriority.MEDIUM,
                                title = "Stem Blanching (Earthing-Up) for Green Onion",
                                summary = "Scallion stems are elongating (~$daysElapsed days). Mound 4cm of friable soil around the lower stems.",
                                explanation = "Excluding sunlight from the stem base blanches the lower pseudostem, producing long, tender, crisp white stalks.",
                                source = "DA-BAR Allium Crop Management Guide",
                                actionText = "Magtabon sa Tangkay (Blanch)",
                                actionTaskType = TaskType.SOIL_AMENDMENT,
                                evaluatedAt = today.toString()
                            )
                        )
                    }
                }
                SpecificVegetable.SQUASH -> {
                    if (daysElapsed in 18..35) {
                        decisions.add(
                            DssDecision(
                                id = "dss_task_guide_squash_${plot.id}",
                                farmId = farmId,
                                plotId = plot.id,
                                plotLabel = plot.plotLabel,
                                cropName = crop.name,
                                decisionType = DssDecisionType.TASK,
                                category = DssCategory.GROWTH_CARE,
                                priority = DssPriority.MEDIUM,
                                title = "Guide Squash Runners over Thick Rice Straw Bed",
                                summary = "Vines are running fast (~$daysElapsed days). Orient vines in a circular pattern over thick, clean rice straw mulch away from walkways.",
                                explanation = "Dayami barrier prevents squash stems and developing female flower buds from contacting wet soil pathogens.",
                                source = "DA-BPI Lowland Cucurbit Production Guide",
                                actionText = "Igabay ang Baging sa Dayami",
                                actionTaskType = TaskType.OBSERVATION,
                                evaluatedAt = today.toString()
                            )
                        )
                    }
                }
                else -> {}
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
                            summary = "Maintain 0.5m weed-free circle around ${crop.name} base.",
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

            // 5. Research Yield Studies & Specialized IPM Protocols by Vegetable
            when (specificVeg) {
                SpecificVegetable.TOMATO -> {
                    val studyA = input.referenceData.yieldStudies.firstOrNull { it.studyCode == "STUDY_A_CAMIGUIN_2016" }
                    val studyB = input.referenceData.yieldStudies.firstOrNull { it.studyCode == "STUDY_B_BACNOTAN_2025" }

                    if (stage == CropGrowthStage.VEGETATIVE || daysElapsed in 15..35) {
                        decisions.add(
                            DssDecision(
                                id = "dss_rec_mulch_camiguin_${plot.id}",
                                farmId = farmId, plotId = plot.id, plotLabel = plot.plotLabel, cropName = crop.name,
                                decisionType = DssDecisionType.RECOMMENDATION, category = DssCategory.GROWTH_CARE, priority = DssPriority.MEDIUM,
                                title = "Sawdust / Straw Mulch Protocol (Camiguin Study)",
                                summary = studyA?.treatmentDescription ?: "Apply 5cm organic sawdust mulch to achieve peak yield of 5.08 t/ha for Diamante Max F1.",
                                explanation = studyA?.keyFindings ?: "Field research in Catarman, Camiguin (Solis et al., 2016) proved that sawdust mulch (V1M4) on Diamante Max F1 elevated yield from 4.28 t/ha to 5.08 t/ha by reducing soil water evaporation, cooling root zones, and suppressing weed competition.",
                                source = studyA?.publicationReference ?: "Camiguin Polytechnic State College Agronomic Field Trial (2015–2016)",
                                actionText = "Apply Sawdust Mulch", actionTaskType = TaskType.SOIL_AMENDMENT, evaluatedAt = today.toString()
                            )
                        )
                    }

                    if (stage == CropGrowthStage.FLOWERING || stage == CropGrowthStage.RIPENING || daysElapsed in 35..55) {
                        decisions.add(
                            DssDecision(
                                id = "dss_rec_trehalose_bacnotan_${plot.id}",
                                farmId = farmId, plotId = plot.id, plotLabel = plot.plotLabel, cropName = crop.name,
                                decisionType = DssDecisionType.RECOMMENDATION, category = DssCategory.GROWTH_CARE, priority = DssPriority.MEDIUM,
                                title = "Trehalose Foliar Protocol (Bacnotan 2025 Study)",
                                summary = studyB?.treatmentDescription ?: "Apply trehalose foliar spray (2 tbsp / 16L water) thrice during flowering to maximize fruit count and harvest frequency.",
                                explanation = studyB?.keyFindings ?: "Bacnotan, La Union research under protected cultivation (IJEAB, 2025) demonstrated that 3x foliar trehalose applications significantly enhanced marketable fruit count and sustained continuous harvest without excess input cost.",
                                source = studyB?.publicationReference ?: "International Journal of Environment, Agriculture and Biotechnology (Bacnotan Study 2025)",
                                actionText = "Schedule Foliar Spray", actionTaskType = TaskType.FERTILIZE, evaluatedAt = today.toString()
                            )
                        )
                    }

                    if (stage == CropGrowthStage.FLOWERING || stage == CropGrowthStage.RIPENING) {
                        decisions.add(
                            DssDecision(
                                id = "dss_task_borer_scout_${plot.id}",
                                farmId = farmId, plotId = plot.id, plotLabel = plot.plotLabel, cropName = crop.name,
                                decisionType = DssDecisionType.TASK, category = DssCategory.PEST_DISEASE, priority = DssPriority.HIGH,
                                title = "Check for Caterpillars (Uod sa Bunga) on ${plot.plotLabel}",
                                summary = "Inspect flower clusters and baby green tomatoes in the morning. Pick off caterpillars by hand and spray mild garlic-chili or neem water.",
                                explanation = "Catching caterpillars early prevents them from drilling holes into developing tomatoes.",
                                source = "DA-BPI Lowland Vegetable IPM Guide", actionText = "Tingnan ang Halaman", actionTaskType = TaskType.OBSERVATION, evaluatedAt = today.toString()
                            )
                        )
                    }
                }

                SpecificVegetable.EGGPLANT -> {
                    if (stage == CropGrowthStage.VEGETATIVE || stage == CropGrowthStage.FLOWERING || stage == CropGrowthStage.RIPENING) {
                        decisions.add(
                            DssDecision(
                                id = "dss_task_efsb_scout_${plot.id}",
                                farmId = farmId, plotId = plot.id, plotLabel = plot.plotLabel, cropName = crop.name,
                                decisionType = DssDecisionType.TASK, category = DssCategory.PEST_DISEASE, priority = DssPriority.HIGH,
                                title = "Scout for Shoot & Fruit Borer (EFSB) on ${plot.plotLabel}",
                                summary = "Inspect terminal shoot tips and baby eggplants. Prune and burn wilted shoot tips immediately to kill internal larvae.",
                                explanation = "Eggplant Fruit and Shoot Borer (Leucinodes orbonalis) bores into tender shoots causing wilting, then enters fruit. Clipping and destroying wilted tips breaks their reproductive cycle without synthetic pesticides.",
                                source = "UPLB-IPB & DA-BPI Eggplant IPM Protocol",
                                actionText = "Suriin ang Dulo ng Sanga", actionTaskType = TaskType.OBSERVATION, evaluatedAt = today.toString()
                            )
                        )
                    }
                }

                SpecificVegetable.BITTER_GOURD -> {
                    if (stage == CropGrowthStage.FLOWERING || stage == CropGrowthStage.RIPENING || daysElapsed in 35..60) {
                        decisions.add(
                            DssDecision(
                                id = "dss_task_bagging_ampalaya_${plot.id}",
                                farmId = farmId, plotId = plot.id, plotLabel = plot.plotLabel, cropName = crop.name,
                                decisionType = DssDecisionType.TASK, category = DssCategory.PEST_DISEASE, priority = DssPriority.HIGH,
                                title = "Fruit Bagging (Balot-Bunga) against Melon Fruit Fly",
                                summary = "Wrap newly pollinated young fruits (5–7cm) with old newspaper or punctured plastic sleeve as soon as petals drop.",
                                explanation = "Melon fruit fly (Bactrocera cucurbitae) oviposits beneath young fruit rind, causing curling, rotting, and maggots. Physical bagging provides 100% organic protection without chemicals.",
                                source = "DA-BAR & BPI Organic Bitter Gourd Production Manual",
                                actionText = "Magbalot ng Bunga", actionTaskType = TaskType.OBSERVATION, evaluatedAt = today.toString()
                            )
                        )
                    }
                }

                SpecificVegetable.PECHAY, SpecificVegetable.CABBAGE -> {
                    decisions.add(
                        DssDecision(
                            id = "dss_task_dbm_scout_${plot.id}",
                            farmId = farmId, plotId = plot.id, plotLabel = plot.plotLabel, cropName = crop.name,
                            decisionType = DssDecisionType.TASK, category = DssCategory.PEST_DISEASE, priority = DssPriority.HIGH,
                            title = "Inspect Underside Leaves for Diamondback Moth (DBM)",
                            summary = "Flip lower leaves and check for tiny green caterpillars and window-pane feeding holes. Spray botanical neem or chili-soap in the late afternoon.",
                            explanation = "Diamondback moth (Plutella xylostella) multiplies exponentially on brassicas. Catching early instar larvae prevents skeletonized leaves.",
                            source = "DA-BPI National Organic Brassica Crop Protection Standard",
                            actionText = "Suriin ang Ilalim ng Dahon", actionTaskType = TaskType.OBSERVATION, evaluatedAt = today.toString()
                        )
                    )
                }

                SpecificVegetable.CARROT -> {
                    if (daysElapsed in 45..75) {
                        decisions.add(
                            DssDecision(
                                id = "dss_task_earthing_carrot_${plot.id}",
                                farmId = farmId, plotId = plot.id, plotLabel = plot.plotLabel, cropName = crop.name,
                                decisionType = DssDecisionType.TASK, category = DssCategory.GROWTH_CARE, priority = DssPriority.MEDIUM,
                                title = "Shoulder Earthing-Up to Prevent Carrot Greening",
                                summary = "Inspect carrot crowns. Pull 2cm of soil over any exposed orange taproot shoulders protruding above the surface.",
                                explanation = "Direct sunlight exposure on exposed carrot root tops stimulates chlorophyll and solarine synthesis, making the crown green, fibrous, and bitter.",
                                source = "DA-BAR Commercial Root Crop Quality Preservation Bulletin",
                                actionText = "Tabunan ang Balikat ng Karot", actionTaskType = TaskType.SOIL_AMENDMENT, evaluatedAt = today.toString()
                            )
                        )
                    }
                }

                SpecificVegetable.POTATO -> {
                    if (daysElapsed in 35..65) {
                        decisions.add(
                            DssDecision(
                                id = "dss_task_tuber_moth_potato_${plot.id}",
                                farmId = farmId, plotId = plot.id, plotLabel = plot.plotLabel, cropName = crop.name,
                                decisionType = DssDecisionType.TASK, category = DssCategory.PEST_DISEASE, priority = DssPriority.HIGH,
                                title = "Check Soil Cracks for Potato Tuber Moth & Late Blight",
                                summary = "Inspect lower leaf canopies for dark water-soaked concentric lesions (late blight) and close soil fissures around tuber hills.",
                                explanation = "Potato tuber moth (Phthorimaea operculella) crawls down soil cracks to lay eggs directly on shallow tubers. Firming soil and inspecting foliage protects harvest value.",
                                source = "DA-BPI Highland & Lowland Potato IPM Bulletin",
                                actionText = "Suriin ang Kama ng Patatas", actionTaskType = TaskType.OBSERVATION, evaluatedAt = today.toString()
                            )
                        )
                    }
                }

                SpecificVegetable.STRING_BEANS -> {
                    if (stage == CropGrowthStage.FLOWERING || stage == CropGrowthStage.RIPENING || daysElapsed in 25..50) {
                        decisions.add(
                            DssDecision(
                                id = "dss_task_podborer_sitaw_${plot.id}",
                                farmId = farmId, plotId = plot.id, plotLabel = plot.plotLabel, cropName = crop.name,
                                decisionType = DssDecisionType.TASK, category = DssCategory.PEST_DISEASE, priority = DssPriority.HIGH,
                                title = "Scout Flower Clusters for Pod Borer & Black Aphids",
                                summary = "Examine flower buds and young pods for entry pinholes and black aphid clusters at growing vine tips. Spray mild neem-soap extract.",
                                explanation = "Maruca vitrata pod borer larvae feed inside blossoms before entering pods. Early botanical sprays at 5:30 PM protect flower integrity.",
                                source = "DA-BPI Legume Integrated Pest Management Manual",
                                actionText = "Suriin ang Bulaklak at Pods", actionTaskType = TaskType.OBSERVATION, evaluatedAt = today.toString()
                            )
                        )
                    }
                }

                SpecificVegetable.CUCUMBER -> {
                    if (daysElapsed in 20..45) {
                        decisions.add(
                            DssDecision(
                                id = "dss_task_downy_cuke_${plot.id}",
                                farmId = farmId, plotId = plot.id, plotLabel = plot.plotLabel, cropName = crop.name,
                                decisionType = DssDecisionType.TASK, category = DssCategory.PEST_DISEASE, priority = DssPriority.MEDIUM,
                                title = "Inspect Leaves for Downy Mildew Angular Spots",
                                summary = "Check upper leaf surfaces for yellow angular lesions bound by leaf veins. Prune bottom 30cm of old leaves to boost air circulation.",
                                explanation = "Pseudoperonospora cubensis spreads rapidly in warm humid microclimates. Bottom pruning and drip-zone watering halt fungal sporulation.",
                                source = "DA-BPI Cucurbit Disease Early Warning Bulletin",
                                actionText = "Suriin ang Dahon ng Pipino", actionTaskType = TaskType.OBSERVATION, evaluatedAt = today.toString()
                            )
                        )
                    }
                }

                SpecificVegetable.SQUASH -> {
                    if (daysElapsed in 35..65) {
                        decisions.add(
                            DssDecision(
                                id = "dss_task_board_squash_${plot.id}",
                                farmId = farmId, plotId = plot.id, plotLabel = plot.plotLabel, cropName = crop.name,
                                decisionType = DssDecisionType.TASK, category = DssCategory.GROWTH_CARE, priority = DssPriority.MEDIUM,
                                title = "Place Protective Board / Straw Mat under Squash Fruit",
                                summary = "Slide a small wooden board, dry tile, or thick rice straw pad under each enlarging squash fruit lying on the ground.",
                                explanation = "Lifting developing kalabasa off damp soil stops belly rot (Rhizoctonia/Fusarium) and insect chewing during monsoons.",
                                source = "DA-BPI Lowland Vegetable Production Handbook",
                                actionText = "Maglagay ng Sapin sa Kalabasa", actionTaskType = TaskType.SOIL_AMENDMENT, evaluatedAt = today.toString()
                            )
                        )
                    }
                }

                SpecificVegetable.BELL_PEPPER -> {
                    if (daysElapsed in 25..55) {
                        decisions.add(
                            DssDecision(
                                id = "dss_task_mites_pepper_${plot.id}",
                                farmId = farmId, plotId = plot.id, plotLabel = plot.plotLabel, cropName = crop.name,
                                decisionType = DssDecisionType.TASK, category = DssCategory.PEST_DISEASE, priority = DssPriority.MEDIUM,
                                title = "Inspect New Shoots for Broad Mites & Thrips Curl",
                                summary = "Check terminal leaf shoots for downward curling, coppery discoloration, or brittle texture. Spray sulfur or botanical soap water.",
                                explanation = "Polyphagotarsonemus latus broad mites inject toxic saliva into young leaf tissues, causing distorted downward leaf curling.",
                                source = "DA-BPI Solanaceous IPM Guidelines",
                                actionText = "Suriin ang Usbong ng Sili", actionTaskType = TaskType.OBSERVATION, evaluatedAt = today.toString()
                            )
                        )
                    }
                }

                SpecificVegetable.GREEN_ONION -> {
                    if (daysElapsed in 15..45) {
                        decisions.add(
                            DssDecision(
                                id = "dss_task_thrips_onion_${plot.id}",
                                farmId = farmId, plotId = plot.id, plotLabel = plot.plotLabel, cropName = crop.name,
                                decisionType = DssDecisionType.TASK, category = DssCategory.PEST_DISEASE, priority = DssPriority.MEDIUM,
                                title = "Check Leaf Sheaths for Thrips & Purple Blotch",
                                summary = "Part inner leaf sheaths and check for minute slender yellowish thrips and small sunken purple-centered leaf spots.",
                                explanation = "Thrips tabaci rasp leaf epidermis causing silvery streaks that allow Alternaria porri (purple blotch) fungus to penetrate.",
                                source = "DA-BAR Allium Crop Protection Compendium",
                                actionText = "Suriin ang Singit ng Sibuyas", actionTaskType = TaskType.OBSERVATION, evaluatedAt = today.toString()
                            )
                        )
                    }
                }

                SpecificVegetable.LETTUCE -> {
                    if (daysElapsed in 10..35) {
                        decisions.add(
                            DssDecision(
                                id = "dss_task_slugs_lettuce_${plot.id}",
                                farmId = farmId, plotId = plot.id, plotLabel = plot.plotLabel, cropName = crop.name,
                                decisionType = DssDecisionType.TASK, category = DssCategory.PEST_DISEASE, priority = DssPriority.MEDIUM,
                                title = "Inspect Bed Borders for Snails, Slugs & Aphids",
                                summary = "Examine lower leaves and bed borders in the early morning for slimy trails, snails, or slugs. Dust wood ash along bed borders.",
                                explanation = "Slugs and snails cause severe defoliation on succulent lettuce overnight. Wood ash creates an alkaline barrier that dehydrates mollusks.",
                                source = "DA-BPI Urban Gardening Pest Defense Manual",
                                actionText = "Suriin ang Snails at Slugs", actionTaskType = TaskType.OBSERVATION, evaluatedAt = today.toString()
                            )
                        )
                    }
                }

                else -> {}
            }
        }

        return decisions
    }
}

typealias DssGrowthCareEvaluator = GrowthCareEvaluator
