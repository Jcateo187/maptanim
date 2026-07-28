# 22. Calendar Screen

## 📌 Overview
The **Calendar Screen** corresponds to the `📅 Calendar` tab in the bottom navigation bar. It displays all farming activities, scheduled tasks, and upcoming milestones in a monthly calendar view. All data is sourced **live from Supabase via Room DB** — no static or mock data is used.

---

## 🔹 Data Sources

| Data | Source | Updated By |
|------|--------|-----------|
| Scheduled tasks | `tasks` table (Supabase → Room) | DSS `evaluate-dss` Edge Function |
| Completed activities | `activities` table (Supabase → Room) | Farmer manual logging |
| Harvest records | `harvest_records` table (Supabase → Room) | Farmer harvest confirmation |
| Planting dates | `beds.planted_date` (Supabase → Room) | Set when bed is created |
| Harvest milestones | Calculated: `beds.planted_date + crops.days_to_harvest` | Derived at runtime |

> ⚠️ **No hardcoded dates or static task lists.** All calendar entries are generated from live database records.

---

## 🔹 Calendar Views

### Monthly View
- Standard monthly grid (7 columns, 4–6 rows)
- Color-coded event dots on each date cell:
  - 💧 Blue dot = Water task(s) on that day
  - 🌿 Green dot = Fertilize task(s) on that day
  - 🌾 Amber dot = Harvest milestone
  - 🐛 Red dot = Pest alert
- Tapping a date → expands that day's task list below the calendar

### Day Detail List (below calendar on date tap)
Mirrors the TODAY'S TASKS format:
```
[💧] Water BED 3        Tomato     >
[🌿] Fertilize Eggplant Bed 1      >
```

---

## 🔹 CalendarViewModel

```kotlin
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val harvestRepository: HarvestRecordRepository,
    private val bedRepository: BedRepository,
    private val cropRepository: CropRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth: StateFlow<YearMonth> = _selectedMonth.asStateFlow()

    // Tasks for the selected month — pulled from Room (synced from Supabase)
    val monthTasks: StateFlow<Map<LocalDate, List<FarmTask>>> = combine(
        _selectedMonth,
        taskRepository.observeAllTasks()    // Flow<List<TaskEntity>>
    ) { month, tasks ->
        tasks
            .filter { YearMonth.from(LocalDate.parse(it.dueDate)) == month }
            .groupBy { LocalDate.parse(it.dueDate) }
            .mapValues { (_, taskEntities) -> taskEntities.map { it.toDomain() } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Tasks for the selected day
    val selectedDayTasks: StateFlow<List<FarmTask>> = combine(
        _selectedDate, monthTasks
    ) { date, tasks ->
        tasks[date] ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Harvest milestones — calculated from bed planting dates + crop days_to_harvest
    val harvestMilestones: StateFlow<Map<LocalDate, List<HarvestMilestone>>> =
        bedRepository.observeAllBedsWithCrop()
            .map { beds ->
                beds.mapNotNull { bed ->
                    val plantedDate = bed.plantedDate?.let { LocalDate.parse(it) } ?: return@mapNotNull null
                    val daysToHarvest = bed.crop?.daysToHarvest ?: return@mapNotNull null
                    val harvestDate = plantedDate.plusDays(daysToHarvest.toLong())
                    harvestDate to HarvestMilestone(bedLabel = bed.bedLabel, cropName = bed.cropName ?: "", harvestDate = harvestDate)
                }.groupBy({ it.first }, { it.second })
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun selectDate(date: LocalDate) { _selectedDate.value = date }
    fun nextMonth() { _selectedMonth.value = _selectedMonth.value.plusMonths(1) }
    fun previousMonth() { _selectedMonth.value = _selectedMonth.value.minusMonths(1) }
}
```

---

## 🔹 TaskDao Queries

```kotlin
@Dao
interface TaskDao {
    // Observe all tasks for the authenticated user's farms — no static data
    @Query("""
        SELECT t.* FROM tasks t
        INNER JOIN farms f ON t.farm_id = f.id
        WHERE f.farmer_id = :farmerId
        ORDER BY t.due_date ASC
    """)
    fun observeAllTasks(farmerId: String): Flow<List<TaskEntity>>

    // Tasks for a specific date range
    @Query("""
        SELECT t.* FROM tasks t
        INNER JOIN farms f ON t.farm_id = f.id
        WHERE f.farmer_id = :farmerId
          AND t.due_date BETWEEN :startDate AND :endDate
        ORDER BY t.due_date ASC
    """)
    fun observeTasksForRange(farmerId: String, startDate: String, endDate: String): Flow<List<TaskEntity>>
}
```

---

## 🔹 Adding Activities to Calendar

When a farmer completes a task:
1. `TaskEntity.is_completed` is set to `true` locally in Room
2. An `ActivityEntity` is inserted into Room with `performed_at = now()`
3. Both changes are added to `SyncQueue` for upload to Supabase
4. Calendar UI refreshes via `Flow` — no manual reload needed
