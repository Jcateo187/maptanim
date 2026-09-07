package com.maptanim.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maptanim.app.domain.model.PlantingMonitor

/**
 * Room entity for public.planting_monitors — monitoring/observation logs
 * and task records for active plantings. Denormalized crop_id/crop_name/crop_variety
 * enables direct filtering by soil type, season, and category in the
 * Monitoring side nav. due_date/is_completed supports Today's Tasks.
 */
@Entity(tableName = "planting_monitors")
data class PlantingMonitorEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "planting_id") val plantingId: String,
    @ColumnInfo(name = "crop_id") val cropId: String,
    @ColumnInfo(name = "crop_name") val cropName: String,
    @ColumnInfo(name = "crop_variety") val cropVariety: String? = null,
    @ColumnInfo(name = "monitor_type") val monitorType: String,
    @ColumnInfo(name = "value") val value: Float?,
    @ColumnInfo(name = "unit") val unit: String?,
    @ColumnInfo(name = "notes") val notes: String?,
    @ColumnInfo(name = "due_date") val dueDate: String?,
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean,
    @ColumnInfo(name = "completed_at") val completedAt: String?,
    @ColumnInfo(name = "recorded_at") val recordedAt: String,
    @ColumnInfo(name = "created_at") val createdAt: String
)

fun PlantingMonitorEntity.toDomain() = PlantingMonitor(
    id = id,
    plantingId = plantingId,
    cropId = cropId,
    cropName = cropName,
    cropVariety = cropVariety,
    monitorType = monitorType,
    value = value,
    unit = unit,
    notes = notes,
    dueDate = dueDate,
    isCompleted = isCompleted,
    completedAt = completedAt,
    recordedAt = recordedAt,
    createdAt = createdAt
)

fun PlantingMonitor.toEntity() = PlantingMonitorEntity(
    id = id,
    plantingId = plantingId,
    cropId = cropId,
    cropName = cropName,
    cropVariety = cropVariety,
    monitorType = monitorType,
    value = value,
    unit = unit,
    notes = notes,
    dueDate = dueDate,
    isCompleted = isCompleted,
    completedAt = completedAt,
    recordedAt = recordedAt,
    createdAt = createdAt
)
