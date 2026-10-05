package com.maptanim.app.features.farm.viewmodel.state

import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.EditTool
import com.maptanim.app.domain.model.SoilType
import com.maptanim.app.features.farm.canvas.CanvasLayer
import com.maptanim.app.features.farm.renderer.model.CropZoneRenderData
import com.maptanim.app.features.farm.renderer.model.PlotRenderData
import com.maptanim.app.features.farm.tabs.plan.AVAILABLE_CROP_CATALOG
import com.maptanim.app.features.farm.tabs.plan.CropOption
import com.maptanim.app.features.farm.viewmodel.CropPlantingDraft

/**
 * State sub-delegate for the Plan tab: layout editing, zoning, scale, and tools.
 */
data class FarmHubPlanState(
    val plots: List<PlotRenderData> = emptyList(),
    val rawPlots: List<CropPlot> = emptyList(),
    val cropZones: List<CropZoneRenderData> = emptyList(),
    val availableCrops: List<CropOption> = AVAILABLE_CROP_CATALOG,
    val selectedPlotId: String? = null,
    val selectedZoneId: String? = null,
    val activeTool: EditTool = EditTool.SELECT_MOVE,
    val activeSoilType: SoilType = SoilType.LOAM,
    val canvasLayer: CanvasLayer = CanvasLayer.CROPS,
    val isResizeMode: Boolean = false,
    val isGridEnabled: Boolean = true,
    val isSnapEnabled: Boolean = true,
    val zoom: Float = 1.0f,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val hasUnsavedChanges: Boolean = false,
    val pendingCropPlantings: List<CropPlantingDraft> = emptyList(),
    val showAddBedDialog: Boolean = false,
    val showBasketballScale: Boolean = false,
    val farmEnvironment: com.maptanim.app.domain.model.FarmEnvironment = com.maptanim.app.domain.model.FarmEnvironment(),
    val showFarmSetupDialog: Boolean = false
)
