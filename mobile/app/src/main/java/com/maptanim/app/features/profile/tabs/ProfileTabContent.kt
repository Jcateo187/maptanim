package com.maptanim.app.features.profile.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.maptanim.app.core.preferences.FavoriteCropsManager
import com.maptanim.app.features.farm.dialogs.CropInformationDialog
import com.maptanim.app.features.profile.ProfileViewModel
import com.maptanim.app.features.profile.components.ConfirmChoiceDialog
import com.maptanim.app.features.profile.components.UserCommunityActivityCard
import com.maptanim.app.features.profile.components.UserFavoriteCropsSection
import com.maptanim.app.features.profile.components.UserProfileIdentityCard
import com.maptanim.app.features.profile.modals.FullCommunityActivityModal
import com.maptanim.app.features.profile.model.ProfileUiState

/**
 * ProfileTabContent — Profile tab configured for portrait layout:
 * - Top: 1 card containing Avatar (left) and Nickname with edit icon (right)
 * - Middle: 1 row horizontal scroll for Favorite items (bottom of avatar card)
 * - Bottom: User's Activity History with heading outside/top of card
 */
@Composable
fun ProfileTabContent(
    uiState: ProfileUiState,
    viewModel: ProfileViewModel
) {
    val context = LocalContext.current
    val favoriteManager = remember(context) { FavoriteCropsManager.getInstance(context) }
    val favoriteCrops by favoriteManager.favoriteCrops.collectAsState()
    var isForumExpanded by remember { mutableStateOf(false) }
    var selectedCropDialog by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TOP: Single card with Avatar on the left and Nickname with edit icon on the right
        item {
            UserProfileIdentityCard(
                uiState = uiState,
                onOpenViewAvatar = { viewModel.openViewAvatar() },
                onStartEditNickname = { viewModel.startEditNickname() },
                onNicknameInputChange = { viewModel.updateNicknameInput(it) },
                onSubmitNicknameCheck = { viewModel.submitNicknameCheck() },
                onCancelEditNickname = { viewModel.cancelEditNickname() }
            )
        }

        // MIDDLE: Favorite Crops horizontal row (bottom of avatar card)
        item {
            UserFavoriteCropsSection(
                favoriteCrops = favoriteCrops.toList(),
                onCropClick = { cropName ->
                    selectedCropDialog = cropName
                }
            )
        }

        // BOTTOM: User Activity History (heading is outside on top of the card)
        item {
            UserCommunityActivityCard(
                userPosts = uiState.userPosts,
                userProfile = uiState.userProfile,
                onSeeMoreClick = { isForumExpanded = true }
            )
        }
    }

    // Full activity history modal when "See More" is tapped
    if (isForumExpanded) {
        FullCommunityActivityModal(
            posts = uiState.userPosts,
            currentUserNickname = uiState.userProfile.nickname,
            currentUserId = uiState.userProfile.id,
            onDismiss = { isForumExpanded = false }
        )
    }

    // Crop Information Dialog when a favorite crop card is clicked
    selectedCropDialog?.let { cropName ->
        CropInformationDialog(
            cropName = cropName,
            onDismiss = { selectedCropDialog = null }
        )
    }

    // Nickname confirmation dialog
    if (uiState.showNicknameConfirmDialog) {
        ConfirmChoiceDialog(
            title = "Confirm Nickname Change",
            message = "Are you sure you want to change your nickname to '${uiState.nicknameInput.trim()}'? You will not be able to change it again for 15 days.",
            onConfirm = { viewModel.confirmNicknameChange() },
            onDismiss = { viewModel.cancelNicknameConfirm() }
        )
    }
}
