package com.maptanim.app.domain.usecase

import com.maptanim.app.data.local.dao.PlantingMonitorDao
import com.maptanim.app.data.local.entity.PlantingMonitorEntity
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.domain.model.Activity
import com.maptanim.app.domain.model.Crop
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.CropZone
import com.maptanim.app.domain.model.Farm
import com.maptanim.app.domain.model.FarmTask
import com.maptanim.app.domain.model.HarvestRecord
import com.maptanim.app.domain.model.TaskType
import com.maptanim.app.domain.repository.ActivityRepository
import com.maptanim.app.domain.repository.CropPlotRepository
import com.maptanim.app.domain.repository.CropRepository
import com.maptanim.app.domain.repository.CropZoneRepository
import com.maptanim.app.domain.repository.DssRepository
import com.maptanim.app.domain.repository.DssRuleRepository
import com.maptanim.app.domain.repository.FarmRepository
import com.maptanim.app.domain.repository.HarvestRepository
import com.maptanim.app.domain.repository.TaskRepository
import com.maptanim.app.dss.engine.DssEngine
import com.maptanim.app.dss.model.DssDecision
import com.maptanim.app.dss.model.DssDecisionType
import com.maptanim.app.dss.model.DssFarmerData
import com.maptanim.app.dss.model.DssInput
import com.maptanim.app.dss.model.DssReferenceData
import com.maptanim.app.dss.model.DssResult
import com.maptanim.app.dss.model.DssSession
import com.maptanim.app.dss.rules.DssRuleCatalog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

// ─── 1. Create DSS Session Use Case ──────────────────────────────────────────

class CreateDssSessionUseCase {
    operator fun invoke(farmId: String, farmName: String): DssSession {
        val sessionId = "dss_session_" + UUID.randomUUID().toString().take(8)
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        return DssSession(
            sessionId = sessionId,
            farmId = farmId,
            farmName = farmName,
            startedAt = timestamp,
            engineVersion = "2.0.0-DA-BPI"
        )
    }
}

// ─── 2. Build DSS Input Use Case ─────────────────────────────────────────────

class BuildDssInputUseCase(
    private val farmRepository: FarmRepository = RepositoryProvider.farmRepository,
    private val plotRepository: CropPlotRepository = RepositoryProvider.cropPlotRepository,
    private val cropZoneRepository: CropZoneRepository = RepositoryProvider.cropZoneRepository,
    private val cropRepository: CropRepository = RepositoryProvider.cropRepository,
    private val harvestRepository: HarvestRepository = RepositoryProvider.harvestRepository,
    private val activityRepository: ActivityRepository = RepositoryProvider.activityRepository,
    private val dssRuleRepository: DssRuleRepository = RepositoryProvider.dssRuleRepository,
    private val plantingMonitorDao: PlantingMonitorDao? = RepositoryProvider.plantingMonitorDao,
    private val createSession: CreateDssSessionUseCase = CreateDssSessionUseCase()
) {
    suspend operator fun invoke(farmId: String): DssInput = withContext(Dispatchers.IO) {
        // 1. Gather Farmer Actual Data through Repositories
        val farm: Farm? = farmRepository.observeFarm(farmId).firstOrNull()
        val plots: List<CropPlot> = plotRepository.observePlots(farmId).firstOrNull() ?: emptyList()
        val plotIds = plots.map { it.id }
        val cropZones: List<CropZone> = if (plotIds.isNotEmpty()) {
            cropZoneRepository.observeZonesByPlotIds(plotIds).firstOrNull() ?: emptyList()
        } else emptyList()

        val harvests: List<HarvestRecord> = harvestRepository.observeHarvestRecords(farmId).firstOrNull() ?: emptyList()
        val activities: List<Activity> = activityRepository.observeAllActivities().firstOrNull() ?: emptyList()
        val monitors: List<PlantingMonitorEntity> = plantingMonitorDao?.getAllMonitors() ?: emptyList()
        val currentDate = LocalDate.now()

        val farmerData = DssFarmerData(
            farm = farm,
            plots = plots,
            cropZones = cropZones,
            monitors = monitors,
            harvestHistory = harvests,
            recentActivities = activities,
            currentDate = currentDate
        )

        // 2. Gather Agricultural Reference Data through Repositories
        val crops: List<Crop> = cropRepository.observeAllCrops().firstOrNull() ?: emptyList()
        val dynamicRules = dssRuleRepository.getAllRules()
        val referenceData = DssReferenceData(
            crops = crops,
            companionRules = dynamicRules,
            documentedRules = DssRuleCatalog.documentedRules
        )

        // 3. Create Session Context
        val farmName = farm?.farmName ?: "MapTanim Farm"
        val session = createSession(farmId, farmName)

        DssInput(
            session = session,
            farmerData = farmerData,
            referenceData = referenceData
        )
    }
}

// ─── 3. Run DSS Evaluation Use Case ──────────────────────────────────────────

/**
 * Coordinates the full decision-support pipeline:
 * 1. Creates session
 * 2. Builds DSS Input through repositories
 * 3. Runs DSS Engine with documented condition evaluators
 * 4. Resolves conflicts, eliminates duplicates, assigns priority
 * 5. Attaches explanation and scientific citation to each decision
 * 6. Saves/caches DSS result locally in Room and queues sync
 * 7. Dispatches actionable tasks to TaskRepository
 * 8. Returns final DssResult to ViewModel
 */
class RunDssEvaluationUseCase(
    private val buildInput: BuildDssInputUseCase = BuildDssInputUseCase(),
    private val dssEngine: DssEngine = DssEngine(),
    private val dssRepository: DssRepository = RepositoryProvider.dssRepository,
    private val taskRepository: TaskRepository = RepositoryProvider.taskRepository,
    private val notificationRepository: com.maptanim.app.domain.repository.NotificationRepository = RepositoryProvider.notificationRepository
) {
    suspend operator fun invoke(farmId: String): DssResult = withContext(Dispatchers.IO) {
        // Step 1 & 2: Build standardized DSS Input from repositories
        val input = buildInput(farmId)

        // Step 3, 4, 5: Run pure evaluation engine over documented rules
        val result = dssEngine.evaluateSession(input)

        // Step 6: Save and cache the DSS Result locally in Room
        dssRepository.saveDecisions(farmId, result.decisions)

        // Step 7: Push actionable DSS tasks into the task repository
        val todayStr = input.farmerData.currentDate.toString()
        val generatedTasks = result.decisions
            .filter { it.decisionType == DssDecisionType.TASK && it.actionTaskType != null }
            .map { decision ->
                FarmTask(
                    id = "dss_task_${decision.id}",
                    farmId = farmId,
                    plotId = decision.plotId ?: "general",
                    plotLabel = decision.plotLabel ?: "General Bed",
                    cropName = decision.cropName,
                    taskType = decision.actionTaskType ?: TaskType.OBSERVATION,
                    title = decision.title,
                    subLabel = "${decision.cropName ?: "Bed"} • ${decision.source.take(25)}...",
                    dueDate = todayStr,
                    isCompleted = decision.isCompleted,
                    completedAt = null
                )
            }

        if (generatedTasks.isNotEmpty()) {
            taskRepository.upsertTasks(generatedTasks)
        }

        // Step 8: Push high-priority decisions (CRITICAL / HIGH) to notification system
        val highPriorityDecisions = result.decisions.filter {
            it.priority == com.maptanim.app.dss.model.DssPriority.CRITICAL ||
            it.priority == com.maptanim.app.dss.model.DssPriority.HIGH
        }
        if (highPriorityDecisions.isNotEmpty()) {
            val alerts = highPriorityDecisions.map { decision ->
                com.maptanim.app.domain.model.Notification(
                    id = "notif_dss_${decision.id}",
                    userId = input.farmerData.farm?.farmerId ?: "farmer-1",
                    title = "⚠️ DSS Babala: ${decision.title}",
                    body = decision.summary,
                    taskType = decision.actionTaskType,
                    isRead = false,
                    createdAt = java.time.Instant.now().toString()
                )
            }
            try {
                notificationRepository.upsertNotifications(alerts)
            } catch (_: Exception) {}
        }

        result
    }
}

// ─── 4. Observe Cached DSS Decisions Use Case ────────────────────────────────

class ObserveDssDecisionsUseCase(
    private val dssRepository: DssRepository = RepositoryProvider.dssRepository
) {
    operator fun invoke(farmId: String): Flow<List<DssDecision>> =
        dssRepository.observeDecisions(farmId)
}

// ─── 5. Act on DSS Recommendation / Task Use Case ───────────────────────────

/**
 * Handles farmer interaction on a recommendation or task:
 * 1. Marks the decision as acted upon in Room
 * 2. If it was a physical care task, logs the activity in ActivityRepository
 * 3. Updated farmer activity immediately feeds into future DSS evaluations
 */
class ActOnDssDecisionUseCase(
    private val dssRepository: DssRepository = RepositoryProvider.dssRepository,
    private val activityRepository: ActivityRepository = RepositoryProvider.activityRepository,
    private val taskRepository: TaskRepository = RepositoryProvider.taskRepository
) {
    suspend operator fun invoke(decision: DssDecision): Unit = withContext(Dispatchers.IO) {
        // 1. Mark decision completed in DSS cache
        dssRepository.markDecisionCompleted(decision.id)

        // 2. If it's a physical care task, record the activity so future evaluations see it
        val taskType = decision.actionTaskType
        val plotId = decision.plotId
        if (taskType != null && plotId != null) {
            val nowStr = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val activity = Activity(
                id = "act_dss_" + UUID.randomUUID().toString().take(8),
                plotId = plotId,
                farmId = decision.farmId,
                type = taskType,
                notes = "${decision.title} — Completed via DSS: ${decision.summary}",
                performedAt = nowStr
            )
            activityRepository.logActivity(activity)

            // Mark corresponding task completed if exists
            taskRepository.completeTask("dss_task_${decision.id}", nowStr)
        }
    }
}
