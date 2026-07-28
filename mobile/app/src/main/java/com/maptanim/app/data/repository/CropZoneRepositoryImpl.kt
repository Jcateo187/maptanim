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

    override fun observeZonesByBedId(bedId: String): Flow<List<CropZone>> {
        return cropZoneDao?.observeZonesByBedId(bedId)?.map { entities ->
            entities.map { it.toDomain() }
        } ?: kotlinx.coroutines.flow.flowOf(inMemoryCache[bedId] ?: emptyList())
    }

    override fun observeZonesByBedIds(bedIds: List<String>): Flow<List<CropZone>> {
        return cropZoneDao?.observeZonesByBedIds(bedIds)?.map { entities ->
            entities.map { it.toDomain() }
        } ?: kotlinx.coroutines.flow.flowOf(inMemoryCache.filterKeys { it in bedIds }.values.flatten())
    }

    override suspend fun saveZones(zones: List<CropZone>) {
        cropZoneDao?.upsertZones(zones.map { it.toEntity() })
        zones.groupBy { it.bedId }.forEach { (bedId, bedZones) ->
            inMemoryCache[bedId] = bedZones
        }
    }

    override suspend fun deleteZone(zoneId: String) {
        cropZoneDao?.deleteZone(zoneId)
        inMemoryCache.keys.forEach { bedId ->
            inMemoryCache[bedId] = inMemoryCache[bedId]?.filter { it.id != zoneId } ?: emptyList()
        }
    }
}
