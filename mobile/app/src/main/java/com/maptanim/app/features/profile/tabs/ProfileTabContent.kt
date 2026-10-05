package com.maptanim.app.features.profile.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maptanim.app.features.profile.ProfileViewModel
import com.maptanim.app.features.profile.components.ConfirmChoiceDialog
import com.maptanim.app.features.profile.components.UserCommunityActivityCard
import com.maptanim.app.features.profile.components.UserFarmsListCard
import com.maptanim.app.features.profile.components.UserHarvestHistoryCard
import com.maptanim.app.features.profile.components.UserProfileIdentityCard
import com.maptanim.app.features.profile.modals.FullCommunityActivityModal
import com.maptanim.app.features.profile.modals.FullFarmsListModal
import com.maptanim.app.features.profile.modals.FullHarvestHistoryModal
import com.maptanim.app.features.profile.model.ProfileUiState

/**
 * ProfileTabContent — Profile tab coordinator composed of focused sub-cards in Daylight theme.
 * Decomposed from a 719-line monolith down to ~120 lines to strictly satisfy Gate 3.
 */
@Composable
fun ProfileTabContent(
    uiState: ProfileUiState,
    viewModel: ProfileViewModel
) {
    var isFarmsExpanded by remember { mutableStateOf(false) }
    var isHarvestExpanded by remember { mutableStateOf(false) }
    var isForumExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // LEFT SIDE: Avatar & Nickname Identity Card
        UserProfileIdentityCard(
            uiState = uiState,
            onOpenViewAvatar = { viewModel.openViewAvatar() },
            onStartEditNickname = { viewModel.startEditNickname() },
            onNicknameInputChange = { viewModel.updateNicknameInput(it) },
            onSubmitNicknameCheck = { viewModel.submitNicknameCheck() },
            onCancelEditNickname = { viewModel.cancelEditNickname() },
            modifier = Modifier
                .weight(0.38f)
                .fillMaxHeight()
        )

        // RIGHT SIDE: Farms List, Harvest History & Community Forum Activity Cards
        LazyColumn(
            modifier = Modifier
                .weight(0.62f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Farms List Card
            item {
                UserFarmsListCard(
                    farms = uiState.farms,
                    activeFarmId = uiState.activeFarmId,
                    onCreateFarmClick = { viewModel.openCreateFarm() },
                    onSeeMoreClick = { isFarmsExpanded = true },
                    onSelectActiveFarm = { viewModel.selectActiveFarm(it) },
                    onRenameFarmClick = { viewModel.openRenameFarm(it) }
                )
            }

            // 2. Farm Harvest History Card
            item {
                UserHarvestHistoryCard(
                    harvestHistory = uiState.harvestHistory,
                    onSeeMoreClick = { isHarvestExpanded = true }
                )
            }

            // 3. Community Forum Activity Card
            item {
                UserCommunityActivityCard(
                    userPosts = uiState.userPosts,
                    userProfile = uiState.userProfile,
                    onSeeMoreClick = { isForumExpanded = true }
                )
            }
        }
    }

    // Render Full List Modals when See More is clicked
    if (isFarmsExpanded) {
        FullFarmsListModal(
            farms = uiState.farms,
            activeFarmId = uiState.activeFarmId,
            onCreateFarmClick = { viewModel.openCreateFarm() },
            onRenameFarmClick = { viewModel.openRenameFarm(it) },
            onDeleteFarmClick = { viewModel.openDeleteFarm(it) },
            onSelectActiveFarm = { viewModel.selectActiveFarm(it) },
            onDismiss = { isFarmsExpanded = false }
        )
    }

    if (isHarvestExpanded) {
        FullHarvestHistoryModal(
            harvestHistory = uiState.harvestHistory,
            onDismiss = { isHarvestExpanded = false }
        )
    }

    if (isForumExpanded) {
        FullCommunityActivityModal(
            posts = uiState.userPosts,
            currentUserNickname = uiState.userProfile.nickname,
            currentUserId = uiState.userProfile.id,
            onDismiss = { isForumExpanded = false }
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
