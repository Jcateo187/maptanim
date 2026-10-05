package com.maptanim.app.features.profile.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.NotificationItem
import com.maptanim.app.features.profile.ProfileViewModel
import com.maptanim.app.features.profile.model.ProfileUiState
import kotlinx.coroutines.launch

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * NotificationsTabContent — System bulletins, crop updates, and support messages in Daylight theme.
 */
@Composable
fun NotificationsTabContent(
    uiState: ProfileUiState,
    viewModel: ProfileViewModel
) {
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredNotifications = remember(uiState.notifications, selectedFilter) {
        val base = when (selectedFilter) {
            "UNREAD" -> uiState.notifications.filter { !it.isRead }
            "GUIDE" -> uiState.notifications.filter { it.type.uppercase().contains("AGRONOMIC") || it.type.uppercase().contains("GUIDE") }
            "CROP" -> uiState.notifications.filter { it.type.uppercase().contains("CROP") }
            "SYSTEM" -> uiState.notifications.filter { it.type.uppercase().contains("SYSTEM") || it.type.uppercase().contains("ADMIN") }
            "SUPPORT" -> uiState.notifications.filter { it.type.uppercase().contains("SUPPORT") || it.type.uppercase().contains("REPLY") }
            "BUG" -> uiState.notifications.filter { it.type.uppercase().contains("BUG") }
            else -> uiState.notifications
        }
        base.sortedWith(
            compareByDescending<NotificationItem> { it.rawTimestamp ?: "" }
                .thenByDescending { it.id }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Filter Chips with horizontal scrolling
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            listOf(
                "ALL" to "All Updates",
                "UNREAD" to "Unread",
                "GUIDE" to "Agronomic Guides",
                "CROP" to "Crops Updated",
                "SYSTEM" to "System Announcements",
                "SUPPORT" to "Support Advisories",
                "BUG" to "Bug Fixes"
            ).forEach { (filterKey, label) ->
                val isSelected = selectedFilter == filterKey
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = filterKey },
                    label = { Text(label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LushGreen,
                        selectedLabelColor = Color.White,
                        containerColor = LightSurface,
                        labelColor = DeepBlack
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) LushGreen else CardBorderColor
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
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = LushGreen,
                        modifier = Modifier.size(44.dp)
                    )
                    Text("No notifications found", color = DeepBlack, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Updates and alerts will appear here.", color = MutedText, fontSize = 12.sp)
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

    var isDownloadingUpdate by remember { mutableStateOf(false) }
    var updateDownloadedMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Detail Dialog Modal
    uiState.selectedNotification?.let { notif ->
        AlertDialog(
            onDismissRequest = {
                updateDownloadedMessage = null
                viewModel.dismissNotificationDetail()
            },
            title = {
                Text(notif.title, fontWeight = FontWeight.Bold, color = DeepBlack, fontSize = 16.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(notif.message, color = DeepBlack, fontSize = 13.sp)
                    Text("Time: ${notif.timestamp}", color = MutedText, fontSize = 11.sp)

                    val isSystemUpdate = notif.type.uppercase().contains("SYSTEM") ||
                            notif.type.uppercase().contains("CROP") ||
                            notif.title.contains("Update", ignoreCase = true) ||
                            notif.title.contains("Pananim", ignoreCase = true)

                    if (isSystemUpdate) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFE8F5E9),
                            border = BorderStroke(1.dp, LushGreen),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "System Update Available",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = LushGreen
                                )
                                Text(
                                    text = "Download latest crop data and rules from Central Database without a full app update.",
                                    fontSize = 11.sp,
                                    color = DeepBlack
                                )

                                Button(
                                    onClick = {
                                        scope.launch {
                                            isDownloadingUpdate = true
                                            try {
                                                com.maptanim.app.data.repository.RepositoryProvider.cropRepository.refreshCrops()
                                                updateDownloadedMessage = "Data update downloaded successfully!"
                                            } catch (e: Exception) {
                                                updateDownloadedMessage = "Error downloading data: ${e.message}"
                                            } finally {
                                                isDownloadingUpdate = false
                                            }
                                        }
                                    },
                                    enabled = !isDownloadingUpdate,
                                    colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                ) {
                                    if (isDownloadingUpdate) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Downloading...", fontSize = 12.sp, color = Color.White)
                                    } else {
                                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Sync / Download Data", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }

                                if (updateDownloadedMessage != null) {
                                    Text(
                                        text = updateDownloadedMessage!!,
                                        fontSize = 11.sp,
                                        color = LushGreen,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        updateDownloadedMessage = null
                        viewModel.dismissNotificationDetail()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.deleteNotification(notif.id) }) {
                    Text("Delete", color = Color(0xFFC62828))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
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
        shape = RoundedCornerShape(12.dp),
        color = if (notif.isRead) Color.White else Color(0xFFF4F9F4),
        border = BorderStroke(1.dp, if (!notif.isRead) LushGreen else CardBorderColor),
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
                                notif.type.uppercase().contains("AGRONOMIC") || notif.type.uppercase().contains("GUIDE") -> Color(0xFF00897B)
                                notif.type.uppercase().contains("SUPPORT") || notif.type.uppercase().contains("REPLY") -> Color(0xFF8E24AA)
                                notif.type.uppercase().contains("CROP") -> LushGreen
                                notif.type.uppercase().contains("BUG") || notif.type.uppercase().contains("FIX") -> Color(0xFFE65100)
                                notif.type.uppercase().contains("SYSTEM") || notif.type.uppercase().contains("ADMIN") -> Color(0xFF1565C0)
                                else -> LushGreen
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            notif.type.uppercase().contains("AGRONOMIC") || notif.type.uppercase().contains("GUIDE") -> Icons.AutoMirrored.Filled.MenuBook
                            notif.type.uppercase().contains("SUPPORT") || notif.type.uppercase().contains("REPLY") -> Icons.Default.SupportAgent
                            notif.type.uppercase().contains("CROP") -> Icons.Default.Eco
                            notif.type.uppercase().contains("BUG") || notif.type.uppercase().contains("FIX") -> Icons.Default.Build
                            notif.type.uppercase().contains("SYSTEM") || notif.type.uppercase().contains("ADMIN") -> Icons.Default.Campaign
                            else -> Icons.Default.Notifications
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(notif.title, fontWeight = FontWeight.Bold, color = DeepBlack, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(notif.message, color = MutedText, fontSize = 11.sp, maxLines = 2)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(notif.timestamp, color = MutedText.copy(alpha = 0.7f), fontSize = 10.sp)
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFC62828), modifier = Modifier.size(16.dp))
            }
        }
    }
}
