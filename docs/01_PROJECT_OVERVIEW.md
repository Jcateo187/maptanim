# 01. Project Overview

> 📌 **Navigation**: [◀ 00. Getting Started Guide](file:///d:/Development/MapTanim/docs/00_GETTING_STARTED.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [02. Software Requirements Specification ▶](file:///d:/Development/MapTanim/docs/02_SOFTWARE_REQUIREMENTS_SPECIFICATION.md)

---
## 📌 Capstone Project Information

| Field | Detail |
|-------|--------|
| **Project Title** | MapTanim: An Android-Based Interactive Farm Plot Mapping with Decision Support System for High-Value Vegetable Farming |
| **Institution** | STI West Negros University — College of Computer Studies |
| **Academic Year** | 2025–2026 |
| **Capstone Category** | Applied Research / Software Development |
| **Platform** | Native Android (Kotlin + Jetpack Compose) |
| **Backend** | Supabase (PostgreSQL BaaS) |

---

## 👥 Project Team

| Name | Role |
|------|------|
| **Jomarey D. Parreño** | Project Manager |
| **John Ryan R. Vasquez** | Systems Analyst |
| **Jason B. Juanillo** | Lead Developer |
| **James M. Cateo** | UI/UX Designer |

**Adviser**: Ms. Danica S. Duazo

---

## 🌱 Problem Statement

Filipino vegetable farmers—particularly in Negros Occidental—rely on:
- **Handwritten notebooks** for planting records
- **Memory** for watering and fertilization schedules
- **Fragmented verbal advice** from neighbors or extension workers

This results in:
- Missed harvest windows due to untracked planting dates
- Suboptimal crop placement (ignoring companion planting principles)
- Soil nutrient depletion from uninformed fertilizer decisions
- No historical yield data for year-over-year comparison
- Loss of farm income from preventable pest outbreaks

**MapTanim** provides a visual, interactive, digital solution optimized for the realities of small-to-medium Philippine farms.

---

## 🎯 Project Objectives

1. Develop a **2D interactive direct soil planting canvas** where farmers can place, move, hold-to-drag, resize, and delete crop plots visually with high-res PNG crop sprites (**Carrot** 🥕, **String Beans** 🫘).
2. Implement a **Decision Support System (DSS)** that generates daily task recommendations (watering, fertilizing, harvesting, pest alerts) based on crop growth stages.
3. Provide a **companion planting matrix** covering 13 high-value vegetables to help farmers optimize plot placement.
4. Integrate **Supabase** as a cloud BaaS for data persistence, authentication, and real-time sync with local Room storage fallback.
5. Support **offline-first** operation with Room SQLite and WorkManager background sync.
6. Deliver a **landscape-optimized UI** suitable for tablet and large-screen Android devices used in field conditions.

---

## 📚 Related Documentation & Cross References
- 📄 [Master Documentation Hub](file:///d:/Development/MapTanim/docs/README.md)
- 📄 [00. Getting Started Guide](file:///d:/Development/MapTanim/docs/00_GETTING_STARTED.md)
- 📄 [03. System Architecture](file:///d:/Development/MapTanim/docs/03_SYSTEM_ARCHITECTURE.md)
- 📄 [28. Project Structure](file:///d:/Development/MapTanim/docs/28_PROJECT_STRUCTURE.md)
- 📄 [31. Contributing Guidelines](file:///d:/Development/MapTanim/docs/31_CONTRIBUTING.md)
