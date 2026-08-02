# 03. System Architecture

## 📌 Overview
MapTanim uses a **Client-BaaS (Backend-as-a-Service)** architecture. The Android app connects directly to **Supabase** — no custom middleware server is required for v1.0.

---

## 🏗️ Architecture Tiers

```
┌─────────────────────────────────────────────────────────────────────┐
│                         TIER 1: CLIENT                              │
│         Android App (Kotlin + Jetpack Compose)                      │
│                                                                     │
│  ┌─────────────────┐  ┌────────────────┐  ┌────────────────────┐  │
│  │ Presentation    │  │ Domain         │  │ Data               │  │
│  │ (Compose UI)    │  │ (Use Cases)    │  │ (Repository)       │  │
│  │ ViewModels      │  │ DSS Engine     │  │ Room DB + Sync     │  │
│  └────────┬────────┘  └───────┬────────┘  └────────┬───────────┘  │
│           └──────────────────┬┘                    │               │
│                              ▼                     ▼               │
│                    ┌──────────────────┐  ┌──────────────────┐     │
│                    │  Hilt DI Graph   │  │  WorkManager     │     │
│                    └──────────────────┘  └──────────────────┘     │
└───────────────────────────────────────────────────────────────────┬─┘
                                                                    │ HTTPS / TLS 1.3
┌───────────────────────────────────────────────────────────────────▼─┐
│                         TIER 2: BAAS                                │
│                Supabase (ojilvcglpzbtpjxguhzj.supabase.co)          │
│                                                                     │
│  ┌──────────┐ ┌──────────────┐ ┌─────────┐ ┌───────────────────┐  │
│  │ Supabase │ │  PostgreSQL  │ │ Storage │ │  Edge Functions   │  │
│  │  Auth    │ │   + RLS      │ │ Buckets │ │  (Deno/TypeScript)│  │
│  │ (OTP)    │ │  (PostgREST) │ │ crop-   │ │  verify-otp       │  │
│  └──────────┘ └──────────────┘ │ images  │ │  evaluate-dss     │  │
│                                │ avatars │ │  generate-report  │  │
│  ┌──────────────────────────┐  │ guides  │ └───────────────────┘  │
│  │       Realtime           │  └─────────┘                        │
│  │ (bed updates, tasks)     │                                     │
│  └──────────────────────────┘                                     │
└─────────────────────────────────────────────────────────────────────┘
                                                                    │
┌───────────────────────────────────────────────────────────────────▼─┐
│                         TIER 3: ADMIN                               │
│                    Web Admin Dashboard                              │
│              (React + TypeScript + Supabase JS SDK)                 │
│        User Management | Crop Library | DSS Rule Editor            │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🔹 Android App Data Flow

### View Mode Data Flow
```
ViewMode Canvas opens
  → HomeViewModel.loadFarm()
    → GetFarmUseCase → FarmRepository
      → Room DB (local) → emits beds, tasks, summary
      → WorkManager checks pending sync
        → Supabase PostgREST (if online) → updates Room
  → HomeUiState updated via StateFlow
  → FarmCanvasView recomposed with bed list
  → StatusBadgePin overlays drawn per task type
```

### Edit Mode Save Flow
```
User taps [SAVE CHANGES]
  → EditViewModel.saveChanges()
    → SaveFarmLayoutUseCase
      → CropPlotRepository.savePlots(plots)
        → Room DB immediately updated (offline-safe)
        → SyncQueueEntity inserted
          → WorkManager SyncWorker dispatched
            → Supabase PostgREST PATCH/INSERT/DELETE
              → Room updated with server timestamps
```

---

## 🔹 Supabase Services Used

| Service | Purpose |
|---------|---------|
| **Supabase Auth** | OTP email authentication, JWT management |
| **PostgreSQL** | Primary cloud database (all entities) |
| **PostgREST** | Auto-generated REST API for all tables |
| **Row Level Security (RLS)** | Enforces farmer data isolation |
| **Storage** | `crop-images/`, `user-avatars/`, `pest-guides/` buckets |
| **Edge Functions** | `verify-otp`, `evaluate-dss`, `generate-report` |
| **Realtime** | Subscribes to bed layout changes + task updates |

---

## 🔹 Offline-First Architecture

```
┌─────────────────────────────────┐
│        Android App              │
│                                 │
│  Room SQLite  ←───  Repository  │
│  (Source of   ───→  (reads/     │
│   Truth)             writes)    │
│                                 │
│  SyncQueue ─→ WorkManager       │
│              (background sync)  │
└─────────────────────────────────┘
         ↑↓ (when online)
┌─────────────────────────────────┐
│         Supabase Cloud          │
│       (PostgreSQL + RLS)        │
└─────────────────────────────────┘
```

**Sync strategy**: Write-through to Room first, enqueue to SyncQueue, WorkManager retries until successful. Server timestamp wins on conflict.

---

## 🔹 Technology Stack Summary

| Layer | Technology | Version |
|-------|-----------|---------|
| Language | Kotlin | 2.0.0 |
| UI Framework | Jetpack Compose | BOM 2024.09.03 |
| Architecture Pattern | MVVM + Clean Architecture | — |
| DI | Hilt | 2.52 |
| Navigation | Compose Navigation | 2.8.x |
| Local DB | Room | 2.7.x |
| Background Sync | WorkManager | 2.9.x |
| BaaS | Supabase | ojilvcglpzbtpjxguhzj |
| Supabase SDK | kotlin-supabase | 3.1.4 |
| Networking | Ktor HTTP Client | 3.1.3 |
| Build System | Gradle with KTS | 9.4.1 |
| Min SDK | Android 8.0 | API 26 |
| Target SDK | Android 14 | API 34 |
