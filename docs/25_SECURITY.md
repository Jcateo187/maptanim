# 25. Security & Data Protection

## 📌 Overview
MapTanim enforces multiple defense-in-depth security layers covering authentication, database Row Level Security (RLS), transport layer security, and local hardware-backed data protection. All security mechanisms apply directly to live database transactions — no authentication bypass or security fallback exists in production code paths.

---

## 🔹 Authentication Security (Email OTP)

As formalized in system specifications, MapTanim utilizes **Email OTP (via Gmail SMTP relay or Supabase Auth)** to provide secure multi-factor authentication without operational SMS gateway costs.

| Security Measure | Implementation | Detail |
|---|---|---|
| Email OTP Verification | Supabase Auth / SMTP | 6-digit numeric OTP delivered via Email |
| OTP Expiration | Supabase Auth Config | 6-digit code expires after 5 minutes |
| Rate Limiting & Lockout | Supabase Auth + Edge Function | 3 consecutive failed attempts → 15-minute account lockout |
| JWT Token Lifecycle | Supabase Auth SDK | Access token valid 1h; `alwaysAutoRefresh = true` silent refresh |
| Secure Token Storage | Android EncryptedSharedPreferences | Encrypted using AES-256-GCM key via AndroidKeyStore |
| Session Validation | `LoadingViewModel.kt` | Validates `auth.currentSessionOrNull()` on every app startup |
| Password Hashing | Supabase Auth | Bcrypt algorithm with unique salt per user |

---

## 🔹 Database Security — Row Level Security (RLS)

Every table in the Supabase PostgreSQL database enforces **Row Level Security (RLS)**. Farmers can only query, modify, or delete records belonging to their authenticated user account (`auth.uid()`).

### Core RLS Policies

```sql
-- 1. Farms: Farmers own their farm records
CREATE POLICY "farmers_own_farms" ON public.farms
    FOR ALL
    USING (auth.uid() = farmer_id)
    WITH CHECK (auth.uid() = farmer_id);

-- 2. Crop Plots: Farmer must own the parent farm
CREATE POLICY "farmers_own_plots" ON public.crop_plots
    FOR ALL
    USING (
        EXISTS (
            SELECT 1 FROM public.farms f
            WHERE f.id = crop_plots.farm_id
              AND f.farmer_id = auth.uid()
        )
    )
    WITH CHECK (
        EXISTS (
            SELECT 1 FROM public.farms f
            WHERE f.id = crop_plots.farm_id
              AND f.farmer_id = auth.uid()
        )
    );

-- 3. Crop Zones: Farmer must own the parent plot and farm
CREATE POLICY "farmers_own_crop_zones" ON public.crop_zones
    FOR ALL
    USING (
        EXISTS (
            SELECT 1 FROM public.crop_plots p
            JOIN public.farms f ON f.id = p.farm_id
            WHERE p.id = crop_zones.plot_id
              AND f.farmer_id = auth.uid()
        )
    );

-- 4. Farm Objects: Farmer must own the parent farm
CREATE POLICY "farmers_own_farm_objects" ON public.farm_objects
    FOR ALL
    USING (
        EXISTS (
            SELECT 1 FROM public.farms f
            WHERE f.id = farm_objects.farm_id
              AND f.farmer_id = auth.uid()
        )
    );

-- 5. Tasks: Farmer must own the parent farm
CREATE POLICY "farmers_own_tasks" ON public.tasks
    FOR ALL
    USING (
        EXISTS (
            SELECT 1 FROM public.farms f
            WHERE f.id = tasks.farm_id
              AND f.farmer_id = auth.uid()
        )
    );

-- 6. Notifications: User sees only their own notifications
CREATE POLICY "users_own_notifications" ON public.notifications
    FOR ALL
    USING (auth.uid() = user_id);

-- 7. Static Reference Tables: Read-only for authenticated users
CREATE POLICY "authenticated_read_crops" ON public.crops
    FOR SELECT USING (auth.role() = 'authenticated');

CREATE POLICY "authenticated_read_dss_rules" ON public.dss_rules
    FOR SELECT USING (auth.role() = 'authenticated');
```

---

## 🔹 Transport Security

| Security Component | Standard / Implementation |
|---|---|
| Protocol Enforcement | HTTPS / TLS 1.3 enforced for all network endpoints |
| HTTP Engine | Ktor HTTP client with Android engine inheriting Android system TLS stack |
| Supabase Key Separation | Anonymous publishable key (`anon`) used in mobile app; `service_role` key restricted strictly to secure backend Edge Functions |

---

## 🔹 Local Data Encryption

```kotlin
// EncryptedPreferencesManager.kt
val masterKey = MasterKey.Builder(context)
    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)   // Hardware-backed AndroidKeyStore
    .build()

val prefs = EncryptedSharedPreferences.create(
    context,
    "maptanim_secure_prefs",
    masterKey,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,   // Key encryption
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM  // Value encryption
)
```

- Auth tokens, refresh tokens, and user credentials are encrypted via `EncryptedSharedPreferences`.
- Room Database SQLite file stores cached local data for offline mode.

---

## 🔹 Data Validation & Input Sanitization

| Location | Validation Rule |
|---|---|
| Email input | Standard RFC 5322 regex validation via `Patterns.EMAIL_ADDRESS` |
| OTP input | 6-digit numeric, trimmed, non-empty |
| Plot position (`pos_x`, `pos_y`) | Coerced within farm canvas grid bounds (`0.0` to `29.0` meters) |
| Plot dimensions (`width_m`, `height_m`) | Range bounded (`1.0m` min, `20.0m` max) |
| Crop selection | Validated against static reference catalog in Room SQLite |
| Soil type | Validated against `SoilType` enum (`LOAM`, `CLAY`, `SANDY`, `SILTY`, `PEATY`, `CHALKY`) |

---

## 🔹 OWASP MASVS Checklist (v1.0 Status)

| MASVS Verification Requirement | Status | Implementation |
|---|---|---|
| V1 – Architecture & Design | ✅ Pass | Clean Architecture + MVVM + PostgreSQL RLS |
| V2 – Data Storage Security | ✅ Pass | AndroidKeyStore EncryptedSharedPreferences |
| V3 – Cryptography | ✅ Pass | AES-256-GCM / AES-256-SIV |
| V4 – Authentication & Session | ✅ Pass | Email OTP + JWT auto-refresh + lockout |
| V5 – Network Communication | ✅ Pass | Strict TLS 1.3, no HTTP cleartext fallback |
| V6 – Platform Interaction | ✅ Pass | Native Jetpack Compose UI, no insecure WebViews |
| V7 – Code Quality | ✅ Pass | Kotlin null safety, strict type checking |
