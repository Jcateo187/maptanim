package com.maptanim.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id: String = "",
    @SerialName("farm_id") val farm_id: String,
    @SerialName("plot_id") val plot_id: String? = null,
    @SerialName("task_type") val task_type: String,
    val title: String,
    @SerialName("sub_label") val sub_label: String? = null,
    @SerialName("due_date") val due_date: String,
    @SerialName("is_completed") val is_completed: Boolean = false,
    @SerialName("completed_at") val completed_at: String? = null,
    val notes: String? = null,
    @SerialName("created_at") val created_at: String? = null
)
