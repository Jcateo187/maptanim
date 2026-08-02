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
- System SHALL display a farm selector showing active farm name (`farmName ▼`) with a dropdown chevron.
- System SHALL display Top Bar Statistics Cards:
  1. `Total Crops` (total active planted crops)
  2. `Ready to Harvest` (harvest-ready crop plots)
- System SHALL display a notification bell `🔔` with a badge showing unread notification count.
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
- System SHALL render direct soil planted crops with high-resolution PNG sprites (**Carrot** 🥕, **String Beans** 🫘, **Tomato** 🍅, **Eggplant** 🍆, etc.).
- System SHALL display status badge pins on plots with active DSS tasks (Water 💧, Fertilize 🌿, Harvest 🌾, Pest Alert 🐛).
- System SHALL enforce 70% min zoomout (`minZoom = 0.70f`) and expanded camera pan bounds (`maxPanX/Y = 2500f * zoom`) for smooth left-side map navigation.

---

### FR-02: Edit Mode / Create Farm Layout

**FR-02.1 Edit Tools Panel (`EDIT TOOLS`)**
- System SHALL provide a slide-out left panel containing:
  1. **Select & Move (`SELECT_MOVE`)**: Highlights selected crop plot with outer dashed boundary and 8 corner/edge handles for resizing; holding a crop plot allows dragging and dropping it anywhere inside the farm map bounds.
  2. **Add Plot (`ADD_PLOT`)**: Instantiates a new crop plot on the soil grid.
  3. **Add Plant (`ADD_PLANT`)**: Opens right-side `CropTray` drawer to select and assign crops.
  4. **Delete (`DELETE`)**: Deletes selected crop plot or item from farm layout.
  5. **Divider Line**: `────────────────────`
  6. **Save**: Saves layout changes to Room SQLite and queues background Supabase synchronization.
  7. **Exit**: Discards unsaved changes and returns to View Mode.

**FR-02.2 Crop Selection Panel (`CropTray`)**
- System SHALL display a right-side panel featuring active PNG sprite crops (**Carrot** 🥕, **String Beans** 🫘) and catalog crops.
- Tapping or dragging a crop plants it directly on the soil canvas.

**FR-02.3 Save Farm Layout & Setup Confirmation**
- Clicking **Save** SHALL display a dialog asking the user to `Type farm name`.
- Clicking **Cancel** SHALL close the dialog and remain on the EditScreen.
- Clicking **Okay** SHALL save layout data to **Supabase** (for authenticated users) or **Local Storage** (for guest users).
- System SHALL display an on-screen confirmation message `"Excellent Successful set up the farm"`.
- Clicking **Okay** on the confirmation dialog SHALL navigate the user directly to `HomeScreen`.

---

## 2. System Scope Boundaries & Explicit Clarifications

For complete technical specifications, see **[37_SYSTEM_SPECIFICATIONS_AND_SCOPE_REFINEMENTS.md](file:///d:/Development/MapTanim/docs/37_SYSTEM_SPECIFICATIONS_AND_SCOPE_REFINEMENTS.md)**.

1. **Authentication (Email OTP Only)**: Account registration and login use **Email OTP (Gmail SMTP Relay / Supabase Auth)**. Third-party paid SMS gateway services are explicitly excluded to maintain zero subscription cost.
2. **2D Isometric Canvas & Direct Soil Planting**: The 2D farm canvas strictly renders soil beds, crop growth stages, and outer perimeter scenery. Interactive grid tile compatibility overlays are omitted from the canvas layout.
3. **Monitoring Hub Compatibility Overlay**: Companion planting compatibility evaluation (beneficial vs. antagonistic pairings) is handled dynamically inside the **Monitoring Hub / Decision Support Overlay**.
4. **AgriLibrary Reference Materials**: DIY Support Structure & Trellising construction guides and material lists for climbing vegetables (*Ampalaya, Sitaw, Pipino*) are provided as reference guides within the **AgriLibrary**, not as draggable canvas objects.
5. **No Financial Market Estimator & No External Weather API**: Yield tracking records output in kilograms/units without live market price estimators. Weather advisories use offline seasonal calendars without paid third-party weather APIs.

