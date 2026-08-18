# 15. 2D Isometric Render Engine Specifications

> 📌 **Navigation**: [◀ 14. Component Library](file:///d:/Development/MapTanim/docs/14_COMPONENT_LIBRARY.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [16. Interactive Plot Mapping ▶](file:///d:/Development/MapTanim/docs/16_INTERACTIVE_PLOT_MAPPING.md)

---
## 📌 Overview
The **MapTanim 2D Isometric Render Engine** (`FarmCanvasRenderer` & `IsometricProjection`) is a custom Compose `Canvas` rendering system built for 60 FPS performance, high-resolution direct crop sprite rendering, and Clash of Clans (CoC) inspired drag-and-drop mechanics.

---

## 📐 Isometric Coordinate Systems

### Tile Metrics & Projection
- **Standard Tile Width (`TILE_W`)**: `64.0f` pixels
- **Standard Tile Height (`TILE_H`)**: `32.0f` pixels
- **Farm Grid Bounds**: `45.0f` meters x `45.0f` meters (0f to 45f world space)

### Projection Formulas (`IsometricProjection`)
- **World to Screen**:
  $$\text{screenX} = (X - Y) \times \frac{\text{TILE\_W}}{2} \times \text{zoom} + \text{panX}$$
  $$\text{screenY} = (X + Y) \times \frac{\text{TILE\_H}}{2} \times \text{zoom} + \text{panY}$$

- **Screen to World**:
  $$\text{unpannedX} = \frac{\text{screenX} - \text{panX}}{\text{zoom}}, \quad \text{unpannedY} = \frac{\text{screenY} - \text{panY}}{\text{zoom}}$$
  $$\text{worldX} = \frac{\text{unpannedX}}{\text{TILE\_W}} + \frac{\text{unpannedY}}{\text{TILE\_H}}$$
  $$\text{worldY} = \frac{\text{unpannedY}}{\text{TILE\_H}} - \frac{\text{unpannedX}}{\text{TILE\_W}}$$

---

## 🎨 Layered Rendering Architecture (`FarmCanvasRenderer.render`)

The renderer executes a streamlined high-performance depth-sorted rendering pipeline focused on custom background scenery and direct crop sprites:

| Layer | Component | Description |
|---|---|---|
| **Layer 0** | **Background Scenery** | Renders high-resolution custom isometric background scenery image (`background_scenery/backgound_1.png`) centered on the farm map. |
| **Layer 1.5** | **CoC Tile Highlight** | Glowing semi-transparent blue (`#1E88E5`) or red (`#E53935`) rhombus preview overlay for active touch placement. Rendered in both **HomeScreen** (`CanvasMode.VIEW`) and **EditScreen** (`CanvasMode.EDIT`). |
| **Layer 2** | **Direct Planted Crops** | Crop Zone plant instances generated dynamically via `PlantInstanceGenerator` rendering 5 crop stage sprites (`1` to `5`) directly over the background scenery. |
| **Layer 3.5** | **Crop Zone Top Labels** | Floating crop name, variety, and growth day status pill badges rendered at top edge center of placed crop plots (e.g. `🥕 Carrot`, `🍆 Eggplant`). |
| **Layer 4** | **Status Pins (View Mode)** | Interactive task pins (Water, Fertilize, Harvest, Pest Alert, Unstarted Calendar Badge `📅`) floating above crops. |
| **Layer 5** | **Selection & Grid** | Selected Crop Zone white border outline, 8 interactive resize handles (Edit Mode), and 45×45 isometric white grid overlay (always visible in Edit screen; visible when crop selected in Home screen). |

> 💡 **Note**: All procedural grass/soil tile loops, procedural trees, rocks, flowers, perimeter fences, and separate trellis objects have been removed in favor of the high-res background scenery asset with direct crop sprite rendering.

## 🌾 Crop Zone & Plant Instance Rendering (MD 34 Architecture)

1. **Crop Zone Loop**: `FarmCanvasRenderer` iterates through all `CropZoneRenderData` instances in the layout.
2. **Dynamic Generation**: `PlantInstanceGenerator.generate()` calculates individual plant coordinates for every tile within `[0 until width]` and `[0 until height]`.
3. **5 Growth Stage Lifecycles**: Plant sprites dynamically select growth stage images from `crops/${cropClean}_${stage}.png` matching the 5 lifecycle phases:
   - **Stage 1**: Sprout / Germination
   - **Stage 2**: Seedling
   - **Stage 3**: Vegetative Growth
   - **Stage 4**: Flowering / Podding
   - **Stage 5**: Harvest Ready
4. **No Sprite Scaling**: Plant sprites render at constant scale `1.0f`. Resizing expands grid coverage rather than scaling images.
5. **8-Handle Bounding Box Overlay**: Selecting **Resize** from the bottom toolbar renders 8 interactive resize handles (4 corners + 4 edge midpoints) over the Crop Zone bounds.

---

## ⚡ Low-Level 60 FPS Gesture Engine (`awaitPointerEventScope`)

Replaces high-level Compose gesture wrappers with low-level pointer event handling:
1. **Selection-First Crop Drag**: Pressing down on an already-selected crop locks onto it instantly (`onDragStart`), tracking finger movement 1:1 using grid snapping. Sliding across unselected crops pans the camera smoothly without accidental crop movement.
2. **Deferred Overlap Reversion**: Releasing a finger over an overlapping location leaves the crop sitting in its **RED** floating state. Farmers can freely drag/pan the map and pinch zoom in/out while in RED state. Tapping empty map ground or tapping another crop automatically reverts the RED floating crop back to its pre-drag location (`startPos`).
3. **2-Finger Pinch Zoom**: Computes pointer distance and centroid to scale zoom smoothly between **70% minimum zoom (`0.70f`)** and **400% maximum zoom (`4.00f`)**.
4. **1-Finger Camera Pan**: Dragging empty ground pans the camera across the 45×45 farm map with 0 lag or touch-slop delay.
5. **Boundary Containment**: All crop movements and tile highlights are strictly clamped to `[0f, 45f - cropSize]` meters.

---

## 📚 Related Documentation & Cross References
- 📄 [Master Documentation Hub](file:///d:/Development/MapTanim/docs/README.md)
- 📄 [00. Getting Started Guide](file:///d:/Development/MapTanim/docs/00_GETTING_STARTED.md)
- 📄 [03. System Architecture](file:///d:/Development/MapTanim/docs/03_SYSTEM_ARCHITECTURE.md)
- 📄 [11. App Navigation](file:///d:/Development/MapTanim/docs/11_NAVIGATION.md)
- 📄 [12. UI/UX Guidelines](file:///d:/Development/MapTanim/docs/12_UI_UX_GUIDELINES.md)
- 📄 [13. Design System](file:///d:/Development/MapTanim/docs/13_DESIGN_SYSTEM.md)
- 📄 [14. Component Library](file:///d:/Development/MapTanim/docs/14_COMPONENT_LIBRARY.md)
- 📄 [16. Interactive Plot Mapping](file:///d:/Development/MapTanim/docs/16_INTERACTIVE_PLOT_MAPPING.md)
- 📄 [18. View Mode](file:///d:/Development/MapTanim/docs/18_VIEW_MODE.md)
- 📄 [19. Edit Mode](file:///d:/Development/MapTanim/docs/19_EDIT_MODE.md)
- 📄 [34. Direct Soil Crop Planting & Resize System](file:///d:/Development/MapTanim/docs/34_CROP_PLANTING_AND_RESIZE_SYSTEM.md)
- 📄 [35. Asset Planning & Sprites](file:///d:/Development/MapTanim/docs/35_ASSETS_PLANNING.md)
- 📄 [38. Audio & Sound Assets Planning](file:///d:/Development/MapTanim/docs/38_AUDIO_AND_SOUND_ASSETS_PLANNING.md)
- 📄 [39. Crop View Interaction & Variety Simulation](file:///d:/Development/MapTanim/docs/39_CROP_VIEW_INTERACTION_AND_VARIETY_SIMULATION.md)
