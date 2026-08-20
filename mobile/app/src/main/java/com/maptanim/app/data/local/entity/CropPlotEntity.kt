package com.maptanim.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maptanim.app.domain.model.CropPlot
import com.maptanim.app.domain.model.SoilType

@Entity(tableName = "crop_plots")
data class CropPlotEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "farm_id") val farmId: String,
    @ColumnInfo(name = "plot_label") val plotLabel: String,
    @ColumnInfo(name = "crop_name") val cropName: String?,
    @ColumnInfo(name = "crop_id") val cropId: String?,
    @ColumnInfo(name = "crop_variety") val cropVariety: String? = null,
    @ColumnInfo(name = "soil_type") val soilType: String,
    @ColumnInfo(name = "pos_x") val posX: Float,
    @ColumnInfo(name = "pos_y") val posY: Float,
    @ColumnInfo(name = "width_m") val widthM: Float,
    @ColumnInfo(name = "height_m") val heightM: Float,
    @ColumnInfo(name = "rotation_deg") val rotationDeg: Float,
    @ColumnInfo(name = "planted_date") val plantedDate: String?,
    @ColumnInfo(name = "is_active") val isActive: Boolean,
    @ColumnInfo(name = "notes") val notes: String?,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String
)

fun CropPlotEntity.toDomain() = CropPlot(
    id = id,
    farmId = farmId,
    plotLabel = plotLabel,
    cropName = cropName,
    cropId = cropId,
    cropVariety = cropVariety,
    soilType = try { SoilType.valueOf(soilType) } catch (e: Exception) { SoilType.LOAM },
    posX = posX,
    posY = posY,
    widthM = widthM,
    heightM = heightM,
    rotationDeg = rotationDeg,
    plantedDate = plantedDate,
    isActive = isActive,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun CropPlot.toEntity() = CropPlotEntity(
    id = id,
    farmId = farmId,
    plotLabel = plotLabel,
    cropName = cropName,
    cropId = cropId,
    cropVariety = cropVariety,
    soilType = soilType.name,
    posX = posX,
    posY = posY,
    widthM = widthM,
    heightM = heightM,
    rotationDeg = rotationDeg,
    plantedDate = plantedDate,
    isActive = isActive,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)
