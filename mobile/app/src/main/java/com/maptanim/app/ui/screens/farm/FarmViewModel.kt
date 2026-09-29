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
    CROPS("Crops"),
    CALENDAR("Calendar"),
    MONITOR("Monitor"),
    ACTIVITY("Activity")
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
    val plotLabel: String? = null
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
    private val recordHarvestUseCase: RecordHarvestUseCase = RecordHarvestUseCase(harvestRepository)
) : ViewModel() {

    private val _uiState = MutableStateFlow(FarmUiState())
    val uiState: StateFlow<FarmUiState> = _uiState.asStateFlow()

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
            val flowGroup1 = combine(
                getFarmPlotsUseCase(resolvedFarmId),
                getAllCropsUseCase(),
                getTodayTasksUseCase(resolvedFarmId, todayStr)
            ) { plots, crops, tasks -> Triple(plots, crops, tasks) }

            val flowGroup2 = combine(
                observeFarmActivitiesUseCase(),
                dssRuleRepository.observeRules(),
                knowledgeBaseRepository.observePestGuides()
            ) { activities, dynamicRules, allPests -> Triple(activities, dynamicRules, allPests) }

            combine(flowGroup1, flowGroup2) { g1, g2 ->
                buildFarmState(
                    farmId = resolvedFarmId,
                    plots = g1.first,
                    crops = g1.second,
                    tasks = g1.third,
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
            val isPlainBed = plot.cropName.isNullOrBlank() ||
                plot.cropName.equals("Bed", ignoreCase = true) ||
                plot.cropId.equals("bed", ignoreCase = true)

            val crop = crops.firstOrNull { it.name.equals(plot.cropName, ignoreCase = true) }
            val isPlanted = !isPlainBed && plot.plantedDate != null

            if (isPlanted) {
                val plantedLocalDate = try {
                    LocalDate.parse(plot.plantedDate!!.take(10))
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
                    progress < 0.35f -> 1
                    progress < 0.65f -> 2
                    progress < 0.90f -> 3
                    else -> 4
                }

                val isOverdue = !isFuture && daysPlanted > daysToHarvest
                val isHarvestReady = !isFuture && (stageIndex == 4 || daysPlanted >= daysToHarvest)

                val stageName = when {
                    isFuture -> "Planned (${plot.plantedDate?.take(10)})"
                    isOverdue -> "Stage 5: Harvest Overdue ⚠️"
                    isHarvestReady -> "Stage 5: Harvest Ready 🌾"
                    stageIndex == 3 -> "Stage 4: Flowering"
                    stageIndex == 2 -> "Stage 3: Vegetative"
                    stageIndex == 1 -> "Stage 2: Seedling"
                    else -> "Stage 1: Sprout"
                }

                val cropCleanName = (crop?.name ?: plot.cropName ?: "carrot").lowercase().replace(" ", "")
                val soilScore = dssResult.soilScores.firstOrNull { it.plotLabel == plot.plotLabel }?.score
                val plotTasks = dssResult.tasks.filter { it.plotId == plot.id }
                val plotAlerts = dssResult.companionAlerts.filter {
                    it.plotALabel == plot.plotLabel || it.plotBLabel == plot.plotLabel
                }

                plantedPlotsList.add(
                    MonitoredPlant(
                        id = plot.id,
                        farmId = plot.farmId,
                        cropId = plot.cropId ?: crop?.id,
                        cropName = crop?.name ?: plot.cropName ?: "Vegetable",
                        localName = crop?.localName ?: plot.cropName ?: "Gulay",
                        cropVariety = plot.cropVariety ?: "Standard Variety",
                        plotLabel = plot.plotLabel,
                        seasonality = SeasonalityFilter.ALL,
                        category = CropCategoryFilter.ALL,
                        currentStageIndex = stageIndex,
                        stageName = stageName,
                        daysPlanted = daysPlanted,
                        daysToHarvest = daysToHarvest,
                        healthStatus = when {
                            isFuture -> "📅 Scheduled for ${plot.plantedDate?.take(10)}"
                            isOverdue -> "Harvest Overdue — Harvest Immediately"
                            isHarvestReady -> "Harvest Ready — Ready to harvest"
                            else -> "Active Healthy Growth"
                        },
                        companionCrop = crop?.companionPlants?.joinToString(", ") ?: "None",
                        companionStatus = if (plotAlerts.isNotEmpty()) "Alert: ${plotAlerts.first().message}" else "Good Companion Environment",
                        growingTip = crop?.description ?: "Ensure adequate irrigation and weed management.",
                        pestInfo = allPests.firstOrNull { it.affectedCrops.any { c -> c.contains(plot.cropName ?: "", true) } }?.name ?: "Regular inspection recommended.",
                        assetPath = "crops/crop_${cropCleanName}_${stageIndex + 1}.png",
                        imageUrl = crop?.imageUrl,
                        rawPlantedDate = plot.plantedDate,
                        isMonitoringStarted = true,
                        soilType = plot.soilType,
                        suitableSoils = crop?.suitableSoils ?: listOf(plot.soilType),
                        season = Season.YEAR_ROUND,
                        soilScore = soilScore,
                        dssTasks = plotTasks,
                        companionAlerts = plotAlerts
                    )
                )
            } else {
                plannedPlotsList.add(plot)
            }
        }

        // ── 2. Farm Attention Items ───────────────────────────────────────────
        val attentionList = mutableListOf<FarmAttentionItem>()

        // DSS Companion Alerts
        dssResult.companionAlerts.forEach { alert ->
            attentionList.add(
                FarmAttentionItem(
                    id = "alert-${alert.plotALabel}-${alert.plotBLabel}",
                    title = "Companion Risk Detected",
                    description = alert.message,
                    severity = AttentionSeverity.HIGH,
                    icon = "⚠️",
                    plotLabel = "${alert.plotALabel} & ${alert.plotBLabel}"
                )
            )
        }

        // Overdue Harvests
        plantedPlotsList.filter { it.daysPlanted > it.daysToHarvest }.forEach { plant ->
            attentionList.add(
                FarmAttentionItem(
                    id = "overdue-${plant.id}",
                    title = "Harvest Overdue",
                    description = "${plant.plotLabel} (${plant.cropName}) is past its ${plant.daysToHarvest}-day harvest window.",
                    severity = AttentionSeverity.HIGH,
                    icon = "🌾",
                    plotLabel = plant.plotLabel
                )
            )
        }

        // Pest risks on planted crops
        plantedPlotsList.forEach { plant ->
            val pest = allPests.firstOrNull { it.affectedCrops.any { c -> c.contains(plant.cropName, true) } }
            if (pest != null && attentionList.size < 4) {
                attentionList.add(
                    FarmAttentionItem(
                        id = "pest-${plant.id}-${pest.id}",
                        title = "Pest Alert: ${pest.name}",
                        description = "${plant.cropName} on ${plant.plotLabel} is vulnerable to ${pest.name}. Early scouting recommended.",
                        severity = AttentionSeverity.MEDIUM,
                        icon = "🐛",
                        plotLabel = plant.plotLabel
                    )
                )
            }
        }

        // Planned / Unplanted notice
        if (plannedPlotsList.isNotEmpty() && attentionList.size < 4) {
            attentionList.add(
                FarmAttentionItem(
                    id = "unplanted-notice",
                    title = "Beds Ready for Planting",
                    description = "${plannedPlotsList.size} bed(s) planned and waiting for crops.",
                    severity = AttentionSeverity.INFO,
                    icon = "🌱",
                    plotLabel = plannedPlotsList.firstOrNull()?.plotLabel
                )
            )
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
                    subtitle = "${plant.cropName} (${plant.plotLabel}) • Early Sprout Check",
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

    fun recordHarvest(plotId: String, yieldKg: Float = 0f, notes: String? = null) {
        viewModelScope.launch {
            val plot = _uiState.value.plots.firstOrNull { it.id == plotId } ?: return@launch
            val now = ZonedDateTime.now().toString()

            plotRepository.recordHarvest(plotId, yieldKg, notes)
            recordHarvestUseCase(
                HarvestRecord(
                    id = UUID.randomUUID().toString(),
                    plotId = plotId,
                    farmId = plot.farmId,
                    farmName = _uiState.value.farmName,
                    plotLabel = plot.plotLabel,
                    cropName = plot.cropName ?: "Vegetable",
                    cropVariety = plot.cropVariety,
                    plantedDate = plot.plantedDate,
                    harvestedAt = now,
                    yieldKg = yieldKg,
                    notes = notes
                )
            )

            logFarmActivityUseCase(
                Activity(
                    id = UUID.randomUUID().toString(),
                    plotId = plotId,
                    farmId = plot.farmId,
                    type = TaskType.HARVEST,
                    notes = "Harvest recorded: ${if (yieldKg > 0) "$yieldKg kg" else "Harvest completed"} for ${plot.plotLabel}",
                    performedAt = now
                )
            )

            _uiState.update {
                it.copy(
                    selectedPlantForDetails = null,
                    toastMessage = "Harvest recorded successfully! 🌾"
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
