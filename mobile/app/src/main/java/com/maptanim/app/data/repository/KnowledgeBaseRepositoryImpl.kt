package com.maptanim.app.data.repository

import com.maptanim.app.domain.model.PestGuide
import com.maptanim.app.domain.model.SeasonalWindowInfo
import com.maptanim.app.domain.model.SoilGuide
import com.maptanim.app.domain.model.SoilType
import com.maptanim.app.domain.repository.KnowledgeBaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class KnowledgeBaseRepositoryImpl : KnowledgeBaseRepository {

    private val pestsCache = MutableStateFlow(defaultPestGuides)
    private val soilsCache = MutableStateFlow(defaultSoilGuides)
    private val calendarCache = MutableStateFlow(defaultSeasonalWindows)

    override fun observePestGuides(): Flow<List<PestGuide>> = pestsCache.map { it }

    override fun observeSoilGuides(): Flow<List<SoilGuide>> = soilsCache.map { it }

    override fun observeSeasonalWindows(): Flow<List<SeasonalWindowInfo>> = calendarCache.map { it }
}

fun getPestAssetImagePath(pestId: String?, pestName: String?): String {
    val clean = (pestId ?: pestName ?: "").lowercase().replace(" ", "").replace("_", "").replace("-", "")
    val fileName = when {
        clean.contains("fruitborer") || clean.contains("helicoverpa") || clean.contains("earworm") -> "Fruit_borer.png"
        clean.contains("tylcv") || clean.contains("leafcurl") || clean.contains("whitefly") -> "Tomato_leaf_curlvirus.png"
        clean.contains("diamondback") || clean.contains("plutella") -> "Diamondback_moth.png"
        clean.contains("thrip") -> "Onion_thrips.png"
        clean.contains("armyworm") || clean.contains("spodoptera") -> "Fall_armyworm.png"
        clean.contains("powdery") || clean.contains("erysiphe") -> "Powdery_mildew.png"
        clean.contains("aphid") || clean.contains("aphis") -> "Melon_and_cotton_aphids.png"
        clean.contains("leafminer") || clean.contains("liriomyza") -> "Vegetable_leafminer.png"
        clean.contains("fleabeetle") || clean.contains("epitrix") -> "Eggplant_and_brassica_fleabeetle.png"
        clean.contains("bacterialwilt") || clean.contains("ralstonia") -> "Bacterial_wilt.png"
        clean.contains("downymildew") || clean.contains("pseudoperonospora") -> "Cucurbit_downy_mildew.png"
        clean.contains("anthracnose") || clean.contains("colletotrichum") -> "Chilli_anthracnose_fruit_rot.png"
        else -> "Fruit_borer.png"
    }
    return "file:///android_asset/metadata/pest/$fileName"
}

fun getSoilAssetImagePath(soilType: SoilType?, soilTitle: String?): String {
    val clean = (soilType?.name ?: soilTitle ?: "").uppercase()
    val fileName = when {
        clean.contains("LOAM") -> "Loam_soil.png"
        clean.contains("CLAY") -> "Clay_soil.png"
        clean.contains("SAND") -> "Sandy_soil.png"
        clean.contains("SILT") -> "Silty_soil.png"
        clean.contains("PEAT") -> "Peaty_soil.png"
        clean.contains("CHALK") -> "Chalky_soil.png"
        else -> "Loam_soil.png"
    }
    return "file:///android_asset/metadata/soil_images/$fileName"
}

internal val defaultPestGuides = listOf(
    PestGuide(
        id = "pest_fruit_borer",
        name = "Fruit Borer / Corn Earworm",
        localName = "Ubod ng Kamatis / Harabas",
        scientificName = "Helicoverpa armigera",
        affectedCrops = listOf("Tomato", "Eggplant", "Corn", "Okra", "Bell Pepper"),
        category = "Insect Pest",
        organicControl = "Spray Neem Oil extract (30ml/L water) or Bacillus thuringiensis (Bt). Handpick caterpillars early morning.",
        chemicalControl = "Apply DA-approved Chlorantraniliprole or Emamectin benzoate at early instar stage.",
        preventionTips = "Practice crop rotation with non-host crops (e.g., Kangkong/Lettuce). Install yellow sticky traps and pheromone traps.",
        imageUrl = "file:///android_asset/metadata/pest/Fruit_borer.png"
    ),
    PestGuide(
        id = "pest_tylcv",
        name = "Tomato Leaf Curl Virus (TyLCV)",
        localName = "Kulot sa Kamatis / Whitefly Disease",
        scientificName = "Begomovirus (transmitted by Bemisia tabaci)",
        affectedCrops = listOf("Tomato", "Bell Pepper", "Squash"),
        category = "Viral Disease",
        organicControl = "Spray soapy water or Neem oil to target whitefly vector. Remove infected plants immediately and burn.",
        chemicalControl = "Control vector whiteflies with Imidacloprid or Thiamethoxam during seedling stage.",
        preventionTips = "Use TyLCV-resistant varieties (e.g., Diamante Max F1). Install fine insect netting over nursery beds.",
        imageUrl = "file:///android_asset/metadata/pest/Tomato_leaf_curlvirus.png"
    ),
    PestGuide(
        id = "pest_diamondback_moth",
        name = "Diamondback Moth",
        localName = "Ulod sa Repolyo",
        scientificName = "Plutella xylostella",
        affectedCrops = listOf("Cabbage", "Pechay", "Mustard"),
        category = "Insect Pest",
        organicControl = "Apply Bt (Bacillus thuringiensis) kurstaki strain every 5–7 days. Intercrop with onions/garlic as repellent.",
        chemicalControl = "Rotate Spinetoram and Spinetoram-based foliar sprays to prevent pesticide resistance.",
        preventionTips = "Use overhead sprinkler irrigation to disturb egg-laying adult moths. Clear brassica weed residue.",
        imageUrl = "file:///android_asset/metadata/pest/Diamondback_moth.png"
    ),
    PestGuide(
        id = "pest_thrips",
        name = "Onion Thrips",
        localName = "Peste sa Sibuyas / Thrips",
        scientificName = "Thrips tabaci",
        affectedCrops = listOf("Onion", "Garlic", "Cabbage", "Watermelon"),
        category = "Insect Pest",
        organicControl = "Blue sticky card traps (20 traps/ha). Spray bio-pesticide Beauveria bassiana.",
        chemicalControl = "Apply Abamectin or Fipronil in severe infestations during early bulb establishment.",
        preventionTips = "Maintain proper soil moisture (dry soil accelerates thrips breeding). Avoid planting near older onion crops.",
        imageUrl = "file:///android_asset/metadata/pest/Onion_thrips.png"
    ),
    PestGuide(
        id = "pest_fall_armyworm",
        name = "Fall Armyworm",
        localName = "Armyworm / Harabas sa Mais",
        scientificName = "Spodoptera frugiperda",
        affectedCrops = listOf("Corn", "String Beans", "Cucumber"),
        category = "Insect Pest",
        organicControl = "Drop sand/ash mixed with Neem powder into corn whorls. Release Trichogramma parasitic wasps.",
        chemicalControl = "Target whorls with Spinetoram or Methomyl sprays during early egg hatch.",
        preventionTips = "Deep plowing after harvest to destroy pupae in soil. Synchronized planting within farming cluster.",
        imageUrl = "file:///android_asset/metadata/pest/Fall_armyworm.png"
    ),
    PestGuide(
        id = "pest_powdery_mildew",
        name = "Powdery Mildew",
        localName = "Pulbos sa Dahon ng Kalabasa",
        scientificName = "Erysiphe cichoracearum",
        affectedCrops = listOf("Squash", "Cucumber", "Okra", "Ampalaya"),
        category = "Fungal Disease",
        organicControl = "Foliar spray of 10% baking soda solution (1 tsp baking soda + 1L water + 2 drops liquid soap) or diluted milk spray.",
        chemicalControl = "Apply Potassium bicarbonate or Sulfur-based fungicide at first sign of white powdery spots.",
        preventionTips = "Ensure wider plant spacing for air circulation. Avoid overhead watering in late afternoon.",
        imageUrl = "file:///android_asset/metadata/pest/Powdery_mildew.png"
    ),
    PestGuide(
        id = "pest_aphids",
        name = "Melon & Cotton Aphids",
        localName = "Kuto sa Halaman / Aphids",
        scientificName = "Aphis gossypii",
        affectedCrops = listOf("Eggplant", "Chili Pepper", "Watermelon", "Lettuce"),
        category = "Insect Pest",
        organicControl = "Release ladybugs or lacewings. Spray insecticidal soap solution or garlic-chili extract.",
        chemicalControl = "Apply Imidacloprid or Acetamiprid if infestation exceeds threshold.",
        preventionTips = "Reflective plastic mulch deters winged aphids from colonizing young seedlings.",
        imageUrl = "file:///android_asset/metadata/pest/Melon_and_cotton_aphids.png"
    ),
    PestGuide(
        id = "pest_leafminer",
        name = "Vegetable Leafminer",
        localName = "Gurami / Uod sa Dahon",
        scientificName = "Liriomyza sativae",
        affectedCrops = listOf("Pechay", "Tomato", "Watermelon", "Kangkong"),
        category = "Insect Pest",
        organicControl = "Hang yellow sticky traps. Squeeze visible leafminer maggots inside leaf trails manually.",
        chemicalControl = "Foliar spray of Cyromazine or Abamectin targetting early serpentine mines.",
        preventionTips = "Destroy crop residue immediately after harvest; clear alternative weed hosts.",
        imageUrl = "file:///android_asset/metadata/pest/Vegetable_leafminer.png"
    ),
    PestGuide(
        id = "pest_flea_beetle",
        name = "Eggplant & Brassica Flea Beetle",
        localName = "Talon-Talong / Flea Beetle",
        scientificName = "Epitrix cucumeris",
        affectedCrops = listOf("Eggplant", "Pechay", "Radish", "Tomato"),
        category = "Insect Pest",
        organicControl = "Dust leaves with diatomaceous earth or wood ash. Apply Neem oil spray.",
        chemicalControl = "Apply Carbaryl or Lambda-cyhalothrin at first shot-hole leaf damage.",
        preventionTips = "Use row covers during early germination stage until plants establish.",
        imageUrl = "file:///android_asset/metadata/pest/Eggplant_and_brassica_fleabeetle.png"
    ),
    PestGuide(
        id = "pest_bacterial_wilt",
        name = "Bacterial Wilt",
        localName = "Lantang Baktirya / Layong Baktirya",
        scientificName = "Ralstonia solanacearum",
        affectedCrops = listOf("Tomato", "Eggplant", "Chili Pepper", "Ginger"),
        category = "Bacterial Disease",
        organicControl = "Soil solarization; drench soil with Trichoderma harzianum biocontrol agent.",
        chemicalControl = "Soil treatment with Copper hydroxide or Copper oxychloride drenches.",
        preventionTips = "Use wilt-resistant rootstocks; maintain soil pH above 6.5; practice 3-year crop rotation.",
        imageUrl = "file:///android_asset/metadata/pest/Bacterial_wilt.png"
    ),
    PestGuide(
        id = "pest_downy_mildew",
        name = "Cucurbit Downy Mildew",
        localName = "Baking sa Dahon ng Pipino",
        scientificName = "Pseudoperonospora cubensis",
        affectedCrops = listOf("Cucumber", "Ampalaya", "Squash", "Watermelon"),
        category = "Fungal Disease",
        organicControl = "Copper sulfate spray; remove yellow angular leaf spots promptly.",
        chemicalControl = "Apply Mancozeb or Metalaxyl preventive spray before rainy humid spells.",
        preventionTips = "Improve trellis air flow; avoid overhead night irrigation.",
        imageUrl = "file:///android_asset/metadata/pest/Cucurbit_downy_mildew.png"
    ),
    PestGuide(
        id = "pest_anthracnose",
        name = "Chili Anthracnose Fruit Rot",
        localName = "Nangangamatis / Anthracnose",
        scientificName = "Colletotrichum gloeosporioides",
        affectedCrops = listOf("Chili Pepper", "Bell Pepper", "Tomato", "Mango"),
        category = "Fungal Disease",
        organicControl = "Spray compost tea or hot pepper-garlic bio-fungicide; destroy sunken rot pods.",
        chemicalControl = "Foliar spray of Azoxystrobin or Chlorothalonil during flowering/fruit set.",
        preventionTips = "Use pathogen-free seeds; avoid overhead watering; space plants wide.",
        imageUrl = "file:///android_asset/metadata/pest/Chilli_anthracnose_fruit_rot.png"
    )
)

internal val defaultSoilGuides = listOf(
    SoilGuide(
        soilType = SoilType.LOAM,
        title = "Loam Soil",
        localName = "Lupang Luto / Ideal Loam",
        description = "Dark, rich, crumbly soil with a balanced mixture of sand, silt, and clay. Considered the golden standard for Philippine vegetable production.",
        characteristics = "Excellent moisture retention with optimal internal drainage; rich in organic humus and soil microbes.",
        drainageSpeed = "Moderate / Ideal (15–25 mm/hr)",
        phRange = "6.0 – 7.0 (Slightly Acidic to Neutral)",
        texture = "Crumbly and soft when dry, forms a loose ball when moist that easily crumbles.",
        bestCrops = listOf("Tomato", "Eggplant", "Bell Pepper", "Carrot", "Onion", "Lettuce", "Corn", "Squash"),
        imageUrl = "file:///android_asset/metadata/soil_images/Loam_soil.png",
        colorHex = "#3E2723"
    ),
    SoilGuide(
        soilType = SoilType.CLAY,
        title = "Clay Soil",
        localName = "Lupang Malagkit / Pula",
        description = "Fine-textured soil composed of dense clay mineral particles. High nutrient storage capacity but sticky when wet and hard when dry.",
        characteristics = "Holds abundant plant nutrients; high water capacity; slow to drain and warm up in rainy season.",
        drainageSpeed = "Slow (< 5 mm/hr)",
        phRange = "5.5 – 7.0",
        texture = "Sticky and smooth when wet, forms hard clods when dry.",
        bestCrops = listOf("Eggplant", "Okra", "Kangkong", "Squash", "Corn"),
        imageUrl = "file:///android_asset/metadata/soil_images/Clay_soil.png",
        colorHex = "#5D4037"
    ),
    SoilGuide(
        soilType = SoilType.SANDY,
        title = "Sandy Soil",
        localName = "Lupang Buhangin",
        description = "Coarse-grained soil with large quartz particles. Highly porous and fast-draining, ideal for root vegetables when enriched with compost.",
        characteristics = "Warms up rapidly in dry season; excellent root penetration; requires frequent organic compost and split watering.",
        drainageSpeed = "Fast (> 50 mm/hr)",
        phRange = "5.5 – 6.8",
        texture = "Gritty and loose, cannot hold form when squeezed.",
        bestCrops = listOf("Carrot", "Watermelon", "Onion", "Tomato", "Radish"),
        imageUrl = "file:///android_asset/metadata/soil_images/Sandy_soil.png",
        colorHex = "#C6A700"
    ),
    SoilGuide(
        soilType = SoilType.SILTY,
        title = "Silty Soil",
        localName = "Lupang Banlik / River Silt",
        description = "Smooth, fertile soil deposited by alluvial river beds. Highly fertile with fine silt grains that retain moisture efficiently.",
        characteristics = "Very smooth texture; easily tilled; prone to surface crusting after heavy rains if mulching is omitted.",
        drainageSpeed = "Moderate (10–20 mm/hr)",
        phRange = "6.0 – 7.0",
        texture = "Floury and smooth when dry, silky when moist.",
        bestCrops = listOf("Cabbage", "Kangkong", "Lettuce", "String Beans", "Pechay"),
        imageUrl = "file:///android_asset/metadata/soil_images/Silty_soil.png",
        colorHex = "#795548"
    ),
    SoilGuide(
        soilType = SoilType.PEATY,
        title = "Peaty Soil",
        localName = "Lupang Organiko / Peat",
        description = "Dark, spongy soil high in decomposed organic plant materials. Naturally acidic with exceptional water holding capacity.",
        characteristics = "High organic content; rich in nitrogen; requires lime application to raise pH for sensitive crops.",
        drainageSpeed = "Moderate to Slow",
        phRange = "4.5 – 6.0 (Acidic)",
        texture = "Spongy, dark brown or black, lightweight.",
        bestCrops = listOf("Lettuce", "Cabbage", "Kangkong", "Radish"),
        imageUrl = "file:///android_asset/metadata/soil_images/Peaty_soil.png",
        colorHex = "#212121"
    ),
    SoilGuide(
        soilType = SoilType.CHALKY,
        title = "Chalky / Calcareous Soil",
        localName = "Lupang Apog / Alkaline",
        description = "Light soil sitting over limestone bedrock. Naturally alkaline and rich in calcium carbonate but may lock phosphorus.",
        characteristics = "Alkaline pH; fast draining; benefits from regular organic matter incorporation.",
        drainageSpeed = "Fast to Moderate",
        phRange = "7.0 – 8.0 (Alkaline)",
        texture = "Stony or chalky white flecks throughout dry soil.",
        bestCrops = listOf("Okra", "Corn", "String Beans", "Mungbean"),
        imageUrl = "file:///android_asset/metadata/soil_images/Chalky_soil.png",
        colorHex = "#9E9E9E"
    )
)

internal val defaultSeasonalWindows = listOf(
    SeasonalWindowInfo(
        cropName = "Tomato",
        localName = "Kamatis",
        drySeasonStatus = "OPTIMAL",
        wetSeasonStatus = "HIGH_RISK",
        peakMonths = "Nov – Apr",
        notes = "Avoid heavy rain months (Jul-Sep) to prevent bacterial wilt and fruit cracking."
    ),
    SeasonalWindowInfo(
        cropName = "Eggplant",
        localName = "Talong",
        drySeasonStatus = "OPTIMAL",
        wetSeasonStatus = "ACCEPTABLE",
        peakMonths = "Year-Round",
        notes = "Very resilient lowland vegetable; provide raised beds in wet season."
    ),
    SeasonalWindowInfo(
        cropName = "Bell Pepper",
        localName = "Siling Pula",
        drySeasonStatus = "OPTIMAL",
        wetSeasonStatus = "HIGH_RISK",
        peakMonths = "Oct – Mar",
        notes = "Requires good drainage; high humidity triggers anthracnose fruit rot."
    ),
    SeasonalWindowInfo(
        cropName = "Chili Pepper",
        localName = "Siling Haba",
        drySeasonStatus = "OPTIMAL",
        wetSeasonStatus = "OPTIMAL",
        peakMonths = "Year-Round",
        notes = "Extremely hardy; continuous fruiting under full sunlight."
    ),
    SeasonalWindowInfo(
        cropName = "Cabbage",
        localName = "Repolyo",
        drySeasonStatus = "OPTIMAL",
        wetSeasonStatus = "HIGH_RISK",
        peakMonths = "Nov – Feb",
        notes = "Cool dry season delivers firm solid heads without soft rot."
    ),
    SeasonalWindowInfo(
        cropName = "Pechay",
        localName = "Pechay",
        drySeasonStatus = "OPTIMAL",
        wetSeasonStatus = "OPTIMAL",
        peakMonths = "Year-Round",
        notes = "Quick 30-day crop; protect from heavy downpours with rain shelters."
    )
)
