package com.maptanim.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val id: String = "",
    @SerialName("user_id") val user_id: String? = null,
    val title: String,
    val body: String? = null,
    @SerialName("task_type") val task_type: String? = null,
    @SerialName("notification_type") val notification_type: String = "SYSTEM_UPDATE",
    @SerialName("is_read") val is_read: Boolean = false,
    @SerialName("created_at") val created_at: String? = null
)
