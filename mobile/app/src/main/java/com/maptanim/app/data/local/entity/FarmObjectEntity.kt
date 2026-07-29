package com.maptanim.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maptanim.app.domain.model.FarmObject
import com.maptanim.app.domain.model.FarmObjectType

@Entity(tableName = "farm_objects")
data class FarmObjectEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "farm_id") val farmId: String,
    @ColumnInfo(name = "object_type") val objectType: String,
    @ColumnInfo(name = "world_x") val worldX: Float,
    @ColumnInfo(name = "world_y") val worldY: Float,
    @ColumnInfo(name = "width_m") val widthM: Float,
    @ColumnInfo(name = "height_m") val heightM: Float,
    @ColumnInfo(name = "rotation_deg") val rotationDeg: Float,
    @ColumnInfo(name = "attached_plot_id") val attachedPlotId: String?,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String
)

fun FarmObjectEntity.toDomain() = FarmObject(
    id = id,
    farmId = farmId,
    objectType = FarmObjectType.valueOf(objectType),
    worldX = worldX,
    worldY = worldY,
    widthM = widthM,
    heightM = heightM,
    rotationDeg = rotationDeg,
    attachedPlotId = attachedPlotId,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun FarmObject.toEntity() = FarmObjectEntity(
    id = id,
    farmId = farmId,
    objectType = objectType.name,
    worldX = worldX,
    worldY = worldY,
    widthM = widthM,
    heightM = heightM,
    rotationDeg = rotationDeg,
    attachedPlotId = attachedPlotId,
    createdAt = createdAt,
    updatedAt = updatedAt
)
