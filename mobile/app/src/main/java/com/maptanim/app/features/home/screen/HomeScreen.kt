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
import com.maptanim.app.features.farm.schedule.CalendarScreenTab
import com.maptanim.app.features.farm.dialogs.CropCareDssDialog
import com.maptanim.app.features.farm.dialogs.CropInformationDialog
import com.maptanim.app.features.farm.dialogs.CropScheduleCalendarScreen
import com.maptanim.app.features.farm.schedule.CropScheduleStatus
import com.maptanim.app.features.farm.schedule.resolveScheduleStatus
import com.maptanim.app.features.home.components.*
import com.maptanim.app.features.home.viewmodel.HomeViewModel
import com.maptanim.app.navigation.MainBottomNavBar
import com.maptanim.app.navigation.Routes
import java.time.LocalDate

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)

private data class ActiveCheckUpTarget(
    val cropName: String,
    val varietyName: String,
    val gardenLabel: String,
    val plotId: String,
    val stage: ManagementStage,
    val scheduleStatus: CropScheduleStatus? = null
)

private data class CalendarLaunchTarget(
    val cropName: String,
    val varietyName: String?,
    val gardenLabel: String,
    val plotId: String,
    val plantedDateStr: String
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
    var activeCalendarTarget by remember { mutableStateOf<CalendarLaunchTarget?>(null) }

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

                    // Notification Button with sleek small circle background
                    IconButton(
                        onClick = { navController.navigate(Routes.NOTIFICATIONS) },
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color.Black.copy(alpha = 0.28f), CircleShape)
                    ) {
                        BadgedBox(
                            badge = {
                                if (uiState.notificationCount > 0) {
                                    Badge(
                                        containerColor = Color(0xFFE53935),
                                        contentColor = Color.White
                                    ) {
                                        Text("${uiState.notificationCount}", fontSize = 9.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
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
                            activeCheckUp = null
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
                    farmName = uiState.activeFarm?.farmName,
                    onStartCheckUp = { cropName, variety, gardenLabel, plotId, stage ->
                        val matchingPlot = uiState.plots.firstOrNull { it.id == plotId }
                        val schedStatus = resolveScheduleStatus(
                            plantedDate = matchingPlot?.plantedDate,
                            cropName = cropName,
                            isMonitoringStarted = matchingPlot?.isMonitoringStarted ?: true
                        )
                        activeCheckUp = ActiveCheckUpTarget(
                            cropName = cropName,
                            varietyName = variety,
                            gardenLabel = gardenLabel,
                            plotId = plotId,
                            stage = schedStatus.stageOrNull ?: stage,
                            scheduleStatus = schedStatus
                        )
                    },
                    onOpenFarmHub = {
                        activeCheckUp = null
                        navController.navigate(Routes.FARM)
                    },
                    onGardenClick = { gardenId ->
                        activeCheckUp = null
                        navController.navigate(Routes.growingRoute(gardenId))
                    }
                )
            }

            // ── 2. Today's Care Tasks ────────────────────────────────────────
            item {
                TodayTasksCard(
                    tasks = uiState.todayTasks,
                    onCompleteTask = { taskId -> homeViewModel.completeTask(taskId) },
                    onTaskClick = { task ->
                        val matchingPlot = uiState.plots.firstOrNull {
                            it.id == task.plotId ||
                            it.id.startsWith("${task.plotId}_") ||
                            task.plotId.startsWith("${it.id}_") ||
                            (!task.cropName.isNullOrBlank() && it.cropName.equals(task.cropName, ignoreCase = true)) ||
                            (!task.plotLabel.isBlank() && it.plotLabel.equals(task.plotLabel, ignoreCase = true))
                        }
                        val resolvedCropName = task.cropName?.takeIf { it.isNotBlank() }
                            ?: matchingPlot?.cropName?.takeIf { it.isNotBlank() && !it.equals("Bed", ignoreCase = true) && !it.equals("Garden", ignoreCase = true) }
                            ?: uiState.plots.firstOrNull { !it.cropName.isNullOrBlank() && !it.cropName.equals("Bed", ignoreCase = true) && !it.cropName.equals("Garden", ignoreCase = true) }?.cropName
                            ?: "Pananim"
                        val resolvedVariety = matchingPlot?.cropVariety?.takeIf { it.isNotBlank() }
                            ?: com.maptanim.app.data.local.seeds.CropVarietySeeds.list.firstOrNull {
                                it.cropName.equals(resolvedCropName, ignoreCase = true) ||
                                (it.localNamePh?.contains(resolvedCropName, ignoreCase = true) == true)
                            }?.varietyName
                        val resolvedGardenLabel = matchingPlot?.plotLabel?.substringBefore(" (Z")?.takeIf { it.isNotBlank() }
                            ?: task.plotLabel.takeIf { it.isNotBlank() && !it.equals("PLOT", ignoreCase = true) }
                            ?: "Garden"
                        val farmPrefs = com.maptanim.app.core.preferences.FarmPreferencesManager.getInstance()
                        val prefDate = farmPrefs.resolveCropPlantedDate(task.plotId, matchingPlot?.id ?: task.plotId, resolvedCropName)
                        val plantedDateStr = matchingPlot?.plantedDate?.takeIf { it.isNotBlank() }
                            ?: prefDate
                            ?: if ((matchingPlot?.daysPlanted ?: 0) > 0) LocalDate.now().minusDays(matchingPlot!!.daysPlanted.toLong()).toString()
                            else LocalDate.now().toString()

                        activeCalendarTarget = CalendarLaunchTarget(
                            cropName = resolvedCropName,
                            varietyName = resolvedVariety,
                            gardenLabel = resolvedGardenLabel,
                            plotId = task.plotId.ifBlank { matchingPlot?.id ?: "plot-1" },
                            plantedDateStr = plantedDateStr
                        )
                    },
                    onViewMore = {
                        navController.navigate(Routes.GROWING)
                    }
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
            scheduleStatus = checkUp.scheduleStatus,
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
    // Dedicated Calendar Screen opened from a Today's Task click (defaults to Today's Task tab)
    activeCalendarTarget?.let { target ->
        CropScheduleCalendarScreen(
            cropName = target.cropName,
            varietyName = target.varietyName,
            gardenLabel = target.gardenLabel,
            plotId = target.plotId,
            plantedDateStr = target.plantedDateStr,
            initialTab = CalendarScreenTab.TODAYS_TASK,
            onDismiss = { activeCalendarTarget = null },
            onReschedule = { /* Kept resilient */ }
        )
    }

    // Celebration & Next Action Dialog when all daily tasks are completed
    if (uiState.showTaskCompletionOverlay) {
        TaskCompletionCelebrationDialog(
            completedPlot = uiState.completedTaskPlotTarget,
            onDismiss = { homeViewModel.dismissTaskCompletionOverlay() },
            onReplant = { plotId, cropName ->
                homeViewModel.replantCropOnPlot(plotId, cropName)
            },
            onCompostAndFallow = { plotId ->
                homeViewModel.compostAndFallowPlot(plotId)
            },
            onRotateCrop = { plotId, newCropName ->
                homeViewModel.rotateCropOnPlot(plotId, newCropName)
            }
        )
    }
    }
}
