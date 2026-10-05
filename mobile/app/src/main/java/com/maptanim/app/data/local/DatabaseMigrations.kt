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
            ensureColumn(db, "harvest_records", "is_final_harvest", "INTEGER NOT NULL DEFAULT 1")
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
            ensureColumn(db, "harvest_records", "is_final_harvest", "INTEGER NOT NULL DEFAULT 1")
            ensureColumn(db, "harvest_records", "crop_planting_id", "TEXT")
            ensureColumn(db, "harvest_records", "harvest_method", "TEXT")
            ensureColumn(db, "harvest_records", "quantity", "REAL NOT NULL DEFAULT 0")
            ensureColumn(db, "harvest_records", "unit", "TEXT NOT NULL DEFAULT 'kg'")
            ensureColumn(db, "harvest_records", "marketable_pct", "REAL")
        }
    }

    val ALL_MIGRATIONS: Array<Migration> = arrayOf(
        MIGRATION_17_18,
        MIGRATION_18_19,
        MIGRATION_19_20
    )

    private fun ensureColumn(db: SupportSQLiteDatabase, table: String, column: String, colDef: String) {
        // 1. Verify table exists first to avoid SQLiteException
        val tableCheck = db.query("SELECT name FROM sqlite_master WHERE type='table' AND name=?", arrayOf(table))
        val tableExists = tableCheck.moveToFirst()
        tableCheck.close()
        if (!tableExists) return

        // 2. Check if column already exists
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

        // 3. Add column if absent
        if (!columnExists) {
            db.execSQL("ALTER TABLE `$table` ADD COLUMN `$column` $colDef")
        }
    }
}
