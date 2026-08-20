package com.maptanim.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.data.api.AppInitializationController
import com.maptanim.app.data.remote.SupabaseClient
import com.maptanim.app.data.repository.ProfileRepository
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class LoadingDestination {
    object None : LoadingDestination()
    object Welcome : LoadingDestination()
    object Home : LoadingDestination()
}

data class LoadingUiState(
    val progress: Float = 0.10f,
    val statusText: String = "Initializing Agroecological Engine...",
    val destination: LoadingDestination = LoadingDestination.None
)

class LoadingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoadingUiState())
    val uiState: StateFlow<LoadingUiState> = _uiState.asStateFlow()

    init {
        initialize()
    }

    private fun initialize() {
        viewModelScope.launch {
            // Step 1: Initialize Engine & Remote Data
            _uiState.update {
                it.copy(
                    progress = 0.25f,
                    statusText = "Initializing Agroecological Engine..."
                )
            }
            delay(300)

            try {
                kotlinx.coroutines.withTimeoutOrNull(1500L) {
                    val initializer = AppInitializationController()
                    initializer.initialize()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Step 2: Sync Philippine Crop Database
            _uiState.update {
                it.copy(
                    progress = 0.60f,
                    statusText = "Syncing Philippine Crop Database..."
                )
            }
            delay(350)

            // Step 3: Verify Auth Session & User Profile
            _uiState.update {
                it.copy(
                    progress = 0.85f,
                    statusText = "Verifying User Session & Workspace..."
                )
            }
            delay(300)

            val target = try {
                val session = SupabaseClient.client.auth.currentSessionOrNull()
                val user = SupabaseClient.client.auth.currentUserOrNull()

                if (session != null || user != null) {
                    // Authenticated user exists locally - proceed offline to Home screen
                    LoadingDestination.Home
                } else {
                    LoadingDestination.Welcome
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Network unavailable or offline session parse error - fallback to Welcome or Home based on local user
                val cachedUser = try { SupabaseClient.client.auth.currentUserOrNull() } catch (_: Exception) { null }
                if (cachedUser != null) LoadingDestination.Home else LoadingDestination.Welcome
            }

            // Step 4: Final Ready State & Navigation Trigger
            _uiState.update {
                it.copy(
                    progress = 1.0f,
                    statusText = if (target is LoadingDestination.Home) "Loading Farm Workspace..." else "Ready!"
                )
            }
            delay(300)

            _uiState.update {
                it.copy(destination = target)
            }
        }
    }
}
