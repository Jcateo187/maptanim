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

## 🔹 PostgreSQL Tables (10 Core Relational Tables)

All tables use UUID primary keys and enforce Row Level Security (RLS). Obsolete tables (`farm_tiles`, `farm_objects`, `tile_plantings`, `planting_monitors`, `planting_harvests`, `crop_profiles`) have been purged via Migration 020 (`020_cleanup_redundant_schema.sql`). 

> 💡 **Frontend-Only Note**: The 45×45 isometric grid (`farm_tiles`) is computed entirely in Jetpack Compose canvas memory, and environmental scenery (`farm_objects` like trees and fences) is rendered purely via background image layers without wasting database storage.

| Table | Records | Purpose |
|-------|---------|---------|
| `users` & `profiles` | Per user | Farmer accounts, avatars, nicknames, and role definitions |
| `farms` | Per farmer | Farm registry and metadata (zero GPS / zero location coordinates) |
| `crop_plots` | Per farm | Direct-soil planting plots (`pos_x`, `pos_y`, `width_m`, `height_m`, `crop_name`, `soil_type`, `planted_date`) |
| `crop_zones` | Per plot | Sub-regions within plots for fine-grained multi-plant organization |
| `crops` | Static (Admin) | Canonical 15 Philippine vegetable crops reference data |
| `dss_rules` | Static (Admin) | 58 bi-directional companion planting rules & agronomic science reasoning |
| `tasks` | Per farm / plot | Daily actionable tasks (`WATER`, `FERTILIZE`, `HARVEST`, `PEST_ALERT`) |
| `harvest_records` | Per plot | Yield tracking (weight in kg, quality rating, harvest date) synced to Admin KPIs |
| `feedback` | Per user | Farmer feedback tickets and bug reports |
| `community_posts` | Per user | Authentic farmer forum discussions, reactions, comments, and moderation |
| `notifications` | Per user | Broadcast announcements, pest alerts, and system notifications |

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

## 🔹 Supabase Storage Buckets (Zero Cloudflare Architecture)

MapTanim operates with **100% Pure Supabase Storage** with $0 egress fees on the free tier. Legacy Cloudflare Workers and R2 dependencies have been completely removed.

| Bucket Name | Access | Purpose |
|-------------|--------|---------|
| `crop-images` | Public read | High-resolution 30–50KB WebP crop illustrations and sprites for OTA catalog sync |
| `user-avatars` | Authenticated read | Farmer profile avatar images |
| `feedback-attachments`| Authenticated read | Screenshot attachments for farmer support tickets |

---

## 🔹 Serverless Microservices Tier (Supabase Edge Functions)

Serverless microservices hosted on Supabase Edge Functions (Deno + TypeScript runtime) provide deterministic, zero-weather agroecological intelligence:

### 1. `evaluate-dss`
- **Endpoint**: `POST /functions/v1/evaluate-dss`
- **Payload**: `{ farm_id: string, evaluation_date: string }`
- **Architecture**:
  - **Spatial Proximity Engine**: Computes Euclidean distance $d = \sqrt{(x_1-x_2)^2 + (y_1-y_2)^2}$ between active planted plots on the 45×45 isometric canvas. Flagged as neighbors when $d \le 3.0\text{ meters}$.
  - **Companion Matrix Evaluator**: Queries `dss_rules` for pairwise crop pairings. Flags `BENEFICIAL` companion synergies and `ANTAGONIST` risks.
  - **5-Stage Phenological Timeline Engine**:
    1. `SPROUT` (0% – 15% of maturity duration)
    2. `SEEDLING` (15% – 35% of maturity duration)
    3. `VEGETATIVE` (35% – 65% of maturity duration)
    4. `FLOWERING` (65% – 90% of maturity duration)
    5. `HARVEST` (90%+ of maturity duration)
  - **Dynamic Task Generator**: Evaluates watering cadences (`crop.watering_interval_days`), fertilizing cadences (`crop.fertilize_interval_days`), harvest readiness, and schedules `PEST_ALERT` inspection tasks for antagonistic neighbors.
  - **Database Upsert**: Deduplicates and batch-upserts tasks directly into `public.tasks` for the mobile `TodaysTasksOverlay.kt`.
  - **Zero-Weather Guarantee**: Operates with 100% deterministic local agroecological science without external weather APIs or GPS latency.

### 2. `broadcast-dispatcher`
- **Endpoint**: `POST /functions/v1/broadcast-dispatcher`
- **Payload**: `{ title: string, body: string, notification_type: string, user_id?: string }`
- **Architecture**:
  - Validated by `service_role` JWT secret key.
  - Inserts urgent agricultural bulletins, pest alerts, and seasonal notices directly into `public.notifications`.
  - Instantly accessible across all registered mobile farmers via the in-app notification center.

### 3. `verify-otp`
- **Endpoint**: `POST /functions/v1/verify-otp`
- **Payload**: `{ email: string, otp: string }`
- **Logic**: Verifies passwordless OTP via Supabase Admin SDK, enforcing brute-force lockouts.

---

## 🔹 Realtime Subscriptions

```kotlin
// Subscribe to crop plot layout changes for real-time state synchrony
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
