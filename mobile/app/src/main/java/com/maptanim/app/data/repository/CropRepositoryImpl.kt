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

    override suspend fun refreshCrops(): Result<Unit> = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        try {
            fetchFromRemote()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
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

private fun CropDto.toDomain(): Crop {
    CropMetadataAssetDataSource.registerCropImageUrl(id, name, image_url)
    val existingDefault = defaultPhilippineCrops.firstOrNull {
        it.name.equals(name, ignoreCase = true) ||
        it.localName?.equals(local_name, ignoreCase = true) == true ||
        it.id.equals(id, ignoreCase = true)
    }

    return Crop(
        id = id.ifBlank { name.lowercase().replace(" ", "_") },
        name = name.ifBlank { "Crop" },
        localName = local_name ?: existingDefault?.localName,
        botanicalName = botanical_name ?: existingDefault?.botanicalName,
        category = category.ifBlank { existingDefault?.category ?: "FRUIT" },
        daysToHarvest = days_to_harvest ?: existingDefault?.daysToHarvest ?: 60,
        wateringIntervalDays = watering_interval_days ?: existingDefault?.wateringIntervalDays ?: 2,
        fertilizeIntervalDays = fertilize_interval_days ?: existingDefault?.fertilizeIntervalDays ?: 14,
        nRatio = npk_n ?: existingDefault?.nRatio ?: 1.0f,
        pRatio = npk_p ?: existingDefault?.pRatio ?: 1.0f,
        kRatio = npk_k ?: existingDefault?.kRatio ?: 1.0f,
        optimalPhMin = optimal_ph_min ?: existingDefault?.optimalPhMin ?: 6.0f,
        optimalPhMax = optimal_ph_max ?: existingDefault?.optimalPhMax ?: 7.0f,
        idealSoils = (suitable_soils ?: emptyList()).mapNotNull { parseSoilType(it) }.ifEmpty { existingDefault?.idealSoils ?: listOf(SoilType.LOAM) },
        suitableSoils = (suitable_soils ?: emptyList()).mapNotNull { parseSoilType(it) }.ifEmpty { existingDefault?.suitableSoils ?: emptyList() },
        toleratedSoils = existingDefault?.toleratedSoils ?: emptyList(),
        pestRiskSeason = if (season?.uppercase() == "DRY") listOf("DRY") else listOf("WET"),
        seasonality = listOf(season?.uppercase() ?: "YEAR_ROUND"),
        imageUrl = CropMetadataAssetDataSource.resolveCropImage(id, name, image_url ?: existingDefault?.imageUrl),
        companionPlants = companion_plants_good?.takeIf { it.isNotEmpty() } ?: existingDefault?.companionPlants ?: emptyList(),
        avoidPlants = companion_plants_bad?.takeIf { it.isNotEmpty() } ?: existingDefault?.avoidPlants ?: emptyList(),
        commonPests = common_pests?.takeIf { it.isNotEmpty() } ?: existingDefault?.commonPests ?: emptyList(),
        harvestIndicators = harvest_indicators ?: existingDefault?.harvestIndicators,
        description = description ?: existingDefault?.description
    )
}

private fun parseSoilType(soil: String): SoilType? = try {
    SoilType.valueOf(soil.trim().uppercase())
} catch (e: Exception) {
    null
}

internal fun getCropsWithAssetMetadata(context: Context?): List<Crop> {
    val metaList = CropMetadataAssetDataSource.loadAllCropMetadata(context)
    if (metaList.isEmpty()) return defaultPhilippineCrops

    val canonicalCropIds = setOf(
        "crop_tomato", "crop_eggplant", "crop_bell_pepper", "crop_cabbage",
        "crop_pechay", "crop_carrot", "crop_string_beans", "crop_lettuce",
        "crop_cucumber", "crop_squash", "crop_ampalaya", "crop_potato", "crop_green_onion"
    )

    return metaList
        .filter { it.id.lowercase() in canonicalCropIds }
        .distinctBy { it.id.lowercase() }
        .map { meta ->
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
            imageUrl = CropMetadataAssetDataSource.resolveCropImage(meta.id, meta.commonName, existingDefault?.imageUrl),
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
    // 1. Tomato
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
        imageUrl = "file:///android_asset/metadata/crops_images/tomato.png",
        companionPlants = listOf("Lettuce", "Carrot", "Green Onion"),
        avoidPlants = listOf("Eggplant", "Cabbage"),
        commonPests = listOf("Tomato Fruit Borer", "Bacterial Wilt", "Tomato Leaf Curl Virus"),
        harvestIndicators = "Fruit turns bright red/orange; firm to touch",
        description = "High-value fruit vegetable sensitive to excessive moisture."
    ),
    // 2. Eggplant
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
        imageUrl = "file:///android_asset/metadata/crops_images/eggplant.png",
        companionPlants = listOf("Yardlong String Bean", "Bell Pepper"),
        avoidPlants = listOf("Tomato"),
        commonPests = listOf("Fruit Borer", "Flea Beetle"),
        harvestIndicators = "Glossy deep purple skin, firm flesh",
        description = "Popular lowland vegetable, warm-season heavy cropper."
    ),
    // 3. Bell Pepper
    Crop(
        id = "crop_bell_pepper",
        name = "Bell Pepper",
        localName = "Siling Atsal",
        botanicalName = "Capsicum annuum var. grossum",
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
        seasonality = listOf("YEAR_ROUND", "COOL"),
        imageUrl = "file:///android_asset/metadata/crops_images/bell_pepper.png",
        companionPlants = listOf("Carrot", "Tomato", "Lettuce"),
        avoidPlants = listOf("Fennel"),
        commonPests = listOf("Thrips", "Anthracnose", "Bacterial Spot"),
        harvestIndicators = "Firm, glossy, thick-walled blocky pods",
        description = "High-value crisp sweet pepper suitable for cool highland and protected lowland production."
    ),
    // 4. Cabbage
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
        imageUrl = "file:///android_asset/metadata/crops_images/cabbage.png",
        companionPlants = listOf("Green Onion", "Lettuce"),
        avoidPlants = listOf("Tomato", "Yardlong String Bean"),
        commonPests = listOf("Diamondback Moth", "Cabbage Looper"),
        harvestIndicators = "Firm, solid head formed at plant center",
        description = "Cool-season leafy crop high in Vitamin C."
    ),
    // 5. Pechay
    Crop(
        id = "crop_pechay",
        name = "Pechay",
        localName = "Petsay / Bok Choy",
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
        imageUrl = "file:///android_asset/metadata/crops_images/pechay.png",
        companionPlants = listOf("Green Onion", "Carrot", "Cucumber"),
        avoidPlants = listOf("Strawberry"),
        commonPests = listOf("Flea Beetle", "Cutworm"),
        harvestIndicators = "Crisp upright green petioles at 25-30 days",
        description = "Fastest turnaround leafy brassica widely grown in lowland beds."
    ),
    // 6. Carrot
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
        imageUrl = "file:///android_asset/metadata/crops_images/carrot.png",
        companionPlants = listOf("Lettuce", "Tomato", "Green Onion"),
        avoidPlants = listOf("Parsnip"),
        commonPests = listOf("Root-knot Nematode", "Cutworm"),
        harvestIndicators = "Root crown reaches 1 inch diameter, bright orange",
        description = "Deep loose soil preferred for straight root development."
    ),
    // 7. Yardlong String Bean
    Crop(
        id = "crop_string_beans",
        name = "Yardlong String Bean",
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
        imageUrl = "file:///android_asset/metadata/crops_images/sitaw.png",
        companionPlants = listOf("Cucumber", "Eggplant", "Squash"),
        avoidPlants = listOf("Green Onion"),
        commonPests = listOf("Bean Pod Borer", "Black Aphids"),
        harvestIndicators = "Long tender pods snap easily before seeds bulge",
        description = "Nitrogen-fixing legume vegetable providing soil enrichment."
    ),
    // 8. Lettuce
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
        imageUrl = "file:///android_asset/metadata/crops_images/lettuce.png",
        companionPlants = listOf("Carrot", "Tomato", "Green Onion"),
        avoidPlants = listOf("Celery"),
        commonPests = listOf("Snails and Slugs", "Aphids"),
        harvestIndicators = "Full compact leaf rosette, crisp tender leaves",
        description = "Fast-growing tender salad crop requiring cool moist root zone."
    ),
    // 9. Cucumber
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
        imageUrl = "file:///android_asset/metadata/crops_images/pipino.png",
        companionPlants = listOf("Yardlong String Bean", "Pechay"),
        avoidPlants = listOf("Potato"),
        commonPests = listOf("Cucumber Beetle", "Downy Mildew"),
        harvestIndicators = "Medium size green fruit with firm non-bitter skin",
        description = "Vining fruit crop requiring support structure or ground mulch."
    ),
    // 10. Squash
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
        imageUrl = "file:///android_asset/metadata/crops_images/pumpkin.png",
        companionPlants = listOf("Yardlong String Bean"),
        avoidPlants = listOf("Potato"),
        commonPests = listOf("Squash Bug", "Powdery Mildew"),
        harvestIndicators = "Rind hardens into deep yellowish-tan, stem turns dry",
        description = "Sprawling vine crop rich in Vitamin A and long-storing fruit."
    ),
    // 11. Bitter Gourd
    Crop(
        id = "crop_ampalaya",
        name = "Bitter Gourd",
        localName = "Ampalaya",
        botanicalName = "Momordica charantia",
        category = "FRUIT",
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
        imageUrl = "file:///android_asset/metadata/crops_images/ampalaya.png",
        companionPlants = listOf("Eggplant", "Yardlong String Bean"),
        avoidPlants = emptyList(),
        commonPests = listOf("Fruit Fly", "Powdery Mildew", "Aphids"),
        harvestIndicators = "Firm ribbed fruit before turning yellow/orange",
        description = "High-value medicinal bitter gourd vining crop cultivated with bamboo trellises nationwide."
    ),
    // 12. Potato
    Crop(
        id = "crop_potato",
        name = "Potato",
        localName = "Patatas",
        botanicalName = "Solanum tuberosum",
        category = "TUBER",
        daysToHarvest = 90,
        wateringIntervalDays = 3,
        fertilizeIntervalDays = 14,
        nRatio = 1.0f, pRatio = 2.0f, kRatio = 2.5f,
        optimalPhMin = 5.0f, optimalPhMax = 6.2f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SANDY, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.PEATY),
        pestRiskSeason = listOf("WET"),
        seasonality = listOf("COOL", "HIGHLAND"),
        imageUrl = "file:///android_asset/metadata/crops_images/potato.png",
        companionPlants = listOf("Cabbage", "Yardlong String Bean"),
        avoidPlants = listOf("Tomato", "Eggplant", "Squash", "Cucumber"),
        commonPests = listOf("Late Blight", "Potato Tuber Moth", "Bacterial Wilt"),
        harvestIndicators = "Vines yellow and senesce; firm tuber skin set after 10-day dehaulming",
        description = "Premier cool-climate caloric staple and high-value highland tuber crop."
    ),
    // 13. Green Onion
    Crop(
        id = "crop_green_onion",
        name = "Green Onion",
        localName = "Dahon ng Sibuyas / Scallion",
        botanicalName = "Allium fistulosum",
        category = "BULB",
        daysToHarvest = 60,
        wateringIntervalDays = 1,
        fertilizeIntervalDays = 12,
        nRatio = 2.0f, pRatio = 1.0f, kRatio = 1.5f,
        optimalPhMin = 6.0f, optimalPhMax = 7.0f,
        idealSoils = listOf(SoilType.LOAM),
        suitableSoils = listOf(SoilType.SANDY, SoilType.SILTY),
        toleratedSoils = listOf(SoilType.PEATY),
        pestRiskSeason = listOf("WET"),
        seasonality = listOf("YEAR_ROUND"),
        imageUrl = "file:///android_asset/metadata/crops_images/green_onion.png",
        companionPlants = listOf("Cabbage", "Tomato", "Carrot", "Lettuce"),
        avoidPlants = listOf("Yardlong String Bean"),
        commonPests = listOf("Onion Thrips", "Purple Blotch"),
        harvestIndicators = "Crisp upright green tubular leaves and firm white base stalks",
        description = "High-value bunching allium with non-bulbing stalks and continuous ratoon harvesting."
    )
)
