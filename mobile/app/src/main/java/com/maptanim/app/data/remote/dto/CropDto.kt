package com.maptanim.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CropDto(
    val id: String = "",
    val name: String = "",
    @SerialName("local_name") val local_name: String? = null,
    @SerialName("botanical_name") val botanical_name: String? = null,
    val category: String = "LEAFY",
    @SerialName("days_to_harvest") val days_to_harvest: Int? = 60,
    @SerialName("watering_interval_days") val watering_interval_days: Int? = 2,
    @SerialName("fertilize_interval_days") val fertilize_interval_days: Int? = 14,
    @SerialName("npk_n") val npk_n: Float? = 1.0f,
    @SerialName("npk_p") val npk_p: Float? = 1.0f,
    @SerialName("npk_k") val npk_k: Float? = 1.0f,
    @SerialName("optimal_ph_min") val optimal_ph_min: Float? = 6.0f,
    @SerialName("optimal_ph_max") val optimal_ph_max: Float? = 7.0f,
    @SerialName("suitable_soils") val suitable_soils: List<String>? = emptyList(),
    @SerialName("image_url") val image_url: String? = null,
    val description: String? = null
)
