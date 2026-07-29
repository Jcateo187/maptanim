package com.maptanim.app.data.repository

import com.maptanim.app.domain.model.AvatarItem
import com.maptanim.app.domain.model.NotificationItem
import com.maptanim.app.domain.model.UserProfile
import com.maptanim.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepositoryImpl : UserRepository {

    private val userProfileState = MutableStateFlow(
        UserProfile(
            id = "user_01",
            nickname = "FarmerJames",
            avatarAssetPath = "Avatar/Male_Avatar.png",
            isAccountBound = false,
            boundEmail = null
        )
    )

    private val notificationsState = MutableStateFlow(
        listOf(
            NotificationItem(
                id = "notif_1",
                title = "Welcome to MapTanim!",
                message = "Your farm layout is initialized. Start planning your crop plots now.",
                timestamp = "Today, 08:30 AM",
                isRead = false,
                type = "SYSTEM"
            ),
            NotificationItem(
                id = "notif_2",
                title = "DSS Advisory Update",
                message = "Optimal planting conditions for String Beans detected in Murcia region.",
                timestamp = "Yesterday, 04:15 PM",
                isRead = false,
                type = "ALERT"
            ),
            NotificationItem(
                id = "notif_3",
                title = "System Recommendation",
                message = "Check irrigation schedule for Plot 1. Soil moisture target is 75%.",
                timestamp = "2 days ago",
                isRead = true,
                type = "TASK"
            )
        )
    )

    // Simulating taken nicknames in Supabase user registry
    private val takenNicknames = setOf("admin", "system", "maptanim", "superuser", "taken")

    override fun observeUserProfile(): Flow<UserProfile> = userProfileState.asStateFlow()

    override fun observeNotifications(): Flow<List<NotificationItem>> = notificationsState.asStateFlow()

    override suspend fun getAvailableAvatars(): List<AvatarItem> {
        return listOf(
            AvatarItem("avatar_1", "Male Farmer", "Avatar/Male_Avatar.png"),
            AvatarItem("avatar_2", "Female Farmer", "Avatar/Female_Avatar.png"),
            AvatarItem("avatar_3", "Young Girl", "Avatar/Girl_Avatar.png"),
            AvatarItem("avatar_4", "Grandmother", "Avatar/Grandmother_Avatar.png"),
            AvatarItem("avatar_5", "Grandfather", "Avatar/Grandfather_Avatar.png"),
            AvatarItem("avatar_6", "Kid", "Avatar/Kid_Avatar.png")
        )
    }

    override suspend fun isNicknameAvailable(nickname: String): Boolean {
        val cleanName = nickname.trim().lowercase()
        if (cleanName.isBlank()) return false
        return cleanName !in takenNicknames && cleanName != userProfileState.value.nickname.lowercase()
    }

    override suspend fun updateNickname(newNickname: String): Boolean {
        if (!isNicknameAvailable(newNickname)) return false
        userProfileState.value = userProfileState.value.copy(nickname = newNickname.trim())
        return true
    }

    override suspend fun updateAvatar(newAvatarPath: String): Boolean {
        userProfileState.value = userProfileState.value.copy(avatarAssetPath = newAvatarPath)
        return true
    }

    override suspend fun markNotificationAsRead(notificationId: String) {
        notificationsState.value = notificationsState.value.map {
            if (it.id == notificationId) it.copy(isRead = true) else it
        }
    }

    override suspend fun deleteNotification(notificationId: String) {
        notificationsState.value = notificationsState.value.filter { it.id != notificationId }
    }

    override suspend fun bindAccount(email: String): Boolean {
        userProfileState.value = userProfileState.value.copy(
            isAccountBound = true,
            boundEmail = email
        )
        return true
    }

    override suspend fun logout(): Boolean {
        // Reset user session state
        return true
    }

    companion object {
        val instance by lazy { UserRepositoryImpl() }
    }
}
