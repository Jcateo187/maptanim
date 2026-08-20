# 36. Crop Variety Timeline & Seasonality Specifications

> 📌 **Navigation**: [◀ 35. Asset Planning & Sprites](file:///d:/Development/MapTanim/docs/35_ASSETS_PLANNING.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [37. System Specifications & Scope Refinements ▶](file:///d:/Development/MapTanim/docs/37_SYSTEM_SPECIFICATIONS_AND_SCOPE_REFINEMENTS.md)

---
# 📅 MapTanim Crop Variety & Seasonality Engine
## Variety-Specific Timelines, Philippine Planting Calendars & Stage Duration Rules

> **Purpose**
>
> This document defines the variety-level growth stage timeline engine, Philippine regional planting seasonality matrix, and dynamic stage calculators for MapTanim. It bridges **[34_CROP_PLANTING_AND_RESIZE_SYSTEM.md](file:///d:/Development/MapTanim/docs/34_CROP_PLANTING_AND_RESIZE_SYSTEM.md)** (farm layout) and **[35_ASSETS_PLANNING.md](file:///d:/Development/MapTanim/docs/35_ASSETS_PLANNING.md)** (asset specs) with real-world Philippine agronomic data.

---

# 1. Overview & System Philosophy

In MapTanim, crop growth duration is **NOT hardcoded at the species level**. Different registered seed varieties (*cultivars*) exhibit significantly different days-to-maturity, heat/wet tolerance, and stage transition rates.

When a farmer creates or manages a `CropZone` on the farm canvas, they select the specific **Crop Variety** (e.g. *Tomato* $\rightarrow$ *Diamante Max F1* vs *Apollo*).

The engine automatically calculates:
1. **Exact Days to Harvest** based on the chosen variety.
2. **5 Growth Stage Transition Dates** (Stage 1 Seedling $\rightarrow$ Stage 5 Harvest Ready).
3. **Philippine Regional Seasonality Compatibility** (Wet Season vs Dry Season vs Year-Round).
4. **Variety-Specific DSS Tasks** (Irrigation intervals, NPK fertilization windows, pest alert triggers).

---

# 2. Philippine Crop Variety & Growth Timeline Matrix

Below is the verified agronomic matrix for the 12 priority Philippine vegetable crops and their top registered commercial and heritage cultivars (DA-PhilRice / East-West Seed / IPB standards).

| Local Name | Crop Species | Commercial Variety | Growth Duration (Total Days) | Optimal PH Planting Season | Stage 1 (Sprout) | Stage 2 (Seedling) | Stage 3 (Vegetative) | Stage 4 (Flowering) | Stage 5 (Harvest Ready) |
|---|---|---|---|---|---|---|---|---|---|
| **Sitaw** 🫘 | String Beans | *Sandigan F1* | **48 Days** | Year-Round | Days 1–4 | Days 5–12 | Days 13–28 | Days 29–42 | Days 43–48+ |
| **Sitaw** 🫘 | String Beans | *Galante F1* | **52 Days** | Dry Season (Dec–May) | Days 1–5 | Days 6–14 | Days 15–30 | Days 31–45 | Days 46–52+ |
| **Talong** 🍆 | Eggplant | *Morena F1* | **75 Days** | Year-Round | Days 1–7 | Days 8–22 | Days 23–48 | Days 49–68 | Days 69–75+ |
| **Talong** 🍆 | Eggplant | *Dumaguete Long Purple* | **85 Days** | Wet Season (Jun–Nov) | Days 1–8 | Days 9–25 | Days 26–55 | Days 56–78 | Days 79–85+ |
| **Kamatis** 🍅 | Tomato | *Diamante Max F1* | **60 Days** | Year-Round (TyLCV Res.) | Days 1–5 | Days 6–18 | Days 19–38 | Days 39–54 | Days 55–60+ |
| **Kamatis** 🍅 | Tomato | *Apollo* | **72 Days** | Dry Season (Nov–Apr) | Days 1–6 | Days 7–21 | Days 22–45 | Days 46–65 | Days 66–72+ |
| **Karots** 🥕 | Carrot | *Terracotta F1* | **85 Days** | Cool / Highland | Days 1–7 | Days 8–24 | Days 25–56 | Days 57–78 | Days 79–85+ |
| **Karots** 🥕 | Carrot | *Kuroda Improved* | **95 Days** | Highland (Benguet) | Days 1–8 | Days 9–28 | Days 29–64 | Days 65–88 | Days 89–95+ |
| **Sibuyas** 🧅 | Red Onion | *Red Pinoy F1* | **110 Days** | Dry Season (Nov–Mar) | Days 1–10 | Days 11–32 | Days 33–72 | Days 73–100 | Days 101–110+ |
| **Sibuyas** 🧅 | Yellow Onion | *Yellow Granex* | **100 Days** | Dry Season (Nov–Feb) | Days 1–9 | Days 10–30 | Days 31–65 | Days 66–92 | Days 93–100+ |
| **Kalabasa** 🎃| Pumpkin / Squash | *Suprema F1* | **80 Days** | Year-Round | Days 1–6 | Days 7–20 | Days 21–48 | Days 49–72 | Days 73–80+ |
| **Mais** 🌽 | Sweet Corn | *Machismo F1* | **65 Days** | Year-Round (Irrigated) | Days 1–4 | Days 5–16 | Days 17–38 | Days 39–58 | Days 59–65+ |
| **Mais** 🌽 | White Corn | *IPB Var 6 (Glutinous)*| **72 Days** | Wet Season (Jun–Oct) | Days 1–5 | Days 6–18 | Days 19–42 | Days 43–64 | Days 65–72+ |
| **Repolyo** 🥬| Cabbage | *K-S Cross F1* | **60 Days** | Lowland / Heat Tol. | Days 1–5 | Days 6–18 | Days 19–38 | Days 39–54 | Days 55–60+ |
| **Pechay** 🥬 | Pechay | *Pavon* | **28 Days** | Year-Round | Days 1–3 | Days 4–10 | Days 11–20 | Days 21–25 | Days 26–28+ |
| **Ampalaya** 🥒| Bitter Gourd | *Jade Star XL F1* | **55 Days** | Year-Round | Days 1–5 | Days 6–16 | Days 17–34 | Days 35–48 | Days 49–55+ |
| **Okra** 🌿 | Okra | *Smooth Green* | **45 Days** | Wet / Dry Season | Days 1–4 | Days 5–14 | Days 15–28 | Days 29–38 | Days 39–45+ |
| **Sili** 🌶️ | Chili Pepper | *Django F1 (Siling Haba)*| **65 Days** | Year-Round | Days 1–6 | Days 7–20 | Days 21–42 | Days 43–58 | Days 59–65+ |

---

# 3. Philippine Planting Seasonality & Agro-Climatic Zones

The Decision Support System (DSS) evaluates local planting dates against Philippine weather patterns (PAGASA climate types).

### 3.1 Seasonal Classifications
1. **Wet Season (June – November)**:
   - High rainfall, high humidity, risk of typhoons and waterlogging.
   - Recommended: Bacterial wilt-resistant and virus-resistant hybrid varieties (*Morena F1*, *Diamante Max F1*, *Suprema F1*).
2. **Dry Season (December – May)**:
   - Sun-intensive, lower disease pressure, higher irrigation requirement.
   - Recommended: Heat-tolerant and sun-loving bulb/solanaceous varieties (*Red Pinoy*, *Apollo*, *Galante F1*).
3. **Year-Round (Irrigated)**:
   - Adapted varieties capable of year-round cultivation under proper soil management.

### 3.2 Seasonal Warning Matrix

If a farmer attempts to plant a variety outside its optimal season, the DSS triggers an **Agronimic Seasonality Advisory**:

```text
⚠️ SEASONAL ADVISORY
Variety: Yellow Granex (Onion)
Selected Planting Date: July 15 (Wet Season)
Warning: High risk of bulb rot and fungal infection during rainy months. 
Recommended Action: Switch to Red Pinoy F1 or delay planting to November (Dry Season).
```

---

# 4. Data Model Specifications

To support variety-level growth tracking, the database schemas are structured as follows:

### 4.1 SQLite / Supabase Table: `crop_varieties`

```sql
CREATE TABLE crop_varieties (
    id VARCHAR(50) PRIMARY KEY,
    crop_id VARCHAR(50) NOT NULL,            -- e.g. 'crop_tomato'
    variety_name VARCHAR(100) NOT NULL,      -- e.g. 'Diamante Max F1'
    local_name_ph VARCHAR(100),              -- e.g. 'Kamatis Diamante'
    growth_duration_days INT NOT NULL,       -- Total days to harvest (e.g. 60)
    stage1_days INT NOT NULL,                 -- Sprout duration (e.g. 5)
    stage2_days INT NOT NULL,                 -- Seedling duration (e.g. 13)
    stage3_days INT NOT NULL,                 -- Vegetative duration (e.g. 20)
    stage4_days INT NOT NULL,                 -- Flowering duration (e.g. 16)
    stage5_days INT NOT NULL,                 -- Harvest ready window (e.g. 6)
    optimal_seasons TEXT NOT NULL,           -- JSON array: ["WET", "DRY", "YEAR_ROUND"]
    watering_interval_days INT NOT NULL,     -- e.g. 2
    fertilize_interval_days INT NOT NULL,    -- e.g. 14
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 4.2 Updated `CropZone` Entity (Doc 34 Engine Integration)

```kotlin
data class CropZone(
    val id: String,
    val farmId: String,
    val cropId: String,               // e.g. "crop_tomato"
    val varietyId: String,            // e.g. "var_tomato_diamante_max"
    val varietyName: String,          // e.g. "Diamante Max F1"
    val positionX: Float,
    val positionY: Float,
    val width: Float,
    val height: Float,
    val plantedDate: String,          // ISO-8601 Date e.g. "2026-08-01"
    val currentStage: Int = 1,        // Computed 1..5
    val daysToHarvest: Int,           // Dynamic based on variety
    val healthStatus: String = "HEALTHY",
    val lastWatered: String? = null,
    val lastFertilized: String? = null
)
```

---

# 5. Dynamic Growth Stage Calculator

The `VarietyGrowthCalculator` utility computes real-time growth stages and estimated harvest dates without hardcoded static day thresholds.

### Kotlin Production Implementation

```kotlin
package com.maptanim.app.dss

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class VarietyGrowthInfo(
    val currentStage: Int,            // 1 to 5
    val stageName: String,            // e.g. "Stage 3 (Vegetative)"
    val daysPlanted: Int,             // Total days elapsed since planting
    val daysRemainingToHarvest: Int,  // Days left until Stage 5
    val progressPercentage: Float     // 0.0f to 1.0f overall growth progress
)

object VarietyGrowthCalculator {

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
}
```

---

# 6. UI Integration & User Planting Workflow

### 6.1 Variety Selection in Crop Tray (`CropTray.kt`)
When a user selects a crop card from the Crop Tray drawer (Doc 34):
1. A bottom sheet reveals registered **Philippine Varieties** for that crop.
2. The current recommended variety is highlighted with a green badge: `⭐ Recommended for Current Season (Dry Season)`.
3. Selecting a variety updates the placement preview label: `🥕 Carrot - Terracotta F1 (85 Days)`.

```text
┌──────────────────────────────────────────────┐
│  SELECT VARIETY: TOMATO                      │
├──────────────────────────────────────────────┤
│  🟢 Diamante Max F1                          │
│     Duration: 60 Days • Year-Round • TyLCV   │
│     [ SELECT VARIETY ]                       │
├──────────────────────────────────────────────┤
│  ⚪ Apollo                                   │
│     Duration: 72 Days • Dry Season           │
│     [ SELECT VARIETY ]                       │
└──────────────────────────────────────────────┘
```

### 6.2 Top Label Display on Farm Canvas
The floating top label (Doc 34 Section 10.2) displays the variety name:
```text
       [ 🍅 Tomato - Diamante Max F1 (Stage 3 • 42 Days Left) ]
                               /\
                              /  \
```

---

# 7. Summary & Implementation Steps

1. **Database Seed Migration**: Seed Room SQLite DB with real variety records (`crop_varieties`) for all 12 Philippine vegetable species.
2. **Entity Update**: Extend `CropZone` domain entity and DTOs to include `varietyId` and `varietyName`.
3. **UI Binding**: Integrate variety selection modal into `CropTray.kt` prior to drag-and-drop placement.
4. **DSS Engine Binding**: Update `DSSManager` to call `VarietyGrowthCalculator` using variety-specific stage duration days.

---

## 📚 Related Documentation & Cross References
- 📄 [Master Documentation Hub](file:///d:/Development/MapTanim/docs/README.md)
- 📄 [00. Getting Started Guide](file:///d:/Development/MapTanim/docs/00_GETTING_STARTED.md)
- 📄 [03. System Architecture](file:///d:/Development/MapTanim/docs/03_SYSTEM_ARCHITECTURE.md)
- 📄 [17. Farm Management](file:///d:/Development/MapTanim/docs/17_FARM_MANAGEMENT.md)
- 📄 [20. Decision Support System](file:///d:/Development/MapTanim/docs/20_DECISION_SUPPORT_SYSTEM.md)
- 📄 [21. Knowledge Base](file:///d:/Development/MapTanim/docs/21_KNOWLEDGE_BASE.md)
- 📄 [22. Calendar Engine](file:///d:/Development/MapTanim/docs/22_CALENDAR.md)
- 📄 [23. Notification System](file:///d:/Development/MapTanim/docs/23_NOTIFICATION_SYSTEM.md)
- 📄 [37. System Specifications & Scope Refinements](file:///d:/Development/MapTanim/docs/37_SYSTEM_SPECIFICATIONS_AND_SCOPE_REFINEMENTS.md)
