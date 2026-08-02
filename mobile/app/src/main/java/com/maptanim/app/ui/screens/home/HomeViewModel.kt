package com.maptanim.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.domain.model.*
import com.maptanim.app.domain.usecase.*
import com.maptanim.app.renderer.model.PlotRenderData
import com.maptanim.app.renderer.model.TaskPinData
import com.maptanim.app.renderer.model.toRenderData
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import com.maptanim.app.data.repository.RepositoryProvider

// ─── HomeUiState ─────────────────────────────────────────────────────────

data class HomeUiState(
    val isLoading: Boolean = true,
    val activeFarm: Farm? = null,
    val todayTasks: List<FarmTask> = emptyList(),
    val farmSummary: FarmSummary = FarmSummary(),
    val plots: List<PlotRenderData> = emptyList(),
    val notificationCount: Int = 0,
    val canvasMode: CanvasMode = CanvasMode.VIEW,
    val weatherInfo: WeatherInfo? = null,
    val error: String? = null
)

data class WeatherInfo(
    val temperatureCelsius: Float,  // "28°C" shown in top bar
    val description: String,        // "Partly Cloudy"
    val iconCode: String            // Maps to weather icon asset
)

// ─── HomeViewModel ────────────────────────────────────────────────────────

class HomeViewModel(
    private val getTodayTasksUseCase: GetTodayTasksUseCase = GetTodayTasksUseCase(RepositoryProvider.taskRepository),
    private val getFarmSummaryUseCase: GetFarmSummaryUseCase = GetFarmSummaryUseCase(RepositoryProvider.cropPlotRepository, RepositoryProvider.taskRepository),
    private val getFarmPlotsUseCase: GetFarmPlotsUseCase = GetFarmPlotsUseCase(RepositoryProvider.cropPlotRepository),
    private val getUnreadNotificationCountUseCase: GetUnreadNotificationCountUseCase = GetUnreadNotificationCountUseCase(RepositoryProvider.notificationRepository),
    private val getFarmsUseCase: GetFarmsUseCase = GetFarmsUseCase(RepositoryProvider.farmRepository)
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var activeFarmId: String = "farm-1"
    private var currentFarmerId: String = "farmer-1"

    init {
        loadFarmData("farm-1")
    }

    fun initialize(farmerId: String) {
        if (currentFarmerId == farmerId) return
        currentFarmerId = farmerId

        getFarmsUseCase?.let { useCase ->
            viewModelScope.launch {
                useCase(farmerId)
                    .take(1)
                    .collect { farms ->
                        val farm = farms.firstOrNull()
                        if (farm != null) {
                            activeFarmId = farm.id
                            _uiState.update { it.copy(activeFarm = farm) }
                            loadFarmData(farm.id)
                        } else {
                            _uiState.update { it.copy(isLoading = false, error = "No farm found.") }
                        }
                    }
            }
        }
    }

    private fun loadFarmData(farmId: String) {
        val today = LocalDate.now().toString()

        viewModelScope.launch {
            getFarmSummaryUseCase(farmId).collect { summary ->
                _uiState.update { it.copy(farmSummary = summary) }
            }
        }

        viewModelScope.launch {
            getTodayTasksUseCase(farmId, today).collect { tasks ->
                _uiState.update { it.copy(todayTasks = tasks) }
            }
        }

        viewModelScope.launch {
            getUnreadNotificationCountUseCase(currentFarmerId).collect { count ->
                _uiState.update { it.copy(notificationCount = count) }
            }
        }

        viewModelScope.launch {
            combine(
                getFarmPlotsUseCase(farmId),
                getTodayTasksUseCase(farmId, today)
            ) { plots, tasks ->
                plots.map { plot ->
                    val plotTasks = tasks.filter { it.plotId == plot.id }
                    plot.toRenderData(
                        activeTasks = plotTasks.map { task ->
                            TaskPinData(
                                taskId   = task.id,
                                taskType = task.taskType,
                                plotId   = task.plotId
                            )
                        }
                    )
                }
            }.collect { renderPlots ->
                _uiState.update { it.copy(plots = renderPlots) }
            }
        }
    }

    fun toggleEditMode() {
        _uiState.update { state ->
            val nextMode = if (state.canvasMode == CanvasMode.VIEW) CanvasMode.EDIT else CanvasMode.VIEW
            state.copy(canvasMode = nextMode)
        }
    }

    fun completeTask(taskId: String) {
        viewModelScope.launch {
            val updatedTasks = _uiState.value.todayTasks.filter { it.id != taskId }
            _uiState.update { it.copy(todayTasks = updatedTasks) }
        }
    }
}
