# 18. View Mode — Home Screen Dashboard

## 📌 Overview
**View Mode** is the default interactive monitoring dashboard of the MapTanim Home Screen. It renders the **exact same 2D Isometric Scenery (`FarmCanvasRenderer`)** as Edit Mode, displaying direct soil planted crops with high-resolution PNG sprites (**Carrot** 🥕, **String Beans** 🫘), grass terrain, soil tiles, surrounding fences, coconut trees, mango trees, banana trees, flowers, bushes, and rocks.

---

## 🖼️ User Interface & Layout (Landscape-First)

### 1. Top HUD Bar
- **Profile Avatar**: Circular green avatar icon (`Color(0xFF4CAF50)`).
- **Farm Selector**: Dark pill dropdown displaying farm name (`"Murcia Farm"` ▾).
- **Crops Counter Chip**: Dark pill chip displaying total active plants (`🌱 186 Crops`).
- **Harvest Counter Chip**: Orange pill chip displaying harvest-ready crops (`🚜 4 Ready to Harvest`).
- **Quick Action Buttons**:
  - **Notification Bell Icon**: Dark circular button for unread alerts.
  - **Settings Gear Icon**: Dark circular button navigating to Settings (`Routes.SETTINGS`).

### 2. Left HUD Floating Panel
- **Monitoring Card**: Dark rounded container with green radio icon, titled **"Monitoring"**, subtitle `"Full Screen"`.
- **Today's Tasks Card**: Dark rounded container with blue clipboard icon, titled **"Today's Tasks"**, subtitle `"4 Tasks"`.

### 3. Right HUD Floating Panel
- **Library Card**: Dark rounded container with book icon, titled **"Library"**.
- **Community Card**: Dark rounded container with chat bubbles icon, titled **"Community"**.

### 4. Bottom Right Action Button (Edit Mode FAB)
- **Edit Floating Action Button (FAB)**: Prominent white rounded rectangular card with a green pencil icon and `"Edit"` label. Tapping navigates seamlessly to `FarmEditorScreen` (`Routes.FARM_EDITOR`).

---

## 🔹 Shared Scenery & Layout Persistence
- **Unified Render Engine**: Utilizes `FarmCanvasRenderer` for 100% visual consistency between View Mode and Edit Mode.
- **Offline Persistence & Sync**: Displays layouts saved from Edit Mode (synced to Supabase for logged-in farmers or cached in Room local database for guest farmers).
- **Interactive Multi-Touch Engine**: Supports 1-finger camera panning and 2-finger pinch zooming (70% minimum to 400% maximum zoom).
