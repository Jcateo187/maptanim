-- ==============================================================================
-- Migration 023: Production RLS Hardening, Admin Audit Trail & DSS Schema Sync
-- Target: Supabase PostgreSQL (public schema)
-- ==============================================================================

-- 1. Helper function: public.is_admin()
-- Evaluates whether the current authenticated JWT belongs to an active administrator
-- or is executing under the Supabase service_role key.
-- Explicitly casts id::text = (auth.uid())::text to avoid uuid = text operator mismatches.
CREATE OR REPLACE FUNCTION public.is_admin()
RETURNS BOOLEAN
LANGUAGE sql
SECURITY DEFINER
STABLE
AS $$
  SELECT (
    COALESCE(auth.role() = 'service_role', false)
    OR EXISTS (
      SELECT 1 FROM public.users
      WHERE id::text = (auth.uid())::text
        AND role::text IN ('ADMINISTRATOR', 'ADMIN', 'SUPER_ADMIN')
        AND status::text = 'ACTIVE'
    )
  );
$$;

-- 2. Yard Dimensions on farms table (for real-world yard calibration sync)
ALTER TABLE public.farms ADD COLUMN IF NOT EXISTS yard_width_m NUMERIC(6,2);
ALTER TABLE public.farms ADD COLUMN IF NOT EXISTS yard_length_m NUMERIC(6,2);

-- 3. Formalize DSS tables into versioned migration flow
DO $$ BEGIN
    CREATE TYPE dss_decision_type_enum AS ENUM ('RECOMMENDATION', 'ALERT', 'TASK', 'INSUFFICIENT_INFO');
EXCEPTION WHEN duplicate_object THEN null; END $$;

DO $$ BEGIN
    CREATE TYPE dss_category_enum AS ENUM (
        'SEASON_WINDOW', 'SOIL_COMPATIBILITY', 'GROWTH_CARE',
        'PEST_DISEASE', 'HARVEST_READINESS', 'COMPANION_INTERCROPPING',
        'CROP_ROTATION_FALLOW', 'NUTRIENT_WATER'
    );
EXCEPTION WHEN duplicate_object THEN null; END $$;

DO $$ BEGIN
    CREATE TYPE dss_priority_enum AS ENUM ('CRITICAL', 'HIGH', 'MEDIUM', 'LOW', 'INFO');
EXCEPTION WHEN duplicate_object THEN null; END $$;

-- DSS Evaluations Table
CREATE TABLE IF NOT EXISTS public.dss_evaluations (
    id                  TEXT                        PRIMARY KEY DEFAULT ('dss_eval_' || substr(md5(random()::text || clock_timestamp()::text), 1, 12)),
    session_id          VARCHAR(100)                NOT NULL,
    farm_id             TEXT                        NOT NULL,
    farmer_id           TEXT,
    summary             JSONB                       DEFAULT '{}',
    evaluated_at        TIMESTAMPTZ                 NOT NULL DEFAULT NOW(),
    created_at          TIMESTAMPTZ                 NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_dss_eval_farm ON public.dss_evaluations(farm_id);
CREATE INDEX IF NOT EXISTS idx_dss_eval_date ON public.dss_evaluations(evaluated_at DESC);

-- DSS Decisions Table
CREATE TABLE IF NOT EXISTS public.dss_decisions (
    id                  TEXT                        PRIMARY KEY,
    evaluation_id       TEXT                        REFERENCES public.dss_evaluations(id) ON DELETE CASCADE,
    farm_id             TEXT                        NOT NULL,
    plot_id             TEXT,
    plot_label          VARCHAR(50),
    crop_name           VARCHAR(100),
    decision_type       dss_decision_type_enum      NOT NULL,
    category            dss_category_enum           NOT NULL,
    priority            dss_priority_enum           NOT NULL DEFAULT 'MEDIUM',
    title               VARCHAR(255)                NOT NULL,
    summary             TEXT                        NOT NULL,
    explanation         TEXT                        NOT NULL,
    source              VARCHAR(255)                NOT NULL,
    action_text         VARCHAR(100),
    action_task_type    VARCHAR(50),
    rule_id             VARCHAR(100),
    is_actionable       BOOLEAN                     NOT NULL DEFAULT TRUE,
    is_completed        BOOLEAN                     NOT NULL DEFAULT FALSE,
    evaluated_at        TIMESTAMPTZ                 NOT NULL DEFAULT NOW(),
    created_at          TIMESTAMPTZ                 NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_dss_decisions_farm ON public.dss_decisions(farm_id);
CREATE INDEX IF NOT EXISTS idx_dss_decisions_priority ON public.dss_decisions(priority);
CREATE INDEX IF NOT EXISTS idx_dss_decisions_category ON public.dss_decisions(category);

-- Crop Logs Table
CREATE TABLE IF NOT EXISTS public.crop_logs (
    id                  TEXT                        PRIMARY KEY DEFAULT ('log_' || substr(md5(random()::text || clock_timestamp()::text), 1, 12)),
    crop_planting_id    TEXT                        NOT NULL,
    farm_id             TEXT                        NOT NULL,
    bed_id              TEXT,
    crop_id             TEXT,
    variety_id          TEXT,
    crop_name           VARCHAR(100)                NOT NULL,
    variety_name        VARCHAR(100)                NOT NULL,
    current_stage       VARCHAR(50)                 NOT NULL,
    log_context         VARCHAR(50)                 NOT NULL,
    care_activity       VARCHAR(50),
    selected_choice     VARCHAR(10)                 NOT NULL,
    selected_checkboxes JSONB                       DEFAULT '[]',
    notes               TEXT,
    log_date            DATE                        NOT NULL DEFAULT CURRENT_DATE,
    created_at          TIMESTAMPTZ                 NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_crop_logs_planting ON public.crop_logs(crop_planting_id);
CREATE INDEX IF NOT EXISTS idx_crop_logs_farm ON public.crop_logs(farm_id);
CREATE INDEX IF NOT EXISTS idx_crop_logs_date ON public.crop_logs(log_date DESC);

-- 4. Immutable Administrative Audit Trail Table
CREATE TABLE IF NOT EXISTS public.admin_audit_logs (
    id                  TEXT                        PRIMARY KEY DEFAULT ('audit_' || substr(md5(random()::text || clock_timestamp()::text), 1, 12)),
    admin_id            TEXT                        NOT NULL,
    admin_email         VARCHAR(255)                NOT NULL,
    action              VARCHAR(100)                NOT NULL,
    target_module       VARCHAR(100)                NOT NULL,
    details             TEXT                        NOT NULL,
    target_id           TEXT,
    status              VARCHAR(50)                 NOT NULL DEFAULT 'SUCCESS',
    ip_address          VARCHAR(100),
    created_at          TIMESTAMPTZ                 NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_admin_audit_created ON public.admin_audit_logs(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_admin_audit_admin ON public.admin_audit_logs(admin_id);

ALTER TABLE public.admin_audit_logs ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "admin_audit_logs_select" ON public.admin_audit_logs;
CREATE POLICY "admin_audit_logs_select" ON public.admin_audit_logs
    FOR SELECT USING (public.is_admin());

DROP POLICY IF EXISTS "admin_audit_logs_insert" ON public.admin_audit_logs;
CREATE POLICY "admin_audit_logs_insert" ON public.admin_audit_logs
    FOR INSERT WITH CHECK (public.is_admin());

-- Notice: No UPDATE or DELETE policy on admin_audit_logs (immutable by design)

-- ==============================================================================
-- 5. RLS HARDENING: REFERENCE CATALOGS
-- ==============================================================================

-- public.crops (Everyone can read, only admin can mutate)
ALTER TABLE public.crops ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "crops_read_all" ON public.crops;
DROP POLICY IF EXISTS "crops_all" ON public.crops;
DROP POLICY IF EXISTS "crops_select" ON public.crops;
DROP POLICY IF EXISTS "crops_insert_admin" ON public.crops;
DROP POLICY IF EXISTS "crops_update_admin" ON public.crops;
DROP POLICY IF EXISTS "crops_delete_admin" ON public.crops;

CREATE POLICY "crops_select" ON public.crops
    FOR SELECT USING (true);
CREATE POLICY "crops_insert_admin" ON public.crops
    FOR INSERT WITH CHECK (public.is_admin());
CREATE POLICY "crops_update_admin" ON public.crops
    FOR UPDATE USING (public.is_admin());
CREATE POLICY "crops_delete_admin" ON public.crops
    FOR DELETE USING (public.is_admin());

-- public.dss_rules (Everyone can read, only admin can mutate)
ALTER TABLE public.dss_rules ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "dss_rules_all" ON public.dss_rules;
DROP POLICY IF EXISTS "dss_rules_select" ON public.dss_rules;
DROP POLICY IF EXISTS "dss_rules_insert_admin" ON public.dss_rules;
DROP POLICY IF EXISTS "dss_rules_update_admin" ON public.dss_rules;
DROP POLICY IF EXISTS "dss_rules_delete_admin" ON public.dss_rules;

CREATE POLICY "dss_rules_select" ON public.dss_rules
    FOR SELECT USING (true);
CREATE POLICY "dss_rules_insert_admin" ON public.dss_rules
    FOR INSERT WITH CHECK (public.is_admin());
CREATE POLICY "dss_rules_update_admin" ON public.dss_rules
    FOR UPDATE USING (public.is_admin());
CREATE POLICY "dss_rules_delete_admin" ON public.dss_rules
    FOR DELETE USING (public.is_admin());

-- ==============================================================================
-- 6. RLS HARDENING: USERS & PROFILES
-- ==============================================================================

-- public.users (Only self or admin can inspect; only admin can elevate roles/statuses)
ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "users_read_all" ON public.users;
DROP POLICY IF EXISTS "users_select_self_or_admin" ON public.users;
CREATE POLICY "users_select_self_or_admin" ON public.users
    FOR SELECT USING (
        id::text = (auth.uid())::text
        OR public.is_admin()
    );

DROP POLICY IF EXISTS "users_insert_self_or_admin" ON public.users;
CREATE POLICY "users_insert_self_or_admin" ON public.users
    FOR INSERT WITH CHECK (
        id::text = (auth.uid())::text
        OR public.is_admin()
    );

DROP POLICY IF EXISTS "users_update_self_or_admin" ON public.users;
CREATE POLICY "users_update_self_or_admin" ON public.users
    FOR UPDATE USING (
        id::text = (auth.uid())::text
        OR public.is_admin()
    )
    WITH CHECK (
        public.is_admin()
        OR (
            id::text = (auth.uid())::text
            AND role::text = (SELECT u.role::text FROM public.users u WHERE u.id::text = (auth.uid())::text)
            AND status::text = (SELECT u.status::text FROM public.users u WHERE u.id::text = (auth.uid())::text)
        )
    );

-- public.profiles
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "profiles_select_all" ON public.profiles;
DROP POLICY IF EXISTS "profiles_read_all" ON public.profiles;
DROP POLICY IF EXISTS "profiles_insert_own" ON public.profiles;
DROP POLICY IF EXISTS "profiles_update_own" ON public.profiles;

CREATE POLICY "profiles_select_all" ON public.profiles
    FOR SELECT USING (true);
CREATE POLICY "profiles_insert_own" ON public.profiles
    FOR INSERT WITH CHECK (id::text = (auth.uid())::text OR public.is_admin());
CREATE POLICY "profiles_update_own" ON public.profiles
    FOR UPDATE USING (id::text = (auth.uid())::text OR public.is_admin());

-- ==============================================================================
-- 7. RLS HARDENING: FARMER PRIVATE AGRONOMIC DATA (OWNER-SCOPED)
-- ==============================================================================

-- public.farms
ALTER TABLE public.farms ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "farms_read_all" ON public.farms;
DROP POLICY IF EXISTS "farms_insert_all" ON public.farms;
DROP POLICY IF EXISTS "farms_update_all" ON public.farms;
DROP POLICY IF EXISTS "farms_delete_all" ON public.farms;

CREATE POLICY "farms_select_owner_or_admin" ON public.farms
    FOR SELECT USING (farmer_id::text = (auth.uid())::text OR public.is_admin());
CREATE POLICY "farms_insert_owner_or_admin" ON public.farms
    FOR INSERT WITH CHECK (farmer_id::text = (auth.uid())::text OR public.is_admin());
CREATE POLICY "farms_update_owner_or_admin" ON public.farms
    FOR UPDATE USING (farmer_id::text = (auth.uid())::text OR public.is_admin());
CREATE POLICY "farms_delete_owner_or_admin" ON public.farms
    FOR DELETE USING (farmer_id::text = (auth.uid())::text OR public.is_admin());

-- public.crop_plots
ALTER TABLE public.crop_plots ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "crop_plots_all" ON public.crop_plots;
DROP POLICY IF EXISTS "crop_plots_read_all" ON public.crop_plots;

CREATE POLICY "crop_plots_select" ON public.crop_plots
    FOR SELECT USING (
        farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
        OR public.is_admin()
    );
CREATE POLICY "crop_plots_insert" ON public.crop_plots
    FOR INSERT WITH CHECK (
        farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
        OR public.is_admin()
    );
CREATE POLICY "crop_plots_update" ON public.crop_plots
    FOR UPDATE USING (
        farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
        OR public.is_admin()
    );
CREATE POLICY "crop_plots_delete" ON public.crop_plots
    FOR DELETE USING (
        farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
        OR public.is_admin()
    );

-- public.crop_zones
ALTER TABLE public.crop_zones ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "crop_zones_all" ON public.crop_zones;

CREATE POLICY "crop_zones_select" ON public.crop_zones
    FOR SELECT USING (
        plot_id::text IN (
            SELECT p.id::text FROM public.crop_plots p
            JOIN public.farms f ON p.farm_id::text = f.id::text
            WHERE f.farmer_id::text = (auth.uid())::text
        )
        OR public.is_admin()
    );
CREATE POLICY "crop_zones_insert" ON public.crop_zones
    FOR INSERT WITH CHECK (
        plot_id::text IN (
            SELECT p.id::text FROM public.crop_plots p
            JOIN public.farms f ON p.farm_id::text = f.id::text
            WHERE f.farmer_id::text = (auth.uid())::text
        )
        OR public.is_admin()
    );
CREATE POLICY "crop_zones_update" ON public.crop_zones
    FOR UPDATE USING (
        plot_id::text IN (
            SELECT p.id::text FROM public.crop_plots p
            JOIN public.farms f ON p.farm_id::text = f.id::text
            WHERE f.farmer_id::text = (auth.uid())::text
        )
        OR public.is_admin()
    );
CREATE POLICY "crop_zones_delete" ON public.crop_zones
    FOR DELETE USING (
        plot_id::text IN (
            SELECT p.id::text FROM public.crop_plots p
            JOIN public.farms f ON p.farm_id::text = f.id::text
            WHERE f.farmer_id::text = (auth.uid())::text
        )
        OR public.is_admin()
    );

-- public.harvest_records
ALTER TABLE public.harvest_records ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "harvest_records_all" ON public.harvest_records;

CREATE POLICY "harvest_records_select" ON public.harvest_records
    FOR SELECT USING (
        farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
        OR public.is_admin()
    );
CREATE POLICY "harvest_records_insert" ON public.harvest_records
    FOR INSERT WITH CHECK (
        farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
        OR public.is_admin()
    );
CREATE POLICY "harvest_records_update" ON public.harvest_records
    FOR UPDATE USING (
        farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
        OR public.is_admin()
    );
CREATE POLICY "harvest_records_delete" ON public.harvest_records
    FOR DELETE USING (
        farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
        OR public.is_admin()
    );

-- public.tasks
ALTER TABLE public.tasks ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "tasks_select" ON public.tasks;
DROP POLICY IF EXISTS "tasks_insert" ON public.tasks;
DROP POLICY IF EXISTS "tasks_update" ON public.tasks;
DROP POLICY IF EXISTS "tasks_delete" ON public.tasks;
DROP POLICY IF EXISTS "tasks_all" ON public.tasks;

CREATE POLICY "tasks_select" ON public.tasks
    FOR SELECT USING (
        farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
        OR public.is_admin()
    );
CREATE POLICY "tasks_insert" ON public.tasks
    FOR INSERT WITH CHECK (
        farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
        OR public.is_admin()
    );
CREATE POLICY "tasks_update" ON public.tasks
    FOR UPDATE USING (
        farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
        OR public.is_admin()
    );
CREATE POLICY "tasks_delete" ON public.tasks
    FOR DELETE USING (
        farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
        OR public.is_admin()
    );

-- public.crop_logs
ALTER TABLE public.crop_logs ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "crop_logs_all" ON public.crop_logs;

CREATE POLICY "crop_logs_select" ON public.crop_logs
    FOR SELECT USING (
        farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
        OR public.is_admin()
    );
CREATE POLICY "crop_logs_insert" ON public.crop_logs
    FOR INSERT WITH CHECK (
        farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
        OR public.is_admin()
    );
CREATE POLICY "crop_logs_update" ON public.crop_logs
    FOR UPDATE USING (
        farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
        OR public.is_admin()
    );
CREATE POLICY "crop_logs_delete" ON public.crop_logs
    FOR DELETE USING (
        farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
        OR public.is_admin()
    );

-- public.dss_evaluations & public.dss_decisions
ALTER TABLE IF EXISTS public.dss_evaluations ADD COLUMN IF NOT EXISTS farmer_id TEXT;
ALTER TABLE public.dss_evaluations ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "dss_evaluations_all" ON public.dss_evaluations;

CREATE POLICY "dss_evaluations_select" ON public.dss_evaluations
    FOR SELECT USING (
        farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
        OR public.is_admin()
    );
CREATE POLICY "dss_evaluations_insert" ON public.dss_evaluations
    FOR INSERT WITH CHECK (
        farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
        OR public.is_admin()
    );

ALTER TABLE public.dss_decisions ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "dss_decisions_all" ON public.dss_decisions;

CREATE POLICY "dss_decisions_select" ON public.dss_decisions
    FOR SELECT USING (
        farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
        OR public.is_admin()
    );
CREATE POLICY "dss_decisions_insert" ON public.dss_decisions
    FOR INSERT WITH CHECK (
        farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
        OR public.is_admin()
    );
CREATE POLICY "dss_decisions_update" ON public.dss_decisions
    FOR UPDATE USING (
        farm_id::text IN (SELECT f.id::text FROM public.farms f WHERE f.farmer_id::text = (auth.uid())::text)
        OR public.is_admin()
    );

-- ==============================================================================
-- 8. RLS HARDENING: SUPPORT, NOTIFICATIONS & COMMUNITY
-- ==============================================================================

-- public.feedback
ALTER TABLE public.feedback ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "feedback_all" ON public.feedback;

CREATE POLICY "feedback_select" ON public.feedback
    FOR SELECT USING (user_id::text = (auth.uid())::text OR public.is_admin());
CREATE POLICY "feedback_insert" ON public.feedback
    FOR INSERT WITH CHECK (user_id::text = (auth.uid())::text OR user_id IS NULL OR public.is_admin());
CREATE POLICY "feedback_update" ON public.feedback
    FOR UPDATE USING (public.is_admin());

-- public.notifications
ALTER TABLE public.notifications ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "notifications_all" ON public.notifications;

CREATE POLICY "notifications_select" ON public.notifications
    FOR SELECT USING (
        user_id::text = (auth.uid())::text
        OR user_id IS NULL
        OR public.is_admin()
    );
CREATE POLICY "notifications_insert" ON public.notifications
    FOR INSERT WITH CHECK (public.is_admin());
CREATE POLICY "notifications_update" ON public.notifications
    FOR UPDATE USING (
        user_id::text = (auth.uid())::text
        OR public.is_admin()
    );
CREATE POLICY "notifications_delete" ON public.notifications
    FOR DELETE USING (public.is_admin());

-- public.community_posts
ALTER TABLE public.community_posts ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "community_posts_select_all" ON public.community_posts;
DROP POLICY IF EXISTS "community_posts_insert_all" ON public.community_posts;
DROP POLICY IF EXISTS "community_posts_update_all" ON public.community_posts;
DROP POLICY IF EXISTS "community_posts_delete_all" ON public.community_posts;

CREATE POLICY "community_posts_select" ON public.community_posts
    FOR SELECT USING (true);
CREATE POLICY "community_posts_insert" ON public.community_posts
    FOR INSERT WITH CHECK (author_id::text = (auth.uid())::text OR public.is_admin());
CREATE POLICY "community_posts_update" ON public.community_posts
    FOR UPDATE USING (author_id::text = (auth.uid())::text OR public.is_admin());
CREATE POLICY "community_posts_delete" ON public.community_posts
    FOR DELETE USING (author_id::text = (auth.uid())::text OR public.is_admin());

-- public.community_comments
ALTER TABLE public.community_comments ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "community_comments_select_all" ON public.community_comments;
DROP POLICY IF EXISTS "community_comments_insert_all" ON public.community_comments;
DROP POLICY IF EXISTS "community_comments_update_all" ON public.community_comments;
DROP POLICY IF EXISTS "community_comments_delete_all" ON public.community_comments;

CREATE POLICY "community_comments_select" ON public.community_comments
    FOR SELECT USING (true);
CREATE POLICY "community_comments_insert" ON public.community_comments
    FOR INSERT WITH CHECK (author_id::text = (auth.uid())::text OR public.is_admin());
CREATE POLICY "community_comments_update" ON public.community_comments
    FOR UPDATE USING (author_id::text = (auth.uid())::text OR public.is_admin());
CREATE POLICY "community_comments_delete" ON public.community_comments
    FOR DELETE USING (author_id::text = (auth.uid())::text OR public.is_admin());

-- public.community_reports
ALTER TABLE public.community_reports ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "community_reports_select_all" ON public.community_reports;
DROP POLICY IF EXISTS "community_reports_insert_all" ON public.community_reports;
DROP POLICY IF EXISTS "community_reports_update_all" ON public.community_reports;
DROP POLICY IF EXISTS "community_reports_delete_all" ON public.community_reports;

CREATE POLICY "community_reports_select" ON public.community_reports
    FOR SELECT USING (public.is_admin());
CREATE POLICY "community_reports_insert" ON public.community_reports
    FOR INSERT WITH CHECK (auth.uid() IS NOT NULL OR public.is_admin());
CREATE POLICY "community_reports_update" ON public.community_reports
    FOR UPDATE USING (public.is_admin());
CREATE POLICY "community_reports_delete" ON public.community_reports
    FOR DELETE USING (public.is_admin());
