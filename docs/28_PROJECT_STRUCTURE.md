# 28. Project Structure

## 📌 Overview
Full annotated directory tree for the MapTanim monorepo.

---

## 🗂️ Root Structure

```
MapTanim/
├── .github/
│   ├── ISSUE_TEMPLATE/
│   │   ├── bug_report.md
│   │   └── feature_request.md
│   ├── workflows/
│   │   ├── build.yml                    # CI/CD — Build + test on push
│   │   └── deploy.yml                   # CD — Release AAB to Play Console
│   ├── PULL_REQUEST_TEMPLATE.md
│   └── CODEOWNERS
│
├── admin/                               # Web Admin Dashboard (React + TypeScript)
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   └── services/
│   ├── package.json
│   └── vite.config.ts
│
├── assets/                              # Brand and design assets
│   ├── icons/
│   ├── images/
│   ├── logos/
│   └── screenshots/                     # View Mode + Edit Mode PNG screenshots
│
├── backend/                             # Supabase BaaS config + shared Kotlin data layer
│   ├── src/main/java/com/maptanim/backend/
│   │   ├── data/
│   │   │   ├── api/
│   │   │   │   └── AppInitializationController.kt  # Initial Supabase → Room data load
│   │   │   ├── local/                   # (stub — Room entities live in mobile/)
│   │   │   ├── model/
│   │   │   │   └── Profile.kt           # Profile data model
│   │   │   ├── remote/
│   │   │   │   ├── SupabaseClient.kt    # Live Supabase client (real credentials)
│   │   │   │   ├── api/                 # API controller interfaces
│   │   │   │   └── dto/                 # Data Transfer Objects (DTO → Entity mapping)
│   │   │   └── repository/
│   │   │       ├── AuthRepository.kt    # OTP auth, session management
│   │   │       └── ProfileRepository.kt # profiles table read/write
│   ├── supabase/
│   │   ├── migrations/                  # Sequential .sql migration files
│   │   ├── schema/                      # Full schema definition files
│   │   ├── policies/                    # RLS policy SQL files
│   │   ├── triggers/                    # DB trigger SQL files
│   │   ├── seed/                        # Seed data for crops + dss_rules (13 crops)
│   │   └── functions/                   # Edge Function source (Deno/TypeScript)
│   │       ├── verify-otp/
│   │       ├── evaluate-dss/
│   │       └── generate-report/
│   └── build.gradle.kts
│
├── database/
│   ├── migrations/                      # Backup/reference migration files
│   ├── schema/                          # ERD diagrams + schema docs
│   └── seed/                            # CSV/JSON seed data for crops
│
├── diagrams/
│   ├── architecture/                    # System architecture diagrams
│   ├── database/                        # ERD diagrams
│   ├── navigation/                      # Navigation flow diagrams
│   ├── renderer/                        # Canvas rendering layer diagrams
│   ├── ui/                              # UI wireframe diagrams
│   └── workflow/                        # Data flow diagrams
│
├── docs/                                # All 34 documentation markdown files
│   ├── 00_GETTING_STARTED.md
│   ├── 01_PROJECT_OVERVIEW.md
│   ├── 02_SOFTWARE_REQUIREMENTS_SPECIFICATION.md
│   ├── 03_SYSTEM_ARCHITECTURE.md
│   ├── 04_ANDROID_ARCHITECTURE.md
│   ├── 05_BACKEND_ARCHITECTURE.md
│   ├── 06_ADMIN_DASHBOARD.md
│   ├── 07_DATABASE_DESIGN.md
│   ├── 08_SUPABASE_CONFIGURATION.md
│   ├── 09_AUTHENTICATION.md
│   ├── 10_API_DOCUMENTATION.md
│   ├── 11_NAVIGATION.md
│   ├── 12_UI_UX_GUIDELINES.md
│   ├── 13_DESIGN_SYSTEM.md
│   ├── 14_COMPONENT_LIBRARY.md
│   ├── 15_RENDER_ENGINE.md
│   ├── 16_INTERACTIVE_PLOT_MAPPING.md
│   ├── 17_FARM_MANAGEMENT.md
│   ├── 18_VIEW_MODE.md
│   ├── 19_EDIT_MODE.md
│   ├── 20_DECISION_SUPPORT_SYSTEM.md
│   ├── 21_KNOWLEDGE_BASE.md
│   ├── 22_CALENDAR.md
│   ├── 23_NOTIFICATION_SYSTEM.md
│   ├── 24_OFFLINE_SYNCHRONIZATION.md
│   ├── 25_SECURITY.md
│   ├── 26_TESTING.md
│   ├── 27_DEPLOYMENT.md
│   ├── 28_PROJECT_STRUCTURE.md    ← this file
│   ├── 29_CODING_STANDARDS.md
│   ├── 30_GIT_WORKFLOW.md
│   ├── 31_CONTRIBUTING.md
│   ├── 32_CHANGELOG.md
│   └── 33_ROADMAP.md
│
├── mobile/                              # Android application module
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/maptanim/app/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── core/
│   │   │   │   │   ├── constants/
│   │   │   │   │   ├── datastore/       # EncryptedSharedPreferences wrapper
│   │   │   │   │   ├── extensions/
│   │   │   │   │   ├── helper/
│   │   │   │   │   └── network/         # ConnectivityObserver
│   │   │   │   ├── data/
│   │   │   │   │   ├── datasource/
│   │   │   │   │   ├── local/           # Room DAOs + Entities + Database
│   │   │   │   │   ├── mapper/          # Entity ↔ Domain model mappers
│   │   │   │   │   ├── remote/          # Supabase SupabaseClient reference
│   │   │   │   │   └── repository/      # Concrete repository implementations
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/           # Pure Kotlin domain models (no Android deps)
│   │   │   │   │   ├── repository/      # Repository interfaces
│   │   │   │   │   ├── rules/           # DSS business rules
│   │   │   │   │   └── usecase/         # Use case classes
│   │   │   │   ├── dss/
│   │   │   │   │   ├── engine/          # DssEngine.kt, RuleEvaluator.kt
│   │   │   │   │   ├── companion/       # CompanionPlantsMatrix.kt
│   │   │   │   │   ├── soil/            # SoilSuitabilityScorer.kt
│   │   │   │   │   └── growth/          # GrowthStageCalculator.kt
│   │   │   │   ├── navigation/
│   │   │   │   │   ├── AppNavGraph.kt
│   │   │   │   │   ├── Routes.kt
│   │   │   │   │   └── BottomNavItem.kt
│   │   │   │   ├── renderer/
│   │   │   │   │   ├── canvas/          # FarmCanvasRenderer.kt
│   │   │   │   │   ├── model/           # BedRenderData, CameraState
│   │   │   │   │   ├── gesture/         # CanvasGestureHandler.kt
│   │   │   │   │   └── handle/          # SelectionHandlesRenderer.kt
│   │   │   │   ├── service/
│   │   │   │   │   └── SyncWorker.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── components/      # All reusable Composables (see doc 14)
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── loading/
│   │   │   │   │   │   ├── auth/
│   │   │   │   │   │   ├── home/        # HomeScreen.kt (View Mode)
│   │   │   │   │   │   ├── edit/        # EditScreen.kt / Edit Mode state
│   │   │   │   │   │   ├── farms/
│   │   │   │   │   │   ├── calendar/
│   │   │   │   │   │   ├── library/
│   │   │   │   │   │   └── profile/
│   │   │   │   │   ├── dialogs/
│   │   │   │   │   ├── bottomsheet/
│   │   │   │   │   ├── widget/
│   │   │   │   │   └── theme/
│   │   │   │   ├── viewmodel/
│   │   │   │   │   ├── AuthViewModel.kt
│   │   │   │   │   ├── LoadingViewModel.kt
│   │   │   │   │   └── ProfileViewModel.kt
│   │   │   │   └── worker/
│   │   │   └── res/
│   │   ├── src/test/                    # Unit tests
│   │   ├── src/androidTest/             # Compose UI tests
│   │   └── build.gradle.kts
│   ├── core/
│   ├── data/
│   ├── domain/
│   ├── dss/
│   ├── navigation/
│   ├── renderer/
│   ├── service/
│   ├── ui/
│   ├── viewmodel/
│   └── worker/
│
├── scripts/
│   ├── generate_docs.py                 # Documentation generation helper
│   ├── seed_crops.py                    # Seeds crops table from CSV to Supabase
│   └── validate_migrations.sh
│
├── shared/
│   ├── constants/
│   ├── dto/
│   ├── enums/
│   ├── mapper/
│   ├── models/
│   └── validation/
│
├── tests/
│   ├── integration/                     # Room ↔ Supabase sync tests
│   ├── ui/                              # Compose UI tests (also in mobile/androidTest)
│   └── unit/                           # DSS + domain unit tests
│
├── AGENTS.md                            # AI agent guidelines
├── CODE_OF_CONDUCT.md
├── CONTRIBUTING.md
├── README.md                            # Project overview + quick start
├── SECURITY.md
├── build.gradle.kts                     # Root Gradle build
├── gradle/
│   └── libs.versions.toml               # Version catalog
├── local.properties                     # Local secrets (NOT committed)
└── settings.gradle.kts
```

---

## 🔑 Key Source Files

| File | Purpose |
|------|---------|
| [SupabaseClient.kt](../backend/src/main/java/com/maptanim/backend/data/remote/SupabaseClient.kt) | Live Supabase client initialization |
| [AppInitializationController.kt](../backend/src/main/java/com/maptanim/backend/data/api/AppInitializationController.kt) | First-launch Supabase → Room data sync |
| [LoadingViewModel.kt](../mobile/app/src/main/java/com/maptanim/app/viewmodel/LoadingViewModel.kt) | Session check → navigation routing |
| [AuthRepository.kt](../backend/src/main/java/com/maptanim/backend/data/repository/AuthRepository.kt) | OTP send/verify, session management |
| [ProfileRepository.kt](../backend/src/main/java/com/maptanim/backend/data/repository/ProfileRepository.kt) | profiles table CRUD |
| `AppNavGraph.kt` | Full Compose navigation graph |
| `DssEngine.kt` | DSS evaluation engine |
| `FarmCanvasRenderer.kt` | 2D farm canvas rendering |
