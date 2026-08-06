# 05. Backend Architecture — Supabase BaaS

> 📌 **Navigation**: [◀ 04. Android Architecture](file:///d:/Development/MapTanim/docs/04_ANDROID_ARCHITECTURE.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [06. Admin Dashboard ▶](file:///d:/Development/MapTanim/docs/06_ADMIN_DASHBOARD.md)

---
## 📌 Overview
MapTanim uses **Supabase** as its complete Backend-as-a-Service. No custom server (Spring Boot, Express, etc.) is required for v1.0. The Android app communicates directly with Supabase via the official Kotlin SDK.

**Live Project URL**: `https://ojilvcglpzbtpjxguhzj.supabase.co`

---

## 🔹 Supabase Project Configuration

### Project Details
| Property | Value |
|----------|-------|
| Project Reference | `ojilvcglpzbtpjxguhzj` |
| Region | Southeast Asia (ap-southeast-1) |
| Database | PostgreSQL 15.x |
| Auth Provider | Email OTP |

### Client Initialization (SupabaseClient.kt)
```kotlin
// backend/src/main/java/com/maptanim/backend/data/remote/SupabaseClient.kt
val supabaseClient = createSupabaseClient(
    supabaseUrl = "https://ojilvcglpzbtpjxguhzj.supabase.co",
    supabaseKey = "sb_publishable_fH5qY2HaAg-coV89IxOl2Q_Xf9ySGMU"
) {
    install(Auth) {
        autoLoadFromStorage = true
        alwaysAutoRefresh = true
        platformType = AuthPlatformType.PHONE
    }
    install(Postgrest)
    install(Storage)
}
```

---

## 🔹 Supabase Auth

| Property | Configuration |
|----------|-------------|
| Provider | Email + OTP (Magic Link / 6-digit code) |
| Code expiry | 5 minutes |
| Attempt limit | 3 failed → 15-minute lockout |
| JWT storage | Android `EncryptedSharedPreferences` |
| Auto-refresh | Enabled (`alwaysAutoRefresh = true`) |
| Session persistence | Enabled (`autoLoadFromStorage = true`) |
| SMTP | Gmail / SendGrid via Supabase SMTP settings |

---

## 🔹 PostgreSQL Tables

All tables use UUID primary keys and include RLS. See `07_DATABASE_DESIGN.md` for full SQL schema.

| Table | Records | Purpose |
|-------|---------|---------|
| `users` | Per user | Farmer profiles and roles |
| `farms` | Per farmer | Farm registry |
| `beds` | Per farm | Planting bed layout (positions, soil, crops) |
| `crops` | Static | 13+ high-value vegetable reference data |
| `tasks` | Per farm | DSS-generated daily task list |
| `activities` | Per bed | Manual activity log |
| `harvest_records` | Per bed | Yield tracking |
| `dss_rules` | Static | Companion planting matrix |
| `notifications` | Per user | Notification center content |

---

## 🔹 Row Level Security (RLS)

All user-owned tables enforce isolation via:
```sql
-- Template policy: farmer sees only their own data
CREATE POLICY "policy_name" ON public.table_name
    FOR ALL USING (
        auth.uid() = farmer_id
        -- or via JOIN for child tables
    );
```

Static tables (`crops`, `dss_rules`) are read-only for all authenticated users:
```sql
CREATE POLICY "public_read_crops" ON public.crops
    FOR SELECT USING (true);
```

---

## 🔹 Supabase Storage Buckets

| Bucket Name | Access | Purpose |
|-------------|--------|---------|
| `crop-images` | Public read | Crop illustration images per crop type |
| `user-avatars` | Authenticated read | Farmer profile photo |
| `pest-guides` | Authenticated read | Pest identification guides (PDF/images) |

### Storage Upload (Example — avatar)
```kotlin
supabaseClient.storage["user-avatars"].upload(
    path = "${userId}/avatar.jpg",
    data = imageByteArray,
    upsert = true
)
```

---

## 🔹 Edge Functions

Serverless functions hosted on Supabase (Deno + TypeScript runtime):

### `verify-otp`
- **Endpoint**: `POST /functions/v1/verify-otp`
- **Input**: `{ email: string, otp: string }`
- **Output**: `{ access_token: string, refresh_token: string, user: User }`
- **Logic**: Verifies OTP via Supabase Admin SDK, enforces attempt counting

### `evaluate-dss`
- **Endpoint**: `POST /functions/v1/evaluate-dss`
- **Input**: `{ farm_id: string, evaluation_date: string }`
- **Output**: `{ tasks: Task[], companion_alerts: Alert[], soil_scores: SoilScore[] }`
- **Logic**: Reads beds + crops, evaluates DSS rules, returns task recommendations

### `generate-report`
- **Endpoint**: `POST /functions/v1/generate-report`
- **Input**: `{ farm_id: string, from_date: string, to_date: string }`
- **Output**: Farm summary report data (JSON or PDF stream)

---

## 🔹 Realtime Subscriptions

```kotlin
// Subscribe to plot layout changes for real-time collaboration
supabaseClient.realtime.createChannel("farm-$farmId")
    .on<CropPlotEntity>(PostgresAction.Update, schema = "public", table = "crop_plots") { change ->
        cropPlotRepository.applyServerChange(change.record)
    }
    .subscribe()
```

---

## 🔹 Supabase CLI Commands

```bash
# Link to live project
supabase link --project-ref ojilvcglpzbtpjxguhzj

# Apply all migrations to cloud DB
supabase db push

# Generate TypeScript types for admin panel
supabase gen types typescript --project-id ojilvcglpzbtpjxguhzj > types/supabase.ts

# Deploy an Edge Function
supabase functions deploy verify-otp --project-ref ojilvcglpzbtpjxguhzj

# Open Supabase studio locally
supabase studio
```

---

## 📚 Related Documentation & Cross References
- 📄 [Master Documentation Hub](file:///d:/Development/MapTanim/docs/README.md)
- 📄 [00. Getting Started Guide](file:///d:/Development/MapTanim/docs/00_GETTING_STARTED.md)
- 📄 [03. System Architecture](file:///d:/Development/MapTanim/docs/03_SYSTEM_ARCHITECTURE.md)
- 📄 [04. Android Architecture](file:///d:/Development/MapTanim/docs/04_ANDROID_ARCHITECTURE.md)
- 📄 [06. Admin Dashboard](file:///d:/Development/MapTanim/docs/06_ADMIN_DASHBOARD.md)
- 📄 [42. Scalability & Multi-Tenancy Architecture](file:///d:/Development/MapTanim/docs/42_SCALABILITY_AND_MULTI_TENANCY_ARCHITECTURE.md)
