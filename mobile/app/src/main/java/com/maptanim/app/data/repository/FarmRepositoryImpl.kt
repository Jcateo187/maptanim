package com.maptanim.app.data.repository

import com.maptanim.app.domain.model.Farm
import com.maptanim.app.domain.repository.FarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FarmRepositoryImpl(
    private val remoteRepository: FarmRemoteRepository = FarmRemoteRepository()
) : FarmRepository {

    private val farmsCache = MutableStateFlow<List<Farm>>(emptyList())

    override fun observeFarms(farmerId: String): Flow<List<Farm>> {
        return farmsCache.map { farms ->
            farms.filter { it.farmerId == farmerId }
        }
    }

    override fun observeFarm(farmId: String): Flow<Farm?> {
        return farmsCache.map { farms ->
            farms.firstOrNull { it.id == farmId }
        }
    }

    override suspend fun upsertFarm(farm: Farm) {
        farmsCache.value = farmsCache.value.filter { it.id != farm.id } + farm
    }

    override suspend fun deleteFarm(farmId: String) {
        farmsCache.value = farmsCache.value.filter { it.id != farmId }
    }

    suspend fun fetchFromRemote(farmerId: String) {
        val result = remoteRepository.getFarmsForFarmer(farmerId)
        result.getOrNull()?.let { dtos ->
            farmsCache.value = dtos.map { it.toDomain() }
        }
    }
}

private fun FarmDto.toDomain(): Farm = Farm(
    id = id,
    farmerId = farmer_id,
    farmName = farm_name,
    location = location,
    totalAreaSqm = total_area_sqm,
    createdAt = created_at ?: "",
    updatedAt = updated_at ?: ""
)
