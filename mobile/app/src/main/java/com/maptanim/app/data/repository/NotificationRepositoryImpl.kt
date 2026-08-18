package com.maptanim.app.data.repository

import com.maptanim.app.data.local.dao.NotificationDao
import com.maptanim.app.data.local.entity.toDomain
import com.maptanim.app.data.local.entity.toEntity
import com.maptanim.app.domain.model.Notification
import com.maptanim.app.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class NotificationRepositoryImpl(
    private val notificationDao: NotificationDao? = null
) : NotificationRepository {

    private val notificationsCache = MutableStateFlow<List<Notification>>(emptyList())

    override fun observeAllNotifications(userId: String): Flow<List<Notification>> {
        return notificationDao?.observeAllNotifications(userId)?.map { entities ->
            entities.map { it.toDomain() }
        } ?: notificationsCache.map { notifications ->
            notifications.filter { it.userId == userId }
        }
    }

    override fun observeUnreadCount(userId: String): Flow<Int> {
        return notificationDao?.observeUnreadCount(userId)
            ?: notificationsCache.map { notifications ->
                notifications.count { it.userId == userId && !it.isRead }
            }
    }

    override suspend fun markRead(notificationId: String) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        notificationDao?.markRead(notificationId)
        notificationsCache.value = notificationsCache.value.map {
            if (it.id == notificationId) it.copy(isRead = true) else it
        }
    }

    override suspend fun markAllRead(userId: String) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        notificationDao?.markAllRead(userId)
        notificationsCache.value = notificationsCache.value.map {
            if (it.userId == userId) it.copy(isRead = true) else it
        }
    }

    override suspend fun upsertNotifications(notifications: List<Notification>) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        notificationDao?.upsertNotifications(notifications.map { it.toEntity() })
        val existingIds = notifications.map { it.id }.toSet()
        notificationsCache.value = notificationsCache.value.filter { it.id !in existingIds } + notifications
    }
}
