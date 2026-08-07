# 32. Changelog

> 📌 **Navigation**: [◀ 31. Contributing Guidelines](file:///d:/Development/MapTanim/docs/31_CONTRIBUTING.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [33. Roadmap ▶](file:///d:/Development/MapTanim/docs/33_ROADMAP.md)

---
All notable changes to the **MapTanim** project are documented in this file.

---

## 📅 [1.4.0-RELEASE] — 2026-08-07

### 🌟 Added & Fixed
- **45×45 Farm Grid Tile Map Alignment**:
  - Expanded all grid boundary calculations, tile hover snapping, duplicate plot logic, and bounds checks to $45\text{m} \times 45\text{m}$ (`45.0f`).
- **MD 34 Real-Time Isometric Rhombus Preview**:
  - Implemented real-time $1\text{m} \times 1\text{m}$ isometric rhombus outline preview tracking active touch/drag gestures over the farm map:
    - **🟦 Blue Rhombus (`#1E88E5`)**: Highlights valid, empty target soil tiles.
    - **🟥 Red Rhombus (`#E53935`)**: Highlights invalid, occupied, or out-of-bounds tiles.
- **Direct Crop Tray Drag & Drop**:
  - Enabled direct drag gestures on all crop cards in `CropTray.kt` without requiring prior card selection.
- **Strict Crop Placement Collision Prevention**:
  - Enforced strict overlap collision checks in `EditViewModel.kt` (`addDirectPlantingPlot`), `FarmCanvas.kt`, and `FarmEditorScreen.kt` to reject dropping crops onto occupied crop plots.

---

## 📅 [1.3.0-RELEASE] — 2026-08-07

### 🌟 Added & Refined
- **Drag-and-Drop Gesture Overlay Hiding**:
  - All guide speech cards and pointing hands automatically hide while actively dragging crops (`isDraggingCrop`), preventing visual obstruction during placement gestures.
- **Post-Drop Bottom Toolbar Decision Overlay**:
  - After dropping a crop onto the farm grid, an overlay explains the bottom contextual edit toolbar (`EditBottomLayout`) with two decision choices:
    - **"Continue Editing"**: Returns to free editor mode (`EDIT_DRAGGING_CROP`) so farmers can continue dragging, duplicating, resizing, or deleting crops.
    - **"Proceed"**: Advances to the next walkthrough step (`EDIT_CLOSE_TRAY`).
- **Interactive Pointing Targets & Fallback Actions**:
  - Pointing hands and spotlight rings for **"TAP TO ADD CROPS"**, **"CLOSE TRAY ('X')"**, and **"CLICK SAVE"** are directly clickable, preventing tutorial stalls and allowing users to advance by tapping guide targets directly.
  - Added explicit **"Open Tray"** and **"Close Tray"** fallback buttons inside Tatay Juan's guide speech cards.
- **Automatic Step Advancement on Tray Open**:
  - Added `LaunchedEffect` auto-advancing `EDIT_ADD_PLANT` to `EDIT_SELECT_CROP` whenever the Crop Tray drawer is opened (`isRightPanelVisible == true`).

---

## 📅 [1.2.0-RELEASE] — 2026-08-01

### 🌟 Added
- **End-to-End Edit-to-View Persistence**:
  - Tapping **SAVE CHANGES** in Edit Mode (`FarmEditorScreen.kt`) persists all bed plots and crop zones to `CropPlotRepository` and `CropZoneRepository`.
  - Returning to View Mode (`HomeScreen.kt`) immediately reflects saved beds, planted crops, and updates top HUD resource counters (`totalCrops`).
- **Top Crop Label Calendar Monitoring Badge & Navigation**:
  - Plots with unstarted crops render a floating **Glowing Calendar Badge (`📅`)** above the crop label on the 2D bed plot on the farm map (`FarmCanvasRenderer.kt`).
  - Tapping the calendar icon badge or bed plot in View mode opens the **Monitoring Dashboard Overlay** (`HomeScreen.kt` & `FarmCanvas.kt`).
  - Clicking **"📅 Start"** in Monitoring starts growth tracking and clears the unstarted badge.
- **Full 15 Philippine Crop Catalog & Scroll Protection**:
  - Expanded `CropTray.kt` to all 15 Philippine vegetable crops: Carrot 🥕, String Beans 🫘, Eggplant 🍆, Tomato 🍅, Onion 🧅, Squash 🎃, Corn 🌽, Cabbage 🥬, Pechay 🥬, Ampalaya 🥒, Okra 🌿, Chili Pepper 🌶️, Cucumber 🥒, Kangkong 🥬, and Lettuce 🥗.
  - Required click-selection (`isSelected == true`) before drag gesture detection activates, enabling smooth vertical scrolling through the Crop Tray without accidental drag interference.
- **Click-First Plot Repositioning Protection**:
  - Enforced click-to-select before plot moving/repositioning on the 2D grid (`CanvasGestureHandler.kt`), preventing accidental plot displacement while panning the canvas.
- **Room Local Database Integration & Clean Documentation**:
  - Implemented Room Local Database (`AppDatabase`, `CropPlotEntity`, `FarmEntity`, `CropPlotDao`, `FarmDao`).
  - Consolidated specifications in `docs/35_ASSETS_PLANNING.md` and indexed all 37 documentation chapters in `README.md` and `docs/28_PROJECT_STRUCTURE.md`.

---

## 📅 [1.1.0-RELEASE] — 2026-07-28

### 🌟 Added
- **Direct Soil Planting System**:
  - Replaced raised wooden bed frames and path tiles with direct soil planting plots on the 2D isometric canvas.
  - Crops are planted directly onto soil at exact tapped/dragged world coordinates.
- **High-Resolution Crop PNG Sprites**:
  - Integrated high-res crop PNG sprites for **Carrot** 🥕 (`crop_carrot_*.png`) and **String Beans** 🫘 (`crop_stringbeans_*.png`) with growth stage scaling.
- **Hold-to-Drag Crop Positioning**:
  - Holding any crop plot enables direct drag-and-drop repositioning anywhere on the soil grid.
- **Outer Crop Selection & Resize Handles**:
  - Selecting a crop plot renders an outer dashed blue boundary with 8 corner/edge resize handles.
  - Delete tool removes selected crop plot from layout.
- **Synchronized 2D Isometric Scenery**:
  - Unified `FarmCanvasRenderer` between **HomeScreen (View Mode)** and **FarmEditorScreen (Edit Mode)** for identical 2D isometric scenery.
- **70% Min Zoom Limit & Left-Side Camera Pan Bounds**:
  - Enforced 70% min zoom (`minZoom = 0.70f`) and expanded camera pan bounds (`maxPanX/Y = 2500f * zoom`) for smooth left-side map navigation.
- **Edit Scene Layout Streamlining & Control Removal**:
  - Removed left panel (`EditLeftToolbar`) including `Select / Move` and `Delete` tool choices.
  - Deleted right toolbar controls for **Undo**, **Redo**, **Grid**, **Snap**, and **Zoom** (`EditRightToolbar`).
  - Made **Add Plant / Crops** a single standalone floating button on the right side.
  - Placed **Save** and **Exit** as single action buttons located side-by-side in the top-right corner.
- **UI Terminology Migration**:
  - Replaced user-facing "Bed" and "Beds" strings across app UI overlays (Task list, MiniStatistics, Summary cards, CropPickerDialog) with **Plot** / **Plots**.
- **HUD Top Bar & Right Toolbar Refinement**:
  - Removed Beds card from top bar center resource statistics; now displays **Crops** and **Ready to Harvest** cards.
  - Added **Settings** icon pill adjacent to the Notification icon pill in the top bar right HUD section (`Notification Icon - Settings Icon`).
  - Removed **Center** button from the right floating toolbar.
- **Save Farm Dialog & Confirmation Flow**:
  - Save action opens "Save Farm Layout" dialog prompting `Type farm name`.
  - Saves to Supabase (logged-in farmer) or Room Local Storage (guest farmer).
  - Displays on-screen alert message: `"Excellent Successful set up the farm"`.
  - Navigates to HomeScreen on clicking `Okay`.

---

## 📅 [1.0.0-BETA] — 2026-07-24

### 🌟 Added
- **Clean MVVM + Clean Architecture Core**: Domain models, domain interfaces, and use cases.
- **Live Supabase Data Repositories**: Remote PostgREST API integration.
- **Decision Support System (DSS Engine)**: Growth stage calculator & Dynamic task generator.
- **Interactive 2D Isometric Canvas**: Viewport clipping & pre-warmed soil bitmap rendering.

---

## 📚 Related Documentation & Cross References
- 📄 [Master Documentation Hub](file:///d:/Development/MapTanim/docs/README.md)
- 📄 [00. Getting Started Guide](file:///d:/Development/MapTanim/docs/00_GETTING_STARTED.md)
- 📄 [03. System Architecture](file:///d:/Development/MapTanim/docs/03_SYSTEM_ARCHITECTURE.md)
- 📄 [26. Testing Strategy](file:///d:/Development/MapTanim/docs/26_TESTING.md)
- 📄 [27. Deployment Guide](file:///d:/Development/MapTanim/docs/27_DEPLOYMENT.md)
- 📄 [30. Git Workflow](file:///d:/Development/MapTanim/docs/30_GIT_WORKFLOW.md)
- 📄 [33. Roadmap](file:///d:/Development/MapTanim/docs/33_ROADMAP.md)
- 📄 [DevOps Architecture & Free CI/CD Pipelines](file:///d:/Development/MapTanim/docs/DEVOPS.md)
