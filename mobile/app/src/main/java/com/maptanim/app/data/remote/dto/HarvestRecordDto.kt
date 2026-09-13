package com.maptanim.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Remote DTO mapping to Supabase PostgreSQL table `public.harvest_records`.
 * Synchronizes mobile harvest history with the Admin Dashboard.
 */
@Serializable
data class HarvestRecordDto(
    val id: String,
    @SerialName("farm_id") val farmId: String,
    @SerialName("plot_id") val plotId: String? = null,
    @SerialName("farm_name") val farmName: String? = "MapTanim Main Farm",
    @SerialName("plot_label") val plotLabel: String? = "Plot 1",
    @SerialName("crop_name") val cropName: String,
    @SerialName("crop_variety") val cropVariety: String? = null,
    @SerialName("planted_date") val plantedDate: String? = null,
    @SerialName("harvested_at") val harvestedAt: String? = null,
    @SerialName("harvested_date") val harvestedDate: String? = null,
    @SerialName("growing_duration_days") val growingDurationDays: Int = 0,
    @SerialName("yield_kg") val yieldKg: Float = 0f,
    @SerialName("quality_rating") val qualityRating: Int = 5,
    val notes: String? = null
)
