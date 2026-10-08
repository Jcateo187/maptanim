package com.maptanim.app.features.home.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maptanim.app.R
import com.maptanim.app.domain.model.ManagementStage
import com.maptanim.app.features.farm.dialogs.CropCareDssDialog
import com.maptanim.app.features.farm.dialogs.CropInformationDialog
import com.maptanim.app.features.home.components.*
import com.maptanim.app.features.home.viewmodel.HomeViewModel
import com.maptanim.app.navigation.MainBottomNavBar
import com.maptanim.app.navigation.Routes

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)

private data class ActiveCheckUpTarget(
    val cropName: String,
    val varietyName: String,
    val gardenLabel: String,
    val plotId: String,
    val stage: ManagementStage
)

/**
 * HomeScreen — Clean, high-performance home dashboard.
 * Composes focused modules:
 * 1. VegetableCheckUpCard (inspect gardens & perform vegetable health check-ups)
 * 2. TodayTasksCard (daily care tasks with completion checks)
 * 3. HomeVegetablesGridCard (2x2 grid of Tomato, Carrot, Eggplant, and Chilli Pepper with library navigation)
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
    var activeCheckUp by remember { mutableStateOf<ActiveCheckUpTarget?>(null) }
    var selectedCropForInfo by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        homeViewModel.refreshCalibration()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ── Extended Background Header Image reaching down to the Vegetable Check Up area ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.top_image_bgheader),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Multi-stop gradient: subtle dark scrim at top for logo/status, smooth blend into pure white at bottom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.0f to Color.Black.copy(alpha = 0.40f),
                                0.22f to Color.Black.copy(alpha = 0.18f),
                                0.50f to Color.Transparent,
                                0.72f to Color.White.copy(alpha = 0.45f),
                                0.88f to Color.White.copy(alpha = 0.88f),
                                1.0f to Color.White
                            )
                        )
                    )
            )
        }

        Scaffold(
            topBar = {
                // Header Content Row: App Logo with drop shadow + MapTanim with text shadow on left, Notifications on right
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // App Logo + MapTanim Title ("Map" white, "Tanim" green) with shadow
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 6.dp,
                            color = Color.Transparent
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.app_logo),
                                contentDescription = "MapTanim Logo",
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }

                        val titleShadow = Shadow(
                            color = Color.Black.copy(alpha = 0.85f),
                            offset = Offset(2f, 2f),
                            blurRadius = 8f
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Map",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                style = LocalTextStyle.current.copy(shadow = titleShadow)
                            )
                            Text(
                                text = "Tanim",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF4CAF50),
                                style = LocalTextStyle.current.copy(shadow = titleShadow)
                            )
                        }
                    }

                    // Notification Button with shadow pill
                    IconButton(
                        onClick = { navController.navigate(Routes.NOTIFICATIONS) },
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color.Black.copy(alpha = 0.28f), CircleShape)
                    ) {
                        BadgedBox(
                            badge = {
                                if (uiState.notificationCount > 0) {
                                    Badge(
                                        containerColor = Color(0xFFE53935),
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
                                tint = Color.White
                            )
                        }
                    }
                }
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
            containerColor = Color.Transparent
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 6.dp, bottom = 16.dp)
            ) {
            // ── 1. Vegetables Check Up ───────────────────────────────────────
            item {
                VegetableCheckUpCard(
                    plots = uiState.plots,
                    farmName = uiState.activeFarm?.farmName ?: "My Vegetable Farm",
                    onStartCheckUp = { cropName, variety, gardenLabel, plotId, stage ->
                        activeCheckUp = ActiveCheckUpTarget(cropName, variety, gardenLabel, plotId, stage)
                    },
                    onOpenFarmHub = { navController.navigate(Routes.FARM) }
                )
            }

            // ── 2. Today's Care Tasks ────────────────────────────────────────
            item {
                TodayTasksCard(
                    tasks = uiState.todayTasks,
                    onCompleteTask = { taskId -> homeViewModel.completeTask(taskId) }
                )
            }

            // ── 3. Vegetables (2 Rows × 2 Columns Grid) ──────────────────────
            item {
                HomeVegetablesGridCard(
                    onVegetableClick = { cropName ->
                        selectedCropForInfo = cropName
                    }
                )
            }
        }
    }

    // Direct Agronomic Check-Up Dialog for the selected vegetable
    activeCheckUp?.let { checkUp ->
        CropCareDssDialog(
            cropName = checkUp.cropName,
            varietyName = checkUp.varietyName,
            gardenLabel = checkUp.gardenLabel,
            plotId = checkUp.plotId,
            currentStage = checkUp.stage,
            onDismiss = { activeCheckUp = null }
        )
    }

    // Comprehensive Vegetable Encyclopedia Dossier Dialog
    selectedCropForInfo?.let { cropName ->
        CropInformationDialog(
            cropName = cropName,
            onDismiss = { selectedCropForInfo = null }
        )
    }
    }
}
