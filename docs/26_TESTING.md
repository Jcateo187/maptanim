# 26. Testing Strategy

> 📌 **Navigation**: [◀ 25. Security & RLS](file:///d:/Development/MapTanim/docs/25_SECURITY.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [27. Deployment Guide ▶](file:///d:/Development/MapTanim/docs/27_DEPLOYMENT.md)

---
## 📌 Overview
MapTanim's test strategy covers three layers: **Unit Tests** (domain logic + DSS), **Compose UI Tests** (View Mode + Edit Mode interactions), and **Integration Tests** (Room ↔ Supabase sync). All tests use **real database schemas and Supabase test environment** — no fake repositories or mock data providers that diverge from production behavior.

---

## 🔹 No Mock Data Rule in Tests

> Tests use **in-memory Room databases** with the same schema as production, and a **Supabase local emulator** (via `supabase start`) for integration tests. No `FakeRepository`, `StubData`, or hardcoded return values that bypass the actual data layer.

Acceptable test doubles:
- `inMemoryDatabaseBuilder` (Room) — uses real DAO/schema, just in-memory storage
- `supabase start` local container — real PostgreSQL with real RLS
- `TestCoroutineScheduler` / `UnconfinedTestDispatcher` — for controlling async timing only

---

## 🔹 Unit Tests — Domain & DSS Layer

**Location**: `tests/unit/`  
**Command**: `./gradlew :mobile:app:test`

### DssEngine Tests
```kotlin
// DssEngineTest.kt
class DssEngineTest {

    private val growthCalculator = GrowthStageCalculator()
    private val companionMatrix = CompanionPlantsMatrix()
    private val soilScorer = SoilSuitabilityScorer()
    private val engine = DssEngine(growthCalculator, companionMatrix, soilScorer)

    @Test
    fun `water task generated when watering is overdue`() {
        val plot = CropPlot(
            id = "plot-1", plotLabel = "PLOT 3", cropName = "Tomato", cropId = "tomato",
            soilType = SoilType.LOAM, posX = 0f, posY = 0f, widthM = 2f, heightM = 2f,
            rotationDeg = 0f, plantedDate = LocalDate.now().minusDays(30).toString(),
            isActive = true, notes = null, createdAt = "", updatedAt = ""
        )
        val crop = Crop("Tomato", daysToHarvest = 70, wateringIntervalDays = 2)
        val lastWatered = LocalDate.now().minusDays(3) // overdue by 1 day
        val activities = listOf(Activity(type = TaskType.WATER, performedAt = lastWatered.atStartOfDay().toInstant(ZoneOffset.UTC)))

        val result = engine.evaluate(listOf(plot), listOf(crop), activities, LocalDate.now())

        assertTrue(result.tasks.any { it.taskType == TaskType.WATER && it.plotLabel == "PLOT 3" })
    }

    @Test
    fun `harvest task generated when days to harvest elapsed`() {
        val plot = CropPlot(
            id = "plot-r", plotLabel = "PLOT R", cropName = "Lettuce", cropId = "lettuce",
            soilType = SoilType.CLAY, posX = 0f, posY = 0f, widthM = 2f, heightM = 2f,
            rotationDeg = 0f, plantedDate = LocalDate.now().minusDays(55).toString(),
            isActive = true, notes = null, createdAt = "", updatedAt = ""
        )
        val crop = Crop("Lettuce", daysToHarvest = 50)

        val result = engine.evaluate(listOf(plot), listOf(crop), emptyList(), LocalDate.now())

        assertTrue(result.tasks.any { it.taskType == TaskType.HARVEST && it.plotLabel == "PLOT R" })
    }

    @Test
    fun `companion antagonist detected between Tomato and Eggplant`() {
        val plots = listOf(
            CropPlot(id = "p1", plotLabel = "PLOT 1", cropName = "Eggplant", cropId = "eggplant", soilType = SoilType.LOAM, posX = 0f, posY = 0f, widthM = 2f, heightM = 2f, rotationDeg = 0f, plantedDate = null, isActive = true, notes = null, createdAt = "", updatedAt = ""),
            CropPlot(id = "p3", plotLabel = "PLOT 3", cropName = "Tomato", cropId = "tomato", soilType = SoilType.LOAM, posX = 3f, posY = 0f, widthM = 2f, heightM = 2f, rotationDeg = 0f, plantedDate = null, isActive = true, notes = null, createdAt = "", updatedAt = "")
        )
        val result = engine.evaluate(plots, emptyList(), emptyList(), LocalDate.now())

        assertTrue(result.companionAlerts.any {
            (it.plotALabel == "PLOT 1" || it.plotBLabel == "PLOT 1") &&
            (it.plotALabel == "PLOT 3" || it.plotBLabel == "PLOT 3") &&
            it.relationship == CompanionRelation.ANTAGONIST
        })
    }

    @Test
    fun `soil suitability score is high for Tomato on Loam`() {
        val crop = Crop("Tomato", idealSoils = listOf(SoilType.LOAM))
        val score = soilScorer.calculateSoilSuitability(SoilType.LOAM, crop)
        assertEquals(1.0f, score, 0.001f)
    }
}
```

### GrowthStageCalculator Tests
```kotlin
class GrowthStageCalculatorTest {
    @Test fun `germination stage for days 0 to 6`() { ... }
    @Test fun `harvest_ready stage when planted_date + days_to_harvest elapsed`() { ... }
    @Test fun `overdue stage when 7 days past harvest window`() { ... }
}
```

---

## 🔹 Compose UI Tests — Presentation Layer

**Location**: `tests/ui/`  
**Command**: `./gradlew :mobile:app:connectedAndroidTest`

### View Mode Tests
```kotlin
// HomeScreenTest.kt
@HiltAndroidTest
class HomeScreenTest {

    @get:Rule val hiltRule = HiltAndroidRule(this)
    @get:Rule val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun todayTasksPanel_showsRealTasksFromRoom() {
        // Insert real task into Room via DAO (not hardcoded to UI)
        val task = TaskEntity(
            id = UUID.randomUUID().toString(),
            farmId = "test-farm-id",
            bedId = "bed-3",
            taskType = "WATER",
            title = "Water Bed 3",
            subLabel = "Tomato",
            dueDate = LocalDate.now().toString(),
            isCompleted = false
        )
        taskDao.insert(task) // Room in-memory test DB

        composeRule.onNodeWithText("Water Bed 3").assertIsDisplayed()
        composeRule.onNodeWithText("Tomato").assertIsDisplayed()
    }

    @Test
    fun farmSummary_displaysCorrectCounts() {
        // Insert 12 plots into Room
        repeat(12) { i -> cropPlotDao.insert(testPlot(i)) }

        composeRule.onNodeWithText("12").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Plots").assertIsDisplayed()
    }
}
```

### Edit Mode Tests
```kotlin
// EditModeTest.kt
@HiltAndroidTest
class EditModeTest {

    @Test
    fun editTools_selectMoveTool_activatesCorrectly() {
        composeRule.onNodeWithTag("tool_SELECT_MOVE").performClick()
        composeRule.onNodeWithTag("tool_SELECT_MOVE").assertIsSelected()
    }

    @Test
    fun soilSwatch_tapLoam_updatesSelectedSoil() {
        composeRule.onNodeWithTag("soil_swatch_LOAM").performClick()
        composeRule.onNodeWithTag("soil_swatch_LOAM").assertIsSelected()
    }

    @Test
    fun saveChangesButton_persistsPlotToRoom() {
        // Move PLOT 3 then tap SAVE CHANGES
        composeRule.onNodeWithTag("save_changes_button").performClick()
        val savedPlot = cropPlotDao.getPlotById("plot-3")
        assertNotNull(savedPlot)
        assertNotEquals(0f, savedPlot.posX)
    }

    @Test
    fun exitEditMode_discardsUnsavedChanges() {
        composeRule.onNodeWithTag("exit_edit_mode_button").performClick()
        // Canvas should revert to pre-edit positions
        composeRule.onNodeWithTag("farm_canvas").assertIsDisplayed()
    }

    @Test
    fun gridToggle_switchesState() {
        composeRule.onNodeWithTag("grid_toggle").performClick()
        // Verify grid state changed in ViewModel
        composeRule.onNodeWithTag("grid_toggle").assertIsOff()
    }

    @Test
    fun bedSelection_showsAllHandles() {
        composeRule.onNodeWithTag("bed_BED3").performClick()
        composeRule.onNodeWithTag("handle_drag").assertIsDisplayed()
        composeRule.onNodeWithTag("handle_delete_quick").assertIsDisplayed()
        composeRule.onNodeWithTag("handle_corner_TL").assertIsDisplayed()
        composeRule.onNodeWithTag("action_button_green").assertIsDisplayed()
    }
}
```

---

## 🔹 Integration Tests — Room ↔ Supabase

**Location**: `tests/integration/`  
**Requires**: `supabase start` running locally

```kotlin
// CropPlotSyncIntegrationTest.kt
class CropPlotSyncIntegrationTest {

    @Test
    fun savePlot_syncsToLocalSupabase() = runTest {
        val plot = CropPlotEntity(id = UUID.randomUUID().toString(), farmId = testFarmId, ...)
        cropPlotRepository.savePlots(listOf(plot))

        // Trigger sync worker
        SyncWorker(...).doWork()

        // Verify plot exists in local Supabase container
        val remotePlot = supabaseClient.postgrest["crop_plots"]
            .select { filter { eq("id", plot.id) } }
            .decodeSingleOrNull<CropPlotEntity>()
        assertNotNull(remotePlot)
        assertEquals(plot.plotLabel, remotePlot.plotLabel)
    }
}
```

---

## 🔹 Test Commands

```bash
# Run all unit tests
./gradlew :mobile:app:test

# Run connected UI tests (requires device or emulator)
./gradlew :mobile:app:connectedAndroidTest

# Run DSS unit tests only
./gradlew :mobile:app:test --tests "*.DssEngineTest"

# Run with test coverage report
./gradlew :mobile:app:testDebugUnitTestCoverage
```

---

## 🔹 Coverage Targets

| Module | Target Coverage |
|--------|---------------|
| DSS Engine | ≥90% |
| Repository Layer | ≥80% |
| ViewModels | ≥75% |
| UI Composables | ≥60% (key interactions only) |

---

## 📚 Related Documentation & Cross References
- 📄 [Master Documentation Hub](file:///d:/Development/MapTanim/docs/README.md)
- 📄 [00. Getting Started Guide](file:///d:/Development/MapTanim/docs/00_GETTING_STARTED.md)
- 📄 [03. System Architecture](file:///d:/Development/MapTanim/docs/03_SYSTEM_ARCHITECTURE.md)
- 📄 [27. Deployment Guide](file:///d:/Development/MapTanim/docs/27_DEPLOYMENT.md)
- 📄 [30. Git Workflow](file:///d:/Development/MapTanim/docs/30_GIT_WORKFLOW.md)
- 📄 [32. Changelog](file:///d:/Development/MapTanim/docs/32_CHANGELOG.md)
- 📄 [33. Roadmap](file:///d:/Development/MapTanim/docs/33_ROADMAP.md)
- 📄 [DevOps Architecture & Free CI/CD Pipelines](file:///d:/Development/MapTanim/docs/DEVOPS.md)
