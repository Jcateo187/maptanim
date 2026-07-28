package com.maptanim.app.domain.model

/**
 * DA / PSA Standard 8-category vegetable classification.
 * Used in the Library screen crop catalog and DSS rule engine.
 */
enum class VegetableCategory {
    BULB,     // Onion, Garlic
    STEM,     // Celery, Asparagus
    SHOOT,    // Labong (Bamboo Shoots), Bean Sprouts
    LEAFY,    // Pechay, Kangkong, Lettuce
    FLOWER,   // Broccoli, Cauliflower
    FRUIT,    // Tomato, Eggplant, Squash, Okra, Cucumber, Pepper, Corn
    ROOT,     // Carrot, Radish
    TUBER     // Potato, Sweet Potato
}

/**
 * 6 soil classification types used in bed data and the DSS soil-crop suitability matrix.
 * Value matches beds.soil_type column in Supabase (uppercase string).
 */
enum class SoilType {
    LOAM,    // Dark reddish-brown, high fertility — ideal for most crops
    CLAY,    // Orange-brown, heavy, water-retentive
    SANDY,   // Light tan, well-draining, low fertility
    SILTY,   // Blue-gray, smooth, moderate fertility
    PEATY,   // Near-black, high organic matter, acidic
    CHALKY   // Off-white, alkaline, low water retention
}

/**
 * Growth stage of a planting bed, calculated from:
 *   beds.planted_date (Room DB) + crops.days_to_harvest (Room DB)
 * by GrowthStageCalculator. Never hardcoded.
 */
enum class GrowthStage {
    GERMINATION,        // Days 0–7 from planting
    EARLY_VEGETATIVE,   // Days 8–21
    MID_VEGETATIVE,     // Days 22–35
    FLOWERING,          // Days 36–50
    FRUITING,           // Days 51 to (days_to_harvest - 1)
    HARVEST_READY,      // days_to_harvest reached
    OVERDUE             // 7+ days past harvest window
}

/**
 * Edit tool enum. Active tool drives left panel highlight and canvas gesture behavior.
 * Persisted in EditViewModel.uiState — not in Room (session only).
 */
enum class EditTool {
    SELECT_MOVE,    // Default — tap to select, drag handle to move
    ADD_BED,        // Tap empty space → place bed
    ADD_PLANT,      // Tap bed → select & add plant/crop
    DELETE          // Tap bed → confirm dialog → soft-delete
}

/**
 * Task/notification type. Determines pin color, icon, and task row icon in the UI.
 * Value stored in tasks.task_type and notifications.task_type columns.
 */
enum class TaskType {
    WATER,           // Blue — watering overdue
    FERTILIZE,       // Green — fertilization due
    HARVEST,         // Amber — harvest window reached
    PEST_ALERT,      // Red — pest risk for crop/season
    APPLY_PESTICIDE  // Orange — follow-up after pest alert
}

/**
 * Canvas rendering mode. Drives top bar content, left panel, bottom bar, and right toolbar.
 */
enum class CanvasMode {
    VIEW,   // View Mode: status pins visible, TODAY'S TASKS + FARM SUMMARY in left panel
    EDIT    // Edit Mode: handles visible, EDIT TOOLS in left panel, EditBottomBar shown
}

/**
 * Companion planting relationship between two crops.
 * Stored in dss_rules table, loaded into Room.
 */
enum class CompanionRelation {
    BENEFICIAL,  // Plants help each other — encouraged to be adjacent
    NEUTRAL,     // No significant interaction
    ANTAGONIST   // Plants harm each other — flagged in DSS alerts
}

/**
 * User role — determines access levels in Supabase RLS and admin dashboard.
 */
enum class UserRole {
    FARMER,        // Standard authenticated user
    GUEST,         // Unauthenticated — Room-only, no cloud sync
    ADMINISTRATOR  // Web admin panel access only
}

/**
 * Seasonal window for crop scheduling and DSS pest rules.
 */
enum class Season {
    DRY,       // October – April (Philippines)
    WET,       // May – September (Philippines)
    YEAR_ROUND // No seasonal restriction
}

/**
 * Types of external farm structures, boundaries, and decorations.
 */
enum class FarmObjectType {
    TRELLIS,       // Wooden A-frame climbing support
    FENCE_SEGMENT, // Perimeter wooden/wire fence segment
    TREE,          // Exterior tree/shrub element
    DECORATION     // Decorative stone, sign, or container
}

