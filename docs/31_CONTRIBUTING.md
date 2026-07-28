# 31. Contributing Guide

## 📌 Overview
Thank you for contributing to **MapTanim**! This guide ensures all contributions meet the project's data integrity, code quality, and documentation standards.

---

## 🚫 Contribution Rule #1: No Static/Mock Data

> **This is a hard rule.** Any PR introducing hardcoded task lists, static farm data, fake bed positions, or mock DSS results will be rejected during review.
>
> All data rendered to users must flow from: `Supabase (live data) → Room (cache) → Repository → ViewModel → Compose UI`.

---

## 🔹 Getting Started

1. Fork the repository: [https://github.com/Jcateo187/maptanim.git](https://github.com/Jcateo187/maptanim.git)
2. Clone your fork
3. Follow setup in `00_GETTING_STARTED.md`
4. Create a branch: `git checkout -b feature/your-feature develop`
5. Make changes following `29_CODING_STANDARDS.md`
6. Commit following `30_GIT_WORKFLOW.md` format
7. Push and open a PR targeting `develop`

---

## 🔹 PR Checklist

Before submitting a PR, verify:

- [ ] No hardcoded data in ViewModels, Repositories, or Composables
- [ ] All new screens read from Room (`Flow<T>` from DAO)
- [ ] New database fields have a corresponding Supabase migration in `backend/supabase/migrations/`
- [ ] Unit tests added for any new DSS rule or use case
- [ ] Compose UI test added for any new interactive component
- [ ] Documentation updated (`docs/` file if applicable)
- [ ] No `!!` force unwraps used
- [ ] All Composable touch targets ≥ 48dp
- [ ] `local.properties` not committed

---

## 🔹 Issue Reporting

Use GitHub Issues with the appropriate template:
- **Bug Report** → `.github/ISSUE_TEMPLATE/bug_report.md`
- **Feature Request** → `.github/ISSUE_TEMPLATE/feature_request.md`

---

## 🔹 Code Review Standards

Reviewers will check for:
1. **Data integrity**: No mock/static data — all values from Supabase/Room
2. **RLS compliance**: New tables must have RLS enabled and policies defined
3. **Offline safety**: New writes must go to Room first, then SyncQueue
4. **Test coverage**: New features need at least 1 unit test + 1 UI test
5. **Documentation**: New components/screens documented in `docs/14_COMPONENT_LIBRARY.md` or relevant doc
