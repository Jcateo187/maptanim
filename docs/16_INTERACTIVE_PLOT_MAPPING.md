# 16. Interactive Plot Mapping Specifications

> 📌 **Navigation**: [◀ 15. Render Engine](file:///d:/Development/MapTanim/docs/15_RENDER_ENGINE.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [17. Farm Management ▶](file:///d:/Development/MapTanim/docs/17_FARM_MANAGEMENT.md)

---
## 📌 Overview
This document specifies the exact technical interaction model for the **Direct Soil Planting, Crop Zone Expansion & Drag-and-Drop Mapping Engine** in MapTanim, aligned with the **Crop Zone Specifications (MD 34)**. It details initial 1×1 placement, hold-and-drag crop positioning, blue/red drag placement previews, white selection borders, 8-handle bounding box overlays for expanding crop zones, contextual bottom toolbar actions (**Duplicate**, **Resize**, **Delete**), and the complete **Save Farm Layout Flow**.

---

## 🔹 Finalized User Workflow (MD 34 Architecture)

```text
1. Open Crop Tray from EditScreen Floating Button
   │
   ▼
2. Select & Hold Crop Card (Carrot 🥕, String Beans 🫘, Eggplant 🍆, etc.)
   │
   ▼
3. Drag across Farm Canvas Soil (Real-time Blue/Green valid or Red invalid preview)
   │
   ▼
4. Release Finger -> Creates Initial 1×1 Crop Zone & Auto-Selects (White Border)
   │
   ▼
5. Display Bottom Toolbar (Duplicate | Resize | Delete)
   │
   ├──> Tap "Resize"    ──> Displays 8 Resize Handles (Top/Mid/Bot Corners & Edges)
   │                       Drag handle to expand width & height (Generates plant grid)
   │
   ├──> Tap "Duplicate" ──> Spawns copy of selected Crop Zone on adjacent tile (+1m)
   │
   └──> Tap "Delete"    ──> Removes selected Crop Zone from layout
   │
   ▼
6. Hold & Drag Crop Zone -> Reposition crop zone anywhere on 30m × 30m farm soil grid
   │
   ▼
7. Click "Save" -> Save Farm Dialog ("Type farm name")
   │
   ├──> Cancel ──> Closes dialog, stays in EditScreen
   └──> Okay   ──> Persists Crop Zone layout to Supabase / Room SQLite
   │
   ▼
8. Confirmation Toast / Dialog: "Excellent Successful set up the farm" -> Returns to HomeScreen
```

---

## 🔹 Visual States & Validation Rules (MD 34)

| State | Visual Indicator Graphic | Meaning & Rule |
|---|---|---|
| **Drag Placement Preview (Valid)** | 🟦 Blue Border / Glowing Green Rhombus | Safe soil grid tile position; zero collision with structures or obstacles. |
| **Drag Placement Preview (Invalid)** | 🟥 Red Border / Red Rhombus | Collides with outer fences, rocks, structures, or occupied plots. |
| **Selected Crop Zone** | ⬜ Permanent White Border / Dashed Outline | Currently active selection; displays bottom toolbar (**Duplicate**, **Resize**, **Delete**). |
| **Resize Mode Active** | ⭕ White Border + 8 Resize Handles | Bounding box active; dragging handles adjusts `width` and `height` without scaling plant sprites. |

---

## 🔹 Core Design & Mechanics (Crop Zone Architecture)
- **Standardized Asset Datasets**: Pre-made isometric PNG crop sprites are used instead of procedural assets per vegetable.
- **Crop Zone Expansion (No Sprite Scaling)**: Resizing does **NOT** enlarge individual plant sprites. Resizing expands the **Crop Zone** area (e.g. 1×1 → 2×2 → 3×3), automatically placing duplicate plant instances across all valid tiles in the zone via `PlantInstanceGenerator`.
- **8-Handle Bounding Box Overlay**: Selecting **Resize** displays 8 editable handles (Top-Left, Top-Center, Top-Right, Mid-Left, Mid-Right, Bottom-Left, Bottom-Center, Bottom-Right).
- **Single Unit Management**: All plants generated within a `CropZone` share the same crop type, planting date, growth stage, and health status.
- **Hold-to-Drag**: Long-pressing / holding any crop zone enables direct drag-and-drop repositioning anywhere on the soil grid.
- **Save & Sync Flow**:
  - **Authenticated Users**: Synchronizes layout directly to Supabase (`farms`, `crop_plots`, `crop_zones`, and `farm_objects` tables).
  - **Guest Users**: Stores layout locally in Room Database (`CropPlotEntity`, `CropZoneEntity`, `FarmObjectEntity`).
  - **Confirmation**: Persists changes instantly and returns to `HomeScreen` in View Mode.

---

## 🔹 Scope Clarification: Companion Compatibility Evaluation
- **Canvas Focus**: The 2D plot canvas focuses exclusively on direct soil planting, crop zone placement, spatial arrangement, and boundary resizing.
- **Monitoring Hub Overlay**: Evaluation of intercropping compatibility (beneficial vs. antagonistic crop pairings) is executed dynamically inside the **Monitoring Hub / Decision Support Overlay** when reviewing active farm plots.
- For complete technical specifications, see **[37_SYSTEM_SPECIFICATIONS_AND_SCOPE_REFINEMENTS.md](file:///d:/Development/MapTanim/docs/37_SYSTEM_SPECIFICATIONS_AND_SCOPE_REFINEMENTS.md)**.

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
- 📄 [18. View Mode](file:///d:/Development/MapTanim/docs/18_VIEW_MODE.md)
- 📄 [19. Edit Mode](file:///d:/Development/MapTanim/docs/19_EDIT_MODE.md)
- 📄 [34. Direct Soil Crop Planting & Resize System](file:///d:/Development/MapTanim/docs/34_CROP_PLANTING_AND_RESIZE_SYSTEM.md)
- 📄 [35. Asset Planning & Sprites](file:///d:/Development/MapTanim/docs/35_ASSETS_PLANNING.md)
- 📄 [38. Audio & Sound Assets Planning](file:///d:/Development/MapTanim/docs/38_AUDIO_AND_SOUND_ASSETS_PLANNING.md)
- 📄 [39. Crop View Interaction & Variety Simulation](file:///d:/Development/MapTanim/docs/39_CROP_VIEW_INTERACTION_AND_VARIETY_SIMULATION.md)
