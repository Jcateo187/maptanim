# 19. Edit Mode — Farm Editor

## 📌 Overview
**Edit Mode** is the direct soil farm layout editor. It features a collapsible **EDIT TOOLS** panel on the left, a right-side **Crop Selection Panel (`CropTray`)**, hold-to-drag crop positioning, outer selection handle overlays, and a complete **Save Farm Dialog** workflow with on-screen confirmation.

---

## 🖼️ User Flow & Interactivity

1. **Add Plant/Crops**: Tap `Add Plant/Crops` in `EditLeftToolbar` to open `CropTray`.
2. **Crop Selection**: Pick active PNG sprite-backed options (**Carrot** 🥕, **String Beans** 🫘) or catalog crops.
3. **Placing & Drag-and-Drop**: Tap soil or drag-and-drop crops anywhere on the farm canvas.
4. **Hold-to-Drag**: Hold a crop plot to drag and drop it anywhere inside the farm bounds.
5. **Select / Move**: Highlights selected crop with an outer dashed blue boundary and 8 corner/edge resize handles.
6. **Delete**: Removes selected crop plot from the layout.
7. **Save Flow**:
   - Opens **Save Farm Layout** dialog: *Type farm name* (TextField, default: `"Murcia Farm"`).
   - **Cancel**: Closes dialog and stays in EditScreen.
   - **Okay**: Saves to Supabase (logged-in user) or Room Local Storage (guest user).
   - Displays confirmation message: `"Excellent Successful set up the farm"`.
   - Click **Okay** to proceed to `HomeScreen`.

---

## 🔹 Note on Trellises
Trellises are excluded from crop auto-spawning. Crop PNG sprites render directly on the soil; trellis assets will be added in future updates.
