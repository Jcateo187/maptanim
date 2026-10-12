package com.maptanim.app.data.local.seeds

import com.maptanim.app.data.local.entity.CropPestDiseaseGuideEntity

object CropPestDiseaseSeeds {

    val list: List<CropPestDiseaseGuideEntity> = listOf(
        // ── 1. Tomato ────────────────────────────────────────────────────────
        pest("Tomato", "Tomato Fruit Borer", "Uod sa Bunga ng Kamatis", "Helicoverpa armigera",
            "INSECT", "DRY", 3,
            "Caterpillars bore holes into developing green and red fruits with dark frass around entrance holes.",
            "Spray Bacillus thuringiensis (Bt) or neem seed kernel extract (30g/L) during early evening.",
            "Handpick larvae at dawn; intercrop with marigold flowers as trap and repellent borders."),
        pest("Tomato", "Bacterial Wilt", "Humpak / Pagkalanta ng Kamatis", "Ralstonia solanacearum",
            "BACTERIAL", "WET", 2,
            "Sudden daytime wilting of green foliage without prior yellowing; stem vascular bundles show white bacterial slime stream when placed in water.",
            "Drench planting holes with Trichoderma harzianum or Bacillus subtilis biological inoculants.",
            "Strict 3-year crop rotation avoiding Solanaceae; use elevated raised beds and avoid wounding roots during weeding."),
        pest("Tomato", "Yellow Leaf Curl Virus", "Kulot ng Kamatis", "TYLCV (Begomovirus)",
            "VIRAL", "DRY", 2,
            "Upward cupping and curling of leaf margins, severe stunting, and bushy chlorotic yellow appearance.",
            "Foliar spray with chili-garlic-soap extract (1 tbsp mild liquid soap + 5 minced sili per liter) to suppress whitefly vectors.",
            "Install yellow sticky insect traps (1 trap per 2 beds) at crop canopy height to monitor and capture whiteflies."),

        // ── 2. Eggplant ──────────────────────────────────────────────────────
        pest("Eggplant", "Fruit and Shoot Borer", "Uod sa Talong (EFSB)", "Leucinodes orbonalis",
            "INSECT", "YEAR_ROUND", 2,
            "Wilting and drooping of growing terminal shoot tips; holes bored into purple fruits with internal sawdust-like larval droppings.",
            "Release Trichogramma chilonis parasitoid wasps or spray Bt biopesticide at dusk.",
            "Clip and bag wilted shoots daily before larvae tunnel into main stem; wrap developing young fruits with protective paper."),
        pest("Eggplant", "Phomopsis Blight / Fruit Rot", "Pangangalawang ng Bunga", "Phomopsis vexans",
            "FUNGAL", "WET", 3,
            "Sunken, circular dark brown spots on fruits that rapidly soften and rot; small dark pycnidia rings on older leaves.",
            "Spray copper-based organic bordeaux mixture (1%) or fermented horsetail/wood vinegar solution.",
            "Avoid overhead irrigation; prune bottom leaves to keep fruits at least 15 cm off moist ground; use clean disease-free seeds."),

        // ── 3. Chili Pepper ──────────────────────────────────────────────────
        pest("Chili Pepper", "Anthracnose Fruit Rot", "Pantal / Bulok sa Sili", "Colletotrichum gloeosporioides",
            "FUNGAL", "WET", 3,
            "Circular, sunken water-soaked lesions on ripe and green pods with concentric rings of dark spores.",
            "Spray bio-fungicide Trichoderma or dilute baking soda solution (1 tsp baking soda + 1/2 tsp vegetable oil per liter).",
            "Harvest pods promptly when mature; remove and burn infected pods; avoid splashing mud onto lower pods."),
        pest("Chili Pepper", "Broad Mites & Thrips", "Tungaw / Kulot ng Dahon ng Sili", "Polyphagotarsonemus latus",
            "INSECT", "DRY", 2,
            "Leaves curl downward, become brittle, bronzed, and inverted canoe-shaped; stunted growing tips.",
            "Spray neem oil (5 ml/L with mild surfactant) or sulfur soap solution directly on undersides of leaves.",
            "Overhead light sprinkler misting in hot mornings (mites dislike high humidity); intercrop with alliums (onions/garlic)."),

        // ── 4. Okra ──────────────────────────────────────────────────────────
        pest("Okra", "Cotton / Melon Aphid", "Kuto ng Halaman sa Okra", "Aphis gossypii",
            "INSECT", "DRY", 1,
            "Clusters of tiny green/black insects on tender young shoot tips and under leaves; sticky honeydew attracting black sooty mold.",
            "Spray strong stream of clean water to knock aphids off; follow with mild potassium insecticidal soap spray.",
            "Encourage ladybird beetles and lacewings; plant sweet basil or mint nearby to repel aphid colonization."),
        pest("Okra", "Yellow Vein Mosaic Virus", "Naninilaw na Ugat ng Dahon", "Okra Enation / YVMV",
            "VIRAL", "YEAR_ROUND", 2,
            "Interwoven network of prominent yellow veins on green leaves; stunted chlorotic pods.",
            "Target whitefly vectors with organic neem oil emulsion every 7 days.",
            "Rogue out and destroy infected plants immediately upon first yellow vein detection; plant certified YVMV-resistant varieties."),

        // ── 5. Pechay ────────────────────────────────────────────────────────
        pest("Pechay", "Diamondback Moth", "Uod sa Pechay (DBM)", "Plutella xylostella",
            "INSECT", "DRY", 2,
            "Tiny green wriggling caterpillars chewing windows and holes in leaves, skeletonizing the foliar canopy.",
            "Foliar spray Bacillus thuringiensis (Bt subsp. kurstaki) late afternoon when larvae feed actively.",
            "Cover raised beds with lightweight fine mesh insect netting (32-mesh) from Day 1 of planting; plant repellent lemongrass."),
        pest("Pechay", "Bacterial Soft Rot", "Bulok na Mabaho sa Pechay", "Pectobacterium carotovorum",
            "BACTERIAL", "WET", 3,
            "Water-soaked mushy lesions at base of leaf petioles that rapidly collapse into foul-smelling slime.",
            "Dust soil base with wood ash or hydrated agricultural lime to elevate surface pH.",
            "Ensure excellent raised bed drainage (25 cm height); do not handle or weed pechay plants when foliage is wet from morning dew."),

        // ── 6. Lettuce ───────────────────────────────────────────────────────
        pest("Lettuce", "Damping-Off / Collar Rot", "Tumbang-Punla ng Letsugas", "Pythium / Rhizoctonia solani",
            "FUNGAL", "WET", 0,
            "Seedlings suddenly collapse at soil line with water-soaked pinched stems shortly after emergence.",
            "Incorporate Trichoderma-enriched compost into seedling germination mix.",
            "Avoid overwatering; ensure seed trays receive bright ventilation and morning sun; sow seeds at shallow depth."),
        pest("Lettuce", "Snails and Slugs", "Kuhol at Suso", "Gastropoda spp.",
            "INSECT", "WET", 1,
            "Large ragged holes chewed in tender leaves with shiny silvery slime trails across beds and leaves.",
            "Place shallow beer traps or yeast-water saucers flush with soil line around garden perimeter.",
            "Surround bed edges with a 5 cm border of crushed eggshells, coarse wood ash, or sharp sand barrier."),

        // ── 7. Kangkong ──────────────────────────────────────────────────────
        pest("Kangkong", "White Rust", "Puting Kalawang ng Kangkong", "Albugo ipomoeae-aquaticae",
            "FUNGAL", "WET", 2,
            "White chalky pustules and blisters on leaf undersides; corresponding upper leaf shows yellow chlorotic spots and leaf curling.",
            "Spray diluted fermented plant juice (FPJ) or copper hydroxide bio-fungicide during early outbreak.",
            "Avoid overhead evening irrigation; maintain wide plant spacing for rapid air drying; destroy heavily infected leaves."),
        pest("Kangkong", "Common Armyworm", "Harabas sa Kangkong", "Spodoptera litura",
            "INSECT", "YEAR_ROUND", 2,
            "Gregarious dark striped caterpillars devouring entire leaf blades overnight, leaving only tough midribs.",
            "Handpick egg masses (covered in brown fuzz) and young caterpillars; apply Bt or nuclear polyhedrosis virus (NPV).",
            "Plow or cultivate soil between crop cycles to expose pupae to sun and predatory birds."),

        // ── 8. Cucumber ──────────────────────────────────────────────────────
        pest("Cucumber", "Downy Mildew", "Pangangalawang ng Pipino", "Pseudoperonospora cubensis",
            "FUNGAL", "WET", 2,
            "Angular yellow patches on upper leaf surface bounded by leaf veins; purplish-gray downy mold on underside.",
            "Spray milk solution (1 part raw fresh milk to 9 parts water) under morning sun or copper-based bio-spray.",
            "Always grow cucumbers on vertical trellises to lift foliage off damp ground; prune lower dense leaves for airflow."),
        pest("Cucumber", "Melon Fruit Fly", "Langaw ng Pipino", "Bactrocera cucurbitae",
            "INSECT", "DRY", 3,
            "Puncture stings on young cucumbers causing resin drop weeping, curved distorted fruits, and internal maggots.",
            "Hang cue-lure pheromone traps with soapy water 15 cm above trellis to capture male flies.",
            "Bag young fruits with perforated plastic bags immediately after female flower petals wither."),

        // ── 9. Yardlong Bean (Sitaw) ─────────────────────────────────────────
        pest("Yardlong Bean", "Legume Pod Borer", "Uod sa Bunga ng Sitaw", "Maruca vitrata",
            "INSECT", "DRY", 3,
            "Larvae web together flower petals and leaves with silk and dark frass, then bore into developing long pods.",
            "Spray neem extract or Bt at first flower bud emergence (5:00 PM application).",
            "Plant corn or sorghum barrier rows to catch moths; handpick and destroy webbed blossom clusters daily."),
        pest("Yardlong Bean", "Black Bean Aphid", "Itim na Kuto sa Sitaw", "Aphis craccivora",
            "INSECT", "DRY", 1,
            "Dense clusters of small black aphids coating growing vine tips, flower stalks, and young bean pods.",
            "Foliar spray with chili-garlic-dishsoap organic wash (repeat every 4 days for 2 cycles).",
            "Conserve beneficial predators like hoverfly larvae and lady beetles; prune out heavily colonized tip clusters."),

        // ── 10. Sweet Corn ───────────────────────────────────────────────────
        pest("Sweet Corn", "Asian Corn Borer", "Uod sa Mais", "Ostrinia furnacalis",
            "INSECT", "YEAR_ROUND", 1,
            "Pinholes and shot-holes in whorl leaves; sawdust-like frass on leaf axils; broken tassels and bore holes in stalks and ears.",
            "Drop 5–10 granules of Bt powder into central leaf whorl or release Trichogramma evanescens cards.",
            "Detassel 3 out of every 4 rows after pollen shed begins; destroy infested crop residue immediately after harvest."),
        pest("Sweet Corn", "Downy Mildew of Corn", "Apo / Naninilaw na Mais", "Peronosclerospora philippinensis",
            "FUNGAL", "WET", 1,
            "Distinct yellow-white chlorotic stripes along leaf length; downy white growth under morning dew; stunted crazy-top tassels.",
            "Seed treatment with biological biocontrol agents (metalaxyl or Trichoderma) before sowing.",
            "Rogue out and burn infected plants early before spores disperse to neighboring plants; avoid planting downwind of old corn fields.")
    )

    private fun pest(
        crop: String, name: String, local: String, scientific: String,
        category: String, season: String, stageIdx: Int,
        symptoms: String, biocontrol: String, prevention: String
    ): CropPestDiseaseGuideEntity = CropPestDiseaseGuideEntity(
        id = "pest_${crop.lowercase().replace(" ", "_")}_${name.lowercase().replace(" ", "_").replace("/", "_")}",
        cropName = crop,
        pestDiseaseName = name,
        localNamePh = local,
        scientificName = scientific,
        category = category,
        riskSeason = season,
        criticalStageIndex = stageIdx,
        symptoms = symptoms,
        organicBiocontrol = biocontrol,
        culturalPrevention = prevention,
        sourceCitation = "Bureau of Plant Industry (DA-BPI) Integrated Pest Management Field Handbook (2023–2024)"
    )
}
