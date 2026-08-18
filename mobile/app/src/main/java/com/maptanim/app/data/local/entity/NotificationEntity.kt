package com.maptanim.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maptanim.app.domain.model.Notification
import com.maptanim.app.domain.model.TaskType

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "user_id") val userId: String?,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "body") val body: String?,
    @ColumnInfo(name = "task_type") val taskType: String?,
    @ColumnInfo(name = "notification_type") val notificationType: String = "SYSTEM_UPDATE",
    @ColumnInfo(name = "is_read") val isRead: Boolean,
    @ColumnInfo(name = "created_at") val createdAt: String
)

fun NotificationEntity.toDomain() = Notification(
    id = id,
    userId = userId ?: "",
    title = title,
    body = body,
    taskType = taskType?.let { try { TaskType.valueOf(it) } catch (e: Exception) { null } },
    isRead = isRead,
    createdAt = createdAt
)

fun Notification.toEntity() = NotificationEntity(
    id = id,
    userId = userId,
    title = title,
    body = body,
    taskType = taskType?.name,
    notificationType = "SYSTEM_UPDATE",
    isRead = isRead,
    createdAt = createdAt
)
