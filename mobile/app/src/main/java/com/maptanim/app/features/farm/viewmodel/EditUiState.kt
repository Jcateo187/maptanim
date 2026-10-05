package com.maptanim.app.features.farm.viewmodel

import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.EditTool
import com.maptanim.app.domain.model.SoilType
import com.maptanim.app.features.farm.renderer.model.CropZoneRenderData
import com.maptanim.app.features.farm.renderer.model.PlotRenderData
import com.maptanim.app.features.farm.tabs.plan.AVAILABLE_CROP_CATALOG
import com.maptanim.app.features.farm.tabs.plan.CropOption

/**
 * Draft model for reviewing new or changed crop plantings before saving.
 */
data class CropPlantingDraft(
    val id: String,                  // Unique ID (zoneId or plotId)
    val cropName: String,            // e.g. "Carrot", "Tomato"
    val cropId: String,              // e.g. "carrot", "tomato"
    val bedId: String,               // Parent bed / plot ID
    val bedLabel: String,            // e.g. "Bed #1"
    val variety: String = "",        // e.g. "New Kuroda"
    val varietyId: String = "",      // e.g. "new_kuroda"
    val plantingDate: String = "",   // YYYY-MM-DD
    val plantCount: Int = 1,         // Number of plants
    val soilType: SoilType = SoilType.LOAM, // Bed soil classification
    val bedDimensions: String = "1.5m × 2.0m", // Dimension string e.g. "1.5m × 2.0m"
    val notes: String = "",          // Farmer notes
    val isNew: Boolean = true,
    val isChanged: Boolean = false,
    val validationError: String? = null,
    val plantingMethod: String = "Direct Seeding",
    val growingApproach: String = "Organic"
)

/**
 * EditUiState — Immutable state holder for FarmEditorScreen.
 */
data class EditUiState(
    val editedPlots: List<CropPlot> = emptyList(),
    val plots: List<PlotRenderData> = emptyList(),
    val cropZones: List<CropZoneRenderData> = emptyList(),
    val availableCrops: List<CropOption> = AVAILABLE_CROP_CATALOG,
    val isSyncingCrops: Boolean = false,
    val selectedPlotId: String? = null,
    val selectedZoneId: String? = null,
    val isResizeMode: Boolean = false,
    val activeTool: EditTool = EditTool.SELECT_MOVE,
    val activeSoilType: SoilType = SoilType.LOAM,
    val isGridEnabled: Boolean = true,
    val isSnapEnabled: Boolean = true,
    val zoom: Float = 1.0f,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val hasUnsavedChanges: Boolean = false,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val showCropSummaryOverlay: Boolean = false,
    val pendingCropPlantings: List<CropPlantingDraft> = emptyList(),
    val saveErrorMessage: String? = null,
    val dropFeedbackMessage: String? = null,
    val isSaveSuccessful: Boolean = false
)
