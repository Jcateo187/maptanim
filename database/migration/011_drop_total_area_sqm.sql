-- ==============================================================================
-- Migration 011: Drop total_area_sqm column from public.farms
-- ==============================================================================

ALTER TABLE IF EXISTS public.farms DROP COLUMN IF EXISTS total_area_sqm;
