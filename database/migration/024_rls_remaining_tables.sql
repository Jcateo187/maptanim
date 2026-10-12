-- ==============================================================================
-- Migration 024: Production RLS Hardening for Remaining Tables & Storage Bucket
-- Target: Supabase PostgreSQL (public & storage schemas)
-- Enforces owner-scoping on crop_rotation_log, conditional check on farm_objects,
-- and admin-only storage mutation policies on crop-images bucket.
-- ==============================================================================

-- 1. public.crop_rotation_log (Ensure table and columns exist, then harden RLS)
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

ALTER TABLE public.crop_rotation_log ADD COLUMN IF NOT EXISTS farm_id TEXT;
ALTER TABLE public.crop_rotation_log ADD COLUMN IF NOT EXISTS plot_id TEXT;

ALTER TABLE public.crop_rotation_log ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "crop_rotation_log_all" ON public.crop_rotation_log;
DROP POLICY IF EXISTS "crop_rotation_log_select" ON public.crop_rotation_log;
DROP POLICY IF EXISTS "crop_rotation_log_insert" ON public.crop_rotation_log;
DROP POLICY IF EXISTS "crop_rotation_log_update" ON public.crop_rotation_log;
DROP POLICY IF EXISTS "crop_rotation_log_delete" ON public.crop_rotation_log;

CREATE POLICY "crop_rotation_log_select" ON public.crop_rotation_log
    FOR SELECT USING (
        farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
        OR public.is_admin()
    );

CREATE POLICY "crop_rotation_log_insert" ON public.crop_rotation_log
    FOR INSERT WITH CHECK (
        farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
        OR public.is_admin()
    );

CREATE POLICY "crop_rotation_log_update" ON public.crop_rotation_log
    FOR UPDATE USING (
        farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
        OR public.is_admin()
    );

CREATE POLICY "crop_rotation_log_delete" ON public.crop_rotation_log
    FOR DELETE USING (
        farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
        OR public.is_admin()
    );

-- 2. public.farm_objects (Conditional execution: table was dropped in migration 020)
-- Only configure RLS if the table exists in this database instance.
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'farm_objects') THEN
        ALTER TABLE public.farm_objects ADD COLUMN IF NOT EXISTS farm_id TEXT;
        ALTER TABLE public.farm_objects ENABLE ROW LEVEL SECURITY;
        
        DROP POLICY IF EXISTS "farm_objects_all" ON public.farm_objects;
        DROP POLICY IF EXISTS "farm_objects_select" ON public.farm_objects;
        DROP POLICY IF EXISTS "farm_objects_insert" ON public.farm_objects;
        DROP POLICY IF EXISTS "farm_objects_update" ON public.farm_objects;
        DROP POLICY IF EXISTS "farm_objects_delete" ON public.farm_objects;

        EXECUTE 'CREATE POLICY "farm_objects_select" ON public.farm_objects
            FOR SELECT USING (
                farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
                OR public.is_admin()
            )';

        EXECUTE 'CREATE POLICY "farm_objects_insert" ON public.farm_objects
            FOR INSERT WITH CHECK (
                farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
                OR public.is_admin()
            )';

        EXECUTE 'CREATE POLICY "farm_objects_update" ON public.farm_objects
            FOR UPDATE USING (
                farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
                OR public.is_admin()
            )';

        EXECUTE 'CREATE POLICY "farm_objects_delete" ON public.farm_objects
            FOR DELETE USING (
                farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
                OR public.is_admin()
            )';
    END IF;
END $$;

-- 3. Storage Bucket 'crop-images' RLS Hardening
-- Public read access for images; strictly admin-only for uploads, updates, and deletes
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'storage' AND table_name = 'objects') THEN
        DROP POLICY IF EXISTS "Public Access crop-images" ON storage.objects;
        DROP POLICY IF EXISTS "crop_images_select_public" ON storage.objects;
        EXECUTE 'CREATE POLICY "crop_images_select_public"
            ON storage.objects FOR SELECT
            USING (bucket_id = ''crop-images'')';

        DROP POLICY IF EXISTS "Public Upload crop-images" ON storage.objects;
        DROP POLICY IF EXISTS "crop_images_insert_admin" ON storage.objects;
        EXECUTE 'CREATE POLICY "crop_images_insert_admin"
            ON storage.objects FOR INSERT
            WITH CHECK (bucket_id = ''crop-images'' AND public.is_admin())';

        DROP POLICY IF EXISTS "Public Update crop-images" ON storage.objects;
        DROP POLICY IF EXISTS "crop_images_update_admin" ON storage.objects;
        EXECUTE 'CREATE POLICY "crop_images_update_admin"
            ON storage.objects FOR UPDATE
            USING (bucket_id = ''crop-images'' AND public.is_admin())';

        DROP POLICY IF EXISTS "Public Delete crop-images" ON storage.objects;
        DROP POLICY IF EXISTS "crop_images_delete_admin" ON storage.objects;
        EXECUTE 'CREATE POLICY "crop_images_delete_admin"
            ON storage.objects FOR DELETE
            USING (bucket_id = ''crop-images'' AND public.is_admin())';
    END IF;
END $$;
