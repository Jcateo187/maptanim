-- ==============================================================================
-- Migration 012: Full Schema & Table Alignment with MapTanim Mobile App
-- Target: Supabase PostgreSQL (public schema)
-- Ensures all tables, columns, constraints, and RLS policies match the active codebase
-- ==============================================================================

-- 1. ENUMS (Create if not exist)
DO $$ BEGIN
    CREATE TYPE role_enum AS ENUM ('FARMER', 'ADMINISTRATOR', 'GUEST');
EXCEPTION WHEN duplicate_object THEN null; END $$;

DO $$ BEGIN
    CREATE TYPE soil_type_enum AS ENUM ('LOAM', 'CLAY', 'SANDY', 'SILTY', 'PEATY', 'CHALKY');
EXCEPTION WHEN duplicate_object THEN null; END $$;

DO $$ BEGIN
    CREATE TYPE season_enum AS ENUM ('DRY', 'WET', 'YEAR_ROUND');
EXCEPTION WHEN duplicate_object THEN null; END $$;

DO $$ BEGIN
    CREATE TYPE category_enum AS ENUM ('BULB', 'STEM', 'SHOOT', 'LEAFY', 'FLOWER', 'FRUIT', 'ROOT', 'TUBER');
EXCEPTION WHEN duplicate_object THEN null; END $$;

DO $$ BEGIN
    CREATE TYPE task_type_enum AS ENUM ('WATER', 'FERTILIZE', 'HARVEST', 'PEST_ALERT', 'APPLY_PESTICIDE', 'SOIL_AMENDMENT', 'PRUNING', 'OBSERVATION');
EXCEPTION WHEN duplicate_object THEN null; END $$;

-- 2. TABLE: public.profiles
CREATE TABLE IF NOT EXISTS public.profiles (
    id                      UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    nickname                VARCHAR(100),
    avatar                  TEXT,
    onboarding_completed    BOOLEAN NOT NULL DEFAULT FALSE,
    nickname_updated_at     TIMESTAMPTZ,
    tutorial_completed_at   TIMESTAMPTZ,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE public.profiles ADD COLUMN IF NOT EXISTS nickname_updated_at TIMESTAMPTZ;
ALTER TABLE public.profiles ADD COLUMN IF NOT EXISTS tutorial_completed_at TIMESTAMPTZ;
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "profiles_select_all" ON public.profiles;
CREATE POLICY "profiles_select_all" ON public.profiles FOR SELECT USING (true);
DROP POLICY IF EXISTS "profiles_insert_own" ON public.profiles;
CREATE POLICY "profiles_insert_own" ON public.profiles FOR INSERT WITH CHECK (auth.uid() = id);
DROP POLICY IF EXISTS "profiles_update_own" ON public.profiles;
CREATE POLICY "profiles_update_own" ON public.profiles FOR UPDATE USING (auth.uid() = id);

-- 3. TABLE: public.farms
CREATE TABLE IF NOT EXISTS public.farms (
    id              TEXT PRIMARY KEY DEFAULT ('farm_' || substr(md5(random()::text || clock_timestamp()::text), 1, 8)),
    farmer_id       TEXT NOT NULL,
    farm_name       VARCHAR(100) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE public.farms DROP COLUMN IF EXISTS location;
ALTER TABLE public.farms DROP COLUMN IF EXISTS total_area_sqm;
ALTER TABLE public.farms ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "farms_read_all" ON public.farms;
CREATE POLICY "farms_read_all" ON public.farms FOR SELECT USING (true);
DROP POLICY IF EXISTS "farms_insert_all" ON public.farms;
CREATE POLICY "farms_insert_all" ON public.farms FOR INSERT WITH CHECK (true);
DROP POLICY IF EXISTS "farms_update_all" ON public.farms;
CREATE POLICY "farms_update_all" ON public.farms FOR UPDATE USING (true);
DROP POLICY IF EXISTS "farms_delete_all" ON public.farms;
CREATE POLICY "farms_delete_all" ON public.farms FOR DELETE USING (true);

-- 4. TABLE: public.crop_plots
CREATE TABLE IF NOT EXISTS public.crop_plots (
    id              TEXT PRIMARY KEY DEFAULT ('plot_' || substr(md5(random()::text || clock_timestamp()::text), 1, 8)),
    farm_id         TEXT NOT NULL,
    plot_label      VARCHAR(50) NOT NULL,
    crop_name       VARCHAR(100),
    crop_id         TEXT,
    crop_variety    VARCHAR(100),
    soil_type       VARCHAR(50) NOT NULL DEFAULT 'LOAM',
    pos_x           FLOAT NOT NULL DEFAULT 0.0,
    pos_y           FLOAT NOT NULL DEFAULT 0.0,
    width_m         FLOAT NOT NULL DEFAULT 1.0,
    height_m        FLOAT NOT NULL DEFAULT 1.0,
    rotation_deg    FLOAT NOT NULL DEFAULT 0.0,
    notes           TEXT,
    planted_date    TEXT,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE public.crop_plots ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "crop_plots_all" ON public.crop_plots;
CREATE POLICY "crop_plots_all" ON public.crop_plots FOR ALL USING (true) WITH CHECK (true);

-- 5. TABLE: public.crop_zones
CREATE TABLE IF NOT EXISTS public.crop_zones (
    id              TEXT PRIMARY KEY,
    plot_id         TEXT NOT NULL,
    crop_name       VARCHAR(100),
    crop_id         TEXT,
    offset_x        FLOAT NOT NULL DEFAULT 0.0,
    offset_y        FLOAT NOT NULL DEFAULT 0.0,
    width_m         FLOAT NOT NULL DEFAULT 1.0,
    height_m        FLOAT NOT NULL DEFAULT 1.0,
    spacing_m       FLOAT NOT NULL DEFAULT 1.0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE public.crop_zones ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "crop_zones_all" ON public.crop_zones;
CREATE POLICY "crop_zones_all" ON public.crop_zones FOR ALL USING (true) WITH CHECK (true);

-- 6. TABLE: public.farm_objects
CREATE TABLE IF NOT EXISTS public.farm_objects (
    id                  TEXT PRIMARY KEY,
    farm_id             TEXT NOT NULL,
    object_type         VARCHAR(50) NOT NULL,
    world_x             FLOAT NOT NULL DEFAULT 0.0,
    world_y             FLOAT NOT NULL DEFAULT 0.0,
    width_m             FLOAT NOT NULL DEFAULT 1.0,
    height_m            FLOAT NOT NULL DEFAULT 1.0,
    attached_plot_id    TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE public.farm_objects ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "farm_objects_all" ON public.farm_objects;
CREATE POLICY "farm_objects_all" ON public.farm_objects FOR ALL USING (true) WITH CHECK (true);

-- 7. TABLE: public.harvest_records
CREATE TABLE IF NOT EXISTS public.harvest_records (
    id              TEXT PRIMARY KEY DEFAULT ('harv_' || substr(md5(random()::text || clock_timestamp()::text), 1, 8)),
    farm_id         TEXT NOT NULL,
    plot_id         TEXT,
    crop_name       VARCHAR(100) NOT NULL,
    farm_name       VARCHAR(100),
    harvested_date  TEXT NOT NULL,
    yield_kg        FLOAT NOT NULL DEFAULT 0.0,
    quality_grade   VARCHAR(20) DEFAULT 'Grade A',
    notes           TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS farm_name VARCHAR(100);
ALTER TABLE public.harvest_records ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "harvest_records_all" ON public.harvest_records;
CREATE POLICY "harvest_records_all" ON public.harvest_records FOR ALL USING (true) WITH CHECK (true);

-- 8. TABLE: public.feedback / support_tickets
CREATE TABLE IF NOT EXISTS public.feedback (
    id              TEXT PRIMARY KEY DEFAULT ('fb_' || substr(md5(random()::text || clock_timestamp()::text), 1, 8)),
    user_id         TEXT,
    farmer_name     VARCHAR(150) NOT NULL DEFAULT 'Mobile Farmer',
    farm_name       VARCHAR(150),
    category        VARCHAR(50) NOT NULL DEFAULT 'GENERAL',
    subject         VARCHAR(255) NOT NULL,
    message         TEXT NOT NULL,
    status          VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    admin_reply     TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    resolved_at     TIMESTAMPTZ
);

ALTER TABLE public.feedback ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "feedback_all" ON public.feedback;
CREATE POLICY "feedback_all" ON public.feedback FOR ALL USING (true) WITH CHECK (true);

-- 9. TABLE: public.notifications
CREATE TABLE IF NOT EXISTS public.notifications (
    id                  TEXT PRIMARY KEY DEFAULT ('notif_' || substr(md5(random()::text || clock_timestamp()::text), 1, 8)),
    user_id             TEXT,
    title               VARCHAR(255) NOT NULL,
    body                TEXT,
    notification_type   VARCHAR(50) NOT NULL DEFAULT 'SYSTEM_UPDATE',
    is_read             BOOLEAN NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE public.notifications ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "notifications_all" ON public.notifications;
CREATE POLICY "notifications_all" ON public.notifications FOR ALL USING (true) WITH CHECK (true);
