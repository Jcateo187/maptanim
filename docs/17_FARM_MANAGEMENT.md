# 17. Farm Management

## 📌 Overview
Farm Management covers how farms and direct soil planted crops are created, managed, saved, and rendered in MapTanim.

---

## 🔹 Save & Data Storage Pipeline

```
1. User clicks Save in Edit Mode
2. Save Farm Dialog prompts: "Type farm name"
3. If User is Logged-In -> Saves layout to Supabase remote database
4. If User is Guest    -> Saves layout to Room Local Storage (PlantingPlotEntity)
5. Displays on-screen message: "Excellent Successful set up the farm"
6. User clicks Okay -> Navigates to HomeScreen
```

---

## 🔹 Direct Planting Plot Entity

```kotlin
// PlantingPlotEntity.kt (Room @Entity)
@Entity(tableName = "beds")
data class PlantingPlotEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val farmId: String,
    val bedLabel: String,       // e.g., "Carrot Plot"
    val cropName: String?,      // e.g., "Carrot", "String Beans"
    val cropId: String?,        // FK to crops table
    val soilType: String,       // SoilType enum name
    val posX: Float,            // world X meters
    val posY: Float,            // world Y meters
    val widthM: Float,          // plot width in meters
    val heightM: Float,         // plot height in meters
    val rotationDeg: Float,
    val plantedDate: String?,
    val isActive: Boolean = true,
    val notes: String? = null,
    val createdAt: String,
    val updatedAt: String
)
```
