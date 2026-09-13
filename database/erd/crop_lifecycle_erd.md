# MapTanim Database Schema — Crop Lifecycle ERD

Complete entity-relationship diagram for the MapTanim Supabase PostgreSQL schema,
including the new crop lifecycle tables from Migration 013.

## Auth Model

| User Type | Auth Method | Database Presence |
|-----------|------------|-------------------|
| **Admin** (Web) | Vercel env variables (`VITE_ADMIN_EMAIL`/`VITE_ADMIN_PASSWORD`) | No `auth.users` row — uses Supabase service_role key for writes |
| **Farmer** (Mobile) | Supabase Auth (Email/OTP) | `auth.users` row → auto-creates `profiles` row via trigger |

## Full ERD

```mermaid
erDiagram
    %% ─── Auth & Identity ───────────────────────────────────
    AUTH_USERS {
        uuid id PK
        text email
        jsonb raw_user_meta_data
    }

    PROFILES {
        uuid id PK "FK → auth.users"
        varchar nickname
        text avatar
        timestamptz nickname_updated_at
        timestamptz tutorial_completed_at
    }

    AUTH_USERS ||--|| PROFILES : "trigger creates"

    %% ─── Farms ─────────────────────────────────────────────
    FARMS {
        text id PK
        text farmer_id "auth.uid()"
        varchar farm_name
        timestamptz created_at
    }

    AUTH_USERS ||--o{ FARMS : "owns"

    %% ─── Reference Data (Admin-managed) ────────────────────
    CROPS {
        uuid id PK
        varchar name UK
        varchar local_name
        varchar botanical_name
        category_enum category
        int days_to_harvest
        season_enum season
        soil_type_enum[] suitable_soils
        text image_url
    }

    CROP_PROFILES {
        text id PK
        text crop_id "FK → crops"
        jsonb growth_stage_durations "6 stages in days"
        text planting_instructions
        text pest_risks
        text fertilizer_schedule
        text watering_guide
        text[] image_urls "external URLs"
        text thumbnail_url
        varchar created_by_admin
        boolean is_published
    }

    CROPS ||--o| CROP_PROFILES : "enriched by admin"

    %% ─── Isometric Grid (45×45) — white tiles only ──────
    FARM_TILES {
        text id PK
        text farm_id "FK → farms"
        int grid_x "0-44"
        int grid_y "0-44"
        varchar status "tile_status_enum"
        text current_crop_id "FK → crops"
        varchar tile_label
    }

    FARMS ||--o{ FARM_TILES : "contains 45x45 grid"
    CROPS ||--o{ FARM_TILES : "current crop"

    %% ─── Drag-Drop Plantings ──────────────────────────────
    TILE_PLANTINGS {
        text id PK
        text tile_id "FK → farm_tiles"
        text crop_id "FK → crops"
        varchar crop_name
        varchar crop_variety "e.g. Diamante Max F1"
        float width_m "resizable"
        float height_m "resizable"
        float offset_x
        float offset_y
        varchar current_stage "growth_stage_enum (6)"
        timestamptz planted_at
        date expected_harvest_date
        text crop_profile_id "FK → crop_profiles"
        boolean is_active
    }

    FARM_TILES ||--o{ TILE_PLANTINGS : "crop placed on"
    CROPS ||--o{ TILE_PLANTINGS : "crop type"
    CROP_PROFILES ||--o{ TILE_PLANTINGS : "growth durations"

    %% ─── Monitoring & Today's Tasks ────────────────────────
    PLANTING_MONITORS {
        text id PK
        text planting_id "FK → tile_plantings"
        text crop_id "FK → crops (for side nav filter)"
        varchar crop_name
        varchar crop_variety
        varchar monitor_type "WATER FERTILIZE etc"
        float value
        varchar unit
        text notes
        date due_date "for Today's Tasks collection"
        boolean is_completed
        timestamptz completed_at
        timestamptz recorded_at
    }

    TILE_PLANTINGS ||--o{ PLANTING_MONITORS : "care logs"
    CROPS ||--o{ PLANTING_MONITORS : "crop-specific task"

    %% ─── Harvests ─────────────────────────────────────────
    PLANTING_HARVESTS {
        text id PK
        text planting_id "FK → tile_plantings"
        varchar crop_name
        varchar crop_variety
        float yield_kg
        int yield_units
        varchar quality_grade
        date harvest_date
        int growing_days
    }

    TILE_PLANTINGS ||--o{ PLANTING_HARVESTS : "yield records"

    %% ─── Legacy Tables (still active) ─────────────────────
    CROP_PLOTS {
        text id PK
        text farm_id "FK → farms"
        varchar plot_label
        varchar crop_name
        text crop_id
        varchar soil_type
        float pos_x
        float pos_y
        float width_m
        float height_m
        boolean is_active
    }

    FARMS ||--o{ CROP_PLOTS : "contains"

    DSS_RULES {
        uuid id PK
        varchar crop_a
        varchar crop_b
        companion_relation_enum relationship
        text reason
    }

    NOTIFICATIONS {
        text id PK
        text user_id
        varchar title
        text body
        varchar notification_type
        boolean is_read
    }

    COMMUNITY_POSTS {
        text id PK
        uuid author_id "FK → auth.users"
        varchar category
        varchar title
        text content
        int likes_count
        boolean is_pinned
    }

    AUTH_USERS ||--o{ COMMUNITY_POSTS : "authors"
```

## Crop Lifecycle Flow

```mermaid
flowchart LR
    A["🌱 Admin creates<br/>Crop Profile & Variety"] --> B["📋 Crop Tray<br/>(mobile app)"]
    B --> C["🖱️ Drag & Drop<br/>onto Isometric White Tile"]
    C --> D["📊 tile_plantings<br/>(resizable, variety recorded)"]
    D --> E["🔄 Growth Stage<br/>Monitoring<br/>(6 stages)"]
    E --> F["💧 planting_monitors<br/>WATER / FERTILIZE /<br/>PEST_ALERT / Tasks"]
    E --> G["🌾 HARVEST stage<br/>reached"]
    G --> H["📦 planting_harvests<br/>yield & grade recorded"]
    H --> I["📈 Farmer Profile<br/>harvest history"]
```

## Monitoring & Today's Tasks Navigation Design

### 1. Monitoring Dashboard (Plot-First Design)
* **Full Landscape Width Grid**: Displays active planted crops directly from the isometric farm plots (`crop_plots`). No arbitrary catalog filtering sidebar or unplanted crops.
* **Plot Context on Every Card**:
  * **Plot Label**: Identified by assigned plot (e.g. `Plot 1`, `Plot 2`).
  * **Plot Soil Type Badge**: Static soil assigned to that plot (e.g., `Loam Soil`, `Clay Soil`) from agricultural classification.
  * **Seasonal Window Badge**: Growing window for the crop (e.g., `☀️ Tag-araw (Dry)`, `🌧️ Tag-ulan (Wet)`, `🔄 Buong Taon (Year-Round)`).
  * **Growth Progress**: Days planted vs. days to harvest with active 5-stage progress indicator.
* **Screen 2 DSS Panels**: Tapping any plot card opens the 6 operational DSS panels for that specific planted crop (`Overview`, `Timeline`, `Calendar`, `Companions`, `Growing Tips`, `Pest & Disease`).

### 2. Today's Tasks Screen
* **Aggregate Today's Tasks**: Collects all tasks scheduled for the current date across all active farm plots (`due_date <= CURRENT_DATE` and `is_completed = FALSE`).
* **Direct Plot Task List View**: When a farmer taps a task card, the app allows direct completion (watering, fertilizer, weeding) which updates the plot's health and stage tracking.

## Growth Stages (Canonical 5-Stage Timeline)

| # | Stage | Description | Typical Progress |
|---|-------|-------------|-----------------|
| 1 | `SPROUT` | Seed emergence / initial sprouting | 0–15% |
| 2 | `SEEDLING` | Early root establishment and true leaves | 15–35% |
| 3 | `VEGETATIVE` | Rapid stem & foliage development | 35–65% |
| 4 | `FLOWERING` | Budding, pollination & fruit/pod formation | 65–90% |
| 5 | `HARVEST` | Full maturity, ready for harvesting & logging | 90%+ |
