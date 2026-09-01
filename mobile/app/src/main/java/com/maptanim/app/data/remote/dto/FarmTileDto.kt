package com.maptanim.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Remote DTO mapping to Supabase PostgreSQL table `public.farm_tiles`.
 * Represents a single cell in the 45×45 isometric farm grid.
 */
@Serializable
data class FarmTileDto(
    val id: String = "",
    @SerialName("farm_id") val farm_id: String,
    @SerialName("grid_x") val grid_x: Int = 0,
    @SerialName("grid_y") val grid_y: Int = 0,
    val status: String = "EMPTY",
    @SerialName("current_crop_id") val current_crop_id: String? = null,
    @SerialName("tile_label") val tile_label: String? = null,
    @SerialName("created_at") val created_at: String? = null,
    @SerialName("updated_at") val updated_at: String? = null
)
