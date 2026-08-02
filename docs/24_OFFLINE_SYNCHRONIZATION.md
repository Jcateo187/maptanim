# 24. Offline Synchronization

## 📌 Overview
MapTanim is **offline-first**. All reads and writes go to **Room SQLite** first. A `SyncWorker` powered by WorkManager pushes pending changes to Supabase when connectivity is available. No static or in-memory-only data is used — everything is persisted to Room and eventually synced to Supabase.

---

## 🔹 Offline-First Data Flow

```
User Action (e.g., SAVE CHANGES in Edit Mode)
        │
        ▼
Repository writes to Room immediately
        │
        ▼
SyncQueueEntity inserted into Room sync_queue table
        │
        ▼
UI updates via StateFlow (instant, no network needed)
        │
        ▼ (when connected)
WorkManager SyncWorker triggered
        │
        ▼
SyncWorker reads sync_queue
        │
        ▼
Executes Supabase PostgREST call (INSERT / PATCH / DELETE)
        │
        ├── Success: mark sync_queue entry as SYNCED, update Room with server response
        └── Failure: mark as PENDING, retry on next WorkManager execution
```

---

## 🔹 SyncQueueEntity

```kotlin
// SyncQueueEntity.kt
@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val tableName: String,           // e.g., "beds", "tasks", "activities"
    val recordId: String,            // PK of the record to sync
    val operation: String,           // "INSERT", "UPDATE", "DELETE"
    val payload: String,             // JSON-serialized record data
    val status: String = "PENDING",  // "PENDING", "SYNCING", "SYNCED", "FAILED"
    val attemptCount: Int = 0,
    val lastAttemptAt: String? = null,
    val createdAt: String = Instant.now().toString()
)
```

---

## 🔹 SyncWorker

```kotlin
// SyncWorker.kt
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val syncQueueDao: SyncQueueDao,
    private val cropPlotRepository: CropPlotRepository,
    private val taskRepository: TaskRepository,
    private val activityRepository: ActivityRepository,
    private val supabaseClient: SupabaseClient
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val pendingItems = syncQueueDao.getPendingItems()

        pendingItems.forEach { item ->
            try {
                syncQueueDao.setStatus(item.id, "SYNCING")
                when (item.operation) {
                    "INSERT" -> supabaseClient.postgrest[item.tableName].insert(
                        Json.parseToJsonElement(item.payload)
                    )
                    "UPDATE" -> supabaseClient.postgrest[item.tableName]
                        .update(Json.parseToJsonElement(item.payload)) {
                            filter { eq("id", item.recordId) }
                        }
                    "DELETE" -> supabaseClient.postgrest[item.tableName]
                        .delete { filter { eq("id", item.recordId) } }
                }
                syncQueueDao.setStatus(item.id, "SYNCED")
            } catch (e: Exception) {
                syncQueueDao.incrementAttempt(item.id, Instant.now().toString())
                // Item remains PENDING for next retry
            }
        }

        return if (syncQueueDao.hasFailedItems()) Result.retry() else Result.success()
    }
}
```

---

## 🔹 SyncWorker Registration

```kotlin
// SyncScheduler.kt
object SyncScheduler {
    fun scheduleSync(context: Context) {
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "supabase_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }

    // One-time sync triggered immediately after SAVE CHANGES
    fun triggerImmediateSync(context: Context) {
        val oneTimeSync = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueue(oneTimeSync)
    }
}
```

---

## 🔹 Conflict Resolution

| Scenario | Resolution |
|---------|-----------|
| Local edit, no server change | Local wins — upload to server |
| Server update while offline | Server `updated_at` timestamp compared on sync; newer wins |
| Simultaneous edit on two devices | Server timestamp wins; local Room updated with server version |
| DELETE conflict | Server delete always wins; local record removed from Room |

---

## 🔹 Repository Sync Pattern

All repository methods follow this pattern to guarantee Room persistence before network:

```kotlin
// CropPlotRepository.kt
class CropPlotRepositoryImpl @Inject constructor(
    private val cropPlotDao: CropPlotDao,
    private val syncQueueDao: SyncQueueDao,
    private val supabaseClient: SupabaseClient
) : CropPlotRepository {

    // Called when user saves Edit Mode changes
    override suspend fun savePlots(plots: List<CropPlotEntity>) {
        // 1. Write to Room immediately (offline-safe, instant UI update)
        cropPlotDao.upsertPlots(plots)

        // 2. Queue sync operations — no direct Supabase call here
        plots.forEach { plot ->
            syncQueueDao.upsert(
                SyncQueueEntity(
                    tableName = "crop_plots",
                    recordId = plot.id,
                    operation = "UPDATE",
                    payload = Json.encodeToString(plot)
                )
            )
        }

        // 3. Trigger immediate sync attempt if connected
        // (SyncWorker handles retry on failure)
    }

    // Observe plots — always reads from Room (never directly from Supabase in UI)
    override fun observePlots(farmId: String): Flow<List<CropPlotEntity>> =
        cropPlotDao.observePlots(farmId)   // Flow<List<CropPlotEntity>> from Room
}
```

---

## 🔹 Initial Data Load (First-Time Sync)

When a user logs in for the first time on a new device:

```kotlin
// AppInitializationController.kt
class AppInitializationController {
    suspend fun initialize() {
        // Pull all user data from Supabase → populate Room
        val farms = supabaseClient.postgrest["farms"].select().decodeList<FarmEntity>()
        farmDao.upsertFarms(farms)

        val plots = supabaseClient.postgrest["crop_plots"].select().decodeList<CropPlotEntity>()
        cropPlotDao.upsertPlots(plots)

        val tasks = supabaseClient.postgrest["tasks"].select { filter { eq("is_completed", false) } }.decodeList<TaskEntity>()
        taskDao.upsertTasks(tasks)

        val crops = supabaseClient.postgrest["crops"].select().decodeList<CropEntity>()
        cropDao.upsertCrops(crops)

        val dssRules = supabaseClient.postgrest["dss_rules"].select().decodeList<DssRuleEntity>()
        dssRuleDao.upsertRules(dssRules)

        // All subsequent reads come from Room — no network on every load
    }
}
```

> ✅ After initialization, the app operates fully from Room. Supabase is only contacted during sync cycles or on explicit refresh. **No screen shows data that came directly from a hardcoded source.**
