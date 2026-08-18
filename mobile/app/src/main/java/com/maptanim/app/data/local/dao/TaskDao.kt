package com.maptanim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maptanim.app.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE farm_id = :farmId AND due_date = :today AND is_completed = 0")
    fun observeTodayTasks(farmId: String, today: String): Flow<List<TaskEntity>>

    @Query("SELECT tasks.* FROM tasks INNER JOIN farms ON tasks.farm_id = farms.id WHERE farms.farmer_id = :farmerId")
    fun observeAllTasks(farmerId: String): Flow<List<TaskEntity>>

    @Query("SELECT tasks.* FROM tasks INNER JOIN farms ON tasks.farm_id = farms.id WHERE farms.farmer_id = :farmerId AND due_date BETWEEN :startDate AND :endDate")
    fun observeTasksForRange(farmerId: String, startDate: String, endDate: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE farm_id = :farmId AND is_completed = 0 AND (task_type = 'PEST_ALERT' OR task_type = 'APPLY_PESTICIDE')")
    fun observeActiveAlerts(farmId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE farm_id = :farmId AND is_completed = 0 AND task_type = 'HARVEST'")
    fun observeHarvestReady(farmId: String): Flow<List<TaskEntity>>

    @Query("UPDATE tasks SET is_completed = 1, completed_at = :completedAt WHERE id = :taskId")
    fun markTaskCompleted(taskId: String, completedAt: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertTasks(tasks: List<TaskEntity>)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    fun deleteTask(taskId: String): Int
}
