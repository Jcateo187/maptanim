package com.maptanim.app.ui.screens.profile

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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
                            1 -> NotificationTabContent(uiState = uiState, viewModel = viewModel)
                            2 -> SettingsTabContent(uiState = uiState, viewModel = viewModel, navController = navController)
                        }
                    }
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
    var isFarmsExpanded by remember { mutableStateOf(false) }
    var isHarvestExpanded by remember { mutableStateOf(false) }
    var isForumExpanded by remember { mutableStateOf(false) }

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
                        val remainingDays = com.maptanim.app.domain.model.getDaysRemainingForNicknameChange(uiState.userProfile.nicknameUpdatedAt)
                        if (remainingDays > 0) {
                            Text(
                                text = "🔒 Next change available in $remainingDays day(s)",
                                fontSize = 10.sp,
                                color = Color(0xFFFFB74D)
                            )
                        } else {
                            Text(
                                text = "Can be changed once every 15 days",
                                fontSize = 10.sp,
                                color = White.copy(alpha = 0.5f)
                            )
                        }
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
            // 1. Farms List Card (with See More toggle)
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Agriculture, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("My Farms List", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = White)
                            }
                            if (uiState.farms.size > 2) {
                                TextButton(onClick = { isFarmsExpanded = true }) {
                                    Text(
                                        text = "See More (${uiState.farms.size}) ▼",
                                        color = ForestGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        if (uiState.farms.isEmpty()) {
                            Text(
                                text = "No farms registered yet.",
                                fontSize = 12.sp,
                                color = White.copy(alpha = 0.6f)
                            )
                        } else {
                            val visibleFarms = uiState.farms.take(2)
                            visibleFarms.forEach { farm ->
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
                                            Text(
                                                text = "📍 ${farm.location.ifBlank { "Murcia, Negros Occidental" }}",
                                                fontSize = 11.sp,
                                                color = White.copy(alpha = 0.6f)
                                            )
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = ForestGreen.copy(alpha = 0.25f),
                                            border = BorderStroke(1.dp, ForestGreen)
                                        ) {
                                            Text(
                                                text = "ACTIVE",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. Farm Harvest History Card (with Activity Timestamp & See More modal)
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Agriculture, contentDescription = null, tint = Color(0xFFD48806), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Farm Harvest History", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = White)
                            }
                            if (uiState.harvestHistory.size > 3) {
                                TextButton(onClick = { isHarvestExpanded = true }) {
                                    Text(
                                        text = "See More (${uiState.harvestHistory.size}) ▼",
                                        color = Color(0xFFD48806),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Preserved crop yield records for rotation planning & management decisions.",
                            fontSize = 11.sp,
                            color = White.copy(alpha = 0.6f)
                        )

                        if (uiState.harvestHistory.isEmpty()) {
                            Text(
                                text = "No harvest records stored yet. Harvest ready crops from active monitoring to record history.",
                                fontSize = 12.sp,
                                color = White.copy(alpha = 0.5f),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        } else {
                            val visibleHarvests = uiState.harvestHistory.take(3)
                            visibleHarvests.forEach { record ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF141A12),
                                    border = BorderStroke(1.dp, Color(0xFF2A3828)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(getCropEmoji(record.cropName), fontSize = 16.sp)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = record.cropName.uppercase(),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = White
                                                )
                                                if (!record.cropVariety.isNullOrBlank()) {
                                                    Text(
                                                        text = " (${record.cropVariety})",
                                                        fontSize = 12.sp,
                                                        color = ForestGreen
                                                    )
                                                }
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFFD48806).copy(alpha = 0.25f),
                                                border = BorderStroke(1.dp, Color(0xFFD48806))
                                            ) {
                                                Text(
                                                    text = record.farmName,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = White,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "📍 Plot/Zone: ${record.plotLabel}",
                                                fontSize = 11.sp,
                                                color = White.copy(alpha = 0.8f)
                                            )
                                            Text(
                                                text = "⚖️ Yield: ${if (record.yieldKg > 0f) "${record.yieldKg} kg" else "N/A"}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFD48806)
                                            )
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "🌱 Planted: ${record.plantedDate?.take(10) ?: "N/A"}",
                                                fontSize = 10.sp,
                                                color = White.copy(alpha = 0.6f)
                                            )
                                            Text(
                                                text = "🌾 Harvested: ${record.harvestedAt.take(10)}",
                                                fontSize = 10.sp,
                                                color = White.copy(alpha = 0.6f)
                                            )
                                            Text(
                                                text = "⏱️ ${record.growingDurationDays} ${if (record.cropName.lowercase().contains("ampalaya") || record.cropVariety?.contains("10s", ignoreCase = true) == true) "Secs" else "Days"}",
                                                fontSize = 10.sp,
                                                color = ForestGreen
                                            )
                                        }

                                        // Exact Time Activity Timestamp
                                        Text(
                                            text = "🕒 Activity Time: ${formatActivityTime(record.harvestedAt)}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFFD48806)
                                        )

                                        if (!record.notes.isNullOrBlank()) {
                                            Text(
                                                text = "📝 Notes: ${record.notes}",
                                                fontSize = 11.sp,
                                                color = White.copy(alpha = 0.75f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Community Forum Activity Card (with Activity Timestamp & See More modal)
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Forum, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Community Forum Activity", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = White)
                            }
                            if (uiState.userPosts.size > 3) {
                                TextButton(onClick = { isForumExpanded = true }) {
                                    Text(
                                        text = "See More (${uiState.userPosts.size}) ▼",
                                        color = ForestGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        if (uiState.userPosts.isEmpty()) {
                            Text(
                                text = "No recent community activity.",
                                fontSize = 12.sp,
                                color = White.copy(alpha = 0.6f)
                            )
                        } else {
                            val visiblePosts = uiState.userPosts.take(3)
                            visiblePosts.forEach { post ->
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
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Text("❤️ ${post.likesCount}", fontSize = 10.sp, color = White.copy(alpha = 0.5f))
                                                Text("💬 ${post.commentsCount} comments", fontSize = 10.sp, color = White.copy(alpha = 0.5f))
                                            }

                                            // Exact Time Activity Timestamp
                                            Text(
                                                text = "🕒 ${formatActivityTime(post.timestamp)}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = ForestGreen
                                            )
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

    // Render Full List Modals when See More is clicked
    if (isFarmsExpanded) {
        FullFarmsListModal(
            farms = uiState.farms,
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
            onDismiss = { isForumExpanded = false }
        )
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
            "SUPPORT" -> uiState.notifications.filter { it.type.uppercase().contains("SUPPORT") || it.type.uppercase().contains("REPLY") }
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
                "SUPPORT" to "Support Advisories",
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
                                notif.type.uppercase().contains("SUPPORT") || notif.type.uppercase().contains("REPLY") -> Color(0xFF8E24AA)
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
                            notif.type.uppercase().contains("SUPPORT") || notif.type.uppercase().contains("REPLY") -> Icons.Default.SupportAgent
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
                        imageVector = if (soundManager.isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = null,
                        tint = ForestGreen
                    )
                    Column {
                        Text("Audio Adjustment", fontWeight = FontWeight.Bold, color = White, fontSize = 15.sp)
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

        // Replay Tutorial Section
        val tutorialViewModel: com.maptanim.app.viewmodel.TutorialViewModel = viewModel()
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF1E261A),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    soundManager.playSfx(SoundEffect.TAP_BUTTON)
                    tutorialViewModel.restartTutorial()
                    navController.navigate(com.maptanim.app.navigation.Routes.HOME) {
                        popUpTo(com.maptanim.app.navigation.Routes.HOME) { inclusive = true }
                    }
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
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = ForestGreen
                    )
                    Column {
                        Text("Replay Farm Guide Tutorial", fontWeight = FontWeight.Bold, color = White, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Restart Tatay Juan step-by-step interactive guide",
                            color = White.copy(alpha = 0.6f),
                            fontSize = 13.sp
                        )
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = White)
            }
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
                    Text("No, Stay in Audio Adjustment", color = White)
                }
            },
            containerColor = Color(0xFF1E261A)
        )
    }
}

// ── Crop Emoji Helper ────────────────────────────────────────────────────────
private fun getCropEmoji(name: String): String = when (name.lowercase().replace(" ", "")) {
    "carrot", "karot" -> "🥕"
    "stringbeans", "sitaw", "beans" -> "🫘"
    "eggplant", "talong" -> "🍆"
    "tomato", "kamatis" -> "🍅"
    "onion", "sibuyas" -> "🧅"
    "pumpkin", "squash", "kalabasa" -> "🎃"
    "corn", "mais" -> "🌽"
    "cabbage", "repolyo" -> "🥬"
    "pechay" -> "🥬"
    "ampalaya" -> "🥒"
    "okra" -> "🌿"
    "sili", "chilipepper", "chili" -> "🌶️"
    "cucumber", "pipino" -> "🥒"
    else -> "🌱"
}

// ── Time Activity Formatter ──────────────────────────────────────────────────
private fun formatActivityTime(rawTimestamp: String?): String {
    if (rawTimestamp.isNullOrBlank()) return "Recently"
    return try {
        val zdt = java.time.ZonedDateTime.parse(rawTimestamp)
        val localZdt = zdt.withZoneSameInstant(java.time.ZoneId.systemDefault())
        val now = java.time.ZonedDateTime.now()
        val diffHours = java.time.Duration.between(localZdt, now).toHours()
        val diffMins = java.time.Duration.between(localZdt, now).toMinutes()
        when {
            diffMins < 1 -> "Just now"
            diffMins < 60 -> "$diffMins mins ago"
            diffHours < 24 && localZdt.dayOfMonth == now.dayOfMonth -> "Today at ${localZdt.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"))}"
            diffHours < 48 -> "Yesterday at ${localZdt.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"))}"
            else -> localZdt.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy • hh:mm a"))
        }
    } catch (e: Exception) {
        try {
            val date = java.time.LocalDate.parse(rawTimestamp.take(10))
            date.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        } catch (e2: Exception) {
            rawTimestamp.take(16).replace("T", " ")
        }
    }
}

// ── Modals with Search Filter & Pagination ───────────────────────────────────

@Composable
private fun FullFarmsListModal(
    farms: List<com.maptanim.app.domain.model.Farm>,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var currentPage by remember { mutableStateOf(1) }
    val itemsPerPage = 5

    val filteredFarms = remember(farms, searchQuery) {
        if (searchQuery.isBlank()) farms
        else farms.filter {
            it.farmName.contains(searchQuery, ignoreCase = true) ||
            it.location.contains(searchQuery, ignoreCase = true)
        }
    }

    val totalPages = (filteredFarms.size + itemsPerPage - 1) / itemsPerPage
    val pageItems = remember(filteredFarms, currentPage) {
        val safePage = currentPage.coerceIn(1, (totalPages).coerceAtLeast(1))
        val startIndex = (safePage - 1) * itemsPerPage
        filteredFarms.drop(startIndex).take(itemsPerPage)
    }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.5.dp, ForestGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
            color = Color(0xFA121811)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Agriculture, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🚜 My Registered Farms (${filteredFarms.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = White
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close Modal", tint = White)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF2A3424),
                    border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = White.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        androidx.compose.foundation.text.BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it; currentPage = 1 },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = White, fontSize = 12.sp),
                            cursorBrush = androidx.compose.ui.graphics.SolidColor(ForestGreen),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text("Search farms by name or location...", color = White.copy(alpha = 0.45f), fontSize = 12.sp)
                                }
                                innerTextField()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (pageItems.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("No matching farms found.", color = White.copy(alpha = 0.6f), fontSize = 13.sp)
                            }
                        }
                    } else {
                        items(pageItems) { farm ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF1B2317),
                                border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(farm.farmName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = White)
                                        Text("📍 Location: ${farm.location.ifBlank { "Murcia, Negros Occidental" }}", fontSize = 11.sp, color = White.copy(alpha = 0.7f))
                                        Text("📐 Total Area: ${(farm.totalAreaSqm ?: 0f).toInt()} sqm", fontSize = 10.sp, color = ForestGreen)
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = ForestGreen.copy(alpha = 0.25f),
                                        border = BorderStroke(1.dp, ForestGreen)
                                    ) {
                                        Text("ACTIVE FARM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                if (totalPages > 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { if (currentPage > 1) currentPage-- },
                            enabled = currentPage > 1,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("◄ Previous", fontSize = 11.sp, color = White)
                        }
                        Text("Page $currentPage of $totalPages", fontSize = 12.sp, color = White, fontWeight = FontWeight.Bold)
                        OutlinedButton(
                            onClick = { if (currentPage < totalPages) currentPage++ },
                            enabled = currentPage < totalPages,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Next ►", fontSize = 11.sp, color = White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FullHarvestHistoryModal(
    harvestHistory: List<com.maptanim.app.domain.model.HarvestRecord>,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedDateFilter by remember { mutableStateOf<String?>(null) }
    var showDatePickerModal by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(1) }
    val itemsPerPage = 5

    val filteredRecords = remember(harvestHistory, searchQuery, selectedDateFilter) {
        harvestHistory.filter { record ->
            val matchesSearch = searchQuery.isBlank() || (
                record.cropName.contains(searchQuery, ignoreCase = true) ||
                (record.cropVariety?.contains(searchQuery, ignoreCase = true) == true) ||
                record.farmName.contains(searchQuery, ignoreCase = true) ||
                record.plotLabel.contains(searchQuery, ignoreCase = true) ||
                (record.notes?.contains(searchQuery, ignoreCase = true) == true)
            )
            val matchesDate = selectedDateFilter.isNullOrBlank() || (
                record.harvestedAt.take(10) == selectedDateFilter ||
                (record.plantedDate?.take(10) == selectedDateFilter)
            )
            matchesSearch && matchesDate
        }
    }

    val totalPages = (filteredRecords.size + itemsPerPage - 1) / itemsPerPage
    val pageItems = remember(filteredRecords, currentPage) {
        val safePage = currentPage.coerceIn(1, (totalPages).coerceAtLeast(1))
        val startIndex = (safePage - 1) * itemsPerPage
        filteredRecords.drop(startIndex).take(itemsPerPage)
    }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.5.dp, Color(0xFFD48806).copy(alpha = 0.6f), RoundedCornerShape(20.dp)),
            color = Color(0xFA121811)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Agriculture, contentDescription = null, tint = Color(0xFFD48806), modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedDateFilter != null) "🌾 Harvests on $selectedDateFilter (${filteredRecords.size})" else "🌾 Complete Harvest History (${filteredRecords.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = White
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close Modal", tint = White)
                    }
                }

                // Search Bar with 📅 Date Selection Icon Button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF2A3424),
                    border = BorderStroke(1.dp, Color(0xFFD48806).copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = White.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        androidx.compose.foundation.text.BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it; currentPage = 1 },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = White, fontSize = 12.sp),
                            cursorBrush = androidx.compose.ui.graphics.SolidColor(Color(0xFFD48806)),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text("Search harvest history by crop, variety, plot...", color = White.copy(alpha = 0.45f), fontSize = 12.sp)
                                }
                                innerTextField()
                            },
                            modifier = Modifier.weight(1f)
                        )
                        // 📅 Date Selection Icon Button
                        IconButton(
                            onClick = { showDatePickerModal = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = "Select Activity Date",
                                tint = if (selectedDateFilter != null) Color(0xFFD48806) else White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Date Filter Badge if Active
                if (selectedDateFilter != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFD48806).copy(alpha = 0.25f),
                            border = BorderStroke(1.dp, Color(0xFFD48806))
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable { selectedDateFilter = null }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📅 Activity Date: $selectedDateFilter ✖",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = White
                                )
                            }
                        }
                        Text(
                            text = "Showing all harvest activity on $selectedDateFilter",
                            fontSize = 10.sp,
                            color = White.copy(alpha = 0.6f)
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (pageItems.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (selectedDateFilter != null) "No harvest activities found on $selectedDateFilter." else "No harvest records match your search filter.",
                                    color = White.copy(alpha = 0.6f),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        items(pageItems) { record ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF1B2317),
                                border = BorderStroke(1.dp, Color(0xFF2A3828)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(getCropEmoji(record.cropName), fontSize = 18.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = record.cropName.uppercase(),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = White
                                            )
                                            if (!record.cropVariety.isNullOrBlank()) {
                                                Text(
                                                    text = " (${record.cropVariety})",
                                                    fontSize = 12.sp,
                                                    color = ForestGreen
                                                )
                                            }
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFD48806).copy(alpha = 0.25f),
                                            border = BorderStroke(1.dp, Color(0xFFD48806))
                                        ) {
                                            Text(
                                                text = record.farmName,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("📍 Plot/Zone: ${record.plotLabel}", fontSize = 11.sp, color = White.copy(alpha = 0.8f))
                                        Text("⚖️ Yield: ${if (record.yieldKg > 0f) "${record.yieldKg} kg" else "N/A"}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD48806))
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("🌱 Planted: ${record.plantedDate?.take(10) ?: "N/A"}", fontSize = 10.sp, color = White.copy(alpha = 0.6f))
                                        Text("🌾 Harvested: ${record.harvestedAt.take(10)}", fontSize = 10.sp, color = White.copy(alpha = 0.6f))
                                        Text("⏱️ ${record.growingDurationDays} ${if (record.cropName.lowercase().contains("ampalaya") || record.cropVariety?.contains("10s", ignoreCase = true) == true) "Secs" else "Days"}", fontSize = 10.sp, color = ForestGreen)
                                    }

                                    Text(
                                        text = "🕒 Activity Time: ${formatActivityTime(record.harvestedAt)}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFD48806)
                                    )

                                    if (!record.notes.isNullOrBlank()) {
                                        Text("📝 Notes: ${record.notes}", fontSize = 11.sp, color = White.copy(alpha = 0.8f))
                                    }
                                }
                            }
                        }
                    }
                }

                if (totalPages > 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { if (currentPage > 1) currentPage-- },
                            enabled = currentPage > 1,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("◄ Previous", fontSize = 11.sp, color = White)
                        }
                        Text("Page $currentPage of $totalPages", fontSize = 12.sp, color = White, fontWeight = FontWeight.Bold)
                        OutlinedButton(
                            onClick = { if (currentPage < totalPages) currentPage++ },
                            enabled = currentPage < totalPages,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Next ►", fontSize = 11.sp, color = White)
                        }
                    }
                }
            }
        }
    }

    if (showDatePickerModal) {
        DatePickerSelectionDialog(
            selectedDate = selectedDateFilter,
            onDateSelected = { selectedDateFilter = it; currentPage = 1 },
            onDismiss = { showDatePickerModal = false }
        )
    }
}

@Composable
private fun FullCommunityActivityModal(
    posts: List<com.maptanim.app.domain.model.CommunityPost>,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedDateFilter by remember { mutableStateOf<String?>(null) }
    var showDatePickerModal by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(1) }
    val itemsPerPage = 5

    val filteredPosts = remember(posts, searchQuery, selectedDateFilter) {
        posts.filter { post ->
            val matchesSearch = searchQuery.isBlank() || (
                post.title.contains(searchQuery, ignoreCase = true) ||
                post.content.contains(searchQuery, ignoreCase = true) ||
                post.category.contains(searchQuery, ignoreCase = true)
            )
            val matchesDate = selectedDateFilter.isNullOrBlank() || (
                post.timestamp.contains(selectedDateFilter!!)
            )
            matchesSearch && matchesDate
        }
    }

    val totalPages = (filteredPosts.size + itemsPerPage - 1) / itemsPerPage
    val pageItems = remember(filteredPosts, currentPage) {
        val safePage = currentPage.coerceIn(1, (totalPages).coerceAtLeast(1))
        val startIndex = (safePage - 1) * itemsPerPage
        filteredPosts.drop(startIndex).take(itemsPerPage)
    }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.5.dp, ForestGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
            color = Color(0xFA121811)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Forum, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedDateFilter != null) "💬 Forum Activity on $selectedDateFilter (${filteredPosts.size})" else "💬 Community Forum Activity (${filteredPosts.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = White
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close Modal", tint = White)
                    }
                }

                // Search Bar with 📅 Date Selection Icon Button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF2A3424),
                    border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = White.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        androidx.compose.foundation.text.BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it; currentPage = 1 },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = White, fontSize = 12.sp),
                            cursorBrush = androidx.compose.ui.graphics.SolidColor(ForestGreen),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text("Search forum activity by title, content, category...", color = White.copy(alpha = 0.45f), fontSize = 12.sp)
                                }
                                innerTextField()
                            },
                            modifier = Modifier.weight(1f)
                        )
                        // 📅 Date Selection Icon Button
                        IconButton(
                            onClick = { showDatePickerModal = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = "Select Activity Date",
                                tint = if (selectedDateFilter != null) ForestGreen else White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Date Filter Badge if Active
                if (selectedDateFilter != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ForestGreen.copy(alpha = 0.25f),
                            border = BorderStroke(1.dp, ForestGreen)
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable { selectedDateFilter = null }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📅 Activity Date: $selectedDateFilter ✖",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = White
                                )
                            }
                        }
                        Text(
                            text = "Showing all forum activity on $selectedDateFilter",
                            fontSize = 10.sp,
                            color = White.copy(alpha = 0.6f)
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (pageItems.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (selectedDateFilter != null) "No forum activity found on $selectedDateFilter." else "No community posts match your search filter.",
                                    color = White.copy(alpha = 0.6f),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        items(pageItems) { post ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF1B2317),
                                border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(post.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = White)
                                        Text(post.category, fontSize = 10.sp, color = ForestGreen, fontWeight = FontWeight.SemiBold)
                                    }
                                    Text(post.content, fontSize = 11.sp, color = White.copy(alpha = 0.8f))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                            Text("❤️ ${post.likesCount}", fontSize = 10.sp, color = White.copy(alpha = 0.6f))
                                            Text("💬 ${post.commentsCount} comments", fontSize = 10.sp, color = White.copy(alpha = 0.6f))
                                        }
                                        Text("🕒 ${formatActivityTime(post.timestamp)}", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = ForestGreen)
                                    }
                                }
                            }
                        }
                    }
                }

                if (totalPages > 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { if (currentPage > 1) currentPage-- },
                            enabled = currentPage > 1,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("◄ Previous", fontSize = 11.sp, color = White)
                        }
                        Text("Page $currentPage of $totalPages", fontSize = 12.sp, color = White, fontWeight = FontWeight.Bold)
                        OutlinedButton(
                            onClick = { if (currentPage < totalPages) currentPage++ },
                            enabled = currentPage < totalPages,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Next ►", fontSize = 11.sp, color = White)
                        }
                    }
                }
            }
        }
    }

    if (showDatePickerModal) {
        DatePickerSelectionDialog(
            selectedDate = selectedDateFilter,
            onDateSelected = { selectedDateFilter = it; currentPage = 1 },
            onDismiss = { showDatePickerModal = false }
        )
    }
}

// ── Interactive Date Selection Dialog ────────────────────────────────────────

@Composable
private fun DatePickerSelectionDialog(
    selectedDate: String?,
    onDateSelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    var dateInput by remember { mutableStateOf(selectedDate ?: java.time.LocalDate.now().toString()) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E261A),
            border = BorderStroke(1.dp, ForestGreen),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Select Activity Date", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = White)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = White)
                    }
                }

                Text(
                    text = "Filter and view all recorded farm harvest and community activities on a specific day.",
                    fontSize = 11.sp,
                    color = White.copy(alpha = 0.7f)
                )

                // Quick Preset Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val todayStr = java.time.LocalDate.now().toString()
                    val yesterdayStr = java.time.LocalDate.now().minusDays(1).toString()

                    FilterChip(
                        selected = dateInput == todayStr,
                        onClick = { dateInput = todayStr },
                        label = { Text("Today", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ForestGreen,
                            selectedLabelColor = White
                        )
                    )
                    FilterChip(
                        selected = dateInput == yesterdayStr,
                        onClick = { dateInput = yesterdayStr },
                        label = { Text("Yesterday", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ForestGreen,
                            selectedLabelColor = White
                        )
                    )
                    FilterChip(
                        selected = selectedDate == null,
                        onClick = { onDateSelected(null); onDismiss() },
                        label = { Text("Show All", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFD48806),
                            selectedLabelColor = White
                        )
                    )
                }

                // Custom Date Entry Field
                OutlinedTextField(
                    value = dateInput,
                    onValueChange = { dateInput = it },
                    label = { Text("Enter Date (YYYY-MM-DD)", color = White.copy(alpha = 0.7f), fontSize = 11.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = White.copy(alpha = 0.3f),
                        focusedTextColor = White,
                        unfocusedTextColor = White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { onDateSelected(null); onDismiss() }) {
                        Text("Reset / All", color = White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = { onDateSelected(dateInput.trim()); onDismiss() },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                    ) {
                        Text("Apply Date Filter", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
