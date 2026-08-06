# 00. Getting Started Guide

> 📌 **Navigation**: [◀ DevOps Architecture & Free CI/CD Pipelines](file:///d:/Development/MapTanim/docs/DEVOPS.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [01. Project Overview ▶](file:///d:/Development/MapTanim/docs/01_PROJECT_OVERVIEW.md)

---
## 📌 Overview
Welcome to **MapTanim** — a multi-tier, mobile-based interactive farm management system and decision support platform (DSS) designed for vegetable farmers in the Philippines.

The MapTanim platform consists of 5 integrated components:
1. **Native Android Application** (`mobile/`): Built with **Kotlin 2.2.10**, **AGP 9.2.1**, **Jetpack Compose**, and **Room Database** with offline-first synchronization.
2. **Admin Web Dashboard** (`admin/`): Web management platform built with **React 18**, **Vite 6**, **TypeScript 5.7**, and **Tailwind CSS 4**.
3. **Backend & Database** (`backend/`, `database/`): **Supabase** (PostgreSQL, Row-Level Security, database migrations, and Edge Functions).
4. **Data & Asset Scripts** (`scripts/`): **Python 3.10+** automated data pipelines for Philippine crop metadata generation and asset rendering.
5. **DevOps & Infrastructure** (`deployment/`, `.github/workflows/`): **Docker**, **Nginx**, **Docker Compose**, and **GitHub Actions** CI/CD pipelines.

- **GitHub Repository**: [https://github.com/Jcateo187/maptanim.git](https://github.com/Jcateo187/maptanim.git)
- **Supabase Cloud Project**: `https://ojilvcglpzbtpjxguhzj.supabase.co`

---

## 🛠️ Prerequisites & System Requirements

| Tool / Technology | Minimum / Target Version | Purpose |
|-------------------|--------------------------|---------|
| **OS** | Windows 10/11, macOS Ventura+, Ubuntu 22.04+ | 64-bit operating system |
| **JDK** | JDK 17 (Eclipse Temurin / Azul Zulu) | Android Gradle Plugin 9.2.1 requirement |
| **Android Studio** | Koala (2024.1.1+) / Ladybug (2024.2.1+) | Android IDE & Emulator management |
| **Android SDK** | API Level 34 (Android 14) | Target SDK |
| **Kotlin** | 2.2.10 | Mobile application language |
| **Gradle** | 9.4.1 | Mobile build wrapper (`./gradlew`) |
| **Node.js** | v20.x or higher | Admin Web Dashboard runtime & Supabase CLI |
| **npm** | v10.x or higher | Web package manager |
| **Python** | 3.10 or higher | Metadata & asset generation scripts |
| **Docker & Docker Compose** | Docker Desktop 4.x / Compose v2.20+ | Containerized local stack & production builds |
| **Supabase CLI** | Latest (`v1.150+`) | Database migrations and local backend |
| **Git** | 2.40+ | Version control |

---

## 🚀 Environment Setup Step-by-Step

### Step 1 — Clone the Repository
```bash
git clone https://github.com/Jcateo187/maptanim.git
cd MapTanim
```

---

### Step 2 — Mobile Application Setup (`mobile/`)

1. **Configure `local.properties`**:
   Create or edit `local.properties` in the project root:
   ```properties
   sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
   SUPABASE_URL=https://ojilvcglpzbtpjxguhzj.supabase.co
   SUPABASE_ANON_KEY=sb_publishable_fH5qY2HaAg-coV89IxOl2Q_Xf9ySGMU
   ```
   > **Note**: Replace `YourUsername` with your system username. Do NOT commit `local.properties` to version control — it is listed in `.gitignore`.

2. **Open in Android Studio**:
   - Launch Android Studio and select **File → Open**, navigating to the `MapTanim` root directory.
   - Allow Gradle Sync to finish downloading dependencies.

3. **Compile & Run Unit Tests (CLI)**:
   ```bash
   # Run unit tests
   ./gradlew test

   # Run Android Lint check
   ./gradlew lintDebug

   # Assemble Debug APK
   ./gradlew assembleDebug
   ```

4. **Launch on Emulator / Device**:
   - Select the **`mobile.app`** run configuration.
   - Launch on an emulator or physical device running API 26+ (Android 8.0+).
   - **Note**: MapTanim UI is optimized for **Landscape Orientation**.

---

### Step 3 — Admin Web Dashboard Setup (`admin/`)

1. **Navigate to the `admin` directory**:
   ```bash
   cd admin
   ```

2. **Install Node.js Dependencies**:
   ```bash
   npm install
   ```

3. **Start Development Server**:
   ```bash
   npm run dev
   ```
   - Open your browser at `http://localhost:5173`.

4. **Type-Check & Build Production Bundle**:
   ```bash
   # Type check TypeScript files without emitting code
   npx tsc --noEmit

   # Build production assets to admin/dist/
   npm run build
   ```

---

### Step 4 — Supabase Backend & Database Setup (`backend/`, `database/`)

1. **Install Supabase CLI**:
   ```bash
   npm install -g supabase
   ```

2. **Link Local Project to Supabase Cloud**:
   ```bash
   supabase link --project-ref ojilvcglpzbtpjxguhzj
   ```

3. **Apply Database Migrations**:
   ```bash
   supabase db push
   ```
   Alternatively, inspect migration SQL files located inside `database/` and execute them via the Supabase Dashboard SQL Editor.

4. **Optional: Run Local Supabase Instance**:
   ```bash
   supabase start
   ```

---

### Step 5 — Data & Metadata Generation Pipeline (`scripts/`)

1. **Verify Python Installation**:
   ```bash
   python --version  # Should be 3.10+
   ```

2. **Run Philippine Crop Metadata Generator**:
   ```bash
   python scripts/generate_philippine_metadata.py
   ```

3. **Test Image & Asset URLs**:
   ```bash
   python scripts/test_unsplash_urls.py
   ```

---

### Step 6 — Containerized Deployment Setup (`deployment/`)

1. **Configure Environment Variables**:
   Copy `.env.example` in `deployment/` to `.env`:
   ```bash
   cp deployment/.env.example deployment/.env
   ```

2. **Build and Launch Containerized Admin Dashboard**:
   ```bash
   docker compose -f deployment/docker-compose.yml up -d --build
   ```
   - Access the Nginx-served Admin Dashboard at `http://localhost:8080`.

3. **Verify Container Health**:
   ```bash
   docker compose -f deployment/docker-compose.yml ps
   ```

---

## 🧪 Verification & Continuous Integration (CI/CD)

MapTanim includes GitHub Actions workflows (`.github/workflows/`) that run automated builds and tests on code pushes:

| Workflow | File | Trigger Paths | Validation Steps |
|----------|------|---------------|------------------|
| **Android CI** | `.github/workflows/android-ci.yml` | `mobile/**`, `backend/**`, Gradle files | JDK 17, `./gradlew lintDebug`, `./gradlew test`, Debug APK build |
| **Admin CI** | `.github/workflows/admin-ci.yml` | `admin/**` | Node 20, `npm ci`, `npx tsc --noEmit`, `npm run build` |
| **DevOps CI** | `.github/workflows/devops-ci.yml` | `deployment/**` | Validates `Dockerfile.admin` and `docker-compose.yml` |

> 💡 **No Google Play Account Required**: Every push to `main` or `develop` automatically builds an `app-debug.apk` stored under **GitHub Actions → Artifacts**. Download the `.apk` zip and install it directly on any Android device for 100% free deployment and testing. See [DEVOPS.md](file:///d:/Development/MapTanim/docs/DEVOPS.md#%F0%9F%93%B1-free-android-deployment--testing-no-google-play-store-required) for full options.

---

## ✅ System Smoke Test Checklist

After setting up all components, run through this verification matrix:

### 1. Mobile Android App
- [ ] App launches with splash screen showing **MAPTANIM** logo.
- [ ] `LoadingScreen` checks auth session and populates local Room DB.
- [ ] Welcome screen presents Login / Register / Guest Mode options.
- [ ] `HomeScreen` renders 30m x 30m 2D Isometric Soil Grid with HUD panels.
- [ ] Tapping **Edit Mode** displays green "EDIT MODE" HUD badge and tool drawer.
- [ ] Placing crop plot updates local Room database and schedules Supabase sync.

### 2. Admin Web Dashboard
- [ ] `http://localhost:5173` (or `http://localhost:8080` in Docker) loads cleanly.
- [ ] Dashboard metrics render overview statistics, analytics charts, and crop tables.
- [ ] Crop library view allows reviewing and filtering Philippine crop metadata.

### 3. Backend & Supabase
- [ ] Supabase connection is established with anonymous key.
- [ ] Row Level Security (RLS) policies permit authorized user profile reads and writes.

### 4. Container Deployment
- [ ] Docker container `maptanim-admin-web` reaches healthy status.
- [ ] Nginx correctly handles SPA route refreshes without 404 errors.

---

## ❓ Troubleshooting

| Issue | Common Cause | Solution |
|-------|--------------|----------|
| **Gradle Sync Failure in Android Studio** | Incompatible JDK version | Ensure JDK 17 is configured under **File → Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK**. |
| **`SUPABASE_URL` missing build error** | `local.properties` missing keys | Add `SUPABASE_URL` and `SUPABASE_ANON_KEY` to `local.properties` in project root. |
| **Admin Dashboard `npm install` error** | Outdated Node.js | Upgrade Node.js to v20+ (`node -v`). |
| **Admin SPA 404 on page refresh (Nginx)** | Missing try_files rule | Ensure `nginx.conf` contains `try_files $uri $uri/ /index.html;`. |
| **Port 8080 already in use (Docker)** | Port collision | Update `ADMIN_PORT=8081` in `deployment/.env` before running `docker compose up`. |

---

## 📚 Related Documentation & Cross References
- 📄 [Master Documentation Hub](file:///d:/Development/MapTanim/docs/README.md)
- 📄 [01. Project Overview](file:///d:/Development/MapTanim/docs/01_PROJECT_OVERVIEW.md)
- 📄 [03. System Architecture](file:///d:/Development/MapTanim/docs/03_SYSTEM_ARCHITECTURE.md)
- 📄 [28. Project Structure](file:///d:/Development/MapTanim/docs/28_PROJECT_STRUCTURE.md)
- 📄 [31. Contributing Guidelines](file:///d:/Development/MapTanim/docs/31_CONTRIBUTING.md)
