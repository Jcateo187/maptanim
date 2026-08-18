-- MapTanim Versioned Migration 001: Initial Schema & RLS Policies
-- Target: Supabase PostgreSQL (public schema)

-- 1. ENUM TYPES
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

DO $$ BEGIN
    CREATE TYPE companion_relation_enum AS ENUM ('BENEFICIAL', 'ANTAGONIST', 'NEUTRAL');
EXCEPTION WHEN duplicate_object THEN null; END $$;

-- 2. TABLES & RLS POLICIES

-- Table: public.users
CREATE TABLE IF NOT EXISTS public.users (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255)    UNIQUE NOT NULL,
    role            role_enum       NOT NULL DEFAULT 'FARMER',
    avatar_url      TEXT,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "users_own_data" ON public.users;
CREATE POLICY "users_own_data" ON public.users
    FOR ALL USING (auth.uid() = id);

-- Table: public.profiles
CREATE TABLE IF NOT EXISTS public.profiles (
    id                      UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    nickname                VARCHAR(100),
    avatar                  TEXT,
    onboarding_completed    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "profiles_select_own" ON public.profiles;
CREATE POLICY "profiles_select_own" ON public.profiles FOR SELECT USING (auth.uid() = id);

DROP POLICY IF EXISTS "profiles_insert_own" ON public.profiles;
CREATE POLICY "profiles_insert_own" ON public.profiles FOR INSERT WITH CHECK (auth.uid() = id);

DROP POLICY IF EXISTS "profiles_update_own" ON public.profiles;
CREATE POLICY "profiles_update_own" ON public.profiles FOR UPDATE USING (auth.uid() = id);

-- Table: public.crops
CREATE TABLE IF NOT EXISTS public.crops (
    id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    name                VARCHAR(50)     NOT NULL UNIQUE,
    local_name          VARCHAR(100),
    botanical_name      VARCHAR(150),
    category            category_enum   NOT NULL,
    days_to_harvest     INT             NOT NULL,
    optimal_ph_min      FLOAT,
    optimal_ph_max      FLOAT,
    season              season_enum     NOT NULL DEFAULT 'YEAR_ROUND',
    npk_n               FLOAT,
    npk_p               FLOAT,
    npk_k               FLOAT,
    suitable_soils      soil_type_enum[],
    image_url           TEXT,
    description         TEXT,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.crops ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "crops_read_all" ON public.crops;
CREATE POLICY "crops_read_all" ON public.crops FOR SELECT USING (true);

-- Table: public.farms
CREATE TABLE IF NOT EXISTS public.farms (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    farmer_id       UUID            NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    farm_name       VARCHAR(100)    NOT NULL,
    location        VARCHAR(255),
    total_area_sqm  FLOAT,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.farms ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "farmers_own_farms" ON public.farms;
CREATE POLICY "farmers_own_farms" ON public.farms FOR ALL USING (auth.uid() = farmer_id);

-- Table: public.crop_plots
CREATE TABLE IF NOT EXISTS public.crop_plots (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    farm_id         UUID            NOT NULL REFERENCES public.farms(id) ON DELETE CASCADE,
    plot_label      VARCHAR(20)     NOT NULL,
    crop_name       VARCHAR(100),
    crop_id         UUID            REFERENCES public.crops(id),
    crop_variety    VARCHAR(100),
    soil_type       soil_type_enum  NOT NULL DEFAULT 'LOAM',
    pos_x           FLOAT           NOT NULL DEFAULT 0.0,
    pos_y           FLOAT           NOT NULL DEFAULT 0.0,
    width_m         FLOAT           NOT NULL DEFAULT 2.0,
    height_m        FLOAT           NOT NULL DEFAULT 3.0,
    rotation_deg    FLOAT           NOT NULL DEFAULT 0.0,
    notes           TEXT,
    planted_date    DATE,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.crop_plots ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "farmers_own_crop_plots" ON public.crop_plots;
CREATE POLICY "farmers_own_crop_plots" ON public.crop_plots
    FOR ALL USING (
        EXISTS (
            SELECT 1 FROM public.farms f
            WHERE f.id = crop_plots.farm_id AND f.farmer_id = auth.uid()
        )
    );

-- Table: public.tasks
CREATE TABLE IF NOT EXISTS public.tasks (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    farm_id         UUID            NOT NULL REFERENCES public.farms(id) ON DELETE CASCADE,
    plot_id         UUID            REFERENCES public.crop_plots(id) ON DELETE CASCADE,
    task_type       task_type_enum  NOT NULL,
    title           VARCHAR(200)    NOT NULL,
    sub_label       VARCHAR(200),
    due_date        DATE            NOT NULL,
    is_completed    BOOLEAN         NOT NULL DEFAULT FALSE,
    completed_at    TIMESTAMPTZ,
    notes           TEXT,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.tasks ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "farmers_own_tasks" ON public.tasks;
CREATE POLICY "farmers_own_tasks" ON public.tasks
    FOR ALL USING (
        EXISTS (
            SELECT 1 FROM public.farms f
            WHERE f.id = tasks.farm_id AND f.farmer_id = auth.uid()
        )
    );

-- Table: public.activities
CREATE TABLE IF NOT EXISTS public.activities (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    farm_id         UUID            NOT NULL REFERENCES public.farms(id) ON DELETE CASCADE,
    plot_id         UUID            REFERENCES public.crop_plots(id),
    activity_type   task_type_enum  NOT NULL,
    performed_at    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    amount          FLOAT,
    unit            VARCHAR(20),
    notes           TEXT,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.activities ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "farmers_own_activities" ON public.activities;
CREATE POLICY "farmers_own_activities" ON public.activities
    FOR ALL USING (
        EXISTS (
            SELECT 1 FROM public.farms f
            WHERE f.id = activities.farm_id AND f.farmer_id = auth.uid()
        )
    );

-- Table: public.harvest_records
CREATE TABLE IF NOT EXISTS public.harvest_records (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    plot_id         UUID            NOT NULL REFERENCES public.crop_plots(id) ON DELETE CASCADE,
    crop_name       VARCHAR(100)    NOT NULL,
    yield_kg        FLOAT,
    yield_units     INT,
    harvest_date    DATE            NOT NULL DEFAULT CURRENT_DATE,
    quality_rating  INT             CHECK (quality_rating BETWEEN 1 AND 5),
    notes           TEXT,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.harvest_records ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "farmers_own_harvest" ON public.harvest_records;
CREATE POLICY "farmers_own_harvest" ON public.harvest_records
    FOR ALL USING (
        EXISTS (
            SELECT 1 FROM public.crop_plots p
            JOIN public.farms f ON f.id = p.farm_id
            WHERE p.id = harvest_records.plot_id AND f.farmer_id = auth.uid()
        )
    );

-- Table: public.dss_rules
CREATE TABLE IF NOT EXISTS public.dss_rules (
    id              UUID                    PRIMARY KEY DEFAULT gen_random_uuid(),
    crop_a          VARCHAR(50)             NOT NULL,
    crop_b          VARCHAR(50)             NOT NULL,
    relationship    companion_relation_enum NOT NULL,
    reason          TEXT,
    source          VARCHAR(200),
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.dss_rules ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "dss_rules_read_all" ON public.dss_rules;
CREATE POLICY "dss_rules_read_all" ON public.dss_rules FOR SELECT USING (true);

-- Table: public.notifications
CREATE TABLE IF NOT EXISTS public.notifications (
    id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID            REFERENCES public.users(id) ON DELETE CASCADE,
    title               VARCHAR(200)    NOT NULL,
    body                TEXT,
    task_type           task_type_enum,
    notification_type   VARCHAR(50)     NOT NULL DEFAULT 'SYSTEM_UPDATE',
    is_read             BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.notifications ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "notifications_read_all" ON public.notifications;
CREATE POLICY "notifications_read_all" ON public.notifications
    FOR SELECT USING (user_id IS NULL OR auth.uid() = user_id);
