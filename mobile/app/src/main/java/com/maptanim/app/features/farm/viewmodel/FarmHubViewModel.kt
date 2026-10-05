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
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

/**
 * Unified UI State for the Farm Hub, combining Plan, Guide, CheckUp, and Harvest tabs.
 */
data class FarmHubUiState(
    val activeFarmId: String = "",
    val farmName: String = "My Farm",
    val selectedTopTab: TopTab = TopTab.PLAN,
    val planState: FarmHubPlanState = FarmHubPlanState(),
    val guideState: FarmHubGuideState = FarmHubGuideState(),
    val checkUpState: FarmHubCheckUpState = FarmHubCheckUpState(),
    val harvestState: FarmHubHarvestState = FarmHubHarvestState(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * FarmHubViewModel — Consolidated Hub ViewModel coordinating the 4 core agricultural pillars:
 * 1. PlanTab (spatial layout, bed sizing, basketball court reference)
 * 2. GuideTab (agronomic rules, growth stages, daily care)
 * 3. CheckUpTab (observations, pest/disease logging, crop health)
 * 4. HarvestTab (readiness, yield records, post-harvest protocol)
 */
class FarmHubViewModel(
    private val cropPlotRepository: CropPlotRepository = RepositoryProvider.cropPlotRepository,
    private val cropZoneRepository: CropZoneRepository = RepositoryProvider.cropZoneRepository,
    private val cropRepository: CropRepository = RepositoryProvider.cropRepository,
    private val taskRepository: TaskRepository = RepositoryProvider.taskRepository,
    private val cropLogRepository: CropLogRepository = RepositoryProvider.cropLogRepository,
    private val harvestRepository: HarvestRepository = RepositoryProvider.harvestRepository,
    private val activityRepository: ActivityRepository = RepositoryProvider.activityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FarmHubUiState())
    val uiState: StateFlow<FarmHubUiState> = _uiState.asStateFlow()

    private val undoStack = ArrayDeque<EditAction>()
    private val redoStack = ArrayDeque<EditAction>()

    init {
        resolveActiveFarm()
        observePlotsAndZones()
    }

    fun selectTopTab(tab: TopTab) {
        _uiState.update { it.copy(selectedTopTab = tab) }
    }

    // ─── Farm Resolution & Data Observation ──────────────────────────────────

    private fun resolveActiveFarm() {
        viewModelScope.launch {
            val user = SupabaseClient.client.auth.currentUserOrNull()
            val localFarm = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                RepositoryProvider.getDatabase()?.farmDao()?.getActiveFarm()
            }
            val farmId = localFarm?.id ?: user?.id?.takeIf { it.isNotBlank() } ?: "farm-default"
            val farmName = localFarm?.farmName ?: "My Farm"
            _uiState.update { it.copy(activeFarmId = farmId, farmName = farmName) }
        }
    }

    private fun observePlotsAndZones() {
        val farmId = _uiState.value.activeFarmId
        viewModelScope.launch {
            cropPlotRepository.observePlots(farmId).collect { plots ->
                val plotRenderData = plots.map { it.toRenderData() }
                _uiState.update { state ->
                    state.copy(
                        planState = state.planState.copy(
                            rawPlots = plots,
                            plots = plotRenderData,
                            selectedPlotId = state.planState.selectedPlotId ?: plots.firstOrNull()?.id
                        )
                    )
                }
            }
        }
    }

    // ─── Plan Tab Actions ───────────────────────────────────────────────────

    fun selectPlot(plotId: String?) {
        _uiState.update { state ->
            state.copy(planState = state.planState.copy(selectedPlotId = plotId))
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
