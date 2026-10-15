# 15. 2D Top-Down Render Engine Specifications

> 📌 **Navigation**: [◀ 14. Component Library](file:///d:/Development/MapTanim/docs/14_COMPONENT_LIBRARY.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [16. Interactive Plot Mapping ▶](file:///d:/Development/MapTanim/docs/16_INTERACTIVE_PLOT_MAPPING.md)

---
## 📌 Overview
The **MapTanim 2D Top-Down Render Engine** ([`TopDownFarmCanvas.kt`](file:///d:/Development/MapTanim/mobile/app/src/main/java/com/maptanim/app/features/farm/renderer/canvas/TopDownFarmCanvas.kt), [`CropSvgRenderer.kt`](file:///d:/Development/MapTanim/mobile/app/src/main/java/com/maptanim/app/features/farm/renderer/canvas/CropSvgRenderer.kt), and [`FarmCanvasView.kt`](file:///d:/Development/MapTanim/mobile/app/src/main/java/com/maptanim/app/features/farm/canvas/FarmCanvasView.kt)) is a flat, orthographic 2D canvas rendering system designed for 60 FPS performance, direct-to-soil drag-and-drop vegetable planting, and sub-inch precision bounding box manipulation.

The engine uses flat **2D Top-Down Orthographic Projection** instead of isometric geometry. Farmers view their garden beds and crop placements from a true aerial bird's-eye perspective, matching real-world yard measurements in inches, feet, and yards.

---

## 📐 2D Top-Down Orthographic Coordinate System

### Screen & World Coordinate Transformation (`TopDownProjection`)
- **Pixels Per Meter (`PPM`)**: `40.0f` pixels per world meter at `zoom = 1.0f`.
- **TopDownCamera**: Stores viewport translation (`panX`, `panY`) and magnification (`zoom`).

#### World to Screen Mapping:
$$\text{screenX} = \text{worldX} \times \text{PPM} \times \text{zoom} + \text{panX}$$
$$\text{screenY} = \text{worldY} \times \text{PPM} \times \text{zoom} + \text{panY}$$

#### Screen to World Mapping:
$$\text{worldX} = \frac{\text{screenX} - \text{panX}}{\text{PPM} \times \text{zoom}}$$
$$\text{worldY} = \frac{\text{screenY} - \text{panY}}{\text{PPM} \times \text{zoom}}$$

#### Grid Snapping:
Snaps world coordinates to 6-inch increments (`SIX_INCHES_IN_METERS = 0.1524f`) or 1-foot increments (`FOOT_IN_METERS = 0.3048f`):
$$\text{snapped} = \text{round}\left(\frac{\text{worldPos}}{\text{snap}}\right) \times \text{snap}$$

---

## 🎨 Layered 2D Canvas Pipeline (`TopDownFarmCanvas`)

The top-down canvas draws in strict z-order layers:

| Layer | Component | Implementation |
|---|---|---|
| **Layer 0** | **Soil Grid Canvas** | Centered rich dark brown loam soil background (`#261814`) with crisp top-down grid lines. |
| **Layer 1** | **Metric Yard Rulers** | Dynamic top (width) and left (height) yard measurement rulers with foot/inch tick marks. |
| **Layer 2** | **Garden Plot Outlines** | Rectangular boundaries with crisp borders and dimension labels (e.g. $10\text{ ft} \times 8\text{ ft}$). |
| **Layer 3** | **Direct Planted Crops** | Vector SVG vegetable rendering via [`CropSvgRenderer`](file:///d:/Development/MapTanim/mobile/app/src/main/java/com/maptanim/app/features/farm/renderer/canvas/CropSvgRenderer.kt) with grounding shadow and plant spacing. |
| **Layer 4** | **Focus Dimming & Ring** | Soft background tint upon selecting a crop, highlighting the active plant. |
| **Layer 5** | **Radial Action Overlay** | [`CropCircleActionOverlay`](file:///d:/Development/MapTanim/mobile/app/src/main/java/com/maptanim/app/features/farm/components/CropCircleActionOverlay.kt) positioning 6 tactile clock-hour action buttons around the crop. |
| **Layer 6** | **8-Handle Resize Box** | Interactive corner and midpoint handles for expanding/shrinking crop zones in real-time. |

---

## 🥦 Real-Life Vegetable Spacing & Canopy Scaling

Vegetables are rendered using authentic physiological dimensions in meters ([`RenderModels.kt`](file:///d:/Development/MapTanim/mobile/app/src/main/java/com/maptanim/app/features/farm/renderer/model/RenderModels.kt)):

| Crop | Diameter (Inches / Meters) | Single Plant Spacing |
|---|---|---|
| **Carrot** | 1 inch ($0.0254\text{m}$) | 3 inches ($0.0762\text{m}$) |
| **Green Onion / Scallion** | 3 inches ($0.0762\text{m}$) | 3 inches ($0.0762\text{m}$) |
| **Pechay / Bok Choy** | 6 inches ($0.1524\text{m}$) | 6 inches ($0.1524\text{m}$) |
| **Lettuce** | 8 inches ($0.2032\text{m}$) | 8 inches ($0.2032\text{m}$) |
| **String Beans (Sitaw)** | 12 inches / 1 ft ($0.3048\text{m}$) | 12 inches / 1 ft ($0.3048\text{m}$) |
| **Potato** | 12 inches / 1 ft ($0.3048\text{m}$) | 12 inches / 1 ft ($0.3048\text{m}$) |
| **Cabbage** | 15 inches ($0.381\text{m}$) | 15 inches ($0.381\text{m}$) |
| **Tomato / Eggplant / Cucumber / Bell Pepper** | 18 inches / 1.5 ft ($0.4572\text{m}$) | 18 inches / 1.5 ft ($0.4572\text{m}$) |
| **Squash / Kalabasa** | 36 inches / 1 yard ($0.9144\text{m}$) | 36 inches / 1 yard ($0.9144\text{m}$) |

---

## 🎯 Direct Touch Interactions

1. **Tap to Select**: Tapping a planted crop opens the 6-action radial overlay ring.
2. **Radial Actions**:
   - **12:00 (Info)**: Cultural guide and variety information.
   - **2:00 (Check Up)**: Health diagnostics and symptom examination.
   - **4:00 (Calendar)**: Sowing date, growth timeline, and harvest window.
   - **6:00 (Resize)**: 8-handle bounding box expansion.
   - **8:00 (Copy)**: Duplicate to adjacent tile.
   - **10:00 (Delete)**: Remove plant from soil grid.
3. **Pinch-to-Zoom & Pan**: Orthographic camera zoom from whole-yard overview down to 10-inch single-plant close-up.
