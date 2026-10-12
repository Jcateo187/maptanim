package com.maptanim.app.features.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.core.validation.AuthValidator
import com.maptanim.backend.data.repository.AuthRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())

    val uiState: StateFlow<AuthUiState> =
        _uiState.asStateFlow()

    fun signUp(
        email: String,
        password: String,
        confirmPassword: String = password,
        acceptedTerms: Boolean = true
    ) {
        val emailValidation = AuthValidator.validateGoogleEmail(email)
        if (emailValidation is AuthValidator.ValidationResult.Invalid) {
            _uiState.value = AuthUiState(errorMessage = emailValidation.message)
            return
        }

        if (password.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Please enter a password.")
            return
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState(errorMessage = "Password must be at least 6 characters.")
            return
        }
        if (password != confirmPassword) {
            _uiState.value = AuthUiState(errorMessage = "Passwords do not match.")
            return
        }
        if (!acceptedTerms) {
            _uiState.value = AuthUiState(errorMessage = "Please accept the Terms & Conditions and Privacy Policy.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)

            val result = repository.signUp(
                email = email.trim(),
                password = password
            )

            result.onSuccess {
                _uiState.value = AuthUiState(isSuccess = true)
            }

            result.onFailure {
                _uiState.value = AuthUiState(
                    errorMessage = sanitizeAuthError(it, "Registration failed. Please check your details and try again.")
                )
            }
        }
    }

    fun signIn(
        email: String,
        password: String
    ) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Please enter your email address.")
            return
        }
        if (password.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Please enter your password.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState(
                isLoading = true
            )

            val result = repository.signIn(
                trimmedEmail,
                password
            )

            result.onSuccess {
                // Verify active user status against public.users table
                val currentUserId = try { com.maptanim.app.data.remote.SupabaseClient.client.auth.currentUserOrNull()?.id } catch (_: Exception) { null }
                var isSuspended = false
                if (!currentUserId.isNullOrBlank()) {
                    try {
                        val statusDto = com.maptanim.app.data.remote.SupabaseClient.client
                            .from("users")
                            .select {
                                filter { eq("id", currentUserId) }
                            }
                            .decodeSingleOrNull<com.maptanim.app.data.remote.dto.UserStatusDto>()
                        if (statusDto?.status.equals("SUSPENDED", ignoreCase = true)) {
                            isSuspended = true
                            com.maptanim.app.data.remote.SupabaseClient.client.auth.signOut()
                            com.maptanim.app.data.repository.RepositoryProvider.clearAllLocalCache()
                        }
                    } catch (_: Exception) {}
                }

                if (isSuspended) {
                    _uiState.value = AuthUiState(
                        errorMessage = "This account has been suspended by an administrator."
                    )
                } else {
                    _uiState.value = AuthUiState(
                        isSuccess = true
                    )
                }
            }

            result.onFailure {
                _uiState.value = AuthUiState(
                    errorMessage = sanitizeAuthError(it, "Incorrect email or password. Please try again.")
                )
            }
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            _uiState.value = AuthUiState(
                isLoading = true
            )

            val result = repository.signInAnonymously()

            result.onSuccess {
                _uiState.value = AuthUiState(
                    isSuccess = true
                )
            }

            result.onFailure {
                _uiState.value = AuthUiState(
                    errorMessage = sanitizeAuthError(it, "Guest login failed. Please try again.")
                )
            }
        }
    }

    fun resetPassword(email: String) {
        val trimmedEmail = email.trim()
        val emailValidation = AuthValidator.validateGoogleEmail(trimmedEmail)
        if (emailValidation is AuthValidator.ValidationResult.Invalid) {
            _uiState.value = AuthUiState(errorMessage = emailValidation.message)
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)

            val result = repository.resetPassword(trimmedEmail)

            result.onSuccess {
                _uiState.value = AuthUiState(isSuccess = true)
            }

            result.onFailure {
                _uiState.value = AuthUiState(
                    errorMessage = sanitizeAuthError(it, "Failed to send reset link. Please check your email and try again.")
                )
            }
        }
    }

    /**
     * Sanitizes raw exception messages from Supabase/Ktor so technical details,
     * request URLs, and API keys are never exposed to the user interface.
     */
    private fun sanitizeAuthError(throwable: Throwable, defaultMessage: String): String {
        val raw = throwable.message.orEmpty()
        return when {
            raw.contains("User already registered", ignoreCase = true) || raw.contains("already exists", ignoreCase = true) ->
                "An account with this email already exists."
            raw.contains("Password should be at least", ignoreCase = true) || raw.contains("weak password", ignoreCase = true) ->
                "Password must be at least 6 characters."
            raw.contains("Invalid login credentials", ignoreCase = true) || raw.contains("invalid email or password", ignoreCase = true) ->
                "Incorrect email or password. Please try again."
            raw.contains("rate limit", ignoreCase = true) ->
                "Too many attempts. Please wait a moment and try again."
            raw.contains("Unable to resolve host", ignoreCase = true) || raw.contains("ConnectException", ignoreCase = true) || raw.contains("timeout", ignoreCase = true) ->
                "Unable to connect to the server. Please check your internet connection."
            raw.contains("invalid email", ignoreCase = true) || raw.contains("email format", ignoreCase = true) ->
                "Please enter a valid email address."
            else ->
                defaultMessage
        }
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)