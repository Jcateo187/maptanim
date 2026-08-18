# ♾️ MapTanim DevOps Architecture & CI/CD Documentation

> 📌 **Navigation**: [◀ 42. Scalability & Multi-Tenancy Architecture](file:///d:/Development/MapTanim/docs/42_SCALABILITY_AND_MULTI_TENANCY_ARCHITECTURE.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [00. Getting Started Guide ▶](file:///d:/Development/MapTanim/docs/00_GETTING_STARTED.md)

---
This document outlines the DevOps strategy, continuous integration and deployment (CI/CD) pipelines, containerization standards, branching workflow, and release practices for **MapTanim**.

---

## 🏛️ DevOps Infrastructure Architecture

MapTanim operates a multi-target architecture:
- **Mobile Client**: Native Android Application built using Jetpack Compose and Kotlin (`mobile/app`).
- **Backend API & Data Layer**: Shared Kotlin Backend library (`backend/`) and Supabase PostgreSQL / BaaS (`database/`).
- **Admin Dashboard**: React + Vite + TypeScript web application (`admin/`).
- **Data & Utility Layer**: Python metadata generation and automated asset pipeline (`scripts/`).

```
                              +-------------------------+
                              |   GitHub Repository     |
                              +------------+------------+
                                           |
                   +-----------------------+-----------------------+
                   |                                               |
                   v                                               v
        [Push/PR to main/develop]                       [Push/PR to admin/**]
                   |                                               |
        +----------v----------+                         +----------v----------+
        |  Android CI Workflow|                         |   Admin CI Workflow |
        | (.github/workflows/ |                         | (.github/workflows/ |
        |    android-ci.yml)  |                         |    admin-ci.yml)    |
        +----------+----------+                         +----------+----------+
                   |                                               |
     +-------------+-------------+                  +--------------+--------------+
     |             |             |                  |              |              |
     v             v             v                  v              v              v
[JDK 17]     [Lint Check]   [Unit Test]         [Node 20]      [TSC Check]   [Vite Build]
     |                                              |
     v                                              v
[Debug APK]                                    [Dist Assets]
(Artifact)                                     (Artifact / Docker)
```

---

## 🔄 CI/CD Pipelines (GitHub Actions)

### 1. Android CI Workflow (`.github/workflows/android-ci.yml`)
- **Triggers**: Push or Pull Request impacting `mobile/**`, `backend/**`, `build.gradle.kts`, or `settings.gradle.kts`.
- **Jobs**:
  1. Sets up JDK 17 (Temurin) with Gradle dependency caching.
  2. Runs Android Lint analysis (`./gradlew lintDebug`).
  3. Executes unit tests (`./gradlew test`).
  4. Compiles the Debug APK (`./gradlew assembleDebug`).
  5. Uploads compiled APK (`app-debug.apk`) and lint HTML reports as workflow artifacts.

### 2. Admin Web Dashboard CI Workflow (`.github/workflows/admin-ci.yml`)
- **Triggers**: Push or Pull Request impacting files inside `admin/**`.
- **Jobs**:
  1. Sets up Node.js 20.x with npm dependency caching.
  2. Performs clean dependency installation (`npm ci`).
  3. Executes TypeScript type-checking without emitting JS (`npx tsc --noEmit`).
  4. Executes Vite production bundle compilation (`npm run build`).
  5. Stores the generated `dist/` folder as a workflow artifact.

### 3. DevOps Configuration CI Workflow (`.github/workflows/devops-ci.yml`)
- **Triggers**: Push or Pull Request impacting `deployment/**`.
- **Jobs**:
  1. Validates `Dockerfile.admin` build integrity.
  2. Validates `docker-compose.yml` configuration syntax.

---

## 🐳 Containerization & Deployment

### Multi-Stage Docker Build for Admin Dashboard (`deployment/Dockerfile.admin`)
- **Stage 1 (Builder)**: Uses `node:20-alpine`, installs NPM packages cleanly via `npm ci`, and runs `npm run build`.
- **Stage 2 (Runner)**: Uses `nginx:1.27-alpine`, copies `dist` output from Stage 1 into Nginx web root `/usr/share/nginx/html`, and applies custom `nginx.conf`.

### Production Nginx Features (`deployment/nginx.conf`)
- **SPA Client Routing**: Ensures dynamic single-page web app routing works seamlessly without 404 errors using `try_files $uri $uri/ /index.html`.
- **Gzip Compression**: Compresses HTML, JavaScript, CSS, JSON, and SVG payloads.
- **Security Headers**: Enforces `X-Frame-Options`, `X-Content-Type-Options`, `X-XSS-Protection`, and `Content-Security-Policy`.

---

## 🌿 Branching Strategy & Release Lifecycle

MapTanim follows a modified **Git Flow** methodology:

```
main        ------------------------● (v1.0.0 Release Tag)
               ^                /
               |               /
develop     ---●-------●------●-----
                 \    /
feature/*         `--●  (Feature Branches)
```

1. **`main`**: Production-ready branch. Direct commits are restricted; changes must arrive via PR from `develop` or hotfix branches.
2. **`develop`**: Integration branch for upcoming features.
3. **`feature/*`**: Topic branches for new UI screens, backend integration, or DSS features.
4. **`hotfix/*`**: Urgent patch releases branched directly from `main`.

---

## 🔒 Security & Best Practices

1. **Secrets Management**:
   - Never commit sensitive API keys or Supabase service secrets into public git history.
   - Use `deployment/.env.example` as a template and keep `.env` git-ignored.
2. **Dependency Audit**:
   - Automated GitHub Dependabot alerts are enabled.
   - Run `npm audit` periodically inside `admin/`.
3. **Artifact Retention**:
   - CI build artifacts (APKs, HTML lint reports, web dist zip) are retained for 14 days on GitHub Actions.

---

## 📱 Free Android Deployment & Testing (No Google Play Store Required)

If you have not purchased a Google Play Console developer account ($25 one-time fee), you can deploy, test, and distribute MapTanim **100% free** using these 4 production-grade DevOps strategies:

### 1. 📦 GitHub Actions Artifacts (Automatic CI Build)
- Every git push to `main` or `develop` triggers `.github/workflows/android-ci.yml`.
- GitHub Actions automatically compiles `app-debug.apk` and attaches it to the workflow run.
- **How to download & install**:
  1. Go to your GitHub repository -> **Actions** tab.
  2. Click the latest successful workflow run.
  3. Scroll down to **Artifacts** and download `maptanim-debug-apk.zip`.
  4. Extract and send `app-debug.apk` to any Android phone via USB, Google Drive, or messaging apps.

### 2. 🏷️ GitHub Releases (Direct Download Link & QR Code)
- Publish APK files directly on GitHub Releases for public download.
- Users click a single link (or scan a QR code) to download and install `MapTanim.apk` directly on Android without visiting the Play Store.

### 3. 🚀 Firebase App Distribution (Free Beta Testing Platform)
- Free distribution platform provided by Google Firebase.
- Upload compiled APK/AAB to Firebase App Distribution console or via CLI.
- Invite testers via email — testers get automatic update notifications and in-app feedback tools.

### 4. 🌐 Free Web Hosting for Admin Dashboard
- Host the Admin Web Dashboard (`admin/`) for free on **Vercel** (`npx vercel --prod`), **Netlify**, or **GitHub Pages**.
- Combined with **Supabase Cloud Free Tier**, the entire stack (Android App + Web Dashboard + Database) runs 100% free with automated testing!

---

## 📚 Related Documentation & Cross References
- 📄 [Master Documentation Hub](file:///d:/Development/MapTanim/docs/README.md)
- 📄 [00. Getting Started Guide](file:///d:/Development/MapTanim/docs/00_GETTING_STARTED.md)
- 📄 [03. System Architecture](file:///d:/Development/MapTanim/docs/03_SYSTEM_ARCHITECTURE.md)
- 📄 [26. Testing Strategy](file:///d:/Development/MapTanim/docs/26_TESTING.md)
- 📄 [27. Deployment Guide](file:///d:/Development/MapTanim/docs/27_DEPLOYMENT.md)
- 📄 [30. Git Workflow](file:///d:/Development/MapTanim/docs/30_GIT_WORKFLOW.md)
- 📄 [32. Changelog](file:///d:/Development/MapTanim/docs/32_CHANGELOG.md)
- 📄 [33. Roadmap](file:///d:/Development/MapTanim/docs/33_ROADMAP.md)
