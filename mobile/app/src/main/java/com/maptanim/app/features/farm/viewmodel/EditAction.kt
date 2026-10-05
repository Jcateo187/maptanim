package com.maptanim.app.features.farm.viewmodel

import androidx.compose.ui.geometry.Offset
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.SoilType
import com.maptanim.app.features.farm.renderer.model.CropZoneRenderData

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
