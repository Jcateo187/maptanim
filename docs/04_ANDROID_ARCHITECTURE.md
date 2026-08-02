# 04. Android Architecture — MVVM + Clean Architecture

## 📌 Overview
MapTanim follows **MVVM (Model-View-ViewModel)** combined with **Clean Architecture** principles (Presentation → Domain → Data layers). **Hilt** is used for Dependency Injection throughout.

---

## 🏗️ Architecture Layers

```
┌─────────────────────────────────────────────────────┐
│  PRESENTATION LAYER                                 │
│  (Jetpack Compose UI + ViewModels)                  │
│                                                     │
│  HomeScreen     ──▶  HomeViewModel                  │
│  EditScreen     ──▶  EditViewModel                  │
│  AuthScreen     ──▶  AuthViewModel                  │
│  FarmsScreen    ──▶  FarmsViewModel                 │
│  CalendarScreen ──▶  CalendarViewModel              │
│  LibraryScreen  ──▶  LibraryViewModel               │
│  ProfileScreen  ──▶  ProfileViewModel               │
│  LoadingScreen  ──▶  LoadingViewModel               │
└───────────────────────┬─────────────────────────────┘
                        │ StateFlow / collectAsStateWithLifecycle
┌───────────────────────▼─────────────────────────────┐
│  DOMAIN LAYER                                       │
│  (Use Cases + Business Logic + DSS Engine)          │
│                                                     │
│  GetTodayTasksUseCase                               │
│  GetFarmSummaryUseCase                              │
│  GetFarmPlotsUseCase                                │
│  SelectPlotUseCase                                  │
│  MovePlotUseCase                                    │
│  ResizePlotUseCase                                  │
│  SaveFarmLayoutUseCase                              │
│  AddPlotUseCase                                     │
│  DeletePlotUseCase                                  │
│  ChangeSoilUseCase                                  │
│  ChangeCropUseCase                                  │
│  EvaluateDssUseCase                                 │
│  GetCompanionPlantsUseCase                          │
└───────────────────────┬─────────────────────────────┘
                        │ Repository interfaces
┌───────────────────────▼─────────────────────────────┐
│  DATA LAYER                                         │
│  (Repositories + Room + Supabase)                   │
│                                                     │
│  FarmRepository       ──▶  FarmDao (Room)           │
│                         ──▶  Supabase PostgREST      │
│  CropPlotRepository   ──▶  CropPlotDao (Room)       │
│                         ──▶  Supabase PostgREST      │
│  TaskRepository       ──▶  TaskDao (Room)           │
│  CropRepository       ──▶  CropDao (Room)           │
│  AuthRepository       ──▶  Supabase Auth            │
│  SyncRepository       ──▶  SyncQueueDao + Worker    │
└─────────────────────────────────────────────────────┘
```

---

## 🔹 Package Structure

```
com.maptanim.app/
├── core/
│   ├── constants/          # AppConstants, SuiteConstants
│   ├── datastore/          # EncryptedPreferences wrapper
│   ├── extensions/         # Kotlin extension functions
│   ├── helper/             # Utility helpers
│   └── network/            # NetworkMonitor, ConnectivityObserver
│
├── data/
│   ├── local/
│   │   ├── dao/            # FarmDao, CropPlotDao, CropZoneDao, FarmObjectDao, TaskDao, CropDao
│   │   ├── entity/         # Room @Entity classes
│   │   └── database/       # MapTanimDatabase.kt
│   ├── remote/
│   │   └── SupabaseClient.kt
│   ├── repository/         # Concrete repository implementations
│   └── sync/               # SyncWorker, SyncQueueEntity
│
├── domain/
│   ├── model/              # Pure Kotlin domain models
│   ├── repository/         # Repository interfaces
│   └── usecase/            # All use case classes
│
├── dss/
│   ├── engine/             # DssEngine.kt, RuleEvaluator.kt
│   ├── companion/          # CompanionPlantsMatrix.kt
│   ├── soil/               # SoilSuitabilityScorer.kt
│   └── growth/             # GrowthStageCalculator.kt
│
├── navigation/
│   ├── AppNavGraph.kt      # Full navigation graph
│   ├── Routes.kt           # Sealed Screen routes
│   └── BottomNavItem.kt    # Nav tab definitions
│
├── renderer/
│   ├── canvas/             # FarmCanvasRenderer.kt
│   ├── model/              # BedRenderData, CameraState
│   ├── gesture/            # CanvasGestureHandler.kt
│   └── handle/             # SelectionHandlesRenderer.kt
│
├── service/
│   └── SyncWorker.kt       # WorkManager sync worker
│
├── ui/
│   ├── screens/
│   │   ├── loading/        # LoadingScreen, LoadingViewModel
│   │   ├── auth/           # AuthScreen, AuthViewModel
│   │   ├── home/           # HomeScreen, HomeViewModel
│   │   ├── edit/           # EditScreen, EditViewModel
│   │   ├── farms/          # FarmsScreen, FarmsViewModel
│   │   ├── calendar/       # CalendarScreen, CalendarViewModel
│   │   ├── library/        # LibraryScreen, LibraryViewModel
│   │   └── profile/        # ProfileScreen, ProfileViewModel
│   ├── components/         # Reusable Composables (see doc 14)
│   ├── dialogs/            # CropPickerDialog, DeleteConfirmDialog
│   ├── bottomsheet/        # BedDetailBottomSheet
│   ├── widget/             # WeatherWidget, NotificationBell
│   └── theme/              # Color.kt, Typography.kt, Theme.kt
│
└── viewmodel/              # Shared ViewModels (if any cross-screen)
```

---

## 🔹 HomeViewModel

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTodayTasksUseCase: GetTodayTasksUseCase,
    private val getFarmSummaryUseCase: GetFarmSummaryUseCase,
    private val getBedsUseCase: GetFarmBedsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadFarm(farmId: String) {
        viewModelScope.launch {
            combine(
                getTodayTasksUseCase(farmId),
                getFarmSummaryUseCase(farmId),
                getBedsUseCase(farmId)
            ) { tasks, summary, beds ->
                HomeUiState(
                    todayTasks = tasks,
                    farmSummary = summary,
                    beds = beds
                )
            }.collectLatest { _uiState.value = it }
        }
    }
}
```

---

## 🔹 EditViewModel

```kotlin
@HiltViewModel
class EditViewModel @Inject constructor(
    private val saveFarmLayoutUseCase: SaveFarmLayoutUseCase,
    private val cropPlotRepository: CropPlotRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditUiState())
    val uiState: StateFlow<EditUiState> = _uiState.asStateFlow()

    // Undo/Redo stack
    private val undoStack = ArrayDeque<EditAction>()
    private val redoStack = ArrayDeque<EditAction>()

    fun selectTool(tool: EditTool) {
        _uiState.update { it.copy(activeTool = tool, selectedBedId = null) }
    }

    fun selectBed(bedId: String) {
        _uiState.update { it.copy(selectedBedId = bedId) }
    }

    fun moveBed(bedId: String, newPosition: Offset) {
        val action = EditAction.MoveBed(bedId, currentPosition(bedId), newPosition)
        undoStack.addLast(action)
        redoStack.clear()
        applyAction(action)
        _uiState.update { it.copy(hasUnsavedChanges = true) }
    }

    fun undo() {
        val action = undoStack.removeLastOrNull() ?: return
        redoStack.addLast(action)
        reverseAction(action)
    }

    fun redo() {
        val action = redoStack.removeLastOrNull() ?: return
        undoStack.addLast(action)
        applyAction(action)
    }

    fun saveChanges(farmId: String) {
        viewModelScope.launch {
            saveFarmLayoutUseCase(farmId, _uiState.value.editedBeds)
            _uiState.update { it.copy(hasUnsavedChanges = false) }
        }
    }
}
```

---

## 🔹 Navigation Graph

```kotlin
// Routes.kt
sealed class Screen(val route: String) {
    object Splash   : Screen("splash")
    object Loading  : Screen("loading")
    object Auth     : Screen("auth")
    object Home     : Screen("home")
    object Farms    : Screen("farms")
    object Calendar : Screen("calendar")
    object Library  : Screen("library")
    object Profile  : Screen("profile")
}
```

---

## 🔹 Hilt DI Modules

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): MapTanimDatabase =
        Room.databaseBuilder(ctx, MapTanimDatabase::class.java, "maptanim.db").build()

    @Provides fun provideFarmDao(db: MapTanimDatabase): FarmDao = db.farmDao()
    @Provides fun provideCropPlotDao(db: MapTanimDatabase): CropPlotDao = db.cropPlotDao()
    @Provides fun provideCropZoneDao(db: MapTanimDatabase): CropZoneDao = db.cropZoneDao()
    @Provides fun provideFarmObjectDao(db: MapTanimDatabase): FarmObjectDao = db.farmObjectDao()
    @Provides fun provideTaskDao(db: MapTanimDatabase): TaskDao = db.taskDao()
}

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {
    @Provides @Singleton
    fun provideSupabaseClient(): SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://ojilvcglpzbtpjxguhzj.supabase.co",
        supabaseKey = "sb_publishable_fH5qY2HaAg-coV89IxOl2Q_Xf9ySGMU"
    ) {
        install(Auth) { autoLoadFromStorage = true; alwaysAutoRefresh = true }
        install(Postgrest)
        install(Storage)
    }
}
```
