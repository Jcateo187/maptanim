# 10. API Documentation

## 📌 Overview
MapTanim communicates with Supabase via two API surfaces:
1. **PostgREST** — Auto-generated REST API for all database tables
2. **Edge Functions** — Custom Deno serverless functions for complex logic

**Base URL**: `https://ojilvcglpzbtpjxguhzj.supabase.co`

---

## 🔹 Authentication Header

All authenticated requests require:
```http
Authorization: Bearer <access_token>
apikey: sb_publishable_fH5qY2HaAg-coV89IxOl2Q_Xf9ySGMU
Content-Type: application/json
```

---

## 🔹 PostgREST Endpoints

### Farms

**GET all farms for current user**
```http
GET /rest/v1/farms?select=*&order=created_at.desc
```

**GET single farm**
```http
GET /rest/v1/farms?id=eq.{farm_id}&select=*,beds(*)
```

**POST create farm**
```http
POST /rest/v1/farms
Body: { "farm_name": "Murcia Farm", "location": "Murcia, Negros Occidental", "total_area_sqm": 500.0 }
```

---

### Crop Plots

**GET all crop plots for farm**
```http
GET /rest/v1/crop_plots?farm_id=eq.{farm_id}&select=*&order=created_at.asc
```

**POST create plot**
```http
POST /rest/v1/crop_plots
Body: {
  "farm_id": "{farm_id}",
  "plot_label": "PLOT 1",
  "crop_name": "Eggplant",
  "soil_type": "LOAM",
  "pos_x": 1.0,
  "pos_y": 2.0,
  "width_m": 2.0,
  "height_m": 3.0,
  "rotation_deg": 0.0
}
```

**PATCH update plot (position/size/crop/soil)**
```http
PATCH /rest/v1/crop_plots?id=eq.{plot_id}
Body: { "pos_x": 1.5, "pos_y": 3.0, "soil_type": "CLAY" }
```

**DELETE remove plot**
```http
DELETE /rest/v1/crop_plots?id=eq.{plot_id}
```

---

### Tasks

**GET today's tasks for farm**
```http
GET /rest/v1/tasks?farm_id=eq.{farm_id}&due_date=eq.{today}&is_completed=eq.false&select=*,crop_plots(plot_label)&order=due_date.asc
```

Response:
```json
[
  {
    "id": "uuid",
    "task_type": "WATER",
    "title": "Water PLOT 3",
    "sub_label": "Tomato",
    "due_date": "2026-07-24",
    "is_completed": false,
    "crop_plots": { "plot_label": "PLOT 3" }
  }
]
```

**PATCH mark task complete**
```http
PATCH /rest/v1/tasks?id=eq.{task_id}
Body: { "is_completed": true, "completed_at": "2026-07-24T09:30:00Z" }
```

---

### Crops (Reference)

**GET all crops**
```http
GET /rest/v1/crops?select=*&order=name.asc
```

**GET single crop with companion info**
```http
GET /rest/v1/crops?name=eq.Tomato&select=*
```

---

### Notifications

**GET unread notifications**
```http
GET /rest/v1/notifications?user_id=eq.{user_id}&is_read=eq.false&select=*&order=created_at.desc
```

**GET count (for bell badge)**
```http
GET /rest/v1/notifications?user_id=eq.{user_id}&is_read=eq.false&select=count
```
Response provides the count that drives the `🔔 3` badge displayed in the top bar.

**PATCH mark notification read**
```http
PATCH /rest/v1/notifications?id=eq.{notification_id}
Body: { "is_read": true }
```

---

## 🔹 Edge Functions

### `verify-otp`

**Endpoint**: `POST /functions/v1/verify-otp`

**Request**:
```json
{
  "email": "farmer@example.com",
  "otp": "123456"
}
```

**Response (success)**:
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "...",
  "user": {
    "id": "uuid",
    "email": "farmer@example.com",
    "full_name": "James Cateo"
  }
}
```

**Response (failed / locked)**:
```json
{
  "error": "TOO_MANY_ATTEMPTS",
  "message": "Account locked for 15 minutes due to 3 failed OTP attempts.",
  "retry_after_seconds": 900
}
```

---

### `evaluate-dss`

**Endpoint**: `POST /functions/v1/evaluate-dss`

**Request**:
```json
{
  "farm_id": "uuid",
  "evaluation_date": "2026-07-24"
}
```

**Response**:
```json
{
  "tasks": [
    {
      "plot_id": "uuid",
      "plot_label": "PLOT 3",
      "crop_name": "Tomato",
      "task_type": "WATER",
      "title": "Water PLOT 3",
      "sub_label": "Tomato",
      "due_date": "2026-07-24"
    },
    {
      "plot_id": "uuid",
      "plot_label": "PLOT 1",
      "crop_name": "Eggplant",
      "task_type": "FERTILIZE",
      "title": "Fertilize Eggplant",
      "sub_label": "PLOT 1",
      "due_date": "2026-07-24"
    }
  ],
  "companion_alerts": [
    {
      "bed_a_label": "BED 1",
      "bed_b_label": "BED 3",
      "relationship": "ANTAGONIST",
      "message": "Tomato and Eggplant share the same pests (fruit borer). Consider separating."
    }
  ],
  "farm_summary": {
    "total_beds": 12,
    "total_plants": 186,
    "ready_to_harvest": 4,
    "active_alerts": 2
  }
}
```

---

### `generate-report`

**Endpoint**: `POST /functions/v1/generate-report`

**Request**:
```json
{
  "farm_id": "uuid",
  "from_date": "2026-07-01",
  "to_date": "2026-07-31"
}
```

**Response**: JSON report object or PDF stream (Content-Type: `application/pdf`)

---

## 🔹 Kotlin SDK Usage Examples

```kotlin
// Get all crop plots for a farm
val plots = supabaseClient.postgrest["crop_plots"]
    .select {
        filter { eq("farm_id", farmId) }
        order("created_at", Order.ASCENDING)
    }
    .decodeList<CropPlotEntity>()

// Update plot position after drag-and-drop
supabaseClient.postgrest["crop_plots"]
    .update(mapOf("pos_x" to newX, "pos_y" to newY)) {
        filter { eq("id", plotId) }
    }

// Delete a plot
supabaseClient.postgrest["crop_plots"]
    .delete {
        filter { eq("id", plotId) }
    }
```
