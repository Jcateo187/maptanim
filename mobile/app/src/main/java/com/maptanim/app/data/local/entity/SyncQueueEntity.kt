package com.maptanim.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maptanim.app.domain.repository.SyncQueueItem

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "table_name") val tableName: String,
    @ColumnInfo(name = "record_id") val recordId: String,
    @ColumnInfo(name = "operation") val operation: String,
    @ColumnInfo(name = "payload") val payload: String,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "attempt_count") val attemptCount: Int,
    @ColumnInfo(name = "last_attempt_at") val lastAttemptAt: String? = null
)

fun SyncQueueEntity.toDomain() = SyncQueueItem(
    id = id,
    tableName = tableName,
    recordId = recordId,
    operation = operation,
    payload = payload,
    status = status,
    attemptCount = attemptCount
)

fun SyncQueueItem.toEntity(lastAttemptAt: String? = null) = SyncQueueEntity(
    id = id,
    tableName = tableName,
    recordId = recordId,
    operation = operation,
    payload = payload,
    status = status,
    attemptCount = attemptCount,
    lastAttemptAt = lastAttemptAt
)
