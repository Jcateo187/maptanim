package com.maptanim.backend.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val nickname: String? = null,
    val avatar: String? = null,
    val onboarding_completed: Boolean = false
)