package com.maptanim.app.data.sync

import android.content.Context
import androidx.work.*
import com.maptanim.app.data.remote.*
import com.maptanim.app.data.remote.dto.CropPlotDto
import com.maptanim.app.data.remote.dto.FarmDto
import com.maptanim.app.data.remote.dto.HarvestRecordDto
import com.maptanim.app.data.remote.dto.TaskDto
import com.maptanim.app.data.repository.*
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

/**
 * SyncWorker — Background WorkManager worker responsible for dequeuing and executing
 * offline mutations and synchronizing pending farm/plot changes when network is available.
 */
class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val json = Json { ignoreUnknownKeys = true }

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
                        val plotRemote = CropPlotRemoteDataSource()
                        if (item.operation == "DELETE") {
                            if (item.recordId.isNotBlank()) {
                                plotRemote.deletePlot(item.recordId)
                            }
                        } else if (!item.payload.isNullOrBlank()) {
                            val dto = json.decodeFromString(CropPlotDto.serializer(), item.payload)
                            plotRemote.upsertPlot(dto)
                        }
                        syncRepo.markSynced(item.id)
                    }
                    "farms" -> {
                        val farmRemote = FarmRemoteRepository()
                        if (item.operation == "DELETE") {
                            if (item.recordId.isNotBlank()) {
                                farmRemote.deleteFarm(item.recordId)
                            }
                        } else if (!item.payload.isNullOrBlank()) {
                            val dto = json.decodeFromString(FarmDto.serializer(), item.payload)
                            farmRemote.upsertFarm(dto)
                        }
                        val farmRepo = RepositoryProvider.farmRepository as? FarmRepositoryImpl
                        farmRepo?.fetchFromRemote(user.id)
                        syncRepo.markSynced(item.id)
                    }
                    "tasks" -> {
                        val taskRemote = TaskRemoteDataSource()
                        if (item.operation == "UPDATE" && !item.payload.isNullOrBlank() && item.payload.contains("is_completed")) {
                            taskRemote.completeTask(item.recordId, java.time.Instant.now().toString())
                        } else if (!item.payload.isNullOrBlank()) {
                            val dto = json.decodeFromString(TaskDto.serializer(), item.payload)
                            taskRemote.upsertTasks(listOf(dto))
                        }
                        syncRepo.markSynced(item.id)
                    }
                    "harvest_records" -> {
                        val harvestRemote = HarvestRemoteDataSource()
                        if (!item.payload.isNullOrBlank()) {
                            val dto = json.decodeFromString(HarvestRecordDto.serializer(), item.payload)
                            harvestRemote.recordHarvest(dto)
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
