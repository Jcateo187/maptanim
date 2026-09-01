package com.maptanim.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maptanim.app.domain.model.PlantingHarvest

/**
 * Room entity for public.planting_harvests — harvest records tied to a
 * specific tile planting. Captures yield, quality, variety, and duration data
 * when a crop completes its lifecycle.
 */
@Entity(tableName = "planting_harvests")
data class PlantingHarvestEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "planting_id") val plantingId: String,
    @ColumnInfo(name = "crop_name") val cropName: String,
    @ColumnInfo(name = "crop_variety") val cropVariety: String? = null,
    @ColumnInfo(name = "yield_kg") val yieldKg: Float,
    @ColumnInfo(name = "yield_units") val yieldUnits: Int?,
    @ColumnInfo(name = "quality_grade") val qualityGrade: String?,
    @ColumnInfo(name = "harvest_date") val harvestDate: String,
    @ColumnInfo(name = "growing_days") val growingDays: Int?,
    @ColumnInfo(name = "notes") val notes: String?,
    @ColumnInfo(name = "created_at") val createdAt: String
)

fun PlantingHarvestEntity.toDomain() = PlantingHarvest(
    id = id,
    plantingId = plantingId,
    cropName = cropName,
    cropVariety = cropVariety,
    yieldKg = yieldKg,
    yieldUnits = yieldUnits,
    qualityGrade = qualityGrade,
    harvestDate = harvestDate,
    growingDays = growingDays,
    notes = notes,
    createdAt = createdAt
)

fun PlantingHarvest.toEntity() = PlantingHarvestEntity(
    id = id,
    plantingId = plantingId,
    cropName = cropName,
    cropVariety = cropVariety,
    yieldKg = yieldKg,
    yieldUnits = yieldUnits,
    qualityGrade = qualityGrade,
    harvestDate = harvestDate,
    growingDays = growingDays,
    notes = notes,
    createdAt = createdAt
)
