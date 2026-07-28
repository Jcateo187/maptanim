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

    private val cropsCache = MutableStateFlow<List<Crop>>(emptyList())

    override fun observeAllCrops(): Flow<List<Crop>> {
        return cropsCache.map { it }
    }

    override fun observeCropByName(name: String): Flow<Crop?> {
        return cropsCache.map { crops ->
            crops.firstOrNull { it.name.equals(name, ignoreCase = true) }
        }
    }

    override suspend fun upsertCrops(crops: List<Crop>) {
        cropsCache.value = crops
    }

    suspend fun fetchFromRemote() {
        val result = remoteRepository.getAllCrops()
        result.getOrNull()?.let { dtos ->
            cropsCache.value = dtos.map { it.toDomain() }
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
