# 🚀 MapTanim Deployment & Infrastructure Guide

This directory contains containerization configs, deployment manifests, environment setup templates, and developer scripts for the **MapTanim** platform.

---

## 📂 Directory Overview

```
deployment/
├── Dockerfile.admin          # Multi-stage production build for Admin Web (Node + Nginx)
├── nginx.conf                # Nginx web server config (SPA routing, Gzip, security headers)
├── docker-compose.yml        # Multi-container service setup
├── .env.example              # Centralized environment variable template
└── scripts/
    ├── bootstrap-env.ps1    # Local environment setup script (Windows PowerShell)
    ├── bootstrap-env.sh     # Local environment setup script (Linux/macOS Bash)
    ├── run-all-tests.ps1    # Unified test runner (Windows PowerShell)
    └── run-all-tests.sh     # Unified test runner (Linux/macOS Bash)
```

---

## 🛠️ Quick Start

### 1. Developer Environment Setup

Run the automated environment bootstrap script:

**Windows (PowerShell):**
```powershell
.\deployment\scripts\bootstrap-env.ps1
```

**Linux / macOS (Bash):**
```bash
./deployment/scripts/bootstrap-env.sh
```

### 2. Running Local Docker Containers

Build and spin up the Admin Web Dashboard container:

```bash
docker compose -f deployment/docker-compose.yml --env-file deployment/.env up --build -d
```

Access the Admin Dashboard at [http://localhost:8080](http://localhost:8080).

To view logs:
```bash
docker compose -f deployment/docker-compose.yml logs -f
```

To stop containers:
```bash
docker compose -f deployment/docker-compose.yml down
```

---

## 🧪 Running Automated Tests

Run all unit tests and static code checks across the Android app, Admin frontend, and backend scripts:

**Windows (PowerShell):**
```powershell
.\deployment\scripts\run-all-tests.ps1
```

**Linux / macOS (Bash):**
```bash
./deployment/scripts/run-all-tests.sh
```

---

## ⚙️ Environment Variables Reference

Copy `.env.example` to `.env` in the `deployment/` directory:

| Variable Name | Description | Default |
|---|---|---|
| `ENVIRONMENT` | Deployment stage (`development`, `staging`, `production`) | `development` |
| `ADMIN_PORT` | Port for the Admin Dashboard web server | `8080` |
| `VITE_SUPABASE_URL` | Supabase backend service URL | `https://your-supabase-project.supabase.co` |
| `VITE_SUPABASE_ANON_KEY` | Supabase public anonymous API key | `your-supabase-anon-key` |
