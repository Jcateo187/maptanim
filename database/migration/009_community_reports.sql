-- Migration 009: Community Hub Moderation Reports for Supabase
-- Target Project: ojilvcglpzbtpjxguhzj.supabase.co

-- 1. Table: public.community_reports
CREATE TABLE IF NOT EXISTS public.community_reports (
    id                  TEXT            PRIMARY KEY DEFAULT ('rep_' || substr(md5(random()::text || clock_timestamp()::text), 1, 16)),
    reporter_id         UUID            REFERENCES auth.users(id) ON DELETE SET NULL,
    reporter_name       VARCHAR(150)    NOT NULL DEFAULT 'Farmer Member',
    target_type         VARCHAR(50)     NOT NULL, -- 'POST', 'USER', 'COMMENT'
    target_id           TEXT            NOT NULL,
    target_name         VARCHAR(150)    NOT NULL,
    target_content      TEXT,
    reason              VARCHAR(100)    NOT NULL,
    details             TEXT,
    status              VARCHAR(50)     NOT NULL DEFAULT 'PENDING', -- 'PENDING', 'INVESTIGATING', 'RESOLVED', 'DISMISSED'
    admin_notes         TEXT,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    resolved_at         TIMESTAMPTZ
);

-- 2. Enable RLS
ALTER TABLE public.community_reports ENABLE ROW LEVEL SECURITY;

-- 3. RLS Policies for community_reports
DROP POLICY IF EXISTS "community_reports_select_all" ON public.community_reports;
CREATE POLICY "community_reports_select_all" ON public.community_reports
    FOR SELECT USING (true);

DROP POLICY IF EXISTS "community_reports_insert_all" ON public.community_reports;
CREATE POLICY "community_reports_insert_all" ON public.community_reports
    FOR INSERT WITH CHECK (true);

DROP POLICY IF EXISTS "community_reports_update_all" ON public.community_reports;
CREATE POLICY "community_reports_update_all" ON public.community_reports
    FOR UPDATE USING (true);

DROP POLICY IF EXISTS "community_reports_delete_all" ON public.community_reports;
CREATE POLICY "community_reports_delete_all" ON public.community_reports
    FOR DELETE USING (true);

-- 4. Indexes for Report Queries
CREATE INDEX IF NOT EXISTS idx_community_reports_target ON public.community_reports(target_type, target_id);
CREATE INDEX IF NOT EXISTS idx_community_reports_status ON public.community_reports(status);
CREATE INDEX IF NOT EXISTS idx_community_reports_created ON public.community_reports(created_at DESC);

-- 5. Seed Sample Community Moderation Reports
INSERT INTO public.community_reports (id, reporter_name, target_type, target_id, target_name, target_content, reason, details, status, created_at)
VALUES
    ('rep_1', 'Ka Ryan Vasquez', 'POST', 'post_3', 'Aling Maria Juanillo', '🚜 Bamboo Stakes & Insect Netting Seed Swap — Extra Sitaw Seeds', 'Spam / Commercial Selling', 'Selling untreated seeds without phytosanitary clearance or certified label.', 'PENDING', NOW() - INTERVAL '3 hours'),
    ('rep_2', 'Farmer Partner', 'USER', 'james', 'Farmer James', 'Farmer James direct messaging unsolicited links in community chat.', 'Harassment / Unsolicited Direct Messaging', 'Sent repetitive unsolicited promotional messages in direct chat.', 'PENDING', NOW() - INTERVAL '1 day')
ON CONFLICT (id) DO NOTHING;
