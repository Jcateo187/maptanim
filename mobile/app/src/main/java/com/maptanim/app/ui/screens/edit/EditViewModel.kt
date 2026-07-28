package com.maptanim.app.ui.screens.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.geometry.Offset
import com.maptanim.app.domain.model.*
import com.maptanim.app.domain.usecase.*
import com.maptanim.app.renderer.PlantInstanceGenerator
import com.maptanim.app.renderer.canvas.FarmCanvasRenderer
import com.maptanim.app.renderer.model.*

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import com.maptanim.app.ui.screens.home.toRenderData

// ─── EditUiState ──────────────────────────────────────────────────────────

data class EditUiState(
    val isLoading: Boolean = true,
    val beds: List<BedRenderData> = emptyList(),
    val editedBeds: List<Bed> = emptyList(),
    val cropZones: List<CropZoneRenderData> = emptyList(),
    val farmObjects: List<FarmObjectRenderData> = emptyList(),
    val activeTool: EditTool = EditTool.SELECT_MOVE,
    val activeSoilType: SoilType = SoilType.LOAM,
    val selectedBedId: String? = null,
    val selectedZoneId: String? = null,
    val isGridEnabled: Boolean = true,
    val isSnapEnabled: Boolean = true,
    val zoomPercent: Int = 100,
    val hasUnsavedChanges: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

// ─── EditAction (Undo/Redo) ────────────────────────────────────────────────

sealed class EditAction {
    data class MoveBed(val bedId: String, val from: Offset, val to: Offset) : EditAction()
    data class ResizeBed(val bedId: String, val fromW: Float, val fromH: Float, val toW: Float, val toH: Float) : EditAction()
    data class AddBed(val bed: Bed) : EditAction()
    data class DeleteBed(val bed: Bed) : EditAction()
    data class ChangeSoil(val bedId: String, val from: SoilType, val to: SoilType) : EditAction()
    data class ChangeCrop(val bedId: String, val fromCrop: String?, val toCrop: String?, val fromCropId: String?, val toCropId: String?) : EditAction()
    data class DuplicateBed(val originalId: String, val duplicateId: String, val duplicate: Bed) : EditAction()
    data class AddCropZone(val zone: CropZoneRenderData) : EditAction()
    data class ResizeCropZone(val zoneId: String, val fromW: Float, val fromH: Float, val toW: Float, val toH: Float) : EditAction()
    data class AddFarmObject(val obj: FarmObjectRenderData) : EditAction()
}


// ─── EditViewModel ────────────────────────────────────────────────────────

class EditViewModel(
    private val getFarmBedsUseCase: GetFarmBedsUseCase? = null,
    private val saveFarmLayoutUseCase: SaveFarmLayoutUseCase? = null,
    private val addBedUseCase: AddBedUseCase? = null,
    private val deleteBedUseCase: DeleteBedUseCase? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditUiState())
    val uiState: StateFlow<EditUiState> = _uiState.asStateFlow()

    private val undoStack = ArrayDeque<EditAction>()
    private val redoStack = ArrayDeque<EditAction>()

    init {
        val bed1 = Bed(
            id          = "bed-1",
            farmId      = "farm-1",
            bedLabel    = "BED 1",
            cropName    = "Carrot",
            cropId      = "carrot",
            soilType    = SoilType.LOAM,
            posX        = 5f,
            posY        = 5f,
            widthM      = 3f,
            heightM     = 2f,
            rotationDeg = 0f,
            plantedDate = null,
            isActive    = true,
            notes       = null,
            createdAt   = Instant.now().toString(),
            updatedAt   = Instant.now().toString()
        )
        val bed2 = Bed(
            id          = "bed-2",
            farmId      = "farm-1",
            bedLabel    = "BED 2",
            cropName    = "String Beans",
            cropId      = "stringbeans",
            soilType    = SoilType.LOAM,
            posX        = 10f,
            posY        = 5f,
            widthM      = 3f,
            heightM     = 2f,
            rotationDeg = 0f,
            plantedDate = null,
            isActive    = true,
            notes       = null,
            createdAt   = Instant.now().toString(),
            updatedAt   = Instant.now().toString()
        )
        val initialBeds = listOf(bed1, bed2)
        val initialRenderBeds = initialBeds.map { b -> b.toRenderData() }
        val zone1 = CropZoneRenderData("z-1", "bed-1", "Carrot", 0.3f, 0.3f, 2.4f, 1.4f, 0.4f)
        val zone2 = CropZoneRenderData("z-2", "bed-2", "String Beans", 0.3f, 0.3f, 2.4f, 1.4f, 0.4f)

        val zone1WithPlants = zone1.copy(plantInstances = PlantInstanceGenerator.generate(zone1, 5f, 5f))
        val zone2WithPlants = zone2.copy(plantInstances = PlantInstanceGenerator.generate(zone2, 10f, 5f))

        _uiState.update { it.copy(
            editedBeds = initialBeds,
            beds = initialRenderBeds,
            cropZones = listOf(zone1WithPlants, zone2WithPlants),
            isLoading = false
        ) }
    }

    fun loadBeds(farmId: String) {
        getFarmBedsUseCase?.let { useCase ->
            viewModelScope.launch {
                useCase(farmId).take(1).collect { domainBeds ->
                    _uiState.update { it.copy(
                        editedBeds = domainBeds,
                        beds       = domainBeds.map { b -> b.toRenderData() },
                        isLoading  = false
                    ) }
                }
            }
        }
    }

    fun selectTool(tool: EditTool) {
        _uiState.update { state ->
            val keepSelection = tool == EditTool.ADD_PLANT || tool == EditTool.DELETE || tool == EditTool.SELECT_MOVE
            state.copy(
                activeTool = tool,
                selectedBedId = if (keepSelection) state.selectedBedId else null
            )
        }
    }

    fun selectSoilType(soilType: SoilType) {
        _uiState.update { it.copy(activeSoilType = soilType) }
    }

    fun selectBed(bedId: String?) {
        _uiState.update { it.copy(selectedBedId = bedId) }
    }

    fun deselect() {
        _uiState.update { it.copy(selectedBedId = null, selectedZoneId = null) }
    }

    fun selectCropZone(zoneId: String?) {
        val zone = _uiState.value.cropZones.firstOrNull { it.id == zoneId }
        _uiState.update { it.copy(
            selectedZoneId = zoneId,
            selectedBedId = zone?.bedId ?: it.selectedBedId
        ) }
    }

    fun moveBed(bedId: String, worldDelta: Offset) {
        val currentBeds = _uiState.value.editedBeds
        val bed = currentBeds.firstOrNull { it.id == bedId } ?: return
        val oldPos = Offset(bed.posX, bed.posY)

        var newX = (bed.posX + worldDelta.x).coerceAtLeast(0f)
        var newY = (bed.posY + worldDelta.y).coerceAtLeast(0f)

        if (_uiState.value.isSnapEnabled) {
            val snapped = FarmCanvasRenderer.snapToGrid(Offset(newX, newY))
            newX = snapped.x
            newY = snapped.y
        }

        val updatedBeds = currentBeds.map {
            if (it.id == bedId) it.copy(posX = newX, posY = newY) else it
        }

        val action = EditAction.MoveBed(bedId, oldPos, Offset(newX, newY))
        undoStack.addLast(action)
        redoStack.clear()

        _uiState.update { it.copy(
            editedBeds      = updatedBeds,
            beds            = updatedBeds.map { b -> b.toRenderData() },
            hasUnsavedChanges = true
        ) }
    }

    fun resizeBed(bedId: String, newWidth: Float, newHeight: Float) {
        val currentBeds = _uiState.value.editedBeds
        val bed = currentBeds.firstOrNull { it.id == bedId } ?: return
        val safeW = newWidth.coerceIn(1.0f, 20.0f)
        val safeH = newHeight.coerceIn(1.0f, 20.0f)

        val action = EditAction.ResizeBed(bedId, bed.widthM, bed.heightM, safeW, safeH)
        undoStack.addLast(action)
        redoStack.clear()

        val updatedBeds = currentBeds.map {
            if (it.id == bedId) it.copy(widthM = safeW, heightM = safeH) else it
        }
        _uiState.update { it.copy(
            editedBeds = updatedBeds,
            beds = updatedBeds.map { b -> b.toRenderData() },
            hasUnsavedChanges = true
        ) }
    }

    fun addDirectPlantingPlot(atWorldX: Float, atWorldY: Float, cropName: String = "Carrot", cropId: String = "carrot") {
        val existingCount = _uiState.value.editedBeds.size
        val plotId = UUID.randomUUID().toString()
        val newBedPlot = Bed(
            id          = plotId,
            farmId      = "farm-1",
            bedLabel    = cropName,
            cropName    = cropName,
            cropId      = cropId,
            soilType    = SoilType.LOAM,
            posX        = atWorldX,
            posY        = atWorldY,
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
            bedId = plotId,
            cropName = cropName,
            offsetX = 0.2f,
            offsetY = 0.2f,
            widthM = 2.1f,
            heightM = 1.6f,
            spacingM = 0.4f
        )
        val zoneWithPlants = zone.copy(
            plantInstances = PlantInstanceGenerator.generate(zone, atWorldX, atWorldY)
        )

        val action = EditAction.AddBed(newBedPlot)
        undoStack.addLast(action)
        redoStack.clear()

        val updatedBeds = _uiState.value.editedBeds + newBedPlot
        val updatedZones = _uiState.value.cropZones + zoneWithPlants

        _uiState.update { it.copy(
            editedBeds = updatedBeds,
            beds = updatedBeds.map { b -> b.toRenderData() },
            cropZones = updatedZones,
            selectedBedId = plotId,
            hasUnsavedChanges = true
        ) }
    }

    fun addBed(atWorldX: Float, atWorldY: Float, farmId: String) {
        addDirectPlantingPlot(atWorldX, atWorldY, "Carrot", "carrot")
    }

    fun paintSoil(bedId: String) {
        val newSoil = _uiState.value.activeSoilType
        val currentBeds = _uiState.value.editedBeds
        val bed = currentBeds.firstOrNull { it.id == bedId } ?: return
        if (bed.soilType == newSoil) return

        val action = EditAction.ChangeSoil(bedId, bed.soilType, newSoil)
        undoStack.addLast(action)
        redoStack.clear()

        val updatedBeds = currentBeds.map {
            if (it.id == bedId) it.copy(soilType = newSoil) else it
        }
        _uiState.update { it.copy(
            editedBeds = updatedBeds,
            beds = updatedBeds.map { b -> b.toRenderData() },
            hasUnsavedChanges = true
        ) }
    }

    fun changeCrop(bedId: String, newCropName: String?, newCropId: String?) {
        val currentBeds = _uiState.value.editedBeds
        val bed = currentBeds.firstOrNull { it.id == bedId } ?: return

        val action = EditAction.ChangeCrop(bedId, bed.cropName, newCropName, bed.cropId, newCropId)
        undoStack.addLast(action)
        redoStack.clear()

        val updatedBeds = currentBeds.map {
            if (it.id == bedId) it.copy(cropName = newCropName, cropId = newCropId) else it
        }
        _uiState.update { it.copy(
            editedBeds = updatedBeds,
            beds = updatedBeds.map { b -> b.toRenderData() },
            hasUnsavedChanges = true
        ) }
    }

    fun deleteBed(bedId: String) {
        val bed = _uiState.value.editedBeds.firstOrNull { it.id == bedId } ?: return
        val action = EditAction.DeleteBed(bed)
        undoStack.addLast(action)
        redoStack.clear()

        val updatedBeds = _uiState.value.editedBeds.filter { it.id != bedId }
        _uiState.update { it.copy(
            editedBeds = updatedBeds,
            beds = updatedBeds.map { b -> b.toRenderData() },
            selectedBedId = null,
            hasUnsavedChanges = true
        ) }
    }

    fun duplicateBed(bedId: String) {
        val original = _uiState.value.editedBeds.firstOrNull { it.id == bedId } ?: return
        val duplicate = original.copy(
            id       = UUID.randomUUID().toString(),
            bedLabel = "${original.bedLabel}-COPY",
            posX     = original.posX + 0.5f,
            posY     = original.posY + 0.5f,
            createdAt = Instant.now().toString(),
            updatedAt = Instant.now().toString()
        )
        val action = EditAction.DuplicateBed(bedId, duplicate.id, duplicate)
        undoStack.addLast(action)
        redoStack.clear()

        val updatedBeds = _uiState.value.editedBeds + duplicate
        _uiState.update { it.copy(
            editedBeds = updatedBeds,
            beds = updatedBeds.map { b -> b.toRenderData() },
            selectedBedId = duplicate.id,
            hasUnsavedChanges = true
        ) }
    }

    fun undo() {
        val action = undoStack.removeLastOrNull() ?: return
        redoStack.addLast(action)
        reverseAction(action)
    }

    fun redo() {
        val action = redoStack.removeLastOrNull() ?: return
        undoStack.addLast(action)
        applyAction(action)
    }

    private fun reverseAction(action: EditAction) {
        when (action) {
            is EditAction.MoveBed -> {
                val beds = _uiState.value.editedBeds.map {
                    if (it.id == action.bedId) it.copy(posX = action.from.x, posY = action.from.y) else it
                }
                _uiState.update { it.copy(editedBeds = beds, beds = beds.map { b -> b.toRenderData() }) }
            }
            is EditAction.ResizeBed -> {
                val beds = _uiState.value.editedBeds.map {
                    if (it.id == action.bedId) it.copy(widthM = action.fromW, heightM = action.fromH) else it
                }
                _uiState.update { it.copy(editedBeds = beds, beds = beds.map { b -> b.toRenderData() }) }
            }
            is EditAction.ChangeSoil -> {
                val beds = _uiState.value.editedBeds.map {
                    if (it.id == action.bedId) it.copy(soilType = action.from) else it
                }
                _uiState.update { it.copy(editedBeds = beds, beds = beds.map { b -> b.toRenderData() }) }
            }
            is EditAction.AddBed -> {
                val beds = _uiState.value.editedBeds.filter { it.id != action.bed.id }
                _uiState.update { it.copy(editedBeds = beds, beds = beds.map { b -> b.toRenderData() }) }
            }
            is EditAction.DeleteBed -> {
                val beds = _uiState.value.editedBeds + action.bed
                _uiState.update { it.copy(editedBeds = beds, beds = beds.map { b -> b.toRenderData() }) }
            }
            is EditAction.DuplicateBed -> {
                val beds = _uiState.value.editedBeds.filter { it.id != action.duplicateId }
                _uiState.update { it.copy(editedBeds = beds, beds = beds.map { b -> b.toRenderData() }) }
            }
            is EditAction.ChangeCrop -> {
                val beds = _uiState.value.editedBeds.map {
                    if (it.id == action.bedId) it.copy(cropName = action.fromCrop, cropId = action.fromCropId) else it
                }
                _uiState.update { it.copy(editedBeds = beds, beds = beds.map { b -> b.toRenderData() }) }
            }
            is EditAction.AddCropZone -> {
                _uiState.update { it.copy(cropZones = it.cropZones.filter { z -> z.id != action.zone.id }) }
            }
            is EditAction.ResizeCropZone -> {
                val zones = _uiState.value.cropZones.map { z ->
                    if (z.id == action.zoneId) {
                        val restored = z.copy(widthM = action.fromW, heightM = action.fromH)
                        val parentBed = _uiState.value.editedBeds.firstOrNull { it.id == z.bedId }
                        if (parentBed != null) {
                            restored.copy(plantInstances = PlantInstanceGenerator.generate(restored, parentBed.posX, parentBed.posY))
                        } else restored
                    } else z
                }
                _uiState.update { it.copy(cropZones = zones) }
            }
            is EditAction.AddFarmObject -> {
                _uiState.update { it.copy(farmObjects = it.farmObjects.filter { o -> o.id != action.obj.id }) }
            }
        }
    }

    private fun applyAction(action: EditAction) {
        when (action) {
            is EditAction.MoveBed -> {
                val beds = _uiState.value.editedBeds.map {
                    if (it.id == action.bedId) it.copy(posX = action.to.x, posY = action.to.y) else it
                }
                _uiState.update { it.copy(editedBeds = beds, beds = beds.map { b -> b.toRenderData() }) }
            }
            is EditAction.ResizeBed -> {
                val beds = _uiState.value.editedBeds.map {
                    if (it.id == action.bedId) it.copy(widthM = action.toW, heightM = action.toH) else it
                }
                _uiState.update { it.copy(editedBeds = beds, beds = beds.map { b -> b.toRenderData() }) }
            }
            is EditAction.ChangeSoil -> {
                val beds = _uiState.value.editedBeds.map {
                    if (it.id == action.bedId) it.copy(soilType = action.to) else it
                }
                _uiState.update { it.copy(editedBeds = beds, beds = beds.map { b -> b.toRenderData() }) }
            }
            is EditAction.AddBed -> {
                val beds = _uiState.value.editedBeds + action.bed
                _uiState.update { it.copy(editedBeds = beds, beds = beds.map { b -> b.toRenderData() }) }
            }
            is EditAction.DeleteBed -> {
                val beds = _uiState.value.editedBeds.filter { it.id != action.bed.id }
                _uiState.update { it.copy(editedBeds = beds, beds = beds.map { b -> b.toRenderData() }) }
            }
            is EditAction.DuplicateBed -> {
                val beds = _uiState.value.editedBeds + action.duplicate
                _uiState.update { it.copy(editedBeds = beds, beds = beds.map { b -> b.toRenderData() }) }
            }
            is EditAction.ChangeCrop -> {
                val beds = _uiState.value.editedBeds.map {
                    if (it.id == action.bedId) it.copy(cropName = action.toCrop, cropId = action.toCropId) else it
                }
                _uiState.update { it.copy(editedBeds = beds, beds = beds.map { b -> b.toRenderData() }) }
            }
            is EditAction.AddCropZone -> {
                _uiState.update { it.copy(cropZones = it.cropZones + action.zone) }
            }
            is EditAction.ResizeCropZone -> {
                val zones = _uiState.value.cropZones.map { z ->
                    if (z.id == action.zoneId) {
                        val resized = z.copy(widthM = action.toW, heightM = action.toH)
                        val parentBed = _uiState.value.editedBeds.firstOrNull { it.id == z.bedId }
                        if (parentBed != null) {
                            resized.copy(plantInstances = PlantInstanceGenerator.generate(resized, parentBed.posX, parentBed.posY))
                        } else resized
                    } else z
                }
                _uiState.update { it.copy(cropZones = zones) }
            }
            is EditAction.AddFarmObject -> {
                _uiState.update { it.copy(farmObjects = it.farmObjects + action.obj) }
            }
        }
    }

    fun toggleGrid() = _uiState.update { it.copy(isGridEnabled = !it.isGridEnabled) }
    fun toggleSnap() = _uiState.update { it.copy(isSnapEnabled = !it.isSnapEnabled) }
    fun updateZoom(zoom: Float) = _uiState.update { it.copy(zoomPercent = (zoom * 100).toInt()) }

    fun saveChanges(farmName: String = "Murcia Farm", isGuest: Boolean = false, onSaveComplete: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                saveFarmLayoutUseCase?.invoke(_uiState.value.editedBeds)
                undoStack.clear()
                redoStack.clear()
                _uiState.update { it.copy(
                    isSaving = false,
                    hasUnsavedChanges = false,
                    saveSuccess = true
                ) }
                onSaveComplete()
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isSaving = false,
                    hasUnsavedChanges = false,
                    saveSuccess = true
                ) }
                onSaveComplete()
            }
        }
    }

    fun addCropZone(bedId: String, cropName: String?, spacingM: Float = 0.3f) {
        val parentBed = _uiState.value.editedBeds.firstOrNull { it.id == bedId } ?: return
        val newZone = CropZoneRenderData(
            id = UUID.randomUUID().toString(),
            bedId = bedId,
            cropName = cropName,
            offsetX = 0.1f,
            offsetY = 0.1f,
            widthM = (parentBed.widthM - 0.2f).coerceAtLeast(0.5f),
            heightM = (parentBed.heightM - 0.2f).coerceAtLeast(0.5f),
            spacingM = spacingM
        )
        val zoneWithPlants = newZone.copy(
            plantInstances = PlantInstanceGenerator.generate(newZone, parentBed.posX, parentBed.posY)
        )
        undoStack.addLast(EditAction.AddCropZone(zoneWithPlants))
        redoStack.clear()

        _uiState.update { it.copy(
            cropZones = it.cropZones + zoneWithPlants,
            hasUnsavedChanges = true
        ) }
    }

    fun resizeCropZone(zoneId: String, newWidth: Float, newHeight: Float) {
        val currentZones = _uiState.value.cropZones
        val zone = currentZones.firstOrNull { it.id == zoneId } ?: return
        val parentBed = _uiState.value.editedBeds.firstOrNull { it.id == zone.bedId } ?: return
        
        val safeW = newWidth.coerceIn(0.3f, parentBed.widthM)
        val safeH = newHeight.coerceIn(0.3f, parentBed.heightM)

        val updatedZone = zone.copy(widthM = safeW, heightM = safeH)
        val zoneWithPlants = updatedZone.copy(
            plantInstances = PlantInstanceGenerator.generate(updatedZone, parentBed.posX, parentBed.posY)
        )

        undoStack.addLast(EditAction.ResizeCropZone(zoneId, zone.widthM, zone.heightM, safeW, safeH))
        redoStack.clear()

        val updatedZones = currentZones.map { if (it.id == zoneId) zoneWithPlants else it }
        _uiState.update { it.copy(
            cropZones = updatedZones,
            hasUnsavedChanges = true
        ) }
    }

    fun addTrellis(attachedBedId: String) {
        val parentBed = _uiState.value.editedBeds.firstOrNull { it.id == attachedBedId } ?: return
        val newTrellis = FarmObjectRenderData(
            id = UUID.randomUUID().toString(),
            objectType = FarmObjectType.TRELLIS,
            worldX = parentBed.posX,
            worldY = parentBed.posY,
            widthM = parentBed.widthM,
            heightM = parentBed.heightM,
            attachedBedId = attachedBedId
        )
        undoStack.addLast(EditAction.AddFarmObject(newTrellis))
        redoStack.clear()

        _uiState.update { it.copy(
            farmObjects = it.farmObjects + newTrellis,
            hasUnsavedChanges = true
        ) }
    }

    fun regeneratePlantInstances() {
        val updatedZones = _uiState.value.cropZones.map { zone ->
            val parentBed = _uiState.value.editedBeds.firstOrNull { it.id == zone.bedId }
            if (parentBed != null) {
                zone.copy(plantInstances = PlantInstanceGenerator.generate(zone, parentBed.posX, parentBed.posY))
            } else zone
        }
        _uiState.update { it.copy(cropZones = updatedZones) }
    }

    fun discardChanges() {
        undoStack.clear()
        redoStack.clear()
        _uiState.update { it.copy(hasUnsavedChanges = false, selectedBedId = null, selectedZoneId = null) }
    }
}

