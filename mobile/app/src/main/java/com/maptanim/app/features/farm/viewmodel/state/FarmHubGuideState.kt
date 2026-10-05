package com.maptanim.app.features.farm.viewmodel.state

import com.maptanim.app.domain.model.ManagementStage
import com.maptanim.app.dss.engine.DssLogEvaluator
import com.maptanim.app.features.farm.viewmodel.DssTab

/**
 * State sub-delegate for the Guide tab: stage management, dynamic DSS tasks, and recommendations.
 */
data class FarmHubGuideState(
    val currentStage: ManagementStage = ManagementStage.PREPARATION,
    val selectedDssTab: DssTab = DssTab.TASKS,
    val dynamicTasks: List<DssLogEvaluator.GeneratedLogTask> = emptyList(),
    val dynamicRecommendations: List<DssLogEvaluator.LogRecommendation> = emptyList(),
    val dynamicAlerts: List<DssLogEvaluator.LogAlert> = emptyList(),
    val stageNotificationText: String? = null,
    val pendingStageTransition: ManagementStage? = null,
    val expandedStageAccordions: Set<String> = emptySet(),
    val daysPlanted: Int = 0,
    val daysToHarvest: Int = 75
)
