# 29. Coding Standards

> 📌 **Navigation**: [◀ 28. Project Structure](file:///d:/Development/MapTanim/docs/28_PROJECT_STRUCTURE.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [30. Git Workflow ▶](file:///d:/Development/MapTanim/docs/30_GIT_WORKFLOW.md)

---
## 📌 Overview
All code in MapTanim must follow these standards. The most critical rule: **no static, mock, fake, hardcoded, or demo data in any production code path.**

---

## 🚫 No Static / Mock Data Rule

### ❌ Forbidden Patterns

```kotlin
// FORBIDDEN — hardcoded task list in ViewModel
val todayTasks = listOf(
    FarmTask(title = "Water PLOT 3", taskType = TaskType.WATER),  // ← STATIC DATA
    FarmTask(title = "Fertilize Eggplant", taskType = TaskType.FERTILIZE)
)

// FORBIDDEN — hardcoded farm summary stats
val farmSummary = FarmSummary(totalPlots = 12, totalPlants = 186)  // ← STATIC

// FORBIDDEN — hardcoded plot positions
val plots = listOf(
    CropPlot(id = "1", plotLabel = "PLOT 1", posX = 1.0f, posY = 2.0f, ...)  // ← STATIC
)

// FORBIDDEN — mock DSS tasks not from DB
fun getMockTasks() = listOf(...)  // ← MOCK

// FORBIDDEN — fake plot for preview used in production
val previewPlot = CropPlot(plotLabel = "PREVIEW", cropName = "Test Crop", ...)  // ← never call outside @Preview
```

### ✅ Required Patterns

```kotlin
// CORRECT — data from Room Flow (sourced from Supabase)
val todayTasks: StateFlow<List<FarmTask>> = taskRepository
    .observeTodayTasks(farmId = activeFarmId, date = LocalDate.now())
    .map { entities -> entities.map { it.toDomain() } }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

// CORRECT — aggregated from Room DB
val farmSummary: StateFlow<FarmSummary> = getFarmSummaryUseCase(farmId)
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FarmSummary())

// CORRECT — @Preview only (never referenced in ViewModel/Repository)
@Preview
@Composable
fun TaskRowPreview() {
    TaskRow(task = PreviewData.sampleTask) // OK in @Preview only
}
```

---

## 🔹 Kotlin Standards

### Null Safety
```kotlin
// FORBIDDEN — force unwrap
val name = user!!.name

// CORRECT — safe call + Elvis
val name = user?.name ?: "Unknown"
```

### Coroutines
```kotlin
// FORBIDDEN — GlobalScope
GlobalScope.launch { ... }

// CORRECT — viewModelScope / lifecycleScope
viewModelScope.launch { ... }

// CORRECT — use suspend functions in coroutine scope
suspend fun fetchPlots(farmId: String): List<CropPlotEntity> = withContext(Dispatchers.IO) {
    cropPlotDao.getPlots(farmId)
}
```

### StateFlow vs LiveData
- Always use `StateFlow` + `collectAsStateWithLifecycle()` — no `LiveData` in new code.

---

## 🔹 Compose Standards

### State Hoisting
```kotlin
// FORBIDDEN — state inside Composable
@Composable
fun BadExample() {
    var activeTool by remember { mutableStateOf(EditTool.SELECT_MOVE) }  // ← hoist to ViewModel
}

// CORRECT — state in ViewModel, events passed up
@Composable
fun EditToolsPanel(
    activeTool: EditTool,                  // state down
    onToolSelected: (EditTool) -> Unit    // events up
) { ... }
```

### Naming Conventions
| Type | Convention | Example |
|------|-----------|---------|
| Composable | `PascalCase` | `TodayTasksPanel` |
| Screen | `XScreen` suffix | `HomeScreen`, `EditScreen` |
| ViewModel | `XViewModel` suffix | `HomeViewModel`, `EditViewModel` |
| Route | lowercase, underscore | `"home"`, `"plot_detail/{plotId}"` |
| Repository | `XRepository` interface + `XRepositoryImpl` | `CropPlotRepository`, `CropPlotRepositoryImpl` |
| Use Case | `VerbNounUseCase` | `GetTodayTasksUseCase`, `SaveFarmLayoutUseCase` |
| Entity | `XEntity` suffix | `CropPlotEntity`, `TaskEntity` |
| Domain model | No suffix | `CropPlot`, `FarmTask`, `FarmSummary` |

### Test Tags
All interactive Compose elements must have `Modifier.testTag()` for UI tests:
```kotlin
Button(
    modifier = Modifier.testTag("save_changes_button"),
    onClick = onSaveChanges
) { ... }
```

---

## 🔹 Enum Naming
```kotlin
// CORRECT — UPPER_SNAKE_CASE for enum values
enum class SoilType { LOAM, CLAY, SANDY, SILTY, PEATY, CHALKY }
enum class TaskType { WATER, FERTILIZE, HARVEST, PEST_ALERT, APPLY_PESTICIDE }
enum class EditTool { SELECT_MOVE, ADD_PLOT, ADD_PLANT, DELETE }
enum class GrowthStage { GERMINATION, EARLY_VEGETATIVE, MID_VEGETATIVE, FLOWERING, FRUITING, HARVEST_READY, OVERDUE }
```

---

## 🔹 Repository Pattern
- Repository interfaces live in `domain/repository/`
- Repository implementations live in `data/repository/`
- Implementations must:
  1. Write to Room first
  2. Queue to SyncQueue second
  3. Never block the UI thread (use `Dispatchers.IO`)
  4. Return `Flow<T>` for observable data
  5. Return `Result<T>` for one-shot operations

---

## 🔹 Dependency Injection
- All dependencies injected via **Hilt** — no manual `new` construction in Composables or ViewModels
- `@Singleton` scope for: Database, SupabaseClient, SyncQueue
- `@ViewModelScoped` scope for: Use cases, Repositories when ViewModel-tied

---

## 🔹 Code Documentation
- All `UseCase` classes must have KDoc explaining: input, output, side effects
- All `@Entity` fields must have inline comments explaining units (e.g., `// meters`)
- All DSS rule functions must cite the agricultural source (BPI, DA-BAR, or research paper)

---

## 📚 Related Documentation & Cross References
- 📄 [Master Documentation Hub](file:///d:/Development/MapTanim/docs/README.md)
- 📄 [00. Getting Started Guide](file:///d:/Development/MapTanim/docs/00_GETTING_STARTED.md)
- 📄 [03. System Architecture](file:///d:/Development/MapTanim/docs/03_SYSTEM_ARCHITECTURE.md)
