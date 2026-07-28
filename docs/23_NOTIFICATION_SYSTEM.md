# 23. Notification System

## 📌 Overview
The Notification System drives the `🔔 3` badge visible in the top bar of **both** View Mode and Edit Mode screenshots. All notification data is sourced **live from the `notifications` table in Supabase** — no hardcoded alerts or static badge counts.

---

## 🔹 Notification Count in Top Bar

The badge count `3` seen in both PNGs represents **unread notifications** fetched from Supabase:

```kotlin
// NotificationRepository.kt
fun observeUnreadCount(farmerId: String): Flow<Int> =
    notificationDao.observeUnreadCount(farmerId)   // Room Flow — auto-updates

// NotificationDao.kt
@Query("SELECT COUNT(*) FROM notifications WHERE user_id = :userId AND is_read = 0")
fun observeUnreadCount(userId: String): Flow<Int>
```

This `Flow<Int>` is collected in the `HomeViewModel` and passed to `MapTanimTopBar` via `uiState.notificationCount`. The badge appears/disappears based on the live count — **no hardcoded value of `3`**.

---

## 🔹 Notification Types

All notification types mirror the 4 task/badge types from the View Mode canvas:

| Type | Icon | Trigger |
|------|------|---------|
| WATER | 💧 | Watering overdue for a bed |
| FERTILIZE | 🌿 | Fertilization due per growth stage |
| HARVEST | 🌾 | `planted_date + days_to_harvest` reached |
| PEST_ALERT | 🐛 | Pest calendar risk window active for crop/season |
| SYSTEM | 🔔 | App updates, sync status, admin messages |

---

## 🔹 Notification Generation Flow

```
DSS evaluate-dss Edge Function runs
        │
        ▼
Generates task records (tasks table)
        │
        ▼
For each new/overdue task → inserts notification record
        │
        ▼
Supabase Realtime pushes INSERT event to subscribed app
        │
        ▼
Room notifications table updated via WorkManager / Realtime
        │
        ▼
notificationDao.observeUnreadCount() emits new count
        │
        ▼
HomeViewModel uiState.notificationCount updates
        │
        ▼
NotificationBell badge recomposes with new count
```

---

## 🔹 Notification Entity

```kotlin
// NotificationEntity.kt
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val body: String?,
    val taskType: String?,       // matches TaskType enum name
    val isRead: Boolean,
    val createdAt: String
)
```

---

## 🔹 NotificationDao

```kotlin
@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE user_id = :userId ORDER BY created_at DESC")
    fun observeAllNotifications(userId: String): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE user_id = :userId AND is_read = 0")
    fun observeUnreadCount(userId: String): Flow<Int>

    @Query("UPDATE notifications SET is_read = 1 WHERE id = :id")
    suspend fun markRead(id: String)

    @Query("UPDATE notifications SET is_read = 1 WHERE user_id = :userId")
    suspend fun markAllRead(userId: String)

    @Upsert
    suspend fun upsertNotifications(notifications: List<NotificationEntity>)
}
```

---

## 🔹 Supabase Realtime — Notification Subscription

```kotlin
// In HomeViewModel or NotificationManager
val notificationChannel = supabaseClient.realtime.createChannel("notifications-$userId")

notificationChannel
    .on<NotificationEntity>(
        PostgresAction.Insert,
        schema = "public",
        table = "notifications"
    ) { change ->
        viewModelScope.launch {
            notificationDao.upsertNotifications(listOf(change.record))
            // Flow updates automatically — no manual refresh needed
        }
    }
    .subscribe()
```

---

## 🔹 Local AlarmManager Scheduling

For offline/background alerts (when Realtime is unavailable), `WorkManager` schedules a daily evaluation job:

```kotlin
// NotificationScheduler.kt
object NotificationScheduler {
    fun scheduleDailyEvaluation(context: Context) {
        val request = PeriodicWorkRequestBuilder<DssEvaluationWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(calculateDelayUntil6AM(), TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_dss_evaluation",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
```

The `DssEvaluationWorker` calls the `evaluate-dss` Edge Function, receives task output, inserts notifications into Room, and queues sync. No notification data is ever created from hardcoded rules in the app.

---

## 🔹 Notification Center Screen

Accessed by tapping the `🔔` bell icon in the top bar:

```
Notification Center
────────────────────────────────────
[💧] Water BED 3 overdue       2h ago  ○
[🌿] Fertilize Eggplant today  5h ago  ○
[🌾] Lettuce ready to harvest  1d ago  ●  ← unread
────────────────────────────────────
[Mark All Read]
```

- `○` = unread (filled circle indicator)
- `●` = read
- Tapping a notification: marks as read + navigates to relevant bed/task
