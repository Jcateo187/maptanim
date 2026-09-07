-- ==============================================================================
-- Migration 019: Clean Community Forum Schema & Drop Location Columns
-- Target: Supabase PostgreSQL (Project: ojilvcglpzbtpjxguhzj.supabase.co)
--
-- Ensures absolute removal of all location columns (farms, posts, comments).
-- No hardcoded text values. Posts, comments, and reports are created dynamically
-- by real users and agronomists via the application.
-- ==============================================================================

-- 1. Ensure obsolete and location columns are removed completely
ALTER TABLE IF EXISTS public.farms DROP COLUMN IF EXISTS location;
ALTER TABLE IF EXISTS public.community_posts DROP COLUMN IF EXISTS author_location;
ALTER TABLE IF EXISTS public.community_comments DROP COLUMN IF EXISTS author_location;
ALTER TABLE IF EXISTS public.profiles DROP COLUMN IF EXISTS onboarding_completed;

-- 2. Ensure hardcoded text defaults on author_name are dropped
ALTER TABLE IF EXISTS public.community_posts ALTER COLUMN author_name DROP DEFAULT;
ALTER TABLE IF EXISTS public.community_comments ALTER COLUMN author_name DROP DEFAULT;

-- 3. Ensure indexes are present for realtime performance
CREATE INDEX IF NOT EXISTS idx_community_posts_category ON public.community_posts(category);
CREATE INDEX IF NOT EXISTS idx_community_posts_created ON public.community_posts(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_community_comments_post_id ON public.community_comments(post_id);
