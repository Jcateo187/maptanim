# 07. Database Design & Schema

> 📌 **Navigation**: [◀ 06. Admin Dashboard](file:///d:/Development/MapTanim/docs/06_ADMIN_DASHBOARD.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [08. Supabase Configuration ▶](file:///d:/Development/MapTanim/docs/08_SUPABASE_CONFIGURATION.md)

---
## 📌 Overview
MapTanim uses **PostgreSQL** via Supabase as its cloud database, with **Room SQLite** as the local offline cache. Row Level Security (RLS) enforces data isolation per farmer.

**Supabase Project**: `https://ojilvcglpzbtpjxguhzj.supabase.co`

---

## 🗃️ Entity Relationship Overview

```
users
  ├── farms (farmer_id → users.id)
  │     ├── crop_plots (farm_id → farms.id)
  │     │     ├── crop_zones (plot_id → crop_plots.id)
  │     │     ├── tasks (plot_id → crop_plots.id)
  │     │     ├── activities (plot_id → crop_plots.id)
  │     │     └── harvest_records (plot_id → crop_plots.id)
  │     ├── farm_objects (farm_id → farms.id, attached_plot_id → crop_plots.id)
  │     └── tasks (farm_id → farms.id)
  └── notifications (user_id → users.id)

crops (static reference — admin managed)
dss_rules (static reference — admin managed)
```

---

## 🔹 Enum Types

```sql
-- User roles
CREATE TYPE role_enum AS ENUM ('FARMER', 'ADMINISTRATOR', 'GUEST');

-- Soil types (matching UI: Loam, Clay, Sandy, Silty, Peaty, Chalky)
CREATE TYPE soil_type_enum AS ENUM ('LOAM', 'CLAY', 'SANDY', 'SILTY', 'PEATY', 'CHALKY');

-- Planting seasons
CREATE TYPE season_enum AS ENUM ('DRY', 'WET', 'YEAR_ROUND');

-- Plant-part category (DA/PSA 8 classifications)
CREATE TYPE category_enum AS ENUM ('BULB', 'STEM', 'SHOOT', 'LEAFY', 'FLOWER', 'FRUIT', 'ROOT', 'TUBER');

-- Task types (matching badge pins & TaskType enum in View Mode)
CREATE TYPE task_type_enum AS ENUM ('WATER', 'FERTILIZE', 'HARVEST', 'PEST_ALERT', 'APPLY_PESTICIDE');

-- DSS relationship types (companion planting)
CREATE TYPE companion_relation_enum AS ENUM ('BENEFICIAL', 'ANTAGONIST', 'NEUTRAL');

-- Canvas farm object types (support structures, fences, boundary elements)
CREATE TYPE farm_object_type_enum AS ENUM ('TRELLIS', 'FENCE_SEGMENT', 'TREE', 'DECORATION');
```

---

## 🔹 Table: `users`

```sql
CREATE TABLE public.users (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255)    UNIQUE NOT NULL,
    role            role_enum       NOT NULL DEFAULT 'FARMER',
    avatar_url      TEXT,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- RLS
ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;

CREATE POLICY "users_own_data" ON public.users
    FOR ALL USING (auth.uid() = id);
```

---

## 🔹 Table: `profiles`

Stores user display preferences, avatar selection, and onboarding progress.

```sql
CREATE TABLE public.profiles (
    id                      UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    nickname                VARCHAR(100),
    avatar                  TEXT,
    onboarding_completed    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- RLS
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;

CREATE POLICY "profiles_select_own" ON public.profiles
    FOR SELECT USING (auth.uid() = id);

CREATE POLICY "profiles_insert_own" ON public.profiles
    FOR INSERT WITH CHECK (auth.uid() = id);

CREATE POLICY "profiles_update_own" ON public.profiles
    FOR UPDATE USING (auth.uid() = id);

-- Automatic Profile Creation Trigger
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO public.profiles (id, nickname, onboarding_completed)
  VALUES (
    NEW.id,
    COALESCE(NEW.raw_user_meta_data->>'nickname', split_part(NEW.email, '@', 1)),
    FALSE
  )
  ON CONFLICT (id) DO NOTHING;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();
```

---

## 🔹 Table: `farms`

```sql
CREATE TABLE public.farms (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    farmer_id       UUID            NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    farm_name       VARCHAR(100)    NOT NULL,               -- e.g., "Murcia Farm"
    location        VARCHAR(255),                           -- e.g., "Murcia, Negros Occidental"
    total_area_sqm  FLOAT,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- RLS
ALTER TABLE public.farms ENABLE ROW LEVEL SECURITY;

CREATE POLICY "farmers_own_farms" ON public.farms
    FOR ALL USING (auth.uid() = farmer_id);
```

---

## 🔹 Table: `beds` (Direct-Planted Crop Zones)

> 💡 **Direct Soil Canvas Architecture**: MapTanim features **Direct-to-Soil Canvas Planting**. Farmers drag crops directly onto the soil grid without building raised beds. In the database, the table `public.beds` stores the direct-planted crop area's position ($pos\_x, pos\_y$), grid dimensions ($width\_m, height\_m$), crop variety, soil classification, and growth status.

```sql
CREATE TABLE public.beds (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    farm_id         UUID            NOT NULL REFERENCES public.farms(id) ON DELETE CASCADE,
    bed_label       VARCHAR(20)     NOT NULL,       -- e.g., "PLOT 1", "ZONE A"
    crop_name       VARCHAR(100),                   -- e.g., "Eggplant", "Tomato"
    crop_id         UUID            REFERENCES public.crops(id),
    crop_variety    VARCHAR(100),                   -- e.g., "Diamante Max"
    soil_type       soil_type_enum  NOT NULL DEFAULT 'LOAM',
    pos_x           FLOAT           NOT NULL DEFAULT 0.0,    -- meters from farm origin X
    pos_y           FLOAT           NOT NULL DEFAULT 0.0,    -- meters from farm origin Y
    width_m         FLOAT           NOT NULL DEFAULT 2.0,    -- width in meters
    height_m        FLOAT           NOT NULL DEFAULT 3.0,    -- height in meters
    rotation_deg    FLOAT           NOT NULL DEFAULT 0.0,    -- rotation in degrees
    notes           TEXT,
    planted_date    DATE,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- RLS
ALTER TABLE public.beds ENABLE ROW LEVEL SECURITY;

CREATE POLICY "farmers_own_beds" ON public.beds
    FOR ALL USING (
        EXISTS (
            SELECT 1 FROM public.farms f
            WHERE f.id = beds.farm_id AND f.farmer_id = auth.uid()
        )
    );
```

### Demo Farm Crop Plots

| plot_label | crop_name | soil_type | Notes |
|------------|-----------|-----------|-------|
| PLOT 1 | Eggplant | LOAM | Fertilize task active |
| PLOT 2 | Cucumber | LOAM | Pest alert active |
| PLOT 3 | Tomato | LOAM | Water + Fertilize tasks active |
| PLOT A | Lettuce | CLAY | Warning pin |
| PLOT E | Cabbage | LOAM | |
| PLOT F | Carrot | SANDY | |
| PLOT G | String Beans | LOAM | |
| PLOT R | Mixed Veg | LOAM | Harvest ready |

---

## 🔹 Table: `crop_zones`

`crop_zones` represent sub-regions within a planting plot bed for multi-crop intercropping or grid organization.

```sql
CREATE TABLE public.crop_zones (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    plot_id         UUID            NOT NULL REFERENCES public.crop_plots(id) ON DELETE CASCADE,
    crop_name       VARCHAR(100),                   -- null = empty placeholder zone
    crop_id         UUID            REFERENCES public.crops(id),
    offset_x        FLOAT           NOT NULL DEFAULT 0.0,    -- offset X from plot origin (meters)
    offset_y        FLOAT           NOT NULL DEFAULT 0.0,    -- offset Y from plot origin (meters)
    width_m         FLOAT           NOT NULL DEFAULT 1.0,    -- zone width in meters
    height_m        FLOAT           NOT NULL DEFAULT 1.0,    -- zone height in meters
    spacing_m       FLOAT           NOT NULL DEFAULT 0.3,    -- plant spacing in meters
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- RLS
ALTER TABLE public.crop_zones ENABLE ROW LEVEL SECURITY;

CREATE POLICY "farmers_own_crop_zones" ON public.crop_zones
    FOR ALL USING (
        EXISTS (
            SELECT 1 FROM public.crop_plots p
            JOIN public.farms f ON f.id = p.farm_id
            WHERE p.id = crop_zones.plot_id AND f.farmer_id = auth.uid()
        )
    );
```

---

## 🔹 Table: `farm_objects`

`farm_objects` represent non-crop farm features, boundary fences, trellises, exterior trees, or decorative elements on the 2D canvas.

```sql
CREATE TABLE public.farm_objects (
    id              UUID                  PRIMARY KEY DEFAULT gen_random_uuid(),
    farm_id         UUID                  NOT NULL REFERENCES public.farms(id) ON DELETE CASCADE,
    object_type     farm_object_type_enum NOT NULL,
    world_x         FLOAT                 NOT NULL DEFAULT 0.0,
    world_y         FLOAT                 NOT NULL DEFAULT 0.0,
    width_m         FLOAT                 NOT NULL DEFAULT 1.0,
    height_m        FLOAT                 NOT NULL DEFAULT 1.0,
    rotation_deg    FLOAT                 NOT NULL DEFAULT 0.0,
    attached_plot_id UUID                 REFERENCES public.crop_plots(id) ON DELETE SET NULL,
    created_at      TIMESTAMPTZ           NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ           NOT NULL DEFAULT NOW()
);

-- RLS
ALTER TABLE public.farm_objects ENABLE ROW LEVEL SECURITY;

CREATE POLICY "farmers_own_farm_objects" ON public.farm_objects
    FOR ALL USING (
        EXISTS (
            SELECT 1 FROM public.farms f
            WHERE f.id = farm_objects.farm_id AND f.farmer_id = auth.uid()
        )
    );
```

### Demo Farm Plots (Direct Soil Planting)

| plot_label | crop_name | soil_type | Notes |
|-----------|-----------|-----------|-------|
| PLOT 1 | Eggplant | LOAM | Fertilize task active |
| PLOT 2 | Cucumber | LOAM | Pest alert active |
| PLOT 3 | Tomato | LOAM | Water + Fertilize tasks active; selected in Edit Mode |
| PLOT A | Lettuce | CLAY | Warning pin |
| PLOT E | Cabbage | LOAM | |
| PLOT F | Carrot | SANDY | |
| PLOT G | String Beans | LOAM | |
| PLOT R | Mixed Veg | LOAM | Harvest ready |

---

## 🔹 Table: `crops` (Static Reference)

Managed by administrators via Admin Dashboard. Contains botanical and agronomic data for the 13 high-value vegetables.

```sql
CREATE TABLE public.crops (
    id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    name                VARCHAR(50)     NOT NULL UNIQUE,    -- e.g., "Tomato"
    local_name          VARCHAR(100),                       -- e.g., "Kamatis"
    botanical_name      VARCHAR(150),                       -- e.g., "Solanum lycopersicum"
    category            category_enum   NOT NULL,
    days_to_harvest     INT             NOT NULL,           -- average days from transplant
    optimal_ph_min      FLOAT,                              -- e.g., 6.0
    optimal_ph_max      FLOAT,                              -- e.g., 6.8
    season              season_enum     NOT NULL DEFAULT 'YEAR_ROUND',
    npk_n               FLOAT,                              -- Nitrogen ratio
    npk_p               FLOAT,                              -- Phosphorus ratio
    npk_k               FLOAT,                              -- Potassium ratio
    suitable_soils      soil_type_enum[],                   -- array of suitable soil types
    image_url           TEXT,
    description         TEXT,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);
```

### 13 High-Value Vegetables

| Name | Local Name | Category | Days to Harvest | Season |
|------|-----------|----------|-----------------|--------|
| Tomato | Kamatis | FRUIT | 60–80 | DRY |
| Eggplant | Talong | FRUIT | 65–80 | YEAR_ROUND |
| Bell Pepper | Siling Pula | FRUIT | 70–90 | DRY |
| Cabbage | Repolyo | LEAFY | 80–100 | DRY |
| Onion | Sibuyas | BULB | 100–120 | DRY |
| Carrot | Karot | ROOT | 70–80 | DRY |
| String Beans | Sitaw | FRUIT | 45–60 | YEAR_ROUND |
| Lettuce | Litsugas | LEAFY | 45–60 | WET |
| Cucumber | Pipino | FRUIT | 50–70 | YEAR_ROUND |
| Okra | Okra | FRUIT | 50–60 | WET |
| Corn | Mais | FRUIT | 75–95 | YEAR_ROUND |
| Squash | Kalabasa | FRUIT | 75–90 | WET |
| Kangkong | Kangkong | LEAFY | 30–40 | YEAR_ROUND |

---

## 🔹 Table: `tasks`

Generated by the DSS rule engine. Displayed as TODAY'S TASKS in View Mode.

```sql
CREATE TABLE public.tasks (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    farm_id         UUID            NOT NULL REFERENCES public.farms(id) ON DELETE CASCADE,
    bed_id          UUID            REFERENCES public.beds(id) ON DELETE CASCADE,
    task_type       task_type_enum  NOT NULL,
    title           VARCHAR(200)    NOT NULL,   -- e.g., "Water BED 3"
    sub_label       VARCHAR(200),               -- e.g., "Tomato" or "BED 1"
    due_date        DATE            NOT NULL,
    is_completed    BOOLEAN         NOT NULL DEFAULT FALSE,
    completed_at    TIMESTAMPTZ,
    notes           TEXT,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- RLS
ALTER TABLE public.tasks ENABLE ROW LEVEL SECURITY;

CREATE POLICY "farmers_own_tasks" ON public.tasks
    FOR ALL USING (
        EXISTS (
            SELECT 1 FROM public.farms f
            WHERE f.id = tasks.farm_id AND f.farmer_id = auth.uid()
        )
    );
```

---

## 🔹 Table: `activities`

Manual farming activity log.

```sql
CREATE TABLE public.activities (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    farm_id         UUID            NOT NULL REFERENCES public.farms(id) ON DELETE CASCADE,
    bed_id          UUID            REFERENCES public.beds(id) ON DELETE SET NULL,
    activity_type   task_type_enum  NOT NULL,
    performed_at    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    amount          FLOAT,          -- e.g., liters of water applied
    unit            VARCHAR(20),    -- e.g., "liters", "kg", "bags"
    notes           TEXT,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);
```

---

## 🔹 Table: `harvest_records`

```sql
CREATE TABLE public.harvest_records (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    bed_id          UUID            NOT NULL REFERENCES public.beds(id) ON DELETE CASCADE,
    crop_name       VARCHAR(100)    NOT NULL,
    yield_kg        FLOAT,
    yield_units     INT,
    harvest_date    DATE            NOT NULL DEFAULT CURRENT_DATE,
    quality_rating  INT             CHECK (quality_rating BETWEEN 1 AND 5),
    notes           TEXT,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);
```

---

## 🔹 Table: `dss_rules` (Companion Planting Matrix)

```sql
CREATE TABLE public.dss_rules (
    id              UUID                    PRIMARY KEY DEFAULT gen_random_uuid(),
    crop_a          VARCHAR(50)             NOT NULL,
    crop_b          VARCHAR(50)             NOT NULL,
    relationship    companion_relation_enum NOT NULL,
    reason          TEXT,
    source          VARCHAR(200),   -- BPI / DA-BAR reference
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);
```

### Sample DSS Rules (Companion Planting)

| Crop A | Crop B | Relationship | Reason |
|--------|--------|--------------|--------|
| Tomato | Lettuce | BENEFICIAL | Lettuce shades roots, reduces moisture loss |
| Tomato | Eggplant | ANTAGONIST | Same family, shared pests (fruit borer) |
| Tomato | Carrot | BENEFICIAL | Carrot aerates soil around tomato roots |
| Cucumber | Corn | BENEFICIAL | Corn provides climbing support |
| Cucumber | Potato | ANTAGONIST | Compete for nutrients, attract same blight |
| Eggplant | String Beans | BENEFICIAL | Beans fix nitrogen for eggplant |
| Cabbage | Onion | BENEFICIAL | Onion repels cabbage loopers |
| Lettuce | Carrot | BENEFICIAL | Companion harvest timing aligned |
| Onion | String Beans | ANTAGONIST | Onion inhibits bean growth |

---

## 🔹 Table: `notifications` (System Updates & Admin Announcements)

```sql
CREATE TABLE public.notifications (
    id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID            REFERENCES public.users(id) ON DELETE CASCADE, -- NULL for system-wide broadcasts
    title               VARCHAR(200)    NOT NULL,
    body                TEXT,
    task_type           task_type_enum,
    notification_type   VARCHAR(50)     NOT NULL DEFAULT 'SYSTEM_UPDATE', -- SYSTEM_UPDATE, CROP_ADDITION, BUG_FIX
    is_read             BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- RLS (System announcements read by all authenticated users, user-specific notifications read by owner)
ALTER TABLE public.notifications ENABLE ROW LEVEL SECURITY;

CREATE POLICY "notifications_read_all" ON public.notifications
    FOR SELECT USING (user_id IS NULL OR auth.uid() = user_id);
```

---

## 🔹 Room Database (Local Offline Cache)

Each Supabase table has a corresponding Room `@Entity`. Key Room configuration:

```kotlin
@Database(
    entities = [
        UserEntity::class,
        FarmEntity::class,
        CropPlotEntity::class,
        CropZoneEntity::class,
        FarmObjectEntity::class,
        CropEntity::class,
        TaskEntity::class,
        ActivityEntity::class,
        HarvestRecordEntity::class,
        NotificationEntity::class,
        SyncQueueEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MapTanimDatabase : RoomDatabase() {
    abstract fun farmDao(): FarmDao
    abstract fun cropPlotDao(): CropPlotDao
    abstract fun cropZoneDao(): CropZoneDao
    abstract fun farmObjectDao(): FarmObjectDao
    abstract fun taskDao(): TaskDao
    abstract fun cropDao(): CropDao
    abstract fun userDao(): UserDao
    abstract fun notificationDao(): NotificationDao
}
```

---

## 📚 Related Documentation & Cross References
- 📄 [Master Documentation Hub](file:///d:/Development/MapTanim/docs/README.md)
- 📄 [00. Getting Started Guide](file:///d:/Development/MapTanim/docs/00_GETTING_STARTED.md)
- 📄 [03. System Architecture](file:///d:/Development/MapTanim/docs/03_SYSTEM_ARCHITECTURE.md)
- 📄 [08. Supabase Configuration](file:///d:/Development/MapTanim/docs/08_SUPABASE_CONFIGURATION.md)
- 📄 [09. Authentication](file:///d:/Development/MapTanim/docs/09_AUTHENTICATION.md)
- 📄 [24. Offline Synchronization](file:///d:/Development/MapTanim/docs/24_OFFLINE_SYNCHRONIZATION.md)
- 📄 [25. Security & RLS](file:///d:/Development/MapTanim/docs/25_SECURITY.md)
- 📄 [40. User & Profile Schema Refinement](file:///d:/Development/MapTanim/docs/40_USER_AND_PROFILE_SCHEMA_REFINEMENT.md)
- 📄 [41. Users & Profiles Database Tables](file:///d:/Development/MapTanim/docs/41_USERS_AND_PROFILES_DATABASE_TABLES.md)
