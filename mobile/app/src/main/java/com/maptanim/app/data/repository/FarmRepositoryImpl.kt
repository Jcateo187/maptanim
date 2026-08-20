package com.maptanim.app.data.repository

import com.maptanim.app.data.local.dao.FarmDao
import com.maptanim.app.data.local.entity.toDomain
import com.maptanim.app.data.local.entity.toEntity
import com.maptanim.app.data.remote.FarmRemoteRepository
import com.maptanim.app.data.remote.dto.FarmDto
import com.maptanim.app.domain.model.Farm
import com.maptanim.app.domain.repository.FarmRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FarmRepositoryImpl(
    private val farmDao: FarmDao? = null,
    private val remoteRepository: FarmRemoteRepository = FarmRemoteRepository()
) : FarmRepository {

    private val farmsCache = MutableStateFlow<List<Farm>>(emptyList())

    override fun observeFarms(farmerId: String): Flow<List<Farm>> {
        return farmDao?.observeFarmsByFarmerId(farmerId)?.map { entities ->
            entities.map { it.toDomain() }
        } ?: farmsCache.map { farms ->
            farms.filter { it.farmerId == farmerId }
        }
    }

    override fun observeFarm(farmId: String): Flow<Farm?> {
        return farmDao?.observeFarmById(farmId)?.map { entity ->
            entity?.toDomain()
        } ?: farmsCache.map { farms ->
            farms.firstOrNull { it.id == farmId }
        }
    }

    override suspend fun upsertFarm(farm: Farm) = withContext(Dispatchers.IO) {
        farmDao?.upsertFarm(farm.toEntity())
        remoteRepository.upsertFarm(farm.toDto())
        farmsCache.value = farmsCache.value.filter { it.id != farm.id } + farm
    }

    override suspend fun deleteFarm(farmId: String) = withContext(Dispatchers.IO) {
        farmDao?.deleteFarm(farmId)
        farmsCache.value = farmsCache.value.filter { it.id != farmId }
    }

    suspend fun fetchFromRemote(farmerId: String) = withContext(Dispatchers.IO) {
        val result = remoteRepository.getFarmsForFarmer(farmerId)
        result.getOrNull()?.let { dtos ->
            val farms = dtos.map { it.toDomain() }
            farmDao?.upsertFarms(farms.map { it.toEntity() })
            farmsCache.value = farms
        }
    }
}

private fun Farm.toDto() = FarmDto(
    id = id,
    farmer_id = farmerId,
    farm_name = farmName,
    created_at = createdAt,
    updated_at = updatedAt
)

private fun FarmDto.toDomain(): Farm = Farm(
    id = id,
    farmerId = farmer_id,
    farmName = farm_name,
    createdAt = created_at ?: "",
    updatedAt = updated_at ?: ""
)


