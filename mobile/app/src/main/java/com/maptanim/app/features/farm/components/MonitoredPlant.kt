package com.maptanim.app.features.farm.components

import com.maptanim.app.data.datasource.StageDaysInfo
import com.maptanim.app.domain.model.*
import com.maptanim.app.dss.engine.CompanionAlert
import com.maptanim.app.dss.engine.DssEngine
import com.maptanim.app.dss.knowledgebase.CompanionEntry
import com.maptanim.app.dss.knowledgebase.GrowingTip

enum class SeasonalityFilter(val label: String) {
    ALL("All"),
    SEASONAL("Seasonal"),
    PERMANENT("Permanent"),
    SEMI_PERMANENT("Semi Permanent")
}

enum class CropCategoryFilter(val label: String) {
    ALL("All Categories"),
    LEAFY("Leafy"),
    ROOT("Root"),
    BULB("Bulb"),
    STEM("Stem"),
    FLOWER("Flower"),
    PODDED("Podded"),
    TUBER("Tuber"),
    FRUIT("Fruit")
}

data class MonitoredPlant(
    val id: String,
    val farmId: String = "farm-1",
    val cropId: String? = null,
    val cropName: String,
    val localName: String,
    val cropVariety: String? = null,
    val plotLabel: String,
    val seasonality: SeasonalityFilter = SeasonalityFilter.ALL,
    val category: CropCategoryFilter = CropCategoryFilter.ALL,
    val currentStageIndex: Int = 1,
    val stageName: String = "Vegetative",
    val daysPlanted: Int = 0,
    val daysToHarvest: Int = 60,
    val healthStatus: String = "Healthy",
    val companionCrop: String = "",
    val companionStatus: String = "",
    val growingTip: String = "",
    val pestInfo: String = "",
    val assetPath: String = "",
    val imageUrl: String? = null,
    val rawPlantedDate: String? = null,
    val isMonitoringStarted: Boolean = false,
    val soilType: SoilType = SoilType.LOAM,
    val suitableSoils: List<SoilType> = listOf(SoilType.LOAM),
    val season: Season = Season.YEAR_ROUND,
    val soilScore: Float? = null,
    val nRatio: Float = 1.0f,
    val pRatio: Float = 1.0f,
    val kRatio: Float = 1.0f,
    val optimalPhMin: Float = 6.0f,
    val optimalPhMax: Float = 7.0f,
    val dssTasks: List<DssEngine.GeneratedTask> = emptyList(),
    val companionAlerts: List<CompanionAlert> = emptyList(),
    val activeCompanionEvaluations: List<CompanionEntry> = emptyList(),
    val beneficialCompanions: List<String> = emptyList(),
    val antagonistCompanions: List<String> = emptyList(),
    val growingTipsList: List<GrowingTip> = emptyList(),
    val generalCareTips: List<GrowingTip> = emptyList(),
    val affectedPests: List<PestGuide> = emptyList(),
    val stageDays: StageDaysInfo? = null
)
