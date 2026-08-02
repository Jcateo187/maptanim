# 18. View Mode — Home Screen Dashboard

## 📌 Overview
**View Mode** is the default interactive monitoring dashboard of the MapTanim Home Screen. It renders the **exact same 2D Isometric Scenery (`FarmCanvasRenderer`)** as Edit Mode, displaying direct soil planted crops with high-resolution PNG sprites (**Carrot** 🥕, **String Beans** 🫘, **Tomato** 🍅, **Eggplant** 🍆, **Cucumber** 🥒, **Lettuce** 🥬, **Cabbage** 🥬, **Onion** 🧅, **Okra** 🌿, **Corn** 🌽, **Squash** 🎃, **Kangkong** 🌿, **Bell Pepper** 🫑), grass terrain, soil tiles, surrounding fences, coconut trees, mango trees, banana trees, flowers, bushes, and rocks.

All values rendered in View Mode are driven dynamically by ViewModel StateFlow from Room DB and Supabase — no static or hardcoded values are displayed.

---

## 🖼️ User Interface & Layout (Landscape-First)

### 1. Top HUD Bar
- **Profile Avatar**: Circular avatar icon (`Profile.avatarUrl` or default fallback).
- **Farm Selector**: Dark pill dropdown displaying dynamic farm name (`uiState.farmName` ▾).
- **Crops Counter Chip**: Dark pill chip displaying total active plants (`🌱 ${uiState.farmSummary.totalPlants} Crops`).
- **Harvest Counter Chip**: Orange pill chip displaying harvest-ready crops (`🚜 ${uiState.farmSummary.readyToHarvest} Ready to Harvest`).
- **Quick Action Buttons**:
  - **Notification Bell Icon**: Dark circular button displaying live unread count badge (`uiState.notificationCount`).
  - **Settings Gear Icon**: Dark circular button navigating to Settings (`Routes.SETTINGS`).

### 2. Left HUD Floating Panel
- **Monitoring Card**: Dark rounded container with green radio icon, titled **"Monitoring"**, subtitle `"Full Screen"`. Opens the **Monitoring Dashboard System** (`MonitoringDashboardOverlay.kt`).
- **Today's Tasks Card**: Dark rounded container with blue clipboard icon, titled **"Today's Tasks"**, subtitle `${uiState.todayTasks.size} Tasks`. Expands `TodaysTasksOverlay.kt`.

### 3. Right HUD Floating Panel
- **AgriLibrary Card**: Dark rounded container with book icon, titled **"AgriLibrary"**. Opens offline agricultural knowledge base.
- **Community Card**: Dark rounded container with chat bubbles icon, titled **"Community"**. Opens farmer forum feed.

### 4. Bottom Right Action Button (Edit Mode FAB)
- **Edit Floating Action Button (FAB)**: Prominent white rounded rectangular card with a green pencil icon and `"Edit"` label. Tapping transitions to `FarmEditorScreen` (`Routes.FARM_EDITOR`).

---

## 🔹 Shared Scenery & Layout Persistence
- **Unified Render Engine**: Utilizes `FarmCanvasRenderer` for 100% visual consistency between View Mode and Edit Mode.
- **Offline Persistence & Sync**: Displays layouts saved from Edit Mode (synced to Supabase for authenticated farmers or cached in Room local database for offline operation).
- **Interactive Multi-Touch Engine**: Supports 1-finger camera panning and 2-finger pinch zooming (70% minimum to 400% maximum zoom).
