package com.maptanim.app.data.repository

import com.maptanim.app.data.remote.SupabaseClient
import com.maptanim.app.domain.model.AvatarItem
import com.maptanim.app.domain.model.NotificationItem
import com.maptanim.app.domain.model.UserProfile
import com.maptanim.app.domain.repository.UserRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepositoryImpl(
    private val profileRepository: ProfileRepository = ProfileRepository()
) : UserRepository {

    private val userProfileState = MutableStateFlow(UserProfile())
    private val notificationsState = MutableStateFlow<List<NotificationItem>>(emptyList())

    override fun observeUserProfile(): Flow<UserProfile> = userProfileState.asStateFlow()

    override fun observeNotifications(): Flow<List<NotificationItem>> = notificationsState.asStateFlow()

    override suspend fun refreshNotifications() {
        try {
            val user = SupabaseClient.client.auth.currentUserOrNull()
            val userId = user?.id ?: userProfileState.value.id.ifBlank { null }

            // 1. Direct notifications from notifications table
            val notifDtos = profileRepository.getNotifications(userId)
            val notifItems = notifDtos.map { dto ->
                NotificationItem(
                    id = dto.id ?: java.util.UUID.randomUUID().toString(),
                    title = dto.title.ifBlank { "Notification" },
                    message = dto.body ?: "",
                    timestamp = formatTimestamp(dto.createdAt),
                    isRead = dto.isRead,
                    type = dto.notificationType
                )
            }

            // 2. Admin replies from feedback table
            val feedbackDtos = profileRepository.getFeedbackRepliesForUser(userId)
            val feedbackItems = feedbackDtos.map { dto ->
                NotificationItem(
                    id = dto.id ?: java.util.UUID.randomUUID().toString(),
                    title = "Support Advisory: ${dto.subject}",
                    message = dto.adminReply ?: "",
                    timestamp = formatTimestamp(dto.resolvedAt ?: dto.createdAt),
                    isRead = false,
                    type = "SUPPORT_REPLY"
                )
            }

            val allItems = (feedbackItems + notifItems).distinctBy { it.id }
            if (allItems.isNotEmpty()) {
                notificationsState.value = allItems
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun formatTimestamp(rawTimestamp: String?): String {
        if (rawTimestamp.isNullOrBlank()) return "Just now"
        return try {
            rawTimestamp.take(16).replace("T", " ")
        } catch (_: Exception) {
            rawTimestamp
        }
    }

    suspend fun loadUserProfile() {
        try {
            val user = SupabaseClient.client.auth.currentUserOrNull()
            if (user != null) {
                val profile = try { profileRepository.getProfile(user.id) } catch (_: Exception) { null }
                userProfileState.value = UserProfile(
                    id = profile?.id ?: user.id,
                    nickname = profile?.nickname ?: user.email?.substringBefore("@") ?: "",
                    avatarAssetPath = profile?.avatar ?: "Avatar/Male_Avatar.png",
                    isAccountBound = !user.email.isNullOrEmpty(),
                    boundEmail = user.email,
                    nicknameUpdatedAt = profile?.nicknameUpdatedAt,
                    tutorialCompletedAt = profile?.tutorialCompletedAt
                )
            }
            refreshNotifications()
        } catch (e: Exception) {
            e.printStackTrace()
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
        val cleanName = newNickname.trim()
        if (!isNicknameAvailable(cleanName)) return false

        val currentUser = userProfileState.value
        val daysRemaining = com.maptanim.app.domain.model.getDaysRemainingForNicknameChange(currentUser.nicknameUpdatedAt)
        if (daysRemaining > 0) {
            return false
        }

        val nowIso = java.time.ZonedDateTime.now().toString()
        userProfileState.value = currentUser.copy(
            nickname = cleanName,
            nicknameUpdatedAt = nowIso
        )

        val user = SupabaseClient.client.auth.currentUserOrNull()
        if (user != null) {
            profileRepository.upsertProfile(
                ProfileDto(
                    id = user.id,
                    nickname = cleanName,
                    nicknameUpdatedAt = nowIso,
                    updatedAt = nowIso
                )
            )
        }
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
        try {
            profileRepository.markNotificationRead(notificationId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteNotification(notificationId: String) {
        notificationsState.value = notificationsState.value.filter { it.id != notificationId }
        try {
            profileRepository.deleteNotification(notificationId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun bindAccount(email: String): Boolean {
        userProfileState.value = userProfileState.value.copy(
            isAccountBound = true,
            boundEmail = email
        )
        return true
    }

    override suspend fun sendSupportTicket(subject: String, message: String, category: String): Boolean {
        return try {
            val user = SupabaseClient.client.auth.currentUserOrNull()
            val profile = userProfileState.value
            val dto = FeedbackDto(
                userId = user?.id ?: profile.id.ifBlank { null },
                farmerName = profile.nickname.ifBlank { "Mobile Farmer" },
                category = category,
                subject = subject,
                message = message,
                status = "PENDING"
            )
            SupabaseClient.client.from("feedback").insert(dto)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun resetState() {
        userProfileState.value = UserProfile()
    }

    override suspend fun logout(): Boolean {
        try {
            SupabaseClient.client.auth.signOut()
            resetState()
            RepositoryProvider.clearAllLocalCache()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

    companion object {
        val instance by lazy { UserRepositoryImpl() }
    }
}
