package com.maptanim.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maptanim.app.domain.model.Crop
import com.maptanim.app.domain.model.SoilType

@Entity(tableName = "crops")
data class CropEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "local_name") val localName: String?,
    @ColumnInfo(name = "botanical_name") val botanicalName: String?,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "days_to_harvest") val daysToHarvest: Int,
    @ColumnInfo(name = "watering_interval_days") val wateringIntervalDays: Int,
    @ColumnInfo(name = "fertilize_interval_days") val fertilizeIntervalDays: Int,
    @ColumnInfo(name = "n_ratio") val nRatio: Float,
    @ColumnInfo(name = "p_ratio") val pRatio: Float,
    @ColumnInfo(name = "k_ratio") val kRatio: Float,
    @ColumnInfo(name = "optimal_ph_min") val optimalPhMin: Float,
    @ColumnInfo(name = "optimal_ph_max") val optimalPhMax: Float,
    @ColumnInfo(name = "ideal_soils") val idealSoilsCsv: String,
    @ColumnInfo(name = "suitable_soils") val suitableSoilsCsv: String,
    @ColumnInfo(name = "tolerated_soils") val toleratedSoilsCsv: String,
    @ColumnInfo(name = "pest_risk_season") val pestRiskSeasonCsv: String,
    @ColumnInfo(name = "seasonality") val seasonalityCsv: String,
    @ColumnInfo(name = "image_url") val imageUrl: String?,
    @ColumnInfo(name = "companion_plants") val companionPlantsCsv: String,
    @ColumnInfo(name = "avoid_plants") val avoidPlantsCsv: String,
    @ColumnInfo(name = "common_pests") val commonPestsCsv: String,
    @ColumnInfo(name = "harvest_indicators") val harvestIndicators: String?,
    @ColumnInfo(name = "description") val description: String?
)

fun CropEntity.toDomain(): Crop = Crop(
    id = id,
    name = name,
    localName = localName,
    botanicalName = botanicalName,
    category = category,
    daysToHarvest = daysToHarvest,
    wateringIntervalDays = wateringIntervalDays,
    fertilizeIntervalDays = fertilizeIntervalDays,
    nRatio = nRatio,
    pRatio = pRatio,
    kRatio = kRatio,
    optimalPhMin = optimalPhMin,
    optimalPhMax = optimalPhMax,
    idealSoils = idealSoilsCsv.split(",").mapNotNull { parseSoilType(it) },
    suitableSoils = suitableSoilsCsv.split(",").mapNotNull { parseSoilType(it) },
    toleratedSoils = toleratedSoilsCsv.split(",").mapNotNull { parseSoilType(it) },
    pestRiskSeason = pestRiskSeasonCsv.split(",").filter { it.isNotBlank() },
    seasonality = seasonalityCsv.split(",").filter { it.isNotBlank() },
    imageUrl = imageUrl,
    companionPlants = companionPlantsCsv.split(",").filter { it.isNotBlank() },
    avoidPlants = avoidPlantsCsv.split(",").filter { it.isNotBlank() },
    commonPests = commonPestsCsv.split(",").filter { it.isNotBlank() },
    harvestIndicators = harvestIndicators,
    description = description
)

fun Crop.toEntity(): CropEntity = CropEntity(
    id = id,
    name = name,
    localName = localName,
    botanicalName = botanicalName,
    category = category,
    daysToHarvest = daysToHarvest,
    wateringIntervalDays = wateringIntervalDays,
    fertilizeIntervalDays = fertilizeIntervalDays,
    nRatio = nRatio,
    pRatio = pRatio,
    kRatio = kRatio,
    optimalPhMin = optimalPhMin,
    optimalPhMax = optimalPhMax,
    idealSoilsCsv = idealSoils.joinToString(",") { it.name },
    suitableSoilsCsv = suitableSoils.joinToString(",") { it.name },
    toleratedSoilsCsv = toleratedSoils.joinToString(",") { it.name },
    pestRiskSeasonCsv = pestRiskSeason.joinToString(","),
    seasonalityCsv = seasonality.joinToString(","),
    imageUrl = imageUrl,
    companionPlantsCsv = companionPlants.joinToString(","),
    avoidPlantsCsv = avoidPlants.joinToString(","),
    commonPestsCsv = commonPests.joinToString(","),
    harvestIndicators = harvestIndicators,
    description = description
)

private fun parseSoilType(name: String): SoilType? = try {
    SoilType.valueOf(name.trim())
} catch (e: Exception) {
    null
}
