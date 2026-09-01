-- MapTanim Versioned Migration 006: Support & Feedback Bridge with Notification Dispatch
-- Enables mobile users to send reports/queries and receives admin replies via live Supabase notifications

-- 1. Create public.feedback table if missing
CREATE TABLE IF NOT EXISTS public.feedback (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID,
    farmer_name VARCHAR(150) NOT NULL DEFAULT 'Mobile Farmer',
    farm_name VARCHAR(150),
    category VARCHAR(50) NOT NULL DEFAULT 'GENERAL',
    subject TEXT NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    admin_reply TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    resolved_at TIMESTAMP WITH TIME ZONE
);

-- 2. Add status constraint check
ALTER TABLE public.feedback DROP CONSTRAINT IF EXISTS check_feedback_status;
ALTER TABLE public.feedback ADD CONSTRAINT check_feedback_status CHECK (status IN ('PENDING', 'IN_PROGRESS', 'RESOLVED'));

-- 3. Enable RLS on public.feedback
ALTER TABLE public.feedback ENABLE ROW LEVEL SECURITY;

-- 4. RLS Policies for public.feedback
DROP POLICY IF EXISTS "feedback_read_all" ON public.feedback;
CREATE POLICY "feedback_read_all" ON public.feedback FOR SELECT USING (true);

DROP POLICY IF EXISTS "feedback_insert_all" ON public.feedback;
CREATE POLICY "feedback_insert_all" ON public.feedback FOR INSERT WITH CHECK (true);

DROP POLICY IF EXISTS "feedback_update_all" ON public.feedback;
CREATE POLICY "feedback_update_all" ON public.feedback FOR UPDATE USING (true);

-- 5. Seed initial demonstration feedback item if table is empty
INSERT INTO public.feedback (farmer_name, farm_name, category, subject, message, status)
SELECT 'Juan Dela Cruz', 'Dela Cruz Organic Farm', 'PEST_DISEASE', 'Aphid Infestation on Tomato Beds', 'Noticed yellowing leaves and small insects under tomato leaves in Plot B. Requesting advice.', 'PENDING'
WHERE NOT EXISTS (SELECT 1 FROM public.feedback);
