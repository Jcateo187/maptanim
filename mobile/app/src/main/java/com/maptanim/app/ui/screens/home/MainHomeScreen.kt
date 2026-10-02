package com.maptanim.app.ui.screens.home

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maptanim.app.core.audio.AmbientSound
import com.maptanim.app.core.audio.BackgroundTrack
import com.maptanim.app.core.audio.TrackAmbientEffect
import com.maptanim.app.core.audio.TrackBgmEffect
import com.maptanim.app.domain.model.*
import java.time.LocalDate
import com.maptanim.app.navigation.BottomNavItem
import com.maptanim.app.navigation.Routes
import com.maptanim.app.renderer.model.PlotRenderData
import com.maptanim.app.ui.components.avatar.ProfileAvatar
import com.maptanim.app.ui.dialogs.CropDssManagementDialog
import com.maptanim.app.ui.theme.*

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.maptanim.app.R

// ─── Monitoring Dashboard Color Palette ─────────────────────────────────────
private val MonitoringBg = Color(0xFF10160F)            // Deep dark forest/charcoal background
private val MonitoringSurface = Color(0xFF131D15)       // Overlay container surface
private val MonitoringCard = Color(0xFF1B2317)          // Card container dark olive
private val MonitoringBorder = Color(0xFF2E4D3E)        // Deep forest card border
private val MonitoringGreenLight = Color(0xFF81C784)    // Accent bright green

/**
 * MainHomeScreen — Responsive farm dashboard styled with MapTanim's signature
 * Monitoring Dashboard dark forest color palette and blended scenic header.
 * Supports both Portrait and Landscape orientations.
 */
@Composable
fun MainHomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {
    TrackBgmEffect(BackgroundTrack.PEACEFUL_FARM)
    TrackAmbientEffect(AmbientSound.DAY_BIRDS)

    val uiState by homeViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    var isSideNavOpen by remember { mutableStateOf(false) }
    var selectedCropForDss by remember { mutableStateOf<PlotRenderData?>(null) }

    val plantedCrops = remember(uiState.plots) {
        uiState.plots.filter {
            !it.cropName.isNullOrBlank() &&
            !it.cropName.equals("Bed", ignoreCase = true) &&
            !it.cropName!!.startsWith("Bed #", ignoreCase = true) &&
            it.cropId != "bed"
        }
    }
    val alertTask = uiState.todayTasks.firstOrNull()
    val alertPlot = if (alertTask != null) {
        uiState.plots.firstOrNull { it.id == alertTask.plotId } ?: plantedCrops.firstOrNull() ?: uiState.plots.firstOrNull()
    } else {
        plantedCrops.firstOrNull() ?: uiState.plots.firstOrNull()
    }
    val alertBedLabel = alertTask?.plotLabel ?: alertPlot?.plotLabel ?: "Bed #1"
    val alertCropName = alertTask?.cropName ?: alertPlot?.cropName ?: "Tomato"
    val alertTitle = "Farm Alert • $alertBedLabel"
    val alertMessage = when {
        alertTask != null -> "[$alertBedLabel • $alertCropName] ${alertTask.taskType.name}: ${alertTask.title}"
        alertPlot != null && !alertPlot.cropName.isNullOrBlank() -> "[$alertBedLabel • ${alertPlot.cropName}] ${when(alertPlot.growthStage) {
            1 -> "Stage 1: Preparation • Bed and soil conditioning, organic compost application."
            2 -> "Stage 2: Planting • Seed depth & spacing calibration, initial gentle watering."
            3 -> "Stage 3: Early Growth • Seedling emergence check, root aeration, weed suppression."
            4 -> "Stage 4: Vegetative Growth • Rapid foliage expansion, vermicast side-dressing, trellising."
            5 -> "Stage 5: Flowering & Fruit Development • Bloom protection, potassium nutrition, pest scouting."
            else -> "Stage 6: Harvest • Fruit maturity assessment, selective morning harvesting."
        }}"
        else -> "[$alertBedLabel] Bed prepared • Tap to assign crops and plant."
    }

    val insightPlot = plantedCrops.firstOrNull() ?: uiState.plots.firstOrNull()
    val insightBedLabel = insightPlot?.plotLabel ?: "Bed #1"
    val insightCropName = insightPlot?.cropName ?: "Tomato"
    val insightVariety = insightPlot?.cropVariety?.takeIf { it.isNotBlank() } ?: "Diamante Max F1"
    val insightStage = insightPlot?.growthStage ?: 3

    val destinationCropLabel = "[$insightBedLabel] $insightCropName ($insightVariety)"
    val destinationInsightTopic = when (insightStage) {
        1 -> "Stage 1 (Preparation): Maintain consistent soil moisture and organic matter in $insightBedLabel."
        2 -> "Stage 2 (Planting): Verify proper spacing and adequate shading for newly planted seedlings."
        3 -> "Stage 3 (Early Growth): Clear root-zone weeds and check early root establishment."
        4 -> "Stage 4 (Vegetative Growth): Side-dress with organic vermicast and check mulch to suppress weed competition."
        5 -> "Stage 5 (Flowering & Fruit): Boost potassium nutrition and monitor for pests and blossom drop."
        else -> "Stage 6 (Harvest): Check fruit maturity break. Harvest in the cool early morning."
    }

    // Refresh on resume
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                homeViewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        containerColor = MonitoringBg,
        bottomBar = {
            if (!isLandscape) {
                MainBottomNavBar(
                    selectedRoute = Routes.HOME,
                    onNavigate = { route ->
                        if (route != Routes.HOME) {
                            navController.navigate(route)
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            // Floating Action Button (Green Pencil) -> Navigates to FarmEditorScreen to add crops & edit
            FloatingActionButton(
                onClick = {
                    navController.navigate(Routes.EDIT)
                },
                containerColor = ForestGreen,
                contentColor = White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 8.dp, end = 8.dp)
                    .size(60.dp)
                    .shadow(12.dp, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Farm & Add Crops",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLandscape) {
                // ─── Landscape Responsive Layout with Side Nav Bar & Blended Hero Header ─────
                Row(modifier = Modifier.fillMaxSize()) {
                    // Left Side Navigation Bar (With Open / Close Toggle)
                    LandscapeSideNavBar(
                        isOpen = isSideNavOpen,
                        onToggleOpen = { isSideNavOpen = !isSideNavOpen },
                        selectedRoute = Routes.HOME,
                        onNavigate = { route ->
                            if (route != Routes.HOME) {
                                navController.navigate(route)
                            }
                        }
                    )

                    // Scrollable Landscape Dashboard Content
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // 1. Panoramic Blended Header Section
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Background Image with Left & Bottom Blends
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(310.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.top_image_bgheader),
                                    contentDescription = "MapTanim Header Background",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                // Left color blend for app name and logo
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(
                                            Brush.horizontalGradient(
                                                0.0f to MonitoringBg.copy(alpha = 0.96f),
                                                0.28f to MonitoringBg.copy(alpha = 0.88f),
                                                0.58f to MonitoringBg.copy(alpha = 0.40f),
                                                0.82f to Color.Transparent
                                            )
                                        )
                                    )


                                // Bottom semi-blend into screen background color
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(
                                            Brush.verticalGradient(
                                                0.0f to Color.Transparent,
                                                0.35f to Color.Transparent,
                                                0.72f to MonitoringBg.copy(alpha = 0.72f),
                                                1.0f to MonitoringBg
                                            )
                                        )
                                )
                            }

                            // Foreground: 2-Column Hero Area (Left: Brand + San Isidro Farm; Right: Farmer + Alerts)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Left Column: Logo & Slogan above San Isidro Farm Card
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(0.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(96.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Image(
                                                painter = painterResource(R.drawable.app_logo),
                                                contentDescription = "MapTanim Logo",
                                                modifier = Modifier.size(52.dp)
                                            )
                                            Column {
                                                Text(
                                                    text = "MapTanim",
                                                    fontSize = 25.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = White,
                                                    letterSpacing = (-0.5).sp
                                                )
                                                Text(
                                                    text = "Your Farm, Our Support.",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color(0xFFC5D1C8)
                                                )
                                            }
                                        }
                                    }

                                    FarmOverviewHeroCard(
                                        farmName = uiState.activeFarm?.farmName?.ifBlank { "San Isidro Farm" } ?: "San Isidro Farm",
                                        plots = uiState.plots,
                                        activeCropsCount = plantedCrops.size,
                                        onViewFarm = { navController.navigate(Routes.FARMS) },
                                        onSelectPlot = { plot -> selectedCropForDss = plot }
                                    )
                                }

                                // Right Column: Farmer + Sync & Notification sitting FLUSH directly on top of Farm Alerts Card
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(0.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(96.dp)
                                    ) {
                                        // Farmer image sticks directly onto the top border of Farm Alerts Card (zero gap!)
                                        Image(
                                            painter = painterResource(R.drawable.top_of_alert_area),
                                            contentDescription = "Farmer Alert Guide",
                                            modifier = Modifier
                                                .height(104.dp)
                                                .align(Alignment.BottomCenter)
                                                .offset(x = (-30).dp, y = 3.dp),
                                            contentScale = ContentScale.FillHeight
                                        )

                                        // Sleek compact Notification & Rotate Buttons & High-Contrast Synced Capsule on Right
                                        Column(
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(bottom = 6.dp, end = 2.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            // Sleek 32dp compact notification button (not bulky gray!)
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xE60D1810))
                                                    .border(1.dp, Color(0x662E4D3E), CircleShape)
                                                    .clickable { navController.navigate(Routes.NOTIFICATIONS) },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                BadgedBox(
                                                    badge = {
                                                        if (uiState.notificationCount > 0) {
                                                            Badge(
                                                                containerColor = Danger,
                                                                contentColor = White,
                                                                modifier = Modifier.offset(x = 2.dp, y = (-2).dp)
                                                            ) {
                                                                Text(
                                                                    text = if (uiState.notificationCount > 99) "99+" else uiState.notificationCount.toString(),
                                                                    fontSize = 8.sp,
                                                                    fontWeight = FontWeight.Bold
                                                                )
                                                            }
                                                        }
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Notifications,
                                                        contentDescription = "Notifications",
                                                        tint = White,
                                                        modifier = Modifier.size(17.dp)
                                                    )
                                                }
                                            }

                                            // High-contrast dark capsule for Synced now so it's clearly visible against sunrise sky
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(Color(0xE60A140E))
                                                    .border(1.dp, Color(0x662E4D3E), RoundedCornerShape(10.dp))
                                                    .padding(horizontal = 7.dp, vertical = 3.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Refresh,
                                                    contentDescription = "Synced",
                                                    tint = Color(0xFF81C784),
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Text(
                                                    text = "Synced now.",
                                                    fontSize = 10.sp,
                                                    color = Color(0xFFA5D6A7),
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    }

                                    FarmAlertsHeroCard(
                                        alertTitle = alertTitle,
                                        alertMessage = alertMessage,
                                        onViewGuide = { navController.navigate(Routes.libraryRoute(alertCropName)) },
                                        onClick = { if (alertPlot != null) selectedCropForDss = alertPlot else navController.navigate(Routes.EDIT) }
                                    )
                                }
                            }
                        }

                        // 2. Lower 2-Column Section for Landscape
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                MyCropsSection(
                                    plots = uiState.plots,
                                    onCropClick = { plot -> selectedCropForDss = plot },
                                    onManageCrops = { navController.navigate(Routes.EDIT) }
                                )

                                TodaysTasksCard(
                                    tasks = uiState.todayTasks,
                                    plots = uiState.plots,
                                    onToggleTask = { taskId -> homeViewModel.completeTask(taskId) },
                                    onTaskClick = { task ->
                                        val targetPlot = uiState.plots.firstOrNull { it.id == task.plotId } ?: plantedCrops.firstOrNull()
                                        if (targetPlot != null) selectedCropForDss = targetPlot
                                    },
                                    onViewAllTasks = { navController.navigate(Routes.FARMS) }
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                FarmInsightCard(
                                    cropLabel = destinationCropLabel,
                                    insightTopic = destinationInsightTopic,
                                    cropName = insightCropName,
                                    onViewRecommendation = { navController.navigate(Routes.libraryRoute(insightCropName)) },
                                    onClick = { if (insightPlot != null) selectedCropForDss = insightPlot }
                                )

                                Spacer(modifier = Modifier.height(64.dp))
                            }
                        }
                    }
                }
            } else {
                // ─── Portrait Single-Column Layout with Blended Hero Header ───
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header Section with Blended Background
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.top_image_bgheader),
                                contentDescription = "MapTanim Header Background",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Left color blend for app name and logo
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            0.0f to MonitoringBg.copy(alpha = 0.96f),
                                            0.35f to MonitoringBg.copy(alpha = 0.88f),
                                            0.65f to MonitoringBg.copy(alpha = 0.40f),
                                            0.90f to Color.Transparent
                                        )
                                    )
                            )

                            // Bottom semi-blend into screen background color
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(
                                        Brush.verticalGradient(
                                            0.0f to Color.Transparent,
                                            0.35f to Color.Transparent,
                                            0.70f to MonitoringBg.copy(alpha = 0.72f),
                                            1.0f to MonitoringBg
                                        )
                                    )
                            )
                        }

                        // Foreground Header
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Header Top Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Left: Logo + App Name + Slogan
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.app_logo),
                                        contentDescription = "MapTanim Logo",
                                        modifier = Modifier.size(46.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "MapTanim",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = White,
                                            letterSpacing = (-0.4).sp
                                        )
                                        Text(
                                            text = "Your Farm, Our Support.",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFFC5D1C8)
                                        )
                                    }
                                }

                                // Right: Notification Bell, Rotate & Synced now
                                Column(
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xE60D1810))
                                            .border(1.dp, Color(0x662E4D3E), CircleShape)
                                            .clickable { navController.navigate(Routes.NOTIFICATIONS) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        BadgedBox(
                                            badge = {
                                                if (uiState.notificationCount > 0) {
                                                    Badge(
                                                        containerColor = Danger,
                                                        contentColor = White,
                                                        modifier = Modifier.offset(x = 2.dp, y = (-2).dp)
                                                    ) {
                                                        Text(
                                                            text = if (uiState.notificationCount > 99) "99+" else uiState.notificationCount.toString(),
                                                            fontSize = 8.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Notifications,
                                                contentDescription = "Notifications",
                                                tint = White,
                                                modifier = Modifier.size(17.dp)
                                            )
                                        }
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xE60A140E))
                                            .border(1.dp, Color(0x662E4D3E), RoundedCornerShape(10.dp))
                                            .padding(horizontal = 7.dp, vertical = 3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "Synced",
                                            tint = Color(0xFF81C784),
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Text(
                                            text = "Synced now.",
                                            fontSize = 10.sp,
                                            color = Color(0xFFA5D6A7),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            // 2 Columns in Portrait: Farm Name Section (weight 5) & Farm Alerts (weight 4)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                // Left Column (weight 5): Farm Name Section with Canvas View Plot
                                FarmOverviewHeroCard(
                                    farmName = uiState.activeFarm?.farmName?.ifBlank { "San Isidro Farm" } ?: "San Isidro Farm",
                                    plots = uiState.plots,
                                    activeCropsCount = plantedCrops.size,
                                    onViewFarm = { navController.navigate(Routes.FARMS) },
                                    onSelectPlot = { plot -> selectedCropForDss = plot },
                                    modifier = Modifier.weight(5f)
                                )

                                // Right Column (weight 4): Farmer Artwork sticking directly on top of Farm Alerts Card
                                Column(
                                    modifier = Modifier.weight(4f),
                                    verticalArrangement = Arrangement.spacedBy(0.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(end = 6.dp),
                                        contentAlignment = Alignment.BottomEnd
                                    ) {
                                        Image(
                                            painter = painterResource(R.drawable.top_of_alert_area),
                                            contentDescription = "Farmer Alert Guide",
                                            modifier = Modifier
                                                .height(58.dp)
                                                .offset(y = 3.dp), // Sticking flush into the alert card border
                                            contentScale = ContentScale.FillHeight
                                        )
                                    }

                                    FarmAlertsHeroCard(
                                        alertTitle = alertTitle,
                                        alertMessage = alertMessage,
                                        onViewGuide = { navController.navigate(Routes.libraryRoute(alertCropName)) },
                                        onClick = { if (alertPlot != null) selectedCropForDss = alertPlot else navController.navigate(Routes.EDIT) }
                                    )
                                }
                            }
                        }
                    }

                    // Remaining Dashboard Cards for Portrait
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        MyCropsSection(
                            plots = uiState.plots,
                            onCropClick = { plot -> selectedCropForDss = plot },
                            onManageCrops = { navController.navigate(Routes.EDIT) }
                        )

                        TodaysTasksCard(
                            tasks = uiState.todayTasks,
                            plots = uiState.plots,
                            onToggleTask = { taskId -> homeViewModel.completeTask(taskId) },
                            onTaskClick = { task ->
                                val targetPlot = uiState.plots.firstOrNull { it.id == task.plotId } ?: plantedCrops.firstOrNull()
                                if (targetPlot != null) selectedCropForDss = targetPlot
                            },
                            onViewAllTasks = { navController.navigate(Routes.FARMS) }
                        )

                        FarmInsightCard(
                            cropLabel = destinationCropLabel,
                            insightTopic = destinationInsightTopic,
                            cropName = insightCropName,
                            onViewRecommendation = { navController.navigate(Routes.libraryRoute(insightCropName)) },
                            onClick = { if (insightPlot != null) selectedCropForDss = insightPlot }
                        )

                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // ── Interactive Crop DSS Management Dialog (Decision Support System) ─────
    if (selectedCropForDss != null) {
        CropDssManagementDialog(
            plotRender = selectedCropForDss,
            onDismiss = { selectedCropForDss = null },
            onReadInLibrary = { cropName ->
                selectedCropForDss = null
                navController.navigate(Routes.libraryRoute(cropName))
            },
            onRecordObservation = {
                homeViewModel.refresh()
            },
            onHarvest = {
                selectedCropForDss = null
                homeViewModel.refresh()
            },
            onStartPlanting = {
                selectedCropForDss = null
                navController.navigate(Routes.FARMS)
            }
        )
    }
}

// ─── Farm Canvas Plot Thumbnail (Canvas View Plot) ───────────────────────────
@Composable
private fun FarmCanvasPlotThumbnail(
    plots: List<PlotRenderData>,
    onSelectPlot: (PlotRenderData) -> Unit,
    modifier: Modifier = Modifier
) {
    val displayPlots = remember(plots) {
        if (plots.isNotEmpty()) plots.take(4) else listOf(
            PlotRenderData(
                id = "demo_plot_1",
                farmId = "farm-1",
                plotLabel = "Bed #1",
                cropName = "Tomato",
                cropId = "tomato",
                cropVariety = "Diamante Max F1",
                soilType = SoilType.LOAM,
                posX = 0f,
                posY = 0f,
                widthM = 1.2f,
                heightM = 3.0f
            )
        )
    }

    Box(
        modifier = modifier
            .background(Color(0xFF132217))
            .padding(3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            displayPlots.forEach { plotItem ->
                val rawCropName = plotItem.cropName?.lowercase() ?: ""
                val isRealCrop = rawCropName.isNotBlank() &&
                    !rawCropName.startsWith("bed") &&
                    !rawCropName.equals("bed")

                val cropTint = when {
                    rawCropName.contains("tomato") -> Color(0xFFFF5252)
                    rawCropName.contains("eggplant") -> Color(0xFFBA68C8)
                    rawCropName.contains("pechay") -> Color(0xFF66BB6A)
                    rawCropName.contains("okra") -> Color(0xFF81C784)
                    rawCropName.contains("carrot") -> Color(0xFFFFA726)
                    rawCropName.contains("corn") -> Color(0xFFFFD54F)
                    else -> Color(0xFF81C784)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF2C1E17))
                        .border(
                            1.dp,
                            if (plotItem.isHarvestReady) Color(0xFFFFD54F) else Color(0xFF5D4037),
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { onSelectPlot(plotItem) }
                        .padding(horizontal = 3.dp, vertical = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Bed Number badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0x99000000))
                                .padding(horizontal = 3.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = plotItem.plotLabel,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD7CCC8),
                                maxLines = 1
                            )
                        }

                        // Sprout dots
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isRealCrop) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(cropTint)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF81C784))
                                )
                            } else {
                                Text(
                                    text = "Ready",
                                    fontSize = 7.sp,
                                    color = Color(0xFF8D6E63)
                                )
                            }
                        }

                        // Crop label or harvest ready
                        Text(
                            text = if (plotItem.isHarvestReady) "Harvest" else (if (isRealCrop) (plotItem.cropName ?: "Planted") else "Planned"),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (plotItem.isHarvestReady) Color(0xFFFFD54F) else (if (isRealCrop) Color(0xFFA5D6A7) else Color(0xFFBCAAA4)),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// ─── 1. Farm Overview Hero Card (Farm Name Section) ─────────────────────────
@Composable
private fun FarmOverviewHeroCard(
    farmName: String,
    plots: List<PlotRenderData>,
    activeCropsCount: Int,
    onViewFarm: () -> Unit,
    onSelectPlot: (PlotRenderData) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val bedCount = plots.size
    val bedsSummary = when {
        bedCount == 0 -> "0 Beds"
        bedCount == 1 -> "Bed #1"
        else -> "Bed #1 – #$bedCount"
    }

    val cropNames = plots.mapNotNull { it.cropName }
        .filter { it.isNotBlank() && !it.startsWith("Bed", ignoreCase = true) && !it.equals("bed", ignoreCase = true) }
        .distinct()
    val cropsLabel = when {
        cropNames.isNotEmpty() -> "$activeCropsCount Crops (${cropNames.take(2).joinToString(", ")})"
        activeCropsCount > 0 -> "$activeCropsCount Active Crops"
        else -> "No crops planted"
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF111E16).copy(alpha = 0.94f),
        border = BorderStroke(1.2.dp, Color(0xFF26402E)),
        shadowElevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Farm Name
            Text(
                text = farmName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Canvas View Plot (The farm plots rendered directly on canvas!)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF2E4D3E), RoundedCornerShape(8.dp))
            ) {
                FarmCanvasPlotThumbnail(
                    plots = plots,
                    onSelectPlot = onSelectPlot,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Bottom of that is crops and bed number (NO location, NO hectare, NO plots)
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = "Crops",
                        tint = Color(0xFFA5D6A7),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = cropsLabel,
                        fontSize = 11.sp,
                        color = Color(0xFFE0E0E0),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GridOn,
                        contentDescription = "Bed Number",
                        tint = Color(0xFFA5D6A7),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = bedsSummary,
                        fontSize = 11.sp,
                        color = Color(0xFFE0E0E0),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // View Farm link
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onViewFarm() }
                    .padding(vertical = 1.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "View Farm.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = White
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "View Farm",
                    tint = White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

// ─── 2. Farm Alerts Hero Card (Farm Alerts) ─────────────────────────────────
@Composable
private fun FarmAlertsHeroCard(
    alertTitle: String = "Farm Alerts",
    alertMessage: String = "Pechay needs pest checking",
    onViewGuide: () -> Unit,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = { onClick?.invoke() ?: onViewGuide() },
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF131D15).copy(alpha = 0.94f),
        border = BorderStroke(1.2.dp, Color(0xFFE67E22).copy(alpha = 0.85f)),
        shadowElevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Alert",
                    tint = Color(0xFFFF8A65),
                    modifier = Modifier.size(17.dp)
                )
                Text(
                    text = alertTitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF8A65),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = alertMessage,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = White,
                lineHeight = 16.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.weight(1f, fill = false))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onViewGuide() }
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "View guide.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFF8A65)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "View Guide",
                    tint = Color(0xFFFF8A65),
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}


// ─── 4. Today's Tasks Card (TODAY'S TASKS · 3) ──────────────────────────────
@Composable
private fun TodaysTasksCard(
    tasks: List<FarmTask>,
    plots: List<PlotRenderData> = emptyList(),
    onToggleTask: (String) -> Unit,
    onTaskClick: ((FarmTask) -> Unit)? = null,
    onViewAllTasks: () -> Unit
) {
    val displayTasks = remember(tasks, plots) {
        if (tasks.isNotEmpty()) {
            tasks.take(4)
        } else {
            val p1 = plots.getOrNull(0)?.plotLabel ?: "Bed #1"
            val c1 = plots.getOrNull(0)?.cropName ?: "Tomato"
            val p2 = plots.getOrNull(1)?.plotLabel ?: "Bed #2"
            val c2 = plots.getOrNull(1)?.cropName ?: "Eggplant"
            val p3 = plots.getOrNull(2)?.plotLabel ?: "Bed #3"
            val c3 = plots.getOrNull(2)?.cropName ?: "Pechay"

            listOf(
                FarmTask(
                    id = "default_task_1",
                    farmId = "farm-1",
                    plotId = plots.getOrNull(0)?.id ?: "plot-1",
                    plotLabel = p1,
                    cropName = c1,
                    taskType = TaskType.WATER,
                    title = "Morning Soil Moisture Check",
                    subLabel = "Check root zone 5cm depth moisture",
                    dueDate = LocalDate.now().toString(),
                    isCompleted = false,
                    completedAt = null
                ),
                FarmTask(
                    id = "default_task_2",
                    farmId = "farm-1",
                    plotId = plots.getOrNull(1)?.id ?: "plot-2",
                    plotLabel = p2,
                    cropName = c2,
                    taskType = TaskType.FERTILIZE,
                    title = "Organic Vermicast Top-Dress",
                    subLabel = "Apply 2 kg/m² compost",
                    dueDate = LocalDate.now().toString(),
                    isCompleted = false,
                    completedAt = null
                ),
                FarmTask(
                    id = "default_task_3",
                    farmId = "farm-1",
                    plotId = plots.getOrNull(2)?.id ?: "plot-3",
                    plotLabel = p3,
                    cropName = c3,
                    taskType = TaskType.OBSERVATION,
                    title = "Foliage & Pest Scouting",
                    subLabel = "Inspect undersides for leafminers",
                    dueDate = LocalDate.now().toString(),
                    isCompleted = false,
                    completedAt = null
                )
            )
        }
    }
    val taskCount = if (tasks.isNotEmpty()) tasks.size else displayTasks.size

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MonitoringCard,
        border = BorderStroke(1.2.dp, MonitoringBorder),
        shadowElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TODAY'S TASKS · $taskCount",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = White.copy(alpha = 0.9f),
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Area Specific",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = MonitoringGreenLight
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            displayTasks.forEach { task ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onTaskClick?.invoke(task) ?: onToggleTask(task.id) }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .border(1.8.dp, MonitoringGreenLight.copy(alpha = 0.8f), CircleShape)
                            .clickable { onToggleTask(task.id) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (task.isCompleted) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(MonitoringGreenLight)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0x3381C784))
                            .border(0.8.dp, Color(0x6681C784), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.plotLabel ?: "Bed #1",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MonitoringGreenLight
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = White.copy(alpha = 0.92f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        val cropPart = task.cropName?.takeIf { it.isNotBlank() } ?: "Garden"
                        val typePart = task.taskType.name.lowercase().replaceFirstChar { it.uppercase() }
                        Text(
                            text = "$cropPart • $typePart",
                            fontSize = 10.sp,
                            color = Color(0xFFA5D6A7),
                            maxLines = 1
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    onClick = onViewAllTasks,
                    color = Color.Transparent,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "VIEW ALL TASKS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MonitoringGreenLight,
                            letterSpacing = 0.5.sp
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "View All Tasks",
                            tint = MonitoringGreenLight,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─── 5. My Crops Section (MY CROPS) ─────────────────────────────────────────
private data class MyCropSummaryItem(
    val plotId: String,
    val name: String,
    val variety: String,
    val bedLabel: String,
    val stage: String,
    val progressRatio: Float,
    val daysText: String,
    val isHarvestReady: Boolean,
    val accentColor: Color
)

private fun getCropAccentColor(cropName: String): Color {
    val clean = cropName.lowercase()
    return when {
        clean.contains("tomato") || clean.contains("kamatis") -> Color(0xFFFF5252)
        clean.contains("eggplant") || clean.contains("talong") -> Color(0xFFBA68C8)
        clean.contains("pechay") || clean.contains("bokchoy") -> Color(0xFF66BB6A)
        clean.contains("okra") -> Color(0xFF81C784)
        clean.contains("sitaw") || clean.contains("stringbean") || clean.contains("bean") -> Color(0xFF8BC34A)
        clean.contains("ampalaya") || clean.contains("bitter") -> Color(0xFF4CAF50)
        clean.contains("carrot") || clean.contains("karot") -> Color(0xFFFFA726)
        clean.contains("chili") || clean.contains("sili") || clean.contains("pepper") -> Color(0xFFFF7043)
        clean.contains("corn") || clean.contains("mais") -> Color(0xFFFFD54F)
        clean.contains("onion") || clean.contains("sibuyas") -> Color(0xFFB39DDB)
        clean.contains("squash") || clean.contains("kalabasa") || clean.contains("pumpkin") -> Color(0xFFFFB74D)
        clean.contains("cabbage") || clean.contains("repolyo") -> Color(0xFF80CBC4)
        clean.contains("lettuce") || clean.contains("kangkong") -> Color(0xFF4DB6AC)
        else -> Color(0xFF81C784)
    }
}

@Composable
private fun MyCropsSection(
    plots: List<PlotRenderData>,
    onCropClick: (PlotRenderData) -> Unit,
    onManageCrops: () -> Unit
) {
    val plantedPlots = remember(plots) {
        plots.filter {
            !it.cropName.isNullOrBlank() &&
            !it.cropName.equals("Bed", ignoreCase = true) &&
            !it.cropName!!.startsWith("Bed #", ignoreCase = true) &&
            it.cropId != "bed"
        }
    }

    val displayCrops = remember(plantedPlots) {
        plantedPlots.map { p ->
            val stageLabel = when (p.growthStage) {
                1 -> "Preparation"
                2 -> "Planting"
                3 -> "Early Growth"
                4 -> "Vegetative Growth"
                5 -> "Flowering & Fruit"
                else -> if (p.isHarvestReady) "Harvest Ready" else "Harvest"
            }
            val daysLeft = (p.daysToHarvest - p.daysPlanted).coerceAtLeast(0)
            val daysText = if (p.isHarvestReady) "Harvest Ready" else "${daysLeft}d left"
            val varietyText = p.cropVariety?.takeIf { it.isNotBlank() } ?: "Standard"

            MyCropSummaryItem(
                plotId = p.id,
                name = p.cropName ?: "Crop",
                variety = varietyText,
                bedLabel = p.plotLabel,
                stage = stageLabel,
                progressRatio = p.stageProgressRatio.coerceIn(0.05f, 1f),
                daysText = daysText,
                isHarvestReady = p.isHarvestReady,
                accentColor = getCropAccentColor(p.cropName ?: "")
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "MY CROPS",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = White.copy(alpha = 0.9f),
                    letterSpacing = 0.5.sp
                )
                if (displayCrops.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x3381C784))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${displayCrops.size}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MonitoringGreenLight
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onManageCrops() }
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Plant / Edit Crops",
                    tint = MonitoringGreenLight,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = if (displayCrops.isEmpty()) "Plant Crops" else "Edit Crops",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MonitoringGreenLight
                )
            }
        }

        if (displayCrops.isEmpty()) {
            // Real Empty State with direct call to action to plant in Edit Screen
            Surface(
                onClick = onManageCrops,
                shape = RoundedCornerShape(16.dp),
                color = MonitoringCard,
                border = BorderStroke(1.dp, MonitoringBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0x2681C784))
                            .border(1.dp, Color(0x4D81C784), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = "No Crops",
                            tint = MonitoringGreenLight,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "No Crops Planted Yet",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                        Text(
                            text = "Tap to plant crops on your beds in Edit Screen",
                            fontSize = 11.sp,
                            color = Color(0xFFA5D6A7),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Go to Edit Screen",
                        tint = MonitoringGreenLight,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        } else {
            // Horizontal scrolling row of real user crops
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                displayCrops.forEach { cropItem ->
                    val plot = plantedPlots.firstOrNull { it.id == cropItem.plotId }
                    Surface(
                        onClick = { if (plot != null) onCropClick(plot) },
                        shape = RoundedCornerShape(14.dp),
                        color = MonitoringCard,
                        border = BorderStroke(1.dp, if (cropItem.isHarvestReady) Color(0x80FFD54F) else MonitoringBorder),
                        modifier = Modifier.width(138.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            // Top Row: Accent Dot + Bed Label
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    modifier = Modifier.weight(1f, fill = false)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(cropItem.accentColor)
                                    )
                                    Text(
                                        text = cropItem.bedLabel,
                                        fontSize = 10.sp,
                                        color = White.copy(alpha = 0.6f),
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                if (cropItem.isHarvestReady) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFFE65100))
                                            .clickable { if (plot != null) onCropClick(plot) }
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "HARVEST",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = White
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Crop Name
                            Text(
                                text = cropItem.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            // Variety
                            Text(
                                text = cropItem.variety,
                                fontSize = 10.sp,
                                color = Color(0xFFA5D6A7),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(top = 1.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Stage progress bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0x332E4D3E))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(cropItem.progressRatio)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(if (cropItem.isHarvestReady) Color(0xFFFFD54F) else cropItem.accentColor)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Stage & Days remaining
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = cropItem.stage,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (cropItem.isHarvestReady) Color(0xFFFFD54F) else MonitoringGreenLight,
                                    maxLines = 1
                                )
                                Text(
                                    text = cropItem.daysText,
                                    fontSize = 10.sp,
                                    color = White.copy(alpha = 0.65f),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── 6. Farm Insight Card (FARM INSIGHT) ─────────────────────────────────────
@Composable
private fun FarmInsightCard(
    cropLabel: String,
    insightTopic: String,
    cropName: String = "Crop",
    onViewRecommendation: () -> Unit,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "FARM INSIGHT",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = White.copy(alpha = 0.9f),
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Surface(
            onClick = { onClick?.invoke() ?: onViewRecommendation() },
            shape = RoundedCornerShape(16.dp),
            color = MonitoringCard,
            border = BorderStroke(1.2.dp, MonitoringBorder),
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🌱", fontSize = 14.sp)
                    Text(
                        text = "Area Guidance & Recommendations",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MonitoringGreenLight
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = cropLabel,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Text(
                    text = insightTopic,
                    fontSize = 13.sp,
                    color = White.copy(alpha = 0.85f),
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        onClick = onViewRecommendation,
                        color = Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "VIEW ${cropName.uppercase()} RECOMMENDATION",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MonitoringGreenLight,
                                letterSpacing = 0.5.sp
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "View Recommendation",
                                tint = MonitoringGreenLight,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── 7. HomeScreen Alias (Full Compatibility) ────────────────────────────────
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {
    MainHomeScreen(navController = navController, homeViewModel = homeViewModel)
}

// ─── 8. Bottom Navigation Bar (Home, Farm, Community, Vegetables, Profile) ───
@Composable
fun MainBottomNavBar(
    selectedRoute: String,
    onNavigate: (String) -> Unit
) {
    Surface(
        color = MonitoringSurface,
        border = BorderStroke(1.dp, MonitoringBorder.copy(alpha = 0.7f)),
        shadowElevation = 16.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        NavigationBar(
            containerColor = MonitoringSurface,
            tonalElevation = 0.dp,
            modifier = Modifier.height(64.dp)
        ) {
            BottomNavItem.items.forEach { item ->
                val isSelected = when (item) {
                    BottomNavItem.Home -> selectedRoute == Routes.HOME
                    BottomNavItem.Farm -> selectedRoute == Routes.FARMS || selectedRoute == Routes.FARM
                    BottomNavItem.Community -> selectedRoute == Routes.COMMUNITY
                    BottomNavItem.Vegetables -> selectedRoute == Routes.LIBRARY || selectedRoute == Routes.VEGETABLES || selectedRoute == Routes.KNOWLEDGE
                    BottomNavItem.Profile -> selectedRoute == Routes.PROFILE || selectedRoute.startsWith("profile")
                }
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onNavigate(item.route) },
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ForestGreen,
                        selectedTextColor = ForestGreen,
                        unselectedIconColor = White.copy(alpha = 0.5f),
                        unselectedTextColor = White.copy(alpha = 0.5f),
                        indicatorColor = ForestGreen.copy(alpha = 0.25f)
                    )
                )
            }
        }
    }
}

// ─── 9. Landscape Side Navigation Bar (With Open / Close Toggle) ──────────────
@Composable
fun LandscapeSideNavBar(
    isOpen: Boolean,
    onToggleOpen: () -> Unit,
    selectedRoute: String,
    onNavigate: (String) -> Unit
) {
    val navWidth by animateDpAsState(
        targetValue = if (isOpen) 175.dp else 60.dp,
        label = "side_nav_width"
    )

    Surface(
        color = MonitoringSurface,
        border = BorderStroke(1.dp, MonitoringBorder.copy(alpha = 0.7f)),
        modifier = Modifier
            .width(navWidth)
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Open / Close toggle button at top with open/close icon
                IconButton(
                    onClick = onToggleOpen,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF162319))
                        .border(1.dp, MonitoringBorder.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        imageVector = if (isOpen) Icons.Default.Close else Icons.Default.Menu,
                        contentDescription = if (isOpen) "Close Side Navigation" else "Open Side Navigation",
                        tint = MonitoringGreenLight,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Navigation items
                BottomNavItem.items.forEach { item ->
                    val isSelected = when (item) {
                        BottomNavItem.Home -> selectedRoute == Routes.HOME
                        BottomNavItem.Farm -> selectedRoute == Routes.FARMS || selectedRoute == Routes.FARM
                        BottomNavItem.Community -> selectedRoute == Routes.COMMUNITY
                        BottomNavItem.Vegetables -> selectedRoute == Routes.LIBRARY || selectedRoute == Routes.VEGETABLES || selectedRoute == Routes.KNOWLEDGE
                        BottomNavItem.Profile -> selectedRoute == Routes.PROFILE || selectedRoute.startsWith("profile")
                    }

                    Surface(
                        onClick = { onNavigate(item.route) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) ForestGreen.copy(alpha = 0.22f) else Color.Transparent,
                        border = if (isSelected) BorderStroke(1.dp, ForestGreen.copy(alpha = 0.45f)) else null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = if (isOpen) 12.dp else 8.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = if (isOpen) Arrangement.Start else Arrangement.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) ForestGreen else White.copy(alpha = 0.65f),
                                modifier = Modifier.size(22.dp)
                            )
                            if (isOpen) {
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = item.label,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) ForestGreen else White.copy(alpha = 0.85f),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // Bottom brand indicator when expanded
            if (isOpen) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = "MapTanim",
                        tint = ForestGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "MapTanim",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                }
            }
        }
    }
}

