package com.maptanim.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.backend.data.repository.AuthRepository
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

        password: String

    ) {

        viewModelScope.launch {

            _uiState.value = AuthUiState(
                isLoading = true
            )

            val result = repository.signUp(

                email = email,

                password = password

            )

            result.onSuccess {

                _uiState.value = AuthUiState(
                    isSuccess = true
                )

            }

            result.onFailure {

                _uiState.value = AuthUiState(
                    errorMessage = it.message
                )

            }

        }

    }

    fun signIn(

        email: String,

        password: String

    ) {

        viewModelScope.launch {

            _uiState.value = AuthUiState(
                isLoading = true
            )

            val result = repository.signIn(

                email,

                password

            )

            result.onSuccess {

                _uiState.value = AuthUiState(
                    isSuccess = true
                )

            }

            result.onFailure {

                _uiState.value = AuthUiState(
                    errorMessage = it.message
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
                    errorMessage = it.message
                )
            }
        }
    }

}