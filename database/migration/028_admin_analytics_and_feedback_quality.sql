-- ============================================================================
-- Migration 028: Admin Analytics, User Favorite Crops & Customer Service CSAT
-- Scope: Supabase PostgreSQL (user_favorite_crops, feedback, crop_logs, crop_plots)
-- ============================================================================

-- 1. USER FAVORITE CROPS TABLE (Cloud synchronization for farmer favorites)
CREATE TABLE IF NOT EXISTS public.user_favorite_crops (
    id TEXT PRIMARY KEY DEFAULT ('fav_' || substr(md5(random()::text || clock_timestamp()::text), 1, 8)),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    crop_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(user_id, crop_name)
);

ALTER TABLE public.user_favorite_crops ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "user_favorite_crops_select_policy" ON public.user_favorite_crops;
DROP POLICY IF EXISTS "user_favorite_crops_insert_policy" ON public.user_favorite_crops;
DROP POLICY IF EXISTS "user_favorite_crops_delete_policy" ON public.user_favorite_crops;

CREATE POLICY "user_favorite_crops_select_policy" ON public.user_favorite_crops FOR SELECT
    USING (user_id = auth.uid() OR public.is_admin());

CREATE POLICY "user_favorite_crops_insert_policy" ON public.user_favorite_crops FOR INSERT
    WITH CHECK (user_id = auth.uid() OR public.is_admin());

CREATE POLICY "user_favorite_crops_delete_policy" ON public.user_favorite_crops FOR DELETE
    USING (user_id = auth.uid() OR public.is_admin());

-- 2. FEEDBACK & CUSTOMER SERVICE SATISFACTION (CSAT & SLA)
ALTER TABLE public.feedback ADD COLUMN IF NOT EXISTS service_rating INT DEFAULT NULL;         -- 1 to 5 Stars
ALTER TABLE public.feedback ADD COLUMN IF NOT EXISTS was_helpful BOOLEAN DEFAULT NULL;        -- Did response solve concern?
ALTER TABLE public.feedback ADD COLUMN IF NOT EXISTS rated_at TIMESTAMPTZ DEFAULT NULL;       -- Timestamp when farmer submitted rating
ALTER TABLE public.feedback ADD COLUMN IF NOT EXISTS rating_comment TEXT DEFAULT NULL;        -- Optional comment from farmer on service

-- 3. ENSURE INDEXES FOR ANALYTICS PERFORMANCE
CREATE INDEX IF NOT EXISTS idx_crop_plots_planted_date ON public.crop_plots(planted_date);
CREATE INDEX IF NOT EXISTS idx_crop_plots_crop_name ON public.crop_plots(crop_name);
CREATE INDEX IF NOT EXISTS idx_crop_logs_crop_name ON public.crop_logs(crop_name);
CREATE INDEX IF NOT EXISTS idx_harvest_records_quality_rating ON public.harvest_records(quality_rating);
CREATE INDEX IF NOT EXISTS idx_user_favorite_crops_name ON public.user_favorite_crops(crop_name);
