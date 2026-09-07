package com.maptanim.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maptanim.app.domain.model.TilePlanting

/**
 * Room entity for public.tile_plantings — records each crop drag-drop
 * onto a farm tile. Tracks growth stage progression through the 6-stage
 * lifecycle. Crops are resizable via width_m/height_m.
 */
@Entity(tableName = "tile_plantings")
data class TilePlantingEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "tile_id") val tileId: String,
    @ColumnInfo(name = "crop_id") val cropId: String,
    @ColumnInfo(name = "crop_name") val cropName: String,
    @ColumnInfo(name = "crop_variety") val cropVariety: String? = null,
    @ColumnInfo(name = "width_m") val widthM: Float,
    @ColumnInfo(name = "height_m") val heightM: Float,
    @ColumnInfo(name = "offset_x") val offsetX: Float,
    @ColumnInfo(name = "offset_y") val offsetY: Float,
    @ColumnInfo(name = "current_stage") val currentStage: String,
    @ColumnInfo(name = "stage_changed_at") val stageChangedAt: String,
    @ColumnInfo(name = "planted_at") val plantedAt: String,
    @ColumnInfo(name = "expected_harvest_date") val expectedHarvestDate: String?,
    @ColumnInfo(name = "crop_profile_id") val cropProfileId: String?,
    @ColumnInfo(name = "is_active") val isActive: Boolean,
    @ColumnInfo(name = "notes") val notes: String?,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String
)

fun TilePlantingEntity.toDomain() = TilePlanting(
    id = id,
    tileId = tileId,
    cropId = cropId,
    cropName = cropName,
    cropVariety = cropVariety,
    widthM = widthM,
    heightM = heightM,
    offsetX = offsetX,
    offsetY = offsetY,
    currentStage = currentStage,
    stageChangedAt = stageChangedAt,
    plantedAt = plantedAt,
    expectedHarvestDate = expectedHarvestDate,
    cropProfileId = cropProfileId,
    isActive = isActive,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun TilePlanting.toEntity() = TilePlantingEntity(
    id = id,
    tileId = tileId,
    cropId = cropId,
    cropName = cropName,
    cropVariety = cropVariety,
    widthM = widthM,
    heightM = heightM,
    offsetX = offsetX,
    offsetY = offsetY,
    currentStage = currentStage,
    stageChangedAt = stageChangedAt,
    plantedAt = plantedAt,
    expectedHarvestDate = expectedHarvestDate,
    cropProfileId = cropProfileId,
    isActive = isActive,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)
