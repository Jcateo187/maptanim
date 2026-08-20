-- MapTanim Versioned Migration 003: User Profile Auto-Creation Trigger

CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO public.profiles (id, nickname, onboarding_completed)
  VALUES (
    NEW.id,
    COALESCE(NEW.raw_user_meta_data->>'nickname', split_part(NEW.email, '@', 1)),
    FALSE
  )
  ON CONFLICT (id) DO NOTHING;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();

-- Backfill profile records for any existing users in auth.users
INSERT INTO public.profiles (id, nickname, onboarding_completed)
SELECT 
    id,
    COALESCE(raw_user_meta_data->>'nickname', split_part(email, '@', 1)),
    FALSE
FROM auth.users
ON CONFLICT (id) DO NOTHING;
