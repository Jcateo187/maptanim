# Document 40: User & Profile Schema Refinement (Email + Password Architecture)

> 📌 **Navigation**: [◀ 39. Crop View Interaction & Variety Simulation](file:///d:/Development/MapTanim/docs/39_CROP_VIEW_INTERACTION_AND_VARIETY_SIMULATION.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [41. Users & Profiles Database Tables ▶](file:///d:/Development/MapTanim/docs/41_USERS_AND_PROFILES_DATABASE_TABLES.md)

---
## Overview
This document outlines the removal of `full_name` from the `public.users` table and `first_name` / `last_name` from the `public.profiles` table. The application authentication architecture is streamlined so that user registration relies strictly on **Email + Password**.

---

## 1. Supabase PostgreSQL Schema Changes

### `public.users` Table Definition
```sql
CREATE TABLE IF NOT EXISTS public.users (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255)    UNIQUE, -- Nullable to support guest / anonymous users
    role            role_enum       NOT NULL DEFAULT 'FARMER',
    avatar_url      TEXT,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);
```

### `public.profiles` Table Definition
```sql
CREATE TABLE IF NOT EXISTS public.profiles (
    id                      UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    nickname                VARCHAR(100),
    avatar                  TEXT,
    nickname_updated_at     TIMESTAMPTZ,
    tutorial_completed_at   TIMESTAMPTZ,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

---

## 2. Live Supabase SQL Migration Script

Run this SQL statement in your **Supabase Dashboard → SQL Editor**:

```sql
-- Remove full_name, phone_number, first_name, last_name, and onboarding_completed columns
ALTER TABLE public.users DROP COLUMN IF EXISTS full_name;
ALTER TABLE public.users DROP COLUMN IF EXISTS phone_number;
ALTER TABLE public.profiles DROP COLUMN IF EXISTS first_name;
ALTER TABLE public.profiles DROP COLUMN IF EXISTS last_name;
ALTER TABLE public.profiles DROP COLUMN IF EXISTS onboarding_completed;

-- Support guest users (allow null email for anonymous accounts)
ALTER TABLE public.users ALTER COLUMN email DROP NOT NULL;

-- Add timestamp columns for nickname change limit and tutorial completion re-guiding
ALTER TABLE public.profiles ADD COLUMN IF NOT EXISTS nickname_updated_at TIMESTAMPTZ;
ALTER TABLE public.profiles ADD COLUMN IF NOT EXISTS tutorial_completed_at TIMESTAMPTZ;

-- Update trigger function for new users
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
```

---

## 3. Kotlin Model & Repository Updates

### [User.kt](file:///d:/Development/MapTanim/backend/src/main/java/com/maptanim/backend/data/model/User.kt)
```kotlin
@Serializable
data class User(
    val id: String,
    val email: String? = null,
    val role: String = "FARMER",
    @SerialName("avatar_url") val avatar_url: String? = null
)
```

### [Profile.kt](file:///d:/Development/MapTanim/backend/src/main/java/com/maptanim/backend/data/model/Profile.kt)
```kotlin
@Serializable
data class Profile(
    val id: String,
    val nickname: String? = null,
    val avatar: String? = null,
    @SerialName("nickname_updated_at") val nickname_updated_at: String? = null,
    @SerialName("tutorial_completed_at") val tutorial_completed_at: String? = null,
    @SerialName("created_at") val created_at: String? = null,
    @SerialName("updated_at") val updated_at: String? = null
)
```

### [AuthRepository.kt](file:///d:/Development/MapTanim/backend/src/main/java/com/maptanim/backend/data/repository/AuthRepository.kt)
```kotlin
suspend fun signUp(email: String, password: String): Result<Unit> {
    // Registers user with Supabase Auth
    // Creates public.users record with id + email
    // Creates public.profiles record with id
}
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
- 📄 [41. Users & Profiles Database Tables](file:///d:/Development/MapTanim/docs/41_USERS_AND_PROFILES_DATABASE_TABLES.md)
