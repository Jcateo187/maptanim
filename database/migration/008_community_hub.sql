-- Migration 008: Community Hub Tables & Policies for Supabase
-- Target Project: ojilvcglpzbtpjxguhzj.supabase.co

-- 1. Table: public.community_posts
CREATE TABLE IF NOT EXISTS public.community_posts (
    id                  TEXT            PRIMARY KEY DEFAULT ('post_' || substr(md5(random()::text || clock_timestamp()::text), 1, 16)),
    author_id           UUID            REFERENCES auth.users(id) ON DELETE SET NULL,
    author_name         VARCHAR(150)    NOT NULL DEFAULT 'Mobile Farmer',
    author_avatar_url   TEXT,
    category            VARCHAR(50)     NOT NULL DEFAULT 'GENERAL',
    title               VARCHAR(255)    NOT NULL,
    content             TEXT            NOT NULL,
    likes_count         INT             NOT NULL DEFAULT 0,
    comments_count      INT             NOT NULL DEFAULT 0,
    is_pinned           BOOLEAN         NOT NULL DEFAULT FALSE,
    tags                TEXT[]          NOT NULL DEFAULT '{}',
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- 2. Table: public.community_comments
CREATE TABLE IF NOT EXISTS public.community_comments (
    id                  TEXT            PRIMARY KEY DEFAULT ('comm_' || substr(md5(random()::text || clock_timestamp()::text), 1, 16)),
    post_id             TEXT            NOT NULL REFERENCES public.community_posts(id) ON DELETE CASCADE,
    author_id           UUID            REFERENCES auth.users(id) ON DELETE SET NULL,
    author_name         VARCHAR(150)    NOT NULL DEFAULT 'Farmer Partner',
    author_avatar_url   TEXT,
    content             TEXT            NOT NULL,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- 3. Enable RLS
ALTER TABLE public.community_posts ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.community_comments ENABLE ROW LEVEL SECURITY;

-- 4. RLS Policies for community_posts
DROP POLICY IF EXISTS "community_posts_select_all" ON public.community_posts;
CREATE POLICY "community_posts_select_all" ON public.community_posts
    FOR SELECT USING (true);

DROP POLICY IF EXISTS "community_posts_insert_all" ON public.community_posts;
CREATE POLICY "community_posts_insert_all" ON public.community_posts
    FOR INSERT WITH CHECK (true);

DROP POLICY IF EXISTS "community_posts_update_all" ON public.community_posts;
CREATE POLICY "community_posts_update_all" ON public.community_posts
    FOR UPDATE USING (true);

DROP POLICY IF EXISTS "community_posts_delete_all" ON public.community_posts;
CREATE POLICY "community_posts_delete_all" ON public.community_posts
    FOR DELETE USING (true);

-- 5. RLS Policies for community_comments
DROP POLICY IF EXISTS "community_comments_select_all" ON public.community_comments;
CREATE POLICY "community_comments_select_all" ON public.community_comments
    FOR SELECT USING (true);

DROP POLICY IF EXISTS "community_comments_insert_all" ON public.community_comments;
CREATE POLICY "community_comments_insert_all" ON public.community_comments
    FOR INSERT WITH CHECK (true);

DROP POLICY IF EXISTS "community_comments_update_all" ON public.community_comments;
CREATE POLICY "community_comments_update_all" ON public.community_comments
    FOR UPDATE USING (true);

DROP POLICY IF EXISTS "community_comments_delete_all" ON public.community_comments;
CREATE POLICY "community_comments_delete_all" ON public.community_comments
    FOR DELETE USING (true);

-- 6. Indexes for High Performance Queries
CREATE INDEX IF NOT EXISTS idx_community_posts_category ON public.community_posts(category);
CREATE INDEX IF NOT EXISTS idx_community_posts_created ON public.community_posts(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_community_comments_post_id ON public.community_comments(post_id);

-- 7. Seed Initial Community Posts & Comments
INSERT INTO public.community_posts (id, author_name, author_location, category, title, content, likes_count, comments_count, is_pinned, tags, created_at)
VALUES
    ('post_1', 'Mang Jose Parreño', 'Murcia, Negros Occidental', 'PEST_ALERT', '🚨 Fall Armyworm Outbreak in Murcia & Talisay Bed Plots', 'Attention fellow vegetable growers! We spotted Fall Armyworm caterpillars on early sweet corn and bean plots around Barangay Canlandog, Murcia. Spraying Neem oil extract mixed with soapy water early morning has proven effective. Check your leaves for tiny hole punctures!', 18, 2, true, ARRAY['PestAlert', 'Armyworm', 'Corn', 'Murcia'], NOW() - INTERVAL '2 hours'),
    ('post_2', 'Ka Ryan Vasquez', 'Bago City, Negros Occidental', 'FARMING_TIP', '💡 High-Yield Tomato Diamante Max F1 Double A-Frame Trellising', 'For those planting Diamante Max F1 tomato this dry season, using a 2-meter bamboo A-frame trellis with nylon twine stringing doubled our yield harvest compared to single stake poles. It provides superior airflow and keeps lower branches off damp ground.', 24, 1, false, ARRAY['FarmingTip', 'Tomato', 'Trellis', 'HighYield'], NOW() - INTERVAL '5 hours'),
    ('post_3', 'Aling Maria Juanillo', 'Silay City, Negros Occidental', 'EQUIPMENT', '🚜 Bamboo Stakes & Insect Netting Seed Swap — Extra Sitaw Seeds', 'I have 50 extra bundles of treated 6ft bamboo stakes and 3 packets of certified Sitaw (String Beans) seeds available for trade in Silay. Looking to trade for surplus Pechay or Lettuce seeds. Send me a message!', 12, 0, false, ARRAY['SeedSwap', 'BambooStakes', 'Sitaw', 'Silay'], NOW() - INTERVAL '1 day'),
    ('post_4', 'Tatay Juan Cateo', 'Murcia, Negros Occidental', 'GENERAL', '❓ Best Organic Solution for Flea Beetles on Talong Leaves?', 'Magandang araw mga kasama. My 40-day old Eggplant (Talong) plot is starting to show small pinhole damage from flea beetles. Is baking soda spray or wood ash dusting better for organic pest control without burning young leaves?', 9, 1, false, ARRAY['Question', 'Eggplant', 'OrganicPestControl', 'Talong'], NOW() - INTERVAL '2 days')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.community_comments (id, post_id, author_name, author_location, content, created_at)
VALUES
    ('comm_1', 'post_1', 'Aling Danica', 'Talisay', 'Salamat sa babala Mang Jose! Applied wood ash around our corn whorls this morning, so far it contained the spread.', NOW() - INTERVAL '1 hour'),
    ('comm_2', 'post_1', 'Jason B.', 'Bacolod', 'You can also release Trichogramma parasitic wasps from the BPI office to control egg clusters naturally.', NOW() - INTERVAL '45 minutes'),
    ('comm_3', 'post_2', 'James C.', 'Murcia', 'Tested this A-frame method on plot 3 last week! Stems are upright even after heavy afternoon wind.', NOW() - INTERVAL '3 hours'),
    ('comm_4', 'post_4', 'Ka Ryan Vasquez', 'Bago', 'Wood ash mixed with dry sand (1:1 ratio) dusted lightly early morning while dew is present works best against flea beetles!', NOW() - INTERVAL '1 day')
ON CONFLICT (id) DO NOTHING;
