-- ==============================================================================
-- Migration 022: Clean Users and Profiles Schema
-- Target: Supabase PostgreSQL (public schema)
-- Supports:
-- 1. Streamlines user registration strictly to Email + Password
-- 2. Permanently drops first_name, last_name, phone_number, full_name, and name
--    from both public.users and public.profiles tables
-- 3. Updates handle_new_user() trigger to maintain clean profile & user records
-- ==============================================================================

-- 1. Drop redundant personal identity columns from public.users
ALTER TABLE IF EXISTS public.users DROP COLUMN IF EXISTS first_name;
ALTER TABLE IF EXISTS public.users DROP COLUMN IF EXISTS last_name;
ALTER TABLE IF EXISTS public.users DROP COLUMN IF EXISTS phone_number;
ALTER TABLE IF EXISTS public.users DROP COLUMN IF EXISTS full_name;
ALTER TABLE IF EXISTS public.users DROP COLUMN IF EXISTS name;

-- 2. Drop redundant personal identity columns from public.profiles
ALTER TABLE IF EXISTS public.profiles DROP COLUMN IF EXISTS first_name;
ALTER TABLE IF EXISTS public.profiles DROP COLUMN IF EXISTS last_name;
ALTER TABLE IF EXISTS public.profiles DROP COLUMN IF EXISTS phone_number;
ALTER TABLE IF EXISTS public.profiles DROP COLUMN IF EXISTS full_name;
ALTER TABLE IF EXISTS public.profiles DROP COLUMN IF EXISTS name;

-- 3. Streamlined handle_new_user trigger without name or phone dependencies
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
DECLARE
  v_nickname TEXT := COALESCE(
    NEW.raw_user_meta_data->>'nickname',
    split_part(NEW.email, '@', 1),
    'Farmer'
  );
BEGIN
  -- Insert or update public.profiles (id, nickname, nickname_updated_at)
  INSERT INTO public.profiles (
    id,
    nickname,
    nickname_updated_at
  )
  VALUES (
    NEW.id,
    v_nickname,
    NOW()
  )
  ON CONFLICT (id) DO UPDATE SET
    nickname = COALESCE(EXCLUDED.nickname, public.profiles.nickname);

  -- Insert or update public.users (id, email, role)
  INSERT INTO public.users (
    id,
    email,
    role
  )
  VALUES (
    NEW.id,
    NEW.email,
    'FARMER'
  )
  ON CONFLICT (id) DO UPDATE SET
    email = EXCLUDED.email,
    updated_at = NOW();

  RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Re-attach trigger
DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();
