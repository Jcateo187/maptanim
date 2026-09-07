-- ==============================================================================
-- Migration 017: Supabase Storage Bucket Setup (100% Free, No Credit Card)
-- Target: Supabase PostgreSQL (storage schema)
-- Creates the public 'crop-images' bucket so admins can upload crop images directly
-- without needing Cloudflare R2 or any credit card.
-- ==============================================================================

-- 1. Create the 'crop-images' storage bucket with public read access
INSERT INTO storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
VALUES (
    'crop-images',
    'crop-images',
    true,
    5242880, -- 5 MB max per image
    ARRAY['image/png', 'image/jpeg', 'image/webp', 'image/gif']
)
ON CONFLICT (id) DO UPDATE SET
    public = true,
    file_size_limit = 5242880,
    allowed_mime_types = ARRAY['image/png', 'image/jpeg', 'image/webp', 'image/gif'];

-- 2. Configure Row Level Security (RLS) on storage.objects for 'crop-images'
-- Enable public viewing (so Android app and web dashboard can load images without auth)
DROP POLICY IF EXISTS "Public Access crop-images" ON storage.objects;
CREATE POLICY "Public Access crop-images"
ON storage.objects FOR SELECT
USING (bucket_id = 'crop-images');

-- Enable upload for crop images
DROP POLICY IF EXISTS "Public Upload crop-images" ON storage.objects;
CREATE POLICY "Public Upload crop-images"
ON storage.objects FOR INSERT
WITH CHECK (bucket_id = 'crop-images');

-- Enable update for crop images
DROP POLICY IF EXISTS "Public Update crop-images" ON storage.objects;
CREATE POLICY "Public Update crop-images"
ON storage.objects FOR UPDATE
USING (bucket_id = 'crop-images');

-- Enable delete for crop images
DROP POLICY IF EXISTS "Public Delete crop-images" ON storage.objects;
CREATE POLICY "Public Delete crop-images"
ON storage.objects FOR DELETE
USING (bucket_id = 'crop-images');
