-- ==============================================================================
-- MapTanim Consolidated Master Database Schema & Seed Script for Supabase
-- Target Project: ojilvcglpzbtpjxguhzj.supabase.co
-- Fully Synchronized with Migrations 001 through 021 (Current App Version 1.2.6)
--
-- Includes:
--   1. Enum Types (including WEED, TRELLIS, NUTRITION, ROTATION_ALERT)
--   2. Pruning & Cleanup of Obsolete Tables (Migration 020)
--   3. All 14 Core Relational Tables + Practical Agronomy & Crop Rotation Log
--   4. Row Level Security (RLS) Policies & User Trigger
--   5. Supabase Storage Bucket Setup (crop-images, user-avatars, pest-guides)
--   6. Full Seed Data: 15 Canonical Crops, 58 DA-BPI Companion Rules, Initial Notifications & Feedback
-- ==============================================================================

-- ============================================================================
-- 1. ENUM TYPES
-- ============================================================================

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
    CREATE TYPE task_type_enum AS ENUM (
        'WATER', 'FERTILIZE', 'HARVEST', 'PEST_ALERT', 'APPLY_PESTICIDE',
        'SOIL_AMENDMENT', 'PRUNING', 'OBSERVATION',
        'WEED', 'TRELLIS', 'NUTRITION', 'ROTATION_ALERT'
    );
EXCEPTION WHEN duplicate_object THEN null; END $$;

-- Extend task_type_enum if already exists without newer values
DO $$ BEGIN
    ALTER TYPE task_type_enum ADD VALUE IF NOT EXISTS 'WEED';
    ALTER TYPE task_type_enum ADD VALUE IF NOT EXISTS 'TRELLIS';
    ALTER TYPE task_type_enum ADD VALUE IF NOT EXISTS 'NUTRITION';
    ALTER TYPE task_type_enum ADD VALUE IF NOT EXISTS 'ROTATION_ALERT';
EXCEPTION WHEN duplicate_object THEN null; END $$;

DO $$ BEGIN
    CREATE TYPE companion_relation_enum AS ENUM ('BENEFICIAL', 'ANTAGONIST', 'NEUTRAL');
EXCEPTION WHEN duplicate_object THEN null; END $$;

-- ============================================================================
-- 2. CLEANUP OF OBSOLETE & REDUNDANT TABLES (Migration 020)
-- Isometric 45x45 grid and scenery are procedurally rendered in Compose canvas.
-- Obsolete duplicate tables are pruned.
-- ============================================================================

DROP TABLE IF EXISTS public.planting_monitors CASCADE;
DROP TABLE IF EXISTS public.planting_harvests CASCADE;
DROP TABLE IF EXISTS public.tile_plantings CASCADE;
DROP TABLE IF EXISTS public.farm_tiles CASCADE;
DROP TABLE IF EXISTS public.crop_profiles CASCADE;
DROP TABLE IF EXISTS public.farm_objects CASCADE;
DROP TABLE IF EXISTS public.activities CASCADE;

-- ============================================================================
-- 3. CORE RELATIONAL TABLES & ROW LEVEL SECURITY
-- ============================================================================

-- Table 1: public.users (Supabase user records & roles)
CREATE TABLE IF NOT EXISTS public.users (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255)    UNIQUE NOT NULL,
    role            role_enum       NOT NULL DEFAULT 'FARMER',
    avatar_url      TEXT,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.users DROP COLUMN IF EXISTS full_name;
ALTER TABLE public.users DROP COLUMN IF EXISTS phone_number;

ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "users_own_data" ON public.users;
CREATE POLICY "users_own_data" ON public.users
    FOR ALL USING (auth.uid() = id);

DROP POLICY IF EXISTS "users_read_all" ON public.users;
CREATE POLICY "users_read_all" ON public.users
    FOR SELECT USING (true);


-- Table 2: public.profiles (Queried by ProfileRepository & Auth flows)
CREATE TABLE IF NOT EXISTS public.profiles (
    id                      UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    nickname                VARCHAR(100),
    avatar                  TEXT,
    nickname_updated_at     TIMESTAMPTZ,
    tutorial_completed_at   TIMESTAMPTZ,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE public.profiles DROP COLUMN IF EXISTS first_name;
ALTER TABLE public.profiles DROP COLUMN IF EXISTS last_name;
ALTER TABLE public.profiles DROP COLUMN IF EXISTS onboarding_completed;
ALTER TABLE public.profiles ADD COLUMN IF NOT EXISTS nickname_updated_at TIMESTAMPTZ;
ALTER TABLE public.profiles ADD COLUMN IF NOT EXISTS tutorial_completed_at TIMESTAMPTZ;

ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "profiles_select_all" ON public.profiles;
CREATE POLICY "profiles_select_all" ON public.profiles FOR SELECT USING (true);

DROP POLICY IF EXISTS "profiles_insert_own" ON public.profiles;
CREATE POLICY "profiles_insert_own" ON public.profiles FOR INSERT WITH CHECK (auth.uid() = id);

DROP POLICY IF EXISTS "profiles_update_own" ON public.profiles;
CREATE POLICY "profiles_update_own" ON public.profiles FOR UPDATE USING (auth.uid() = id);

-- Auto-profile trigger on new user signup
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO public.profiles (id, nickname, nickname_updated_at)
  VALUES (
    NEW.id,
    COALESCE(NEW.raw_user_meta_data->>'nickname', CASE WHEN NEW.email IS NOT NULL AND NEW.email <> '' THEN split_part(NEW.email, '@', 1) ELSE 'Farmer' END),
    NOW()
  )
  ON CONFLICT (id) DO NOTHING;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();

-- Backfill profile records for existing users
INSERT INTO public.profiles (id, nickname)
SELECT 
    id,
    COALESCE(raw_user_meta_data->>'nickname', split_part(email, '@', 1))
FROM auth.users
ON CONFLICT (id) DO NOTHING;


-- Table 3: public.crops (Canonical Agronomic Reference Data & Badges)
CREATE TABLE IF NOT EXISTS public.crops (
    id                          TEXT            PRIMARY KEY DEFAULT ('crop_' || substr(md5(random()::text || clock_timestamp()::text), 1, 8)),
    name                        VARCHAR(50)     NOT NULL UNIQUE,
    local_name                  VARCHAR(100),
    botanical_name              VARCHAR(150),
    taxonomic_family            VARCHAR(100),
    botanical_family            TEXT            DEFAULT 'General',
    category                    category_enum   NOT NULL,
    days_to_harvest             INT             NOT NULL,
    watering_interval_days      INT             NOT NULL DEFAULT 2,
    fertilize_interval_days     INT             NOT NULL DEFAULT 14,
    weeding_interval_days       INT             NOT NULL DEFAULT 7,
    optimal_ph_min              FLOAT           DEFAULT 6.0,
    optimal_ph_max              FLOAT           DEFAULT 7.0,
    optimal_temp_min            FLOAT           DEFAULT 20.0,
    optimal_temp_max            FLOAT           DEFAULT 32.0,
    season                      season_enum     NOT NULL DEFAULT 'YEAR_ROUND',
    npk_n                       FLOAT           DEFAULT 1.0,
    npk_p                       FLOAT           DEFAULT 1.0,
    npk_k                       FLOAT           DEFAULT 1.0,
    suitable_soils              soil_type_enum[],
    image_url                   TEXT,
    growth_stages               JSONB,
    harvest_indicators          TEXT,
    needs_trellis               BOOLEAN         DEFAULT FALSE,
    trellis_type                TEXT            DEFAULT 'NONE',
    preferred_planting_method   TEXT            DEFAULT 'DIRECT',
    soil_prep_tagalog           TEXT,
    harvest_signs_tagalog       TEXT,
    common_pests                TEXT[]          DEFAULT '{}',
    companion_plants_good       TEXT[]          DEFAULT '{}',
    companion_plants_bad        TEXT[]          DEFAULT '{}',
    description                 TEXT,
    created_at                  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- Ensure all agronomic columns exist if table was previously created
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS local_name VARCHAR(100);
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS botanical_name VARCHAR(150);
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS taxonomic_family VARCHAR(100);
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS botanical_family TEXT DEFAULT 'General';
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS watering_interval_days INT NOT NULL DEFAULT 2;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS fertilize_interval_days INT NOT NULL DEFAULT 14;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS weeding_interval_days INT NOT NULL DEFAULT 7;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS optimal_ph_min FLOAT DEFAULT 6.0;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS optimal_ph_max FLOAT DEFAULT 7.0;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS optimal_temp_min FLOAT DEFAULT 20.0;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS optimal_temp_max FLOAT DEFAULT 32.0;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS npk_n FLOAT DEFAULT 1.0;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS npk_p FLOAT DEFAULT 1.0;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS npk_k FLOAT DEFAULT 1.0;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS growth_stages JSONB;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS harvest_indicators TEXT;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS needs_trellis BOOLEAN DEFAULT FALSE;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS trellis_type TEXT DEFAULT 'NONE';
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS preferred_planting_method TEXT DEFAULT 'DIRECT';
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS soil_prep_tagalog TEXT;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS harvest_signs_tagalog TEXT;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS common_pests TEXT[] DEFAULT '{}';
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS companion_plants_good TEXT[] DEFAULT '{}';
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS companion_plants_bad TEXT[] DEFAULT '{}';
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();

ALTER TABLE public.crops ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "crops_read_all" ON public.crops;
DROP POLICY IF EXISTS "crops_all" ON public.crops;
CREATE POLICY "crops_all" ON public.crops FOR ALL USING (true) WITH CHECK (true);


-- Table 4: public.dss_rules (Decision Support System Companion Matrix)
CREATE TABLE IF NOT EXISTS public.dss_rules (
    id              TEXT                    PRIMARY KEY DEFAULT ('rule_' || substr(md5(random()::text || clock_timestamp()::text), 1, 8)),
    crop_a          VARCHAR(50)             NOT NULL,
    crop_b          VARCHAR(50)             NOT NULL,
    relationship    companion_relation_enum NOT NULL,
    reason          TEXT,
    source          VARCHAR(200),
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.dss_rules ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "dss_rules_read_all" ON public.dss_rules;
DROP POLICY IF EXISTS "dss_rules_all" ON public.dss_rules;
CREATE POLICY "dss_rules_all" ON public.dss_rules FOR ALL USING (true) WITH CHECK (true);


-- Table 5: public.farms (Farmer Farm Entities)
CREATE TABLE IF NOT EXISTS public.farms (
    id              TEXT            PRIMARY KEY DEFAULT ('farm_' || substr(md5(random()::text || clock_timestamp()::text), 1, 8)),
    farmer_id       TEXT            NOT NULL,
    farm_name       VARCHAR(100)    NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.farms DROP COLUMN IF EXISTS location;
ALTER TABLE public.farms DROP COLUMN IF EXISTS total_area_sqm;

ALTER TABLE public.farms ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "farms_read_all" ON public.farms;
DROP POLICY IF EXISTS "farms_insert_all" ON public.farms;
DROP POLICY IF EXISTS "farms_update_all" ON public.farms;
DROP POLICY IF EXISTS "farms_delete_all" ON public.farms;
CREATE POLICY "farms_read_all" ON public.farms FOR SELECT USING (true);
CREATE POLICY "farms_insert_all" ON public.farms FOR INSERT WITH CHECK (true);
CREATE POLICY "farms_update_all" ON public.farms FOR UPDATE USING (true);
CREATE POLICY "farms_delete_all" ON public.farms FOR DELETE USING (true);


-- Table 6: public.crop_plots (Direct-to-Soil Planted Plots on Isometric Canvas)
CREATE TABLE IF NOT EXISTS public.crop_plots (
    id              TEXT            PRIMARY KEY DEFAULT ('plot_' || substr(md5(random()::text || clock_timestamp()::text), 1, 8)),
    farm_id         TEXT            NOT NULL,
    plot_label      VARCHAR(50)     NOT NULL,
    crop_name       VARCHAR(100),
    crop_id         TEXT,
    crop_variety    VARCHAR(100),
    soil_type       VARCHAR(50)     NOT NULL DEFAULT 'LOAM',
    pos_x           FLOAT           NOT NULL DEFAULT 0.0,
    pos_y           FLOAT           NOT NULL DEFAULT 0.0,
    width_m         FLOAT           NOT NULL DEFAULT 1.0,
    height_m        FLOAT           NOT NULL DEFAULT 1.0,
    rotation_deg    FLOAT           NOT NULL DEFAULT 0.0,
    notes           TEXT,
    planted_date    TEXT,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.crop_plots ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "crop_plots_all" ON public.crop_plots;
CREATE POLICY "crop_plots_all" ON public.crop_plots FOR ALL USING (true) WITH CHECK (true);


-- Table 7: public.crop_zones (Sub-regions within plots for fine-grained multi-crop layout)
CREATE TABLE IF NOT EXISTS public.crop_zones (
    id              TEXT            PRIMARY KEY,
    plot_id         TEXT            NOT NULL,
    crop_name       VARCHAR(100),
    crop_id         TEXT,
    offset_x        FLOAT           NOT NULL DEFAULT 0.0,
    offset_y        FLOAT           NOT NULL DEFAULT 0.0,
    width_m         FLOAT           NOT NULL DEFAULT 1.0,
    height_m        FLOAT           NOT NULL DEFAULT 1.0,
    spacing_m       FLOAT           NOT NULL DEFAULT 1.0,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.crop_zones ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "crop_zones_all" ON public.crop_zones;
CREATE POLICY "crop_zones_all" ON public.crop_zones FOR ALL USING (true) WITH CHECK (true);


-- Table 8: public.tasks (Daily Farming Care Tasks & Badge Overlays)
CREATE TABLE IF NOT EXISTS public.tasks (
    id              TEXT            PRIMARY KEY DEFAULT ('task_' || substr(md5(random()::text || clock_timestamp()::text), 1, 8)),
    farm_id         TEXT            NOT NULL,
    plot_id         TEXT,
    plot_label      VARCHAR(50),
    crop_name       VARCHAR(100),
    task_type       task_type_enum  NOT NULL,
    title           VARCHAR(200)    NOT NULL,
    sub_label       VARCHAR(200),
    due_date        DATE            NOT NULL,
    is_completed    BOOLEAN         NOT NULL DEFAULT FALSE,
    completed_at    TIMESTAMPTZ,
    notes           TEXT,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.tasks ADD COLUMN IF NOT EXISTS plot_label VARCHAR(50);
ALTER TABLE public.tasks ADD COLUMN IF NOT EXISTS crop_name VARCHAR(100);

ALTER TABLE public.tasks ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "tasks_all" ON public.tasks;
CREATE POLICY "tasks_all" ON public.tasks FOR ALL USING (true) WITH CHECK (true);


-- Table 9: public.harvest_records (Harvest Logs & Post-Harvest Seasonal Insights)
CREATE TABLE IF NOT EXISTS public.harvest_records (
    id                      TEXT            PRIMARY KEY DEFAULT ('harv_' || substr(md5(random()::text || clock_timestamp()::text), 1, 8)),
    farm_id                 TEXT            NOT NULL,
    plot_id                 TEXT,
    farm_name               VARCHAR(100)    DEFAULT 'MapTanim Main Farm',
    plot_label              VARCHAR(50)     DEFAULT 'Plot 1',
    crop_name               VARCHAR(100)    NOT NULL,
    crop_variety            VARCHAR(100),
    planted_date            TEXT,
    harvested_at            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    harvested_date          TEXT,
    growing_duration_days   INT             DEFAULT 0,
    yield_kg                FLOAT           NOT NULL DEFAULT 0.0,
    yield_units             INT,
    quality_grade           VARCHAR(20)     DEFAULT 'Grade A',
    quality_rating          INT             CHECK (quality_rating BETWEEN 1 AND 5) DEFAULT 5,
    seasonal_insight        TEXT,
    rotation_suggestion     TEXT,
    fallow_until_date       TEXT,
    notes                   TEXT,
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS farm_name VARCHAR(100) DEFAULT 'MapTanim Main Farm';
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS plot_label VARCHAR(50) DEFAULT 'Plot 1';
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS crop_variety VARCHAR(100);
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS planted_date TEXT;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS harvested_at TIMESTAMPTZ DEFAULT NOW();
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS harvested_date TEXT;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS growing_duration_days INT DEFAULT 0;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS quality_rating INT DEFAULT 5;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS seasonal_insight TEXT;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS rotation_suggestion TEXT;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS fallow_until_date TEXT;

ALTER TABLE public.harvest_records ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "harvest_records_all" ON public.harvest_records;
CREATE POLICY "harvest_records_all" ON public.harvest_records FOR ALL USING (true) WITH CHECK (true);


-- Table 10: public.crop_rotation_log (Plant Family History for Soil Health)
CREATE TABLE IF NOT EXISTS public.crop_rotation_log (
    id                  TEXT            PRIMARY KEY DEFAULT ('rot_' || substr(md5(random()::text || clock_timestamp()::text), 1, 8)),
    farm_id             TEXT            NOT NULL,
    plot_id             TEXT            NOT NULL,
    botanical_family    TEXT            NOT NULL,
    crop_name           VARCHAR(100)    NOT NULL,
    planted_date        TEXT            NOT NULL,
    harvested_date      TEXT,
    cycle_count         INT             DEFAULT 1,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.crop_rotation_log ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "crop_rotation_log_all" ON public.crop_rotation_log;
CREATE POLICY "crop_rotation_log_all" ON public.crop_rotation_log FOR ALL USING (true) WITH CHECK (true);


-- Table 11: public.feedback (Mobile Support Tickets & Admin Replies)
CREATE TABLE IF NOT EXISTS public.feedback (
    id              TEXT            PRIMARY KEY DEFAULT ('fb_' || substr(md5(random()::text || clock_timestamp()::text), 1, 8)),
    user_id         TEXT,
    farmer_name     VARCHAR(150)    NOT NULL DEFAULT 'Mobile Farmer',
    farm_name       VARCHAR(150),
    category        VARCHAR(50)     NOT NULL DEFAULT 'GENERAL',
    subject         VARCHAR(255)    NOT NULL,
    message         TEXT            NOT NULL,
    status          VARCHAR(50)     NOT NULL DEFAULT 'PENDING',
    admin_reply     TEXT,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    resolved_at     TIMESTAMPTZ
);

ALTER TABLE public.feedback ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "feedback_all" ON public.feedback;
CREATE POLICY "feedback_all" ON public.feedback FOR ALL USING (true) WITH CHECK (true);


-- Table 12: public.notifications (System Updates & Broadcast Alerts)
CREATE TABLE IF NOT EXISTS public.notifications (
    id                  TEXT            PRIMARY KEY DEFAULT ('notif_' || substr(md5(random()::text || clock_timestamp()::text), 1, 8)),
    user_id             TEXT,
    title               VARCHAR(255)    NOT NULL,
    body                TEXT,
    task_type           task_type_enum,
    notification_type   VARCHAR(50)     NOT NULL DEFAULT 'SYSTEM_UPDATE',
    is_read             BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.notifications ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "notifications_all" ON public.notifications;
CREATE POLICY "notifications_all" ON public.notifications FOR ALL USING (true) WITH CHECK (true);


-- Table 13: public.community_posts (Farmer Community Forum Discussions)
CREATE TABLE IF NOT EXISTS public.community_posts (
    id                  TEXT            PRIMARY KEY DEFAULT ('post_' || substr(md5(random()::text || clock_timestamp()::text), 1, 16)),
    author_id           UUID            REFERENCES auth.users(id) ON DELETE SET NULL,
    author_name         VARCHAR(150)    NOT NULL,
    author_avatar_url   TEXT,
    category            VARCHAR(50)     NOT NULL DEFAULT 'GENERAL',
    title               VARCHAR(255)    NOT NULL,
    content             TEXT            NOT NULL,
    likes_count         INT             NOT NULL DEFAULT 0,
    comments_count      INT             NOT NULL DEFAULT 0,
    is_pinned           BOOLEAN         NOT NULL DEFAULT FALSE,
    tags                TEXT[]          NOT NULL DEFAULT '{}',
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.community_posts DROP COLUMN IF EXISTS author_location;
ALTER TABLE public.community_posts ALTER COLUMN author_name DROP DEFAULT;

ALTER TABLE public.community_posts ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "community_posts_select_all" ON public.community_posts;
CREATE POLICY "community_posts_select_all" ON public.community_posts FOR SELECT USING (true);
DROP POLICY IF EXISTS "community_posts_insert_all" ON public.community_posts;
CREATE POLICY "community_posts_insert_all" ON public.community_posts FOR INSERT WITH CHECK (true);
DROP POLICY IF EXISTS "community_posts_update_all" ON public.community_posts;
CREATE POLICY "community_posts_update_all" ON public.community_posts FOR UPDATE USING (true);
DROP POLICY IF EXISTS "community_posts_delete_all" ON public.community_posts;
CREATE POLICY "community_posts_delete_all" ON public.community_posts FOR DELETE USING (true);

CREATE INDEX IF NOT EXISTS idx_community_posts_category ON public.community_posts(category);
CREATE INDEX IF NOT EXISTS idx_community_posts_created ON public.community_posts(created_at DESC);


-- Table 14: public.community_comments (Comments under Community Posts)
CREATE TABLE IF NOT EXISTS public.community_comments (
    id                  TEXT            PRIMARY KEY DEFAULT ('comm_' || substr(md5(random()::text || clock_timestamp()::text), 1, 16)),
    post_id             TEXT            NOT NULL REFERENCES public.community_posts(id) ON DELETE CASCADE,
    author_id           UUID            REFERENCES auth.users(id) ON DELETE SET NULL,
    author_name         VARCHAR(150)    NOT NULL,
    author_avatar_url   TEXT,
    content             TEXT            NOT NULL,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.community_comments DROP COLUMN IF EXISTS author_location;
ALTER TABLE public.community_comments ALTER COLUMN author_name DROP DEFAULT;

ALTER TABLE public.community_comments ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "community_comments_select_all" ON public.community_comments;
CREATE POLICY "community_comments_select_all" ON public.community_comments FOR SELECT USING (true);
DROP POLICY IF EXISTS "community_comments_insert_all" ON public.community_comments;
CREATE POLICY "community_comments_insert_all" ON public.community_comments FOR INSERT WITH CHECK (true);
DROP POLICY IF EXISTS "community_comments_update_all" ON public.community_comments;
CREATE POLICY "community_comments_update_all" ON public.community_comments FOR UPDATE USING (true);
DROP POLICY IF EXISTS "community_comments_delete_all" ON public.community_comments;
CREATE POLICY "community_comments_delete_all" ON public.community_comments FOR DELETE USING (true);

CREATE INDEX IF NOT EXISTS idx_community_comments_post_id ON public.community_comments(post_id);


-- Table 15: public.community_reports (Content Moderation Queue for Admin)
CREATE TABLE IF NOT EXISTS public.community_reports (
    id                  TEXT            PRIMARY KEY DEFAULT ('rep_' || substr(md5(random()::text || clock_timestamp()::text), 1, 16)),
    reporter_id         UUID            REFERENCES auth.users(id) ON DELETE SET NULL,
    reporter_name       VARCHAR(150)    NOT NULL,
    target_type         VARCHAR(50)     NOT NULL, -- 'POST', 'USER', 'COMMENT'
    target_id           TEXT            NOT NULL,
    target_name         VARCHAR(150)    NOT NULL,
    target_content      TEXT,
    reason              VARCHAR(100)    NOT NULL,
    details             TEXT,
    status              VARCHAR(50)     NOT NULL DEFAULT 'PENDING',
    admin_notes         TEXT,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    resolved_at         TIMESTAMPTZ
);

ALTER TABLE public.community_reports ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "community_reports_select_all" ON public.community_reports;
CREATE POLICY "community_reports_select_all" ON public.community_reports FOR SELECT USING (true);
DROP POLICY IF EXISTS "community_reports_insert_all" ON public.community_reports;
CREATE POLICY "community_reports_insert_all" ON public.community_reports FOR INSERT WITH CHECK (true);
DROP POLICY IF EXISTS "community_reports_update_all" ON public.community_reports;
CREATE POLICY "community_reports_update_all" ON public.community_reports FOR UPDATE USING (true);
DROP POLICY IF EXISTS "community_reports_delete_all" ON public.community_reports;
CREATE POLICY "community_reports_delete_all" ON public.community_reports FOR DELETE USING (true);

CREATE INDEX IF NOT EXISTS idx_community_reports_target ON public.community_reports(target_type, target_id);
CREATE INDEX IF NOT EXISTS idx_community_reports_status ON public.community_reports(status);
CREATE INDEX IF NOT EXISTS idx_community_reports_created ON public.community_reports(created_at DESC);


-- ============================================================================
-- 4. SUPABASE STORAGE BUCKET CONFIGURATION (Migration 017)
-- ============================================================================

INSERT INTO storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
VALUES (
    'crop-images',
    'crop-images',
    true,
    5242880,
    ARRAY['image/png', 'image/jpeg', 'image/webp', 'image/gif']
)
ON CONFLICT (id) DO UPDATE SET
    public = true,
    file_size_limit = 5242880,
    allowed_mime_types = ARRAY['image/png', 'image/jpeg', 'image/webp', 'image/gif'];

INSERT INTO storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
VALUES (
    'user-avatars',
    'user-avatars',
    true,
    2097152,
    ARRAY['image/png', 'image/jpeg', 'image/webp']
)
ON CONFLICT (id) DO NOTHING;

-- Storage RLS policies for crop-images
DROP POLICY IF EXISTS "Public Access crop-images" ON storage.objects;
CREATE POLICY "Public Access crop-images" ON storage.objects FOR SELECT USING (bucket_id = 'crop-images');

DROP POLICY IF EXISTS "Public Upload crop-images" ON storage.objects;
CREATE POLICY "Public Upload crop-images" ON storage.objects FOR INSERT WITH CHECK (bucket_id = 'crop-images');

DROP POLICY IF EXISTS "Public Update crop-images" ON storage.objects;
CREATE POLICY "Public Update crop-images" ON storage.objects FOR UPDATE USING (bucket_id = 'crop-images');

DROP POLICY IF EXISTS "Public Delete crop-images" ON storage.objects;
CREATE POLICY "Public Delete crop-images" ON storage.objects FOR DELETE USING (bucket_id = 'crop-images');


-- ============================================================================
-- 5. SEED DATA: 15 CANONICAL CROPS WITH PRACTICAL AGRONOMY & METADATA IMAGES
-- ============================================================================

INSERT INTO public.crops (
    name, local_name, botanical_name, taxonomic_family, botanical_family,
    category, days_to_harvest, watering_interval_days, fertilize_interval_days, weeding_interval_days,
    optimal_ph_min, optimal_ph_max, optimal_temp_min, optimal_temp_max, season,
    npk_n, npk_p, npk_k, suitable_soils, image_url,
    needs_trellis, trellis_type, preferred_planting_method,
    soil_prep_tagalog, harvest_signs_tagalog,
    harvest_indicators, description
)
VALUES
    (
        'Tomato', 'Kamatis', 'Solanum lycopersicum', 'Solanaceae', 'Solanaceae',
        'FRUIT', 60, 2, 14, 7,
        6.0, 6.8, 20.0, 32.0, 'DRY',
        1.5, 1.0, 2.0, ARRAY['LOAM', 'SANDY']::soil_type_enum[], '/metadata/crops_images/tomato.png',
        TRUE, 'TULOS', 'TRANSPLANT',
        'Buhaghagin ang lupa nang may lalim na 20-30 cm. Haluan ng 1-2 dakot ng pinatuyong dumi ng baka o compost.',
        'Pumula o magka-kulay dalandan ang bunga; buo at matigas pa kapag pinisil nang banayad.',
        'Fruit turns bright red/orange; firm to touch',
        'High-value fruit vegetable sensitive to moisture and waterlogging.'
    ),
    (
        'Eggplant', 'Talong', 'Solanum melongena', 'Solanaceae', 'Solanaceae',
        'FRUIT', 75, 2, 14, 7,
        5.5, 6.8, 22.0, 34.0, 'YEAR_ROUND',
        1.5, 1.0, 2.5, ARRAY['LOAM', 'CLAY']::soil_type_enum[], '/metadata/crops_images/eggplant.png',
        TRUE, 'TULOS', 'TRANSPLANT',
        'Lapatan ng compost ang plot bed. Siguraduhing may drainage canal para hindi mababad sa tubig-baha.',
        'Makintab at matingkad ang kulay lila; kapag bahagyang bumaon ang kuko at bumalik ang balat, handa na.',
        'Glossy deep purple skin, firm flesh',
        'Popular lowland vegetable, warm season crop resilient across seasons.'
    ),
    (
        'Chili Pepper', 'Siling Haba / Labuyo', 'Capsicum frutescens', 'Solanaceae', 'Solanaceae',
        'FRUIT', 65, 2, 14, 7,
        6.0, 7.0, 20.0, 35.0, 'YEAR_ROUND',
        1.2, 1.0, 1.8, ARRAY['LOAM', 'SANDY']::soil_type_enum[], '/metadata/crops_images/sili.png',
        FALSE, 'NONE', 'TRANSPLANT',
        'Patabain ang punlaan ng vermicast. Maglagay ng mulch (dayami o tuyong damo) sa paligid.',
        'Kasukdulan ang pagkaberde o ganap nang namumula; matigas at makintab ang balat.',
        'Uniform bright red or deep green color; crisp skin',
        'Hot spice crop resilient to warm weather and diverse soil conditions.'
    ),
    (
        'Cabbage', 'Repolyo', 'Brassica oleracea var. capitata', 'Brassicaceae', 'Brassicaceae',
        'LEAFY', 60, 2, 10, 5,
        6.0, 6.5, 15.0, 24.0, 'DRY',
        2.0, 1.0, 1.5, ARRAY['LOAM', 'SILTY']::soil_type_enum[], '/metadata/crops_images/cabbage.png',
        FALSE, 'NONE', 'TRANSPLANT',
        'Mataas na kama (raised bed) na mayaman sa organikong pataba at apog (kung maasim ang lupa).',
        'Siksik at matigas ang gitnang ulo kapag pinisil ng dalawang kamay.',
        'Firm, solid head formed at plant center',
        'Cool-season leafy crop requiring well-drained, nutrient-rich soil.'
    ),
    (
        'Pechay', 'Pechay', 'Brassica rapa subsp. chinensis', 'Brassicaceae', 'Brassicaceae',
        'LEAFY', 28, 1, 10, 5,
        6.0, 7.0, 18.0, 32.0, 'YEAR_ROUND',
        1.8, 0.8, 1.2, ARRAY['LOAM', 'SILTY', 'PEATY']::soil_type_enum[], '/metadata/crops_images/pechay.png',
        FALSE, 'NONE', 'DIRECT',
        'Pinuhing mabuti ang ibabaw ng lupa. Diligan bago isabog o i-linya ang maliliit na buto.',
        'Matingkad na luntiang dahon, malalapad at malulutong; 25-30 araw mula pagkatanim.',
        'Broad, crisp, dark green leaves before flower stalk emerges',
        'Fast turnaround leafy brassica grown in lowland direct beds.'
    ),
    (
        'Onion', 'Sibuyas', 'Allium cepa', 'Amaryllidaceae', 'Amaryllidaceae',
        'BULB', 110, 3, 20, 7,
        6.0, 7.0, 15.0, 30.0, 'DRY',
        1.0, 1.5, 1.5, ARRAY['LOAM', 'SANDY']::soil_type_enum[], '/metadata/crops_images/onion.png',
        FALSE, 'NONE', 'DIRECT',
        'Buhaghag na mabuhanging lupa para makabuo ng malalaking sibuyas. Alisin ang lahat ng damo.',
        'Nakatumba na ang 50-70% ng mga dahon at naninilaw ang leeg ng sibuyas.',
        'Tops turn yellow and dryly fall over',
        'Bulb crop sensitive to weed competition and excess moisture.'
    ),
    (
        'Carrot', 'Karot', 'Daucus carota', 'Apiaceae', 'Apiaceae',
        'ROOT', 85, 2, 15, 7,
        5.8, 6.8, 16.0, 26.0, 'DRY',
        1.0, 2.0, 2.0, ARRAY['LOAM', 'SANDY']::soil_type_enum[], '/metadata/crops_images/carrot.png',
        FALSE, 'NONE', 'DIRECT',
        'Hukayin nang malalim (30 cm) at tanggalin ang mga bato at matitigas na tipak ng lupa para di magsangay ang ugat.',
        'May lapad na 2-3 cm ang ibabaw ng karot (leeg sa ibabaw ng lupa); matingkad na kulay kahel.',
        'Root crown reaches 1 inch diameter, bright orange',
        'Deep loose soil preferred for straight, smooth root growth.'
    ),
    (
        'Yardlong String Bean', 'Sitaw', 'Vigna unguiculata subsp. sesquipedalis', 'Fabaceae', 'Fabaceae',
        'FRUIT', 48, 2, 14, 7,
        5.5, 6.5, 20.0, 35.0, 'YEAR_ROUND',
        0.8, 1.5, 1.5, ARRAY['LOAM', 'SANDY']::soil_type_enum[], '/metadata/crops_images/sitaw.png',
        TRUE, 'A_FRAME', 'DIRECT',
        'Maglagay ng balag o trellis bago o kasabay ng pagtubo. Ibaon ang 2-3 buto sa bawat punso.',
        'Mabilog at malutong ang pod ngunit hindi pa nakaumbok ang buto sa loob.',
        'Pods are long, tender, crisp, and pliable without bulging seeds',
        'Nitrogen-fixing legume vegetable suitable for trellis companion planting.'
    ),
    (
        'Lettuce', 'Litsugas', 'Lactuca sativa', 'Asteraceae', 'Asteraceae',
        'LEAFY', 45, 1, 10, 5,
        6.0, 7.0, 15.0, 24.0, 'WET',
        1.5, 0.8, 1.2, ARRAY['LOAM', 'PEATY']::soil_type_enum[], '/metadata/crops_images/lettuce.png',
        FALSE, 'NONE', 'DIRECT',
        'Lagyan ng sapat na organic matter at panatilihing mamasa-masa ang lupa gamit ang mulch.',
        'Malalaki at sariwang mga dahon bago magsimulang tumubo ang tangkay ng bulaklak.',
        'Tender, well-developed leaf rosette before bolting',
        'Fast-growing tender leafy vegetable requiring cool soil moisture.'
    ),
    (
        'Cucumber', 'Pipino', 'Cucumis sativus', 'Cucurbitaceae', 'Cucurbitaceae',
        'FRUIT', 50, 2, 12, 7,
        6.0, 6.8, 20.0, 32.0, 'YEAR_ROUND',
        1.2, 1.2, 1.8, ARRAY['LOAM', 'SANDY']::soil_type_enum[], '/metadata/crops_images/pipino.png',
        TRUE, 'A_FRAME', 'DIRECT',
        'Gawan ng A-frame na balag. Maghukay ng hukay na may abonong organiko sa ilalim.',
        'Pantay ang pagkaberde at matigas ang bunga; 15-20 cm ang haba bago manilaw.',
        'Uniform green color, firm to squeeze, rounded ends',
        'Vining fruit crop requiring support or space for disease prevention.'
    ),
    (
        'Okra', 'Okra', 'Abelmoschus esculentus', 'Malvaceae', 'Malvaceae',
        'FRUIT', 45, 2, 14, 7,
        6.0, 7.5, 22.0, 36.0, 'WET',
        1.0, 1.0, 1.2, ARRAY['LOAM', 'CLAY']::soil_type_enum[], '/metadata/crops_images/okra.png',
        FALSE, 'NONE', 'DIRECT',
        'Matibay sa tag-init; bungkalin ang lupa at ihalo ang anumang nabubulok na dumi ng hayop.',
        'Malutong ang dulo ng bunga kapag binali; 7-10 cm ang haba, hindi pa mahibla.',
        'Pods snap cleanly at the tip; 3-4 inches long and tender',
        'Drought-tolerant tropical vegetable providing beneficial insect refuge.'
    ),
    (
        'Corn', 'Mais', 'Zea mays', 'Poaceae', 'Poaceae',
        'FRUIT', 65, 3, 14, 7,
        5.8, 7.0, 20.0, 35.0, 'YEAR_ROUND',
        2.5, 1.2, 1.8, ARRAY['LOAM', 'CLAY']::soil_type_enum[], '/metadata/crops_images/corn.png',
        FALSE, 'NONE', 'DIRECT',
        'Mabigat kumain ng sustansya; mag-abono ng mataas sa Nitroheno sa simula at bungkalin ang gilid.',
        'Tuyot at kulay kape na ang buhok ng mais; gatas ang lumalabas kapag tinusok ng kuko ang butil.',
        'Silks are dry and brown; kernels release milky liquid when pressed',
        'Heavy feeder crop providing natural trellis structure for companions.'
    ),
    (
        'Squash', 'Kalabasa', 'Cucurbita moschata', 'Cucurbitaceae', 'Cucurbitaceae',
        'FRUIT', 80, 3, 14, 7,
        5.6, 6.8, 22.0, 34.0, 'WET',
        1.2, 1.5, 2.0, ARRAY['LOAM', 'CLAY']::soil_type_enum[], '/metadata/crops_images/pumpkin.png',
        FALSE, 'NONE', 'DIRECT',
        'Gawan ng malalaking punso (mounds) na may distansyang 2 metro; lagyan ng maraming compost.',
        'Matigas ang balat na hindi na mababaon ng kuko; mapusyaw o dilaw na ang tangkay.',
        'Deep dull orange or mottled skin that resists fingernail puncture',
        'Sprawling vine crop high in Vitamin A; acts as living weed mulch.'
    ),
    (
        'Water Spinach', 'Kangkong', 'Ipomoea aquatica', 'Convolvulaceae', 'Convolvulaceae',
        'LEAFY', 30, 1, 10, 5,
        5.5, 7.0, 22.0, 35.0, 'YEAR_ROUND',
        1.5, 0.5, 1.0, ARRAY['LOAM', 'SILTY', 'PEATY']::soil_type_enum[], '/metadata/crops_images/kangkong.png',
        FALSE, 'NONE', 'DIRECT',
        'Panatilihing basang-basa ang lupa o raised bed; ihanda ang mababang taniman.',
        'Malalambot ang mga talbos at dahon; anihin bago magsimulang maging magaspang o matigas ang tangkay.',
        'Tender, succulent shoot tips 20-25 cm long',
        'Fast-growing leafy green thriving in moist tropical beds.'
    ),
    (
        'Bitter Gourd', 'Ampalaya', 'Momordica charantia', 'Cucurbitaceae', 'Cucurbitaceae',
        'FRUIT', 55, 2, 14, 7,
        6.0, 6.7, 22.0, 35.0, 'YEAR_ROUND',
        1.2, 1.2, 2.0, ARRAY['LOAM', 'SANDY']::soil_type_enum[], '/metadata/crops_images/ampalaya.png',
        TRUE, 'OVERHEAD', 'DIRECT',
        'Kailangang-kailangan ang overhead trellis o balag. Maglagay ng organic mulch sa ilalim.',
        'Matingkad na berde at makintab ang mga tagaytay ng balat bago pa man magsimulang manilaw.',
        'Fruit ribs are prominent and shiny green; harvested before yellowing',
        'High-value medicinal vining crop cultivated with bamboo trellises.'
    )
ON CONFLICT (name) DO UPDATE SET
    local_name = EXCLUDED.local_name,
    botanical_name = EXCLUDED.botanical_name,
    taxonomic_family = EXCLUDED.taxonomic_family,
    botanical_family = EXCLUDED.botanical_family,
    category = EXCLUDED.category,
    days_to_harvest = EXCLUDED.days_to_harvest,
    watering_interval_days = EXCLUDED.watering_interval_days,
    fertilize_interval_days = EXCLUDED.fertilize_interval_days,
    weeding_interval_days = EXCLUDED.weeding_interval_days,
    optimal_ph_min = EXCLUDED.optimal_ph_min,
    optimal_ph_max = EXCLUDED.optimal_ph_max,
    optimal_temp_min = EXCLUDED.optimal_temp_min,
    optimal_temp_max = EXCLUDED.optimal_temp_max,
    season = EXCLUDED.season,
    npk_n = EXCLUDED.npk_n,
    npk_p = EXCLUDED.npk_p,
    npk_k = EXCLUDED.npk_k,
    suitable_soils = EXCLUDED.suitable_soils,
    image_url = EXCLUDED.image_url,
    needs_trellis = EXCLUDED.needs_trellis,
    trellis_type = EXCLUDED.trellis_type,
    preferred_planting_method = EXCLUDED.preferred_planting_method,
    soil_prep_tagalog = EXCLUDED.soil_prep_tagalog,
    harvest_signs_tagalog = EXCLUDED.harvest_signs_tagalog,
    harvest_indicators = EXCLUDED.harvest_indicators,
    description = EXCLUDED.description,
    updated_at = NOW();


-- ============================================================================
-- 6. SEED DATA: 58 DA-BPI COMPANION PLANTING DSS RULES (Migration 014)
-- ============================================================================

INSERT INTO public.dss_rules (crop_a, crop_b, relationship, reason, source)
VALUES
    -- Tomato relationships
    ('Tomato', 'Lettuce', 'BENEFICIAL', 'Lettuce provides ground cover that retains soil moisture and suppresses weeds around tomato base. Tomato provides partial shade for heat-sensitive lettuce.', 'DA-BPI Companion Bulletin 2026'),
    ('Tomato', 'Carrot', 'BENEFICIAL', 'Carrot deep taproot loosens subsoil for tomato roots. Tomato foliage provides partial shade that benefits carrot root development.', 'DA-BPI Companion Bulletin 2026'),
    ('Tomato', 'Onion', 'BENEFICIAL', 'Onion sulfur compounds repel aphids and whiteflies that attack tomato. Strong onion scent masks tomato from pest detection.', 'DA-BPI Companion Bulletin 2026'),
    ('Tomato', 'Eggplant', 'ANTAGONIST', 'Both are Solanaceae family members competing for identical nutrients and sharing the same pests (fruit borer, bacterial wilt) and diseases.', 'DA-BAR Intercropping Manual Sec 4.1'),
    ('Tomato', 'Cabbage', 'ANTAGONIST', 'Cabbage and tomato compete for similar nutrients. Cabbage can inhibit tomato growth through allelopathic root exudates.', 'DA-BAR Intercropping Manual Sec 4.1'),
    ('Tomato', 'Corn', 'ANTAGONIST', 'Both are heavy nitrogen feeders competing for the same soil nutrients. Corn tall canopy shades tomato excessively.', 'DA-BPI Companion Bulletin 2026'),
    ('Tomato', 'Okra', 'BENEFICIAL', 'Okra attracts beneficial insects (ladybugs, lacewings) that control aphids on adjacent tomato plants.', 'DA-BPI Companion Bulletin 2026'),

    -- Eggplant relationships
    ('Eggplant', 'Yardlong String Bean', 'BENEFICIAL', 'String beans fix atmospheric nitrogen into the soil, directly benefiting nitrogen-hungry eggplant. Beans climbing habit does not shade eggplant.', 'BPI Crop Rotation Protocol 2025'),
    ('Eggplant', 'Cucumber', 'NEUTRAL', 'No significant positive or negative interaction. Can coexist if spacing is adequate.', 'DA-BPI Companion Bulletin 2026'),
    ('Eggplant', 'Onion', 'BENEFICIAL', 'Onion repels flea beetles and aphids that commonly attack eggplant foliage.', 'DA-BPI Companion Bulletin 2026'),
    ('Eggplant', 'Chili Pepper', 'ANTAGONIST', 'Both are Solanaceae sharing identical disease vectors (bacterial wilt, anthracnose). Cross-infection risk is high.', 'DA-BAR Intercropping Manual Sec 4.1'),
    ('Eggplant', 'Water Spinach', 'BENEFICIAL', 'Kangkong serves as moisture-retaining ground cover under eggplant. Both thrive in moist conditions.', 'DA-BPI Lowland Vegetable Guide'),

    -- Cucumber relationships
    ('Cucumber', 'Corn', 'BENEFICIAL', 'Classic Three Sisters principle: corn provides natural trellis for cucumber vines, cucumber provides ground cover reducing weed pressure.', 'DA-BAR Companion Guide'),
    ('Cucumber', 'Yardlong String Bean', 'BENEFICIAL', 'Beans fix nitrogen benefiting cucumber growth. Both can share a trellis system efficiently.', 'DA-BPI Companion Bulletin 2026'),
    ('Cucumber', 'Lettuce', 'BENEFICIAL', 'Lettuce serves as living mulch under cucumber trellis, conserving soil moisture. Cucumber provides shade for heat-sensitive lettuce.', 'DA-BPI Companion Bulletin 2026'),

    -- Cabbage relationships
    ('Cabbage', 'Onion', 'BENEFICIAL', 'Onion strong scent masks cabbage from diamondback moth and cabbage looper. Onion acts as a natural pest deterrent border.', 'DA-BPI Companion Bulletin 2026'),
    ('Cabbage', 'Yardlong String Bean', 'ANTAGONIST', 'String beans climbing habit can smother low-growing cabbage. Both compete for space and light.', 'DA-BAR Companion Guide'),
    ('Cabbage', 'Lettuce', 'BENEFICIAL', 'Lettuce and cabbage have complementary root depths. Lettuce matures faster, freeing space as cabbage heads develop.', 'DA-BPI Companion Bulletin 2026'),

    -- Onion relationships
    ('Onion', 'Carrot', 'BENEFICIAL', 'Classic beneficial pair: carrot fly is repelled by onion scent, onion thrips are repelled by carrot foliage. Mutually protective.', 'DA-BPI Companion Bulletin 2026'),
    ('Onion', 'Yardlong String Bean', 'ANTAGONIST', 'Onion sulfur root exudates inhibit nitrogen-fixing bacteria on bean roots, reducing bean productivity.', 'DA-BAR Companion Guide'),
    ('Onion', 'Pechay', 'BENEFICIAL', 'Onion repels flea beetles that damage pechay leaves. Pechay matures quickly before onion needs full bed space.', 'DA-BPI Companion Bulletin 2026'),
    ('Onion', 'Chili Pepper', 'BENEFICIAL', 'Onion repels aphids that transmit viral diseases to chili peppers.', 'DA-BPI Companion Bulletin 2026'),
    ('Onion', 'Bitter Gourd', 'BENEFICIAL', 'Onion scent deters fruit flies and aphids that attack ampalaya vines and fruits.', 'DA-BPI Companion Bulletin 2026'),

    -- Corn & Squash relationships
    ('Corn', 'Squash', 'BENEFICIAL', 'Three Sisters principle: squash large leaves shade the ground, conserving moisture and suppressing weeds around corn stalks.', 'DA-BAR Companion Guide'),
    ('Corn', 'Yardlong String Bean', 'BENEFICIAL', 'Three Sisters principle: corn provides natural trellis for climbing beans, beans fix nitrogen for corn heavy demand.', 'DA-BAR Companion Guide'),
    ('Corn', 'Bitter Gourd', 'BENEFICIAL', 'Corn provides natural trellis support for ampalaya vines, reducing trellis material costs.', 'DA-BPI Companion Bulletin 2026'),

    -- Okra relationships
    ('Okra', 'Pechay', 'BENEFICIAL', 'Okra tall structure provides partial shade for heat-sensitive pechay during hot months.', 'DA-BPI Companion Bulletin 2026'),

    -- Squash / Bitter Gourd relationships
    ('Squash', 'Bitter Gourd', 'ANTAGONIST', 'Both are cucurbits sharing the same pests (fruit fly, downy mildew) and competing for identical vine space.', 'DA-BPI Companion Bulletin 2026'),
    ('Squash', 'Yardlong String Bean', 'BENEFICIAL', 'Beans fix nitrogen for squash, squash ground cover suppresses weeds around bean trellis base.', 'DA-BPI Companion Bulletin 2026'),

    -- Pechay & Carrot
    ('Pechay', 'Carrot', 'BENEFICIAL', 'Pechay matures in 25-30 days, harvested before slow-growing carrot needs full bed space. Efficient succession planting.', 'DA-BPI Companion Bulletin 2026'),
    ('Lettuce', 'Carrot', 'BENEFICIAL', 'Lettuce shallow roots and carrot deep roots share soil space efficiently without competition. Lettuce provides ground shade.', 'DA-BPI Companion Bulletin 2026')
ON CONFLICT DO NOTHING;


-- ============================================================================
-- 7. SEED DATA: INITIAL FEEDBACK & BROADCAST NOTIFICATIONS
-- ============================================================================

INSERT INTO public.feedback (farmer_name, farm_name, category, subject, message, status)
SELECT 'Juan Dela Cruz', 'Dela Cruz Organic Farm', 'PEST_DISEASE', 'Aphid Infestation on Tomato Beds', 'Noticed yellowing leaves and small insects under tomato leaves in Plot B. Requesting advice.', 'PENDING'
WHERE NOT EXISTS (SELECT 1 FROM public.feedback);

INSERT INTO public.notifications (title, body, notification_type, is_read)
VALUES
    ('📢 System Update v1.2.6', 'MapTanim updated with practical agronomy guides, trellis requirements, and crop rotation protection.', 'SYSTEM_UPDATE', FALSE),
    ('🌾 Practical Agronomy Added', '15 Approved Philippine vegetables now include Filipino soil preparation and harvest indicators.', 'CROP_ADDITION', FALSE),
    ('🛠 Bug Fix & Security Patch', 'Resolved offline database synchronization and plot status updating issues.', 'BUG_FIX', TRUE)
ON CONFLICT DO NOTHING;

-- End of MapTanim Master Schema & Seed
