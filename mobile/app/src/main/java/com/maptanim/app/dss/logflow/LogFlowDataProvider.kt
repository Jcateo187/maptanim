package com.maptanim.app.dss.logflow

import com.maptanim.app.domain.model.*

/**
 * LogFlowDataProvider — Provides the questions, choices, and checkbox options
 * for each stage + context combination in the "Add Log" flow.
 *
 * This implements the user's exact pseudocode specification:
 *   CURRENT STAGE → ALLOWED LOG CONTEXTS → QUESTION → A/B/C → RELEVANT CHECKBOXES
 */
object LogFlowDataProvider {

    data class LogFlowQuestion(
        val question: String,
        val choices: List<LogFlowChoice>
    )

    data class LogFlowChoice(
        val key: String,       // "A", "B", or "C"
        val label: String,
        val checkboxes: List<String>
    )

    // ─── PREPARATION Context ───────────────────────────────────────────────

    fun getPreparationQuestion(): LogFlowQuestion = LogFlowQuestion(
        question = "What are you preparing?",
        choices = listOf(
            LogFlowChoice(
                key = "A",
                label = "Planting Area",
                checkboxes = listOf(
                    "Planting area prepared",
                    "Soil/media prepared",
                    "Water available",
                    "Tools available"
                )
            ),
            LogFlowChoice(
                key = "B",
                label = "Materials",
                checkboxes = listOf(
                    "Required materials available",
                    "Planting inputs available",
                    "Water available",
                    "Tools available"
                )
            ),
            LogFlowChoice(
                key = "C",
                label = "Seeds or Seedlings",
                checkboxes = listOf(
                    "Seeds ready",
                    "Seedlings ready",
                    "Seedlings suitable for planting"
                )
            )
        )
    )

    // ─── READINESS Context ─────────────────────────────────────────────────

    fun getReadinessQuestion(): LogFlowQuestion = LogFlowQuestion(
        question = "What is ready now?",
        choices = listOf(
            LogFlowChoice(
                key = "A",
                label = "Area Ready",
                checkboxes = listOf(
                    "Bed soil is leveled",
                    "Drainage is clear",
                    "Area is weed-free",
                    "Compost/amendments applied"
                )
            ),
            LogFlowChoice(
                key = "B",
                label = "Materials Ready",
                checkboxes = listOf(
                    "Fertilizer on hand",
                    "Stakes/trellis materials ready",
                    "Mulching materials prepared",
                    "Pest control inputs available"
                )
            ),
            LogFlowChoice(
                key = "C",
                label = "Crop Ready",
                checkboxes = listOf(
                    "Seeds treated/soaked",
                    "Seedlings hardened off",
                    "Seedlings are healthy",
                    "Seedlings at proper transplant age"
                )
            )
        )
    )

    // ─── PLANTING Context ──────────────────────────────────────────────────

    fun getPlantingQuestion(plantingMethod: String): LogFlowQuestion {
        val isDirectSeeding = plantingMethod.contains("Direct", ignoreCase = true)

        return LogFlowQuestion(
            question = "What have you completed?",
            choices = listOf(
                LogFlowChoice(
                    key = "A",
                    label = "Not Started",
                    checkboxes = if (isDirectSeeding) listOf(
                        "Planting area not yet prepared",
                        "Seeds not yet ready",
                        "Will start soon"
                    ) else listOf(
                        "Planting area not yet prepared",
                        "Seedlings not yet ready",
                        "Will start soon"
                    )
                ),
                LogFlowChoice(
                    key = "B",
                    label = "Partially Completed",
                    checkboxes = if (isDirectSeeding) listOf(
                        "Seeds prepared",
                        "Seeds placed",
                        "Planting partially completed",
                        "Initial watering completed (if applicable)"
                    ) else listOf(
                        "Seedlings prepared",
                        "Seedlings placed",
                        "Transplanting partially completed",
                        "Initial watering completed (if applicable)"
                    )
                ),
                LogFlowChoice(
                    key = "C",
                    label = "Completed",
                    checkboxes = if (isDirectSeeding) listOf(
                        "Seeds sown",
                        "Planting completed",
                        "Initial watering completed"
                    ) else listOf(
                        "Seedlings transplanted",
                        "Planting completed",
                        "Initial watering completed"
                    )
                )
            )
        )
    }

    // ─── INITIAL OBSERVATION Context ────────────────────────────────────────

    fun getInitialObservationQuestion(): LogFlowQuestion = LogFlowQuestion(
        question = "How does the newly planted crop look?",
        choices = listOf(
            LogFlowChoice(
                key = "A",
                label = "Normal",
                checkboxes = listOf(
                    "Leaves look healthy",
                    "Stems are upright",
                    "Root establishment looks good",
                    "No visible damage"
                )
            ),
            LogFlowChoice(
                key = "B",
                label = "Needs Attention",
                checkboxes = listOf(
                    "Some wilting observed",
                    "Transplant shock visible",
                    "Uneven emergence",
                    "Needs extra watering"
                )
            ),
            LogFlowChoice(
                key = "C",
                label = "Damaged",
                checkboxes = listOf(
                    "Physical damage from handling",
                    "Pest damage visible",
                    "Root damage during transplant",
                    "Needs replanting"
                )
            )
        )
    )

    // ─── OBSERVE Context (Stage-Dependent) ──────────────────────────────────

    fun getObserveQuestion(stage: ManagementStage): LogFlowQuestion = when (stage) {
        ManagementStage.EARLY_GROWTH -> LogFlowQuestion(
            question = "What do you see on the young plant?",
            choices = listOf(
                LogFlowChoice(
                    key = "A",
                    label = "Healthy / Normal",
                    checkboxes = listOf(
                        "Leaves normal",
                        "Growth normal",
                        "No visible damage",
                        "Root establishment progressing"
                    )
                ),
                LogFlowChoice(
                    key = "B",
                    label = "Weak / Abnormal",
                    checkboxes = listOf(
                        "Yellowing observed",
                        "Wilting visible",
                        "Poor development",
                        "Stunted growth"
                    )
                ),
                LogFlowChoice(
                    key = "C",
                    label = "Pest Damage",
                    checkboxes = listOf(
                        "Holes in leaves",
                        "Insects visible",
                        "Leaf damage",
                        "Stem damage"
                    )
                )
            )
        )

        ManagementStage.VEGETATIVE_GROWTH -> LogFlowQuestion(
            question = "How is the plant developing?",
            choices = listOf(
                LogFlowChoice(
                    key = "A",
                    label = "Growing Normally",
                    checkboxes = listOf(
                        "Good leaf development",
                        "Strong stem growth",
                        "Healthy branching",
                        "Normal color"
                    )
                ),
                LogFlowChoice(
                    key = "B",
                    label = "Needs Care",
                    checkboxes = listOf(
                        "Nutrient deficiency signs",
                        "Slow growth",
                        "Needs staking/support",
                        "Needs pruning"
                    )
                ),
                LogFlowChoice(
                    key = "C",
                    label = "Problem Visible",
                    checkboxes = listOf(
                        "Disease symptoms",
                        "Pest infestation",
                        "Physical damage",
                        "Water stress"
                    )
                )
            )
        )

        ManagementStage.FLOWERING_FRUIT_DEVELOPMENT -> LogFlowQuestion(
            question = "What do you see on the flowers or fruits?",
            choices = listOf(
                LogFlowChoice(
                    key = "A",
                    label = "Normal",
                    checkboxes = listOf(
                        "Flowers present",
                        "Flowers developing",
                        "Fruit set observed",
                        "Fruit developing normally"
                    )
                ),
                LogFlowChoice(
                    key = "B",
                    label = "Flower Problem",
                    checkboxes = listOf(
                        "Flowers dropping",
                        "Poor flower development",
                        "Low flower count",
                        "Pollination issues"
                    )
                ),
                LogFlowChoice(
                    key = "C",
                    label = "Fruit or Pest Problem",
                    checkboxes = listOf(
                        "Fruit damage",
                        "Pest on fruit",
                        "Fruit rot/disease",
                        "Abnormal fruit development"
                    )
                )
            )
        )

        // Fallback for stages that don't typically have OBSERVE
        else -> LogFlowQuestion(
            question = "What do you observe?",
            choices = listOf(
                LogFlowChoice("A", "Normal", listOf("Everything looks good")),
                LogFlowChoice("B", "Needs Attention", listOf("Something needs attention")),
                LogFlowChoice("C", "Problem", listOf("Problem observed"))
            )
        )
    }

    // ─── CARE / MAINTENANCE Context ─────────────────────────────────────────

    fun getCareMaintenanceQuestion(): LogFlowQuestion = LogFlowQuestion(
        question = "What did you do?",
        choices = emptyList() // Uses activity dropdown instead of A/B/C
    )

    /**
     * Returns the applicable checkboxes for a specific care activity.
     */
    fun getCheckboxesForCareActivity(activity: CareActivity): List<String> = when (activity) {
        CareActivity.WATERING -> listOf(
            "Watered at root base",
            "Used drip irrigation",
            "Overhead watering",
            "Amount: light watering",
            "Amount: deep watering"
        )
        CareActivity.WEEDING -> listOf(
            "Hand-pulled weeds",
            "Used hoe/cultivator",
            "Cleared around root zone",
            "Disposed of weed material"
        )
        CareActivity.FERTILIZING -> listOf(
            "Applied organic fertilizer",
            "Applied synthetic fertilizer",
            "Used foliar spray",
            "Applied compost/vermicast",
            "Side-dressed fertilizer"
        )
        CareActivity.MULCHING -> listOf(
            "Applied rice straw mulch",
            "Applied grass clippings",
            "Applied plastic mulch",
            "Refreshed existing mulch"
        )
        CareActivity.STAKING -> listOf(
            "Installed bamboo stakes",
            "Tied plants to stakes",
            "Installed trellis/support",
            "Adjusted plant ties"
        )
        CareActivity.PRUNING -> listOf(
            "Removed dead leaves",
            "Removed suckers/side shoots",
            "Thinned overcrowded branches",
            "Removed diseased foliage"
        )
        CareActivity.PEST_CONTROL -> listOf(
            "Applied organic spray (neem/BT)",
            "Applied synthetic pesticide",
            "Manual pest removal (handpicking)",
            "Set traps or barriers",
            "Applied fungicide"
        )
        CareActivity.OTHER -> listOf(
            "General maintenance performed",
            "Cleaned growing area",
            "Adjusted spacing",
            "Other care activity"
        )
    }

    // ─── HARVEST Readiness ──────────────────────────────────────────────────

    fun getHarvestReadinessQuestion(): LogFlowQuestion = LogFlowQuestion(
        question = "Is the crop ready to harvest?",
        choices = listOf(
            LogFlowChoice(
                key = "A",
                label = "Not Ready",
                checkboxes = listOf(
                    "Fruits still developing",
                    "More time needed",
                    "Color not yet mature"
                )
            ),
            LogFlowChoice(
                key = "B",
                label = "Partially Ready",
                checkboxes = listOf(
                    "Some fruits are mature",
                    "Partial harvest possible",
                    "Staggered ripening observed"
                )
            ),
            LogFlowChoice(
                key = "C",
                label = "Ready to Harvest",
                checkboxes = listOf(
                    "Fruits are fully mature",
                    "Color indicates ripeness",
                    "Size is at expected level",
                    "Plant is at harvest stage"
                )
            )
        )
    )

    // ─── Applicable Care Activities per Stage ───────────────────────────────

    /**
     * Returns which care activities are applicable based on the current stage.
     * Not all activities are relevant at every stage.
     */
    fun getApplicableCareActivities(stage: ManagementStage): List<CareActivity> = when (stage) {
        ManagementStage.PREPARATION -> listOf(
            CareActivity.WATERING,
            CareActivity.OTHER
        )
        ManagementStage.PLANTING -> listOf(
            CareActivity.WATERING,
            CareActivity.MULCHING,
            CareActivity.OTHER
        )
        ManagementStage.EARLY_GROWTH -> listOf(
            CareActivity.WATERING,
            CareActivity.WEEDING,
            CareActivity.MULCHING,
            CareActivity.PEST_CONTROL,
            CareActivity.OTHER
        )
        ManagementStage.VEGETATIVE_GROWTH -> listOf(
            CareActivity.WATERING,
            CareActivity.WEEDING,
            CareActivity.FERTILIZING,
            CareActivity.MULCHING,
            CareActivity.STAKING,
            CareActivity.PRUNING,
            CareActivity.PEST_CONTROL,
            CareActivity.OTHER
        )
        ManagementStage.FLOWERING_FRUIT_DEVELOPMENT -> listOf(
            CareActivity.WATERING,
            CareActivity.WEEDING,
            CareActivity.FERTILIZING,
            CareActivity.STAKING,
            CareActivity.PRUNING,
            CareActivity.PEST_CONTROL,
            CareActivity.OTHER
        )
        ManagementStage.HARVEST -> listOf(
            CareActivity.WATERING,
            CareActivity.PEST_CONTROL,
            CareActivity.OTHER
        )
    }

    /**
     * Main entry point: get the question for a given context and stage.
     * @param plantingMethod Required for PLANTING context to differentiate direct seeding vs transplanting.
     */
    fun getQuestion(
        context: LogContext,
        stage: ManagementStage,
        plantingMethod: String = "Direct Seeding"
    ): LogFlowQuestion = when (context) {
        LogContext.PREPARATION -> getPreparationQuestion()
        LogContext.READINESS -> getReadinessQuestion()
        LogContext.PLANTING -> getPlantingQuestion(plantingMethod)
        LogContext.INITIAL_OBSERVATION -> getInitialObservationQuestion()
        LogContext.OBSERVE -> getObserveQuestion(stage)
        LogContext.CARE_MAINTENANCE -> getCareMaintenanceQuestion()
    }
}
