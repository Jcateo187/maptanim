package com.maptanim.app.dss.knowledgebase

/**
 * A single growing tip with icon, title, and description.
 * Displayed in the Monitoring Dashboard → Growing Tips panel.
 */
data class GrowingTip(
    val icon: String,
    val title: String,
    val description: String
)

/**
 * Deterministic knowledge-base provider that returns stage-specific growing tips
 * for each of the 15 Philippine vegetable crops.
 *
 * Data sourced from DA-BPI Philippine National Standards and published
 * commercial vegetable production guides.
 *
 * Tips are matched by:
 *  - Crop name (case-insensitive)
 *  - Current growth stage index (0–4)
 */
object GrowingTipsProvider {

    /**
     * Returns stage-specific growing tips for the given crop at the given stage.
     *
     * @param cropName  The crop common name (e.g. "Tomato", "Carrot")
     * @param stageIndex  Growth stage index: 0=Sprout, 1=Seedling, 2=Vegetative, 3=Flowering, 4=Harvest Ready
     */
    fun getTips(cropName: String, stageIndex: Int): List<GrowingTip> {
        val key = cropName.lowercase().trim().replace(" ", "")
        val cropTips = cropTipsMap[key] ?: defaultTips
        return cropTips.getOrElse(stageIndex.coerceIn(0, 4)) { cropTips[0] ?: defaultStageTips }
    }

    /**
     * Returns general crop care info (soil, pH, NPK, sunlight) independent of stage.
     */
    fun getGeneralInfo(
        cropName: String,
        soilScore: Float?,
        nRatio: Float,
        pRatio: Float,
        kRatio: Float,
        optimalPhMin: Float,
        optimalPhMax: Float
    ): List<GrowingTip> {
        val tips = mutableListOf<GrowingTip>()

        // Soil suitability
        val soilLabel = when {
            soilScore == null -> "Unknown"
            soilScore >= 1.0f -> "Optimal Match ✅"
            soilScore >= 0.75f -> "Suitable ✅"
            soilScore >= 0.50f -> "Marginal — Use With Caution ⚠️"
            else -> "Poor Match — Not Recommended ❌"
        }
        tips.add(GrowingTip("🌍", "Soil Suitability", "Current soil-crop match: $soilLabel (${((soilScore ?: 0f) * 100).toInt()}%)"))

        // NPK
        tips.add(GrowingTip("🧪", "NPK Requirements", "Nutrient ratio — N:$nRatio, P:$pRatio, K:$kRatio. Apply balanced fertilizer matching this ratio for optimal growth."))

        // pH
        tips.add(GrowingTip("📊", "Optimal Soil pH", "Maintain soil pH between $optimalPhMin and $optimalPhMax. Test soil periodically. Lime (raise pH) or sulfur (lower pH) as needed."))

        // Sunlight
        tips.add(GrowingTip("☀️", "Sunlight", "Most Philippine vegetables require 6–8 hours of direct sunlight daily. Ensure adequate light exposure."))

        return tips
    }

    // ── Per-Crop, Per-Stage Tips ──────────────────────────────────────────

    private val defaultStageTips = listOf(
        GrowingTip("💧", "Maintain Moisture", "Keep soil consistently moist but not waterlogged."),
        GrowingTip("🌿", "Monitor Growth", "Check plants regularly for signs of stress or pest damage."),
        GrowingTip("🔎", "Inspect Weekly", "Scout for pests and diseases at least once per week.")
    )

    private val defaultTips = mapOf(
        0 to defaultStageTips,
        1 to defaultStageTips,
        2 to defaultStageTips,
        3 to defaultStageTips,
        4 to defaultStageTips
    )

    private val cropTipsMap: Map<String, Map<Int, List<GrowingTip>>> = mapOf(
        // ── TOMATO ────────────────────────────────────────────────────────
        "tomato" to mapOf(
            0 to listOf(
                GrowingTip("🌱", "Seed Depth", "Sow seeds 0.5–1 cm deep in moist seedbed or tray. Cover lightly with fine soil."),
                GrowingTip("💧", "Germination Moisture", "Keep seedbed uniformly moist. Mist daily. Avoid waterlogging which causes damping-off."),
                GrowingTip("🌡️", "Temperature", "Optimal germination at 25–30°C. Seedlings emerge in 5–7 days.")
            ),
            1 to listOf(
                GrowingTip("✂️", "Thinning", "Thin seedlings to strongest plant per cell/spot when 2 true leaves appear."),
                GrowingTip("🌿", "Early Fertilizer", "Apply diluted organic liquid fertilizer (fish emulsion) at 14 days after sowing."),
                GrowingTip("🔄", "Hardening Off", "Gradually expose seedlings to full sun 3–5 days before transplanting.")
            ),
            2 to listOf(
                GrowingTip("📏", "Spacing", "Transplant at 50×40 cm spacing. Wider spacing improves air circulation and reduces disease."),
                GrowingTip("🪵", "Staking", "Install bamboo stakes or trellis at transplanting. Tie stems loosely with soft twine."),
                GrowingTip("🌿", "Side-Dress Fertilizer", "Apply 14-14-14 or compost side-dress 2 weeks after transplanting. Repeat every 14 days."),
                GrowingTip("🪴", "Weed Control", "Keep planting area weed-free. Mulch with rice straw to suppress weeds and retain moisture.")
            ),
            3 to listOf(
                GrowingTip("💧", "Consistent Watering", "Maintain even soil moisture during flowering. Irregular watering causes blossom-end rot."),
                GrowingTip("🐝", "Pollination", "Gently tap flower clusters to aid pollination on calm days. Avoid pesticide sprays during blooming."),
                GrowingTip("🧪", "Calcium", "Apply calcium foliar spray if blossom-end rot appears. Ensure soil pH is 6.0–6.8."),
                GrowingTip("✂️", "Pruning", "Remove suckers below the first flower cluster for determinate varieties to direct energy to fruit set.")
            ),
            4 to listOf(
                GrowingTip("🌾", "Harvest Indicators", "Harvest when fruits are fully colored (red/orange), firm to touch, and easily detach from stem."),
                GrowingTip("🕐", "Harvest Timing", "Harvest early morning when fruits are cool. Avoid harvesting wet fruits to prevent rot."),
                GrowingTip("📦", "Post-Harvest", "Sort by size and quality. Store at 12–15°C for up to 7 days. Do not refrigerate below 10°C.")
            )
        ),

        "kamatis" to mapOf(/* alias for tomato — handled by key normalization */),

        // ── EGGPLANT ──────────────────────────────────────────────────────
        "eggplant" to mapOf(
            0 to listOf(
                GrowingTip("🌱", "Seed Depth", "Sow 0.5 cm deep in seedling tray. Germination in 7–10 days at 25–30°C."),
                GrowingTip("💧", "Moisture", "Mist seedbed twice daily. Avoid direct heavy watering which displaces tiny seeds.")
            ),
            1 to listOf(
                GrowingTip("✂️", "Thinning", "Keep strongest seedling per cell at 2-leaf stage."),
                GrowingTip("🌿", "Fertilizer", "Apply diluted complete fertilizer (1/4 strength) once true leaves emerge.")
            ),
            2 to listOf(
                GrowingTip("📏", "Spacing", "Transplant at 60×50 cm. Eggplant is a large shrub that needs room."),
                GrowingTip("🌿", "Side-Dress", "Apply ammonium sulfate (21-0-0) or complete fertilizer 14 DAT. Repeat every 2 weeks."),
                GrowingTip("🪴", "Weed & Mulch", "Mulch with rice hull or dried leaves to conserve moisture.")
            ),
            3 to listOf(
                GrowingTip("💧", "Watering", "Water deeply every 2–3 days. Drought stress during flowering causes flower drop."),
                GrowingTip("✂️", "Pruning", "Remove lower old leaves and damaged branches to improve air circulation."),
                GrowingTip("🐛", "Pest Watch", "Scout for fruit and shoot borer. Apply Bt spray or neem oil if detected early.")
            ),
            4 to listOf(
                GrowingTip("🌾", "Harvest Indicators", "Harvest when skin is glossy and firm. Dull skin indicates over-maturity."),
                GrowingTip("✂️", "Harvest Method", "Cut fruit with 2 cm stem attached using sharp knife. Do not twist or pull."),
                GrowingTip("📦", "Post-Harvest", "Handle gently — eggplant bruises easily. Store at 10–12°C, use within 5 days.")
            )
        ),

        "talong" to mapOf(),

        // ── CARROT ────────────────────────────────────────────────────────
        "carrot" to mapOf(
            0 to listOf(
                GrowingTip("🌱", "Direct Sow", "Sow seeds 1 cm deep directly in prepared bed. Do not transplant — carrots are direct-seeded only."),
                GrowingTip("💧", "Moisture", "Keep bed surface moist until germination (10–14 days). Use fine mist spray."),
                GrowingTip("🌍", "Soil Prep", "Remove all rocks and clods from top 30 cm. Carrots fork in stony soil.")
            ),
            1 to listOf(
                GrowingTip("✂️", "Thinning", "Thin seedlings to 3–5 cm apart when 5 cm tall. Overcrowding produces thin roots."),
                GrowingTip("🪴", "Weed Control", "Hand-weed carefully. Carrot seedlings are slow growers easily outcompeted by weeds.")
            ),
            2 to listOf(
                GrowingTip("📏", "Row Spacing", "Maintain 15–20 cm between rows. Hill soil around exposed root shoulders to prevent greening."),
                GrowingTip("🌿", "Fertilizer", "Apply potassium-rich fertilizer (0-0-60 MOP). Excess nitrogen causes hairy, forked roots."),
                GrowingTip("💧", "Irrigation", "Water deeply and evenly every 2–3 days. Uneven moisture causes cracking.")
            ),
            3 to listOf(
                GrowingTip("💧", "Moisture", "Continue consistent deep watering. Root swelling occurs rapidly during this stage."),
                GrowingTip("🔎", "Inspect", "Check for carrot fly damage (wilting foliage). Use row covers if detected.")
            ),
            4 to listOf(
                GrowingTip("🌾", "Harvest Indicators", "Harvest when root tops are 2–3 cm diameter at soil surface. Foliage begins to yellow."),
                GrowingTip("🥕", "Harvest Method", "Loosen soil alongside row with fork. Pull gently by foliage crown."),
                GrowingTip("📦", "Post-Harvest", "Remove foliage immediately (it draws moisture). Store at 0–4°C in perforated bags.")
            )
        ),

        "karot" to mapOf(),

        // ── STRING BEANS / SITAW ──────────────────────────────────────────
        "stringbeans" to mapOf(
            0 to listOf(
                GrowingTip("🌱", "Seed Depth", "Sow 2–3 cm deep directly in prepared mound or furrow. Space seeds 10 cm apart."),
                GrowingTip("💧", "Moisture", "Water immediately after sowing. Keep soil moist but not saturated."),
                GrowingTip("🌡️", "Temperature", "Germinates in 5–7 days at 25–30°C. Avoid sowing during heavy rains.")
            ),
            1 to listOf(
                GrowingTip("🪵", "Early Support", "Install bamboo trellis or string support at 10 DAP. Sitaw is a vigorous climber."),
                GrowingTip("🌿", "Fertilizer", "Apply basal complete fertilizer (14-14-14) at planting hole.")
            ),
            2 to listOf(
                GrowingTip("🪵", "Trellis Training", "Guide vines onto trellis. Tie loosely with soft twine every 30 cm of growth."),
                GrowingTip("🌿", "Side-Dress", "Apply urea (46-0-0) side-dress 21 DAP. Repeat at first flower appearance."),
                GrowingTip("💧", "Watering", "Water deeply every 2 days. Drought during vine growth reduces pod yield.")
            ),
            3 to listOf(
                GrowingTip("💧", "Consistent Water", "Maintain regular irrigation during podding. Pods lengthen rapidly this stage."),
                GrowingTip("🐛", "Pest Watch", "Scout for bean fly (stem borer) and pod borer. Remove affected pods."),
                GrowingTip("✂️", "Harvesting Start", "Begin harvesting young pods as they reach 25–30 cm. Regular picking promotes more flowering.")
            ),
            4 to listOf(
                GrowingTip("🌾", "Harvest Indicators", "Harvest pods when tender, crisp, and seeds are not bulging. Snap test — should break cleanly."),
                GrowingTip("🕐", "Harvest Frequency", "Pick every 2–3 days. Leaving mature pods on vine signals plant to stop producing."),
                GrowingTip("📦", "Post-Harvest", "Bundle loosely. Store at 7–10°C. Use within 3 days for best crispness.")
            )
        ),

        "sitaw" to mapOf(),
        "beans" to mapOf(),

        // ── ONION ─────────────────────────────────────────────────────────
        "onion" to mapOf(
            0 to listOf(
                GrowingTip("🌱", "Seed Depth", "Sow seeds 1 cm deep in seedbed tray. Cover with fine compost."),
                GrowingTip("💧", "Moisture", "Keep seedbed moist. Germination takes 7–12 days at 20–25°C.")
            ),
            1 to listOf(
                GrowingTip("✂️", "Thinning", "Thin to strongest seedling per cell. Trim leaf tips to 10 cm to strengthen stem."),
                GrowingTip("🔄", "Transplant Timing", "Transplant when seedlings are pencil-thick (35–45 DAP in seedbed).")
            ),
            2 to listOf(
                GrowingTip("📏", "Spacing", "Transplant at 10×15 cm spacing in raised beds. Shallow planting — base of bulb at soil level."),
                GrowingTip("🌿", "Fertilizer", "Apply ammonium sulfate 14 DAT. Stop nitrogen application 30 days before expected harvest."),
                GrowingTip("💧", "Watering", "Water every 3–5 days. Excess moisture promotes fungal disease. Allow soil surface to dry between waterings.")
            ),
            3 to listOf(
                GrowingTip("💧", "Reduce Water", "Begin reducing irrigation as bulbs mature. Over-watering delays curing."),
                GrowingTip("🔎", "Disease Watch", "Scout for purple blotch and downy mildew. Apply copper-based fungicide preventively.")
            ),
            4 to listOf(
                GrowingTip("🌾", "Harvest Indicators", "Harvest when 50–75% of tops have naturally fallen over. Bulb neck should be thin and dry."),
                GrowingTip("☀️", "Field Curing", "Cure in the field for 3–5 days under sun. Then cure in shaded area for 2 weeks."),
                GrowingTip("📦", "Storage", "Store cured bulbs in well-ventilated area. Properly cured onions store 2–3 months.")
            )
        ),

        "sibuyas" to mapOf(),

        // ── SQUASH / PUMPKIN / KALABASA ───────────────────────────────────
        "squash" to mapOf(
            0 to listOf(
                GrowingTip("🌱", "Seed Depth", "Sow 2–3 cm deep in mounds. Place 2 seeds per mound, thin to 1 after germination."),
                GrowingTip("💧", "Moisture", "Water mound thoroughly after sowing. Germination in 5–7 days.")
            ),
            1 to listOf(
                GrowingTip("🌿", "Early Feed", "Apply complete fertilizer ring around seedling at 14 DAP."),
                GrowingTip("🪴", "Weed Control", "Keep 1-meter radius around plant weed-free. Squash vines need room to spread.")
            ),
            2 to listOf(
                GrowingTip("📏", "Vine Management", "Train vines in one direction. Spacing: 2×2 m between mounds."),
                GrowingTip("🌿", "Side-Dress", "Apply urea 21 DAP. Repeat when first female flowers appear."),
                GrowingTip("💧", "Deep Watering", "Water deeply at base. Avoid wetting leaves — prevents powdery mildew.")
            ),
            3 to listOf(
                GrowingTip("🐝", "Pollination", "Hand-pollinate if bee activity is low. Transfer pollen from male flower to female flower stigma."),
                GrowingTip("🐛", "Pest Watch", "Scout for squash vine borer and powdery mildew. Apply sulfur-based fungicide if needed.")
            ),
            4 to listOf(
                GrowingTip("🌾", "Harvest Indicators", "Harvest when rind is hard, stem is dry and corky, and skin color is deep orange."),
                GrowingTip("✂️", "Harvest Method", "Cut stem 5 cm from fruit. Do not carry by stem."),
                GrowingTip("📦", "Storage", "Cure at 27–30°C for 10 days. Store at 12–15°C. Squash stores 2–6 months when properly cured.")
            )
        ),

        "pumpkin" to mapOf(),
        "kalabasa" to mapOf(),

        // ── CORN ──────────────────────────────────────────────────────────
        "corn" to mapOf(
            0 to listOf(
                GrowingTip("🌱", "Seed Depth", "Sow 3–5 cm deep. Space 25 cm within rows, 75 cm between rows."),
                GrowingTip("💧", "Moisture", "Water immediately after planting. Corn needs consistent moisture for germination (5–7 days).")
            ),
            1 to listOf(
                GrowingTip("🌿", "Basal Fertilizer", "Apply 14-14-14 complete fertilizer in furrow at planting."),
                GrowingTip("🪴", "Weed Control", "Cultivate between rows at 14 DAP to remove weeds before canopy closure.")
            ),
            2 to listOf(
                GrowingTip("🌿", "Side-Dress", "Apply urea (46-0-0) side-dress at knee-high stage (21–28 DAP). Hill soil around base."),
                GrowingTip("💧", "Critical Watering", "Corn is most sensitive to drought during tasseling. Ensure adequate irrigation."),
                GrowingTip("🐛", "Armyworm Watch", "Check whorls for fall armyworm. Apply Bt or sand+neem powder into whorls if detected.")
            ),
            3 to listOf(
                GrowingTip("💧", "Silking Moisture", "Maintain irrigation during silking and grain fill. Stress now drastically reduces yield."),
                GrowingTip("🔎", "Ear Check", "Inspect developing ears for corn earworm. Apply mineral oil to silk tips.")
            ),
            4 to listOf(
                GrowingTip("🌾", "Sweet Corn Harvest", "For sweet corn: harvest when kernels are plump, milky when pressed, silk is brown and dry."),
                GrowingTip("🕐", "Timing", "Harvest sweet corn early morning for maximum sugar content. Cook/sell within hours."),
                GrowingTip("📦", "Field Corn", "For field corn: harvest when husks are fully dry and kernels are hard. Dry to 14% moisture.")
            )
        ),

        "mais" to mapOf(),

        // ── CABBAGE ───────────────────────────────────────────────────────
        "cabbage" to mapOf(
            0 to listOf(
                GrowingTip("🌱", "Seed Depth", "Sow 0.5 cm deep in seedbed tray. Germination in 5–7 days at 20–25°C."),
                GrowingTip("💧", "Moisture", "Mist seedbed morning and afternoon. Avoid waterlogging.")
            ),
            1 to listOf(
                GrowingTip("✂️", "Thinning", "Thin to one seedling per cell at 2-leaf stage."),
                GrowingTip("🔄", "Hardening", "Harden seedlings in partial sun for 5 days before transplanting.")
            ),
            2 to listOf(
                GrowingTip("📏", "Spacing", "Transplant at 40×50 cm spacing. Firm soil around base."),
                GrowingTip("🌿", "Heavy Feeding", "Cabbage is a heavy feeder. Apply ammonium sulfate 14 and 28 DAT. Add compost mulch."),
                GrowingTip("💧", "Regular Watering", "Water deeply every 2–3 days. Consistent moisture produces solid, crack-free heads.")
            ),
            3 to listOf(
                GrowingTip("🐛", "Diamondback Moth", "Scout for small holes in wrapper leaves. Apply Bt spray every 5–7 days during head formation."),
                GrowingTip("🪴", "Weeding", "Hand-weed carefully. Avoid disturbing shallow root system.")
            ),
            4 to listOf(
                GrowingTip("🌾", "Harvest Indicators", "Harvest when head is firm and compact when pressed. Over-mature heads crack open."),
                GrowingTip("✂️", "Harvest Method", "Cut head at base with sharp knife leaving 2–3 wrapper leaves for protection."),
                GrowingTip("📦", "Post-Harvest", "Store at 0–2°C, 95% humidity. Properly stored cabbage keeps 3–5 months.")
            )
        ),

        "repolyo" to mapOf(),

        // ── PECHAY ────────────────────────────────────────────────────────
        "pechay" to mapOf(
            0 to listOf(
                GrowingTip("🌱", "Seed Depth", "Sow 0.5 cm deep. Can direct-seed or use seedbed tray."),
                GrowingTip("💧", "Moisture", "Keep constantly moist. Pechay germinates fast (3–5 days).")
            ),
            1 to listOf(
                GrowingTip("✂️", "Thinning", "Thin seedlings to 10 cm apart. Thinnings are edible — use in salads."),
                GrowingTip("🌿", "Fertilizer", "Apply urea (1 tbsp/gallon water) drench at 7 DAP.")
            ),
            2 to listOf(
                GrowingTip("💧", "Daily Watering", "Water daily in dry season. Pechay wilts quickly under drought."),
                GrowingTip("🌿", "Side-Dress", "Apply ammonium sulfate 14 DAP for rapid leaf growth."),
                GrowingTip("🐛", "Pest Watch", "Check for flea beetles (shot holes in leaves) and leafminer trails.")
            ),
            3 to listOf(
                GrowingTip("💧", "Moisture", "Continue daily watering. Leaves should be turgid and crisp."),
                GrowingTip("🔎", "Quality Check", "Remove any yellowed or damaged outer leaves.")
            ),
            4 to listOf(
                GrowingTip("🌾", "Harvest", "Harvest entire plant at 25–30 DAP. Cut at base leaving roots in soil."),
                GrowingTip("🕐", "Timing", "Harvest early morning. Pechay wilts rapidly in afternoon heat."),
                GrowingTip("📦", "Post-Harvest", "Dip in cold water immediately after harvest. Bundle and sell same day for best quality.")
            )
        ),

        // ── AMPALAYA ──────────────────────────────────────────────────────
        "ampalaya" to mapOf(
            0 to listOf(
                GrowingTip("🌱", "Seed Prep", "Nick seed coat with nail clipper and soak in water 12–24 hours before planting."),
                GrowingTip("🌱", "Seed Depth", "Sow 2 cm deep in mound. Germination in 5–8 days."),
                GrowingTip("💧", "Moisture", "Keep mound moist but well-drained.")
            ),
            1 to listOf(
                GrowingTip("🪵", "Trellis Setup", "Install bamboo A-frame or overhead trellis before vines start climbing."),
                GrowingTip("🌿", "Fertilizer", "Apply complete fertilizer at base 14 DAP.")
            ),
            2 to listOf(
                GrowingTip("🪵", "Vine Training", "Guide main vine to trellis. Allow laterals to spread along overhead wires."),
                GrowingTip("🌿", "Side-Dress", "Apply urea 21 and 35 DAP. Ampalaya is a heavy nitrogen feeder."),
                GrowingTip("💧", "Watering", "Water deeply every 2 days. Mulch base to retain moisture.")
            ),
            3 to listOf(
                GrowingTip("🐝", "Pollination", "Hand-pollinate female flowers (have small fruit behind petals) early morning for best fruit set."),
                GrowingTip("🐛", "Pest Watch", "Scout for fruit fly. Use methyl eugenol traps or bagging technique."),
                GrowingTip("✂️", "Pruning", "Remove excess laterals and old leaves to improve light penetration and air flow.")
            ),
            4 to listOf(
                GrowingTip("🌾", "Harvest Indicators", "Harvest when fruit is mature green, firm, and bumpy texture is prominent. Avoid yellow/ripe fruits."),
                GrowingTip("🕐", "Harvest Frequency", "Pick every 2–3 days. Regular harvesting promotes continuous fruiting up to 3 months."),
                GrowingTip("📦", "Post-Harvest", "Store at 10–12°C. Wrap individually to prevent bruising during transport.")
            )
        ),

        // ── OKRA ──────────────────────────────────────────────────────────
        "okra" to mapOf(
            0 to listOf(
                GrowingTip("🌱", "Seed Prep", "Soak seeds overnight to speed germination. Sow 2 cm deep directly."),
                GrowingTip("💧", "Moisture", "Water well after sowing. Germination in 5–10 days.")
            ),
            1 to listOf(
                GrowingTip("✂️", "Thinning", "Thin to 30 cm apart when seedlings are 10 cm tall."),
                GrowingTip("🌿", "Fertilizer", "Apply complete fertilizer at 14 DAP.")
            ),
            2 to listOf(
                GrowingTip("📏", "Spacing", "Final spacing 30×60 cm. Okra grows tall (1–2 m) — ensure adequate room."),
                GrowingTip("🌿", "Side-Dress", "Apply urea at flower bud initiation for continuous production."),
                GrowingTip("💧", "Watering", "Water every 2–3 days. Drought causes tough, fibrous pods.")
            ),
            3 to listOf(
                GrowingTip("🌾", "Early Harvest Start", "Begin picking pods 4–6 days after flowering. Pods grow rapidly."),
                GrowingTip("🐛", "Pest Watch", "Check for aphids on growing tips and flower buds.")
            ),
            4 to listOf(
                GrowingTip("🌾", "Harvest Indicators", "Harvest pods at 7–10 cm length while still tender. Over-mature pods are woody and inedible."),
                GrowingTip("🕐", "Daily Picking", "Pick every 1–2 days during peak production. Wear gloves — okra has irritating hairs."),
                GrowingTip("📦", "Post-Harvest", "Use within 2 days. Store at 7–10°C. Do not wash before storage.")
            )
        ),

        // ── CHILI / SILI ──────────────────────────────────────────────────
        "sili" to mapOf(
            0 to listOf(
                GrowingTip("🌱", "Seed Depth", "Sow 0.5 cm deep in seedbed tray. Germination 10–14 days at 25–30°C."),
                GrowingTip("💧", "Moisture", "Keep seedbed moist. Chili seeds are slow to germinate.")
            ),
            1 to listOf(
                GrowingTip("✂️", "Thinning", "Keep strongest seedling per cell at 3-leaf stage."),
                GrowingTip("🔄", "Transplant", "Transplant at 4–6 true leaf stage. Harden for 5 days before field transplanting.")
            ),
            2 to listOf(
                GrowingTip("📏", "Spacing", "40×50 cm for siling haba, 30×40 cm for siling labuyo."),
                GrowingTip("🌿", "Fertilizer", "Apply complete fertilizer at transplanting and side-dress urea 21 DAT."),
                GrowingTip("🪵", "Support", "Stake tall varieties (siling haba) to prevent lodging under fruit weight.")
            ),
            3 to listOf(
                GrowingTip("💧", "Watering", "Water every 2–3 days. Excess water during fruiting causes fruit rot."),
                GrowingTip("🐛", "Anthracnose Watch", "Scout for dark sunken lesions on fruits. Remove and destroy affected fruits immediately.")
            ),
            4 to listOf(
                GrowingTip("🌾", "Harvest Indicators", "Siling haba: harvest green when firm, 10–15 cm. Siling labuyo: harvest red when fully ripe."),
                GrowingTip("🕐", "Harvest Frequency", "Pick every 3–5 days. Regular harvesting extends production to 4+ months."),
                GrowingTip("📦", "Post-Harvest", "Air-dry labuyo for preservation. Fresh sili stores 7 days at 7–10°C.")
            )
        ),

        "chili" to mapOf(),
        "chilipepper" to mapOf(),

        // ── CUCUMBER / PIPINO ─────────────────────────────────────────────
        "cucumber" to mapOf(
            0 to listOf(
                GrowingTip("🌱", "Seed Depth", "Sow 2 cm deep directly in mounds. Place 2–3 seeds per mound."),
                GrowingTip("💧", "Moisture", "Water immediately. Germination in 4–7 days.")
            ),
            1 to listOf(
                GrowingTip("✂️", "Thinning", "Thin to 1 plant per mound at 2-leaf stage."),
                GrowingTip("🪵", "Trellis", "Install trellis at sowing. Trellised cucumbers produce straighter, cleaner fruits.")
            ),
            2 to listOf(
                GrowingTip("📏", "Spacing", "Mounds at 60×100 cm spacing."),
                GrowingTip("🌿", "Fertilizer", "Side-dress urea at flower initiation. Apply potassium-rich fertilizer for fruit quality."),
                GrowingTip("💧", "Watering", "Water every 1–2 days. Cucumbers are 95% water — drought causes bitter, misshapen fruits.")
            ),
            3 to listOf(
                GrowingTip("💧", "Consistent Water", "Maintain even soil moisture during fruiting. Stress causes bitterness."),
                GrowingTip("🐛", "Pest Watch", "Check for downy mildew (angular yellow spots under leaves). Improve trellis air flow.")
            ),
            4 to listOf(
                GrowingTip("🌾", "Harvest Indicators", "Harvest when fruits are 15–20 cm, firm, dark green, and seeds are small."),
                GrowingTip("🕐", "Harvest Frequency", "Pick every 1–2 days. Missed fruits turn yellow and stop vine production."),
                GrowingTip("📦", "Post-Harvest", "Store at 10–12°C. Wrap in plastic to prevent moisture loss. Use within 5 days.")
            )
        ),

        "pipino" to mapOf(),

        // ── KANGKONG ──────────────────────────────────────────────────────
        "kangkong" to mapOf(
            0 to listOf(
                GrowingTip("🌱", "Seed Prep", "Soak seeds overnight. Sow 1–2 cm deep directly in rows or broadcast in raised beds."),
                GrowingTip("💧", "Moisture", "Keep continuously wet. Kangkong thrives in moist/waterlogged conditions.")
            ),
            1 to listOf(
                GrowingTip("✂️", "Thinning", "Thin to 10 cm apart when seedlings are 10 cm tall."),
                GrowingTip("🌿", "Fertilizer", "Apply urea (1 tbsp/gallon water) drench at 7 DAP.")
            ),
            2 to listOf(
                GrowingTip("💧", "Heavy Watering", "Water daily or twice daily. Kangkong can even grow in shallow standing water."),
                GrowingTip("🌿", "Nitrogen Feed", "Apply urea side-dress every 10 days for rapid lush growth."),
                GrowingTip("🐛", "Pest Watch", "Check for leafminer trails and aphids on young shoot tips.")
            ),
            3 to listOf(
                GrowingTip("💧", "Moisture", "Continue heavy irrigation. Kangkong does not have a distinct flowering stage for harvest."),
                GrowingTip("✂️", "Cut-and-Come-Again", "First harvest at 25–30 DAP. Cut 5 cm above soil to allow regrowth.")
            ),
            4 to listOf(
                GrowingTip("🌾", "Harvest Method", "Cut shoots at 20–25 cm length. Leave 2–3 nodes for regrowth."),
                GrowingTip("🔄", "Ratoon Harvests", "Kangkong regrows for 3–4 successive harvests. Apply urea after each cut."),
                GrowingTip("📦", "Post-Harvest", "Bundle and submerge stem ends in water. Sell same day — kangkong wilts within hours.")
            )
        ),

        // ── LETTUCE ───────────────────────────────────────────────────────
        "lettuce" to mapOf(
            0 to listOf(
                GrowingTip("🌱", "Seed Depth", "Sow on surface or barely cover (0.3 cm). Lettuce seeds need light to germinate."),
                GrowingTip("💧", "Moisture", "Mist frequently. Germination in 3–7 days at 18–22°C."),
                GrowingTip("🌡️", "Heat Sensitivity", "Lettuce germinates poorly above 30°C. Start in partial shade during hot months.")
            ),
            1 to listOf(
                GrowingTip("✂️", "Thinning", "Thin to 15–20 cm apart. Thinnings make excellent baby salad greens."),
                GrowingTip("🌿", "Light Feed", "Apply diluted liquid organic fertilizer weekly.")
            ),
            2 to listOf(
                GrowingTip("💧", "Consistent Moisture", "Water every 1–2 days. Drought causes bitter leaves and premature bolting."),
                GrowingTip("🌿", "Nitrogen", "Apply ammonium sulfate for rapid leaf expansion. Avoid excess — causes tipburn."),
                GrowingTip("☀️", "Shade Management", "Provide 30% shade during hottest months (March–May) to delay bolting.")
            ),
            3 to listOf(
                GrowingTip("🔎", "Bolt Watch", "If center stem elongates rapidly, harvest immediately — bolting causes extreme bitterness."),
                GrowingTip("💧", "Cool Watering", "Water early morning with cool water to keep plants crisp.")
            ),
            4 to listOf(
                GrowingTip("🌾", "Harvest Method", "Cut entire head at soil level for heading varieties. Pick outer leaves for loose-leaf."),
                GrowingTip("🕐", "Timing", "Harvest at dawn when leaves are most turgid and sugars are highest."),
                GrowingTip("📦", "Post-Harvest", "Wash in cold water, spin dry, store at 2–5°C in perforated bags. Use within 5–7 days.")
            )
        ),

        "laitus" to mapOf()
    )
}
