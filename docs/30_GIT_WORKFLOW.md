# 30. Git Workflow

## 📌 Repository

**GitHub**: [https://github.com/Jcateo187/maptanim.git](https://github.com/Jcateo187/maptanim.git)

---

## 🔹 Branch Strategy

| Branch | Purpose | Direct Push |
|--------|---------|------------|
| `main` | Production-ready code only | ❌ PR only |
| `develop` | Active development integration branch | ❌ PR only |
| `feature/*` | New features | ✅ Author only |
| `bugfix/*` | Bug fixes | ✅ Author only |
| `hotfix/*` | Critical production fixes | ✅ Author only |
| `docs/*` | Documentation updates | ✅ Author only |

---

## 🔹 Branch Naming Convention

```
feature/edit-mode-soil-painter
feature/dss-companion-planting-matrix
bugfix/bed-selection-handle-z-index
hotfix/otp-lockout-not-triggering
docs/update-view-mode-spec
```

---

## 🔹 Commit Message Format

```
<type>: <description>

[optional body]
[optional footer]
```

### Types

| Type | When to Use |
|------|------------|
| `feat` | New feature |
| `fix` | Bug fix |
| `docs` | Documentation changes |
| `style` | Formatting, no logic change |
| `refactor` | Code restructure, no behavior change |
| `test` | Adding or updating tests |
| `chore` | Build system, dependencies, CI |
| `perf` | Performance improvements |

### Examples
```
feat: Add soil painter tool to Edit Mode left panel
fix: Correct selection handle z-index layering on canvas
docs: Update 19_EDIT_MODE.md with exact PNG 2 handle specs
test: Add DssEngine unit tests for companion planting matrix
chore: Update supabase-kotlin SDK to 3.1.4
refactor: Extract BedRepository from HomeViewModel
perf: Optimize farm canvas re-composition by key-stabilizing beds
```

---

## 🔹 Pull Request Process

1. Create feature branch from `develop`
2. Implement changes following `29_CODING_STANDARDS.md`
3. Ensure: no static data, no `!!` usage, all tests pass
4. Open PR targeting `develop`
5. Fill in `PULL_REQUEST_TEMPLATE.md`
6. Request review from at least one team member
7. Squash merge after approval

---

## 🔹 `.gitignore` Critical Entries

```gitignore
# Secrets — NEVER commit
local.properties
*.jks
*.keystore
.env
.env.local

# Build outputs
build/
*.apk
*.aab

# IDE
.idea/
*.iml
.DS_Store

# Supabase local
supabase/.branches
supabase/.temp
```

---

## 🔹 Protected Branch Rules (GitHub)

- `main`: Require PR + 1 approval + all status checks pass + no force push
- `develop`: Require PR + status checks pass

---

## 🔹 CODEOWNERS

```
# .github/CODEOWNERS
/mobile/         @jcateo
/backend/        @jcateo
/docs/           @jcateo
/admin/          @jcateo
```
