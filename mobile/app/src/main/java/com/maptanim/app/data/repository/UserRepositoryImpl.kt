package com.maptanim.app.data.repository

import com.maptanim.app.data.remote.SupabaseClient
import com.maptanim.app.domain.model.AvatarItem
import com.maptanim.app.domain.model.NotificationItem
import com.maptanim.app.domain.model.UserProfile
import com.maptanim.app.domain.repository.UserRepository
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepositoryImpl(
    private val profileRepository: ProfileRepository = ProfileRepository()
) : UserRepository {

    private val userProfileState = MutableStateFlow(UserProfile())
    private val notificationsState = MutableStateFlow<List<NotificationItem>>(
        listOf(
            NotificationItem(
                id = "notif_admin_01",
                title = "📢 System Update v1.2.0",
                message = "MapTanim Admin deployed direct-to-soil grid performance optimizations and sync upgrades.",
                timestamp = "Today, 10:30 AM",
                isRead = false,
                type = "SYSTEM_UPDATE"
            ),
            NotificationItem(
                id = "notif_admin_02",
                title = "🌾 New Crop Added: Sweet Corn",
                message = "Admin added Sweet Corn (Zea mays) to the crop planting library. Tap to view growth stages.",
                timestamp = "Yesterday, 4:15 PM",
                isRead = false,
                type = "CROP_ADDITION"
            ),
            NotificationItem(
                id = "notif_admin_03",
                title = "🛠 Bug Fix & Security Patch",
                message = "Resolved offline database synchronization and plot status updating issues.",
                timestamp = "2 days ago",
                isRead = true,
                type = "BUG_FIX"
            )
        )
    )

    override fun observeUserProfile(): Flow<UserProfile> = userProfileState.asStateFlow()

    override fun observeNotifications(): Flow<List<NotificationItem>> = notificationsState.asStateFlow()

    suspend fun loadUserProfile() {
        val user = SupabaseClient.client.auth.currentUserOrNull()
        if (user != null) {
            val profile = profileRepository.getProfile(user.id)
            if (profile != null) {
                userProfileState.value = UserProfile(
                    id = profile.id,
                    nickname = profile.nickname ?: user.email?.substringBefore("@") ?: "Farmer",
                    avatarAssetPath = profile.avatar ?: "Avatar/Male_Avatar.png",
                    isAccountBound = true,
                    boundEmail = user.email
                )
            }
        }
    }

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
        val cleanName = nickname.trim()
        if (cleanName.isBlank()) return false
        val takenNames = setOf("admin", "system", "maptanim", "superuser")
        return cleanName.lowercase() !in takenNames
    }

    override suspend fun updateNickname(newNickname: String): Boolean {
        if (!isNicknameAvailable(newNickname)) return false
        val currentUser = userProfileState.value
        userProfileState.value = currentUser.copy(nickname = newNickname.trim())
        profileRepository.upsertProfile(
            ProfileDto(
                id = currentUser.id,
                nickname = newNickname.trim()
            )
        )
        return true
    }

    override suspend fun updateAvatar(newAvatarPath: String): Boolean {
        val currentUser = userProfileState.value
        userProfileState.value = currentUser.copy(avatarAssetPath = newAvatarPath)
        profileRepository.upsertProfile(
            ProfileDto(
                id = currentUser.id,
                avatar = newAvatarPath
            )
        )
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
        try {
            SupabaseClient.client.auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

    companion object {
        val instance by lazy { UserRepositoryImpl() }
    }
}
