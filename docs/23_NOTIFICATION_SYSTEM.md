# 23. Notification System

> 📌 **Navigation**: [◀ 22. Calendar Engine](file:///d:/Development/MapTanim/docs/22_CALENDAR.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [24. Offline Synchronization ▶](file:///d:/Development/MapTanim/docs/24_OFFLINE_SYNCHRONIZATION.md)

---
## 📌 Overview
The **Notification System** powers the top bar `🔔` bell badge and the in-app Notification Center. It serves strictly as an **informational alert, reminder, and system broadcast channel**. All notification records are stored in Room SQLite (`notifications` table) and synchronized with Supabase PostgREST — no static alerts or hardcoded badge values are used.

---

## 🔹 Component Boundaries: Notifications vs. Today's Tasks

To maintain clean system architecture and avoid duplicate functionality, MapTanim enforces a strict separation of concerns between **Today's Tasks** and the **Notification System**:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                       MAPTANIM COMPONENT BOUNDARIES                         │
├──────────────────────────────────────┬──────────────────────────────────────┤
│ TODAY'S TASKS (`TodaysTasksOverlay`) │ NOTIFICATION SYSTEM (`Notification`) │
├──────────────────────────────────────┼──────────────────────────────────────┤
│ • Interactive daily execution queue  │ • Read/unread informational alerts    │
│ • Actionable: Water, Fertilize, etc. │ • Top bar `🔔` badge counter         │
│ • Completing task updates plot state │ • Deep-link navigation triggers      │
│ • Floating 2D canvas pins            │ • Pest warnings, system sync updates │
└──────────────────────────────────────┴──────────────────────────────────────┘
```

> 💡 **Key Rule**: The Notification System does **NOT** manage or complete daily farming tasks (which belong strictly to `tasks` and `TodaysTasksOverlay.kt`). Instead, notifications deliver timely alerts (e.g., high-risk pest advisories, upcoming harvest milestones, system sync status) and provide quick navigation shortcuts to the appropriate screen.

---

## 🔹 Notification Count & Top Bar Badge

The unread count badge on the top bar `🔔` icon is computed dynamically from Room DB:

```kotlin
// NotificationRepository.kt
fun observeUnreadCount(farmerId: String): Flow<Int> =
    notificationDao.observeUnreadCount(farmerId)   // Reactive Room Flow
```

```sql
-- NotificationDao query
SELECT COUNT(*) FROM notifications WHERE user_id = :userId AND is_read = 0;
```

This `Flow<Int>` is collected in `HomeViewModel` and passed to `MapTanimTopBar` via `uiState.notificationCount`. When all notifications are marked as read, the badge automatically hides — **no hardcoded count value**.

---

## 🔹 Notification Categories & Triggers

| Category | Icon | Trigger Event | Destination Screen / Target |
|---|---|---|---|
| **PEST_ADVISORY** | 🐛 | High pest/disease risk detected for active crop | AgriLibrary Pest Guide / Monitoring Hub |
| **HARVEST_REMINDER** | 🌾 | Plot approaching target harvest window | Monitoring Hub / Plot Details |
| **WEATHER_ALERT** | 🌧️ | Seasonal climate advisory (heavy rainfall/dry spell) | DSS Advisory Panel |
| **SYSTEM_SYNC** | 🔔 | Database synchronization status / account security | Profile / Settings |

---

## 🔹 Notification Generation Flow

```
DSS Rule Engine / Edge Function / System Event
                      │
                      ▼
        Inserts notification record (notifications table)
                      │
                      ▼
Supabase Realtime pushes INSERT event to Room SQLite
                      │
                      ▼
   notificationDao.observeUnreadCount() emits new count
                      │
                      ▼
    Top Bar `🔔` Badge recomposes automatically
                      │
                      ▼
   Farmer taps notification → Deep-links to target screen
```

---

## 🔹 Notification Entity Schema

```kotlin
// NotificationEntity.kt
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "body") val body: String?,
    @ColumnInfo(name = "category") val category: String,       // PEST_ADVISORY, HARVEST_REMINDER, etc.
    @ColumnInfo(name = "target_plot_id") val targetPlotId: String?,
    @ColumnInfo(name = "is_read") val isRead: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: String
)
```

---

## 🔹 NotificationDao (Room SQLite)

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

## 🔹 Notification Center Overlay

Accessed by tapping the `🔔` bell icon in the top bar:

```
Notification Center
────────────────────────────────────────────────────────────
[🐛] High Fruit Borer risk for PLOT 1 (Tomato)   2h ago  ○
[🌾] PLOT R (Lettuce) target harvest date in 3d 5h ago  ○
[🔔] Offline Sync Complete — 4 records uploaded  1d ago  ●  ← read
────────────────────────────────────────────────────────────
[Mark All as Read]
```

- `○` = Unread alert indicator (increments top bar badge counter).
- `●` = Read alert.
- **Tapping an Item**: Marks the notification as read and navigates directly to the associated plot, AgriLibrary guide, or monitoring screen.

---

## 📚 Related Documentation & Cross References
- 📄 [Master Documentation Hub](file:///d:/Development/MapTanim/docs/README.md)
- 📄 [00. Getting Started Guide](file:///d:/Development/MapTanim/docs/00_GETTING_STARTED.md)
- 📄 [03. System Architecture](file:///d:/Development/MapTanim/docs/03_SYSTEM_ARCHITECTURE.md)
- 📄 [17. Farm Management](file:///d:/Development/MapTanim/docs/17_FARM_MANAGEMENT.md)
- 📄 [20. Decision Support System](file:///d:/Development/MapTanim/docs/20_DECISION_SUPPORT_SYSTEM.md)
- 📄 [21. Knowledge Base](file:///d:/Development/MapTanim/docs/21_KNOWLEDGE_BASE.md)
- 📄 [22. Calendar Engine](file:///d:/Development/MapTanim/docs/22_CALENDAR.md)
- 📄 [36. Crop Variety Timeline & Seasonality](file:///d:/Development/MapTanim/docs/36_CROP_VARIETY_TIMELINE_AND_SEASONALITY.md)
- 📄 [37. System Specifications & Scope Refinements](file:///d:/Development/MapTanim/docs/37_SYSTEM_SPECIFICATIONS_AND_SCOPE_REFINEMENTS.md)
