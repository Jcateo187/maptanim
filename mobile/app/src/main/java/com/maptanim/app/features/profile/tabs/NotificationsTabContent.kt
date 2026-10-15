package com.maptanim.app.features.profile.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.domain.model.NotificationItem
import com.maptanim.app.features.profile.ProfileViewModel
import com.maptanim.app.features.profile.model.ProfileUiState
import kotlinx.coroutines.launch

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE5E7EB)
private val MutedText = Color(0xFF6B7280)
private val LightSurface = Color(0xFFF9FAF8)

data class NotificationCategoryFilter(
    val key: String,
    val label: String,
    val icon: ImageVector
)

/**
 * NotificationsTabContent — System bulletins, crop updates, and support messages in a clean list layout.
 */
@Composable
fun NotificationsTabContent(
    uiState: ProfileUiState,
    viewModel: ProfileViewModel
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    var showAllItems by remember { mutableStateOf(false) }

    val categories = remember {
        listOf(
            NotificationCategoryFilter("ALL", "All Updates", Icons.Default.Notifications),
            NotificationCategoryFilter("UNREAD", "Unread", Icons.Default.MarkEmailUnread),
            NotificationCategoryFilter("GUIDE", "Agronomic Guides", Icons.AutoMirrored.Filled.MenuBook),
            NotificationCategoryFilter("CROP", "Crops Updated", Icons.Default.Eco),
            NotificationCategoryFilter("SYSTEM", "System Announcements", Icons.Default.Campaign),
            NotificationCategoryFilter("SUPPORT", "Support Advisories", Icons.Default.SupportAgent),
            NotificationCategoryFilter("BUG", "Bug Fixes", Icons.Default.Build)
        )
    }

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

    val pageSize = 5
    val hasMore = !showAllItems && filteredNotifications.size > pageSize
    val displayedNotifications = if (showAllItems) filteredNotifications else filteredNotifications.take(pageSize)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Filter Chips with icons INSIDE the tab/category
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            categories.forEach { cat ->
                val isSelected = selectedFilter == cat.key
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedFilter = cat.key
                        showAllItems = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = cat.icon,
                            contentDescription = null,
                            tint = if (isSelected) Color.White else LushGreen,
                            modifier = Modifier.size(15.dp)
                        )
                    },
                    label = {
                        Text(
                            text = cat.label,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LushGreen,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = DeepBlack
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) LushGreen else CardBorderColor
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        if (filteredNotifications.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF1F8E9),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = LushGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Text("No notifications found", color = DeepBlack, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Updates and alerts will appear here.", color = MutedText, fontSize = 12.sp)
                    }
                }
            }
        } else {
            // Unified Clean List Layout (Not separate cards)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    displayedNotifications.forEachIndexed { index, notif ->
                        NotificationListItemRow(
                            notif = notif,
                            onClick = { viewModel.selectNotification(notif) },
                            onDelete = { viewModel.deleteNotification(notif.id) }
                        )

                        if (index < displayedNotifications.lastIndex || hasMore) {
                            HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)
                        }
                    }

                    // Button to "See more..." ("Tingnan ang higit pa...")
                    if (hasMore) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showAllItems = true }
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "See more... (${filteredNotifications.size - pageSize} more notifications)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LushGreen
                                )
                                Icon(
                                    imageVector = Icons.Default.ExpandMore,
                                    contentDescription = "See more",
                                    tint = LushGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    } else if (showAllItems && filteredNotifications.size > pageSize) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showAllItems = false }
                                .padding(vertical = 10.dp, horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Show less",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MutedText
                                )
                                Icon(
                                    imageVector = Icons.Default.ExpandLess,
                                    contentDescription = "Show less",
                                    tint = MutedText,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
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
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE8F5E9),
                            border = BorderStroke(1.dp, LushGreen.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "May bagong bersyon ng datos ng pananim mula sa DA-BPI na handa nang i-sync.",
                                    fontSize = 11.sp,
                                    color = DeepBlack
                                )

                                if (updateDownloadedMessage != null) {
                                    Text(
                                        text = updateDownloadedMessage!!,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LushGreen
                                    )
                                } else {
                                    Button(
                                        onClick = {
                                            isDownloadingUpdate = true
                                            scope.launch {
                                                kotlinx.coroutines.delay(1200)
                                                isDownloadingUpdate = false
                                                updateDownloadedMessage = "Matagumpay na na-download ang update! Naka-sync na ang mga bagong gabay."
                                            }
                                        },
                                        enabled = !isDownloadingUpdate,
                                        colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.fillMaxWidth().height(36.dp)
                                    ) {
                                        if (isDownloadingUpdate) {
                                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Nag-a-update...", fontSize = 11.sp)
                                        } else {
                                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("I-download ang Bagong Gabay", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    updateDownloadedMessage = null
                    viewModel.dismissNotificationDetail()
                }) {
                    Text("OK", color = LushGreen, fontWeight = FontWeight.Bold)
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

/**
 * Clean List item row for notifications (replacing bulky individual cards).
 */
@Composable
private fun NotificationListItemRow(
    notif: NotificationItem,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val (badgeBg, badgeColor, typeLabel) = when {
        notif.type.uppercase().contains("AGRONOMIC") || notif.type.uppercase().contains("GUIDE") ->
            Triple(Color(0xFFE0F2F1), Color(0xFF00796B), "GABAY SA PAGSASAKA")
        notif.type.uppercase().contains("SUPPORT") || notif.type.uppercase().contains("REPLY") ->
            Triple(Color(0xFFF3E5F5), Color(0xFF7B1FA2), "TUGON NG SUPPORT")
        notif.type.uppercase().contains("CROP") ->
            Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "PANANIM AT GULAY")
        notif.type.uppercase().contains("BUG") || notif.type.uppercase().contains("FIX") ->
            Triple(Color(0xFFFFF3E0), Color(0xFFE65100), "PAGPAPANATILI")
        notif.type.uppercase().contains("SYSTEM") || notif.type.uppercase().contains("ADMIN") ->
            Triple(Color(0xFFE3F2FD), Color(0xFF1565C0), "ANUNSYO SA SISTEMA")
        else ->
            Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "PAABISO")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(if (!notif.isRead) Color(0xFFF7FAF7) else Color.White)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Top
        ) {
            // Category Icon in circular badge
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(badgeBg),
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
                    tint = badgeColor,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = badgeBg,
                        modifier = Modifier.height(17.dp)
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = typeLabel,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeColor
                            )
                        }
                    }

                    if (!notif.isRead) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(LushGreen)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = notif.title,
                    fontWeight = if (!notif.isRead) FontWeight.Bold else FontWeight.SemiBold,
                    color = DeepBlack,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = notif.message,
                    color = MutedText,
                    fontSize = 11.5.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notif.timestamp,
                    color = MutedText.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            }
        }

        // Delete button
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color(0xFFC62828).copy(alpha = 0.7f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
