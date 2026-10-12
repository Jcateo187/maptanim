package com.maptanim.app.dss.engine

import com.maptanim.app.domain.model.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * DssLogEvaluator — Processes a submitted CropLog and evaluates
 * how it affects the crop management lifecycle.
 *
 * From the spec, when a log is submitted the DSS evaluates:
 * 1. TASK STATUS — Does the log satisfy a pending task?
 * 2. RECOMMENDATION — Does the condition match a documented recommendation?
 * 3. ALERT — Does the condition match an alert condition?
 * 4. CROP RELATIONSHIP — Check crops sharing the same bed
 * 5. STAGE PROGRESSION — Check if current stage requirements are met
 * 6. ACTIVITY HISTORY — Save to activity log
 */
class DssLogEvaluator {

    data class LogEvaluationResult(
        val completedTaskIds: List<String>,
        val newTasks: List<GeneratedLogTask>,
        val newRecommendations: List<LogRecommendation>,
        val newAlerts: List<LogAlert>,
        val stageAdvanced: Boolean,
        val newStage: ManagementStage?,
        val statusChange: CropPlantingStatus?
    )

    data class GeneratedLogTask(
        val id: String,
        val title: String,
        val description: String,
        val taskType: TaskType,
        val dueDate: String,
        val stage: ManagementStage = ManagementStage.PREPARATION
    )

    data class LogRecommendation(
        val id: String,
        val title: String,
        val content: String,
        val stage: ManagementStage,
        val priority: Int = 0  // 0=normal, 1=high, 2=critical
    )

    data class LogAlert(
        val id: String,
        val title: String,
        val content: String,
        val alertType: String,  // "PEST", "DISEASE", "WEATHER", "CARE", "COMPANION"
        val stage: ManagementStage,
        val severity: Int = 1  // 1=info, 2=warning, 3=critical
    )

    /**
     * Main evaluation entry point.
     * @param log The newly submitted crop log
     * @param previousLogs All previous logs for this crop planting (chronological)
     * @param currentStage The current management stage
     * @param pendingTasks Currently pending tasks for this crop planting
     * @param plantingDate The original planting date
     * @param plantingMethod Direct Seeding or Transplanting
     * @param daysToHarvest Expected days from planting to harvest
     */
    fun evaluate(
        log: CropLog,
        previousLogs: List<CropLog>,
        currentStage: ManagementStage,
        pendingTasks: List<FarmTask>,
        plantingDate: String?,
        plantingMethod: String,
        daysToHarvest: Int,
        today: LocalDate = LocalDate.now()
    ): LogEvaluationResult {
        val completedTaskIds = evaluateTaskStatus(log, pendingTasks)
        val newTasks = generateFollowUpTasks(log, currentStage, today)
        val newRecommendations = evaluateRecommendations(log, previousLogs, currentStage)
        val newAlerts = evaluateAlerts(log, previousLogs, currentStage)
        val (stageAdvanced, newStage) = evaluateStageProgression(
            log, previousLogs, currentStage, plantingDate, daysToHarvest, today
        )
        val statusChange = evaluateStatusChange(log, currentStage, stageAdvanced, newStage)

        return LogEvaluationResult(
            completedTaskIds = completedTaskIds,
            newTasks = newTasks,
            newRecommendations = newRecommendations,
            newAlerts = newAlerts,
            stageAdvanced = stageAdvanced,
            newStage = newStage,
            statusChange = statusChange
        )
    }

    // ─── 1. TASK STATUS ────────────────────────────────────────────────────

    private fun evaluateTaskStatus(log: CropLog, pendingTasks: List<FarmTask>): List<String> {
        val completed = mutableListOf<String>()

        pendingTasks.forEach { task ->
            val matches = when (task.taskType) {
                TaskType.WATER -> log.logContext == LogContext.CARE_MAINTENANCE &&
                        log.careActivity == CareActivity.WATERING
                TaskType.FERTILIZE -> log.logContext == LogContext.CARE_MAINTENANCE &&
                        log.careActivity == CareActivity.FERTILIZING
                TaskType.WEED -> log.logContext == LogContext.CARE_MAINTENANCE &&
                        log.careActivity == CareActivity.WEEDING
                TaskType.PRUNING -> log.logContext == LogContext.CARE_MAINTENANCE &&
                        log.careActivity == CareActivity.PRUNING
                TaskType.OBSERVATION -> log.logContext == LogContext.OBSERVE
                TaskType.PEST_ALERT -> log.logContext == LogContext.CARE_MAINTENANCE &&
                        log.careActivity == CareActivity.PEST_CONTROL
                TaskType.TRELLIS -> log.logContext == LogContext.CARE_MAINTENANCE &&
                        log.careActivity == CareActivity.STAKING
                else -> false
            }
            if (matches) completed.add(task.id)
        }

        return completed
    }

    // ─── 2. FOLLOW-UP TASKS (GENERATED FOR THIS DAY) ─────────────────────────

    private fun generateFollowUpTasks(
        log: CropLog,
        currentStage: ManagementStage,
        today: LocalDate
    ): List<GeneratedLogTask> {
        val tasks = mutableListOf<GeneratedLogTask>()
        val todayStr = today.toString()
        val conditionsText = if (log.selectedCheckboxes.isNotEmpty()) log.selectedCheckboxes.joinToString(", ") else "observation logged"

        when (log.logContext) {
            LogContext.OBSERVE -> {
                when (log.selectedChoice) {
                    "A" -> {
                        // Normal growth -> standard daily maintenance
                        tasks.add(GeneratedLogTask(
                            id = "task-maint-${log.id}",
                            title = "Daily Maintenance for ${log.cropName}",
                            description = "Normal growth observed ($conditionsText). Maintain 3–5 cm organic mulch layer and verify morning base soil moisture.",
                            taskType = TaskType.WATER,
                            dueDate = todayStr,
                            stage = currentStage
                        ))
                    }
                    "B" -> {
                        // Stress / Needs care -> immediate corrective action today
                        val hasDampingOff = log.selectedCheckboxes.any { it.contains("Hulas", ignoreCase = true) || it.contains("Stem rot", ignoreCase = true) }
                        val isLeggy = log.selectedCheckboxes.any { it.contains("Leggy", ignoreCase = true) }
                        val (taskTitle, taskDesc, taskType) = when {
                            hasDampingOff -> Triple(
                                "Dust Wood Ash for Damping-Off ('Hulas')",
                                "Halt overhead watering, scrape damp topsoil away from collar, and dust dry wood ash (abo) around base.",
                                TaskType.SOIL_AMENDMENT
                            )
                            isLeggy -> Triple(
                                "Mound Soil & Move Leggy Seedlings to Sun",
                                "Mound 1cm compost ring around stem and relocate seedlings to 6+ hours direct morning sun.",
                                TaskType.SOIL_AMENDMENT
                            )
                            else -> Triple(
                                "Corrective Care for ${log.cropName}",
                                "Stress indicators noted ($conditionsText). Side-dress with compost or organic vermicast and check drainage today.",
                                TaskType.FERTILIZE
                            )
                        }
                        tasks.add(GeneratedLogTask(
                            id = "task-care-${log.id}",
                            title = taskTitle,
                            description = taskDesc,
                            taskType = taskType,
                            dueDate = todayStr,
                            stage = currentStage
                        ))
                    }
                    "C" -> {
                        // Pest / Damage -> urgent mitigation today
                        val hasDampingOff = log.selectedCheckboxes.any { it.contains("Hulas", ignoreCase = true) || it.contains("Stem rot", ignoreCase = true) }
                        val isChicken = log.selectedCheckboxes.any { it.contains("Chicken", ignoreCase = true) || it.contains("animal", ignoreCase = true) }
                        val isPulled = log.selectedCheckboxes.any { it.contains("Pulled", ignoreCase = true) || it.contains("missing", ignoreCase = true) }
                        val isBlossomEndRot = log.selectedCheckboxes.any { it.contains("Blossom-End Rot", ignoreCase = true) }
                        val isBacterialWilt = log.selectedCheckboxes.any { it.contains("Bacterial wilt", ignoreCase = true) }
                        val (taskTitle, taskDesc, taskType) = when {
                            hasDampingOff -> Triple(
                                "Dust Wood Ash for Damping-Off ('Hulas')",
                                "Halt overhead watering, scrape damp topsoil away from collar, and dust dry wood ash (abo) around base.",
                                TaskType.SOIL_AMENDMENT
                            )
                            isChicken -> Triple(
                                "Install Bamboo Suksok Barrier (Chicken Scratch)",
                                "Firm disturbed root crown, water lightly, and plant 10cm sharpened bamboo skewers (suksok) around perimeter.",
                                TaskType.OBSERVATION
                            )
                            isPulled -> Triple(
                                "Interplant Pechay in Open Spacing Gap",
                                "Plant gap > 30cm detected. Transplant fast-growing Pechay/Mustard to utilize root zone and suppress weeds.",
                                TaskType.SOIL_AMENDMENT
                            )
                            isBlossomEndRot -> Triple(
                                "Apply Steeped Eggshell Vinegar for Blossom-End Rot",
                                "Calcium uptake blocked. Drench root zone with steeped eggshell solution and mulch with rice straw.",
                                TaskType.FERTILIZE
                            )
                            isBacterialWilt -> Triple(
                                "Rogue & Quarantine Bacterial Wilt Plant",
                                "Dig out entire wilted plant with root soil and dispose outside garden. Plan Legume rotation next cycle.",
                                TaskType.PEST_ALERT
                            )
                            else -> Triple(
                                "Apply Organic Spray for ${log.cropName}",
                                "Pest/damage detected ($conditionsText). Spray organic chili-garlic-soap extract or neem solution late afternoon.",
                                TaskType.APPLY_PESTICIDE
                            )
                        }
                        tasks.add(GeneratedLogTask(
                            id = "task-pest-${log.id}",
                            title = taskTitle,
                            description = taskDesc,
                            taskType = taskType,
                            dueDate = todayStr,
                            stage = currentStage
                        ))
                    }
                }
            }
            LogContext.CARE_MAINTENANCE -> {
                tasks.add(GeneratedLogTask(
                    id = "task-careact-${log.id}",
                    title = "Verify ${log.careActivity?.label ?: "Care Activity"} on ${log.cropName}",
                    description = "Completed ${log.careActivity?.label ?: "care"} today ($conditionsText). Check crop reaction and ensure root zone hydration.",
                    taskType = when (log.careActivity) {
                        CareActivity.WATERING -> TaskType.WATER
                        CareActivity.FERTILIZING -> TaskType.FERTILIZE
                        CareActivity.WEEDING -> TaskType.WEED
                        CareActivity.PRUNING -> TaskType.PRUNING
                        CareActivity.STAKING -> TaskType.TRELLIS
                        CareActivity.PEST_CONTROL -> TaskType.PEST_ALERT
                        else -> TaskType.OBSERVATION
                    },
                    dueDate = todayStr,
                    stage = currentStage
                ))
            }
            LogContext.PLANTING -> {
                tasks.add(GeneratedLogTask(
                    id = "task-plant-${log.id}",
                    title = "Post-Planting Hydration Check for ${log.cropName}",
                    description = "Planting status ($conditionsText). Provide gentle base watering in late afternoon and protect seedling root collar.",
                    taskType = TaskType.WATER,
                    dueDate = todayStr,
                    stage = currentStage
                ))
            }
            LogContext.PREPARATION, LogContext.READINESS -> {
                tasks.add(GeneratedLogTask(
                    id = "task-prep-${log.id}",
                    title = "Bed Conditioning Check for ${log.cropName}",
                    description = "Soil preparation ($conditionsText). Verify soil aeration and level before scheduled planting.",
                    taskType = TaskType.OBSERVATION,
                    dueDate = todayStr,
                    stage = currentStage
                ))
            }
            LogContext.INITIAL_OBSERVATION -> {
                tasks.add(GeneratedLogTask(
                    id = "task-initobs-${log.id}",
                    title = "Establishment Check for ${log.cropName}",
                    description = "Post-planting observation ($conditionsText). Verify root settlement and shelter from excessive heat.",
                    taskType = TaskType.OBSERVATION,
                    dueDate = todayStr,
                    stage = currentStage
                ))
            }
        }

        return tasks
    }

    // ─── 3. RECOMMENDATIONS & GUIDES ──────────────────────────────────────────

    private fun evaluateRecommendations(
        log: CropLog,
        previousLogs: List<CropLog>,
        currentStage: ManagementStage
    ): List<LogRecommendation> {
        val recs = mutableListOf<LogRecommendation>()
        val conditionsText = if (log.selectedCheckboxes.isNotEmpty()) log.selectedCheckboxes.joinToString(", ") else "Standard protocol"

        when (log.logContext) {
            LogContext.OBSERVE -> {
                when (log.selectedChoice) {
                    "A" -> {
                        recs.add(LogRecommendation(
                            id = "rec-maint-${log.id}",
                            title = "Optimal Stage Protocol (${currentStage.label})",
                            content = "${log.cropName} is showing healthy vigor ($conditionsText). Maintain steady morning root hydration. Keep 3–5 cm organic mulch layer intact to preserve soil moisture and regulate root temperature.",
                            stage = currentStage,
                            priority = 0
                        ))
                    }
                    "B" -> {
                        val hasDampingOff = log.selectedCheckboxes.any { it.contains("Hulas", ignoreCase = true) || it.contains("Stem rot", ignoreCase = true) }
                        val isLeggy = log.selectedCheckboxes.any { it.contains("Leggy", ignoreCase = true) }
                        val content = when {
                            hasDampingOff -> "Damping-off ('Hulas') fungal collar rot detected. Immediately halt overhead watering. Scrape damp topsoil away from stem collar and dust dry wood ash (abo) around base to dehydrate fungal mycelium. Expose to full morning sun."
                            isLeggy -> "Seedlings are leggy from insufficient sunlight. Move immediately to 6+ hours direct morning sun. Mound a small ring of compost/garden soil 1cm up the stem to provide physical stabilization."
                            else -> "Plant stress indicators detected ($conditionsText). Apply diluted organic compost tea or aged manure water around root drip-line. Inspect drainage to prevent waterlogging."
                        }
                        recs.add(LogRecommendation(
                            id = "rec-care-${log.id}",
                            title = if (hasDampingOff) "Emergency Damping-Off ('Hulas') Protocol" else "Agronomic Recovery Guide (${currentStage.label})",
                            content = content,
                            stage = currentStage,
                            priority = if (hasDampingOff) 2 else 1
                        ))
                    }
                    "C" -> {
                        val isPulled = log.selectedCheckboxes.any { it.contains("Pulled", ignoreCase = true) || it.contains("missing", ignoreCase = true) }
                        val isChicken = log.selectedCheckboxes.any { it.contains("Chicken", ignoreCase = true) || it.contains("animal", ignoreCase = true) }
                        val isBlossomEndRot = log.selectedCheckboxes.any { it.contains("Blossom-End Rot", ignoreCase = true) }
                        val isBacterialWilt = log.selectedCheckboxes.any { it.contains("Bacterial wilt", ignoreCase = true) }

                        val (title, content, priority) = when {
                            isPulled -> Triple(
                                "Accidental Loss / Plant Pulled Recovery",
                                "A plant was pulled or destroyed. Check spacing to neighboring crops. If gap > 30cm, transplant a fast-growing leafy companion (Pechay or Mustard) to utilize open root space and suppress weed takeover.",
                                1
                            )
                            isChicken -> Triple(
                                "Animal / Chicken Scratch Damage Barrier",
                                "Roots and soil disturbed by stray animals. Gently firm loose soil back around root crown and water base lightly. Insert sharpened bamboo skewers/twigs (suksok) spaced 10cm apart around bed perimeter as a physical deterrent.",
                                2
                            )
                            isBlossomEndRot -> Triple(
                                "Blossom-End Rot (Calcium & Water Imbalance)",
                                "Dark leathery sunken bottom is Blossom-End Rot caused by erratic watering blocking Calcium uptake. Apply steeped crushed eggshells in water/vinegar at soil base. Water regularly at base and apply rice straw mulch.",
                                2
                            )
                            isBacterialWilt -> Triple(
                                "Bacterial Wilt Quarantine Protocol",
                                "Sudden daytime plant wilt is Ralstonia bacterial wilt. Dig out entire plant with root soil immediately and dispose away from garden (do not compost). Rotate this bed to Sitaw or Corn next cycle.",
                                2
                            )
                            else -> Triple(
                                "Pest & Foliar Management Guide",
                                "Damage or pest detected ($conditionsText). Spray organic chili-garlic-soap extract or neem solution late afternoon. Handpick visible caterpillars and prune lowest mud-splashed leaves.",
                                2
                            )
                        }
                        recs.add(LogRecommendation(
                            id = "rec-pest-${log.id}",
                            title = title,
                            content = content,
                            stage = currentStage,
                            priority = priority
                        ))
                    }
                }
            }
            LogContext.CARE_MAINTENANCE -> {
                val actLabel = log.careActivity?.label ?: "Care"
                recs.add(LogRecommendation(
                    id = "rec-care-${log.id}",
                    title = "$actLabel Follow-Up Guidance",
                    content = "Recorded $actLabel ($conditionsText). Ensure soil moisture stays balanced. For heavy fruiting varieties, continue tying main stems to support stakes as plant weight increases.",
                    stage = currentStage,
                    priority = 0
                ))
            }
            LogContext.PLANTING -> {
                recs.add(LogRecommendation(
                    id = "rec-plant-${log.id}",
                    title = "Transplanting Establishment Protocol",
                    content = "Newly planted ${log.cropName} ($conditionsText). Transplant during late afternoon to minimize shock. Water deeply at root base and maintain partial afternoon shade for the first 3 days.",
                    stage = currentStage,
                    priority = 1
                ))
            }
            LogContext.PREPARATION, LogContext.READINESS -> {
                recs.add(LogRecommendation(
                    id = "rec-prep-${log.id}",
                    title = "Bed Conditioning & Soil Health",
                    content = "Soil preparation ($conditionsText). Incorporate 2–3 kg/m² composted organic matter to 20 cm depth. Maintain 60% soil moisture for beneficial microbial proliferation.",
                    stage = currentStage,
                    priority = 0
                ))
            }
            LogContext.INITIAL_OBSERVATION -> {
                recs.add(LogRecommendation(
                    id = "rec-init-${log.id}",
                    title = "Early Establishment Monitoring",
                    content = "Early observation ($conditionsText). Inspect seedling stems daily for signs of damping-off or cutworm damage. Maintain consistent base moisture.",
                    stage = currentStage,
                    priority = 1
                ))
            }
        }

        return recs
    }

    // ─── 4. ALERTS ─────────────────────────────────────────────────────────

    private fun evaluateAlerts(
        log: CropLog,
        previousLogs: List<CropLog>,
        currentStage: ManagementStage
    ): List<LogAlert> {
        val alerts = mutableListOf<LogAlert>()

        // Pest damage reported
        if (log.logContext == LogContext.OBSERVE && log.selectedChoice == "C") {
            val hasPestCheckbox = log.selectedCheckboxes.any {
                it.contains("pest", ignoreCase = true) ||
                it.contains("insect", ignoreCase = true) ||
                it.contains("damage", ignoreCase = true)
            }
            if (hasPestCheckbox) {
                alerts.add(LogAlert(
                    id = "alert-pest-${log.id}",
                    title = "Pest Activity Detected",
                    content = "Pest damage reported on ${log.cropName}. Apply organic pest control (neem oil or BT spray) early morning. Monitor daily.",
                    alertType = "PEST",
                    stage = currentStage,
                    severity = 2
                ))
            }
        }

        // Repeated negative observations (3+ bad observations in a row)
        val recentBadObs = previousLogs
            .filter { it.logContext == LogContext.OBSERVE }
            .takeLast(3)
            .count { it.selectedChoice != "A" }
        if (recentBadObs >= 2 && log.logContext == LogContext.OBSERVE && log.selectedChoice != "A") {
            alerts.add(LogAlert(
                id = "alert-recurring-${log.id}",
                title = "Recurring Issue Detected",
                content = "Multiple negative observations recorded. The crop may need more intensive care or diagnosis.",
                alertType = "CARE",
                stage = currentStage,
                severity = 3
            ))
        }

        // Flower dropping in flowering stage
        if (currentStage == ManagementStage.FLOWERING_FRUIT_DEVELOPMENT &&
            log.selectedChoice == "B" &&
            log.selectedCheckboxes.any { it.contains("dropping", ignoreCase = true) }
        ) {
            alerts.add(LogAlert(
                id = "alert-flowerdrop-${log.id}",
                title = "Flower Drop Alert",
                content = "Flower dropping may indicate heat stress, water stress, or poor pollination. Check microclimate conditions.",
                alertType = "CARE",
                stage = currentStage,
                severity = 2
            ))
        }

        return alerts
    }

    // ─── 5. STAGE PROGRESSION ──────────────────────────────────────────────

    /**
     * Checks whether the current stage requirements are satisfied
     * and the crop is eligible for the next stage.
     *
     * Returns (stageAdvanced, newStage) — newStage is null if no advance.
     */
    private fun evaluateStageProgression(
        log: CropLog,
        previousLogs: List<CropLog>,
        currentStage: ManagementStage,
        plantingDate: String?,
        daysToHarvest: Int,
        today: LocalDate
    ): Pair<Boolean, ManagementStage?> {

        val allLogs = previousLogs + log

        return when (currentStage) {
            ManagementStage.PREPARATION -> {
                // Advance to PLANTING when preparation and readiness logs exist
                val hasPrep = allLogs.any { it.logContext == LogContext.PREPARATION && it.selectedChoice == "C" } ||
                        allLogs.count { it.logContext == LogContext.PREPARATION } >= 2
                val hasReadiness = allLogs.any { it.logContext == LogContext.READINESS }
                if (hasPrep && hasReadiness) {
                    Pair(true, ManagementStage.PLANTING)
                } else {
                    Pair(false, null)
                }
            }

            ManagementStage.PLANTING -> {
                // Advance to EARLY_GROWTH when planting is confirmed completed
                val plantingCompleted = allLogs.any {
                    it.logContext == LogContext.PLANTING && it.selectedChoice == "C"
                }
                if (plantingCompleted) {
                    Pair(true, ManagementStage.EARLY_GROWTH)
                } else {
                    Pair(false, null)
                }
            }

            ManagementStage.EARLY_GROWTH -> {
                // Advance to VEGETATIVE_GROWTH based on:
                // - At least 2 observations logged
                // - At least 7 days since planting (or first EARLY_GROWTH log)
                val earlyGrowthLogs = allLogs.filter { it.currentStage == ManagementStage.EARLY_GROWTH }
                val hasEnoughObs = earlyGrowthLogs.count { it.logContext == LogContext.OBSERVE } >= 2
                val daysSincePlanting = plantingDate?.let {
                    try {
                        ChronoUnit.DAYS.between(LocalDate.parse(it.take(10)), today).toInt()
                    } catch (_: Exception) { 0 }
                } ?: 0
                val timeReady = daysSincePlanting >= 7 || daysToHarvest <= 30
                if (hasEnoughObs && timeReady) {
                    Pair(true, ManagementStage.VEGETATIVE_GROWTH)
                } else {
                    Pair(false, null)
                }
            }

            ManagementStage.VEGETATIVE_GROWTH -> {
                // Advance to FLOWERING when:
                // - Enough time has elapsed (roughly 35-65% of daysToHarvest)
                // - At least 3 care/observation logs in vegetative stage
                val vegLogs = allLogs.filter { it.currentStage == ManagementStage.VEGETATIVE_GROWTH }
                val hasEnoughActivity = vegLogs.size >= 3
                val daysSincePlanting = plantingDate?.let {
                    try {
                        ChronoUnit.DAYS.between(LocalDate.parse(it.take(10)), today).toInt()
                    } catch (_: Exception) { 0 }
                } ?: 0
                val progressPct = if (daysToHarvest > 0) daysSincePlanting.toFloat() / daysToHarvest else 0f
                val timeReady = progressPct >= 0.35f
                if (hasEnoughActivity && timeReady) {
                    Pair(true, ManagementStage.FLOWERING_FRUIT_DEVELOPMENT)
                } else {
                    Pair(false, null)
                }
            }

            ManagementStage.FLOWERING_FRUIT_DEVELOPMENT -> {
                // Advance to HARVEST when:
                // - Enough time has elapsed (roughly 65-90% of daysToHarvest)
                // - Fruit development observations are present
                val flowerLogs = allLogs.filter { it.currentStage == ManagementStage.FLOWERING_FRUIT_DEVELOPMENT }
                val hasEnoughActivity = flowerLogs.size >= 2
                val hasFruitDev = flowerLogs.any {
                    it.logContext == LogContext.OBSERVE && it.selectedCheckboxes.any { cb ->
                        cb.contains("fruit", ignoreCase = true) && cb.contains("develop", ignoreCase = true)
                    }
                }
                val daysSincePlanting = plantingDate?.let {
                    try {
                        ChronoUnit.DAYS.between(LocalDate.parse(it.take(10)), today).toInt()
                    } catch (_: Exception) { 0 }
                } ?: 0
                val progressPct = if (daysToHarvest > 0) daysSincePlanting.toFloat() / daysToHarvest else 0f
                val timeReady = progressPct >= 0.65f
                if (hasEnoughActivity && (hasFruitDev || timeReady)) {
                    Pair(true, ManagementStage.HARVEST)
                } else {
                    Pair(false, null)
                }
            }

            ManagementStage.HARVEST -> {
                // HARVEST is the final stage — no further progression
                Pair(false, null)
            }
        }
    }

    // ─── 6. STATUS CHANGE ──────────────────────────────────────────────────

    private fun evaluateStatusChange(
        log: CropLog,
        currentStage: ManagementStage,
        stageAdvanced: Boolean,
        newStage: ManagementStage?
    ): CropPlantingStatus? {
        // When planting is confirmed completed → change status from PLANNED to ACTIVE
        if (currentStage == ManagementStage.PLANTING &&
            log.logContext == LogContext.PLANTING &&
            log.selectedChoice == "C"
        ) {
            return CropPlantingStatus.ACTIVE
        }

        // Stage advanced to EARLY_GROWTH also means ACTIVE
        if (stageAdvanced && newStage == ManagementStage.EARLY_GROWTH) {
            return CropPlantingStatus.ACTIVE
        }

        return null
    }
}
