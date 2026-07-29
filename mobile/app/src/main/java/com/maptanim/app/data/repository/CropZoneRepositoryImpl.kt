package com.maptanim.app.data.repository

import com.maptanim.app.data.local.dao.CropZoneDao
import com.maptanim.app.data.local.entity.toDomain
import com.maptanim.app.data.local.entity.toEntity
import com.maptanim.app.domain.model.CropZone
import com.maptanim.app.domain.repository.CropZoneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CropZoneRepositoryImpl(
    private val cropZoneDao: CropZoneDao? = null
) : CropZoneRepository {

    private val inMemoryCache = mutableMapOf<String, List<CropZone>>()

    override fun observeZonesByPlotId(plotId: String): Flow<List<CropZone>> {
        return cropZoneDao?.observeZonesByPlotId(plotId)?.map { entities ->
            entities.map { it.toDomain() }
        } ?: kotlinx.coroutines.flow.flowOf(inMemoryCache[plotId] ?: emptyList())
    }

    override fun observeZonesByPlotIds(plotIds: List<String>): Flow<List<CropZone>> {
        return cropZoneDao?.observeZonesByPlotIds(plotIds)?.map { entities ->
            entities.map { it.toDomain() }
        } ?: kotlinx.coroutines.flow.flowOf(inMemoryCache.filterKeys { it in plotIds }.values.flatten())
    }

    override suspend fun saveZones(zones: List<CropZone>) {
        cropZoneDao?.upsertZones(zones.map { it.toEntity() })
        zones.groupBy { it.plotId }.forEach { (plotId, plotZones) ->
            inMemoryCache[plotId] = plotZones
        }
    }

    override suspend fun deleteZone(zoneId: String) {
        cropZoneDao?.deleteZone(zoneId)
        inMemoryCache.keys.forEach { plotId ->
            inMemoryCache[plotId] = inMemoryCache[plotId]?.filter { it.id != zoneId } ?: emptyList()
        }
    }
}
