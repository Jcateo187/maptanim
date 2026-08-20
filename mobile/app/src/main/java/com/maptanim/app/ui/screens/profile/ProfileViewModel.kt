package com.maptanim.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maptanim.app.data.remote.SupabaseClient
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.data.repository.UserRepositoryImpl
import com.maptanim.app.core.preferences.FarmPreferencesManager
import com.maptanim.app.domain.model.AvatarItem
import com.maptanim.app.domain.model.CommunityPost
import com.maptanim.app.domain.model.Farm
import com.maptanim.app.domain.model.NotificationItem
import com.maptanim.app.domain.model.UserProfile
import com.maptanim.app.domain.model.getDaysRemainingForNicknameChange
import com.maptanim.app.domain.repository.UserRepository
import io.github.jan.supabase.auth.auth
import java.util.UUID
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
    val farms: List<Farm> = emptyList(),
    val activeFarmId: String? = null,
    val userPosts: List<CommunityPost> = emptyList(),
    val harvestHistory: List<com.maptanim.app.domain.model.HarvestRecord> = emptyList(),

    // Farm creation & rename states
    val showCreateFarmModal: Boolean = false,
    val createFarmNameInput: String = "",
    val createFarmError: String? = null,
    val farmToRename: Farm? = null,
    val renameFarmInput: String = "",
    val renameFarmError: String? = null,
    val farmToDelete: Farm? = null,
    val isOperationInProgress: Boolean = false,
    val operationProgressMessage: String? = null,

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

        // Observe real community posts & forum activity
        viewModelScope.launch {
            RepositoryProvider.communityRepository.observePosts().collect { posts ->
                _uiState.update { it.copy(userPosts = posts) }
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
        val remainingDays = com.maptanim.app.domain.model.getDaysRemainingForNicknameChange(_uiState.value.userProfile.nicknameUpdatedAt)
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

    fun updateNicknameInput(text: String) {
        _uiState.update { it.copy(nicknameInput = text, nicknameError = null) }
    }

    fun submitNicknameCheck() {
        val input = _uiState.value.nicknameInput.trim()
        if (input.isBlank()) {
            _uiState.update { it.copy(nicknameError = "Nickname cannot be empty") }
            return
        }

        val remainingDays = com.maptanim.app.domain.model.getDaysRemainingForNicknameChange(_uiState.value.userProfile.nicknameUpdatedAt)
        if (remainingDays > 0) {
            _uiState.update {
                it.copy(nicknameError = "Nickname can only be changed once every 15 days. Please try again in $remainingDays day(s).")
            }
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
                        successMessage = "Nickname updated successfully in Supabase!"
                    )
                }
            } else {
                val remainingDays = com.maptanim.app.domain.model.getDaysRemainingForNicknameChange(_uiState.value.userProfile.nicknameUpdatedAt)
                val errMsg = if (remainingDays > 0) {
                    "Nickname can only be changed once every 15 days. Please try again in $remainingDays day(s)."
                } else "Failed to update nickname. Please try again."
                _uiState.update {
                    it.copy(
                        showNicknameConfirmDialog = false,
                        nicknameError = errMsg
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

    // ─── Farm Flow Handlers ──────────────────────────────────────────────────

    fun openCreateFarm() {
        _uiState.update {
            it.copy(
                showCreateFarmModal = true,
                createFarmNameInput = "",
                createFarmError = null
            )
        }
    }

    fun closeCreateFarm() {
        _uiState.update { it.copy(showCreateFarmModal = false, createFarmError = null) }
    }

    fun updateCreateFarmNameInput(name: String) {
        _uiState.update { it.copy(createFarmNameInput = name, createFarmError = null) }
    }

    fun confirmCreateFarm() {
        val name = _uiState.value.createFarmNameInput.trim()
        if (name.isBlank()) {
            _uiState.update { it.copy(createFarmError = "Farm name cannot be empty") }
            return
        }
        if (name.length < 2) {
            _uiState.update { it.copy(createFarmError = "Farm name must be at least 2 characters") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isOperationInProgress = true, operationProgressMessage = "Creating farm workspace...") }
            val user = SupabaseClient.client.auth.currentUserOrNull()
            val userId = user?.id ?: "guest"
            val newFarmId = "farm_${UUID.randomUUID().toString().take(8)}"
            val now = java.time.LocalDate.now().toString()
            val newFarm = Farm(
                id = newFarmId,
                farmerId = userId,
                farmName = name,
                createdAt = now,
                updatedAt = now
            )
            RepositoryProvider.farmRepository.upsertFarm(newFarm)
            FarmPreferencesManager.getInstance().setActiveFarmId(userId, newFarmId)
            _uiState.update {
                it.copy(
                    showCreateFarmModal = false,
                    createFarmNameInput = "",
                    activeFarmId = newFarmId,
                    isOperationInProgress = false,
                    operationProgressMessage = null,
                    successMessage = "Farm '$name' created successfully!"
                )
            }
        }
    }

    fun openRenameFarm(farm: Farm) {
        _uiState.update {
            it.copy(
                farmToRename = farm,
                renameFarmInput = farm.farmName,
                renameFarmError = null
            )
        }
    }

    fun closeRenameFarm() {
        _uiState.update { it.copy(farmToRename = null, renameFarmError = null) }
    }

    fun updateRenameFarmNameInput(name: String) {
        _uiState.update { it.copy(renameFarmInput = name, renameFarmError = null) }
    }

    fun confirmRenameFarm() {
        val farm = _uiState.value.farmToRename ?: return
        val newName = _uiState.value.renameFarmInput.trim()
        if (newName.isBlank()) {
            _uiState.update { it.copy(renameFarmError = "Farm name cannot be empty") }
            return
        }
        if (newName == farm.farmName) {
            _uiState.update { it.copy(farmToRename = null) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isOperationInProgress = true, operationProgressMessage = "Renaming farm...") }
            val updatedFarm = farm.copy(
                farmName = newName,
                updatedAt = java.time.LocalDate.now().toString()
            )
            RepositoryProvider.farmRepository.upsertFarm(updatedFarm)
            _uiState.update {
                it.copy(
                    farmToRename = null,
                    renameFarmInput = "",
                    isOperationInProgress = false,
                    operationProgressMessage = null,
                    successMessage = "Farm renamed to '$newName'!"
                )
            }
        }
    }

    fun openDeleteFarm(farm: Farm) {
        _uiState.update { it.copy(farmToDelete = farm) }
    }

    fun closeDeleteFarm() {
        _uiState.update { it.copy(farmToDelete = null) }
    }

    fun confirmDeleteFarm() {
        val farm = _uiState.value.farmToDelete ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isOperationInProgress = true, operationProgressMessage = "Deleting farm...") }
            RepositoryProvider.farmRepository.deleteFarm(farm.id)
            val user = SupabaseClient.client.auth.currentUserOrNull()
            val userId = user?.id ?: "guest"
            val remainingFarms = _uiState.value.farms.filter { it.id != farm.id }
            val newActiveId = if (_uiState.value.activeFarmId == farm.id) {
                remainingFarms.firstOrNull()?.id
            } else {
                _uiState.value.activeFarmId
            }
            if (newActiveId != null) {
                FarmPreferencesManager.getInstance().setActiveFarmId(userId, newActiveId)
            } else {
                FarmPreferencesManager.getInstance().clearActiveFarmId(userId)
            }
            _uiState.update {
                it.copy(
                    farmToDelete = null,
                    activeFarmId = newActiveId,
                    isOperationInProgress = false,
                    operationProgressMessage = null,
                    successMessage = "Farm '${farm.farmName}' deleted."
                )
            }
        }
    }

    fun selectActiveFarm(farmId: String) {
        viewModelScope.launch {
            val user = SupabaseClient.client.auth.currentUserOrNull()
            val userId = user?.id ?: "guest"
            _uiState.update { it.copy(isOperationInProgress = true, operationProgressMessage = "Switching active farm...") }
            FarmPreferencesManager.getInstance().setActiveFarmId(userId, farmId)
            val selectedFarm = _uiState.value.farms.firstOrNull { it.id == farmId }
            val name = selectedFarm?.farmName ?: "Farm"
            kotlinx.coroutines.delay(200)
            _uiState.update {
                it.copy(
                    activeFarmId = farmId,
                    isOperationInProgress = false,
                    operationProgressMessage = null,
                    successMessage = "Active farm set to '$name'!"
                )
            }
        }
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
            com.maptanim.app.data.repository.RepositoryProvider.userRepository.logout()
            onComplete()
        }
    }
}
