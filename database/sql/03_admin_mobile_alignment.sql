-- ============================================================================
-- MapTanim 03: Admin <-> Mobile Backend Alignment
-- Run AFTER 01_schema_and_seed.sql and dss_migration.sql. Idempotent.
--
-- Ownership boundaries:
--   MOBILE (writes): profiles, farms, crop_plots, crop_zones, tasks,
--                    harvest_records, crop_logs, crop_rotation_log,
--                    dss_evaluations, dss_decisions (Home / Garden / Library / Profile)
--   SHARED:          community_posts, community_comments, community_reports,
--                    feedback, notifications  (mobile writes, admin moderates/replies)
--   ADMIN (writes):  users.status / users.role, admin_audit_logs
--   READ-ONLY REF:   crops, dss_rules, crop_* knowledge tables
--
-- Fixes columns/tables referenced by current code but missing from the schema.
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. USERS: account status for admin User Management + mobile suspension check
--    (admin api.ts updateUserStatus, mobile UserStatusDto / AuthViewModel)
-- ----------------------------------------------------------------------------
ALTER TABLE public.users ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
ALTER TABLE public.users DROP CONSTRAINT IF EXISTS users_status_check;
ALTER TABLE public.users ADD CONSTRAINT users_status_check
    CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'PENDING'));

-- Anonymous / guest mobile sessions have no email
ALTER TABLE public.users ALTER COLUMN email DROP NOT NULL;

-- Auto-provision public.users alongside public.profiles on signup so admin
-- status/role updates always hit a real row.
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO public.profiles (id, nickname, nickname_updated_at)
  VALUES (
    NEW.id,
    COALESCE(NEW.raw_user_meta_data->>'nickname', CASE WHEN NEW.email IS NOT NULL AND NEW.email <> '' THEN split_part(NEW.email, '@', 1) ELSE 'Farmer' END),
    NOW()
  )
  ON CONFLICT (id) DO NOTHING;

  INSERT INTO public.users (id, email, role, status)
  VALUES (
    NEW.id,
    NULLIF(NEW.email, ''),
    CASE WHEN NEW.email IS NULL OR NEW.email = '' THEN 'GUEST'::role_enum ELSE 'FARMER'::role_enum END,
    'ACTIVE'
  )
  ON CONFLICT (id) DO NOTHING;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Backfill users rows for every existing auth account
INSERT INTO public.users (id, email, role, status)
SELECT
    au.id,
    NULLIF(au.email, ''),
    CASE WHEN au.email IS NULL OR au.email = '' THEN 'GUEST'::role_enum ELSE 'FARMER'::role_enum END,
    'ACTIVE'
FROM auth.users au
WHERE NOT EXISTS (SELECT 1 FROM public.users u WHERE u.id = au.id)
  AND (au.email IS NULL OR au.email = '' OR NOT EXISTS (SELECT 1 FROM public.users u2 WHERE u2.email = au.email))
ON CONFLICT DO NOTHING;

-- Admin (authenticated session with ADMINISTRATOR role, enforced by admin
-- AuthContext) must be able to update status/role of other users.
CREATE OR REPLACE FUNCTION public.is_admin()
RETURNS BOOLEAN AS $$
  SELECT EXISTS (SELECT 1 FROM public.users WHERE id = auth.uid() AND role = 'ADMINISTRATOR');
$$ LANGUAGE sql STABLE SECURITY DEFINER SET search_path = public;

DROP POLICY IF EXISTS "users_admin_update" ON public.users;
CREATE POLICY "users_admin_update" ON public.users
    FOR UPDATE USING (public.is_admin()) WITH CHECK (public.is_admin());

DROP POLICY IF EXISTS "users_admin_insert" ON public.users;
CREATE POLICY "users_admin_insert" ON public.users
    FOR INSERT WITH CHECK (public.is_admin());

-- ----------------------------------------------------------------------------
-- 2. COMMUNITY: image_url is sent by both mobile CommunityPostDto and admin
--    createCommunityPost but did not exist -> every post insert failed.
-- ----------------------------------------------------------------------------
ALTER TABLE public.community_posts ADD COLUMN IF NOT EXISTS image_url TEXT;

-- Keep comments_count accurate regardless of which client adds/deletes comments
CREATE OR REPLACE FUNCTION public.sync_post_comments_count()
RETURNS TRIGGER AS $$
BEGIN
  UPDATE public.community_posts
     SET comments_count = (SELECT COUNT(*) FROM public.community_comments c WHERE c.post_id = COALESCE(NEW.post_id, OLD.post_id)),
         updated_at = NOW()
   WHERE id = COALESCE(NEW.post_id, OLD.post_id);
  RETURN NULL;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS trg_sync_post_comments_count ON public.community_comments;
CREATE TRIGGER trg_sync_post_comments_count
  AFTER INSERT OR DELETE ON public.community_comments
  FOR EACH ROW EXECUTE FUNCTION public.sync_post_comments_count();

-- ----------------------------------------------------------------------------
-- 3. CUSTOMER SERVICE: feedback + notifications indexes for admin inbox and
--    mobile "my tickets" / notification feed (user_id OR broadcast NULL)
-- ----------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_feedback_user ON public.feedback(user_id);
CREATE INDEX IF NOT EXISTS idx_feedback_status ON public.feedback(status);
CREATE INDEX IF NOT EXISTS idx_feedback_created ON public.feedback(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_notifications_user ON public.notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_created ON public.notifications(created_at DESC);

-- ----------------------------------------------------------------------------
-- 4. TRACKING SYSTEM: admin_audit_logs (queried by admin getAuditLogs/logAction)
-- ----------------------------------------------------------------------------
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

ALTER TABLE public.admin_audit_logs ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "admin_audit_logs_select" ON public.admin_audit_logs;
CREATE POLICY "admin_audit_logs_select" ON public.admin_audit_logs FOR SELECT USING (auth.role() = 'authenticated');
DROP POLICY IF EXISTS "admin_audit_logs_insert" ON public.admin_audit_logs;
CREATE POLICY "admin_audit_logs_insert" ON public.admin_audit_logs FOR INSERT WITH CHECK (auth.role() = 'authenticated');

-- ----------------------------------------------------------------------------
-- 5. MOBILE GARDEN: crop_logs alignment (variety_name nullable, farm_id nullable)
-- ----------------------------------------------------------------------------
ALTER TABLE public.crop_logs ALTER COLUMN variety_name DROP NOT NULL;
ALTER TABLE public.crop_logs ALTER COLUMN farm_id DROP NOT NULL;

-- Automatically convert empty string farm_id to NULL so FK check does not fail
CREATE OR REPLACE FUNCTION public.clean_crop_logs_farm_id()
RETURNS TRIGGER AS $$
BEGIN
  IF NEW.farm_id IS NOT NULL AND TRIM(NEW.farm_id) = '' THEN
    NEW.farm_id := NULL;
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_clean_crop_logs_farm_id ON public.crop_logs;
CREATE TRIGGER trg_clean_crop_logs_farm_id
  BEFORE INSERT OR UPDATE ON public.crop_logs
  FOR EACH ROW EXECUTE FUNCTION public.clean_crop_logs_farm_id();

-- ----------------------------------------------------------------------------
-- 6. MOBILE GARDEN: crop_plots lifecycle tracking columns
-- ----------------------------------------------------------------------------
ALTER TABLE public.crop_plots ADD COLUMN IF NOT EXISTS current_stage VARCHAR(50) NOT NULL DEFAULT 'PREPARATION';
ALTER TABLE public.crop_plots ADD COLUMN IF NOT EXISTS harvest_count INT NOT NULL DEFAULT 0;
ALTER TABLE public.crop_plots ADD COLUMN IF NOT EXISTS total_yield_kg FLOAT NOT NULL DEFAULT 0.0;
ALTER TABLE public.crop_plots ADD COLUMN IF NOT EXISTS previous_crops_history TEXT NOT NULL DEFAULT '';

-- ----------------------------------------------------------------------------
-- 7. ENUMS: Sitaw category + Kotlin TaskType enum parity
-- ----------------------------------------------------------------------------
ALTER TYPE category_enum ADD VALUE IF NOT EXISTS 'PODDED';
ALTER TYPE task_type_enum ADD VALUE IF NOT EXISTS 'SOIL_AMENDMENT';
ALTER TYPE task_type_enum ADD VALUE IF NOT EXISTS 'PRUNING';
ALTER TYPE task_type_enum ADD VALUE IF NOT EXISTS 'OBSERVATION';

-- ----------------------------------------------------------------------------
-- 8. REALTIME: admin Customer Service & Community live updates
-- ----------------------------------------------------------------------------
DO $$
DECLARE t TEXT;
BEGIN
  FOREACH t IN ARRAY ARRAY['feedback', 'community_posts', 'community_comments', 'community_reports', 'notifications'] LOOP
    BEGIN
      EXECUTE format('ALTER PUBLICATION supabase_realtime ADD TABLE public.%I', t);
    EXCEPTION WHEN duplicate_object OR undefined_object THEN NULL;
    END;
  END LOOP;
END $$;

