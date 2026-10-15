package com.maptanim.app.features.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.data.remote.SupabaseClient
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.domain.model.*
import com.maptanim.app.domain.usecase.*
import com.maptanim.app.features.farm.renderer.model.PlotRenderData
import com.maptanim.app.features.farm.renderer.model.TaskPinData
import com.maptanim.app.features.farm.renderer.model.toRenderData
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.UUID

data class HomeUiState(
    val isLoading: Boolean = true,
    val activeFarm: Farm? = null,
    val nickname: String = "",
    val avatarAssetPath: String? = null,
    val todayTasks: List<FarmTask> = emptyList(),
    val farmSummary: FarmSummary = FarmSummary(),
    val plots: List<PlotRenderData> = emptyList(),
    val isFarmCalibrated: Boolean = true,
    val notificationCount: Int = 0,
    val systemUpdateAvailable: Boolean = false,
    val systemUpdateTitle: String? = null,
    val canvasMode: CanvasMode = CanvasMode.VIEW,
    val showTaskCompletionOverlay: Boolean = false,
    val completedTaskPlotTarget: PlotRenderData? = null,
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
                            id = if (farmerId == "guest") "farm-guest" else "farm_${farmerId.take(8)}",
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
            val isCalibrated = com.maptanim.app.core.preferences.FarmPreferencesManager.getInstance().isFarmCalibrated(farmId)
            _uiState.update { it.copy(isFarmCalibrated = isCalibrated) }

            // One-time cleanup: delete old hardcoded demo plots from local Room cache if present
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
                combine(
                    getTodayTasksUseCase(farmId, today),
                    _uiState.map { it.plots }.distinctUntilChanged()
                ) { tasks, plots ->
                    if (plots.isEmpty()) tasks
                    else {
                        val activePlots = plots.filter { !it.plantedDate.isNullOrBlank() }
                        tasks.filter { task ->
                            if (activePlots.isEmpty()) return@filter false
                            val taskCrop = task.cropName?.lowercase()?.trim()
                            if (!taskCrop.isNullOrBlank()) {
                                activePlots.any { activePlot ->
                                    val matchesCrop = activePlot.cropName?.lowercase()?.trim() == taskCrop
                                    val rawTaskPlotId = task.plotId.substringBefore("_zone_").substringBefore("_crop_")
                                    val rawActivePlotId = activePlot.id.substringBefore("_zone_").substringBefore("_crop_")
                                    val matchesPlot = task.plotId == "general" ||
                                        activePlot.id == task.plotId ||
                                        rawActivePlotId == rawTaskPlotId
                                    matchesCrop && matchesPlot
                                }
                            } else {
                                activePlots.any { activePlot ->
                                    val rawTaskPlotId = task.plotId.substringBefore("_zone_").substringBefore("_crop_")
                                    val rawActivePlotId = activePlot.id.substringBefore("_zone_").substringBefore("_crop_")
                                    activePlot.id == task.plotId || rawActivePlotId == rawTaskPlotId
                                }
                            }
                        }
                    }
                }.collect { filteredTasks ->
                    _uiState.update { it.copy(todayTasks = filteredTasks) }
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
                getFarmPlotsUseCase(farmId).flatMapLatest { allPlots ->
                    val plots = allPlots.filter { it.isActive }
                    val plotIds = plots.map { it.id }
                    if (plotIds.isNotEmpty()) {
                        combine(
                            RepositoryProvider.cropZoneRepository.observeZonesByPlotIds(plotIds),
                            getTodayTasksUseCase(farmId, today)
                        ) { zones, tasks ->
                            val renderPlots = mutableListOf<PlotRenderData>()
                            plots.forEach { plot ->
                                val matchingZones = zones.filter {
                                    it.plotId == plot.id &&
                                    !it.cropName.isNullOrBlank() &&
                                    !it.cropName.equals("Bed", ignoreCase = true)
                                }
                                val plotTasks = tasks.filter { it.plotId == plot.id }
                                val taskPins = plotTasks.map { task ->
                                    TaskPinData(
                                        taskId   = task.id,
                                        taskType = task.taskType,
                                        plotId   = task.plotId
                                    )
                                }

                                if (matchingZones.isNotEmpty()) {
                                    val farmPrefs = com.maptanim.app.core.preferences.FarmPreferencesManager.getInstance()
                                    matchingZones.forEachIndexed { zIdx, zone ->
                                        val zCropName = zone.cropName ?: "Vegetable"
                                        val zCropId = zone.cropId ?: zCropName.lowercase()
                                        val zPlantedDate = farmPrefs.resolveCropPlantedDate(zone.id, plot.id, zCropName)
                                            ?: if (matchingZones.size == 1 && !plot.cropName.isNullOrBlank() && !plot.cropName.equals("Bed", ignoreCase = true) && !plot.cropName.equals("Garden", ignoreCase = true)) {
                                                farmPrefs.getCropPlantedDate(plot.id)
                                            } else null
                                        renderPlots.add(
                                            plot.copy(
                                                id = if (matchingZones.size > 1) "${plot.id}_zone_${zone.id}" else plot.id,
                                                plotLabel = if (matchingZones.size > 1) "${plot.plotLabel} (Z${zIdx + 1})" else plot.plotLabel,
                                                cropName = zCropName,
                                                cropId = zCropId,
                                                plantedDate = zPlantedDate
                                            ).toRenderData(activeTasks = taskPins)
                                        )
                                    }
                                } else {
                                    // Fallback: If matchingZones is empty, check plot.notes or plot.cropName
                                    val bedCropsFromNotes = if (plot.notes?.startsWith("Crops: ") == true) {
                                        plot.notes.removePrefix("Crops: ").split(", ").map { it.trim() }.filter { it.isNotBlank() }
                                    } else emptyList()

                                    val farmPrefs = com.maptanim.app.core.preferences.FarmPreferencesManager.getInstance()
                                    if (bedCropsFromNotes.isNotEmpty()) {
                                        bedCropsFromNotes.forEachIndexed { cIdx, cName ->
                                            val cPlantedDate = farmPrefs.resolveCropPlantedDate(null, plot.id, cName)
                                                ?: farmPrefs.getCropPlantedDate("${plot.id}_crop_$cIdx")
                                            renderPlots.add(
                                                plot.copy(
                                                    id = if (bedCropsFromNotes.size > 1) "${plot.id}_crop_$cIdx" else plot.id,
                                                    plotLabel = if (bedCropsFromNotes.size > 1) "${plot.plotLabel} (${cIdx + 1})" else plot.plotLabel,
                                                    cropName = cName,
                                                    cropId = cName.lowercase(),
                                                    plantedDate = cPlantedDate
                                                ).toRenderData(activeTasks = taskPins)
                                            )
                                        }
                                    } else if (!plot.cropName.isNullOrBlank() && !plot.cropName.equals("Bed", ignoreCase = true) && !plot.cropName.equals("Garden", ignoreCase = true) && plot.cropId != "bed") {
                                        val pPlantedDate = farmPrefs.resolveCropPlantedDate(null, plot.id, plot.cropName)
                                            ?: farmPrefs.getCropPlantedDate(plot.id)
                                        renderPlots.add(
                                            plot.copy(
                                                plantedDate = pPlantedDate
                                            ).toRenderData(activeTasks = taskPins)
                                        )
                                    } else {
                                        renderPlots.add(
                                            plot.copy(
                                                plantedDate = null
                                            ).toRenderData(activeTasks = taskPins)
                                        )
                                    }
                                }
                            }
                            renderPlots
                        }
                    } else {
                        flowOf(emptyList<PlotRenderData>())
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
            val task = _uiState.value.todayTasks.firstOrNull { it.id == taskId }
            val now = ZonedDateTime.now().toString()
            val dateOnly = LocalDate.now().toString()
            RepositoryProvider.taskRepository.completeTask(taskId, now)

            if (task != null) {
                val bedStr = task.plotLabel ?: "Bed #1"
                val cropStr = task.cropName ?: "Crop"
                val actNotes = if (task.taskType == TaskType.HARVEST) {
                    "Harvested $cropStr from $bedStr on $dateOnly (Task completed)"
                } else {
                    "Completed ${task.taskType.name} task: ${task.title} for $bedStr ($cropStr) on $dateOnly"
                }
                val activity = Activity(
                    id = UUID.randomUUID().toString(),
                    plotId = task.plotId,
                    farmId = task.farmId,
                    type = task.taskType,
                    notes = actNotes,
                    performedAt = now
                )
                RepositoryProvider.activityRepository.logActivity(activity)

                if (task.taskType == TaskType.HARVEST) {
                    RepositoryProvider.cropPlotRepository.recordHarvest(task.plotId, 0f, actNotes)
                }
            }

            // Check if vegetable harvest completed or all today's tasks completed
            val remainingTasks = _uiState.value.todayTasks.filter { it.id != taskId && !it.isCompleted }
            val matchingPlot = _uiState.value.plots.firstOrNull {
                it.id == task?.plotId ||
                (!task?.cropName.isNullOrBlank() && it.cropName.equals(task?.cropName, ignoreCase = true)) ||
                (!task?.plotLabel.isNullOrBlank() && it.plotLabel.equals(task?.plotLabel, ignoreCase = true))
            }
            val isVegetableHarvest = (task?.taskType == TaskType.HARVEST)
            val allDailyTasksCompleted = remainingTasks.isEmpty() && _uiState.value.todayTasks.isNotEmpty()

            if (isVegetableHarvest || allDailyTasksCompleted) {
                _uiState.update {
                    it.copy(
                        showTaskCompletionOverlay = true,
                        completedTaskPlotTarget = matchingPlot
                    )
                }
            }

            activeFarmId?.let { loadFarmData(it) }
        }
    }

    fun dismissTaskCompletionOverlay() {
        _uiState.update { it.copy(showTaskCompletionOverlay = false) }
    }

    fun replantCropOnPlot(plotId: String, cropName: String) {
        viewModelScope.launch {
            startPlantingSchedule(plotId, cropName, LocalDate.now())
            dismissTaskCompletionOverlay()
        }
    }

    fun compostAndFallowPlot(plotId: String) {
        viewModelScope.launch {
            val rawPlotId = plotId.substringBefore("_zone_").substringBefore("_crop_")
            val farmId = activeFarmId ?: _uiState.value.activeFarm?.id ?: return@launch
            val existingPlots = RepositoryProvider.cropPlotRepository.observePlots(farmId).firstOrNull() ?: emptyList()
            val plot = existingPlots.firstOrNull { it.id == rawPlotId }
            if (plot != null) {
                val updatedPlot = plot.copy(
                    cropName = "Soil Rehabilitation (Fallow & Compost)",
                    cropId = null,
                    cropVariety = "Organic Compost / Mulch",
                    notes = "Plot placed in 10-day fallow with organic compost for soil rejuvenation.",
                    plantedDate = LocalDate.now().toString(),
                    currentStage = ManagementStage.PREPARATION,
                    isActive = true
                )
                RepositoryProvider.cropPlotRepository.upsertPlot(updatedPlot)
            }
            activeFarmId?.let { loadFarmData(it) }
            dismissTaskCompletionOverlay()
        }
    }

    fun rotateCropOnPlot(plotId: String, newCropName: String) {
        viewModelScope.launch {
            val rawPlotId = plotId.substringBefore("_zone_").substringBefore("_crop_")
            val farmId = activeFarmId ?: _uiState.value.activeFarm?.id ?: return@launch
            val existingPlots = RepositoryProvider.cropPlotRepository.observePlots(farmId).firstOrNull() ?: emptyList()
            val plot = existingPlots.firstOrNull { it.id == rawPlotId }
            if (plot != null) {
                val prevList = if (!plot.cropName.isNullOrBlank()) {
                    plot.previousCropsHistory + plot.cropName
                } else {
                    plot.previousCropsHistory
                }
                val updatedPlot = plot.copy(
                    cropName = newCropName,
                    cropId = newCropName.lowercase().replace(" ", "_"),
                    cropVariety = "Standard",
                    notes = "Crop rotated from ${plot.cropName ?: "previous crop"} to $newCropName for nutrient balance.",
                    plantedDate = LocalDate.now().toString(),
                    currentStage = ManagementStage.PREPARATION,
                    harvestCount = 0,
                    totalYieldKg = 0f,
                    previousCropsHistory = prevList,
                    isActive = true
                )
                RepositoryProvider.cropPlotRepository.upsertPlot(updatedPlot)
            }
            startPlantingSchedule(plotId, newCropName, LocalDate.now())
            activeFarmId?.let { loadFarmData(it) }
            dismissTaskCompletionOverlay()
        }
    }

    fun refreshCalibration() {
        val farmId = activeFarmId ?: return
        val isCalibrated = com.maptanim.app.core.preferences.FarmPreferencesManager.getInstance().isFarmCalibrated(farmId)
        _uiState.update { it.copy(isFarmCalibrated = isCalibrated) }
    }

    fun startPlantingSchedule(plotId: String, cropName: String? = null, plantingDate: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            val rawPlotId = plotId.substringBefore("_zone_").substringBefore("_crop_")
            val zoneId = if (plotId.contains("_zone_")) plotId.substringAfter("_zone_") else null
            val farmId = activeFarmId ?: _uiState.value.activeFarm?.id ?: return@launch
            val dateStr = plantingDate.toString()

            val farmPrefs = com.maptanim.app.core.preferences.FarmPreferencesManager.getInstance()
            if (plotId.contains("_zone_") || plotId.contains("_crop_")) {
                farmPrefs.saveCropPlantedDate(plotId, dateStr)
            }
            if (!cropName.isNullOrBlank()) {
                farmPrefs.saveCropPlantedDate("${rawPlotId}_${cropName}", dateStr)
                farmPrefs.saveCropPlantedDate("${plotId}_${cropName}", dateStr)
            }
            if (!zoneId.isNullOrBlank()) {
                farmPrefs.saveCropPlantedDate(zoneId, dateStr)
            }

            val existingPlots = RepositoryProvider.cropPlotRepository.observePlots(farmId).firstOrNull() ?: emptyList()
            val existingZones = RepositoryProvider.cropZoneRepository.observeZonesByPlotIds(listOf(rawPlotId)).firstOrNull() ?: emptyList()
            val plot = existingPlots.firstOrNull { it.id == rawPlotId }
            if (plot != null) {
                val hasMultipleCropsInNotes = plot.notes?.startsWith("Crops: ") == true && plot.notes.contains(",")
                val hasMultipleZones = existingZones.size > 1
                val isSingleDedicatedPlot = !hasMultipleCropsInNotes && !hasMultipleZones && (
                    zoneId == null ||
                    plot.cropName.equals(cropName, ignoreCase = true)
                )

                if (isSingleDedicatedPlot) {
                    farmPrefs.saveCropPlantedDate(rawPlotId, dateStr)
                    val updated = plot.copy(
                        plantedDate = dateStr,
                        cropName = cropName ?: plot.cropName,
                        currentStage = ManagementStage.VEGETATIVE_GROWTH
                    )
                    RepositoryProvider.cropPlotRepository.upsertPlot(updated)
                }
            }
            loadFarmData(farmId)
        }
    }
}
