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
            "Rogue out and burn infected plants early before spores disperse to neighboring plants; avoid planting downwind of old corn fields."),

        // ── 11. Bitter Gourd (Ampalaya) ──────────────────────────────────────
        pest("Bitter Gourd", "Melon Fruit Fly", "Langaw ng Prutas sa Ampalaya", "Bactrocera cucurbitae",
            "INSECT", "YEAR_ROUND", 3,
            "Punctures on young fruits weeping amber sap; curved, deformed fruits with internal feeding maggots causing premature yellowing and rotting.",
            "Install cue-lure pheromone traps (1 trap per 50 sqm) and protein bait sprays on border corn plants.",
            "Bag young fruits (5–7 cm long) with paper sleeves or mesh bags immediately after female flower closure; bury all fallen infested fruits at least 50 cm deep."),
        pest("Bitter Gourd", "Downy Mildew of Cucurbits", "Amag sa Dahon ng Ampalaya", "Pseudoperonospora cubensis",
            "FUNGAL", "WET", 2,
            "Angular yellow chlorotic lesions bound by leaf veins on upper leaf surface; purplish-gray velvety downy mold underneath in humid mornings.",
            "Spray baking soda solution (5g/L with mild soap) or bio-fungicide Bacillus subtilis upon first sign of angular spots.",
            "Maintain wide 1.8m bamboo trellis spacing; prune lower lateral vines up to 1 meter for maximum aeration; avoid overhead sprinkler watering."),

        // ── 12. Squash (Kalabasa) ────────────────────────────────────────────
        pest("Squash", "Yellow Squash Beetle", "Kuwang ng Kalabasa", "Aulacophora foveicollis",
            "INSECT", "DRY", 1,
            "Bright orange-yellow beetles chewing circular trenches in cotyledons and leaves; subterranean grubs boring into root collar and base.",
            "Dust wood ash mixed with fine neem cake powder around the base of young seedlings.",
            "Handpick beetles into soapy water in early morning when sluggish; cover young seedlings with floating row covers until flowering begins."),
        pest("Squash", "Powdery Mildew of Squash", "Puting Pulbos sa Kalabasa", "Podosphaera xanthii",
            "FUNGAL", "DRY", 2,
            "White talcum-powder-like fungal patches expanding over upper leaf surfaces, petioles, and stems; leaves turn yellow, brown, and brittle.",
            "Foliar spray with diluted cow's milk solution (1 part milk to 9 parts water) or wettable sulfur in early morning under sunlight.",
            "Space hills 2 meters apart; mulch ground with clean rice straw; avoid wetting foliage during irrigation; plant in full sun locations."),

        // ── 13. Carrot (Karot) ───────────────────────────────────────────────
        pest("Carrot", "Alternaria Leaf Blight", "Batik sa Dahon ng Karot", "Alternaria dauci",
            "FUNGAL", "WET", 2,
            "Small dark brown to black irregular spots with yellow halos on leaf margins; older leaves turn brown, wither, and die resembling frost damage.",
            "Foliar spray with copper bordeaux mixture (1%) or fermented equisetum / horsetail extract.",
            "Use certified clean seed; maintain wide row spacing (20–25 cm); water between rows with furrow irrigation rather than overhead sprinklers."),
        pest("Carrot", "Black Cutworm", "Harabas sa Karot", "Agrotis ipsilon",
            "INSECT", "YEAR_ROUND", 1,
            "Plump dark caterpillars hiding in topsoil by day and severing young carrot seedlings at soil level at night.",
            "Apply beneficial entomopathogenic nematodes (Steinernema carpocapsae) or Bt bait around seedlings at dusk.",
            "Clear all crop residue and weeds 2 weeks before sowing; place paper collars (5 cm high) around vulnerable garden patches."),

        // ── 14. Potato (Patatas) ─────────────────────────────────────────────
        pest("Potato", "Potato Tuber Moth", "Gamu-gamo ng Patatas", "Phthorimaea operculella",
            "INSECT", "DRY", 3,
            "Transparent blotch leaf mines on foliage; pinkish-white larvae tunneling into exposed tubers near soil cracks leaving dark frass.",
            "Foliar spray with Bacillus thuringiensis (Bt kurstaki) or neem seed kernel extract in late afternoon.",
            "Perform thorough hilling-up (tambak lupa) keeping tubers buried at least 5 cm deep; close soil cracks with light mulch; store harvested tubers in moth-proof diffuse light."),
        pest("Potato", "Late Blight of Potato", "Late Blight ng Patatas", "Phytophthora infestans",
            "FUNGAL", "WET", 3,
            "Rapidly expanding water-soaked pale green lesions on leaves turning dark brown/purplish with white fungal mold under leaf edges in humid weather; brown rot in tubers.",
            "Preventive spray with Trichoderma harzianum or copper hydroxide bio-fungicide during foggy, wet monsoon periods.",
            "Plant certified disease-free seed tubers (e.g., Igorota variety); mound soil well over tubers; de-haulm (cut and remove stems) 10 days before digging tubers."),

        // ── 15. Green Onion (Sibuyas Dahon) ──────────────────────────────────
        pest("Green Onion", "Onion Thrips", "Tungaw / Thrips sa Sibuyas", "Thrips tabaci",
            "INSECT", "DRY", 1,
            "Tiny slender yellowish-brown insects in leaf sheaths; leaves show silvery patches, white speckles, and tip dieback.",
            "Spray mild potassium soap solution (1 tbsp/L) or neem oil emulsion directly into leaf sheaths at dawn.",
            "Install blue sticky insect monitoring cards; overhead fine sprinkler misting in hot dry mornings suppresses thrips populations; mulch with reflective silver plastic or straw."),
        pest("Green Onion", "Purple Blotch of Alliums", "Lilang Batik sa Sibuyas", "Alternaria porri",
            "FUNGAL", "WET", 2,
            "Small water-soaked sunken lesions on hollow leaves rapidly expanding into oval purplish-brown blotches with dark concentric rings.",
            "Foliar spray with Trichoderma biological suspension or dilute copper fungicide.",
            "Ensure excellent bed drainage with 20 cm raised beds; avoid overhead watering in late afternoon; clear all infected onion debris immediately."),

        // ── 16. Bell Pepper (Siling Atsal) ───────────────────────────────────
        pest("Bell Pepper", "Broad Mite of Pepper", "Tungaw ng Atsal", "Polyphagotarsonemus latus",
            "INSECT", "DRY", 2,
            "Terminal leaves curl tightly downward, becoming rigid, brittle, bronzed, and stunted; flower buds abort and drop.",
            "Spray wettable sulfur (3g/L) or neem oil (5 ml/L with soap) targeting leaf undersides where microscopic mites congregate.",
            "Isolate and discard heavily infested nursery plants; overhead sprinkler misting during hot dry weather discourages mite proliferation."),
        pest("Bell Pepper", "Bacterial Spot of Pepper", "Bacterial Spot ng Sili", "Xanthomonas campestris pv. vesicatoria",
            "BACTERIAL", "WET", 2,
            "Small circular water-soaked dark spots on leaves with pale centers; leaves turn yellow and drop prematurely; scabby raised spots on green peppers.",
            "Preventive spray with copper soap fungicide combined with Bacillus amyloliquefaciens biocontrol.",
            "Use certified hot-water-treated seeds; maintain wide plant spacing (45–50 cm); avoid touching or working among wet pepper foliage."),

        // ── 17. Cabbage (Repolyo) ────────────────────────────────────────────
        pest("Cabbage", "Diamondback Moth (DBM)", "Uod ng Repolyo / DBM", "Plutella xylostella",
            "INSECT", "YEAR_ROUND", 1,
            "Small green caterpillars feeding on leaf undersides leaving transparent 'window pane' patches and ragged holes; silk cocoons attached to leaves.",
            "Release Diadegma semiclausum parasitoid wasps; spray Bacillus thuringiensis (Bt) or neem oil during early evening.",
            "Intercrop with strong-scented alliums, tomato, or coriander; inspect underside of frame leaves every 3 days; destroy old crop residues promptly."),
        pest("Cabbage", "Clubroot Disease", "Pamamaga ng Ugat / Clubroot", "Plasmodiophora brassicae",
            "FUNGAL", "WET", 1,
            "Daytime wilting of leaves despite moist soil; plants severely stunted; roots develop large, spindle-shaped club-like galls and rot.",
            "Soil drench with biological Trichoderma or compost tea; maintain high organic matter.",
            "Incorporate agricultural lime (calcium carbonate) to raise soil pH strictly to 7.0–7.2; ensure excellent drainage; practice minimum 4-year non-brassica crop rotation.")
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
