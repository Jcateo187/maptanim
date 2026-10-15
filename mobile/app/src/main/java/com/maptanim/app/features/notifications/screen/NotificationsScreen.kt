package com.maptanim.app.features.notifications.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maptanim.app.features.profile.ProfileViewModel
import com.maptanim.app.features.profile.tabs.NotificationsTabContent

private val ScreenBg = Color(0xFFF9FAF8)
private val DeepBlack = Color(0xFF111813)
private val LushGreen = Color(0xFF2E7D32)
private val CardBorderColor = Color(0xFFE5E7EB)

/**
 * NotificationsScreen — Full screen notification center.
 * Displays farm bulletins, agronomic visual guides, crop reminders, and system updates.
 */
@Composable
fun NotificationsScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val unreadCount = uiState.notifications.count { !it.isRead }
    val totalCount = uiState.notifications.size

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ScreenBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header Bar
            Surface(
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left: Back button + Title + Counter badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = DeepBlack,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = LushGreen,
                                modifier = Modifier.size(22.dp)
                            )

                            Text(
                                text = "Notifications",
                                color = DeepBlack,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // Number badge
                            Surface(
                                color = if (unreadCount > 0) Color(0xFFE53935) else Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (unreadCount > 0) "$unreadCount" else "$totalCount",
                                    color = if (unreadCount > 0) Color.White else LushGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Right: Mark all as read button with icon
                        TextButton(
                            onClick = { viewModel.markAllNotificationsAsRead() },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            enabled = unreadCount > 0
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DoneAll,
                                    contentDescription = "Mark all as read",
                                    tint = if (unreadCount > 0) LushGreen else Color(0xFF9E9E9E),
                                    modifier = Modifier.size(17.dp)
                                )
                                Text(
                                    text = "Mark all as read",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (unreadCount > 0) LushGreen else Color(0xFF9E9E9E)
                                )
                            }
                        }
                    }

                    // Clean dividing line that separates header from bottom
                    HorizontalDivider(color = CardBorderColor, thickness = 1.dp)
                }
            }

            // Notification Tab Content (Filters, Clean List, See More Button)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 14.dp)
            ) {
                NotificationsTabContent(uiState = uiState, viewModel = viewModel)
            }
        }
    }
}
