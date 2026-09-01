package com.maptanim.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Remote DTO mapping to Supabase PostgreSQL table `public.tile_plantings`.
 * Records each crop drag-drop placement onto an isometric farm tile.
 * Supports resizable crops via width_m/height_m and variety selection.
 */
@Serializable
data class TilePlantingDto(
    val id: String = "",
    @SerialName("tile_id") val tile_id: String,
    @SerialName("crop_id") val crop_id: String,
    @SerialName("crop_name") val crop_name: String,
    @SerialName("crop_variety") val crop_variety: String? = null,
    @SerialName("width_m") val width_m: Float = 1.0f,
    @SerialName("height_m") val height_m: Float = 1.0f,
    @SerialName("offset_x") val offset_x: Float = 0.0f,
    @SerialName("offset_y") val offset_y: Float = 0.0f,
    @SerialName("current_stage") val current_stage: String = "GERMINATION",
    @SerialName("stage_changed_at") val stage_changed_at: String? = null,
    @SerialName("planted_at") val planted_at: String? = null,
    @SerialName("expected_harvest_date") val expected_harvest_date: String? = null,
    @SerialName("crop_profile_id") val crop_profile_id: String? = null,
    @SerialName("is_active") val is_active: Boolean = true,
    val notes: String? = null,
    @SerialName("created_at") val created_at: String? = null,
    @SerialName("updated_at") val updated_at: String? = null
)
