package com.maptanim.app.domain.model

import com.maptanim.app.domain.model.SoilType

// ─── Farm Environment & Agro-Zone ──────────────────────────────────────────

enum class AgroZone(val label: String, val description: String, val elevationRange: String) {
    LOWLAND("Lowland", "Warm tropical climate (0–500m elevation). Thrives with warm-season fruiting vegetables.", "0 – 500 m"),
    HIGHLAND("Highland", "Cool temperate microclimate (>500m elevation). Ideal for brassicas and root crops.", "> 500 m");

    companion object {
        fun fromName(name: String?): AgroZone =
            entries.firstOrNull { it.name.equals(name, ignoreCase = true) || it.label.equals(name, ignoreCase = true) } ?: LOWLAND
    }
}

enum class SiteConstraint(val code: String, val label: String, val description: String) {
    FLOODING("fl", "Flooding / Poor Drainage", "Prone to water pooling during tropical downpours; requires elevated beds."),
    WIND_EXPOSURE("wi", "Strong Wind Exposure", "Open or gusty site; tall and trellised plants require sturdy staking."),
    SHADY("sh", "Shady (< 5 hrs sun)", "Limited direct sunlight; fruiting crops need full sun or reflective mulch."),
    WATER_SCARCITY("nw", "Water Scarcity", "Limited tap or well water access; requires heavy mulching or drip bottles."),
    CHICKENS_ANIMALS("an", "Chickens & Stray Animals", "Free-range chickens or dogs present; requires thorny branch barriers or stick cages."),
    SLOPING_WELL_DRAINED("sl", "Sloping / No Stagnant Flood", "Natural slope with good runoff; reduced root-rot risk.");

    companion object {
        fun fromCode(code: String?): SiteConstraint? =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) || it.name.equals(code, ignoreCase = true) }
    }
}

data class FarmEnvironment(
    val zone: AgroZone = AgroZone.LOWLAND,
    val defaultSoil: SoilType = SoilType.LOAM,
    val widthM: Float = 10f,
    val heightM: Float = 8f,
    val constraints: Set<SiteConstraint> = emptySet(),
    val season: String = "Wet / Rainy Season",
    val availableMaterials: Set<String> = emptySet(),
    val locationName: String = ""
) {
    val areaSqM: Float get() = widthM * heightM
    val stepPacingEstimate: String get() {
        val stepsW = kotlin.math.round(widthM / 0.85f).toInt()
        val stepsH = kotlin.math.round(heightM / 0.85f).toInt()
        return "≈ $stepsW × $stepsH steps (1 step ≈ 0.85m)"
    }

    val yardPresetComparisonText: String get() {
        return when {
            areaSqM <= 24f -> "Compact Yard (6m × 4m preset / 24 m²)"
            areaSqM <= 96f -> "Medium Backyard (12m × 8m preset / 96 m²)"
            areaSqM <= 216f -> "Spacious Lot (18m × 12m preset / 216 m²)"
            else -> "Expansive Rural Lot (${String.format("%.1f", areaSqM)} m²)"
        }
    }
}

// ─── Farm ──────────────────────────────────────────────────────────────────

data class Farm(
    val id: String,
    val farmerId: String,
    val farmName: String,      // e.g. "Murcia Farm"
    val createdAt: String,
    val updatedAt: String,
    val environment: FarmEnvironment = FarmEnvironment()
)



// ─── CropPlot ─────────────────────────────────────────────────────────────

data class CropPlot(
    val id: String,
    val farmId: String,
    val plotLabel: String,      // "PLOT 1", "PLOT A", etc.
    val cropName: String?,      // null = no crop assigned
    val cropId: String?,
    val cropVariety: String? = null,
    val soilType: SoilType,     // loaded from crop_plots.soil_type via Room
    val posX: Float,            // meters from farm origin
    val posY: Float,
    val widthM: Float,
    val heightM: Float,
    val rotationDeg: Float = 0f,
    val plantedDate: String? = null,   // ISO-8601 date string, null if not planted
    val isActive: Boolean = true,
    val notes: String? = null,
    val createdAt: String = "",
    val updatedAt: String = "",
    val currentStage: ManagementStage = ManagementStage.PREPARATION,
    val harvestCount: Int = 0,
    val totalYieldKg: Float = 0f,
    val previousCropsHistory: List<String> = emptyList()
)

// ─── Crop (reference data from crops table) ────────────────────────────────

data class Crop(
    val id: String,
    val name: String,                    // "Tomato"
    val localName: String?,             // "Kamatis"
    val botanicalName: String?,
    val category: String,               // DA/PSA 8-category classification
    val daysToHarvest: Int,
    val wateringIntervalDays: Int,
    val fertilizeIntervalDays: Int,
    val nRatio: Float,
    val pRatio: Float,
    val kRatio: Float,
    val optimalPhMin: Float,
    val optimalPhMax: Float,
    val idealSoils: List<SoilType>,
    val suitableSoils: List<SoilType>,
    val toleratedSoils: List<SoilType>,
    val pestRiskSeason: List<String>,   // ["WET", "DRY"]
    val seasonality: List<String>,      // ["DRY", "WET", "YEAR_ROUND"]
    val imageUrl: String?,              // Supabase Storage public URL
    val companionPlants: List<String> = emptyList(),
    val avoidPlants: List<String> = emptyList(),
    val commonPests: List<String> = emptyList(),
    val harvestIndicators: String? = null,
    val description: String? = null
)

// ─── Pest & Disease Guide ──────────────────────────────────────────────────

data class PestGuide(
    val id: String,
    val name: String,                    // e.g. "Fruit Borer"
    val localName: String,               // e.g. "Ubod ng Kamatis / Harabas"
    val scientificName: String,          // e.g. "Helicoverpa armigera"
    val affectedCrops: List<String>,     // ["Tomato", "Eggplant", "Corn"]
    val category: String,                // "Insect Pest", "Fungal Disease", "Viral Disease"
    val organicControl: String,          // Neem oil, BT spray, handpicking
    val chemicalControl: String,         // Recommended DA pesticide if severe
    val preventionTips: String,          // Crop rotation, weed clearing
    val imageUrl: String? = null
)

// ─── Soil Type Guide ───────────────────────────────────────────────────────

data class SoilGuide(
    val soilType: SoilType,
    val title: String,                   // "Loam Soil"
    val localName: String,               // "Lupang Luto / Loam"
    val description: String,
    val characteristics: String,
    val drainageSpeed: String,           // "Moderate / Ideal"
    val phRange: String,                 // "6.0 – 7.0"
    val texture: String,                 // "Crumbly, rich in organic matter"
    val bestCrops: List<String>,         // ["Tomato", "Carrot", "Lettuce", "Onion"]
    val imageUrl: String? = null,
    val colorHex: String? = null
)

// ─── Seasonal Planting Matrix Info ────────────────────────────────────────

data class SeasonalWindowInfo(
    val cropName: String,
    val localName: String,
    val drySeasonStatus: String,         // "OPTIMAL", "ACCEPTABLE", "HIGH_RISK"
    val wetSeasonStatus: String,         // "OPTIMAL", "ACCEPTABLE", "HIGH_RISK"
    val peakMonths: String,              // "Nov – Feb"
    val notes: String
)


// ─── FarmTask (generated by DSS, stored in tasks table) ───────────────────

data class FarmTask(
    val id: String,
    val farmId: String,
    val plotId: String,
    val plotLabel: String,      // denormalized from crop_plots.plot_label
    val cropName: String?,      // denormalized from crop_plots.crop_name
    val taskType: TaskType,
    val title: String,          // "Water Plot 3"
    val subLabel: String?,      // "Tomato"
    val dueDate: String,        // ISO-8601 date
    val isCompleted: Boolean,
    val completedAt: String?
)

// ─── FarmSummary (derived from plots + tasks via UseCases) ────────────────

data class FarmSummary(
    val totalPlots: Int = 0,
    val totalPlants: Int = 0,
    val readyToHarvest: Int = 0,
    val activeAlerts: Int = 0
)

// ─── HarvestRecord ─────────────────────────────────────────────────────────

data class HarvestRecord(
    val id: String,
    val plotId: String,
    val farmId: String,
    val farmName: String = "My Farm",
    val plotLabel: String = "Plot 1",
    val cropName: String,
    val cropVariety: String? = null,
    val plantedDate: String? = null,
    val harvestedAt: String,
    val growingDurationDays: Int = 0,
    val yieldKg: Float = 0f,
    val qualityRating: Int = 5,   // 1–5
    val notes: String? = null,
    // New fields from Crop Management flow spec
    val harvestMethod: String? = null,     // e.g. "Manual Pick", "Cut at Base"
    val quantity: Float = 0f,              // Numeric quantity harvested
    val unit: String = "kg",               // "kg", "pcs", "bundles", "sacks"
    val marketablePct: Float? = null,      // Optional: % marketable
    val cropPlantingId: String? = null,     // Links to crop_planting for lifecycle tracking
    val isFinalHarvest: Boolean = true      // False for continuous multi-pick harvests (e.g. Tomato, Okra, Eggplant)
)

// ─── Activity (farmer manual log) ─────────────────────────────────────────

data class Activity(
    val id: String,
    val plotId: String,
    val farmId: String,
    val type: TaskType,
    val notes: String?,
    val performedAt: String
)

// ─── CropLog (crop management observation / action log) ───────────────────

/**
 * Represents a single log entry in the Crop Management flow.
 * Created when the farmer submits a log via the "Add Log" dialog.
 *
 * Flow: STAGE → LOG_CONTEXT → QUESTION → CHOICE (A/B/C) → CHECKBOXES → SUBMIT
 *
 * After submission, this log is sent to the DSS engine for evaluation
 * which may update tasks, recommendations, alerts, and trigger stage progression.
 */
data class CropLog(
    val id: String,
    val cropPlantingId: String,      // Links to crop_planting (plot/zone ID)
    val farmId: String = "",
    val bedId: String = "",
    val cropId: String? = null,
    val cropName: String = "",
    val varietyId: String? = null,
    val varietyName: String? = null,
    val currentStage: ManagementStage = ManagementStage.PREPARATION,
    val logContext: LogContext = LogContext.PREPARATION,
    val selectedChoice: String = "A",       // "A", "B", or "C"
    val selectedCheckboxes: List<String> = emptyList(),  // List of checkbox labels that were checked
    val careActivity: CareActivity? = null, // Only for CARE_MAINTENANCE context
    val notes: String? = null,
    val date: String = "",                 // ISO-8601 date (YYYY-MM-DD)
    val createdAt: String = ""             // ISO-8601 datetime
)

// ─── Notification ──────────────────────────────────────────────────────────

data class Notification(
    val id: String,
    val userId: String,
    val title: String,
    val body: String?,
    val taskType: TaskType?,
    val isRead: Boolean,
    val createdAt: String
)

// ─── Profile ──────────────────────────────────────────────────────────────

data class Profile(
    val id: String,             // equals auth.uid()
    val email: String,
    val nickname: String,
    val avatarUrl: String?,     // Supabase Storage user-avatars/{userId}/avatar.jpg
    val role: UserRole,
    val createdAt: String
)

// ─── CropZone (sub-region of a CropPlot) ──────────────────────────────────

data class CropZone(
    val id: String,
    val plotId: String,
    val cropName: String?,          // null = empty zone (shows + placeholder)
    val cropId: String?,
    val offsetX: Float,             // meters from plot origin (top-left)
    val offsetY: Float,
    val widthM: Float,              // zone width in meters
    val heightM: Float,             // zone height in meters
    val spacingM: Float = 0.3f,     // plant-to-plant spacing in meters
    val createdAt: String = "",
    val updatedAt: String = ""
)

// ─── PlantInstance (computed at runtime, not persisted) ───────────────────

data class PlantInstance(
    val worldX: Float,              // absolute world position in meters
    val worldY: Float,
    val scaleFactor: Float,         // proportional to zone area
    val cropName: String
)

// ─── Farmer Community & Forum Models ──────────────────────────────────────

data class CommunityPost(
    val id: String,
    val authorId: String? = null,
    val authorName: String,
    val authorAvatarUrl: String? = null,
    val category: String,                // "PEST_ALERT", "FARMING_TIP", "EQUIPMENT", "GENERAL"
    val title: String,
    val content: String,
    val imageUrl: String? = null,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val timestamp: String = "Just now",               // e.g. "2 hours ago"
    val isLikedByMe: Boolean = false,
    val tags: List<String> = emptyList()
)

data class CommunityComment(
    val id: String,
    val postId: String,
    val authorName: String,
    val content: String,
    val timestamp: String
)

data class CommunityReport(
    val id: String,
    val reporterName: String,
    val targetType: String,             // "POST", "USER", "COMMENT"
    val targetId: String,
    val targetName: String,
    val targetContent: String? = null,
    val reason: String,
    val details: String? = null,
    val status: String = "PENDING",     // "PENDING", "INVESTIGATING", "RESOLVED", "DISMISSED"
    val createdAt: String = "Just now"
)
