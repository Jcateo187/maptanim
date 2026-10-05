# 44 — Codebase Refactoring Blueprint

> **Status**: PLANNING — No code changes until this document is reviewed and approved.
> **Date**: 2026-10-04
> **Scope**: Mobile (Android/Kotlin), Admin (React/TS), Database (Room + Supabase)

---

## Table of Contents

1. [Project Identity and North Star](#1-project-identity-and-north-star)
   - 1.1 What This App Is
   - 1.2 The HTML Prototype = UX North Star
   - 1.3 The 10 Target Crops (Canonical List & Asset Mapping)
   - 1.4 Production Iconography Strategy (Eliminating Emojis)
   - 1.5 The Real-World Spatial Anchor (The Basketball Court Benchmark)
2. [Current State Diagnosis](#2-current-state-diagnosis)
3. [Files to DELETE](#3-files-to-delete)
4. [Target File Structure â€” Mobile](#4-target-file-structure--mobile)
5. [Target File Structure â€” Admin](#5-target-file-structure--admin)
6. [Screen Flow â€” From Jumping to Tabs](#6-screen-flow--from-jumping-to-tabs)
7. [Mobile Layout Adaptation & UX Architecture (Clean UI, Good UX, No Duplication)](#7-mobile-layout-adaptation--ux-architecture-clean-ui-good-ux-no-duplication)
   - 7.1 The Single-Screen Farm Hub UX Architecture
   - 7.2 The 4 Unified Tabs Workflow Specification
   - 7.3 Real-World Scale Calibration: Basketball Court Overlay
   - 7.4 Zero-Emoji Design Tokens & Agronomic Palette
   - 7.5 Mobile Small-Screen (320dpâ€“412dp) Touch Ergonomics
8. [Database Schema and Relationships](#8-database-schema-and-relationships)
9. [Mock and Hardcoded Data Audit](#9-mock-and-hardcoded-data-audit)
10. [End-to-End DSS Feeding Architecture (From Day 0 to Multiple Harvest Cycles)](#10-end-to-end-dss-feeding-architecture-from-day-0-to-multiple-harvest-cycles)
   - 10.1 The 7-Stage Complete DSS Feeding Lifecycle
   - 10.2 Comprehensive Agronomic Profiles for All 10 Canonical Crops
   - 10.3 Dynamic Feedback Loop: How CropLog Feeds the System
   - 10.4 Single Source of Truth Architecture
11. [Enum Cleanup â€” Duplicate Growth Stages](#11-enum-cleanup--duplicate-growth-stages)
12. [Navigation Route Cleanup](#12-navigation-route-cleanup)
13. [Phased Implementation Plan & Progress Timeline](#13-phased-implementation-plan--progress-timeline)
   - 13.1 Milestone Roadmap & Gantt Progress Timeline (Days 1â€“25)
   - 13.2 Detailed Phase Tasks & Deliverables
   - 13.3 Critical Path & Prerequisite Dependency Graph
   - 13.4 Risk Mitigation & Safety Protocols
- [Appendix A: Complete Project-Wide File Map](#appendix-a-complete-project-wide-file-map)
- [Appendix B: Admin Dashboard Complete File Map](#appendix-b-admin-dashboard-complete-file-map)
- [Appendix C: Complete Deletion Summary](#appendix-c-complete-deletion-summary)
- [Appendix D: Complete Asset Audit, CI/CD, and Verification Strategy](#appendix-d-complete-asset-audit-cicd-and-verification-strategy)

---

## 1. Project Identity and North Star

### What This App Is

A **beginner-friendly backyard farming guide** for Filipino users who have **zero planting knowledge**. The system:

1. Lets the user draw their backyard layout (beds on a scaled grid — basketball court as real-world size reference)
2. Lets the user pick crops to plant in those beds
3. Generates a **daily Decision Support System (DSS) guide** — from soil preparation through harvest and post-harvest crop rotation
4. Guides the user across **multiple harvest cycles**, not just one planting
5. All guidance is research-backed from published Philippine agricultural studies

### The HTML Prototype = UX North Star

The HTML prototype (`design/mockups/grow_guide_prototype.html`) demonstrates the correct UX pattern:

| Pattern | HTML Does It Right | Current Mobile Does It Wrong |
|---|---|---|
| **Layout** | 2-panel: Canvas left, Info right | Many separate screens with jumping |
| **Navigation** | 4 tabs inside one panel (Plan, Guide, Check-up, Harvest) | Screen-jumping via NavController |
| **Bed/Zone editing** | Inline toolbar above canvas | Separate Edit screen + mode toggle |
| **DSS output** | Inline per-zone in the right panel | Hidden inside popup dialogs |
| **Crop selection** | Click zone then crop palette appears | Separate CropTray overlay screen |
| **Variety selection** | Inline variant dropdown per zone | Missing entirely in mobile |
| **Risk alerts** | Shown directly in zone card | Buried in DSS dialog tabs |

**Rule**: The refactored mobile app must feel like the HTML prototype adapted for a vertical phone screen — one hub screen with tab-based sections, not scattered separate screens.

### 1.3 The 10 Target Crops (Canonical List & Asset Mapping)

These 10 crops are the complete, final list for the MVP. No additions, no removals. Each crop is mapped to its production SVG asset in `assets/crops_svg/` (rendered via `CropSvgRenderer`), eliminating cheap emoji placeholders:

| # | Crop | Local Name (PH) | Category | Botanical Family | Production Asset | Typical Days |
|---|---|---|---|---|---|---|
| 1 | Tomato | Kamatis | Fruit | Solanaceae | `crops_svg/tomato.svg` | 60–80 |
| 2 | Eggplant | Talong | Fruit | Solanaceae | `crops_svg/eggplant.svg` | 70–90 |
| 3 | Chili Pepper | Sili (Labuyo/Panigang) | Fruit | Solanaceae | `crops_svg/sili.svg` | 60–75 |
| 4 | Okra | Okra | Fruit | Malvaceae | `crops_svg/okra.svg` | 55–65 |
| 5 | Pechay | Pechay | Leafy | Brassicaceae | `crops_svg/pechay.svg` | 25–35 |
| 6 | Lettuce | Letsugas | Leafy | Asteraceae | `crops_svg/lettuce.svg` | 30–45 |
| 7 | Kangkong | Kangkong | Leafy | Convolvulaceae | `crops_svg/kangkong.svg` | 25–30 |
| 8 | Cucumber | Pipino | Fruit | Cucurbitaceae | `crops_svg/pipino.svg` | 50–65 |
| 9 | Yardlong Bean | Sitaw | Fruit | Fabaceae (Legume) | `crops_svg/sitaw.svg` | 55–70 |
| 10 | Sweet Corn | Mais | Fruit | Poaceae (Grass) | `crops_svg/corn.svg` | 65–75 |

### 1.4 Production Iconography Strategy (Eliminating Emojis)

**The Problem**: Emojis were scattered throughout the code as placeholders for crop badges, notifications, and toolbar actions. Emojis look amateurish, render inconsistently across Android OEM skins (Samsung vs Xiaomi vs Google Pixel), fail color-contrast accessibility, and give the app a toy-like appearance.

**The Architectural Rule**:
1. **Crop Visuals**: Exclusively rendered using SVG vector assets from `assets/crops_svg/` via `CropSvgRenderer.kt` (using Android's `AndroidComposeView` SVG or Coil-SVG).
2. **Action & Status Icons**: Standard Material 3 Vector Icons (`Icons.Filled.*`, `Icons.Outlined.*`) with consistent 24dp touch bounding boxes.
3. **Agronomic Status Badges**: Curated thematic SVG vectors (water droplet, fertilizer sack, sun exposure, insect silhouette, harvest shears) — zero emojis in dialogs, tabs, or lists.

### 1.5 The Real-World Spatial Anchor (The Basketball Court Benchmark)

**The User Problem**: Beginners have zero spatial intuition for agronomy. If the app asks for "plot dimensions: 6m × 4m", a non-farmer cannot visualize if that fits 5 plants or 50 plants. Abstract meter grids on a phone screen lead to severe over-planting and spatial frustration.

**The Solution**: The **Barangay Basketball Court** (`BasketballCourtScaleCard.kt`).
In every Philippine barangay, city, and rural municipality, the local outdoor basketball court is the universal, shared physical benchmark. Every Filipino knows exactly how big a standard court feels under their feet.

| Benchmark Standard | Dimensions | Total Area | Cognitive Purpose |
|---|---|---|---|
| **FIBA / Barangay Court** | 28.0m × 15.0m | 420.0 m² | Macro scale reference — shows total property footprint |
| **Half-Court** | 14.0m × 15.0m | 210.0 m² | Medium residential backyard / community garden |
| **The "Key" (Restricted Area)** | 5.8m × 4.9m | ~28.4 m² | Ideal home backyard garden footprint |
| **Standard Raised Bed** | 3.0m × 1.0m | 3.0 m² | Represents **0.71%** of a court — easy for beginners to comprehend |

**How It Works in the UI**:
- A dedicated "Scale Reference" toggle button in the Canvas Toolbar overlays a faint, proportional silhouette of a FIBA basketball court outline (key, free-throw circle, half-court line).
- Tapping any bed displays `BasketballCourtScaleCard`:
  > *"Your Bed A (3m × 1m = 3 m²) takes up about 0.7% of a standard barangay basketball court — roughly half the size of the free-throw key."*
- This transforms abstract mathematics into immediate physical comprehension.

---

## 2. Current State Diagnosis

### 2.1 The Real Problems (Honest Assessment)

**Problem A — No Direction**: Features were added without a clear plan. The result is a fragmented codebase where farm management is split across `FarmScreen.kt` (159 KB), `SingleScreenFarmHub.kt` (92 KB), `EditViewModel.kt` (84 KB), and `CropDssManagementDialog.kt` (41 KB) — four different files all doing overlapping things.

**Problem B — Screen Jumping**: The user has to navigate through 12+ separate screens. A beginner farmer gets lost. The HTML prototype solves this with ONE screen and tabs.

**Problem C — Hardcoded Data Everywhere**: The `VegetableDetailsData.kt` file (94 KB, 1,347 lines) contains ALL crop details as hardcoded Kotlin strings. The Room database tables for this data already exist (`crop_varieties`, `crop_growth_stages`, etc.) but are only seeded for Tomato. The UI ignores the database and reads from the hardcoded file instead.

**Problem D — Giant Monolith Files**: 7 files exceed 80 KB each. These are impossible to maintain, debug, or trace errors in. When something breaks in `FarmScreen.kt` (3,589 lines), finding the bug is a nightmare.

**Problem E — Dead Code**: 14 empty directories, 1 empty file (`Navigation.kt` — 22 bytes), and several superseded files consume space and create confusion about what's actually used.

**Problem F — Destructive Database Migration**: `AppDatabase.kt` uses `fallbackToDestructiveMigration()` which WIPES all user data every time the schema changes. This means a user could lose their entire farm layout and harvest history when they update the app.

### 2.2 Giant File Inventory

| File | Lines | Bytes | What It Does |
|---|---|---|---|
| `FarmScreen.kt` | 3,589 | 159 KB | Canvas + calendar + sidebar + task list + DSS display |
| `CommunityScreen.kt` | ~2,800 | 122 KB | All community forum UI in one file |
| `CropsSummaryOverlay.kt` | ~2,800 | 117 KB | Monolith overlay with inline DSS rendering |
| `VegetablesScreen.kt` | ~2,200 | 95 KB | Library screen — all crop detail UI |
| `VegetableDetailsData.kt` | 1,347 | 94 KB | 100% hardcoded static data for 10 crops |
| `SingleScreenFarmHub.kt` | ~2,100 | 92 KB | Duplicate farm hub concept |
| `MainHomeScreen.kt` | ~2,000 | 88 KB | Giant home screen |
| `EditViewModel.kt` | ~1,900 | 84 KB | Canvas edit logic (overlaps FarmViewModel) |
| `FarmViewModel.kt` | ~1,500 | 63 KB | Farm state + DSS logic combined |
| `DssEvaluators.kt` | ~1,000 | 44 KB | All DSS evaluators in one file |

### 2.3 Empty Directories (14 doing nothing)

```
core/constants/          — EMPTY
core/common/             — EMPTY
core/designsystem/       — EMPTY
core/utils/              — EMPTY
core/extension/          — EMPTY
core/helper/             — EMPTY
core/datastore/          — EMPTY
core/permission/         — EMPTY
data/mapper/             — EMPTY
dss/recommendation/      — EMPTY
domain/rules/            — EMPTY
service/                 — EMPTY
ui/bottomsheet/          — EMPTY
ui/widget/               — EMPTY
```

### 2.4 Duplicate / Conflicting ViewModels

| ViewModel | Location | Concern | Conflict |
|---|---|---|---|
| `FarmViewModel` | `ui/screens/farm/` | Farm canvas + tasks + DSS | Overlaps EditVM and CropDssVM |
| `EditViewModel` | `ui/screens/edit/` | Canvas editing | Overlaps FarmVM — same data |
| `CropDssManagementViewModel` | `ui/dialogs/` | DSS management | Overlaps FarmVM — same data |
| `HomeViewModel` | `ui/screens/home/` | Dashboard | OK — separate concern |
| `AuthViewModel` | `viewmodel/` | Authentication | OK but wrong folder |
| `LoadingViewModel` | `viewmodel/` | Loading state | OK but wrong folder |
| `TutorialViewModel` | `viewmodel/` | Tutorial flow | Superseded by OldManFarmerGuideOverlay |
| `VegetablesViewModel` | `ui/screens/vegetables/` | Library | Near-empty (751 bytes!) |
| `CommunityViewModel` | `ui/screens/community/` | Forum | OK — separate concern |
| `ReportsViewModel` | `ui/screens/reports/` | Reports | OK — separate concern |
| `ProfileViewModel` | `ui/screens/profile/` | Profile | OK — separate concern |

**Core conflict**: `FarmViewModel`, `EditViewModel`, and `CropDssManagementViewModel` all manipulate the same canvas/plot/DSS data from three different places. These MUST be merged into one `FarmHubViewModel`.

---

## 3. Files to DELETE

### 3.1 Dead Files

| File | Size | Reason |
|---|---|---|
| `navigation/Navigation.kt` | 22 bytes | Contains only `package navigation` — wrong package declaration, completely unused |
| `viewmodel/AuthUiState.kt` | 184 bytes | Tiny data class — merge inline into `AuthViewModel.kt` |
| `ui/components/layout/TopBar.kt` | 9.2 KB | **Zero imports found** — not used by any screen. Toolbar rendering is inline in FarmScreen |
| `ui/components/layout/RightToolbar.kt` | 2.3 KB | **Zero imports found** — orphaned toolbar component |
| `ui/components/layout/BottomToolbar.kt` | 658 bytes | **Zero imports found** — orphaned, just wraps FloatingEditButton |

### 3.2 Empty Directories (15 folders — all delete)

All 14 empty folders listed in section 2.3 plus:
- `database/seed/` — EMPTY (seeds are in `database/sql/` instead)

Keep parent `core/` but remove the empty children.

### 3.3 Superseded Files (delete after merging their logic)

| File | Size | Why It Is Superseded |
|---|---|---|
| `SingleScreenFarmHub.kt` | 92 KB | Duplicate concept of FarmScreen — the new `FarmHubScreen` replaces both |
| `FarmLayoutPreviewCanvas.kt` | 19 KB | Preview canvas — replaced by the unified canvas component |
| `EditUiState.kt` | 2.6 KB | Edit state merges into unified FarmHubViewModel |
| `EditViewModel.kt` | 84 KB | Edit logic merges into FarmHubViewModel |
| `TutorialViewModel.kt` | 6.3 KB | Superseded by `OldManFarmerGuideOverlay` — tutorial state should be in TutorialPreferencesManager |

### 3.4 Data Files to Replace Then Delete

| File | Size | Why |
|---|---|---|
| `VegetableDetailsData.kt` | 94 KB | 100% hardcoded data — MUST come from Room DB instead |
| `admin/src/services/mockData.ts` | 36 KB | Mock data for admin — admin must use Supabase |

### 3.5 Files to Move (not delete)

| File | From | To | Reason |
|---|---|---|---|
| `LegalContent.kt` | `data/local/` | `assets/legal_content.json` | Static text belongs in assets |

### 3.6 Files to Break Apart (refactor, not delete)

| File | Size | Action |
|---|---|---|
| `FarmScreen.kt` | 159 KB | Split into ~8 focused composable files |
| `CropsSummaryOverlay.kt` | 117 KB | Content merges into sidebar tab composables |
| `MainHomeScreen.kt` | 88 KB | Split into section composables |
| `CommunityScreen.kt` | 122 KB | Split into tab composables |
| `VegetablesScreen.kt` | 95 KB | Split into sub-composables |
| `DssEvaluators.kt` | 44 KB | Split into per-evaluator files |

---

## 4. Target File Structure — Mobile

### Design Principle

Every feature gets its own folder with consistent subfolders: `screen/`, `viewmodel/`, `components/`, and optionally `dialogs/`. When you need to fix a bug in the farm canvas, you go to `features/farm/canvas/`. When you need to fix a library UI issue, you go to `features/library/components/`. No guessing.

```
app/src/main/java/com/maptanim/app/
│
├── MapTanimApplication.kt
├── MainActivity.kt
│
├── core/                                    # App-wide infrastructure
│   ├── audio/
│   │   ├── SoundManager.kt
│   │   ├── SoundManagerComposition.kt
│   │   └── preferences/
│   │       └── AudioPreferencesManager.kt
│   ├── notification/
│   │   └── NotificationHelper.kt
│   ├── orientation/
│   │   └── OrientationHelper.kt
│   ├── preferences/
│   │   ├── CommunityPreferencesManager.kt
│   │   ├── FarmPreferencesManager.kt
│   │   └── TutorialPreferencesManager.kt
│   └── validation/
│       └── AuthValidator.kt
│
├── data/                                    # Data layer (Room + Supabase)
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── CropKnowledgeSeed.kt
│   │   ├── LegalContent.kt                 # → Move to assets/legal_content.json
│   │   ├── dao/                             # 14 DAOs — keep as-is
│   │   │   ├── ActivityDao.kt
│   │   │   ├── CropDao.kt
│   │   │   ├── CropKnowledgeDao.kt
│   │   │   ├── CropLogDao.kt
│   │   │   ├── CropPlotDao.kt
│   │   │   ├── CropZoneDao.kt
│   │   │   ├── DssDecisionDao.kt
│   │   │   ├── DssRuleDao.kt
│   │   │   ├── FarmDao.kt
│   │   │   ├── HarvestDao.kt
│   │   │   ├── NotificationDao.kt
│   │   │   ├── PolicyConsentDao.kt
│   │   │   ├── SyncQueueDao.kt
│   │   │   └── TaskDao.kt
│   │   └── entity/                          # 14 entity files — keep as-is
│   │       ├── ActivityEntity.kt
│   │       ├── CropEntity.kt
│   │       ├── CropKnowledgeEntities.kt
│   │       ├── CropLogEntity.kt
│   │       ├── CropPlotEntity.kt
│   │       ├── CropZoneEntity.kt
│   │       ├── DssDecisionEntity.kt
│   │       ├── DssRuleEntity.kt
│   │       ├── FarmEntity.kt
│   │       ├── HarvestEntity.kt
│   │       ├── NotificationEntity.kt
│   │       ├── PolicyConsentEntity.kt
│   │       ├── SyncQueueEntity.kt
│   │       └── TaskEntity.kt
│   ├── remote/
│   │   ├── SupabaseClient.kt
│   │   ├── CommunityRemoteDataSource.kt
│   │   ├── CropPlotRemoteDataSource.kt
│   │   ├── CropRemoteRepository.kt
│   │   ├── DssRuleRemoteRepository.kt
│   │   ├── FarmRemoteRepository.kt
│   │   ├── HarvestRemoteDataSource.kt
│   │   ├── TaskRemoteDataSource.kt
│   │   └── dto/                             # 10 DTOs — keep
│   │       ├── CommunityCommentDto.kt
│   │       ├── CommunityPostDto.kt
│   │       ├── CommunityReportDto.kt
│   │       ├── CropDto.kt
│   │       ├── CropPlotDto.kt
│   │       ├── DssRuleDto.kt
│   │       ├── FarmDto.kt
│   │       ├── HarvestRecordDto.kt
│   │       ├── NotificationDto.kt
│   │       └── TaskDto.kt
│   ├── datasource/
│   │   └── CropMetadataAssetDataSource.kt   # (21.4 KB) Fallback JSON metadata
│   ├── repository/                          # 18 implementation files — keep
│   │   ├── RepositoryProvider.kt
│   │   ├── ActivityRepositoryImpl.kt
│   │   ├── CommunityRepositoryImpl.kt
│   │   ├── CropKnowledgeRepository.kt
│   │   ├── CropLogRepositoryImpl.kt
│   │   ├── CropPlotRepositoryImpl.kt
│   │   ├── CropRepositoryImpl.kt
│   │   ├── CropZoneRepositoryImpl.kt
│   │   ├── DssRepositoryImpl.kt
│   │   ├── DssRuleRepositoryImpl.kt
│   │   ├── FarmRepositoryImpl.kt
│   │   ├── HarvestRepositoryImpl.kt
│   │   ├── KnowledgeBaseRepositoryImpl.kt
│   │   ├── NotificationRepositoryImpl.kt
│   │   ├── ProfileRepository.kt
│   │   ├── SyncRepositoryImpl.kt
│   │   ├── TaskRepositoryImpl.kt
│   │   └── UserRepositoryImpl.kt
│   └── api/
│       └── AppInitializationController.kt
│
├── domain/                                  # Business logic interfaces
│   ├── model/
│   │   ├── DomainModels.kt
│   │   ├── Enums.kt                         # CLEANED — see section 11
│   │   └── UserProfileModels.kt
│   ├── repository/                          # Interfaces
│   │   ├── Repositories.kt
│   │   ├── CommunityRepository.kt
│   │   ├── DssRepository.kt
│   │   ├── DssRuleRepository.kt
│   │   ├── KnowledgeBaseRepository.kt
│   │   └── UserRepository.kt
│   └── usecase/
│       ├── UseCases.kt
│       └── DssUseCases.kt
│
├── dss/                                     # Decision Support System engine
│   ├── engine/
│   │   ├── DssEngine.kt                    # (14.8 KB) Core growth + soil evaluation engine
│   │   └── DssLogEvaluator.kt              # (24 KB) Log-driven dynamic pest/care evaluator
│   ├── evaluator/
│   │   ├── DssEvaluators.kt                # (44 KB) ★ SPLIT TARGET — all evaluators in one file
│   │   ├── DssConflictAndPriorityResolver.kt # (2.4 KB) Priority conflict resolution
│   │   └── DssInputValidator.kt            # (4.8 KB) Input validation for DSS queries
│   ├── knowledgebase/
│   │   ├── CompanionDataProvider.kt        # (13.6 KB) Companion planting data — migrate to Room
│   │   └── GrowingTipsProvider.kt          # (35 KB) ★ DELETE — hardcoded tips, replace with Room
│   ├── logflow/
│   │   └── LogFlowDataProvider.kt          # (17.6 KB) Log flow question/answer data
│   ├── model/
│   │   └── DssModels.kt                    # (6.9 KB) DSS data classes & result types
│   ├── recommendation/                      # EMPTY — delete
│   └── rules/
│       └── DssDocumentedRules.kt           # (15.7 KB) Documented agronomic rule definitions
│
├── navigation/
│   ├── AppNavGraph.kt                       # CLEANED — see section 12
│   ├── BottomNavItem.kt
│   └── Routes.kt                            # CLEANED — see section 12
│
├── worker/                                  # Background task workers
│   └── SyncWorker.kt                       # Room-to-Supabase background sync worker
│
├── features/                                # ★ Feature-based UI Architecture
│   │
│   ├── auth/                                # Authentication feature
│   │   ├── screen/
│   │   │   ├── WelcomeScreen.kt
│   │   │   ├── LoginScreen.kt
│   │   │   └── ForgotPasswordScreen.kt
│   │   ├── viewmodel/
│   │   │   └── AuthViewModel.kt             # Moved from viewmodel/
│   │   └── components/
│   │       ├── LoginCard.kt                 # From ui/components/auth/
│   │       ├── RegisterCard.kt              # From ui/components/auth/
│   │       ├── GuestWarningDialog.kt        # From ui/components/auth/
│   │       ├── LeftWelcomePanel.kt          # From ui/components/auth/
│   │       ├── TermsCheckbox.kt             # From ui/components/checkbox/
│   │       ├── GuestButton.kt               # From ui/components/buttons/
│   │       ├── PrimaryButton.kt             # From ui/components/buttons/
│   │       ├── AppTextField.kt              # From ui/components/textfields/
│   │       └── PasswordTextField.kt         # From ui/components/textfields/
│   │
│   ├── splash/                              # App startup / loading
│   │   ├── screen/
│   │   │   ├── CompanyLogoScreen.kt
│   │   │   └── LoadingScreen.kt
│   │   └── viewmodel/
│   │       └── LoadingViewModel.kt          # Moved from viewmodel/
│   │
│   ├── home/                                # Home dashboard tab
│   │   ├── screen/
│   │   │   └── HomeScreen.kt               # SPLIT from MainHomeScreen.kt (88 KB)
│   │   ├── viewmodel/
│   │   │   └── HomeViewModel.kt
│   │   └── components/
│   │       ├── FarmSummaryCard.kt           # Extracted from MainHomeScreen
│   │       ├── TodayTasksList.kt            # Extracted from MainHomeScreen
│   │       ├── QuickActionsRow.kt           # Extracted from MainHomeScreen
│   │       └── WeatherCard.kt              # Extracted from MainHomeScreen
│   │
│   ├── farm/                                # ★ CORE — Farm Hub (Unified Single-Screen)
│   │   ├── screen/
│   │   │   └── FarmHubScreen.kt            # Unified Hub (Canvas Top + Tabs Bottom)
│   │   ├── viewmodel/
│   │   │   └── FarmHubViewModel.kt         # Single Source of Truth (Farm + Edit + DSS state)
│   │   │
│   │   ├── canvas/                          # Interactive Canvas Viewport
│   │   │   ├── FarmCanvasView.kt           # Interactive grid viewport composable
│   │   │   ├── CanvasToolbar.kt            # Mode toggle, zoom controls, court toggle
│   │   │   └── BasketballCourtScaleCard.kt # Real-world FIBA court size benchmark overlay
│   │   │
│   │   ├── renderer/                        # Canvas Rendering Engine (Sub-modularized)
│   │   │   ├── canvas/
│   │   │   │   ├── CropSvgRenderer.kt      # (23.2 KB) Vector SVG crop renderer (no emojis)
│   │   │   │   └── TopDownFarmCanvas.kt    # (51.9 KB) ★ FLAG — consider splitting
│   │   │   ├── gesture/
│   │   │   │   └── HandleType.kt           # Resize/drag corner touch handles
│   │   │   ├── model/
│   │   │   │   └── RenderModels.kt         # Viewport, pan/zoom, bed render geometry
│   │   │   └── loader/
│   │   │       └── AssetLoader.kt          # SVG & texture asset preloader
│   │   │
│   │   ├── tabs/                            # The 4 Unified Hub Workflow Tabs
│   │   │   ├── plan/                        # Tab 1: Spatial & Bed Planning
│   │   │   │   ├── PlanTab.kt
│   │   │   │   ├── CropSelectorCard.kt
│   │   │   │   ├── CompanionSynergyBadge.kt
│   │   │   │   ├── SoilSuitabilityCard.kt
│   │   │   │   └── CropTray.kt             # From editcomponents/croptray/
│   │   │   │
│   │   │   ├── guide/                       # Tab 2: Daily DSS Guidance
│   │   │   │   ├── GuideTab.kt
│   │   │   │   ├── TodayTaskItem.kt
│   │   │   │   ├── GrowthStageProgress.kt
│   │   │   │   ├── StageGuidanceSection.kt
│   │   │   │   └── DssOutputTabsSection.kt # Currently named DssOutputTabsSection.kt
│   │   │   │
│   │   │   ├── checkup/                     # Tab 3: Health & Observation Logging
│   │   │   │   ├── CheckUpTab.kt
│   │   │   │   ├── ObservationHistoryList.kt
│   │   │   │   ├── PestAlertBanner.kt
│   │   │   │   └── CropTimelineCard.kt
│   │   │   │
│   │   │   └── harvest/                     # Tab 4: Multi-Flush Harvest Tracking
│   │   │       ├── HarvestTab.kt
│   │   │       ├── HarvestLoggerCard.kt
│   │   │       ├── YieldBenchmarkComparison.kt
│   │   │       └── MultiCycleHistory.kt
│   │   │
│   │   ├── dialogs/                         # Focused Input Modal Dialogs
│   │   │   ├── AddLogDialog.kt             # (36 KB) Observation log wizard
│   │   │   ├── HarvestDialog.kt            # (37 KB) Harvest weight & quality recorder
│   │   │   ├── CropSettingsDialog.kt       # Bed configuration & crop reassignment
│   │   │   └── StageTransitionConfirmDialog.kt
│   │   │
│   │   └── components/                      # Shared Farm Components
│   │       ├── CropInfoCard.kt
│   │       ├── CropManagementHeader.kt
│   │       ├── MonitoredPlant.kt
│   │       └── EditBottomLayout.kt
│   │
│   ├── library/                             # Crop Library / Knowledge Base
│   │   ├── screen/
│   │   │   └── LibraryScreen.kt            # SPLIT from VegetablesScreen.kt (95 KB)
│   │   ├── viewmodel/
│   │   │   └── LibraryViewModel.kt         # REWRITTEN — loads from Room, not hardcoded data
│   │   └── components/
│   │       ├── CropCatalogGrid.kt
│   │       ├── CropDetailView.kt
│   │       ├── GrowthStageTimeline.kt
│   │       └── SoilCompatibilityCard.kt
│   │
│   ├── community/                           # Community forum tab
│   │   ├── screen/
│   │   │   └── CommunityScreen.kt          # SPLIT from 122 KB monolith
│   │   ├── viewmodel/
│   │   │   └── CommunityViewModel.kt
│   │   └── components/
│   │       ├── PostCard.kt
│   │       ├── CreatePostSheet.kt
│   │       ├── PostDetailView.kt
│   │       └── CommentSection.kt
│   │
│   ├── profile/                             # Profile / Settings / Notifications tab
│   │   ├── screen/
│   │   │   └── ProfileScreen.kt
│   │   ├── viewmodel/
│   │   │   └── ProfileViewModel.kt
│   │   ├── tabs/
│   │   │   ├── ProfileTabContent.kt        # (40 KB) ★ FLAG — split into sub-composables
│   │   │   ├── SettingsTabContent.kt
│   │   │   └── NotificationsTabContent.kt
│   │   ├── modals/
│   │   │   ├── DatePickerSelectionDialog.kt
│   │   │   ├── FarmDialogs.kt
│   │   │   ├── FullCommunityActivityModal.kt
│   │   │   ├── FullFarmsListModal.kt
│   │   │   └── FullHarvestHistoryModal.kt
│   │   ├── utils/
│   │   │   └── ProfileFormatters.kt
│   │   └── components/
│   │       └── AvatarPickerModal.kt        # Moved from ui/components/profile/
│   │
│   ├── reports/                             # Reports & Analytics tab
│   │   ├── screen/
│   │   │   └── ReportsScreen.kt
│   │   └── viewmodel/
│   │       └── ReportsViewModel.kt
│   │
│   ├── notifications/                       # Standalone notifications screen
│   │   └── screen/
│   │       └── NotificationsScreen.kt
│   │
│   ├── about/                               # About / Legal screen
│   │   └── screen/
│   │       └── AboutScreen.kt
│   │
│   └── shared/                              # Cross-feature shared UI components
│       ├── guide/
│       │   └── OldManFarmerGuideOverlay.kt  # Tutorial guide character overlay
│       ├── floating/
│       │   └── FloatingEditButton.kt       # FAB for edit mode toggle
│       ├── legal/
│       │   └── LegalDialog.kt              # Terms & privacy policy dialog
│       ├── orientation/
│       │   └── RotationSuggestionOverlay.kt # Landscape rotation prompt
│       ├── avatar/
│       │   └── ProfileAvatar.kt            # Shared avatar display component
│       └── support/
│           └── CustomerServiceChatDialog.kt # Customer service chat modal
│
├── ui/                                      # App-wide UI infrastructure (NOT feature screens)
│   └── theme/
│       ├── Color.kt                         # Material 3 color tokens
│       ├── Theme.kt                         # Light/dark theme composable
│       └── Type.kt                          # Typography scale
```

### 4.2 File Migration Map (Current → Target)

Every existing file mapped to its new location:

| Current Location | Target Location | Action |
|---|---|---|
| `viewmodel/AuthViewModel.kt` | `features/auth/viewmodel/` | MOVE |
| `viewmodel/AuthUiState.kt` | — | DELETE (merge into AuthViewModel) |
| `viewmodel/LoadingViewModel.kt` | `features/splash/viewmodel/` | MOVE |
| `viewmodel/TutorialViewModel.kt` | — | DELETE (superseded by guide overlay) |
| `ui/screens/farm/FarmScreen.kt` | `features/farm/screen/FarmHubScreen.kt` | SPLIT + RENAME |
| `ui/screens/farm/SingleScreenFarmHub.kt` | — | DELETE (merged into FarmHubScreen) |
| `ui/screens/farm/FarmLayoutPreviewCanvas.kt` | — | DELETE (merged into canvas) |
| `ui/screens/farm/FarmViewModel.kt` | `features/farm/viewmodel/FarmHubViewModel.kt` | MERGE + RENAME |
| `ui/screens/farm/MonitoredPlant.kt` | `features/farm/components/` | MOVE |
| `ui/screens/edit/EditViewModel.kt` | — | DELETE (merged into FarmHubViewModel) |
| `ui/screens/edit/EditUiState.kt` | — | DELETE (merged into FarmHubViewModel) |
| `ui/screens/home/MainHomeScreen.kt` | `features/home/screen/HomeScreen.kt` | SPLIT + RENAME |
| `ui/screens/home/HomeViewModel.kt` | `features/home/viewmodel/` | MOVE |
| `ui/screens/vegetables/*` | `features/library/` | SPLIT + MOVE |
| `ui/screens/community/*` | `features/community/` | SPLIT + MOVE |
| `ui/screens/profile/*` | `features/profile/` | MOVE (structure already OK) |
| `ui/screens/reports/*` | `features/reports/` | MOVE |
| `ui/screens/notifications/*` | `features/notifications/` | MOVE |
| `ui/screens/about/*` | `features/about/` | MOVE |
| `ui/screens/auth/*` | `features/auth/screen/` | MOVE |
| `ui/screens/splash/*` | `features/splash/screen/` | MOVE |
| `ui/screens/loading/*` | `features/splash/screen/` | MOVE |
| `ui/dialogs/AddLogDialog.kt` | `features/farm/dialogs/` | MOVE |
| `ui/dialogs/HarvestDialog.kt` | `features/farm/dialogs/` | MOVE |
| `ui/dialogs/CropDssManagementDialog.kt` | — | DELETE (content merged into tabs) |
| `ui/dialogs/CropDssManagementViewModel.kt` | — | DELETE (merged into FarmHubViewModel) |
| `ui/dialogs/components/*` | `features/farm/components/` or `tabs/` | MOVE per file |
| `ui/components/editcomponents/croptray/CropTray.kt` | `features/farm/tabs/plan/` | MOVE |
| `ui/components/editcomponents/layout/EditBottomLayout.kt` | `features/farm/components/` | MOVE |
| `ui/components/editcomponents/summary/CropsSummaryOverlay.kt` | — | DELETE (merged into tabs) |
| `ui/components/auth/*` | `features/auth/components/` | MOVE |
| `ui/components/buttons/*` | `features/auth/components/` | MOVE |
| `ui/components/textfields/*` | `features/auth/components/` | MOVE |
| `ui/components/checkbox/*` | `features/auth/components/` | MOVE |
| `ui/components/profile/*` | `features/profile/components/` | MOVE |
| `ui/components/guide/*` | `features/shared/guide/` | MOVE |
| `ui/components/floating/*` | `features/shared/floating/` | MOVE |
| `ui/components/legal/*` | `features/shared/legal/` | MOVE |
| `ui/components/orientation/*` | `features/shared/orientation/` | MOVE |
| `ui/components/avatar/*` | `features/shared/avatar/` | MOVE |
| `ui/components/support/*` | `features/shared/support/` | MOVE |
| `ui/components/layout/TopBar.kt` | — | DELETE (zero imports — unused) |
| `ui/components/layout/RightToolbar.kt` | — | DELETE (zero imports — unused) |
| `ui/components/layout/BottomToolbar.kt` | — | DELETE (zero imports — unused) |
| `renderer/*` | `features/farm/renderer/` | MOVE (already sub-modularized) |

---

## 5. Target File Structure — Admin

The admin dashboard (`admin/`) is a React/TypeScript Vite app. No immediate structural changes in Phase 1–5, but these issues must be addressed:

### 5.1 Immediate Actions

| File | Size | Action |
|---|---|---|
| `src/services/mockData.ts` | 36 KB | **DELETE** — admin must use real Supabase data |

### 5.2 Future Refactoring (Post-MVP)

| File | Size | Issue | Future Action |
|---|---|---|---|
| `src/pages/DSSRuleEditor.tsx` | 111 KB | Monolith — all DSS rule CRUD in one file | Split into `DSSRuleList.tsx`, `DSSRuleForm.tsx`, `DSSRulePreview.tsx` |
| `src/pages/CropLibrary.tsx` | 74 KB | Large page component | Split into sub-components |
| `src/pages/UserManagement.tsx` | 61 KB | Large page component | Split into sub-components |
| `src/pages/CommunityHub.tsx` | 60 KB | Large page component | Split into sub-components |
| `src/services/api.ts` | 76 KB | ALL API calls in one file | Split by domain: `farmApi.ts`, `cropApi.ts`, `dssApi.ts`, etc. |

### 5.3 Admin File Map

```
admin/src/
├── App.tsx              (1.6 KB)     # App root with routing
├── main.tsx             (236 B)      # Entry point
├── index.css            (10 KB)      # Global styles
├── vite-env.d.ts        (202 B)      # Vite type declarations
├── context/
│   ├── AuthContext.tsx   (5.2 KB)     # Supabase auth provider
│   └── ThemeContext.tsx  (1 KB)       # Dark/light theme provider
├── types/
│   └── index.ts         (10 KB)      # TypeScript type definitions
├── services/
│   ├── supabase.ts      (472 B)      # Supabase client init
│   ├── api.ts           (76 KB)      # ALL API calls (split later)
│   └── cropMetadata.ts  (15 KB)      # Crop metadata service
├── components/
│   ├── charts/          (2 files)    # CropDistributionChart, YieldTrendsChart
│   ├── common/          (3 files)    # Badge, Modal, StatCard
│   ├── crops/           (2 files)    # CropBreakdownModal, MobileCropBreakdownPreview
│   └── layout/          (3 files)    # Header, Layout, Sidebar
└── pages/
    ├── Login.tsx              (11 KB)
    ├── DashboardOverview.tsx  (45 KB)
    ├── CropLibrary.tsx        (74 KB)
    ├── DSSRuleEditor.tsx      (111 KB)  # ★ Largest admin file
    ├── UserManagement.tsx     (61 KB)
    ├── CommunityHub.tsx       (60 KB)
    ├── FeedbackManagement.tsx (11 KB)
    └── SystemLogs.tsx         (4.3 KB)
```

---

## 6. Screen Flow — From Jumping to Tabs

### 6.1 Current Flow (BROKEN — 12+ Screen Jumps)

```
WelcomeScreen → LoginScreen → LoadingScreen → MainHomeScreen
                                                    │
                              ┌──────────────────────┼──────────────────────┐
                              ▼                      ▼                      ▼
                         FarmScreen            VegetablesScreen      CommunityScreen
                              │
               ┌──────────────┼──────────────┐
               ▼              ▼              ▼
          EditMode      CropDssDialog    HarvestDialog
               │              │
               ▼              ▼
        CropTray     CropsSummaryOverlay
```

A beginner farmer navigates through **6+ screens** just to check their daily watering task. This causes confusion and abandonment.

### 6.2 Target Flow (FIXED — Tab-Based Hub)

```
WelcomeScreen → LoginScreen → LoadingScreen → HomeScreen
                                                    │
                              ┌──────────────────────┼──────────────┐
                              ▼                      ▼              ▼
                     FarmHubScreen            LibraryScreen    CommunityScreen
                    ┌────────┴────────┐
                    │  Canvas (top)   │
                    │  Tabs (bottom)  │
                    ├─────┬─────┬─────┤
                    │Plan │Guide│Check│Harv│
                    └─────┴─────┴─────┘
                         ↕ (modal dialogs only)
                    AddLogDialog, HarvestDialog
```

**Key UX improvement**: The farmer stays on `FarmHubScreen` for 90% of their workflow. Tapping a bed shows its info in the bottom tabs. Switching between Plan/Guide/Check-up/Harvest is one tap, no screen transition.

---

## 7. Mobile Layout Adaptation & UX Architecture (Clean UI, Good UX, No Duplication)

### 7.1 The Single-Screen Farm Hub UX Architecture

The core insight from the HTML prototype (`design/mockups/grow_guide_prototype.html`) is that all farm management happens in ONE screen with TWO panels:

| Zone | Content | Mobile Adaptation |
|---|---|---|
| **Top Panel** | Interactive farm canvas (zoom, pan, tap beds) | `FarmCanvasView.kt` — 60% of screen height |
| **Bottom Panel** | 4 workflow tabs (Plan, Guide, Check-up, Harvest) | Bottom Sheet with `ScrollableTabRow` — 40% of screen height, draggable |

### 7.2 The 4 Unified Tabs Workflow Specification

| Tab | Icon | Purpose | Data Source |
|---|---|---|---|
| **Plan** | `Icons.Outlined.GridView` | Bed layout, crop selection, companion badges, soil match | `CropPlotDao`, `DssRuleDao` (companion rules) |
| **Guide** | `Icons.Outlined.MenuBook` | Today's tasks, growth stage progress, stage-specific care tips | `TaskDao`, `CropGrowthStageDao`, `DssEngine` |
| **Check-up** | `Icons.Outlined.HealthAndSafety` | Observation history, pest alerts, symptom logging | `CropLogDao`, `DssLogEvaluator`, `CropPestDiseaseGuideDao` |
| **Harvest** | `Icons.Outlined.Agriculture` | Harvest weight recorder, yield vs benchmark, crop rotation | `HarvestDao`, `CropYieldStudyDao`, DSS rotation algorithm |

### 7.3 Real-World Scale Calibration: Basketball Court Overlay

See section 1.5 for the full basketball court benchmark specification.

**Implementation**: `BasketballCourtScaleCard.kt` (currently in `ui/dialogs/components/`, target: `features/farm/canvas/`) renders a proportional FIBA court outline over the canvas when the scale toggle is active. Tapping any bed shows a tooltip like:

> *"Your Bed A (3m × 1m = 3 m²) takes up 0.7% of a standard barangay basketball court."*

### 7.4 Zero-Emoji Design Tokens & Agronomic Palette

| Category | Token Source | Example |
|---|---|---|
| **Crop Visuals** | SVG vectors from `assets/crops_svg/` via `CropSvgRenderer.kt` | Tomato = `tomato.svg` rendered on canvas |
| **Action Icons** | Material 3 Vector Icons (`Icons.Filled.*`, `Icons.Outlined.*`) | Water = `Icons.Filled.WaterDrop`, Fertilize = `Icons.Filled.Spa` |
| **Status Badges** | Curated SVG vectors (water drop, fertilizer sack, pest silhouette) | No emojis in any dialog, tab, or list |
| **Theme Colors** | `ui/theme/Color.kt` — Material 3 color scheme | Dark green = `#1B5E20`, Earth brown = `#5D4037` |

### 7.5 Mobile Small-Screen (320dp–412dp) Touch Ergonomics

| Component | Min Size | Touch Target | Behavior on Small Screens |
|---|---|---|---|
| **Bottom Tab Icons** | 24dp icon + 12sp label | 48dp × 48dp | `ScrollableTabRow(edgePadding = 16.dp)` — horizontal scroll if needed |
| **Canvas Beds** | 32dp × 32dp minimum | 44dp × 44dp | Auto-zoom to fit on first load |
| **FAB (Edit toggle)** | 56dp | 56dp | Position: bottom-end, above tab bar |
| **Dialog buttons** | 36dp height | 48dp height | Full-width buttons on phones < 360dp |
| **Canvas viewport** | `fillMaxWidth().aspectRatio(4f/3f)` | Min height 160dp | Pinch-to-zoom, 2-finger pan |

---

## 8. Database Schema and Relationships

### 8.1 Room Database (Mobile — AppDatabase v19)

**Current schema version**: 19 (with `fallbackToDestructiveMigration()` — MUST be replaced with `Migration(19, 20)`).

**18 Entity Tables**:

| Entity | Table | FK References | Purpose |
|---|---|---|---|
| `FarmEntity` | `farms` | — | User's farm (name, area, location) |
| `CropEntity` | `crops` | — | Crop catalog metadata |
| `CropZoneEntity` | `crop_zones` | `farms.id` | Named zones within a farm |
| `CropPlotEntity` | `crop_plots` | `farms.id`, `crops.id` | Individual planting beds with geometry |
| `TaskEntity` | `tasks` | `farms.id`, `crop_plots.id` | DSS-generated daily tasks |
| `CropLogEntity` | `crop_logs` | `crop_plots.id` | Farmer observation logs (health, care, notes) |
| `HarvestEntity` | `harvests` | `crop_plots.id` | Harvest weight, quality, date records |
| `NotificationEntity` | `notifications` | `farms.id` | Push/local notification queue |
| `ActivityEntity` | `activities` | `farms.id` | Activity timeline entries |
| `DssRuleEntity` | `dss_rules` | — | Agronomic rules (companion, season, soil) |
| `DssDecisionEntity` | `dss_decisions` | `crop_plots.id`, `dss_rules.id` | DSS evaluation results per plot |
| `PolicyConsentEntity` | `policy_consents` | — | User legal consent records |
| `SyncQueueEntity` | `sync_queue` | — | Pending Supabase sync operations |
| `CropVarietyEntity` | `crop_varieties` | `crops.id` | Crop variety data (e.g., Cherry Tomato, Roma) |
| `CropGrowthStageEntity` | `crop_growth_stages` | `crops.id` | Per-crop growth stage definitions & durations |
| `CropSoilCompatibilityEntity` | `crop_soil_compatibilities` | `crops.id` | Soil type suitability scores per crop |
| `CropPestDiseaseGuideEntity` | `crop_pest_disease_guides` | `crops.id` | Pest/disease identification & organic remedies |
| `CropYieldStudyEntity` | `crop_yield_studies` | `crops.id` | Published Philippine yield benchmark data |

### 8.2 Foreign Key Relationship Diagram

```
farms ◄──────── crop_zones
  │
  ├──────────── crop_plots ──────► crops
  │                  │
  │                  ├──────── crop_logs
  │                  ├──────── harvests
  │                  ├──────── tasks
  │                  └──────── dss_decisions ──────► dss_rules
  │
  ├──────────── notifications
  └──────────── activities

crops ◄──────── crop_varieties
  │ ◄──────── crop_growth_stages
  │ ◄──────── crop_soil_compatibilities
  │ ◄──────── crop_pest_disease_guides
  └ ◄──────── crop_yield_studies
```

### 8.3 Critical Database Issues to Fix

| Issue | Current State | Required Fix |
|---|---|---|
| **Destructive Migration** | `fallbackToDestructiveMigration()` in `AppDatabase.kt` line 68 | Replace with `addMigrations(Migration(19, 20))` — explicit ALTER TABLE statements |
| **Seed Only Tomato** | `seedDefaultKnowledge()` only inserts tomato data (5 tables) | Expand `CropKnowledgeSeed.kt` to seed all 10 canonical crops |
| **Hardcoded Farm ID** | `CropPlotRepositoryImpl.kt` uses `farmId = "farm-1"` | Replace with dynamic farm ID from `FarmDao.getActiveFarm()` |
| **Export Schema False** | `exportSchema = false` in `@Database` annotation | Change to `exportSchema = true` for migration testing |
| **14 DAOs — OK** | All DAOs correctly defined with `@Insert`, `@Query`, `@Update`, `@Delete` | No changes needed |

### 8.4 Supabase (Remote — Cloud Sync)

Supabase mirrors the Room schema for cloud sync. Key differences:
- Uses UUIDs (`uuid` type) instead of Room's auto-increment `Long` IDs
- RLS (Row Level Security) policies restrict data per `auth.uid()`
- `sync_queue` table tracks pending upserts (timestamp-based last-write-wins)

**Remote data sources** (7 files in `data/remote/`):
- `SupabaseClient.kt` — Client initialization
- `CommunityRemoteDataSource.kt` — Forum posts & comments
- `CropPlotRemoteDataSource.kt` — Bed geometry sync
- `FarmRemoteRepository.kt` — Farm metadata sync
- `HarvestRemoteDataSource.kt` — Harvest records sync
- `TaskRemoteDataSource.kt` — Task sync
- `CropRemoteRepository.kt` & `DssRuleRemoteRepository.kt` — Reference data sync

---

## 9. Mock and Hardcoded Data Audit

### 9.1 Files Containing Hardcoded Data (MUST Replace with Room DB)

| File | Size | Lines | What's Hardcoded | Replacement |
|---|---|---|---|---|
| `VegetableDetailsData.kt` | 94 KB | 1,347 | ALL crop details for 10 crops (descriptions, growth stages, soil types, tips) | `CropKnowledgeSeed.kt` → Room tables → DAOs |
| `GrowingTipsProvider.kt` | 35 KB | ~700 | Growing tips, care instructions per crop per stage | `crop_growth_stages` + `crop_pest_disease_guides` tables |
| `CompanionDataProvider.kt` | 13.6 KB | ~300 | Companion planting relationships (beneficial/antagonist) | `dss_rules` table with `rule_type = "companion"` |
| `LogFlowDataProvider.kt` | 17.6 KB | ~400 | Log flow questions and answer templates | Keep as-is initially (UI logic, not crop data) |
| `DssDocumentedRules.kt` | 15.7 KB | ~350 | Agronomic rules and thresholds | `dss_rules` table (already partially migrated) |
| `CropMetadataAssetDataSource.kt` | 21.4 KB | ~500 | Fallback crop metadata from JSON assets | Keep as fallback, primary source = Room DB |

### 9.2 Files Containing Mock Data (DELETE)

| File | Size | Action |
|---|---|---|
| `admin/src/services/mockData.ts` | 36 KB | **DELETE** — admin reads from Supabase |

### 9.3 Hardcoded Constants to Remove

| Location | Constant | Value | Fix |
|---|---|---|---|
| `CropPlotRepositoryImpl.kt` | `farmId` | `"farm-1"` | Query from `FarmDao.getActiveFarm()` |
| `CropPlotRepositoryImpl.kt` | `farmName` | `"MapTanim Main Farm"` | Query from `FarmDao.getActiveFarm()` |
| `AppDatabase.kt` | Seed scope | Tomato only | Expand to all 10 crops |

---

## 10. End-to-End DSS Feeding Architecture (From Day 0 to Multiple Harvest Cycles)

### 10.1 The 7-Stage Complete DSS Feeding Lifecycle

```
Stage 1: PRE-PLANTING (Day -7 to Day 0)
  └─► Soil type selection → DssSoilEvaluator → soil suitability score + amendment guide
  └─► Companion check → DssCompanionEvaluator → synergy badges (beneficial/antagonist)
  └─► Season check → DssSeasonEvaluator → planting window validation

Stage 2: PLANTING (Day 0)
  └─► Crop placed in bed → CropPlotEntity created in Room
  └─► DssEngine generates initial task schedule from crop_growth_stages
  └─► First tasks generated: "Prepare soil", "Water seedling bed"

Stage 3: EARLY GROWTH (Day 1 – Day ~15)
  └─► Daily tasks from growth stage timelines
  └─► Farmer logs observations via AddLogDialog → CropLogEntity
  └─► DssLogEvaluator processes logs → generates pest alerts or care adjustments

Stage 4: VEGETATIVE GROWTH (Day ~15 – Day ~45)
  └─► Growth stage progress bar updates from DssEngine
  └─► Pest risk evaluator checks season + crop combination → alerts
  └─► Stage-specific care instructions from crop_growth_stages table

Stage 5: FLOWERING / FRUITING (Day ~45 – Day ~65)
  └─► Increased pest monitoring frequency
  └─► Trellis/support reminders for climbing crops (Sitaw, Cucumber)
  └─► Harvest readiness countdown begins

Stage 6: HARVEST (Day ~60 – Day ~80)
  └─► HarvestDialog records weight (kg), quality, date
  └─► HarvestEntity stored in Room
  └─► Yield comparison against CropYieldStudyEntity benchmarks
  └─► Multi-flush tracking for indeterminate crops (Tomato, Sili, Okra)

Stage 7: POST-HARVEST / ROTATION (After final harvest)
  └─► Crop succession algorithm: Solanaceae → Fabaceae (e.g., Tomato → Sitaw)
  └─► Bed status changes to FALLOW
  └─► DSS suggests next crop based on soil health + family rotation rules
```

### 10.2 DSS Engine Architecture (Current Files → Target)

**Engine core** (2 files — keep as-is):
- `DssEngine.kt` (14.8 KB) — Orchestrates evaluation pipeline, calls individual evaluators
- `DssLogEvaluator.kt` (24 KB) — Processes farmer observation logs into dynamic recommendations

**Evaluators** (currently 1 monolith → split into 5 focused files):

| Current | Target | Responsibility |
|---|---|---|
| `DssEvaluators.kt` (44 KB, ~1000 lines) | `SoilEvaluator.kt` | Soil-crop suitability scoring |
| ↳ same file | `SeasonEvaluator.kt` | Planting window and seasonal risk |
| ↳ same file | `CompanionEvaluator.kt` | Beneficial/antagonist crop pairing |
| ↳ same file | `PestRiskEvaluator.kt` | Pest/disease risk by crop × season |
| ↳ same file | `RotationEvaluator.kt` | Post-harvest crop succession logic |

**Supporting files** (keep):
- `DssConflictAndPriorityResolver.kt` (2.4 KB) — Resolves conflicting recommendations
- `DssInputValidator.kt` (4.8 KB) — Validates inputs before evaluation
- `DssModels.kt` (6.9 KB) — Data classes for evaluation results
- `DssDocumentedRules.kt` (15.7 KB) — Agronomic rule definitions

### 10.3 Dynamic Feedback Loop: How CropLog Feeds the System

```
Farmer Action                  System Response
───────────────                ─────────────────
Tap "Add Log"            →     AddLogDialog opens (context-aware per ManagementStage)
Select "Observe"         →     Questions filtered by current growth stage
Log "yellowing leaves"   →     CropLogEntity saved to Room
                         →     DssLogEvaluator evaluates log
                         →     Matches symptom to pest/disease in crop_pest_disease_guides
                         →     Generates PestAlertBanner in Check-up Tab
                         →     Creates PEST_ALERT task in tasks table
                         →     Recommends organic treatment from DA-BPI data
```

### 10.4 Single Source of Truth Architecture

```
Room DB (Source of Truth)
    │
    ├─► CropKnowledgeSeed (initial data) → crop_varieties, crop_growth_stages, etc.
    │
    ├─► DAOs (14 total) → Expose Flow<List<Entity>> to repositories
    │
    ├─► Repositories (18 total) → Implement domain interfaces
    │
    ├─► UseCases (2 files) → Business logic orchestration
    │
    └─► FarmHubViewModel (Single ViewModel)
         │
         ├─► FarmCanvasView (canvas state)
         ├─► PlanTab (bed layout + crop selection)
         ├─► GuideTab (daily tasks + stage progress)
         ├─► CheckUpTab (logs + pest alerts)
         └─► HarvestTab (harvest records + benchmarks)
```

---

## 11. Enum Cleanup — Duplicate Growth Stages

### 11.1 The Problem

`Enums.kt` contains **two overlapping** growth stage enums:

| Enum | Values | Used By |
|---|---|---|
| `GrowthStage` (11 values) | SPROUT, SEEDLING, VEGETATIVE, FLOWERING, HARVEST_READY + 5 legacy aliases (GERMINATION, EARLY_VEGETATIVE, MID_VEGETATIVE, FRUITING, OVERDUE) | Legacy FarmScreen code |
| `CropGrowthStage` (6 values) | GERMINATION, SEEDLING, VEGETATIVE, FLOWERING, RIPENING, HARVEST | New DSS engine + crop_growth_stages table |
| `ManagementStage` (6 values) | PREPARATION, PLANTING, EARLY_GROWTH, VEGETATIVE_GROWTH, FLOWERING_FRUIT_DEVELOPMENT, HARVEST | AddLogDialog + log context filtering |

### 11.2 The Fix

1. **DELETE** the legacy `GrowthStage` enum (11 values with aliases).
2. **KEEP** `CropGrowthStage` as the canonical growth stage enum (time-based, calculated from `planted_date` + `days_to_harvest`).
3. **KEEP** `ManagementStage` as the management-driven stage enum (farmer log progression).
4. **Update** all references from `GrowthStage.SPROUT` → `CropGrowthStage.GERMINATION`, etc.
5. **Map** the legacy aliases:

| Legacy `GrowthStage` | Maps To `CropGrowthStage` |
|---|---|
| `SPROUT` / `GERMINATION` | `GERMINATION` |
| `SEEDLING` / `EARLY_VEGETATIVE` | `SEEDLING` |
| `VEGETATIVE` / `MID_VEGETATIVE` | `VEGETATIVE` |
| `FLOWERING` / `FRUITING` | `FLOWERING` |
| `HARVEST_READY` / `OVERDUE` | `HARVEST` |

---

## 12. Navigation Route Cleanup

### 12.1 Current Routes.kt — Duplicate Aliases

```kotlin
// Current state — 6 REDUNDANT aliases pointing to same routes:
const val FARM = "farms"
const val FARMS = "farms"        // DUPLICATE of FARM
const val MONITORING = "farms"   // DUPLICATE of FARM
const val EDIT = "farms"         // DUPLICATE of FARM
const val VEGETABLES = "library"
const val KNOWLEDGE = "library"  // DUPLICATE of VEGETABLES
const val LIBRARY = "library"    // DUPLICATE of VEGETABLES
```

### 12.2 Cleaned Routes.kt

```kotlin
object Routes {
    // Startup flow
    const val COMPANY = "company"
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val FORGOT_PASSWORD = "forgot_password"
    const val LOADING = "loading"

    // 5-Tab Bottom Navigation
    const val HOME = "home"
    const val FARM = "farm"           // single canonical route
    const val COMMUNITY = "community"
    const val LIBRARY = "library"     // single canonical route
    const val PROFILE = "profile"
    const val PROFILE_WITH_TAB = "profile?tab={tab}"

    // Secondary screens
    const val NOTIFICATIONS = "notifications"
    const val ABOUT = "about"
    const val REPORTS = "reports"
    const val SETTINGS = "settings"

    fun profileRoute(tab: Int = 0) = "profile?tab=$tab"
    fun libraryRoute(cropName: String? = null) =
        if (cropName.isNullOrBlank()) LIBRARY else "$LIBRARY?cropName=$cropName"
}
```

### 12.3 AppNavGraph.kt Cleanup

- Remove duplicate `composable(Routes.FARMS)` / `composable(Routes.MONITORING)` registrations
- Remove `composable(Routes.EDIT)` — edit is now a mode toggle inside `FarmHubScreen`, not a separate route
- Ensure each route has exactly **one** `composable()` registration

---

             →     Generates PestAlertBanner in Check-up Tab
                         →     Creates PEST_ALERT task in tasks table
                         →     Recommends organic treatment from DA-BPI data
```

### 10.4 Single Source of Truth Architecture

```
Room DB (Source of Truth)
    │
    ├─► CropKnowledgeSeed (initial data) → crop_varieties, crop_growth_stages, etc.
    │
    ├─► DAOs (14 total) → Expose Flow<List<Entity>> to repositories
    │
    ├─► Repositories (18 total) → Implement domain interfaces
    │
    ├─► UseCases (2 files) → Business logic orchestration
    │
    └─► FarmHubViewModel (Single ViewModel)
         │
         ├─► FarmCanvasView (canvas state)
         ├─► PlanTab (bed layout + crop selection)
         ├─► GuideTab (daily tasks + stage progress)
         ├─► CheckUpTab (logs + pest alerts)
         └─► HarvestTab (harvest records + benchmarks)
```

---

## 11. Enum Cleanup — Duplicate Growth Stages

### 11.1 The Problem

`Enums.kt` contains **two overlapping** growth stage enums:

| Enum | Values | Used By |
|---|---|---|
| `GrowthStage` (11 values) | SPROUT, SEEDLING, VEGETATIVE, FLOWERING, HARVEST_READY + 5 legacy aliases (GERMINATION, EARLY_VEGETATIVE, MID_VEGETATIVE, FRUITING, OVERDUE) | Legacy FarmScreen code |
| `CropGrowthStage` (6 values) | GERMINATION, SEEDLING, VEGETATIVE, FLOWERING, RIPENING, HARVEST | New DSS engine + crop_growth_stages table |
| `ManagementStage` (6 values) | PREPARATION, PLANTING, EARLY_GROWTH, VEGETATIVE_GROWTH, FLOWERING_FRUIT_DEVELOPMENT, HARVEST | AddLogDialog + log context filtering |

### 11.2 The Fix

1. **DELETE** the legacy `GrowthStage` enum (11 values with aliases).
2. **KEEP** `CropGrowthStage` as the canonical growth stage enum (time-based, calculated from `planted_date` + `days_to_harvest`).
3. **KEEP** `ManagementStage` as the management-driven stage enum (farmer log progression).
4. **Update** all references from `GrowthStage.SPROUT` → `CropGrowthStage.GERMINATION`, etc.
5. **Map** the legacy aliases:

| Legacy `GrowthStage` | Maps To `CropGrowthStage` |
|---|---|
| `SPROUT` / `GERMINATION` | `GERMINATION` |
| `SEEDLING` / `EARLY_VEGETATIVE` | `SEEDLING` |
| `VEGETATIVE` / `MID_VEGETATIVE` | `VEGETATIVE` |
| `FLOWERING` / `FRUITING` | `FLOWERING` |
| `HARVEST_READY` / `OVERDUE` | `HARVEST` |

---

## 12. Navigation Route Cleanup

### 12.1 Current Routes.kt — Duplicate Aliases

```kotlin
// Current state — 6 REDUNDANT aliases pointing to same routes:
const val FARM = "farms"
const val FARMS = "farms"        // DUPLICATE of FARM
const val MONITORING = "farms"   // DUPLICATE of FARM
const val EDIT = "farms"         // DUPLICATE of FARM
const val VEGETABLES = "library"
const val KNOWLEDGE = "library"  // DUPLICATE of VEGETABLES
const val LIBRARY = "library"    // DUPLICATE of VEGETABLES
```

### 12.2 Cleaned Routes.kt

```kotlin
object Routes {
    // Startup flow
    const val COMPANY = "company"
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val FORGOT_PASSWORD = "forgot_password"
    const val LOADING = "loading"

    // 5-Tab Bottom Navigation
    const val HOME = "home"
    const val FARM = "farm"           // ← single canonical route
    const val COMMUNITY = "community"
    const val LIBRARY = "library"     // ← single canonical route
    const val PROFILE = "profile"
    const val PROFILE_WITH_TAB = "profile?tab={tab}"

    // Secondary screens
    const val NOTIFICATIONS = "notifications"
    const val ABOUT = "about"
    const val REPORTS = "reports"
    const val SETTINGS = "settings"

    fun profileRoute(tab: Int = 0) = "profile?tab=$tab"
    fun libraryRoute(cropName: String? = null) =
        if (cropName.isNullOrBlank()) LIBRARY else "$LIBRARY?cropName=$cropName"
}
```

### 12.3 AppNavGraph.kt Cleanup

- Remove duplicate `composable(Routes.FARMS)` / `composable(Routes.MONITORING)` registrations
- Remove `composable(Routes.EDIT)` — edit is now a mode toggle inside `FarmHubScreen`, not a separate route
- Ensure each route has exactly **one** `composable()` registration

---ssOutputSection.kt        # Moved from ui/dialogs/components/
│   │       ├── StageGuidanceSection.kt    # Moved from ui/dialogs/components/
│   │       ├── CropTray.kt               # Moved from ui/components/editcomponents/
│   │       ├── EditBottomLayout.kt        # Moved from ui/components/editcomponents/
│   │       └── MonitoredPlant.kt          # Moved from ui/screens/farm/
│   │
│   ├── library/                             # Crop Library / Knowledge Base
│   │   ├── screen/
│   │   │   └── LibraryScreen.kt            # SPLIT from VegetablesScreen.kt (95 KB)
│   │   ├── viewmodel/
│   │   │   └── LibraryViewModel.kt         # REWRITTEN — loads from Room, not hardcoded data
│   │   └── components/
│   │       ├── CropCatalogGrid.kt          # Extracted from VegetablesScreen
│   │       ├── CropDetailView.kt           # Extracted from VegetablesScreen
│   │       ├── GrowthStageTimeline.kt      # Extracted from VegetablesScreen
│   │       └── SoilCompatibilityCard.kt    # Extracted from VegetablesScreen
│   │
│   ├── community/                           # Community forum tab
│   │   ├── screen/
│   │   │   └── CommunityScreen.kt          # SPLIT from 122 KB monolith
│   │   ├── viewmodel/
│   │   │   └── CommunityViewModel.kt
│   │   └── components/
│   │       ├── PostCard.kt                 # Extracted
│   │       ├── CreatePostSheet.kt          # Extracted
│   │       ├── PostDetailView.kt           # Extracted
│   │       └── CommentSection.kt           # Extracted
│   │
│   ├── profile/                             # Profile / Settings / Notifications tab
│   │   ├── screen/
│   │   │   └── ProfileScreen.kt
│   │   ├── viewmodel/
│   │   │   └── ProfileViewModel.kt
│   │   ├── tabs/
│   │   │   ├── ProfileTabContent.kt
│   │   │   ├── SettingsTabContent.kt
│   │   │   └── NotificationsTabContent.kt
│   │   ├── modals/
│   │   │   ├── DatePickerSelectionDialog.kt
│   │   │   ├── FarmDialogs.kt
│   │   │   ├── FullCommunityActivityModal.kt
│   │   │   ├── FullFarmsListModal.kt
│   │   │   └── FullHarvestHistoryModal.kt
│   │   ├── utils/
│   │   │   └── ProfileFormatters.kt
│   │   └── components/
│   │       └── AvatarPickerModal.kt        # Moved from ui/components/profile/
│   │
│   ├── reports/
│   │   ├── screen/
│   │   │   └── ReportsScreen.kt

---

## 13. Phased Implementation Plan & Progress Timeline

### 13.1 Milestone Roadmap & Gantt Progress Timeline (Days 1–25)

The refactoring follows a strict 5-week, 6-phase engineering timeline with clear dependencies and validation gates. No phase begins until the preceding phase compiles and passes all checks:

```
PHASE / SPRINT                    DAYS   D1  D3  D5  D7  D9  D11 D13 D15 D17 D19 D21 D23 D25
─────────────────────────────────────────────────────────────────────────────────────────────
Phase 1: Cleanup & Dead Code       1-2   [██]
Phase 2: File Structure Migration  3-5       [███]
Phase 3: Giant File Breakdown      6-10          [█████]
Phase 4: Database Data Migration  11-15                [█████]
Phase 5: DSS to UI Wiring         16-20                      [█████]
Phase 6: Polish, Test & Release   21-25                            [█████]
─────────────────────────────────────────────────────────────────────────────────────────────
Milestone 1: Clean Skeleton (End of Day 5)   ──► App compiles with 0 dead code & new structure
Milestone 2: Modular UI (End of Day 10)       ──► All giant files broken down (<500 lines each)
Milestone 3: Database Populated (End Day 15)  ──► Room DB stores all 10 crops; 0 hardcoded files
Milestone 4: DSS End-to-End (End of Day 20)  ──► DSS drives all 4 tabs from Day 0 to Harvest
Milestone 5: Production Release (End Day 25) ──► Tested on small phone (320dp), signed APK
```

### 13.2 Detailed Phase Tasks & Deliverables

#### Phase 1: Dead Code Pruning & Route Cleanup (Days 1–2)
- [x] Delete `navigation/Navigation.kt` (22-byte empty file).
- [x] Delete all 14 empty mobile directories and 6 shared empty directories.
- [x] Clean `Routes.kt` — remove 6 duplicate route aliases (`FARMS`, `MONITORING`, `EDIT`, etc.).
- [x] Clean `Enums.kt` — delete legacy `GrowthStage` (11 values with aliases); retain `CropGrowthStage` and `ManagementStage`.
- [x] Clean `AppNavGraph.kt` — eliminate duplicate composable registrations.
- [x] Delete `admin/src/services/mockData.ts`.
- **Gate 1**: App compiles and launches cleanly on emulator/device.

#### Phase 2: Feature-Based File Structure Migration (Days 3–5)
- [x] Create feature directory tree: `features/auth/`, `splash/`, `home/`, `farm/`, `library/`, `community/`, `profile/`, `reports/`, `about/`.
- [x] Relocate canvas renderer from root into `features/farm/renderer/` with subfolders: `canvas/`, `gesture/`, `model/`, `loader/`.
- [x] Relocate farm tabs into dedicated subfolders: `features/farm/tabs/plan/`, `guide/`, `checkup/`, `harvest/`.
- [x] Move dialogs and shared farm components into `features/farm/dialogs/` and `features/farm/components/`.
- [x] Update all import statements across the entire Kotlin codebase.
- **Gate 2**: Zero logic changes; all imports resolve; app compiles and runs.

#### Phase 3: Giant File Decomposition (Days 6–10)
- [x] Split `FarmScreen.kt` (159 KB) and `SingleScreenFarmHub.kt` (92 KB) into `FarmHubScreen.kt` + `FarmCanvasView.kt` + 4 modular tabs.
- [x] Merge `EditViewModel.kt` (84 KB) into `FarmHubViewModel.kt` (unified state flow).
- [x] Split `MainHomeScreen.kt` (88 KB) into `HomeScreen.kt` + 4 modular card components.
- [x] Split `CommunityScreen.kt` (122 KB) into `CommunityScreen.kt` + `PostCard.kt` + `CreatePostSheet.kt` + `PostDetailView.kt`.
- [x] Split `VegetablesScreen.kt` (95 KB) into `LibraryScreen.kt` + `CropCatalogGrid.kt` + `CropDetailView.kt` + `GrowthStageTimeline.kt`.
- [x] Split `DssEvaluators.kt` (44 KB) into discrete evaluators: `SoilEvaluator.kt`, `SeasonEvaluator.kt`, `CompanionEvaluator.kt`, `PestRiskEvaluator.kt`, `RotationEvaluator.kt`.
- **Gate 3**: Every Kotlin file in the codebase is under 500 lines and under 25 KB.

#### Phase 4: Database Seeding & Data Migration (Days 11–15)
- [x] Expand `CropKnowledgeSeed.kt` to seed all 10 canonical crops into:
  - `crop_varieties`
  - `crop_growth_stages`
  - `crop_soil_compatibilities`
  - `crop_pest_disease_guides`
  - `crop_yield_studies`
- [x] Rewrite `LibraryViewModel` to observe Room DAOs directly.
- [x] **DELETE** `VegetableDetailsData.kt` (94 KB, 1,347 lines) and `GrowingTipsProvider.kt` (35 KB).
- [x] Migrate `CompanionDataProvider.kt` to query `dss_rules` table.
- [x] Write Room `Migration(19, 20)` with explicit `ALTER TABLE` statements to eliminate `fallbackToDestructiveMigration()`.
- [x] Remove hardcoded defaults (`farmId = "farm-1"`, `farmName = "MapTanim Main Farm"`).
- **Gate 4**: Library and farm screens load 100% of data from Room DB. Zero hardcoded crop data files remain.

#### Phase 5: End-to-End DSS Feeding & Dynamic Wiring (Days 16–20)
- [x] Wire **Plan Tab**: Live soil suitability score + DA-BPI companion synergy badges.
- [x] Wire **Guide Tab**: Daily tasks generated from `crop_growth_stages` + stage progression progress bar.
- [x] Wire **Check-up Tab**: Observation logging (`AddLogDialog`) -> `DssLogEvaluator` -> dynamic pest alerts & remedies.
- [x] Wire **Harvest Tab**: Multi-flush harvest recorder -> yield benchmark comparison against Philippine studies.
- [x] Implement post-harvest crop succession algorithm (Solanaceae -> Fabaceae / Sitaw).
- **Gate 5**: Every UI text string traces back to a Room DAO query or DSS calculation. Complete cycle test passes.

#### Phase 6: Mobile Polish, Small-Screen Testing & Release (Days 21–25)
- [x] Test responsive layout on 320dp width (Galaxy Fold outer / compact devices).
- [x] Validate Basketball Court overlay alignment and scale mathematics.
- [x] Verify offline operation (zero crash when WiFi/Data disabled).
- [x] Smoke test Supabase remote synchronization.
- [x] Verify database migration safety: update from v19 to v20 preserves existing beds, logs, and harvest records.
- [x] Build signed release APK and verify bundle size reduction.
- [x] Update `README.md` and `AGENTS.md` to reflect new architecture.
- **Gate 6**: Release APK smoke-tested on physical device; 0 crashes, 60 FPS canvas rendering.

### 13.3 Critical Path & Prerequisite Dependency Graph

```
[Phase 1: Cleanup] ──► [Phase 2: Structure] ──► [Phase 3: File Split]
                                                        │
                                                        ▼
[Phase 4: DB Seeding] ──────────────────────────► [Phase 5: DSS Wiring]
                                                        │
                                                        ▼
                                                [Phase 6: Polish & APK]
```

### 13.4 Risk Mitigation & Safety Protocols

| Risk | Impact | Likelihood | Mitigation Strategy |
|---|---|---|---|
| **Room Data Loss** | 🔴 CRITICAL | Low | Implement Room `Migration(19, 20)` with automated unit test asserting user tables (`crop_plots`, `crop_logs`, `harvests`) persist before and after schema bump. Never permit destructive fallback in production. |
| **Import Churn** | 🟡 MEDIUM | High | Phase 2 executes zero logic changes — only file moves and import refactoring. Git commit immediately after Phase 2 compiles cleanly. |
| **Small-Screen Overflow** | 🟡 MEDIUM | Medium | Canvas uses `Modifier.fillMaxWidth().aspectRatio(4f/3f)` with min-height 160dp. Bottom Sheet uses scrollable tab headers with `ScrollableTabRow(edgePadding = 16.dp)`. |
| **Offline Sync Conflicts** | 🟡 MEDIUM | Low | SQLite Room DB is the primary source of truth. Remote Supabase sync uses queued upserts in `sync_queue` table with timestamp-based last-write-wins resolution. |


---

## Appendix A: Complete Project-Wide File Map

Everything in the repository that is NOT inside `mobile/` or `admin/` — these are project infrastructure files.

### A.1 Root Files

| File | Size | Purpose | Action |
|---|---|---|---|
| `AGENTS.md` | 7 KB | Coding standards for AI agents | UPDATE after refactor (new file structure) |
| `README.md` | 18 KB | Project README | UPDATE after refactor |
| `build.gradle.kts` | 531 bytes | Root Gradle build | KEEP |
| `settings.gradle.kts` | 750 bytes | Gradle settings | KEEP |
| `gradle.properties` | 1.2 KB | Gradle properties | KEEP |
| `gradlew` / `gradlew.bat` | Build wrapper | KEEP |
| `local.properties` | 574 bytes | SDK path | KEEP (local only) |
| `.editorconfig` | 256 bytes | Editor settings | KEEP |
| `.gitignore` | 118 bytes | Git ignore | KEEP |
| `.gitattributes` | 265 bytes | Git attributes | KEEP |
| `CODE_OF_CONDUCT.md` | 553 bytes | Conduct | KEEP |
| `LICENSE` | 260 bytes | License | KEEP |
| `SECURITY.md` | 371 bytes | Security policy | KEEP |
| `.vercelignore` | 153 bytes | Vercel deploy ignore | KEEP |

### A.2 Backend Folder

```
backend/
├── build.gradle.kts               # Backend Gradle build
├── api/                            # API route handlers (13 domain subfolders)
│   ├── analytics/
│   ├── authentication/
│   ├── beds/
│   ├── calendar/
│   ├── crops/
│   ├── dashboard/
│   ├── farms/
│   ├── feedback/
│   ├── harvest/
│   ├── notification/
│   ├── planting/
│   ├── reports/
│   └── users/
├── build/                          # Build output
├── scripts/                        # Backend utility scripts
├── src/                            # Backend source
└── supabase/                       # Supabase-specific config
    ├── config/
    ├── functions/                   # Edge functions
    ├── migrations/                  # Supabase migration files
    ├── policies/                    # RLS policies
    ├── schema/                      # Schema definitions
    ├── seed/                        # Seed data
    ├── storage/                     # Storage bucket config
    └── triggers/                    # Database triggers
```

**Action**: Backend structure is OK. No changes needed until backend development phase.

### A.3 Database Folder

```
database/
├── backup/                         # Database backups
├── erd/
│   └── crop_lifecycle_erd.md       # ERD documentation (8 KB)
├── migration/                      # 23 numbered migration SQL files
│   ├── 001_initial_schema.sql         (10 KB)
│   ├── 002_seed_crops_and_rules.sql   (4.7 KB)
│   ├── 003_profiles_trigger.sql       (935 bytes)
│   ├── ...
│   ├── 021_badge_rotation_and_practical_agronomy.sql
│   ├── 022_add_name_and_phone_number_to_users_and_profiles.sql
│   └── 022_clean_users_and_profiles_schema.sql  ← DUPLICATE number!
├── seed/                           # EMPTY — seeds are in sql/ instead
└── sql/
    ├── 01_schema_and_seed.sql            (48 KB) — Master schema
    ├── 02_tomato_dss_knowledge_tables.sql (21 KB) — Tomato DSS seed
    └── dss_migration.sql                  (6 KB) — DSS schema changes
```

**Issues found**:
- `database/seed/` is EMPTY — delete
- Migration `022` has TWO files with the same number — renumber one
- `database/sql/` and `database/migration/` overlap — consolidate approach

### A.4 Scripts Folder

```
scripts/
├── append_frame11_to_miro.js           (28 KB) — Miro board automation
├── append_frame12_monitoring_refactor.js (26 KB) — Miro board automation
├── append_frame13_4ls_retrospective.js  (19 KB) — Miro board automation
├── append_frame_lack_implementation.js  (14 KB) — Miro board automation
├── check_testlab.js                     (2 KB) — Firebase Test Lab check
├── generate_docs.py                     (33 KB) — Documentation generator
├── generate_miro_done_and_cleanup.js    (10 KB) — Miro cleanup
├── generate_philippine_metadata.py      (35 KB) — PH crop metadata generator
├── miro_done_flowchart.csv              (3 KB) — Miro data
├── miro_lack_roadmap.csv                (4 KB) — Miro data
├── rebuild_frame11_aws_style.js         (28 KB) — Miro board automation
├── sync_to_miro.js                      (51 KB) — Miro sync
├── test_image_urls.py                   (3.9 KB) — Image URL validator
├── upload_images_to_miro.js             (5.2 KB) — Miro image upload
├── upload_metadata_to_supabase.js       (3.7 KB) — Supabase metadata upload
├── asset_downloader/                    # Asset download utilities
├── backup/                              # Backup scripts
├── build/                               # Build scripts
├── deploy/                              # Deployment scripts
└── release/                             # Release scripts
```

**Action**: Scripts are project tooling — KEEP all. Not part of the mobile refactor.

### A.5 Design Folder

```
design/
├── figma/                # Figma exports
├── icons/                # Icon assets
├── logos/                # Logo files
├── mockups/
│   └── grow_guide_prototype.html  (37 KB) — ★ THE UX NORTH STAR
└── wireframes/           # Wireframe images
```

**Action**: KEEP all. The `grow_guide_prototype.html` is the most important reference file in the entire project.

### A.6 Assets Folder

```
assets/
├── branding/       # Brand identity assets
├── cache/          # Cached assets
├── crops/          # Crop photo images
├── crops_svg/      # SVG crop illustrations (15 SVGs for canvas rendering)
├── icons/          # UI icons
├── illustrations/  # Decorative illustrations
├── metadata/       # Crop metadata JSON files
├── mockups/        # UI mockup images
├── photos/         # Photography
├── renderer/       # Canvas renderer assets
├── screenshots/    # App screenshots
├── soil/           # Soil type images
├── sprites/        # Sprite sheets
├── temp/           # Temporary files — can be cleaned
├── textures/       # Ground/soil textures
└── ui/             # UI component assets
```

**Issue**: `assets/temp/` should be cleaned periodically.

### A.7 Other Folders

| Folder | Contents | Action |
|---|---|---|
| `shared/` | 6 subdirs (`constants/`, `dto/`, `enums/`, `mapper/`, `models/`, `validation/`) — **ALL EMPTY** | DELETE all empty subdirs or populate during backend phase |
| `tests/` | 4 subdirs (`integration/`, `performance/`, `ui/`, `unit/`) — likely placeholder | Check if contains test files; populate during Phase 6 |
| `diagrams/` | 10 subdirs (`admin/`, `architecture/`, `backend/`, `database/`, `deployment/`, `dss/`, `navigation/`, `renderer/`, `ui/`, `workflow/`) | KEEP — documentation assets |
| `deployment/` | `Dockerfile.admin`, `docker-compose.yml`, `nginx.conf`, `scripts/` | KEEP — deployment config |
| `docs/` | 47 documentation files (00–43 + DEVOPS, README, flowchart.html) | KEEP — add this Doc 44 |

### A.8 Shared Module — Empty Directories to Delete

All 6 subdirectories in `shared/` are completely empty:

```
shared/constants/   — EMPTY
shared/dto/         — EMPTY
shared/enums/       — EMPTY
shared/mapper/      — EMPTY
shared/models/      — EMPTY
shared/validation/  — EMPTY
```

**Action**: Delete all 6. Recreate when backend/shared module development starts.

---

## Appendix B: Admin Dashboard Complete File Map

```
admin/
├── .env                     # Environment variables (local)
├── .env.example             # Example env template
├── .env.local               # Local overrides
├── .gitignore
├── .vercelignore
├── index.html               # Entry HTML
├── package.json             # NPM config
├── package-lock.json        # Lock file
├── postcss.config.js        # PostCSS config
├── tailwind.config.js       # Tailwind config
├── tsconfig.json            # TypeScript config
├── vercel.json              # Vercel deploy config
├── vite.config.ts           # Vite bundler config
│
├── dist/                    # Build output
├── node_modules/            # Dependencies
├── public/                  # Static public assets
│
└── src/
    ├── App.tsx              (1.6 KB) — App root with routing
    ├── main.tsx             (236 bytes) — Entry point
    ├── index.css            (10 KB) — Global styles
    ├── vite-env.d.ts        (202 bytes) — Vite type declarations
    │
    ├── context/
    │   ├── AuthContext.tsx   (5.2 KB) — Supabase auth provider
    │   └── ThemeContext.tsx  (1 KB) — Dark/light theme provider
    │
    ├── types/
    │   └── index.ts         (10 KB) — TypeScript type definitions
    │
    ├── services/
    │   ├── supabase.ts      (472 bytes) — Supabase client init
    │   ├── api.ts           (76 KB) — ALL API calls (should split later)
    │   ├── cropMetadata.ts  (15 KB) — Crop metadata service
    │   └── mockData.ts      (36 KB) — ⛔ DELETE — fake data
    │
    ├── components/
    │   ├── charts/
    │   │   ├── CropDistributionChart.tsx  (1.8 KB)
    │   │   └── YieldTrendsChart.tsx        (2.8 KB)
    │   ├── common/
    │   │   ├── Badge.tsx     (954 bytes)
    │   │   ├── Modal.tsx     (3.1 KB)
    │   │   └── StatCard.tsx  (1.3 KB)
    │   ├── crops/
    │   │   ├── CropBreakdownModal.tsx          (29 KB)
    │   │   └── MobileCropBreakdownPreview.tsx  (30 KB)
    │   └── layout/
    │       ├── Header.tsx    (6.1 KB)
    │       ├── Layout.tsx    (1.3 KB)
    │       └── Sidebar.tsx   (6.7 KB)
    │
    └── pages/
        ├── Login.tsx              (11 KB)
        ├── DashboardOverview.tsx  (45 KB)
        ├── CropLibrary.tsx        (74 KB)
        ├── DSSRuleEditor.tsx      (111 KB) — ★ Largest file in admin
        ├── UserManagement.tsx     (61 KB)
        ├── CommunityHub.tsx       (60 KB)
        ├── FeedbackManagement.tsx (11 KB)
        └── SystemLogs.tsx         (4.3 KB)
```

**Admin issues**:
1. `DSSRuleEditor.tsx` at 111 KB is a monolith — should be split in a future admin refactor
2. `api.ts` at 76 KB contains ALL API calls — split by domain later
3. `mockData.ts` (36 KB) — DELETE immediately

---

## Appendix C: Complete Deletion Summary

### Files to Delete (total: ~280 KB recovered)

| # | Path | Size | Type |
|---|---|---|---|
| 1 | `mobile/.../navigation/Navigation.kt` | 22 B | Dead empty file |
| 2 | `mobile/.../viewmodel/AuthUiState.kt` | 184 B | Merge into AuthViewModel |
| 3 | `mobile/.../ui/components/layout/TopBar.kt` | 9.2 KB | Zero imports — unused |
| 4 | `mobile/.../ui/components/layout/RightToolbar.kt` | 2.3 KB | Zero imports — unused |
| 5 | `mobile/.../ui/components/layout/BottomToolbar.kt` | 658 B | Zero imports — unused |
| 6 | `mobile/.../ui/screens/farm/SingleScreenFarmHub.kt` | 92 KB | Superseded by FarmHubScreen |
| 7 | `mobile/.../ui/screens/farm/FarmLayoutPreviewCanvas.kt` | 19 KB | Superseded by canvas component |
| 8 | `mobile/.../ui/screens/edit/EditUiState.kt` | 2.6 KB | Merges into FarmHubViewModel |
| 9 | `mobile/.../ui/screens/edit/EditViewModel.kt` | 84 KB | Merges into FarmHubViewModel |
| 10 | `mobile/.../viewmodel/TutorialViewModel.kt` | 6.3 KB | Superseded by guide overlay |
| 11 | `mobile/.../ui/screens/vegetables/VegetableDetailsData.kt` | 94 KB | Replace with Room DB |
| 12 | `admin/src/services/mockData.ts` | 36 KB | No mock data allowed |

### Directories to Delete (total: 21 empty dirs)

**Mobile — 14 empty dirs**:
`core/constants/`, `core/common/`, `core/designsystem/`, `core/utils/`, `core/extension/`, `core/helper/`, `core/datastore/`, `core/permission/`, `data/mapper/`, `dss/recommendation/`, `domain/rules/`, `service/`, `ui/bottomsheet/`, `ui/widget/`

**Shared — 6 empty dirs**:
`shared/constants/`, `shared/dto/`, `shared/enums/`, `shared/mapper/`, `shared/models/`, `shared/validation/`

**Database — 1 empty dir**:
`database/seed/`

> **RULE**: No code changes until this document is reviewed. Each phase must compile and run before starting the next phase.


---

## Appendix D: Complete Asset Audit, CI/CD, and Verification Strategy

### D.1 Crop SVG Asset Audit

The app contains 15 SVG vector files in mobile/app/src/main/assets/crops_svg/ (and root ssets/crops_svg/). All 10 canonical MVP crops have dedicated, lightweight SVGs ready for canvas and UI rendering:

| SVG File | Size | MVP Status | Target Crop Mapping |
|---|---|---|---|
| 	omato.svg | 1.2 KB | **CANONICAL MVP** | #1 Tomato / Kamatis |
| eggplant.svg | 1.0 KB | **CANONICAL MVP** | #2 Eggplant / Talong |
| sili.svg | 1.0 KB | **CANONICAL MVP** | #3 Chili Pepper / Sili |
| okra.svg | 1.1 KB | **CANONICAL MVP** | #4 Okra |
| pechay.svg | 1.5 KB | **CANONICAL MVP** | #5 Pechay |
| lettuce.svg | 1.2 KB | **CANONICAL MVP** | #6 Lettuce / Letsugas |
| kangkong.svg | 1.4 KB | **CANONICAL MVP** | #7 Kangkong |
| pipino.svg | 1.2 KB | **CANONICAL MVP** | #8 Cucumber / Pipino |
| sitaw.svg | 1.4 KB | **CANONICAL MVP** | #9 Yardlong Bean / Sitaw |
| corn.svg | 1.9 KB | **CANONICAL MVP** | #10 Sweet Corn / Mais |
| mpalaya.svg | 1.9 KB | *Future Expansion* | Post-MVP (Bitter Gourd) |
| cabbage.svg | 1.5 KB | *Future Expansion* | Post-MVP (Repolyo) |
| carrot.svg | 1.4 KB | *Future Expansion* | Post-MVP (Karot) |
| onion.svg | 1.7 KB | *Future Expansion* | Post-MVP (Sibuyas) |
| pumpkin.svg | 1.3 KB | *Future Expansion* | Post-MVP (Kalabasa) |

### D.2 Mobile Assets vs Root Assets Inventory

| Asset Category | Android Location (mobile/app/src/main/assets/) | Root Location (ssets/) | Synchronization Rule |
|---|---|---|---|
| **Crop SVGs** | ssets/crops_svg/*.svg (15 files) | ssets/crops_svg/*.svg | Mirrored â€” bundled into APK for fast local vector rendering |
| **Crop Metadata** | ssets/metadata/crops.json | ssets/metadata/ | JSON reference dataset for fallback seeding |
| **Audio SFX** | es/raw/*.mp3 (click, success, error) | ssets/audio/ | Android uses es/raw/ via SoundManager.kt |
| **UI Textures** | ssets/textures/ | ssets/textures/ | Soil and grass canvas textures |

### D.3 CI/CD Workflow (.github/workflows/android-ci.yml)

The Android CI workflow validates pull requests and commits to ensure zero regression:
1. **Lint Check**: ./gradlew lintDebug â€” catches missing imports, invalid resource IDs, and deprecated APIs.
2. **Unit Tests**: ./gradlew testDebugUnitTest â€” executes Room DAO tests, DSS Evaluator rule tests, and ViewModel state machine tests.
3. **Assemble Debug**: ./gradlew assembleDebug â€” verifies full APK compilation.

### D.4 Automated Verification Strategy

| Test Layer | Test Location | Test Focus |
|---|---|---|
| **DSS Unit Tests** | mobile/app/src/test/.../dss/ | Evaluates DssSoilEvaluator, DssSeasonEvaluator, DssCompanionEvaluator, DssLogEvaluator, and DssRotationEvaluator with parameterized crop inputs. Asserts research citations and recommended treatments match DA-BPI standards. |
| **Room Migration Tests** | mobile/app/src/test/.../data/local/ | Uses MigrationTestHelper to execute migration from schema version 19 to 20. Asserts user data in crop_plots, crop_logs, and harvests is completely preserved. |
| **UI Compose Tests** | mobile/app/src/androidTest/.../ui/ | Tests FarmHubScreen on 320dp, 360dp, and 412dp viewport widths. Verifies Bottom Sheet expand/collapse gestures, canvas zoom/pan, and tab switching without recomposition stutter. |
