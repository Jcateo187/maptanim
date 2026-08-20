-- MapTanim Versioned Migration 007: Fix Notifications Table RLS Policies
-- Resolves RLS error 42501 ("new row violates row-level security policy for table notifications")

-- 1. Enable RLS on public.notifications
 