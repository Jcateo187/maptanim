package com.maptanim.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Remote DTO mapping to Supabase PostgreSQL table `public.crop_zones`.
 * Represents fine-grained vegetable zones and plantings within a 2D crop plot / garden.
 */
@Serializable
data class CropZoneDto(
    val id: String = "",
    @SerialName("plot_id") val plot_id: String,
    @SerialName("crop_name") val crop_name: String? = null,
    @SerialName("crop_id") val crop_id: String? = null,
    @SerialName("offset_x") val offset_x: Float = 0.0f,
    @SerialName("offset_y") val offset_y: Float = 0.0f,
    @SerialName("width_m") val width_m: Float = 1.0f,
    @SerialName("height_m") val height_m: Float = 1.0f,
    @SerialName("spacing_m") val spacing_m: Float = 0.3f,
    @SerialName("created_at") val created_at: String? = null,
    @SerialName("updated_at") val updated_at: String? = null
)
