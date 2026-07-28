# AGENTS.md – MapTanim Developer & Agent Guidelines

## 📌 Project Overview
**MapTanim** is a Mobile-Based Interactive Farm Management and Agricultural Decision Support System (DSS) for vegetable farmers in the Philippines. Developed as a Capstone Project at STI West Negros University (2026).

### 👥 Authors & Proponents
- Jomarey D. Parreño (Project Manager)
- John Ryan R. Vasquez (System Analyst)
- Jason B. Juanillo (Lead Programmer)
- James M. Cateo (UI/UX & Assistant Programmer)
- Capstone Adviser: Ms. Danica S. Duazo

---

## 🛠️ Technology Stack
- **Mobile Application**: Native Android built using **Kotlin** & **Jetpack Compose**.
- **Architecture**: **MVVM + Clean Architecture** (Presentation, Domain, Data layers).
- **Offline Persistence**: **Room Database** (Offline-first architecture).
- **Backend Services**: **Supabase** (PostgreSQL Database, Supabase Auth, Storage, Edge Functions, Realtime, Row Level Security).
- **Admin Dashboard**: Web-based administration panel built with React + TypeScript.
- **Decision Support System (DSS)**: Deterministic agroecological rule engine built in Kotlin / Supabase Edge Functions.

---

## 🌿 Core Business Logic & Domain Specifications
1. **8 Plant-Part Vegetable Classifications (DA / PSA Standard)**:
   - *Bulb*: Onion, Garlic
   - *Stem*: Celery, Asparagus
   - *Shoot*: Labong (Bamboo Shoots), Bean Sprouts
   - *Leafy*: Pechay, Kangkong, Mustasa, Alugbati, Malunggay, Pako, Lettuce
   - *Flower*: Broccoli, Cauliflower, Katuray, Banana Blossom
   - *Fruit*: Tomato, Eggplant, Ampalaya, Squash, Okra, Cucumber, Pepper, Corn
   - *Root*: Carrot, Radish, Singkamas, Ginger, Turmeric
   - *Tuber*: Potato, Sweet Potato, Cassava, Gabi, Ube
2. **13 High-Value Target Vegetables**: Tomato, Eggplant, Bell Pepper, Cabbage, Onion, Carrot, String Beans, Lettuce, Cucumber, Okra, Corn, Squash, Kangkong.
3. **6 Soil Classifications**: Loam, Clay, Sandy, Silty, Peaty, Chalky.
4. **3 Seasonal Windows**: Dry Season, Wet Season, Year-Round.
5. **Growth Stages**: Germination -> Seedling -> Vegetative -> Flowering -> Ripening/Maturity -> Harvest.

---

## 📱 UI / UX Design Standards (Landscape-First)
- **Primary Layout**: Landscape orientation optimized for 16:9 smartphones and Android tablets.
- **Main Workspace**: Interactive 2D isometric farm canvas occupying 70–80% of screen.
- **Edit Mode**: Left edit tools (Select/Move, Add Bed, Paint Soil, Add Trellis, Add Fence, Delete), Soil Type selector, right floating toolbar (Undo, Redo, Grid, Snap, Zoom), bottom contextual item bar (Duplicate, Resize, Change Crop, Change Soil, Save Changes).
- **View Mode / Dashboard**: Interactive farm canvas with bed status overlays (Water, Fertilize, Harvest, Pest Alert), Today's Tasks panel, Farm Summary cards (Beds, Plants, Ready to Harvest, Active Alerts), right action toolbar (+ Add, Search, Center, Layers).

---

## 📂 Repository Structure
- `mobile/` – Native Android Kotlin application (`app/` module).
- `backend/` – Supabase database migrations, schemas, Edge Functions, RLS policies, seed files.
- `admin/` – Web-based administration panel for user management, crop library, DSS rules, reports.
- `shared/` – Shared DTOs, data models, validation logic, and constants.
- `docs/` – Complete documentation suite (34 numbered chapters).
- `diagrams/` – Architecture, UML, ERD, and workflow diagrams.
- `assets/` – Visual branding, crop textures, icons, and UI resources.

---

## 🔒 Security & Code Standards
- Enforce strict null safety in Kotlin.
- Enforce Supabase Row Level Security (RLS) on all PostgreSQL tables.
- Authentication uses Supabase Auth + Email (Gmail SMTP) / SMS OTP with 5-minute expiry and lockout after 3 failed attempts.
- Passwords MUST be hashed with Bcrypt before storage.
- Never commit API keys or sensitive credentials; use environment variables or local Gradle properties.

---

## 🚫 CRITICAL RULE: No Static, Mock, or Hardcoded Data

**This is the highest-priority rule for all developers and AI agents working on this codebase.**

### What is FORBIDDEN
- Hardcoded task lists in ViewModels (e.g., `listOf(FarmTask(...), FarmTask(...))`)
- Static farm summary stats (e.g., `FarmSummary(totalBeds = 12, totalPlants = 186)`)
- Hardcoded bed positions, sizes, or crop names in any ViewModel or Repository
- `FakeRepository`, `StubRepository`, or any test double that bypasses the Room/Supabase data layer in production builds
- Dummy/sample data returned from Repository methods in production code paths
- Hardcoded notification badge counts (e.g., hardcoding `notificationCount = 3`)
- Any data rendered to the user that did not originate from Supabase (via PostgREST, Edge Functions, or Realtime) with Room as the local cache

### What is ALLOWED
- `@Preview`-only Composables that use local `PreviewData` objects — BUT these MUST NOT be referenced from any ViewModel or Repository
- In-memory Room databases in unit/integration tests (real schema, no fake DAOs)
- `supabase start` local container for integration tests (real PostgreSQL + real RLS)
- Seed data scripts (`scripts/seed_crops.py`) that populate the live Supabase `crops` and `dss_rules` tables

### Required Data Flow (MUST be followed)
```
Supabase (live data)
  → Room SQLite (offline cache via SyncWorker)
  → Repository (interface + implementation)
  → UseCase (domain logic)
  → ViewModel (StateFlow<UiState>)
  → Compose UI (collectAsStateWithLifecycle)
```

### Live Supabase Project
- URL: `https://ojilvcglpzbtpjxguhzj.supabase.co`
- Anon Key: `sb_publishable_fH5qY2HaAg-coV89IxOl2Q_Xf9ySGMU`
- GitHub: `https://github.com/Jcateo187/maptanim.git`

All features must work against this live project. No alternative "test project" or local-only data flow is acceptable in submitted code.
