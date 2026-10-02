package com.maptanim.app.ui.screens.edit

import androidx.compose.ui.geometry.Offset
import kotlin.math.abs
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.data.remote.SupabaseClient
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.EditTool
import com.maptanim.app.domain.model.SoilType
import com.maptanim.app.domain.repository.CropPlotRepository
import com.maptanim.app.domain.repository.CropRepository
import com.maptanim.app.domain.repository.CropZoneRepository
import com.maptanim.app.renderer.canvas.TopDownProjection
import com.maptanim.app.renderer.model.CropZoneRenderData
import com.maptanim.app.renderer.model.PlotRenderData
import com.maptanim.app.renderer.model.toRenderData
import com.maptanim.app.ui.components.editcomponents.croptray.AVAILABLE_CROP_CATALOG
import com.maptanim.app.ui.components.editcomponents.croptray.CropOption
import com.maptanim.app.ui.components.editcomponents.croptray.toCropOption
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
    data class DeletePlot(val plot: CropPlot, val childZones: List<CropZoneRenderData> = emptyList()) : EditAction
    data class ChangeSoil(val plotId: String, val oldSoil: SoilType, val newSoil: SoilType) : EditAction
    data class ModifyPlot(val oldPlot: CropPlot, val newPlot: CropPlot) : EditAction
    data class AddCropToBed(val zone: CropZoneRenderData) : EditAction
    data class MoveCropToBed(val zoneId: String, val oldPlotId: String, val oldOffset: Offset, val newPlotId: String, val newOffset: Offset) : EditAction
    data class RemoveCropFromBed(val zone: CropZoneRenderData) : EditAction
    data class ResizeCropZone(val zoneId: String, val oldOffset: Offset, val oldW: Float, val oldH: Float, val newOffset: Offset, val newW: Float, val newH: Float) : EditAction
}

/**
 * EditViewModel — Manages state, undo/redo stacks, auto-save draft, and DSS integration for FarmEditorScreen.
 */
class EditViewModel(
    private val cropPlotRepository: CropPlotRepository = RepositoryProvider.cropPlotRepository,
    private val cropZoneRepository: CropZoneRepository = RepositoryProvider.cropZoneRepository,
    private val cropRepository: CropRepository = RepositoryProvider.cropRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditUiState())
    val uiState: StateFlow<EditUiState> = _uiState.asStateFlow()

    private val undoStack = ArrayDeque<EditAction>()
    private val redoStack = ArrayDeque<EditAction>()

    private var initialPlotsSnapshot: List<CropPlot> = emptyList()
    private var initialZonesSnapshot: List<CropZoneRenderData> = emptyList()
    private val runDssEvaluationUseCase: com.maptanim.app.domain.usecase.RunDssEvaluationUseCase = com.maptanim.app.domain.usecase.RunDssEvaluationUseCase()

    /** Resolved active farm ID — matches HomeViewModel's resolution logic */
    private var activeFarmId: String = "farm-1"

    private var farmLayoutJob: kotlinx.coroutines.Job? = null

    init {
        resolveActiveFarmId()
        observeAvailableCrops()
    }

    fun refresh() {
        resolveActiveFarmId()
        refreshCropsFromRemote()
    }

    private fun observeAvailableCrops() {
        viewModelScope.launch {
            cropRepository.observeAllCrops().collect { crops ->
                if (crops.isNotEmpty()) {
                    // Strictly adhere to the 15 canonical crops to prevent tray bloat or duplicates
                    val canonicalList = AVAILABLE_CROP_CATALOG.map { catalogCrop: CropOption ->
                        val dbCrop = crops.firstOrNull { c ->
                            c.name.equals(catalogCrop.name, ignoreCase = true) ||
                            (catalogCrop.localName != null && c.localName?.equals(catalogCrop.localName, ignoreCase = true) == true) ||
                            c.id.equals(catalogCrop.id, ignoreCase = true)
                        }
                        if (dbCrop != null && !dbCrop.imageUrl.isNullOrBlank()) {
                            catalogCrop.copy(imageUrl = dbCrop.imageUrl)
                        } else {
                            catalogCrop
                        }
                    }
                    _uiState.update { it.copy(availableCrops = canonicalList) }
                }
            }
        }
        viewModelScope.launch {
            try {
                cropRepository.refreshCrops()
            } catch (_: Exception) {}
        }
    }

    fun refreshCropsFromRemote() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncingCrops = true) }
            try {
                cropRepository.refreshCrops()
            } finally {
                _uiState.update { it.copy(isSyncingCrops = false) }
            }
        }
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
                val plotIds = plots.map { it.id }

                val existingDomainZones = try {
                    cropZoneRepository.observeZonesByPlotIds(plotIds).firstOrNull() ?: emptyList()
                } catch (_: Exception) {
                    emptyList()
                }

                val zones = if (existingDomainZones.isNotEmpty()) {
                    existingDomainZones.mapNotNull { domainZone ->
                        val parentPlot = plots.firstOrNull { it.id == domainZone.plotId } ?: return@mapNotNull null
                        val zone = CropZoneRenderData(
                            id = domainZone.id,
                            plotId = domainZone.plotId,
                            cropName = domainZone.cropName ?: "Crop",
                            offsetX = domainZone.offsetX,
                            offsetY = domainZone.offsetY,
                            widthM = domainZone.widthM,
                            heightM = domainZone.heightM,
                            spacingM = domainZone.spacingM
                        )
                        zone
                    }
                } else {
                    val fallbackZones = mutableListOf<CropZoneRenderData>()
                    plots.forEach { plot ->
                        val cropsFromNotes = if (plot.notes?.startsWith("Crops: ") == true) {
                            plot.notes.removePrefix("Crops: ").split(", ").map { it.trim() }.filter { it.isNotBlank() }
                        } else emptyList()

                        if (cropsFromNotes.isNotEmpty()) {
                            cropsFromNotes.forEachIndexed { idx, cropName ->
                                fallbackZones.add(
                                    CropZoneRenderData(
                                        id = "zone-${plot.id}-$idx",
                                        plotId = plot.id,
                                        cropName = cropName,
                                        offsetX = (idx * 0.5f).coerceIn(0f, (plot.widthM - 0.5f).coerceAtLeast(0f)),
                                        offsetY = 0f,
                                        widthM = 0.5f,
                                        heightM = 0.5f,
                                        spacingM = 0.25f
                                    )
                                )
                            }
                        } else if (!plot.cropName.isNullOrBlank() && !plot.cropName.equals("Bed", ignoreCase = true) && plot.cropId != "bed") {
                            fallbackZones.add(
                                CropZoneRenderData(
                                    id = "zone-${plot.id}",
                                    plotId = plot.id,
                                    cropName = plot.cropName,
                                    offsetX = 0.0f,
                                    offsetY = 0.0f,
                                    widthM = plot.widthM,
                                    heightM = plot.heightM,
                                    spacingM = 0.5f
                                )
                            )
                        }
                    }
                    fallbackZones
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
                initialPlotsSnapshot = plots
                initialZonesSnapshot = zones
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
            val isSamePlot = plotId != null && plotId == state.selectedPlotId && state.selectedZoneId == null
            state.copy(
                selectedPlotId = plotId,
                selectedZoneId = null,
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
            val isSameZone = zoneId != null && zoneId == state.selectedZoneId
            state.copy(
                selectedZoneId = zoneId,
                selectedPlotId = newPlotId,
                isResizeMode = if (isSameZone) state.isResizeMode else false
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
        plotDragStartPos[plotId] = Offset(plot.posX, plot.posY)
        selectPlot(plotId)
    }

    fun onPlotDragEnd(plotId: String, isValidPlacement: Boolean = true) {
        plotDragStartPos.remove(plotId)
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
            val snapped = TopDownProjection.snapToGrid(Offset(targetX, targetY))
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

        // Bed cannot shrink smaller than the bounding box of its existing crops (User rules 4 & 5)
        val bedCrops = _uiState.value.cropZones.filter { it.plotId == plotId && !it.cropName.isNullOrBlank() && !it.cropName.equals("Bed", ignoreCase = true) }
        val minCropW = (bedCrops.maxOfOrNull { it.offsetX + it.widthM } ?: 1.0f).coerceAtLeast(1.0f)
        val minCropH = (bedCrops.maxOfOrNull { it.offsetY + it.heightM } ?: 1.0f).coerceAtLeast(1.0f)
        val minCropOffsetX = bedCrops.minOfOrNull { it.offsetX } ?: 0f
        val minCropOffsetY = bedCrops.minOfOrNull { it.offsetY } ?: 0f

        val safeW = newW.coerceIn(Math.ceil(minCropW.toDouble()).toFloat(), 45.0f - basePlot.posX)
        val safeH = newH.coerceIn(Math.ceil(minCropH.toDouble()).toFloat(), 45.0f - basePlot.posY)

        var roundedW = Math.round(safeW).toFloat().coerceAtLeast(Math.ceil(minCropW.toDouble()).toFloat())
        var roundedH = Math.round(safeH).toFloat().coerceAtLeast(Math.ceil(minCropH.toDouble()).toFloat())

        val maxX = basePlot.posX + minCropOffsetX
        val maxY = basePlot.posY + minCropOffsetY

        val safeX = newX.coerceIn(0f, maxX.coerceAtMost(45.0f - roundedW))
        val safeY = newY.coerceIn(0f, maxY.coerceAtMost(45.0f - roundedH))

        var roundedX = Math.round(safeX).toFloat().coerceIn(0f, maxX.coerceAtMost(45.0f - roundedW))
        var roundedY = Math.round(safeY).toFloat().coerceIn(0f, maxY.coerceAtMost(45.0f - roundedH))

        // Clamp expansion so crop zone cannot exceed/overlap into another crop zone
        while (hasOverlap(roundedX, roundedY, roundedW, roundedH, plotId, currentPlots)) {
            when (handle) {
                com.maptanim.app.renderer.gesture.HandleType.MID_RIGHT -> {
                    if (roundedW > Math.ceil(minCropW.toDouble()).toFloat()) roundedW -= 1.0f else break
                }
                com.maptanim.app.renderer.gesture.HandleType.MID_BOTTOM -> {
                    if (roundedH > Math.ceil(minCropH.toDouble()).toFloat()) roundedH -= 1.0f else break
                }
                com.maptanim.app.renderer.gesture.HandleType.MID_LEFT -> {
                    if (roundedW > Math.ceil(minCropW.toDouble()).toFloat()) { roundedW -= 1.0f; roundedX += 1.0f } else break
                }
                com.maptanim.app.renderer.gesture.HandleType.MID_TOP -> {
                    if (roundedH > Math.ceil(minCropH.toDouble()).toFloat()) { roundedH -= 1.0f; roundedY += 1.0f } else break
                }
                com.maptanim.app.renderer.gesture.HandleType.CORNER_BR -> {
                    if (roundedW > Math.ceil(minCropW.toDouble()).toFloat()) roundedW -= 1.0f
                    if (roundedH > Math.ceil(minCropH.toDouble()).toFloat()) roundedH -= 1.0f
                    if (roundedW <= Math.ceil(minCropW.toDouble()).toFloat() && roundedH <= Math.ceil(minCropH.toDouble()).toFloat()) break
                }
                com.maptanim.app.renderer.gesture.HandleType.CORNER_TL -> {
                    if (roundedW > Math.ceil(minCropW.toDouble()).toFloat()) { roundedW -= 1.0f; roundedX += 1.0f }
                    if (roundedH > Math.ceil(minCropH.toDouble()).toFloat()) { roundedH -= 1.0f; roundedY += 1.0f }
                    if (roundedW <= Math.ceil(minCropW.toDouble()).toFloat() && roundedH <= Math.ceil(minCropH.toDouble()).toFloat()) break
                }
                com.maptanim.app.renderer.gesture.HandleType.CORNER_TR -> {
                    if (roundedW > Math.ceil(minCropW.toDouble()).toFloat()) roundedW -= 1.0f
                    if (roundedH > Math.ceil(minCropH.toDouble()).toFloat()) { roundedH -= 1.0f; roundedY += 1.0f }
                    if (roundedW <= Math.ceil(minCropW.toDouble()).toFloat() && roundedH <= Math.ceil(minCropH.toDouble()).toFloat()) break
                }
                com.maptanim.app.renderer.gesture.HandleType.CORNER_BL -> {
                    if (roundedW > Math.ceil(minCropW.toDouble()).toFloat()) { roundedW -= 1.0f; roundedX += 1.0f }
                    if (roundedH > Math.ceil(minCropH.toDouble()).toFloat()) roundedH -= 1.0f
                    if (roundedW <= Math.ceil(minCropW.toDouble()).toFloat() && roundedH <= Math.ceil(minCropH.toDouble()).toFloat()) break
                }
                else -> break
            }
        }

        val updatedPlots = currentPlots.map {
            if (it.id == plotId) it.copy(posX = roundedX, posY = roundedY, widthM = roundedW, heightM = roundedH) else it
        }
        updatePlotsState(updatedPlots)
    }

    // ── Individual Crop Zone Drag & Resize (User rules 6, 7, 8, 9) ─────────

    private var initialZoneForResize: CropZoneRenderData? = null

    fun onZoneHandleDragStart(zoneId: String) {
        initialZoneForResize = _uiState.value.cropZones.firstOrNull { it.id == zoneId }
    }

    fun onZoneHandleDragEnd() {
        val initial = initialZoneForResize
        if (initial != null) {
            val current = _uiState.value.cropZones.firstOrNull { it.id == initial.id }
            if (current != null && (current.offsetX != initial.offsetX || current.offsetY != initial.offsetY || current.widthM != initial.widthM || current.heightM != initial.heightM)) {
                undoStack.addLast(
                    EditAction.ResizeCropZone(
                        zoneId = initial.id,
                        oldOffset = Offset(initial.offsetX, initial.offsetY),
                        oldW = initial.widthM,
                        oldH = initial.heightM,
                        newOffset = Offset(current.offsetX, current.offsetY),
                        newW = current.widthM,
                        newH = current.heightM
                    )
                )
                redoStack.clear()
                _uiState.update { it.copy(canUndo = undoStack.isNotEmpty(), canRedo = redoStack.isNotEmpty()) }
            }
        }
        initialZoneForResize = null
    }

    fun resizeCropZoneByHandle(
        zoneId: String,
        handle: com.maptanim.app.renderer.gesture.HandleType,
        totalWorldDelta: Offset
    ) {
        val currentZones = _uiState.value.cropZones
        val baseZone = initialZoneForResize ?: currentZones.firstOrNull { it.id == zoneId } ?: return
        val parentBed = _uiState.value.editedPlots.firstOrNull { it.id == baseZone.plotId } ?: return

        var newOffsetX = baseZone.offsetX
        var newOffsetY = baseZone.offsetY
        var newW = baseZone.widthM
        var newH = baseZone.heightM

        when (handle) {
            com.maptanim.app.renderer.gesture.HandleType.CORNER_TL -> {
                newOffsetX += totalWorldDelta.x
                newOffsetY += totalWorldDelta.y
                newW -= totalWorldDelta.x
                newH -= totalWorldDelta.y
            }
            com.maptanim.app.renderer.gesture.HandleType.CORNER_TR -> {
                newOffsetY += totalWorldDelta.y
                newW += totalWorldDelta.x
                newH -= totalWorldDelta.y
            }
            com.maptanim.app.renderer.gesture.HandleType.CORNER_BL -> {
                newOffsetX += totalWorldDelta.x
                newW -= totalWorldDelta.x
                newH += totalWorldDelta.y
            }
            com.maptanim.app.renderer.gesture.HandleType.CORNER_BR -> {
                newW += totalWorldDelta.x
                newH += totalWorldDelta.y
            }
            com.maptanim.app.renderer.gesture.HandleType.MID_TOP -> {
                newOffsetY += totalWorldDelta.y
                newH -= totalWorldDelta.y
            }
            com.maptanim.app.renderer.gesture.HandleType.MID_BOTTOM -> {
                newH += totalWorldDelta.y
            }
            com.maptanim.app.renderer.gesture.HandleType.MID_LEFT -> {
                newOffsetX += totalWorldDelta.x
                newW -= totalWorldDelta.x
            }
            com.maptanim.app.renderer.gesture.HandleType.MID_RIGHT -> {
                newW += totalWorldDelta.x
            }
            else -> {}
        }

        // Clamp crop zone strictly within parent Bed Zone bounds (User rules 6 & 7)
        val minCropSize = 0.5f
        val maxCropW = parentBed.widthM
        val maxCropH = parentBed.heightM

        var clampedW = newW.coerceIn(minCropSize, maxCropW)
        var clampedH = newH.coerceIn(minCropSize, maxCropH)

        var clampedX = newOffsetX.coerceIn(0f, (parentBed.widthM - clampedW).coerceAtLeast(0f))
        var clampedY = newOffsetY.coerceIn(0f, (parentBed.heightM - clampedH).coerceAtLeast(0f))

        if (_uiState.value.isSnapEnabled) {
            clampedW = (Math.round(clampedW * 2f) / 2f).coerceIn(minCropSize, maxCropW)
            clampedH = (Math.round(clampedH * 2f) / 2f).coerceIn(minCropSize, maxCropH)
            clampedX = (Math.round(clampedX * 2f) / 2f).coerceIn(0f, (parentBed.widthM - clampedW).coerceAtLeast(0f))
            clampedY = (Math.round(clampedY * 2f) / 2f).coerceIn(0f, (parentBed.heightM - clampedH).coerceAtLeast(0f))
        }

        val updatedZone = baseZone.copy(
            offsetX = clampedX,
            offsetY = clampedY,
            widthM = clampedW,
            heightM = clampedH
        )
        _uiState.update { state ->
            state.copy(
                cropZones = state.cropZones.map { if (it.id == zoneId) updatedZone else it },
                hasUnsavedChanges = true
            )
        }
    }

    private val zoneDragStartOffset = mutableMapOf<String, Offset>()

    fun onCropZoneDragStart(zoneId: String) {
        val zone = _uiState.value.cropZones.firstOrNull { it.id == zoneId } ?: return
        if (!zoneDragStartOffset.containsKey(zoneId)) {
            zoneDragStartOffset[zoneId] = Offset(zone.offsetX, zone.offsetY)
        }
        selectCropZone(zoneId)
    }

    fun onCropZoneDragEnd(zoneId: String) {
        zoneDragStartOffset.remove(zoneId)
    }

    fun moveCropZone(zoneId: String, worldDelta: Offset) {
        val currentZones = _uiState.value.cropZones
        val targetZone = currentZones.firstOrNull { it.id == zoneId } ?: return
        val currentPlots = _uiState.value.editedPlots
        val parentBed = currentPlots.firstOrNull { it.id == targetZone.plotId } ?: return
        val startOffset = zoneDragStartOffset[zoneId] ?: Offset(targetZone.offsetX, targetZone.offsetY)

        var targetOffsetX = startOffset.x + worldDelta.x
        var targetOffsetY = startOffset.y + worldDelta.y

        if (_uiState.value.isSnapEnabled) {
            targetOffsetX = Math.round(targetOffsetX * 2f) / 2f
            targetOffsetY = Math.round(targetOffsetY * 2f) / 2f
        }

        // Check if dragged to another Bed (User rule 9)
        val cropWorldX = parentBed.posX + targetOffsetX
        val cropWorldY = parentBed.posY + targetOffsetY

        val targetOtherBed = currentPlots.firstOrNull { bed ->
            bed.id != parentBed.id &&
            cropWorldX >= bed.posX && (cropWorldX + targetZone.widthM) <= (bed.posX + bed.widthM + 0.3f) &&
            cropWorldY >= bed.posY && (cropWorldY + targetZone.heightM) <= (bed.posY + bed.heightM + 0.3f)
        }

        val effectiveBed = targetOtherBed ?: parentBed
        val effectiveOffsetX = if (targetOtherBed != null) {
            (cropWorldX - targetOtherBed.posX).coerceIn(0f, (targetOtherBed.widthM - targetZone.widthM).coerceAtLeast(0f))
        } else {
            targetOffsetX.coerceIn(0f, (parentBed.widthM - targetZone.widthM).coerceAtLeast(0f))
        }
        val effectiveOffsetY = if (targetOtherBed != null) {
            (cropWorldY - targetOtherBed.posY).coerceIn(0f, (targetOtherBed.heightM - targetZone.heightM).coerceAtLeast(0f))
        } else {
            targetOffsetY.coerceIn(0f, (parentBed.heightM - targetZone.heightM).coerceAtLeast(0f))
        }

        val updatedZone = targetZone.copy(
            plotId = effectiveBed.id,
            offsetX = effectiveOffsetX,
            offsetY = effectiveOffsetY
        )

        _uiState.update { state ->
            state.copy(
                cropZones = state.cropZones.map { if (it.id == zoneId) updatedZone else it },
                selectedPlotId = effectiveBed.id,
                selectedZoneId = zoneId,
                hasUnsavedChanges = true
            )
        }
    }

    fun duplicateCropZone(zoneId: String): Boolean {
        val targetZone = _uiState.value.cropZones.firstOrNull { it.id == zoneId } ?: return false
        val parentBed = _uiState.value.editedPlots.firstOrNull { it.id == targetZone.plotId } ?: return false

        val candX = (targetZone.offsetX + targetZone.widthM)
        val candY = targetZone.offsetY
        val fitsX = (candX + targetZone.widthM) <= parentBed.widthM
        val newOffset = if (fitsX) {
            Offset(candX, candY)
        } else {
            val candY2 = (targetZone.offsetY + targetZone.heightM)
            if ((candY2 + targetZone.heightM) <= parentBed.heightM) {
                Offset(0f, candY2)
            } else {
                Offset(
                    (targetZone.offsetX + 0.5f).coerceIn(0f, (parentBed.widthM - targetZone.widthM).coerceAtLeast(0f)),
                    (targetZone.offsetY + 0.5f).coerceIn(0f, (parentBed.heightM - targetZone.heightM).coerceAtLeast(0f))
                )
            }
        }

        return plantCropInBed(
            bedPlotId = parentBed.id,
            newCropName = targetZone.cropName ?: "Crop",
            newCropId = targetZone.cropName?.lowercase() ?: "crop",
            atWorldX = parentBed.posX + newOffset.x,
            atWorldY = parentBed.posY + newOffset.y
        )
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
        val isBed = cropId.equals("bed", ignoreCase = true) || cropName.equals("Bed", ignoreCase = true)
        val defaultW = if (isBed && initialW <= 1.0f) 2.0f else initialW
        val safeW = defaultW.coerceIn(1.0f, 20.0f)
        val safeH = initialH.coerceIn(1.0f, 20.0f)
        val safeX = atWorldX.coerceIn(0f, (45.0f - safeW))
        val safeY = atWorldY.coerceIn(0f, (45.0f - safeH))

        // Reject placement if target location overlaps an existing crop zone
        if (hasOverlap(safeX, safeY, safeW, safeH, "", _uiState.value.editedPlots)) {
            return false
        }

        val bedCount = _uiState.value.editedPlots.count { it.cropName == "Bed" || it.cropId == "bed" } + 1
        val finalPlotLabel = if (isBed) "Bed #$bedCount" else cropName
        val finalCropName = if (isBed) "Bed" else cropName
        val finalCropId = if (isBed) "bed" else cropId

        val newPlot = CropPlot(
            id          = plotId,
            farmId      = activeFarmId,
            plotLabel   = finalPlotLabel,
            cropName    = finalCropName,
            cropId      = finalCropId,
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

        val updatedPlots = _uiState.value.editedPlots + newPlot
        val updatedZones = if (isBed) {
            _uiState.value.cropZones
        } else {
            val zone = CropZoneRenderData(
                id = "zone-$plotId",
                plotId = plotId,
                cropName = finalCropName,
                offsetX = 0.0f,
                offsetY = 0.0f,
                widthM = safeW,
                heightM = safeH,
                spacingM = 1.0f
            )
            _uiState.value.cropZones + zone
        }

        undoStack.addLast(EditAction.AddPlot(newPlot))
        redoStack.clear()

        _uiState.update { state ->
            state.copy(
                editedPlots = updatedPlots,
                plots = updatedPlots.map { it.toRenderData() },
                cropZones = updatedZones,
                selectedPlotId = plotId,
                selectedZoneId = if (isBed) null else "zone-$plotId",
                hasUnsavedChanges = true,
                canUndo = undoStack.isNotEmpty(),
                canRedo = redoStack.isNotEmpty()
            )
        }
        return true
    }

    fun plantCropInBed(
        bedPlotId: String,
        newCropName: String,
        newCropId: String,
        atWorldX: Float? = null,
        atWorldY: Float? = null
    ): Boolean {
        val targetPlot = _uiState.value.plots.firstOrNull { it.id == bedPlotId }
        if (targetPlot == null) {
            _uiState.update { it.copy(dropFeedbackMessage = "⚠️ Hindi wasto ang lokasyon: Paki-lagay ang pananim sa loob ng isang Garden Bed.") }
            return false
        }

        // Filter existing zones in bed (ignore any dummy "Bed" entries)
        val existingZonesInBed = _uiState.value.cropZones.filter {
            it.plotId == bedPlotId && !it.cropName.isNullOrBlank() && !it.cropName.equals("Bed", ignoreCase = true)
        }

        // Allow multiple crops: minimum capacity of 8 crops per bed
        val maxCapacity = ((targetPlot.widthM * targetPlot.heightM * 4).toInt()).coerceAtLeast(8)
        if (existingZonesInBed.size >= maxCapacity) {
            _uiState.update { it.copy(dropFeedbackMessage = "⚠️ Puno na ang kama (Maximum bed capacity reached: $maxCapacity pananim).") }
            return false
        }

        // Crop zone dimensions: 1.0m for spacious beds, 0.5m for smaller beds or compact planting
        val cropW = if (targetPlot.widthM >= 2.0f && existingZonesInBed.size < targetPlot.widthM.toInt()) 1.0f else 0.5f
        val cropH = if (targetPlot.heightM >= 2.0f && existingZonesInBed.size < (targetPlot.widthM * targetPlot.heightM).toInt()) 1.0f else 0.5f

        // Determine relative position inside the bed
        var relX: Float
        var relY: Float
        if (atWorldX != null && atWorldY != null) {
            val rawRelX = (atWorldX - cropW / 2f) - targetPlot.posX
            val rawRelY = (atWorldY - cropH / 2f) - targetPlot.posY
            val step = 0.5f
            relX = (Math.round(rawRelX / step) * step).coerceIn(0f, (targetPlot.widthM - cropW).coerceAtLeast(0f))
            relY = (Math.round(rawRelY / step) * step).coerceIn(0f, (targetPlot.heightM - cropH).coerceAtLeast(0f))

            // Check if this spot is already occupied by an existing crop zone
            val isOccupied = existingZonesInBed.any {
                abs(it.offsetX - relX) < (cropW - 0.1f) && abs(it.offsetY - relY) < (cropH - 0.1f)
            }
            if (isOccupied) {
                // Find next free spot inside the bed
                var found = false
                var tx = 0f
                while (tx <= targetPlot.widthM - cropW && !found) {
                    var ty = 0f
                    while (ty <= targetPlot.heightM - cropH && !found) {
                        val collision = existingZonesInBed.any {
                            abs(it.offsetX - tx) < (cropW - 0.1f) && abs(it.offsetY - ty) < (cropH - 0.1f)
                        }
                        if (!collision) {
                            relX = tx
                            relY = ty
                            found = true
                        }
                        ty += 0.5f
                    }
                    tx += 0.5f
                }
                if (!found) {
                    _uiState.update { it.copy(dropFeedbackMessage = "⚠️ Walang bakanteng espasyo sa bed na ito para sa pananim.") }
                    return false
                }
            }
        } else {
            val cols = (targetPlot.widthM / cropW).toInt().coerceAtLeast(1)
            val index = existingZonesInBed.size
            val col = index % cols
            val row = index / cols
            relX = (col * cropW).coerceIn(0f, (targetPlot.widthM - cropW).coerceAtLeast(0f))
            relY = (row * cropH).coerceIn(0f, (targetPlot.heightM - cropH).coerceAtLeast(0f))
        }

        val zoneId = "cp-${UUID.randomUUID().toString().take(8)}"
        val spacing = if (cropW <= 0.5f || cropH <= 0.5f) 0.25f else 0.5f
        val zone = CropZoneRenderData(
            id = zoneId,
            plotId = bedPlotId,
            cropName = newCropName,
            cropId = newCropId,
            offsetX = relX,
            offsetY = relY,
            widthM = cropW,
            heightM = cropH,
            spacingM = spacing
        )

        val updatedZones = _uiState.value.cropZones + zone

        undoStack.addLast(EditAction.AddCropToBed(zone))
        redoStack.clear()

        _uiState.update { state ->
            state.copy(
                cropZones = updatedZones,
                selectedPlotId = bedPlotId,
                selectedZoneId = zoneId,
                hasUnsavedChanges = true,
                canUndo = undoStack.isNotEmpty(),
                canRedo = redoStack.isNotEmpty(),
                dropFeedbackMessage = null
            )
        }
        return true
    }

    fun reportInvalidDropLocation(message: String) {
        _uiState.update { it.copy(dropFeedbackMessage = message) }
    }

    fun clearDropFeedbackMessage() {
        _uiState.update { it.copy(dropFeedbackMessage = null) }
    }

    fun moveCropToBed(
        zoneId: String,
        newBedPlotId: String,
        atWorldX: Float,
        atWorldY: Float
    ): Boolean {
        val state = _uiState.value
        val targetZone = state.cropZones.firstOrNull { it.id == zoneId } ?: return false
        val targetBed = state.editedPlots.firstOrNull { it.id == newBedPlotId } ?: return false

        // Check if new location is inside target bed bounds
        val insideBed = atWorldX >= targetBed.posX && atWorldX < (targetBed.posX + targetBed.widthM) &&
                atWorldY >= targetBed.posY && atWorldY < (targetBed.posY + targetBed.heightM)
        if (!insideBed) {
            return false // Reject move: drop location is outside the bed
        }

        // Check space capacity if moving to a different bed
        if (targetZone.plotId != newBedPlotId) {
            val maxCapacity = ((targetBed.widthM * targetBed.heightM).toInt()).coerceAtLeast(1)
            val existingInTarget = state.cropZones.filter { it.plotId == newBedPlotId && it.cropName != "Bed" }
            if (existingInTarget.size >= maxCapacity) {
                return false // Reject move: target bed is full
            }
        }

        val relX = (atWorldX - targetBed.posX).coerceIn(0f, (targetBed.widthM - 1f).coerceAtLeast(0f))
        val relY = (atWorldY - targetBed.posY).coerceIn(0f, (targetBed.heightM - 1f).coerceAtLeast(0f))

        val oldPlotId = targetZone.plotId
        val oldOffset = Offset(targetZone.offsetX, targetZone.offsetY)
        val newOffset = Offset(relX, relY)

        val updatedZone = targetZone.copy(
            plotId = newBedPlotId,
            offsetX = relX,
            offsetY = relY
        )

        undoStack.addLast(EditAction.MoveCropToBed(zoneId, oldPlotId, oldOffset, newBedPlotId, newOffset))
        redoStack.clear()

        _uiState.update { current ->
            current.copy(
                cropZones = current.cropZones.map { if (it.id == zoneId) updatedZone else it },
                selectedPlotId = newBedPlotId,
                selectedZoneId = zoneId,
                hasUnsavedChanges = true,
                canUndo = undoStack.isNotEmpty(),
                canRedo = redoStack.isNotEmpty()
            )
        }
        return true
    }

    fun removeCropFromBed(zoneId: String) {
        val zone = _uiState.value.cropZones.firstOrNull { it.id == zoneId } ?: return
        undoStack.addLast(EditAction.RemoveCropFromBed(zone))
        redoStack.clear()
        _uiState.update { state ->
            state.copy(
                cropZones = state.cropZones.filter { it.id != zoneId },
                selectedZoneId = if (state.selectedZoneId == zoneId) null else state.selectedZoneId,
                hasUnsavedChanges = true,
                canUndo = undoStack.isNotEmpty(),
                canRedo = redoStack.isNotEmpty()
            )
        }
    }

    fun addPlot(atWorldX: Float, atWorldY: Float, farmId: String = activeFarmId) {
        addDirectPlantingPlot(atWorldX, atWorldY, "Carrot", "carrot")
    }

    fun deletePlot(plotId: String) {
        val currentPlots = _uiState.value.editedPlots
        val plotToDelete = currentPlots.firstOrNull { it.id == plotId } ?: return
        val childZones = _uiState.value.cropZones.filter { it.plotId == plotId }

        undoStack.addLast(EditAction.DeletePlot(plotToDelete, childZones))
        redoStack.clear()

        val updatedPlots = currentPlots.filter { it.id != plotId }
        val updatedZones = _uiState.value.cropZones.filter { it.plotId != plotId }

        _uiState.update { state ->
            state.copy(
                editedPlots = updatedPlots,
                plots = updatedPlots.map { it.toRenderData() },
                cropZones = updatedZones,
                selectedPlotId = if (state.selectedPlotId == plotId) null else state.selectedPlotId,
                selectedZoneId = if (childZones.any { it.id == state.selectedZoneId }) null else state.selectedZoneId,
                isResizeMode = if (state.selectedPlotId == plotId) false else state.isResizeMode,
                hasUnsavedChanges = true,
                canUndo = undoStack.isNotEmpty(),
                canRedo = redoStack.isNotEmpty()
            )
        }
    }

    fun duplicatePlot(plotId: String) {
        val plot = _uiState.value.editedPlots.firstOrNull { it.id == plotId } ?: return
        var newX = (plot.posX + 1.0f).coerceIn(0f, 45.0f - plot.widthM)
        var newY = (plot.posY + 1.0f).coerceIn(0f, 45.0f - plot.heightM)

        if (hasOverlap(newX, newY, plot.widthM, plot.heightM, "", _uiState.value.editedPlots)) {
            newX = (plot.posX + plot.widthM).coerceIn(0f, 45.0f - plot.widthM)
            newY = plot.posY.coerceIn(0f, 45.0f - plot.heightM)
        }
        if (hasOverlap(newX, newY, plot.widthM, plot.heightM, "", _uiState.value.editedPlots)) {
            newX = plot.posX.coerceIn(0f, 45.0f - plot.widthM)
            newY = (plot.posY + plot.heightM).coerceIn(0f, 45.0f - plot.heightM)
        }
        addDirectPlantingPlot(
            atWorldX = newX,
            atWorldY = newY,
            cropName = plot.cropName ?: "Bed",
            cropId = plot.cropId ?: "bed",
            initialW = plot.widthM,
            initialH = plot.heightM
        )
    }

    fun rotatePlot(plotId: String) {
        val currentPlots = _uiState.value.editedPlots
        val plot = currentPlots.firstOrNull { it.id == plotId } ?: return
        val newW = plot.heightM
        val newH = plot.widthM
        val newX = plot.posX.coerceIn(0f, (45.0f - newW).coerceAtLeast(0f))
        val newY = plot.posY.coerceIn(0f, (45.0f - newH).coerceAtLeast(0f))
        val updatedPlot = plot.copy(
            posX = newX,
            posY = newY,
            widthM = newW,
            heightM = newH,
            updatedAt = Instant.now().toString()
        )
        undoStack.addLast(EditAction.ModifyPlot(plot, updatedPlot))
        redoStack.clear()
        val updatedPlots = currentPlots.map { if (it.id == plotId) updatedPlot else it }
        updatePlotsState(updatedPlots)
    }

    fun bringPlotToFront(plotId: String) {
        val currentPlots = _uiState.value.editedPlots
        val plot = currentPlots.firstOrNull { it.id == plotId } ?: return
        val updatedPlots = currentPlots.filter { it.id != plotId } + plot
        updatePlotsState(updatedPlots)
    }

    fun sendPlotToBack(plotId: String) {
        val currentPlots = _uiState.value.editedPlots
        val plot = currentPlots.firstOrNull { it.id == plotId } ?: return
        val updatedPlots = listOf(plot) + currentPlots.filter { it.id != plotId }
        updatePlotsState(updatedPlots)
    }

    fun updatePlotLabel(plotId: String, newLabel: String) {
        val currentPlots = _uiState.value.editedPlots
        val plot = currentPlots.firstOrNull { it.id == plotId } ?: return
        val updatedPlot = plot.copy(plotLabel = newLabel.trim(), updatedAt = Instant.now().toString())
        undoStack.addLast(EditAction.ModifyPlot(plot, updatedPlot))
        redoStack.clear()
        val updatedPlots = currentPlots.map { if (it.id == plotId) updatedPlot else it }
        updatePlotsState(updatedPlots)
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
            is EditAction.DeletePlot -> {
                currentPlots.add(action.plot)
                _uiState.update { state ->
                    state.copy(cropZones = state.cropZones + action.childZones)
                }
            }
            is EditAction.ChangeSoil -> {
                val idx = currentPlots.indexOfFirst { it.id == action.plotId }
                if (idx != -1) currentPlots[idx] = currentPlots[idx].copy(soilType = action.oldSoil)
            }
            is EditAction.ModifyPlot -> {
                val idx = currentPlots.indexOfFirst { it.id == action.oldPlot.id }
                if (idx != -1) currentPlots[idx] = action.oldPlot
            }
            is EditAction.AddCropToBed -> {
                _uiState.update { state ->
                    state.copy(
                        cropZones = state.cropZones.filter { it.id != action.zone.id },
                        selectedZoneId = if (state.selectedZoneId == action.zone.id) null else state.selectedZoneId
                    )
                }
            }
            is EditAction.MoveCropToBed -> {
                _uiState.update { state ->
                    val parentPlot = state.editedPlots.firstOrNull { it.id == action.oldPlotId }
                    val updatedZones = state.cropZones.map { zone ->
                        if (zone.id == action.zoneId) {
                            zone.copy(
                                plotId = action.oldPlotId,
                                offsetX = action.oldOffset.x,
                                offsetY = action.oldOffset.y
                            )
                        } else zone
                    }
                    state.copy(cropZones = updatedZones)
                }
            }
            is EditAction.RemoveCropFromBed -> {
                _uiState.update { state ->
                    state.copy(cropZones = state.cropZones + action.zone)
                }
            }
            is EditAction.ResizeCropZone -> {
                _uiState.update { state ->
                    val updatedZones = state.cropZones.map { zone ->
                        if (zone.id == action.zoneId) {
                            val parent = state.editedPlots.firstOrNull { it.id == zone.plotId }
                            zone.copy(
                                offsetX = action.oldOffset.x,
                                offsetY = action.oldOffset.y,
                                widthM = action.oldW,
                                heightM = action.oldH
                            )
                        } else zone
                    }
                    state.copy(cropZones = updatedZones)
                }
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
            is EditAction.DeletePlot -> {
                currentPlots.removeAll { it.id == action.plot.id }
                _uiState.update { state ->
                    state.copy(cropZones = state.cropZones.filter { it.plotId != action.plot.id })
                }
            }
            is EditAction.ChangeSoil -> {
                val idx = currentPlots.indexOfFirst { it.id == action.plotId }
                if (idx != -1) currentPlots[idx] = currentPlots[idx].copy(soilType = action.newSoil)
            }
            is EditAction.ModifyPlot -> {
                val idx = currentPlots.indexOfFirst { it.id == action.newPlot.id }
                if (idx != -1) currentPlots[idx] = action.newPlot
            }
            is EditAction.AddCropToBed -> {
                _uiState.update { state ->
                    state.copy(cropZones = state.cropZones + action.zone)
                }
            }
            is EditAction.MoveCropToBed -> {
                _uiState.update { state ->
                    val parentPlot = state.editedPlots.firstOrNull { it.id == action.newPlotId }
                    val updatedZones = state.cropZones.map { zone ->
                        if (zone.id == action.zoneId) {
                            zone.copy(
                                plotId = action.newPlotId,
                                offsetX = action.newOffset.x,
                                offsetY = action.newOffset.y
                            )
                        } else zone
                    }
                    state.copy(cropZones = updatedZones)
                }
            }
            is EditAction.RemoveCropFromBed -> {
                _uiState.update { state ->
                    state.copy(cropZones = state.cropZones.filter { it.id != action.zone.id })
                }
            }
            is EditAction.ResizeCropZone -> {
                _uiState.update { state ->
                    val updatedZones = state.cropZones.map { zone ->
                        if (zone.id == action.zoneId) {
                            val parent = state.editedPlots.firstOrNull { it.id == zone.plotId }
                            zone.copy(
                                offsetX = action.newOffset.x,
                                offsetY = action.newOffset.y,
                                widthM = action.newW,
                                heightM = action.newH
                            )
                        } else zone
                    }
                    state.copy(cropZones = updatedZones)
                }
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

    fun detectNewOrChangedCrops(): List<CropPlantingDraft> {
        val currentPlots = _uiState.value.editedPlots
        val currentZones = _uiState.value.cropZones.filter { !it.cropName.isNullOrBlank() && !it.cropName.equals("Bed", ignoreCase = true) }
        val drafts = mutableListOf<CropPlantingDraft>()

        for (zone in currentZones) {
            val initial = initialZonesSnapshot.firstOrNull { it.id == zone.id }
            val isNew = initial == null
            val isChanged = initial != null && (
                initial.plotId != zone.plotId ||
                initial.cropName != zone.cropName ||
                initial.widthM != zone.widthM ||
                initial.heightM != zone.heightM ||
                initial.offsetX != zone.offsetX ||
                initial.offsetY != zone.offsetY
            )

            if (isNew || isChanged || initialZonesSnapshot.isEmpty()) {
                val parentBed = currentPlots.firstOrNull { it.id == zone.plotId }
                val cropName = zone.cropName ?: "Vegetable"
                val cropId = zone.cropName?.lowercase() ?: "crop"
                val variety = parentBed?.cropVariety?.ifBlank { null } ?: getDefaultCropVariety(cropName)
                val date = parentBed?.plantedDate?.take(10)?.ifBlank { null } ?: java.time.LocalDate.now().toString()
                val plantCount = zone.plantInstances.size.takeIf { it > 0 } ?: ((zone.widthM / zone.spacingM) * (zone.heightM / zone.spacingM)).toInt().coerceAtLeast(1)

                drafts.add(
                    CropPlantingDraft(
                        id = zone.id,
                        cropName = cropName,
                        cropId = cropId,
                        bedId = parentBed?.id ?: zone.plotId,
                        bedLabel = parentBed?.plotLabel ?: "Bed",
                        variety = variety,
                        varietyId = variety.lowercase().replace(" ", "_"),
                        plantingDate = date,
                        plantCount = plantCount,
                        soilType = parentBed?.soilType ?: SoilType.LOAM,
                        bedDimensions = if (parentBed != null) "${String.format(java.util.Locale.US, "%.1f", parentBed.widthM)}m × ${String.format(java.util.Locale.US, "%.1f", parentBed.heightM)}m" else "1.5m × 2.0m",
                        notes = parentBed?.notes ?: "",
                        isNew = isNew,
                        isChanged = isChanged
                    )
                )
            }
        }

        for (plot in currentPlots.filter { !it.cropName.isNullOrBlank() && !it.cropName.equals("Bed", ignoreCase = true) && it.cropId != "bed" }) {
            if (drafts.any { it.bedId == plot.id }) continue
            val initial = initialPlotsSnapshot.firstOrNull { it.id == plot.id }
            val isNew = initial == null
            val isChanged = initial != null && (
                initial.cropName != plot.cropName ||
                initial.widthM != plot.widthM ||
                initial.heightM != plot.heightM ||
                initial.posX != plot.posX ||
                initial.posY != plot.posY
            )
            if (isNew || isChanged || initialPlotsSnapshot.isEmpty()) {
                val cropName = plot.cropName ?: "Vegetable"
                val cropId = plot.cropId ?: cropName.lowercase()
                val variety = plot.cropVariety?.ifBlank { null } ?: getDefaultCropVariety(cropName)
                val date = plot.plantedDate?.take(10)?.ifBlank { null } ?: java.time.LocalDate.now().toString()
                drafts.add(
                    CropPlantingDraft(
                        id = plot.id,
                        cropName = cropName,
                        cropId = cropId,
                        bedId = plot.id,
                        bedLabel = plot.plotLabel,
                        variety = variety,
                        varietyId = variety.lowercase().replace(" ", "_"),
                        plantingDate = date,
                        plantCount = (plot.widthM * plot.heightM).toInt().coerceAtLeast(1),
                        soilType = plot.soilType,
                        bedDimensions = "${String.format(java.util.Locale.US, "%.1f", plot.widthM)}m × ${String.format(java.util.Locale.US, "%.1f", plot.heightM)}m",
                        notes = plot.notes ?: "",
                        isNew = isNew,
                        isChanged = isChanged
                    )
                )
            }
        }

        return drafts
    }

    fun onSavePressed(onReturnToFarm: () -> Unit) {
        val drafts = detectNewOrChangedCrops()
        if (drafts.isNotEmpty()) {
            _uiState.update { it.copy(
                showCropSummaryOverlay = true,
                pendingCropPlantings = drafts,
                saveErrorMessage = null
            ) }
        } else {
            saveFarmLayoutDirectly(onReturnToFarm)
        }
    }

    fun dismissCropSummary() {
        _uiState.update { it.copy(showCropSummaryOverlay = false, saveErrorMessage = null) }
    }

    fun saveFarmLayoutDirectly(onReturnToFarm: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveErrorMessage = null) }
            try {
                cropPlotRepository.savePlots(_uiState.value.editedPlots)
                val domainZones = _uiState.value.cropZones.filter {
                    !it.cropName.isNullOrBlank() && !it.cropName.equals("Bed", ignoreCase = true)
                }.map { zoneData ->
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

                try {
                    runDssEvaluationUseCase(activeFarmId)
                } catch (_: Exception) {}

                _uiState.update { it.copy(
                    isSaving = false,
                    hasUnsavedChanges = false,
                    isSaveSuccessful = true
                ) }
                onReturnToFarm()
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isSaving = false,
                    saveErrorMessage = "Hindi nai-save ang layout ng sakahan: ${e.localizedMessage ?: "Database error"}"
                ) }
            }
        }
    }

    fun confirmSaveCropPlantings(
        drafts: List<CropPlantingDraft>,
        onReturnToFarm: () -> Unit
    ) {
        viewModelScope.launch {
            for (draft in drafts) {
                if (draft.cropName.isBlank() || draft.variety.isBlank() || draft.plantingDate.isBlank() || draft.plantCount <= 0 || draft.bedId.isBlank()) {
                    _uiState.update { it.copy(
                        saveErrorMessage = "Pakiusap punan ang lahat ng kinakailangang impormasyon (Crop, Variety, at Petsa ng Pagtanim para sa ${draft.cropName})."
                    ) }
                    return@launch
                }
                val validDate = try { java.time.LocalDate.parse(draft.plantingDate.take(10)) } catch (_: Exception) { null }
                if (validDate == null) {
                    _uiState.update { it.copy(
                        saveErrorMessage = "Hindi wasto ang petsa ng pagtatanim para sa ${draft.cropName}. Pakiusap pumili ng wastong petsa."
                    ) }
                    return@launch
                }
            }

            _uiState.update { it.copy(isSaving = true, saveErrorMessage = null) }
            try {
                val updatedPlots = _uiState.value.editedPlots.map { plot ->
                    val isBed = plot.cropName.equals("Bed", ignoreCase = true) || plot.cropId?.equals("bed", ignoreCase = true) == true
                    val bedDrafts = drafts.filter { it.bedId == plot.id }
                    if (isBed) {
                        val cropsSummary = bedDrafts.mapNotNull { it.cropName }.distinct().joinToString(", ")
                        val varietiesSummary = bedDrafts.mapNotNull { it.variety }.distinct().joinToString(", ")
                        val earliestDate = bedDrafts.minOfOrNull { it.plantingDate } ?: plot.plantedDate
                        val primaryCrop = if (bedDrafts.size == 1) bedDrafts.first().cropName else "Bed"
                        val primaryCropId = if (bedDrafts.size == 1) (bedDrafts.first().cropId.ifBlank { bedDrafts.first().cropName.lowercase() }) else "bed"
                        plot.copy(
                            farmId = activeFarmId,
                            cropName = primaryCrop,
                            cropId = primaryCropId,
                            soilType = bedDrafts.firstOrNull()?.soilType ?: plot.soilType,
                            plantedDate = earliestDate,
                            cropVariety = varietiesSummary.ifBlank { plot.cropVariety },
                            notes = if (cropsSummary.isNotBlank()) "Crops: $cropsSummary" else plot.notes,
                            updatedAt = Instant.now().toString()
                        )
                    } else {
                        val matchingDraft = bedDrafts.firstOrNull() ?: drafts.firstOrNull { it.id == plot.id }
                        if (matchingDraft != null) {
                            plot.copy(
                                farmId = activeFarmId,
                                soilType = matchingDraft.soilType,
                                plantedDate = matchingDraft.plantingDate,
                                cropVariety = matchingDraft.variety,
                                notes = matchingDraft.notes.ifBlank { "${matchingDraft.plantCount} plants" },
                                cropName = matchingDraft.cropName.ifBlank { plot.cropName },
                                cropId = matchingDraft.cropId.ifBlank { plot.cropId },
                                updatedAt = Instant.now().toString()
                            )
                        } else {
                            plot
                        }
                    }
                }
                cropPlotRepository.savePlots(updatedPlots)

                // Ensure EVERY draft has a corresponding CropZone saved into Room
                val domainZones = drafts.map { draft ->
                    val existingZone = _uiState.value.cropZones.firstOrNull { it.id == draft.id }
                    com.maptanim.app.domain.model.CropZone(
                        id = draft.id,
                        plotId = draft.bedId,
                        cropName = draft.cropName,
                        cropId = draft.cropId.ifBlank { draft.cropName.lowercase() },
                        offsetX = existingZone?.offsetX ?: 0f,
                        offsetY = existingZone?.offsetY ?: 0f,
                        widthM = existingZone?.widthM ?: 1.0f,
                        heightM = existingZone?.heightM ?: 1.0f,
                        spacingM = existingZone?.spacingM ?: 0.5f,
                        createdAt = Instant.now().toString(),
                        updatedAt = Instant.now().toString()
                    )
                }
                cropZoneRepository.saveZones(domainZones)

                // ── Anchor Schedule Generation ─────────────────────────────
                // Planting date becomes the anchor date for monitoring, crop care, tasks, and expected harvest
                val scheduledTasks = mutableListOf<com.maptanim.app.domain.model.FarmTask>()
                for (draft in drafts) {
                    val anchorDate = try {
                        java.time.LocalDate.parse(draft.plantingDate.take(10))
                    } catch (_: Exception) {
                        java.time.LocalDate.now()
                    }
                    val isSim = draft.variety.contains("10s", ignoreCase = true)
                    val daysToHarvest = if (isSim) 1 else getDaysToHarvestEstimate(draft.cropName)
                    val harvestDate = anchorDate.plusDays(daysToHarvest.toLong())
                    val isClimbing = draft.cropName.lowercase().let { c ->
                        c.contains("ampalaya") || c.contains("sitaw") || c.contains("tomato") ||
                        c.contains("cucumber") || c.contains("squash") || c.contains("beans")
                    }

                    // 1. Monitoring Dates
                    scheduledTasks.add(
                        com.maptanim.app.domain.model.FarmTask(
                            id = "mon_germ_${draft.id}",
                            farmId = activeFarmId,
                            plotId = draft.bedId,
                            plotLabel = draft.bedLabel,
                            cropName = draft.cropName,
                            taskType = com.maptanim.app.domain.model.TaskType.OBSERVATION,
                            title = "Monitoring: Pagpapatubo (Germination Check)",
                            subLabel = "${draft.cropName} (${draft.variety}) • ${draft.bedLabel}",
                            dueDate = anchorDate.plusDays(4).toString(),
                            isCompleted = false,
                            completedAt = null
                        )
                    )
                    scheduledTasks.add(
                        com.maptanim.app.domain.model.FarmTask(
                            id = "mon_scout_${draft.id}",
                            farmId = activeFarmId,
                            plotId = draft.bedId,
                            plotLabel = draft.bedLabel,
                            cropName = draft.cropName,
                            taskType = com.maptanim.app.domain.model.TaskType.OBSERVATION,
                            title = "Monitoring: Kalusugan ng Dahon at Peste",
                            subLabel = "${draft.cropName} (${draft.variety}) • ${draft.bedLabel}",
                            dueDate = anchorDate.plusDays((daysToHarvest * 0.40f).toLong().coerceAtLeast(10)).toString(),
                            isCompleted = false,
                            completedAt = null
                        )
                    )
                    scheduledTasks.add(
                        com.maptanim.app.domain.model.FarmTask(
                            id = "mon_preharvest_${draft.id}",
                            farmId = activeFarmId,
                            plotId = draft.bedId,
                            plotLabel = draft.bedLabel,
                            cropName = draft.cropName,
                            taskType = com.maptanim.app.domain.model.TaskType.OBSERVATION,
                            title = "Monitoring: Kahandaan sa Pag-ani (Pre-Harvest Check)",
                            subLabel = "${draft.cropName} (${draft.variety}) • ${draft.bedLabel}",
                            dueDate = harvestDate.minusDays(3).coerceAtLeast(anchorDate.plusDays(1)).toString(),
                            isCompleted = false,
                            completedAt = null
                        )
                    )

                    // 2. Crop Care Dates
                    scheduledTasks.add(
                        com.maptanim.app.domain.model.FarmTask(
                            id = "care_water_${draft.id}",
                            farmId = activeFarmId,
                            plotId = draft.bedId,
                            plotLabel = draft.bedLabel,
                            cropName = draft.cropName,
                            taskType = com.maptanim.app.domain.model.TaskType.WATER,
                            title = "Crop Care: Dilig at Pag-aalaga ng Lupa",
                            subLabel = "${draft.cropName} • Unang Pagdilig",
                            dueDate = anchorDate.plusDays(1).toString(),
                            isCompleted = false,
                            completedAt = null
                        )
                    )
                    scheduledTasks.add(
                        com.maptanim.app.domain.model.FarmTask(
                            id = "care_fert_${draft.id}",
                            farmId = activeFarmId,
                            plotId = draft.bedId,
                            plotLabel = draft.bedLabel,
                            cropName = draft.cropName,
                            taskType = com.maptanim.app.domain.model.TaskType.FERTILIZE,
                            title = "Crop Care: Paglalagay ng Abono (Fertilizer)",
                            subLabel = "${draft.cropName} • Side-dressing",
                            dueDate = anchorDate.plusDays((daysToHarvest * 0.25f).toLong().coerceAtLeast(7)).toString(),
                            isCompleted = false,
                            completedAt = null
                        )
                    )
                    scheduledTasks.add(
                        com.maptanim.app.domain.model.FarmTask(
                            id = "care_weed_${draft.id}",
                            farmId = activeFarmId,
                            plotId = draft.bedId,
                            plotLabel = draft.bedLabel,
                            cropName = draft.cropName,
                            taskType = com.maptanim.app.domain.model.TaskType.WEED,
                            title = "Crop Care: Pag-aalis ng Damo (Weeding)",
                            subLabel = "${draft.cropName} • Weed & Soil Check",
                            dueDate = anchorDate.plusDays((daysToHarvest * 0.45f).toLong().coerceAtLeast(14)).toString(),
                            isCompleted = false,
                            completedAt = null
                        )
                    )

                    // 3. Other Tasks
                    if (isClimbing) {
                        scheduledTasks.add(
                            com.maptanim.app.domain.model.FarmTask(
                                id = "task_trellis_${draft.id}",
                                farmId = activeFarmId,
                                plotId = draft.bedId,
                                plotLabel = draft.bedLabel,
                                cropName = draft.cropName,
                                taskType = com.maptanim.app.domain.model.TaskType.TRELLIS,
                                title = "Gawain: Paglalagay ng Balag / Trellis",
                                subLabel = "${draft.cropName} (${draft.variety})",
                                dueDate = anchorDate.plusDays(10).toString(),
                                isCompleted = false,
                                completedAt = null
                            )
                        )
                    }
                    scheduledTasks.add(
                        com.maptanim.app.domain.model.FarmTask(
                            id = "task_prune_${draft.id}",
                            farmId = activeFarmId,
                            plotId = draft.bedId,
                            plotLabel = draft.bedLabel,
                            cropName = draft.cropName,
                            taskType = com.maptanim.app.domain.model.TaskType.PRUNING,
                            title = "Gawain: Pagsasaayos at Pag-prune ng Sanga",
                            subLabel = "${draft.cropName} • Thinning/Pruning",
                            dueDate = anchorDate.plusDays((daysToHarvest * 0.50f).toLong().coerceAtLeast(15)).toString(),
                            isCompleted = false,
                            completedAt = null
                        )
                    )

                    // 4. Expected Harvest
                    scheduledTasks.add(
                        com.maptanim.app.domain.model.FarmTask(
                            id = "task_harvest_${draft.id}",
                            farmId = activeFarmId,
                            plotId = draft.bedId,
                            plotLabel = draft.bedLabel,
                            cropName = draft.cropName,
                            taskType = com.maptanim.app.domain.model.TaskType.HARVEST,
                            title = "Inaasahang Pag-ani: ${draft.cropName}",
                            subLabel = "${draft.cropName} (${draft.variety}) • $daysToHarvest araw",
                            dueDate = harvestDate.toString(),
                            isCompleted = false,
                            completedAt = null
                        )
                    )
                }

                if (scheduledTasks.isNotEmpty()) {
                    try {
                        RepositoryProvider.taskRepository.upsertTasks(scheduledTasks)
                    } catch (_: Exception) {}
                }

                try {
                    runDssEvaluationUseCase(activeFarmId)
                } catch (_: Exception) {}

                _uiState.update { it.copy(
                    plots = updatedPlots.map { p -> p.toRenderData() },
                    editedPlots = updatedPlots,
                    cropZones = domainZones.map { z ->
                        CropZoneRenderData(
                            id = z.id,
                            plotId = z.plotId,
                            cropName = z.cropName ?: "Crop",
                            offsetX = z.offsetX,
                            offsetY = z.offsetY,
                            widthM = z.widthM,
                            heightM = z.heightM,
                            spacingM = z.spacingM
                        )
                    },
                    isSaving = false,
                    hasUnsavedChanges = false,
                    showCropSummaryOverlay = false,
                    isSaveSuccessful = true
                ) }
                onReturnToFarm()
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isSaving = false,
                    saveErrorMessage = "Nabigong i-save ang mga pananim: ${e.localizedMessage ?: "Database error"}"
                ) }
            }
        }
    }

    fun getDaysToHarvestEstimate(cropName: String): Int {
        val clean = cropName.lowercase()
        return when {
            clean.contains("pechay") || clean.contains("kangkong") -> 30
            clean.contains("lettuce") -> 45
            clean.contains("okra") -> 50
            clean.contains("sitaw") || clean.contains("stringbean") -> 55
            clean.contains("ampalaya") || clean.contains("bitter") -> 60
            clean.contains("tomato") || clean.contains("kamatis") -> 65
            clean.contains("eggplant") || clean.contains("talong") -> 70
            clean.contains("corn") || clean.contains("mais") -> 75
            clean.contains("carrot") || clean.contains("karot") -> 85
            clean.contains("cabbage") || clean.contains("repolyo") -> 85
            clean.contains("pumpkin") || clean.contains("squash") -> 90
            clean.contains("onion") || clean.contains("sibuyas") -> 100
            clean.contains("sili") || clean.contains("chili") -> 75
            else -> 60
        }
    }

    private fun getDefaultCropVariety(cropName: String): String {
        val clean = cropName.lowercase()
        return when {
            clean.contains("ampalaya") || clean.contains("bitter") -> "Galaxy Max F1"
            clean.contains("tomato") || clean.contains("kamatis") -> "Diamante Max F1"
            clean.contains("eggplant") || clean.contains("talong") -> "Fortuner F1"
            clean.contains("carrot") || clean.contains("karot") -> "Terracotta F1"
            clean.contains("cabbage") || clean.contains("repolyo") -> "Rare Ball F1"
            clean.contains("pechay") || clean.contains("bokchoy") -> "Black Behi"
            clean.contains("onion") || clean.contains("sibuyas") -> "Red Pinoy F1"
            clean.contains("pumpkin") || clean.contains("squash") || clean.contains("kalabasa") -> "Suprema F1"
            clean.contains("corn") || clean.contains("mais") -> "Macho Sweet F1"
            clean.contains("okra") -> "Smooth Green F1"
            clean.contains("sili") || clean.contains("chili") || clean.contains("pepper") -> "Django F1"
            clean.contains("sitaw") || clean.contains("stringbean") || clean.contains("beans") -> "Sandigan F1"
            clean.contains("lettuce") -> "General F1"
            clean.contains("kangkong") -> "Upland Green"
            else -> "East-West Standard F1"
        }
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

            try {
                runDssEvaluationUseCase(activeFarmId)
            } catch (_: Exception) {}

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

            val currentZones = _uiState.value.cropZones.filter {
                !it.cropName.isNullOrBlank() && !it.cropName.equals("Bed", ignoreCase = true)
            }
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
            val validPlotIds = updatedPlots.map { it.id }.toSet()
            val retainedZones = state.cropZones.filter { it.plotId in validPlotIds }
            val updatedZones = retainedZones.map { zone ->
                val parentPlot = updatedPlots.firstOrNull { it.id == zone.plotId }
                if (parentPlot != null) {
                    val clampedOffsetX = zone.offsetX.coerceIn(0f, (parentPlot.widthM - zone.widthM).coerceAtLeast(0f))
                    val clampedOffsetY = zone.offsetY.coerceIn(0f, (parentPlot.heightM - zone.heightM).coerceAtLeast(0f))
                    zone.copy(offsetX = clampedOffsetX, offsetY = clampedOffsetY)
                } else {
                    zone
                }
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

