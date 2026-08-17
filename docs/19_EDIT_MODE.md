# 19. Edit Mode — Farm Editor

> 📌 **Navigation**: [◀ 18. View Mode](file:///d:/Development/MapTanim/docs/18_VIEW_MODE.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [20. Decision Support System ▶](file:///d:/Development/MapTanim/docs/20_DECISION_SUPPORT_SYSTEM.md)

---
## 📌 Overview
**Edit Mode** is the interactive direct soil farm layout editor in MapTanim. It features Clash of Clans (CoC) inspired drag-and-drop plot placement, live glowing green/red isometric tile collision highlighting, 1:1 finger tracking, clean selection handles, and streamlined bottom editing tools (`SELECT_MOVE`, `ADD_PLOT`, `ADD_PLANT`, `DELETE`).

---

## 🖼️ User Interface Specifications

### 1. Top Right Action Buttons
- **Save Button**: Green rounded pill button (`Color(0xFF2E7D32)`) with save icon + `"Save"` label. Saves layout changes to Room SQLite DB and enqueues background Supabase sync.
- **Exit Button**: Red rounded pill button (`Color(0xFFC62828)`) with exit icon + `"Exit"` label. Discards unsaved layout edits and returns to View Mode (`HomeScreen`).

### 2. Right Action Button: Add Plant / Crops
- **Add Plant / Crops Button**: Floating dark pill button (`Color.Black.copy(alpha = 0.75f)`) on the right side of the canvas with flower icon + `"Add Plant / Crops"`.
- **Crop Selection Drawer (`CropTray.kt`)**: Opens when tapping **Add Plant / Crops**. Displays `SELECT CROPS (${crops.size})` header, category tabs (`All`, `Bulb`, `Stem`, `Shoot`, `Leafy`, `Flower`, `Fruit`, `Root`, `Tuber`), search filter, drag hint, and crop cards populated from the catalog.

### 3. Contextual Bottom Toolbar (`EditBottomLayout.kt`)
Active only when a direct-planted crop plot on the soil canvas is tapped/selected:
- **Duplicate**: Clones the selected crop plot and places an identical copy on adjacent soil grid coordinates (`+1.0m`).
- **Resize**: Toggles 8-point bounding box corner and edge handles to adjust plot width ($width\_m$) and height ($height\_m$).
- **Delete**: Red action button (`Color(0xFFEF5350)`) that removes the selected crop plot from the farm canvas and Room DB.

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

### 3. Contextual Bottom Toolbar & Resizing (`EditBottomLayout.kt`)
Selecting a plot or zone displays the floating bottom toolbar with contextual tools:
- **Duplicate Plot**: Clones the selected `CropPlot` and places an identical copy on adjacent soil grid coordinates (`+1.0m`).
- **Resize Plot**: Activates 8 interactive resize handles (Top-Left, Top-Center, Top-Right, Mid-Left, Mid-Right, Bottom-Left, Bottom-Center, Bottom-Right). Dragging handles expands plot `widthM` and `heightM` (meters).
- **Soil Type Selector**: Dropdown to switch soil classification (`LOAM`, `CLAY`, `SANDY`, `SILTY`, `PEATY`, `CHALKY`).
- **Delete Plot**: Red action button (`Color(0xFFEF5350)`) that removes the selected plot from Room DB.

### 4. Placed Plot Drag & Re-positioning
- **Touch & Drag**: Dragging any selected plot tracks 1:1 with world position coordinates (`Math.round` grid snap).
- **Boundary Containment**: Clamped to `[0.0, 45.0 - widthM]` meters to prevent plots from crossing farm bounds.

### 5. Onboarding & Tutorial Walkthrough System (`OldManFarmerGuideOverlay.kt`)
- **Drag-and-Drop Overlay Hiding**: All guide speech cards and pointing hands automatically hide while actively dragging crops (`isDraggingCrop == true`), preventing visual obstruction during placement gestures.
- **Post-Drop Bottom Toolbar Decision Overlay**: After dropping a crop onto the farm grid, an overlay explains the bottom contextual edit toolbar (`EditBottomLayout`) with two decision choices:
  - **"Continue Editing"**: Returns to free editor mode (`EDIT_DRAGGING_CROP`) so farmers can continue dragging, duplicating, resizing, or deleting crops.
  - **"Proceed"**: Advances to the next walkthrough step (`EDIT_CLOSE_TRAY`).
- **Interactive Pointing Targets**: Pointing hands and spotlight rings for **"TAP TO ADD CROPS"**, **"CLOSE TRAY ('X')"**, and **"CLICK SAVE"** are directly clickable, allowing users to advance by tapping guide targets directly.
- **Automatic Step Advancement**: `LaunchedEffect(isRightPanelVisible)` automatically advances `EDIT_ADD_PLANT` to `EDIT_SELECT_CROP` whenever the Crop Tray drawer is opened.

---

## 💾 Save & Confirmation Flow
1. Tap **Save** in top bar.
2. **Save Farm Layout**: Persists layout state (`crop_plots`, `crop_zones`, `farm_objects`) to Room DB and inserts `SyncQueueEntity` records for Supabase sync.
3. **Success Feedback**: Displays confirmation toast/dialog and returns to View Mode (`HomeScreen`) with live updated HUD counters.

---

## 📚 Related Documentation & Cross References
- 📄 [Master Documentation Hub](file:///d:/Development/MapTanim/docs/README.md)
- 📄 [00. Getting Started Guide](file:///d:/Development/MapTanim/docs/00_GETTING_STARTED.md)
- 📄 [03. System Architecture](file:///d:/Development/MapTanim/docs/03_SYSTEM_ARCHITECTURE.md)
- 📄 [11. App Navigation](file:///d:/Development/MapTanim/docs/11_NAVIGATION.md)
- 📄 [12. UI/UX Guidelines](file:///d:/Development/MapTanim/docs/12_UI_UX_GUIDELINES.md)
- 📄 [13. Design System](file:///d:/Development/MapTanim/docs/13_DESIGN_SYSTEM.md)
- 📄 [14. Component Library](file:///d:/Development/MapTanim/docs/14_COMPONENT_LIBRARY.md)
- 📄 [15. Render Engine](file:///d:/Development/MapTanim/docs/15_RENDER_ENGINE.md)
- 📄 [16. Interactive Plot Mapping](file:///d:/Development/MapTanim/docs/16_INTERACTIVE_PLOT_MAPPING.md)
- 📄 [18. View Mode](file:///d:/Development/MapTanim/docs/18_VIEW_MODE.md)
- 📄 [34. Direct Soil Crop Planting & Resize System](file:///d:/Development/MapTanim/docs/34_CROP_PLANTING_AND_RESIZE_SYSTEM.md)
- 📄 [35. Asset Planning & Sprites](file:///d:/Development/MapTanim/docs/35_ASSETS_PLANNING.md)
- 📄 [38. Audio & Sound Assets Planning](file:///d:/Development/MapTanim/docs/38_AUDIO_AND_SOUND_ASSETS_PLANNING.md)
- 📄 [39. Crop View Interaction & Variety Simulation](file:///d:/Development/MapTanim/docs/39_CROP_VIEW_INTERACTION_AND_VARIETY_SIMULATION.md)
