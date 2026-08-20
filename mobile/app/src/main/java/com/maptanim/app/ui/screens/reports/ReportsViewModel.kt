package com.maptanim.app.ui.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.data.remote.SupabaseClient
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.domain.repository.CropPlotRepository
import com.maptanim.app.domain.repository.CropRepository
import com.maptanim.app.domain.repository.TaskRepository
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CategorySummary(
    val category: String,
    val count: Int,
    val percentage: Float
)

data class ReportsUiState(
    val totalPlots: Int = 0,
    val totalPlantedCrops: Int = 0,
    val completedTasks: Int = 0,
    val pendingTasks: Int = 0,
    val completionRate: Float = 0f,
    val categoryDistribution: List<CategorySummary> = emptyList(),
    val isLoading: Boolean = false
)

class ReportsViewModel(
    private val plotRepository: CropPlotRepository = RepositoryProvider.cropPlotRepository,
    private val taskRepository: TaskRepository = RepositoryProvider.taskRepository,
    private val cropRepository: CropRepository = RepositoryProvider.cropRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        loadReportMetrics()
    }

    private fun loadReportMetrics() {
        val farmerId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: "farmer-1"
        viewModelScope.launch {
            combine(
                plotRepository.observeAllPlotsWithCrop(farmerId),
                taskRepository.observeAllTasks(farmerId),
                cropRepository.observeAllCrops()
            ) { plots, tasks, crops ->
                val activePlots = plots.filter { it.isActive }
                val totalPlots = activePlots.size
                val plantedCrops = activePlots.count { !it.cropName.isNull_or_empty() }

                val completed = tasks.count { it.isCompleted }
                val pending = tasks.count { !it.isCompleted }
                val totalTasks = tasks.size
                val compRate = if (totalTasks > 0) (completed.toFloat() / totalTasks) * 100f else 100f

                val categories = activePlots.mapNotNull { plot ->
                    val crop = crops.firstOrNull { it.name.equals(plot.cropName, ignoreCase = true) }
                    crop?.category ?: "FRUIT"
                }.groupBy { it }

                val catSummaries = categories.map { (cat, list) ->
                    CategorySummary(
                        category = cat,
                        count = list.size,
                        percentage = if (plantedCrops > 0) (list.size.toFloat() / plantedCrops) * 100f else 0f
                    )
                }.sortedByDescending { it.count }

                ReportsUiState(
                    totalPlots = totalPlots,
                    totalPlantedCrops = plantedCrops,
                    completedTasks = completed,
                    pendingTasks = pending,
                    completionRate = compRate,
                    categoryDistribution = catSummaries
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun String?.isNull_or_empty(): Boolean = this == null || this.trim().isEmpty()
}
