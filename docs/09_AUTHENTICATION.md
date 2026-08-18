# 09. Authentication System

> 📌 **Navigation**: [◀ 08. Supabase Configuration](file:///d:/Development/MapTanim/docs/08_SUPABASE_CONFIGURATION.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [10. API Documentation ▶](file:///d:/Development/MapTanim/docs/10_API_DOCUMENTATION.md)

---
## 📌 Overview
MapTanim supports three user access levels: **Authenticated Farmer**, **Guest**, and **Administrator**. Authentication is powered by **Supabase Auth** using Email OTP (One-Time Password).

> [!NOTE]
> **Authentication Scope Decision**: To eliminate paid service dependencies and recurring carrier gateway fees for Filipino smallholder farmers, MapTanim relies exclusively on **Email OTP** and standard security protocols. Third-party paid SMS Gateways are excluded from the system architecture. For complete details, see **[37_SYSTEM_SPECIFICATIONS_AND_SCOPE_REFINEMENTS.md](file:///d:/Development/MapTanim/docs/37_SYSTEM_SPECIFICATIONS_AND_SCOPE_REFINEMENTS.md)**.


---

## 🔹 User States (from PNG Screenshots)

| State | Top Bar Display | Sync | Features |
|-------|----------------|------|---------|
| Authenticated | `👤 James ▼` — shows name + avatar | Full cloud sync | All features |
| Guest | Avatar without name text | None — Room only | Farm view, limited editing |
| Administrator | Web admin panel only | Full | Crop library, DSS rules, all users |

---

## 🔹 OTP Auth Flow

```
User enters email
        │
        ▼
Supabase sends 6-digit OTP code to email
        │
        ▼
User enters OTP in app
        │
        ▼
verify-otp Edge Function validates code
        │
    ┌───┴───┐
   Valid?   Invalid?
    │           │
    ▼           ▼
JWT issued   Increment attempt counter
Save to      ──────────────────────────
EncryptedSP  3 attempts reached?
Navigate          │
to Home      ──► Lock for 15 minutes
```

---

## 🔹 Auth Specifications

| Property | Value |
|----------|-------|
| Method | Email OTP (6-digit code) |
| OTP Expiry | 5 minutes (300 seconds) |
| Max Attempts | 3 |
| Lockout Duration | 15 minutes |
| Token Storage | Android `EncryptedSharedPreferences` |
| Auto-refresh | Enabled — token refreshed silently before expiry |
| Session Persistence | Enabled — user stays logged in across app restarts |

---

## 🔹 Supabase Auth Code Examples

### Send OTP
```kotlin
// AuthRepository.kt
suspend fun sendOtp(email: String): Result<Unit> = runCatching {
    supabaseClient.auth.signInWith(OTP) {
        this.email = email
    }
}
```

### Verify OTP
```kotlin
suspend fun verifyOtp(email: String, token: String): Result<UserSession> = runCatching {
    supabaseClient.auth.verifyEmailOtp(type = OtpType.Email.EMAIL, email = email, token = token)
    supabaseClient.auth.currentSessionOrNull()
        ?: throw IllegalStateException("Session not established after OTP verify")
}
```

### Get Current User
```kotlin
val currentUser = supabaseClient.auth.currentUserOrNull()
val userId = currentUser?.id
val userEmail = currentUser?.email
```

### Sign Out
```kotlin
suspend fun signOut() {
    supabaseClient.auth.signOut()
}
```

---

## 🔹 JWT Token Handling

```kotlin
// EncryptedPreferencesManager.kt
class EncryptedPreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "maptanim_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveAccessToken(token: String) = prefs.edit().putString("access_token", token).apply()
    fun getAccessToken(): String? = prefs.getString("access_token", null)
    fun clearTokens() = prefs.edit().clear().apply()
}
```

---

## 🔹 Guest Mode

When the user selects **Guest Mode** on the Auth screen:
- No Supabase Auth call is made
- A local Room-only session is created
- Farm data is persisted locally only (no cloud sync)
- Notification bell badge count pulls from local Room only
- Top bar: shows avatar placeholder without username
- Register/Login prompt shown on Profile screen

---

## 🔹 AuthViewModel

```kotlin
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun sendOtp(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            authRepository.sendOtp(email)
                .onSuccess { _uiState.update { it.copy(otpSent = true, isLoading = false) } }
                .onFailure { _uiState.update { it.copy(error = it.toString(), isLoading = false) } }
        }
    }

    fun verifyOtp(email: String, code: String) {
        viewModelScope.launch {
            authRepository.verifyOtp(email, code)
                .onSuccess { _uiState.update { it.copy(isAuthenticated = true) } }
                .onFailure { _uiState.update { it.copy(error = "Invalid OTP. Please try again.") } }
        }
    }

    fun continueAsGuest() {
        _uiState.update { it.copy(isGuest = true) }
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val otpSent: Boolean = false,
    val isAuthenticated: Boolean = false,
    val isGuest: Boolean = false,
    val error: String? = null
)
```

---

## 🔹 Security Requirements

| Requirement | Implementation |
|-------------|---------------|
| Password hashing | Bcrypt (managed by Supabase Auth) |
| Token storage | Android EncryptedSharedPreferences (AES-256-GCM) |
| Transport | HTTPS / TLS 1.3 enforced by Ktor |
| OTP lockout | 3 attempts → 15-minute block |
| Session expiry | JWT auto-refresh via Supabase SDK |
| Data isolation | RLS policies on all tables |

---

## 📚 Related Documentation & Cross References
- 📄 [Master Documentation Hub](file:///d:/Development/MapTanim/docs/README.md)
- 📄 [00. Getting Started Guide](file:///d:/Development/MapTanim/docs/00_GETTING_STARTED.md)
- 📄 [03. System Architecture](file:///d:/Development/MapTanim/docs/03_SYSTEM_ARCHITECTURE.md)
- 📄 [07. Database Design](file:///d:/Development/MapTanim/docs/07_DATABASE_DESIGN.md)
- 📄 [08. Supabase Configuration](file:///d:/Development/MapTanim/docs/08_SUPABASE_CONFIGURATION.md)
- 📄 [24. Offline Synchronization](file:///d:/Development/MapTanim/docs/24_OFFLINE_SYNCHRONIZATION.md)
- 📄 [25. Security & RLS](file:///d:/Development/MapTanim/docs/25_SECURITY.md)
- 📄 [40. User & Profile Schema Refinement](file:///d:/Development/MapTanim/docs/40_USER_AND_PROFILE_SCHEMA_REFINEMENT.md)
- 📄 [41. Users & Profiles Database Tables](file:///d:/Development/MapTanim/docs/41_USERS_AND_PROFILES_DATABASE_TABLES.md)
