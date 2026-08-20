package com.maptanim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maptanim.app.data.local.entity.SyncQueueEntity

@Dao
interface SyncQueueDao {

    @Query("SELECT * FROM sync_queue WHERE status = 'PENDING' ORDER BY attempt_count ASC")
    fun getPendingItems(): List<SyncQueueEntity>

    @Query("SELECT COUNT(*) FROM sync_queue WHERE status = 'PENDING'")
    fun hasPendingItems(): Int

    @Query("UPDATE sync_queue SET status = 'SYNCED' WHERE id = :id")
    fun markSynced(id: String): Int

    @Query("UPDATE sync_queue SET status = 'FAILED', attempt_count = attempt_count + 1, last_attempt_at = :lastAttemptAt WHERE id = :id")
    fun markFailed(id: String, lastAttemptAt: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(item: SyncQueueEntity)

    @Query("DELETE FROM sync_queue WHERE id = :id")
    fun deleteItem(id: String): Int
}
