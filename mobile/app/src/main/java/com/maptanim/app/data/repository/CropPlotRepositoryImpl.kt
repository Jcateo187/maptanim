package com.maptanim.app.data.repository

import com.maptanim.app.data.local.dao.CropPlotDao
import com.maptanim.app.data.local.entity.toDomain
import com.maptanim.app.data.local.entity.toEntity
import com.maptanim.app.data.remote.CropPlotRemoteDataSource
import com.maptanim.app.data.remote.dto.CropPlotDto
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.ManagementStage
import com.maptanim.app.domain.model.SoilType
import com.maptanim.app.domain.repository.CropPlotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class CropPlotRepositoryImpl(
    private val initialDao: CropPlotDao? = null,
    private val remoteDataSource: CropPlotRemoteDataSource = CropPlotRemoteDataSource()
) : CropPlotRepository {

    private val cropPlotDao: CropPlotDao?
        get() = initialDao ?: RepositoryProvider.getDatabase()?.cropPlotDao()

    private val inMemoryCache = MutableStateFlow<Map<String, List<CropPlot>>>(emptyMap())

    override fun observePlots(farmId: String): Flow<List<CropPlot>> {
        return cropPlotDao?.observePlotsByFarmId(farmId)?.map { entities ->
            entities.map { it.toDomain() }
        } ?: inMemoryCache.map { map -> map[farmId] ?: emptyList() }
    }

    override fun observePlot(plotId: String): Flow<CropPlot?> {
        return cropPlotDao?.observePlotById(plotId)?.map { entity ->
            entity?.toDomain()
        } ?: inMemoryCache.map { map -> map.values.flatten().firstOrNull { it.id == plotId } }
    }

    override fun observeAllPlotsWithCrop(farmerId: String): Flow<List<CropPlot>> {
        return cropPlotDao?.observeAllPlotsWithCrop()?.map { entities ->
            entities.map { it.toDomain() }.filter { !it.cropName.isNullOrEmpty() }
        } ?: inMemoryCache.map { map -> map.values.flatten().filter { !it.cropName.isNullOrEmpty() } }
    }

    override suspend fun savePlots(plots: List<CropPlot>) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val resolvedFarmId = plots.firstOrNull()?.farmId
            ?: RepositoryProvider.getDatabase()?.farmDao()?.getActiveFarm()?.id
            ?: "farm-default"
        val plotIds = plots.map { it.id }
        if (plotIds.isNotEmpty()) {
            cropPlotDao?.deletePlotsNotInList(resolvedFarmId, plotIds)
        } else {
            cropPlotDao?.deletePlotsByFarmId(resolvedFarmId)
        }
        cropPlotDao?.upsertPlots(plots.map { it.toEntity() })
        
        val cached = inMemoryCache.value[resolvedFarmId] ?: emptyList()
        // Avoid network traffic: only push plots that were actually added or modified
        val changedPlots = plots.filter { p ->
            val prev = cached.firstOrNull { it.id == p.id }
            prev == null || prev != p
        }

        // Guest accounts operate local-only to prevent RLS violations and queue pollution
        if (!resolvedFarmId.contains("guest", ignoreCase = true)) {
            changedPlots.forEach { plot ->
                try {
                    val dto = plot.toDto()
                    val res = remoteDataSource.upsertPlot(dto)
                    if (res.isFailure) {
                        val jsonPayload = kotlinx.serialization.json.Json.encodeToString(CropPlotDto.serializer(), dto)
                        RepositoryProvider.syncRepository.enqueueSyncItem(
                            tableName = "crop_plots",
                            recordId = plot.id,
                            operation = "UPSERT",
                            payload = jsonPayload
                        )
                    }
                } catch (_: Exception) {
                    try {
                        val jsonPayload = kotlinx.serialization.json.Json.encodeToString(CropPlotDto.serializer(), plot.toDto())
                        RepositoryProvider.syncRepository.enqueueSyncItem(
                            tableName = "crop_plots",
                            recordId = plot.id,
                            operation = "UPSERT",
                            payload = jsonPayload
                        )
                    } catch (_: Exception) {}
                }
            }
        }

        val current = inMemoryCache.value.toMutableMap()
        current[resolvedFarmId] = plots
        inMemoryCache.value = current
    }

    override suspend fun deletePlot(plotId: String) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        cropPlotDao?.deletePlot(plotId)
        val current = inMemoryCache.value.toMutableMap()
        current.keys.forEach { farmId ->
            current[farmId] = current[farmId]?.filter { it.id != plotId } ?: emptyList()
        }
        inMemoryCache.value = current

        if (plotId.contains("guest", ignoreCase = true) || plotId.isBlank()) {
            return@withContext
        }

        try {
            val res = remoteDataSource.deletePlot(plotId)
            if (res.isFailure) {
                RepositoryProvider.syncRepository.enqueueSyncItem(
                    tableName = "crop_plots",
                    recordId = plotId,
                    operation = "DELETE",
                    payload = plotId
                )
            }
        } catch (_: Exception) {
            RepositoryProvider.syncRepository.enqueueSyncItem(
                tableName = "crop_plots",
                recordId = plotId,
                operation = "DELETE",
                payload = plotId
            )
        }
    }

    override suspend fun upsertPlot(plot: CropPlot) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        cropPlotDao?.upsertPlot(plot.toEntity())
        if (!plot.farmId.contains("guest", ignoreCase = true) && plot.id.isNotBlank() && plot.farmId.isNotBlank()) {
            val dto = plot.toDto()
            val res = try { remoteDataSource.upsertPlot(dto) } catch (e: Exception) { Result.failure(e) }
            if (res.isFailure) {
                try {
                    RepositoryProvider.syncRepository.enqueueSyncItem(
                        tableName = "crop_plots",
                        recordId = plot.id,
                        operation = "UPSERT",
                        payload = kotlinx.serialization.json.Json.encodeToString(CropPlotDto.serializer(), dto)
                    )
                } catch (_: Exception) {}
            }
        }

        val current = inMemoryCache.value.toMutableMap()
        val list = (current[plot.farmId] ?: emptyList()).filter { it.id != plot.id } + plot
        current[plot.farmId] = list
        inMemoryCache.value = current
    }

    override suspend fun recordHarvest(plotId: String, yieldKg: Float?, notes: String?, isFinalHarvest: Boolean) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val currentList = inMemoryCache.value.values.flatten()
        val plot = currentList.firstOrNull { it.id == plotId }
            ?: cropPlotDao?.observePlotById(plotId)?.firstOrNull()?.toDomain()

        if (plot != null && !plot.cropName.isNullOrEmpty()) {
            val cropName = plot.cropName ?: "Vegetable"
            val cropVariety = plot.cropVariety
            val plantedDate = plot.plantedDate
            val plotLabel = plot.plotLabel
            val farmId = plot.farmId
            val currentYield = yieldKg ?: 0f

            // Calculate total growing duration in real days
            val growingDurationDays = if (!plantedDate.isNullOrBlank()) {
                try {
                    val pDate = if (plantedDate.contains("T")) {
                        java.time.ZonedDateTime.parse(plantedDate).toLocalDate()
                    } else {
                        java.time.LocalDate.parse(plantedDate.take(10))
                    }
                    java.time.temporal.ChronoUnit.DAYS.between(pDate, java.time.LocalDate.now()).toInt().coerceAtLeast(1)
                } catch (e: Exception) {
                    30
                }
            } else {
                30
            }

            val farmDao = RepositoryProvider.getDatabase()?.farmDao()
            val activeFarm = farmDao?.getFarmById(farmId) ?: farmDao?.getActiveFarm()
            val resolvedFarmName = activeFarm?.farmName ?: "My Farm"

            val harvestRecord = com.maptanim.app.domain.model.HarvestRecord(
                id = java.util.UUID.randomUUID().toString(),
                plotId = plot.id,
                farmId = farmId,
                farmName = resolvedFarmName,
                plotLabel = plotLabel,
                cropName = cropName,
                cropVariety = cropVariety,
                plantedDate = plantedDate,
                harvestedAt = java.time.ZonedDateTime.now().toString(),
                growingDurationDays = growingDurationDays,
                yieldKg = currentYield,
                qualityRating = 5,
                notes = notes,
                isFinalHarvest = isFinalHarvest
            )

            // Save complete harvest record under farm history
            try {
                RepositoryProvider.harvestRepository.recordHarvest(harvestRecord)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val nowStr = java.time.ZonedDateTime.now().toString()
            if (isFinalHarvest) {
                // Final harvest: remove crop from active bed, save to rotation history
                val updatedHistory = (plot.previousCropsHistory + cropName).distinct()
                val clearedPlot = plot.copy(
                    cropName = null,
                    cropId = null,
                    cropVariety = null,
                    plantedDate = null,
                    currentStage = com.maptanim.app.domain.model.ManagementStage.PREPARATION,
                    harvestCount = plot.harvestCount + 1,
                    totalYieldKg = plot.totalYieldKg + currentYield,
                    previousCropsHistory = updatedHistory,
                    updatedAt = nowStr
                )
                upsertPlot(clearedPlot)
            } else {
                // Continuous multi-pick: keep crop active, increment harvest count and total yield
                val continuousPlot = plot.copy(
                    currentStage = com.maptanim.app.domain.model.ManagementStage.HARVEST,
                    harvestCount = plot.harvestCount + 1,
                    totalYieldKg = plot.totalYieldKg + currentYield,
                    updatedAt = nowStr
                )
                upsertPlot(continuousPlot)
            }
        }
    }

    override suspend fun renamePlot(plotId: String, newLabel: String) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val nowStr = java.time.Instant.now().toString()
        cropPlotDao?.updatePlotLabel(plotId, newLabel, nowStr)
        val currentPlot = cropPlotDao?.getPlotById(plotId)?.toDomain()
            ?: inMemoryCache.value.values.flatten().firstOrNull { it.id == plotId }
        if (currentPlot != null) {
            val updated = currentPlot.copy(plotLabel = newLabel, updatedAt = nowStr)
            upsertPlot(updated)
        }
    }

    override suspend fun setPlotArchived(plotId: String, isArchived: Boolean, archiveTimestamp: String?) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val nowStr = java.time.Instant.now().toString()
        val currentPlot = cropPlotDao?.getPlotById(plotId)?.toDomain()
            ?: inMemoryCache.value.values.flatten().firstOrNull { it.id == plotId }
        if (currentPlot != null) {
            val updatedNotes = if (isArchived) {
                "DELETED_AT:${archiveTimestamp ?: nowStr}"
            } else {
                if (currentPlot.notes?.startsWith("DELETED_AT:") == true || currentPlot.notes?.startsWith("ARCHIVED_AT:") == true) null else currentPlot.notes
            }
            val updated = currentPlot.copy(
                isActive = !isArchived,
                notes = updatedNotes,
                updatedAt = nowStr
            )
            cropPlotDao?.updatePlotActiveStatus(plotId, !isArchived, nowStr, updatedNotes)
            upsertPlot(updated)
        }
    }

    override suspend fun purgeExpiredArchivedPlots(olderThanDays: Int) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val archivedEntities = cropPlotDao?.getAllArchivedPlots() ?: emptyList()
        val now = java.time.Instant.now()
        archivedEntities.forEach { entity ->
            val dateStr = if (entity.notes?.startsWith("DELETED_AT:") == true) {
                entity.notes.removePrefix("DELETED_AT:")
            } else if (entity.notes?.startsWith("ARCHIVED_AT:") == true) {
                entity.notes.removePrefix("ARCHIVED_AT:")
            } else {
                entity.updatedAt
            }
            try {
                val deletedInstant = if (dateStr.contains("T")) {
                    java.time.Instant.parse(dateStr)
                } else {
                    java.time.LocalDate.parse(dateStr.take(10)).atStartOfDay(java.time.ZoneOffset.UTC).toInstant()
                }
                val days = java.time.temporal.ChronoUnit.DAYS.between(deletedInstant, now)
                if (days >= olderThanDays) {
                    deletePlot(entity.id)
                }
            } catch (_: Exception) {}
        }
    }

    suspend fun fetchFromRemote(farmId: String): List<CropPlot> = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val result = remoteDataSource.getPlotsByFarm(farmId)
        val plots = result.getOrNull()?.map { it.toDomain() } ?: emptyList()
        if (plots.isNotEmpty()) {
            cropPlotDao?.upsertPlots(plots.map { it.toEntity() })
            val current = inMemoryCache.value.toMutableMap()
            current[farmId] = plots
            inMemoryCache.value = current
        }
        plots
    }
}

private fun CropPlot.toDto() = CropPlotDto(
    id = id,
    farm_id = farmId,
    plot_label = plotLabel,
    crop_name = cropName,
    crop_id = cropId,
    crop_variety = cropVariety,
    soil_type = soilType.name,
    pos_x = posX,
    pos_y = posY,
    width_m = widthM,
    height_m = heightM,
    rotation_deg = rotationDeg,
    notes = notes,
    planted_date = plantedDate,
    is_active = isActive,
    current_stage = currentStage.name,
    harvest_count = harvestCount,
    total_yield_kg = totalYieldKg,
    previous_crops_history = previousCropsHistory.joinToString(","),
    created_at = createdAt,
    updated_at = updatedAt
)

private fun CropPlotDto.toDomain() = CropPlot(
    id = id,
    farmId = farm_id,
    plotLabel = plot_label,
    cropName = crop_name,
    cropId = crop_id,
    cropVariety = crop_variety,
    soilType = try { SoilType.valueOf(soil_type) } catch (e: Exception) { SoilType.LOAM },
    posX = pos_x,
    posY = pos_y,
    widthM = width_m,
    heightM = height_m,
    rotationDeg = rotation_deg,
    plantedDate = planted_date,
    isActive = is_active,
    notes = notes,
    createdAt = created_at ?: "",
    updatedAt = updated_at ?: "",
    currentStage = try { ManagementStage.valueOf(current_stage) } catch (e: Exception) { ManagementStage.PREPARATION },
    harvestCount = harvest_count,
    totalYieldKg = total_yield_kg,
    previousCropsHistory = if (previous_crops_history.isBlank()) emptyList() else previous_crops_history.split(",")
)
