package com.maptanim.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.core.preferences.TutorialPreferencesManager
import com.maptanim.app.data.remote.SupabaseClient
import com.maptanim.app.data.repository.ProfileRepository
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class TutorialStep {
    SPOTLIGHT_EDIT_BUTTON,
    EDIT_ADD_PLANT,
    EDIT_SELECT_CROP,
    EDIT_DRAGGING_CROP,
    EDIT_BOTTOM_TOOLBAR_EXPLAIN,
    EDIT_CLOSE_TRAY,
    EDIT_SAVE_FARM,
    COMPLETED
}

data class TutorialUiState(
    val currentStep: TutorialStep = TutorialStep.SPOTLIGHT_EDIT_BUTTON,
    val isTutorialActive: Boolean = true,
    val userNickname: String = ""
)

class TutorialViewModel : ViewModel() {

    private val profileRepository = ProfileRepository()
    private val preferencesManager = TutorialPreferencesManager.getInstance()

    private val _uiState = MutableStateFlow(TutorialUiState())
    val uiState: StateFlow<TutorialUiState> = _uiState.asStateFlow()

    init {
        checkOnboardingStatus()
    }

    private fun checkOnboardingStatus() {
        viewModelScope.launch {
            val user = SupabaseClient.client.auth.currentUserOrNull()
            val userId = user?.id

            if (preferencesManager.isCompletedForUser(userId)) {
                _uiState.value = _uiState.value.copy(
                    currentStep = TutorialStep.COMPLETED,
                    isTutorialActive = false
                )
                return@launch
            }

            if (user != null) {
                val profile = try { profileRepository.getProfile(user.id) } catch (_: Exception) { null }
                val completedAtStr = profile?.tutorialCompletedAt

                if (!completedAtStr.isNullOrBlank()) {
                    preferencesManager.markCompletedForUser(userId)
                    _uiState.value = _uiState.value.copy(
                        currentStep = TutorialStep.COMPLETED,
                        isTutorialActive = false,
                        userNickname = profile?.nickname ?: ""
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        currentStep = TutorialStep.SPOTLIGHT_EDIT_BUTTON,
                        isTutorialActive = true,
                        userNickname = profile?.nickname ?: ""
                    )
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    currentStep = TutorialStep.SPOTLIGHT_EDIT_BUTTON,
                    isTutorialActive = true
                )
            }
        }
    }

    fun submitNickname(nickname: String) {
        val user = SupabaseClient.client.auth.currentUserOrNull()
        val finalNickname = nickname.ifEmpty { "Farmer" }
        _uiState.value = _uiState.value.copy(
            userNickname = finalNickname
        )

        viewModelScope.launch {
            com.maptanim.app.data.repository.RepositoryProvider.userRepository.updateNickname(finalNickname)
            if (user != null) {
                profileRepository.updateProfile(
                    userId = user.id,
                    nickname = finalNickname
                )
            }
        }
    }

    fun setStep(step: TutorialStep) {
        val user = SupabaseClient.client.auth.currentUserOrNull()
        if (preferencesManager.isCompletedForUser(user?.id) || _uiState.value.currentStep == TutorialStep.COMPLETED) {
            return
        }
        _uiState.value = _uiState.value.copy(
            currentStep = step,
            isTutorialActive = (step != TutorialStep.COMPLETED)
        )
    }

    fun advanceStep() {
        val user = SupabaseClient.client.auth.currentUserOrNull()
        if (preferencesManager.isCompletedForUser(user?.id) || _uiState.value.currentStep == TutorialStep.COMPLETED) {
            return
        }

        val next = when (_uiState.value.currentStep) {
            TutorialStep.SPOTLIGHT_EDIT_BUTTON -> TutorialStep.EDIT_ADD_PLANT
            TutorialStep.EDIT_ADD_PLANT -> TutorialStep.EDIT_SELECT_CROP
            TutorialStep.EDIT_SELECT_CROP -> TutorialStep.EDIT_DRAGGING_CROP
            TutorialStep.EDIT_DRAGGING_CROP -> TutorialStep.EDIT_BOTTOM_TOOLBAR_EXPLAIN
            TutorialStep.EDIT_BOTTOM_TOOLBAR_EXPLAIN -> TutorialStep.EDIT_CLOSE_TRAY
            TutorialStep.EDIT_CLOSE_TRAY -> TutorialStep.EDIT_SAVE_FARM
            TutorialStep.EDIT_SAVE_FARM -> TutorialStep.COMPLETED
            TutorialStep.COMPLETED -> TutorialStep.COMPLETED
        }
        
        _uiState.value = _uiState.value.copy(
            currentStep = next,
            isTutorialActive = (next != TutorialStep.COMPLETED)
        )
        if (next == TutorialStep.COMPLETED) {
            completeTutorial()
        }
    }

    fun completeTutorial() {
        val user = SupabaseClient.client.auth.currentUserOrNull()
        preferencesManager.markCompletedForUser(user?.id)
        _uiState.value = _uiState.value.copy(
            currentStep = TutorialStep.COMPLETED,
            isTutorialActive = false
        )
        saveCompletionToBackend()
    }

    fun restartTutorial() {
        val user = SupabaseClient.client.auth.currentUserOrNull()
        preferencesManager.resetTutorialForUser(user?.id)
        _uiState.value = _uiState.value.copy(
            currentStep = TutorialStep.SPOTLIGHT_EDIT_BUTTON,
            isTutorialActive = true
        )
    }

    private fun saveCompletionToBackend() {
        val user = SupabaseClient.client.auth.currentUserOrNull()
        if (user != null) {
            viewModelScope.launch {
                val nowIso = java.time.ZonedDateTime.now().toString()
                try {
                    profileRepository.updateProfile(
                        userId = user.id,
                        nickname = _uiState.value.userNickname.ifEmpty { "Farmer" },
                        tutorialCompletedAt = nowIso
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun skipTutorial() {
        completeTutorial()
    }
}

