-- ==============================================================================
-- Migration 015: Crop Uploader Dynamic Sync & Cloudflare Image Integration
-- Target: Supabase PostgreSQL (public schema)
-- Enables full administrative control of crops in Supabase with Cloudflare images,
-- allowing mobile farmers to receive live system updates without code changes.
-- ==============================================================================

-- 1. Ensure all agronomic and lifecycle columns exist on public.crops
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS local_name VARCHAR(100);
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS botanical_name VARCHAR(150);
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS taxonomic_family VARCHAR(100);
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS watering_interval_days INT NOT NULL DEFAULT 2;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS fertilize_interval_days INT NOT NULL DEFAULT 14;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS optimal_ph_min FLOAT DEFAULT 6.0;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS optimal_ph_max FLOAT DEFAULT 7.0;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS optimal_temp_min FLOAT DEFAULT 20.0;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS optimal_temp_max FLOAT DEFAULT 32.0;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS npk_n FLOAT DEFAULT 1.0;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS npk_p FLOAT DEFAULT 1.0;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS npk_k FLOAT DEFAULT 1.0;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS growth_stages JSONB DEFAULT '{"sprout": 5, "seedling": 12, "vegetative": 20, "flowering": 16, "harvest": 7}'::jsonb;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS harvest_indicators TEXT;
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS common_pests TEXT[] DEFAULT '{}';
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS companion_plants_good TEXT[] DEFAULT '{}';
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS companion_plants_bad TEXT[] DEFAULT '{}';
ALTER TABLE public.crops ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();

-- 2. Configure RLS Policies for public.crops (Allow Web Admin Full CRUD)
ALTER TABLE public.crops ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "crops_read_all" ON public.crops;
DROP POLICY IF EXISTS "crops_all" ON public.crops;

CREATE POLICY "crops_all" ON public.crops
    FOR ALL
    USING (true)
    WITH CHECK (true);

-- 3. Seed / Backfill existing 15 crops with complete agronomic parameters
UPDATE public.crops SET
    local_name = 'Kamatis',
    botanical_name = 'Solanum lycopersicum',
    watering_interval_days = 2,
    fertilize_interval_days = 14,
    optimal_ph_min = 6.0,
    optimal_ph_max = 6.8,
    npk_n = 1.5, npk_p = 1.0, npk_k = 2.0,
    harvest_indicators = 'Fruit turns bright red/orange; firm to touch',
    growth_stages = '{"sprout": 5, "seedling": 14, "vegetative": 21, "flowering": 15, "harvest": 7}'::jsonb
WHERE name = 'Tomato';

UPDATE public.crops SET
    local_name = 'Talong',
    botanical_name = 'Solanum melongena',
    watering_interval_days = 2,
    fertilize_interval_days = 14,
    optimal_ph_min = 5.5,
    optimal_ph_max = 6.8,
    npk_n = 1.5, npk_p = 1.0, npk_k = 2.5,
    harvest_indicators = 'Glossy deep purple skin, firm flesh',
    growth_stages = '{"sprout": 7, "seedling": 18, "vegetative": 25, "flowering": 18, "harvest": 7}'::jsonb
WHERE name = 'Eggplant';

UPDATE public.crops SET
    local_name = 'Karot',
    botanical_name = 'Daucus carota',
    watering_interval_days = 2,
    fertilize_interval_days = 15,
    optimal_ph_min = 5.8,
    optimal_ph_max = 6.8,
    npk_n = 1.0, npk_p = 2.0, npk_k = 2.0,
    harvest_indicators = 'Root crown reaches 1 inch diameter, bright orange',
    growth_stages = '{"sprout": 8, "seedling": 20, "vegetative": 32, "flowering": 18, "harvest": 7}'::jsonb
WHERE name = 'Carrot';

UPDATE public.crops SET
    local_name = 'Sibuyas',
    botanical_name = 'Allium cepa',
    watering_interval_days = 3,
    fertilize_interval_days = 20,
    optimal_ph_min = 6.0,
    optimal_ph_max = 7.0,
    npk_n = 1.0, npk_p = 1.5, npk_k = 1.5,
    harvest_indicators = 'Tops turn yellow and dryly fall over',
    growth_stages = '{"sprout": 10, "seedling": 25, "vegetative": 45, "flowering": 20, "harvest": 10}'::jsonb
WHERE name = 'Onion';

UPDATE public.crops SET
    local_name = 'Repolyo',
    botanical_name = 'Brassica oleracea var. capitata',
    watering_interval_days = 2,
    fertilize_interval_days = 10,
    optimal_ph_min = 6.0,
    optimal_ph_max = 6.5,
    npk_n = 2.0, npk_p = 1.0, npk_k = 1.5,
    harvest_indicators = 'Firm, solid head formed at plant center',
    growth_stages = '{"sprout": 5, "seedling": 14, "vegetative": 25, "flowering": 10, "harvest": 6}'::jsonb
WHERE name = 'Cabbage';

UPDATE public.crops SET
    local_name = 'Pechay',
    botanical_name = 'Brassica rapa subsp. chinensis',
    watering_interval_days = 1,
    fertilize_interval_days = 7,
    optimal_ph_min = 6.0,
    optimal_ph_max = 7.0,
    npk_n = 2.0, npk_p = 1.0, npk_k = 1.0,
    harvest_indicators = 'Crisp upright green petioles at 25-30 days',
    growth_stages = '{"sprout": 3, "seedling": 7, "vegetative": 12, "flowering": 4, "harvest": 4}'::jsonb
WHERE name = 'Pechay';

UPDATE public.crops SET
    local_name = 'Sitaw',
    botanical_name = 'Vigna unguiculata subsp. sesquipedalis',
    watering_interval_days = 2,
    fertilize_interval_days = 14,
    optimal_ph_min = 5.5,
    optimal_ph_max = 6.5,
    npk_n = 0.5, npk_p = 1.5, npk_k = 1.5,
    harvest_indicators = 'Long tender pods snap easily before seeds bulge',
    growth_stages = '{"sprout": 4, "seedling": 10, "vegetative": 18, "flowering": 10, "harvest": 6}'::jsonb
WHERE name = 'Yardlong String Bean';

-- 4. Initial System Update Broadcast for Mobile Devices
INSERT INTO public.notifications (title, body, notification_type, is_read)
VALUES (
    '🌾 Dynamic Crop Catalog & System Update v1.3.0',
    'MapTanim Admin upgraded the cloud crop catalog. Tap to download new crops, updated watering schedules, and companion rules directly to your offline map.',
    'SYSTEM_UPDATE',
    FALSE
)
ON CONFLICT DO NOTHING;
