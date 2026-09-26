package com.maptanim.app.dss.model

import com.maptanim.app.domain.model.Activity
import com.maptanim.app.domain.model.Crop
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.CropZone
import com.maptanim.app.domain.model.Farm
import com.maptanim.app.domain.model.HarvestRecord
import com.maptanim.app.domain.model.PestGuide
import com.maptanim.app.domain.model.TaskType
import com.maptanim.app.dss.engine.DssRule
import java.time.LocalDate

// ─── 1. DSS Working Session ──────────────────────────────────────────────────

/**
 * Temporary working context created when a farmer opens the DSS or triggers an evaluation.
 * Tracks the session ID, target farm, timestamp, and evaluation metadata.
 */
data class DssSession(
    val sessionId: String,
    val farmId: String,
    val farmName: String,
    val startedAt: String,
    val engineVersion: String = "2.0.0-DA-BPI"
)

// ─── 2. Farmer Actual Data Context ──────────────────────────────────────────

/**
 * Encapsulates all real-time farmer data gathered through repositories
 * for the target farm context.
 */
data class DssFarmerData(
    val farm: Farm?,
    val plots: List<CropPlot>,
    val cropZones: List<CropZone> = emptyList(),
    val monitors: List<com.maptanim.app.data.local.entity.PlantingMonitorEntity> = emptyList(),
    val harvestHistory: List<HarvestRecord> = emptyList(),
    val recentActivities: List<Activity> = emptyList(),
    val currentDate: LocalDate = LocalDate.now()
)

// ─── 3. Agricultural Reference Data Context ─────────────────────────────────

/**
 * Encapsulates verified agricultural reference data gathered through repositories
 * (DA-BPI, DA-BAR, PCARRD standards).
 */
data class DssReferenceData(
    val crops: List<Crop>,
    val companionRules: List<DssRule>,
    val pestGuides: List<PestGuide> = emptyList(),
    val documentedRules: List<com.maptanim.app.dss.rules.DssDocumentedRule> = emptyList()
)

// ─── 4. Validation Issues (Transparent Data Limitations) ─────────────────────

enum class IssueSeverity {
    WARNING,
    INFO,
    MISSING_REQUIRED
}

/**
 * Transparently records when required farmer data or agricultural relationships
 * are missing, preventing the engine from inventing values.
 */
data class DssValidationIssue(
    val plotId: String?,
    val plotLabel: String?,
    val fieldName: String,
    val message: String,
    val severity: IssueSeverity = IssueSeverity.WARNING
)

// ─── 5. Unified DSS Input ───────────────────────────────────────────────────

/**
 * Standardized input payload built for the DSS Engine, combining farmer reality
 * with agricultural scientific reference data.
 */
data class DssInput(
    val session: DssSession,
    val farmerData: DssFarmerData,
    val referenceData: DssReferenceData,
    val validationIssues: List<DssValidationIssue> = emptyList()
)

// ─── 6. Decision Types, Categories, Priorities ──────────────────────────────

enum class DssDecisionType {
    RECOMMENDATION,   // Agronomic guidance & best practices
    ALERT,            // Warning of risk (antagonist plants, pest window, moisture stress)
    TASK,             // Immediate or scheduled physical action required
    INSUFFICIENT_INFO // Declared data gap where farmer input is needed
}

enum class DssCategory {
    SEASON_WINDOW,          // Seasonality & planting calendar
    SOIL_COMPATIBILITY,     // Soil suitability, pH & amendments
    GROWTH_CARE,            // Irrigation, fertilization & weeding intervals
    PEST_DISEASE,           // Pest risk, symptom monitoring & organic controls
    HARVEST_READINESS,      // Maturity index & harvest timing
    COMPANION_INTERCROPPING,// Spatial companion synergy & antagonist isolation
    CROP_ROTATION_FALLOW,   // Botanical family rotation & soil resting
    NUTRIENT_WATER          // Water & nutrition balancing
}

enum class DssPriority(val rank: Int) {
    CRITICAL(1), // Urgent intervention needed (e.g. active pest damage, antagonist planting)
    HIGH(2),     // Immediate care needed (e.g. harvest ready, overdue irrigation)
    MEDIUM(3),   // Standard scheduled care (e.g. planned fertilization, weeding)
    LOW(4),      // Optimization guidance (e.g. future companion planning)
    INFO(5)      // Contextual educational insight
}

// ─── 7. Final DSS Decision Item ─────────────────────────────────────────────

/**
 * A single research-based decision result with full explanation and documented source.
 */
data class DssDecision(
    val id: String,
    val farmId: String,
    val plotId: String? = null,
    val plotLabel: String? = null,
    val cropName: String? = null,
    val decisionType: DssDecisionType,
    val category: DssCategory,
    val priority: DssPriority,
    val title: String,
    val summary: String,
    val explanation: String,
    val source: String,
    val actionText: String? = null,
    val actionTaskType: TaskType? = null,
    val ruleId: String? = null,
    val isActionable: Boolean = true,
    val isCompleted: Boolean = false,
    val evaluatedAt: String = ""
)

// ─── 8. DSS Result & Evaluation Summary ─────────────────────────────────────

data class DssResultSummary(
    val totalDecisions: Int = 0,
    val criticalAlerts: Int = 0,
    val highPriorityCount: Int = 0,
    val recommendationsCount: Int = 0,
    val tasksCount: Int = 0,
    val monitoredPlotsCount: Int = 0
)

/**
 * The complete output returned by the DSS Use Case to the ViewModel.
 */
data class DssResult(
    val session: DssSession,
    val farmId: String,
    val farmName: String,
    val decisions: List<DssDecision>,
    val insufficientDataNotices: List<DssValidationIssue>,
    val summary: DssResultSummary,
    val evaluatedAt: String
)
