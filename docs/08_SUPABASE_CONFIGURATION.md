# 08. Supabase Configuration

## 📌 Project Credentials

| Property | Value |
|----------|-------|
| **Project URL** | `https://ojilvcglpzbtpjxguhzj.supabase.co` |
| **Publishable (Anon) Key** | `sb_publishable_fH5qY2HaAg-coV89IxOl2Q_Xf9ySGMU` |
| **Project Reference** | `ojilvcglpzbtpjxguhzj` |

> ⚠️ The publishable key is safe to include in client apps — it enforces RLS policies. Never expose the `service_role` key in the mobile app.

---

## 🔹 Android SDK Setup

### Gradle Dependencies (`libs.versions.toml`)
```toml
[versions]
kotlin-supabase = "3.1.4"
ktor = "3.1.3"

[libraries]
supabase-auth = { module = "io.github.jan-tennert.supabase:auth-kt", version.ref = "kotlin-supabase" }
supabase-postgrest = { module = "io.github.jan-tennert.supabase:postgrest-kt", version.ref = "kotlin-supabase" }
supabase-storage = { module = "io.github.jan-tennert.supabase:storage-kt", version.ref = "kotlin-supabase" }
ktor-android = { module = "io.ktor:ktor-client-android", version.ref = "ktor" }
```

### SupabaseClient.kt (full initialization)
```kotlin
package com.maptanim.backend.data.remote

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

val supabaseClient = createSupabaseClient(
    supabaseUrl = "https://ojilvcglpzbtpjxguhzj.supabase.co",
    supabaseKey = "sb_publishable_fH5qY2HaAg-coV89IxOl2Q_Xf9ySGMU"
) {
    install(Auth) {
        autoLoadFromStorage = true
        alwaysAutoRefresh = true
    }
    install(Postgrest)
    install(Storage)
}
```

---

## 🔹 `local.properties` Configuration
```properties
sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
SUPABASE_URL=https://ojilvcglpzbtpjxguhzj.supabase.co
SUPABASE_ANON_KEY=sb_publishable_fH5qY2HaAg-coV89IxOl2Q_Xf9ySGMU
```

### Reading in `build.gradle.kts`
```kotlin
val localProperties = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}

android {
    defaultConfig {
        buildConfigField("String", "SUPABASE_URL",
            "\"${localProperties["SUPABASE_URL"]}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY",
            "\"${localProperties["SUPABASE_ANON_KEY"]}\"")
    }
}
```

---

## 🔹 Supabase Auth Configuration

```
Authentication → Email → Enable Email OTP
OTP Expiry: 300 seconds (5 minutes)
Redirect URL: (leave blank for mobile)
SMTP: Gmail or SendGrid
```

### SMTP Settings (example via Gmail)
```
SMTP Host: smtp.gmail.com
SMTP Port: 587
SMTP User: noreply@maptanim.app
SMTP Pass: [app-specific password]
Sender Name: MapTanim
```

---

## 🔹 Storage Bucket Configuration

```sql
-- Create buckets via Supabase Studio or CLI
INSERT INTO storage.buckets (id, name, public)
VALUES
    ('crop-images', 'crop-images', true),
    ('user-avatars', 'user-avatars', false),
    ('pest-guides', 'pest-guides', false);
```

### Storage RLS Policies
```sql
-- crop-images: public read
CREATE POLICY "Public crop images" ON storage.objects
    FOR SELECT USING (bucket_id = 'crop-images');

-- user-avatars: only own avatar
CREATE POLICY "Farmers access own avatar" ON storage.objects
    FOR ALL USING (
        bucket_id = 'user-avatars'
        AND (storage.foldername(name))[1] = auth.uid()::text
    );
```

---

## 🔹 CLI Setup & Migration Commands

```bash
# 1. Install Supabase CLI
npm install -g supabase

# 2. Login
supabase login

# 3. Link to project
supabase link --project-ref ojilvcglpzbtpjxguhzj

# 4. Pull remote schema (initial sync)
supabase db pull

# 5. Create a new migration
supabase migration new add_harvest_records_table

# 6. Push all migrations to cloud
supabase db push

# 7. Seed data
supabase db seed

# 8. Deploy an Edge Function
supabase functions deploy evaluate-dss --project-ref ojilvcglpzbtpjxguhzj

# 9. View logs
supabase functions logs evaluate-dss
```

---

## 🔹 Realtime Setup

Enable Realtime in Supabase Studio:

```
Database → Replication → Enable for tables:
  ✅ beds
  ✅ tasks
  ✅ notifications
```

```kotlin
// Subscribe in ViewModel or Repository
val channel = supabaseClient.realtime.createChannel("farm-updates-$farmId")
channel
    .on<BedEntity>(PostgresAction.Update, schema = "public", table = "beds") { change ->
        handleBedUpdate(change.record)
    }
    .on<TaskEntity>(PostgresAction.Insert, schema = "public", table = "tasks") { change ->
        handleNewTask(change.record)
    }
channel.subscribe()

// On ViewModel onCleared()
override fun onCleared() {
    supabaseClient.realtime.removeChannel(channel)
}
```
