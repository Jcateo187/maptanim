-- ============================================================================
-- Migration 029: Allow Read Access for Admin Analytics & Planting Features
-- Scope: Supabase PostgreSQL (farms, crop_plots, crop_logs, tasks, harvest_records, user_favorite_crops)
-- 
-- Rationale:
-- 1. Allows the Admin Dashboard to aggregate planting analytics, garden sizes,
--    check-up diagnostics, and harvest trends.
-- 2. Keeps all data mutations (INSERT, UPDATE, DELETE) STRICTLY RESTRICTED
--    to authenticated owners (auth.uid()) or administrators.
-- ============================================================================

-- 1. FARMS (Read: Global for analytics; Write: Farm owner only)
DROP POLICY IF EXISTS "farms_select_policy" ON public.farms;
CREATE POLICY "farms_select_policy" ON public.farms FOR SELECT USING (true);

-- 2. CROP PLOTS (Read: Global for 2D topdown layout & admin analytics; Write: Farm owner only)
DROP POLICY IF EXISTS "crop_plots_select_policy" ON public.crop_plots;
CREATE POLICY "crop_plots_select_policy" ON public.crop_plots FOR SELECT USING (true);

-- 3. CROP LOGS (Read: Global for check-up diagnostics; Write: Author / farm owner only)
DROP POLICY IF EXISTS "crop_logs_select_policy" ON public.crop_logs;
CREATE POLICY "crop_logs_select_policy" ON public.crop_logs FOR SELECT USING (true);

-- 4. TASKS (Read: Global for calendar/tasks; Write: Farm owner only)
DROP POLICY IF EXISTS "tasks_select_policy" ON public.tasks;
CREATE POLICY "tasks_select_policy" ON public.tasks FOR SELECT USING (true);

-- 5. HARVEST RECORDS (Read: Global for yield analytics; Write: Farm owner only)
DROP POLICY IF EXISTS "harvest_records_select_policy" ON public.harvest_records;
CREATE POLICY "harvest_records_select_policy" ON public.harvest_records FOR SELECT USING (true);

-- 6. USER FAVORITE CROPS (Read: Global for donut chart & leaderboard; Write: Owner only)
DROP POLICY IF EXISTS "user_favorite_crops_select_policy" ON public.user_favorite_crops;
CREATE POLICY "user_favorite_crops_select_policy" ON public.user_favorite_crops FOR SELECT USING (true);
