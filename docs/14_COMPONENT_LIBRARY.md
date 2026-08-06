# 14. UI Component Library Specifications

> 📌 **Navigation**: [◀ 13. Design System](file:///d:/Development/MapTanim/docs/13_DESIGN_SYSTEM.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [15. Render Engine ▶](file:///d:/Development/MapTanim/docs/15_RENDER_ENGINE.md)

---
## 📌 Overview
The MapTanim UI Component Library contains modular Jetpack Compose elements designed for landscape-first smartphone screens. All components receive dynamic StateFlow state from ViewModels — no hardcoded mock text or static numbers are rendered.

---

## 🧩 Core UI Components

### 1. `TopBar` ([TopBar.kt](file:///d:/Development/MapTanim/mobile/app/src/main/java/com/maptanim/app/ui/components/layout/TopBar.kt))
- **Role**: Main HUD top navigation bar for View Mode.
- **Dynamic State Spec**:
  - User Avatar: `Profile.avatarUrl` or default initial circle.
  - Farm Name Dropdown Pill: `uiState.farmName` (dynamically loaded from `farms` table via Room DB).
  - Resource Stat Chips: `🌱 ${uiState.farmSummary.totalPlants} Crops`, `🚜 ${uiState.farmSummary.readyToHarvest} Ready to Harvest`.
  - Action Icon Buttons: `NotificationBell` (with dynamic unread count `uiState.notificationCount`) and Settings Gear.

### 2. `LeftToolbar` ([LeftToolbar.kt](file:///d:/Development/MapTanim/mobile/app/src/main/java/com/maptanim/app/ui/components/layout/LeftToolbar.kt))
- **Role**: Left HUD action panel in View Mode / Edit Mode.
- **Dynamic State Spec**:
  - **Monitoring Chip**: Green radio icon, `"Monitoring"`, subtitle `"Full Screen"`.
  - **Today's Tasks Chip**: Blue clipboard icon, `"Today's Tasks"`, subtitle `${uiState.todayTasks.size} Tasks`.
  - **Edit Mode Tools Panel**: Active tool selection (`SELECT_MOVE`, `ADD_PLOT`, `ADD_PLANT`, `DELETE`).

### 3. `RightToolbar` ([RightToolbar.kt](file:///d:/Development/MapTanim/mobile/app/src/main/java/com/maptanim/app/ui/components/layout/RightToolbar.kt))
- **Role**: Right HUD panel in View Mode.
- **Dynamic State Spec**:
  - **Library Chip**: Book icon, `"AgriLibrary"` (opens offline DA/BPI crop, soil, and pest knowledge base).
  - **Community Chip**: Chat bubbles icon, `"Community"` (opens farmer forum feed).

### 4. `CropTray` ([CropTray.kt](file:///d:/Development/MapTanim/mobile/app/src/main/java/com/maptanim/app/ui/components/editcomponents/croptray/CropTray.kt))
- **Role**: Right slide-out crop selection drawer in Edit Mode.
- **Dynamic State Spec**:
  - Dynamic container title: `SELECT CROPS (${crops.size})`.
  - Category Filter Tabs: `All`, `Bulb`, `Stem`, `Shoot`, `Leafy`, `Flower`, `Fruit`, `Root`, `Tuber`.
  - Interactive Search Bar & Soil Filter dropdown.
  - Crop Cards: Dynamically populated from Room SQLite `crops` table (13 High-Value Philippine crops).

### 5. `EditBottomLayout` ([EditBottomLayout.kt](file:///d:/Development/MapTanim/mobile/app/src/main/java/com/maptanim/app/ui/components/editcomponents/layout/EditBottomLayout.kt))
- **Role**: Contextual floating bottom bar displayed when a direct-planted crop plot is selected in Edit Mode.
- **Dynamic State Spec**:
  - Dark floating pill container (`Color.Black.copy(alpha = 0.85f)`).
  - Contextual Actions: **Duplicate** (clones crop plot), **Resize** (toggles 8-point bounding box handles), and **Delete** (removes selected plot).

### 6. `FarmCanvas` ([FarmCanvas.kt](file:///d:/Development/MapTanim/mobile/app/src/main/java/com/maptanim/app/renderer/canvas/FarmCanvas.kt))
- **Role**: Interactive 2D Isometric Farm Canvas container.
- **Interactivity**: Low-level Compose pointer input scope supporting 60 FPS crop hold-to-drag, direct soil grid plot placement, 1-finger camera panning, and 2-finger pinch zooming.

---

## 📚 Related Documentation & Cross References
- 📄 [Master Documentation Hub](file:///d:/Development/MapTanim/docs/README.md)
- 📄 [00. Getting Started Guide](file:///d:/Development/MapTanim/docs/00_GETTING_STARTED.md)
- 📄 [03. System Architecture](file:///d:/Development/MapTanim/docs/03_SYSTEM_ARCHITECTURE.md)
- 📄 [11. App Navigation](file:///d:/Development/MapTanim/docs/11_NAVIGATION.md)
- 📄 [12. UI/UX Guidelines](file:///d:/Development/MapTanim/docs/12_UI_UX_GUIDELINES.md)
- 📄 [13. Design System](file:///d:/Development/MapTanim/docs/13_DESIGN_SYSTEM.md)
- 📄 [15. Render Engine](file:///d:/Development/MapTanim/docs/15_RENDER_ENGINE.md)
- 📄 [16. Interactive Plot Mapping](file:///d:/Development/MapTanim/docs/16_INTERACTIVE_PLOT_MAPPING.md)
- 📄 [18. View Mode](file:///d:/Development/MapTanim/docs/18_VIEW_MODE.md)
- 📄 [19. Edit Mode](file:///d:/Development/MapTanim/docs/19_EDIT_MODE.md)
- 📄 [34. Direct Soil Crop Planting & Resize System](file:///d:/Development/MapTanim/docs/34_CROP_PLANTING_AND_RESIZE_SYSTEM.md)
- 📄 [35. Asset Planning & Sprites](file:///d:/Development/MapTanim/docs/35_ASSETS_PLANNING.md)
- 📄 [38. Audio & Sound Assets Planning](file:///d:/Development/MapTanim/docs/38_AUDIO_AND_SOUND_ASSETS_PLANNING.md)
- 📄 [39. Crop View Interaction & Variety Simulation](file:///d:/Development/MapTanim/docs/39_CROP_VIEW_INTERACTION_AND_VARIETY_SIMULATION.md)
