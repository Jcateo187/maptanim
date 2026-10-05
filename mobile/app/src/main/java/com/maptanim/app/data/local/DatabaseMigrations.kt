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
            ensureColumn(db, "crop_plots", "current_stage", "TEXT NOT NULL DEFAULT 'PREPARATION'")
            ensureColumn(db, "crop_plots", "harvest_count", "INTEGER NOT NULL DEFAULT 0")
            ensureColumn(db, "crop_plots", "total_yield_kg", "REAL NOT NULL DEFAULT 0.0")
            ensureColumn(db, "crop_plots", "previous_crops_history", "TEXT NOT NULL DEFAULT ''")
            ensureColumn(db, "harvests", "is_final_harvest", "INTEGER NOT NULL DEFAULT 1")
        }
    }

    val MIGRATION_19_20 = object : Migration(19, 20) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Version 20: Explicit schema validation & ALTER TABLE statements
            // Preserves user beds, logs, and harvests across schema transitions
            ensureColumn(db, "crop_plots", "current_stage", "TEXT NOT NULL DEFAULT 'PREPARATION'")
            ensureColumn(db, "crop_plots", "harvest_count", "INTEGER NOT NULL DEFAULT 0")
            ensureColumn(db, "crop_plots", "total_yield_kg", "REAL NOT NULL DEFAULT 0.0")
            ensureColumn(db, "crop_plots", "previous_crops_history", "TEXT NOT NULL DEFAULT ''")
            ensureColumn(db, "crop_plots", "crop_variety", "TEXT")
            ensureColumn(db, "harvests", "is_final_harvest", "INTEGER NOT NULL DEFAULT 1")
            ensureColumn(db, "harvests", "crop_planting_id", "TEXT")
        }
    }

    val ALL_MIGRATIONS: Array<Migration> = arrayOf(
        MIGRATION_17_18,
        MIGRATION_18_19,
        MIGRATION_19_20
    )

    private fun ensureColumn(db: SupportSQLiteDatabase, table: String, column: String, colDef: String) {
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
