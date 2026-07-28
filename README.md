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
- [Background & Problem Statement](#-background--problem-statement)
- [Vision & Mission](#-vision--mission)
- [Project Objectives](#-project-objectives)
- [Scope & Delimitations](#-scope--delimitations)
- [Key Features](#-key-features)
- [User Interface & Layout (Landscape-First)](#-user-interface--layout-landscape-first)
- [System Architecture](#-system-architecture)
- [Agroecological Decision Support System (DSS)](#-agroecological-decision-support-system-dss)
- [Technology Stack](#-technology-stack)
- [Repository Structure](#-repository-structure)
- [Development to Deployment Guide](#-development-to-deployment-guide)
- [Documentation Index](#-documentation-index)
- [Contributors & Academic Attributions](#-contributors--academic-attributions)

---

## 🌿 Overview

**MapTanim** is a landscape-oriented native Android application designed to empower smallholder vegetable farmers in the Philippines through an **Interactive Farm Workspace**, **Digital Farm Management**, and a deterministic **Agroecological Decision Support System (DSS)**.

Instead of relying on notebooks, memory, or complex form-based data entry, MapTanim transforms the farm itself into an interactive 2D visual workspace. Farmers directly draw, move, resize, rotate, and manage planting beds on a digital map, view companion planting compatibility overlays, monitor crop growth stages, and receive science-based recommendations aligned with Department of Agriculture (DA) and Bureau of Plant Industry (BPI) standards.

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

## 🌍 Background & Problem Statement

Vegetable farming is a cornerstone of food security in the Philippines. However, smallholder farmers often face critical challenges:
- Reliance on traditional guesswork without science-based soil or seasonal guidance.
- Lost or unorganized farming records across fragmented small plots.
- Over-application or misapplication of chemical fertilizers and pesticides.
- Lack of companion planting and intercropping knowledge to optimize land usage and suppress pests naturally.
- Existing digital tools are often designed for large commercial enterprise farms, require high-end hardware, or lack Philippine localization.

**MapTanim** bridges this gap by offering a locally adapted, offline-first mobile platform built specifically for Filipino vegetable farmers.

---

## 🌾 Vision & Mission

- **Vision**: To become a trusted, accessible digital farm management platform that empowers Filipino vegetable farmers through visual layout planning, organized record-keeping, and practical agricultural decision support.
- **Mission**: To modernize traditional vegetable farm operations in the Philippines by providing intuitive mobile tools that improve crop yield, promote sustainable intercropping, preserve digital records, and encourage technology adoption in rural communities.

---

## 🎯 Project Objectives

### General Objective
To design and develop MapTanim, a mobile-based farm management application that empowers smallholder vegetable farmers in the Philippines through structured crop knowledge, agricultural decision support, and interactive plot mapping.

### Specific Objectives
1. **Crop Knowledge & Cultivation**: Digitize care, growth stage, and harvest guidance for Philippine vegetable crops spanning all 8 plant-part classifications.
2. **Interactive Farm Management**: Enable farmers to visually plan, map, and manage planting beds, crops, soil types, and seasonal activities in an organized system.
3. **Agricultural Decision Support**: Provide deterministic recommendations for crop selection, companion planting, soil suitability, NPK nutrient management, and pest/disease alerts.
4. **Offline Caching & Cloud Sync**: Support offline farm management through Room database caching with seamless cloud synchronization to Supabase when connectivity is available.
5. **System Management & Security**: Enforce role-based access control (RBAC), OTP-based user authentication, and secure administrative monitoring.

---

## 📌 Scope & Delimitations

### Scope
- **8 Plant-Part Vegetable Classifications** (DA / PSA standards):
  1. *Bulb* (Onion, Garlic)
  2. *Stem* (Celery, Asparagus)
  3. *Shoot* (Labong / Bamboo Shoots, Bean Sprouts)
  4. *Leafy* (Pechay, Kangkong, Mustasa, Alugbati, Malunggay, Pako, Lettuce)
  5. *Flower* (Broccoli, Cauliflower, Katuray, Banana Blossom)
  6. *Fruit* (Tomato, Eggplant, Ampalaya, Squash, Okra, Cucumber, Pepper, Corn)
  7. *Root* (Carrot, Radish, Singkamas, Ginger, Turmeric)
  8. *Tuber* (Potato, Sweet Potato, Cassava, Gabi, Ube)
- **13 Covered High-Value Vegetables**: Tomato, Eggplant, Bell Pepper, Cabbage, Onion, Carrot, String Beans, Lettuce, Cucumber, Okra, Corn, Squash, Kangkong.
- **6 Soil Classifications**: Loam, Clay, Sandy, Silty, Peaty, Chalky.
- **3 Seasonal Windows**: Dry Season, Wet Season, Year-Round.
- **Growth Stage Tracking**: Germination -> Seedling -> Vegetative -> Flowering -> Ripening/Maturity -> Harvest.

### Delimitations
- Excludes artificial intelligence / machine learning models; decision support uses deterministic, rule-based algorithms derived from BPI and DA-BAR research.
- Excludes live commodity market trading or e-commerce transaction processing.
- Primary UI is in English with localized Tagalog/Philippine agricultural terminology.

---

## ✨ Key Features

### 🗺️ Interactive Plot Mapping Engine
- Draw, move, resize, rotate, and delete visual planting beds directly on a digital canvas.
- Paint soil types (Loam, Clay, Sandy, Silty, Peaty, Chalky) onto individual plot beds.
- Add physical structures such as trellises, fences, pathways, and water sources.
- Visual companion planting compatibility indicators (green compatible / red antagonist overlays).

### 🌱 Farm Management & Monitoring Dashboard
- Today's Tasks list (watering reminders, fertilizer application, harvest readiness, pest alerts).
- Farm summary counters (Total Beds, Active Plants, Ready to Harvest count, Active Alerts count).
- Growth stage timeline tracker with progress indicators.
- Yield recording and post-harvest handling guidelines.

### 📋 Agroecological Decision Support System (DSS)
- **Soil & Season Matching**: Filters suitable crops based on user-selected soil classification and seasonal windows.
- **Companion Planting Matrix**: Identifies beneficial vs. antagonist crop pairings for intercropping optimization.
- **NPK Nutrient Management**: Monitors Nitrogen, Phosphorus, and Potassium requirements per growth stage and provides deficiency alerts.
- **Pest & Disease Risk Calendar**: Season and growth-stage filtered alerts with biological and chemical intervention guides.
- **Crop Rotation & Soil Rest Planner**: Recommends soil recovery periods and optimal succeeding crops post-harvest.

---

## 📱 User Interface & Layout (Landscape-First)

MapTanim features a landscape-oriented user interface optimized for touch interactions on Android smartphones and tablets.

### 🎨 1. Edit Mode Interface
```
┌───────────────────────────────────────────────────────────────────────────────────────────────────┐
│ MAPTANIM   Murcia Farm (Negros Occidental)   [ EDIT MODE ]    28°C Partly Cloudy   (3) Notifications │
├───────────────────┬─────────────────────────────────────────────────────────────┬─────────────────┤
│ EDIT TOOLS        │                                                             │  [Undo] [Redo]  │
│ [Select/Move]     │               [BED 1: Eggplant]                             │  [Grid: ON]     │
│ [Add Bed]         │                                      [BED 3: Tomato]        │  [Snap: ON]     │
│ [Paint Soil]      │               [BED 2: Cucumber]        (Selected)           │  [Zoom: 100%]   │
│ [Add Trellis]     │   [BED A: Lettuce]                                          │                 │
│ [Add Fence]       │                        [BED F: Carrot]    [BED E: Cabbage]    │                 │
│ [Delete]          │                                                             │                 │
├───────────────────┤                                                             │                 │
│ SOIL TYPE         │                                                             │                 │
│ (Loam)(Clay)...   │                                                             │                 │
├───────────────────┴─────────────────────────────────────────────────────────────┴─────────────────┤
│ [Exit Edit Mode]  | 1 Bed Selected: Bed 3 • Tomato | (Duplicate)(Resize)(Soil)  [ SAVE CHANGES ]  │
├───────────────────────────────────────────────────────────────────────────────────────────────────┤
│ [ Home ]            [ Farms ]            [ Calendar ]         [ Library ]           [ Profile ]   │
└───────────────────────────────────────────────────────────────────────────────────────────────────┘
```

### 👁️ 2. View Mode / Dashboard Interface
```
┌───────────────────────────────────────────────────────────────────────────────────────────────────┐
│ MAPTANIM   Murcia Farm (Negros Occidental)                    28°C Partly Cloudy   (3) Notifications │
├───────────────────┬─────────────────────────────────────────────────────────────┬─────────────────┤
│ TODAY'S TASKS     │                 💧 (Water Bed 3)                            │  [ + Add ]      │
│ 💧 Water Bed 3    │                                      🌿 (Fertilize Bed 1)   │  [ Search ]     │
│ 🌿 Fertilize Bed 1│               🌾 (Harvest Bed R)                            │  [ Center ]     │
│ 🌾 Harvest Bed R  │                                      🐛 (Pest Alert Bed 2)  │  [ Layers ]     │
│ 🐛 Check Pest     │                                                             │                 │
├───────────────────┤                                                             │                 │
│ FARM SUMMARY      │                                                             │                 │
│ Beds: 12  Plants:186                                                            │                 │
│ Harvest:4 Alert: 2│                                                             │                 │
├───────────────────┴─────────────────────────────────────────────────────────────┴─────────────────┤
│ [ Home ]            [ Farms ]            [ Calendar ]         [ Library ]           [ Profile ]   │
└───────────────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 🏛️ System Architecture

```
                        MAPTANIM SYSTEM ARCHITECTURE

    ┌───────────────────────────────────────────────────────────────────┐
    │                 Android Mobile Client (Kotlin)                    │
    │  • Jetpack Compose UI (Landscape Workspace & Dashboard)           │
    │  • ViewModels & StateFlow                                         │
    │  • Clean Architecture (Presentation, Domain, Data)                │
    │  • Offline Persistence: Room Database + Encrypted Preferences     │
    └─────────────────────────────────┬─────────────────────────────────┘
                                      │
                         HTTPS / Supabase Kotlin SDK
                                      │
    ┌─────────────────────────────────▼─────────────────────────────────┐
    │                      Supabase Cloud Backend                       │
    │  • Authentication: Supabase Auth (Email / SMS OTP)                │
    │  • Cloud Database: PostgreSQL (RLS Policies Enabled)              │
    │  • Cloud Storage: Image & Reference Buckets                       │
    │  • Edge Functions: Rule Engine API & Report Processing            │
    │  • Realtime: Live Data Synchronization                            │
    └─────────────────────────────────┬─────────────────────────────────┘
                                      │
    ┌─────────────────────────────────▼─────────────────────────────────┐
    │                      Web Administration Panel                     │
    │  • User Management, Crop Library, DSS Rules, Analytics            │
    └───────────────────────────────────────────────────────────────────┘
```

---

## 🧠 Agroecological Decision Support System (DSS)

MapTanim employs a deterministic rule engine operating on pre-loaded agronomic research datasets:

```
┌─────────────────┐     ┌─────────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│  Farmer Inputs  │ ──► │ Agroecological Rules│ ──► │  Rule Evaluator  │ ──► │ Recommendations  │
│ Soil, Season,   │     │ Companion Matrix,   │     │ Soil Match, NPK, │     │ Crop suitability,│
│ Growth Stage    │     │ Pest Risk Calendar  │     │ Growth Stage Calc│     │ Care tasks, Pest │
└─────────────────┘     └─────────────────────┘     └──────────────────┘     └──────────────────┘
```

---

## 🛠️ Technology Stack

| Layer | Technology | Purpose |
|-------|------------|---------|
| **Mobile OS** | Android (API 26+) | Operating environment |
| **Language** | Kotlin 2.0+ | Mobile application language |
| **UI Toolkit** | Jetpack Compose | Declarative landscape UI |
| **Architecture** | MVVM + Clean Architecture | Separation of concerns |
| **Local Database** | Room Database | Offline-first data caching |
| **DI Framework** | Hilt | Dependency injection |
| **Networking** | Retrofit 2 + OkHttp 4 | HTTPS API communication |
| **Image Loading** | Coil | Efficient image caching |
| **Backend Services** | Supabase | BaaS Platform |
| **Database** | PostgreSQL 15+ | Relational cloud database |
| **Authentication** | Supabase Auth | OTP & JWT authentication |
| **Web Admin Panel** | React + TypeScript | Web administration dashboard |

---

## 📂 Repository Structure

```
MapTanim/
├── README.md                 # Primary project documentation
├── AGENTS.md                 # Developer & AI Agent guidelines
├── LICENSE                   # License information
├── CONTRIBUTING.md           # Contribution guidelines
├── CHANGELOG.md              # Project version history
├── CODE_OF_CONDUCT.md        # Code of conduct
├── SECURITY.md               # Security policy
│
├── mobile/                   # Native Android Kotlin App
│   ├── app/                  # Main Android Application module
│   │   └── src/main/java/com/maptanim/app/
│   │       ├── core/         # Datastore, Network, Utils, Validation
│   │       ├── data/         # Repositories, DAOs, Room DB, Mappers
│   │       ├── domain/       # UseCases, Domain Models, Rules
│   │       ├── dss/          # Rule Engine, Evaluator, Recommendation
│   │       ├── renderer/     # 2D Plot Render Engine (Camera, Grid, Layer)
│   │       ├── navigation/   # Jetpack Compose Navigation Graph
│   │       ├── ui/           # Screens (Edit, Home, Calendar, Library), Components
│   │       ├── viewmodel/    # StateHolders & UI StateFlows
│   │       ├── service/      # Background services
│   │       └── worker/       # WorkManager sync workers
│
├── backend/                  # Supabase Backend Configuration
│   ├── supabase/
│   │   ├── migrations/       # PostgreSQL SQL Migration scripts
│   │   ├── schema/           # Table schemas & Enum definitions
│   │   ├── policies/         # Row Level Security (RLS) policies
│   │   ├── functions/        # Supabase Edge Functions (Deno/TypeScript)
│   │   ├── storage/          # Storage bucket configurations
│   │   └── seed/             # Botanical & agronomic seed datasets
│   └── api/                  # API endpoint definitions
│
├── admin/                    # Web-Based Admin Panel (React + TypeScript)
│   ├── public/
│   └── src/                  # Admin Dashboard components & pages
│
├── shared/                   # Shared DTOs, Enums, and Constants
├── database/                 # SQL ERDs, backup files, and migration scripts
├── docs/                     # 34 Complete Documentation Modules (00 - 33)
├── diagrams/                 # Architecture, DFD, ERD, and UML diagrams
├── assets/                   # Visual textures, crop graphics, and branding assets
├── scripts/                  # Build, deployment, and database backup scripts
└── tests/                    # Mobile, Backend, and Integration test suites
```

---

## 🚀 Development to Deployment Guide

### 1. Prerequisites
- **JDK 17** or higher
- **Android Studio Jellyfish / Koala** or newer
- **Android SDK API 34**
- **Supabase CLI** (`npm install -g supabase`)

### 2. Local Setup
```bash
# Clone the repository
git clone https://github.com/Jcateo187/maptanim.git
cd MapTanim

# Start local Supabase environment (optional for local DB testing)
cd backend/supabase
supabase start

# Open Android project in Android Studio
# Open project directory: MapTanim/mobile
```

### 3. Environment Configuration
Create or edit `local.properties` in root:
```properties
SUPABASE_URL=https://ojilvcglpzbtpjxguhzj.supabase.co
SUPABASE_ANON_KEY=sb_publishable_fH5qY2HaAg-coV89IxOl2Q_Xf9ySGMU
```

### 4. Running the Mobile App
- Select the `app` configuration in Android Studio.
- Choose a landscape-oriented Android Emulator or physical device (Android 8.0+).
- Click **Run** (`Shift + F10`).

### 5. Deployment Pipeline
- **Supabase Backend**: Apply migrations using `supabase db push`.
- **Android App Bundle**: Generate signed AAB via `Build -> Generate Signed Bundle / APK` in Android Studio.
- **Play Store Release**: Upload AAB to Google Play Console.

---

## 📚 Documentation Index

All detailed specifications are stored in the [`docs/`](docs/) directory:

| Chapter | Document Title | Description |
|---------|----------------|-------------|
| 00 | [Getting Started](docs/00_GETTING_STARTED.md) | Developer workspace setup & prerequisites |
| 01 | [Project Overview](docs/01_PROJECT_OVERVIEW.md) | Background, vision, mission, 8 vegetable categories |
| 02 | [Software Requirements Specification](docs/02_SOFTWARE_REQUIREMENTS_SPECIFICATION.md) | Comprehensive SRS specifications |
| 03 | [System Architecture](docs/03_SYSTEM_ARCHITECTURE.md) | High-level system architecture & data flow |
| 04 | [Android Architecture](docs/04_ANDROID_ARCHITECTURE.md) | Kotlin MVVM + Clean Architecture details |
| 05 | [Backend Architecture](docs/05_BACKEND_ARCHITECTURE.md) | Supabase BaaS, PostgreSQL, & Edge Functions |
| 06 | [Admin Dashboard](docs/06_ADMIN_DASHBOARD.md) | Web administration dashboard design |
| 07 | [Database Design](docs/07_DATABASE_DESIGN.md) | PostgreSQL schema & table relationships |
| 08 | [Supabase Configuration](docs/08_SUPABASE_CONFIGURATION.md) | Step-by-step Supabase deployment guide |
| 09 | [Authentication](docs/09_AUTHENTICATION.md) | Supabase Auth, OTP delivery, & RBAC rules |
| 10 | [API Documentation](docs/10_API_DOCUMENTATION.md) | Supabase PostgREST endpoints & Edge Functions |
| 11 | [Navigation](docs/11_NAVIGATION.md) | Jetpack Compose screen routes & state flow |
| 12 | [UI/UX Guidelines](docs/12_UI_UX_GUIDELINES.md) | Landscape layout standards & design principles |
| 13 | [Design System](docs/13_DESIGN_SYSTEM.md) | Color palette, typography, & icon tokens |
| 14 | [Component Library](docs/14_COMPONENT_LIBRARY.md) | Reusable Compose UI components |
| 15 | [Render Engine](docs/15_RENDER_ENGINE.md) | 2D Isometric Plot Rendering Engine specifications |
| 16 | [Interactive Plot Mapping](docs/16_INTERACTIVE_PLOT_MAPPING.md) | Plot bed drawing, snapping, & soil painting |
| 17 | [Farm Management](docs/17_FARM_MANAGEMENT.md) | Farm creation, bed assignment, & crop tracking |
| 18 | [View Mode](docs/18_VIEW_MODE.md) | Farm dashboard, tasks, & status overlays |
| 19 | [Edit Mode](docs/19_EDIT_MODE.md) | Bed editor, tools panel, & save workflow |
| 20 | [Decision Support System](docs/20_DECISION_SUPPORT_SYSTEM.md) | Deterministic DSS rule engine & matrices |
| 21 | [Knowledge Base](docs/21_KNOWLEDGE_BASE.md) | Vegetable profiles, NPK tables, & pest guides |
| 22 | [Calendar](docs/22_CALENDAR.md) | Monthly planting calendar & task scheduler |
| 23 | [Notification System](docs/23_NOTIFICATION_SYSTEM.md) | Local alarms & Firebase push notifications |
| 24 | [Offline Synchronization](docs/24_OFFLINE_SYNCHRONIZATION.md) | Room caching & Supabase sync queue strategy |
| 25 | [Security](docs/25_SECURITY.md) | Encryption, Bcrypt hashing, & OWASP MASVS compliance |
| 26 | [Testing](docs/26_TESTING.md) | Unit tests, Compose UI tests, & integration tests |
| 27 | [Deployment](docs/27_DEPLOYMENT.md) | Android App Bundle build & Play Store release |
| 28 | [Project Structure](docs/28_PROJECT_STRUCTURE.md) | Taxonomy of modules and source packages |
| 29 | [Coding Standards](docs/29_CODING_STANDARDS.md) | Kotlin style guide & code conventions |
| 30 | [Git Workflow](docs/30_GIT_WORKFLOW.md) | Branching strategy & pull request guidelines |
| 31 | [Contributing](docs/31_CONTRIBUTING.md) | Contributor guidelines and workflow |
| 32 | [Changelog](docs/32_CHANGELOG.md) | Release version history |
| 33 | [Roadmap](docs/33_ROADMAP.md) | Future development milestones |

---

## 👥 Contributors & Academic Attributions

This project is developed by the Capstone Research Team at **STI West Negros University** (Bacolod City, Philippines):

- **Jomarey D. Parreño** – Project Manager
- **John Ryan R. Vasquez** – System Analyst
- **Jason B. Juanillo** – Lead Programmer
- **James M. Cateo** – UI/UX & Assistant Programmer

**Academic Supervision**:
- **Ms. Danica S. Duazo** – Capstone Adviser
- **Engr. Nahdem C. Columida, CpE** – Capstone Coordinator
- **Mae B. Lodana, PhD TM** – CICT Dean

*Copyright © 2026 MapTanim Capstone Team. All rights reserved.*
