package com.maptanim.app.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.data.remote.SupabaseClient
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.domain.model.Crop
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.FarmTask
import com.maptanim.app.domain.repository.CropPlotRepository
import com.maptanim.app.domain.repository.CropRepository
import com.maptanim.app.domain.repository.TaskRepository
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

data class CropTimelineEntry(
    val plot: CropPlot,
    val crop: Crop?,
    val plantedDate: LocalDate,
    val expectedHarvestDate: LocalDate,
    val daysRemaining: Int
)

data class CalendarUiState(
    val currentMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val tasksByDate: Map<LocalDate, List<FarmTask>> = emptyMap(),
    val timelineEntries: List<CropTimelineEntry> = emptyList(),
    val selectedDayTasks: List<FarmTask> = emptyList(),
    val selectedDayHarvests: List<CropTimelineEntry> = emptyList(),
    val isLoading: Boolean = false
)

class CalendarViewModel(
    private val taskRepository: TaskRepository = RepositoryProvider.taskRepository,
    private val plotRepository: CropPlotRepository = RepositoryProvider.cropPlotRepository,
    private val cropRepository: CropRepository = RepositoryProvider.cropRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val farmerId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: "farmer-1"
        viewModelScope.launch {
            combine(
                taskRepository.observeAllTasks(farmerId),
                plotRepository.observeAllPlotsWithCrop(farmerId),
                cropRepository.observeAllCrops()
            ) { tasks, plots, crops ->
                val taskMap = tasks.mapNotNull { task ->
                    try {
                        val date = LocalDate.parse(task.dueDate.take(10))
                        date to task
                    } catch (e: Exception) {
                        null
                    }
                }.groupBy({ it.first }, { it.second })

                val timelines = plots.mapNotNull { plot ->
                    val crop = crops.firstOrNull { it.name.equals(plot.cropName, ignoreCase = true) }
                    val plantedStr = plot.plantedDate ?: return@mapNotNull null
                    try {
                        val plantedDate = LocalDate.parse(plantedStr.take(10))
                        val daysToHarvest = crop?.daysToHarvest ?: 60
                        val harvestDate = plantedDate.plusDays(daysToHarvest.toLong())
                        val daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), harvestDate).toInt()
                        CropTimelineEntry(
                            plot = plot,
                            crop = crop,
                            plantedDate = plantedDate,
                            expectedHarvestDate = harvestDate,
                            daysRemaining = daysRemaining
                        )
                    } catch (e: Exception) {
                        null
                    }
                }

                Triple(taskMap, timelines, plots)
            }.collect { (taskMap, timelines, _) ->
                val selDate = _uiState.value.selectedDate
                _uiState.update { state ->
                    state.copy(
                        tasksByDate = taskMap,
                        timelineEntries = timelines,
                        selectedDayTasks = taskMap[selDate] ?: emptyList(),
                        selectedDayHarvests = timelines.filter { it.expectedHarvestDate == selDate || it.plantedDate == selDate }
                    )
                }
            }
        }
    }

    fun selectDate(date: LocalDate) {
        _uiState.update { state ->
            state.copy(
                selectedDate = date,
                currentMonth = YearMonth.from(date),
                selectedDayTasks = state.tasksByDate[date] ?: emptyList(),
                selectedDayHarvests = state.timelineEntries.filter { it.expectedHarvestDate == date || it.plantedDate == date }
            )
        }
    }

    fun previousMonth() {
        _uiState.update { state ->
            val prevMonth = state.currentMonth.minusMonths(1)
            state.copy(currentMonth = prevMonth)
        }
    }

    fun nextMonth() {
        _uiState.update { state ->
            val nextMonth = state.currentMonth.plusMonths(1)
            state.copy(currentMonth = nextMonth)
        }
    }

    fun completeTask(taskId: String) {
        viewModelScope.launch {
            taskRepository.completeTask(taskId, java.time.Instant.now().toString())
        }
    }
}
