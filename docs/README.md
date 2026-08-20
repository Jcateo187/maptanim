# 📚 MapTanim Master Documentation Hub & Navigation Index

Welcome to the comprehensive technical documentation hub for **MapTanim** — a multi-tier, mobile-based interactive farm management and agroecological Decision Support System (DSS) designed for vegetable farmers in the Philippines.

---

## 🗺️ Documentation Suites Overview

This documentation suite consists of **44 technical chapters** covering setup, system architecture, database design, 2D isometric rendering, direct-to-soil canvas planting, agroecological decision algorithms, offline sync pipelines, and DevOps release workflows.

```
                              +---------------------------------+
                              |   MapTanim Technical Docs Hub  |
                              +----------------+----------------+
                                               |
         +-------------------+-----------------+-------------------+-------------------+
         |                   |                 |                   |                   |
         v                   v                 v                   v                   v
  [1. Architecture]  [2. Database & Auth]  [3. UI & Rendering]  [4. DSS & Domain]   [5. DevOps & Testing]
  - 00 Getting Started - 07 Database Design - 15 Render Engine   - 20 DSS Engine     - 26 Testing
  - 03 Architecture   - 08 Supabase Config  - 16 Plot Mapping   - 21 Knowledge Base - 27 Deployment
  - 04 Android App    - 09 Auth & Security  - 19 Edit Mode      - 22 Calendar       - DEVOPS Architecture
  - 05 Backend        - 40 Profile Schema   - 34 Canvas System  - 36 Crop Seasonality
  - 06 Admin Dashboard- 41 User Tables      - 35 Assets Spec    - 39 Crop Simulation
```

---

## 📁 Workspace Source Code Shortcuts

| Module | Location | Core Technologies | Primary Documentation |
|--------|----------|-------------------|-----------------------|
| **Android App** | [`mobile/`](file:///d:/Development/MapTanim/mobile) | Kotlin 2.2.10, AGP 9.2.1, Jetpack Compose, Room DB | [04_ANDROID_ARCHITECTURE.md](file:///d:/Development/MapTanim/docs/04_ANDROID_ARCHITECTURE.md) |
| **Admin Web Dashboard** | [`admin/`](file:///d:/Development/MapTanim/admin) | React 18, Vite 6, TypeScript 5.7, Tailwind CSS 4 | [06_ADMIN_DASHBOARD.md](file:///d:/Development/MapTanim/docs/06_ADMIN_DASHBOARD.md) |
| **Backend & Database** | [`backend/`](file:///d:/Development/MapTanim/backend), [`database/`](file:///d:/Development/MapTanim/database) | Supabase PostgreSQL, RLS Policies, Edge Functions | [05_BACKEND_ARCHITECTURE.md](file:///d:/Development/MapTanim/docs/05_BACKEND_ARCHITECTURE.md), [07_DATABASE_DESIGN.md](file:///d:/Development/MapTanim/docs/07_DATABASE_DESIGN.md) |
| **Data & Asset Scripts** | [`scripts/`](file:///d:/Development/MapTanim/scripts) | Python 3.10+, PIL asset pipelines, Crop CSV generators | [35_ASSETS_PLANNING.md](file:///d:/Development/MapTanim/docs/35_ASSETS_PLANNING.md) |
| **DevOps & Containers** | [`deployment/`](file:///d:/Development/MapTanim/deployment), [`.github/workflows/`](file:///d:/Development/MapTanim/.github/workflows) | Docker Compose, Nginx, GitHub Actions | [DEVOPS.md](file:///d:/Development/MapTanim/docs/DEVOPS.md), [27_DEPLOYMENT.md](file:///d:/Development/MapTanim/docs/27_DEPLOYMENT.md) |
| **Integration Tests** | [`tests/`](file:///d:/Development/MapTanim/tests) | Pytest, API integration suites, DB verification | [26_TESTING.md](file:///d:/Development/MapTanim/docs/26_TESTING.md) |

---

## 📖 Complete Master Chapter Directory

### 🚀 Group 1: Getting Started & Project Overview
- 📄 [**00. Getting Started & Environment Setup**](file:///d:/Development/MapTanim/docs/00_GETTING_STARTED.md) — Prerequisites, installation guide, JDK 17, Android Studio setup, local.properties, Node.js admin stack, and smoke test checklist.
- 📄 [**01. Project Overview & Capstone Information**](file:///d:/Development/MapTanim/docs/01_PROJECT_OVERVIEW.md) — Problem statement, academic attributions (STI West Negros University), core objectives, and target agricultural domains.
- 📄 [**02. Software Requirements Specification (SRS)**](file:///d:/Development/MapTanim/docs/02_SOFTWARE_REQUIREMENTS_SPECIFICATION.md) — Functional and non-functional requirements, user personas, hardware limits, and system constraints.
- 📄 [**28. Repository Directory Structure**](file:///d:/Development/MapTanim/docs/28_PROJECT_STRUCTURE.md) — Full layout of `mobile/`, `admin/`, `backend/`, `database/`, `deployment/`, `scripts/`, and `tests/`.
- 📄 [**31. Developer Contribution Guidelines**](file:///d:/Development/MapTanim/docs/31_CONTRIBUTING.md) — Standard operating procedures for pull requests, code reviews, issue logging, and environment standards.
- 📄 [**32. Version Release History & Changelog**](file:///d:/Development/MapTanim/docs/32_CHANGELOG.md) — Detailed version notes from initial prototype to production release.
- 📄 [**33. Project Feature Roadmap**](file:///d:/Development/MapTanim/docs/33_ROADMAP.md) — Short-term, mid-term, and long-term milestones for multi-region expansion and AI drone integrations.

---

### 🏛️ Group 2: System Architecture & Core Stack
- 📄 [**03. High-Level System Architecture**](file:///d:/Development/MapTanim/docs/03_SYSTEM_ARCHITECTURE.md) — End-to-end data flow, sync topology, client-server communication protocols, and component boundaries.
- 📄 [**04. Android Native Application Architecture**](file:///d:/Development/MapTanim/docs/04_ANDROID_ARCHITECTURE.md) — MVVM + Clean Architecture layer division, ViewModel lifecycle, Hilt dependency injection, and Jetpack Compose state management.
- 📄 [**05. Backend Services & Supabase Architecture**](file:///d:/Development/MapTanim/docs/05_BACKEND_ARCHITECTURE.md) — Supabase Edge Functions, RESTful endpoints, and database connection pooling.
- 📄 [**06. Admin Web Dashboard Specifications**](file:///d:/Development/MapTanim/docs/06_ADMIN_DASHBOARD.md) — React 18 + Vite 6 + TypeScript 5.7 management platform specs for crop catalog and platform analytics.
- 📄 [**29. Coding Standards & Conventions**](file:///d:/Development/MapTanim/docs/29_CODING_STANDARDS.md) — Formatting rules, linting configurations, naming conventions, and Kotlin idiom guidelines.
- 📄 [**42. High-Scalability & Multi-Tenancy Architecture**](file:///d:/Development/MapTanim/docs/42_SCALABILITY_AND_MULTI_TENANCY_ARCHITECTURE.md) — Multi-farm isolation, database partitioning, and enterprise scalability strategies.

---

### 🗄️ Group 3: Database Design, Authentication & Security
- 📄 [**07. Primary Database Schema & SQL Design**](file:///d:/Development/MapTanim/docs/07_DATABASE_DESIGN.md) — PostgreSQL table definitions, foreign key constraints, indexes, and Room entity mirrors.
- 📄 [**08. Supabase Project Configuration**](file:///d:/Development/MapTanim/docs/08_SUPABASE_CONFIGURATION.md) — Connection strings, migration management via Supabase CLI, and environment variables.
- 📄 [**09. Authentication & Session Management**](file:///d:/Development/MapTanim/docs/09_AUTHENTICATION.md) — Email/Password auth, JWT token storage, guest mode capabilities, and session restoration.
- 📄 [**10. API Reference & Endpoint Specifications**](file:///d:/Development/MapTanim/docs/10_API_DOCUMENTATION.md) — Supabase REST endpoint definitions, HTTP headers, request payloads, and error codes.
- 📄 [**24. Offline-First Synchronization Architecture**](file:///d:/Development/MapTanim/docs/24_OFFLINE_SYNCHRONIZATION.md) — Two-way Room-Supabase synchronization, WorkManager jobs, and conflict resolution rules.
- 📄 [**25. Data Security & Row-Level Security (RLS)**](file:///d:/Development/MapTanim/docs/25_SECURITY.md) — PostgreSQL RLS policies, input sanitization, and credential encryption.
- 📄 [**40. User & Profile Schema Refinements**](file:///d:/Development/MapTanim/docs/40_USER_AND_PROFILE_SCHEMA_REFINEMENT.md) — Extended profile attributes, farm role assignments, and preference settings.
- 📄 [**41. Users & Profiles Database Tables Specs**](file:///d:/Development/MapTanim/docs/41_USERS_AND_PROFILES_DATABASE_TABLES.md) — SQL DDL definitions for `profiles`, `user_roles`, and audit log tables.

---

### 🎨 Group 4: UI/UX, Design System & 2D Isometric Render Canvas
- 📄 [**11. Android Navigation Graph & Route Map**](file:///d:/Development/MapTanim/docs/11_NAVIGATION.md) — Navigation Compose route mapping, argument passing, and transition animations.
- 📄 [**12. UI / UX Guidelines & Visual Identity**](file:///d:/Development/MapTanim/docs/12_UI_UX_GUIDELINES.md) — Dark mode palette, landscape-first UI rules, accessibility standards, and typography.
- 📄 [**13. Design System & Theme Tokens**](file:///d:/Development/MapTanim/docs/13_DESIGN_SYSTEM.md) — Color tokens, spacing metrics, typography hierarchy, and shape specs.
- 📄 [**14. Jetpack Compose Component Library**](file:///d:/Development/MapTanim/docs/14_COMPONENT_LIBRARY.md) — Reusable HUD cards, pill chips, dialog modals, custom buttons, and sliders.
- 📄 [**15. 2D Isometric Render Engine**](file:///d:/Development/MapTanim/docs/15_RENDER_ENGINE.md) — Canvas projection algorithms (Cartesian grid to Isometric screen coordinates), tile rendering, and z-ordering.
- 📄 [**16. Interactive Plot Mapping Specifications**](file:///d:/Development/MapTanim/docs/16_INTERACTIVE_PLOT_MAPPING.md) — 45m x 45m loam soil grid specs, boundary bounds, background scenery objects, and touch interaction handling.
- 📄 [**18. View Mode & Dashboard Specifications**](file:///d:/Development/MapTanim/docs/18_VIEW_MODE.md) — Non-destructive inspection mode, crop info tooltips, growth stage previews, and weather overlays.
- 📄 [**19. Edit Mode & Farm Layout Tooling**](file:///d:/Development/MapTanim/docs/19_EDIT_MODE.md) — Crop placement tray, active edit controls, save/exit handlers, and placement validation overlays.
- 📄 [**34. Direct Soil Crop Planting & 8-Handle Resize System**](file:///d:/Development/MapTanim/docs/34_CROP_PLANTING_AND_RESIZE_SYSTEM.md) — Direct soil tile drop mechanics (1x1m base zones), multi-cell 8-handle bounding box expansion, companion planting matrix calculations, and collision checks.
- 📄 [**35. Asset Pipeline & Graphic Specifications**](file:///d:/Development/MapTanim/docs/35_ASSETS_PLANNING.md) — Sprite sheet resolutions, PNG asset organization, Python PIL asset generation scripts, and memory caching.
- 📄 [**38. Audio & Sound Design Specifications**](file:///d:/Development/MapTanim/docs/38_AUDIO_AND_SOUND_ASSETS_PLANNING.md) — Sound effects (SFX) triggers, ambient background music, and audio manager implementation.
- 📄 [**39. Crop View Interaction & Variety Simulation**](file:///d:/Development/MapTanim/docs/39_CROP_VIEW_INTERACTION_AND_VARIETY_SIMULATION.md) — Interactive crop inspection modals, variety simulation controls, and real-time yield estimates.

---

### 🌱 Group 5: Digital Farm Management & Decision Support System (DSS)
- 📄 [**17. Digital Farm Management Engine**](file:///d:/Development/MapTanim/docs/17_FARM_MANAGEMENT.md) — Farm record-keeping, activity logs, plot lifecycle management, and harvest tracking.
- 📄 [**20. Agroecological Decision Support System (DSS)**](file:///d:/Development/MapTanim/docs/20_DECISION_SUPPORT_SYSTEM.md) — Deterministic recommendation engine algorithms, DA/BPI Philippine agricultural guidelines, and growth stage formulas.
- 📄 [**21. Philippine Vegetable Knowledge Base**](file:///d:/Development/MapTanim/docs/21_KNOWLEDGE_BASE.md) — Comprehensive agronomic dataset covering 13 Philippine vegetable crops, pest identification, and soil requirements.
- 📄 [**22. Planting Calendar Engine**](file:///d:/Development/MapTanim/docs/22_CALENDAR.md) — Task scheduling, planting timelines, harvest date calculations, and seasonal advisories.
- 📄 [**23. Push & Local Notification System**](file:///d:/Development/MapTanim/docs/23_NOTIFICATION_SYSTEM.md) — AlarmManager local notification scheduling, task reminder alerts, and weather notification triggers.
- 📄 [**36. Crop Variety Timeline & Seasonality Specs**](file:///d:/Development/MapTanim/docs/36_CROP_VARIETY_TIMELINE_AND_SEASONALITY.md) — Detailed timeline curves, maturity days, wet/dry season suitability, and crop variety parameters.
- 📄 [**37. Specifications & Scope Refinements**](file:///d:/Development/MapTanim/docs/37_SYSTEM_SPECIFICATIONS_AND_SCOPE_REFINEMENTS.md) — Clarified functional boundaries, hardware requirements, and localized crop scope definitions.

---

### ♾️ Group 6: DevOps, Continuous Integration & Release Operations
- 📄 [**26. Automated Testing Strategy & Verification**](file:///d:/Development/MapTanim/docs/26_TESTING.md) — Android unit tests, Compose UI tests, Python API integration tests (`tests/integration`), and linting protocols.
- 📄 [**27. Deployment & Release Pipeline**](file:///d:/Development/MapTanim/docs/27_DEPLOYMENT.md) — Build configurations, APK artifact generation, Nginx admin containerization, and release checklists.
- 📄 [**30. Git Branching & Commit Workflow**](file:///d:/Development/MapTanim/docs/30_GIT_WORKFLOW.md) — GitFlow branching strategy (`main`, `develop`, `feature/*`), commit message formatting, and version tagging.
- 📄 [**DevOps Architecture & Free Release Pipelines (`DEVOPS.md`)**](file:///d:/Development/MapTanim/docs/DEVOPS.md) — Zero-cost deployment strategy, GitHub Actions CI workflows (`android-ci.yml`, `admin-ci.yml`, `devops-ci.yml`), APK artifact distribution, and Docker stack orchestration.

---

## 🔗 Documentation Maintenance & Standards

When creating or updating documentation within `docs/`:
1. **Header Navigation**: Every document must start with the standard top navigation bar linking to its previous chapter, `docs/README.md`, and next chapter.
2. **Cross-Links**: Use GitHub markdown links with `file:///` URIs or relative paths to link concepts to their corresponding chapters and source code files.
3. **No External Outdated Word References**: Base all technical claims on actual project implementations in `mobile/`, `admin/`, `backend/`, `database/`, `deployment/`, `scripts/`, `tests/` and well-planned `.md` specs.
