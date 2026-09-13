-- ==============================================================================
-- Migration 020: Schema Consolidation & Redundant Table Cleanup
-- Target: Supabase PostgreSQL (public schema)
-- 
-- Rationale:
-- 1. `farm_tiles`: Isometric 45x45 grid is procedurally drawn on the frontend canvas
--    (FarmCanvasRenderer.renderGridOverlay). Storing 2,025 empty rows per farm in Postgres
--    is redundant. Only placed crops on the grid are persisted in `crop_plots`.
-- 2. `farm_objects`: Scenery (trees, rocks, flowers, fences) is rendered purely via
--    frontend background image layers, making static object rows unnecessary.
-- 3. `tile_plantings`, `planting_monitors`, `planting_harvests`, `crop_profiles`:
--    Experimental duplicate schema from Migration 013. The active mobile app and admin
--    dashboard already use `crop_plots`, `tasks`, `harvest_records`, and `crops`.
-- ==============================================================================

-- 1. Drop redundant / obsolete tables if they exist
DROP TABLE IF EXISTS public.planting_monitors CASCADE;
DROP TABLE IF EXISTS public.planting_harvests CASCADE;
DROP TABLE IF EXISTS public.tile_plantings CASCADE;
DROP TABLE IF EXISTS public.farm_tiles CASCADE;
DROP TABLE IF EXISTS public.crop_profiles CASCADE;
DROP TABLE IF EXISTS public.farm_objects CASCADE;

-- 2. Ensure public.harvest_records schema is completely aligned with mobile & admin
CREATE TABLE IF NOT EXISTS public.harvest_records (
    id                      TEXT PRIMARY KEY DEFAULT ('harv_' || substr(md5(random()::text || clock_timestamp()::text), 1, 8)),
    farm_id                 TEXT NOT NULL,
    plot_id                 TEXT,
    farm_name               VARCHAR(100) DEFAULT 'MapTanim Main Farm',
    plot_label              VARCHAR(50) DEFAULT 'Plot 1',
    crop_name               VARCHAR(100) NOT NULL,
    crop_variety            VARCHAR(100),
    planted_date            TEXT,
    harvested_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    harvested_date          TEXT,
    growing_duration_days   INT DEFAULT 0,
    yield_kg                FLOAT NOT NULL DEFAULT 0.0,
    quality_grade           VARCHAR(20) DEFAULT 'Grade A',
    quality_rating          INT DEFAULT 5,
    notes                   TEXT,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Ensure columns exist in case harvest_records already existed
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS farm_name VARCHAR(100) DEFAULT 'MapTanim Main Farm';
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS plot_label VARCHAR(50) DEFAULT 'Plot 1';
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS crop_variety VARCHAR(100);
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS planted_date TEXT;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS harvested_at TIMESTAMPTZ DEFAULT NOW();
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS harvested_date TEXT;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS growing_duration_days INT DEFAULT 0;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS quality_rating INT DEFAULT 5;

-- Enable RLS and permissive policies for authenticated / service_role
ALTER TABLE public.harvest_records ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "harvest_records_all" ON public.harvest_records;
CREATE POLICY "harvest_records_all" ON public.harvest_records FOR ALL USING (true) WITH CHECK (true);

-- 3. Document Active Schema Status
COMMENT ON TABLE public.crops IS 'Core reference agronomic metadata for the Mobile Crop Library and Admin Crop Catalog.';
COMMENT ON TABLE public.dss_rules IS 'Companion planting matrix evaluated by DSS engine and managed by Admin DSS Rule Editor.';
COMMENT ON TABLE public.farms IS 'Farms registered by mobile farmers.';
COMMENT ON TABLE public.crop_plots IS 'Active planted crop plots positioned on the isometric farm grid (Add Plant in Isometric).';
COMMENT ON TABLE public.crop_zones IS 'Sub-zones and plant instances within crop plots for isometric rendering.';
COMMENT ON TABLE public.tasks IS 'Daily farming care tasks (Today''s Task overlay and monitoring badges).';
COMMENT ON TABLE public.harvest_records IS 'Completed harvest logs, yield quantities, and quality ratings.';
COMMENT ON TABLE public.feedback IS 'Support tickets and feedback submitted from mobile to admin.';
COMMENT ON TABLE public.community_posts IS 'Farmer community discussion posts.';
COMMENT ON TABLE public.community_comments IS 'Replies and comments under community posts.';
COMMENT ON TABLE public.community_reports IS 'Moderation queue for reported community content.';
COMMENT ON TABLE public.profiles IS 'Farmer profile identity (nickname, avatar, onboarding status).';
COMMENT ON TABLE public.notifications IS 'System-wide and targeted broadcast notifications.';
