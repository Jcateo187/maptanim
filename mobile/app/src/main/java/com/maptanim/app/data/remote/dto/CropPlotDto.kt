package com.maptanim.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Remote DTO mapping to Supabase PostgreSQL table `public.crop_plots`.
 * Represents a direct-planted crop plot area on the 2D farm canvas.
 */
@Serializable
data class CropPlotDto(
    val id: String = "",
    @SerialName("farm_id") val farm_id: String,
    @SerialName("plot_label") val plot_label: String,
    @SerialName("crop_name") val crop_name: String? = null,
    @SerialName("crop_id") val crop_id: String? = null,
    @SerialName("crop_variety") val crop_variety: String? = null,
    @SerialName("soil_type") val soil_type: String = "LOAM",
    @SerialName("pos_x") val pos_x: Float = 0.0f,
    @SerialName("pos_y") val pos_y: Float = 0.0f,
    @SerialName("width_m") val width_m: Float = 2.0f,
    @SerialName("height_m") val height_m: Float = 3.0f,
    @SerialName("rotation_deg") val rotation_deg: Float = 0.0f,
    val notes: String? = null,
    @SerialName("planted_date") val planted_date: String? = null,
    @SerialName("is_active") val is_active: Boolean = true,
    @SerialName("created_at") val created_at: String? = null,
    @SerialName("updated_at") val updated_at: String? = null
)
