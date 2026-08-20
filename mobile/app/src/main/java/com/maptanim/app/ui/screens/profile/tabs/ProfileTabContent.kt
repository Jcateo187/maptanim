package com.maptanim.app.ui.screens.profile.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.getDaysRemainingForNicknameChange
import com.maptanim.app.ui.components.avatar.ProfileAvatar
import com.maptanim.app.ui.screens.profile.ProfileUiState
import com.maptanim.app.ui.screens.profile.ProfileViewModel
import com.maptanim.app.ui.screens.profile.modals.FullCommunityActivityModal
import com.maptanim.app.ui.screens.profile.modals.FullFarmsListModal
import com.maptanim.app.ui.screens.profile.modals.FullHarvestHistoryModal
import com.maptanim.app.ui.screens.profile.utils.formatActivityTime
import com.maptanim.app.ui.screens.profile.utils.getCropEmoji
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.White

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
        // LEFT SIDE: Avatar & Nickname Only
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
                        val remainingDays = getDaysRemainingForNicknameChange(uiState.userProfile.nicknameUpdatedAt)
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

        // RIGHT SIDE: Available Farms List & Community Forum Activity
        LazyColumn(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Farms List Card
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

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                TextButton(
                                    onClick = { viewModel.openCreateFarm() },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "New Farm",
                                        tint = ForestGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "New Farm",
                                        color = ForestGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }

                                if (uiState.farms.size > 2) {
                                    TextButton(
                                        onClick = { isFarmsExpanded = true },
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "See More (${uiState.farms.size}) ▼",
                                            color = ForestGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }

                        if (uiState.farms.isEmpty()) {
                            Column(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "No farms registered yet.",
                                    fontSize = 12.sp,
                                    color = White.copy(alpha = 0.6f)
                                )
                                OutlinedButton(
                                    onClick = { viewModel.openCreateFarm() },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, ForestGreen)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Create First Farm", color = ForestGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            val visibleFarms = uiState.farms.take(2)
                            visibleFarms.forEach { farm ->
                                val isActive = farm.id == uiState.activeFarmId
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF141A12),
                                    border = BorderStroke(1.dp, if (isActive) ForestGreen.copy(alpha = 0.8f) else ForestGreen.copy(alpha = 0.2f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = farm.farmName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = White
                                            )
                                            Text(
                                                text = if (farm.createdAt.isNotBlank()) "Created: ${farm.createdAt}" else "Farm Workspace",
                                                fontSize = 10.sp,
                                                color = White.copy(alpha = 0.45f)
                                            )
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            if (isActive) {
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
                                            } else {
                                                OutlinedButton(
                                                    onClick = { viewModel.selectActiveFarm(farm.id) },
                                                    shape = RoundedCornerShape(6.dp),
                                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                    border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.5f)),
                                                    modifier = Modifier.height(26.dp)
                                                ) {
                                                    Text(
                                                        text = "Select",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = ForestGreen
                                                    )
                                                }
                                            }

                                            // Rename Button
                                            IconButton(
                                                onClick = { viewModel.openRenameFarm(farm) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Edit,
                                                    contentDescription = "Rename Farm",
                                                    tint = White.copy(alpha = 0.85f),
                                                    modifier = Modifier.size(15.dp)
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

            // 2. Farm Harvest History Card
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

            // 3. Community Forum Activity Card
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
            onDismiss = { isForumExpanded = false }
        )
    }
}
