package com.maptanim.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.maptanim.app.navigation.Routes
import com.maptanim.app.ui.components.avatar.ProfileAvatar
import com.maptanim.app.ui.components.profile.ChangeAvatarModal
import com.maptanim.app.ui.components.profile.ConfirmChoiceDialog
import com.maptanim.app.ui.components.profile.ViewAvatarDialog
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.TextPrimary
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "User Hub",
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1B2317))
            )
        },
        containerColor = Color(0xFF121810)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ── 3 Main Top Tabs (Profile, Notification, Settings) ────────────────
            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = Color(0xFF1B2317),
                contentColor = ForestGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab]),
                        color = ForestGreen,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    text = {
                        Text(
                            "Profile",
                            color = if (uiState.selectedTab == 0) White else White.copy(alpha = 0.6f),
                            fontWeight = if (uiState.selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = null, tint = if (uiState.selectedTab == 0) ForestGreen else White.copy(alpha = 0.6f)) }
                )
                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    text = {
                        Text(
                            "Notification",
                            color = if (uiState.selectedTab == 1) White else White.copy(alpha = 0.6f),
                            fontWeight = if (uiState.selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = {
                        BadgedBox(badge = {
                            val unread = uiState.notifications.count { !it.isRead }
                            if (unread > 0) {
                                Badge(containerColor = Color.Red, contentColor = White) {
                                    Text(unread.toString())
                                }
                            }
                        }) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = if (uiState.selectedTab == 1) ForestGreen else White.copy(alpha = 0.6f))
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
                            fontWeight = if (uiState.selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null, tint = if (uiState.selectedTab == 2) ForestGreen else White.copy(alpha = 0.6f)) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Toast / Snack notification message
            uiState.successMessage?.let { msg ->
                Surface(
                    color = ForestGreen,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(msg, color = White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        IconButton(onClick = { viewModel.dismissSuccessMessage() }, modifier = Modifier.size(20.dp)) {
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
                    1 -> NotificationTabContent(uiState = uiState, viewModel = viewModel)
                    2 -> SettingsTabContent(uiState = uiState, viewModel = viewModel, navController = navController)
                }
            }
        }
    }

    // ── Modals & Dialogs ───────────────────────────────────────────────────────

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
}

// ─── TAB 1: PROFILE CONTENT ────────────────────────────────────────────────

@Composable
private fun ProfileTabContent(
    uiState: ProfileUiState,
    viewModel: ProfileViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Avatar Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E261A),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileAvatar(
                    avatarAssetPath = uiState.userProfile.avatarAssetPath,
                    size = 96.dp,
                    onClick = { viewModel.openViewAvatar() }
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = uiState.userProfile.nickname,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Text(
                    text = "Tap avatar to view or change profile photo",
                    fontSize = 12.sp,
                    color = White.copy(alpha = 0.6f)
                )
            }
        }

        // Nickname Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E261A),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Badge, contentDescription = null, tint = ForestGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Nickname", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = White)
                    }

                    if (!uiState.isEditingNickname) {
                        TextButton(onClick = { viewModel.startEditNickname() }) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit", color = ForestGreen)
                        }
                    }
                }

                if (uiState.isEditingNickname) {
                    OutlinedTextField(
                        value = uiState.nicknameInput,
                        onValueChange = { viewModel.updateNicknameInput(it) },
                        label = { Text("Enter Unique Nickname", color = White.copy(alpha = 0.7f)) },
                        isError = uiState.nicknameError != null,
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            unfocusedBorderColor = White.copy(alpha = 0.3f),
                            focusedTextColor = White,
                            unfocusedTextColor = White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    uiState.nicknameError?.let { err ->
                        Text(text = err, color = Color.Red, fontSize = 12.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { viewModel.cancelEditNickname() }) {
                            Text("Cancel", color = White.copy(alpha = 0.7f))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { viewModel.submitNicknameCheck() },
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            enabled = !uiState.isCheckingNickname
                        ) {
                            if (uiState.isCheckingNickname) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = White, strokeWidth = 2.dp)
                            } else {
                                Text("Save & Check Availability")
                            }
                        }
                    }
                } else {
                    Text(
                        text = uiState.userProfile.nickname,
                        fontSize = 16.sp,
                        color = White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

// ─── TAB 2: NOTIFICATION CONTENT ───────────────────────────────────────────

@Composable
private fun NotificationTabContent(
    uiState: ProfileUiState,
    viewModel: ProfileViewModel
) {
    if (uiState.notifications.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No notifications available.", color = White.copy(alpha = 0.6f))
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(uiState.notifications) { item ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (item.isRead) Color(0xFF192016) else Color(0xFF243020),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectNotification(item) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (item.isRead) Color.Gray else ForestGreen)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                fontWeight = if (item.isRead) FontWeight.Normal else FontWeight.Bold,
                                color = White,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.message,
                                color = White.copy(alpha = 0.75f),
                                fontSize = 13.sp,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.timestamp,
                                color = White.copy(alpha = 0.45f),
                                fontSize = 11.sp
                            )
                        }
                        IconButton(onClick = { viewModel.deleteNotification(item.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = White.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }

    // Detail dialog when clicking system message
    uiState.selectedNotification?.let { notif ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissNotificationDetail() },
            title = { Text(notif.title, color = White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(notif.message, color = White.copy(alpha = 0.9f))
                    Text("Received: ${notif.timestamp}", color = White.copy(alpha = 0.5f), fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissNotificationDetail() },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.deleteNotification(notif.id) }) {
                    Text("Delete Message", color = Color.Red)
                }
            },
            containerColor = Color(0xFF1E261A)
        )
    }
}

// ─── TAB 3: SETTINGS CONTENT ───────────────────────────────────────────────

@Composable
private fun SettingsTabContent(
    uiState: ProfileUiState,
    viewModel: ProfileViewModel,
    navController: NavHostController
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Bind Account Section
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF1E261A),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Account Binding", fontWeight = FontWeight.Bold, color = White, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (uiState.userProfile.isAccountBound)
                            "Already bound (${uiState.userProfile.boundEmail})"
                        else "Not bound to external cloud",
                        color = if (uiState.userProfile.isAccountBound) ForestGreen else White.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                }

                if (!uiState.userProfile.isAccountBound) {
                    Button(
                        onClick = { viewModel.openBindAccount() },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                    ) {
                        Text("Bind Account")
                    }
                } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Bound", tint = ForestGreen)
                }
            }
        }

        // System Recommendation & Report Issue
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF1E261A),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.openReportIssue() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Report Issue / Feedback", fontWeight = FontWeight.Bold, color = White, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Send system feedback or issues to Admin", color = White.copy(alpha = 0.6f), fontSize = 13.sp)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = White)
            }
        }

        // Log out Section
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF1E261A),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.openLogoutConfirm() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Log Out", fontWeight = FontWeight.Bold, color = Color.Red, fontSize = 15.sp)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Red)
            }
        }
    }

    // Modal: Bind Account
    if (uiState.showBindAccountModal) {
        AlertDialog(
            onDismissRequest = { viewModel.closeBindAccount() },
            title = { Text("Bind Account", color = White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter email to bind your local farm data to Supabase cloud:", color = White.copy(alpha = 0.8f))
                    OutlinedTextField(
                        value = uiState.bindEmailInput,
                        onValueChange = { viewModel.updateBindEmailInput(it) },
                        label = { Text("Email Address") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ForestGreen, focusedTextColor = White, unfocusedTextColor = White)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.submitBindAccount() },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Text("Create & Bind")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeBindAccount() }) {
                    Text("Cancel", color = White.copy(alpha = 0.7f))
                }
            },
            containerColor = Color(0xFF1E261A)
        )
    }

    // Modal: Report Issue
    if (uiState.showReportIssueModal) {
        AlertDialog(
            onDismissRequest = { viewModel.closeReportIssue() },
            title = { Text("Report Issue to Admin", color = White, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = uiState.issueTextInput,
                    onValueChange = { viewModel.updateIssueInput(it) },
                    label = { Text("Describe the issue...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ForestGreen, focusedTextColor = White, unfocusedTextColor = White)
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.submitReportIssue() },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Text("Send to Admin")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeReportIssue() }) {
                    Text("Cancel", color = White.copy(alpha = 0.7f))
                }
            },
            containerColor = Color(0xFF1E261A)
        )
    }

    // Modal: Logout Confirm
    if (uiState.showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.cancelLogout() },
            title = { Text("Log Out Confirmation", color = White, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to log out?", color = White.copy(alpha = 0.9f)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelLogout()
                        navController.navigate(Routes.WELCOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Yes, Log Out")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.cancelLogout() }) {
                    Text("No, Stay in Settings", color = White)
                }
            },
            containerColor = Color(0xFF1E261A)
        )
    }
}
