package com.maptanim.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FarmDto(
    val id: String = "",
    @SerialName("farmer_id") val farmer_id: String,
    @SerialName("farm_name") val farm_name: String,
    @SerialName("created_at") val created_at: String? = null,
    @SerialName("updated_at") val updated_at: String? = null
)


