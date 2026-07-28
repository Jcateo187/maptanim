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
- [x] DSS Engine: growth stage + water/fertilize/harvest/pest tasks
- [x] Offline-first: Room + SyncWorker + SyncQueue
- [x] 34 documentation files (`docs/00`–`docs/33`)

### Architecture Guarantees
- ✅ Zero static/mock/hardcoded data in production code paths
- ✅ All user-facing data from Supabase (via Room cache)
- ✅ RLS enforced on all tables
- ✅ MVVM + Clean Architecture

---

## 🔄 Phase 2 — v2.0 (Post-Capstone)

**Target**: 6 months after v1.1 submission

### Mobile App Enhancements
- [ ] **Custom Trellis Asset System**: Custom trellis structures manually built for climbing crops.
- [ ] **Localization**: Tagalog + Hiligaynon language support (`strings.xml` translations)
- [ ] **Expanded crop library**: 30+ Philippine regional varieties
- [ ] **Live weather integration**: OpenWeatherMap or PAGASA API
- [ ] **Harvest record entry UI**: Farmers log yield_kg, quality_rating per harvest
- [ ] **SQLCipher Room encryption**: Full local database encryption
- [ ] **Certificate pinning**: Prevent MITM attacks
