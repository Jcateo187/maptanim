package com.maptanim.app.data.repository

import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.SoilType
import com.maptanim.app.domain.repository.CropPlotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class CropPlotRepositoryImpl : CropPlotRepository {

    private val plotsCache = MutableStateFlow<Map<String, List<CropPlot>>>(emptyMap())

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
        val farmId = plots.firstOrNull()?.farmId ?: return
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
