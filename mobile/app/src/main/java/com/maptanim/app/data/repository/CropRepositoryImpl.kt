package com.maptanim.app.data.repository

import android.content.Context
import com.maptanim.app.data.datasource.CropMetadataAssetDataSource
import com.maptanim.app.data.local.dao.CropDao
import com.maptanim.app.data.local.entity.toDomain
import com.maptanim.app.data.local.entity.toEntity
import com.maptanim.app.data.remote.CropRemoteRepository
import com.maptanim.app.data.remote.dto.CropDto
import com.maptanim.app.domain.model.Crop
import com.maptanim.app.domain.model.SoilType
import com.maptanim.app.domain.repository.CropRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class CropRepositoryImpl(
    private val cropDao: CropDao? = null,
    private val remoteRepository: CropRemoteRepository = CropRemoteRepository(),
    private val context: Context? = RepositoryProvider.appContext
) : CropRepository {

    private val activeCropsList: List<Crop> by lazy {
        getCropsWithAssetMetadata(context)
    }

    private val inMemoryFallback = MutableStateFlow(activeCropsList)

    override fun observeAllCrops(): Flow<List<Crop>> {
        return cropDao?.observeAllCrops()?.map { entities ->
            if (entities.isEmpty()) activeCropsList else entities.map { it.toDomain() }
        } ?: inMemoryFallback
    }

    override fun observeCropByName(name: String): Flow<Crop?> {
        return cropDao?.observeCropByName(name)?.map { entity ->
            entity?.toDomain() ?: activeCropsList.firstOrNull {
                it.name.equals(name, ignoreCase = true) || it.localName.equals(name, ignoreCase = true)
            }
        } ?: inMemoryFallback.map { crops ->
            crops.firstOrNull { it.name.equals(name, ignoreCase = true) || it.localName.equals(name, ignoreCase = true) }
        }
    }

    override suspend fun upsertCrops(crops: List<Crop>) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        cropDao?.upsertCrops(crops.map { it.toEntity() })
        inMemoryFallback.value = crops
    }

    suspend fun fetchFromRemote() = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val result = remoteRepository.getAllCrops()
        result.getOrNull()?.let { dtos ->
            if (dtos.isNotEmpty()) {
                val domainCrops = dtos.map { it.toDomain() }
                cropDao?.upsertCrops(domainCrops.map { it.toEntity() })
                inMemoryFallback.value = domainCrops
            }
        }
    }
}

private fun CropDto.toDomain(): Crop = Crop(
    id = id.ifBlank { name.lowercase().replace(" ", "_") },
    name = name.ifBlank { "Crop" },
    localName = local_name,
    botanicalName = botanical_name,
    category = category,
    daysToHarvest = days_to_harvest ?: 60,
    wateringIntervalDays = watering_interval_days ?: 2,
    fertilizeIntervalDays = fertilize_interval_days ?: 14,
    nRatio = npk_n ?: 1.0f,
    pRatio = npk_p ?: 1.0f,
    kRatio = npk_k ?: 1.0f,
    optimalPhMin = optimal_ph_min ?: 6.0f,
    optimalPhMax = optimal_ph_max ?: 7.0f,
    idealSoils = (suitable_soils ?: emptyList()).mapNotNull { parseSoilType(it) },
    suitableSoils = (suitable_soils ?: emptyList()).mapNotNull { parseSoilType(it) },
    toleratedSoils = listOf(SoilType.SANDY, SoilType.PEATY),
    pestRiskSeason = listOf("WET"),
    seasonality = listOf("YEAR_ROUND"),
    imageUrl = image_url,
    description = description
)

private fun parseSoilType(soil: String): SoilType? = try {
    SoilType.valueOf(soil.uppercase())
} catch (e: Exception) {
    SoilType.LOAM
}

internal fun getCropsWithAssetMetadata(context: Context?): List<Crop> {
    val metaList = CropMetadataAssetDataSource.loadAllCropMetadata(context)
    if (metaList.isEmpty()) return defaultPhilippineCrops

    return metaList.map { meta ->
        val existingDefault = defaultPhilippineCrops.firstOrNull {
            it.name.equals(meta.commonName, ignoreCase = true) ||
            it.localName?.equals(meta.localNamePh, ignoreCase = true) == true ||
            it.id.equals(meta.id, ignoreCase = true)
        }

        val daysToHarvestVal = meta.varieties.firstOrNull()?.growthDurationDays
            ?: meta.daysToHarvestStr.split("-").firstOrNull()?.trim()?.toIntOrNull()
            ?: existingDefault?.daysToHarvest
            ?: 60

        val (phMin, phMax) = parsePhRange(meta.optimalPhStr, existingDefault?.optimalPhMin ?: 6.0f, existingDefault?.optimalPhMax ?: 7.0f)

        Crop(
            id = meta.id,
            name = meta.commonName,
            localName = meta.localNamePh,
            botanicalName = meta.scientificName,
            category = meta.cropType.uppercase(),
            daysToHarvest = daysToHarvestVal,
            wateringIntervalDays = meta.varieties.firstOrNull()?.wateringIntervalDays ?: existingDefault?.wateringIntervalDays ?: 2,
            fertilizeIntervalDays = meta.varieties.firstOrNull()?.fertilizeIntervalDays ?: existingDefault?.fertilizeIntervalDays ?: 14,
            nRatio = existingDefault?.nRatio ?: 1.5f,
            pRatio = existingDefault?.pRatio ?: 1.0f,
            kRatio = existingDefault?.kRatio ?: 1.5f,
            optimalPhMin = phMin,
            optimalPhMax = phMax,
            idealSoils = existingDefault?.idealSoils ?: listOf(SoilType.LOAM),
            suitableSoils = existingDefault?.suitableSoils ?: listOf(SoilType.SANDY, SoilType.SILTY),
            toleratedSoils = existingDefault?.toleratedSoils ?: listOf(SoilType.CLAY),
            pestRiskSeason = existingDefault?.pestRiskSeason ?: listOf("WET"),
            seasonality = meta.varieties.flatMap { it.optimalSeasons }.distinct().ifEmpty { existingDefault?.seasonality ?: listOf("YEAR_ROUND") },
            imageUrl = CropMetadataAssetDataSource.getCropAssetImagePath(meta.id, meta.commonName),
            companionPlants = existingDefault?.companionPlants ?: emptyList(),
            avoidPlants = existingDefault?.avoidPlants ?: emptyList(),
            commonPests = existingDefault?.commonPests ?: emptyList(),
            harvestIndicators = existingDefault?.harvestIndicators ?: "Matures at $daysToHarvestVal days",
            description = meta.description.ifBlank { existingDefault?.description }
        )
    }
}

private fun parsePhRange(phStr: String, defaultMin: Float, defaultMax: Float): Pair<Float, Float> {
    if (phStr.isBlank()) return Pair(defaultMin, defaultMax)
    val parts = phStr.split("-")
    if (parts.size == 2) {
        val min = parts[0].trim().toFloatOrNull() ?: defaultMin
        val max = parts[1].trim().toFloatOrNull() ?: defaultMax
        return Pair(min, max)
    }
    return Pair(defaultMin, defaultMax)
}

internal val defaultPhilippineCrops = listOf(
    Crop(
        id = "crop_tomato",
        name = "Tomato",
        localName = "Kamatis",
        botanicalName = "Solanum lycopersicum",
        category = "FRUIT",
        daysToHarvest = 60,
        wateringIntervalDays = 2,
        fertilizeIntervalDays = 14,
        nRatio = 1.5f, pRatio = 1.0f, kRatio = 2.0f,
        optimalPhMin = 6.0f, optimalPhMax = 6.8f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SANDY, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.CLAY),
        pestRiskSeason = listOf("WET"),
        seasonality = listOf("YEAR_ROUND", "DRY"),
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/89/Tomato_je.jpg/800px-Tomato_je.jpg",
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
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/76/Solanum_melongena_24_08_2012_%281%29.JPG/800px-Solanum_melongena_24_08_2012_%281%29.JPG",
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
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5c/Baby_Bell_pepper_%27%27Capsicum_annuum%27%27_.jpg/800px-Baby_Bell_pepper_%27%27Capsicum_annuum%27%27_.jpg",
        companionPlants = listOf("Tomato", "Onion", "Okra"),
        avoidPlants = listOf("String Beans"),
        commonPests = listOf("Aphids", "Pepper Anthracnose"),
        harvestIndicators = "Full size fruit with thick, firm glossy walls",
        description = "Requires well-drained fertile soil and consistent watering."
    ),
    Crop(
        id = "crop_chili_pepper",
        name = "Chili Pepper",
        localName = "Siling Haba / Labuyo",
        botanicalName = "Capsicum frutescens",
        category = "FRUIT",
        daysToHarvest = 65,
        wateringIntervalDays = 2,
        fertilizeIntervalDays = 14,
        nRatio = 1.2f, pRatio = 1.0f, kRatio = 2.0f,
        optimalPhMin = 6.0f, optimalPhMax = 7.0f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SANDY, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.CLAY),
        pestRiskSeason = listOf("WET"),
        seasonality = listOf("YEAR_ROUND"),
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5c/Baby_Bell_pepper_%27%27Capsicum_annuum%27%27_.jpg/800px-Baby_Bell_pepper_%27%27Capsicum_annuum%27%27_.jpg",
        companionPlants = listOf("Eggplant", "Tomato", "Basil"),
        avoidPlants = listOf("Fennel"),
        commonPests = listOf("Thrips", "Anthracnose"),
        harvestIndicators = "Pods turn bright green or deep red and firm",
        description = "Hot spice crop resilient to high heat and warm weather."
    ),
    Crop(
        id = "crop_cabbage",
        name = "Cabbage",
        localName = "Repolyo",
        botanicalName = "Brassica oleracea var. capitata",
        category = "LEAFY",
        daysToHarvest = 60,
        wateringIntervalDays = 2,
        fertilizeIntervalDays = 10,
        nRatio = 2.0f, pRatio = 1.0f, kRatio = 1.5f,
        optimalPhMin = 6.0f, optimalPhMax = 6.5f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SILTY, SoilType.PEATY),
        toleratedSoils = listOf(SoilType.CLAY),
        pestRiskSeason = listOf("DRY"),
        seasonality = listOf("DRY", "LOWLAND", "YEAR_ROUND"),
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6f/Chou_cabus_blanc_01.jpg/800px-Chou_cabus_blanc_01.jpg",
        companionPlants = listOf("Onion", "Celery"),
        avoidPlants = listOf("Tomato", "String Beans"),
        commonPests = listOf("Diamondback Moth", "Cabbage Looper"),
        harvestIndicators = "Firm, solid head formed at plant center",
        description = "Cool-season leafy crop high in Vitamin C."
    ),
    Crop(
        id = "crop_pechay",
        name = "Pechay",
        localName = "Pechay",
        botanicalName = "Brassica rapa subsp. chinensis",
        category = "LEAFY",
        daysToHarvest = 28,
        wateringIntervalDays = 1,
        fertilizeIntervalDays = 7,
        nRatio = 2.0f, pRatio = 1.0f, kRatio = 1.0f,
        optimalPhMin = 6.0f, optimalPhMax = 7.0f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SILTY, SoilType.PEATY),
        toleratedSoils = listOf(SoilType.SANDY),
        pestRiskSeason = listOf("DRY"),
        seasonality = listOf("YEAR_ROUND"),
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/48/Pak_Choi_%28Brassica_rapa_subsp._chinensis%29.jpg/800px-Pak_Choi_%28Brassica_rapa_subsp._chinensis%29.jpg",
        companionPlants = listOf("Onion", "Carrot", "Cucumber"),
        avoidPlants = listOf("Strawberry"),
        commonPests = listOf("Flea Beetle", "Cutworm"),
        harvestIndicators = "Crisp upright green petioles at 25-30 days",
        description = "Fastest turnaround leafy brassica widely grown in lowland beds."
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
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/25/Allium_cepa_viviparum_001.JPG/800px-Allium_cepa_viviparum_001.JPG",
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
        daysToHarvest = 85,
        wateringIntervalDays = 2,
        fertilizeIntervalDays = 15,
        nRatio = 1.0f, pRatio = 2.0f, kRatio = 2.0f,
        optimalPhMin = 5.8f, optimalPhMax = 6.8f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SANDY),
        toleratedSoils = listOf(SoilType.SILTY),
        pestRiskSeason = listOf("WET"),
        seasonality = listOf("DRY", "COOL", "HIGHLAND"),
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/Papilio_machaon_-_Daucus_carota_-_Keila.jpg/800px-Papilio_machaon_-_Daucus_carota_-_Keila.jpg",
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
        category = "PODDED",
        daysToHarvest = 48,
        wateringIntervalDays = 2,
        fertilizeIntervalDays = 14,
        nRatio = 0.5f, pRatio = 1.5f, kRatio = 1.5f,
        optimalPhMin = 5.5f, optimalPhMax = 6.5f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SANDY, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.CLAY),
        pestRiskSeason = listOf("WET"),
        seasonality = listOf("YEAR_ROUND"),
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f6/Yard_Long_Bean_Flowers.jpg/800px-Yard_Long_Bean_Flowers.jpg",
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
        daysToHarvest = 45,
        wateringIntervalDays = 1,
        fertilizeIntervalDays = 10,
        nRatio = 1.5f, pRatio = 1.0f, kRatio = 1.0f,
        optimalPhMin = 6.0f, optimalPhMax = 7.0f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.PEATY, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.SANDY),
        pestRiskSeason = listOf("DRY"),
        seasonality = listOf("WET", "YEAR_ROUND"),
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/da/Lactuca_sativa_001.jpg/800px-Lactuca_sativa_001.jpg",
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
        daysToHarvest = 50,
        wateringIntervalDays = 1,
        fertilizeIntervalDays = 10,
        nRatio = 1.5f, pRatio = 1.0f, kRatio = 2.0f,
        optimalPhMin = 6.0f, optimalPhMax = 6.8f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SANDY, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.CLAY),
        pestRiskSeason = listOf("WET"),
        seasonality = listOf("YEAR_ROUND"),
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/96/Cucumis_sativus_002.jpg/800px-Cucumis_sativus_002.jpg",
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
        category = "PODDED",
        daysToHarvest = 45,
        wateringIntervalDays = 2,
        fertilizeIntervalDays = 14,
        nRatio = 1.2f, pRatio = 1.0f, kRatio = 1.5f,
        optimalPhMin = 6.0f, optimalPhMax = 7.5f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.CLAY, SoilType.SANDY),
        toleratedSoils = listOf(SoilType.SILTY),
        pestRiskSeason = listOf("DRY"),
        seasonality = listOf("WET", "YEAR_ROUND"),
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e4/Okra_%28Abelmoschus_esculentus%29_Feb_2019._DSC_0060_01.jpg/800px-Okra_%28Abelmoschus_esculentus%29_Feb_2019._DSC_0060_01.jpg",
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
        category = "GRAIN",
        daysToHarvest = 65,
        wateringIntervalDays = 3,
        fertilizeIntervalDays = 14,
        nRatio = 2.5f, pRatio = 1.0f, kRatio = 1.5f,
        optimalPhMin = 5.8f, optimalPhMax = 7.0f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.CLAY, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.SANDY),
        pestRiskSeason = listOf("WET"),
        seasonality = listOf("YEAR_ROUND"),
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/Zea_mays_%27morado%27_MHNT.BOT.2015.34.11.jpg/800px-Zea_mays_%27morado%27_MHNT.BOT.2015.34.11.jpg",
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
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5c/Cucurbita_moschata_Butternut_2012_G2.jpg/800px-Cucurbita_moschata_Butternut_2012_G2.jpg",
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
        daysToHarvest = 30,
        wateringIntervalDays = 1,
        fertilizeIntervalDays = 7,
        nRatio = 2.0f, pRatio = 1.0f, kRatio = 1.0f,
        optimalPhMin = 5.5f, optimalPhMax = 7.0f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SILTY, SoilType.PEATY),
        toleratedSoils = listOf(SoilType.CLAY),
        pestRiskSeason = listOf("DRY"),
        seasonality = listOf("YEAR_ROUND"),
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Ipomoea_aquatica_water_spinach.jpg/800px-Ipomoea_aquatica_water_spinach.jpg",
        companionPlants = listOf("Pechay", "Cucumber"),
        avoidPlants = emptyList(),
        commonPests = listOf("Leafminer"),
        harvestIndicators = "Tender leafy stems 20-30cm tall",
        description = "Fast-growing semi-aquatic leafy green staple."
    ),
    Crop(
        id = "crop_ampalaya",
        name = "Bitter Gourd",
        localName = "Ampalaya",
        botanicalName = "Momordica charantia",
        category = "CUCURBIT / VINE",
        daysToHarvest = 55,
        wateringIntervalDays = 2,
        fertilizeIntervalDays = 10,
        nRatio = 1.5f, pRatio = 1.2f, kRatio = 2.0f,
        optimalPhMin = 6.0f, optimalPhMax = 6.7f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SANDY, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.CLAY),
        pestRiskSeason = listOf("WET"),
        seasonality = listOf("YEAR_ROUND"),
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b3/Momordica_charantia_22042014_%282%29.JPG/800px-Momordica_charantia_22042014_%282%29.JPG",
        companionPlants = listOf("Eggplant", "String Beans", "Corn"),
        avoidPlants = emptyList(),
        commonPests = listOf("Fruit Fly", "Powdery Mildew", "Aphids"),
        harvestIndicators = "Firm ribbed fruit before turning yellow/orange",
        description = "High-value medicinal bitter gourd vining crop cultivated with bamboo trellises nationwide."
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
        imageUrl = "https://images.unsplash.com/photo-1576045057995-568f588f82fb?auto=format&fit=crop&w=800&q=80",
        companionPlants = listOf("Taro", "Sweet Potato"),
        avoidPlants = emptyList(),
        commonPests = listOf("Leaf Miners", "Flea Beetles"),
        harvestIndicators = "Shoots 20–30 cm tall with succulent green stems",
        description = "Water spinach, ultra fast-growing green tolerant of wet soils."
    ),
    Crop(
        id = "crop_ampalaya",
        name = "Bitter Gourd",
        localName = "Ampalaya",
        botanicalName = "Momordica charantia",
        category = "FRUIT",
        daysToHarvest = 60,
        wateringIntervalDays = 2,
        fertilizeIntervalDays = 14,
        nRatio = 1.5f, pRatio = 1.2f, kRatio = 2.0f,
        optimalPhMin = 6.0f, optimalPhMax = 6.7f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SILTY, SoilType.SANDY),
        toleratedSoils = listOf(SoilType.CLAY),
        pestRiskSeason = listOf("WET"),
        seasonality = listOf("YEAR_ROUND"),
        imageUrl = "https://images.unsplash.com/photo-1588615419955-5233519894e6?auto=format&fit=crop&w=800&q=80",
        companionPlants = listOf("String Beans", "Corn"),
        avoidPlants = listOf("Potato"),
        commonPests = listOf("Fruit Fly", "Downy Mildew"),
        harvestIndicators = "Fruit reaches full length with firm green ridges",
        description = "Medicinal tropical vine vegetable known for health benefits."
    ),
    Crop(
        id = "crop_radish",
        name = "Radish",
        localName = "Labanos",
        botanicalName = "Raphanus sativus",
        category = "ROOT",
        daysToHarvest = 45,
        wateringIntervalDays = 2,
        fertilizeIntervalDays = 10,
        nRatio = 1.0f, pRatio = 1.5f, kRatio = 1.5f,
        optimalPhMin = 6.0f, optimalPhMax = 7.0f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SANDY, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.PEATY),
        pestRiskSeason = listOf("DRY"),
        seasonality = listOf("YEAR_ROUND"),
        imageUrl = "https://images.unsplash.com/photo-1593105544559-ecb03bf76f82?auto=format&fit=crop&w=800&q=80",
        companionPlants = listOf("Cucumber", "Lettuce", "Squash"),
        avoidPlants = listOf("Hyssop"),
        commonPests = listOf("Flea Beetle", "Root Maggot"),
        harvestIndicators = "White taproot emerges 2 inches above soil surface",
        description = "Crisp pungent white root crop quick to mature."
    ),
    Crop(
        id = "crop_ginger",
        name = "Ginger",
        localName = "Luya",
        botanicalName = "Zingiber officinale",
        category = "ROOT",
        daysToHarvest = 240,
        wateringIntervalDays = 3,
        fertilizeIntervalDays = 30,
        nRatio = 1.5f, pRatio = 1.0f, kRatio = 2.5f,
        optimalPhMin = 5.5f, optimalPhMax = 6.5f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SANDY, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.PEATY),
        pestRiskSeason = listOf("WET"),
        seasonality = listOf("YEAR_ROUND"),
        imageUrl = "https://images.unsplash.com/photo-1599940824399-b87987ceb72a?auto=format&fit=crop&w=800&q=80",
        companionPlants = listOf("Taro", "Chili Pepper"),
        avoidPlants = emptyList(),
        commonPests = listOf("Bacterial Wilt", "Shoot Borer"),
        harvestIndicators = "Leaves turn yellow and stem tops wither naturally",
        description = "Aromatic root rhizome crop used extensively in Filipino dishes."
    ),
    Crop(
        id = "crop_garlic",
        name = "Garlic",
        localName = "Bawang",
        botanicalName = "Allium sativum",
        category = "BULB",
        daysToHarvest = 120,
        wateringIntervalDays = 3,
        fertilizeIntervalDays = 20,
        nRatio = 1.0f, pRatio = 1.5f, kRatio = 1.5f,
        optimalPhMin = 6.0f, optimalPhMax = 7.0f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SANDY, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.PEATY),
        pestRiskSeason = listOf("WET"),
        seasonality = listOf("DRY"),
        imageUrl = "https://images.unsplash.com/photo-1540148426945-6cf22a6b2383?auto=format&fit=crop&w=800&q=80",
        companionPlants = listOf("Tomato", "Eggplant", "Pepper"),
        avoidPlants = listOf("String Beans", "Peas"),
        commonPests = listOf("Mites", "Purple Blotch"),
        harvestIndicators = "Lower two-thirds of foliage turns brownish-dry",
        description = "High-value staple spice crop prized for aromatic bulbs."
    ),
    Crop(
        id = "crop_sweet_potato",
        name = "Sweet Potato",
        localName = "Kamote",
        botanicalName = "Ipomoea batatas",
        category = "TUBER",
        daysToHarvest = 110,
        wateringIntervalDays = 3,
        fertilizeIntervalDays = 25,
        nRatio = 1.0f, pRatio = 1.5f, kRatio = 2.5f,
        optimalPhMin = 5.5f, optimalPhMax = 6.6f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SANDY, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.CLAY),
        pestRiskSeason = listOf("WET"),
        seasonality = listOf("YEAR_ROUND"),
        imageUrl = "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?auto=format&fit=crop&w=800&q=80",
        companionPlants = listOf("Corn", "Squash"),
        avoidPlants = emptyList(),
        commonPests = listOf("Sweet Potato Weevil", "Stem Borer"),
        harvestIndicators = "Vines begin yellowing; tubers reach desirable size",
        description = "Nutritious resilient root tuber staple crop in tropical climate."
    ),
    Crop(
        id = "crop_cassava",
        name = "Cassava",
        localName = "Kamoteng Kahoy",
        botanicalName = "Manihot esculenta",
        category = "TUBER",
        daysToHarvest = 270,
        wateringIntervalDays = 4,
        fertilizeIntervalDays = 35,
        nRatio = 1.0f, pRatio = 1.0f, kRatio = 2.0f,
        optimalPhMin = 5.5f, optimalPhMax = 6.5f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SANDY, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.CLAY),
        pestRiskSeason = listOf("DRY"),
        seasonality = listOf("YEAR_ROUND"),
        imageUrl = "https://images.unsplash.com/photo-1590779033100-9f60a05a013d?auto=format&fit=crop&w=800&q=80",
        companionPlants = listOf("Mungbean", "Peanut"),
        avoidPlants = emptyList(),
        commonPests = listOf("Spider Mites", "Mealybugs"),
        harvestIndicators = "Base of stem turns woody and foliage yellowing starts",
        description = "Ultra drought-tolerant starchy root crop essential for food security."
    ),
    Crop(
        id = "crop_mungbean",
        name = "Mungbean",
        localName = "Monggo",
        botanicalName = "Vigna radiata",
        category = "PODDED",
        daysToHarvest = 60,
        wateringIntervalDays = 3,
        fertilizeIntervalDays = 15,
        nRatio = 0.5f, pRatio = 1.5f, kRatio = 1.5f,
        optimalPhMin = 6.2f, optimalPhMax = 7.2f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SANDY, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.CLAY),
        pestRiskSeason = listOf("WET"),
        seasonality = listOf("DRY", "YEAR_ROUND"),
        imageUrl = "https://images.unsplash.com/photo-1515543904379-3d757afe72e4?auto=format&fit=crop&w=800&q=80",
        companionPlants = listOf("Corn", "Sugarcane"),
        avoidPlants = emptyList(),
        commonPests = listOf("Pod Borer", "Powdery Mildew"),
        harvestIndicators = "80% of seed pods turn dark brown or black",
        description = "Short-duration nitrogen-fixing pulse ideal for post-rice rotation."
    ),
    Crop(
        id = "crop_watermelon",
        name = "Watermelon",
        localName = "Pakwan",
        botanicalName = "Citrullus lanatus",
        category = "FRUIT",
        daysToHarvest = 85,
        wateringIntervalDays = 2,
        fertilizeIntervalDays = 14,
        nRatio = 1.5f, pRatio = 1.0f, kRatio = 2.5f,
        optimalPhMin = 6.0f, optimalPhMax = 6.8f,
        idealSoils = listOf(SoilType.SANDY),
        suitableSoils = listOf(SoilType.LOAM, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.CLAY),
        pestRiskSeason = listOf("WET"),
        seasonality = listOf("DRY"),
        imageUrl = "https://images.unsplash.com/photo-1587049352847-4a222e784d38?auto=format&fit=crop&w=800&q=80",
        companionPlants = listOf("Radish", "Corn"),
        avoidPlants = listOf("Potato"),
        commonPests = listOf("Thrips", "Fusarium Wilt"),
        harvestIndicators = "Tendril nearest fruit dries up; creamy yellow belly spot",
        description = "High-value juicy melon fruit thriving in sunny dry season."
    ),
    Crop(
        id = "crop_calamansi",
        name = "Calamansi",
        localName = "Kalamansi",
        botanicalName = "Citrofortunella microcarpa",
        category = "FRUIT",
        daysToHarvest = 180,
        wateringIntervalDays = 3,
        fertilizeIntervalDays = 30,
        nRatio = 2.0f, pRatio = 1.0f, kRatio = 2.0f,
        optimalPhMin = 5.5f, optimalPhMax = 6.5f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SANDY, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.CLAY),
        pestRiskSeason = listOf("WET"),
        seasonality = listOf("YEAR_ROUND"),
        imageUrl = "https://images.unsplash.com/photo-1534531141161-bc8144299a8b?auto=format&fit=crop&w=800&q=80",
        companionPlants = listOf("Legumes", "Coffee"),
        avoidPlants = emptyList(),
        commonPests = listOf("Citrus Leafminer", "Rind Borer"),
        harvestIndicators = "Fruits turn glossy dark green to yellow-green, plump",
        description = "Iconic Philippine citrus tree producing tart vitamin-rich fruits."
    )
)
