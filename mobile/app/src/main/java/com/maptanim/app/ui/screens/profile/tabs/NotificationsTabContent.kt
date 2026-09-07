package com.maptanim.app.ui.screens.profile.tabs

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
import com.maptanim.app.ui.screens.profile.ProfileUiState
import com.maptanim.app.ui.screens.profile.ProfileViewModel
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.White
import kotlinx.coroutines.launch

@Composable
fun NotificationsTabContent(
    uiState: ProfileUiState,
    viewModel: ProfileViewModel
) {
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredNotifications = remember(uiState.notifications, selectedFilter) {
        when (selectedFilter) {
            "UNREAD" -> uiState.notifications.filter { !it.isRead }
            "GUIDE" -> uiState.notifications.filter { it.type.uppercase().contains("AGRONOMIC") || it.type.uppercase().contains("GUIDE") }
            "CROP" -> uiState.notifications.filter { it.type.uppercase().contains("CROP") }
            "SYSTEM" -> uiState.notifications.filter { it.type.uppercase().contains("SYSTEM") || it.type.uppercase().contains("ADMIN") }
            "SUPPORT" -> uiState.notifications.filter { it.type.uppercase().contains("SUPPORT") || it.type.uppercase().contains("REPLY") }
            "BUG" -> uiState.notifications.filter { it.type.uppercase().contains("BUG") }
            else -> uiState.notifications
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Filter Chips for Admin & System Bulletins with horizontal scrolling
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
                Text(notif.title, fontWeight = FontWeight.Bold, color = White)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(notif.message, color = White.copy(alpha = 0.85f), fontSize = 14.sp)
                    Text("Time: ${notif.timestamp}", color = White.copy(alpha = 0.5f), fontSize = 11.sp)

                    val isSystemUpdate = notif.type.uppercase().contains("SYSTEM") ||
                            notif.type.uppercase().contains("CROP") ||
                            notif.title.contains("Update", ignoreCase = true) ||
                            notif.title.contains("Pananim", ignoreCase = true)

                    if (isSystemUpdate) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF2E7D32).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFF43A047)),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "System Update Available",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF81C784)
                                )
                                Text(
                                    text = "I-download ang pinakabagong pananim at mga patakaran mula sa DA / Central Database nang walang bagong app update.",
                                    fontSize = 11.sp,
                                    color = White.copy(alpha = 0.7f)
                                )

                                Button(
                                    onClick = {
                                        scope.launch {
                                            isDownloadingUpdate = true
                                            try {
                                                com.maptanim.app.data.repository.RepositoryProvider.cropRepository.refreshCrops()
                                                updateDownloadedMessage = "Matagumpay na na-download ang bagong datos!"
                                            } catch (e: Exception) {
                                                updateDownloadedMessage = "Error sa pag-download: ${e.message}"
                                            } finally {
                                                isDownloadingUpdate = false
                                            }
                                        }
                                    },
                                    enabled = !isDownloadingUpdate,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                ) {
                                    if (isDownloadingUpdate) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = White
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Dina-download ang datos...", fontSize = 12.sp, color = White)
                                    } else {
                                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp), tint = White)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("I-download ang Bagong Datos (Sync)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = White)
                                    }
                                }

                                if (updateDownloadedMessage != null) {
                                    Text(
                                        text = updateDownloadedMessage!!,
                                        fontSize = 11.sp,
                                        color = Color(0xFF81C784),
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
        border = if (!notif.isRead) BorderStroke(1.dp, ForestGreen) else null,
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
                            notif.type.uppercase().contains("AGRONOMIC") || notif.type.uppercase().contains("GUIDE") -> Icons.Default.MenuBook
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
