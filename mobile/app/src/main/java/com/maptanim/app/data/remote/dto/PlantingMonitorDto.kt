package com.maptanim.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Remote DTO mapping to Supabase PostgreSQL table `public.planting_monitors`.
 * Monitoring/observation logs with denormalized crop fields for direct filtering.
 * Includes crop_variety, and due_date/is_completed for Today's Tasks integration.
 */
@Serializable
data class PlantingMonitorDto(
    val id: String = "",
    @SerialName("planting_id") val planting_id: String,
    @SerialName("crop_id") val crop_id: String,
    @SerialName("crop_name") val crop_name: String,
    @SerialName("crop_variety") val crop_variety: String? = null,
    @SerialName("monitor_type") val monitor_type: String,
    val value: Float? = null,
    val unit: String? = null,
    val notes: String? = null,
    @SerialName("due_date") val due_date: String? = null,
    @SerialName("is_completed") val is_completed: Boolean = false,
    @SerialName("completed_at") val completed_at: String? = null,
    @SerialName("recorded_at") val recorded_at: String? = null,
    @SerialName("created_at") val created_at: String? = null
)
