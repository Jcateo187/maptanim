# Document 42: High-Scalability & Multi-Tenancy Architecture (Millions of Users)

> 📌 **Navigation**: [◀ 41. Users & Profiles Database Tables](file:///d:/Development/MapTanim/docs/41_USERS_AND_PROFILES_DATABASE_TABLES.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [DevOps Architecture & Free CI/CD Pipelines ▶](file:///d:/Development/MapTanim/docs/DEVOPS.md)

---
## 📌 Overview
This document specifies the technical architecture, database indexing strategy, horizontal partitioning, connection pooling, and caching mechanics designed to scale **MapTanim** to support **millions of active farmers across the Philippines** without performance degradation or data cross-contamination.

---

## 1. Multi-Tenant Data Isolation Strategy

MapTanim employs a **Shared Database, Separate Schema / Row-Level Isolation** multi-tenancy model powered by PostgreSQL **Row Level Security (RLS)**.

```
                  ┌─────────────────────────────────────────┐
                  │          Mobile Android App             │
                  └────────────────────┬────────────────────┘
                                       │ Bearer JWT (auth.uid)
                                       ▼
                  ┌─────────────────────────────────────────┐
                  │    Supabase PostgREST API Gateway       │
                  └────────────────────┬────────────────────┘
                                       │ RLS Policy Evaluation
                                       ▼
   ┌───────────────────────────────────────────────────────────────────────┐
   │                       PostgreSQL Database                             │
   │                                                                       │
   │  ┌─────────────────────────┐         ┌─────────────────────────────┐  │
   │  │  public.users / profile │         │        public.farms         │  │
   │  │  WHERE id = auth.uid()  │         │  WHERE farmer_id=auth.uid() │  │
   │  └─────────────────────────┘         └─────────────────────────────┘  │
   │                                                                       │
   │  ┌─────────────────────────────────────────────────────────────────┐  │
   │  │                       public.crop_plots                         │  │
   │  │  WHERE farm_id IN (SELECT id FROM farms WHERE farmer_id=uid())  │  │
   │  └─────────────────────────────────────────────────────────────────┘  │
   └───────────────────────────────────────────────────────────────────────┘
```

### Key Security Guarantees:
- **Zero Cross-Tenant Leakage**: Every query automatically appends tenant scope filters (`auth.uid() = farmer_id`).
- **Stateless Authorization**: JWT contains signed claims (`uid`, `role`), eliminating authorization lookup latency per request.

---

## 2. High-Performance Indexing Strategy for Millions of Records

To maintain sub-millisecond query latency when tables scale to millions of rows, dedicated B-Tree and Composite indexes are defined across high-frequency query paths.

```sql
-- 1. Indexing farms by owner
CREATE INDEX IF NOT EXISTS idx_farms_farmer_id 
ON public.farms USING btree (farmer_id);

-- 2. Composite indexing crop_plots by farm and status
CREATE INDEX IF NOT EXISTS idx_crop_plots_farm_active 
ON public.crop_plots USING btree (farm_id, is_active);

-- 3. Composite indexing tasks by farm, status, and due date
CREATE INDEX IF NOT EXISTS idx_tasks_farm_status_due 
ON public.tasks USING btree (farm_id, status, due_date);

-- 4. Indexing notifications by farmer and read status
CREATE INDEX IF NOT EXISTS idx_notifications_farmer_read 
ON public.notifications USING btree (farmer_id, is_read, created_at DESC);
```

---

## 3. Database Table Partitioning (Horizontal Scaling)

For massive tables like `tasks` and `notifications` that grow continuously over time, range partitioning by date is implemented to reduce index depth and enable fast data archiving.

```sql
-- Declarative Range Partitioning on Tasks by Creation Date
CREATE TABLE public.tasks_partitioned (
    id              UUID            NOT NULL DEFAULT gen_random_uuid(),
    farm_id         UUID            NOT NULL,
    plot_id         UUID,
    task_type       VARCHAR(50)     NOT NULL,
    title           VARCHAR(150)    NOT NULL,
    due_date        DATE            NOT NULL,
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
) PARTITION BY RANGE (due_date);

-- Annual Partitions
CREATE TABLE public.tasks_2026 PARTITION OF public.tasks_partitioned
    FOR VALUES FROM ('2026-01-01') TO ('2027-01-01');

CREATE TABLE public.tasks_2027 PARTITION OF public.tasks_partitioned
    FOR VALUES FROM ('2027-01-01') TO ('2028-01-01');
```

---

## 4. Connection Pooling & Concurrent Session Management

To handle **100,000+ concurrent active mobile devices** during peak farming hours (sunrise/sunset):

| Component | Strategy | Configuration |
|-----------|----------|---------------|
| **Pooler** | Supavisor / PgBouncer | Transaction Mode |
| **Max Client Connections** | Pool Size: 10,000 active sockets | Shared across instances |
| **Statement Timeout** | Strict cancellation on slow queries | 3,000 ms limit |
| **Idle Timeout** | Reclaims inactive connections | 10 seconds |

---

## 5. Offline-First Synchronization & Delta Sync (Bandwidth Optimization)

To prevent write amplification and server overload when millions of farmers reconnect to cellular networks:

```
Mobile App (Room SQLite)
   │
   ├─► Local Read/Write (Instant UI feedback, 0ms network latency)
   │
   └─► Background Sync (SyncWorker)
         ├─► Pull Delta: GET /rest/v1/crop_plots?updated_at=gt.{last_sync_timestamp}
         └─► Batch Push: POST /rest/v1/rpc/sync_offline_batch
```

### Delta Sync Benefits:
- **Minimal Payload Size**: Devices request only records modified since `last_sync_timestamp`.
- **Batch Processing**: Multiple offline changes are grouped into a single atomic RPC transaction.
- **Exponential Backoff**: Automatic retry jitter prevents thundering herd problem during network outages.

---

## 6. CDN Caching for Visual Assets

High-volume static assets (crop texture sprites, avatar graphics, decision support rules) are served via CDN edge nodes:

- **Cache Control**: `public, max-age=31536000, immutable`
- **Edge Acceleration**: Cloudflare / Fastly CDN nodes in Manila and Cebu to minimize latency for Philippine mobile networks (SMART / Globe 4G/5G).

---

## 📚 Related Documentation & Cross References
- 📄 [Master Documentation Hub](file:///d:/Development/MapTanim/docs/README.md)
- 📄 [00. Getting Started Guide](file:///d:/Development/MapTanim/docs/00_GETTING_STARTED.md)
- 📄 [03. System Architecture](file:///d:/Development/MapTanim/docs/03_SYSTEM_ARCHITECTURE.md)
- 📄 [04. Android Architecture](file:///d:/Development/MapTanim/docs/04_ANDROID_ARCHITECTURE.md)
- 📄 [05. Backend Architecture](file:///d:/Development/MapTanim/docs/05_BACKEND_ARCHITECTURE.md)
- 📄 [06. Admin Dashboard](file:///d:/Development/MapTanim/docs/06_ADMIN_DASHBOARD.md)
