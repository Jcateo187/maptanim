package com.maptanim.app.data.sync

import android.content.Context
import androidx.work.*
import com.maptanim.app.data.remote.SupabaseClient
import com.maptanim.app.data.repository.CropPlotRepositoryImpl
import com.maptanim.app.data.repository.FarmRepositoryImpl
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.data.repository.TaskRepositoryImpl
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * SyncWorker — Background WorkManager worker responsible for dequeuing and executing
 * offline mutations and synchronizing pending farm/plot changes when network is available.
 */
class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val syncRepo = RepositoryProvider.syncRepository
        val user = try {
            SupabaseClient.client.auth.currentUserOrNull()
        } catch (e: Exception) {
            null
        } ?: return@withContext Result.success()

        val pending = try {
            syncRepo.getPendingItems()
        } catch (e: Exception) {
            emptyList()
        }

        if (pending.isEmpty()) {
            return@withContext Result.success()
        }

        var anyFailed = false

        for (item in pending) {
            try {
                when (item.tableName) {
                    "crop_plots" -> {
                        val plotRepo = RepositoryProvider.cropPlotRepository as? CropPlotRepositoryImpl
                        if (item.recordId.isNotBlank()) {
                            // If recordId represents farmId, refresh remote plots
                            plotRepo?.fetchFromRemote(item.recordId)
                        }
                        syncRepo.markSynced(item.id)
                    }
                    "farms" -> {
                        val farmRepo = RepositoryProvider.farmRepository as? FarmRepositoryImpl
                        farmRepo?.fetchFromRemote(user.id)
                        syncRepo.markSynced(item.id)
                    }
                    "tasks" -> {
                        val taskRepo = RepositoryProvider.taskRepository as? TaskRepositoryImpl
                        if (item.recordId.isNotBlank()) {
                            taskRepo?.fetchFromRemote(item.recordId)
                        }
                        syncRepo.markSynced(item.id)
                    }
                    else -> {
                        syncRepo.markSynced(item.id)
                    }
                }
            } catch (e: Exception) {
                anyFailed = true
                syncRepo.markFailed(item.id, java.time.Instant.now().toString())
            }
        }

        if (anyFailed) Result.retry() else Result.success()
    }

    companion object {
        private const val SYNC_WORK_NAME = "MapTanimOfflineSyncWork"

        fun enqueuePeriodic(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        fun enqueueImmediate(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.SECONDS)
                .build()

            WorkManager.getInstance(context).enqueue(request)
        }
    }
}
