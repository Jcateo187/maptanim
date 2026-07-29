package com.maptanim.app.domain.repository

import com.maptanim.app.domain.model.AvatarItem
import com.maptanim.app.domain.model.NotificationItem
import com.maptanim.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeUserProfile(): Flow<UserProfile>
    fun observeNotifications(): Flow<List<NotificationItem>>
    suspend fun getAvailableAvatars(): List<AvatarItem>
    suspend fun isNicknameAvailable(nickname: String): Boolean
    suspend fun updateNickname(newNickname: String): Boolean
    suspend fun updateAvatar(newAvatarPath: String): Boolean
    suspend fun markNotificationAsRead(notificationId: String)
    suspend fun deleteNotification(notificationId: String)
    suspend fun bindAccount(email: String): Boolean
    suspend fun logout(): Boolean
}
