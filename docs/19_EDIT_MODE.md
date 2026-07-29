# 19. Edit Mode — Farm Editor

## 📌 Overview
**Edit Mode** is the interactive direct soil farm layout editor in MapTanim. It features Clash of Clans (CoC) inspired drag-and-drop crop placement, live CoC-style glowing green isometric tile highlighting, 1:1 finger tracking, clean selection outlines, and streamlined contextual bottom editing tools (**Duplicate**, **Resize**, **Delete**).

---

## 🖼️ User Interface Specifications

### 1. Top Right Navigation Bar
- **Save Button**: Green rounded pill button (`Color(0xFF2E7D32)`) with white save icon + `"Save"` label. Triggers the **Save Farm Layout** modal dialog.
- **Exit Button**: Red rounded pill button (`Color(0xFFC62828)`) with white exit icon + `"Exit"` label. Discards unsaved changes and returns to `HomeScreen`.

### 2. Right Floating Action Button
- **Add Plant / Crops Button**: Dark floating pill button with green flower icon + `"Add Plant / Crops"` label. Toggles the `CropTray` drawer.

### 3. Right Crop Selection Drawer (`CropTray`)
- **Header**: Title `"SELECT CROPS (2)"` with a close `✕` button.
- **Filter Tabs**: `All`, `Seasonal`, `Permanent` with green active underline indicator.
- **Search & Category**: Search input field (`"Search crops..."`) with search icon and category filter button.
- **Drag Hint**: Light green hint box: `💡 Hold & drag crop card onto farm area to plant`.
- **Crop Cards**:
  - **Carrot Card**: Light green card with carrot graphic, titled **"Carrot"**, subtitle `Root • Drag/Tap`.
  - **String Beans Card**: Light gray card with beans graphic, titled **"String Beans"**, subtitle `Podded • Drag/Tap`.

---

## 🎮 Interactivity & Placement Mechanics

### 1. CoC-Style Floating Drag & Drop (Right Panel)
- **Floating Preview**: Dragging a crop card from `CropTray` spawns an elevated floating circular preview layer containing the single crop Stage 1 PNG sprite (`crop_carrot_1.png` / `crop_stringbeans_1.png`) right beneath your finger.
- **Live Green Tile Highlight**: An isometric glowing green rhombus (`Color(0x554CAF50)` fill, `Color(0xFF4CAF50)` border) glides along the farm soil, snapping smoothly to the nearest grid tile in real-time.
- **Drop Placement**: Releasing the crop calculates exact world coordinates via `IsometricProjection.toWorld` + camera pan/zoom and places the Stage 1 PNG sprite directly on the soil tile.

### 2. Placed Crop Drag & Re-positioning
- **Touch & Drag**: Pressing and dragging any placed crop on the farm area locks onto it instantly with 1:1 touch-to-world position tracking (`Math.round` nearest grid rounding).
- **Live Tile Highlight**: While dragging a placed crop across ground tiles, the glowing green isometric tile highlight glides beneath it in real-time.
- **No Drifting**: Zero sliding or top-drifting. Crops follow your finger 1:1 in all directions.

### 3. Strict 30m x 30m Farm Boundary Containment
- **Edge Clamping**: All crop drops, drag re-positioning, and hover tile highlights are strictly clamped to `[0.0, 30.0 - cropSize]` meters.
- Crops and green tile highlights stop cleanly at the perimeter fences and can never be dragged into outer grass or scenery areas.

---

## 🛠️ Clean Selection & Contextual Bottom Tools

### 1. Clean Dashed Selection Outline
- Tapping a placed crop highlights it with a clean dashed blue selection border (`Color(0xFF1E88E5)`) around the crop bounds.
- All cluttering corner handle circles, midpoint dots, and center green buttons are hidden for a clean, uncluttered visual aesthetic.

### 2. Contextual Bottom Tools (`EditBottomLayout`)
Selecting a crop displays a dark floating pill toolbar at the bottom center with 3 primary tools:
- **Duplicate**: Clones the selected crop and places a copy at an adjacent tile (`+1.0m`).
- **Resize**: Toggles precision corner resize handles for adjusting plot dimensions (`widthM`, `heightM`).
- **Delete**: Red text button (`Color(0xFFEF5350)`) that removes the selected crop from the farm layout.

---

## 💾 Save & Confirmation Flow
1. Tap **Save** in top right.
2. **Save Farm Layout Dialog**: Displays input field for farm name (default: `"Murcia Farm"`).
3. Tap **Okay**: Persists layout to Supabase (authenticated user) or Room local database (guest user).
4. **Success Dialog**: Displays confirmation icon + message `"Excellent Successful set up the farm"`.
5. Tap **Okay**: Redirects to `HomeScreen` in View Mode.
