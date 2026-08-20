package com.maptanim.app.ui.screens.profile

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.maptanim.app.ui.components.profile.ChangeAvatarModal
import com.maptanim.app.ui.components.profile.ConfirmChoiceDialog
import com.maptanim.app.ui.components.profile.ViewAvatarDialog
import com.maptanim.app.ui.screens.profile.modals.CreateFarmDialog
import com.maptanim.app.ui.screens.profile.modals.RenameFarmDialog
import com.maptanim.app.ui.screens.profile.tabs.NotificationsTabContent
import com.maptanim.app.ui.screens.profile.tabs.ProfileTabContent
import com.maptanim.app.ui.screens.profile.tabs.SettingsTabContent
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    initialTab: Int = 0,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(initialTab) {
        if (initialTab in 0..2) {
            viewModel.selectTab(initialTab)
        }
    }

    val view = LocalView.current
    DisposableEffect(view) {
        val window = (view.parent as? DialogWindowProvider)?.window
            ?: (view.context as? Activity)?.window
        window?.let { win ->
            WindowCompat.setDecorFitsSystemWindows(win, false)
            WindowInsetsControllerCompat(win, win.decorView).apply {
                hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
        onDispose {}
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                navController.popBackStack()
            },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .fillMaxHeight(0.88f)
                .padding(vertical = 12.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {},
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D15)),
            border = BorderStroke(1.5.dp, Color(0xFF2E4D3E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header Bar (Back button + Profile / Notification / Settings Tabs)
                Surface(
                    color = Color(0xFF1C271E),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = White
                            )
                        }

                        PrimaryTabRow(
                            selectedTabIndex = uiState.selectedTab,
                            containerColor = Color.Transparent,
                            contentColor = ForestGreen,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            indicator = {
                                TabRowDefaults.PrimaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(selectedTabIndex = uiState.selectedTab),
                                    color = ForestGreen,
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
                                        "Profile",
                                        color = if (uiState.selectedTab == 0) White else White.copy(alpha = 0.6f),
                                        fontWeight = if (uiState.selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp
                                    )
                                },
                                icon = { Icon(Icons.Default.Person, contentDescription = null, tint = if (uiState.selectedTab == 0) ForestGreen else White.copy(alpha = 0.6f), modifier = Modifier.size(18.dp)) }
                            )
                            Tab(
                                selected = uiState.selectedTab == 1,
                                onClick = { viewModel.selectTab(1) },
                                text = {
                                    Text(
                                        "Notification",
                                        color = if (uiState.selectedTab == 1) White else White.copy(alpha = 0.6f),
                                        fontWeight = if (uiState.selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp
                                    )
                                },
                                icon = {
                                    BadgedBox(badge = {
                                        val unread = uiState.notifications.count { !it.isRead }
                                        if (unread > 0) {
                                            Badge(containerColor = Color.Red, contentColor = White) {
                                                Text(unread.toString(), fontSize = 10.sp)
                                            }
                                        }
                                    }) {
                                        Icon(Icons.Default.Notifications, contentDescription = null, tint = if (uiState.selectedTab == 1) ForestGreen else White.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
                                    }
                                }
                            )
                            Tab(
                                selected = uiState.selectedTab == 2,
                                onClick = { viewModel.selectTab(2) },
                                text = {
                                    Text(
                                        "Settings",
                                        color = if (uiState.selectedTab == 2) White else White.copy(alpha = 0.6f),
                                        fontWeight = if (uiState.selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp
                                    )
                                },
                                icon = { Icon(Icons.Default.Settings, contentDescription = null, tint = if (uiState.selectedTab == 2) ForestGreen else White.copy(alpha = 0.6f), modifier = Modifier.size(18.dp)) }
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 10.dp)
                ) {
                    // Toast / Snack notification message
                    uiState.successMessage?.let { msg ->
                        Surface(
                            color = ForestGreen,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(msg, color = White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                IconButton(onClick = { viewModel.dismissSuccessMessage() }, modifier = Modifier.size(18.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = White)
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
                            1 -> NotificationsTabContent(uiState = uiState, viewModel = viewModel)
                            2 -> SettingsTabContent(uiState = uiState, viewModel = viewModel, navController = navController)
                        }
                    }
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
        ChangeAvatarModal(
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

    if (uiState.showNicknameConfirmDialog) {
        ConfirmChoiceDialog(
            title = "Confirm Nickname Change",
            message = "Are you sure you want to choose nickname '${uiState.nicknameInput.trim()}'?",
            onConfirm = { viewModel.confirmNicknameChange() },
            onDismiss = { viewModel.cancelNicknameConfirm() }
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
        androidx.compose.ui.window.Dialog(
            onDismissRequest = {},
            properties = androidx.compose.ui.window.DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1B2418),
                border = BorderStroke(1.5.dp, ForestGreen.copy(alpha = 0.6f)),
                shadowElevation = 12.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        color = ForestGreen,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = uiState.operationProgressMessage ?: "Please wait...",
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
