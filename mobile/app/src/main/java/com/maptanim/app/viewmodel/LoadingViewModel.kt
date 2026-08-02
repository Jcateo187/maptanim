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
import kotlinx.coroutines.launch

sealed class LoadingDestination {
    object None : LoadingDestination()
    object Welcome : LoadingDestination()
    object WelcomeGuide : LoadingDestination()
    object Home : LoadingDestination()
}

class LoadingViewModel : ViewModel() {

    private val _destination = MutableStateFlow<LoadingDestination>(LoadingDestination.None)
    val destination: StateFlow<LoadingDestination> = _destination.asStateFlow()

    init {
        initialize()
    }

    private fun initialize() {
        viewModelScope.launch {
            val initializer = AppInitializationController()
            initializer.initialize()

            val session = SupabaseClient.client.auth.currentSessionOrNull()
            val user = SupabaseClient.client.auth.currentUserOrNull()

            val target = if (session == null || user == null) {
                LoadingDestination.Welcome
            } else {
                val profileRepository = ProfileRepository()
                val profile = profileRepository.getProfile(user.id)
                if (profile?.onboarding_completed == true) {
                    LoadingDestination.Home
                } else {
                    LoadingDestination.WelcomeGuide
                }
            }

            // Enforce exact 2-second (2000ms) loading screen display timer before navigating
            delay(2000)
            _destination.value = target
        }
    }
}
