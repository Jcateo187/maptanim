package com.maptanim.app.data.repository

import com.maptanim.app.data.local.dao.HarvestDao
import com.maptanim.app.data.local.entity.toDomain
import com.maptanim.app.data.local.entity.toEntity
import com.maptanim.app.data.remote.HarvestRemoteDataSource
import com.maptanim.app.data.remote.dto.HarvestRecordDto
import com.maptanim.app.domain.model.HarvestRecord
import com.maptanim.app.domain.repository.HarvestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HarvestRepositoryImpl(
    private val harvestDao: HarvestDao,
    private val remoteDataSource: HarvestRemoteDataSource = HarvestRemoteDataSource()
) : HarvestRepository {

    override fun observeHarvestRecords(farmId: String): Flow<List<HarvestRecord>> {
        return harvestDao.observeHarvestRecords(farmId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeAllHarvestRecords(): Flow<List<HarvestRecord>> {
        return harvestDao.observeAllHarvestRecords().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun recordHarvest(record: HarvestRecord) {
        harvestDao.upsertHarvest(record.toEntity())

        val dto = record.toDto()
        val result = remoteDataSource.recordHarvest(dto)
        if (result.isFailure) {
            try {
                val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
                val payload = json.encodeToString(HarvestRecordDto.serializer(), dto)
                RepositoryProvider.syncRepository.enqueueSyncItem(
                    tableName = "harvest_records",
                    recordId = dto.id,
                    operation = "INSERT",
                    payload = payload
                )
            } catch (_: Exception) {}
        }
    }

    suspend fun fetchFromRemote(farmId: String) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val result = remoteDataSource.getHarvestsByFarm(farmId)
        result.getOrNull()?.forEach { dto ->
            harvestDao.upsertHarvest(dto.toEntity())
        }
    }
}

private fun HarvestRecord.toDto() = HarvestRecordDto(
    id = id,
    farmId = farmId,
    plotId = plotId,
    farmName = farmName,
    plotLabel = plotLabel,
    cropName = cropName,
    cropVariety = cropVariety,
    plantedDate = plantedDate,
    harvestedAt = harvestedAt,
    harvestedDate = try { harvestedAt.take(10) } catch (_: Exception) { null },
    growingDurationDays = growingDurationDays,
    yieldKg = yieldKg,
    qualityRating = qualityRating,
    notes = notes
)

private fun HarvestRecordDto.toEntity() = com.maptanim.app.data.local.entity.HarvestEntity(
    id = id,
    plotId = plotId ?: "",
    farmId = farmId,
    farmName = farmName ?: "My Farm",
    plotLabel = plotLabel ?: "Plot 1",
    cropName = cropName,
    cropVariety = cropVariety,
    plantedDate = plantedDate,
    harvestedAt = harvestedAt ?: java.time.Instant.now().toString(),
    growingDurationDays = growingDurationDays,
    yieldKg = yieldKg,
    qualityRating = qualityRating,
    notes = notes
)

