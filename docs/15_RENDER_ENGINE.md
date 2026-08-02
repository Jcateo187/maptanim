# 15. 2D Isometric Render Engine Specifications

## 📌 Overview
The **MapTanim 2D Isometric Render Engine** (`FarmCanvasRenderer` & `IsometricProjection`) is a custom Compose `Canvas` rendering system built for 60 FPS performance, high-resolution direct crop sprite rendering, and Clash of Clans (CoC) inspired drag-and-drop mechanics.

---

## 📐 Isometric Coordinate Systems

### Tile Metrics & Projection
- **Standard Tile Width (`TILE_W`)**: `64.0f` pixels
- **Standard Tile Height (`TILE_H`)**: `32.0f` pixels
- **Farm Grid Bounds**: `30.0f` meters x `30.0f` meters (0f to 30f world space)

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

The renderer executes a strict depth-sorted rendering pipeline:

| Layer | Component | Description |
|---|---|---|
| **Layer 0** | **Ground Terrain & Soil** | Renders 30x30 loam soil tile grid surrounded by grass terrain. |
| **Layer 1** | **Scenery & Perimeter** | Perimeter wooden fences, coconut trees, mango trees, banana trees, flowers, bushes, and rocks sorted by depth ($X + Y$). |
| **Layer 1.5** | **CoC Tile Highlight** | Glowing semi-transparent green rhombus (`Color(0x554CAF50)` fill, `Color(0xFF4CAF50)` 3dp stroke) preview on ground soil. |
| **Layer 2** | **Direct Planted Crops** | Crop Zone plant instances generated dynamically via `PlantInstanceGenerator` without database row duplication. Renders 1 plant per grid tile at `scaleFactor = 1.0f`. |
| **Layer 3** | **Exterior Scenery** | Secondary trees and decorations sorted by depth. |
| **Layer 4** | **Status Pins (View Mode)** | Interactive task pins (Water, Fertilize, Harvest, Pest Alert) floating above crops. |
| **Layer 5** | **Selection & Grid (Edit Mode)**| Selected Crop Zone white border / blue dashed outline, 8 resize handles (Top-Left to Bottom-Right), and grid overlay. |

---

## 🌾 Crop Zone & Plant Instance Rendering (MD 34 Architecture)

1. **Crop Zone Loop**: `FarmCanvasRenderer` iterates through all `CropZoneRenderData` instances in the layout.
2. **Dynamic Generation**: `PlantInstanceGenerator.generate()` calculates individual plant coordinates for every tile within `[0 until width]` and `[0 until height]`.
3. **No Sprite Scaling**: Plant sprites render at constant scale `1.0f`. Resizing expands grid coverage rather than scaling images.
4. **8-Handle Bounding Box Overlay**: Selecting **Resize** from the bottom toolbar renders 8 interactive resize handles (4 corners + 4 edge midpoints) over the Crop Zone bounds.

---

## ⚡ Low-Level 60 FPS Gesture Engine (`awaitPointerEventScope`)

Replaces high-level Compose gesture wrappers with low-level pointer event handling:
1. **1-Touch Direct Drag**: Pressing down on any placed crop zone locks onto it instantly (`onDragStart`), tracking finger movement 1:1 using `Math.round` nearest grid rounding.
2. **2-Finger Pinch Zoom**: Computes pointer distance and centroid to scale zoom smoothly between **70% minimum zoom (`0.70f`)** and **400% maximum zoom (`4.00f`)**.
3. **1-Finger Camera Pan**: Dragging empty ground pans the camera across the farm map with 0 lag or touch-slop delay.
4. **Boundary Containment**: All crop movements and tile highlights are strictly clamped to `[0f, 30f - cropSize]` meters.
