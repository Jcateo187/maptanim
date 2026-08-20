package com.maptanim.app.data.repository

import com.maptanim.app.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val id: String,
    val nickname: String? = null,
    val avatar: String? = null,
    @SerialName("nickname_updated_at") val nicknameUpdatedAt: String? = null,
    @SerialName("tutorial_completed_at") val tutorialCompletedAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class FeedbackDto(
    val id: String? = null,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("farmer_name") val farmerName: String = "Mobile Farmer",
    @SerialName("farm_name") val farmName: String? = null,
    val category: String = "GENERAL",
    val subject: String,
    val message: String,
    val status: String = "PENDING",
    @SerialName("admin_reply") val adminReply: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("resolved_at") val resolvedAt: String? = null
)

@Serializable
data class NotificationDto(
    val id: String? = null,
    @SerialName("user_id") val userId: String? = null,
    val title: String = "",
    val body: String? = null,
    @SerialName("notification_type") val notificationType: String = "SYSTEM_UPDATE",
    @SerialName("is_read") val isRead: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
)

class ProfileRepository {

    suspend fun getFeedbackRepliesForUser(userId: String?): List<FeedbackDto> {
        return try {
            val all = SupabaseClient.client
                .from("feedback")
                .select()
                .decodeList<FeedbackDto>()

            if (!userId.isNullOrBlank()) {
                all.filter { it.userId == userId && !it.adminReply.isNullOrBlank() }
            } else {
                all.filter { !it.adminReply.isNullOrBlank() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getNotifications(userId: String?): List<NotificationDto> {
        return try {
            val all = SupabaseClient.client
                .from("notifications")
                .select()
                .decodeList<NotificationDto>()

            if (!userId.isNullOrBlank()) {
                all.filter { it.userId == null || it.userId == userId }
            } else {
                all.filter { it.userId == null }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun markNotificationRead(notificationId: String): Boolean {
        return try {
            SupabaseClient.client
                .from("notifications")
                .update({
                    set("is_read", true)
                }) {
                    filter {
                        eq("id", notificationId)
                    }
                }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteNotification(notificationId: String): Boolean {
        return try {
            SupabaseClient.client
                .from("notifications")
                .delete {
                    filter {
                        eq("id", notificationId)
                    }
                }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getProfile(userId: String): ProfileDto? {
        return try {
            SupabaseClient.client
                .from("profiles")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<ProfileDto>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun upsertProfile(profile: ProfileDto): Boolean {
        return try {
            SupabaseClient.client
                .from("profiles")
                .upsert(profile)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateProfile(
        userId: String,
        nickname: String,
        avatar: String? = null,
        nicknameUpdatedAt: String? = null,
        tutorialCompletedAt: String? = null
    ): Result<Unit> {
        return try {
            val dto = ProfileDto(
                id = userId,
                nickname = nickname,
                avatar = avatar,
                nicknameUpdatedAt = nicknameUpdatedAt,
                tutorialCompletedAt = tutorialCompletedAt,
                updatedAt = java.time.ZonedDateTime.now().toString()
            )
            val success = upsertProfile(dto)
            if (success) Result.success(Unit) else Result.failure(Exception("Failed to update profile"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

