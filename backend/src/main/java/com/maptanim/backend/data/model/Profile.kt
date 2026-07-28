package com.maptanim.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Profile(

    val id: String,

    val first_name: String,

    val last_name: String,

    val nickname: String? = null,

    val avatar: String? = null,

    val onboarding_completed: Boolean = false

)