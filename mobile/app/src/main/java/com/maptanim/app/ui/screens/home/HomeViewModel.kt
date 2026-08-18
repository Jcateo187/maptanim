package com.maptanim.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.data.remote.SupabaseClient
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.domain.model.*
import com.maptanim.app.domain.usecase.*
import com.maptanim.app.renderer.model.PlotRenderData
import com.maptanim.app.renderer.model.TaskPinData
import com.maptanim.app.renderer.model.toRenderData
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HomeUiState(
    val isLoading: Boolean = true,
    val activeFarm: Farm? = null,
    val nickname: String = "",
    val avatarAssetPath: String? = null,
    val todayTasks: List<FarmTask> = emptyList(),
    val farmSummary: FarmSummary = FarmSummary(),
    val plots: List<PlotRenderData> = emptyList(),
    val notificationCount: Int = 0,
    val canvasMode: CanvasMode = CanvasMode.VIEW,
    val weatherInfo: WeatherInfo? = null,
    val error: String? = null
)

data class WeatherInfo(
    val temperatureCelsius: Float,
    val description: String,
    val iconCode: String
)

class HomeViewModel(
    private val getTodayTasksUseCase: GetTodayTasksUseCase = GetTodayTasksUseCase(RepositoryProvider.taskRepository),
    private val getFarmSummaryUseCase: GetFarmSummaryUseCase = GetFarmSummaryUseCase(RepositoryProvider.cropPlotRepository, RepositoryProvider.taskRepository),
    private val getFarmPlotsUseCase: GetFarmPlotsUseCase = GetFarmPlotsUseCase(RepositoryProvider.cropPlotRepository),
    private val getUnreadNotificationCountUseCase: GetUnreadNotificationCountUseCase = GetUnreadNotificationCountUseCase(RepositoryProvider.notificationRepository),
    private val getFarmsUseCase: GetFarmsUseCase = GetFarmsUseCase(RepositoryProvider.farmRepository)
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var activeFarmId: String? = null
    private var currentFarmerId: String? = null

    init {
        resolveUserAndLoadFarm()
        observeUserProfile()
    }

    /**
     * Observe the user profile (nickname + avatar) reactively.
     * Data source: Supabase if account is bound, local defaults if guest.
     */
    private fun observeUserProfile() {
        viewModelScope.launch {
            RepositoryProvider.userRepository.observeUserProfile().collect { profile ->
                _uiState.update {
                    it.copy(
                        nickname = profile.nickname.ifBlank { "Farmer" },
                        avatarAssetPath = profile.avatarAssetPath
                    )
                }
            }
        }
    }

    private fun resolveUserAndLoadFarm() {
        // Load real profile data from Supabase (cloud) for bound accounts
        viewModelScope.launch {
            (RepositoryProvider.userRepository as? com.maptanim.app.data.repository.UserRepositoryImpl)?.loadUserProfile()
        }
        val user = SupabaseClient.client.auth.currentUserOrNull()
        if (user != null) {
            currentFarmerId = user.id
            initialize(user.id)
        } else {
            // Guest session — uses local Room data only
            loadFarmData("farm-1")
        }
    }

    fun initialize(farmerId: String) {
        currentFarmerId = farmerId
        viewModelScope.launch {
            getFarmsUseCase(farmerId)
                .collect { farms ->
                    val farm = farms.firstOrNull()
                    if (farm != null) {
                        activeFarmId = farm.id
                        _uiState.update { it.copy(activeFarm = farm) }
                        loadFarmData(farm.id)
                    } else {
                        // Create initial default farm if newly registered user
                        val defaultFarm = Farm(
                            id = "farm_${farmerId.take(8)}",
                            farmerId = farmerId,
                            farmName = "My Vegetable Farm",
                            location = "Murcia, Negros Occidental",
                            totalAreaSqm = 1000f,
                            createdAt = LocalDate.now().toString(),
                            updatedAt = LocalDate.now().toString()
                        )
                        RepositoryProvider.farmRepository.upsertFarm(defaultFarm)
                        activeFarmId = defaultFarm.id
                        _uiState.update { it.copy(activeFarm = defaultFarm) }
                        loadFarmData(defaultFarm.id)
                    }
                }
        }
    }

    private fun loadFarmData(farmId: String) {
        val today = LocalDate.now().toString()
        val farmerId = currentFarmerId ?: "guest"

        // One-time cleanup: delete old hardcoded demo plots that were previously seeded
        viewModelScope.launch {
            val oldPlotIds = listOf("plot-1", "plot-2", "plot-3", "plot-4")
            oldPlotIds.forEach { plotId ->
                try { RepositoryProvider.cropPlotRepository.deletePlot(plotId) } catch (_: Exception) {}
            }
        }

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
            getUnreadNotificationCountUseCase(farmerId).collect { count ->
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
                _uiState.update { it.copy(plots = renderPlots, isLoading = false) }
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
            RepositoryProvider.taskRepository.completeTask(taskId, LocalDate.now().toString())
        }
    }
}
