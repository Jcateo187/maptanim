-- MapTanim Versioned Migration 005: Admin RLS Read Policies & User Tracking Support
-- Enables read queries for admin dashboard to monitor users, farms, and plots across the application

-- 1. Add status column to users table if missing
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'public' 
          AND table_name = 'users' 
          AND column_name = 'status'
    ) THEN
        ALTER TABLE public.users ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
    END IF;
END $$;

-- 2. Update RLS policies to allow reading users, profiles, farms, and crop plots
DROP POLICY IF EXISTS "users_read_all" ON public.users;
CREATE POLICY "users_read_all" ON public.users FOR SELECT USING (true);

DROP POLICY IF EXISTS "profiles_read_all" ON public.profiles;
CREATE POLICY "profiles_read_all" ON public.profiles FOR SELECT USING (true);

DROP POLICY IF EXISTS "farms_read_all" ON public.farms;
CREATE POLICY "farms_read_all" ON public.farms FOR SELECT USING (true);

DROP POLICY IF EXISTS "crop_plots_read_all" ON public.crop_plots;
CREATE POLICY "crop_plots_read_all" ON public.crop_plots FOR SELECT USING (true);
