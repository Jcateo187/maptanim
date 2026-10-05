package com.maptanim.app.features.farm.viewmodel.state

import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.FarmEnvironment
import com.maptanim.app.features.farm.viewmodel.TopTab

/**
 * Unified UI State for the Farm Hub, combining Plan, Guide, CheckUp, and Harvest tabs.
 */
data class FarmHubUiState(
    val activeFarmId: String = "",
    val farmName: String = "My Farm",
    val farmEnvironment: FarmEnvironment = FarmEnvironment(),
    val showFarmSetupDialog: Boolean = false,
    val selectedTopTab: TopTab = TopTab.PLAN,
    val planState: FarmHubPlanState = FarmHubPlanState(),
    val guideState: FarmHubGuideState = FarmHubGuideState(),
    val checkUpState: FarmHubCheckUpState = FarmHubCheckUpState(),
    val harvestState: FarmHubHarvestState = FarmHubHarvestState(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val activePlot: CropPlot?
        get() = planState.rawPlots.firstOrNull { it.id == planState.selectedPlotId }
            ?: planState.rawPlots.firstOrNull()
}
