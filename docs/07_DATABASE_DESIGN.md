# 07. Database Design & Schema

## 📌 Overview
MapTanim uses **PostgreSQL** via Supabase as its cloud database, with **Room SQLite** as the local offline cache. Row Level Security (RLS) enforces data isolation per farmer.

**Supabase Project**: `https://ojilvcglpzbtpjxguhzj.supabase.co`

---

## 🗃️ Entity Relationship Overview

```
users
  ├── farms (farmer_id → users.id)
  │     ├── beds (farm_id → farms.id)
  │     │     ├── tasks (bed_id → beds.id)
  │     │     └── activities (bed_id → beds.id)
  │     │     └── harvest_records (bed_id → beds.id)
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

-- Task types (matching badge pins in View Mode)
CREATE TYPE task_type_enum AS ENUM ('WATER', 'FERTILIZE', 'HARVEST', 'PEST_ALERT', 'APPLY_PESTICIDE', 'SOIL_AMENDMENT', 'PRUNING', 'OBSERVATION');

-- DSS relationship types (companion planting)
CREATE TYPE companion_relation_enum AS ENUM ('BENEFICIAL', 'ANTAGONIST', 'NEUTRAL');
```

---

## 🔹 Table: `users`

```sql
CREATE TABLE public.users (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255)    UNIQUE NOT NULL,
    phone_number    VARCHAR(20),
    full_name       VARCHAR(100)    NOT NULL,
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

## 🔹 Table: `beds`

Beds represent individual planting plot beds on the farm canvas. All position and size values are in **meters** relative to farm origin (0, 0).

```sql
CREATE TABLE public.beds (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    farm_id         UUID            NOT NULL REFERENCES public.farms(id) ON DELETE CASCADE,
    bed_label       VARCHAR(20)     NOT NULL,       -- e.g., "BED 1", "BED A", "BED R"
    crop_name       VARCHAR(100),                   -- e.g., "Eggplant", "Tomato", "Mixed Veg"
    crop_id         UUID            REFERENCES public.crops(id),
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

### Demo Farm Beds (from PNG screenshots)

| bed_label | crop_name | soil_type | Notes |
|-----------|-----------|-----------|-------|
| BED 1 | Eggplant | LOAM | Fertilize task active |
| BED 2 | Cucumber | LOAM | Pest alert active |
| BED 3 | Tomato | LOAM | Water + Fertilize tasks active; selected in Edit Mode |
| BED A | Lettuce | CLAY | Warning pin |
| BED E | Cabbage | LOAM | |
| BED F | Carrot | SANDY | |
| BED G | String Beans | LOAM | |
| BED R | Mixed Veg | LOAM | Harvest ready |

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
    title           VARCHAR(200)    NOT NULL,   -- e.g., "Water Bed 3"
    sub_label       VARCHAR(200),               -- e.g., "Tomato" or "Bed 1"
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
    bed_id          UUID            REFERENCES public.beds(id),
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

## 🔹 Table: `notifications`

```sql
CREATE TABLE public.notifications (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID            NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    title           VARCHAR(200)    NOT NULL,
    body            TEXT,
    task_type       task_type_enum,
    is_read         BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- RLS
ALTER TABLE public.notifications ENABLE ROW LEVEL SECURITY;

CREATE POLICY "users_own_notifications" ON public.notifications
    FOR ALL USING (auth.uid() = user_id);
```

---

## 🔹 Room Database (Local Offline Cache)

Each Supabase table has a corresponding Room `@Entity`. Key Room configuration:

```kotlin
@Database(
    entities = [
        UserEntity::class,
        FarmEntity::class,
        BedEntity::class,
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
    abstract fun bedDao(): BedDao
    abstract fun taskDao(): TaskDao
    abstract fun cropDao(): CropDao
}
```
