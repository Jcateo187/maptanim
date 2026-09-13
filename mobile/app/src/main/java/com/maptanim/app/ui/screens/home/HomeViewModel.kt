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
    val systemUpdateAvailable: Boolean = false,
    val systemUpdateTitle: String? = null,
    val canvasMode: CanvasMode = CanvasMode.VIEW,
    val error: String? = null
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

    private var farmDataJob: kotlinx.coroutines.Job? = null
    private var farmsObserveJob: kotlinx.coroutines.Job? = null

    init {
        resolveUserAndLoadFarm()
        observeUserProfile()
        observeActiveFarmChanges()
    }

    /**
     * Reactively observe active farm changes triggered from Profile / Modals anywhere in the app.
     */
    private fun observeActiveFarmChanges() {
        viewModelScope.launch {
            com.maptanim.app.core.preferences.FarmPreferencesManager.getInstance().activeFarmChanges.collect { (userId, newFarmId) ->
                val currentId = currentFarmerId ?: "guest"
                if (userId == currentId || (userId == null && currentId == "guest")) {
                    val farms = getFarmsUseCase(currentId).firstOrNull() ?: emptyList()
                    val farm = farms.firstOrNull { it.id == newFarmId }
                    if (farm != null) {
                        activeFarmId = farm.id
                        _uiState.update { it.copy(activeFarm = farm) }
                        loadFarmData(farm.id)
                    }
                }
            }
        }
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

    fun refresh() {
        resolveUserAndLoadFarm()
    }

    private fun resolveUserAndLoadFarm() {
        // Load real profile data and latest information updates from Supabase (cloud)
        viewModelScope.launch {
            (RepositoryProvider.userRepository as? com.maptanim.app.data.repository.UserRepositoryImpl)?.loadUserProfile()
            RepositoryProvider.userRepository.refreshNotifications()
            try {
                RepositoryProvider.cropRepository.refreshCrops()
            } catch (_: Exception) {}
        }
        val user = SupabaseClient.client.auth.currentUserOrNull()
        val farmerId = user?.id ?: "guest"
        currentFarmerId = farmerId
        initialize(farmerId)
    }

    private var presenceJob: kotlinx.coroutines.Job? = null

    private fun startPresenceHeartbeat(farmerId: String) {
        presenceJob?.cancel()
        if (farmerId == "guest") return
        presenceJob = viewModelScope.launch {
            while (true) {
                try {
                    com.maptanim.app.data.repository.ProfileRepository().touchActivity(farmerId)
                } catch (_: Exception) {}
                kotlinx.coroutines.delay(60_000L) // Refresh presence every 60s while app is open
            }
        }
    }

    fun initialize(farmerId: String) {
        currentFarmerId = farmerId
        startPresenceHeartbeat(farmerId)
        farmsObserveJob?.cancel()
        farmsObserveJob = viewModelScope.launch {
            getFarmsUseCase(farmerId)
                .collect { farms ->
                    val savedActiveId = com.maptanim.app.core.preferences.FarmPreferencesManager.getInstance().getActiveFarmId(farmerId)
                    val farm = farms.firstOrNull { it.id == savedActiveId } ?: farms.firstOrNull()
                    if (farm != null) {
                        val isDifferentFarm = activeFarmId != farm.id
                        val isRenamed = _uiState.value.activeFarm?.farmName != farm.farmName
                        activeFarmId = farm.id
                        _uiState.update { it.copy(activeFarm = farm) }
                        if (isDifferentFarm || isRenamed || _uiState.value.plots.isEmpty()) {
                            loadFarmData(farm.id)
                        }
                    } else {
                        // Create initial default farm if newly registered user
                        val defaultFarm = Farm(
                            id = if (farmerId == "guest") "farm-1" else "farm_${farmerId.take(8)}",
                            farmerId = farmerId,
                            farmName = "My Vegetable Farm",
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

    fun loadFarmData(farmId: String) {
        farmDataJob?.cancel()
        farmDataJob = viewModelScope.launch {
            val today = LocalDate.now().toString()
            val farmerId = currentFarmerId ?: "guest"

            // One-time cleanup: delete old hardcoded demo plots that were previously seeded
            val oldPlotIds = listOf("plot-1", "plot-2", "plot-3", "plot-4")
            oldPlotIds.forEach { plotId ->
                try { RepositoryProvider.cropPlotRepository.deletePlot(plotId) } catch (_: Exception) {}
            }

            launch {
                getFarmSummaryUseCase(farmId).collect { summary ->
                    _uiState.update { it.copy(farmSummary = summary) }
                }
            }

            launch {
                getTodayTasksUseCase(farmId, today).collect { tasks ->
                    _uiState.update { it.copy(todayTasks = tasks) }
                }
            }

            launch {
                getUnreadNotificationCountUseCase(farmerId).collect { count ->
                    _uiState.update { it.copy(notificationCount = count) }
                }
            }

            launch {
                RepositoryProvider.notificationRepository.observeAllNotifications(farmerId).collect { notifs ->
                    val systemUpdate = notifs.firstOrNull { notif ->
                        !notif.isRead && (
                            notif.title.contains("System", ignoreCase = true) ||
                            notif.title.contains("Update", ignoreCase = true) ||
                            notif.title.contains("Pananim", ignoreCase = true) ||
                            notif.title.contains("Crop", ignoreCase = true) ||
                            (notif.body?.contains("crop", ignoreCase = true) == true) ||
                            (notif.body?.contains("update", ignoreCase = true) == true)
                        )
                    }
                    _uiState.update {
                        it.copy(
                            systemUpdateAvailable = systemUpdate != null,
                            systemUpdateTitle = systemUpdate?.title
                        )
                    }
                }
            }

            launch {
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
    }

    fun downloadSystemUpdate() {
        viewModelScope.launch {
            try {
                RepositoryProvider.cropRepository.refreshCrops()
                val user = SupabaseClient.client.auth.currentUserOrNull()
                val farmerId = user?.id ?: "guest"
                val notifs = RepositoryProvider.notificationRepository.observeAllNotifications(farmerId).firstOrNull() ?: emptyList()
                notifs.filter { notif ->
                    !notif.isRead && (
                        notif.title.contains("System", ignoreCase = true) ||
                        notif.title.contains("Update", ignoreCase = true) ||
                        notif.title.contains("Pananim", ignoreCase = true) ||
                        notif.title.contains("Crop", ignoreCase = true) ||
                        (notif.body?.contains("crop", ignoreCase = true) == true) ||
                        (notif.body?.contains("update", ignoreCase = true) == true)
                    )
                }.forEach { notif ->
                    RepositoryProvider.notificationRepository.markRead(notif.id)
                }
                _uiState.update { it.copy(systemUpdateAvailable = false, systemUpdateTitle = null) }
            } catch (_: Exception) {}
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
