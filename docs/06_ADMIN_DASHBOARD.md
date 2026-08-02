# 06. Admin Dashboard

## 📌 Overview
The **MapTanim Admin Dashboard** is a web-based management panel accessible to users with the `ADMINISTRATOR` role. It is separate from the Android mobile app and connects to the same Supabase project (`ojilvcglpzbtpjxguhzj.supabase.co`) using elevated server-side access.

---

## 🔹 Tech Stack

| Component | Technology |
|-----------|-----------|
| Frontend | React + TypeScript |
| UI Library | MUI (Material UI) or Ant Design |
| Build Tool | Vite |
| Supabase SDK | `@supabase/supabase-js` v2 |
| Auth | Supabase Auth (email/password for admins) |

---

## 🔹 Admin Features

### 1. User Management
- View all registered farmers
- Activate / suspend farmer accounts
- Reset farmer OTP lockout
- View login activity logs

### 2. Crop Library Management
- Add, edit, delete crop records
- Upload crop images to `crop-images` Storage bucket
- Set crop agronomic properties: days to harvest, NPK, soil preferences, season
- Manage companion planting rules (`dss_rules` table)

### 3. DSS Rule Editor
- Add/edit/remove companion planting rules
- View and edit crop pest calendars
- Set soil-crop suitability rules

### 4. Farm Monitoring (Read-Only)
- View any registered farm (read-only, RLS bypass via `service_role` key)
- View farm canvas layout data (`crop_plots`, `crop_zones`, `farm_objects` tables)
- View harvest records and yield analytics

### 5. Analytics & Reports
- Registered users count
- Active farms count
- Most planted crops chart
- Average yield per crop type

### 6. Feedback Management
- View feedback submitted by farmers
- Tag and resolve feedback items

---

## 🔹 Admin Role Enforcement

Admin access is enforced via Supabase RLS:
```sql
-- Only ADMINISTRATOR role can read admin_logs
CREATE POLICY "admins_only" ON public.admin_logs
    FOR ALL USING (
        EXISTS (
            SELECT 1 FROM public.users u
            WHERE u.id = auth.uid() AND u.role = 'ADMINISTRATOR'
        )
    );
```

The Admin Dashboard uses a **`service_role` key** (stored as server-side environment variable — never in client code) for operations that bypass RLS, such as viewing any farmer's data.

---

## 🔹 Admin Dashboard URL (Development)

```
http://localhost:5173
```

Production deployment target: Vercel (connected to the same Supabase project).
