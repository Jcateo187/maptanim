# 19. Edit Mode — Farm Editor

## 📌 Overview
**Edit Mode** is the interactive direct soil farm layout editor in MapTanim. It features Clash of Clans (CoC) inspired drag-and-drop plot placement, live glowing green/red isometric tile collision highlighting, 1:1 finger tracking, clean selection handles, and streamlined bottom editing tools (`SELECT_MOVE`, `ADD_PLOT`, `ADD_PLANT`, `DELETE`).

---

## 🖼️ User Interface Specifications

### 1. Top Navigation Bar
- **Save Button**: Green rounded pill button (`Color(0xFF2E7D32)`) with white save icon + `"Save"` label. Saves layout changes to Room SQLite and triggers background Supabase sync via `SyncWorker`.
- **Exit Button**: Red rounded pill button (`Color(0xFFC62828)`) with white exit icon + `"Exit"` label. Discards unsaved changes and returns to View Mode (`HomeScreen`).

### 2. Left Edit Tools Toolbar (`LeftToolbar.kt`)
- **Select & Move (`SELECT_MOVE`)**: Tap plot to select, drag to reposition on soil grid.
- **Add Plot (`ADD_PLOT`)**: Tap empty soil grid space to instantiate a new `CropPlot`.
- **Add Plant (`ADD_PLANT`)**: Tap plot to open `CropTray` drawer and assign vegetable crops.
- **Delete (`DELETE`)**: Tap plot or object to delete with confirmation dialog.

### 3. Right Crop Selection Drawer (`CropTray.kt`)
- **Header**: Dynamic title `SELECT CROPS (${crops.size})` with a close `✕` button.
- **Filter Tabs**: `All`, `Bulb`, `Stem`, `Shoot`, `Leafy`, `Flower`, `Fruit`, `Root`, `Tuber`.
- **Search & Category Filter**: Interactive search input field with category filter dropdown.
- **Drag Hint**: Light green hint card: `💡 Hold & drag crop card onto farm area to plant`.
- **Crop Cards**: Populated dynamically from Room SQLite `crops` table (13 High-Value Philippine crops).

---

## 🎮 Interactivity & Placement Mechanics (MD 34 Architecture)

### 1. Drag & Drop Crop Placement (Right Panel)
- **Floating Preview**: Dragging a crop card from `CropTray` spawns an elevated floating circular preview layer containing the single crop Stage 1 PNG sprite right beneath your finger.
- **Live Tile Preview**:
  - **Blue/Green Border (Valid)**: Glowing isometric rhombus border indicates safe placement on empty soil tiles.
  - **Red Border (Invalid)**: Red preview border indicates collision with fences, obstacles, structures, or occupied plot bounds.
- **1×1 Drop Instantiation**: Releasing finger instantiates an initial 1×1 `CropZone` (`width = 1.0m`, `height = 1.0m`) on the farm grid within the targeted `CropPlot`.

### 2. Plot / Zone Selection & Border Highlight
- **Selection State**: Tapping an existing plot or zone sets `selectedPlotId`.
- **Selection Outline**: A clean white border (`Color(0xFFFFFFFF)`) or dashed outline surrounds the active `CropPlot`.

### 3. Contextual Bottom Toolbar & Resizing (`EditBottomBar.kt`)
Selecting a plot or zone displays the floating bottom toolbar with contextual tools:
- **Duplicate Plot**: Clones the selected `CropPlot` and places an identical copy on adjacent soil grid coordinates (`+1.0m`).
- **Resize Plot**: Activates 8 interactive resize handles (Top-Left, Top-Center, Top-Right, Mid-Left, Mid-Right, Bottom-Left, Bottom-Center, Bottom-Right). Dragging handles expands plot `widthM` and `heightM` (meters).
- **Soil Type Selector**: Dropdown to switch soil classification (`LOAM`, `CLAY`, `SANDY`, `SILTY`, `PEATY`, `CHALKY`).
- **Delete Plot**: Red action button (`Color(0xFFEF5350)`) that removes the selected plot from Room DB.

### 4. Placed Plot Drag & Re-positioning
- **Touch & Drag**: Dragging any selected plot tracks 1:1 with world position coordinates (`Math.round` grid snap).
- **Boundary Containment**: Clamped to `[0.0, 30.0 - widthM]` meters to prevent plots from crossing farm perimeter fences.

---

## 💾 Save & Confirmation Flow
1. Tap **Save** in top bar.
2. **Save Farm Layout**: Persists layout state (`crop_plots`, `crop_zones`, `farm_objects`) to Room DB and inserts `SyncQueueEntity` records for Supabase sync.
3. **Success Feedback**: Displays confirmation toast/dialog and returns to View Mode (`HomeScreen`) with live updated HUD counters.
