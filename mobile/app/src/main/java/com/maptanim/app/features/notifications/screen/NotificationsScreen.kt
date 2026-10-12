package com.maptanim.app.features.notifications.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.White

private val MonitoringBg = Color(0xFF10160F)
private val MonitoringCard = Color(0xFF1B2317)
private val MonitoringBorder = Color(0xFF2E4D3E)

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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MonitoringBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Bar
            Surface(
                color = MonitoringCard,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MonitoringBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Notifications",
                        color = White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (unreadCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFFE53935),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "$unreadCount new",
                                color = White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Notification Tab Content (Filters, Cards, and Actions)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                NotificationsTabContent(uiState = uiState, viewModel = viewModel)
            }
        }
    }
}
