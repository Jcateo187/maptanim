package com.maptanim.app.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room database migrations for MapTanim.
 * Explicitly migrates schema versions without data loss, preserving all user beds, logs, and harvests.
 */
object DatabaseMigrations {

    val MIGRATION_17_18 = object : Migration(17, 18) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `crop_logs` (
                    `id` TEXT NOT NULL,
                    `crop_planting_id` TEXT NOT NULL,
                    `farm_id` TEXT NOT NULL,
                    `bed_id` TEXT NOT NULL,
                    `crop_id` TEXT,
                    `crop_name` TEXT NOT NULL,
                    `variety_id` TEXT,
                    `variety_name` TEXT,
                    `current_stage` TEXT NOT NULL,
                    `log_context` TEXT NOT NULL,
                    `selected_choice` TEXT NOT NULL,
                    `selected_checkboxes` TEXT NOT NULL,
                    `care_activity` TEXT,
                    `notes` TEXT,
                    `date` TEXT NOT NULL,
                    `created_at` TEXT NOT NULL,
                    PRIMARY KEY(`id`)
                )
            """.trimIndent())
        }
    }

    val MIGRATION_18_19 = object : Migration(18, 19) {
        override fun migrate(db: SupportSQLiteDatabase) {
            ensureAllTablesAndColumns(db)
        }
    }

    val MIGRATION_19_20 = object : Migration(19, 20) {
        override fun migrate(db: SupportSQLiteDatabase) {
            ensureAllTablesAndColumns(db)
        }
    }

    val MIGRATION_13_20 = object : Migration(13, 20) {
        override fun migrate(db: SupportSQLiteDatabase) {
            ensureAllTablesAndColumns(db)
        }
    }

    val MIGRATION_14_20 = object : Migration(14, 20) {
        override fun migrate(db: SupportSQLiteDatabase) {
            ensureAllTablesAndColumns(db)
        }
    }

    val MIGRATION_15_20 = object : Migration(15, 20) {
        override fun migrate(db: SupportSQLiteDatabase) {
            ensureAllTablesAndColumns(db)
        }
    }

    val MIGRATION_16_20 = object : Migration(16, 20) {
        override fun migrate(db: SupportSQLiteDatabase) {
            ensureAllTablesAndColumns(db)
        }
    }

    val ALL_MIGRATIONS: Array<Migration> = arrayOf(
        MIGRATION_13_20,
        MIGRATION_14_20,
        MIGRATION_15_20,
        MIGRATION_16_20,
        MIGRATION_17_18,
        MIGRATION_18_19,
        MIGRATION_19_20
    )

    fun ensureAllTablesAndColumns(db: SupportSQLiteDatabase) {
        // 1. Ensure all Room entity tables exist
        db.execSQL("CREATE TABLE IF NOT EXISTS `crop_zones` (`id` TEXT NOT NULL, `plot_id` TEXT NOT NULL, `crop_name` TEXT, `crop_id` TEXT, `offset_x` REAL NOT NULL, `offset_y` REAL NOT NULL, `width_m` REAL NOT NULL, `height_m` REAL NOT NULL, `spacing_m` REAL NOT NULL, `created_at` TEXT NOT NULL, `updated_at` TEXT NOT NULL, PRIMARY KEY(`id`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `crop_plots` (`id` TEXT NOT NULL, `farm_id` TEXT NOT NULL, `plot_label` TEXT NOT NULL, `crop_name` TEXT, `crop_id` TEXT, `crop_variety` TEXT, `soil_type` TEXT NOT NULL, `pos_x` REAL NOT NULL, `pos_y` REAL NOT NULL, `width_m` REAL NOT NULL, `height_m` REAL NOT NULL, `rotation_deg` REAL NOT NULL, `planted_date` TEXT, `is_active` INTEGER NOT NULL, `notes` TEXT, `created_at` TEXT NOT NULL, `updated_at` TEXT NOT NULL, `current_stage` TEXT NOT NULL DEFAULT 'PREPARATION', `harvest_count` INTEGER NOT NULL DEFAULT 0, `total_yield_kg` REAL NOT NULL DEFAULT 0.0, `previous_crops_history` TEXT NOT NULL DEFAULT '', PRIMARY KEY(`id`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `farms` (`id` TEXT NOT NULL, `farmer_id` TEXT NOT NULL, `farm_name` TEXT NOT NULL, `created_at` TEXT NOT NULL, `updated_at` TEXT NOT NULL, PRIMARY KEY(`id`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `crops` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `local_name` TEXT, `botanical_name` TEXT, `category` TEXT NOT NULL, `days_to_harvest` INTEGER NOT NULL, `watering_interval_days` INTEGER NOT NULL, `fertilize_interval_days` INTEGER NOT NULL, `n_ratio` REAL NOT NULL, `p_ratio` REAL NOT NULL, `k_ratio` REAL NOT NULL, `optimal_ph_min` REAL NOT NULL, `optimal_ph_max` REAL NOT NULL, `ideal_soils` TEXT NOT NULL, `suitable_soils` TEXT NOT NULL, `tolerated_soils` TEXT NOT NULL, `pest_risk_season` TEXT NOT NULL, `seasonality` TEXT NOT NULL, `image_url` TEXT, `companion_plants` TEXT NOT NULL, `avoid_plants` TEXT NOT NULL, `common_pests` TEXT NOT NULL, `harvest_indicators` TEXT, `description` TEXT, PRIMARY KEY(`id`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `tasks` (`id` TEXT NOT NULL, `farm_id` TEXT NOT NULL, `plot_id` TEXT NOT NULL, `plot_label` TEXT NOT NULL, `crop_name` TEXT, `task_type` TEXT NOT NULL, `title` TEXT NOT NULL, `sub_label` TEXT, `due_date` TEXT NOT NULL, `is_completed` INTEGER NOT NULL, `completed_at` TEXT, PRIMARY KEY(`id`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `notifications` (`id` TEXT NOT NULL, `user_id` TEXT, `title` TEXT NOT NULL, `body` TEXT, `task_type` TEXT, `notification_type` TEXT NOT NULL, `is_read` INTEGER NOT NULL, `created_at` TEXT NOT NULL, PRIMARY KEY(`id`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `sync_queue` (`id` TEXT NOT NULL, `table_name` TEXT NOT NULL, `record_id` TEXT NOT NULL, `operation` TEXT NOT NULL, `payload` TEXT NOT NULL, `status` TEXT NOT NULL, `attempt_count` INTEGER NOT NULL, `last_attempt_at` TEXT, PRIMARY KEY(`id`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `harvest_records` (`id` TEXT NOT NULL, `zone_id` TEXT NOT NULL, `farm_id` TEXT NOT NULL, `farm_name` TEXT NOT NULL, `plot_label` TEXT NOT NULL, `crop_name` TEXT NOT NULL, `crop_variety` TEXT, `planted_date` TEXT, `harvested_at` TEXT NOT NULL, `growing_duration_days` INTEGER NOT NULL, `yield_kg` REAL NOT NULL, `quality_rating` INTEGER NOT NULL, `notes` TEXT, `harvest_method` TEXT, `quantity` REAL NOT NULL DEFAULT 0, `unit` TEXT NOT NULL DEFAULT 'kg', `marketable_pct` REAL, `crop_planting_id` TEXT, `is_final_harvest` INTEGER NOT NULL DEFAULT 1, PRIMARY KEY(`id`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `activities` (`id` TEXT NOT NULL, `plot_id` TEXT NOT NULL, `farm_id` TEXT NOT NULL, `type` TEXT NOT NULL, `notes` TEXT, `performed_at` TEXT NOT NULL, PRIMARY KEY(`id`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `dss_rules` (`id` TEXT NOT NULL, `crop_a` TEXT NOT NULL, `crop_b` TEXT NOT NULL, `relationship` TEXT NOT NULL, `reason` TEXT, `source` TEXT, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `dss_cached_decisions` (`id` TEXT NOT NULL, `farm_id` TEXT NOT NULL, `plot_id` TEXT, `plot_label` TEXT, `crop_name` TEXT, `decision_type` TEXT NOT NULL, `category` TEXT NOT NULL, `priority` TEXT NOT NULL, `title` TEXT NOT NULL, `summary` TEXT NOT NULL, `explanation` TEXT NOT NULL, `source` TEXT NOT NULL, `action_text` TEXT, `action_task_type` TEXT, `rule_id` TEXT, `is_actionable` INTEGER NOT NULL, `is_completed` INTEGER NOT NULL, `evaluated_at` TEXT NOT NULL, `cached_at` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `policy_consents` (`consentKey` TEXT NOT NULL, `isAccepted` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`consentKey`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `crop_yield_studies` (`id` TEXT NOT NULL, `crop_name` TEXT NOT NULL, `variety_name` TEXT NOT NULL, `study_code` TEXT NOT NULL, `study_title` TEXT NOT NULL, `location` TEXT NOT NULL, `study_period` TEXT NOT NULL, `cultivation_condition` TEXT NOT NULL, `treatment_description` TEXT NOT NULL, `baseline_yield_t_per_ha` REAL, `reported_yield_t_per_ha` REAL NOT NULL, `yield_increase_percent` REAL, `key_findings` TEXT NOT NULL, `authors` TEXT NOT NULL, `institution` TEXT NOT NULL, `publication_reference` TEXT NOT NULL, PRIMARY KEY(`id`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `crop_varieties` (`id` TEXT NOT NULL, `crop_name` TEXT NOT NULL, `variety_name` TEXT NOT NULL, `local_name_ph` TEXT, `breeder_organization` TEXT, `growth_duration_days` INTEGER NOT NULL, `stage1_sprout_days` INTEGER NOT NULL, `stage2_seedling_days` INTEGER NOT NULL, `stage3_vegetative_days` INTEGER NOT NULL, `stage4_flowering_days` INTEGER NOT NULL, `stage5_harvest_days` INTEGER NOT NULL, `watering_interval_days` INTEGER NOT NULL, `fertilize_interval_days` INTEGER NOT NULL, `optimal_seasons_csv` TEXT NOT NULL, `disease_resistance` TEXT, `description` TEXT, `source_citation` TEXT NOT NULL, PRIMARY KEY(`id`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `crop_growth_stages` (`id` TEXT NOT NULL, `crop_name` TEXT NOT NULL, `stage_index` INTEGER NOT NULL, `stage_name` TEXT NOT NULL, `day_start` INTEGER NOT NULL, `day_end` INTEGER NOT NULL, `primary_farmer_action` TEXT NOT NULL, `care_recommendation` TEXT NOT NULL, `irrigation_advice` TEXT NOT NULL, `nutrition_advice` TEXT NOT NULL, `critical_risks` TEXT NOT NULL, `source_citation` TEXT NOT NULL, PRIMARY KEY(`id`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `crop_soil_compatibilities` (`id` TEXT NOT NULL, `crop_name` TEXT NOT NULL, `soil_type` TEXT NOT NULL, `suitability_rating` TEXT NOT NULL, `suitability_score` REAL NOT NULL, `agronomic_rationale` TEXT NOT NULL, `amendment_action` TEXT, `alert_warning` TEXT, `source_citation` TEXT NOT NULL, PRIMARY KEY(`id`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `crop_pest_disease_guides` (`id` TEXT NOT NULL, `crop_name` TEXT NOT NULL, `pest_disease_name` TEXT NOT NULL, `local_name_ph` TEXT, `scientific_name` TEXT, `category` TEXT NOT NULL, `risk_season` TEXT NOT NULL, `critical_stage_index` INTEGER, `symptoms` TEXT NOT NULL, `organic_biocontrol` TEXT NOT NULL, `cultural_prevention` TEXT NOT NULL, `source_citation` TEXT NOT NULL, PRIMARY KEY(`id`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `crop_logs` (`id` TEXT NOT NULL, `crop_planting_id` TEXT NOT NULL, `farm_id` TEXT NOT NULL, `bed_id` TEXT NOT NULL, `crop_id` TEXT, `crop_name` TEXT NOT NULL, `variety_id` TEXT, `variety_name` TEXT, `current_stage` TEXT NOT NULL, `log_context` TEXT NOT NULL, `selected_choice` TEXT NOT NULL, `selected_checkboxes` TEXT NOT NULL, `care_activity` TEXT, `notes` TEXT, `date` TEXT NOT NULL, `created_at` TEXT NOT NULL, PRIMARY KEY(`id`))")

        // 2. Ensure columns on existing tables
        ensureColumn(db, "crop_plots", "current_stage", "TEXT NOT NULL DEFAULT 'PREPARATION'")
        ensureColumn(db, "crop_plots", "harvest_count", "INTEGER NOT NULL DEFAULT 0")
        ensureColumn(db, "crop_plots", "total_yield_kg", "REAL NOT NULL DEFAULT 0.0")
        ensureColumn(db, "crop_plots", "previous_crops_history", "TEXT NOT NULL DEFAULT ''")
        ensureColumn(db, "crop_plots", "crop_variety", "TEXT")

        ensureColumn(db, "harvest_records", "is_final_harvest", "INTEGER NOT NULL DEFAULT 1")
        ensureColumn(db, "harvest_records", "crop_planting_id", "TEXT")
        ensureColumn(db, "harvest_records", "harvest_method", "TEXT")
        ensureColumn(db, "harvest_records", "quantity", "REAL NOT NULL DEFAULT 0")
        ensureColumn(db, "harvest_records", "unit", "TEXT NOT NULL DEFAULT 'kg'")
        ensureColumn(db, "harvest_records", "marketable_pct", "REAL")
    }

    private fun ensureColumn(db: SupportSQLiteDatabase, table: String, column: String, colDef: String) {
        val tableCheck = db.query("SELECT name FROM sqlite_master WHERE type='table' AND name=?", arrayOf(table))
        val tableExists = tableCheck.moveToFirst()
        tableCheck.close()
        if (!tableExists) return

        val cursor = db.query("PRAGMA table_info(`$table`)")
        var columnExists = false
        val nameIndex = cursor.getColumnIndex("name")
        while (cursor.moveToNext()) {
            if (nameIndex != -1 && cursor.getString(nameIndex).equals(column, ignoreCase = true)) {
                columnExists = true
                break
            }
        }
        cursor.close()

        if (!columnExists) {
            db.execSQL("ALTER TABLE `$table` ADD COLUMN `$column` $colDef")
        }
    }
}
