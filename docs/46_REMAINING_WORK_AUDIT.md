# 46 — Remaining Work Audit (after Doc 45 Phase 1 & 2)

> **Audited branch:** `feature/vegplotter` @ `59e2d9d`
> **Audit date:** 2026-10-06
> **Method:** Checked the live code with grep and file reads. Did not rely on commit messages.
> **Baseline:** [45_PRODUCTION_READINESS_GAP_ANALYSIS.md](./45_PRODUCTION_READINESS_GAP_ANALYSIS.md)

---

## 1. Progress Snapshot

| Phase (Doc 45 §8) | Status | Notes |
|---|---|---|
| 1 — Security | 🟡 **Mostly done, has blockers** | Migration 023 written but **not yet confirmed to run**. Admin login has a role bug (§2.1). |
| 2 — Remove fakes | 🟡 **Mobile done, admin not done** | Mobile M1–M7 fixed. Admin `api.ts` is still mock-first. |
| 3 — Real sync | 🔴 **Not started** | No `SyncWorker`. Cloud restore is never called. |
| 4 — Admin completeness | 🔴 **Mostly not started** | Only `admin_audit_logs` was added. |
| 5 — DSS polish | 🔴 **Not started** | Today's Tasks card is still fixed. Basketball files are still present. |

### Already done (verified)

| Item | Where |
|---|---|
| `is_admin()` helper, owner-scoped RLS on 14 tables, idempotent drops | [023_production_rls_hardening.sql](../database/migration/023_production_rls_hardening.sql) |
| `crop_logs`, `dss_evaluations`, `dss_decisions` now in a versioned migration | 023 |
| `admin_audit_logs` table (insert/select by admin only, no update/delete) | 023 |
| `farms.yard_width_m` / `yard_length_m` columns | 023 |
| Admin login uses `supabase.auth.signInWithPassword()`; `VITE_ADMIN_*` and fallback password removed | [AuthContext.tsx](../admin/src/context/AuthContext.tsx) |
| M1 Ampalaya counted in seconds | [CropPlotRepositoryImpl.kt](../mobile/app/src/main/java/com/maptanim/app/data/repository/CropPlotRepositoryImpl.kt) |
| M2 "Secs" label in harvest history | `UserHarvestHistoryCard.kt`, `FullHarvestHistoryModal.kt` |
| M3 "10s Simulation Test" varieties | `CropsSummaryOverlay.kt` |
| M4 `10s` → 1-day harvest override | `EditViewModel.kt` |
| M5 Fake demo bed on home | `MainHomeScreen.kt` |
| M6 14-day demo planted date | `BedTimelineDialog.kt` |
| M7 Fake support ticket | [CustomerServiceChatDialog.kt L255](../mobile/app/src/main/java/com/maptanim/app/features/shared/support/CustomerServiceChatDialog.kt#L255) → `sendSupportTicket()` |
| WorkManager dependency added | [build.gradle.kts L90](../mobile/app/build.gradle.kts#L90) |

---

## 2. 🔴 P0 — Blockers (do these first)

### 2.1 Admin login rejects every real admin account
[AuthContext.tsx L54](../admin/src/context/AuthContext.tsx#L54) and [L132](../admin/src/context/AuthContext.tsx#L132) only accept `['ADMIN', 'SUPER_ADMIN']`.
But `role_enum` (migration 001) is `('FARMER', 'ADMINISTRATOR', 'GUEST')`. `ADMIN` is not a valid value.

**Effect:** no one can sign in to the admin dashboard.
**Fix:** accept `ADMINISTRATOR` (and `SUPER_ADMIN` only if you add it to the enum). Match the list used by `is_admin()`.

### 2.2 Confirm migration 023 runs fully
It has been fixed 6 times (`uuid = text`, enum value, FK types, `crop_profiles`, `farmer_id`, `farm_id`).
- [ ] Run it once in the Supabase SQL editor with no errors.
- [ ] Run it a **second** time. It must also pass (idempotency check).

### 2.3 Create the first real admin account
`is_admin()` needs a row in `public.users` with `role = 'ADMINISTRATOR'` and `status = 'ACTIVE'`, and the `id` must equal the `auth.users.id`.
- [ ] Create the user in Supabase Auth.
- [ ] Update their `public.users` row (role + status).
- [ ] Without this, the admin dashboard will see **0 rows** everywhere after 023.

### 2.4 Mobile writes after RLS is on
All farmer tables now need `farms.farmer_id = auth.uid()`.
- [ ] Check that the mobile app always has a Supabase session before it writes. Guest/offline mode will now be rejected by RLS.
- [ ] Check that `FarmRepositoryImpl` sets `farmer_id` to `auth.uid()`, not a local id.
- [ ] Today, every rejected write is swallowed by `try { } catch {}`, so you will not see an error. Test with Logcat.

### 2.5 Admin session is still seeded from `localStorage`
[AuthContext.tsx L23–36](../admin/src/context/AuthContext.tsx#L23-L36) trusts the saved JSON before Supabase confirms the session. If `getSession()` returns nothing, the stale user is **not cleared**.
- RLS protects the data, so this is a UI leak, not a data leak.
- **Fix:** start with `user = null`. Set the user only from `getSession()` + role check. Clear it when there is no session.

### 2.6 Tables still wide open (not covered by 023)
| Table / Resource | Current policy | Source |
|---|---|---|
| `crop_rotation_log` | `FOR ALL USING (true)` | 021 |
| `farm_objects` | `FOR ALL USING (true)` | 012 |
| Storage bucket `crop-images` | Public upload / update / delete | 017 |

Add these to a new migration `024_rls_remaining_tables.sql`.

### 2.7 Private support reply becomes a global broadcast
[api.ts L1344](../admin/src/services/api.ts#L1344): `user_id: farmerId && farmerId !== 'usr-001' ? farmerId : null`.
A `null` user id is shown to **every** farmer.
**Fix:** if the farmer id is unknown, block the reply and show an error.

### 2.8 Admin-only operations need a server (Edge Functions)
| Operation | Today | Needed |
|---|---|---|
| Create farmer | Fake id `usr-XXXX` ([api.ts L620](../admin/src/services/api.ts#L620)), no auth user | `admin-create-user` Edge Function |
| Suspend | Only updates `users.status` | Also `auth.admin.updateUserById(ban_duration)` |
| Change role | Direct update from browser | `admin-set-role` (SUPER_ADMIN only) |
| Broadcast | Direct insert | Use `broadcast-dispatcher` with admin JWT check |

---

## 3. 🟠 P1 — Admin Dashboard Still Uses Mock Data

[api.ts L2](../admin/src/services/api.ts#L2) still imports 10 mock arrays. The class is seeded from them (L6–L11).

| # | Location | Problem |
|---|---|---|
| A1 | [api.ts L50, L132, L220–241](../admin/src/services/api.ts#L220-L241) | Dashboard counts fall back to `MOCK_STATS` when 0 or on error. |
| A2 | [DashboardOverview.tsx L22, L89](../admin/src/pages/DashboardOverview.tsx#L89) | Initial state is `MOCK_STATS`. Fake numbers flash first. |
| A3 | [api.ts L507, L510](../admin/src/services/api.ts#L507-L510) | `getFarmers` returns `MOCK_FARMERS` if empty or error. |
| A4 | [api.ts L549, L553](../admin/src/services/api.ts#L549-L553) | Activity metrics use `MOCK_USER_TRACKING_METRICS`. |
| A5 | `getUserActivityLogs` | Reads in-memory mock only. |
| A6 | `updateUserStatus` / `updateUserRole` | `{ error }` not checked. UI shows success when the DB rejects it. |
| A9 | `getCrops` | Fills missing crops from mock. **Deletes rows during a read.** |
| A10 | `uploadCropImage` | Stores Base64 in the `crops` row when Storage fails. |
| A11 | [api.ts L1176](../admin/src/services/api.ts#L1176) | DSS rule fallback id `dss-XXXX`. |
| A12 | [api.ts L1259](../admin/src/services/api.ts#L1259) | `getFarms` returns `MOCK_FARMS`; soil hardcoded `LOAM`; downloads 4 full tables. |
| A13 | [api.ts L1288](../admin/src/services/api.ts#L1288) | `getBedsForFarm` returns `MOCK_BEDS`; growth stage `2`, health `92` hardcoded. |
| A14 | [api.ts L1303](../admin/src/services/api.ts#L1303) | Feedback uses fake `farmerId: 'usr-001'`. |
| A16 | `getBroadcastNotifications` | Returns 2 fake broadcasts on error. |
| A17 | `broadcastInformationUpdate` | Returns `true` even when the insert fails. |
| A19 | [CropLibrary.tsx L11, L38–39](../admin/src/pages/CropLibrary.tsx#L38-L39) | Pest and soil tabs read `MOCK_PESTS` / `MOCK_SOILS` only. No DB tables. |
| A20 | `DSSRuleEditor.tsx` | Simulator is a TypeScript copy of `DssEngine.kt`. Will drift. |

**Done when:**
- [ ] `grep -r "MOCK_" admin/src` returns only `mockData.ts` (or test files).
- [ ] Empty DB → admin shows empty states, not sample farmers.
- [ ] Network off → error banner, not fake data.
- [ ] Every write checks `{ error }` and shows a toast.

---

## 4. 🟠 P1 — Mobile ↔ Supabase Sync (Phase 3)

### 4.1 Offline write queue — not built
| Piece | Status |
|---|---|
| `sync_queue` table + `SyncQueueDao` | ✅ exists |
| `enqueueSyncItem()` callers | ❌ **0 callers** (only defined in [SyncRepositoryImpl.kt L14](../mobile/app/src/main/java/com/maptanim/app/data/repository/SyncRepositoryImpl.kt#L14)) |
| `SyncWorker` class | ❌ does not exist |
| WorkManager dependency | ✅ added |

**To do:**
- [ ] Each repository write: Room + `enqueueSyncItem()` in one transaction.
- [ ] `SyncWorker`: `NetworkType.CONNECTED`, exponential backoff, coalesce updates of the same row (1 upsert per bed, not per drag).
- [ ] Trigger after each write + every 15 min.
- [ ] "⚠ N changes not synced" indicator in Profile.

### 4.2 Cloud restore — never called
[AppInitializationController.kt](../mobile/app/src/main/java/com/maptanim/app/data/api/AppInitializationController.kt) only pulls crops (L11) and DSS rules (L15).

| Method | Defined | Called |
|---|---|---|
| `FarmRepositoryImpl.fetchFromRemote(farmerId)` | [L50](../mobile/app/src/main/java/com/maptanim/app/data/repository/FarmRepositoryImpl.kt#L50) | ❌ |
| `CropPlotRepositoryImpl.fetchFromRemote(farmId)` | [L180](../mobile/app/src/main/java/com/maptanim/app/data/repository/CropPlotRepositoryImpl.kt#L180) | ❌ |
| `TaskRepositoryImpl.fetchFromRemote(farmId)` | [L73](../mobile/app/src/main/java/com/maptanim/app/data/repository/TaskRepositoryImpl.kt#L73) | ❌ |
| Harvest records pull | ❌ not written | — |
| Crop zones pull | ❌ not written | — |

**Effect:** reinstall or new phone = farm is gone from the phone.

### 4.3 Data that never leaves the phone
- [ ] `crop_zones` — `CropZoneRepositoryImpl` is local only.
- [ ] `farm_objects` — not synced.
- [ ] `tasks` — generated locally, never inserted remotely; `completeTask()` updates a row that does not exist.
- [ ] Yard W × L — columns now exist on `farms` (023), but mobile still saves to Preferences only.

### 4.4 Admin actions with no effect on the phone
- [ ] **Suspend:** mobile never reads `users.status`. Read it on start/resume; sign out if `SUSPENDED`.
- [ ] **Delete DSS rule / crop:** mobile sync only upserts. Deleted rows stay forever. Use replace-set or soft delete.
- [ ] **Broadcast:** fetched only at app start. Add a resume pull (max once per 6 h) or FCM topic.
- [ ] **Read state:** global broadcasts share one `is_read`. Add `notification_reads (notification_id, user_id, read_at)`.

### 4.5 Direct messages are fake (M8)
[CommunityChatSection.kt L74](../mobile/app/src/main/java/com/maptanim/app/features/community/components/CommunityChatSection.kt#L74): messages live in `mutableStateMapOf` and are lost on exit.
- [ ] Hide the DM tab, **or** add a `direct_messages` table with RLS `auth.uid() IN (sender_id, receiver_id)`.

---

## 5. 🟡 P2 — Data Quality & Schema Hygiene

| Item | Action |
|---|---|
| Old Ampalaya harvest rows | Data repair: recompute `growing_duration_days` from `harvested_at - planted_date` for rows that were saved in seconds. |
| M11 invented crop values | [CropRepositoryImpl.kt L71–101](../mobile/app/src/main/java/com/maptanim/app/data/repository/CropRepositoryImpl.kt): `toleratedSoils = [SANDY, PEATY]` for every crop; default 60 days / NPK 1:1:1. Show "unknown" instead of inventing values. |
| Duplicate migration number 022 | Rename one to keep a clear order. |
| Migration 008 duplicate `CREATE POLICY` | Clean up (023 now drops them, but a fresh run of 008 still fails). |
| Two `harvest_records` definitions (012, 020) and 001 with `plot_id` only | 023 now adds missing columns. Document the final shape in [07_DATABASE_DESIGN.md](./07_DATABASE_DESIGN.md). |
| Hardcoded Supabase URL/key | Move to `BuildConfig` (mobile) and env only (admin). |
| Edge Functions unused | Decide: delete `evaluate-dss` or use it for the admin simulator. Fix false comments in `UseCases.kt` L14 and [Repositories.kt L26, L59, L136](../mobile/app/src/main/java/com/maptanim/app/domain/repository/Repositories.kt#L26). |
| M10 `inMemoryFallback` | Keep only for previews; log an error in release builds. |

---

## 6. 🟢 P3 — DSS Polish (Phase 5)

### 6.1 Today's Tasks still fixed on screen
[TodayTasksCard.kt](../mobile/app/src/main/java/com/maptanim/app/features/home/components/TodayTasksCard.kt) is used as a full card in [HomeScreen.kt L148](../mobile/app/src/main/java/com/maptanim/app/features/home/screen/HomeScreen.kt#L148).
- [ ] Collapsed pill (`🧺 3 due · 1 urgent`), red only for CRITICAL/HIGH.
- [ ] Tap → bottom sheet grouped by bed (Done / Snooze / Why?).
- [ ] Hidden while editing, dragging, or resizing a bed.
- [ ] Bed-scoped when a bed is selected.
- [ ] Swipe to dismiss for today (persist in `FarmPreferencesManager`).
- [ ] Hidden when there are 0 tasks.
- [ ] Merge duplicates ("Water 2 beds").

### 6.2 On-device tests not done
- [ ] 5 anomaly scenarios (pulled plant, animals, *hulas*, blossom-end rot, bacterial wilt) → correct advice offline in < 1 s.
- [ ] Murcia beginner flow end-to-end, no blank screens.

### 6.3 Basketball cleanup — Purged completely ✅
All 18 locations updated; zero occurrences of "basketball" remain in application source code. Replaced with metric yard presets (6×4m, 12×8m, 18×12m) and step pacing (1 step ≈ 0.85m).

| Action | Target | Status |
|---|---|---|
| **Delete file** | `BasketballScaleCard.kt` | ✅ Deleted |
| **Delete file** | `BasketballCourtScaleCard.kt` | ✅ Deleted |
| **Delete file** | `CropPredictiveDssAdvisor.kt` | ✅ Deleted |
| Remove usage | `HomeScreen.kt` | ✅ Replaced with yard calibration & wired task completion |
| Remove usage | `PlanTab.kt` | ✅ Removed |
| Remove usage | `SingleScreenFarmHub.kt` | ✅ Removed |
| Remove usage | `CropDssManagementDialog.kt` | ✅ Removed |
| Remove state | `FarmHubPlanState.kt` | ✅ Removed `showBasketballScale` |
| Replace props | `DomainModels.kt` | ✅ `stepPacingEstimate` & `yardPresetComparisonText` |
| Update text | `CropPlaceSuitabilityCard.kt` | ✅ Metric step pacing |
| Update text | `DssOnboardingSetupCard.kt` | ✅ Yard calibration |
| Update text | `DssObservationSections.kt` | ✅ Backyard Size Presets (Metric) |
| Update text | `FarmSetupDialog.kt` | ✅ Yard calibration guide |
| Update text | `InterconnectedWorkflowHeader.kt` | ✅ Yard scale presets |
| Rename constant | `FarmCanvasView.kt` | ✅ `MeasureOrange` |
| Rename constant | `CanvasTopToolbar.kt` | ✅ `MeasureOrange` |
| Update comment | `FarmHubScreen.kt` | ✅ Yard Measurement Guide |

> [!NOTE]
> Compilation verified: Both `.\gradlew.bat :mobile:app:compileDebugKotlin` and `npm run build` in `admin/` compile with 0 errors!

---

## 7. Execution Status Summary

| # | Task | Status | Details |
|---|---|---|---|
| 1 | Fix admin role check (§2.1) + localStorage safety | ✅ Complete | Updated `AuthContext.tsx` to handle `ADMINISTRATOR` & clear stale storage |
| 2 | Migration 024: `crop_rotation_log`, `farm_objects`, `crop-images` | ✅ Complete | Created `024_rls_remaining_tables.sql` with idempotent drops |
| 3 | Support reply private broadcast leak fix | ✅ Complete | Dispatches exclusively to target user, never null broadcast |
| 4 | Today's Tasks canvas pill refinement | ✅ Complete | Collapsible 1-line summary header, honest empty state, zero fake tasks |
| 5 | Basketball court benchmark purge | ✅ Complete | Zero references in source code, metric yard calibration installed |
| 6 | Cloud restore on user authentication | ✅ Complete | `AppInitializationController.kt` syncs farms, plots, and tasks |
| 7 | Offline `SyncWorker` (WorkManager) | ✅ Complete | `SyncWorker.kt` implemented & scheduled in `MapTanimApplication.kt` |
| 8 | Admin dashboard mock stats & guide purge | ✅ Complete | Zero initial stats flash; `agronomicGuides.ts` created |
| 9 | Compile & Build verification | ✅ Complete | Mobile Gradle + Admin Vite both exit with code 0 |
