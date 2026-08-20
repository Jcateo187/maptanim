# MapTanim Database Specification: `users` & `profiles` Tables

> 📌 **Navigation**: [◀ 40. User & Profile Schema Refinement](file:///d:/Development/MapTanim/docs/40_USER_AND_PROFILE_SCHEMA_REFINEMENT.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [42. Scalability & Multi-Tenancy Architecture ▶](file:///d:/Development/MapTanim/docs/42_SCALABILITY_AND_MULTI_TENANCY_ARCHITECTURE.md)

---
This document contains the exact database table specifications and executable SQL scripts for `public.users` and `public.profiles` tables in **Supabase PostgreSQL**.

---

## 1. Table: `public.users`

Stores primary account records linked to Supabase Authentication.

### Column Specification
| Column Name | Data Type | Constraints | Default Value | Description |
|-------------|-----------|-------------|---------------|-------------|
| `id` | `UUID` | `PRIMARY KEY` | `gen_random_uuid()` | Unique user identifier (matches `auth.users.id`) |
| `email` | `VARCHAR(255)` | `UNIQUE` | `NULL` | User email address (nullable to support guest/anonymous users) |
| `role` | `public.role_enum` | `NOT NULL` | `'FARMER'` | User access role (`'FARMER'`, `'ADMIN'`) |
| `avatar_url` | `TEXT` | `NULL` | `NULL` | Optional avatar image URL |
| `created_at` | `TIMESTAMPTZ` | `NOT NULL` | `NOW()` | Account creation timestamp |
| `updated_at` | `TIMESTAMPTZ` | `NOT NULL` | `NOW()` | Record last update timestamp |

### Row Level Security (RLS) Policies
- **Security Mode**: `ENABLED`
- **Policy `users_own_data`**: Users can read and write only their own user record (`auth.uid() = id`).

---

## 2. Table: `public.profiles`

Stores user profile settings.

### Column Specification
| Column Name | Data Type | Constraints | Default Value | Description |
|-------------|-----------|-------------|---------------|-------------|
| `id` | `UUID` | `PRIMARY KEY`, `REFERENCES auth.users(id) ON DELETE CASCADE` | *None* | Foreign key referencing Supabase Auth user ID |
| `nickname` | `VARCHAR(100)` | `NULL` | *None* | Display name / nickname shown in top bar |
| `avatar` | `TEXT` | `NULL` | *None* | Asset path for selected profile avatar (e.g. `Avatar/Male_Avatar.png`) |
| `nickname_updated_at` | `TIMESTAMPTZ` | `NULL` | `NULL` | Timestamp of last nickname change (enforces 15-day limit) |
| `tutorial_completed_at` | `TIMESTAMPTZ` | `NULL` | `NULL` | Timestamp of tutorial completion / last spotlight guide display |
| `created_at` | `TIMESTAMPTZ` | `NOT NULL` | `NOW()` | Profile creation timestamp |
| `updated_at` | `TIMESTAMPTZ` | `NOT NULL` | `NOW()` | Profile last update timestamp |

### Row Level Security (RLS) Policies
- **Security Mode**: `ENABLED`
- **`profiles_select_own`**: `FOR SELECT USING (auth.uid() = id)`
- **`profiles_insert_own`**: `FOR INSERT WITH CHECK (auth.uid() = id)`
- **`profiles_update_own`**: `FOR UPDATE USING (auth.uid() = id)`

---

## 3. Automatic User Profile Trigger

When a new user registers via Supabase Auth, a trigger automatically inserts a default profile row into `public.profiles`:

```sql
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
  RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();
```

---

## 4. Complete Executable SQL Script

Paste and run this SQL in your **Supabase Dashboard → SQL Editor**:

```sql
-- 1. Enum Definition (if not existing)
DO $$ BEGIN
    CREATE TYPE public.role_enum AS ENUM ('FARMER', 'ADMIN');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- 2. Create public.users Table
CREATE TABLE IF NOT EXISTS public.users (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255)    UNIQUE, -- Nullable for guest accounts
    role            role_enum       NOT NULL DEFAULT 'FARMER',
    avatar_url      TEXT,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- Migrations for existing deployments:
ALTER TABLE public.users DROP COLUMN IF EXISTS full_name;
ALTER TABLE public.users DROP COLUMN IF EXISTS phone_number;
ALTER TABLE public.users ALTER COLUMN email DROP NOT NULL;

ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "users_own_data" ON public.users;
CREATE POLICY "users_own_data" ON public.users
    FOR ALL USING (auth.uid() = id);

-- 3. Create public.profiles Table
CREATE TABLE IF NOT EXISTS public.profiles (
    id                      UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    nickname                VARCHAR(100),
    avatar                  TEXT,
    nickname_updated_at     TIMESTAMPTZ,
    tutorial_completed_at   TIMESTAMPTZ,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Migrations for existing deployments:
ALTER TABLE public.profiles DROP COLUMN IF EXISTS first_name;
ALTER TABLE public.profiles DROP COLUMN IF EXISTS last_name;
ALTER TABLE public.profiles DROP COLUMN IF EXISTS onboarding_completed;
ALTER TABLE public.profiles ADD COLUMN IF NOT EXISTS nickname_updated_at TIMESTAMPTZ;
ALTER TABLE public.profiles ADD COLUMN IF NOT EXISTS tutorial_completed_at TIMESTAMPTZ;

ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "profiles_select_own" ON public.profiles;
CREATE POLICY "profiles_select_own" ON public.profiles
    FOR SELECT USING (auth.uid() = id);

DROP POLICY IF EXISTS "profiles_insert_own" ON public.profiles;
CREATE POLICY "profiles_insert_own" ON public.profiles
    FOR INSERT WITH CHECK (auth.uid() = id);

DROP POLICY IF EXISTS "profiles_update_own" ON public.profiles;
CREATE POLICY "profiles_update_own" ON public.profiles
    FOR UPDATE USING (auth.uid() = id);

-- 4. Trigger Function & Trigger Setup
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO public.profiles (id, nickname, nickname_updated_at)
  VALUES (
    NEW.id,
    COALESCE(NEW.raw_user_meta_data->>'nickname', split_part(NEW.email, '@', 1)),
    NOW()
  )
  ON CONFLICT (id) DO NOTHING;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();
```

---

## 📚 Related Documentation & Cross References
- 📄 [Master Documentation Hub](file:///d:/Development/MapTanim/docs/README.md)
- 📄 [00. Getting Started Guide](file:///d:/Development/MapTanim/docs/00_GETTING_STARTED.md)
- 📄 [03. System Architecture](file:///d:/Development/MapTanim/docs/03_SYSTEM_ARCHITECTURE.md)
- 📄 [07. Database Design](file:///d:/Development/MapTanim/docs/07_DATABASE_DESIGN.md)
- 📄 [08. Supabase Configuration](file:///d:/Development/MapTanim/docs/08_SUPABASE_CONFIGURATION.md)
- 📄 [09. Authentication](file:///d:/Development/MapTanim/docs/09_AUTHENTICATION.md)
- 📄 [24. Offline Synchronization](file:///d:/Development/MapTanim/docs/24_OFFLINE_SYNCHRONIZATION.md)
- 📄 [25. Security & RLS](file:///d:/Development/MapTanim/docs/25_SECURITY.md)
- 📄 [40. User & Profile Schema Refinement](file:///d:/Development/MapTanim/docs/40_USER_AND_PROFILE_SCHEMA_REFINEMENT.md)
