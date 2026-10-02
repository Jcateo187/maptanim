package com.maptanim.app.data.local.dao

import androidx.room.*
import com.maptanim.app.data.local.entity.CropLogEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for crop management logs (crop_logs table).
 * Provides queries needed by the Crop Management flow and DSS engine.
 */
@Dao
interface CropLogDao {

    /** All logs for a specific crop planting, newest first. */
    @Query("SELECT * FROM crop_logs WHERE crop_planting_id = :cropPlantingId ORDER BY created_at DESC")
    fun observeLogsForPlanting(cropPlantingId: String): Flow<List<CropLogEntity>>

    /** All logs for a specific crop planting (non-reactive, for DSS evaluation). */
    @Query("SELECT * FROM crop_logs WHERE crop_planting_id = :cropPlantingId ORDER BY created_at ASC")
    fun getLogsForPlanting(cropPlantingId: String): List<CropLogEntity>

    /** All logs in a specific bed. */
    @Query("SELECT * FROM crop_logs WHERE bed_id = :bedId ORDER BY created_at DESC")
    fun observeLogsForBed(bedId: String): Flow<List<CropLogEntity>>

    /** All logs for a farm, newest first. */
    @Query("SELECT * FROM crop_logs WHERE farm_id = :farmId ORDER BY created_at DESC")
    fun observeLogsForFarm(farmId: String): Flow<List<CropLogEntity>>

    /** Count of logs per stage for a planting (for stage progression checks). */
    @Query("SELECT COUNT(*) FROM crop_logs WHERE crop_planting_id = :cropPlantingId AND current_stage = :stage")
    fun countLogsForStage(cropPlantingId: String, stage: String): Int

    /** Most recent log for a planting (to determine current state). */
    @Query("SELECT * FROM crop_logs WHERE crop_planting_id = :cropPlantingId ORDER BY created_at DESC LIMIT 1")
    fun getLatestLog(cropPlantingId: String): CropLogEntity?

    /** Logs filtered by context type for a planting. */
    @Query("SELECT * FROM crop_logs WHERE crop_planting_id = :cropPlantingId AND log_context = :context ORDER BY created_at DESC")
    fun getLogsByContext(cropPlantingId: String, context: String): List<CropLogEntity>

    @Upsert
    fun upsertLog(log: CropLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLogs(logs: List<CropLogEntity>)

    @Query("DELETE FROM crop_logs WHERE id = :id")
    fun deleteLog(id: String): Int

    @Query("DELETE FROM crop_logs WHERE crop_planting_id = :cropPlantingId")
    fun deleteLogsForPlanting(cropPlantingId: String): Int
}
