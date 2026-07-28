# 15. Render Engine — Isometric Plot Mapping Engine

## 📌 Overview
The MapTanim farm canvas uses a **high-performance 2D isometric renderer** built on Jetpack Compose `Canvas`. All rendered content — ground soil terrain, direct planted crops, high-res PNG plant foliage instances (`crop_carrot_*.png`, `crop_stringbeans_*.png`), scenery objects, status pins, and interactive resize handles — is driven by **floating-point world grid coordinates (meters)**.

---

## 🔹 Core Capabilities & Interactivity
- **Direct Soil Planting**: Tap or drag & drop crops onto the soil canvas.
- **Hold-to-Drag Positioning**: Hold any crop plot to drag and drop it anywhere inside the farm bounds.
- **Selection Overlays**: Selecting a crop plot renders an outer dashed blue boundary with 8 corner/edge resize handles.
- **No Trellises Auto-Generated**: Trellises are excluded from auto-generation; crops render direct PNG sprites on soil.
- **Camera Bounds & Zoom**: 70% min zoom limit (`minZoom = 0.70f`) and expanded panning boundaries (`maxPanX/Y = 2500f * zoom`) for smooth left-side map navigation.
- **Shared Scenery**: Shared 2D isometric canvas engine between HomeScreen (View Mode) and FarmEditorScreen (Edit Mode).
