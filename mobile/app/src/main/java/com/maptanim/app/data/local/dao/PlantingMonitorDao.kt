package com.maptanim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maptanim.app.data.local.entity.PlantingMonitorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantingMonitorDao {

    @Query("SELECT * FROM planting_monitors WHERE planting_id = :plantingId ORDER BY recorded_at DESC")
    fun observeMonitorsByPlantingId(plantingId: String): Flow<List<PlantingMonitorEntity>>

    @Query("SELECT * FROM planting_monitors WHERE id = :monitorId")
    fun observeMonitorById(monitorId: String): Flow<PlantingMonitorEntity?>

    /**
     * Query monitoring records & tasks for a specific crop (used by Monitoring side nav when crop is clicked).
     */
    @Query("SELECT * FROM planting_monitors WHERE crop_id = :cropId ORDER BY recorded_at DESC")
    fun observeMonitorsByCropId(cropId: String): Flow<List<PlantingMonitorEntity>>

    /**
     * Query monitoring records & tasks for a specific crop and variety.
     */
    @Query("SELECT * FROM planting_monitors WHERE crop_id = :cropId AND (:variety IS NULL OR crop_variety = :variety) ORDER BY recorded_at DESC")
    fun observeMonitorsByCropAndVariety(cropId: String, variety: String?): Flow<List<PlantingMonitorEntity>>

    /**
     * Today's Tasks: Collect all pending tasks scheduled for today across all crops.
     */
    @Query("SELECT * FROM planting_monitors WHERE due_date <= :todayDate AND is_completed = 0 ORDER BY due_date ASC, recorded_at DESC")
    fun observeTodaysTasks(todayDate: String): Flow<List<PlantingMonitorEntity>>

    /**
     * Today's Tasks by Crop: When a user clicks a crop card on the Today's Tasks screen,
     * directly show that specific crop's task list without navigating to the whole monitoring screen.
     */
    @Query("SELECT * FROM planting_monitors WHERE crop_id = :cropId AND due_date <= :todayDate AND is_completed = 0 ORDER BY due_date ASC")
    fun observeTodaysTasksByCrop(cropId: String, todayDate: String): Flow<List<PlantingMonitorEntity>>

    /**
     * Query all pending tasks for a specific crop (including upcoming).
     */
    @Query("SELECT * FROM planting_monitors WHERE crop_id = :cropId AND is_completed = 0 ORDER BY due_date ASC")
    fun observePendingTasksByCrop(cropId: String): Flow<List<PlantingMonitorEntity>>

    @Query("UPDATE planting_monitors SET is_completed = :isCompleted, completed_at = :completedAt WHERE id = :id")
    fun updateTaskCompletion(id: String, isCompleted: Boolean, completedAt: String?): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertMonitor(monitor: PlantingMonitorEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertMonitors(monitors: List<PlantingMonitorEntity>)

    @Query("DELETE FROM planting_monitors WHERE id = :monitorId")
    fun deleteMonitor(monitorId: String): Int

    @Query("DELETE FROM planting_monitors WHERE planting_id = :plantingId")
    fun deleteMonitorsByPlantingId(plantingId: String): Int
}
