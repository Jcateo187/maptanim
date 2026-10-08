-- ==============================================================================
-- Migration 026: Add image_url to public.community_posts
-- Target: Supabase PostgreSQL (Project: ojilvcglpzbtpjxguhzj.supabase.co)
-- Enables farmers and agronomists to share photo attachments in community posts.
-- ==============================================================================

-- 1. Add image_url column to community_posts
ALTER TABLE IF EXISTS public.community_posts
ADD COLUMN IF NOT EXISTS image_url TEXT;

-- 2. Add comment for documentation
COMMENT ON COLUMN public.community_posts.image_url IS 'Public URL or storage URI for attached post photo/image.';
