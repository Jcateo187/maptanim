package com.maptanim.app.data.repository

import com.maptanim.app.data.local.dao.CropZoneDao
import com.maptanim.app.data.local.entity.toDomain
import com.maptanim.app.data.local.entity.toEntity
import com.maptanim.app.data.remote.CropZoneRemoteDataSource
import com.maptanim.app.data.remote.dto.CropZoneDto
import com.maptanim.app.domain.model.CropZone
import com.maptanim.app.domain.repository.CropZoneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CropZoneRepositoryImpl(
    private val initialDao: CropZoneDao? = null,
    private val remoteDataSource: CropZoneRemoteDataSource = CropZoneRemoteDataSource()
) : CropZoneRepository {

    private val cropZoneDao: CropZoneDao?
        get() = initialDao ?: RepositoryProvider.getDatabase()?.cropZoneDao()

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

    override suspend fun saveZones(zones: List<CropZone>): Unit = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        cropZoneDao?.upsertZones(zones.map { it.toEntity() })
        zones.groupBy { it.plotId }.forEach { (plotId, plotZones) ->
            inMemoryCache[plotId] = plotZones
        }

        // Synchronize with remote Supabase crop_zones
        try {
            val dtos = zones.map { it.toDto() }
            remoteDataSource.upsertZones(dtos)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Unit
    }

    override suspend fun deleteZone(zoneId: String): Unit = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        cropZoneDao?.deleteZone(zoneId)
        inMemoryCache.keys.forEach { plotId ->
            inMemoryCache[plotId] = inMemoryCache[plotId]?.filter { it.id != zoneId } ?: emptyList()
        }

        // Delete from remote Supabase crop_zones
        try {
            remoteDataSource.deleteZone(zoneId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Unit
    }

    suspend fun fetchFromRemote(plotIds: List<String>): List<CropZone> = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        if (plotIds.isEmpty()) return@withContext emptyList()
        val result = remoteDataSource.getZonesByPlotIds(plotIds)
        val zones = result.getOrNull()?.map { it.toDomain() } ?: emptyList()
        if (zones.isNotEmpty()) {
            cropZoneDao?.upsertZones(zones.map { it.toEntity() })
            zones.groupBy { it.plotId }.forEach { (plotId, plotZones) ->
                inMemoryCache[plotId] = plotZones
            }
        }
        zones
    }
}

private fun CropZone.toDto() = CropZoneDto(
    id = id,
    plot_id = plotId,
    crop_name = cropName,
    crop_id = cropId,
    offset_x = offsetX,
    offset_y = offsetY,
    width_m = widthM,
    height_m = heightM,
    spacing_m = spacingM,
    created_at = createdAt.ifBlank { null },
    updated_at = updatedAt.ifBlank { null }
)

private fun CropZoneDto.toDomain() = CropZone(
    id = id,
    plotId = plot_id,
    cropName = crop_name,
    cropId = crop_id,
    offsetX = offset_x,
    offsetY = offset_y,
    widthM = width_m,
    heightM = height_m,
    spacingM = spacing_m,
    createdAt = created_at ?: "",
    updatedAt = updated_at ?: ""
)
