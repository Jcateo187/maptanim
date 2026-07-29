# 14. UI Component Library Specifications

## 📌 Overview
The MapTanim UI Component Library contains modular Jetpack Compose elements designed for landscape-first smartphone screens.

---

## 🧩 Core UI Components

### 1. `TopBar` ([TopBar.kt](file:///d:/Development/MapTanim/mobile/app/src/main/java/com/maptanim/app/ui/components/layout/TopBar.kt))
- **Role**: Main HUD top navigation bar for View Mode.
- **Visual Spec**:
  - Green circular avatar icon (`Color(0xFF4CAF50)`).
  - Farm Name Dropdown Pill (`"Murcia Farm"` ▾).
  - Stat Chips (`🌱 186 Crops`, `🚜 4 Ready to Harvest`).
  - Action Icon Buttons: Notification Bell and Settings Gear.

### 2. `LeftToolbar` ([LeftToolbar.kt](file:///d:/Development/MapTanim/mobile/app/src/main/java/com/maptanim/app/ui/components/layout/LeftToolbar.kt))
- **Role**: Left HUD action panel in View Mode.
- **Visual Spec**:
  - **Monitoring Chip**: Green radio icon, `"Monitoring"`, subtitle `"Full Screen"`.
  - **Today's Tasks Chip**: Blue clipboard icon, `"Today's Tasks"`, subtitle `"4 Tasks"`.

### 3. `RightToolbar` ([RightToolbar.kt](file:///d:/Development/MapTanim/mobile/app/src/main/java/com/maptanim/app/ui/components/layout/RightToolbar.kt))
- **Role**: Right HUD panel in View Mode.
- **Visual Spec**:
  - **Library Chip**: Book icon, `"Library"`.
  - **Community Chip**: Chat bubbles icon, `"Community"`.

### 4. `CropTray` ([CropTray.kt](file:///d:/Development/MapTanim/mobile/app/src/main/java/com/maptanim/app/ui/components/editcomponents/croptray/CropTray.kt))
- **Role**: Right slide-out crop selection drawer in Edit Mode.
- **Visual Spec**:
  - Rounded white container with title `"SELECT CROPS (2)"` and close `✕` button.
  - Tab bar: `All`, `Seasonal`, `Permanent` with active green underline.
  - Search bar with Category filter button.
  - Light green drag instruction card: `💡 Hold & drag crop card onto farm area to plant`.
  - Crop Cards: **Carrot** (Root) and **String Beans** (Podded).

### 5. `EditBottomLayout` ([EditBottomLayout.kt](file:///d:/Development/MapTanim/mobile/app/src/main/java/com/maptanim/app/ui/components/editcomponents/layout/EditBottomLayout.kt))
- **Role**: Contextual bottom toolbar displayed when a placed crop is selected.
- **Visual Spec**:
  - Dark floating pill bar (`Color.Black.copy(alpha = 0.85f)`).
  - Actions: **Duplicate** (copy crop), **Resize** (plot dimensions), **Delete** (red text `Color(0xFFEF5350)`).

### 6. `FarmCanvas` ([FarmCanvas.kt](file:///d:/Development/MapTanim/mobile/app/src/main/java/com/maptanim/app/renderer/canvas/FarmCanvas.kt))
- **Role**: Interactive 2D Isometric Farm Canvas container.
- **Interactivity**: Low-level Compose pointer input scope supporting 60 FPS crop hold-to-drag, CoC tile highlight preview, 1-finger camera panning, and 2-finger pinch zooming.
