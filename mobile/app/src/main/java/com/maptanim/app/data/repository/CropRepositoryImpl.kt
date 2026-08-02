package com.maptanim.app.data.repository

import com.maptanim.app.domain.model.Crop
import com.maptanim.app.domain.model.SoilType
import com.maptanim.app.domain.repository.CropRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class CropRepositoryImpl(
    private val remoteRepository: CropRemoteRepository = CropRemoteRepository()
) : CropRepository {

    private val cropsCache = MutableStateFlow<List<Crop>>(defaultPhilippineCrops)

    override fun observeAllCrops(): Flow<List<Crop>> {
        return cropsCache.map { it }
    }

    override fun observeCropByName(name: String): Flow<Crop?> {
        return cropsCache.map { crops ->
            crops.firstOrNull { it.name.equals(name, ignoreCase = true) || it.localName.equals(name, ignoreCase = true) }
        }
    }

    override suspend fun upsertCrops(crops: List<Crop>) {
        cropsCache.value = crops
    }

    suspend fun fetchFromRemote() {
        val result = remoteRepository.getAllCrops()
        result.getOrNull()?.let { dtos ->
            if (dtos.isNotEmpty()) {
                cropsCache.value = dtos.map { it.toDomain() }
            }
        }
    }
}

private fun CropDto.toDomain(): Crop = Crop(
    id = id,
    name = name,
    localName = local_name,
    botanicalName = null,
    category = category,
    daysToHarvest = days_to_harvest,
    wateringIntervalDays = watering_interval_days,
    fertilizeIntervalDays = fertilize_interval_days,
    nRatio = 1.0f,
    pRatio = 1.0f,
    kRatio = 1.0f,
    optimalPhMin = 6.0f,
    optimalPhMax = 7.0f,
    idealSoils = listOf(SoilType.LOAM),
    suitableSoils = listOf(SoilType.CLAY, SoilType.SILTY),
    toleratedSoils = listOf(SoilType.SANDY, SoilType.PEATY, SoilType.CHALKY),
    pestRiskSeason = listOf("WET"),
    seasonality = listOf("YEAR_ROUND"),
    imageUrl = image_url
)

internal val defaultPhilippineCrops = listOf(
    Crop(
        id = "crop_tomato",
        name = "Tomato",
        localName = "Kamatis",
        botanicalName = "Solanum lycopersicum",
        category = "FRUIT",
        daysToHarvest = 70,
        wateringIntervalDays = 2,
        fertilizeIntervalDays = 14,
        nRatio = 1.5f, pRatio = 1.0f, kRatio = 2.0f,
        optimalPhMin = 6.0f, optimalPhMax = 6.8f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SANDY, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.CLAY),
        pestRiskSeason = listOf("WET"),
        seasonality = listOf("DRY"),
        imageUrl = "crop_tomato",
        companionPlants = listOf("Lettuce", "Carrot", "Onion"),
        avoidPlants = listOf("Eggplant", "Cabbage", "Corn"),
        commonPests = listOf("Fruit Borer", "Tomato Leaf Curl Virus"),
        harvestIndicators = "Fruit turns bright red/orange; firm to touch",
        description = "High-value fruit vegetable sensitive to excessive moisture."
    ),
    Crop(
        id = "crop_eggplant",
        name = "Eggplant",
        localName = "Talong",
        botanicalName = "Solanum melongena",
        category = "FRUIT",
        daysToHarvest = 75,
        wateringIntervalDays = 2,
        fertilizeIntervalDays = 14,
        nRatio = 1.5f, pRatio = 1.0f, kRatio = 2.5f,
        optimalPhMin = 5.5f, optimalPhMax = 6.8f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.CLAY, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.SANDY),
        pestRiskSeason = listOf("WET", "DRY"),
        seasonality = listOf("YEAR_ROUND"),
        imageUrl = "crop_eggplant",
        companionPlants = listOf("String Beans", "Bell Pepper"),
        avoidPlants = listOf("Tomato"),
        commonPests = listOf("Fruit Borer", "Flea Beetle"),
        harvestIndicators = "Glossy deep purple skin, firm flesh",
        description = "Popular lowland vegetable, warm-season heavy cropper."
    ),
    Crop(
        id = "crop_bell_pepper",
        name = "Bell Pepper",
        localName = "Siling Pula",
        botanicalName = "Capsicum annuum",
        category = "FRUIT",
        daysToHarvest = 80,
        wateringIntervalDays = 2,
        fertilizeIntervalDays = 14,
        nRatio = 1.2f, pRatio = 1.0f, kRatio = 2.0f,
        optimalPhMin = 6.0f, optimalPhMax = 7.0f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SANDY, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.CLAY),
        pestRiskSeason = listOf("WET"),
        seasonality = listOf("DRY"),
        imageUrl = "crop_pepper",
        companionPlants = listOf("Tomato", "Onion", "Okra"),
        avoidPlants = listOf("String Beans"),
        commonPests = listOf("Aphids", "Pepper Anthracnose"),
        harvestIndicators = "Full size fruit with thick, firm glossy walls",
        description = "Requires well-drained fertile soil and consistent watering."
    ),
    Crop(
        id = "crop_cabbage",
        name = "Cabbage",
        localName = "Repolyo",
        botanicalName = "Brassica oleracea var. capitata",
        category = "LEAFY",
        daysToHarvest = 90,
        wateringIntervalDays = 2,
        fertilizeIntervalDays = 10,
        nRatio = 2.0f, pRatio = 1.0f, kRatio = 1.5f,
        optimalPhMin = 6.0f, optimalPhMax = 6.5f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SILTY, SoilType.PEATY),
        toleratedSoils = listOf(SoilType.CLAY),
        pestRiskSeason = listOf("DRY"),
        seasonality = listOf("DRY"),
        imageUrl = "crop_cabbage",
        companionPlants = listOf("Onion", "Celery"),
        avoidPlants = listOf("Tomato", "String Beans"),
        commonPests = listOf("Diamondback Moth", "Cabbage Looper"),
        harvestIndicators = "Firm, solid head formed at plant center",
        description = "Cool-season leafy crop high in Vitamin C."
    ),
    Crop(
        id = "crop_onion",
        name = "Onion",
        localName = "Sibuyas",
        botanicalName = "Allium cepa",
        category = "BULB",
        daysToHarvest = 110,
        wateringIntervalDays = 3,
        fertilizeIntervalDays = 20,
        nRatio = 1.0f, pRatio = 1.5f, kRatio = 1.5f,
        optimalPhMin = 6.0f, optimalPhMax = 7.0f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SANDY, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.PEATY),
        pestRiskSeason = listOf("WET"),
        seasonality = listOf("DRY"),
        imageUrl = "crop_onion",
        companionPlants = listOf("Cabbage", "Tomato", "Carrot"),
        avoidPlants = listOf("String Beans"),
        commonPests = listOf("Thrips", "Purple Blotch"),
        harvestIndicators = "Tops turn yellow and dryly fall over",
        description = "High-value bulb crop sensitive to weed competition."
    ),
    Crop(
        id = "crop_carrot",
        name = "Carrot",
        localName = "Karot",
        botanicalName = "Daucus carota",
        category = "ROOT",
        daysToHarvest = 75,
        wateringIntervalDays = 2,
        fertilizeIntervalDays = 15,
        nRatio = 1.0f, pRatio = 2.0f, kRatio = 2.0f,
        optimalPhMin = 5.8f, optimalPhMax = 6.8f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SANDY),
        toleratedSoils = listOf(SoilType.SILTY),
        pestRiskSeason = listOf("WET"),
        seasonality = listOf("DRY"),
        imageUrl = "crop_carrot",
        companionPlants = listOf("Lettuce", "Tomato", "Onion"),
        avoidPlants = listOf("Parsnip"),
        commonPests = listOf("Root-knot Nematode", "Cutworm"),
        harvestIndicators = "Root crown reaches 1 inch diameter, bright orange",
        description = "Deep loose soil preferred for straight root development."
    ),
    Crop(
        id = "crop_string_beans",
        name = "String Beans",
        localName = "Sitaw",
        botanicalName = "Vigna unguiculata subsp. sesquipedalis",
        category = "FRUIT",
        daysToHarvest = 55,
        wateringIntervalDays = 2,
        fertilizeIntervalDays = 14,
        nRatio = 0.5f, pRatio = 1.5f, kRatio = 1.5f,
        optimalPhMin = 5.5f, optimalPhMax = 6.5f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SANDY, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.CLAY),
        pestRiskSeason = listOf("WET"),
        seasonality = listOf("YEAR_ROUND"),
        imageUrl = "crop_beans",
        companionPlants = listOf("Corn", "Cucumber", "Eggplant"),
        avoidPlants = listOf("Onion"),
        commonPests = listOf("Bean Pod Borer", "Black Aphids"),
        harvestIndicators = "Long tender pods snap easily before seeds bulge",
        description = "Nitrogen-fixing legume vegetable providing soil enrichment."
    ),
    Crop(
        id = "crop_lettuce",
        name = "Lettuce",
        localName = "Litsugas",
        botanicalName = "Lactuca sativa",
        category = "LEAFY",
        daysToHarvest = 50,
        wateringIntervalDays = 1,
        fertilizeIntervalDays = 10,
        nRatio = 1.5f, pRatio = 1.0f, kRatio = 1.0f,
        optimalPhMin = 6.0f, optimalPhMax = 7.0f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.PEATY, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.SANDY),
        pestRiskSeason = listOf("DRY"),
        seasonality = listOf("WET", "YEAR_ROUND"),
        imageUrl = "crop_lettuce",
        companionPlants = listOf("Carrot", "Tomato", "Onion"),
        avoidPlants = listOf("Celery"),
        commonPests = listOf("Snails and Slugs", "Aphids"),
        harvestIndicators = "Full compact leaf rosette, crisp tender leaves",
        description = "Fast-growing tender salad crop requiring cool moist root zone."
    ),
    Crop(
        id = "crop_cucumber",
        name = "Cucumber",
        localName = "Pipino",
        botanicalName = "Cucumis sativus",
        category = "FRUIT",
        daysToHarvest = 60,
        wateringIntervalDays = 1,
        fertilizeIntervalDays = 10,
        nRatio = 1.5f, pRatio = 1.0f, kRatio = 2.0f,
        optimalPhMin = 6.0f, optimalPhMax = 6.8f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SANDY, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.CLAY),
        pestRiskSeason = listOf("WET"),
        seasonality = listOf("YEAR_ROUND"),
        imageUrl = "crop_cucumber",
        companionPlants = listOf("Corn", "String Beans", "Okra"),
        avoidPlants = listOf("Potato"),
        commonPests = listOf("Cucumber Beetle", "Downy Mildew"),
        harvestIndicators = "Medium size green fruit with firm non-bitter skin",
        description = "Vining fruit crop requiring support structure or ground mulch."
    ),
    Crop(
        id = "crop_okra",
        name = "Okra",
        localName = "Okra",
        botanicalName = "Abelmoschus esculentus",
        category = "FRUIT",
        daysToHarvest = 55,
        wateringIntervalDays = 2,
        fertilizeIntervalDays = 14,
        nRatio = 1.2f, pRatio = 1.0f, kRatio = 1.5f,
        optimalPhMin = 6.0f, optimalPhMax = 7.5f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.CLAY, SoilType.SANDY),
        toleratedSoils = listOf(SoilType.SILTY),
        pestRiskSeason = listOf("DRY"),
        seasonality = listOf("WET", "YEAR_ROUND"),
        imageUrl = "crop_okra",
        companionPlants = listOf("Pepper", "Eggplant", "Cucumber"),
        avoidPlants = listOf("Squash"),
        commonPests = listOf("Cotton Leafhopper", "Fruit Borer"),
        harvestIndicators = "Pods 3–4 inches long, tip snaps cleanly",
        description = "Drought-tolerant tropical vegetable producing continuous harvest."
    ),
    Crop(
        id = "crop_corn",
        name = "Corn",
        localName = "Mais",
        botanicalName = "Zea mays",
        category = "FRUIT",
        daysToHarvest = 85,
        wateringIntervalDays = 3,
        fertilizeIntervalDays = 14,
        nRatio = 2.5f, pRatio = 1.0f, kRatio = 1.5f,
        optimalPhMin = 5.8f, optimalPhMax = 7.0f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.CLAY, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.SANDY),
        pestRiskSeason = listOf("WET"),
        seasonality = listOf("YEAR_ROUND"),
        imageUrl = "crop_corn",
        companionPlants = listOf("Cucumber", "String Beans", "Squash"),
        avoidPlants = listOf("Tomato"),
        commonPests = listOf("Fall Armyworm", "Corn Earworm"),
        harvestIndicators = "Silks turn brown/dry, kernels exude milky sap",
        description = "Heavy feeder cereal crop providing trellis support for beans."
    ),
    Crop(
        id = "crop_squash",
        name = "Squash",
        localName = "Kalabasa",
        botanicalName = "Cucurbita moschata",
        category = "FRUIT",
        daysToHarvest = 80,
        wateringIntervalDays = 2,
        fertilizeIntervalDays = 14,
        nRatio = 1.5f, pRatio = 1.5f, kRatio = 2.0f,
        optimalPhMin = 5.6f, optimalPhMax = 6.8f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.CLAY, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.SANDY),
        pestRiskSeason = listOf("WET"),
        seasonality = listOf("WET", "YEAR_ROUND"),
        imageUrl = "crop_squash",
        companionPlants = listOf("Corn", "String Beans"),
        avoidPlants = listOf("Potato"),
        commonPests = listOf("Squash Bug", "Powdery Mildew"),
        harvestIndicators = "Rind hardens into deep yellowish-tan, stem turns dry",
        description = "Sprawling vine crop rich in Vitamin A and long-storing fruit."
    ),
    Crop(
        id = "crop_kangkong",
        name = "Kangkong",
        localName = "Kangkong",
        botanicalName = "Ipomoea aquatica",
        category = "LEAFY",
        daysToHarvest = 35,
        wateringIntervalDays = 1,
        fertilizeIntervalDays = 7,
        nRatio = 2.0f, pRatio = 1.0f, kRatio = 1.0f,
        optimalPhMin = 5.3f, optimalPhMax = 7.0f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SILTY, SoilType.CLAY, SoilType.PEATY),
        toleratedSoils = listOf(SoilType.SANDY),
        pestRiskSeason = listOf("DRY"),
        seasonality = listOf("YEAR_ROUND"),
        imageUrl = "crop_kangkong",
        companionPlants = listOf("Taro", "Sweet Potato"),
        avoidPlants = emptyList(),
        commonPests = listOf("Leaf Miners", "Flea Beetles"),
        harvestIndicators = "Shoots 20–30 cm tall with succulent green stems",
        description = "Water spinach, ultra fast-growing green tolerant of wet soils."
    )
)

