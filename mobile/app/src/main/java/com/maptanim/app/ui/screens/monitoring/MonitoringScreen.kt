package com.maptanim.app.ui.screens.monitoring

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maptanim.app.navigation.Routes
import com.maptanim.app.ui.components.monitoring.MonitoringDashboardOverlay
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.White

@Composable
fun MonitoringScreen(
    navController: NavController,
    viewModel: MonitoringViewModel = viewModel()
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF10160F)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(Color(0xFF1B2317), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = "Monitoring",
                    tint = ForestGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "FARM MONITORING DASHBOARD",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = White
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Main Monitoring Dashboard Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                MonitoringDashboardOverlay(
                    onDismiss = { navController.popBackStack() },
                    onNavigateToLibrary = { navController.navigate(Routes.LIBRARY) },
                    viewModel = viewModel
                )
            }
        }
    }
}
