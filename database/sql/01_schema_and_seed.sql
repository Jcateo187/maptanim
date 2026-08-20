-- MapTanim PostgreSQL Schema & Seed Script for Supabase
-- Target Project: ojilvcglpzbtpjxguhzj.supabase.co

-- ============================================================================
-- 1. ENUM TYPES
-- ============================================================================

DO $$ BEGIN
    CREATE TYPE role_enum AS ENUM ('FARMER', 'ADMINISTRATOR', 'GUEST');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE soil_type_enum AS ENUM ('LOAM', 'CLAY', 'SANDY', 'SILTY', 'PEATY', 'CHALKY');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE season_enum AS ENUM ('DRY', 'WET', 'YEAR_ROUND');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE category_enum AS ENUM ('BULB', 'STEM', 'SHOOT', 'LEAFY', 'FLOWER', 'FRUIT', 'ROOT', 'TUBER');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE task_type_enum AS ENUM ('WATER', 'FERTILIZE', 'HARVEST', 'PEST_ALERT', 'APPLY_PESTICIDE', 'SOIL_AMENDMENT', 'PRUNING', 'OBSERVATION');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE companion_relation_enum AS ENUM ('BENEFICIAL', 'ANTAGONIST', 'NEUTRAL');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- ============================================================================
-- 2. TABLES & RLS POLICIES
-- ============================================================================

-- Table: public.users
CREATE TABLE IF NOT EXISTS public.users (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255)    UNIQUE NOT NULL,
    role            role_enum       NOT NULL DEFAULT 'FARMER',
    avatar_url      TEXT,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- Migrations for existing deployments:
ALTER TABLE public.users DROP COLUMN IF EXISTS full_name;
ALTER TABLE public.users DROP COLUMN IF EXISTS phone_number;
ALTER TABLE public.profiles DROP COLUMN IF EXISTS first_name;
ALTER TABLE public.profiles DROP COLUMN IF EXISTS last_name;

DO $$ BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'beds') THEN
        ALTER TABLE public.beds RENAME TO crop_plots;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'public' AND table_name = 'crop_plots' AND column_name = 'bed_label') THEN
        ALTER TABLE public.crop_plots RENAME COLUMN bed_label TO plot_label;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'public' AND table_name = 'tasks' AND column_name = 'bed_id') THEN
        ALTER TABLE public.tasks RENAME COLUMN bed_id TO plot_id;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'public' AND table_name = 'activities' AND column_name = 'bed_id') THEN
        ALTER TABLE public.activities RENAME COLUMN bed_id TO plot_id;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'public' AND table_name = 'harvest_records' AND column_name = 'bed_id') THEN
        ALTER TABLE public.harvest_records RENAME COLUMN bed_id TO plot_id;
    END IF;
END $$;

ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "users_own_data" ON public.users;
CREATE POLICY "users_own_data" ON public.users
    FOR ALL USING (auth.uid() = id);

DROP POLICY IF EXISTS "users_read_all" ON public.users;
CREATE POLICY "users_read_all" ON public.users
    FOR SELECT USING (true);

-- Table: public.profiles (Queried by ProfileRepository & LoadingViewModel)
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
CREATE POLICY "profiles_select_own" ON public.profiles
    FOR SELECT USING (auth.uid() = id);

DROP POLICY IF EXISTS "profiles_insert_own" ON public.profiles;
CREATE POLICY "profiles_insert_own" ON public.profiles
    FOR INSERT WITH CHECK (auth.uid() = id);

DROP POLICY IF EXISTS "profiles_update_own" ON public.profiles;
CREATE POLICY "profiles_update_own" ON public.profiles
    FOR UPDATE USING (auth.uid() = id);

-- Trigger function to automatically create a profile when a new user signs up in auth.users
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

DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();

-- Backfill profile records for any existing users in auth.users
INSERT INTO public.profiles (id, nickname, onboarding_completed)
SELECT 
    id,
    COALESCE(raw_user_meta_data->>'nickname', split_part(email, '@', 1)),
    FALSE
FROM auth.users
ON CONFLICT (id) DO NOTHING;

-- Table: public.crops (Static Reference Data)
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
CREATE POLICY "crops_read_all" ON public.crops
    FOR SELECT USING (true);

-- Table: public.farms
CREATE TABLE IF NOT EXISTS public.farms (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    farmer_id       UUID            NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    farm_name       VARCHAR(100)    NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);



ALTER TABLE public.farms ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "farmers_own_farms" ON public.farms;
CREATE POLICY "farmers_own_farms" ON public.farms
    FOR ALL USING (auth.uid() = farmer_id);

DROP POLICY IF EXISTS "farms_read_all" ON public.farms;
CREATE POLICY "farms_read_all" ON public.farms
    FOR SELECT USING (true);

-- Table: public.crop_plots (Direct-Planted Crop Plots)
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

-- Table: public.dss_rules (Companion Planting Matrix)
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
CREATE POLICY "dss_rules_read_all" ON public.dss_rules
    FOR SELECT USING (true);

-- Table: public.notifications (System Updates & Admin Announcements)
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

-- Migration block for existing installations
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'public' 
          AND table_name = 'notifications' 
          AND column_name = 'notification_type'
    ) THEN
        ALTER TABLE public.notifications ADD COLUMN notification_type VARCHAR(50) NOT NULL DEFAULT 'SYSTEM_UPDATE';
    END IF;
    
    -- Ensure user_id is nullable for system-wide broadcasts
    ALTER TABLE public.notifications ALTER COLUMN user_id DROP NOT NULL;
END $$;

ALTER TABLE public.notifications ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "users_own_notifications" ON public.notifications;
DROP POLICY IF EXISTS "notifications_read_all" ON public.notifications;
CREATE POLICY "notifications_read_all" ON public.notifications
    FOR SELECT USING (user_id IS NULL OR auth.uid() = user_id);

-- ============================================================================
-- 3. SEED DATA (High-Value Vegetables & Companion Matrix)
-- ============================================================================

INSERT INTO public.crops (name, local_name, botanical_name, category, days_to_harvest, optimal_ph_min, optimal_ph_max, season, suitable_soils, description)
VALUES
    ('Tomato', 'Kamatis', 'Solanum lycopersicum', 'FRUIT', 70, 6.0, 6.8, 'DRY', ARRAY['LOAM', 'SANDY']::soil_type_enum[], 'High-value fruit vegetable sensitive to moisture.'),
    ('Eggplant', 'Talong', 'Solanum melongena', 'FRUIT', 75, 5.5, 6.8, 'YEAR_ROUND', ARRAY['LOAM', 'CLAY']::soil_type_enum[], 'Popular lowland vegetable, warm season crop.'),
    ('Bell Pepper', 'Siling Pula', 'Capsicum annuum', 'FRUIT', 80, 6.0, 7.0, 'DRY', ARRAY['LOAM']::soil_type_enum[], 'Requires well-drained fertile soil.'),
    ('Cabbage', 'Repolyo', 'Brassica oleracea var. capitata', 'LEAFY', 90, 6.0, 6.5, 'DRY', ARRAY['LOAM', 'SILTY']::soil_type_enum[], 'Cool-season leafy crop.'),
    ('Onion', 'Sibuyas', 'Allium cepa', 'BULB', 110, 6.0, 7.0, 'DRY', ARRAY['LOAM', 'SANDY']::soil_type_enum[], 'Bulb crop sensitive to weed competition.'),
    ('Carrot', 'Karot', 'Daucus carota', 'ROOT', 75, 5.8, 6.8, 'DRY', ARRAY['LOAM', 'SANDY']::soil_type_enum[], 'Deep loose soil preferred for smooth root growth.'),
    ('String Beans', 'Sitaw', 'Vigna unguiculata subsp. sesquipedalis', 'FRUIT', 55, 5.5, 6.5, 'YEAR_ROUND', ARRAY['LOAM']::soil_type_enum[], 'Nitrogen-fixing legume vegetable.'),
    ('Lettuce', 'Litsugas', 'Lactuca sativa', 'LEAFY', 50, 6.0, 7.0, 'WET', ARRAY['LOAM', 'PEATY']::soil_type_enum[], 'Fast-growing tender leafy vegetable.'),
    ('Cucumber', 'Pipino', 'Cucumis sativus', 'FRUIT', 60, 6.0, 6.8, 'YEAR_ROUND', ARRAY['LOAM']::soil_type_enum[], 'Vining fruit crop requiring support or space.'),
    ('Okra', 'Okra', 'Abelmoschus esculentus', 'FRUIT', 55, 6.0, 7.5, 'WET', ARRAY['LOAM', 'CLAY']::soil_type_enum[], 'Drought-tolerant tropical vegetable.'),
    ('Corn', 'Mais', 'Zea mays', 'FRUIT', 85, 5.8, 7.0, 'YEAR_ROUND', ARRAY['LOAM']::soil_type_enum[], 'Heavy feeder crop, good support structure.'),
    ('Squash', 'Kalabasa', 'Cucurbita moschata', 'FRUIT', 80, 5.6, 6.8, 'WET', ARRAY['LOAM', 'CLAY']::soil_type_enum[], 'Sprawling vine crop high in Vitamin A.'),
    ('Kangkong', 'Kangkong', 'Ipomoea aquatica', 'LEAFY', 35, 5.3, 7.0, 'YEAR_ROUND', ARRAY['LOAM', 'SILTY', 'CLAY']::soil_type_enum[], 'Water spinach, fast growing leafy green.')
ON CONFLICT (name) DO NOTHING;

INSERT INTO public.dss_rules (crop_a, crop_b, relationship, reason, source)
VALUES
    ('Tomato', 'Lettuce', 'BENEFICIAL', 'Lettuce shades roots, reduces moisture loss', 'BPI Guidelines'),
    ('Tomato', 'Eggplant', 'ANTAGONIST', 'Same family, shared pests (fruit borer)', 'DA-BAR Companion Guide'),
    ('Tomato', 'Carrot', 'BENEFICIAL', 'Carrot aerates soil around tomato roots', 'BPI Guidelines'),
    ('Cucumber', 'Corn', 'BENEFICIAL', 'Corn provides climbing support', 'DA-BAR Companion Guide'),
    ('Cucumber', 'Potato', 'ANTAGONIST', 'Compete for nutrients, attract same blight', 'BPI Guidelines'),
    ('Eggplant', 'String Beans', 'BENEFICIAL', 'Beans fix nitrogen for eggplant', 'DA-BAR Companion Guide'),
    ('Cabbage', 'Onion', 'BENEFICIAL', 'Onion repels cabbage loopers', 'BPI Guidelines'),
    ('Lettuce', 'Carrot', 'BENEFICIAL', 'Companion harvest timing aligned', 'BPI Guidelines'),
    ('Onion', 'String Beans', 'ANTAGONIST', 'Onion inhibits bean growth', 'DA-BAR Companion Guide')
ON CONFLICT DO NOTHING;

INSERT INTO public.notifications (title, body, notification_type, is_read)
VALUES
    ('📢 System Update v1.2.0', 'MapTanim Admin deployed direct-to-soil grid performance optimizations and sync upgrades.', 'SYSTEM_UPDATE', FALSE),
    ('🌾 New Crop Added: Sweet Corn', 'Admin added Sweet Corn (Zea mays) to the crop planting library. Tap to view growth stages.', 'CROP_ADDITION', FALSE),
    ('🛠 Bug Fix & Security Patch', 'Resolved offline database synchronization and plot status updating issues.', 'BUG_FIX', TRUE)
ON CONFLICT DO NOTHING;

-- Table: public.community_posts
CREATE TABLE IF NOT EXISTS public.community_posts (
    id                  TEXT            PRIMARY KEY DEFAULT ('post_' || substr(md5(random()::text || clock_timestamp()::text), 1, 16)),
    author_id           UUID            REFERENCES auth.users(id) ON DELETE SET NULL,
    author_name         VARCHAR(150)    NOT NULL DEFAULT 'Mobile Farmer',
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

-- Table: public.community_comments
CREATE TABLE IF NOT EXISTS public.community_comments (
    id                  TEXT            PRIMARY KEY DEFAULT ('comm_' || substr(md5(random()::text || clock_timestamp()::text), 1, 16)),
    post_id             TEXT            NOT NULL REFERENCES public.community_posts(id) ON DELETE CASCADE,
    author_id           UUID            REFERENCES auth.users(id) ON DELETE SET NULL,
    author_name         VARCHAR(150)    NOT NULL DEFAULT 'Farmer Partner',
    author_avatar_url   TEXT,
    content             TEXT            NOT NULL,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

ALTER TABLE public.community_posts ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.community_comments ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "community_posts_select_all" ON public.community_posts;
CREATE POLICY "community_posts_select_all" ON public.community_posts FOR SELECT USING (true);
DROP POLICY IF EXISTS "community_posts_insert_all" ON public.community_posts;
CREATE POLICY "community_posts_insert_all" ON public.community_posts FOR INSERT WITH CHECK (true);
DROP POLICY IF EXISTS "community_posts_update_all" ON public.community_posts;
CREATE POLICY "community_posts_update_all" ON public.community_posts FOR UPDATE USING (true);
DROP POLICY IF EXISTS "community_posts_delete_all" ON public.community_posts;
CREATE POLICY "community_posts_delete_all" ON public.community_posts FOR DELETE USING (true);

DROP POLICY IF EXISTS "community_comments_select_all" ON public.community_comments;
CREATE POLICY "community_comments_select_all" ON public.community_comments FOR SELECT USING (true);
DROP POLICY IF EXISTS "community_comments_insert_all" ON public.community_comments;
CREATE POLICY "community_comments_insert_all" ON public.community_comments FOR INSERT WITH CHECK (true);
DROP POLICY IF EXISTS "community_comments_update_all" ON public.community_comments;
CREATE POLICY "community_comments_update_all" ON public.community_comments FOR UPDATE USING (true);
DROP POLICY IF EXISTS "community_comments_delete_all" ON public.community_comments;
CREATE POLICY "community_comments_delete_all" ON public.community_comments FOR DELETE USING (true);

INSERT INTO public.community_posts (id, author_name, category, title, content, likes_count, comments_count, is_pinned, tags, created_at)
VALUES
    ('post_1', 'Mang Jose Parreño', 'PEST_ALERT', '🚨 Fall Armyworm Outbreak in Murcia & Talisay Bed Plots', 'Attention fellow vegetable growers! We spotted Fall Armyworm caterpillars on early sweet corn and bean plots around Barangay Canlandog, Murcia. Spraying Neem oil extract mixed with soapy water early morning has proven effective. Check your leaves for tiny hole punctures!', 18, 2, true, ARRAY['PestAlert', 'Armyworm', 'Corn', 'Murcia'], NOW() - INTERVAL '2 hours'),
    ('post_2', 'Ka Ryan Vasquez', 'FARMING_TIP', '💡 High-Yield Tomato Diamante Max F1 Double A-Frame Trellising', 'For those planting Diamante Max F1 tomato this dry season, using a 2-meter bamboo A-frame trellis with nylon twine stringing doubled our yield harvest compared to single stake poles. It provides superior airflow and keeps lower branches off damp ground.', 24, 1, false, ARRAY['FarmingTip', 'Tomato', 'Trellis', 'HighYield'], NOW() - INTERVAL '5 hours'),
    ('post_3', 'Aling Maria Juanillo', 'EQUIPMENT', '🚜 Bamboo Stakes & Insect Netting Seed Swap — Extra Sitaw Seeds', 'I have 50 extra bundles of treated 6ft bamboo stakes and 3 packets of certified Sitaw (String Beans) seeds available for trade in Silay. Looking to trade for surplus Pechay or Lettuce seeds. Send me a message!', 12, 0, false, ARRAY['SeedSwap', 'BambooStakes', 'Sitaw', 'Silay'], NOW() - INTERVAL '1 day'),
    ('post_4', 'Tatay Juan Cateo', 'GENERAL', '❓ Best Organic Solution for Flea Beetles on Talong Leaves?', 'Magandang araw mga kasama. My 40-day old Eggplant (Talong) plot is starting to show small pinhole damage from flea beetles. Is baking soda spray or wood ash dusting better for organic pest control without burning young leaves?', 9, 1, false, ARRAY['Question', 'Eggplant', 'OrganicPestControl', 'Talong'], NOW() - INTERVAL '2 days')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.community_comments (id, post_id, author_name, content, created_at)
VALUES
    ('comm_1', 'post_1', 'Aling Danica', 'Salamat sa babala Mang Jose! Applied wood ash around our corn whorls this morning, so far it contained the spread.', NOW() - INTERVAL '1 hour'),
    ('comm_2', 'post_1', 'Jason B.', 'You can also release Trichogramma parasitic wasps from the BPI office to control egg clusters naturally.', NOW() - INTERVAL '45 minutes'),
    ('comm_3', 'post_2', 'James C.', 'Tested this A-frame method on plot 3 last week! Stems are upright even after heavy afternoon wind.', NOW() - INTERVAL '3 hours'),
    ('comm_4', 'post_4', 'Ka Ryan Vasquez', 'Wood ash mixed with dry sand (1:1 ratio) dusted lightly early morning while dew is present works best against flea beetles!', NOW() - INTERVAL '1 day')
ON CONFLICT (id) DO NOTHING;


-- Table: public.community_reports
CREATE TABLE IF NOT EXISTS public.community_reports (
    id                  TEXT            PRIMARY KEY DEFAULT ('rep_' || substr(md5(random()::text || clock_timestamp()::text), 1, 16)),
    reporter_id         UUID            REFERENCES auth.users(id) ON DELETE SET NULL,
    reporter_name       VARCHAR(150)    NOT NULL DEFAULT 'Farmer Member',
    target_type         VARCHAR(50)     NOT NULL,
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

INSERT INTO public.community_reports (id, reporter_name, target_type, target_id, target_name, target_content, reason, details, status, created_at)
VALUES
    ('rep_1', 'Ka Ryan Vasquez', 'POST', 'post_3', 'Aling Maria Juanillo', '🚜 Bamboo Stakes & Insect Netting Seed Swap — Extra Sitaw Seeds', 'Spam / Commercial Selling', 'Selling untreated seeds without phytosanitary clearance or certified label.', 'PENDING', NOW() - INTERVAL '3 hours'),
    ('rep_2', 'Farmer Partner', 'USER', 'james', 'Farmer James', 'Farmer James direct messaging unsolicited links in community chat.', 'Harassment / Unsolicited Direct Messaging', 'Sent repetitive unsolicited promotional messages in direct chat.', 'PENDING', NOW() - INTERVAL '1 day')
ON CONFLICT (id) DO NOTHING;

