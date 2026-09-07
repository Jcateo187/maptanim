# 37. System Specifications & Scope Refinements

> 📌 **Navigation**: [◀ 36. Crop Variety Timeline & Seasonality](file:///d:/Development/MapTanim/docs/36_CROP_VARIETY_TIMELINE_AND_SEASONALITY.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [38. Audio & Sound Assets Planning ▶](file:///d:/Development/MapTanim/docs/38_AUDIO_AND_SOUND_ASSETS_PLANNING.md)

---
> **Document Version**: 1.0  
> **Source Baseline**: STI Capstone Manuscript (`MapTanim.docx`)  
> **Status**: Approved System Specification & Scope Baseline

---

## 1. Executive Overview

**MapTanim** is a mobile-based farm management and agricultural decision support platform tailored specifically for smallholder vegetable farmers in the Philippines. Grounded in authoritative research from the Department of Agriculture (DA), Bureau of Plant Industry (BPI), and DA Bureau of Agricultural Research (DA-BAR), MapTanim provides science-based guidance across all **eight plant-part vegetable classifications**.

This document formalizes the complete technical specifications and explicit scope boundaries established for the MapTanim system architecture.

---

## 2. Core Scope & System Clarifications

To ensure low-cost, offline-friendly, and accessible deployment for Filipino smallholder farmers without reliance on paid third-party APIs or excessive mobile data requirements, the system enforces five key scope boundaries:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           MAPTANIM SYSTEM SCOPE                             │
├───────────────────────────────────┬─────────────────────────────────────────┤
│ IN SCOPE                          │ EXPLICITLY OUT OF SCOPE / ADAPTED       │
├───────────────────────────────────┼─────────────────────────────────────────┤
│ • 8 Plant-Part Vegetable Matrix   │ • Paid SMS Gateway OTP (Uses Email OTP) │
│ • 2D Isometric Soil Planting Grid │ • Live Market Price & Estimator         │
│ • Offline-First Rule Engine (Room)│ • Live Weather APIs (PAGASA/OpenWeather)│
│ • Monitoring Hub Compatibility    │ • Interactive Canvas Trellis Objects    │
│ • AgriLibrary DIY Trellis Guides  │   (Moved to AgriLibrary References)     │
│ • Season & Soil Crop Recommendations│ • Machine Learning / Adaptive Models    │
└───────────────────────────────────┴─────────────────────────────────────────┘
```

### 2.1 Companion Compatibility Overlay (Monitoring Hub Only)
*   **Specification**: Companion planting compatibility evaluation is performed dynamically within the **Monitoring Hub / Decision Support Overlay** rather than rendered as live colored tile overlays directly on the 2D plot canvas.
*   **Rationale**: The 2D plot map focuses strictly on intuitive direct-soil planting and spatial crop arrangement. When farmers view active crop zones in the Monitoring Hub, the Decision Support System (DSS) evaluates companion pairs (beneficial vs. antagonistic) and displays clear compatibility status cards and advisories.

### 2.2 User Authentication & Security (Email OTP Only)
*   **Specification**: Account authentication and registration use **Email OTP (via Gmail SMTP relay or Supabase Auth)** alongside salted bcrypt password hashing and secure token management.
*   **Rationale**: Third-party SMS Gateways (e.g., Semaphore, Twilio) incur per-message costs. Using Email OTP provides standard multi-factor verification without operational cost barriers for smallholders.

### 2.3 Financial Market Estimator (Excluded)
*   **Specification**: Yield recording tracks harvest date, crop identity, plot location, and total production weight (in kilograms or units). Live market pricing feeds and financial revenue estimators are excluded.
*   **Rationale**: Farm gate prices in Philippine rural markets vary drastically by local trading post (*bagsakan*) and daily buyer negotiations; static or estimated market prices can be misleading. Harvest tracking focuses strictly on yield metrics.

### 2.4 DIY Support Structures & Trellising (AgriLibrary Guides)
*   **Specification**: Step-by-step guides for constructing DIY crop support structures (bamboo A-frame trellises, T-post wires, string mesh, single stakes) are embedded as rich educational materials inside the **AgriLibrary / Knowledge Base**.
*   **Rationale**: Trellis structures are not rendered as interactive draggable game objects on the soil grid; rather, farmers access DIY construction guides and material lists corresponding to vining vegetable crops (e.g., *Ampalaya, Sitaw, Pipino*).

### 2.5 Offline-First Rule Engine (No Live Weather API Dependency)
*   **Specification**: Soil-based crop recommendations, planting schedules, growth stage tasks, and pest/disease alerts are generated entirely by an on-device deterministic rule engine using pre-loaded agricultural datasets.
*   **Rationale**: Third-party live weather APIs (e.g., OpenWeatherMap, PAGASA) require continuous internet access and paid subscription tiers. MapTanim operates offline-first, using seasonal calendar windows (Dry, Wet, Year-Round) derived from verified Philippine agroclimatic data.

---

## 3. Vegetable Classification & Crop Matrix

MapTanim covers vegetable crops across all **8 plant-part classifications** established by the Department of Agriculture (DA) and Philippine Statistics Authority (PSA):

| Plant-Part Classification | Representative Philippine Vegetables | Growth & Companion Characteristics |
|---|---|---|
| **1. Bulb Vegetables** | Onion (*Sibuyas*), Garlic (*Bawang*) | Natural pest deterrent; shallow root systems; pairs well with root/fruit vegetables. |
| **2. Stem Vegetables** | Celery (*Kintsay*), Asparagus | High moisture requirements; sensitive to weed competition. |
| **3. Shoot Vegetables** | Bamboo Shoots (*Labong*), Bean Sprouts (*Togue*) | Fast-growing shoots; high nitrogen uptake. |
| **4. Leafy Vegetables** | Pechay, Kangkong, Mustasa, Alugbati, Malunggay, Lettuce | High nitrogen needs; fast turnover; excellent for intercropping with tall crops. |
| **5. Flower Vegetables** | Broccoli, Cauliflower, Katuray, Banana Blossom (*Puso ng Saging*) | High nutrient demand; benefit from heavy organic matter and insect control. |
| **6. Fruit Vegetables** | Tomato, Eggplant, Ampalaya, Squash, Okra, Cucumber, Pepper (*Sili*) | Solanaceous and Cucurbit crops; high potassium demand; benefit from trellising support guides. |
| **7. Root Vegetables** | Carrot (*Karots*), Radish (*Labanos*), Singkamas, Ginger, Turmeric | Require deep, loose, well-drained loam/sandy soil; complementary below-ground spatial use. |
| **8. Tuber Vegetables** | Potato, Sweet Potato (*Kamote*), Cassava, Gabi, Ube | Long growing cycles; high carbohydrate accumulation; post-harvest soil rest recommended. |

---

## 4. Soil & Season Compatibility Architecture

The system rule engine evaluates soil classification (6 research-backed soil types) against seasonal planting windows (3 seasonal categories):

```
                     [ FARMER INPUTS ]
              Soil Type  +  Planting Date
                             │
                             ▼
                     [ RULE ENGINE ]
       ┌─────────────────────┴─────────────────────┐
       │                                           │
       ▼                                           ▼
[ Soil Filter ]                             [ Season Filter ]
  • Loam (Optimal)                            • Wet Season (May - Oct)
  • Clay (High retention)                     • Dry Season (Nov - Apr)
  • Sandy (High drainage)                     • Year-Round
  • Silty / Peaty / Chalky                    │
       │                                           │
       └─────────────────────┬─────────────────────┘
                             │
                             ▼
                 [ SUITABILITY MATRIX ]
                             │
                             ▼
                 [ RANKED RECOMMENDATIONS ]
```

---

## 5. Decision Support System (DSS) & Monitoring

The DSS functions as a multi-stage advisor during active farm monitoring:

1. **Growth Stage Tracking**:
   - Germination (Days 1–7)
   - Seedling / Early Vegetative (Days 8–21)
   - Mid-Vegetative (Days 22–45)
   - Flowering / Fruit Initiation (Days 46–65)
   - Maturity & Harvest (Days 66+)

2. **Stage-Based Advisories**:
   - Irrigation frequency (weather-season adjusted baseline).
   - NPK fertilization timing and deficiency corrective actions.
   - Weeding and pruning schedules.

3. **Companion Compatibility Check (Monitoring Overlay)**:
   - Evaluates active crops sharing adjacent farm zones.
   - Highlights **Beneficial Pairings** (e.g., *Tomato + Basil/Onion* for pest repulsion).
   - Highlights **Antagonistic Pairings** (e.g., *Onion + Bean/Legumes* nutrient competition).

4. **Pest & Disease Risk Advisories**:
   - Season- and growth-stage-filtered risk alerts based on pre-loaded BPI/DA pest catalogs.
   - Recommends biological and organic interventions first, with chemical options as fallback.

---

## 6. AgriLibrary & Knowledge Base Architecture

The AgriLibrary serves as a cached, offline-accessible repository containing:
- **Crop Profiles**: Comprehensive details on cultivation, growth habits, and spacing across all 8 plant-part categories.
- **Soil Profiles**: Management guidelines for Loam, Clay, Sandy, Silty, Peaty, and Chalky soils.
- **DIY Support Structure Guides**: Material requirements and construction instructions for bamboo trellises, A-frames, and stakes.
- **Pest & Disease Catalog**: Visual symptom references, preventive measures, and biological control techniques.
- **Post-Harvest Handling**: Sorting, grading, storage moisture targets, and soil rest / crop rotation planning guidelines.

---

## 8. Approved 15 Philippine Crops Scope & Offline-First Assets Architecture

To ensure strict agronomic accuracy, copyright safety, and instant offline responsiveness without cloud bandwidth bottlenecks, MapTanim establishes the **15 Approved Philippine Crops Baseline**:

### 8.1 Approved 15 Crops Matrix
1. **Bitter Gourd (*Ampalaya*)** (`FRUIT`) — Cucurbitaceae
2. **Cabbage (*Repolyo*)** (`LEAFY`) — Brassicaceae
3. **Carrot (*Karot*)** (`ROOT`) — Apiaceae
4. **Corn (*Mais*)** (`FRUIT`) — Poaceae
5. **Eggplant (*Talong*)** (`FRUIT`) — Solanaceae
6. **Water Spinach (*Kangkong*)** (`LEAFY`) — Convolvulaceae
7. **Lettuce (*Litsugas*)** (`LEAFY`) — Asteraceae
8. **Okra (*Okra*)** (`FRUIT`) — Malvaceae
9. **Onion (*Sibuyas*)** (`BULB`) — Amaryllidaceae
10. **Pechay (*Pechay*)** (`LEAFY`) — Brassicaceae
11. **Cucumber (*Pipino*)** (`FRUIT`) — Cucurbitaceae
12. **Squash (*Kalabasa*)** (`FRUIT`) — Cucurbitaceae
13. **Chili Pepper (*Sili*)** (`FRUIT`) — Solanaceae
14. **Yardlong String Bean (*Sitaw*)** (`FRUIT`) — Fabaceae
15. **Tomato (*Kamatis*)** (`FRUIT`) — Solanaceae

> [!NOTE]
> PostgreSQL database schema enforces strict `category_enum ('BULB', 'STEM', 'SHOOT', 'LEAFY', 'FLOWER', 'FRUIT', 'ROOT', 'TUBER')`. Podded vegetables (*Sitaw*), Grains (*Corn*), and Cucurbit fruits are classified under `'FRUIT'` in the schema. Non-approved exploratory crops have been completely expunged from the codebase.

### 8.2 Zero-Cloud Asset Architecture (No Cloudflare Required)
- **Local APK Bundling**: All 15 visual assets are bundled directly inside the Android APK at `file:///android_asset/metadata/crops_images/<crop>.png`.
- **Zero Cloud Storage**: Eliminates reliance on Supabase Storage buckets or external CDNs (such as Cloudflare) for Version 1.
- **Copyright Safe**: All graphics are custom MapTanim original assets paired with verified DA-BPI scientific data.
- **100% Offline Capability**: Farmers can launch the app, view crop details, plan spatial plots, and inspect varieties in deep rural areas without internet connectivity.

---

## 9. Dynamic Admin DSS & Mobile Information Broadcast Pipeline

A core architectural tenet of MapTanim is that **the Web Admin can update DSS rules, agronomic guidelines, crop parameters, and publish advisories without any mobile app code changes, recompilation, or app store re-releases.**

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                 DYNAMIC ADMIN-TO-MOBILE KNOWLEDGE PIPELINE                  │
├───────────────────────────────┬─────────────────────────────────────────────┤
│ Web Admin Dashboard           │ Mobile Android Farmer Client               │
├───────────────────────────────┼─────────────────────────────────────────────┤
│ 1. DSS Rules Management       │ 1. App Launch Synchronization               │
│    • Edit companion rules     │    • AppInitializationController fetches    │
│    • Set beneficial/antagonist│      updated dss_rules & reference_crops    │
│ 2. Crop Library Management    │ 2. Dynamic Rule Evaluation                  │
│    • Update pH, timeline,     │    • CompanionDataProvider & DssRuleEngine  │
│      spacing, irrigation      │      evaluate remote rules + offline cache  │
│ 3. Broadcast Advisory Modal   │ 3. Real-Time HUD Alerts                     │
│    • Crop Updates, DA-BPI     │    • Unread notification badge on TopBar    │
│      Guides, System Bulletins │    • Filterable categories & detail modals  │
└───────────────────────────────┴─────────────────────────────────────────────┘
```

### 9.1 Live Sync Pipeline Mechanics
1. **Remote Rule Synchronization**:
   - Web Admin inserts or updates companion rules in Supabase `public.dss_rules`.
   - On mobile app startup, `AppInitializationController.initializeCoreSync()` invokes `DssRuleRepository.fetchFromRemote()`, saving rules into Room local SQLite.
   - `CompanionDataProvider` queries local Room cache first, gracefully falling back to pre-packaged DA-BPI baseline pairings when offline.
2. **Dynamic Crop Guideline Synchronization**:
   - Admin modifications in `CropLibrary.tsx` persist to Supabase `reference_crops`.
   - `CropRepository.fetchFromRemote()` syncs these parameters to the mobile app seamlessly.
3. **Global Broadcast Advisories**:
   - Web Admin uses the "Broadcast Update to Mobile" modal to push informational updates (`CROP_UPDATE`, `AGRONOMIC_GUIDE`, `SYSTEM_UPDATE`).
   - Advisories are inserted into Supabase `notifications` with `user_id = NULL` (global broadcast).
   - `UserRepository.refreshNotifications()` pulls global broadcasts and user alerts, persisting them to Room `NotificationDao` and emitting reactive state updates to `ProfileViewModel` and `HomeViewModel` (HUD TopBar bell count).

---

## 10. Summary of Changes & Document Map

| Document | Primary Updates Applied |
|---|---|
| **[02_SOFTWARE_REQUIREMENTS_SPECIFICATION.md](file:///d:/Development/MapTanim/docs/02_SOFTWARE_REQUIREMENTS_SPECIFICATION.md)** | Updated FR-01 (Email OTP only), FR-03 (Direct soil canvas), FR-05 (Monitoring Hub overlays), FR-07 (DIY Trellis Library). |
| **[09_AUTHENTICATION.md](file:///d:/Development/MapTanim/docs/09_AUTHENTICATION.md)** | Standardized on Email OTP (Gmail SMTP / Supabase Auth); removed paid SMS gateway endpoints. |
| **[16_INTERACTIVE_PLOT_MAPPING.md](file:///d:/Development/MapTanim/docs/16_INTERACTIVE_PLOT_MAPPING.md)** | Specified 2D soil grid rendering without grid overlay badges; moved companion overlays to Monitoring Hub. |
| **[20_DECISION_SUPPORT_SYSTEM.md](file:///d:/Development/MapTanim/docs/20_DECISION_SUPPORT_SYSTEM.md)** | Detailed DSS Monitoring Hub overlay logic, dynamic `dss_rules` synchronization, and offline fallback engine. |
| **[21_KNOWLEDGE_BASE.md](file:///d:/Development/MapTanim/docs/21_KNOWLEDGE_BASE.md)** | Added DIY Support Structures & Trellising reference guide specifications under AgriLibrary. |
| **[23_NOTIFICATION_SYSTEM.md](file:///d:/Development/MapTanim/docs/23_NOTIFICATION_SYSTEM.md)** | Documented Web Admin Broadcast system (`user_id IS NULL`), multi-type advisory filters, and Room-backed HUD badge. |
| **[33_ROADMAP.md](file:///d:/Development/MapTanim/docs/33_ROADMAP.md)** | Updated roadmap to reflect simplified auth, offline rule engine, and library-based DIY trellises. |

---

## 📚 Related Documentation & Cross References
- 📄 [Master Documentation Hub](file:///d:/Development/MapTanim/docs/README.md)
- 📄 [00. Getting Started Guide](file:///d:/Development/MapTanim/docs/00_GETTING_STARTED.md)
- 📄 [03. System Architecture](file:///d:/Development/MapTanim/docs/03_SYSTEM_ARCHITECTURE.md)
- 📄 [17. Farm Management](file:///d:/Development/MapTanim/docs/17_FARM_MANAGEMENT.md)
- 📄 [20. Decision Support System](file:///d:/Development/MapTanim/docs/20_DECISION_SUPPORT_SYSTEM.md)
- 📄 [21. Knowledge Base](file:///d:/Development/MapTanim/docs/21_KNOWLEDGE_BASE.md)
- 📄 [22. Calendar Engine](file:///d:/Development/MapTanim/docs/22_CALENDAR.md)
- 📄 [23. Notification System](file:///d:/Development/MapTanim/docs/23_NOTIFICATION_SYSTEM.md)
- 📄 [36. Crop Variety Timeline & Seasonality](file:///d:/Development/MapTanim/docs/36_CROP_VARIETY_TIMELINE_AND_SEASONALITY.md)

