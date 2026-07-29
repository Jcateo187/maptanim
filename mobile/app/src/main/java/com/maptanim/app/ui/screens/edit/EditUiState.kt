package com.maptanim.app.ui.screens.edit

import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.EditTool
import com.maptanim.app.domain.model.SoilType
import com.maptanim.app.renderer.model.CropZoneRenderData
import com.maptanim.app.renderer.model.FarmObjectRenderData
import com.maptanim.app.renderer.model.PlotRenderData

/**
 * EditUiState — Immutable state holder for FarmEditorScreen.
 */
data class EditUiState(
    val editedPlots: List<CropPlot> = emptyList(),
    val plots: List<PlotRenderData> = emptyList(),
    val cropZones: List<CropZoneRenderData> = emptyList(),
    val farmObjects: List<FarmObjectRenderData> = emptyList(),
    val selectedPlotId: String? = null,
    val selectedZoneId: String? = null,
    val activeTool: EditTool = EditTool.SELECT_MOVE,
    val activeSoilType: SoilType = SoilType.LOAM,
    val isGridEnabled: Boolean = true,
    val isSnapEnabled: Boolean = true,
    val zoom: Float = 1.0f,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val hasUnsavedChanges: Boolean = false,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false
)
