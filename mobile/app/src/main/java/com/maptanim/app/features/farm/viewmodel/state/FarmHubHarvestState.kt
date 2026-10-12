package com.maptanim.app.features.farm.viewmodel.state

import com.maptanim.app.domain.model.HarvestRecord

/**
 * State sub-delegate for the Harvest tab: readiness indicators, yield logging, and harvest history.
 */
data class FarmHubHarvestState(
    val harvestCount: Int = 0,
    val totalYieldKg: Float = 0f,
    val harvestHistory: List<HarvestRecord> = emptyList(),
    val isHarvestModalOpen: Boolean = false,
    val isReadyForHarvest: Boolean = false,
    val overdueHarvestWarning: String? = null
)
