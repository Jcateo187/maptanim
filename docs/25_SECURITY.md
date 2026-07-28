# 25. Security

## 📌 Overview
MapTanim enforces multiple layers of security covering authentication, data access control, transport security, and local data protection. All security measures apply to live data — no security bypass exists for mock or demo modes.

---

## 🔹 Authentication Security

| Measure | Implementation | Detail |
|---------|---------------|--------|
| OTP expiry | Supabase Auth | 6-digit code expires in 5 minutes |
| Attempt lockout | Supabase Auth + Edge Function | 3 failed attempts → 15-minute lock |
| JWT auto-refresh | Supabase SDK | `alwaysAutoRefresh = true` — silent background refresh |
| Token storage | Android EncryptedSharedPreferences | AES-256-GCM key via AndroidKeyStore |
| Session validation | LoadingViewModel | Checks `auth.currentSessionOrNull()` on every app start |
| Password hashing | Supabase Auth internal | Bcrypt with salt — developer never sees plaintext |

---

## 🔹 Database Security — Row Level Security (RLS)

All tables in the Supabase PostgreSQL database enforce **Row Level Security**. Farmers can only access their own data.

### Core RLS Policy Pattern

```sql
-- Farmers can only SELECT/INSERT/UPDATE/DELETE their own farms
CREATE POLICY "farmers_own_farms" ON public.farms
    FOR ALL
    USING (auth.uid() = farmer_id)
    WITH CHECK (auth.uid() = farmer_id);

-- Beds: farmer must own the parent farm
CREATE POLICY "farmers_own_beds" ON public.beds
    FOR ALL
    USING (
        EXISTS (
            SELECT 1 FROM public.farms f
            WHERE f.id = beds.farm_id
              AND f.farmer_id = auth.uid()
        )
    )
    WITH CHECK (
        EXISTS (
            SELECT 1 FROM public.farms f
            WHERE f.id = beds.farm_id
              AND f.farmer_id = auth.uid()
        )
    );

-- Tasks: farmer must own the parent farm
CREATE POLICY "farmers_own_tasks" ON public.tasks
    FOR ALL
    USING (
        EXISTS (
            SELECT 1 FROM public.farms f
            WHERE f.id = tasks.farm_id
              AND f.farmer_id = auth.uid()
        )
    );

-- Notifications: user sees only own notifications
CREATE POLICY "users_own_notifications" ON public.notifications
    FOR ALL USING (auth.uid() = user_id);

-- Static reference tables: read-only for all authenticated users
CREATE POLICY "authenticated_read_crops" ON public.crops
    FOR SELECT USING (auth.role() = 'authenticated');

CREATE POLICY "authenticated_read_dss_rules" ON public.dss_rules
    FOR SELECT USING (auth.role() = 'authenticated');
```

---

## 🔹 Transport Security

| Layer | Implementation |
|-------|---------------|
| Protocol | HTTPS / TLS 1.3 enforced |
| Certificate pinning | Not enforced in v1.0 (planned for v2.0) |
| HTTP client | Ktor with Android engine (inherits system TLS stack) |
| Supabase key exposure | Publishable (anon) key only in client — `service_role` key server-side only |

---

## 🔹 Local Storage Security

```kotlin
// EncryptedPreferencesManager.kt
val masterKey = MasterKey.Builder(context)
    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)   // Backed by Android KeyStore
    .build()

val prefs = EncryptedSharedPreferences.create(
    context,
    "maptanim_secure_prefs",
    masterKey,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,   // Key encryption
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM  // Value encryption
)
```

- Access tokens, refresh tokens stored encrypted — not in plain SharedPreferences
- Room database file: not encrypted in v1.0 (SQLCipher planned for v2.0 if sensitive farm data grows)

---

## 🔹 AGENTS.md Security Rule

> The `AGENTS.md` file in the project root enforces the following rule for all AI agents working on this codebase:
> **"Never add static, mock, hardcoded, or demo data to any production code path. All data shown to users must originate from Supabase (via PostgREST, Edge Functions, or Realtime) with Room as the local cache. Preview-only Composables annotated with `@Preview` may use local PreviewData objects, but these must never be called from any ViewModel or Repository."**

---

## 🔹 Data Validation

| Location | Validation |
|----------|-----------|
| OTP input | 6-digit numeric, trimmed, non-empty |
| Email input | Android `Patterns.EMAIL_ADDRESS` regex |
| Bed position (pos_x, pos_y) | Must be ≥ 0.0 and within farm bounds |
| Bed dimensions (width_m, height_m) | Must be > 0.0, max 20.0m |
| Crop name | Must exist in `crops` table — validated against local Room cache |
| Soil type | Must be a valid `SoilType` enum value |

---

## 🔹 OWASP MASVS Checklist (v1.0 Status)

| Control | Status | Implementation |
|---------|--------|---------------|
| V1 – Arch & Design | ✅ | MVVM + Clean Architecture, RLS |
| V2 – Data Storage | ✅ | EncryptedSharedPreferences, no plaintext secrets |
| V3 – Cryptography | ✅ | AES-256-GCM via AndroidKeyStore |
| V4 – Auth | ✅ | OTP + JWT, auto-refresh, lockout |
| V5 – Network | ✅ | TLS 1.3, no HTTP fallback |
| V6 – Platform | ⚠️ | WebView not used; deeplinks not implemented |
| V7 – Code Quality | ✅ | Kotlin null safety, no `!!` operator |
| V8 – Resilience | 🔲 | Planned: root detection, anti-tampering |

---

## 🔹 No Static/Mock Data Security Implication

Using static or hardcoded data creates false security assumptions:
- **Authentication bypass risk**: Demo credentials embedded in code can be extracted via reverse engineering.
- **RLS bypass risk**: If a ViewModel uses a hardcoded farmer ID instead of `auth.uid()`, RLS policies are meaningless.
- **Data integrity risk**: Static farm data shown to users may be stale, incorrect, or belong to a different user.

**MapTanim enforces**: every piece of data rendered in the UI comes from `auth.uid()` → Supabase RLS → Room → ViewModel → Compose. No exceptions.
