package com.maptanim.backend.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val nickname: String? = null,
    val avatar: String? = null,
    @SerialName("nickname_updated_at") val nickname_updated_at: String? = null,
    @SerialName("tutorial_completed_at") val tutorial_completed_at: String? = null,
    @SerialName("created_at") val created_at: String? = null,
    @SerialName("updated_at") val updated_at: String? = null
)