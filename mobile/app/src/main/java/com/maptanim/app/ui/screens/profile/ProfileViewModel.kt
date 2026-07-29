package com.maptanim.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.data.repository.UserRepositoryImpl
import com.maptanim.app.domain.model.AvatarItem
import com.maptanim.app.domain.model.NotificationItem
import com.maptanim.app.domain.model.UserProfile
import com.maptanim.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val selectedTab: Int = 0, // 0: Profile, 1: Notification, 2: Settings
    val userProfile: UserProfile = UserProfile(),
    val availableAvatars: List<AvatarItem> = emptyList(),
    val notifications: List<NotificationItem> = emptyList(),
    
    // Avatar picker modal states
    val showAvatarPickerModal: Boolean = false,
    val showViewAvatarModal: Boolean = false,
    val avatarSourceOption: AvatarSourceOption = AvatarSourceOption.AVATAR_STORAGE,
    val pendingAvatarPath: String? = null,
    val showAvatarConfirmDialog: Boolean = false,

    // Nickname edit & validation states
    val isEditingNickname: Boolean = false,
    val nicknameInput: String = "",
    val nicknameError: String? = null,
    val isCheckingNickname: Boolean = false,
    val showNicknameConfirmDialog: Boolean = false,
    val successMessage: String? = null,

    // Notification modal states
    val selectedNotification: NotificationItem? = null,

    // Settings modal states
    val showBindAccountModal: Boolean = false,
    val bindEmailInput: String = "",
    val showReportIssueModal: Boolean = false,
    val issueTextInput: String = "",
    val showLogoutConfirmDialog: Boolean = false
)

enum class AvatarSourceOption {
    TAKE_PHOTO,
    AVATAR_STORAGE,
    PHOTO_ALBUM
}

class ProfileViewModel(
    private val userRepository: UserRepository = UserRepositoryImpl.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.observeUserProfile().collect { profile ->
                _uiState.update { it.copy(userProfile = profile) }
            }
        }
        viewModelScope.launch {
            userRepository.observeNotifications().collect { notifs ->
                _uiState.update { it.copy(notifications = notifs) }
            }
        }
        viewModelScope.launch {
            val avatars = userRepository.getAvailableAvatars()
            _uiState.update { it.copy(availableAvatars = avatars) }
        }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    // ─── Avatar Flow Handlers ──────────────────────────────────────────────

    fun openViewAvatar() {
        _uiState.update { it.copy(showViewAvatarModal = true) }
    }

    fun closeViewAvatar() {
        _uiState.update { it.copy(showViewAvatarModal = false) }
    }

    fun openAvatarPicker() {
        _uiState.update {
            it.copy(
                showViewAvatarModal = false,
                showAvatarPickerModal = true,
                avatarSourceOption = AvatarSourceOption.AVATAR_STORAGE
            )
        }
    }

    fun closeAvatarPicker() {
        _uiState.update { it.copy(showAvatarPickerModal = false) }
    }

    fun selectAvatarOption(option: AvatarSourceOption) {
        _uiState.update { it.copy(avatarSourceOption = option) }
    }

    fun requestAvatarSelect(assetPath: String) {
        _uiState.update {
            it.copy(
                pendingAvatarPath = assetPath,
                showAvatarConfirmDialog = true
            )
        }
    }

    fun confirmAvatarChange() {
        val path = _uiState.value.pendingAvatarPath ?: return
        viewModelScope.launch {
            userRepository.updateAvatar(path)
            _uiState.update {
                it.copy(
                    showAvatarConfirmDialog = false,
                    showAvatarPickerModal = false,
                    pendingAvatarPath = null,
                    successMessage = "Avatar changed successfully!"
                )
            }
        }
    }

    fun cancelAvatarConfirm() {
        _uiState.update {
            it.copy(
                showAvatarConfirmDialog = false,
                pendingAvatarPath = null
            )
        }
    }

    // ─── Nickname Flow Handlers ─────────────────────────────────────────────

    fun startEditNickname() {
        _uiState.update {
            it.copy(
                isEditingNickname = true,
                nicknameInput = it.userProfile.nickname,
                nicknameError = null
            )
        }
    }

    fun updateNicknameInput(text: String) {
        _uiState.update { it.copy(nicknameInput = text, nicknameError = null) }
    }

    fun submitNicknameCheck() {
        val input = _uiState.value.nicknameInput.trim()
        if (input.isBlank()) {
            _uiState.update { it.copy(nicknameError = "Nickname cannot be empty") }
            return
        }

        _uiState.update { it.copy(isCheckingNickname = true, nicknameError = null) }
        viewModelScope.launch {
            val available = userRepository.isNicknameAvailable(input)
            if (available) {
                _uiState.update {
                    it.copy(
                        isCheckingNickname = false,
                        showNicknameConfirmDialog = true
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isCheckingNickname = false,
                        nicknameError = "Nickname is already taken. Please choose a unique nickname."
                    )
                }
            }
        }
    }

    fun confirmNicknameChange() {
        val input = _uiState.value.nicknameInput.trim()
        viewModelScope.launch {
            val success = userRepository.updateNickname(input)
            if (success) {
                _uiState.update {
                    it.copy(
                        showNicknameConfirmDialog = false,
                        isEditingNickname = false,
                        nicknameInput = "",
                        successMessage = "Nickname has been changed!"
                    )
                }
            }
        }
    }

    fun cancelNicknameConfirm() {
        _uiState.update { it.copy(showNicknameConfirmDialog = false) }
    }

    fun cancelEditNickname() {
        _uiState.update {
            it.copy(
                isEditingNickname = false,
                nicknameInput = "",
                nicknameError = null
            )
        }
    }

    fun dismissSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }

    // ─── Notification Flow Handlers ─────────────────────────────────────────

    fun selectNotification(notification: NotificationItem) {
        _uiState.update { it.copy(selectedNotification = notification) }
        viewModelScope.launch {
            userRepository.markNotificationAsRead(notification.id)
        }
    }

    fun dismissNotificationDetail() {
        _uiState.update { it.copy(selectedNotification = null) }
    }

    fun deleteNotification(id: String) {
        viewModelScope.launch {
            userRepository.deleteNotification(id)
            if (_uiState.value.selectedNotification?.id == id) {
                _uiState.update { it.copy(selectedNotification = null) }
            }
        }
    }

    // ─── Settings Flow Handlers ─────────────────────────────────────────────

    fun openBindAccount() {
        _uiState.update { it.copy(showBindAccountModal = true, bindEmailInput = "") }
    }

    fun closeBindAccount() {
        _uiState.update { it.copy(showBindAccountModal = false) }
    }

    fun updateBindEmailInput(email: String) {
        _uiState.update { it.copy(bindEmailInput = email) }
    }

    fun submitBindAccount() {
        val email = _uiState.value.bindEmailInput.trim()
        if (email.contains("@")) {
            viewModelScope.launch {
                userRepository.bindAccount(email)
                _uiState.update {
                    it.copy(
                        showBindAccountModal = false,
                        successMessage = "Account bound successfully!"
                    )
                }
            }
        }
    }

    fun openReportIssue() {
        _uiState.update { it.copy(showReportIssueModal = true, issueTextInput = "") }
    }

    fun closeReportIssue() {
        _uiState.update { it.copy(showReportIssueModal = false) }
    }

    fun updateIssueInput(text: String) {
        _uiState.update { it.copy(issueTextInput = text) }
    }

    fun submitReportIssue() {
        if (_uiState.value.issueTextInput.isNotBlank()) {
            _uiState.update {
                it.copy(
                    showReportIssueModal = false,
                    issueTextInput = "",
                    successMessage = "Issue report sent to Admin!"
                )
            }
        }
    }

    fun openLogoutConfirm() {
        _uiState.update { it.copy(showLogoutConfirmDialog = true) }
    }

    fun cancelLogout() {
        _uiState.update { it.copy(showLogoutConfirmDialog = false) }
    }
}
