-- MapTanim Versioned Migration 004: Notifications & Schema Compatibility Alignment
-- Resolves user_id nullable constraint for system broadcasts & ensures notification_type column exists

-- 1. Make user_id nullable in notifications table so system-wide broadcast notifications can be inserted
ALTER TABLE public.notifications ALTER COLUMN user_id DROP NOT NULL;

-- 2. Add notification_type column to notifications table if missing
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'public' 
          AND table_name = 'notifications' 
          AND column_name = 'notification_type'
    ) THEN
        ALTER TABLE public.notifications ADD COLUMN notification_type VARCHAR(50) NOT NULL DEFAULT 'SYSTEM_UPDATE';
    END IF;
END $$;

-- 3. Add crop_variety column to crop_plots if missing
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'public' 
          AND table_name = 'crop_plots' 
          AND column_name = 'crop_variety'
    ) THEN
        ALTER TABLE public.crop_plots ADD COLUMN crop_variety VARCHAR(100);
    END IF;
END $$;

-- 4. Update notifications RLS policy to allow reading system-wide broadcasts (where user_id IS NULL)
DROP POLICY IF EXISTS "users_own_notifications" ON public.notifications;
DROP POLICY IF EXISTS "notifications_read_all" ON public.notifications;
CREATE POLICY "notifications_read_all" ON public.notifications
    FOR SELECT USING (user_id IS NULL OR auth.uid() = user_id);
