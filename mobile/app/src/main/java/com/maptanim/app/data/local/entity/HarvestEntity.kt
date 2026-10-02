package com.maptanim.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maptanim.app.domain.model.HarvestRecord

@Entity(tableName = "harvest_records")
data class HarvestEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "zone_id") val plotId: String,
    @ColumnInfo(name = "farm_id") val farmId: String,
    @ColumnInfo(name = "farm_name") val farmName: String = "MapTanim Main Farm",
    @ColumnInfo(name = "plot_label") val plotLabel: String = "Plot 1",
    @ColumnInfo(name = "crop_name") val cropName: String,
    @ColumnInfo(name = "crop_variety") val cropVariety: String? = null,
    @ColumnInfo(name = "planted_date") val plantedDate: String? = null,
    @ColumnInfo(name = "harvested_at") val harvestedAt: String,
    @ColumnInfo(name = "growing_duration_days") val growingDurationDays: Int = 0,
    @ColumnInfo(name = "yield_kg") val yieldKg: Float,
    @ColumnInfo(name = "quality_rating") val qualityRating: Int,
    @ColumnInfo(name = "notes") val notes: String?,
    // New fields from Crop Management flow spec
    @ColumnInfo(name = "harvest_method", defaultValue = "") val harvestMethod: String? = null,
    @ColumnInfo(name = "quantity", defaultValue = "0") val quantity: Float = 0f,
    @ColumnInfo(name = "unit", defaultValue = "kg") val unit: String = "kg",
    @ColumnInfo(name = "marketable_pct") val marketablePct: Float? = null,
    @ColumnInfo(name = "crop_planting_id") val cropPlantingId: String? = null
)

fun HarvestEntity.toDomain() = HarvestRecord(
    id = id,
    plotId = plotId,
    farmId = farmId,
    farmName = farmName,
    plotLabel = plotLabel,
    cropName = cropName,
    cropVariety = cropVariety,
    plantedDate = plantedDate,
    harvestedAt = harvestedAt,
    growingDurationDays = growingDurationDays,
    yieldKg = yieldKg,
    qualityRating = qualityRating,
    notes = notes,
    harvestMethod = harvestMethod,
    quantity = quantity,
    unit = unit,
    marketablePct = marketablePct,
    cropPlantingId = cropPlantingId
)

fun HarvestRecord.toEntity() = HarvestEntity(
    id = id,
    plotId = plotId,
    farmId = farmId,
    farmName = farmName,
    plotLabel = plotLabel,
    cropName = cropName,
    cropVariety = cropVariety,
    plantedDate = plantedDate,
    harvestedAt = harvestedAt,
    growingDurationDays = growingDurationDays,
    yieldKg = yieldKg,
    qualityRating = qualityRating,
    notes = notes,
    harvestMethod = harvestMethod,
    quantity = quantity,
    unit = unit,
    marketablePct = marketablePct,
    cropPlantingId = cropPlantingId
)
