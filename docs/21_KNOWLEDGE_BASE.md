# 21. Knowledge Base — Library Screen

> 📌 **Navigation**: [◀ 20. Decision Support System](file:///d:/Development/MapTanim/docs/20_DECISION_SUPPORT_SYSTEM.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [22. Calendar Engine ▶](file:///d:/Development/MapTanim/docs/22_CALENDAR.md)

---
## 📌 Overview
The **Library Screen** corresponds to the `📖 Library` tab in the bottom navigation bar. It provides farmers with a comprehensive, offline-accessible reference database covering crop profiles, pest guides, soil information, and seasonal planting calendars.

---

## 🔹 Library Sections

### 1. Crop Profiles (13 Crops)
Each crop has a detailed profile card:

| Field | Example (Tomato) |
|-------|-----------------|
| Name | Tomato |
| Local Name | Kamatis |
| Botanical Name | Solanum lycopersicum |
| Category | Fruit Vegetable |
| Days to Harvest | 60–80 days |
| Optimal pH | 6.0–6.8 |
| Soil Suitability | Loam (ideal), Sandy, Silty |
| Season | Dry season (Oct–Feb) |
| NPK Ratio | N:1.5, P:1.0, K:2.0 |
| Companion Plants | Lettuce, Carrot |
| Avoid Planting With | Eggplant, Cabbage |
| Common Pests | Fruit borer, Tomato leaf curl virus |
| Harvest Indicators | Fruit turns red/orange; firm to touch |
| Image | From `crop-images` Supabase Storage bucket |

### 2. Pest & Disease Catalog
- Common Philippine crop pests with:
  - Name (local + scientific)
  - Affected crops
  - Identification photos (from `pest-guides` bucket)
  - Organic intervention methods
  - Chemical intervention options
  - Prevention tips

### 3. Soil Types Guide
- Descriptions of all 6 soil types (matching Edit Mode soil swatches)
- Characteristics, drainage, pH range
- Best crops per soil type

### 4. Seasonal Planting Calendar
- Philippine context: Dry season (Nov–Apr), Wet season (May–Oct)
- Per-crop planting windows
- Color-coded calendar view

### 5. DIY Support Structures & Trellising Guides
- Detailed construction references and material requirements for climbing/vining vegetables (*Ampalaya, Sitaw, Pipino*):
  - **Bamboo A-Frame Trellis**: Setup instructions, pole spacing, string netting.
  - **T-Post Wire Support**: Wire tensioning, height limits, heavy-vining crops.
  - **Single Stake / Teepee Structures**: Compact support for high-density plots.
- **Scope Decision**: Embedded strictly as offline educational reference content within the AgriLibrary, eliminating the need for interactive draggable canvas game assets. For complete details, see **[37_SYSTEM_SPECIFICATIONS_AND_SCOPE_REFINEMENTS.md](file:///d:/Development/MapTanim/docs/37_SYSTEM_SPECIFICATIONS_AND_SCOPE_REFINEMENTS.md)**.


---

## 🔹 LibraryViewModel

```kotlin
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val cropRepository: CropRepository
) : ViewModel() {

    val crops: StateFlow<List<Crop>> = cropRepository.observeAllCrops()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val searchQuery = MutableStateFlow("")

    val filteredCrops: StateFlow<List<Crop>> = combine(crops, searchQuery) { all, query ->
        if (query.isBlank()) all
        else all.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.localName?.contains(query, ignoreCase = true) == true
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
```

---

## 🔹 Offline Access
All crop data, DSS rules, pest guides, and soil info are **synced to Room** on first app launch and periodically refreshed. The Library is fully accessible **without internet connection**.

---

## 📚 Related Documentation & Cross References
- 📄 [Master Documentation Hub](file:///d:/Development/MapTanim/docs/README.md)
- 📄 [00. Getting Started Guide](file:///d:/Development/MapTanim/docs/00_GETTING_STARTED.md)
- 📄 [03. System Architecture](file:///d:/Development/MapTanim/docs/03_SYSTEM_ARCHITECTURE.md)
- 📄 [17. Farm Management](file:///d:/Development/MapTanim/docs/17_FARM_MANAGEMENT.md)
- 📄 [20. Decision Support System](file:///d:/Development/MapTanim/docs/20_DECISION_SUPPORT_SYSTEM.md)
- 📄 [22. Calendar Engine](file:///d:/Development/MapTanim/docs/22_CALENDAR.md)
- 📄 [23. Notification System](file:///d:/Development/MapTanim/docs/23_NOTIFICATION_SYSTEM.md)
- 📄 [36. Crop Variety Timeline & Seasonality](file:///d:/Development/MapTanim/docs/36_CROP_VARIETY_TIMELINE_AND_SEASONALITY.md)
- 📄 [37. System Specifications & Scope Refinements](file:///d:/Development/MapTanim/docs/37_SYSTEM_SPECIFICATIONS_AND_SCOPE_REFINEMENTS.md)
