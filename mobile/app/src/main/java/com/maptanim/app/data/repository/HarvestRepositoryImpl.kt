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

    override suspend fun recordHarvest(record: HarvestRecord) {
        harvestDao.upsertHarvest(record.toEntity())

        try {
            remoteDataSource.recordHarvest(record.toDto())
        } catch (e: Exception) {
            e.printStackTrace()
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
