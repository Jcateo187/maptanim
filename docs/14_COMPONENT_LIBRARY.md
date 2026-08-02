# 14. UI Component Library Specifications

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

### 5. `EditBottomBar` ([EditBottomBar.kt](file:///d:/Development/MapTanim/mobile/app/src/main/java/com/maptanim/app/ui/components/editcomponents/layout/EditBottomBar.kt))
- **Role**: Contextual bottom toolbar displayed when a plot or zone is selected in Edit Mode.
- **Dynamic State Spec**:
  - Dark floating pill container.
  - Contextual Actions: **Duplicate Plot**, **Resize Dimensions** (meters width/height), **Soil Type Selector**, **Delete Plot** (`DELETE` tool).

### 6. `FarmCanvas` ([FarmCanvas.kt](file:///d:/Development/MapTanim/mobile/app/src/main/java/com/maptanim/app/renderer/canvas/FarmCanvas.kt))
- **Role**: Interactive 2D Isometric Farm Canvas container.
- **Interactivity**: Low-level Compose pointer input scope supporting 60 FPS crop hold-to-drag, direct soil grid plot placement, 1-finger camera panning, and 2-finger pinch zooming.
