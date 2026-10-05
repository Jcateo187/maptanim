package com.maptanim.app.data.repository

import com.maptanim.app.data.local.dao.CropPlotDao
import com.maptanim.app.data.local.entity.toDomain
import com.maptanim.app.data.local.entity.toEntity
import com.maptanim.app.data.remote.CropPlotRemoteDataSource
import com.maptanim.app.data.remote.dto.CropPlotDto
import com.maptanim.app.domain.model.CropPlot
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
        
        // Sync to remote
        plots.forEach { plot ->
            remoteDataSource.upsertPlot(plot.toDto())
        }

        val current = inMemoryCache.value.toMutableMap()
        current[resolvedFarmId] = plots
        inMemoryCache.value = current
    }

    override suspend fun deletePlot(plotId: String) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        cropPlotDao?.deletePlot(plotId)
        remoteDataSource.deletePlot(plotId)

        val current = inMemoryCache.value.toMutableMap()
        current.keys.forEach { farmId ->
            current[farmId] = current[farmId]?.filter { it.id != plotId } ?: emptyList()
        }
        inMemoryCache.value = current
    }

    override suspend fun upsertPlot(plot: CropPlot) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        cropPlotDao?.upsertPlot(plot.toEntity())
        remoteDataSource.upsertPlot(plot.toDto())

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

            // Calculate total growing duration (days or sim seconds)
            val isSim = cropName.lowercase().contains("ampalaya") || cropVariety?.contains("10s", ignoreCase = true) == true
            val growingDurationDays = if (isSim) {
                val plantedMs = try {
                    java.time.ZonedDateTime.parse(plantedDate).toInstant().toEpochMilli()
                } catch (e: Exception) { 0L }
                val elapsedMs = (System.currentTimeMillis() - plantedMs).coerceAtLeast(0L)
                (elapsedMs / 1000L).toInt()
            } else if (!plantedDate.isNullOrBlank()) {
                try {
                    val pDate = java.time.LocalDate.parse(plantedDate.take(10))
                    java.time.temporal.ChronoUnit.DAYS.between(pDate, java.time.LocalDate.now()).toInt().coerceAtLeast(0)
                } catch (e: Exception) { 30 }
            } else { 30 }

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

    suspend fun fetchFromRemote(farmId: String) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val result = remoteDataSource.getPlotsByFarm(farmId)
        result.getOrNull()?.let { dtos ->
            val plots = dtos.map { it.toDomain() }
            cropPlotDao?.upsertPlots(plots.map { it.toEntity() })
            val current = inMemoryCache.value.toMutableMap()
            current[farmId] = plots
            inMemoryCache.value = current
        }
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
    updatedAt = updated_at ?: ""
)
