-- ==============================================================================
-- MapTanim Decision Support System (DSS) Schema Migration
-- Evaluates real-time farmer data against documented DA-BPI / DA-BAR research rules
-- Synchronized with Mobile App Version 1.3.0 (DSS Engine 2.0.0)
-- ==============================================================================

-- 1. ENUMS FOR DSS
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

-- 2. TABLE: public.dss_evaluations (Historical & Active DSS Sessions)
CREATE TABLE IF NOT EXISTS public.dss_evaluations (
    id                  TEXT                        PRIMARY KEY DEFAULT ('dss_eval_' || substr(md5(random()::text || clock_timestamp()::text), 1, 12)),
    session_id          VARCHAR(100)                NOT NULL,
    farm_id             TEXT                        NOT NULL REFERENCES public.farms(id) ON DELETE CASCADE,
    farmer_id           TEXT,
    summary             JSONB                       DEFAULT '{}',
    evaluated_at        TIMESTAMPTZ                 NOT NULL DEFAULT NOW(),
    created_at          TIMESTAMPTZ                 NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_dss_eval_farm ON public.dss_evaluations(farm_id);
CREATE INDEX IF NOT EXISTS idx_dss_eval_date ON public.dss_evaluations(evaluated_at DESC);

ALTER TABLE public.dss_evaluations ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "dss_evaluations_all" ON public.dss_evaluations;
CREATE POLICY "dss_evaluations_all" ON public.dss_evaluations FOR ALL USING (true) WITH CHECK (true);

-- 3. TABLE: public.dss_decisions (Cached decisions, recommendations, alerts, and care tasks)
CREATE TABLE IF NOT EXISTS public.dss_decisions (
    id                  TEXT                        PRIMARY KEY,
    evaluation_id       TEXT                        REFERENCES public.dss_evaluations(id) ON DELETE CASCADE,
    farm_id             TEXT                        NOT NULL REFERENCES public.farms(id) ON DELETE CASCADE,
    plot_id             TEXT                        REFERENCES public.crop_plots(id) ON DELETE SET NULL,
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

ALTER TABLE public.dss_decisions ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "dss_decisions_all" ON public.dss_decisions;
CREATE POLICY "dss_decisions_all" ON public.dss_decisions FOR ALL USING (true) WITH CHECK (true);

-- 4. TABLE: public.crop_logs (Farmer Stage Logs & Context-Driven Observations)
CREATE TABLE IF NOT EXISTS public.crop_logs (
    id                  TEXT                        PRIMARY KEY DEFAULT ('log_' || substr(md5(random()::text || clock_timestamp()::text), 1, 12)),
    crop_planting_id    TEXT                        NOT NULL,
    farm_id             TEXT                        NOT NULL REFERENCES public.farms(id) ON DELETE CASCADE,
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

ALTER TABLE public.crop_logs ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "crop_logs_all" ON public.crop_logs;
CREATE POLICY "crop_logs_all" ON public.crop_logs FOR ALL USING (true) WITH CHECK (true);

-- 5. EXTEND: public.harvest_records with flow fields
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS harvest_method VARCHAR(100);
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS quantity NUMERIC(10,2) DEFAULT 0;
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS unit VARCHAR(50) DEFAULT 'kg';
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS marketable_pct NUMERIC(5,2);
ALTER TABLE public.harvest_records ADD COLUMN IF NOT EXISTS crop_planting_id TEXT;

