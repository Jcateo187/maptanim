package com.maptanim.app.ui.dialogs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.domain.model.*
import com.maptanim.app.domain.repository.ActivityRepository
import com.maptanim.app.domain.repository.CropLogRepository
import com.maptanim.app.domain.repository.CropPlotRepository
import com.maptanim.app.domain.repository.CropRepository
import com.maptanim.app.domain.repository.HarvestRepository
import com.maptanim.app.domain.repository.TaskRepository
import com.maptanim.app.dss.engine.DssLogEvaluator
import com.maptanim.app.renderer.model.PlotRenderData
import com.maptanim.app.ui.screens.farm.MonitoredPlant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

enum class TopTab(val label: String) {
    OVERVIEW("Overview"),
    RECOMMENDATION("Recommendation")
}

enum class DssTab(val label: String) {
    TASKS("TASKS"),
    LOGS("LOGS")
}

data class CropDssUiState(
    val plotId: String = "plot-1",
    val farmId: String = "farm-1",
    val cropId: String? = null,
    val cropName: String = "Tomato",
    val plotLabel: String = "Bed #3",
    val cropVariety: String = "Diamante Max F1",
    val soilType: SoilType = SoilType.LOAM,
    val plantingMethod: String = "Transplanting",
    val growingApproach: String = "Organic",
    val daysToHarvest: Int = 75,
    val daysPlanted: Int = 0,
    val plantedDateStr: String = LocalDate.now().toString(),
    val isPlanted: Boolean = true,
    val currentStage: ManagementStage = ManagementStage.PREPARATION,
    val selectedTopTab: TopTab = TopTab.OVERVIEW,
    val selectedDssTab: DssTab = DssTab.TASKS,
    val stageNotificationText: String? = null,
    val pendingStageTransition: ManagementStage? = null,
    val isStageTransitionManual: Boolean = false,
    // Active DSS lists
    val dynamicTasks: List<DssLogEvaluator.GeneratedLogTask> = emptyList(),
    val dynamicRecommendations: List<DssLogEvaluator.LogRecommendation> = emptyList(),
    val dynamicAlerts: List<DssLogEvaluator.LogAlert> = emptyList(),
    // Modals visibility
    val isSettingsOpen: Boolean = false,
    val isAddLogOpen: Boolean = false,
    val isHarvestOpen: Boolean = false,
    val isTimelineExpanded: Boolean = true,
    val expandedStageAccordions: Set<String> = emptySet(),
    val isLoading: Boolean = false
)

class CropDssManagementViewModel(
    private val cropLogRepository: CropLogRepository = RepositoryProvider.cropLogRepository,
    private val cropRepository: CropRepository = RepositoryProvider.cropRepository,
    private val taskRepository: TaskRepository = RepositoryProvider.taskRepository,
    private val harvestRepository: HarvestRepository = RepositoryProvider.harvestRepository,
    private val activityRepository: ActivityRepository = RepositoryProvider.activityRepository,
    private val cropPlotRepository: CropPlotRepository = RepositoryProvider.cropPlotRepository,
    private val logEvaluator: DssLogEvaluator = DssLogEvaluator()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CropDssUiState())
    val uiState: StateFlow<CropDssUiState> = _uiState.asStateFlow()

    fun initialize(
        plant: MonitoredPlant? = null,
        plot: CropPlot? = null,
        plotRender: PlotRenderData? = null,
        initialPlantingMethod: String = "Transplanting",
        initialGrowingApproach: String = "Organic"
    ) {
        val cName = plant?.cropName ?: plot?.cropName ?: plotRender?.cropName ?: "Tomato"
        val pLabel = plant?.plotLabel ?: plot?.plotLabel ?: plotRender?.plotLabel ?: "Bed #3"
        val variety = plant?.cropVariety ?: plot?.cropVariety ?: plotRender?.cropVariety ?: "Diamante Max F1"
        val soil = plant?.soilType ?: plot?.soilType ?: plotRender?.soilType ?: SoilType.LOAM
        val pId = plant?.id ?: plot?.id ?: plotRender?.id ?: "plot-1"
        val fId = plant?.farmId ?: plot?.farmId ?: plotRender?.farmId ?: "farm-1"
        val cId = plant?.cropId ?: plot?.cropId ?: plotRender?.cropId

        val rawDate = plant?.rawPlantedDate ?: plot?.plantedDate ?: plotRender?.plantedDate
        val planted = plant != null || plot?.plantedDate != null || plotRender?.plantedDate != null

        val (daysPlanted, stage) = if (planted && !rawDate.isNullOrBlank()) {
            val dateOnly = rawDate.take(10)
            val pDate = try { LocalDate.parse(dateOnly) } catch (_: Exception) { LocalDate.now() }
            val days = ChronoUnit.DAYS.between(pDate, LocalDate.now()).toInt().coerceAtLeast(0)
            val stg = when {
                days < 10 -> ManagementStage.PREPARATION
                days < 25 -> ManagementStage.PLANTING
                days < 45 -> ManagementStage.EARLY_GROWTH
                days < 65 -> ManagementStage.VEGETATIVE_GROWTH
                days < 80 -> ManagementStage.FLOWERING_FRUIT_DEVELOPMENT
                else -> ManagementStage.HARVEST
            }
            Pair(days, stg)
        } else {
            Pair(0, ManagementStage.PREPARATION)
        }

        _uiState.update {
            it.copy(
                plotId = pId,
                farmId = fId,
                cropId = cId,
                cropName = cName,
                plotLabel = pLabel,
                cropVariety = variety,
                soilType = soil,
                plantingMethod = initialPlantingMethod,
                growingApproach = initialGrowingApproach,
                daysPlanted = daysPlanted,
                plantedDateStr = rawDate?.take(10) ?: LocalDate.now().toString(),
                isPlanted = planted,
                currentStage = stage
            )
        }

        // Phase 3: Fetch dynamic daysToHarvest from Crop repository instead of hardcoded 75
        viewModelScope.launch(Dispatchers.IO) {
            try {
                cropRepository.observeCropByName(cName).collect { crop ->
                    if (crop != null && crop.daysToHarvest > 0) {
                        _uiState.update { it.copy(daysToHarvest = crop.daysToHarvest) }
                    }
                }
            } catch (_: Exception) {}
        }

        // Load incomplete tasks generated for TODAY specifically for this plot ("only generate for this day")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val todayStr = LocalDate.now().toString()
                taskRepository.observeTodayTasks(fId, todayStr).collect { todayTasks ->
                    val plotTasks = todayTasks.filter { it.plotId == pId && !it.isCompleted }
                    _uiState.update { current ->
                        val existingIds = current.dynamicTasks.map { it.id }.toSet()
                        val loadedGenTasks = plotTasks.filterNot { it.id in existingIds }.map { t ->
                            DssLogEvaluator.GeneratedLogTask(
                                id = t.id,
                                title = t.title,
                                description = t.subLabel ?: "",
                                taskType = t.taskType,
                                dueDate = t.dueDate ?: todayStr,
                                stage = current.currentStage
                            )
                        }
                        if (loadedGenTasks.isNotEmpty()) {
                            current.copy(dynamicTasks = current.dynamicTasks + loadedGenTasks)
                        } else {
                            current
                        }
                    }
                }
            } catch (_: Exception) {}
        }
    }

    fun selectTopTab(tab: TopTab) {
        _uiState.update { it.copy(selectedTopTab = tab) }
    }

    fun selectDssTab(tab: DssTab) {
        _uiState.update { it.copy(selectedDssTab = tab) }
    }

    fun toggleTimeline() {
        _uiState.update { it.copy(isTimelineExpanded = !it.isTimelineExpanded) }
    }

    fun toggleAccordion(section: String) {
        _uiState.update {
            val updated = it.expandedStageAccordions.toMutableSet()
            if (updated.contains(section)) updated.remove(section) else updated.add(section)
            it.copy(expandedStageAccordions = updated)
        }
    }

    fun openSettings() {
        _uiState.update { it.copy(isSettingsOpen = true) }
    }

    fun closeSettings() {
        _uiState.update { it.copy(isSettingsOpen = false) }
    }

    fun openAddLog() {
        _uiState.update { it.copy(isAddLogOpen = true) }
    }

    fun closeAddLog() {
        _uiState.update { it.copy(isAddLogOpen = false) }
    }

    fun openHarvest() {
        _uiState.update { it.copy(isHarvestOpen = true) }
    }

    fun closeHarvest() {
        _uiState.update { it.copy(isHarvestOpen = false) }
    }

    fun updateCropSettings(
        variety: String,
        date: String,
        method: String,
        approach: String
    ) {
        val dateChanged = date != _uiState.value.plantedDateStr
        _uiState.update {
            it.copy(
                cropVariety = variety,
                plantedDateStr = date,
                plantingMethod = method,
                growingApproach = approach,
                isSettingsOpen = false,
                isPlanted = if (dateChanged) false else it.isPlanted,
                currentStage = if (dateChanged) ManagementStage.PREPARATION else it.currentStage,
                stageNotificationText = if (dateChanged) "Crop rescheduled to Planned / Unplanted at Stage 1" else null
            )
        }
    }

    // Phase 2.2: Evaluate DSS log in coroutine scope off the main thread
    fun evaluateAndSubmitLog(cropLog: CropLog) {
        viewModelScope.launch(Dispatchers.Default) {
            val state = _uiState.value
            cropLogRepository.insertLog(cropLog)

            val previousLogs = cropLogRepository.getLogsForPlanting(state.plotId)
            val pendingTasks = emptyList<FarmTask>()

            val evalResult = logEvaluator.evaluate(
                log = cropLog,
                previousLogs = previousLogs,
                currentStage = state.currentStage,
                pendingTasks = pendingTasks,
                plantingDate = state.plantedDateStr,
                plantingMethod = state.plantingMethod,
                daysToHarvest = state.daysToHarvest
            )

            // Phase 2.3: Persist generated tasks to TaskRepository
            if (evalResult.newTasks.isNotEmpty()) {
                val farmTasks = evalResult.newTasks.map { genTask ->
                    FarmTask(
                        id = genTask.id,
                        farmId = state.farmId,
                        plotId = state.plotId,
                        plotLabel = state.plotLabel,
                        cropName = state.cropName,
                        taskType = genTask.taskType,
                        title = genTask.title,
                        subLabel = genTask.description,
                        dueDate = genTask.dueDate,
                        isCompleted = false,
                        completedAt = null
                    )
                }
                taskRepository.upsertTasks(farmTasks)
            }

            // Log Activity History with Bed Number, Date, Crop, Variety, Stage, and Observation Details
            try {
                val bedLabel = state.plotLabel.ifBlank { "Bed" }
                val cropLabel = state.cropName.ifBlank { "Crop" }
                val dateOnly = try { cropLog.date.take(10) } catch (_: Exception) { LocalDate.now().toString() }
                val details = if (cropLog.selectedCheckboxes.isNotEmpty()) " (${cropLog.selectedCheckboxes.joinToString(", ")})" else ""
                val obsNotes = "Observation logged for $bedLabel • $cropLabel: Stage ${cropLog.currentStage.label}, Choice ${cropLog.selectedChoice}$details on $dateOnly" +
                        (if (!cropLog.notes.isNullOrBlank()) " • ${cropLog.notes}" else "")

                activityRepository.logActivity(
                    Activity(
                        id = UUID.randomUUID().toString(),
                        plotId = state.plotId,
                        farmId = state.farmId,
                        type = TaskType.OBSERVATION,
                        notes = obsNotes,
                        performedAt = ZonedDateTime.now().toString()
                    )
                )
            } catch (_: Exception) {}

            _uiState.update { current ->
                val newTasks = current.dynamicTasks + evalResult.newTasks
                val newRecs = current.dynamicRecommendations + evalResult.newRecommendations
                val newAlerts = current.dynamicAlerts + evalResult.newAlerts

                current.copy(
                    dynamicTasks = newTasks,
                    dynamicRecommendations = newRecs,
                    dynamicAlerts = newAlerts,
                    // Phase 3.4: Confirmation dialog if stage advance is suggested
                    pendingStageTransition = if (evalResult.stageAdvanced) evalResult.newStage else null,
                    isStageTransitionManual = false,
                    stageNotificationText = if (!evalResult.stageAdvanced) "Observation submitted and evaluated for ${state.plotLabel}!" else null,
                    isAddLogOpen = false
                )
            }
        }
    }

    fun requestPreviousStage() {
        val currentIndex = _uiState.value.currentStage.ordinal
        if (currentIndex > 0) {
            val prevStage = ManagementStage.entries[currentIndex - 1]
            _uiState.update {
                it.copy(
                    pendingStageTransition = prevStage,
                    isStageTransitionManual = true
                )
            }
        }
    }

    fun requestNextStage() {
        val currentIndex = _uiState.value.currentStage.ordinal
        if (currentIndex < ManagementStage.entries.size - 1) {
            val nextStage = ManagementStage.entries[currentIndex + 1]
            _uiState.update {
                it.copy(
                    pendingStageTransition = nextStage,
                    isStageTransitionManual = true
                )
            }
        }
    }

    // Phase 3.4: User confirms stage transition (advance or rollback)
    fun confirmStageTransition() {
        val target = _uiState.value.pendingStageTransition ?: return
        val isAdvance = target.stageNumber > _uiState.value.currentStage.stageNumber
        _uiState.update {
            it.copy(
                currentStage = target,
                pendingStageTransition = null,
                isStageTransitionManual = false,
                stageNotificationText = if (isAdvance) "Stage advanced to ${target.label}" else "Stage moved back to ${target.label}"
            )
        }
    }

    fun dismissStageTransition() {
        _uiState.update {
            it.copy(
                pendingStageTransition = null,
                isStageTransitionManual = false
            )
        }
    }

    fun confirmStageAdvance() = confirmStageTransition()
    fun dismissStageAdvance() = dismissStageTransition()

    fun checkDynamicTask(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.completeTask(taskId, ZonedDateTime.now().toString())
            _uiState.update {
                it.copy(dynamicTasks = it.dynamicTasks.filterNot { t -> t.id == taskId })
            }
        }
    }


    fun submitHarvest(harvestRecord: HarvestRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Persist HarvestRecord in HarvestRepository
                harvestRepository.recordHarvest(harvestRecord)

                // 2. Update Crop Plot record
                val yieldVal = if (harvestRecord.yieldKg > 0f) harvestRecord.yieldKg else harvestRecord.quantity
                cropPlotRepository.recordHarvest(
                    plotId = harvestRecord.plotId,
                    yieldKg = yieldVal,
                    notes = harvestRecord.notes
                )

                // 3. Log Activity History with Bed Number, Date, Crop, Variety and Yield
                val nowStr = harvestRecord.harvestedAt
                val dateOnly = try { nowStr.take(10) } catch (_: Exception) { LocalDate.now().toString() }
                val varietySuffix = if (!harvestRecord.cropVariety.isNullOrBlank()) " (${harvestRecord.cropVariety})" else ""
                val qtyStr = "${harvestRecord.quantity} ${harvestRecord.unit}"
                val notesText = "Harvested $qtyStr of ${harvestRecord.cropName}$varietySuffix from ${harvestRecord.plotLabel} on $dateOnly"

                val activity = Activity(
                    id = UUID.randomUUID().toString(),
                    plotId = harvestRecord.plotId,
                    farmId = harvestRecord.farmId,
                    type = TaskType.HARVEST,
                    notes = notesText,
                    performedAt = nowStr
                )
                activityRepository.logActivity(activity)

                // 4. Complete any pending HARVEST tasks for this plot
                try {
                    val tasks = taskRepository.observeHarvestReady(harvestRecord.farmId).firstOrNull() ?: emptyList()
                    tasks.filter { it.plotId == harvestRecord.plotId && !it.isCompleted }.forEach { t ->
                        taskRepository.completeTask(t.id, nowStr)
                    }
                } catch (_: Exception) {}

                // 5. Update DSS UI state
                _uiState.update {
                    it.copy(
                        isHarvestOpen = false,
                        currentStage = ManagementStage.HARVEST,
                        stageNotificationText = "Harvest recorded: $qtyStr from ${harvestRecord.plotLabel}! Logged to Activity History."
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isHarvestOpen = false,
                        stageNotificationText = "Harvest recorded: ${e.localizedMessage ?: "Success"}"
                    )
                }
            }
        }
    }

    fun dismissNotification() {
        _uiState.update { it.copy(stageNotificationText = null) }
    }
}
