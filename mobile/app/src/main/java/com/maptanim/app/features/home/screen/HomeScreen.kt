package com.maptanim.app.features.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
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
import com.maptanim.app.features.home.components.*
import com.maptanim.app.features.home.viewmodel.HomeViewModel
import com.maptanim.app.features.shared.avatar.ProfileAvatar
import com.maptanim.app.navigation.MainBottomNavBar
import com.maptanim.app.navigation.Routes

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)

/**
 * HomeScreen — Clean, high-performance home dashboard.
 * Composes modular cards:
 * 1. QuickActionsBar (fast navigation shortcuts)
 * 2. TodayTasksCard (daily care tasks with completion checks)
 * 3. FarmSummaryCard (4-quadrant metrics overview)
 * 4. FarmMiniMapCard (spatial garden overview)
 *
 * Adheres strictly to the Daylight High-Contrast Theme (Pure White background, Lush Green buttons, Deep Black text).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.refreshCalibration()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProfileAvatar(
                            avatarAssetPath = uiState.avatarAssetPath,
                            size = 40.dp
                        )
                        Column {
                            Text(
                                text = if (uiState.nickname.isNotBlank()) "Mabuhay, ${uiState.nickname}!" else "Mabuhay, Farmer!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = DeepBlack
                            )
                            Text(
                                text = uiState.activeFarm?.farmName ?: "My Vegetable Farm",
                                fontSize = 12.sp,
                                color = LushGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.NOTIFICATIONS) }) {
                        BadgedBox(
                            badge = {
                                if (uiState.notificationCount > 0) {
                                    Badge(
                                        containerColor = LushGreen,
                                        contentColor = Color.White
                                    ) {
                                        Text("${uiState.notificationCount}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = DeepBlack
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = DeepBlack
                )
            )
        },
        bottomBar = {
            MainBottomNavBar(
                selectedRoute = Routes.HOME,
                onNavigate = { route ->
                    if (route != Routes.HOME) {
                        navController.navigate(route) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            // ── 0. Initial DSS Observation & Availability Setup Callout ──────
            if (!uiState.isFarmCalibrated) {
                item {
                    DssOnboardingSetupCard(
                        onStartCalibration = { navController.navigate(Routes.FARM) }
                    )
                }
            }

            // ── 1. Quick Shortcuts Bar ───────────────────────────────────────
            item {
                QuickActionsBar(onNavigate = { navController.navigate(it) })
            }

            // ── 2. Today's Care Tasks ────────────────────────────────────────
            item {
                TodayTasksCard(
                    tasks = uiState.todayTasks,
                    onCompleteTask = { taskId -> homeViewModel.completeTask(taskId) }
                )
            }

            // ── 3. Farm Metrics Summary ──────────────────────────────────────
            item {
                FarmSummaryCard(summary = uiState.farmSummary)
            }

            // ── 4. Spatial Garden Mini-Map ───────────────────────────────────
            item {
                FarmMiniMapCard(
                    plots = uiState.plots,
                    onOpenFarmHub = { navController.navigate(Routes.FARM) }
                )
            }
        }
    }
}
