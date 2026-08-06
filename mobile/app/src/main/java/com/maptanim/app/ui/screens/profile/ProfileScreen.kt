package com.maptanim.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.maptanim.app.core.audio.LocalSoundManager
import com.maptanim.app.core.audio.SoundEffect
import com.maptanim.app.domain.model.NotificationItem
import com.maptanim.app.ui.screens.settings.AudioSettingsDialog

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
            Surface(
                color = Color(0xFF1B2317),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(56.dp)
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = White
                        )
                    }

                    TabRow(
                        selectedTabIndex = uiState.selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = ForestGreen,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab]),
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
        },
        containerColor = Color(0xFF121810)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

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

// ─── TAB 1: PROFILE CONTENT (2-Column Split: Left Avatar+Nickname, Right Farms+Forum) ───

@Composable
private fun ProfileTabContent(
    uiState: ProfileUiState,
    viewModel: ProfileViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── LEFT SIDE: Avatar & Nickname Only ─────────────────────────────────
        Column(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar & Nickname Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E261A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ProfileAvatar(
                        avatarAssetPath = uiState.userProfile.avatarAssetPath,
                        size = 80.dp,
                        onClick = { viewModel.openViewAvatar() }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.userProfile.nickname.ifBlank { "Farmer" },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Text(
                        text = "Tap avatar to view or change",
                        fontSize = 11.sp,
                        color = White.copy(alpha = 0.6f)
                    )
                }
            }

            // Edit Nickname Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E261A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Nickname", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = White)
                        }

                        if (!uiState.isEditingNickname) {
                            TextButton(onClick = { viewModel.startEditNickname() }) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Edit", color = ForestGreen, fontSize = 12.sp)
                            }
                        }
                    }

                    if (uiState.isEditingNickname) {
                        OutlinedTextField(
                            value = uiState.nicknameInput,
                            onValueChange = { viewModel.updateNicknameInput(it) },
                            label = { Text("Enter Nickname", color = White.copy(alpha = 0.7f), fontSize = 12.sp) },
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
                            Text(text = err, color = Color.Red, fontSize = 11.sp)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { viewModel.cancelEditNickname() }) {
                                Text("Cancel", color = White.copy(alpha = 0.7f), fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Button(
                                onClick = { viewModel.submitNicknameCheck() },
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                enabled = !uiState.isCheckingNickname
                            ) {
                                if (uiState.isCheckingNickname) {
                                    CircularProgressIndicator(modifier = Modifier.size(14.dp), color = White, strokeWidth = 2.dp)
                                } else {
                                    Text("Save", fontSize = 12.sp)
                                }
                            }
                        }
                    } else {
                        Text(
                            text = uiState.userProfile.nickname.ifBlank { "Farmer" },
                            fontSize = 14.sp,
                            color = White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        // ── RIGHT SIDE: Available Farms List & Community Forum Activity ────────
        LazyColumn(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Farms List Card (from Supabase cloud or local Room)
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1E261A),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Agriculture, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("My Farms List", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = White)
                        }

                        if (uiState.farms.isEmpty()) {
                            Text(
                                text = "No farms registered yet.",
                                fontSize = 12.sp,
                                color = White.copy(alpha = 0.6f)
                            )
                        } else {
                            uiState.farms.forEach { farm ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF141A12),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = farm.farmName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = White
                                            )

                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. Community Forum Activity Card
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1E261A),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Forum, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Community Forum Activity", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = White)
                        }

                        if (uiState.userPosts.isEmpty()) {
                            Text(
                                text = "No recent community activity.",
                                fontSize = 12.sp,
                                color = White.copy(alpha = 0.6f)
                            )
                        } else {
                            uiState.userPosts.take(3).forEach { post ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF141A12),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = post.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = White
                                            )
                                            Text(
                                                text = post.category,
                                                fontSize = 10.sp,
                                                color = ForestGreen,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        Text(
                                            text = post.content,
                                            fontSize = 11.sp,
                                            color = White.copy(alpha = 0.7f),
                                            maxLines = 2
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Text("❤️ ${post.likesCount}", fontSize = 10.sp, color = White.copy(alpha = 0.5f))
                                            Text("💬 ${post.commentsCount} comments", fontSize = 10.sp, color = White.copy(alpha = 0.5f))
                                        }
                                    }
                                }
                            }
                        }
                    }
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
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredNotifications = remember(uiState.notifications, selectedFilter) {
        when (selectedFilter) {
            "UNREAD" -> uiState.notifications.filter { !it.isRead }
            "SYSTEM" -> uiState.notifications.filter { it.type.uppercase().contains("SYSTEM") }
            "CROP" -> uiState.notifications.filter { it.type.uppercase().contains("CROP") }
            "BUG" -> uiState.notifications.filter { it.type.uppercase().contains("BUG") }
            else -> uiState.notifications
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Filter Chips for Admin & System Bulletins
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(
                "ALL" to "All Updates",
                "UNREAD" to "Unread",
                "SYSTEM" to "System Announcements",
                "CROP" to "Crops Added",
                "BUG" to "Bug Fixes"
            ).forEach { (filterKey, label) ->
                val isSelected = selectedFilter == filterKey
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = filterKey },
                    label = { Text(label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ForestGreen,
                        selectedLabelColor = White,
                        containerColor = Color(0xFF1E261A),
                        labelColor = White.copy(alpha = 0.7f)
                    )
                )
            }
        }

        if (filteredNotifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = White.copy(alpha = 0.3f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No notifications found", color = White.copy(alpha = 0.5f), fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredNotifications, key = { it.id }) { notif ->
                    NotificationCardItem(
                        notif = notif,
                        onClick = { viewModel.selectNotification(notif) },
                        onDelete = { viewModel.deleteNotification(notif.id) }
                    )
                }
            }
        }
    }

    // Detail Dialog Modal
    uiState.selectedNotification?.let { notif ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissNotificationDetail() },
            title = {
                Text(notif.title, fontWeight = FontWeight.Bold, color = White)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(notif.message, color = White.copy(alpha = 0.85f), fontSize = 14.sp)
                    Text("Time: ${notif.timestamp}", color = White.copy(alpha = 0.5f), fontSize = 11.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissNotificationDetail() },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Text("OK", color = White)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.deleteNotification(notif.id) }) {
                    Text("Delete", color = Color(0xFFEF5350))
                }
            },
            containerColor = Color(0xFF1E261A)
        )
    }
}

@Composable
private fun NotificationCardItem(
    notif: NotificationItem,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (notif.isRead) Color(0xFF1E261A) else Color(0xFF263321),
        border = if (!notif.isRead) androidx.compose.foundation.BorderStroke(1.dp, ForestGreen) else null,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                notif.type.uppercase().contains("CROP") -> Color(0xFF4CAF50)
                                notif.type.uppercase().contains("BUG") || notif.type.uppercase().contains("FIX") -> Color(0xFFFFA000)
                                notif.type.uppercase().contains("SYSTEM") || notif.type.uppercase().contains("ADMIN") -> Color(0xFF1E88E5)
                                else -> ForestGreen
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            notif.type.uppercase().contains("CROP") -> Icons.Default.Eco
                            notif.type.uppercase().contains("BUG") || notif.type.uppercase().contains("FIX") -> Icons.Default.Build
                            notif.type.uppercase().contains("SYSTEM") || notif.type.uppercase().contains("ADMIN") -> Icons.Default.Campaign
                            else -> Icons.Default.Notifications
                        },
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(notif.title, fontWeight = FontWeight.Bold, color = White, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(notif.message, color = White.copy(alpha = 0.7f), fontSize = 12.sp, maxLines = 2)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(notif.timestamp, color = White.copy(alpha = 0.4f), fontSize = 10.sp)
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = White.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
            }
        }
    }
}

// ─── TAB 3: SETTINGS CONTENT ───────────────────────────────────────────────

@Composable
private fun SettingsTabContent(
    uiState: ProfileUiState,
    viewModel: ProfileViewModel,
    navController: NavHostController
) {
    var showAudioSettingsModal by remember { mutableStateOf(false) }
    val soundManager = LocalSoundManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Audio & Sound Settings Section
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF1E261A),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    soundManager.playSfx(SoundEffect.TAP_BUTTON)
                    showAudioSettingsModal = true
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(
                        imageVector = if (soundManager.isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = ForestGreen
                    )
                    Column {
                        Text("Audio & Sound Settings", fontWeight = FontWeight.Bold, color = White, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (soundManager.isMuted) "Master Audio Muted" else "Music & SFX Enabled",
                            color = if (soundManager.isMuted) Color(0xFFEF9A9A) else ForestGreen,
                            fontSize = 13.sp
                        )
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = White)
            }
        }

        if (showAudioSettingsModal) {
            AudioSettingsDialog(
                onDismissRequest = { showAudioSettingsModal = false }
            )
        }

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
