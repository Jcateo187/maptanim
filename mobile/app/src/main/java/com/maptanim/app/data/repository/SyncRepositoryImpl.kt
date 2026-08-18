package com.maptanim.app.data.repository

import com.maptanim.app.data.local.dao.SyncQueueDao
import com.maptanim.app.data.local.entity.SyncQueueEntity
import com.maptanim.app.data.local.entity.toDomain
import com.maptanim.app.domain.repository.SyncQueueItem
import com.maptanim.app.domain.repository.SyncRepository
import java.util.UUID

class SyncRepositoryImpl(
    private val syncQueueDao: SyncQueueDao
) : SyncRepository {

    override suspend fun enqueueSyncItem(
        tableName: String,
        recordId: String,
        operation: String,
        payload: String
    ) {
        val item = SyncQueueEntity(
            id = UUID.randomUUID().toString(),
            tableName = tableName,
            recordId = recordId,
            operation = operation,
            payload = payload,
            status = "PENDING",
            attemptCount = 0
        )
        syncQueueDao.insertItem(item)
    }

    override suspend fun getPendingItems(): List<SyncQueueItem> {
        return syncQueueDao.getPendingItems().map { it.toDomain() }
    }

    override suspend fun markSynced(itemId: String) {
        syncQueueDao.markSynced(itemId)
    }

    override suspend fun markFailed(itemId: String, lastAttemptAt: String) {
        syncQueueDao.markFailed(itemId, lastAttemptAt)
    }

    override suspend fun hasPendingItems(): Boolean {
        return syncQueueDao.hasPendingItems() > 0
    }
}
