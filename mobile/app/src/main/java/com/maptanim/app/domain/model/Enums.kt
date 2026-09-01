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
    SPROUT,             // Stage 1: Germination / Emergence (0–15% progress)
    SEEDLING,           // Stage 2: Early Leaf Development (15–35% progress)
    VEGETATIVE,         // Stage 3: Rapid Stem & Leaf Expansion (35–65% progress)
    FLOWERING,          // Stage 4: Budding / Podding / Fruiting (65–90% progress)
    HARVEST_READY,      // Stage 5: Full Maturation (90%+ progress)
    GERMINATION,        // Legacy alias for SPROUT
    EARLY_VEGETATIVE,   // Legacy alias for SEEDLING
    MID_VEGETATIVE,     // Legacy alias for VEGETATIVE
    FRUITING,           // Legacy alias for FLOWERING
    OVERDUE             // Legacy alias for HARVEST_READY
}

/**
 * Edit tool enum. Active tool drives left panel highlight and canvas gesture behavior.
 * Persisted in EditViewModel.uiState — not in Room (session only).
 */
enum class EditTool {
    SELECT_MOVE,    // Default — tap to select, drag handle to move
    ADD_PLOT,       // Tap empty space → place crop plot
    ADD_PLANT,      // Tap plot → select & add plant/crop
    DELETE          // Tap plot → confirm dialog → soft-delete
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

/**
 * Isometric tile lifecycle status for farm_tiles.status column.
 * Tracks the state of each tile cell in the 45×45 grid.
 */
enum class TileStatus {
    EMPTY,              // No crop assigned, tile is available
    PLANTED,            // Crop just placed via drag-drop
    GROWING,            // Crop is actively growing (stages 1–5)
    READY_TO_HARVEST,   // Crop reached HARVEST stage
    HARVESTED,          // Crop has been harvested, yield recorded
    FALLOW              // Resting period after harvest
}

/**
 * 6-stage crop growth lifecycle per AGENTS.md specification.
 * Each crop has configurable durations per stage stored in
 * crop_profiles.growth_stage_durations (JSONB).
 */
enum class CropGrowthStage {
    GERMINATION,   // Stage 1: Seed emergence (0–15% progress)
    SEEDLING,      // Stage 2: Early leaf development (15–30% progress)
    VEGETATIVE,    // Stage 3: Rapid stem & leaf expansion (30–55% progress)
    FLOWERING,     // Stage 4: Budding / podding / fruiting (55–75% progress)
    RIPENING,      // Stage 5: Fruit/tuber maturation (75–95% progress)
    HARVEST        // Stage 6: Full maturity, ready to pick (95%+ progress)
}

