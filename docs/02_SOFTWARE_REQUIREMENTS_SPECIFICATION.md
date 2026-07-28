# 02. Software Requirements Specification (SRS)

## 📌 Document Information

| Field | Value |
|-------|-------|
| **Document Version** | 1.1.1 |
| **System Name** | MapTanim |
| **Status** | Approved |
| **Authors** | Parreño, Vasquez, Juanillo, Cateo |

---

## 1. Functional Requirements

---

### FR-01: View Mode / Home Dashboard

**FR-01.1 Top Bar (View Mode)**
- System SHALL display the user's avatar chip at top-left.
- System SHALL display a farm selector showing active farm name (`Murcia Farm ▼`) with a dropdown chevron.
- System SHALL display 3 Top Bar Statistics Cards:
  1. `Active Alerts` (pest alerts, warnings)
  2. `Total Crops` (total planted crop plots)
  3. `Ready to Harvest` (harvest-ready crop plots)
- System SHALL display a notification bell `🔔` with a red badge showing unread notification count.
- System SHALL display a Settings icon `⚙️` for user account, account binding, and system configuration.

**FR-01.2 Left Toolbar (View Mode)**
- System SHALL display 2 HUD action buttons on the left toolbar:
  - **Monitoring**: Opens full-screen crop monitoring overlay.
  - **Today's Tasks**: Opens daily DSS task care checklist sheet.

**FR-01.3 Right Floating Toolbar (View Mode)**
- System SHALL display a right floating toolbar with:
  - **Crop Library**: Opens Philippine vegetable crop library.
  - **Planting Guides**: Opens companion planting and growth guides.
  - **Re-Center**: Resets camera to center the farm canvas.

**FR-01.4 Shared 2D Isometric Farm Canvas (View Mode & Edit Mode)**
- System SHALL render a 2D isometric soil map shared between View Mode and Edit Mode (`FarmCanvasRenderer`).
- System SHALL render direct soil planted crops with high-resolution PNG sprites (**Carrot** 🥕, **String Beans** 🫘).
- System SHALL display status badge pins on plots with active DSS tasks (Water 💧, Fertilize 🌿, Harvest 🌾, Pest Alert 🐛).
- System SHALL enforce 70% min zoomout (`minZoom = 0.70f`) and expanded camera pan bounds (`maxPanX/Y = 2500f * zoom`) for smooth left-side map navigation.

---

### FR-02: Edit Mode / Create Farm Layout

**FR-02.1 Edit Tools Panel (`EDIT TOOLS`)**
- System SHALL provide a slide-out left panel containing:
  1. **Add Plant/Crops**: Opens right-side `CropTray` panel to select and plant crops.
  2. **Select / Move**: Highlights selected crop plot with outer dashed blue boundary and 8 corner/edge handles for resizing; holding a crop plot allows dragging and dropping it anywhere inside the farm map bounds.
  3. **Delete**: Deletes selected crop plot from farm layout.
  4. **Divider Line**: `────────────────────`
  5. **Save**: Triggers Save Farm Dialog flow (`Type farm name`, `Cancel`, `Okay`).
  6. **Exit**: Discards unsaved changes and returns to View Mode.

**FR-02.2 Crop Selection Panel (`CropTray`)**
- System SHALL display a right-side panel featuring active PNG sprite crops (**Carrot** 🥕, **String Beans** 🫘) and catalog crops.
- Tapping or dragging a crop plants it directly on the soil canvas.

**FR-02.3 Save Farm Layout & Setup Confirmation**
- Clicking **Save** SHALL display a dialog asking the user to `Type farm name`.
- Clicking **Cancel** SHALL close the dialog and remain on the EditScreen.
- Clicking **Okay** SHALL save layout data to **Supabase** (for authenticated users) or **Local Storage** (for guest users).
- System SHALL display an on-screen confirmation message `"Excellent Successful set up the farm"`.
- Clicking **Okay** on the confirmation dialog SHALL navigate the user directly to `HomeScreen`.
