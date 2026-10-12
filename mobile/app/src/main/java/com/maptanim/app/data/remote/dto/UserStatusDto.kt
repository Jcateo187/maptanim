package com.maptanim.app.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Remote DTO mapping to Supabase PostgreSQL table `public.users`.
 * Used to verify active user account status and role permissions.
 */
@Serializable
data class UserStatusDto(
    val id: String = "",
    val status: String = "ACTIVE",
    val role: String = "FARMER"
)
