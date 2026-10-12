-- ==============================================================================
-- Migration 021: Practical Agronomy, Badges, and Crop Rotation System
-- Target: Supabase PostgreSQL (public schema)
-- Supports:
-- 1. Actionable badge system in isometric canvas (Weed, Trellis, Nutrition, Rotation)
-- 2. Practical layman crop metadata (trellis type, planting methods, Filipino guides)
-- 3. Crop family tracking for crop rotation recommendations
-- 4. Enriched post-harvest records with seasonal insights
-- ==============================================================================

-- 1. Extend task_type_enum for new badges
DO $$ BEGIN
    ALTER TYPE task_type_enum ADD VALUE IF NOT EXISTS 'WEED';
    ALTER TYPE task_type_enum ADD VALUE IF NOT EXISTS 'TRELLIS';
    ALTER TYPE task_type_enum ADD VALUE IF NOT EXISTS 'NUTRITION';
    ALTER TYPE task_type_enum ADD VALUE IF NOT EXISTS 'ROTATION_ALERT';
EXCEPTION WHEN duplicate_object THEN null; END $$;

-- 2. Add practical agronomic columns to public.crops
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS needs_trellis BOOLEAN DEFAULT FALSE;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS trellis_type TEXT DEFAULT 'NONE'; -- 'TULOS', 'A_FRAME', 'OVERHEAD', 'NONE'
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS preferred_planting_method TEXT DEFAULT 'DIRECT'; -- 'DIRECT', 'TRANSPLANT', 'BED', 'CONTAINER'
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS weeding_interval_days INT DEFAULT 7;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS soil_prep_tagalog TEXT;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS harvest_signs_tagalog TEXT;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS botanical_family TEXT DEFAULT 'General';

-- 3. Update Botanical Families for standard approved crops
UPDATE public.crops SET botanical_family = 'Solanaceae' WHERE LOWER(name) IN ('tomato', 'eggplant', 'chili pepper', 'sweet pepper', 'bell pepper');
UPDATE public.crops SET botanical_family = 'Cucurbitaceae' WHERE LOWER(name) IN ('cucumber', 'squash', 'bitter gourd', 'bottle gourd', 'sponge gourd');
UPDATE public.crops SET botanical_family = 'Fabaceae' WHERE LOWER(name) IN ('yardlong string bean', 'string bean', 'mungbean', 'snap bean');
UPDATE public.crops SET botanical_family = 'Brassicaceae' WHERE LOWER(name) IN ('cabbage', 'pechay', 'mustard', 'radish');
UPDATE public.crops SET botanical_family = 'Amaryllidaceae' WHERE LOWER(name) IN ('onion', 'garlic', 'shallot', 'leek');
UPDATE public.crops SET botanical_family = 'Apiaceae' WHERE LOWER(name) IN ('carrot', 'celery');
UPDATE public.crops SET botanical_family = 'Poaceae' WHERE LOWER(name) IN ('corn');
UPDATE public.crops SET botanical_family = 'Convolvulaceae' WHERE LOWER(name) IN ('water spinach', 'sweet potato');
UPDATE public.crops SET botanical_family = 'Malvaceae' WHERE LOWER(name) IN ('okra');

-- Set trellis requirements for climbing crops
UPDATE public.crops SET needs_trellis = TRUE, trellis_type = 'OVERHEAD' WHERE LOWER(name) IN ('bitter gourd', 'bottle gourd', 'sponge gourd');
UPDATE public.crops SET needs_trellis = TRUE, trellis_type = 'A_FRAME' WHERE LOWER(name) IN ('yardlong string bean', 'cucumber');
UPDATE public.crops SET needs_trellis = TRUE, trellis_type = 'TULOS' WHERE LOWER(name) IN ('tomato', 'eggplant');

-- 4. Create Crop Rotation History table
CREATE TABLE IF NOT EXISTS public.crop_rotation_log (
    id                  TEXT PRIMARY KEY DEFAULT ('rot_' || substr(md5(random()::text || clock_timestamp()::text), 1, 8)),
    farm_id             TEXT NOT NULL,
    plot_id             TEXT NOT NULL,
    botanical_family    TEXT NOT NULL,
    crop_name           VARCHAR(100) NOT NULL,
    planted_date        TEXT NOT NULL,
    harvested_date      TEXT,
    cycle_count         INT DEFAULT 1,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE public.crop_rotation_log ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "crop_rotation_log_all" ON public.crop_rotation_log;
CREATE POLICY "crop_rotation_log_all" ON public.crop_rotation_log FOR ALL USING (true) WITH CHECK (true);

-- 5. Enrich harvest_records with post-harvest learning & fallow
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS seasonal_insight TEXT;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS rotation_suggestion TEXT;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS fallow_until_date TEXT;

-- 6. Comments for schema documentation
COMMENT ON COLUMN public.crops.needs_trellis IS 'Whether crop requires structural support/trellis (triggers TRELLIS badge)';
COMMENT ON COLUMN public.crops.trellis_type IS 'Type of trellis recommended: TULOS, A_FRAME, or OVERHEAD';
COMMENT ON COLUMN public.crops.botanical_family IS 'Botanical plant family used to calculate rotation conflict warnings';
COMMENT ON TABLE public.crop_rotation_log IS 'Log of crops planted on specific plots to prevent soil depletion from consecutive same-family planting';
