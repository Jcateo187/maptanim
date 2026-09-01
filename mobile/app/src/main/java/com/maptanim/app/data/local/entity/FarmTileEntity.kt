package com.maptanim.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maptanim.app.domain.model.FarmTile

/**
 * Room entity for public.farm_tiles — isometric 45×45 grid tiles.
 * Each tile is a plain white cell on the isometric farm canvas.
 * No soil type — tiles use bg scenery + white tile + crops only.
 */
@Entity(tableName = "farm_tiles")
data class FarmTileEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "farm_id") val farmId: String,
    @ColumnInfo(name = "grid_x") val gridX: Int,
    @ColumnInfo(name = "grid_y") val gridY: Int,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "current_crop_id") val currentCropId: String?,
    @ColumnInfo(name = "tile_label") val tileLabel: String?,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String
)

fun FarmTileEntity.toDomain() = FarmTile(
    id = id,
    farmId = farmId,
    gridX = gridX,
    gridY = gridY,
    status = status,
    currentCropId = currentCropId,
    tileLabel = tileLabel,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun FarmTile.toEntity() = FarmTileEntity(
    id = id,
    farmId = farmId,
    gridX = gridX,
    gridY = gridY,
    status = status,
    currentCropId = currentCropId,
    tileLabel = tileLabel,
    createdAt = createdAt,
    updatedAt = updatedAt
)
