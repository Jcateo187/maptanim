# 43. 2D Isometric Projection Reference & Developer Cookbook

> 📌 **Navigation**: [◀ 42. High-Scalability Architecture](file:///d:/Development/MapTanim/docs/42_SCALABILITY_AND_MULTI_TENANCY_ARCHITECTURE.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [15. Render Engine ▶](file:///d:/Development/MapTanim/docs/15_RENDER_ENGINE.md)

---

## ⚡ Quick Reference / Cheat Sheet

Just like Tailwind CSS provides utility classes for immediate use, this cheat sheet provides drop-in formulas and functions for building 2D isometric renderers across Kotlin (Jetpack Compose) and TypeScript (HTML5 Canvas).

### Core Formulas at a Glance

| Action | Formula / Signature | Output |
|---|---|---|
| **World $\to$ Screen** | `screenX = (x - y) * (TILE_W / 2) * zoom + panX`<br>`screenY = (x + y) * (TILE_H / 2) * zoom + panY` | Screen pixels `(px, py)` |
| **Screen $\to$ World** | `worldX = (unpannedX / TILE_W) + (unpannedY / TILE_H)`<br>`worldY = (unpannedY / TILE_H) - (unpannedX / TILE_W)` | World meters `(wx, wy)` |
| **Drag Delta $\to$ World** | `worldDx = (screenDx / (zoom * TILE_W / 2) + screenDy / (zoom * TILE_H / 2)) / 2`<br>`worldDy = (screenDy / (zoom * TILE_H / 2) - screenDx / (zoom * TILE_W / 2)) / 2` | World delta `(dwx, dwy)` |
| **Grid Snapping** | `snapped = round(pos / snapSize) * snapSize` | Snapped world coord |
| **Depth / Z-Order** | `depthKey = posX + posY` (sort ascending for drawing, descending for hit-testing) | Float sorting key |
| **Standard Tile** | `TILE_W = 64.0f`, `TILE_H = 32.0f` (Ratio: `2:1`, Dimetric angle $\approx 26.565^\circ$) | Diamond aspect |

---

## 🧭 Visual Mental Model & Coordinate Systems

In a standard 2D cartesian plane, $+X$ points right and $+Y$ points down. In **2:1 Isometric (Dimetric)** projection:
- The **World $+X$ axis** runs diagonally **down-right** ($+26.565^\circ$).
- The **World $+Y$ axis** runs diagonally **down-left** ($+153.435^\circ$).
- Tile dimensions follow a strict **2:1 width-to-height ratio** (e.g., $64\text{px} \times 32\text{px}$), allowing pixel-perfect diagonal stepping ($2$ pixels horizontal for every $1$ pixel vertical).

```
          Screen Origin (0, 0)
         ┌────────────────────────────────────────────────────────► +Screen X
         │
         │                         World Origin (0, 0)
         │                                  ▲
         │                                /   \
         │                              /       \
         │                 World +Y   /     ▲     \   World +X
         │                  (Down-Left)     │     (Down-Right)
         │                         /        │        \
         │                       /     Tile Center    \
         │                     ▼                         ▼
         │                      \                       /
         │                        \                   /
         │                          \               /
         │                            \           /
         │                              ▼       ▼
         │                                (X, Y)
         ▼
    +Screen Y
```

### Diamond Vertex Geometry

For a single $1\text{m} \times 1\text{m}$ tile located at world coordinate `(worldX, worldY)`:

```
                         Top Vertex
                IsometricProjection.toScreen(worldX, worldY)
                             ▲
                            / \
                          /     \
    Left Vertex         /         \        Right Vertex
    toScreen(x, y + 1) <           > toScreen(x + 1, y)
                        \         /
                          \     /
                            \ /
                             ▼
                       Bottom Vertex
                IsometricProjection.toScreen(worldX + 1, worldY + 1)
```

---

## 📦 Core Math Modules (Copy-Paste Ready)

### 1. Kotlin `IsometricProjection.kt` (Jetpack Compose)

```kotlin
package com.maptanim.app.renderer.model

import androidx.compose.ui.geometry.Offset

/**
 * High-performance 2:1 Isometric Projection Utility.
 * Converts between continuous World Space (meters) and Screen Space (pixels).
 */
object IsometricProjection {
    // 2:1 ratio dimetric tile dimensions
    const val TILE_W = 64.0f
    const val TILE_H = 32.0f

    /**
     * Converts World coordinates (meters) to Screen coordinates (pixels).
     * @param worldX World X coordinate in meters
     * @param worldY World Y coordinate in meters
     * @param camera Current camera pan offset and zoom scale
     */
    fun toScreen(worldX: Float, worldY: Float, camera: CameraState): Offset {
        val screenX = (worldX - worldY) * (TILE_W / 2f) * camera.zoom + camera.panX
        val screenY = (worldX + worldY) * (TILE_H / 2f) * camera.zoom + camera.panY
        return Offset(screenX, screenY)
    }

    /**
     * Converts Screen coordinates (pixels) back to World coordinates (meters).
     * Used for touch detection, tap picking, and drag placement.
     */
    fun toWorld(screenX: Float, screenY: Float, camera: CameraState): Offset {
        val unpannedX = (screenX - camera.panX) / camera.zoom
        val unpannedY = (screenY - camera.panY) / camera.zoom
        val worldX = (unpannedX / TILE_W) + (unpannedY / TILE_H)
        val worldY = (unpannedY / TILE_H) - (unpannedX / TILE_W)
        return Offset(worldX, worldY)
    }

    /**
     * Converts a Screen drag vector (dx, dy in pixels) into a World movement vector (in meters).
     * Guarantees 1:1 finger tracking regardless of zoom level.
     */
    fun screenDeltaToWorldDelta(screenDelta: Offset, camera: CameraState): Offset {
        val worldDx = screenDelta.x / (camera.zoom * TILE_W / 2f)
        val worldDy = screenDelta.y / (camera.zoom * TILE_H / 2f)
        return Offset(
            x = (worldDx + worldDy) / 2f,
            y = (worldDy - worldDx) / 2f
        )
    }
}
```

---

### 2. Camera State & Clamping `CameraState.kt`

```kotlin
package com.maptanim.app.renderer.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

/**
 * Immutable camera viewport state with strict boundary clamping.
 */
data class CameraState(
    val panX: Float = 0f,
    val panY: Float = 0f,
    val zoom: Float = 0.52f,
    val minZoom: Float = 0.30f,
    val maxZoom: Float = 4.0f
) {
    /**
     * Centers a specific world coordinate (e.g., center of farm map) in the viewport.
     */
    fun centeredOn(worldX: Float, worldY: Float, screenWidth: Float, screenHeight: Float): CameraState {
        val centerPanX = screenWidth / 2f
        val centerPanY = (screenHeight / 2f) - ((worldX + worldY) * (IsometricProjection.TILE_H / 2f) * zoom)
        return copy(panX = centerPanX, panY = centerPanY)
    }

    /**
     * Pans the camera with boundary enforcement.
     */
    fun pan(dx: Float, dy: Float, screenWidth: Float, screenHeight: Float, mapSizeM: Float = 45f): CameraState {
        if (zoom <= minZoom * 1.001f) {
            // At minimum zoom, map fits screen: lock panning to prevent void exposure
            return copy(
                panX = screenWidth / 2f,
                panY = (screenHeight / 2f) - (mapSizeM * (IsometricProjection.TILE_H / 2f) * zoom)
            )
        }
        return copy(panX = panX + dx, panY = panY + dy)
    }
}
```

---

## 🎨 Component Cookbook / Ready-to-Use Recipes

### Recipe 1: Draw a Single Isometric Diamond Tile

```kotlin
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke

fun DrawScope.drawIsoTile(
    worldX: Float,
    worldY: Float,
    camera: CameraState,
    fillColor: Color = Color(0xFF4CAF50),
    strokeColor: Color? = Color(0xFF2E7D32),
    strokeWidthPx: Float = 1.5f
) {
    val top    = IsometricProjection.toScreen(worldX, worldY, camera)
    val right  = IsometricProjection.toScreen(worldX + 1f, worldY, camera)
    val bottom = IsometricProjection.toScreen(worldX + 1f, worldY + 1f, camera)
    val left   = IsometricProjection.toScreen(worldX, worldY + 1f, camera)

    val diamondPath = Path().apply {
        moveTo(top.x, top.y)
        lineTo(right.x, right.y)
        lineTo(bottom.x, bottom.y)
        lineTo(left.x, left.y)
        close()
    }

    drawPath(path = diamondPath, color = fillColor, style = Fill)
    strokeColor?.let {
        drawPath(path = diamondPath, color = it, style = Stroke(width = strokeWidthPx))
    }
}
```

---

### Recipe 2: Draw an $N \times M$ Isometric Grid Overlay

```kotlin
fun DrawScope.drawIsoGrid(
    gridSizeM: Int = 45,
    camera: CameraState,
    lineColor: Color = Color.White.copy(alpha = 0.25f),
    strokeWidthPx: Float = 1.0f
) {
    // Horizontal isometric lines
    for (i in 0..gridSizeM) {
        val p1 = IsometricProjection.toScreen(i.toFloat(), 0f, camera)
        val p2 = IsometricProjection.toScreen(i.toFloat(), gridSizeM.toFloat(), camera)
        drawLine(lineColor, p1, p2, strokeWidth = strokeWidthPx)
    }
    // Vertical isometric lines
    for (i in 0..gridSizeM) {
        val p1 = IsometricProjection.toScreen(0f, i.toFloat(), camera)
        val p2 = IsometricProjection.toScreen(gridSizeM.toFloat(), i.toFloat(), camera)
        drawLine(lineColor, p1, p2, strokeWidth = strokeWidthPx)
    }
}
```

---

### Recipe 3: Draw a Multi-Tile Crop Zone / Plot Rhombus

For an arbitrary rectangular plot of size $W \times H$ meters:

```kotlin
fun DrawScope.drawIsoPlotRhombus(
    worldX: Float,
    worldY: Float,
    widthM: Float,
    heightM: Float,
    camera: CameraState,
    fillColor: Color,
    borderColor: Color,
    borderWidthPx: Float = 2.0f
) {
    val top    = IsometricProjection.toScreen(worldX, worldY, camera)
    val right  = IsometricProjection.toScreen(worldX + widthM, worldY, camera)
    val bottom = IsometricProjection.toScreen(worldX + widthM, worldY + heightM, camera)
    val left   = IsometricProjection.toScreen(worldX, worldY + heightM, camera)

    val plotPath = Path().apply {
        moveTo(top.x, top.y)
        lineTo(right.x, right.y)
        lineTo(bottom.x, bottom.y)
        lineTo(left.x, left.y)
        close()
    }

    drawPath(path = plotPath, color = fillColor)
    drawPath(path = plotPath, color = borderColor, style = Stroke(width = borderWidthPx))
}
```

---

### Recipe 4: Upright 2D Billboard Sprite Placement (Crops, Trees, Fences)

> [!IMPORTANT]
> **Sprite Ground Anchor Rule**: A 2D sprite representing an upright 3D object must be grounded at the **bottom-center** of the base tile, **NOT** drawn from top-left.

```kotlin
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

fun DrawScope.drawIsoSprite(
    bitmap: ImageBitmap,
    worldX: Float,
    worldY: Float,
    camera: CameraState,
    scaleFactor: Float = 1.0f
) {
    // 1. Calculate ground contact point at tile center (worldX + 0.5, worldY + 0.5)
    val groundAnchor = IsometricProjection.toScreen(worldX + 0.5f, worldY + 0.5f, camera)

    // 2. Scale sprite based on camera zoom
    val baseTileW = IsometricProjection.TILE_W * camera.zoom
    val targetW = (baseTileW * scaleFactor).toInt().coerceAtLeast(1)
    val aspect = bitmap.height.toFloat() / bitmap.width.toFloat()
    val targetH = (targetW * aspect).toInt().coerceAtLeast(1)

    // 3. Anchor sprite: bottom-center aligned to groundAnchor
    val left = Math.round(groundAnchor.x - (targetW / 2f))
    val top  = Math.round(groundAnchor.y - targetH)

    drawImage(
        image = bitmap,
        dstOffset = IntOffset(left, top),
        dstSize = IntSize(targetW, targetH)
    )
}
```

---

### Recipe 5: Clash of Clans Style Tile Drag Highlight (Valid/Invalid)

```kotlin
fun DrawScope.drawIsoPlacementHighlight(
    worldPos: Offset,
    widthM: Float,
    heightM: Float,
    isValidPlacement: Boolean,
    camera: CameraState
) {
    val strokeColor = if (isValidPlacement) Color(0xFF1E88E5) else Color(0xFFE53935)
    val fillColor   = if (isValidPlacement) Color(0x331E88E5) else Color(0x44E53935)

    drawIsoPlotRhombus(
        worldX = worldPos.x,
        worldY = worldPos.y,
        widthM = widthM,
        heightM = heightM,
        camera = camera,
        fillColor = fillColor,
        borderColor = strokeColor,
        borderWidthPx = 3f
    )
}
```

---

### Recipe 6: 8-Handle Resize Overlay for Crop Plots

```kotlin
fun DrawScope.drawIsoResizeHandles(
    worldX: Float,
    worldY: Float,
    widthM: Float,
    heightM: Float,
    camera: CameraState,
    handleColor: Color = Color.White,
    handleBorderColor: Color = Color(0xFF1E88E5)
): List<Offset> {
    val top    = IsometricProjection.toScreen(worldX, worldY, camera)
    val right  = IsometricProjection.toScreen(worldX + widthM, worldY, camera)
    val bottom = IsometricProjection.toScreen(worldX + widthM, worldY + heightM, camera)
    val left   = IsometricProjection.toScreen(worldX, worldY + heightM, camera)

    // 8 points: 4 corners + 4 edge midpoints
    val handles = listOf(
        top,
        Offset((top.x + right.x) / 2f, (top.y + right.y) / 2f),
        right,
        Offset((right.x + bottom.x) / 2f, (right.y + bottom.y) / 2f),
        bottom,
        Offset((bottom.x + left.x) / 2f, (bottom.y + left.y) / 2f),
        left,
        Offset((left.x + top.x) / 2f, (left.y + top.y) / 2f)
    )

    val radius = 6f * camera.zoom.coerceAtLeast(1f)
    for (pt in handles) {
        drawCircle(color = handleColor, radius = radius, center = pt)
        drawCircle(color = handleBorderColor, radius = radius, center = pt, style = Stroke(width = 2f))
    }

    return handles
}
```

---

## 🎯 Touch Interaction, Snapping & Hit-Testing

### 1. Screen Tap to World Tile Picking

To find which plot was tapped from a screen touch event:

```kotlin
data class PlotBounds(val id: String, val posX: Float, val posY: Float, val widthM: Float, val heightM: Float)

fun hitTestPlots(
    screenTouch: Offset,
    plots: List<PlotBounds>,
    camera: CameraState,
    paddingM: Float = 0.5f // Generous touch buffer for mobile fingers
): PlotBounds? {
    // 1. Convert screen touch to continuous world coordinate
    val worldTouch = IsometricProjection.toWorld(screenTouch.x, screenTouch.y, camera)

    // 2. Iterate in reverse depth order (front-most items hit first)
    return plots
        .sortedByDescending { it.posX + it.posY }
        .firstOrNull { plot ->
            worldTouch.x >= (plot.posX - paddingM) &&
            worldTouch.x <= (plot.posX + plot.widthM + paddingM) &&
            worldTouch.y >= (plot.posY - paddingM) &&
            worldTouch.y <= (plot.posY + plot.heightM + paddingM)
        }
}
```

### 2. Grid Snapping Algorithm

```kotlin
fun snapToGrid(worldPos: Offset, snapStep: Float = 0.5f): Offset {
    val snappedX = Math.round(worldPos.x / snapStep) * snapStep
    val snappedY = Math.round(worldPos.y / snapStep) * snapStep
    return Offset(snappedX, snappedY)
}
```

---

## 🏔️ Depth Sorting & Z-Ordering (Painter's Algorithm)

In isometric projections, objects positioned further back (towards World origin `0, 0`) must be drawn **first**, and objects closer to the camera (higher $X$ and $Y$) must be drawn **on top**.

```kotlin
// Ascending sort for rendering (back-to-front)
val renderQueue = farmObjects.sortedWith(
    compareBy<FarmObject> { it.posX + it.posY }
        .thenBy { it.posY }
        .thenBy { it.posX }
)

renderQueue.forEach { item ->
    item.render(this, camera)
}
```

> [!TIP]
> **Rule of Thumb**:
> - **Drawing Order**: Sort **ascending** (`it.posX + it.posY`).
> - **Hit-Testing Order**: Sort **descending** (`it.posX + it.posY`) so taps register on foreground items first!

---

## 🌐 Cross-Platform: TypeScript / HTML5 Canvas Module

For use in Web Dashboards (e.g. React Admin), here is the equivalent TypeScript class:

```typescript
export interface CameraState {
  panX: number;
  panY: number;
  zoom: number;
}

export class IsometricProjection {
  static readonly TILE_W = 64;
  static readonly TILE_H = 32;

  static toScreen(worldX: number, worldY: number, camera: CameraState) {
    const screenX = (worldX - worldY) * (this.TILE_W / 2) * camera.zoom + camera.panX;
    const screenY = (worldX + worldY) * (this.TILE_H / 2) * camera.zoom + camera.panY;
    return { x: screenX, y: screenY };
  }

  static toWorld(screenX: number, screenY: number, camera: CameraState) {
    const unpannedX = (screenX - camera.panX) / camera.zoom;
    const unpannedY = (screenY - camera.panY) / camera.zoom;
    const worldX = (unpannedX / this.TILE_W) + (unpannedY / this.TILE_H);
    const worldY = (unpannedY / this.TILE_H) - (unpannedX / this.TILE_W);
    return { x: worldX, y: worldY };
  }

  static drawTile(
    ctx: CanvasRenderingContext2D,
    worldX: number,
    worldY: number,
    camera: CameraState,
    fillColor = '#4CAF50',
    strokeColor = '#2E7D32'
  ) {
    const top    = this.toScreen(worldX, worldY, camera);
    const right  = this.toScreen(worldX + 1, worldY, camera);
    const bottom = this.toScreen(worldX + 1, worldY + 1, camera);
    const left   = this.toScreen(worldX, worldY + 1, camera);

    ctx.beginPath();
    ctx.moveTo(top.x, top.y);
    ctx.lineTo(right.x, right.y);
    ctx.lineTo(bottom.x, bottom.y);
    ctx.lineTo(left.x, left.y);
    ctx.closePath();

    ctx.fillStyle = fillColor;
    ctx.fill();
    ctx.strokeStyle = strokeColor;
    ctx.lineWidth = 1.5;
    ctx.stroke();
  }
}
```

---

## ⚠️ Common Pitfalls & Troubleshooting

| Symptom | Cause | Remedy |
|---|---|---|
| **Sprites look like they are floating** | Sprite anchor placed at top-left instead of bottom-center. | Offset drawing by `(groundAnchor.x - spriteWidth / 2)` and `(groundAnchor.y - spriteHeight)`. |
| **Touch selection hits wrong tile** | Hit-testing before unpanning or unzooming screen coordinates. | Always use `IsometricProjection.toWorld(screenPos.x, screenPos.y, camera)`. |
| **Foreground objects drawn behind background** | Missing depth sorting (`sortedBy { it.posX + it.posY }`). | Ensure the draw loop executes Painter's Algorithm before rendering. |
| **Camera panning jumps or stutters during zoom** | Centroid was not maintained during zoom calculation. | Clamp camera coordinates around the screen center or touch centroid. |
| **Seams between adjacent tiles** | Floating-point rounding gaps in canvas paths. | Slight overlap ($0.2\text{px}$ stroke width or subpixel padding) hides rasterizer seams. |

---

## 📚 Related Chapters
- 📄 [15. 2D Isometric Render Engine](file:///d:/Development/MapTanim/docs/15_RENDER_ENGINE.md)
- 📄 [16. Interactive Plot Mapping](file:///d:/Development/MapTanim/docs/16_INTERACTIVE_PLOT_MAPPING.md)
- 📄 [19. Edit Mode & Farm Layout Tooling](file:///d:/Development/MapTanim/docs/19_EDIT_MODE.md)
- 📄 [34. Direct Soil Crop Planting & Resize System](file:///d:/Development/MapTanim/docs/34_CROP_PLANTING_AND_RESIZE_SYSTEM.md)
- 📄 [35. Asset Planning & Sprites](file:///d:/Development/MapTanim/docs/35_ASSETS_PLANNING.md)
