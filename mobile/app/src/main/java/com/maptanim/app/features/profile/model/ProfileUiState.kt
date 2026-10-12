package com.maptanim.app.features.profile.model

import com.maptanim.app.domain.model.AvatarItem
import com.maptanim.app.domain.model.CommunityPost
import com.maptanim.app.domain.model.Farm
import com.maptanim.app.domain.model.HarvestRecord
import com.maptanim.app.domain.model.NotificationItem
import com.maptanim.app.domain.model.UserProfile

/**
 * Avatar source selection options.
 */
enum class AvatarSourceOption {
    TAKE_PHOTO,
    AVATAR_STORAGE,
    PHOTO_ALBUM
}

/**
 * Unified UI State for Profile, Farms, Harvest History, and Settings.
 */
data class ProfileUiState(
    val selectedTab: Int = 0, // 0: Profile, 1: Notification, 2: Settings
    val userProfile: UserProfile = UserProfile(),
    val availableAvatars: List<AvatarItem> = emptyList(),
    val notifications: List<NotificationItem> = emptyList(),
    val farms: List<Farm> = emptyList(),
    val activeFarmId: String? = null,
    val userPosts: List<CommunityPost> = emptyList(),
    val harvestHistory: List<HarvestRecord> = emptyList(),

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
