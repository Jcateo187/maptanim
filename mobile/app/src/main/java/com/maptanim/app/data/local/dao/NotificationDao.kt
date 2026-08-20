package com.maptanim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maptanim.app.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notifications WHERE user_id = :userId OR user_id IS NULL ORDER BY created_at DESC")
    fun observeAllNotifications(userId: String): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE (user_id = :userId OR user_id IS NULL) AND is_read = 0")
    fun observeUnreadCount(userId: String): Flow<Int>

    @Query("UPDATE notifications SET is_read = 1 WHERE id = :notificationId")
    fun markRead(notificationId: String): Int

    @Query("UPDATE notifications SET is_read = 1 WHERE user_id = :userId OR user_id IS NULL")
    fun markAllRead(userId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertNotifications(notifications: List<NotificationEntity>)
}
