# 33. Development Roadmap

## 📌 Overview
MapTanim follows a phased development plan. Phase 1 (v1.1) is the capstone deliverable featuring the Direct Soil Planting Engine & Custom Trellis Framework.

---

## ✅ Phase 1 — Capstone v1.1 (Current)

**Target**: Academic Submission 2026

### Completed
- [x] Supabase BaaS integration (`ojilvcglpzbtpjxguhzj.supabase.co`)
- [x] Direct soil planting canvas on 2D isometric renderer
- [x] Crop PNG sprite rendering (**Carrot** 🥕, **String Beans** 🫘) with growth stage scaling
- [x] Hold-to-drag crop positioning anywhere on the soil grid
- [x] Outer crop selection border with 8 corner/edge resize handles
- [x] Synchronized 2D isometric scenery engine between HomeScreen (View Mode) and EditScreen (Edit Mode)
- [x] 70% min zoom limit (`minZoom = 0.70f`) and expanded camera pan bounds (`maxPanX/Y = 2500f * zoom`) for smooth left-side map navigation
- [x] Save Farm Dialog (`Type farm name`) + `"Excellent Successful set up the farm"` confirmation flow
- [x] Top bar: Farm selector, location, weather widget, notification bell with live badge count, user avatar
- [x] Bottom navigation: 5 tabs (Home, Farms, Calendar, Library, Profile)
- [x] DSS Engine: growth stage + water/fertilize/harvest/pest tasks + Monitoring Hub companion overlays
- [x] Email OTP authentication (Supabase Auth / Gmail SMTP relay)
- [x] Offline-first: Room + SyncWorker + SyncQueue
- [x] AgriLibrary DIY Support Structure & Trellising Guides
- [x] Comprehensive documentation (`docs/00`–`docs/37`)

### System Scope Guarantees
- ✅ Email OTP Authentication (No paid SMS Gateway fees)
- ✅ Companion overlays in Monitoring Hub (No map overlay clutter)
- ✅ AgriLibrary DIY Trellis Guides (No complex interactive canvas trellis objects)
- ✅ Deterministic offline rule engine (No paid live weather or external API dependency)
- ✅ Yield metric tracking (No misleading live market price estimators)
- For complete details, see **[37_SYSTEM_SPECIFICATIONS_AND_SCOPE_REFINEMENTS.md](file:///d:/Development/MapTanim/docs/37_SYSTEM_SPECIFICATIONS_AND_SCOPE_REFINEMENTS.md)**.

---

## 🔄 Phase 2 — v2.0 (Post-Capstone)

**Target**: 6 months after v1.1 submission

### Mobile App Enhancements
- [ ] **Localization**: Tagalog + Hiligaynon language support (`strings.xml` translations)
- [ ] **Expanded Crop Profiles**: 30+ regional Philippine vegetable varieties across all 8 plant-part categories
- [ ] **Harvest Record Export**: Export historical yield logs (kg) per plot section
- [ ] **SQLCipher Room Encryption**: Full local database encryption for sensitive farm records
- [ ] **Certificate Pinning**: Hardened network transport security

