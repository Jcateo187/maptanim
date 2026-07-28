# 00. Getting Started Guide

## 📌 Overview
Welcome to **MapTanim** — a native Android farm management application built with **Kotlin** and **Jetpack Compose**, backed by **Supabase** as a Backend-as-a-Service (BaaS). This guide covers every step from environment setup to running the application.

- **GitHub**: [https://github.com/Jcateo187/maptanim.git](https://github.com/Jcateo187/maptanim.git)
- **Supabase Project**: [https://ojilvcglpzbtpjxguhzj.supabase.co](https://ojilvcglpzbtpjxguhzj.supabase.co)

---

## 🛠️ Prerequisites & Requirements

| Component | Required Version | Description |
|-----------|------------------|-------------|
| **OS** | Windows 10/11, macOS Ventura+, Ubuntu 22.04+ | 64-bit OS required |
| **JDK** | JDK 17 (Azul Zulu or OpenJDK 17) | Gradle 8.x + Kotlin 2.0+ requirement |
| **IDE** | Android Studio Koala (2024.1.1+) | Primary development IDE |
| **Android SDK** | API Level 34 (Android 14) | Target SDK |
| **Build Tools** | Android SDK Build-Tools 34.0.0 | Required by Gradle |
| **Kotlin** | 2.0.0+ | Language version |
| **Gradle** | 9.4.1 | Build system (included via wrapper) |
| **Node.js** | v18+ | Required for Supabase CLI |
| **Supabase CLI** | Latest | Database migrations and Edge Functions |
| **Git** | 2.40+ | Version control |

---

## 🚀 Environment Setup Step-by-Step

### Step 1 — Clone the Repository
```bash
git clone https://github.com/Jcateo187/maptanim.git
cd MapTanim
```

### Step 2 — Configure `local.properties`
Edit or create `local.properties` in the project root:
```properties
sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
SUPABASE_URL=https://ojilvcglpzbtpjxguhzj.supabase.co
SUPABASE_ANON_KEY=sb_publishable_fH5qY2HaAg-coV89IxOl2Q_Xf9ySGMU
```

> **Note**: Replace `YourUsername` with your Windows username. Do NOT commit `local.properties` to version control — it is listed in `.gitignore`.

### Step 3 — Open in Android Studio
1. Launch **Android Studio Koala** or newer.
2. Select **File → Open** and navigate to the `MapTanim` root folder.
3. Wait for **Gradle Sync** to complete (may take 2–5 minutes on first run).
4. Verify the Kotlin plugin version in `gradle/libs.versions.toml` is `2.0.0` or higher.

### Step 4 — Verify Supabase Connection (Optional Local CLI)
```bash
# Install Supabase CLI globally
npm install -g supabase

# Link to the live project
cd backend/supabase
supabase link --project-ref ojilvcglpzbtpjxguhzj

# Push database migrations
supabase db push
```

### Step 5 — Run the Application
1. Open **Android Studio** → select the **`mobile.app`** run configuration.
2. Choose an emulator or physical Android device (API 26+).
3. **Important**: Set emulator to **Landscape Orientation** — MapTanim is landscape-first.
4. Press **Run** (`Shift + F10`) or click the green ▶ play button.

---

## ✅ Smoke Test Checklist

After the app launches, verify each step:

| Step | Expected Result |
|------|-----------------|
| 1. App starts | Splash Screen with MAPTANIM logo appears |
| 2. Loading | LoadingScreen with progress, transitions to Home |
| 3. Auth | Welcome screen shows Login / Register / Guest Mode |
| 4. Home (View Mode) | Farm canvas renders with beds. Left panel shows TODAY'S TASKS and FARM SUMMARY |
| 5. Top bar | "Murcia Farm" farm name, weather widget, notification bell with badge, user avatar |
| 6. Right toolbar | + Add, Search, Center, Layers buttons visible |
| 7. Edit Mode | Tap the edit button → "EDIT MODE" green badge appears in top bar, left panel switches to EDIT TOOLS + SOIL TYPE |
| 8. Bed selection | Tap a bed → selection handles appear (blue drag circle, red ✕, white corners) |
| 9. SAVE CHANGES | Green "SAVE CHANGES" button in bottom bar saves to Room DB |
| 10. Navigation | Bottom tabs: Home, Farms, Calendar, Library, Profile all respond |
