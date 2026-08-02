# 17. Farm Management

## 📌 Overview
Farm Management covers how farms and direct soil planted crop plots (`crop_plots`), sub-regions (`crop_zones`), and support structures (`farm_objects`) are created, managed, saved, and rendered in MapTanim.

---

## 🔹 Save & Data Storage Pipeline

```
1. User clicks Save in Edit Mode
2. Layout changes are written immediately to Room SQLite DB
3. Background SyncWorker inserts sync items into SyncQueueEntity
4. If Connected -> Synchronizes layout to Supabase PostgREST remote database
5. Displays instant UI confirmation and returns to HomeScreen in View Mode
```

---

## 🔹 Crop Plot Entity (Room SQLite)

```kotlin
// CropPlotEntity.kt (Room @Entity)
@Entity(tableName = "crop_plots")
data class CropPlotEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "farm_id") val farmId: String,
    @ColumnInfo(name = "plot_label") val plotLabel: String,    // e.g., "PLOT 1", "PLOT A"
    @ColumnInfo(name = "crop_name") val cropName: String?,    // e.g., "Tomato", "Carrot"
    @ColumnInfo(name = "crop_id") val cropId: String?,        // FK to crops table
    @ColumnInfo(name = "soil_type") val soilType: String,     // LOAM, CLAY, SANDY, SILTY, PEATY, CHALKY
    @ColumnInfo(name = "pos_x") val posX: Float,              // world X meters from farm origin
    @ColumnInfo(name = "pos_y") val posY: Float,              // world Y meters from farm origin
    @ColumnInfo(name = "width_m") val widthM: Float,          // plot width in meters
    @ColumnInfo(name = "height_m") val heightM: Float,        // plot height in meters
    @ColumnInfo(name = "rotation_deg") val rotationDeg: Float = 0f,
    @ColumnInfo(name = "planted_date") val plantedDate: String?,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String
)
```
