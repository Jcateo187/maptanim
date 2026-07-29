package com.maptanim.app.ui.screens.edit

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.EditTool
import com.maptanim.app.domain.model.SoilType
import com.maptanim.app.domain.repository.CropPlotRepository
import com.maptanim.app.domain.repository.CropZoneRepository
import com.maptanim.app.domain.repository.FarmObjectRepository
import com.maptanim.app.renderer.AssetLoader
import com.maptanim.app.renderer.PlantInstanceGenerator
import com.maptanim.app.renderer.canvas.FarmCanvasRenderer
import com.maptanim.app.renderer.model.CropZoneRenderData
import com.maptanim.app.renderer.model.PlotRenderData
import com.maptanim.app.renderer.model.toRenderData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

/**
 * Action type for Undo/Redo operations in Farm Editor.
 */
sealed interface EditAction {
    data class AddPlot(val plot: CropPlot) : EditAction
    data class MovePlot(val plotId: String, val oldPos: Offset, val newPos: Offset) : EditAction
    data class ResizePlot(val plotId: String, val oldW: Float, val oldH: Float, val newW: Float, val newH: Float) : EditAction
    data class DeletePlot(val plot: CropPlot) : EditAction
    data class ChangeSoil(val plotId: String, val oldSoil: SoilType, val newSoil: SoilType) : EditAction
}

/**
 * EditViewModel — Manages state, undo/redo stacks, auto-save draft, and DSS integration for FarmEditorScreen.
 */
class EditViewModel(
    private val cropPlotRepository: CropPlotRepository? = null,
    private val cropZoneRepository: CropZoneRepository? = null,
    private val farmObjectRepository: FarmObjectRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditUiState())
    val uiState: StateFlow<EditUiState> = _uiState.asStateFlow()

    private val undoStack = ArrayDeque<EditAction>()
    private val redoStack = ArrayDeque<EditAction>()

    init {
        loadFarmLayout("farm-1")
    }

    private fun loadFarmLayout(farmId: String) {
        viewModelScope.launch {
            cropPlotRepository?.observePlots(farmId)?.collect { plots ->
                _uiState.update { state ->
                    state.copy(
                        editedPlots = plots,
                        plots = plots.map { it.toRenderData() },
                        isLoading = false
                    )
                }
            } ?: run {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun selectTool(tool: EditTool) {
        _uiState.update { state ->
            val keepSelection = tool == EditTool.ADD_PLANT || tool == EditTool.DELETE || tool == EditTool.SELECT_MOVE
            state.copy(
                activeTool = tool,
                selectedPlotId = if (keepSelection) state.selectedPlotId else null
            )
        }
    }

    fun selectSoilType(soilType: SoilType) {
        _uiState.update { it.copy(activeSoilType = soilType) }
    }

    fun selectPlot(plotId: String?) {
        _uiState.update { it.copy(selectedPlotId = plotId) }
    }

    fun deselect() {
        _uiState.update { it.copy(selectedPlotId = null, selectedZoneId = null) }
    }

    fun selectCropZone(zoneId: String?) {
        val zone = _uiState.value.cropZones.firstOrNull { it.id == zoneId }
        _uiState.update { state ->
            state.copy(
                selectedZoneId = zoneId,
                selectedPlotId = zone?.plotId ?: state.selectedPlotId
            )
        }
    }

    fun movePlot(plotId: String, worldDelta: Offset) {
        val currentPlots = _uiState.value.editedPlots
        val plot = currentPlots.firstOrNull { it.id == plotId } ?: return
        val oldPos = Offset(plot.posX, plot.posY)

        val maxX = (30.0f - plot.widthM).coerceAtLeast(0f)
        val maxY = (30.0f - plot.heightM).coerceAtLeast(0f)

        var newX = (plot.posX + worldDelta.x).coerceIn(0f, maxX)
        var newY = (plot.posY + worldDelta.y).coerceIn(0f, maxY)

        if (_uiState.value.isSnapEnabled) {
            val snapped = FarmCanvasRenderer.snapToGrid(Offset(newX, newY))
            newX = snapped.x.coerceIn(0f, maxX)
            newY = snapped.y.coerceIn(0f, maxY)
        }

        val updatedPlots = currentPlots.map {
            if (it.id == plotId) it.copy(posX = newX, posY = newY) else it
        }

        undoStack.addLast(EditAction.MovePlot(plotId, oldPos, Offset(newX, newY)))
        redoStack.clear()

        updatePlotsState(updatedPlots)
    }

    fun resizePlot(plotId: String, newWidth: Float, newHeight: Float) {
        val currentPlots = _uiState.value.editedPlots
        val plot = currentPlots.firstOrNull { it.id == plotId } ?: return
        val safeW = newWidth.coerceIn(1.0f, 20.0f)
        val safeH = newHeight.coerceIn(1.0f, 20.0f)

        undoStack.addLast(EditAction.ResizePlot(plotId, plot.widthM, plot.heightM, safeW, safeH))
        redoStack.clear()

        val updatedPlots = currentPlots.map {
            if (it.id == plotId) it.copy(widthM = safeW, heightM = safeH) else it
        }
        updatePlotsState(updatedPlots)
    }

    fun addDirectPlantingPlot(atWorldX: Float, atWorldY: Float, cropName: String = "Carrot", cropId: String = "carrot") {
        val plotId = UUID.randomUUID().toString()
        val safeX = atWorldX.coerceIn(0f, 27.5f)
        val safeY = atWorldY.coerceIn(0f, 28.0f)

        val newPlot = CropPlot(
            id          = plotId,
            farmId      = "farm-1",
            plotLabel   = cropName,
            cropName    = cropName,
            cropId      = cropId,
            soilType    = SoilType.LOAM,
            posX        = safeX,
            posY        = safeY,
            widthM      = 2.5f,
            heightM     = 2.0f,
            rotationDeg = 0f,
            plantedDate = Instant.now().toString(),
            isActive    = true,
            notes       = null,
            createdAt   = Instant.now().toString(),
            updatedAt   = Instant.now().toString()
        )
        val zone = CropZoneRenderData(
            id = "zone-$plotId",
            plotId = plotId,
            cropName = cropName,
            offsetX = 0.2f,
            offsetY = 0.2f,
            widthM = 2.1f,
            heightM = 1.6f,
            spacingM = 0.4f
        )
        val zoneWithPlants = zone.copy(
            plantInstances = PlantInstanceGenerator.generate(zone, safeX, safeY)
        )

        undoStack.addLast(EditAction.AddPlot(newPlot))
        redoStack.clear()

        val updatedPlots = _uiState.value.editedPlots + newPlot
        val updatedZones = _uiState.value.cropZones + zoneWithPlants

        _uiState.update { state ->
            state.copy(
                editedPlots = updatedPlots,
                plots = updatedPlots.map { it.toRenderData() },
                cropZones = updatedZones,
                selectedPlotId = plotId,
                hasUnsavedChanges = true
            )
        }
    }

    fun addPlot(atWorldX: Float, atWorldY: Float, farmId: String = "farm-1") {
        addDirectPlantingPlot(atWorldX, atWorldY, "Carrot", "carrot")
    }

    fun deletePlot(plotId: String) {
        val currentPlots = _uiState.value.editedPlots
        val plotToDelete = currentPlots.firstOrNull { it.id == plotId } ?: return

        undoStack.addLast(EditAction.DeletePlot(plotToDelete))
        redoStack.clear()

        val updatedPlots = currentPlots.filter { it.id != plotId }
        val updatedZones = _uiState.value.cropZones.filter { it.plotId != plotId }

        _uiState.update { state ->
            state.copy(
                editedPlots = updatedPlots,
                plots = updatedPlots.map { it.toRenderData() },
                cropZones = updatedZones,
                selectedPlotId = if (state.selectedPlotId == plotId) null else state.selectedPlotId,
                hasUnsavedChanges = true
            )
        }
    }

    fun duplicatePlot(plotId: String) {
        val plot = _uiState.value.editedPlots.firstOrNull { it.id == plotId } ?: return
        val newX = (plot.posX + 1.0f).coerceIn(0f, 27.5f)
        val newY = (plot.posY + 1.0f).coerceIn(0f, 28.0f)
        addDirectPlantingPlot(newX, newY, plot.cropName ?: "Carrot", plot.cropId ?: "carrot")
    }

    fun paintSoil(plotId: String) {
        val currentPlots = _uiState.value.editedPlots
        val plot = currentPlots.firstOrNull { it.id == plotId } ?: return
        val newSoil = _uiState.value.activeSoilType

        undoStack.addLast(EditAction.ChangeSoil(plotId, plot.soilType, newSoil))
        redoStack.clear()

        val updatedPlots = currentPlots.map {
            if (it.id == plotId) it.copy(soilType = newSoil) else it
        }
        updatePlotsState(updatedPlots)
    }

    fun addTrellis(plotId: String) {
        val plot = _uiState.value.editedPlots.firstOrNull { it.id == plotId } ?: return
        val trellisObj = com.maptanim.app.renderer.model.FarmObjectRenderData(
            id = "trellis-${plot.id}",
            objectType = com.maptanim.app.domain.model.FarmObjectType.TRELLIS,
            worldX = plot.posX,
            worldY = plot.posY,
            widthM = plot.widthM,
            heightM = plot.heightM,
            attachedPlotId = plot.id
        )
        _uiState.update { state ->
            state.copy(
                farmObjects = state.farmObjects + trellisObj,
                hasUnsavedChanges = true
            )
        }
    }

    fun undo() {
        if (undoStack.isEmpty()) return
        val action = undoStack.removeLast()
        val currentPlots = _uiState.value.editedPlots.toMutableList()

        when (action) {
            is EditAction.AddPlot -> currentPlots.removeAll { it.id == action.plot.id }
            is EditAction.MovePlot -> {
                val idx = currentPlots.indexOfFirst { it.id == action.plotId }
                if (idx != -1) currentPlots[idx] = currentPlots[idx].copy(posX = action.oldPos.x, posY = action.oldPos.y)
            }
            is EditAction.ResizePlot -> {
                val idx = currentPlots.indexOfFirst { it.id == action.plotId }
                if (idx != -1) currentPlots[idx] = currentPlots[idx].copy(widthM = action.oldW, heightM = action.oldH)
            }
            is EditAction.DeletePlot -> currentPlots.add(action.plot)
            is EditAction.ChangeSoil -> {
                val idx = currentPlots.indexOfFirst { it.id == action.plotId }
                if (idx != -1) currentPlots[idx] = currentPlots[idx].copy(soilType = action.oldSoil)
            }
        }
        redoStack.addLast(action)
        updatePlotsState(currentPlots)
    }

    fun redo() {
        if (redoStack.isEmpty()) return
        val action = redoStack.removeLast()
        val currentPlots = _uiState.value.editedPlots.toMutableList()

        when (action) {
            is EditAction.AddPlot -> currentPlots.add(action.plot)
            is EditAction.MovePlot -> {
                val idx = currentPlots.indexOfFirst { it.id == action.plotId }
                if (idx != -1) currentPlots[idx] = currentPlots[idx].copy(posX = action.newPos.x, posY = action.newPos.y)
            }
            is EditAction.ResizePlot -> {
                val idx = currentPlots.indexOfFirst { it.id == action.plotId }
                if (idx != -1) currentPlots[idx] = currentPlots[idx].copy(widthM = action.newW, heightM = action.newH)
            }
            is EditAction.DeletePlot -> currentPlots.removeAll { it.id == action.plot.id }
            is EditAction.ChangeSoil -> {
                val idx = currentPlots.indexOfFirst { it.id == action.plotId }
                if (idx != -1) currentPlots[idx] = currentPlots[idx].copy(soilType = action.newSoil)
            }
        }
        undoStack.addLast(action)
        updatePlotsState(currentPlots)
    }

    fun toggleGrid() {
        _uiState.update { it.copy(isGridEnabled = !it.isGridEnabled) }
    }

    fun toggleSnap() {
        _uiState.update { it.copy(isSnapEnabled = !it.isSnapEnabled) }
    }

    fun updateZoom(zoom: Float) {
        _uiState.update { it.copy(zoom = zoom) }
    }

    fun saveChanges(farmName: String, isGuest: Boolean, onSaveComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val currentPlots = _uiState.value.editedPlots
            currentPlots.forEach { plot ->
                cropPlotRepository?.upsertPlot(plot)
            }
            _uiState.update { state ->
                state.copy(
                    isSaving = false,
                    hasUnsavedChanges = false
                )
            }
            onSaveComplete()
        }
    }

    fun discardChanges() {
        loadFarmLayout("farm-1")
        undoStack.clear()
        redoStack.clear()
        deselect()
    }

    private fun updatePlotsState(updatedPlots: List<CropPlot>) {
        _uiState.update { state ->
            state.copy(
                editedPlots = updatedPlots,
                plots = updatedPlots.map { it.toRenderData() },
                hasUnsavedChanges = true,
                canUndo = undoStack.isNotEmpty(),
                canRedo = redoStack.isNotEmpty()
            )
        }
    }
}
