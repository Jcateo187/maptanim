# 20. Decision Support System (DSS) & Crop Monitoring

> 📌 **Navigation**: [◀ 19. Edit Mode](file:///d:/Development/MapTanim/docs/19_EDIT_MODE.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [21. Knowledge Base ▶](file:///d:/Development/MapTanim/docs/21_KNOWLEDGE_BASE.md)

---
## 📌 Overview
The **DSS Engine** is the intelligence core of MapTanim. It evaluates farm layout, soil conditions, weather, and crop growth timelines to power two primary user-facing systems:

1. **TODAY'S TASKS (`TodaysTasksOverlay.kt`)**:
   - Real-time daily actionable tasks: 💧 **Water**, 🌿 **Fertilize**, 🌾 **Harvest**, and 🐛 **Pest Alert / Scouting**.
   - Renders task cards on the left HUD panel and active status pins floating over 2D crop plots on the farm canvas.

2. **MONITORING DASHBOARD SYSTEM (`MonitoringDashboardOverlay.kt`)**:
   - Comprehensive crop monitoring hub featuring 6 dedicated panels: **My Plants**, **Timeline**, **Calendar**, **Companions**, **Growing Tips**, and **Pest & Disease Control**.
   - Integrates unmonitored crop tracking with a **Glowing Calendar Badge (`📅`)** floating at the top of crop labels on 2D map plots.

---

## 🔹 DSS Inputs

| Input | Source | Example |
|-------|--------|---------|
| Plot layout | Room DB → `crop_plots` | PLOT 1 — Carrot, LOAM, 3m × 2m |
| Crop data | Catalog → `AVAILABLE_CROP_CATALOG` | Carrot: 75 days to harvest, weekly watering |
| DSS rules | `DssEngine.kt` matrix | Tomato + Eggplant = ANTAGONIST alert |
| Current date | System clock | 2026-08-01 |
| Seasonality | Derived from system date | SEASONAL (Wet Season PH) |
| Soil type | `PlotRenderData` entity | LOAM / SANDY / SILTY / CLAY |

---

## 🔹 DSS Outputs & Navigation Breakdown

| Output | Component / Display Location | Interactive Behavior |
|--------|------------------------------|----------------------|
| **WATER Task** | `TodaysTasksOverlay.kt` (💧 Blue pill) + Canvas Pin | Tapping logs watering event & updates soil moisture |
| **FERTILIZE Task** | `TodaysTasksOverlay.kt` (🌿 Green pill) + Canvas Pin | Tapping logs fertilizer application |
| **HARVEST Task** | `TodaysTasksOverlay.kt` (🌾 Amber pill) + Canvas Pin | Tapping opens harvest yield modal |
| **PEST_ALERT Task** | `TodaysTasksOverlay.kt` (🐛 Red pill) + Canvas Pin | Tapping opens organic treatment guide |
| **Unstarted Crop Badge** | 2D Canvas (`FarmCanvasRenderer.kt` 📅 Badge) | Floating badge above crop label for unmonitored plots |
| **Monitoring Hub** | `MonitoringDashboardOverlay.kt` | Opens via Top Bar button or tapping 2D canvas plot/badge |

---

## 🔹 Monitoring Dashboard Hub Architecture

The **Monitoring Dashboard Overlay** (`MonitoringDashboardOverlay.kt` & `MonitoringViewModel.kt`) contains 6 intelligent sub-navigation views:

### 1. My Plants Panel
- Displays cards for all planted crops on active farm plots.
- Shows Crop Name, Local Name (e.g. *Karot*, *Sitaw*, *Talong*), Plot Location (e.g. `PLOT 1`), Category, and Seasonality.
- Features the **"📅 Start Monitoring"** action button for pending crops (`isMonitoringStarted == false`).

### 2. Timeline Panel
- 4-Stage visual growth progress stepper:
  1. `Seedling` (Stage 1–2)
  2. `Vegetative` (Stage 3)
  3. `Flowering / Podding` (Stage 4)
  4. `Harvest Ready` (Stage 5)
- Displays current `daysPlanted` vs `daysToHarvest` (e.g. *Day 28 of 75*).

### 3. Calendar Panel
- Planting calendar mapping crop varieties against Philippine wet and dry seasonal schedules.
- Highlights unstarted crops with glowing calendar badges requiring farmer activation.

### 4. Companion Planting Panel
- Evaluates active farm plot crop assignments against the companion matrix.
- Highlights **BENEFICIAL** pairings (e.g. *Carrot + String Beans*) and flags **ANTAGONIST** conflicts (e.g. *Tomato + Eggplant*).
- **Scope Specification**: Companion planting compatibility overlays operate within this Monitoring Dashboard Panel (and DSS overlay sheets) rather than being rendered directly over individual 2D canvas soil grid tiles. For complete specifications, see **[37_SYSTEM_SPECIFICATIONS_AND_SCOPE_REFINEMENTS.md](file:///d:/Development/MapTanim/docs/37_SYSTEM_SPECIFICATIONS_AND_SCOPE_REFINEMENTS.md)**.


### 5. Growing Tips Panel
- Delivers variety-tailored advice on soil preparation, sunlight exposure, depth, and organic fertilization.

### 6. Pest & Disease Control Panel
- Lists common pests (e.g. *Carrot Rust Fly*, *Aphids*, *Pod Borers*) alongside non-chemical Integrated Pest Management (IPM) solutions.

---

## 🔹 Today's Tasks Generation Rules

### Water Task Rule
```
IF (current_date - last_watered_date) >= crop.watering_interval_days
THEN generate WATER task for this bed
```

### Fertilize Task Rule
```
IF growth_stage IN (EARLY_VEGETATIVE, MID_VEGETATIVE, FLOWERING)
   AND (current_date - last_fertilized_date) >= crop.fertilize_interval_days
THEN generate FERTILIZE task for this bed
```

### Harvest Task Rule
```
IF (current_date - planted_date) >= crop.days_to_harvest
THEN generate HARVEST task + set harvest badge pin on canvas
```

### Pest Alert Rule
```
IF season = WET AND crop.pest_risk_season CONTAINS WET
   AND no pest check recorded in last 7 days
THEN generate PEST_ALERT task for this bed
```

---

## 🔹 Growth Stage Calculator (Variety-Driven)

> [!IMPORTANT]
> Growth stage durations are computed dynamically based on the specific **Crop Variety** (e.g. *Diamante Max F1* vs *Apollo* Tomato) as defined in **[36_CROP_VARIETY_TIMELINE_AND_SEASONALITY.md](file:///d:/Development/MapTanim/docs/36_CROP_VARIETY_TIMELINE_AND_SEASONALITY.md)**. Static hardcoded species-level stage days are no longer used.

```kotlin
// VarietyGrowthCalculator.kt
fun calculate(
    plantedDate: LocalDate,
    today: LocalDate,
    stage1Days: Int,
    stage2Days: Int,
    stage3Days: Int,
    stage4Days: Int,
    totalDurationDays: Int
): VarietyGrowthInfo {
    val daysPlanted = ChronoUnit.DAYS.between(plantedDate, today).toInt().coerceAtLeast(0)

    val stage2Threshold = stage1Days
    val stage3Threshold = stage1Days + stage2Days
    val stage4Threshold = stage1Days + stage2Days + stage3Days
    val stage5Threshold = totalDurationDays

    val (stage, stageName) = when {
        daysPlanted < stage2Threshold -> 1 to "Stage 1 (Sprout)"
        daysPlanted < stage3Threshold -> 2 to "Stage 2 (Seedling)"
        daysPlanted < stage4Threshold -> 3 to "Stage 3 (Vegetative)"
        daysPlanted < stage5Threshold -> 4 to "Stage 4 (Flowering/Fruiting)"
        else -> 5 to "Stage 5 (Harvest Ready)"
    }

    val daysRemaining = (totalDurationDays - daysPlanted).coerceAtLeast(0)
    val progress = (daysPlanted.toFloat() / totalDurationDays.toFloat()).coerceIn(0.0f, 1.0f)

    return VarietyGrowthInfo(
        currentStage = stage,
        stageName = stageName,
        daysPlanted = daysPlanted,
        daysRemainingToHarvest = daysRemaining,
        progressPercentage = progress
    )
}
```

---

## 🔹 Companion Planting Matrix

Covers all 13 high-value vegetables. BENEFICIAL = helps each other; ANTAGONIST = plant separately; NEUTRAL = no significant interaction.

| Crop A | Crop B | Relationship | Reason |
|--------|--------|-------------|--------|
| Tomato | Lettuce | BENEFICIAL | Lettuce shades soil, reduces Tomato moisture loss |
| Tomato | Carrot | BENEFICIAL | Carrot aerates soil for Tomato roots |
| Tomato | Eggplant | ANTAGONIST | Same family, share fruit borer and bacterial wilt |
| Tomato | Cabbage | ANTAGONIST | Allelopathic compounds inhibit each other |
| Eggplant | String Beans | BENEFICIAL | Beans fix nitrogen for Eggplant |
| Eggplant | Cucumber | NEUTRAL | No significant interaction |
| Cucumber | Corn | BENEFICIAL | Corn provides shade and wind protection |
| Cucumber | Potato | ANTAGONIST | Attract same blight; compete for phosphorus |
| Cabbage | Onion | BENEFICIAL | Onion scent repels cabbage loopers |
| Cabbage | String Beans | ANTAGONIST | String Beans inhibit Cabbage growth |
| Onion | Carrot | BENEFICIAL | Onion repels carrot fly; Carrot repels onion fly |
| Onion | String Beans | ANTAGONIST | Onion inhibits Bean root growth |
| Lettuce | Carrot | BENEFICIAL | Complementary harvest timing; no competition |
| Lettuce | Cucumber | BENEFICIAL | Low-height Lettuce uses space under Cucumber trellis |
| Corn | Squash | BENEFICIAL | Three Sisters combination — Squash covers ground |
| Corn | Kangkong | NEUTRAL | No interaction documented |
| Okra | Tomato | BENEFICIAL | Similar climate requirements; share space efficiently |
| Squash | Okra | NEUTRAL | No significant interaction |
| Bell Pepper | Carrot | BENEFICIAL | Carrot improves Bell Pepper root zone |
| Bell Pepper | Eggplant | ANTAGONIST | Same family; share aphid and spider mite pests |

---

## 🔹 Soil-Crop Suitability Scoring

```kotlin
// SoilSuitabilityScorer.kt
fun calculateSoilSuitability(soilType: SoilType, crop: Crop): Float {
    val score = when {
        soilType in crop.idealSoils -> 1.0f       // 100% — optimal
        soilType in crop.suitableSoils -> 0.75f   // 75% — good
        soilType in crop.toleratedSoils -> 0.50f  // 50% — marginal
        else -> 0.25f                              // 25% — poor match
    }
    return score
}
```

### Crop Soil Suitability Reference

| Crop | Ideal Soils | Suitable | Tolerated |
|------|------------|---------|----------|
| Tomato | LOAM | SANDY, SILTY | CLAY |
| Eggplant | LOAM, CLAY | SILTY | SANDY |
| Cabbage | LOAM, CLAY | SILTY | SANDY |
| Carrot | SANDY, LOAM | SILTY | — |
| Lettuce | LOAM | SILTY, CLAY | — |
| Cucumber | LOAM, SANDY | SILTY | CLAY |
| Onion | LOAM, SANDY | SILTY | CLAY |
| String Beans | LOAM | SANDY, SILTY | CLAY |
| Corn | LOAM, CLAY | SILTY | SANDY |
| Squash | LOAM, CLAY | SILTY | — |
| Okra | LOAM, SANDY | SILTY | CLAY |
| Bell Pepper | LOAM | SANDY, SILTY | CLAY |
| Kangkong | CLAY, LOAM | SILTY | SANDY |

---

## 🔹 DssEngine.kt

```kotlin
// dss/engine/DssEngine.kt
class DssEngine @Inject constructor(
    private val growthCalculator: GrowthStageCalculator,
    private val companionMatrix: CompanionPlantsMatrix,
    private val soilScorer: SoilSuitabilityScorer
) {
    fun evaluate(
        plots: List<CropPlot>,
        crops: List<Crop>,
        activities: List<Activity>,
        today: LocalDate
    ): DssResult {
        val tasks = plots.flatMap { plot ->
            val crop = crops.firstOrNull { it.name == plot.cropName } ?: return@flatMap emptyList()
            generateTasks(plot, crop, activities, today)
        }
        val companionAlerts = companionMatrix.evaluate(plots)
        val soilScores = plots.map { plot ->
            val crop = crops.firstOrNull { it.name == plot.cropName }
            SoilScore(plot.plotLabel, plot.soilType,
                if (crop != null) soilScorer.calculateSoilSuitability(plot.soilType, crop) else null)
        }
        return DssResult(tasks = tasks, companionAlerts = companionAlerts, soilScores = soilScores)
    }
}
```

---

## 📚 Related Documentation & Cross References
- 📄 [Master Documentation Hub](file:///d:/Development/MapTanim/docs/README.md)
- 📄 [00. Getting Started Guide](file:///d:/Development/MapTanim/docs/00_GETTING_STARTED.md)
- 📄 [03. System Architecture](file:///d:/Development/MapTanim/docs/03_SYSTEM_ARCHITECTURE.md)
- 📄 [17. Farm Management](file:///d:/Development/MapTanim/docs/17_FARM_MANAGEMENT.md)
- 📄 [21. Knowledge Base](file:///d:/Development/MapTanim/docs/21_KNOWLEDGE_BASE.md)
- 📄 [22. Calendar Engine](file:///d:/Development/MapTanim/docs/22_CALENDAR.md)
- 📄 [23. Notification System](file:///d:/Development/MapTanim/docs/23_NOTIFICATION_SYSTEM.md)
- 📄 [36. Crop Variety Timeline & Seasonality](file:///d:/Development/MapTanim/docs/36_CROP_VARIETY_TIMELINE_AND_SEASONALITY.md)
- 📄 [37. System Specifications & Scope Refinements](file:///d:/Development/MapTanim/docs/37_SYSTEM_SPECIFICATIONS_AND_SCOPE_REFINEMENTS.md)
