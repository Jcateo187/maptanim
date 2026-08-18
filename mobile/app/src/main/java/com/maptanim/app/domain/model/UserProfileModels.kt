package com.maptanim.app.domain.model

data class UserProfile(
    val id: String = "",
    val nickname: String = "",
    val avatarAssetPath: String = "Avatar/Male_Avatar.png",
    val isAccountBound: Boolean = false,
    val boundEmail: String? = null,
    val createdAt: String = "",
    val nicknameUpdatedAt: String? = null,
    val tutorialCompletedAt: String? = null
)

fun getDaysRemainingForNicknameChange(lastUpdatedAt: String?): Long {
    if (lastUpdatedAt.isNullOrBlank()) return 0L
    return try {
        val lastDate = java.time.ZonedDateTime.parse(lastUpdatedAt)
        val currentDate = java.time.ZonedDateTime.now()
        val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(lastDate, currentDate)
        val remaining = 15L - daysBetween
        if (remaining < 0L) 0L else remaining
    } catch (e: Exception) {
        try {
            val lastLocalDate = java.time.LocalDate.parse(lastUpdatedAt.take(10))
            val currentLocalDate = java.time.LocalDate.now()
            val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(lastLocalDate, currentLocalDate)
            val remaining = 15L - daysBetween
            if (remaining < 0L) 0L else remaining
        } catch (ex: Exception) {
            0L
        }
    }
}

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
