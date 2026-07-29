# 🌱 MapTanim

> **A Mobile-Based Interactive Farm Management and Agricultural Decision Support System (DSS) for Vegetable Farmers in the Philippines**

<div align="center">

![Platform](https://img.shields.io/badge/Platform-Android-green?style=for-the-badge&logo=android)
![Language](https://img.shields.io/badge/Language-Kotlin-purple?style=for-the-badge&logo=kotlin)
![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-blue?style=for-the-badge&logo=android)
![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Clean%20Architecture-orange?style=for-the-badge)
![Backend](https://img.shields.io/badge/Backend-Supabase-3ECF8E?style=for-the-badge&logo=supabase)
![Database](https://img.shields.io/badge/Database-Room%20%7C%20PostgreSQL-blue?style=for-the-badge&logo=postgresql)
![Status](https://img.shields.io/badge/Status-Completed%20%2F%20Active-brightgreen?style=for-the-badge)
![License](https://img.shields.io/badge/License-Academic-lightgrey?style=for-the-badge)

</div>

---

## 📖 Table of Contents
- [Overview](#-overview)
- [Capstone Project Details](#-capstone-project-details)
- [Final User Interface & Screen Specifications](#-final-user-interface--screen-specifications)
  - [1. HomeScreen (View Mode Dashboard)](#1-homescreen-view-mode-dashboard)
  - [2. EditScreen (Default View & Floating Controls)](#2-editscreen-default-view--floating-controls)
  - [3. EditScreen (Crop Tray & Crop Selection Active View)](#3-editscreen-crop-tray--crop-selection-active-view)
- [Core Features & Direct Planting Mechanics](#-core-features--direct-planting-mechanics)
- [Technology Stack](#-technology-stack)
- [Backend & Database Architecture](#-backend--database-architecture)
- [Admin Dashboard (React + TypeScript)](#-admin-dashboard-react--typescript)
- [Decision Support System (DSS)](#-decision-support-system-dss)
- [Repository Structure](#-repository-structure)
- [Documentation Index](#-documentation-index)
- [Contributors & Academic Attributions](#-contributors--academic-attributions)

---

## 🌿 Overview

**MapTanim** is a landscape-oriented native Android application designed to empower smallholder vegetable farmers in the Philippines through an **Interactive 2D Isometric Farm Workspace**, **Digital Farm Management**, and a deterministic **Agroecological Decision Support System (DSS)**.

Instead of relying on notebooks, memory, or complex form-based data entry, MapTanim transforms the farm into a dynamic 2D visual workspace. Farmers directly drag, drop, re-position, and manage planting beds on a digital soil grid, view companion planting compatibility overlays, monitor crop growth stages, and receive science-based recommendations aligned with Department of Agriculture (DA) and Bureau of Plant Industry (BPI) standards.

---

## 🎓 Capstone Project Details

- **Title**: MapTanim: A Mobile-Based Interactive Farm Management with Agricultural Decision Support for Vegetable Farmers
- **Institution**: STI West Negros University, College of Information and Communications Technology (2026)
- **Degree**: Bachelor of Science in Information Technology
- **Proponents**:
  - Jomarey D. Parreño (Project Manager)
  - John Ryan R. Vasquez (System Analyst)
  - Jason B. Juanillo (Lead Programmer)
  - James M. Cateo (UI/UX & Assistant Programmer)
- **Capstone Adviser**: Ms. Danica S. Duazo
- **Capstone Coordinator**: Engr. Nahdem C. Columida, CpE
- **CICT Dean**: Mae B. Lodana, PhD TM

---

## 🖼️ Final User Interface & Screen Specifications

### 1. HomeScreen (View Mode Dashboard)
![HomeScreen UI](https://raw.githubusercontent.com/maptanim/mobile/main/docs/assets/homescreen_final.png)

#### UI Elements:
- **Top HUD Bar**:
  - **Profile Avatar**: Circular green avatar (`#4CAF50`).
  - **Farm Selector**: Dark pill dropdown displaying farm name (`"Murcia Farm"` ▾).
  - **Crops Counter Chip**: Dark pill chip displaying total active plants (`🌱 186 Crops`).
  - **Harvest Counter Chip**: Orange pill chip displaying harvest-ready crops (`🚜 4 Ready to Harvest`).
  - **Quick Action Icons**: Notification Bell and Settings Gear buttons.
- **Left HUD Panel**:
  - **Monitoring Card**: Dark rounded card with green radio icon, titled **"Monitoring"**, subtitle `"Full Screen"`.
  - **Today's Tasks Card**: Dark rounded card with blue clipboard icon, titled **"Today's Tasks"**, subtitle `"4 Tasks"`.
- **Right HUD Panel**:
  - **Library Card**: Dark rounded card with book icon, titled **"Library"**.
  - **Community Card**: Dark rounded card with chat bubbles icon, titled **"Community"**.
- **Bottom Right Action Button**: Prominent white rounded rectangular Floating Action Button (FAB) with green pencil icon + `"Edit"` label navigating directly to `FarmEditorScreen`.
- **2D Isometric Map Canvas**: 30m x 30m loam soil grid surrounded by perimeter wooden fences, coconut trees, mango trees, banana trees, flowers, bushes, and rocks.

---

### 2. EditScreen (Default View & Floating Controls)
![EditScreen Default View](https://raw.githubusercontent.com/maptanim/mobile/main/docs/assets/editscreen_default_final.png)

#### UI Elements:
- **Top Right Navigation Bar**:
  - **Save Button**: Green rounded pill button (`#2E7D32`) with white save icon + `"Save"` text. Triggers the **Save Farm Layout** modal dialog.
  - **Exit Button**: Red rounded pill button (`#C62828`) with white exit icon + `"Exit"` text. Returns to `HomeScreen`.
- **Right Floating Toggle Button**:
  - **Add Plant / Crops Button**: Dark floating pill button with green flower icon + `"Add Plant / Crops"` label. Opens `CropTray`.

---

### 3. EditScreen (Crop Tray & Crop Selection Active View)
![EditScreen Active Crop Selection](https://raw.githubusercontent.com/maptanim/mobile/main/docs/assets/editscreen_croptray_final.png)

#### UI Elements & Direct Planting Mechanics:
- **Right Crop Selection Drawer (`CropTray`)**:
  - **Header**: Title `"SELECT CROPS (2)"` with close `✕` button.
  - **Filter Tabs**: `All`, `Seasonal`, `Permanent` with green active underline.
  - **Search & Category**: `"Search crops..."` input field with search icon and category filter button.
  - **Instruction Banner**: `💡 Hold & drag crop card onto farm area to plant`.
  - **Crop Cards**:
    - **Carrot Card**: Light green active card with carrot graphic, titled **"Carrot"**, subtitle `Root • Drag/Tap`.
    - **String Beans Card**: Light gray card with beans graphic, titled **"String Beans"**, subtitle `Podded • Drag/Tap`.
- **Live CoC-Style Glowing Green Tile Highlight**:
  - Dragging a crop card from `CropTray` displays an elevated floating circle preview layer containing the single crop Stage 1 PNG sprite (`crop_carrot_1.png` / `crop_stringbeans_1.png`) right under your finger.
  - A glowing isometric green rhombus (`#4CAF50`) glides across soil tiles in real-time to preview exact drop placement.
- **Placed Crop Re-positioning**:
  - Pressing and dragging any placed crop plot locks onto it with 1:1 finger tracking (`Math.round` nearest grid rounding).
  - The glowing green tile highlight glides beneath the crop during drag re-positioning.
- **Strict 30m x 30m Farm Area Boundary Clamping**:
  - All drops, drag re-positioning, and hover tile highlights are strictly clamped to `[0.0, 30.0 - cropSize]` meters. Crops cannot cross perimeter fences into outer scenery.
- **Clean Selection & Contextual Bottom Bar**:
  - Tapping a placed crop highlights it with a clean dashed blue selection outline (`#1E88E5`).
  - Displays dark floating bottom pill bar with 3 diagram tools: **Duplicate**, **Resize**, **Delete**.

---

## 🛠️ Technology Stack

| Layer | Technologies & Tools |
|---|---|
| **Mobile Client** | Native Android, **Kotlin**, **Jetpack Compose**, Compose Canvas, ViewModel, StateFlow, Coroutines |
| **Local Database** | **Room Database** (SQLite), Offline-first Caching, DataStore |
| **Backend & Cloud** | **Supabase** (PostgreSQL, Supabase Auth, Storage, Edge Functions, Realtime, Row Level Security) |
| **Admin Dashboard** | **React**, **TypeScript**, Vite, TailwindCSS, Recharts, Lucide Icons |
| **Decision Support** | Agroecological Rule Engine (Kotlin / TypeScript Edge Functions), DA/BPI Philippine Data |
| **Build System & Tools**| Gradle (Kotlin DSL), Git, GitHub Actions, Android Studio, Antigravity CLI |

---

## 🗄️ Backend & Database Architecture

- **Supabase Cloud Infrastructure**:
  - URL: `https://ojilvcglpzbtpjxguhzj.supabase.co`
  - PostgreSQL Relational Database storing `farms`, `crop_plots`, `crops`, `dss_rules`, `tasks`, and `profiles`.
  - Row Level Security (RLS) policies enforcing multi-tenant farmer data isolation.
  - Edge Functions (`evaluate-dss`, `sync-offline-queue`) handling automated DSS calculations.
- **Room SQLite Local Persistence**:
  - Offline-first architecture allowing full farm layout editing and task completion without active internet connection.
  - `SyncWorker` automatically pushes local offline mutations to Supabase when network connectivity resumes.

---

## 💻 Admin Dashboard (React + TypeScript)

- **Location**: `admin/` project module.
- **Role**: Web-based management console for agricultural extension officers and system administrators.
- **Features**:
  - User and Farm Management (view registered farmers, active plots, regional analytics).
  - Crop Knowledge Base Editor (manage high-value crops, plant-part categories, growth stages, NPK requirements).
  - DSS Rule Configurator (update soil suitability, companion planting rules, seasonal planting windows).
  - System Telemetry & Logs.

---

## 🧠 Decision Support System (DSS)

Deterministically evaluates agroecological rules against 5 core farm parameters:
1. **Soil Classification**: Loam, Clay, Sandy, Silty, Peaty, Chalky.
2. **Plant-Part Category (8 Types)**: Bulb, Stem, Shoot, Leafy, Flower, Fruit, Root, Tuber.
3. **Seasonal Planting Window**: Dry Season, Wet Season, Year-Round.
4. **Companion Planting Rules**: Intercropping compatibility, pest suppression pairings.
5. **Growth Stage & NPK Requirements**: Nitrogen, Phosphorus, Potassium balance per growth stage.

---

## 📂 Repository Structure

```
MapTanim/
├── mobile/                  # Native Android Kotlin Application
│   └── app/
│       └── src/main/java/com/maptanim/app/
│           ├── data/        # Room entities, DAOs, Repositories, Supabase DTOs
│           ├── domain/      # UseCases, Repositories interfaces, Domain Models
│           ├── renderer/    # FarmCanvasRenderer, IsometricProjection, PlantInstanceGenerator
│           └── ui/          # Jetpack Compose Screens (Home, Edit, Tasks, Settings, HUD)
├── backend/                 # Supabase Database Migrations, RLS Policies, Edge Functions
├── admin/                   # React + TypeScript Web Admin Dashboard
├── shared/                  # Shared DTOs and validation constants
├── docs/                    # Complete 34-chapter Markdown Documentation suite
└── README.md                # Project Overview & Specification
```

---

## 📚 Documentation Index
All 34 chapters are located in the `docs/` directory:
- [14. UI Component Library](docs/14_COMPONENT_LIBRARY.md)
- [15. 2D Isometric Render Engine](docs/15_RENDER_ENGINE.md)
- [18. View Mode — Home Screen Dashboard](docs/18_VIEW_MODE.md)
- [19. Edit Mode — Farm Editor](docs/19_EDIT_MODE.md)
- [20. Decision Support System (DSS)](docs/20_DECISION_SUPPORT_SYSTEM.md)
- [24. Offline Synchronization Architecture](docs/24_OFFLINE_SYNCHRONIZATION.md)

---

## 👥 Contributors & Academic Attributions
- **Jomarey D. Parreño** — Project Manager
- **John Ryan R. Vasquez** — System Analyst
- **Jason B. Juanillo** — Lead Programmer
- **James M. Cateo** — UI/UX & Assistant Programmer
- **Capstone Adviser**: Ms. Danica S. Duazo  
*STI West Negros University — College of Information and Communications Technology (2026)*
