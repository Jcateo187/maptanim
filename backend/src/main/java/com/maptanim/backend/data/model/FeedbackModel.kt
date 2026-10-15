package com.maptanim.backend.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedbackModel(
    val id: String? = null,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("farmer_name") val farmerName: String = "Mobile Farmer",
    @SerialName("farm_name") val farmName: String? = null,
    val category: String = "GENERAL",
    val subject: String = "",
    val message: String = "",
    val status: String = "PENDING",
    @SerialName("admin_reply") val adminReply: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("resolved_at") val resolvedAt: String? = null
)
