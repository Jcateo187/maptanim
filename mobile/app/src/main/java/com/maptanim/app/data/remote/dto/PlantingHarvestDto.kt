package com.maptanim.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Remote DTO mapping to Supabase PostgreSQL table `public.planting_harvests`.
 * Harvest records tied to a specific tile planting, including variety and yield.
 */
@Serializable
data class PlantingHarvestDto(
    val id: String = "",
    @SerialName("planting_id") val planting_id: String,
    @SerialName("crop_name") val crop_name: String,
    @SerialName("crop_variety") val crop_variety: String? = null,
    @SerialName("yield_kg") val yield_kg: Float = 0.0f,
    @SerialName("yield_units") val yield_units: Int? = null,
    @SerialName("quality_grade") val quality_grade: String? = "Grade A",
    @SerialName("harvest_date") val harvest_date: String,
    @SerialName("growing_days") val growing_days: Int? = null,
    val notes: String? = null,
    @SerialName("created_at") val created_at: String? = null
)
