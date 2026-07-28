package com.maptanim.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.domain.model.*
import com.maptanim.app.domain.usecase.*
import com.maptanim.app.renderer.model.BedRenderData
import com.maptanim.app.renderer.model.TaskPinData
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

// ─── HomeUiState ─────────────────────────────────────────────────────────

/**
 * All data the HomeScreen needs to render.
 * Every field is populated from Room DB — never from hardcoded values.
 *
 * todayTasks       → TaskDao.observeTodayTasks()  (drives TODAY'S TASKS panel)
 * farmSummary      → Computed from beds + tasks    (drives FARM SUMMARY panel)
 * beds             → BedDao.observeBeds()          (drives isometric canvas)
 * notificationCount → NotificationDao.observeUnreadCount() (drives 🔔 badge)
 * weatherInfo      → WeatherRepository (live API) or null while loading
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val activeFarm: Farm? = null,
    val todayTasks: List<FarmTask> = emptyList(),
    val farmSummary: FarmSummary = FarmSummary(),
    val beds: List<BedRenderData> = emptyList(),
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
    private val getTodayTasksUseCase: GetTodayTasksUseCase? = null,
    private val getFarmSummaryUseCase: GetFarmSummaryUseCase? = null,
    private val getFarmBedsUseCase: GetFarmBedsUseCase? = null,
    private val getUnreadNotificationCountUseCase: GetUnreadNotificationCountUseCase? = null,
    private val getFarmsUseCase: GetFarmsUseCase? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var activeFarmId: String = ""
    private var currentFarmerId: String = ""

    /**
     * Called when HomeScreen is composed with the authenticated user's ID.
     * Starts all live data flows from Room — no network call on every frame.
     */
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
                            _uiState.update { it.copy(isLoading = false) }
                        }
                    }
            }
        }

        getUnreadNotificationCountUseCase?.let { useCase ->
            viewModelScope.launch {
                useCase(farmerId).collect { count ->
                    _uiState.update { it.copy(notificationCount = count) }
                }
            }
        }
    }

    private fun loadFarmData(farmId: String) {
        val today = LocalDate.now().toString()

        getTodayTasksUseCase?.let { useCase ->
            viewModelScope.launch {
                useCase(farmId, today).collect { tasks ->
                    _uiState.update { it.copy(todayTasks = tasks, isLoading = false) }
                }
            }
        }

        getFarmSummaryUseCase?.let { useCase ->
            viewModelScope.launch {
                useCase(farmId).collect { summary ->
                    _uiState.update { it.copy(farmSummary = summary) }
                }
            }
        }

        if (getFarmBedsUseCase != null && getTodayTasksUseCase != null) {
            viewModelScope.launch {
                combine(
                    getFarmBedsUseCase.invoke(farmId),
                    getTodayTasksUseCase.invoke(farmId, today)
                ) { beds, tasks ->
                    beds.map { bed ->
                        val bedTasks = tasks.filter { it.bedId == bed.id }
                        bed.toRenderData(
                            activeTasks = bedTasks.map { task ->
                                TaskPinData(
                                    taskId   = task.id,
                                    taskType = task.taskType,
                                    bedId    = task.bedId
                                )
                            }
                        )
                    }
                }.collect { renderBeds ->
                    _uiState.update { it.copy(beds = renderBeds) }
                }
            }
        }
    }

    fun toggleEditMode() {
        _uiState.update { state ->
            state.copy(
                canvasMode = if (state.canvasMode == CanvasMode.VIEW)
                    CanvasMode.EDIT else CanvasMode.VIEW
            )
        }
    }

    fun selectFarm(farm: Farm) {
        activeFarmId = farm.id
        _uiState.update { it.copy(activeFarm = farm, isLoading = true) }
        loadFarmData(farm.id)
    }
}

// ─── Extension: Bed → BedRenderData ──────────────────────────────────────

fun Bed.toRenderData(activeTasks: List<TaskPinData> = emptyList()): BedRenderData =
    BedRenderData(
        id            = id,
        bedLabel      = bedLabel,
        cropName      = cropName,
        soilType      = soilType,
        posX          = posX,
        posY          = posY,
        widthM        = widthM,
        heightM       = heightM,
        rotationDeg   = rotationDeg,
        hasActiveTasks = activeTasks.isNotEmpty(),
        activeTasks   = activeTasks
    )
