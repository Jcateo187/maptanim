package com.maptanim.app.features.farm.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.data.remote.SupabaseClient
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.domain.model.*
import com.maptanim.app.domain.repository.*
import com.maptanim.app.features.farm.canvas.CanvasLayer
import com.maptanim.app.features.farm.renderer.model.CropZoneRenderData
import com.maptanim.app.features.farm.renderer.model.PlotRenderData
import com.maptanim.app.features.farm.renderer.model.toRenderData
import com.maptanim.app.features.farm.viewmodel.state.*
import com.maptanim.app.dss.engine.DssLogEvaluator
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.UUID

/**
 * FarmHubViewModel — Consolidated Hub ViewModel coordinating the 4 core agricultural pillars:
 * 1. PlanTab (spatial layout, bed sizing, yard measurements)
 * 2. GuideTab (agronomic rules, growth stages, daily care)
 * 3. CheckUpTab (observations, pest/disease logging, crop health)
 * 4. HarvestTab (readiness, yield records, post-harvest protocol)
 */
class FarmHubViewModel(
    private val cropPlotRepository: CropPlotRepository = RepositoryProvider.cropPlotRepository,
    private val cropZoneRepository: CropZoneRepository = RepositoryProvider.cropZoneRepository,
    private val taskRepository: TaskRepository = RepositoryProvider.taskRepository,
    private val cropLogRepository: CropLogRepository = RepositoryProvider.cropLogRepository,
    private val harvestRepository: HarvestRepository = RepositoryProvider.harvestRepository,
    private val activityRepository: ActivityRepository = RepositoryProvider.activityRepository
) : ViewModel() {

    private val logEvaluator = DssLogEvaluator()

    private val _uiState = MutableStateFlow(FarmHubUiState())
    val uiState: StateFlow<FarmHubUiState> = _uiState.asStateFlow()

    private val undoStack = ArrayDeque<EditAction>()
    private val redoStack = ArrayDeque<EditAction>()

    init {
        resolveActiveFarm()
        observePlotsAndZones()
        observeTasksAndDecisions()
    }

    fun selectTopTab(tab: TopTab) = _uiState.update { it.copy(selectedTopTab = tab) }

    private fun resolveActiveFarm() {
        viewModelScope.launch {
            val user = SupabaseClient.client.auth.currentUserOrNull()
            val localFarm = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                RepositoryProvider.getDatabase()?.farmDao()?.getActiveFarm()
            }
            val farmId = localFarm?.id ?: user?.id?.takeIf { it.isNotBlank() } ?: "farm-default"
            val farmName = localFarm?.farmName ?: "My Farm"
            val prefs = com.maptanim.app.core.preferences.FarmPreferencesManager.getInstance()
            val env = prefs.getFarmEnvironment(farmId)
            val isCalibrated = prefs.isFarmCalibrated(farmId)

            _uiState.update {
                it.copy(
                    activeFarmId = farmId,
                    farmName = farmName,
                    farmEnvironment = env,
                    showFarmSetupDialog = !isCalibrated,
                    planState = it.planState.copy(
                        farmEnvironment = env,
                        activeSoilType = env.defaultSoil,
                        showFarmSetupDialog = !isCalibrated
                    )
                )
            }
        }
    }

    fun openFarmSetupDialog() = _uiState.update {
        it.copy(showFarmSetupDialog = true, planState = it.planState.copy(showFarmSetupDialog = true))
    }

    fun closeFarmSetupDialog() = _uiState.update {
        it.copy(showFarmSetupDialog = false, planState = it.planState.copy(showFarmSetupDialog = false))
    }

    fun updateFarmSetup(name: String, environment: com.maptanim.app.domain.model.FarmEnvironment) {
        viewModelScope.launch {
            val farmId = _uiState.value.activeFarmId
            com.maptanim.app.core.preferences.FarmPreferencesManager.getInstance().saveFarmEnvironment(farmId, environment)

            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                val db = RepositoryProvider.getDatabase()
                val existing = db?.farmDao()?.getFarmById(farmId) ?: db?.farmDao()?.getActiveFarm()
                if (existing != null) {
                    db?.farmDao()?.upsertFarm(existing.copy(farmName = name))
                }
            }

            _uiState.update { state ->
                state.copy(
                    farmName = name,
                    farmEnvironment = environment,
                    showFarmSetupDialog = false,
                    planState = state.planState.copy(
                        farmEnvironment = environment,
                        activeSoilType = environment.defaultSoil,
                        showFarmSetupDialog = false
                    )
                )
            }
        }
    }

    private fun observePlotsAndZones() {
        val farmId = _uiState.value.activeFarmId
        viewModelScope.launch {
            cropPlotRepository.observePlots(farmId).collect { plots ->
                val plotRenderData = plots.map { it.toRenderData() }
                _uiState.update { state ->
                    val selectedPlot = plots.firstOrNull { it.id == state.planState.selectedPlotId }
                        ?: plots.firstOrNull()
                    val selectedPlotId = selectedPlot?.id
                    val updatedGuide = computeGuideStateForPlot(selectedPlot, state.guideState)

                    state.copy(
                        planState = state.planState.copy(
                            rawPlots = plots,
                            plots = plotRenderData,
                            selectedPlotId = selectedPlotId
                        ),
                        guideState = updatedGuide
                    )
                }
            }
        }
    }

    private fun observeTasksAndDecisions() {
        val farmId = _uiState.value.activeFarmId
        viewModelScope.launch {
            taskRepository.observeTodayTasks(farmId, LocalDate.now().toString()).collect { tasks ->
                _uiState.update { state ->
                    val dssTasks = tasks.map { farmTask ->
                        DssLogEvaluator.GeneratedLogTask(
                            id = farmTask.id,
                            title = farmTask.title,
                            description = farmTask.subLabel ?: "Agronomic care task",
                            taskType = farmTask.taskType,
                            dueDate = farmTask.dueDate,
                            stage = ManagementStage.VEGETATIVE_GROWTH
                        )
                    }
                    val currentTasks = if (dssTasks.isNotEmpty()) dssTasks else state.guideState.dynamicTasks
                    state.copy(
                        guideState = state.guideState.copy(dynamicTasks = currentTasks)
                    )
                }
            }
        }
    }

    private fun computeGuideStateForPlot(plot: CropPlot?, currentGuide: FarmHubGuideState): FarmHubGuideState {
        if (plot == null) return currentGuide
        val hasCrops = !plot.cropName.isNullOrBlank() && !plot.cropName.equals("Bed", ignoreCase = true)
        val daysPlanted = try {
            val dateStr = plot.plantedDate?.take(10)
            if (dateStr != null) {
                val planted = LocalDate.parse(dateStr)
                java.time.temporal.ChronoUnit.DAYS.between(planted, LocalDate.now()).toInt().coerceAtLeast(0)
            } else 0
        } catch (_: Exception) { 0 }

        val stage = when {
            daysPlanted < 14 -> ManagementStage.PREPARATION
            daysPlanted in 14..45 -> ManagementStage.VEGETATIVE_GROWTH
            daysPlanted in 46..65 -> ManagementStage.FLOWERING_FRUIT_DEVELOPMENT
            else -> ManagementStage.HARVEST
        }

        val defaultTasks = if (currentGuide.dynamicTasks.isEmpty() && hasCrops) {
            listOf(
                DssLogEvaluator.GeneratedLogTask(
                    id = "task_water_${plot.id}",
                    title = "Morning Deep Root Watering",
                    description = "Apply 2.5L/m² at the base before 9:00 AM for ${plot.cropName}.",
                    taskType = TaskType.WATER,
                    dueDate = LocalDate.now().toString(),
                    stage = stage
                ),
                DssLogEvaluator.GeneratedLogTask(
                    id = "task_scout_${plot.id}",
                    title = "Early Pest & Companion Check",
                    description = "Inspect leaf undersides and verify companion plant spacing.",
                    taskType = TaskType.OBSERVATION,
                    dueDate = LocalDate.now().toString(),
                    stage = stage
                )
            )
        } else currentGuide.dynamicTasks

        return currentGuide.copy(
            currentStage = stage,
            daysPlanted = daysPlanted,
            daysToHarvest = 75,
            dynamicTasks = defaultTasks
        )
    }

    // ─── Plan Tab Actions ───────────────────────────────────────────────────

    fun selectPlot(plotId: String?) {
        _uiState.update { state ->
            val selected = state.planState.rawPlots.firstOrNull { it.id == plotId }
            val updatedGuide = computeGuideStateForPlot(selected, state.guideState)
            state.copy(
                planState = state.planState.copy(selectedPlotId = plotId),
                guideState = updatedGuide
            )
        }
    }

    fun setCanvasLayer(layer: CanvasLayer) {
        _uiState.update { state ->
            state.copy(planState = state.planState.copy(canvasLayer = layer))
        }
    }

    fun setEditTool(tool: EditTool) {
        _uiState.update { state ->
            state.copy(planState = state.planState.copy(activeTool = tool))
        }
    }

    fun setSoilType(soil: SoilType) {
        _uiState.update { state ->
            state.copy(planState = state.planState.copy(activeSoilType = soil))
        }
    }

    fun toggleResizeMode() {
        _uiState.update { state ->
            state.copy(planState = state.planState.copy(isResizeMode = !state.planState.isResizeMode))
        }
    }

    private var savePlotJob: kotlinx.coroutines.Job? = null

    fun movePlot(plotId: String, deltaX: Float, deltaY: Float) {
        val currentPlot = _uiState.value.planState.rawPlots.firstOrNull { it.id == plotId } ?: return
        val newX = (currentPlot.posX + deltaX).coerceIn(0.5f, 40f)
        val newY = (currentPlot.posY + deltaY).coerceIn(0.5f, 40f)
        val updated = currentPlot.copy(posX = newX, posY = newY)

        _uiState.update { state ->
            val updatedPlots = state.planState.rawPlots.map { if (it.id == plotId) updated else it }
            state.copy(
                planState = state.planState.copy(
                    rawPlots = updatedPlots,
                    plots = updatedPlots.map { it.toRenderData() }
                )
            )
        }

        // Real-time immediate persistence on IO dispatcher
        savePlotJob?.cancel()
        savePlotJob = viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            cropPlotRepository.upsertPlot(updated)
        }
    }

    fun resizePlot(plotId: String, newWidthM: Float, newHeightM: Float) {
        val currentPlot = _uiState.value.planState.rawPlots.firstOrNull { it.id == plotId } ?: return
        val clampedW = newWidthM.coerceIn(0.5f, 15f)
        val clampedH = newHeightM.coerceIn(0.5f, 15f)
        val updated = currentPlot.copy(widthM = clampedW, heightM = clampedH)

        undoStack.addLast(EditAction.ModifyPlot(currentPlot, updated))
        redoStack.clear()

        _uiState.update { state ->
            val updatedPlots = state.planState.rawPlots.map { if (it.id == plotId) updated else it }
            state.copy(
                planState = state.planState.copy(
                    rawPlots = updatedPlots,
                    plots = updatedPlots.map { it.toRenderData() }
                )
            )
        }

        // Real-time immediate persistence on IO dispatcher
        savePlotJob?.cancel()
        savePlotJob = viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            cropPlotRepository.upsertPlot(updated)
            updateUndoRedoState()
        }
    }

    fun assignCropToPlot(plotId: String, cropId: String, cropName: String) {
        val currentPlot = _uiState.value.planState.rawPlots.firstOrNull { it.id == plotId } ?: return
        val updated = currentPlot.copy(
            cropId = cropId,
            cropName = cropName,
            plantedDate = currentPlot.plantedDate ?: LocalDate.now().toString()
        )
        undoStack.addLast(EditAction.ModifyPlot(currentPlot, updated))
        redoStack.clear()

        viewModelScope.launch {
            cropPlotRepository.upsertPlot(updated)
            updateUndoRedoState()
        }
    }

    fun addPlot(
        cropName: String? = null,
        widthM: Float = 1.5f,
        heightM: Float = 4.0f,
        soilType: SoilType = SoilType.LOAM
    ) {
        val farmId = _uiState.value.activeFarmId
        val existingCount = _uiState.value.planState.rawPlots.size
        val newPlot = CropPlot(
            id = UUID.randomUUID().toString(),
            farmId = farmId,
            plotLabel = "Bed #${existingCount + 1}",
            cropName = cropName,
            cropId = cropName?.lowercase()?.replace(" ", "_"),
            soilType = soilType,
            posX = 2f,
            posY = (existingCount * 4.5f) + 1f,
            widthM = widthM,
            heightM = heightM,
            plantedDate = if (cropName != null) LocalDate.now().toString() else null
        )

        undoStack.addLast(EditAction.AddPlot(newPlot))
        redoStack.clear()

        viewModelScope.launch {
            cropPlotRepository.upsertPlot(newPlot)
            _uiState.update { state ->
                state.copy(
                    planState = state.planState.copy(
                        canUndo = undoStack.isNotEmpty(),
                        canRedo = false,
                        selectedPlotId = newPlot.id
                    )
                )
            }
        }
    }

    fun deletePlot(plotId: String) {
        val plotToDelete = _uiState.value.planState.rawPlots.firstOrNull { it.id == plotId } ?: return
        undoStack.addLast(EditAction.DeletePlot(plotToDelete))
        redoStack.clear()

        viewModelScope.launch {
            cropPlotRepository.deletePlot(plotId)
            _uiState.update { state ->
                state.copy(
                    planState = state.planState.copy(
                        canUndo = undoStack.isNotEmpty(),
                        canRedo = false,
                        selectedPlotId = state.planState.rawPlots.firstOrNull { it.id != plotId }?.id
                    )
                )
            }
        }
    }

    fun undo() {
        if (undoStack.isEmpty()) return
        val action = undoStack.removeLast()
        redoStack.addLast(action)

        viewModelScope.launch {
            when (action) {
                is EditAction.AddPlot -> cropPlotRepository.deletePlot(action.plot.id)
                is EditAction.DeletePlot -> cropPlotRepository.upsertPlot(action.plot)
                is EditAction.ModifyPlot -> cropPlotRepository.upsertPlot(action.oldPlot)
                else -> Unit
            }
            updateUndoRedoState()
        }
    }

    fun redo() {
        if (redoStack.isEmpty()) return
        val action = redoStack.removeLast()
        undoStack.addLast(action)

        viewModelScope.launch {
            when (action) {
                is EditAction.AddPlot -> cropPlotRepository.upsertPlot(action.plot)
                is EditAction.DeletePlot -> cropPlotRepository.deletePlot(action.plot.id)
                is EditAction.ModifyPlot -> cropPlotRepository.upsertPlot(action.newPlot)
                else -> Unit
            }
            updateUndoRedoState()
        }
    }

    private fun updateUndoRedoState() {
        _uiState.update { state ->
            state.copy(
                planState = state.planState.copy(
                    canUndo = undoStack.isNotEmpty(),
                    canRedo = redoStack.isNotEmpty()
                )
            )
        }
    }

    // ─── Guide Tab Actions ──────────────────────────────────────────────────

    fun selectDssTab(tab: DssTab) {
        _uiState.update { state ->
            state.copy(guideState = state.guideState.copy(selectedDssTab = tab))
        }
    }

    fun completeTask(taskId: String) {
        viewModelScope.launch {
            taskRepository.completeTask(taskId, LocalDate.now().toString())
        }
    }

    // ─── CheckUp Tab Actions ────────────────────────────────────────────────

    fun setAddLogOpen(isOpen: Boolean) {
        _uiState.update { state ->
            state.copy(checkUpState = state.checkUpState.copy(isAddLogOpen = isOpen))
        }
    }

    fun submitCropLog(log: CropLog) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                cropLogRepository.insertLog(log)

                val targetPlot = _uiState.value.planState.rawPlots.firstOrNull { it.id == log.cropPlantingId }
                    ?: _uiState.value.activePlot
                val farmId = _uiState.value.activeFarmId.ifBlank { targetPlot?.farmId ?: "farm-default" }
                val plotId = targetPlot?.id ?: log.cropPlantingId.ifBlank { "plot-1" }
                val plotLabel = targetPlot?.plotLabel ?: log.bedId.ifBlank { "Bed #1" }
                val cropName = targetPlot?.cropName ?: log.cropName.ifBlank { "Vegetable" }
                val cropVariety = targetPlot?.cropVariety ?: log.varietyName ?: "Standard"
                val currentStage = targetPlot?.currentStage ?: log.currentStage

                val previousLogs = try {
                    cropLogRepository.getLogsForPlanting(plotId)
                } catch (_: Exception) {
                    emptyList()
                }

                val evalResult = logEvaluator.evaluate(
                    log = log,
                    previousLogs = previousLogs,
                    currentStage = currentStage,
                    pendingTasks = emptyList(),
                    plantingDate = targetPlot?.plantedDate,
                    plantingMethod = "Transplanting",
                    daysToHarvest = 75
                )

                // Persist generated tasks to TaskRepository
                if (evalResult.newTasks.isNotEmpty()) {
                    val farmTasks = evalResult.newTasks.map { genTask ->
                        FarmTask(
                            id = genTask.id,
                            farmId = farmId,
                            plotId = plotId,
                            plotLabel = plotLabel,
                            cropName = cropName,
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
                    val dateOnly = try { log.date.take(10) } catch (_: Exception) { LocalDate.now().toString() }
                    val details = if (log.selectedCheckboxes.isNotEmpty()) " (${log.selectedCheckboxes.joinToString(", ")})" else ""
                    val obsNotes = "Observation logged for $plotLabel • $cropName ($cropVariety): Stage ${log.currentStage.label}, Choice ${log.selectedChoice}$details on $dateOnly" +
                            (if (!log.notes.isNullOrBlank()) " • ${log.notes}" else "")

                    activityRepository.logActivity(
                        Activity(
                            id = UUID.randomUUID().toString(),
                            plotId = plotId,
                            farmId = farmId,
                            type = TaskType.OBSERVATION,
                            notes = obsNotes,
                            performedAt = ZonedDateTime.now().toString()
                        )
                    )
                } catch (_: Exception) {}

                _uiState.update { state ->
                    val logs = state.checkUpState.observedLogs + log
                    state.copy(
                        checkUpState = state.checkUpState.copy(observedLogs = logs, isAddLogOpen = false),
                        latestDiagnosisResult = evalResult,
                        isDiagnosisResultOpen = true
                    )
                }
            } catch (_: Exception) {}
        }
    }

    fun closeDiagnosisResult() {
        _uiState.update { it.copy(isDiagnosisResultOpen = false) }
    }

    // ─── Harvest Tab Actions ────────────────────────────────────────────────

    fun setHarvestModalOpen(isOpen: Boolean) {
        _uiState.update { state ->
            state.copy(harvestState = state.harvestState.copy(isHarvestModalOpen = isOpen))
        }
    }

    fun recordHarvest(plotId: String, yieldKg: Float, notes: String?, isFinalHarvest: Boolean) {
        val farmId = _uiState.value.activeFarmId
        val plot = _uiState.value.planState.rawPlots.firstOrNull { it.id == plotId }
        val cropName = plot?.cropName ?: "Produce"

        val harvestRecord = HarvestRecord(
            id = UUID.randomUUID().toString(),
            farmId = farmId,
            plotId = plotId,
            cropName = cropName,
            yieldKg = yieldKg,
            harvestedAt = LocalDate.now().toString(),
            notes = notes
        )

        viewModelScope.launch {
            harvestRepository.recordHarvest(harvestRecord)
            if (isFinalHarvest) {
                cropPlotRepository.upsertPlot(plot?.copy(cropName = null, plantedDate = null) ?: return@launch)
            }
            _uiState.update { state ->
                state.copy(
                    harvestState = state.harvestState.copy(
                        isHarvestModalOpen = false,
                        harvestCount = state.harvestState.harvestCount + 1,
                        totalYieldKg = state.harvestState.totalYieldKg + yieldKg
                    )
                )
            }
        }
    }
}
