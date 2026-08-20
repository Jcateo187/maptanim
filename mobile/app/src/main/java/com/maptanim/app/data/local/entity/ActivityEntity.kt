package com.maptanim.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maptanim.app.domain.model.Activity
import com.maptanim.app.domain.model.TaskType

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "plot_id") val plotId: String,
    @ColumnInfo(name = "farm_id") val farmId: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "notes") val notes: String?,
    @ColumnInfo(name = "performed_at") val performedAt: String
)

fun ActivityEntity.toDomain() = Activity(
    id = id,
    plotId = plotId,
    farmId = farmId,
    type = try { TaskType.valueOf(type) } catch (e: Exception) { TaskType.WATER },
    notes = notes,
    performedAt = performedAt
)

fun Activity.toEntity() = ActivityEntity(
    id = id,
    plotId = plotId,
    farmId = farmId,
    type = type.name,
    notes = notes,
    performedAt = performedAt
)
