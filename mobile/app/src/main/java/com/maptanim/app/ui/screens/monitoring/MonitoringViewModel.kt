package com.maptanim.app.ui.screens.monitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.data.datasource.CropMetadataAssetDataSource
import com.maptanim.app.data.datasource.StageDaysInfo
import com.maptanim.app.data.remote.SupabaseClient
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.domain.model.*
import com.maptanim.app.domain.repository.ActivityRepository
import com.maptanim.app.domain.repository.CropPlotRepository
import com.maptanim.app.domain.repository.CropRepository
import com.maptanim.app.domain.repository.KnowledgeBaseRepository
import com.maptanim.app.dss.engine.CompanionAlert
import com.maptanim.app.dss.engine.DssEngine
import com.maptanim.app.dss.engine.DssRule
import com.maptanim.app.dss.knowledgebase.CompanionDataProvider
import com.maptanim.app.dss.knowledgebase.CompanionEntry
import com.maptanim.app.dss.knowledgebase.GrowingTip
import com.maptanim.app.dss.knowledgebase.GrowingTipsProvider
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID

enum class MonitoringNavSection(val title: String) {
    MY_PLANTS("My Plants"),
    TIMELINE("Timeline"),
    CALENDAR("Calendar"),
    COMPANIONS("Companions"),
    GROWING_TIPS("Growing Tips"),
    PEST_DISEASE("Pest & Disease")
}

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
    val cropName: String,
    val localName: String,
    val cropVariety: String? = null,
    val plotLabel: String,
    val seasonality: SeasonalityFilter,
    val category: CropCategoryFilter,
    val currentStageIndex: Int,
    val stageName: String,
    val daysPlanted: Int,
    val daysToHarvest: Int,
    val healthStatus: String,
    val companionCrop: String,
    val companionStatus: String,
    val growingTip: String,
    val pestInfo: String,
    val assetPath: String,
    val imageUrl: String? = null,
    val rawPlantedDate: String? = null,
    val isMonitoringStarted: Boolean = false,
    val soilType: SoilType = SoilType.LOAM,
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

data class MonitoringUiState(
    val selectedNavSection: MonitoringNavSection = MonitoringNavSection.MY_PLANTS,
    val selectedSeasonality: SeasonalityFilter = SeasonalityFilter.ALL,
    val selectedCategory: CropCategoryFilter = CropCategoryFilter.ALL,
    val searchQuery: String = "",
    val isCategoryDropdownExpanded: Boolean = false,
    val plantedCrops: List<MonitoredPlant> = emptyList(),
    val completedTaskMessage: String? = null
)

class MonitoringViewModel(
    private val plotRepository: CropPlotRepository = RepositoryProvider.cropPlotRepository,
    private val cropRepository: CropRepository = RepositoryProvider.cropRepository,
    private val knowledgeBaseRepository: KnowledgeBaseRepository = RepositoryProvider.knowledgeBaseRepository,
    private val activityRepository: ActivityRepository = RepositoryProvider.activityRepository,
    private val dssEngine: DssEngine = DssEngine()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MonitoringUiState())
    val uiState: StateFlow<MonitoringUiState> = _uiState.asStateFlow()

    private val dssRules: List<DssRule> by lazy {
        CompanionDataProvider.companionMatrix.mapIndexed { index, entry ->
            DssRule(
                id = "rule_$index",
                cropA = entry.cropA,
                cropB = entry.cropB,
                relationship = entry.relationship,
                notes = entry.reason
            )
        }
    }

    init {
        observeLivePlantedCrops()
    }

    private fun observeLivePlantedCrops() {
        val farmerId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: "farmer-1"
        viewModelScope.launch {
            combine(
                plotRepository.observeAllPlotsWithCrop(farmerId),
                cropRepository.observeAllCrops(),
                knowledgeBaseRepository.observePestGuides(),
                tickerFlow()
            ) { plots, crops, allPests, currentMs ->
                val today = LocalDate.now()

                // Fetch activities for all plots to feed into DSS
                val dssResult = dssEngine.evaluate(
                    plots = plots,
                    crops = crops,
                    rules = dssRules,
                    activities = emptyList(), // real-time activities evaluated per plot
                    today = today
                )

                plots.map { plot ->
                    val crop = crops.firstOrNull { it.name.equals(plot.cropName, ignoreCase = true) }
                    val varietyName = plot.cropVariety
                    val isAmpalayaOrSim = plot.cropName?.lowercase()?.contains("ampalaya") == true || varietyName?.contains("10s", ignoreCase = true) == true

                    var daysPlanted = 0
                    var daysToHarvest = 60
                    var stageIndex = 0
                    var stageProgress = 0f
                    val isStarted = plot.plantedDate != null

                    if (isAmpalayaOrSim) {
                        val plantedMs = try {
                            java.time.ZonedDateTime.parse(plot.plantedDate).toInstant().toEpochMilli()
                        } catch (e: Exception) {
                            try {
                                val d = LocalDate.parse(plot.plantedDate!!.take(10))
                                d.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                            } catch (e2: Exception) {
                                0L
                            }
                        }
                        val startMs = if (plantedMs > 0L) plantedMs else 0L
                        val elapsedMs = (currentMs - startMs).coerceAtLeast(0L)
                        val simProgress = (elapsedMs / 10000f)
                        stageProgress = simProgress.coerceIn(0f, 1f)
                        daysToHarvest = 10
                        daysPlanted = (elapsedMs / 1000L).toInt()
                        stageIndex = when {
                            simProgress < 0.20f -> 0
                            simProgress < 0.40f -> 1
                            simProgress < 0.60f -> 2
                            simProgress < 0.80f -> 3
                            else -> 4
                        }
                    } else {
                        val plantedLocalDate = plot.plantedDate?.let {
                            try { LocalDate.parse(it.take(10)) } catch (e: Exception) { null }
                        }
                        val isFuture = plantedLocalDate != null && plantedLocalDate.isAfter(LocalDate.now())
                        daysPlanted = if (plantedLocalDate != null && !isFuture) {
                            ChronoUnit.DAYS.between(plantedLocalDate, LocalDate.now()).toInt().coerceAtLeast(0)
                        } else 0

                        daysToHarvest = getVarietyDurationDays(plot.cropName ?: "", varietyName) ?: crop?.daysToHarvest ?: 60
                        stageProgress = if (daysToHarvest > 0 && isStarted && !isFuture) (daysPlanted.toFloat() / daysToHarvest).coerceIn(0f, 1f) else 0f
                        stageIndex = when {
                            isFuture -> 0
                            stageProgress < 0.15f -> 0
                            stageProgress < 0.35f -> 1
                            stageProgress < 0.65f -> 2
                            stageProgress < 0.90f -> 3
                            else -> 4
                        }
                    }

                    val isFutureScheduled = plot.plantedDate != null && try {
                        LocalDate.parse(plot.plantedDate.take(10)).isAfter(LocalDate.now())
                    } catch (e: Exception) { false }

                    val isOverdue = isStarted && !isFutureScheduled && daysToHarvest > 0 && daysPlanted > daysToHarvest
                    val isHarvestReady = isStarted && !isFutureScheduled && (stageIndex == 4 || daysPlanted >= daysToHarvest)

                    val stageName = when {
                        isFutureScheduled -> "Planned (Target: ${plot.plantedDate?.take(10)})"
                        isOverdue -> if (isAmpalayaOrSim) "Stage 5: Harvest Overdue ⚠️ (10s Sim)" else "Stage 5: Harvest Overdue ⚠️"
                        stageIndex == 4 -> if (isAmpalayaOrSim) "Stage 5: Harvest Ready 🌾 (10s Sim)" else "Stage 5: Harvest Ready 🌾"
                        stageIndex == 3 -> if (isAmpalayaOrSim) "Stage 4: Flowering (10s Sim)" else "Stage 4: Flowering"
                        stageIndex == 2 -> if (isAmpalayaOrSim) "Stage 3: Vegetative (10s Sim)" else "Stage 3: Vegetative"
                        stageIndex == 1 -> if (isAmpalayaOrSim) "Stage 2: Seedling (10s Sim)" else "Stage 2: Seedling"
                        else -> if (isAmpalayaOrSim) "Stage 1: Sprout (10s Sim)" else "Stage 1: Sprout"
                    }

                    val cropCleanName = (crop?.name ?: plot.cropName ?: "carrot").lowercase().replace(" ", "")
                    val cropName = crop?.name ?: plot.cropName ?: "Vegetable"
                    val localName = crop?.localName ?: plot.cropName ?: "Gulay"

                    // Soil Suitability Score
                    val soilScore = dssResult.soilScores.firstOrNull { it.plotLabel == plot.plotLabel }?.score

                    // DSS Tasks for this specific plot
                    val plotTasks = dssResult.tasks.filter { it.plotId == plot.id }

                    // Companion alerts involving this plot
                    val plotAlerts = dssResult.companionAlerts.filter {
                        it.plotALabel == plot.plotLabel || it.plotBLabel == plot.plotLabel
                    }

                    // Active companion pairs with other plots on the farm
                    val otherPlotsWithCrops = plots.filter { it.id != plot.id && !it.cropName.isNullOrBlank() }
                    val activeCompanionEvals = otherPlotsWithCrops.mapNotNull { otherPlot ->
                        val rel = CompanionDataProvider.getRelationship(cropName, otherPlot.cropName!!)
                        rel?.let {
                            CompanionEntry(
                                cropA = "${plot.plotLabel} ($cropName)",
                                cropB = "${otherPlot.plotLabel} (${otherPlot.cropName})",
                                relationship = it.relationship,
                                reason = it.reason
                            )
                        }
                    }

                    val beneficialCompanions = CompanionDataProvider.getBeneficialCompanions(cropName)
                    val antagonistCompanions = CompanionDataProvider.getAntagonistCrops(cropName)

                    // Growing Tips from GrowingTipsProvider
                    val stageGrowingTips = GrowingTipsProvider.getTips(cropName, stageIndex)
                    val generalCareTips = GrowingTipsProvider.getGeneralInfo(
                        cropName = cropName,
                        soilScore = soilScore,
                        nRatio = crop?.nRatio ?: 1.0f,
                        pRatio = crop?.pRatio ?: 1.0f,
                        kRatio = crop?.kRatio ?: 1.0f,
                        optimalPhMin = crop?.optimalPhMin ?: 6.0f,
                        optimalPhMax = crop?.optimalPhMax ?: 7.0f
                    )

                    // Affected pests for this crop
                    val matchingPests = allPests.filter { pest ->
                        pest.affectedCrops.any {
                            it.contains(cropName, ignoreCase = true) ||
                            it.contains(localName, ignoreCase = true) ||
                            cropName.contains(it, ignoreCase = true)
                        }
                    }

                    // Load variety stage days if available
                    val metaVarieties = CropMetadataAssetDataSource.getVarietiesForCrop(RepositoryProvider.appContext, cropName)
                    val matchedVariety = metaVarieties.firstOrNull { it.varietyName.equals(varietyName, ignoreCase = true) }
                    val stageDays = matchedVariety?.stageDays

                    MonitoredPlant(
                        id = plot.id,
                        farmId = plot.farmId,
                        cropName = cropName,
                        localName = localName,
                        cropVariety = varietyName ?: if (isAmpalayaOrSim) "Ampalaya 10s Simulation Test ⚡" else null,
                        plotLabel = plot.plotLabel,
                        seasonality = SeasonalityFilter.SEASONAL,
                        category = mapCategory(crop?.category),
                        currentStageIndex = stageIndex,
                        stageName = stageName,
                        daysPlanted = daysPlanted,
                        daysToHarvest = daysToHarvest,
                        healthStatus = when {
                            !isStarted -> "Pending Start"
                            isFutureScheduled -> {
                                val targetD = plot.plantedDate?.take(10) ?: "Scheduled"
                                "📅 Scheduled for $targetD"
                            }
                            isOverdue -> "HARVEST OVERDUE — Harvest Immediately!"
                            isHarvestReady -> "HARVEST READY — Ready to Harvest!"
                            else -> "Active Monitoring — Healthy Growth"
                        },
                        companionCrop = if (activeCompanionEvals.isNotEmpty()) {
                            activeCompanionEvals.joinToString(", ") { it.cropB }
                        } else {
                            crop?.companionPlants?.joinToString(", ") ?: "None listed"
                        },
                        companionStatus = when {
                            plotAlerts.isNotEmpty() -> "ANTAGONIST ALERT: ${plotAlerts.first().message}"
                            activeCompanionEvals.any { it.relationship == CompanionRelation.BENEFICIAL } -> "Beneficial Companion Nearby"
                            activeCompanionEvals.isNotEmpty() -> "Neutral Companion Coexistence"
                            else -> "No companion assigned"
                        },
                        growingTip = crop?.description ?: "Maintain consistent irrigation and soil organic matter.",
                        pestInfo = matchingPests.firstOrNull()?.name ?: "Inspect weekly for caterpillars and aphids.",
                        assetPath = "crops/crop_${cropCleanName}_${stageIndex + 1}.png",
                        imageUrl = crop?.imageUrl,
                        rawPlantedDate = plot.plantedDate,
                        isMonitoringStarted = isStarted,
                        soilType = plot.soilType,
                        soilScore = soilScore,
                        nRatio = crop?.nRatio ?: 1.0f,
                        pRatio = crop?.pRatio ?: 1.0f,
                        kRatio = crop?.kRatio ?: 1.0f,
                        optimalPhMin = crop?.optimalPhMin ?: 6.0f,
                        optimalPhMax = crop?.optimalPhMax ?: 7.0f,
                        dssTasks = plotTasks,
                        companionAlerts = plotAlerts,
                        activeCompanionEvaluations = activeCompanionEvals,
                        beneficialCompanions = beneficialCompanions,
                        antagonistCompanions = antagonistCompanions,
                        growingTipsList = stageGrowingTips,
                        generalCareTips = generalCareTips,
                        affectedPests = matchingPests,
                        stageDays = stageDays
                    )
                }
            }
            .flowOn(kotlinx.coroutines.Dispatchers.Default)
            .collect { monitoredList ->
                _uiState.update { it.copy(plantedCrops = monitoredList) }
            }
        }
    }

    private fun tickerFlow(): Flow<Long> = flow {
        while (true) {
            emit(System.currentTimeMillis())
            kotlinx.coroutines.delay(500)
        }
    }

    private fun mapCategory(categoryStr: String?): CropCategoryFilter {
        return when (categoryStr?.uppercase()) {
            "LEAFY" -> CropCategoryFilter.LEAFY
            "ROOT" -> CropCategoryFilter.ROOT
            "BULB" -> CropCategoryFilter.BULB
            "STEM" -> CropCategoryFilter.STEM
            "FLOWER" -> CropCategoryFilter.FLOWER
            "FRUIT", "FRUITING" -> CropCategoryFilter.FRUIT
            "PODDED", "LEGUMES" -> CropCategoryFilter.PODDED
            "TUBER" -> CropCategoryFilter.TUBER
            else -> CropCategoryFilter.ALL
        }
    }

    private fun getVarietyDurationDays(cropName: String, varietyName: String?): Int? {
        if (varietyName == null) return null
        return when (varietyName.lowercase()) {
            "10s fast simulation test ⚡", "ampalaya 10s simulation test ⚡" -> 10
            "diamante max f1" -> 55
            "apollo" -> 60
            "morena f1" -> 60
            "dumaguete long purple" -> 70
            "sandigan f1" -> 45
            "galante f1" -> 48
            "terracotta f1" -> 75
            "kuroda improved" -> 85
            "red pinoy f1" -> 90
            "yellow granex" -> 100
            "suprema f1" -> 85
            "horizon f1" -> 80
            "machismo f1 (sweet)" -> 70
            "ipb var 6 (white)" -> 75
            "k-s cross f1" -> 55
            "kyross f1" -> 60
            "pavon" -> 28
            "jade star xl f1" -> 55
            "smooth green" -> 45
            "django f1" -> 65
            else -> null
        }
    }

    fun completeDssTask(plotId: String, farmId: String, taskType: TaskType, notes: String? = null) {
        viewModelScope.launch {
            val activity = Activity(
                id = UUID.randomUUID().toString(),
                plotId = plotId,
                farmId = farmId,
                type = taskType,
                notes = notes ?: "Completed via Monitoring Dashboard",
                performedAt = java.time.ZonedDateTime.now().toString()
            )
            activityRepository.logActivity(activity)
            _uiState.update {
                it.copy(completedTaskMessage = "Recorded ${taskType.name.replace("_", " ")} activity ✅")
            }
        }
    }

    fun clearTaskMessage() {
        _uiState.update { it.copy(completedTaskMessage = null) }
    }

    fun startMonitoring(plantId: String, targetDate: String = java.time.LocalDate.now().toString(), varietyName: String = "Ampalaya 10s Simulation Test ⚡") {
        viewModelScope.launch {
            plotRepository.observePlot(plantId).take(1).collect { plot ->
                plot?.let {
                    val dateOnly = targetDate.take(10)
                    val isToday = dateOnly == java.time.LocalDate.now().toString()
                    val isSim = varietyName.contains("10s", ignoreCase = true) ||
                            (plot.cropName?.lowercase()?.contains("ampalaya") == true && varietyName.contains("Simulation", ignoreCase = true))

                    val newPlantedDate = when {
                        isSim && isToday -> java.time.ZonedDateTime.now().toString()
                        else -> "${dateOnly}T00:00:00Z"
                    }

                    val updatedPlot = it.copy(
                        plantedDate = newPlantedDate,
                        cropVariety = varietyName.ifBlank { "Ampalaya 10s Simulation Test ⚡" }
                    )
                    plotRepository.upsertPlot(updatedPlot)
                }
            }
        }
    }

    fun cancelMonitoringSchedule(plantId: String) {
        viewModelScope.launch {
            plotRepository.observePlot(plantId).take(1).collect { plot ->
                plot?.let {
                    val updatedPlot = it.copy(
                        plantedDate = null,
                        cropVariety = null
                    )
                    plotRepository.upsertPlot(updatedPlot)
                }
            }
        }
    }

    fun completeHarvest(plantId: String, yieldKg: Float? = null, notes: String? = null) {
        viewModelScope.launch {
            plotRepository.recordHarvest(plantId, yieldKg, notes)
        }
    }

    fun selectNavSection(section: MonitoringNavSection) {
        _uiState.update { it.copy(selectedNavSection = section) }
    }

    fun selectSeasonality(seasonality: SeasonalityFilter) {
        _uiState.update { it.copy(selectedSeasonality = seasonality) }
    }

    fun selectCategory(category: CropCategoryFilter) {
        _uiState.update { it.copy(selectedCategory = category, isCategoryDropdownExpanded = false) }
    }

    fun toggleCategoryDropdown(expanded: Boolean) {
        _uiState.update { it.copy(isCategoryDropdownExpanded = expanded) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun getFilteredCrops(): List<MonitoredPlant> {
        val state = _uiState.value
        return state.plantedCrops.filter { plant ->
            val matchSeason = state.selectedSeasonality == SeasonalityFilter.ALL || plant.seasonality == state.selectedSeasonality
            val matchCategory = state.selectedCategory == CropCategoryFilter.ALL || plant.category == state.selectedCategory
            val matchSearch = state.searchQuery.isBlank() ||
                    plant.cropName.contains(state.searchQuery, ignoreCase = true) ||
                    plant.localName.contains(state.searchQuery, ignoreCase = true) ||
                    plant.plotLabel.contains(state.searchQuery, ignoreCase = true)
            matchSeason && matchCategory && matchSearch
        }
    }
}
