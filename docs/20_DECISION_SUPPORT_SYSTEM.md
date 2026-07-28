# 20. Decision Support System (DSS)

## 📌 Overview
The **DSS Engine** is the intelligence core of MapTanim. It evaluates farm and crop data to generate:
1. **TODAY'S TASKS** — The task list visible in the View Mode left panel (💧 Water, 🌿 Fertilize, 🌾 Harvest, 🐛 Pest alerts)
2. **Canvas badge pins** — Visual overlays on farm beds indicating active tasks
3. **Companion planting alerts** — Warnings when antagonist crops are placed adjacent

---

## 🔹 DSS Inputs

| Input | Source | Example |
|-------|--------|---------|
| Bed layout | Room DB → beds table | BED 3 — Tomato, LOAM, planted 2026-06-01 |
| Crop data | Room DB → crops table | Tomato: 70 days to harvest, watering every 2 days |
| DSS rules | Room DB → dss_rules table | Tomato + Eggplant = ANTAGONIST |
| Current date | System clock | 2026-07-24 |
| Season | Derived from current date | WET season (June–November PH) |
| Soil type | Bed entity | LOAM |

---

## 🔹 DSS Outputs

| Output | Display Location |
|--------|-----------------|
| WATER task | TODAY'S TASKS row (💧 blue) + canvas water pin on bed |
| FERTILIZE task | TODAY'S TASKS row (🌿 green) + canvas fertilize pin on bed |
| HARVEST task | TODAY'S TASKS row (🌾 amber) + canvas harvest pin on bed |
| PEST_ALERT task | TODAY'S TASKS row (🐛 red) + canvas pest pin on bed |
| Farm Summary | FARM SUMMARY panel (12 Beds, 186 Plants, 4 Ready, 2 Active Alerts) |

---

## 🔹 Task Generation Rules

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

## 🔹 Growth Stage Calculator

```kotlin
// GrowthStageCalculator.kt
enum class GrowthStage {
    GERMINATION,        // 0–7 days from planting
    EARLY_VEGETATIVE,   // 8–21 days
    MID_VEGETATIVE,     // 22–35 days
    FLOWERING,          // 36–50 days
    FRUITING,           // 51–70 days
    HARVEST_READY,      // >= days_to_harvest
    OVERDUE             // >= days_to_harvest + 7
}

fun calculateGrowthStage(plantedDate: LocalDate, crop: Crop, today: LocalDate): GrowthStage {
    val daysFromPlanting = ChronoUnit.DAYS.between(plantedDate, today).toInt()
    return when {
        daysFromPlanting < 7  -> GrowthStage.GERMINATION
        daysFromPlanting < 21 -> GrowthStage.EARLY_VEGETATIVE
        daysFromPlanting < 35 -> GrowthStage.MID_VEGETATIVE
        daysFromPlanting < 50 -> GrowthStage.FLOWERING
        daysFromPlanting < crop.daysToHarvest -> GrowthStage.FRUITING
        daysFromPlanting < crop.daysToHarvest + 7 -> GrowthStage.HARVEST_READY
        else -> GrowthStage.OVERDUE
    }
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
        beds: List<BedUiModel>,
        crops: List<Crop>,
        activities: List<Activity>,
        today: LocalDate
    ): DssResult {
        val tasks = beds.flatMap { bed ->
            val crop = crops.firstOrNull { it.name == bed.cropName } ?: return@flatMap emptyList()
            generateTasks(bed, crop, activities, today)
        }
        val companionAlerts = companionMatrix.evaluate(beds)
        val soilScores = beds.map { bed ->
            val crop = crops.firstOrNull { it.name == bed.cropName }
            SoilScore(bed.bedLabel, bed.soilType,
                if (crop != null) soilScorer.calculateSoilSuitability(bed.soilType, crop) else null)
        }
        return DssResult(tasks = tasks, companionAlerts = companionAlerts, soilScores = soilScores)
    }
}
```
