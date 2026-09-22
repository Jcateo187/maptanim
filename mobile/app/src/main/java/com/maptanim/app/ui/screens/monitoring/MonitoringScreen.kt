package com.maptanim.app.ui.screens.monitoring

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maptanim.app.navigation.Routes
import com.maptanim.app.ui.components.monitoring.MonitoringDashboardOverlay

@Composable
fun MonitoringScreen(
    navController: NavController,
    viewModel: MonitoringViewModel = viewModel()
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        color = Color(0xFF10160F)
    ) {
        MonitoringDashboardOverlay(
            onDismiss = { navController.popBackStack() },
            onNavigateToLibrary = { navController.navigate(Routes.LIBRARY) },
            viewModel = viewModel,
            isFullScreen = true
        )
    }
}
