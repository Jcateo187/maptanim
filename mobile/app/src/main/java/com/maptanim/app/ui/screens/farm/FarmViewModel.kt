package com.maptanim.app.ui.screens.farm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.core.preferences.FarmPreferencesManager
import com.maptanim.app.data.remote.SupabaseClient
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.domain.model.*
import com.maptanim.app.domain.repository.*
import com.maptanim.app.domain.usecase.*
import com.maptanim.app.dss.engine.CompanionAlert
import com.maptanim.app.dss.engine.DssEngine
import com.maptanim.app.dss.engine.DssRule
import com.maptanim.app.dss.engine.DssLogEvaluator
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

enum class FarmTab(val title: String) {
    OVERVIEW("Overview"),
    MONITORING("Monitoring"),
    CALENDAR("Calendar"),
    ACTIVITY("Activity History")
}

enum class CropsFilter(val label: String) {
    PLANTED("Planted"),
    PLANNED("Planned / Unplanted")
}

enum class AttentionSeverity {
    HIGH, MEDIUM, INFO
}

data class FarmAttentionItem(
    val id: String,
    val title: String,
    val description: String,
    val severity: AttentionSeverity,
    val icon: String,
    val plotLabel: String? = null,
    val date: String = LocalDate.now().toString(),
    val companionRecommendation: String? = null,
    val isCompanionAlert: Boolean = false,
    val cropA: String? = null,
    val cropB: String? = null
)

enum class ActivityCategory(val label: String) {
    ALL("All"),
    CROP_ADDED("Crop Added"),
    CROP_PLANTED("Crop Planted"),
    MONITORING_RECORDED("Monitored"),
    ACTIVITY_COMPLETED("Task Done"),
    HARVEST_RECORDED("Harvested")
}

data class FarmHistoryItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val details: String?,
    val timestamp: String,
    val category: ActivityCategory,
    val plotLabel: String? = null
)

enum class CalendarEventType(val label: String, val icon: String) {
    PLANTING("Planting", "🌱"),
    GROWTH_STAGE("Growth Stage", "🌿"),
    MONITORING("Monitoring", "🔍"),
    CROP_CARE("Crop Care", "💧"),
    DSS_TASK("Task", "📋"),
    HARVEST("Harvest", "🌾"),
    COMPLETED("Completed", "✓")
}

data class FarmTimelineEvent(
    val id: String,
    val title: String,
    val subtitle: String,
    val date: LocalDate,
    val type: CalendarEventType,
    val plotLabel: String,
    val isCompleted: Boolean = false,
    val extraInfo: String? = null,
    val plotId: String? = null,
    val cropName: String? = null,
    val cropId: String? = null,
    val taskId: String? = null
)

data class CropMonitorCardData(
    val plot: CropPlot,
    val crop: Crop?,
    val monitoredPlant: MonitoredPlant?,
    val currentStageName: String,
    val daysPlanted: Int,
    val growthObservation: String,
    val plantCondition: String,
    val pestObservation: String,
    val lastRecordedAt: String?
)

data class FarmUiState(
    val farmId: String = "farm-1",
    val farmName: String = "SUNRISE FARM",
    val selectedTab: FarmTab = FarmTab.OVERVIEW,
    val cropsFilter: CropsFilter = CropsFilter.PLANTED,
    // Overview data
    val plots: List<CropPlot> = emptyList(),
    val crops: List<Crop> = emptyList(),
    val cropZones: List<CropZone> = emptyList(),
    val attentionItems: List<FarmAttentionItem> = emptyList(),
    val todayTasks: List<FarmTask> = emptyList(),
    val recentActivities: List<FarmHistoryItem> = emptyList(),
    // Crops tab data
    val plantedPlants: List<MonitoredPlant> = emptyList(),
    val plannedPlots: List<CropPlot> = emptyList(),
    val selectedPlantForDetails: MonitoredPlant? = null,
    val selectedPlotForDetails: CropPlot? = null,
    // Calendar tab data
    val selectedCalendarDate: LocalDate = LocalDate.now(),
    val calendarMonth: java.time.YearMonth = java.time.YearMonth.now(),
    val isCalendarDateFilterActive: Boolean = false,
    val calendarFilter: CalendarEventType? = null,
    val timelineEvents: List<FarmTimelineEvent> = emptyList(),
    // Monitor tab data
    val monitorItems: List<CropMonitorCardData> = emptyList(),
    val isObservationModalOpen: Boolean = false,
    val observationTargetPlot: CropPlot? = null,
    // Activity tab data
    val activityHistory: List<FarmHistoryItem> = emptyList(),
    val selectedActivityFilter: ActivityCategory = ActivityCategory.ALL,
    // Toast & Dialog state
    val toastMessage: String? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

private data class FarmInputGroup(
    val plots: List<CropPlot>,
    val zones: List<CropZone>,
    val crops: List<Crop>,
    val tasks: List<FarmTask>
)

class FarmViewModel(
    private val plotRepository: CropPlotRepository = RepositoryProvider.cropPlotRepository,
    private val cropRepository: CropRepository = RepositoryProvider.cropRepository,
    private val farmRepository: FarmRepository = RepositoryProvider.farmRepository,
    private val taskRepository: TaskRepository = RepositoryProvider.taskRepository,
    private val activityRepository: ActivityRepository = RepositoryProvider.activityRepository,
    private val harvestRepository: HarvestRepository = RepositoryProvider.harvestRepository,
    private val knowledgeBaseRepository: KnowledgeBaseRepository = RepositoryProvider.knowledgeBaseRepository,
    private val dssRuleRepository: DssRuleRepository = RepositoryProvider.dssRuleRepository,
    private val dssEngine: DssEngine = DssEngine(),
    // Domain Use Cases
    private val getFarmPlotsUseCase: GetFarmPlotsUseCase = GetFarmPlotsUseCase(plotRepository),
    private val getFarmsUseCase: GetFarmsUseCase = GetFarmsUseCase(farmRepository),
    private val getTodayTasksUseCase: GetTodayTasksUseCase = GetTodayTasksUseCase(taskRepository),
    private val completeTaskUseCase: CompleteTaskUseCase = CompleteTaskUseCase(taskRepository),
    private val getAllCropsUseCase: GetAllCropsUseCase = GetAllCropsUseCase(cropRepository),
    private val observeFarmActivitiesUseCase: ObserveFarmActivitiesUseCase = ObserveFarmActivitiesUseCase(activityRepository),
    private val logFarmActivityUseCase: LogFarmActivityUseCase = LogFarmActivityUseCase(activityRepository),
    private val startPlantingUseCase: StartPlantingUseCase = StartPlantingUseCase(plotRepository),
    private val recordHarvestUseCase: RecordHarvestUseCase = RecordHarvestUseCase(harvestRepository),
    private val cropZoneRepository: CropZoneRepository = RepositoryProvider.cropZoneRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FarmUiState())
    val uiState: StateFlow<FarmUiState> = _uiState.asStateFlow()

    private val dismissedAlertIds = mutableSetOf<String>()

    private var observeFarmJob: Job? = null

    init {
        // Sync dynamic DSS rules
        viewModelScope.launch {
            try {
                dssRuleRepository.fetchFromRemote()
            } catch (_: Exception) {}
        }
        observeFarmData()
        observeActiveFarmChanges()
    }

    private fun observeActiveFarmChanges() {
        viewModelScope.launch {
            FarmPreferencesManager.getInstance().activeFarmChanges.collect {
                observeFarmData()
            }
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun observeFarmData() {
        observeFarmJob?.cancel()
        observeFarmJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val user = try { SupabaseClient.client.auth.currentUserOrNull() } catch (_: Exception) { null }
            val farmerId = user?.id ?: "guest"
            val savedActiveId = FarmPreferencesManager.getInstance().getActiveFarmId(farmerId)

            // Resolve active farm from Use Case
            val farms = getFarmsUseCase(farmerId).firstOrNull() ?: emptyList()
            val activeFarm = farms.firstOrNull { it.id == savedActiveId } ?: farms.firstOrNull()
            val resolvedFarmId = activeFarm?.id ?: savedActiveId ?: if (farmerId == "guest") "farm-1" else "farm_${farmerId.take(8)}"
            val resolvedFarmName = activeFarm?.farmName ?: "Sunrise Farm"

            _uiState.update { it.copy(farmId = resolvedFarmId, farmName = resolvedFarmName) }

            val todayStr = LocalDate.now().toString()

            // Observe dynamic database streams via Use Cases
            val flowGroup1 = getFarmPlotsUseCase(resolvedFarmId).flatMapLatest { plots ->
                val plotIds = plots.map { it.id }
                val zonesFlow = if (plotIds.isNotEmpty()) {
                    cropZoneRepository.observeZonesByPlotIds(plotIds)
                } else {
                    flowOf(emptyList())
                }
                combine(
                    flowOf(plots),
                    zonesFlow,
                    getAllCropsUseCase(),
                    getTodayTasksUseCase(resolvedFarmId, todayStr)
                ) { p, z, c, t -> FarmInputGroup(p, z, c, t) }
            }

            val flowGroup2 = combine(
                observeFarmActivitiesUseCase(),
                dssRuleRepository.observeRules(),
                knowledgeBaseRepository.observePestGuides()
            ) { activities, dynamicRules, allPests -> Triple(activities, dynamicRules, allPests) }

            combine(flowGroup1, flowGroup2) { g1, g2 ->
                buildFarmState(
                    farmId = resolvedFarmId,
                    plots = g1.plots,
                    zones = g1.zones,
                    crops = g1.crops,
                    tasks = g1.tasks,
                    activities = g2.first.filter { it.farmId == resolvedFarmId },
                    dynamicRules = g2.second,
                    allPests = g2.third,
                    todayStr = todayStr
                )
            }
            .flowOn(Dispatchers.Default)
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
            .collect { newStateModifier ->
                _uiState.update { current -> newStateModifier(current).copy(isLoading = false) }
            }
        }
    }

    private fun buildFarmState(
        farmId: String,
        plots: List<CropPlot>,
        zones: List<CropZone> = emptyList(),
        crops: List<Crop>,
        tasks: List<FarmTask>,
        activities: List<Activity>,
        dynamicRules: List<DssRule>,
        allPests: List<PestGuide>,
        todayStr: String
    ): (FarmUiState) -> FarmUiState {
        val today = LocalDate.now()

        // Real farm plots from Room / Repository
        val effectivePlots = plots

        // Evaluate DSS Engine dynamically with full farm context
        val dssResult = dssEngine.evaluate(
            plots = effectivePlots,
            crops = crops,
            rules = dynamicRules,
            activities = activities,
            today = today
        )

        // ── 1. Crop Plot & Plant Parsing ──────────────────────────────────────
        val plantedPlotsList = mutableListOf<MonitoredPlant>()
        val plannedPlotsList = mutableListOf<CropPlot>()

        for (plot in effectivePlots) {
            val matchingZones = zones.filter {
                it.plotId == plot.id &&
                !it.cropName.isNullOrBlank() &&
                !it.cropName.equals("Bed", ignoreCase = true)
            }

            val plotsToProcess: List<CropPlot> = if (matchingZones.isNotEmpty()) {
                matchingZones.mapIndexed { zIdx, zone ->
                    val zoneCropName = zone.cropName ?: "Vegetable"
                    val zoneCropId = zone.cropId ?: zoneCropName.lowercase()
                    plot.copy(
                        id = if (matchingZones.size > 1) "${plot.id}_zone_${zone.id}" else plot.id,
                        plotLabel = if (matchingZones.size > 1) "${plot.plotLabel} (C${zIdx + 1})" else plot.plotLabel,
                        cropName = zoneCropName,
                        cropId = zoneCropId,
                        plantedDate = plot.plantedDate ?: todayStr
                    )
                }
            } else {
                listOf(plot)
            }

            for (targetPlot in plotsToProcess) {
                val isPlainBed = targetPlot.cropName.isNullOrBlank() ||
                    targetPlot.cropName.equals("Bed", ignoreCase = true) ||
                    targetPlot.cropId.equals("bed", ignoreCase = true)

                val crop = crops.firstOrNull { it.name.equals(targetPlot.cropName, ignoreCase = true) }
                val isPlanted = !isPlainBed && targetPlot.plantedDate != null

                if (isPlanted) {
                    val plantedLocalDate = try {
                        LocalDate.parse(targetPlot.plantedDate!!.take(10))
                    } catch (e: Exception) {
                        today
                    }
                    val isFuture = plantedLocalDate.isAfter(today)
                    val daysPlanted = if (!isFuture) ChronoUnit.DAYS.between(plantedLocalDate, today).toInt().coerceAtLeast(0) else 0
                    val daysToHarvest = crop?.daysToHarvest?.takeIf { it > 0 } ?: 60
                    val progress = if (daysToHarvest > 0 && !isFuture) (daysPlanted.toFloat() / daysToHarvest).coerceIn(0f, 1f) else 0f

                    val stageIndex = when {
                        isFuture -> 0
                        progress < 0.15f -> 0
                        progress < 0.30f -> 1
                        progress < 0.50f -> 2
                        progress < 0.70f -> 3
                        progress < 0.90f -> 4
                        else -> 5
                    }

                    val isOverdue = !isFuture && daysPlanted > daysToHarvest
                    val isHarvestReady = !isFuture && (stageIndex == 5 || daysPlanted >= daysToHarvest)

                    val stageName = when {
                        isFuture -> "Planned (${targetPlot.plantedDate?.take(10)})"
                        isOverdue -> "Stage 6: Harvest Overdue ⚠️"
                        isHarvestReady -> "Stage 6: Harvest Ready 🌾"
                        stageIndex == 4 -> "Stage 5: Flowering & Fruit"
                        stageIndex == 3 -> "Stage 4: Vegetative Growth"
                        stageIndex == 2 -> "Stage 3: Early Growth"
                        stageIndex == 1 -> "Stage 2: Planting"
                        else -> "Stage 1: Preparation"
                    }

                    val cropCleanName = (crop?.name ?: targetPlot.cropName ?: "carrot").lowercase().replace(" ", "")
                    val soilScore = dssResult.soilScores.firstOrNull { it.plotLabel == targetPlot.plotLabel }?.score
                    val plotTasks = dssResult.tasks.filter { it.plotId == targetPlot.id }
                    val plotAlerts = dssResult.companionAlerts.filter {
                        it.plotALabel == targetPlot.plotLabel || it.plotBLabel == targetPlot.plotLabel
                    }

                    plantedPlotsList.add(
                        MonitoredPlant(
                            id = targetPlot.id,
                            farmId = targetPlot.farmId,
                            cropId = targetPlot.cropId ?: crop?.id,
                            cropName = crop?.name ?: targetPlot.cropName ?: "Vegetable",
                            localName = crop?.localName ?: targetPlot.cropName ?: "Gulay",
                            cropVariety = targetPlot.cropVariety ?: "Standard Variety",
                            plotLabel = targetPlot.plotLabel,
                            seasonality = SeasonalityFilter.ALL,
                            category = CropCategoryFilter.ALL,
                            currentStageIndex = stageIndex,
                            stageName = stageName,
                            daysPlanted = daysPlanted,
                            daysToHarvest = daysToHarvest,
                            healthStatus = when {
                                isFuture -> "📅 Scheduled for ${targetPlot.plantedDate?.take(10)}"
                                isOverdue -> "Harvest Overdue — Harvest Immediately"
                                isHarvestReady -> "Harvest Ready — Ready to harvest"
                                else -> "Active Healthy Growth"
                            },
                            companionCrop = crop?.companionPlants?.joinToString(", ") ?: "None",
                            companionStatus = if (plotAlerts.isNotEmpty()) "Alert: ${plotAlerts.first().message}" else "Good Companion Environment",
                            growingTip = crop?.description ?: "Ensure adequate irrigation and weed management.",
                            pestInfo = allPests.firstOrNull { it.affectedCrops.any { c -> c.contains(targetPlot.cropName ?: "", true) } }?.name ?: "Regular inspection recommended.",
                            assetPath = "crops/crop_${cropCleanName}_${stageIndex + 1}.png",
                            imageUrl = crop?.imageUrl,
                            rawPlantedDate = targetPlot.plantedDate,
                            isMonitoringStarted = true,
                            soilType = targetPlot.soilType,
                            suitableSoils = crop?.suitableSoils ?: listOf(targetPlot.soilType),
                            season = Season.YEAR_ROUND,
                            soilScore = soilScore,
                            dssTasks = plotTasks,
                            companionAlerts = plotAlerts
                        )
                    )
                } else {
                    plannedPlotsList.add(targetPlot)
                }
            }
        }

        // ── 2. Farm Attention Items ───────────────────────────────────────────
        val attentionList = mutableListOf<FarmAttentionItem>()

        // DSS Companion Alerts
        dssResult.companionAlerts.forEach { alert ->
            val rec = when (alert.relationship) {
                CompanionRelation.ANTAGONIST -> "Separate ${alert.cropA} and ${alert.cropB} by placing them in different beds. Good companions for ${alert.cropA}: Basil, Marigold, Garlic. Good companions for ${alert.cropB}: Lettuce, Radish, Bush Beans."
                CompanionRelation.NEUTRAL -> "Crops are compatible without strong synergy. Consider intercropping with aromatic herbs like Basil or Mint to deter common garden pests."
                CompanionRelation.BENEFICIAL -> "Great combination! ${alert.cropA} and ${alert.cropB} benefit each other. Continue current spacing and watering protocol."
            }
            attentionList.add(
                FarmAttentionItem(
                    id = "alert-${alert.plotALabel}-${alert.plotBLabel}",
                    title = "Companion Risk: ${alert.cropA} & ${alert.cropB}",
                    description = alert.message,
                    severity = AttentionSeverity.HIGH,
                    icon = "warning",
                    plotLabel = "${alert.plotALabel} & ${alert.plotBLabel}",
                    date = today.toString(),
                    companionRecommendation = rec,
                    isCompanionAlert = true,
                    cropA = alert.cropA,
                    cropB = alert.cropB
                )
            )
        }

        // Companion Advisories for planted or planned beds
        val bedsWithCrops = (plantedPlotsList.map { it.plotLabel to it.cropName } + plannedPlotsList.map { it.plotLabel to (it.cropName ?: "") })
            .filter { it.second.isNotBlank() }
            .distinctBy { it.first }

        bedsWithCrops.forEach { (plotLabel, cropName) ->
            val hasAntagonist = dssResult.companionAlerts.any { it.plotALabel == plotLabel || it.plotBLabel == plotLabel }
            if (!hasAntagonist) {
                val cropObj = crops.firstOrNull { it.name.equals(cropName, ignoreCase = true) }
                val companionList = cropObj?.companionPlants?.filter { it.isNotBlank() }
                val companions = if (!companionList.isNullOrEmpty()) companionList.joinToString(", ") else "Basil, Marigold, Green Onion"
                attentionList.add(
                    FarmAttentionItem(
                        id = "companion-info-$plotLabel",
                        title = "Companion Advisory: $cropName",
                        description = "Beneficial companion plants identified for $cropName on $plotLabel to enhance yield and deter insects.",
                        severity = AttentionSeverity.INFO,
                        icon = "info",
                        plotLabel = plotLabel,
                        date = today.toString(),
                        companionRecommendation = "Recommended beneficial companions for $cropName on $plotLabel: $companions. Intercropping these promotes pollinator activity, nutrient uptake, and natural pest suppression.",
                        isCompanionAlert = true,
                        cropA = cropName
                    )
                )
            }
        }

        // Overdue Harvests
        plantedPlotsList.filter { it.daysPlanted > it.daysToHarvest }.forEach { plant ->
            attentionList.add(
                FarmAttentionItem(
                    id = "overdue-${plant.id}",
                    title = "Harvest Overdue",
                    description = "${plant.plotLabel} (${plant.cropName}) is past its ${plant.daysToHarvest}-day harvest window.",
                    severity = AttentionSeverity.HIGH,
                    icon = "harvest",
                    plotLabel = plant.plotLabel,
                    date = today.toString()
                )
            )
        }

        // Pest risks on planted crops
        plantedPlotsList.forEach { plant ->
            val pest = allPests.firstOrNull { it.affectedCrops.any { c -> c.contains(plant.cropName, true) } }
            if (pest != null && attentionList.size < 8) {
                attentionList.add(
                    FarmAttentionItem(
                        id = "pest-${plant.id}-${pest.id}",
                        title = "Pest Alert: ${pest.name}",
                        description = "${plant.cropName} on ${plant.plotLabel} is vulnerable to ${pest.name}. Early scouting recommended.",
                        severity = AttentionSeverity.MEDIUM,
                        icon = "pest",
                        plotLabel = plant.plotLabel,
                        date = today.toString()
                    )
                )
            }
        }

        // Planned / Unplanted notice
        if (plannedPlotsList.isNotEmpty() && attentionList.size < 8) {
            attentionList.add(
                FarmAttentionItem(
                    id = "unplanted-notice",
                    title = "Beds Ready for Planting",
                    description = "${plannedPlotsList.size} bed(s) planned and waiting for crops.",
                    severity = AttentionSeverity.INFO,
                    icon = "seedling",
                    plotLabel = plannedPlotsList.firstOrNull()?.plotLabel,
                    date = today.toString()
                )
            )
        }

        // Filter out dismissed alerts and sort by priority (HIGH severity first)
        attentionList.removeAll { dismissedAlertIds.contains(it.id) }
        attentionList.sortBy {
            when (it.severity) {
                AttentionSeverity.HIGH -> 0
                AttentionSeverity.MEDIUM -> 1
                AttentionSeverity.INFO -> 2
            }
        }

        // ── 3. Today's Tasks (Room Tasks + DSS generated tasks) ───────────────
        val dssFarmTasks = dssResult.tasks.map { genTask ->
            FarmTask(
                id = "dss-${genTask.plotId}-${genTask.taskType.name}",
                farmId = farmId,
                plotId = genTask.plotId,
                plotLabel = genTask.plotLabel,
                cropName = genTask.cropName,
                taskType = genTask.taskType,
                title = genTask.title,
                subLabel = genTask.subLabel,
                dueDate = genTask.dueDate,
                isCompleted = false,
                completedAt = null
            )
        }
        val dbTodayTasks = tasks.filter { !it.isCompleted && (it.dueDate.take(10) <= todayStr) }
        val todayTasksList = (dbTodayTasks + dssFarmTasks.filter { it.dueDate.take(10) <= todayStr }).distinctBy { it.id }

        // ── 4. Chronological Farm History / Activities ────────────────────────
        val historyList = mutableListOf<FarmHistoryItem>()

        activities.forEach { act ->
            val pLabel = effectivePlots.firstOrNull { it.id == act.plotId }?.plotLabel ?: "Farm Bed"
            historyList.add(
                FarmHistoryItem(
                    id = act.id,
                    title = "Activity Completed: ${act.type.name.replace("_", " ")}",
                    subtitle = pLabel,
                    details = act.notes,
                    timestamp = act.performedAt,
                    category = ActivityCategory.ACTIVITY_COMPLETED,
                    plotLabel = pLabel
                )
            )
        }

        effectivePlots.forEach { plot ->
            if (plot.plantedDate != null) {
                historyList.add(
                    FarmHistoryItem(
                        id = "plant-${plot.id}",
                        title = "Crop Planted: ${plot.cropName ?: "Vegetable"}",
                        subtitle = "${plot.plotLabel} • Variety: ${plot.cropVariety ?: "Standard"}",
                        details = "Planted on ${plot.plantedDate?.take(10)} in ${plot.soilType.name.lowercase().replaceFirstChar { it.uppercase() }} soil",
                        timestamp = plot.plantedDate ?: plot.createdAt,
                        category = ActivityCategory.CROP_PLANTED,
                        plotLabel = plot.plotLabel
                    )
                )
            }
            if (plot.cropName != null && plot.plantedDate == null) {
                historyList.add(
                    FarmHistoryItem(
                        id = "added-${plot.id}",
                        title = "Crop Added: ${plot.cropName}",
                        subtitle = "${plot.plotLabel} (Planned)",
                        details = "Bed allocated for ${plot.cropName} layout design",
                        timestamp = plot.createdAt,
                        category = ActivityCategory.CROP_ADDED,
                        plotLabel = plot.plotLabel
                    )
                )
            }
        }

        historyList.sortByDescending { it.timestamp }

        // ── 5. Farming Timeline Events ────────────────────────────────────────
        val timelineEventsList = mutableListOf<FarmTimelineEvent>()

        for (plant in plantedPlotsList) {
            val pDate = try { LocalDate.parse(plant.rawPlantedDate?.take(10)) } catch (e: Exception) { today }
            val harvestDate = pDate.plusDays(plant.daysToHarvest.toLong())
            val isClimbing = plant.cropName.lowercase().let { c ->
                c.contains("ampalaya") || c.contains("sitaw") || c.contains("tomato") ||
                c.contains("cucumber") || c.contains("squash") || c.contains("beans")
            }

            // 1. Planting Event
            timelineEventsList.add(
                FarmTimelineEvent(
                    id = "plant-event-${plant.id}",
                    title = "Planting: ${plant.cropName}",
                    subtitle = "${plant.plotLabel} • ${plant.cropVariety}",
                    date = pDate,
                    type = CalendarEventType.PLANTING,
                    plotLabel = plant.plotLabel,
                    isCompleted = pDate.isBefore(today) || pDate.isEqual(today),
                    plotId = plant.id,
                    cropName = plant.cropName,
                    cropId = plant.cropId
                )
            )

            // 2. Growth Stage Milestones
            val stage2Date = pDate.plusDays((plant.daysToHarvest * 0.15f).toLong().coerceAtLeast(1))
            val stage3Date = pDate.plusDays((plant.daysToHarvest * 0.35f).toLong().coerceAtLeast(2))
            val stage4Date = pDate.plusDays((plant.daysToHarvest * 0.65f).toLong().coerceAtLeast(3))

            timelineEventsList.add(
                FarmTimelineEvent(
                    id = "stage2-${plant.id}",
                    title = "Seedling Stage",
                    subtitle = "${plant.cropName} (${plant.plotLabel})",
                    date = stage2Date,
                    type = CalendarEventType.GROWTH_STAGE,
                    plotLabel = plant.plotLabel,
                    isCompleted = stage2Date.isBefore(today),
                    plotId = plant.id,
                    cropName = plant.cropName,
                    cropId = plant.cropId
                )
            )
            timelineEventsList.add(
                FarmTimelineEvent(
                    id = "stage3-${plant.id}",
                    title = "Vegetative Stage",
                    subtitle = "${plant.cropName} (${plant.plotLabel})",
                    date = stage3Date,
                    type = CalendarEventType.GROWTH_STAGE,
                    plotLabel = plant.plotLabel,
                    isCompleted = stage3Date.isBefore(today),
                    plotId = plant.id,
                    cropName = plant.cropName,
                    cropId = plant.cropId
                )
            )
            timelineEventsList.add(
                FarmTimelineEvent(
                    id = "stage4-${plant.id}",
                    title = "Flowering Stage",
                    subtitle = "${plant.cropName} (${plant.plotLabel})",
                    date = stage4Date,
                    type = CalendarEventType.GROWTH_STAGE,
                    plotLabel = plant.plotLabel,
                    isCompleted = stage4Date.isBefore(today),
                    plotId = plant.id,
                    cropName = plant.cropName,
                    cropId = plant.cropId
                )
            )

            // 3. Monitoring Dates (anchored to planting date)
            val monGermDate = pDate.plusDays(4)
            timelineEventsList.add(
                FarmTimelineEvent(
                    id = "mon-germ-${plant.id}",
                    title = "Monitoring: Pagpapatubo (Germination)",
                    subtitle = "${plant.cropName} (${plant.plotLabel}) • Early Growth Check",
                    date = monGermDate,
                    type = CalendarEventType.MONITORING,
                    plotLabel = plant.plotLabel,
                    isCompleted = monGermDate.isBefore(today),
                    plotId = plant.id,
                    cropName = plant.cropName,
                    cropId = plant.cropId
                )
            )
            val monScoutDate = pDate.plusDays((plant.daysToHarvest * 0.40f).toLong().coerceAtLeast(10))
            timelineEventsList.add(
                FarmTimelineEvent(
                    id = "mon-scout-${plant.id}",
                    title = "Monitoring: Kalusugan at Peste",
                    subtitle = "${plant.cropName} (${plant.plotLabel}) • Foliage Inspection",
                    date = monScoutDate,
                    type = CalendarEventType.MONITORING,
                    plotLabel = plant.plotLabel,
                    isCompleted = monScoutDate.isBefore(today),
                    plotId = plant.id,
                    cropName = plant.cropName,
                    cropId = plant.cropId
                )
            )
            val monPreDate = harvestDate.minusDays(3).coerceAtLeast(pDate.plusDays(1))
            timelineEventsList.add(
                FarmTimelineEvent(
                    id = "mon-pre-${plant.id}",
                    title = "Monitoring: Pre-Harvest Check",
                    subtitle = "${plant.cropName} (${plant.plotLabel}) • Maturity Readiness",
                    date = monPreDate,
                    type = CalendarEventType.MONITORING,
                    plotLabel = plant.plotLabel,
                    isCompleted = monPreDate.isBefore(today),
                    plotId = plant.id,
                    cropName = plant.cropName,
                    cropId = plant.cropId
                )
            )

            // 4. Crop Care Dates (anchored to planting date)
            val waterDate = pDate.plusDays(1)
            timelineEventsList.add(
                FarmTimelineEvent(
                    id = "care-water-${plant.id}",
                    title = "Crop Care: Dilig at Lupa",
                    subtitle = "${plant.cropName} (${plant.plotLabel}) • Moisture Settling",
                    date = waterDate,
                    type = CalendarEventType.CROP_CARE,
                    plotLabel = plant.plotLabel,
                    isCompleted = waterDate.isBefore(today),
                    plotId = plant.id,
                    cropName = plant.cropName,
                    cropId = plant.cropId
                )
            )
            val fertDate = pDate.plusDays((plant.daysToHarvest * 0.25f).toLong().coerceAtLeast(5))
            timelineEventsList.add(
                FarmTimelineEvent(
                    id = "care-fert-${plant.id}",
                    title = "Crop Care: Pag-abono (Fertilization)",
                    subtitle = "${plant.cropName} (${plant.plotLabel}) • Side-dressing",
                    date = fertDate,
                    type = CalendarEventType.CROP_CARE,
                    plotLabel = plant.plotLabel,
                    isCompleted = fertDate.isBefore(today),
                    plotId = plant.id,
                    cropName = plant.cropName,
                    cropId = plant.cropId
                )
            )
            val weedDate = pDate.plusDays((plant.daysToHarvest * 0.45f).toLong().coerceAtLeast(12))
            timelineEventsList.add(
                FarmTimelineEvent(
                    id = "care-weed-${plant.id}",
                    title = "Crop Care: Pag-aalis ng Damo (Weeding)",
                    subtitle = "${plant.cropName} (${plant.plotLabel}) • Clean Bed",
                    date = weedDate,
                    type = CalendarEventType.CROP_CARE,
                    plotLabel = plant.plotLabel,
                    isCompleted = weedDate.isBefore(today),
                    plotId = plant.id,
                    cropName = plant.cropName,
                    cropId = plant.cropId
                )
            )

            // 5. Other Tasks (anchored to planting date)
            if (isClimbing) {
                val trellisDate = pDate.plusDays(10)
                timelineEventsList.add(
                    FarmTimelineEvent(
                        id = "task-trellis-${plant.id}",
                        title = "Gawain: Paglalagay ng Balag",
                        subtitle = "${plant.cropName} (${plant.plotLabel}) • Trellis Setup",
                        date = trellisDate,
                        type = CalendarEventType.DSS_TASK,
                        plotLabel = plant.plotLabel,
                        isCompleted = trellisDate.isBefore(today),
                        plotId = plant.id,
                        cropName = plant.cropName,
                        cropId = plant.cropId
                    )
                )
            }
            val pruneDate = pDate.plusDays((plant.daysToHarvest * 0.50f).toLong().coerceAtLeast(15))
            timelineEventsList.add(
                FarmTimelineEvent(
                    id = "task-prune-${plant.id}",
                    title = "Gawain: Pagsasaayos ng Sanga (Pruning)",
                    subtitle = "${plant.cropName} (${plant.plotLabel}) • Foliage Training",
                    date = pruneDate,
                    type = CalendarEventType.DSS_TASK,
                    plotLabel = plant.plotLabel,
                    isCompleted = pruneDate.isBefore(today),
                    plotId = plant.id,
                    cropName = plant.cropName,
                    cropId = plant.cropId
                )
            )

            // 6. Expected Harvest Window
            timelineEventsList.add(
                FarmTimelineEvent(
                    id = "harvest-event-${plant.id}",
                    title = "Expected Harvest Window",
                    subtitle = "${plant.cropName} (${plant.plotLabel}) • ${plant.daysToHarvest} days",
                    date = harvestDate,
                    type = CalendarEventType.HARVEST,
                    plotLabel = plant.plotLabel,
                    isCompleted = harvestDate.isBefore(today),
                    plotId = plant.id,
                    cropName = plant.cropName,
                    cropId = plant.cropId
                )
            )
        }

        for (planned in plannedPlotsList) {
            val plannedDate = today.plusDays(3)
            timelineEventsList.add(
                FarmTimelineEvent(
                    id = "planned-event-${planned.id}",
                    title = "Target Planting: ${planned.cropName ?: "Bed"}",
                    subtitle = "${planned.plotLabel} • Ready for seeding",
                    date = plannedDate,
                    type = CalendarEventType.PLANTING,
                    plotLabel = planned.plotLabel,
                    isCompleted = false,
                    plotId = planned.id,
                    cropName = planned.cropName,
                    cropId = planned.cropId
                )
            )
        }

        for (task in tasks) {
            val tDate = try { LocalDate.parse(task.dueDate.take(10)) } catch (e: Exception) { today }
            val cType = when (task.taskType) {
                com.maptanim.app.domain.model.TaskType.OBSERVATION -> CalendarEventType.MONITORING
                com.maptanim.app.domain.model.TaskType.WATER,
                com.maptanim.app.domain.model.TaskType.FERTILIZE,
                com.maptanim.app.domain.model.TaskType.WEED,
                com.maptanim.app.domain.model.TaskType.NUTRITION,
                com.maptanim.app.domain.model.TaskType.SOIL_AMENDMENT -> CalendarEventType.CROP_CARE
                com.maptanim.app.domain.model.TaskType.HARVEST -> CalendarEventType.HARVEST
                else -> CalendarEventType.DSS_TASK
            }
            val finalType = if (task.isCompleted) CalendarEventType.COMPLETED else cType
            timelineEventsList.add(
                FarmTimelineEvent(
                    id = "task-event-${task.id}",
                    title = task.title,
                    subtitle = "${task.plotLabel} • ${task.taskType.name.replace("_", " ")}",
                    date = tDate,
                    type = finalType,
                    plotLabel = task.plotLabel,
                    isCompleted = task.isCompleted,
                    plotId = task.plotId,
                    cropName = task.cropName,
                    taskId = task.id
                )
            )
        }

        timelineEventsList.sortBy { it.date }

        // ── 6. Monitor Cards ──────────────────────────────────────────────────
        val monitorCards = plantedPlotsList.map { plant ->
            val plot = plots.firstOrNull { it.id == plant.id }
            val crop = crops.firstOrNull { it.name.equals(plot?.cropName, ignoreCase = true) }

            val latestObs = activities
                .filter { it.plotId == plant.id }
                .maxByOrNull { it.performedAt }

            val condition = when {
                plant.companionAlerts.isNotEmpty() -> "Under Mild Stress (Companion Conflict)"
                plant.daysPlanted > plant.daysToHarvest -> "Mature / Ready to Harvest"
                else -> "Healthy & Vigorously Growing"
            }

            val obs = latestObs?.notes ?: "Vegetative height optimal for ${plant.daysPlanted} days since planting. Foliage inspection normal."

            CropMonitorCardData(
                plot = plot ?: CropPlot(
                    id = plant.id,
                    farmId = farmId,
                    plotLabel = plant.plotLabel,
                    cropName = plant.cropName,
                    cropId = plant.cropId,
                    cropVariety = plant.cropVariety,
                    soilType = plant.soilType,
                    posX = 0f,
                    posY = 0f,
                    widthM = 4f,
                    heightM = 2f
                ),
                crop = crop,
                monitoredPlant = plant,
                currentStageName = plant.stageName,
                daysPlanted = plant.daysPlanted,
                growthObservation = obs,
                plantCondition = condition,
                pestObservation = plant.pestInfo,
                lastRecordedAt = latestObs?.performedAt?.take(10) ?: plant.rawPlantedDate?.take(10)
            )
        }

        return { current ->
            current.copy(
                plots = effectivePlots,
                crops = crops,
                cropZones = zones,
                attentionItems = attentionList,
                todayTasks = todayTasksList,
                recentActivities = historyList.take(5),
                plantedPlants = plantedPlotsList,
                plannedPlots = plannedPlotsList,
                timelineEvents = timelineEventsList,
                monitorItems = monitorCards,
                activityHistory = historyList
            )
        }
    }

    // ── Actions (Flowing through ViewModel → Use Cases → Database) ───────────

    fun selectTab(tab: FarmTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun selectCropsFilter(filter: CropsFilter) {
        _uiState.update { it.copy(cropsFilter = filter) }
    }

    fun openCropDetails(plant: MonitoredPlant) {
        _uiState.update { it.copy(selectedPlantForDetails = plant, selectedPlotForDetails = null) }
    }

    fun openPlotDetails(plot: CropPlot) {
        _uiState.update { it.copy(selectedPlotForDetails = plot, selectedPlantForDetails = null) }
    }

    fun closeCropDetails() {
        _uiState.update { it.copy(selectedPlantForDetails = null, selectedPlotForDetails = null) }
    }

    fun selectCalendarDate(date: LocalDate) {
        _uiState.update {
            it.copy(
                selectedCalendarDate = date,
                isCalendarDateFilterActive = true,
                calendarMonth = java.time.YearMonth.from(date)
            )
        }
    }

    fun clearCalendarDateFilter() {
        _uiState.update { it.copy(isCalendarDateFilterActive = false) }
    }

    fun previousCalendarMonth() {
        _uiState.update { it.copy(calendarMonth = it.calendarMonth.minusMonths(1)) }
    }

    fun nextCalendarMonth() {
        _uiState.update { it.copy(calendarMonth = it.calendarMonth.plusMonths(1)) }
    }

    fun filterCalendar(type: CalendarEventType?) {
        _uiState.update { it.copy(calendarFilter = type) }
    }

    fun filterActivities(category: ActivityCategory) {
        _uiState.update { it.copy(selectedActivityFilter = category) }
    }

    fun completeTask(taskId: String) {
        viewModelScope.launch {
            val now = ZonedDateTime.now().toString()
            completeTaskUseCase(taskId, now)

            val task = _uiState.value.todayTasks.firstOrNull { it.id == taskId }
            if (task != null) {
                logFarmActivityUseCase(
                    Activity(
                        id = UUID.randomUUID().toString(),
                        plotId = task.plotId,
                        farmId = task.farmId,
                        type = task.taskType,
                        notes = "Completed today's task: ${task.title}",
                        performedAt = now
                    )
                )
            }

            _uiState.update { it.copy(toastMessage = "Task marked completed! ✅") }
        }
    }

    fun openObservationDialog(plot: CropPlot?) {
        _uiState.update { it.copy(isObservationModalOpen = true, observationTargetPlot = plot) }
    }

    fun closeObservationDialog() {
        _uiState.update { it.copy(isObservationModalOpen = false, observationTargetPlot = null) }
    }

    fun recordObservation(
        plotId: String,
        stage: String,
        condition: String,
        pestNotes: String,
        notes: String
    ) {
        viewModelScope.launch {
            val plot = _uiState.value.plots.firstOrNull { it.id == plotId } ?: return@launch
            val now = ZonedDateTime.now().toString()

            val fullNotes = buildString {
                append("Stage: $stage • Condition: $condition")
                if (pestNotes.isNotBlank()) append(" • Pests: $pestNotes")
                if (notes.isNotBlank()) append(" • $notes")
            }

            logFarmActivityUseCase(
                Activity(
                    id = UUID.randomUUID().toString(),
                    plotId = plotId,
                    farmId = plot.farmId,
                    type = if (pestNotes.isNotBlank()) TaskType.PEST_ALERT else TaskType.OBSERVATION,
                    notes = "Observation logged for ${plot.plotLabel}: $fullNotes",
                    performedAt = now
                )
            )

            _uiState.update {
                it.copy(
                    isObservationModalOpen = false,
                    observationTargetPlot = null,
                    toastMessage = "Observation recorded & synced to DSS! 📝"
                )
            }
        }
    }

    fun quickLogMaintenance(plotId: String, actionType: String) {
        viewModelScope.launch {
            val plot = _uiState.value.plots.firstOrNull { it.id == plotId } ?: return@launch
            val now = ZonedDateTime.now().toString()
            val (taskType: TaskType, verbEmoji: String) = when (actionType.lowercase()) {
                "water" -> Pair(TaskType.WATER, "💧 Watered")
                "weed" -> Pair(TaskType.WEED, "🌿 Weeded")
                "fertilize" -> Pair(TaskType.FERTILIZE, "🧪 Fertilized")
                else -> Pair(TaskType.OBSERVATION, "🛠 Maintained")
            }

            logFarmActivityUseCase(
                Activity(
                    id = UUID.randomUUID().toString(),
                    plotId = plotId,
                    farmId = plot.farmId,
                    type = taskType,
                    notes = "$verbEmoji ${plot.plotLabel}" + (if (!plot.cropName.isNullOrBlank()) " (${plot.cropName})" else ""),
                    performedAt = now
                )
            )

            // Auto-complete any matching pending task for this plot
            val matchingTask = _uiState.value.todayTasks.firstOrNull {
                it.plotId == plotId && it.taskType == taskType && !it.isCompleted
            }
            if (matchingTask != null) {
                completeTask(matchingTask.id)
            } else {
                _uiState.update { it.copy(toastMessage = "$verbEmoji ${plot.plotLabel}! ✨") }
            }
        }
    }

    fun submitCropLog(cropLog: CropLog) {
        viewModelScope.launch {
            val plot = _uiState.value.plots.firstOrNull { it.id == cropLog.cropPlantingId }
                ?: _uiState.value.observationTargetPlot
            val bedLabel = plot?.plotLabel ?: cropLog.bedId.ifBlank { "Bed" }
            val cropName = plot?.cropName ?: cropLog.cropName.ifBlank { "Crop" }
            val farmId = plot?.farmId ?: _uiState.value.farmId

            try {
                // 1. Save log to CropLogRepository
                RepositoryProvider.cropLogRepository.insertLog(cropLog)

                // 2. Evaluate with DSS
                val evalResult = DssLogEvaluator().evaluate(
                    log = cropLog,
                    previousLogs = emptyList<CropLog>(),
                    currentStage = cropLog.currentStage,
                    pendingTasks = emptyList<FarmTask>(),
                    plantingDate = plot?.plantedDate ?: LocalDate.now().toString(),
                    plantingMethod = "Transplanting",
                    daysToHarvest = 60
                )

                // 3. Persist generated tasks to TaskRepository
                if (evalResult.newTasks.isNotEmpty()) {
                    val farmTasks = evalResult.newTasks.map { genTask ->
                        FarmTask(
                            id = genTask.id,
                            farmId = farmId,
                            plotId = cropLog.cropPlantingId,
                            plotLabel = bedLabel,
                            cropName = cropName,
                            taskType = genTask.taskType,
                            title = genTask.title,
                            subLabel = genTask.description,
                            dueDate = genTask.dueDate,
                            isCompleted = false,
                            completedAt = null
                        )
                    }
                    RepositoryProvider.taskRepository.upsertTasks(farmTasks)
                }

                // 4. Log Activity History with Bed Number, Date, Crop
                val dateOnly = try { cropLog.date.take(10) } catch (_: Exception) { LocalDate.now().toString() }
                val details = if (cropLog.selectedCheckboxes.isNotEmpty()) " (${cropLog.selectedCheckboxes.joinToString(", ")})" else ""
                val notesText = "Observation logged for $bedLabel • $cropName: Stage ${cropLog.currentStage.label}, Choice ${cropLog.selectedChoice}$details on $dateOnly" +
                        (if (!cropLog.notes.isNullOrBlank()) " • ${cropLog.notes}" else "")

                logFarmActivityUseCase(
                    Activity(
                        id = UUID.randomUUID().toString(),
                        plotId = cropLog.cropPlantingId,
                        farmId = farmId,
                        type = TaskType.OBSERVATION,
                        notes = notesText,
                        performedAt = ZonedDateTime.now().toString()
                    )
                )
            } catch (e: Exception) {
                android.util.Log.e("FarmViewModel", "Error submitting crop log: ${e.message}", e)
            }

            _uiState.update {
                it.copy(
                    isObservationModalOpen = false,
                    observationTargetPlot = null,
                    toastMessage = "Observation evaluated & recorded for $cropName ($bedLabel)! 📝"
                )
            }
        }
    }

    fun startPlantingNow(plotId: String, date: String = LocalDate.now().toString(), variety: String? = null) {
        viewModelScope.launch {
            val plot = _uiState.value.plots.firstOrNull { it.id == plotId } ?: return@launch
            val updated = plot.copy(
                plantedDate = "${date.take(10)}T00:00:00Z",
                cropVariety = variety ?: plot.cropVariety ?: "Standard Variety"
            )
            startPlantingUseCase(updated)

            logFarmActivityUseCase(
                Activity(
                    id = UUID.randomUUID().toString(),
                    plotId = plotId,
                    farmId = plot.farmId,
                    type = TaskType.WEED,
                    notes = "Started planting ${plot.cropName ?: "Vegetable"} on ${plot.plotLabel}",
                    performedAt = ZonedDateTime.now().toString()
                )
            )

            _uiState.update {
                it.copy(
                    selectedPlotForDetails = null,
                    selectedPlantForDetails = null,
                    toastMessage = "Planting commenced on ${plot.plotLabel}! 🌱"
                )
            }
        }
    }

    fun recordHarvest(plotId: String, yieldKg: Float = 0f, notes: String? = null, isFinalHarvest: Boolean = true) {
        viewModelScope.launch {
            val plot = _uiState.value.plots.firstOrNull { it.id == plotId } ?: return@launch
            val now = ZonedDateTime.now().toString()

            plotRepository.recordHarvest(plotId, yieldKg, notes, isFinalHarvest = isFinalHarvest)

            val cropName = plot.cropName ?: "Vegetable"
            val varietySuffix = if (!plot.cropVariety.isNullOrBlank()) " (${plot.cropVariety})" else ""
            val dateOnly = try { now.take(10) } catch (_: Exception) { java.time.LocalDate.now().toString() }
            val yieldText = if (yieldKg > 0) "$yieldKg kg" else "Harvest completed"
            val pickTypeStr = if (isFinalHarvest) "Final harvest" else "Picking"
            val activityNotes = "$pickTypeStr of $yieldText of $cropName$varietySuffix from ${plot.plotLabel} on $dateOnly"

            logFarmActivityUseCase(
                Activity(
                    id = UUID.randomUUID().toString(),
                    plotId = plotId,
                    farmId = plot.farmId,
                    type = TaskType.HARVEST,
                    notes = activityNotes,
                    performedAt = now
                )
            )

            _uiState.update {
                it.copy(
                    selectedPlantForDetails = null,
                    toastMessage = if (isFinalHarvest) "Final harvest recorded! 🌾 Bed ready for rotation." else "Harvest pick recorded! 🌱 +$yieldKg kg"
                )
            }
        }
    }

    fun addNewBed(
        customLabel: String? = null,
        widthM: Float = 4.0f,
        heightM: Float = 1.2f,
        soilType: SoilType = SoilType.LOAM
    ) {
        viewModelScope.launch {
            val farmId = _uiState.value.farmId
            val existingPlots = _uiState.value.plots
            val nextNumber = existingPlots.size + 1
            val label = customLabel?.takeIf { it.isNotBlank() } ?: "Bed #$nextNumber"
            val maxY = existingPlots.maxOfOrNull { it.posY + it.heightM } ?: 0f
            val newPlot = CropPlot(
                id = UUID.randomUUID().toString(),
                farmId = farmId,
                cropId = null,
                cropName = null,
                cropVariety = null,
                plotLabel = label,
                soilType = soilType,
                posX = 2f,
                posY = (maxY + 1.2f).coerceAtMost(28f),
                widthM = widthM,
                heightM = heightM,
                createdAt = ZonedDateTime.now().toString(),
                updatedAt = ZonedDateTime.now().toString()
            )
            plotRepository.upsertPlot(newPlot)
            _uiState.update { it.copy(toastMessage = "Added $label (%.1fm × %.1fm)! 🟫".format(widthM, heightM)) }
        }
    }

    fun deleteBed(plotId: String) {
        viewModelScope.launch {
            plotRepository.deletePlot(plotId)
            _uiState.update { it.copy(toastMessage = "Bed removed. 🗑") }
        }
    }

    fun assignCropToBed(plotId: String, cropName: String, variety: String? = null, method: String = "Direct Seed", isPlanted: Boolean = true) {
        viewModelScope.launch {
            val plot = _uiState.value.plots.firstOrNull { it.id == plotId } ?: return@launch
            val now = LocalDate.now().toString()
            val cropEntity = _uiState.value.crops.firstOrNull { it.name.equals(cropName, ignoreCase = true) }
            val updated = plot.copy(
                cropId = cropEntity?.id ?: cropName.lowercase(),
                cropName = cropName,
                cropVariety = variety ?: "Standard",
                plantedDate = if (isPlanted) "${now}T00:00:00Z" else null,
                currentStage = if (isPlanted) ManagementStage.PLANTING else ManagementStage.PREPARATION,
                updatedAt = ZonedDateTime.now().toString()
            )
            plotRepository.upsertPlot(updated)
            logFarmActivityUseCase(
                Activity(
                    id = UUID.randomUUID().toString(),
                    plotId = plotId,
                    farmId = plot.farmId,
                    type = TaskType.OBSERVATION,
                    notes = "Planted $cropName in ${plot.plotLabel} ($method) 🌱",
                    performedAt = ZonedDateTime.now().toString()
                )
            )
            _uiState.update { it.copy(toastMessage = "Assigned $cropName to ${plot.plotLabel}! 🌱") }
        }
    }

    fun updateBedDimensions(plotId: String, widthM: Float, heightM: Float, soilType: SoilType) {
        viewModelScope.launch {
            val plot = _uiState.value.plots.firstOrNull { it.id == plotId } ?: return@launch
            val updated = plot.copy(
                widthM = widthM,
                heightM = heightM,
                soilType = soilType,
                updatedAt = ZonedDateTime.now().toString()
            )
            plotRepository.upsertPlot(updated)
            _uiState.update { it.copy(toastMessage = "Updated ${plot.plotLabel} size: ${widthM}m × ${heightM}m ✨") }
        }
    }

    fun advancePlotStage(plotId: String, newStage: ManagementStage) {
        viewModelScope.launch {
            val plot = _uiState.value.plots.firstOrNull { it.id == plotId } ?: return@launch
            val updated = plot.copy(
                currentStage = newStage,
                updatedAt = ZonedDateTime.now().toString()
            )
            plotRepository.upsertPlot(updated)
            logFarmActivityUseCase(
                Activity(
                    id = UUID.randomUUID().toString(),
                    plotId = plotId,
                    farmId = plot.farmId,
                    type = TaskType.OBSERVATION,
                    notes = "Advanced ${plot.plotLabel} to ${newStage.label} 🌿",
                    performedAt = ZonedDateTime.now().toString()
                )
            )
            _uiState.update { it.copy(toastMessage = "Stage updated to ${newStage.label}! 🌿") }
        }
    }

    fun dismissAlertAndRecordHistory(alert: FarmAttentionItem, actionNote: String = "Marked as read") {
        viewModelScope.launch {
            dismissedAlertIds.add(alert.id)
            val todayStr = LocalDate.now().toString()
            val nowIso = ZonedDateTime.now().toString()
            val plotLabel = alert.plotLabel ?: "Farm Bed"

            // 1. Log activity to domain repository
            val targetPlot = _uiState.value.plots.firstOrNull { it.plotLabel.equals(plotLabel, ignoreCase = true) }
            val plotId = targetPlot?.id ?: _uiState.value.plots.firstOrNull()?.id ?: UUID.randomUUID().toString()
            val farmId = targetPlot?.farmId ?: _uiState.value.farmId

            logFarmActivityUseCase(
                Activity(
                    id = UUID.randomUUID().toString(),
                    plotId = plotId,
                    farmId = farmId,
                    type = TaskType.OBSERVATION,
                    notes = "$actionNote: ${alert.title} on $plotLabel ($todayStr)",
                    performedAt = nowIso
                )
            )

            // 2. Add to UI state activityHistory and remove from attentionItems
            val historyItem = FarmHistoryItem(
                id = UUID.randomUUID().toString(),
                title = "$actionNote: ${alert.title}",
                subtitle = "$plotLabel • $todayStr",
                details = "${alert.description} (Action taken on $todayStr for $plotLabel)",
                timestamp = nowIso,
                category = ActivityCategory.ACTIVITY_COMPLETED,
                plotLabel = plotLabel
            )

            _uiState.update { state ->
                state.copy(
                    attentionItems = state.attentionItems.filterNot { it.id == alert.id },
                    activityHistory = listOf(historyItem) + state.activityHistory,
                    recentActivities = listOf(historyItem) + state.recentActivities.take(9),
                    toastMessage = "Alert handled and recorded in Activity History for $plotLabel"
                )
            }
        }
    }

    fun dismissAllAlertsAndRecordHistory(alerts: List<FarmAttentionItem>) {
        if (alerts.isEmpty()) return
        viewModelScope.launch {
            val todayStr = LocalDate.now().toString()
            val nowIso = ZonedDateTime.now().toString()

            val newHistoryItems = mutableListOf<FarmHistoryItem>()
            val idsToRemove = alerts.map { it.id }.toSet()
            dismissedAlertIds.addAll(idsToRemove)

            alerts.forEach { alert ->
                val plotLabel = alert.plotLabel ?: "Farm Bed"
                val targetPlot = _uiState.value.plots.firstOrNull { it.plotLabel.equals(plotLabel, ignoreCase = true) }
                val plotId = targetPlot?.id ?: _uiState.value.plots.firstOrNull()?.id ?: UUID.randomUUID().toString()
                val farmId = targetPlot?.farmId ?: _uiState.value.farmId

                logFarmActivityUseCase(
                    Activity(
                        id = UUID.randomUUID().toString(),
                        plotId = plotId,
                        farmId = farmId,
                        type = TaskType.OBSERVATION,
                        notes = "Marked as read: ${alert.title} on $plotLabel ($todayStr)",
                        performedAt = nowIso
                    )
                )

                newHistoryItems.add(
                    FarmHistoryItem(
                        id = UUID.randomUUID().toString(),
                        title = "Marked as read: ${alert.title}",
                        subtitle = "$plotLabel • $todayStr",
                        details = "${alert.description} (Marked as read on $todayStr for $plotLabel)",
                        timestamp = nowIso,
                        category = ActivityCategory.ACTIVITY_COMPLETED,
                        plotLabel = plotLabel
                    )
                )
            }

            _uiState.update { state ->
                state.copy(
                    attentionItems = state.attentionItems.filterNot { idsToRemove.contains(it.id) },
                    activityHistory = newHistoryItems + state.activityHistory,
                    recentActivities = newHistoryItems.take(5) + state.recentActivities.take(5),
                    toastMessage = "${alerts.size} alerts marked as read and saved to Activity History"
                )
            }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun refreshData() {
        observeFarmData()
        _uiState.update { it.copy(toastMessage = "Farm data refreshed 🔄") }
    }
}
