package com.maptanim.backend.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val email: String? = null,
    val role: String = "FARMER",
    @SerialName("avatar_url") val avatar_url: String? = null
)
