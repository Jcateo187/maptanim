# 12. UI/UX Guidelines

> 📌 **Navigation**: [◀ 11. App Navigation](file:///d:/Development/MapTanim/docs/11_NAVIGATION.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [13. Design System ▶](file:///d:/Development/MapTanim/docs/13_DESIGN_SYSTEM.md)

---
## 📌 Overview
MapTanim is a **landscape-first** Android application. All screens are designed for horizontal orientation, resembling a field tablet interface used outdoors.

---

## 🔹 Layout Principles

### 1. Landscape-First Design
- All UI components are sized and positioned for **16:9 landscape** ratio.
- The farm canvas occupies the **center ~70% width** of the screen.
- Left panel is always **~220–250dp wide** (collapsible in Edit Mode).
- Right toolbar is **~64dp wide** — a vertical floating card.

### 2. Three-Zone Horizontal Layout

```
[Left Panel] [Farm Canvas (center)] [Right Toolbar]
   ~220dp          ~70% width            ~64dp
```

---

## 🔹 Top Bar Layout

### View Mode
```
[🌱 Logo] [Farm Name + Location] ────────────── [Weather] [🔔N] [👤 Name ▼]
```

| Zone | Content | Width |
|------|---------|-------|
| Left | Logo + Farm selector + Location | ~280dp |
| Center | Weather widget | ~160dp |
| Right | Notification bell + User avatar | ~120dp |

### Edit Mode
```
[🌱 Logo] [Farm Name + Location] ── [✏ EDIT MODE badge + subtitle] ── [🔔N] [👤▼]
```

| Zone | Content |
|------|---------|
| Left | Logo + Farm selector + Location (unchanged) |
| Center | Green "✏ EDIT MODE" pill + "Tap a plot or item to edit" gray sub-label |
| Right | Bell badge + avatar (name hidden in Edit Mode) |

---

## 🔹 Bottom Bar Behavior

| Screen Mode | Bottom Bar Content |
|-------------|------------------|
| View Mode | Standard 5-tab BottomNavBar |
| Edit Mode | `EditBottomLayout` (Duplicate / Resize / Delete actions when plot selected) |

---

## 🔹 Touch Target Rules

- Minimum touch target: **48dp × 48dp** (Material Design + outdoor-use requirement)
- Farm canvas plot tap target: minimum **60dp × 60dp** (plot must be large enough to tap)
- Selection handles: minimum **24dp diameter** circles
- Bottom nav tabs: minimum **56dp height**

---

## 🔹 Interaction States

| State | Visual Treatment |
|-------|-----------------|
| Default | Base color |
| Hover/Pressed | Ripple effect (Material You) |
| Active (nav tab) | Green fill + underline indicator |
| Selected (plot) | Dashed blue/white border + handles |
| Active tool (edit panel) | Light green `#E8F5E9` background row |
| Toggle ON (Grid/Snap) | Green filled toggle switch |
| Toggle OFF | Gray unfilled toggle switch |
| Disabled button | 38% alpha |

---

## 🔹 Micro-Animations

| Element | Animation |
|---------|----------|
| Mode switch (View → Edit) | Cross-fade of left panel content + top bar center |
| Plot selection | Scale-in of selection handles (spring animation) |
| Soil swatch selection | Scale up + ring border appear |
| SAVE CHANGES tap | Button compress + green flash |
| Task row tap | Ripple + navigate with slide |
| Bottom tab switch | Icon scale + color transition |
| Status badge pins | Subtle bounce on initial render |

---

## 🔹 Contextual Panel Behavior

### Left Panel — Context Switch
```
View Mode left panel:          Edit Mode left panel:
┌─────────────────┐            ┌─────────────────┐
│ TODAY'S TASKS   │   ──────▶  │ EDIT TOOLS [∧]  │
│ [task rows]     │            │ [4 tool rows]   │
│                 │            ├─────────────────┤
│ FARM SUMMARY    │            │ SOIL TYPE       │
│ [4 stat items]  │            │ [6 swatches]    │
│                 │            │ 💡 Tip text     │
└─────────────────┘            └─────────────────┘
```

The left panel content **swaps** when transitioning between View and Edit Mode. Animation: cross-fade over 250ms.

---

## 🔹 Canvas Interaction Feedback

| Event | Feedback |
|-------|---------|
| Tap empty canvas | No visual change (deselect) |
| Tap plot (View Mode) | Plot highlights + Monitoring Hub opens |
| Tap plot (Edit Mode) | Selection handles appear with spring bounce |
| Drag plot | Plot follows finger, grid snap preview |
| Snap to grid | Subtle haptic feedback (if device supports) |
| Zoom at limit | Bounce-back animation |

---

## 📚 Related Documentation & Cross References
- 📄 [Master Documentation Hub](file:///d:/Development/MapTanim/docs/README.md)
- 📄 [00. Getting Started Guide](file:///d:/Development/MapTanim/docs/00_GETTING_STARTED.md)
- 📄 [03. System Architecture](file:///d:/Development/MapTanim/docs/03_SYSTEM_ARCHITECTURE.md)
- 📄 [11. App Navigation](file:///d:/Development/MapTanim/docs/11_NAVIGATION.md)
- 📄 [13. Design System](file:///d:/Development/MapTanim/docs/13_DESIGN_SYSTEM.md)
- 📄 [14. Component Library](file:///d:/Development/MapTanim/docs/14_COMPONENT_LIBRARY.md)
- 📄 [15. Render Engine](file:///d:/Development/MapTanim/docs/15_RENDER_ENGINE.md)
- 📄 [16. Interactive Plot Mapping](file:///d:/Development/MapTanim/docs/16_INTERACTIVE_PLOT_MAPPING.md)
- 📄 [18. View Mode](file:///d:/Development/MapTanim/docs/18_VIEW_MODE.md)
- 📄 [19. Edit Mode](file:///d:/Development/MapTanim/docs/19_EDIT_MODE.md)
- 📄 [34. Direct Soil Crop Planting & Resize System](file:///d:/Development/MapTanim/docs/34_CROP_PLANTING_AND_RESIZE_SYSTEM.md)
- 📄 [35. Asset Planning & Sprites](file:///d:/Development/MapTanim/docs/35_ASSETS_PLANNING.md)
- 📄 [38. Audio & Sound Assets Planning](file:///d:/Development/MapTanim/docs/38_AUDIO_AND_SOUND_ASSETS_PLANNING.md)
- 📄 [39. Crop View Interaction & Variety Simulation](file:///d:/Development/MapTanim/docs/39_CROP_VIEW_INTERACTION_AND_VARIETY_SIMULATION.md)
