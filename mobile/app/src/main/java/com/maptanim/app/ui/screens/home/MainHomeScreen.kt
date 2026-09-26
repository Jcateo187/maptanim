package com.maptanim.app.ui.screens.home

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.maptanim.app.domain.model.FarmTask
import com.maptanim.app.navigation.BottomNavItem
import com.maptanim.app.navigation.Routes
import com.maptanim.app.renderer.model.PlotRenderData
import com.maptanim.app.ui.components.avatar.ProfileAvatar
import com.maptanim.app.ui.theme.*

// ─── Monitoring Dashboard Color Palette ─────────────────────────────────────
private val MonitoringBg = Color(0xFF10160F)            // Deep dark forest/charcoal background
private val MonitoringSurface = Color(0xFF131D15)       // Overlay container surface
private val MonitoringCard = Color(0xFF1B2317)          // Card container dark olive
private val MonitoringBorder = Color(0xFF2E4D3E)        // Deep forest card border
private val MonitoringGreenLight = Color(0xFF81C784)    // Accent bright green

/**
 * MainHomeScreen — Responsive farm dashboard styled with MapTanim's signature
 * Monitoring Dashboard dark forest color palette.
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
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

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
            MainBottomNavBar(
                selectedRoute = Routes.HOME,
                onNavigate = { route ->
                    if (route != Routes.HOME) {
                        navController.navigate(route)
                    }
                }
            )
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
                // ─── Landscape 2-Column Responsive Layout ─────────────────
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Left Column: Brand Header, Farm Overview, Farm Attention, Farm Insight
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        BrandHeader(
                            nickname = uiState.nickname,
                            avatarAssetPath = uiState.avatarAssetPath,
                            notificationCount = uiState.notificationCount,
                            onProfileClick = { navController.navigate(Routes.PROFILE) },
                            onNotificationClick = { navController.navigate(Routes.NOTIFICATIONS) }
                        )

                        FarmOverviewCard(
                            farmName = uiState.activeFarm?.farmName?.ifBlank { "SAN ISIDRO FARM" } ?: "SAN ISIDRO FARM",
                            bedsCount = uiState.plots.size.takeIf { it > 0 } ?: 3,
                            plantingsCount = uiState.plots.count { !it.cropName.isNullOrBlank() }.takeIf { it > 0 } ?: 8,
                            onViewFarm = { navController.navigate(Routes.FARMS) }
                        )

                        FarmAttentionCard(
                            cropLabel = "Pechay #1",
                            attentionMessage = "Pest monitoring recommended",
                            onReview = { navController.navigate(Routes.FARMS) }
                        )

                        FarmInsightCard(
                            cropLabel = "Tomato #1",
                            insightTopic = "Flowering-stage care",
                            onViewRecommendation = { navController.navigate(Routes.VEGETABLES) }
                        )
                    }

                    // Right Column: Today's Tasks, My Crops
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        TodaysTasksCard(
                            tasks = uiState.todayTasks,
                            onToggleTask = { taskId -> homeViewModel.completeTask(taskId) },
                            onViewAllTasks = { navController.navigate(Routes.CALENDAR) }
                        )

                        MyCropsSection(
                            plots = uiState.plots,
                            onCropClick = { navController.navigate(Routes.VEGETABLES) }
                        )

                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }
            } else {
                // ─── Portrait Single-Column Layout (Exact User Layout) ───────
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    BrandHeader(
                        nickname = uiState.nickname,
                        avatarAssetPath = uiState.avatarAssetPath,
                        notificationCount = uiState.notificationCount,
                        onProfileClick = { navController.navigate(Routes.PROFILE) },
                        onNotificationClick = { navController.navigate(Routes.NOTIFICATIONS) }
                    )

                    // 1. SAN ISIDRO FARM
                    FarmOverviewCard(
                        farmName = uiState.activeFarm?.farmName?.ifBlank { "SAN ISIDRO FARM" } ?: "SAN ISIDRO FARM",
                        bedsCount = uiState.plots.size.takeIf { it > 0 } ?: 3,
                        plantingsCount = uiState.plots.count { !it.cropName.isNullOrBlank() }.takeIf { it > 0 } ?: 8,
                        onViewFarm = { navController.navigate(Routes.FARMS) }
                    )

                    // 2. ⚠ FARM ATTENTION
                    FarmAttentionCard(
                        cropLabel = "Pechay #1",
                        attentionMessage = "Pest monitoring recommended (DA-BPI Advisory)",
                        onReview = { navController.navigate(Routes.dssRoute(uiState.activeFarm?.id)) }
                    )

                    // 3. TODAY'S TASKS · 3
                    TodaysTasksCard(
                        tasks = uiState.todayTasks,
                        onToggleTask = { taskId -> homeViewModel.completeTask(taskId) },
                        onViewAllTasks = { navController.navigate(Routes.CALENDAR) }
                    )

                    // 4. MY CROPS
                    MyCropsSection(
                        plots = uiState.plots,
                        onCropClick = { navController.navigate(Routes.VEGETABLES) }
                    )

                    // 5. FARM INSIGHT
                    FarmInsightCard(
                        cropLabel = "Tomato #1",
                        insightTopic = "Flowering-stage care (DSS Guidance)",
                        onViewRecommendation = { navController.navigate(Routes.dssRoute(uiState.activeFarm?.id)) }
                    )

                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }
    }
}

// ─── 1. Brand Header (Profile Left, Logo Center, Notification Right) ─────────
@Composable
private fun BrandHeader(
    nickname: String,
    avatarAssetPath: String?,
    notificationCount: Int,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TOP-LEFT: Profile Avatar + Nickname Pill
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable { onProfileClick() }
                .padding(4.dp)
        ) {
            ProfileAvatar(
                avatarAssetPath = avatarAssetPath ?: "Avatar/Male_Avatar.png",
                size = 38.dp,
                borderWidth = 2.dp,
                borderColor = ForestGreen,
                onClick = onProfileClick
            )
            Column {
                Text(
                    text = nickname.ifBlank { "Farmer" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Profile",
                    fontSize = 11.sp,
                    color = MonitoringGreenLight,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // CENTER: Brand Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = "Logo Leaf",
                tint = ForestGreen,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "MapTanim",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = White,
                letterSpacing = (-0.3).sp
            )
        }

        // TOP-RIGHT: Notification Bell with Badge
        IconButton(
            onClick = onNotificationClick,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(MonitoringCard)
                .border(1.dp, MonitoringBorder, CircleShape)
        ) {
            BadgedBox(
                badge = {
                    if (notificationCount > 0) {
                        Badge(
                            containerColor = Danger,
                            contentColor = White
                        ) {
                            Text(
                                text = if (notificationCount > 99) "99+" else notificationCount.toString(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = if (notificationCount > 0) Sunlight else White.copy(alpha = 0.85f),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

// ─── 2. Farm Overview Card (SAN ISIDRO FARM) ────────────────────────────────
@Composable
private fun FarmOverviewCard(
    farmName: String,
    bedsCount: Int,
    plantingsCount: Int,
    onViewFarm: () -> Unit
) {
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
            Text(
                text = farmName.uppercase(),
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = White,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "$bedsCount beds · $plantingsCount plantings",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = White.copy(alpha = 0.85f)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    onClick = onViewFarm,
                    color = Color.Transparent,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "VIEW FARM",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MonitoringGreenLight,
                            letterSpacing = 0.5.sp
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "View Farm",
                            tint = MonitoringGreenLight,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─── 3. Farm Attention Card (⚠ FARM ATTENTION) ──────────────────────────────
@Composable
private fun FarmAttentionCard(
    cropLabel: String,
    attentionMessage: String,
    onReview: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1E1A11),
        border = BorderStroke(1.2.dp, Color(0xFFFFB300).copy(alpha = 0.7f)),
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
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = "FARM ATTENTION",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFB300),
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = cropLabel,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )
            Text(
                text = attentionMessage,
                fontSize = 13.sp,
                color = White.copy(alpha = 0.85f),
                modifier = Modifier.padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    onClick = onReview,
                    color = Color.Transparent,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "REVIEW",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFB300),
                            letterSpacing = 0.5.sp
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Review",
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─── 4. Today's Tasks Card (TODAY'S TASKS · 3) ──────────────────────────────
@Composable
private fun TodaysTasksCard(
    tasks: List<FarmTask>,
    onToggleTask: (String) -> Unit,
    onViewAllTasks: () -> Unit
) {
    val displayTasks = remember(tasks) {
        if (tasks.isNotEmpty()) {
            tasks.take(3).map { it.title to it.id }
        } else {
            listOf(
                "Check Tomato #1" to "default_task_1",
                "Monitor Eggplant #1" to "default_task_2",
                "Inspect Pechay #1" to "default_task_3"
            )
        }
    }
    val taskCount = if (tasks.isNotEmpty()) tasks.size else 3

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
            Text(
                text = "TODAY'S TASKS · $taskCount",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = White.copy(alpha = 0.9f),
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            displayTasks.forEach { (title, id) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onToggleTask(id) }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .border(1.8.dp, MonitoringGreenLight.copy(alpha = 0.8f), CircleShape)
                    )
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = White.copy(alpha = 0.9f)
                    )
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
    val name: String,
    val stage: String,
    val daysText: String
)

@Composable
private fun MyCropsSection(
    plots: List<PlotRenderData>,
    onCropClick: () -> Unit
) {
    val defaultCrops = listOf(
        MyCropSummaryItem("Tomato", "Flower.", "12 days"),
        MyCropSummaryItem("Eggplant", "Veget.", "28 days"),
        MyCropSummaryItem("Pechay", "Growing", "18 days")
    )

    val displayCrops = remember(plots) {
        val plantedPlots = plots.filter { !it.cropName.isNullOrBlank() }
        if (plantedPlots.isNotEmpty()) {
            plantedPlots.take(3).map { p ->
                val stageLabel = when (p.growthStage) {
                    1 -> "Sprout"
                    2 -> "Seedl."
                    3 -> "Veget."
                    4 -> "Flower."
                    else -> "Growing"
                }
                val daysLeft = (p.daysToHarvest - p.daysPlanted).coerceAtLeast(0)
                MyCropSummaryItem(
                    name = p.cropName ?: "Crop",
                    stage = stageLabel,
                    daysText = "$daysLeft days"
                )
            }
        } else {
            defaultCrops
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "MY CROPS",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = White.copy(alpha = 0.9f),
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            displayCrops.forEach { cropItem ->
                Surface(
                    onClick = onCropClick,
                    shape = RoundedCornerShape(14.dp),
                    color = MonitoringCard,
                    border = BorderStroke(1.dp, MonitoringBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = cropItem.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = White,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = cropItem.stage,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MonitoringGreenLight
                        )
                        Text(
                            text = cropItem.daysText,
                            fontSize = 11.sp,
                            color = White.copy(alpha = 0.65f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
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
    onViewRecommendation: () -> Unit
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
                        text = "What needs attention next?",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MonitoringGreenLight
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = cropLabel,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Text(
                    text = insightTopic,
                    fontSize = 13.sp,
                    color = White.copy(alpha = 0.85f),
                    modifier = Modifier.padding(top = 2.dp)
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
                                text = "VIEW RECOMMENDATION",
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
