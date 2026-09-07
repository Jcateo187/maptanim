-- Migration 008: Community Hub Tables & Policies for Supabase
-- Target Project: ojilvcglpzbtpjxguhzj.supabase.co

-- 1. Table: public.community_posts
CREATE TABLE IF NOT EXISTS public.community_posts (
    id                  TEXT            PRIMARY KEY DEFAULT ('post_' || substr(md5(random()::text || clock_timestamp()::text), 1, 16)),
    author_id           UUID            REFERENCES auth.users(id) ON DELETE SET NULL,
    author_name         VARCHAR(150)    NOT NULL,
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
    author_name         VARCHAR(150)    NOT NULL,
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


