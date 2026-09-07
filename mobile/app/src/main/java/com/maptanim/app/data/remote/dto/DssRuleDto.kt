package com.maptanim.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DssRuleDto(
    val id: String = "",
    @SerialName("crop_a") val crop_a: String = "",
    @SerialName("crop_b") val crop_b: String = "",
    val relationship: String = "NEUTRAL",
    val reason: String? = null,
    val source: String? = null,
    @SerialName("created_at") val created_at: String? = null
)
