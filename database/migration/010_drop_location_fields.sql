-- ==============================================================================
-- Migration 010: Drop location and author_location columns
-- ==============================================================================

-- Drop location from public.farms
ALTER TABLE IF EXISTS public.farms DROP COLUMN IF EXISTS location;

-- Drop author_location from public.community_posts
ALTER TABLE IF EXISTS public.community_posts DROP COLUMN IF EXISTS author_location;

-- Drop author_location from public.community_comments
ALTER TABLE IF EXISTS public.community_comments DROP COLUMN IF EXISTS author_location;
