# 27. Deployment Guide

## 📌 Overview
This guide covers the steps to build, sign, and release MapTanim to production. It also covers Supabase production deployment and admin panel hosting.

---

## 🔹 Pre-Deployment Checklist

- [ ] `local.properties` has production Supabase URL + Anon Key
- [ ] All database migrations applied via `supabase db push`
- [ ] Edge Functions deployed: `verify-otp`, `evaluate-dss`, `generate-report`
- [ ] Storage buckets created: `crop-images`, `user-avatars`, `pest-guides`
- [ ] RLS enabled and tested on all tables
- [ ] No `@Preview` Composables active in any production code path
- [ ] Build variant set to `release` (no debug logs)
- [ ] ProGuard/R8 rules configured for Ktor and Supabase SDK

---

## 🔹 Build Signed AAB (Android App Bundle)

### 1. Generate Release Keystore (first time only)
```bash
keytool -genkey -v \
  -keystore maptanim-release.jks \
  -alias maptanim \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

Store `maptanim-release.jks` securely — **never commit to Git**.

### 2. Configure Signing in `build.gradle.kts`
```kotlin
android {
    signingConfigs {
        create("release") {
            keyAlias = localProperties["KEY_ALIAS"] as String
            keyPassword = localProperties["KEY_PASSWORD"] as String
            storeFile = file(localProperties["STORE_FILE"] as String)
            storePassword = localProperties["STORE_PASSWORD"] as String
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

### 3. Build the Release AAB
```bash
./gradlew :mobile:app:bundleRelease
```
Output: `mobile/app/build/outputs/bundle/release/app-release.aab`

### 4. Build the Release APK (for direct sideload / testing)
```bash
./gradlew :mobile:app:assembleRelease
```

---

## 🔹 ProGuard Rules for Supabase + Ktor

```
# proguard-rules.pro
-keep class io.github.jan.supabase.** { *; }
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**
-keep class kotlinx.serialization.** { *; }
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
```

---

## 🔹 Supabase Production Deployment

### 1. Link to Live Project
```bash
supabase link --project-ref ojilvcglpzbtpjxguhzj
```

### 2. Push All Migrations
```bash
supabase db push
```

### 3. Deploy Edge Functions
```bash
supabase functions deploy verify-otp --project-ref ojilvcglpzbtpjxguhzj
supabase functions deploy evaluate-dss --project-ref ojilvcglpzbtpjxguhzj
supabase functions deploy generate-report --project-ref ojilvcglpzbtpjxguhzj
```

### 4. Seed Initial Crop Data
```bash
supabase db seed --db-url postgresql://postgres.ojilvcglpzbtpjxguhzj:[password]@aws-0-ap-southeast-1.pooler.supabase.com:6543/postgres
```

### 5. Enable Realtime for Required Tables
In Supabase Studio → Database → Replication → Enable for:
- `crop_plots`
- `crop_zones`
- `farm_objects`
- `tasks`
- `notifications`

---

## 🔹 Google Play Console Upload

1. Go to [Google Play Console](https://play.google.com/console)
2. Create app listing: "MapTanim"
3. Set content rating, target audience (Philippines)
4. Upload AAB: `app-release.aab`
5. Required screenshots: **landscape orientation only**:
   - View Mode (Home Screen with farm canvas, task panel, farm summary)
   - Edit Mode (with plot selected, EDIT TOOLS panel visible)
6. Short description: "Interactive farm plot mapping with DSS for vegetable farmers"
7. Full description references 13 crops, 6 soil types, DA/PSA categories
8. Submit for review

---

## 🔹 Admin Dashboard Deployment

```bash
cd admin
npm install
npm run build

# Deploy to Vercel
npx vercel --prod

# Or Netlify
npx netlify deploy --prod --dir=dist
```

Set environment variables in hosting platform:
```
VITE_SUPABASE_URL=https://ojilvcglpzbtpjxguhzj.supabase.co
VITE_SUPABASE_ANON_KEY=sb_publishable_fH5qY2HaAg-coV89IxOl2Q_Xf9ySGMU
```

---

## 🔹 GitHub CI/CD (Optional)

`.github/workflows/build.yml` triggers on:
- Push to `develop` → build debug APK
- Push to `main` → build release AAB + push to Play Console (Fastlane/Gradle Play Publisher)

---

## 🔹 Rollback Procedure

```bash
# Revert Supabase migration
supabase db reset --linked

# Redeploy previous Edge Function version
supabase functions deploy verify-otp --version <previous-version>
```
