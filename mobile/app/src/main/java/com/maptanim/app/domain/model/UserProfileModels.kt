package com.maptanim.app.domain.model

data class UserProfile(
    val id: String = "user_01",
    val nickname: String = "FarmerJames",
    val avatarAssetPath: String = "Avatar/Male_Avatar.png",
    val isAccountBound: Boolean = false,
    val boundEmail: String? = null,
    val createdAt: String = "2026-01-01"
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean = false,
    val type: String = "SYSTEM" // SYSTEM, TASK, ALERT
)

data class AvatarItem(
    val id: String,
    val displayName: String,
    val assetPath: String
)
