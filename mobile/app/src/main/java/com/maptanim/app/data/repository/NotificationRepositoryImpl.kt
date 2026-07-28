package com.maptanim.app.data.repository

import com.maptanim.app.domain.model.Notification
import com.maptanim.app.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class NotificationRepositoryImpl : NotificationRepository {

    private val notificationsCache = MutableStateFlow<List<Notification>>(emptyList())

    override fun observeAllNotifications(userId: String): Flow<List<Notification>> {
        return notificationsCache.map { notifications ->
            notifications.filter { it.userId == userId }
        }
    }

    override fun observeUnreadCount(userId: String): Flow<Int> {
        return notificationsCache.map { notifications ->
            notifications.count { it.userId == userId && !it.isRead }
        }
    }

    override suspend fun markRead(notificationId: String) {
        notificationsCache.value = notificationsCache.value.map {
            if (it.id == notificationId) it.copy(isRead = true) else it
        }
    }

    override suspend fun markAllRead(userId: String) {
        notificationsCache.value = notificationsCache.value.map {
            if (it.userId == userId) it.copy(isRead = true) else it
        }
    }

    override suspend fun upsertNotifications(notifications: List<Notification>) {
        val existingIds = notifications.map { it.id }.toSet()
        notificationsCache.value = notificationsCache.value.filter { it.id !in existingIds } + notifications
    }
}
