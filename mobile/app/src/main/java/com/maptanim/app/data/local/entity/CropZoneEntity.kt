package com.maptanim.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maptanim.app.domain.model.CropZone

@Entity(tableName = "crop_zones")
data class CropZoneEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "plot_id") val plotId: String,
    @ColumnInfo(name = "crop_name") val cropName: String?,
    @ColumnInfo(name = "crop_id") val cropId: String?,
    @ColumnInfo(name = "offset_x") val offsetX: Float,
    @ColumnInfo(name = "offset_y") val offsetY: Float,
    @ColumnInfo(name = "width_m") val widthM: Float,
    @ColumnInfo(name = "height_m") val heightM: Float,
    @ColumnInfo(name = "spacing_m") val spacingM: Float,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String
)

fun CropZoneEntity.toDomain() = CropZone(
    id = id,
    plotId = plotId,
    cropName = cropName,
    cropId = cropId,
    offsetX = offsetX,
    offsetY = offsetY,
    widthM = widthM,
    heightM = heightM,
    spacingM = spacingM,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun CropZone.toEntity() = CropZoneEntity(
    id = id,
    plotId = plotId,
    cropName = cropName,
    cropId = cropId,
    offsetX = offsetX,
    offsetY = offsetY,
    widthM = widthM,
    heightM = heightM,
    spacingM = spacingM,
    createdAt = createdAt,
    updatedAt = updatedAt
)
