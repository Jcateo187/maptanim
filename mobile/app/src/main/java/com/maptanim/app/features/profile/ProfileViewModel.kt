package com.maptanim.app.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.data.remote.SupabaseClient
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.data.repository.UserRepositoryImpl
import com.maptanim.app.core.preferences.CommunityPreferencesManager
import com.maptanim.app.core.preferences.FarmPreferencesManager
import com.maptanim.app.domain.model.Farm
import com.maptanim.app.domain.model.NotificationItem
import com.maptanim.app.domain.model.getDaysRemainingForNicknameChange
import com.maptanim.app.domain.repository.UserRepository
import com.maptanim.app.features.profile.delegate.ProfileFarmDelegate
import com.maptanim.app.features.profile.model.AvatarSourceOption
import com.maptanim.app.features.profile.model.ProfileUiState
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Typealias for backward compatibility
typealias ProfileUiState = com.maptanim.app.features.profile.model.ProfileUiState
typealias AvatarSourceOption = com.maptanim.app.features.profile.model.AvatarSourceOption

class ProfileViewModel(
    private val userRepository: UserRepository = UserRepositoryImpl.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val farmDelegate = ProfileFarmDelegate(viewModelScope, _uiState)

    init {
        viewModelScope.launch {
            (userRepository as? UserRepositoryImpl)?.loadUserProfile()
            userRepository.refreshNotifications()
        }
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

        // Observe real farms list from Room / Supabase database
        viewModelScope.launch {
            val user = SupabaseClient.client.auth.currentUserOrNull()
            val userId = user?.id ?: "guest"
            val savedActiveFarmId = FarmPreferencesManager.getInstance().getActiveFarmId(userId)
            RepositoryProvider.farmRepository.observeFarms(userId).collect { farms ->
                val currentActive = _uiState.value.activeFarmId ?: savedActiveFarmId
                val effectiveActive = if (farms.any { it.id == currentActive }) currentActive else farms.firstOrNull()?.id
                _uiState.update {
                    it.copy(
                        farms = farms,
                        activeFarmId = effectiveActive
                    )
                }
            }
        }

        // Observe real harvest history records
        viewModelScope.launch {
            RepositoryProvider.harvestRepository.observeHarvestRecords("farm-1").collect { records ->
                _uiState.update { it.copy(harvestHistory = records) }
            }
        }

        // Observe real community posts & forum activity for current user
        viewModelScope.launch {
            combine(
                userRepository.observeUserProfile(),
                RepositoryProvider.communityRepository.observePosts()
            ) { profile, allPosts ->
                val currentUserId = try {
                    SupabaseClient.client.auth.currentUserOrNull()?.id
                } catch (e: Exception) {
                    null
                } ?: profile.id.ifBlank { null }

                val userNickname = profile.nickname.trim()
                val userEmailPrefix = profile.boundEmail?.substringBefore('@')?.trim()
                val myLocalPostIds = CommunityPreferencesManager.getInstance().getMyPostIds(currentUserId)
                val myLocalLikedIds = CommunityPreferencesManager.getInstance().getLikedPostIds(currentUserId)

                allPosts.filter { post ->
                    val isAuthoredByMe = (currentUserId != null && post.authorId != null && post.authorId == currentUserId) ||
                            (userNickname.isNotBlank() && post.authorName.equals(userNickname, ignoreCase = true)) ||
                            (!userEmailPrefix.isNullOrBlank() && post.authorName.equals(userEmailPrefix, ignoreCase = true)) ||
                            post.authorName.equals("You", ignoreCase = true) ||
                            post.id in myLocalPostIds

                    val isReactedByMe = post.isLikedByMe || post.id in myLocalLikedIds

                    isAuthoredByMe || isReactedByMe
                }
            }.collect { filteredPosts ->
                _uiState.update { it.copy(userPosts = filteredPosts) }
            }
        }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
        if (index == 1) {
            viewModelScope.launch {
                userRepository.refreshNotifications()
            }
        }
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
        val remainingDays = getDaysRemainingForNicknameChange(_uiState.value.userProfile.nicknameUpdatedAt)
        val initialError = if (remainingDays > 0) {
            "Nickname can only be changed once every 15 days. Please try again in $remainingDays day(s)."
        } else null

        _uiState.update {
            it.copy(
                isEditingNickname = true,
                nicknameInput = it.userProfile.nickname,
                nicknameError = initialError
            )
        }
    }

    fun updateNicknameInput(input: String) {
        _uiState.update {
            it.copy(
                nicknameInput = input,
                nicknameError = null
            )
        }
    }

    fun submitNicknameCheck() {
        val newNickname = _uiState.value.nicknameInput.trim()
        val currentNickname = _uiState.value.userProfile.nickname.trim()

        if (newNickname == currentNickname) {
            _uiState.update { it.copy(isEditingNickname = false) }
            return
        }

        val remainingDays = getDaysRemainingForNicknameChange(_uiState.value.userProfile.nicknameUpdatedAt)
        if (remainingDays > 0) {
            _uiState.update {
                it.copy(nicknameError = "Nickname can only be changed once every 15 days. Please try again in $remainingDays day(s).")
            }
            return
        }

        if (newNickname.isBlank()) {
            _uiState.update { it.copy(nicknameError = "Nickname cannot be empty") }
            return
        }

        if (newNickname.length < 3 || newNickname.length > 20) {
            _uiState.update { it.copy(nicknameError = "Nickname must be 3-20 characters long") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingNickname = true, nicknameError = null) }
            val isAvailable = userRepository.isNicknameAvailable(newNickname)
            _uiState.update { it.copy(isCheckingNickname = false) }

            if (!isAvailable) {
                _uiState.update { it.copy(nicknameError = "Nickname '$newNickname' is already taken") }
            } else {
                _uiState.update { it.copy(showNicknameConfirmDialog = true) }
            }
        }
    }

    fun confirmNicknameChange() {
        val newNickname = _uiState.value.nicknameInput.trim()
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingNickname = true) }
            val success = userRepository.updateNickname(newNickname)
            _uiState.update {
                it.copy(
                    isCheckingNickname = false,
                    showNicknameConfirmDialog = false,
                    isEditingNickname = false,
                    nicknameInput = "",
                    successMessage = if (success) "Nickname updated to '$newNickname'!" else null,
                    nicknameError = if (!success) "Failed to update nickname. Please try again." else null
                )
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

    // ─── Farm Flow Handlers (Delegated to ProfileFarmDelegate) ────────────────

    fun openCreateFarm() = farmDelegate.openCreateFarm()
    fun closeCreateFarm() = farmDelegate.closeCreateFarm()
    fun updateCreateFarmNameInput(name: String) = farmDelegate.updateCreateFarmNameInput(name)
    fun confirmCreateFarm() = farmDelegate.confirmCreateFarm()

    fun openRenameFarm(farm: Farm) = farmDelegate.openRenameFarm(farm)
    fun closeRenameFarm() = farmDelegate.closeRenameFarm()
    fun updateRenameFarmNameInput(name: String) = farmDelegate.updateRenameFarmNameInput(name)
    fun confirmRenameFarm() = farmDelegate.confirmRenameFarm()

    fun openDeleteFarm(farm: Farm) = farmDelegate.openDeleteFarm(farm)
    fun closeDeleteFarm() = farmDelegate.closeDeleteFarm()
    fun confirmDeleteFarm() = farmDelegate.confirmDeleteFarm()

    fun selectActiveFarm(farmId: String) = farmDelegate.selectActiveFarm(farmId)

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
        val message = _uiState.value.issueTextInput.trim()
        if (message.isNotBlank()) {
            viewModelScope.launch {
                userRepository.sendSupportTicket(
                    subject = "Farmer App Issue Report",
                    message = message,
                    category = "GENERAL"
                )
                _uiState.update {
                    it.copy(
                        showReportIssueModal = false,
                        issueTextInput = "",
                        successMessage = "Issue report sent to Admin!"
                    )
                }
            }
        }
    }

    fun openLogoutConfirm() {
        _uiState.update { it.copy(showLogoutConfirmDialog = true) }
    }

    fun cancelLogout() {
        _uiState.update { it.copy(showLogoutConfirmDialog = false) }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(showLogoutConfirmDialog = false) }
            RepositoryProvider.userRepository.logout()
            onComplete()
        }
    }
}
