-- ==============================================================================
-- Migration 013: Crop Lifecycle Schema
-- Admin-managed Crop Profiles → Isometric Tile Grid → Drag-Drop Planting →
-- Monitoring → Harvest
--
-- Auth Model:
--   • Admin (web): Vercel env-variable auth, uses Supabase service_role key
--     for crop_profiles CRUD — NO row in auth.users
--   • Farmer (mobile): Supabase Auth (Email/OTP), auth.uid() drives all RLS
--
-- Target: Supabase PostgreSQL (public schema)
-- ==============================================================================

-- ============================================================================
-- 1. NEW ENUMS
-- ============================================================================

-- 6 growth stages matching AGENTS.md specification
DO $$ BEGIN
    CREATE TYPE growth_stage_enum AS ENUM (
        'GERMINATION',
        'SEEDLING',
        'VEGETATIVE',
        'FLOWERING',
        'RIPENING',
        'HARVEST'
    );
EXCEPTION WHEN duplicate_object THEN null;
END $$;

-- Tile lifecycle statuses
DO $$ BEGIN
    CREATE TYPE tile_status_enum AS ENUM (
        'EMPTY',
        'PLANTED',
        'GROWING',
        'READY_TO_HARVEST',
        'HARVESTED',
        'FALLOW'
    );
EXCEPTION WHEN duplicate_object THEN null;
END $$;

-- ============================================================================
-- 2. TABLE: public.crop_profiles
-- Admin-managed enrichment layer on top of public.crops reference data.
-- Contains full agronomic details, growth stage durations, and planting guides.
-- Images use external URLs (free) — no Supabase Storage buckets.
-- ============================================================================

CREATE TABLE IF NOT EXISTS public.crop_profiles (
    id                      TEXT PRIMARY KEY DEFAULT ('cprf_' || substr(md5(random()::text || clock_timestamp()::text), 1, 12)),
    crop_id                 TEXT NOT NULL,
    -- Configurable duration (in days) for each of the 6 growth stages
    -- Format: {"GERMINATION": 10, "SEEDLING": 15, "VEGETATIVE": 20, "FLOWERING": 15, "RIPENING": 10, "HARVEST": 0}
    growth_stage_durations  JSONB NOT NULL DEFAULT '{}',
    planting_instructions   TEXT,
    pest_risks              TEXT,
    fertilizer_schedule     TEXT,
    watering_guide          TEXT,
    -- External image URLs (free hosting, no Supabase Storage)
    image_urls              TEXT[] NOT NULL DEFAULT '{}',
    thumbnail_url           TEXT,
    -- Admin tracking
    created_by_admin        VARCHAR(100) NOT NULL DEFAULT 'System Administrator',
    is_published            BOOLEAN NOT NULL DEFAULT FALSE,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Add FK constraint if crops table exists (soft reference via crop_id TEXT)
-- Using TEXT FK to match migration 012 pattern where IDs are TEXT

ALTER TABLE public.crop_profiles ENABLE ROW LEVEL SECURITY;

-- Everyone can read published crop profiles
DROP POLICY IF EXISTS "crop_profiles_select_all" ON public.crop_profiles;
CREATE POLICY "crop_profiles_select_all" ON public.crop_profiles
    FOR SELECT USING (true);

-- Only service_role (admin web with service key) can insert/update/delete
DROP POLICY IF EXISTS "crop_profiles_admin_insert" ON public.crop_profiles;
CREATE POLICY "crop_profiles_admin_insert" ON public.crop_profiles
    FOR INSERT WITH CHECK (
        current_setting('role', true) = 'service_role'
    );

DROP POLICY IF EXISTS "crop_profiles_admin_update" ON public.crop_profiles;
CREATE POLICY "crop_profiles_admin_update" ON public.crop_profiles
    FOR UPDATE USING (
        current_setting('role', true) = 'service_role'
    );

DROP POLICY IF EXISTS "crop_profiles_admin_delete" ON public.crop_profiles;
CREATE POLICY "crop_profiles_admin_delete" ON public.crop_profiles
    FOR DELETE USING (
        current_setting('role', true) = 'service_role'
    );

-- ============================================================================
-- 3. TABLE: public.farm_tiles
-- Isometric grid tiles (45×45 per farm). Each tile is a placeable cell
-- on the 2D isometric farm canvas. Farmers drag crops onto tiles.
-- ============================================================================

CREATE TABLE IF NOT EXISTS public.farm_tiles (
    id              TEXT PRIMARY KEY DEFAULT ('tile_' || substr(md5(random()::text || clock_timestamp()::text), 1, 12)),
    farm_id         TEXT NOT NULL,
    -- Grid coordinates within the 45×45 isometric grid
    grid_x          INT NOT NULL DEFAULT 0,
    grid_y          INT NOT NULL DEFAULT 0,
    status          VARCHAR(30) NOT NULL DEFAULT 'EMPTY',
    -- FK to the crop currently occupying this tile (NULL if empty)
    current_crop_id TEXT,
    -- Optional label for farmer reference
    tile_label      VARCHAR(50),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Each grid position is unique per farm
    CONSTRAINT farm_tile_unique_position UNIQUE (farm_id, grid_x, grid_y)
);

ALTER TABLE public.farm_tiles ENABLE ROW LEVEL SECURITY;

-- Farmer owns tiles through farm ownership
DROP POLICY IF EXISTS "farm_tiles_select" ON public.farm_tiles;
CREATE POLICY "farm_tiles_select" ON public.farm_tiles
    FOR SELECT USING (
        EXISTS (
            SELECT 1 FROM public.farms f
            WHERE f.id::text = farm_tiles.farm_id::text AND f.farmer_id::text = auth.uid()::text
        )
    );

DROP POLICY IF EXISTS "farm_tiles_insert" ON public.farm_tiles;
CREATE POLICY "farm_tiles_insert" ON public.farm_tiles
    FOR INSERT WITH CHECK (
        EXISTS (
            SELECT 1 FROM public.farms f
            WHERE f.id::text = farm_tiles.farm_id::text AND f.farmer_id::text = auth.uid()::text
        )
    );

DROP POLICY IF EXISTS "farm_tiles_update" ON public.farm_tiles;
CREATE POLICY "farm_tiles_update" ON public.farm_tiles
    FOR UPDATE USING (
        EXISTS (
            SELECT 1 FROM public.farms f
            WHERE f.id::text = farm_tiles.farm_id::text AND f.farmer_id::text = auth.uid()::text
        )
    );

DROP POLICY IF EXISTS "farm_tiles_delete" ON public.farm_tiles;
CREATE POLICY "farm_tiles_delete" ON public.farm_tiles
    FOR DELETE USING (
        EXISTS (
            SELECT 1 FROM public.farms f
            WHERE f.id::text = farm_tiles.farm_id::text AND f.farmer_id::text = auth.uid()::text
        )
    );

-- Admin service_role can read all tiles for monitoring
DROP POLICY IF EXISTS "farm_tiles_admin_select" ON public.farm_tiles;
CREATE POLICY "farm_tiles_admin_select" ON public.farm_tiles
    FOR SELECT USING (
        current_setting('role', true) = 'service_role'
    );

-- ============================================================================
-- 4. TABLE: public.tile_plantings
-- Records each crop placement on a tile (the drag-drop action from crop tray).
-- Crops are resizable — width_m and height_m track the placed size.
-- Tracks growth stage progression through the 6-stage lifecycle.
-- ============================================================================

CREATE TABLE IF NOT EXISTS public.tile_plantings (
    id                      TEXT PRIMARY KEY DEFAULT ('plnt_' || substr(md5(random()::text || clock_timestamp()::text), 1, 12)),
    tile_id                 TEXT NOT NULL REFERENCES public.farm_tiles(id) ON DELETE CASCADE,
    crop_id                 TEXT NOT NULL,
    crop_name               VARCHAR(100) NOT NULL,
    crop_variety            VARCHAR(100),
    -- Resizable crop dimensions on the tile
    width_m                 FLOAT NOT NULL DEFAULT 1.0,
    height_m                FLOAT NOT NULL DEFAULT 1.0,
    -- Position offset within tile (for precise placement)
    offset_x                FLOAT NOT NULL DEFAULT 0.0,
    offset_y                FLOAT NOT NULL DEFAULT 0.0,
    -- Growth lifecycle tracking (6 stages)
    current_stage           VARCHAR(30) NOT NULL DEFAULT 'GERMINATION',
    stage_changed_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    planted_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expected_harvest_date   DATE,
    -- Crop profile reference for growth durations
    crop_profile_id         TEXT,
    -- Status
    is_active               BOOLEAN NOT NULL DEFAULT TRUE,
    notes                   TEXT,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE public.tile_plantings ENABLE ROW LEVEL SECURITY;

-- Farmer owns plantings through tile → farm ownership chain
DROP POLICY IF EXISTS "tile_plantings_select" ON public.tile_plantings;
CREATE POLICY "tile_plantings_select" ON public.tile_plantings
    FOR SELECT USING (
        EXISTS (
            SELECT 1 FROM public.farm_tiles t
            JOIN public.farms f ON f.id::text = t.farm_id::text
            WHERE t.id::text = tile_plantings.tile_id::text AND f.farmer_id::text = auth.uid()::text
        )
    );

DROP POLICY IF EXISTS "tile_plantings_insert" ON public.tile_plantings;
CREATE POLICY "tile_plantings_insert" ON public.tile_plantings
    FOR INSERT WITH CHECK (
        EXISTS (
            SELECT 1 FROM public.farm_tiles t
            JOIN public.farms f ON f.id::text = t.farm_id::text
            WHERE t.id::text = tile_plantings.tile_id::text AND f.farmer_id::text = auth.uid()::text
        )
    );

DROP POLICY IF EXISTS "tile_plantings_update" ON public.tile_plantings;
CREATE POLICY "tile_plantings_update" ON public.tile_plantings
    FOR UPDATE USING (
        EXISTS (
            SELECT 1 FROM public.farm_tiles t
            JOIN public.farms f ON f.id::text = t.farm_id::text
            WHERE t.id::text = tile_plantings.tile_id::text AND f.farmer_id::text = auth.uid()::text
        )
    );

DROP POLICY IF EXISTS "tile_plantings_delete" ON public.tile_plantings;
CREATE POLICY "tile_plantings_delete" ON public.tile_plantings
    FOR DELETE USING (
        EXISTS (
            SELECT 1 FROM public.farm_tiles t
            JOIN public.farms f ON f.id::text = t.farm_id::text
            WHERE t.id::text = tile_plantings.tile_id::text AND f.farmer_id::text = auth.uid()::text
        )
    );

-- Admin service_role read access
DROP POLICY IF EXISTS "tile_plantings_admin_select" ON public.tile_plantings;
CREATE POLICY "tile_plantings_admin_select" ON public.tile_plantings
    FOR SELECT USING (
        current_setting('role', true) = 'service_role'
    );

-- ============================================================================
-- 5. TABLE: public.planting_monitors
-- Monitoring/observation logs for an active planting. Farmers record
-- watering, fertilizing, pest checks, soil amendments, pruning, etc.
-- ============================================================================

CREATE TABLE IF NOT EXISTS public.planting_monitors (
    id              TEXT PRIMARY KEY DEFAULT ('mon_' || substr(md5(random()::text || clock_timestamp()::text), 1, 12)),
    planting_id     TEXT NOT NULL REFERENCES public.tile_plantings(id) ON DELETE CASCADE,
    -- Denormalized crop reference for direct filtering by soil type / season / category / crop_id
    -- (avoids 3-table join: monitor → planting → crops)
    crop_id         TEXT NOT NULL,
    crop_name       VARCHAR(100) NOT NULL,
    crop_variety    VARCHAR(100),
    -- Reuses existing task_type_enum: WATER, FERTILIZE, PEST_ALERT, etc.
    monitor_type    VARCHAR(30) NOT NULL,
    value           FLOAT,
    unit            VARCHAR(20),
    notes           TEXT,
    -- Today's Tasks integration
    due_date        DATE,
    is_completed    BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at    TIMESTAMPTZ,
    recorded_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE public.planting_monitors ENABLE ROW LEVEL SECURITY;

-- Farmer owns monitors through planting → tile → farm ownership chain
DROP POLICY IF EXISTS "planting_monitors_select" ON public.planting_monitors;
CREATE POLICY "planting_monitors_select" ON public.planting_monitors
    FOR SELECT USING (
        EXISTS (
            SELECT 1 FROM public.tile_plantings p
            JOIN public.farm_tiles t ON t.id::text = p.tile_id::text
            JOIN public.farms f ON f.id::text = t.farm_id::text
            WHERE p.id::text = planting_monitors.planting_id::text AND f.farmer_id::text = auth.uid()::text
        )
    );

DROP POLICY IF EXISTS "planting_monitors_insert" ON public.planting_monitors;
CREATE POLICY "planting_monitors_insert" ON public.planting_monitors
    FOR INSERT WITH CHECK (
        EXISTS (
            SELECT 1 FROM public.tile_plantings p
            JOIN public.farm_tiles t ON t.id::text = p.tile_id::text
            JOIN public.farms f ON f.id::text = t.farm_id::text
            WHERE p.id::text = planting_monitors.planting_id::text AND f.farmer_id::text = auth.uid()::text
        )
    );

DROP POLICY IF EXISTS "planting_monitors_update" ON public.planting_monitors;
CREATE POLICY "planting_monitors_update" ON public.planting_monitors
    FOR UPDATE USING (
        EXISTS (
            SELECT 1 FROM public.tile_plantings p
            JOIN public.farm_tiles t ON t.id::text = p.tile_id::text
            JOIN public.farms f ON f.id::text = t.farm_id::text
            WHERE p.id::text = planting_monitors.planting_id::text AND f.farmer_id::text = auth.uid()::text
        )
    );

DROP POLICY IF EXISTS "planting_monitors_delete" ON public.planting_monitors;
CREATE POLICY "planting_monitors_delete" ON public.planting_monitors
    FOR DELETE USING (
        EXISTS (
            SELECT 1 FROM public.tile_plantings p
            JOIN public.farm_tiles t ON t.id::text = p.tile_id::text
            JOIN public.farms f ON f.id::text = t.farm_id::text
            WHERE p.id::text = planting_monitors.planting_id::text AND f.farmer_id::text = auth.uid()::text
        )
    );

-- Admin service_role read access
DROP POLICY IF EXISTS "planting_monitors_admin_select" ON public.planting_monitors;
CREATE POLICY "planting_monitors_admin_select" ON public.planting_monitors
    FOR SELECT USING (
        current_setting('role', true) = 'service_role'
    );

-- ============================================================================
-- 6. TABLE: public.planting_harvests
-- Harvest records tied to a specific tile planting. Captures yield,
-- quality, and duration data when a crop completes its lifecycle.
-- ============================================================================

CREATE TABLE IF NOT EXISTS public.planting_harvests (
    id              TEXT PRIMARY KEY DEFAULT ('pharv_' || substr(md5(random()::text || clock_timestamp()::text), 1, 12)),
    planting_id     TEXT NOT NULL REFERENCES public.tile_plantings(id) ON DELETE CASCADE,
    crop_name       VARCHAR(100) NOT NULL,
    crop_variety    VARCHAR(100),
    yield_kg        FLOAT NOT NULL DEFAULT 0.0,
    yield_units     INT,
    quality_grade   VARCHAR(20) DEFAULT 'Grade A',
    harvest_date    DATE NOT NULL DEFAULT CURRENT_DATE,
    growing_days    INT,
    notes           TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE public.planting_harvests ENABLE ROW LEVEL SECURITY;

-- Farmer owns harvests through planting → tile → farm ownership chain
DROP POLICY IF EXISTS "planting_harvests_select" ON public.planting_harvests;
CREATE POLICY "planting_harvests_select" ON public.planting_harvests
    FOR SELECT USING (
        EXISTS (
            SELECT 1 FROM public.tile_plantings p
            JOIN public.farm_tiles t ON t.id::text = p.tile_id::text
            JOIN public.farms f ON f.id::text = t.farm_id::text
            WHERE p.id::text = planting_harvests.planting_id::text AND f.farmer_id::text = auth.uid()::text
        )
    );

DROP POLICY IF EXISTS "planting_harvests_insert" ON public.planting_harvests;
CREATE POLICY "planting_harvests_insert" ON public.planting_harvests
    FOR INSERT WITH CHECK (
        EXISTS (
            SELECT 1 FROM public.tile_plantings p
            JOIN public.farm_tiles t ON t.id::text = p.tile_id::text
            JOIN public.farms f ON f.id::text = t.farm_id::text
            WHERE p.id::text = planting_harvests.planting_id::text AND f.farmer_id::text = auth.uid()::text
        )
    );

DROP POLICY IF EXISTS "planting_harvests_update" ON public.planting_harvests;
CREATE POLICY "planting_harvests_update" ON public.planting_harvests
    FOR UPDATE USING (
        EXISTS (
            SELECT 1 FROM public.tile_plantings p
            JOIN public.farm_tiles t ON t.id::text = p.tile_id::text
            JOIN public.farms f ON f.id::text = t.farm_id::text
            WHERE p.id::text = planting_harvests.planting_id::text AND f.farmer_id::text = auth.uid()::text
        )
    );

DROP POLICY IF EXISTS "planting_harvests_delete" ON public.planting_harvests;
CREATE POLICY "planting_harvests_delete" ON public.planting_harvests
    FOR DELETE USING (
        EXISTS (
            SELECT 1 FROM public.tile_plantings p
            JOIN public.farm_tiles t ON t.id::text = p.tile_id::text
            JOIN public.farms f ON f.id::text = t.farm_id::text
            WHERE p.id::text = planting_harvests.planting_id::text AND f.farmer_id::text = auth.uid()::text
        )
    );

-- Admin service_role read access
DROP POLICY IF EXISTS "planting_harvests_admin_select" ON public.planting_harvests;
CREATE POLICY "planting_harvests_admin_select" ON public.planting_harvests
    FOR SELECT USING (
        current_setting('role', true) = 'service_role'
    );

-- ============================================================================
-- 7. INDEXES for query performance
-- ============================================================================

CREATE INDEX IF NOT EXISTS idx_farm_tiles_farm_id ON public.farm_tiles(farm_id);
CREATE INDEX IF NOT EXISTS idx_farm_tiles_position ON public.farm_tiles(farm_id, grid_x, grid_y);
CREATE INDEX IF NOT EXISTS idx_tile_plantings_tile_id ON public.tile_plantings(tile_id);
CREATE INDEX IF NOT EXISTS idx_tile_plantings_crop_id ON public.tile_plantings(crop_id);
CREATE INDEX IF NOT EXISTS idx_tile_plantings_active ON public.tile_plantings(is_active) WHERE is_active = TRUE;
CREATE INDEX IF NOT EXISTS idx_planting_monitors_planting_id ON public.planting_monitors(planting_id);
-- Monitoring: filter by crop (for soil type / season side nav)
CREATE INDEX IF NOT EXISTS idx_planting_monitors_crop_id ON public.planting_monitors(crop_id);
-- Today's Tasks: uncompleted tasks due today
CREATE INDEX IF NOT EXISTS idx_planting_monitors_due_date ON public.planting_monitors(due_date) WHERE is_completed = FALSE;
CREATE INDEX IF NOT EXISTS idx_planting_monitors_crop_tasks ON public.planting_monitors(crop_id, due_date) WHERE is_completed = FALSE;
CREATE INDEX IF NOT EXISTS idx_planting_harvests_planting_id ON public.planting_harvests(planting_id);
CREATE INDEX IF NOT EXISTS idx_crop_profiles_crop_id ON public.crop_profiles(crop_id);
