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

Instead of relying on notebooks, memory, or complex form-based data entry, MapTanim transforms the farm into a dynamic 2D visual workspace. Built on **Direct-to-Soil Canvas Planting**, farmers directly drag and drop crops onto soil grid tiles (creating expandable 1×1 $m^2$ crop zones with no separate physical raised bed objects required), view companion planting compatibility overlays, monitor crop growth stages, and receive science-based recommendations aligned with Department of Agriculture (DA) and Bureau of Plant Industry (BPI) standards.

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
  - **Profile Avatar & Nickname**: Custom profile avatar image and user's nickname (derived from email prefix upon registration or customized in profile settings) in a dark rounded pill.
  - **Farm Name**: Displays the active farm name loaded directly from Supabase / local Room database.
  - **Crops Counter Chip**: Dark pill chip displaying real-time total active crops count (`🌱 Crops`).
  - **Harvest Counter Chip**: Orange pill chip displaying real-time harvest-ready crops count (`🚜 Ready to Harvest`).
  - **Quick Action Icons**: Notification Bell (with unread notification badge count) and Settings Gear buttons.
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
- **Live Placement Preview & Drag-and-Drop**:
  - Dragging a crop card from `CropTray` displays an elevated floating circular preview carrying the Stage 1 crop sprite.
  - A real-time placement preview renders across soil tiles: **Blue Border** (valid placement) or **Red Border** (invalid/collision).
- **1×1 Drop Event & Selection Mode**:
  - Releasing finger instantiates an initial 1×1 `CropZone` (`width = 1`, `height = 1`).
  - Auto-selects the Crop Zone, replacing the blue preview with a permanent **White Border** outline and displaying the floating bottom toolbar.
- **Contextual Bottom Toolbar & 8-Handle Resizing**:
  - Displays dark floating bottom pill bar with 3 tools: **Duplicate**, **Resize**, **Delete**.
  - Tapping **Resize** activates **8 interactive handles** (corners and edge midpoints) to expand `width` and `height`, automatically populating new grid tiles with duplicate plant instances via `PlantInstanceGenerator` without scaling plant sprites.
- **Placed Crop Re-positioning & Boundary Clamping**:
  - Pressing and dragging any placed Crop Zone locks onto it with 1:1 finger tracking (`Math.round` nearest grid rounding).
  - All drops, drag re-positioning, and hover previews are strictly clamped to `[0.0, 30.0 - cropSize]` meters inside perimeter fences.


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
  - PostgreSQL Relational Database storing `users` (email + password authentication), `profiles` (`nickname`, `avatar`), `farms`, `crop_plots`, `crops`, `dss_rules`, and `tasks`.
  - Row Level Security (RLS) policies enforcing multi-tenant farmer data isolation.
  - Automatic `handle_new_user()` trigger for profile creation upon registration.
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
├── docs/                    # Complete 37-chapter Markdown Documentation suite
└── README.md                # Project Overview & Specification
```

---

## 📚 Complete Documentation Suite (44 Technical Modules)
All technical documentation modules are located in the [`docs/`](docs/) directory and indexed in the [Master Documentation Hub](docs/README.md):

| Chapter | Module / Topic | Link |
|---|---|---|
| **Hub** | **Master Documentation Index & Hub** | [README.md](docs/README.md) |
| **00** | Getting Started & Environment Setup | [00_GETTING_STARTED.md](docs/00_GETTING_STARTED.md) |
| **01** | Project Overview & Goals | [01_PROJECT_OVERVIEW.md](docs/01_PROJECT_OVERVIEW.md) |
| **02** | Software Requirements Specification | [02_SOFTWARE_REQUIREMENTS_SPECIFICATION.md](docs/02_SOFTWARE_REQUIREMENTS_SPECIFICATION.md) |
| **03** | System Architecture | [03_SYSTEM_ARCHITECTURE.md](docs/03_SYSTEM_ARCHITECTURE.md) |
| **04** | Android App Architecture | [04_ANDROID_ARCHITECTURE.md](docs/04_ANDROID_ARCHITECTURE.md) |
| **05** | Backend Services Architecture | [05_BACKEND_ARCHITECTURE.md](docs/05_BACKEND_ARCHITECTURE.md) |
| **06** | Admin Dashboard Specifications | [06_ADMIN_DASHBOARD.md](docs/06_ADMIN_DASHBOARD.md) |
| **07** | Database Schema & SQL Design | [07_DATABASE_DESIGN.md](docs/07_DATABASE_DESIGN.md) |
| **08** | Supabase Project Configuration | [08_SUPABASE_CONFIGURATION.md](docs/08_SUPABASE_CONFIGURATION.md) |
| **09** | Authentication & Security | [09_AUTHENTICATION.md](docs/09_AUTHENTICATION.md) |
| **10** | API Reference & Endpoints | [10_API_DOCUMENTATION.md](docs/10_API_DOCUMENTATION.md) |
| **11** | App Navigation Graph | [11_NAVIGATION.md](docs/11_NAVIGATION.md) |
| **12** | UI / UX Design Guidelines | [12_UI_UX_GUIDELINES.md](docs/12_UI_UX_GUIDELINES.md) |
| **13** | Theme & Design System | [13_DESIGN_SYSTEM.md](docs/13_DESIGN_SYSTEM.md) |
| **14** | Jetpack Compose Component Library | [14_COMPONENT_LIBRARY.md](docs/14_COMPONENT_LIBRARY.md) |
| **15** | 2D Isometric Render Engine | [15_RENDER_ENGINE.md](docs/15_RENDER_ENGINE.md) |
| **16** | Interactive Plot Mapping Specs | [16_INTERACTIVE_PLOT_MAPPING.md](docs/16_INTERACTIVE_PLOT_MAPPING.md) |
| **17** | Digital Farm Management Engine | [17_FARM_MANAGEMENT.md](docs/17_FARM_MANAGEMENT.md) |
| **18** | View Mode & Dashboard Specifications | [18_VIEW_MODE.md](docs/18_VIEW_MODE.md) |
| **19** | Edit Mode & Layout Tooling | [19_EDIT_MODE.md](docs/19_EDIT_MODE.md) |
| **20** | Agroecological Decision Support System | [20_DECISION_SUPPORT_SYSTEM.md](docs/20_DECISION_SUPPORT_SYSTEM.md) |
| **21** | Philippine Vegetable Knowledge Base | [21_KNOWLEDGE_BASE.md](docs/21_KNOWLEDGE_BASE.md) |
| **22** | Planting Calendar Engine | [22_CALENDAR.md](docs/22_CALENDAR.md) |
| **23** | Push & Local Notification System | [23_NOTIFICATION_SYSTEM.md](docs/23_NOTIFICATION_SYSTEM.md) |
| **24** | Offline-First Sync Architecture | [24_OFFLINE_SYNCHRONIZATION.md](docs/24_OFFLINE_SYNCHRONIZATION.md) |
| **25** | Data Protection & RLS Security | [25_SECURITY.md](docs/25_SECURITY.md) |
| **26** | Testing Strategy & Automation | [26_TESTING.md](docs/26_TESTING.md) |
| **27** | Deployment & Release Pipeline | [27_DEPLOYMENT.md](docs/27_DEPLOYMENT.md) |
| **28** | Repository Project Directory Structure | [28_PROJECT_STRUCTURE.md](docs/28_PROJECT_STRUCTURE.md) |
| **29** | Kotlin & Compose Coding Standards | [29_CODING_STANDARDS.md](docs/29_CODING_STANDARDS.md) |
| **30** | Git Branching & Commit Workflow | [30_GIT_WORKFLOW.md](docs/30_GIT_WORKFLOW.md) |
| **31** | Developer Contribution Guidelines | [31_CONTRIBUTING.md](docs/31_CONTRIBUTING.md) |
| **32** | Version Release History & Changelog | [32_CHANGELOG.md](docs/32_CHANGELOG.md) |
| **33** | Project Feature Roadmap | [33_ROADMAP.md](docs/33_ROADMAP.md) |
| **34** | Direct Soil Crop Planting & 8-Handle Resize System | [34_CROP_PLANTING_AND_RESIZE_SYSTEM.md](docs/34_CROP_PLANTING_AND_RESIZE_SYSTEM.md) |
| **35** | Asset Pipeline & Isometric Graphics | [35_ASSETS_PLANNING.md](docs/35_ASSETS_PLANNING.md) |
| **36** | Crop Variety Timeline & Seasonality | [36_CROP_VARIETY_TIMELINE_AND_SEASONALITY.md](docs/36_CROP_VARIETY_TIMELINE_AND_SEASONALITY.md) |
| **37** | Specifications & Scope Refinements | [37_SYSTEM_SPECIFICATIONS_AND_SCOPE_REFINEMENTS.md](docs/37_SYSTEM_SPECIFICATIONS_AND_SCOPE_REFINEMENTS.md) |
| **38** | Audio & Sound Design Specifications | [38_AUDIO_AND_SOUND_ASSETS_PLANNING.md](docs/38_AUDIO_AND_SOUND_ASSETS_PLANNING.md) |
| **39** | Crop View Interaction & Variety Simulation | [39_CROP_VIEW_INTERACTION_AND_VARIETY_SIMULATION.md](docs/39_CROP_VIEW_INTERACTION_AND_VARIETY_SIMULATION.md) |
| **40** | User & Profile Schema Refinement | [40_USER_AND_PROFILE_SCHEMA_REFINEMENT.md](docs/40_USER_AND_PROFILE_SCHEMA_REFINEMENT.md) |
| **41** | Users & Profiles Database Tables | [41_USERS_AND_PROFILES_DATABASE_TABLES.md](docs/41_USERS_AND_PROFILES_DATABASE_TABLES.md) |
| **42** | High-Scalability & Multi-Tenancy Architecture | [42_SCALABILITY_AND_MULTI_TENANCY_ARCHITECTURE.md](docs/42_SCALABILITY_AND_MULTI_TENANCY_ARCHITECTURE.md) |
| **Ops** | DevOps Architecture & Free Release Pipelines | [DEVOPS.md](docs/DEVOPS.md) |

---

## 👥 Contributors & Academic Attributions
- **Jomarey D. Parreño** — Project Manager
- **John Ryan R. Vasquez** — System Analyst
- **Jason B. Juanillo** — Lead Programmer
- **James M. Cateo** — UI/UX & Assistant Programmer
- **Capstone Adviser**: Ms. Danica S. Duazo  
*STI West Negros University — College of Information and Communications Technology (2026)*
