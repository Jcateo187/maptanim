# 16. Interactive Plot Mapping Specifications

## 📌 Overview
This document specifies the exact technical interaction model for the **Direct Soil Planting & Drag-and-Drop Crop Mapping Engine** in MapTanim. It details crop selection, hold-and-drag crop positioning, selection handle overlays for resizing, crop deletion, and the complete **Save Farm Layout Flow** (Supabase sync for logged-in farmers / Room Local Storage for guest farmers) with on-screen completion confirmation.

---

## 🔹 Finalized User Workflow

```
1. Click "Add Plant/Crops" in Edit Tools Left Panel
   │
   ▼
2. Select Crop from Right Panel (CropTray: Carrot 🥕, String Beans 🫘, etc.)
   │
   ▼
3. Tap or Drag & Drop crop onto Farm Canvas Soil
   │
   ▼
4. Click "Select / Move" -> Displays outer crop selection overlay & resize handles
   │
   ▼
5. Hold & Drag Crop -> Position crop anywhere inside the farm canvas map
   │
   ▼
6. Click "Delete" -> Removes selected crop plot from farm layout
   │
   ▼
7. Click "Save" in Left Panel
   │
   ├──> Save Farm Dialog: "Type farm name"
   │    ├── Cancel  ──> Closes dialog, stays in EditScreen
   │    └── Okay    ──> Saves to Supabase (Logged-in) or Local Storage (Guest)
   │
   ▼
8. On-Screen Message: "Excellent Successful set up the farm"
   │
   └──> Click "Okay"  ──> Navigates to HomeScreen
```

---

## 🔹 Specifications
- **No Auto Trellises**: Trellises are excluded from auto-generation; trellises will be manually created as distinct assets when crops need them.
- **Hold-to-Drag**: Long-pressing / holding any crop plot enables direct drag-and-drop repositioning anywhere on the soil grid.
- **Selection Overlay**: Tapping **Select / Move** renders an isometric dashed blue boundary with 8 corner/edge resize handles around the selected crop plot.
- **Save & Sync Flow**:
  - **Authenticated Users**: Synchronizes layout directly to Supabase (`farms` & `beds` tables).
  - **Guest Users**: Stores layout in Room Local Storage (`PlantingPlotEntity`).
  - **Confirmation Dialog**: Displays `"Excellent Successful set up the farm"` before proceeding to `HomeScreen`.
