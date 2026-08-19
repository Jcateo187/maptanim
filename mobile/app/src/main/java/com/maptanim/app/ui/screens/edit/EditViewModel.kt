package com.maptanim.app.ui.screens.edit

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.data.remote.SupabaseClient
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
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import com.maptanim.app.data.repository.RepositoryProvider

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
    private val cropPlotRepository: CropPlotRepository = RepositoryProvider.cropPlotRepository,
    private val cropZoneRepository: CropZoneRepository = RepositoryProvider.cropZoneRepository,
    private val farmObjectRepository: FarmObjectRepository = RepositoryProvider.farmObjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditUiState())
    val uiState: StateFlow<EditUiState> = _uiState.asStateFlow()

    private val undoStack = ArrayDeque<EditAction>()
    private val redoStack = ArrayDeque<EditAction>()

    /** Resolved active farm ID — matches HomeViewModel's resolution logic */
    private var activeFarmId: String = "farm-1"

    private var farmLayoutJob: kotlinx.coroutines.Job? = null

    init {
        resolveActiveFarmId()
    }

    fun refresh() {
        resolveActiveFarmId()
    }

    private fun resolveActiveFarmId() {
        val user = try { SupabaseClient.client.auth.currentUserOrNull() } catch (_: Exception) { null }
        if (user != null) {
            viewModelScope.launch {
                val savedActiveId = com.maptanim.app.core.preferences.FarmPreferencesManager.getInstance().getActiveFarmId(user.id)
                val farms = RepositoryProvider.farmRepository.observeFarms(user.id).firstOrNull()
                val farm = farms?.firstOrNull { it.id == savedActiveId } ?: farms?.firstOrNull()
                activeFarmId = farm?.id ?: savedActiveId ?: "farm_${user.id.take(8)}"
                loadFarmLayout(activeFarmId)
            }
        } else {
            val savedActiveId = com.maptanim.app.core.preferences.FarmPreferencesManager.getInstance().getActiveFarmId("guest")
            activeFarmId = savedActiveId ?: "farm-1"
            loadFarmLayout(activeFarmId)
        }
    }

    private fun loadFarmLayout(farmId: String) {
        farmLayoutJob?.cancel()
        farmLayoutJob = viewModelScope.launch {
            cropPlotRepository.observePlots(farmId).collect { plots ->
                val renderPlots = plots.map { it.toRenderData() }
                val zones = plots.map { plot ->
                    val zone = CropZoneRenderData(
                        id = "zone-${plot.id}",
                        plotId = plot.id,
                        cropName = plot.cropName,
                        offsetX = 0.0f,
                        offsetY = 0.0f,
                        widthM = plot.widthM,
                        heightM = plot.heightM,
                        spacingM = 1.0f
                    )
                    zone.copy(
                        plantInstances = PlantInstanceGenerator.generate(zone, plot.posX, plot.posY)
                    )
                }
                _uiState.update { state ->
                    state.copy(
                        editedPlots = plots,
                        plots = renderPlots,
                        cropZones = zones,
                        isLoading = false,
                        hasUnsavedChanges = false
                    )
                }
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



    fun toggleResizeMode() {
        _uiState.update { it.copy(isResizeMode = !it.isResizeMode) }
    }

    fun selectPlot(plotId: String?) {
        val currentSelected = _uiState.value.selectedPlotId
        if (currentSelected != null && currentSelected != plotId) {
            revertIfOverlapping(currentSelected)
        }

        _uiState.update { state ->
            val isSamePlot = plotId != null && plotId == state.selectedPlotId
            state.copy(
                selectedPlotId = plotId,
                isResizeMode = if (isSamePlot) state.isResizeMode else false
            )
        }
    }

    fun deselect() {
        val currentSelected = _uiState.value.selectedPlotId
        if (currentSelected != null) {
            revertIfOverlapping(currentSelected)
        }
        _uiState.update { it.copy(selectedPlotId = null, selectedZoneId = null, isResizeMode = false) }
    }

    private fun revertIfOverlapping(plotId: String) {
        val startPos = plotDragStartPos[plotId]
        val currentPlots = _uiState.value.editedPlots
        val plot = currentPlots.firstOrNull { it.id == plotId }
        if (plot != null && startPos != null) {
            if (hasOverlap(plot.posX, plot.posY, plot.widthM, plot.heightM, plotId, currentPlots)) {
                val revertedPlots = currentPlots.map {
                    if (it.id == plotId) it.copy(posX = startPos.x, posY = startPos.y) else it
                }
                updatePlotsState(revertedPlots)
            }
        }
        plotDragStartPos.remove(plotId)
    }

    fun selectCropZone(zoneId: String?) {
        val zone = _uiState.value.cropZones.firstOrNull { it.id == zoneId }
        val newPlotId = zone?.plotId ?: _uiState.value.selectedPlotId
        _uiState.update { state ->
            val isSamePlot = newPlotId != null && newPlotId == state.selectedPlotId
            state.copy(
                selectedZoneId = zoneId,
                selectedPlotId = newPlotId,
                isResizeMode = if (isSamePlot) state.isResizeMode else false
            )
        }
    }

    private fun hasOverlap(x: Float, y: Float, w: Float, h: Float, ignorePlotId: String, plots: List<CropPlot>): Boolean {
        return plots.any { other ->
            if (other.id == ignorePlotId) false
            else x < (other.posX + other.widthM) && (x + w) > other.posX &&
                 y < (other.posY + other.heightM) && (y + h) > other.posY
        }
    }

    private val plotDragStartPos = mutableMapOf<String, Offset>()

    fun onPlotDragStart(plotId: String) {
        val plot = _uiState.value.editedPlots.firstOrNull { it.id == plotId } ?: return
        if (!plotDragStartPos.containsKey(plotId)) {
            plotDragStartPos[plotId] = Offset(plot.posX, plot.posY)
        }
        selectPlot(plotId)
    }

    fun onPlotDragEnd(plotId: String, isValidPlacement: Boolean = true) {
        // Finger release keeps plot in its current position so user can inspect or pan/zoom map.
        // Reversion triggers automatically when user taps away (deselect or select another plot).
    }

    fun movePlot(plotId: String, worldDelta: Offset) {
        val currentPlots = _uiState.value.editedPlots
        val plot = currentPlots.firstOrNull { it.id == plotId } ?: return
        val startPos = plotDragStartPos[plotId] ?: Offset(plot.posX, plot.posY)
        val oldPos = Offset(plot.posX, plot.posY)

        val maxX = (45.0f - plot.widthM).coerceAtLeast(0f)
        val maxY = (45.0f - plot.heightM).coerceAtLeast(0f)

        var targetX = startPos.x + worldDelta.x
        var targetY = startPos.y + worldDelta.y

        if (_uiState.value.isSnapEnabled) {
            val snapped = FarmCanvasRenderer.snapToGrid(Offset(targetX, targetY))
            targetX = snapped.x
            targetY = snapped.y
        }

        val newX = targetX.coerceIn(0f, maxX)
        val newY = targetY.coerceIn(0f, maxY)

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
        val safeW = newWidth.coerceIn(1.0f, 45.0f)
        val safeH = newHeight.coerceIn(1.0f, 45.0f)

        undoStack.addLast(EditAction.ResizePlot(plotId, plot.widthM, plot.heightM, safeW, safeH))
        redoStack.clear()

        val updatedPlots = currentPlots.map {
            if (it.id == plotId) it.copy(widthM = safeW, heightM = safeH) else it
        }
        updatePlotsState(updatedPlots)
    }

    private var initialPlotForResize: CropPlot? = null

    fun onHandleDragStart(plotId: String) {
        initialPlotForResize = _uiState.value.editedPlots.firstOrNull { it.id == plotId }
    }

    fun onHandleDragEnd() {
        initialPlotForResize = null
    }

    fun resizePlotByHandle(plotId: String, handle: com.maptanim.app.renderer.gesture.HandleType, totalWorldDelta: Offset) {
        val currentPlots = _uiState.value.editedPlots
        val basePlot = initialPlotForResize ?: currentPlots.firstOrNull { it.id == plotId } ?: return

        var newX = basePlot.posX
        var newY = basePlot.posY
        var newW = basePlot.widthM
        var newH = basePlot.heightM

        when (handle) {
            com.maptanim.app.renderer.gesture.HandleType.CORNER_TL -> {
                newX += totalWorldDelta.x
                newY += totalWorldDelta.y
                newW -= totalWorldDelta.x
                newH -= totalWorldDelta.y
            }
            com.maptanim.app.renderer.gesture.HandleType.CORNER_TR -> {
                newY += totalWorldDelta.y
                newW += totalWorldDelta.x
                newH -= totalWorldDelta.y
            }
            com.maptanim.app.renderer.gesture.HandleType.CORNER_BL -> {
                newX += totalWorldDelta.x
                newW -= totalWorldDelta.x
                newH += totalWorldDelta.y
            }
            com.maptanim.app.renderer.gesture.HandleType.CORNER_BR -> {
                newW += totalWorldDelta.x
                newH += totalWorldDelta.y
            }
            com.maptanim.app.renderer.gesture.HandleType.MID_TOP -> {
                newY += totalWorldDelta.y
                newH -= totalWorldDelta.y
            }
            com.maptanim.app.renderer.gesture.HandleType.MID_BOTTOM -> {
                newH += totalWorldDelta.y
            }
            com.maptanim.app.renderer.gesture.HandleType.MID_LEFT -> {
                newX += totalWorldDelta.x
                newW -= totalWorldDelta.x
            }
            com.maptanim.app.renderer.gesture.HandleType.MID_RIGHT -> {
                newW += totalWorldDelta.x
            }
            else -> {}
        }

        // Discrete 1m step snapping per MD 34 Section 10
        val safeW = newW.coerceIn(1.0f, 45.0f - basePlot.posX)
        val safeH = newH.coerceIn(1.0f, 45.0f - basePlot.posY)

        var roundedW = Math.round(safeW).toFloat().coerceAtLeast(1.0f)
        var roundedH = Math.round(safeH).toFloat().coerceAtLeast(1.0f)

        val safeX = newX.coerceIn(0f, 45.0f - roundedW)
        val safeY = newY.coerceIn(0f, 45.0f - roundedH)

        var roundedX = Math.round(safeX).toFloat().coerceIn(0f, 45.0f - roundedW)
        var roundedY = Math.round(safeY).toFloat().coerceIn(0f, 45.0f - roundedH)

        // Clamp expansion so crop zone cannot exceed/overlap into another crop zone
        while (hasOverlap(roundedX, roundedY, roundedW, roundedH, plotId, currentPlots)) {
            when (handle) {
                com.maptanim.app.renderer.gesture.HandleType.MID_RIGHT -> {
                    if (roundedW > 1.0f) roundedW -= 1.0f else break
                }
                com.maptanim.app.renderer.gesture.HandleType.MID_BOTTOM -> {
                    if (roundedH > 1.0f) roundedH -= 1.0f else break
                }
                com.maptanim.app.renderer.gesture.HandleType.MID_LEFT -> {
                    if (roundedW > 1.0f) { roundedW -= 1.0f; roundedX += 1.0f } else break
                }
                com.maptanim.app.renderer.gesture.HandleType.MID_TOP -> {
                    if (roundedH > 1.0f) { roundedH -= 1.0f; roundedY += 1.0f } else break
                }
                com.maptanim.app.renderer.gesture.HandleType.CORNER_BR -> {
                    if (roundedW > 1.0f) roundedW -= 1.0f
                    if (roundedH > 1.0f) roundedH -= 1.0f
                    if (roundedW == 1.0f && roundedH == 1.0f) break
                }
                com.maptanim.app.renderer.gesture.HandleType.CORNER_TL -> {
                    if (roundedW > 1.0f) { roundedW -= 1.0f; roundedX += 1.0f }
                    if (roundedH > 1.0f) { roundedH -= 1.0f; roundedY += 1.0f }
                    if (roundedW == 1.0f && roundedH == 1.0f) break
                }
                com.maptanim.app.renderer.gesture.HandleType.CORNER_TR -> {
                    if (roundedW > 1.0f) roundedW -= 1.0f
                    if (roundedH > 1.0f) { roundedH -= 1.0f; roundedY += 1.0f }
                    if (roundedW == 1.0f && roundedH == 1.0f) break
                }
                com.maptanim.app.renderer.gesture.HandleType.CORNER_BL -> {
                    if (roundedW > 1.0f) { roundedW -= 1.0f; roundedX += 1.0f }
                    if (roundedH > 1.0f) roundedH -= 1.0f
                    if (roundedW == 1.0f && roundedH == 1.0f) break
                }
                else -> break
            }
        }

        val updatedPlots = currentPlots.map {
            if (it.id == plotId) it.copy(posX = roundedX, posY = roundedY, widthM = roundedW, heightM = roundedH) else it
        }
        updatePlotsState(updatedPlots)
    }

    fun addDirectPlantingPlot(
        atWorldX: Float,
        atWorldY: Float,
        cropName: String = "Carrot",
        cropId: String = "carrot",
        initialW: Float = 1.0f,
        initialH: Float = 1.0f
    ): Boolean {
        val plotId = UUID.randomUUID().toString()
        val safeW = initialW.coerceIn(1.0f, 20.0f)
        val safeH = initialH.coerceIn(1.0f, 20.0f)
        val safeX = atWorldX.coerceIn(0f, (45.0f - safeW))
        val safeY = atWorldY.coerceIn(0f, (45.0f - safeH))

        // Reject placement if target location overlaps an existing crop zone
        if (hasOverlap(safeX, safeY, safeW, safeH, "", _uiState.value.editedPlots)) {
            return false
        }

        // Initial Drop creates 1x1 CropZone (or initialW x initialH when duplicating) per MD 34 Section 6 & 8
        val newPlot = CropPlot(
            id          = plotId,
            farmId      = activeFarmId,
            plotLabel   = cropName,
            cropName    = cropName,
            cropId      = cropId,
            soilType    = SoilType.LOAM,
            posX        = safeX,
            posY        = safeY,
            widthM      = safeW,
            heightM     = safeH,
            rotationDeg = 0f,
            plantedDate = java.time.LocalDate.now().toString(),
            isActive    = true,
            notes       = null,
            createdAt   = Instant.now().toString(),
            updatedAt   = Instant.now().toString()
        )
        val zone = CropZoneRenderData(
            id = "zone-$plotId",
            plotId = plotId,
            cropName = cropName,
            offsetX = 0.0f,
            offsetY = 0.0f,
            widthM = safeW,
            heightM = safeH,
            spacingM = 1.0f
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
                selectedZoneId = zone.id,
                hasUnsavedChanges = true,
                canUndo = undoStack.isNotEmpty(),
                canRedo = redoStack.isNotEmpty()
            )
        }
        return true
    }

    fun addPlot(atWorldX: Float, atWorldY: Float, farmId: String = activeFarmId) {
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
                selectedZoneId = if (state.selectedPlotId == plotId) null else state.selectedZoneId,
                isResizeMode = if (state.selectedPlotId == plotId) false else state.isResizeMode,
                hasUnsavedChanges = true,
                canUndo = undoStack.isNotEmpty(),
                canRedo = redoStack.isNotEmpty()
            )
        }
    }

    fun duplicatePlot(plotId: String) {
        val plot = _uiState.value.editedPlots.firstOrNull { it.id == plotId } ?: return
        val newX = (plot.posX + plot.widthM).coerceIn(0f, 45.0f - plot.widthM)
        val newY = plot.posY.coerceIn(0f, 45.0f - plot.heightM)
        addDirectPlantingPlot(
            atWorldX = newX,
            atWorldY = newY,
            cropName = plot.cropName ?: "Carrot",
            cropId = plot.cropId ?: "carrot",
            initialW = plot.widthM,
            initialH = plot.heightM
        )
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

    fun saveChanges(
        farmName: String,
        isGuest: Boolean,
        plantedDatesMap: Map<String, String>? = null,
        varietiesMap: Map<String, String>? = null,
        onSaveComplete: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val currentPlots = _uiState.value.editedPlots.map { plot ->
                val chosenDate = plantedDatesMap?.get(plot.id)
                val chosenVariety = varietiesMap?.get(plot.id)
                plot.copy(
                    plantedDate = if (!chosenDate.isNullOrBlank()) chosenDate else if (plot.plantedDate.isNullOrBlank()) java.time.LocalDate.now().toString() else plot.plantedDate,
                    cropVariety = if (!chosenVariety.isNullOrBlank()) chosenVariety else plot.cropVariety
                )
            }
            cropPlotRepository.savePlots(currentPlots)

            val currentZones = _uiState.value.cropZones
            val domainZones = currentZones.map { zoneData ->
                com.maptanim.app.domain.model.CropZone(
                    id = zoneData.id,
                    plotId = zoneData.plotId,
                    cropName = zoneData.cropName,
                    cropId = zoneData.cropName?.lowercase(),
                    offsetX = zoneData.offsetX,
                    offsetY = zoneData.offsetY,
                    widthM = zoneData.widthM,
                    heightM = zoneData.heightM,
                    spacingM = zoneData.spacingM,
                    createdAt = Instant.now().toString(),
                    updatedAt = Instant.now().toString()
                )
            }
            cropZoneRepository.saveZones(domainZones)

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
        loadFarmLayout(activeFarmId)
        undoStack.clear()
        redoStack.clear()
        deselect()
    }

    fun saveAsDraft(onComplete: () -> Unit = {}) {
        if (!_uiState.value.hasUnsavedChanges) {
            onComplete()
            return
        }
        viewModelScope.launch {
            val currentPlots = _uiState.value.editedPlots
            cropPlotRepository.savePlots(currentPlots)

            val currentZones = _uiState.value.cropZones
            val domainZones = currentZones.map { zoneData ->
                com.maptanim.app.domain.model.CropZone(
                    id = zoneData.id,
                    plotId = zoneData.plotId,
                    cropName = zoneData.cropName,
                    cropId = zoneData.cropName?.lowercase(),
                    offsetX = zoneData.offsetX,
                    offsetY = zoneData.offsetY,
                    widthM = zoneData.widthM,
                    heightM = zoneData.heightM,
                    spacingM = zoneData.spacingM,
                    createdAt = Instant.now().toString(),
                    updatedAt = Instant.now().toString()
                )
            }
            cropZoneRepository.saveZones(domainZones)

            _uiState.update { state ->
                state.copy(
                    hasUnsavedChanges = false
                )
            }
            onComplete()
        }
    }

    private fun updatePlotsState(updatedPlots: List<CropPlot>) {
        _uiState.update { state ->
            val updatedZones = updatedPlots.map { plot ->
                val existingZone = state.cropZones.firstOrNull { it.plotId == plot.id }
                val baseZone = existingZone?.copy(
                    cropName = plot.cropName,
                    offsetX = 0.0f,
                    offsetY = 0.0f,
                    widthM = plot.widthM,
                    heightM = plot.heightM,
                    spacingM = 1.0f
                ) ?: CropZoneRenderData(
                    id = "zone-${plot.id}",
                    plotId = plot.id,
                    cropName = plot.cropName,
                    offsetX = 0.0f,
                    offsetY = 0.0f,
                    widthM = plot.widthM,
                    heightM = plot.heightM,
                    spacingM = 1.0f
                )
                baseZone.copy(
                    plantInstances = PlantInstanceGenerator.generate(baseZone, plot.posX, plot.posY)
                )
            }
            state.copy(
                editedPlots = updatedPlots,
                plots = updatedPlots.map { it.toRenderData() },
                cropZones = updatedZones,
                hasUnsavedChanges = true,
                canUndo = undoStack.isNotEmpty(),
                canRedo = redoStack.isNotEmpty()
            )
        }
    }
}

