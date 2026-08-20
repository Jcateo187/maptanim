package com.maptanim.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maptanim.app.domain.model.FarmTask
import com.maptanim.app.domain.model.TaskType

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "farm_id") val farmId: String,
    @ColumnInfo(name = "plot_id") val plotId: String,
    @ColumnInfo(name = "plot_label") val plotLabel: String,
    @ColumnInfo(name = "crop_name") val cropName: String?,
    @ColumnInfo(name = "task_type") val taskType: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "sub_label") val subLabel: String?,
    @ColumnInfo(name = "due_date") val dueDate: String,
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean,
    @ColumnInfo(name = "completed_at") val completedAt: String?
)

fun TaskEntity.toDomain() = FarmTask(
    id = id,
    farmId = farmId,
    plotId = plotId,
    plotLabel = plotLabel,
    cropName = cropName,
    taskType = try { TaskType.valueOf(taskType) } catch (e: Exception) { TaskType.WATER },
    title = title,
    subLabel = subLabel,
    dueDate = dueDate,
    isCompleted = isCompleted,
    completedAt = completedAt
)

fun FarmTask.toEntity() = TaskEntity(
    id = id,
    farmId = farmId,
    plotId = plotId,
    plotLabel = plotLabel,
    cropName = cropName,
    taskType = taskType.name,
    title = title,
    subLabel = subLabel,
    dueDate = dueDate,
    isCompleted = isCompleted,
    completedAt = completedAt
)
