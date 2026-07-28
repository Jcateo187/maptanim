import os

docs_dir = r"d:\Development\MapTanim\docs"

docs_content = {
    "00_GETTING_STARTED.md": """# 00. Getting Started Guide

## 📌 Overview
Welcome to **MapTanim**! This guide provides step-by-step setup instructions for developers and contributors working on the MapTanim project. MapTanim is a native Android application built using **Kotlin** and **Jetpack Compose**, backed by **Supabase** as a Backend-as-a-Service (BaaS).

---

## 🛠️ Prerequisites & Requirements

Before setting up MapTanim, ensure your development workstation meets the following requirements:

| Component | Required Version | Description |
|-----------|------------------|-------------|
| **OS** | Windows 10/11, macOS (Ventura+), Linux (Ubuntu 22.04+) | 64-bit OS |
| **JDK** | JDK 17 (Azul Zulu or OpenJDK 17) | Required for Gradle 8.x and Kotlin 2.0+ |
| **IDE** | Android Studio Jellyfish (2023.3.1) or Koala (2024.1.1+) | Primary Android IDE |
| **Android SDK** | API Level 34 (Android 14) | Target SDK version |
| **Build Tools** | Android SDK Build-Tools 34.0.0 | SDK build tools |
| **CLI Tools** | Node.js v18+, npm v9+, Supabase CLI | Backend & database management |
| **Version Control** | Git 2.40+ | Code repository management |

---

## 🚀 Environment Setup Step-by-Step

### 1. Clone the Repository
```bash
git clone https://github.com/Jcateo187/maptanim.git
cd MapTanim
```

### 2. Configure Local Secrets (`local.properties`)
Create or edit `d:\\Development\\MapTanim\\local.properties`:
```properties
sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
SUPABASE_URL=https://ojilvcglpzbtpjxguhzj.supabase.co
SUPABASE_ANON_KEY=sb_publishable_fH5qY2HaAg-coV89IxOl2Q_Xf9ySGMU
```

### 3. Open Project in Android Studio
1. Launch **Android Studio**.
2. Select **Open** and navigate to `d:\\Development\\MapTanim`.
3. Wait for Gradle Sync to complete.
4. Verify Kotlin plugin version is set to `2.0.0` or higher in `gradle/libs.versions.toml`.

### 4. Setup Supabase Backend (Local CLI or Cloud)
```bash
# Navigate to Supabase configuration folder
cd backend/supabase

# Start local Supabase container (requires Docker Desktop)
supabase start

# Apply database migrations
supabase db push
```

### 5. Running the Application
1. In Android Studio, select the **mobile.app** run configuration.
2. Select an emulator or physical Android device (API 26+, recommended landscape orientation).
3. Press **Run** (`Shift + F10`).

---

## 🔍 Verification & Smoke Test
After launching the application on your emulator:
1. Verify the **Splash Screen** transitions smoothly to **LoadingScreen**.
2. Verify Guest Mode allows entering **Home Screen (View Mode)**.
3. Switch to **Edit Mode** and test placing a bed (e.g., Bed 1 Eggplant on Loam soil).
4. Verify the bed renders cleanly on the 2D isometric farm canvas.
""",

    "01_PROJECT_OVERVIEW.md": """# 01. Project Overview & Background

## 🎓 Capstone Academic Profile
- **Project Title**: MapTanim: A Mobile-Based Interactive Farm Management with Agricultural Decision Support for Vegetable Farmers
- **Institution**: STI West Negros University, College of Information and Communications Technology (2026)
- **Degree**: Bachelor of Science in Information Technology
- **Authors**: Jomarey D. Parreño, John Ryan R. Vasquez, Jason B. Juanillo, James M. Cateo
- **Capstone Adviser**: Ms. Danica S. Duazo

---

## 🌿 Executive Summary
MapTanim is a mobile-based interactive farm management and agricultural decision support system designed specifically for smallholder vegetable farmers in the Philippines. By transforming the physical farm into a digital 2D interactive workspace, farmers can visually design planting beds, assign crops, monitor growth stages, track harvest yields, and receive deterministic decision support.

---

## 🥦 Plant-Part Vegetable Classifications (DA & PSA Standards)

The platform categorizes vegetables into **8 plant-part classifications** as established by the Department of Agriculture (DA) and Philippine Statistics Authority (PSA):

| Classification | Plant Part Edible | Examples |
|----------------|-------------------|----------|
| **Bulb** | Underground bulb layers | Onion (*Allium cepa*), Garlic (*Allium sativum*) |
| **Stem** | Above-ground stem | Celery (*Apium graveolens*), Asparagus (*Asparagus officinalis*) |
| **Shoot** | Young emerging shoots | Bamboo shoots / Labong, Bean sprouts |
| **Leafy** | Edible leaves | Pechay, Kangkong, Mustasa, Alugbati, Malunggay, Pako, Lettuce |
| **Flower** | Immature floral buds | Broccoli, Cauliflower, Katuray, Banana Blossom |
| **Fruit** | Seed-bearing fruit structures | Tomato, Eggplant, Ampalaya, Squash, Okra, Cucumber, Pepper, Corn |
| **Root** | Fleshy taproot | Carrot, Radish, Singkamas, Ginger, Turmeric |
| **Tuber** | Swollen underground stem | Potato, Sweet Potato, Cassava, Gabi, Ube |

### 🎯 13 High-Value Target Vegetables Covered:
1. **Tomato** (Fruit)
2. **Eggplant** (Fruit)
3. **Bell Pepper** (Fruit)
4. **Cabbage** (Leafy)
5. **Onion** (Bulb)
6. **Carrot** (Root)
7. **String Beans** (Fruit/Legume)
8. **Lettuce** (Leafy)
9. **Cucumber** (Fruit)
10. **Okra** (Fruit)
11. **Corn** (Fruit/Grain)
12. **Squash** (Fruit)
13. **Kangkong** (Leafy)

---

## 🏔️ Soil Classifications Covered (6 Types)
1. **Loam**: Well-balanced soil rich in nutrients; ideal for most vegetables.
2. **Clay**: Heavy soil with high moisture retention; good for leafy and shallow-rooted crops when well-drained.
3. **Sandy**: Quick-draining warm soil; suited for root crops like carrots and singkamas.
4. **Silty**: Smooth soil with high organic capacity; supports fast-growing leafy greens.
5. **Peaty**: Organic-rich acidic soil; suitable for brassicas and root vegetables with soil amendments.
6. **Chalky**: Alkaline stony soil; requires organic matter additions for optimal yield.
""",

    "02_SOFTWARE_REQUIREMENTS_SPECIFICATION.md": """# 02. Software Requirements Specification (SRS)

## 📌 Document Purpose
This Software Requirements Specification (SRS) document defines the functional and non-functional requirements for the **MapTanim** Mobile Application, Supabase Backend, and Web Admin Panel.

---

## 🎯 Functional Requirements (FR)

### FR-01: Interactive Plot Mapping Engine
- **FR-01.1**: The system shall render a 2D isometric representation of the farm grid.
- **FR-01.2**: Users shall be able to create, move, resize, rotate, and delete planting beds in Edit Mode.
- **FR-01.3**: Users shall be able to assign one of 6 soil types (Loam, Clay, Sandy, Silty, Peaty, Chalky) to individual beds.
- **FR-01.4**: Users shall be able to assign crops to plot beds from a selection list of 13 high-value vegetables.
- **FR-01.5**: The engine shall display companion planting overlays (green check for compatible, red warning for antagonist pairs).

### FR-02: Farm Management & Dashboard
- **FR-02.1**: The dashboard shall display a "Today's Tasks" list featuring watering, fertilization, harvest readiness, and pest alerts.
- **FR-02.2**: The dashboard shall display farm summary cards: Total Beds, Total Active Plants, Ready to Harvest count, Active Alerts count.
- **FR-02.3**: Users shall be able to record harvested yields (in kg/units) and view historical logs.

### FR-03: Agroecological Decision Support System (DSS)
- **FR-03.1**: The system shall filter crop suitability based on user-selected soil type and planting season (Dry, Wet, Year-Round).
- **FR-03.2**: The DSS shall calculate growth stages (Germination -> Seedling -> Vegetative -> Flowering -> Ripening -> Harvest) from the planting date.
- **FR-03.3**: The system shall issue stage-specific NPK nutrient application guidance and pest risk alerts.

### FR-04: User Authentication & Security
- **FR-04.1**: Users shall be able to register and log in via Email or Mobile Number.
- **FR-04.2**: The system shall deliver a 6-digit OTP (Gmail SMTP / SMS) with a 5-minute expiration window.
- **FR-04.3**: Account access shall lock for 15 minutes after 3 consecutive failed OTP attempts.
- **FR-04.4**: Passwords shall be salted and hashed using Bcrypt before storage.

---

## ⚡ Non-Functional Requirements (NFR)

- **NFR-01 (Performance)**: The 2D plot mapping canvas shall maintain a frame rate of at least 55-60 FPS during drag and zoom operations.
- **NFR-02 (Offline Capability)**: All core reference datasets (crop profiles, companion matrices, planting guides) shall be available offline via Room Database caching.
- **NFR-03 (Usability)**: Interface layout shall be landscape-first, optimized for single-tap actions and minimal form filling.
- **NFR-04 (Security)**: All client-server communications shall use HTTPS (TLS 1.2+); Supabase PostgreSQL tables shall enforce Row Level Security (RLS).
""",

    "03_SYSTEM_ARCHITECTURE.md": """# 03. System Architecture

## 🏛️ Architecture Overview
MapTanim uses a **Client-Server BaaS Architecture** combining a **Native Android Application (Kotlin)** with **Supabase Cloud Backend** and a **Web Admin Dashboard**.

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                          PRESENTATION LAYER (CLIENT)                            │
│  ┌─────────────────────────────────────┐     ┌───────────────────────────────┐  │
│  │   Android Mobile App (Kotlin)       │     │   Web Admin Panel (React/TS)  │  │
│  │   • Jetpack Compose Canvas UI       │     │   • User & Crop Management    │  │
│  │   • ViewModels & UI StateFlow       │     │   • DSS Rule Maintenance      │  │
│  └──────────────────┬──────────────────┘     └───────────────┬───────────────┘  │
└─────────────────────┼────────────────────────────────────────┼──────────────────┘
                      │                                        │
                      │ HTTPS / Supabase SDK & REST API        │
                      ▼                                        ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           SUPABASE BACKEND LAYER (BaaS)                         │
│  ┌────────────────────┐   ┌────────────────────┐   ┌─────────────────────────┐  │
│  │  Supabase Auth     │   │ PostgreSQL DB      │   │ Supabase Storage        │  │
│  │  (Email/SMS OTP)   │   │ (RLS Enabled)      │   │ (Images & Media)        │  │
│  └────────────────────┘   └────────────────────┘   └─────────────────────────┘  │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │  Supabase Edge Functions (Deno/TypeScript API Endpoints & DSS Rules)     │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 Core Data Flow Sequence
1. **Local Farm Editing**: Farmer interacts with the 2D canvas in Jetpack Compose -> State changes update local `StateFlow` -> Auto-saved to Room SQLite DB.
2. **Cloud Sync**: WorkManager background task detects network connectivity -> Syncs pending bed/crop changes to Supabase PostgreSQL via HTTPS REST API.
3. **DSS Rule Execution**: Local Rule Engine calculates crop suitability & growth stage immediately; edge functions perform secondary sync validation.
""",

    "04_ANDROID_ARCHITECTURE.md": """# 04. Android Application Architecture

## 📱 Architectural Pattern: MVVM + Clean Architecture
The Android mobile application is written in **Kotlin** and follows Google's recommended **Model-View-ViewModel (MVVM)** architecture integrated with **Clean Architecture** principles.

```
mobile/app/src/main/java/com/maptanim/app/
│
├── ui/                   # PRESENTATION LAYER (Jetpack Compose Screens & Components)
│   ├── screens/          # Home, EditMode, Calendar, KnowledgeBase, Profile, Settings
│   ├── components/       # Reusable UI composables (BedCard, TaskItem, ToolButton)
│   └── theme/            # Color tokens, Typography, Shapes
│
├── viewmodel/            # STATE MANAGEMENT LAYER
│   ├── HomeViewModel.kt  # Holds UI state for Dashboard
│   ├── EditViewModel.kt  # Manages canvas state, selection, undo/redo
│   └── AuthViewModel.kt  # Session & OTP state handling
│
├── domain/               # DOMAIN LAYER (Pure Kotlin Business Logic)
│   ├── model/            # Domain Entities (Farm, Bed, Crop, Task)
│   ├── usecase/          # UseCases (GetCropSuitabilityUseCase, EvaluateCompanionUseCase)
│   └── repository/       # Repository Interfaces
│
└── data/                 # DATA LAYER (Data Sources & Persistence)
    ├── local/            # Room Database, DAOs, TypeConverters
    ├── remote/           # Supabase Kotlin SDK API calls & DTOs
    ├── mapper/           # Entity <-> DTO <-> Domain Mappers
    └── repository/       # Repository Implementations (Single Source of Truth)
```

---

## 🧩 Dependency Injection (Hilt)
All repositories, database instances, and API services are managed using **Google Hilt**:
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    object ProvideFarmRepository : FarmRepository { ... }
}
```
""",

    "05_BACKEND_ARCHITECTURE.md": """# 05. Backend Architecture (Supabase BaaS)

## ☁️ Overview
MapTanim utilizes **Supabase** as its cloud Backend-as-a-Service (BaaS), eliminating custom server maintenance while providing enterprise-grade security and scalability.

---

## 🧱 Key Backend Components

### 1. PostgreSQL Database
- Relational storage for user profiles, farm boundaries, plot beds, crop records, activities, harvest logs, and DSS rules.
- Enforces strict data integrity via Foreign Keys, Triggers, and Constraints.

### 2. Row Level Security (RLS)
Every table enforces Row Level Security to ensure farmers can only access their own farm records:
```sql
CREATE POLICY "Farmers can only read own farms"
ON public.farms FOR SELECT
USING (auth.uid() = farmer_id);
```

### 3. Supabase Auth & OTP Service
- Handles authentication via Email and SMS.
- Generates 6-digit One-Time Passwords (OTP) with 5-minute expiry.
- Manages JWT session tokens with auto-refresh capability.

### 4. Supabase Storage
- Buckets: `crop-images`, `pest-references`, `user-avatars`.
""",

    "06_ADMIN_DASHBOARD.md": """# 06. Web Admin Dashboard Specifications

## 🖥️ Overview
The Admin Dashboard is a web-based administration panel built with **React** and **TypeScript**, allowing system administrators to maintain the platform, manage botanical libraries, update DSS rules, and monitor platform usage.

---

## 📊 Core Features
1. **User Management**: View registered farmers, verify accounts, monitor active logins.
2. **Crop Library Manager**: Add/edit crop profiles, growth cycle durations, and NPK requirements.
3. **DSS Rule Editor**: Maintain companion planting matrices (beneficial vs antagonist pairs) and pest risk calendars.
4. **System Analytics**: View platform statistics (total mapped beds, top cultivated vegetables, region reports).
5. **Feedback & Bug Reports**: Review user-submitted issues and feature requests.
""",

    "07_DATABASE_DESIGN.md": """# 07. Database Design & Entity Relationship Diagram

## 🗄️ PostgreSQL Schema Overview

### 1. `users` Table
```sql
CREATE TABLE public.users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'FARMER',
    created_at TIMESTAMPTZ DEFAULT NOW()
);
```

### 2. `farms` Table
```sql
CREATE TABLE public.farms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    farmer_id UUID REFERENCES public.users(id) ON DELETE CASCADE,
    farm_name VARCHAR(100) NOT NULL,
    location VARCHAR(255),
    soil_type VARCHAR(30) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);
```

### 3. `beds` Table
```sql
CREATE TABLE public.beds (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    farm_id UUID REFERENCES public.farms(id) ON DELETE CASCADE,
    bed_name VARCHAR(50) NOT NULL,
    crop_id UUID,
    soil_type VARCHAR(30) NOT NULL,
    pos_x FLOAT NOT NULL,
    pos_y FLOAT NOT NULL,
    width FLOAT NOT NULL,
    height FLOAT NOT NULL,
    rotation FLOAT DEFAULT 0.0,
    created_at TIMESTAMPTZ DEFAULT NOW()
);
```

### 4. `crops` Table (Static Reference)
```sql
CREATE TABLE public.crops (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL,
    botanical_name VARCHAR(100),
    category VARCHAR(30) NOT NULL, -- BULB, STEM, SHOOT, LEAFY, FLOWER, FRUIT, ROOT, TUBER
    days_to_harvest INT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);
```
""",

    "08_SUPABASE_CONFIGURATION.md": """# 08. Supabase Configuration & Setup Guide

## ⚙️ Project Configuration & Connection Details

- **Project URL**: `https://ojilvcglpzbtpjxguhzj.supabase.co`
- **Publishable Key**: `sb_publishable_fH5qY2HaAg-coV89IxOl2Q_Xf9ySGMU`

---

## ⚙️ Step-by-Step Configuration

### 1. Database Connection & Client Configuration
The native Android app connects using the official Supabase Kotlin SDK (`io.github.jan-tennert.supabase`):
```kotlin
val client = createSupabaseClient(
    supabaseUrl = "https://ojilvcglpzbtpjxguhzj.supabase.co",
    supabaseKey = "sb_publishable_fH5qY2HaAg-coV89IxOl2Q_Xf9ySGMU"
) {
    install(Auth) {
        autoLoadFromStorage = true
        alwaysAutoRefresh = true
    }
    install(Postgrest)
    install(Storage)
}
```

### 2. Database Migrations via CLI
```bash
cd backend/supabase
supabase login
supabase link --project-ref ojilvcglpzbtpjxguhzj
supabase db push
```

### 3. Configure Auth & SMTP
1. In Supabase Dashboard, navigate to **Authentication -> Providers -> Email**.
2. Enable Email Provider & OTP code verification.
3. Configure Custom SMTP (Gmail SMTP: `smtp.gmail.com`, Port `587`, TLS).

### 4. Storage Buckets Setup
Create public buckets: `crop-images`, `user-avatars`, `pest-guides`.
""",

    "09_AUTHENTICATION.md": """# 09. Authentication & Security Specifications

## 🔐 Overview
MapTanim implements multi-factor account verification combining standard credentials with One-Time Password (OTP) verification.

---

## 🔑 Authentication Workflow
1. **User Registration**: User submits email/mobile + password -> Password is salted and hashed using **Bcrypt**.
2. **OTP Generation**: Backend generates a 6-digit numeric OTP code (5-minute expiry).
3. **OTP Delivery**: Delivered via Gmail SMTP (Email) or SMS Gateway API.
4. **Validation & Token Issuance**: After correct OTP entry, Supabase issues JWT access & refresh tokens.
5. **Lockout Policy**: 3 consecutive failed OTP attempts trigger a 15-minute temporary lockout.
""",

    "10_API_DOCUMENTATION.md": """# 10. API Documentation & Edge Functions

## 📡 REST API Endpoints (Supabase PostgREST & Edge Functions)

### 1. Authenticate / Verify OTP
`POST /functions/v1/verify-otp`
- **Request Body**:
  ```json
  { "email": "farmer@example.com", "otp_code": "123456" }
  ```
- **Response** (200 OK):
  ```json
  { "access_token": "eyJhbGci...", "expires_in": 3600 }
  ```

### 2. Get Farm Layout & Beds
`GET /rest/v1/beds?farm_id=eq.{id}`
- **Headers**: `Authorization: Bearer <token>`

### 3. Evaluate DSS Crop Suitability
`POST /functions/v1/evaluate-dss`
- **Request Body**:
  ```json
  { "soil_type": "LOAM", "season": "WET", "crop_name": "Tomato" }
  ```
""",

    "11_NAVIGATION.md": """# 11. Navigation Architecture (Jetpack Compose)

## 🧭 Navigation Routes

```kotlin
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Loading : Screen("loading")
    object Auth : Screen("auth")
    object HomeView : Screen("home_view")
    object HomeEdit : Screen("home_edit")
    object Calendar : Screen("calendar")
    object Library : Screen("library")
    object Profile : Screen("profile")
}
```

---

## 🔄 State & Route Flow
- Launch -> `Splash` -> Check Token -> `Loading` -> Load Room Cache -> `HomeView`
- Tap Edit Button -> Transition to `HomeEdit` (Landscape Editor Canvas)
- Tap Save Changes -> Write to Room DB & Sync -> Transition back to `HomeView`
""",

    "12_UI_UX_GUIDELINES.md": """# 12. UI/UX Guidelines (Landscape-First)

## 🎨 Design Philosophy
MapTanim prioritizes an **Interactive Visual Workspace** over forms.

### Key Principles:
1. **Landscape-First**: Designed specifically for 16:9 smartphones held sideways.
2. **Visual Hierarchy**: Farm canvas occupies 70–80% of screen estate.
3. **Touch Targets**: Minimum touch target size of 48dp for outdoor mobile usability.
4. **Color Coding**: Status badges follow standard conventions (Green = Watered/Compatible, Yellow = Action Due, Red = Pest Alert/Antagonist).
""",

    "13_DESIGN_SYSTEM.md": """# 13. Design System & Tokens

## 🎨 Color Palette

| Token | Hex Code | Purpose |
|-------|----------|---------|
| `PrimaryGreen` | `#2E7D32` | Top Bar, Main Navigation, Save Buttons |
| `DarkSoil` | `#4E342E` | Soil overlay tokens |
| `LoamColor` | `#3E2723` | Loam Soil Selector |
| `ClayColor` | `#8D6E63` | Clay Soil Selector |
| `SandyColor` | `#D7CCC8` | Sandy Soil Selector |
| `AlertRed` | `#D32F2F` | Pest alerts, antagonist warning |
| `ReadyYellow` | `#FBC02D` | Harvest readiness indicator |

---

## 🔤 Typography (Google Fonts: Outfit / Inter)
- `HeadlineLarge`: 24sp Bold
- `TitleMedium`: 16sp SemiBold
- `BodyMedium`: 14sp Regular
- `LabelSmall`: 11sp Medium
""",

    "14_COMPONENT_LIBRARY.md": """# 14. Reusable Component Library

## 🧱 Key Composables

### 1. `BedComponent`
Renders an individual plot bed with crop texture, label ("BED 1 Eggplant"), and status overlay icons.

### 2. `SoilSelectorBar`
Row of 6 pill selectors (Loam, Clay, Sandy, Silty, Peaty, Chalky) for soil painting.

### 3. `TodayTaskCard`
Card displaying task icon, title ("Water Bed 3"), crop type ("Tomato"), and quick completion button.

### 4. `FarmSummaryCard`
Card displaying key statistics (Beds count, Plants count, Ready to Harvest, Active Alerts).
""",

    "15_RENDER_ENGINE.md": """# 15. 2D Plot Rendering Engine Specification

## 🎨 Canvas Rendering Logic
The renderer uses Jetpack Compose `Canvas` to draw isometric farm layouts:

```kotlin
Canvas(modifier = Modifier.fillMaxSize().pointerInput(Unit) { ... }) {
    // 1. Draw Grid Lines (if grid enabled)
    drawGrid(zoomLevel, panOffset)
    
    // 2. Draw Soil Ground Texture
    drawGroundTexture(soilType)
    
    // 3. Draw Bed Polygon Objects
    beds.forEach { bed ->
        drawBedObject(bed, isSelected = (bed.id == selectedBedId))
    }
}
```
""",

    "16_INTERACTIVE_PLOT_MAPPING.md": """# 16. Interactive Plot Mapping Specifications

## 🗺️ Editing Tools & Actions
- **Select / Move**: Tap bed to highlight; drag to reposition.
- **Add Bed**: Tap canvas to insert a new default 2x4m bed object.
- **Paint Soil**: Select soil type token -> tap bed to update soil property.
- **Add Trellis / Fence**: Place support structures onto target beds.
- **Resize / Rotate**: Drag corner handles to scale or rotate bed geometry.
- **Grid Snap**: Automatically aligns bed coordinates to nearest 0.5m grid lines.
""",

    "17_FARM_MANAGEMENT.md": """# 17. Farm Management Specifications

## 🚜 Farm Record Management
- Register multiple farm sites per farmer.
- Assign location (e.g., Murcia, Negros Occidental).
- Define plot boundaries, soil classifications, and crop allocations.
- Maintain historical planting cycles and harvest output records.
""",

    "18_VIEW_MODE.md": """# 18. View Mode / Dashboard Specifications

## 👁️ View Mode Workspace (PNG 2 Layout)
- **Top Bar**: Farm Name ("Murcia Farm"), Edit Mode Toggle button, Weather Widget (28°C), Notification Bell.
- **Left Panel**:
  - `TODAY'S TASKS`: Interactive list of daily care items.
  - `FARM SUMMARY`: 4 counter cards (Beds: 12, Plants: 186, Ready Harvest: 4, Active Alerts: 2).
- **Right Toolbar**: Floating buttons for `+ Add`, `Search`, `Center Camera`, `Layers`.
- **Canvas Overlays**: Visual status badges over active plot beds.
""",

    "19_EDIT_MODE.md": """# 19. Edit Mode Specifications

## ✏️ Edit Mode Workspace (PNG 1 Layout)
- **Left Toolbar**: Edit Tools (`Select/Move`, `Add Bed`, `Paint Soil`, `Add Trellis`, `Add Fence`, `Delete`) and `SOIL TYPE` selector palette.
- **Right Toolbar**: `Undo`, `Redo`, `Grid Switch`, `Snap Switch`, `Zoom Level`.
- **Bottom Bar**: Contextual selection details ("1 bed selected: Bed 3 • Tomato"), actions (`Duplicate`, `Resize`, `Change Crop`, `Change Soil`, `Delete`), `SAVE CHANGES` button.
""",

    "20_DECISION_SUPPORT_SYSTEM.md": """# 20. Decision Support System (DSS) Rule Engine

## 🧠 Deterministic Rule Logic

### 1. Soil Suitability Formula
```
SuitabilityScore = (SoilCompatibility * 0.5) + (SeasonCompatibility * 0.5)
```

### 2. Companion Planting Matrix Example
| Primary Crop | Beneficial Companion | Antagonist (Avoid) |
|--------------|----------------------|--------------------|
| **Tomato** | Basil, Carrot, Lettuce | Eggplant, Corn, Potato |
| **Eggplant** | String Beans, Pepper | Tomato, Fennel |
| **Cucumber** | Corn, Radish, Beans | Potato, Aromatic Herbs |
""",

    "21_KNOWLEDGE_BASE.md": """# 21. Agricultural Knowledge Base

## 📚 Reference Catalog
Pre-loaded reference data stored in Room DB & Supabase:
- **13 Crop Profiles**: Botanical details, growth stage timing, optimal pH, NPK needs.
- **6 Soil Profiles**: Description, drainage rating, nutrient retention capacity.
- **Pest & Disease Catalog**: Common Philippine crop pests (e.g., Fruit Borer, Leaf Miner) with biological and chemical intervention options.
""",

    "22_CALENDAR.md": """# 22. Calendar & Task Management

## 📅 Monthly Planting Calendar
- Displays active planting cycles on a monthly calendar grid.
- Auto-generates milestone dates: Land Prep -> Planting Date -> Fertilizer Dates -> Harvest Countdown.
- Weather-adjusted reminders update dynamically based on rainfall and temperature data.
""",

    "23_NOTIFICATION_SYSTEM.md": """# 23. Notification System Specifications

## 🔔 Alarm & Push Notifications
- **Local Alarms**: Scheduled via Android `AlarmManager` for daily task reminders (e.g., 7:00 AM Watering).
- **Remote Push**: Firebase Cloud Messaging (FCM) for pest warnings and weather advisories.
""",

    "24_OFFLINE_SYNCHRONIZATION.md": """# 24. Offline Synchronization Strategy

## 🔄 Offline-First Sync Pipeline

```
User Action ──► Write to Room Local DB ──► Queue Pending Sync Request ──► Network Restored ──► Push to Supabase PostgreSQL
```
- Local changes persist immediately to SQLite via Room.
- Android `WorkManager` monitors network connectivity and syncs queued mutations via HTTPS.
""",

    "25_SECURITY.md": """# 25. Security & OWASP Compliance

## 🛡️ Security Implementation
- **OWASP MASVS Compliance**: Client-side inputs sanitized before API dispatch.
- **Storage Encryption**: Sensitive tokens stored in Android `EncryptedSharedPreferences` backed by Keystore.
- **Database RLS**: All Supabase PostgreSQL tables strictly enforce Row Level Security.
- **Password Security**: Passwords hashed with salted Bcrypt algorithm.
""",

    "26_TESTING.md": """# 26. Testing Strategy & Quality Assurance

## 🧪 Testing Suite Overview

### 1. Unit Tests (`mobile/app/src/test/`)
- Tests DSS Rule Engine logic, soil matching functions, companion matrix evaluation.

### 2. UI Tests (`mobile/app/src/androidTest/`)
- Compose UI test for Edit Mode canvas tools, bed placement, and toolbar interaction.

### 3. Integration Tests (`tests/integration/`)
- Verifies Room DB sync with Supabase REST API endpoints.
""",

    "27_DEPLOYMENT.md": """# 27. Deployment & Release Pipeline

## 🚀 Release Guide

### 1. Build Signed Android App Bundle (AAB)
```bash
cd mobile
./gradlew bundleRelease
```
Output path: `mobile/app/build/outputs/bundle/release/app-release.aab`.

### 2. Google Play Store Release
1. Sign in to **Google Play Console**.
2. Create Release in Production track.
3. Upload `app-release.aab`.
4. Complete Data Safety Declarations & Store Listing screenshots.

### 3. Supabase Production Deployment
```bash
cd backend/supabase
supabase db push --linked
```
""",

    "28_PROJECT_STRUCTURE.md": """# 28. Project Directory Structure & Taxonomy

## 📂 Taxonomy Overview
- `mobile/`: Android application source code and Gradle build configs.
- `backend/`: Supabase database migrations, SQL schemas, Edge Functions.
- `admin/`: Web admin panel (React + TypeScript).
- `shared/`: Shared data transfer objects (DTOs) and constants.
- `docs/`: Complete 34-chapter documentation suite.
- `diagrams/`: UML diagrams, ERDs, and architecture charts.
- `assets/`: UI graphics, textures, branding icons.
- `scripts/`: Automation scripts for build and backup.
""",

    "29_CODING_STANDARDS.md": """# 29. Coding Standards & Guidelines

## 📝 Kotlin & Jetpack Compose Conventions
- **Naming**: `CamelCase` for Composables and Classes, `camelCase` for functions and variables.
- **State Invalidation**: Use `remember` and `derivedStateOf` to prevent redundant Compose recomposition.
- **Null Safety**: Avoid non-null assertion `!!`; use safe calls `?.` and Elvis operator `?:`.
""",

    "30_GIT_WORKFLOW.md": """# 30. Git Workflow & Branching Strategy

## 🌿 Branch Structure
- `main`: Production-ready code.
- `develop`: Main integration branch.
- `feature/<feature-name>`: Feature development branches.
- `bugfix/<bug-name>`: Bug fix branches.

### Commit Conventions:
- `feat: Add companion planting overlay to edit mode`
- `fix: Resolve Room DB type converter crash on sync`
- `docs: Update SRS specification chapter`
""",

    "31_CONTRIBUTING.md": """# 31. Contributor Guidelines

## 🤝 How to Contribute
1. Fork the repository and create a feature branch from `develop`.
2. Follow coding standards detailed in `docs/29_CODING_STANDARDS.md`.
3. Ensure all unit tests pass: `./gradlew test`.
4. Submit a Pull Request targeting `develop` with a clear description of changes.
""",

    "32_CHANGELOG.md": """# 32. Project Changelog

## [1.0.0] - 2026-07-24
### Added
- Complete initial architecture setup for Native Android Kotlin application.
- Jetpack Compose landscape 2D plot mapping canvas (Edit Mode & View Mode).
- Deterministic Agroecological DSS Rule Engine covering 8 plant-part vegetable classifications and 6 soil types.
- Supabase cloud backend integration with Room database offline caching.
- 34 comprehensive documentation chapters in `docs/`.
""",

    "33_ROADMAP.md": """# 33. Project Future Roadmap

## 🛣️ Development Phases

### Phase 1 (Completed - Capstone 2026)
- Core 2D interactive plot mapping engine.
- 13 high-value vegetable profiles across 8 plant-part categories.
- Deterministic DSS engine & offline sync to Supabase.

### Phase 2 (Future Expansion)
- Expand crop library to 30+ Philippine regional vegetable varieties.
- Integrate Bluetooth soil pH & moisture sensor connectivity.
- Multi-language UI support (Tagalog, Ilocano, Cebuano, Hiligaynon).
"""
}

def main():
    os.makedirs(docs_dir, exist_ok=True)
    for filename, content in docs_content.items():
        filepath = os.path.join(docs_dir, filename)
        with open(filepath, "w", encoding="utf-8") as f:
            f.write(content.strip() + "\n")
        print(f"Updated {filename} ({len(content.encode('utf-8'))} bytes)")

if __name__ == "__main__":
    main()
