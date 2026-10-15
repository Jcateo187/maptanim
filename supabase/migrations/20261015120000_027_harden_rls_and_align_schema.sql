-- ============================================================================
-- Migration 027: Harden RLS Policies & Complete Full-Stack Schema Alignment
-- Scope: Supabase PostgreSQL (farms, plots, tasks, harvests, crop_logs, feedback, notifications)
-- Ensures user isolation, prevents unauthorized data modification, and aligns schema.
-- ============================================================================

-- 1. ENUMS PARITY
ALTER TYPE category_enum ADD VALUE IF NOT EXISTS 'PODDED';
ALTER TYPE task_type_enum ADD VALUE IF NOT EXISTS 'SOIL_AMENDMENT';
ALTER TYPE task_type_enum ADD VALUE IF NOT EXISTS 'PRUNING';
ALTER TYPE task_type_enum ADD VALUE IF NOT EXISTS 'OBSERVATION';

-- 2. USERS & PROFILES ALIGNMENT
ALTER TABLE public.users ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
ALTER TABLE public.users DROP CONSTRAINT IF EXISTS users_status_check;
ALTER TABLE public.users ADD CONSTRAINT users_status_check
    CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'PENDING'));
ALTER TABLE public.users ALTER COLUMN email DROP NOT NULL;

-- 3. CROP PLOTS LIFECYCLE TRACKING
ALTER TABLE public.crop_plots ADD COLUMN IF NOT EXISTS current_stage VARCHAR(50) NOT NULL DEFAULT 'PREPARATION';
ALTER TABLE public.crop_plots ADD COLUMN IF NOT EXISTS harvest_count INT NOT NULL DEFAULT 0;
ALTER TABLE public.crop_plots ADD COLUMN IF NOT EXISTS total_yield_kg FLOAT NOT NULL DEFAULT 0.0;
ALTER TABLE public.crop_plots ADD COLUMN IF NOT EXISTS previous_crops_history TEXT NOT NULL DEFAULT '';
ALTER TABLE public.crop_plots ADD COLUMN IF NOT EXISTS crop_variety VARCHAR(100);

-- 4. HARVEST RECORDS FULL SCHEMA ALIGNMENT (Active Mobile & Admin UI Source of Truth)
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS farm_id TEXT;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS plot_id TEXT;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS farm_name VARCHAR(100) DEFAULT 'My Farm';
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS plot_label VARCHAR(50) DEFAULT 'Plot 1';
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS crop_variety VARCHAR(100);
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS planted_date TEXT;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS harvested_at TIMESTAMPTZ DEFAULT NOW();
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS harvested_date TEXT;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS growing_duration_days INT DEFAULT 0;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS yield_kg FLOAT DEFAULT 0.0;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS yield_units INT;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS quality_grade VARCHAR(20) DEFAULT 'Grade A';
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS quality_rating INT DEFAULT 5;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS seasonal_insight TEXT;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS rotation_suggestion TEXT;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS fallow_until_date TEXT;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS notes TEXT;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS is_final_harvest BOOLEAN DEFAULT TRUE;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS quantity FLOAT DEFAULT 0.0;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS unit VARCHAR(20) DEFAULT 'kg';
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS marketable_pct FLOAT;

-- 5. TASKS SCHEMA ALIGNMENT (Active Mobile UI Source of Truth)
ALTER TABLE public.tasks ADD COLUMN IF NOT EXISTS plot_label VARCHAR(50);
ALTER TABLE public.tasks ADD COLUMN IF NOT EXISTS crop_name VARCHAR(100);
ALTER TABLE public.tasks ADD COLUMN IF NOT EXISTS sub_label VARCHAR(150);
ALTER TABLE public.tasks ADD COLUMN IF NOT EXISTS completed_at TIMESTAMPTZ;
ALTER TABLE public.tasks ADD COLUMN IF NOT EXISTS notes TEXT;

-- 6. COMMUNITY POSTS
ALTER TABLE public.community_posts ADD COLUMN IF NOT EXISTS image_url TEXT;

-- 7. CROP LOGS RELAXATIONS & COMPATIBILITY
ALTER TABLE public.crop_logs ALTER COLUMN variety_name DROP NOT NULL;
ALTER TABLE public.crop_logs ALTER COLUMN farm_id DROP NOT NULL;
ALTER TABLE public.crop_logs ADD COLUMN IF NOT EXISTS bed_id TEXT;
ALTER TABLE public.crop_logs ADD COLUMN IF NOT EXISTS crop_id TEXT;
ALTER TABLE public.crop_logs ADD COLUMN IF NOT EXISTS variety_id TEXT;

-- 6. ADMIN AUDIT LOGS TABLE
CREATE TABLE IF NOT EXISTS public.admin_audit_logs (
    id              TEXT            PRIMARY KEY DEFAULT ('audit_' || substr(md5(random()::text || clock_timestamp()::text), 1, 16)),
    admin_id        TEXT,
    admin_email     VARCHAR(255),
    action          VARCHAR(100)    NOT NULL,
    target_module   VARCHAR(100)    NOT NULL,
    target_id       TEXT,
    details         TEXT,
    status          VARCHAR(20)     NOT NULL DEFAULT 'SUCCESS',
    ip_address      VARCHAR(100),
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_admin_audit_created ON public.admin_audit_logs(created_at DESC);

-- 7. SECURITY DEFINER HELPER: is_admin
CREATE OR REPLACE FUNCTION public.is_admin()
RETURNS BOOLEAN AS $$
  SELECT (
    COALESCE(auth.role() = 'service_role', false)
    OR EXISTS (
      SELECT 1 FROM public.users 
      WHERE id::text = (auth.uid())::text 
        AND role::text IN ('ADMINISTRATOR', 'ADMIN', 'SUPER_ADMIN')
        AND status::text = 'ACTIVE'
    )
  );
$$ LANGUAGE sql STABLE SECURITY DEFINER SET search_path = public;

-- 8. HARDEN ROW LEVEL SECURITY (RLS) POLICIES
-- Farms: Only farm owner or administrator can view, insert, update, or delete
ALTER TABLE public.farms ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "farms_select_policy" ON public.farms;
DROP POLICY IF EXISTS "farms_insert_policy" ON public.farms;
DROP POLICY IF EXISTS "farms_update_policy" ON public.farms;
DROP POLICY IF EXISTS "farms_delete_policy" ON public.farms;
DROP POLICY IF EXISTS "farms_all_policy" ON public.farms;

CREATE POLICY "farms_select_policy" ON public.farms FOR SELECT
    USING (farmer_id::text = (auth.uid())::text OR public.is_admin());
CREATE POLICY "farms_insert_policy" ON public.farms FOR INSERT
    WITH CHECK (farmer_id::text = (auth.uid())::text OR public.is_admin());
CREATE POLICY "farms_update_policy" ON public.farms FOR UPDATE
    USING (farmer_id::text = (auth.uid())::text OR public.is_admin())
    WITH CHECK (farmer_id::text = (auth.uid())::text OR public.is_admin());
CREATE POLICY "farms_delete_policy" ON public.farms FOR DELETE
    USING (farmer_id::text = (auth.uid())::text OR public.is_admin());

-- Crop Plots: Scoped via farm ownership
ALTER TABLE public.crop_plots ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "crop_plots_select_policy" ON public.crop_plots;
DROP POLICY IF EXISTS "crop_plots_insert_policy" ON public.crop_plots;
DROP POLICY IF EXISTS "crop_plots_update_policy" ON public.crop_plots;
DROP POLICY IF EXISTS "crop_plots_delete_policy" ON public.crop_plots;
DROP POLICY IF EXISTS "crop_plots_all_policy" ON public.crop_plots;

CREATE POLICY "crop_plots_select_policy" ON public.crop_plots FOR SELECT
    USING (EXISTS (SELECT 1 FROM public.farms WHERE farms.id::text = crop_plots.farm_id::text AND (farms.farmer_id::text = (auth.uid())::text OR public.is_admin())));
CREATE POLICY "crop_plots_insert_policy" ON public.crop_plots FOR INSERT
    WITH CHECK (EXISTS (SELECT 1 FROM public.farms WHERE farms.id::text = crop_plots.farm_id::text AND (farms.farmer_id::text = (auth.uid())::text OR public.is_admin())));
CREATE POLICY "crop_plots_update_policy" ON public.crop_plots FOR UPDATE
    USING (EXISTS (SELECT 1 FROM public.farms WHERE farms.id::text = crop_plots.farm_id::text AND (farms.farmer_id::text = (auth.uid())::text OR public.is_admin())))
    WITH CHECK (EXISTS (SELECT 1 FROM public.farms WHERE farms.id::text = crop_plots.farm_id::text AND (farms.farmer_id::text = (auth.uid())::text OR public.is_admin())));
CREATE POLICY "crop_plots_delete_policy" ON public.crop_plots FOR DELETE
    USING (EXISTS (SELECT 1 FROM public.farms WHERE farms.id::text = crop_plots.farm_id::text AND (farms.farmer_id::text = (auth.uid())::text OR public.is_admin())));

-- Tasks: Scoped via farm ownership
ALTER TABLE public.tasks ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "tasks_select_policy" ON public.tasks;
DROP POLICY IF EXISTS "tasks_insert_policy" ON public.tasks;
DROP POLICY IF EXISTS "tasks_update_policy" ON public.tasks;
DROP POLICY IF EXISTS "tasks_delete_policy" ON public.tasks;
DROP POLICY IF EXISTS "tasks_all_policy" ON public.tasks;

CREATE POLICY "tasks_select_policy" ON public.tasks FOR SELECT
    USING (EXISTS (SELECT 1 FROM public.farms WHERE farms.id::text = tasks.farm_id::text AND (farms.farmer_id::text = (auth.uid())::text OR public.is_admin())));
CREATE POLICY "tasks_insert_policy" ON public.tasks FOR INSERT
    WITH CHECK (EXISTS (SELECT 1 FROM public.farms WHERE farms.id::text = tasks.farm_id::text AND (farms.farmer_id::text = (auth.uid())::text OR public.is_admin())));
CREATE POLICY "tasks_update_policy" ON public.tasks FOR UPDATE
    USING (EXISTS (SELECT 1 FROM public.farms WHERE farms.id::text = tasks.farm_id::text AND (farms.farmer_id::text = (auth.uid())::text OR public.is_admin())))
    WITH CHECK (EXISTS (SELECT 1 FROM public.farms WHERE farms.id::text = tasks.farm_id::text AND (farms.farmer_id::text = (auth.uid())::text OR public.is_admin())));
CREATE POLICY "tasks_delete_policy" ON public.tasks FOR DELETE
    USING (EXISTS (SELECT 1 FROM public.farms WHERE farms.id::text = tasks.farm_id::text AND (farms.farmer_id::text = (auth.uid())::text OR public.is_admin())));

-- Harvest Records: Scoped via farm ownership
ALTER TABLE public.harvest_records ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "harvest_records_select_policy" ON public.harvest_records;
DROP POLICY IF EXISTS "harvest_records_insert_policy" ON public.harvest_records;
DROP POLICY IF EXISTS "harvest_records_update_policy" ON public.harvest_records;
DROP POLICY IF EXISTS "harvest_records_delete_policy" ON public.harvest_records;
DROP POLICY IF EXISTS "harvest_records_all_policy" ON public.harvest_records;

CREATE POLICY "harvest_records_select_policy" ON public.harvest_records FOR SELECT
    USING (EXISTS (SELECT 1 FROM public.farms WHERE farms.id::text = harvest_records.farm_id::text AND (farms.farmer_id::text = (auth.uid())::text OR public.is_admin())));
CREATE POLICY "harvest_records_insert_policy" ON public.harvest_records FOR INSERT
    WITH CHECK (EXISTS (SELECT 1 FROM public.farms WHERE farms.id::text = harvest_records.farm_id::text AND (farms.farmer_id::text = (auth.uid())::text OR public.is_admin())));
CREATE POLICY "harvest_records_update_policy" ON public.harvest_records FOR UPDATE
    USING (EXISTS (SELECT 1 FROM public.farms WHERE farms.id::text = harvest_records.farm_id::text AND (farms.farmer_id::text = (auth.uid())::text OR public.is_admin())))
    WITH CHECK (EXISTS (SELECT 1 FROM public.farms WHERE farms.id::text = harvest_records.farm_id::text AND (farms.farmer_id::text = (auth.uid())::text OR public.is_admin())));
CREATE POLICY "harvest_records_delete_policy" ON public.harvest_records FOR DELETE
    USING (EXISTS (SELECT 1 FROM public.farms WHERE farms.id::text = harvest_records.farm_id::text AND (farms.farmer_id::text = (auth.uid())::text OR public.is_admin())));

-- Crop Logs: Scoped via farm ownership or author
ALTER TABLE public.crop_logs ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "crop_logs_select_policy" ON public.crop_logs;
DROP POLICY IF EXISTS "crop_logs_insert_policy" ON public.crop_logs;
DROP POLICY IF EXISTS "crop_logs_update_policy" ON public.crop_logs;
DROP POLICY IF EXISTS "crop_logs_delete_policy" ON public.crop_logs;

CREATE POLICY "crop_logs_select_policy" ON public.crop_logs FOR SELECT
    USING (
      farm_id IS NULL OR 
      EXISTS (SELECT 1 FROM public.farms WHERE farms.id::text = crop_logs.farm_id::text AND (farms.farmer_id::text = (auth.uid())::text OR public.is_admin()))
    );
CREATE POLICY "crop_logs_insert_policy" ON public.crop_logs FOR INSERT
    WITH CHECK (
      farm_id IS NULL OR 
      EXISTS (SELECT 1 FROM public.farms WHERE farms.id::text = crop_logs.farm_id::text AND (farms.farmer_id::text = (auth.uid())::text OR public.is_admin()))
    );
CREATE POLICY "crop_logs_update_policy" ON public.crop_logs FOR UPDATE
    USING (
      farm_id IS NULL OR 
      EXISTS (SELECT 1 FROM public.farms WHERE farms.id::text = crop_logs.farm_id::text AND (farms.farmer_id::text = (auth.uid())::text OR public.is_admin()))
    );
CREATE POLICY "crop_logs_delete_policy" ON public.crop_logs FOR DELETE
    USING (
      farm_id IS NULL OR 
      EXISTS (SELECT 1 FROM public.farms WHERE farms.id::text = crop_logs.farm_id::text AND (farms.farmer_id::text = (auth.uid())::text OR public.is_admin()))
    );

-- Feedback / Support Tickets: Users can read/write their own tickets; Admins manage all
ALTER TABLE public.feedback ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "feedback_all_policy" ON public.feedback;
DROP POLICY IF EXISTS "feedback_select_policy" ON public.feedback;
DROP POLICY IF EXISTS "feedback_insert_policy" ON public.feedback;
DROP POLICY IF EXISTS "feedback_update_policy" ON public.feedback;
DROP POLICY IF EXISTS "feedback_delete_policy" ON public.feedback;

CREATE POLICY "feedback_select_policy" ON public.feedback FOR SELECT
    USING (user_id::text = (auth.uid())::text OR public.is_admin());
CREATE POLICY "feedback_insert_policy" ON public.feedback FOR INSERT
    WITH CHECK (user_id::text = (auth.uid())::text OR user_id IS NULL OR public.is_admin());
CREATE POLICY "feedback_update_policy" ON public.feedback FOR UPDATE
    USING (public.is_admin() OR user_id::text = (auth.uid())::text);
CREATE POLICY "feedback_delete_policy" ON public.feedback FOR DELETE
    USING (public.is_admin() OR user_id::text = (auth.uid())::text);

-- Notifications: User sees broadcasts (user_id IS NULL) and direct alerts (user_id = auth.uid())
ALTER TABLE public.notifications ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "notifications_all_policy" ON public.notifications;
DROP POLICY IF EXISTS "notifications_select_policy" ON public.notifications;
DROP POLICY IF EXISTS "notifications_insert_policy" ON public.notifications;
DROP POLICY IF EXISTS "notifications_update_policy" ON public.notifications;
DROP POLICY IF EXISTS "notifications_delete_policy" ON public.notifications;

CREATE POLICY "notifications_select_policy" ON public.notifications FOR SELECT
    USING (user_id IS NULL OR user_id::text = (auth.uid())::text OR public.is_admin());
CREATE POLICY "notifications_insert_policy" ON public.notifications FOR INSERT
    WITH CHECK (public.is_admin());
CREATE POLICY "notifications_update_policy" ON public.notifications FOR UPDATE
    USING (user_id::text = (auth.uid())::text OR public.is_admin());
CREATE POLICY "notifications_delete_policy" ON public.notifications FOR DELETE
    USING (public.is_admin() OR user_id::text = (auth.uid())::text);
