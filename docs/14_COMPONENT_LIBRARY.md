# 14. Component Library

## 📌 Overview
This document catalogs every reusable Jetpack Compose component used in MapTanim, mapped directly to elements visible in the View Mode (PNG 1) and Edit Mode (PNG 2) screenshots.

---

## 🔹 TopBar

### `MapTanimTopBar`
The persistent top bar across all screens. Behavior changes between View Mode and Edit Mode.

```kotlin
@Composable
fun MapTanimTopBar(
    farmName: String,
    farmLocation: String,
    isEditMode: Boolean,
    weatherText: String,
    notificationCount: Int,
    userName: String?,
    onFarmSelectorClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onUserAvatarClick: () -> Unit
)
```

**View Mode layout:**
- Left: `🌱 MAPTANIM` logo → `Murcia Farm ▼` + `📍 Murcia, Negros Occidental`
- Center: `28°C ☁️ Partly Cloudy`
- Right: `🔔 [3]` bell badge → `👤 James ▼` avatar chip

**Edit Mode layout:**
- Left: `🌱 MAPTANIM` → `Murcia Farm ▼` + location
- Center: `EditModeBadge` (green pill "✏ EDIT MODE") + subtitle "Tap a bed or item to edit"
- Right: `🔔 [3]` + avatar (no name text)

---

## 🔹 EditModeBadge

```kotlin
@Composable
fun EditModeBadge()
```
- Green pill (`#2E7D32`) background
- White pencil icon + "EDIT MODE" bold text
- Subtitle below: `"Tap a bed or item to edit"` in muted gray

---

## 🔹 FarmSelectorDropdown

```kotlin
@Composable
fun FarmSelectorDropdown(
    farmName: String,
    farmLocation: String,
    onExpand: () -> Unit
)
```
- Two-line display: farm name (bold, larger) + location (small, muted, with 📍 icon)
- Trailing `▼` chevron

---

## 🔹 WeatherWidget

```kotlin
@Composable
fun WeatherWidget(
    temperatureCelsius: Int,
    description: String,
    weatherIconRes: Int
)
```
- Weather icon (sun/cloud/rain)
- Temperature `28°C` (bold)
- Description `Partly Cloudy` (small, muted)

---

## 🔹 NotificationBell

```kotlin
@Composable
fun NotificationBell(
    count: Int,
    onClick: () -> Unit
)
```
- 🔔 Bell icon
- Red circular badge overlay with white count text (e.g., `3`)
- Badge hidden if `count == 0`

---

## 🔹 UserAvatarChip

```kotlin
@Composable
fun UserAvatarChip(
    userName: String?,   // null in Edit Mode (shows avatar only)
    avatarUrl: String?,
    onDropdownClick: () -> Unit
)
```
- Circular avatar image (or initials fallback)
- Username text (View Mode only)
- Trailing `▼` chevron

---

## 🔹 TodayTasksPanel

The left panel card showing today's farm tasks.

```kotlin
@Composable
fun TodayTasksPanel(
    tasks: List<FarmTask>,
    onTaskClick: (FarmTask) -> Unit
)
```

**Panel structure:**
- Card with header "TODAY'S TASKS" (bold, uppercase)
- Scrollable list of `TaskRow` items

---

## 🔹 TaskRow

Single row inside TodayTasksPanel.

```kotlin
@Composable
fun TaskRow(
    task: FarmTask,
    onClick: () -> Unit
)
```

**Layout:**
```
[Icon circle] | Title (bold) | Sub-label (muted) | [>]
```

**Icon colors by task type:**

| TaskType | Background | Icon |
|----------|-----------|------|
| `WATER` | Blue `#1565C0` | 💧 |
| `FERTILIZE` | Green `#388E3C` | 🌿 |
| `HARVEST` | Amber `#F57F17` | 🌾 |
| `PEST_ALERT` | Red `#C62828` | 🐛 |

---

## 🔹 FarmSummaryPanel

The left panel card showing aggregated farm counters.

```kotlin
@Composable
fun FarmSummaryPanel(summary: FarmSummary)
```

**2×2 Grid layout:**
```
[🌱 12 Beds]          [🌿 186 Plants]
[🌾 4 Ready Harvest]  [⚠️ 2 Active Alerts]
```

Each stat item:
```kotlin
@Composable
fun SummaryStatItem(icon: ImageVector, iconTint: Color, value: Int, label: String)
```

---

## 🔹 EditToolsPanel

```kotlin
@Composable
fun EditToolsPanel(
    activeTool: EditTool,
    isCollapsed: Boolean,
    onToolSelected: (EditTool) -> Unit,
    onCollapseToggle: () -> Unit
)
```

**Panel header:** "EDIT TOOLS" + `[∧]` / `[∨]` collapse button

**Tool rows:**
```kotlin
@Composable
fun EditToolRow(
    tool: EditTool,
    isActive: Boolean,
    onClick: () -> Unit
)
```
- Icon + label (bold) + description (muted small)
- Active row: light green `#E8F5E9` background
- Delete tool row: red tint icons and text

---

## 🔹 SoilTypePicker

```kotlin
@Composable
fun SoilTypePicker(
    selectedSoil: SoilType,
    onSoilSelected: (SoilType) -> Unit
)
```

**Layout:**
- Header: "SOIL TYPE"
- Row of 6 circular swatches (40dp diameter each)
- Label text below each swatch
- Tip text at bottom: `"💡 Tip: Drag the corners to resize. Long press a bed to change crop."`

**Swatch component:**
```kotlin
@Composable
fun SoilSwatch(soil: SoilType, isSelected: Boolean, onClick: () -> Unit)
```
- Selected state: white ring/border around swatch + scale up animation

---

## 🔹 FarmCanvasView

The central 2D isometric farm rendering canvas.

```kotlin
@Composable
fun FarmCanvasView(
    beds: List<BedUiModel>,
    mode: CanvasMode,               // VIEW or EDIT
    selectedBedId: String?,
    cameraState: CameraState,
    isGridVisible: Boolean,
    isSnapEnabled: Boolean,
    zoomLevel: Float,
    onBedTapped: (String) -> Unit,
    onBedMoved: (String, Offset) -> Unit,
    onBedResized: (String, Size) -> Unit
)
```

---

## 🔹 BedOverlay

Single bed rendered on the farm canvas.

```kotlin
@Composable
fun BedOverlay(
    bed: BedUiModel,
    isSelected: Boolean,
    showStatusPins: Boolean     // false in Edit Mode selection
)
```

**Sub-elements:**
- Wooden bed frame polygon
- Soil texture fill (based on `SoilType`)
- Crop illustration or placeholder
- `BedLabelChip` (dark green background, white bold text, e.g., "BED 1")
- `StatusBadgePin` overlays (conditional)

---

## 🔹 BedLabelChip

```kotlin
@Composable
fun BedLabelChip(label: String, cropName: String)
```
- Dark green `#1B5E20` background
- White bold text: "BED 1" (label) + crop name below

---

## 🔹 StatusBadgePin

A map-pin style badge floating above a bed.

```kotlin
@Composable
fun StatusBadgePin(taskType: TaskType)
```

| TaskType | Pin Color | Icon |
|----------|----------|------|
| WATER | Blue `#1E88E5` | 💧 |
| FERTILIZE | Green `#43A047` | 🌿 |
| HARVEST | Amber `#FFA000` | 🌾 |
| PEST_ALERT | Red `#E53935` | 🐛 |
| WARNING | Yellow `#FDD835` | ❗ |

---

## 🔹 SelectionHandles

Rendered on top of the selected bed in Edit Mode.

```kotlin
@Composable
fun SelectionHandles(
    bedBounds: Rect,
    rotation: Float,
    onDrag: (Offset) -> Unit,
    onCornerDrag: (corner: Int, Offset) -> Unit,
    onRotate: (Float) -> Unit,
    onDelete: () -> Unit,
    onActionButton: () -> Unit
)
```

**Handles rendered:**

| Handle | Type | Color | Position |
|--------|------|-------|---------|
| Drag | Filled circle (large, ~24dp) | Blue `#1E88E5` | Top-center |
| Rotation text | Text overlay | Dark | Right of drag handle |
| Quick delete | Filled circle with ✕ | Red `#EF5350` | Top-right |
| Corner TL | Outline circle (16dp) | White, blue border | Top-left |
| Corner TR | Outline circle (16dp) | White, blue border | Top-right |
| Corner BL | Outline circle (16dp) | White, blue border | Bottom-left |
| Corner BR | Outline circle (16dp) | White, blue border | Bottom-right |
| Mid-Left | Outline circle (12dp) | White, blue border | Left-center |
| Mid-Right | Outline circle (12dp) | White, blue border | Right-center |
| Action | Filled circle with ⊕ | Green `#43A047` | Center of bed |

---

## 🔹 ViewModeRightToolbar

```kotlin
@Composable
fun ViewModeRightToolbar(
    onAdd: () -> Unit,
    onSearch: () -> Unit,
    onCenter: () -> Unit,
    onLayers: () -> Unit
)
```

Vertical card with 4 icon buttons:
- `+` Add
- `🔍` Search
- `⊙` Center
- `🔲` Layers

---

## 🔹 EditModeRightToolbar

```kotlin
@Composable
fun EditModeRightToolbar(
    canUndo: Boolean,
    canRedo: Boolean,
    isGridEnabled: Boolean,
    isSnapEnabled: Boolean,
    zoomLevel: Float,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onGridToggle: () -> Unit,
    onSnapToggle: () -> Unit
)
```

Vertical card with:
- `↩` Undo (grayed if `canUndo == false`)
- `↪` Redo (grayed if `canRedo == false`)
- `⊞` Grid + green toggle switch
- `🔗` Snap + green toggle switch
- `🔍` Zoom % text

---

## 🔹 EditBottomBar

The full-width bottom bar shown in Edit Mode (replaces nav bar content area).

```kotlin
@Composable
fun EditBottomBar(
    selectedBedLabel: String?,
    selectedCropName: String?,
    hasSelection: Boolean,
    onExitEditMode: () -> Unit,
    onDuplicate: () -> Unit,
    onResize: () -> Unit,
    onChangeCrop: () -> Unit,
    onChangeSoil: () -> Unit,
    onDeleteBed: () -> Unit,
    onSaveChanges: () -> Unit
)
```

**Left:** `← EXIT EDIT MODE` (red) + `"Discard changes"` sub-label
**Center-left:** `◻ 1 bed selected` + `"Bed 3 • Tomato"` (when `hasSelection == true`)
**Center chips:** Duplicate | Resize | Change Crop | Change Soil | 🗑 Delete (red)
**Right:** `✓ SAVE CHANGES` dark green button + `"All changes will be saved"` sub-label

---

## 🔹 BottomNavBar

```kotlin
@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
)
```

**5 tabs:**

| Index | Icon | Label | Route |
|-------|------|-------|-------|
| 0 | 🏠 | Home | `home` |
| 1 | 🌱 | Farms | `farms` |
| 2 | 📅 | Calendar | `calendar` |
| 3 | 📖 | Library | `library` |
| 4 | 👤 | Profile | `profile` |

Active tab: green fill + underline indicator.
