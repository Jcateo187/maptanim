package com.maptanim.app.features.farm.viewmodel.state

import com.maptanim.app.domain.model.CropLog
import com.maptanim.app.features.farm.components.MonitoredPlant

/**
 * State sub-delegate for the CheckUp tab: observation logs, pest/disease checks, and timeline.
 */
data class FarmHubCheckUpState(
    val observedLogs: List<CropLog> = emptyList(),
    val selectedPlantForDetails: MonitoredPlant? = null,
    val isAddLogOpen: Boolean = false,
    val isTimelineExpanded: Boolean = true,
    val pestRiskAlertCount: Int = 0
)
