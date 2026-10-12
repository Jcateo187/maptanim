package com.maptanim.app.features.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.maptanim.app.navigation.MainBottomNavBar
import com.maptanim.app.navigation.Routes
import com.maptanim.app.features.profile.components.AvatarPickerModal
import com.maptanim.app.features.profile.components.ConfirmChoiceDialog
import com.maptanim.app.features.profile.components.ViewAvatarDialog
import com.maptanim.app.features.profile.modals.CreateFarmDialog
import com.maptanim.app.features.profile.modals.RenameFarmDialog
import com.maptanim.app.features.profile.tabs.NotificationsTabContent
import com.maptanim.app.features.profile.tabs.ProfileTabContent
import com.maptanim.app.features.profile.tabs.SettingsTabContent

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * ProfileScreen — User account settings, farms list, harvest records, and notifications.
 * Strictly adheres to the Daylight High-Contrast Theme (Pure White background, Lush Green accents, Deep Black typography).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    initialTab: Int = 0,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(initialTab) {
        val tab = if (initialTab >= 1) 1 else 0
        viewModel.selectTab(tab)
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Surface(
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                PrimaryTabRow(
                    selectedTabIndex = uiState.selectedTab.coerceIn(0, 1),
                    containerColor = Color.White,
                    contentColor = LushGreen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    indicator = {
                        TabRowDefaults.PrimaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(selectedTabIndex = uiState.selectedTab.coerceIn(0, 1)),
                            color = LushGreen,
                            height = 3.dp
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = uiState.selectedTab == 0,
                        onClick = { viewModel.selectTab(0) },
                        text = {
                            Text(
                                text = "Profile",
                                color = if (uiState.selectedTab == 0) DeepBlack else MutedText,
                                fontWeight = if (uiState.selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = if (uiState.selectedTab == 0) LushGreen else MutedText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )

                    Tab(
                        selected = uiState.selectedTab == 1,
                        onClick = { viewModel.selectTab(1) },
                        text = {
                            Text(
                                text = "Settings",
                                color = if (uiState.selectedTab == 1) DeepBlack else MutedText,
                                fontWeight = if (uiState.selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = if (uiState.selectedTab == 1) LushGreen else MutedText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }
        },
        bottomBar = {
            MainBottomNavBar(
                selectedRoute = Routes.PROFILE,
                onNavigate = { route ->
                    if (route != Routes.PROFILE) {
                        navController.navigate(route) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
                // Success Toast Banner
                uiState.successMessage?.let { msg ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE8F5E9),
                        border = BorderStroke(1.dp, LushGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = msg,
                                color = LushGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.dismissSuccessMessage() },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = LushGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                // Tab Content Switcher
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    when (uiState.selectedTab) {
                        0 -> ProfileTabContent(uiState = uiState, viewModel = viewModel)
                        1 -> SettingsTabContent(uiState = uiState, viewModel = viewModel, navController = navController)
                        else -> NotificationsTabContent(uiState = uiState, viewModel = viewModel)
                    }
                }
            }
        }

    // ── Avatar Modals & Dialogs ────────────────────────────────────────────────
    if (uiState.showViewAvatarModal) {
        ViewAvatarDialog(
            avatarAssetPath = uiState.userProfile.avatarAssetPath,
            onDismiss = { viewModel.closeViewAvatar() },
            onChangeAvatarClick = { viewModel.openAvatarPicker() }
        )
    }

    if (uiState.showAvatarPickerModal) {
        AvatarPickerModal(
            availableAvatars = uiState.availableAvatars,
            currentSource = uiState.avatarSourceOption,
            onSelectSource = { viewModel.selectAvatarOption(it) },
            onSelectAvatar = { viewModel.requestAvatarSelect(it) },
            onDismiss = { viewModel.closeAvatarPicker() }
        )
    }

    if (uiState.showAvatarConfirmDialog) {
        ConfirmChoiceDialog(
            title = "Confirm Avatar Change",
            message = "Are you sure you want to choose this avatar?",
            onConfirm = { viewModel.confirmAvatarChange() },
            onDismiss = { viewModel.cancelAvatarConfirm() }
        )
    }

    // ── Farm Modals & Dialogs ──────────────────────────────────────────────────
    if (uiState.showCreateFarmModal) {
        CreateFarmDialog(
            farmName = uiState.createFarmNameInput,
            errorMessage = uiState.createFarmError,
            onFarmNameChange = { viewModel.updateCreateFarmNameInput(it) },
            onConfirm = { viewModel.confirmCreateFarm() },
            onDismiss = { viewModel.closeCreateFarm() }
        )
    }

    uiState.farmToRename?.let { farm ->
        RenameFarmDialog(
            currentFarmName = farm.farmName,
            nameInput = uiState.renameFarmInput,
            errorMessage = uiState.renameFarmError,
            onNameChange = { viewModel.updateRenameFarmNameInput(it) },
            onConfirm = { viewModel.confirmRenameFarm() },
            onDismiss = { viewModel.closeRenameFarm() }
        )
    }

    uiState.farmToDelete?.let { farm ->
        ConfirmChoiceDialog(
            title = "Delete Farm",
            message = "Are you sure you want to delete '${farm.farmName}'? This action cannot be undone.",
            onConfirm = { viewModel.confirmDeleteFarm() },
            onDismiss = { viewModel.closeDeleteFarm() }
        )
    }

    // ── Farm Operation Loading Modal ───────────────────────────────────────────
    if (uiState.isOperationInProgress) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        color = LushGreen,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = uiState.operationProgressMessage ?: "Please wait...",
                        color = DeepBlack,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
