package com.maptanim.app.data.repository

import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.SoilType
import com.maptanim.app.domain.repository.CropPlotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class CropPlotRepositoryImpl : CropPlotRepository {

    private val plotsCache = MutableStateFlow<Map<String, List<CropPlot>>>(
        mapOf("farm-1" to defaultInitialPlots)
    )

    override fun observePlots(farmId: String): Flow<List<CropPlot>> {
        return plotsCache.map { map ->
            map[farmId] ?: emptyList()
        }
    }

    override fun observePlot(plotId: String): Flow<CropPlot?> {
        return plotsCache.map { map ->
            map.values.flatten().firstOrNull { it.id == plotId }
        }
    }

    override fun observeAllPlotsWithCrop(farmerId: String): Flow<List<CropPlot>> {
        return plotsCache.map { map ->
            map.values.flatten().filter { it.cropName != null }
        }
    }

    override suspend fun savePlots(plots: List<CropPlot>) {
        val farmId = plots.firstOrNull()?.farmId ?: "farm-1"
        val current = plotsCache.value.toMutableMap()
        current[farmId] = plots
        plotsCache.value = current
    }

    override suspend fun deletePlot(plotId: String) {
        val current = plotsCache.value.toMutableMap()
        current.keys.forEach { farmId ->
            current[farmId] = current[farmId]?.filter { it.id != plotId } ?: emptyList()
        }
        plotsCache.value = current
    }

    override suspend fun upsertPlot(plot: CropPlot) {
        val current = plotsCache.value.toMutableMap()
        val list = (current[plot.farmId] ?: emptyList()).filter { it.id != plot.id } + plot
        current[plot.farmId] = list
        plotsCache.value = current
    }
}

internal val defaultInitialPlots = listOf(
    CropPlot(
        id = "plot-1",
        farmId = "farm-1",
        plotLabel = "PLOT 1",
        cropName = "Carrot",
        cropId = "carrot",
        soilType = SoilType.LOAM,
        posX = 5.0f,
        posY = 6.0f,
        widthM = 3.0f,
        heightM = 2.0f,
        rotationDeg = 0f,
        plantedDate = "2026-07-01T00:00:00Z",
        isActive = true,
        notes = null,
        createdAt = "2026-07-01T00:00:00Z",
        updatedAt = "2026-07-01T00:00:00Z"
    ),
    CropPlot(
        id = "plot-2",
        farmId = "farm-1",
        plotLabel = "PLOT 2",
        cropName = "String Beans",
        cropId = "stringbeans",
        soilType = SoilType.LOAM,
        posX = 12.0f,
        posY = 8.0f,
        widthM = 4.0f,
        heightM = 2.0f,
        rotationDeg = 0f,
        plantedDate = null,
        isActive = true,
        notes = null,
        createdAt = "2026-07-01T00:00:00Z",
        updatedAt = "2026-07-01T00:00:00Z"
    )
)
