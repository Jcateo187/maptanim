# 18. View Mode — Home Screen Dashboard

## 📌 Overview
**View Mode** is the default monitoring state of the MapTanim Home Screen. It renders the **exact same 2D Isometric Scenery (`FarmCanvasRenderer`)** as Edit Mode, displaying direct soil planted crops with high-resolution PNG sprites (**Carrot** 🥕, **String Beans** 🫘), terrain, trees, rocks, fences, and status pins.

---

## 🔹 Shared Scenery & Layout Persistence
- Shared 2D isometric rendering engine between View Mode and Edit Mode.
- Holds layouts saved from Edit Mode (synced to Supabase for authenticated farmers or stored in local storage for guest farmers).
- Displays total plots, total active plants, harvest status, and active tasks on the TopBar and LeftToolbar.
