package com.maptanim.app.ui.screens.home

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.maptanim.app.domain.model.TaskType
import com.maptanim.app.navigation.Routes
import com.maptanim.app.ui.components.avatar.ProfileAvatar
import com.maptanim.app.ui.theme.*

// ─── Monitoring Dashboard Color Palette ─────────────────────────────────────
private val MonitoringBg = Color(0xFF10160F)            // Deep dark forest/charcoal background
private val MonitoringSurface = Color(0xFF131D15)       // Overlay container surface
private val MonitoringCard = Color(0xFF1B2317)          // Card container dark olive
private val MonitoringCardElevated = Color(0xFF2A3424)  // Elevated / input surface
private val MonitoringBorder = Color(0xFF2E4D3E)        // Deep forest card border
private val MonitoringGreenLight = Color(0xFF81C784)    // Accent bright green
private val MonitoringAmber = Color(0xFFFFB300)         // Amber highlight

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
    var searchQuery by remember { mutableStateOf("") }
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
                    // Left Column: Header, Farm Banner, Search, Metrics
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
                        ScenicFarmBanner()
                        SearchBar(query = searchQuery, onQueryChange = { searchQuery = it })
                        MetricsRow(uiState = uiState)
                    }

                    // Right Column: Dashboard Pill, Tasks, Quick Links
                    Column(
                        modifier = Modifier
                            .weight(1.2f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        DashboardTitleBanner()
                        TodaysTasksSection(
                            tasks = uiState.todayTasks,
                            searchQuery = searchQuery,
                            onToggleTask = { taskId -> homeViewModel.completeTask(taskId) }
                        )
                        DashboardQuickLinks(navController = navController)
                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }
            } else {
                // ─── Portrait Single-Column Feed Layout (Matches Reference) ─
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    BrandHeader(
                        nickname = uiState.nickname,
                        avatarAssetPath = uiState.avatarAssetPath,
                        notificationCount = uiState.notificationCount,
                        onProfileClick = { navController.navigate(Routes.PROFILE) },
                        onNotificationClick = { navController.navigate(Routes.NOTIFICATIONS) }
                    )
                    ScenicFarmBanner()
                    SearchBar(query = searchQuery, onQueryChange = { searchQuery = it })
                    DashboardTitleBanner()
                    MetricsRow(uiState = uiState)
                    TodaysTasksSection(
                        tasks = uiState.todayTasks,
                        searchQuery = searchQuery,
                        onToggleTask = { taskId -> homeViewModel.completeTask(taskId) }
                    )
                    DashboardQuickLinks(navController = navController)
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

// ─── 2. Scenic Farm Illustration Banner (Monitoring Dark Scenery) ───────────
@Composable
private fun ScenicFarmBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E2B1B),
                        Color(0xFF141F12)
                    )
                )
            )
            .border(1.dp, MonitoringBorder, RoundedCornerShape(20.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Rolling Hill 1 (Back)
            val hillBack = Path().apply {
                moveTo(0f, h * 0.70f)
                cubicTo(w * 0.35f, h * 0.45f, w * 0.70f, h * 0.65f, w, h * 0.50f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(path = hillBack, color = Color(0xFF283A24))

            // Rolling Hill 2 (Front)
            val hillFront = Path().apply {
                moveTo(0f, h * 0.80f)
                cubicTo(w * 0.25f, h * 0.60f, w * 0.65f, h * 0.75f, w, h * 0.65f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(path = hillFront, color = Color(0xFF1D2E1A))

            // Farm Rows (Pastoral lines on hill)
            val rows = Path().apply {
                moveTo(w * 0.45f, h * 0.75f)
                lineTo(w * 0.30f, h)
                moveTo(w * 0.55f, h * 0.75f)
                lineTo(w * 0.45f, h)
                moveTo(w * 0.65f, h * 0.75f)
                lineTo(w * 0.60f, h)
                moveTo(w * 0.75f, h * 0.75f)
                lineTo(w * 0.75f, h)
            }
            drawPath(
                path = rows,
                color = ForestGreen.copy(alpha = 0.5f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
            )

            // Warm Moon/Sun in the sky
            drawCircle(
                color = Sunlight.copy(alpha = 0.85f),
                radius = 22f,
                center = Offset(w * 0.82f, h * 0.28f)
            )

            // Cozy Farmhouse (Right)
            val houseLeft = w * 0.75f
            val houseTop = h * 0.45f
            val houseW = 46f
            val houseH = 32f
            drawRect(
                color = MonitoringCardElevated,
                topLeft = Offset(houseLeft, houseTop),
                size = Size(houseW, houseH)
            )
            // Roof (Warm Amber/Danger tone)
            val roof = Path().apply {
                moveTo(houseLeft - 6f, houseTop)
                lineTo(houseLeft + houseW / 2f, houseTop - 18f)
                lineTo(houseLeft + houseW + 6f, houseTop)
                close()
            }
            drawPath(path = roof, color = Danger)

            // Tree beside house
            drawCircle(
                color = ForestGreen,
                radius = 15f,
                center = Offset(houseLeft - 13f, houseTop + 6f)
            )
            drawRect(
                color = Color(0xFF4E342E),
                topLeft = Offset(houseLeft - 15f, houseTop + 14f),
                size = Size(4f, 15f)
            )

            // Tractor (Left)
            val tracX = w * 0.16f
            val tracY = h * 0.68f
            // Big rear wheel
            drawCircle(color = Color(0xFF111710), radius = 13f, center = Offset(tracX, tracY))
            drawCircle(color = Sunlight, radius = 5f, center = Offset(tracX, tracY))
            // Small front wheel
            drawCircle(color = Color(0xFF111710), radius = 7f, center = Offset(tracX + 26f, tracY + 6f))
            drawCircle(color = Sunlight, radius = 2.5f, center = Offset(tracX + 26f, tracY + 6f))
            // Tractor chassis & cabin
            drawRect(
                color = ForestGreen,
                topLeft = Offset(tracX - 10f, tracY - 16f),
                size = Size(34f, 15f)
            )
            drawRect(
                color = Color(0xFF37474F).copy(alpha = 0.8f),
                topLeft = Offset(tracX - 8f, tracY - 26f),
                size = Size(15f, 11f)
            )
        }
    }
}

// ─── 3. Search Bar (Monitoring Dark Surface) ────────────────────────────────
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MonitoringCard,
        border = BorderStroke(1.dp, MonitoringBorder),
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = ForestGreen,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = "Search Any Name / Crops / Tasks...",
                        color = White.copy(alpha = 0.45f),
                        fontSize = 15.sp
                    )
                }
                androidx.compose.foundation.text.BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = White.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ─── 4. "Dashboard" Title Banner (Monitoring Badge Style) ───────────────────
@Composable
private fun DashboardTitleBanner() {
    Surface(
        color = MonitoringCard,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.2.dp, ForestGreen.copy(alpha = 0.8f)),
        shadowElevation = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Dashboard,
                contentDescription = null,
                tint = ForestGreen,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Dashboard",
                color = White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// ─── 5. Metrics Row (3 Cards with Monitoring Palette) ───────────────────────
@Composable
private fun MetricsRow(uiState: HomeUiState) {
    val totalPlots = uiState.plots.size
    val plantedCount = uiState.plots.count { !it.cropName.isNullOrEmpty() }
    val growingPct = if (totalPlots > 0) {
        String.format("%.1f%%", (plantedCount.toFloat() / totalPlots.toFloat()) * 100f)
    } else {
        "${plantedCount} Crops"
    }

    val totalTasks = uiState.todayTasks.size
    val completedTasks = uiState.todayTasks.count { it.isCompleted }
    val taskPct = if (totalTasks > 0) {
        String.format("%.0f%%", (completedTasks.toFloat() / totalTasks.toFloat()) * 100f)
    } else {
        "100%"
    }

    val readyHarvest = uiState.plots.count { it.isHarvestReady }
    val healthMetric = if (readyHarvest > 0) "${readyHarvest} Ready" else "Healthy"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MetricCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.LocalFlorist,
            iconTint = ForestGreen,
            label = "Crops Growing",
            value = growingPct
        )
        MetricCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Handyman,
            iconTint = MonitoringGreenLight,
            label = "Today's Tasks",
            value = if (totalTasks > 0) "$completedTasks/$totalTasks ($taskPct)" else "0 Tasks"
        )
        MetricCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.WaterDrop,
            iconTint = Sunlight,
            label = "Farm Status",
            value = healthMetric
        )
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    label: String,
    value: String
) {
    Surface(
        modifier = modifier.height(115.dp),
        shape = RoundedCornerShape(16.dp),
        color = MonitoringCard,
        border = BorderStroke(1.dp, MonitoringBorder),
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = White.copy(alpha = 0.70f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ─── 6. "Today's Tasks" Section with Interactive Switches ────────────────────
@Composable
private fun TodaysTasksSection(
    tasks: List<FarmTask>,
    searchQuery: String,
    onToggleTask: (String) -> Unit
) {
    val filteredTasks = remember(tasks, searchQuery) {
        if (searchQuery.isBlank()) tasks
        else tasks.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            (it.subLabel?.contains(searchQuery, ignoreCase = true) == true) ||
            it.plotLabel.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Today's Tasks",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = White
        )

        if (filteredTasks.isEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                color = MonitoringCard,
                border = BorderStroke(1.dp, MonitoringBorder),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Success,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "All caught up!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = White
                        )
                        Text(
                            text = "No pending farm tasks remaining for today.",
                            fontSize = 13.sp,
                            color = White.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                filteredTasks.forEach { task ->
                    TaskCard(
                        task = task,
                        onToggle = { onToggleTask(task.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: FarmTask,
    onToggle: () -> Unit
) {
    var isDone by remember(task.isCompleted) { mutableStateOf(task.isCompleted) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MonitoringCard,
        border = BorderStroke(
            1.dp,
            if (isDone) Success.copy(alpha = 0.5f) else MonitoringBorder
        ),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Task Icon
                val icon = when (task.taskType) {
                    TaskType.WATER -> Icons.Default.WaterDrop
                    TaskType.FERTILIZE -> Icons.Default.Eco
                    TaskType.WEED -> Icons.Default.ContentCut
                    TaskType.HARVEST -> Icons.Default.Agriculture
                    TaskType.PEST_ALERT, TaskType.APPLY_PESTICIDE -> Icons.Default.Warning
                    else -> Icons.Default.Handyman
                }
                val iconColor = when (task.taskType) {
                    TaskType.WATER -> SlateBlue
                    TaskType.FERTILIZE -> ForestGreen
                    TaskType.WEED -> MonitoringGreenLight
                    TaskType.HARVEST -> Sunlight
                    TaskType.PEST_ALERT, TaskType.APPLY_PESTICIDE -> Danger
                    else -> MonitoringGreenLight
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconColor.copy(alpha = 0.20f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (isDone) White.copy(alpha = 0.45f) else White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${task.plotLabel} • Due: ${task.dueDate.takeLast(5)}",
                        fontSize = 12.sp,
                        color = White.copy(alpha = 0.6f)
                    )
                }
            }

            // Interactive Toggle Switch
            Switch(
                checked = isDone,
                onCheckedChange = { checked ->
                    isDone = checked
                    onToggle()
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = White,
                    checkedTrackColor = ForestGreen,
                    uncheckedThumbColor = White.copy(alpha = 0.8f),
                    uncheckedTrackColor = MonitoringCardElevated,
                    uncheckedBorderColor = MonitoringBorder
                )
            )
        }
    }
}

// ─── 7. Dashboard Quick Links Section (Monitoring Cards) ────────────────────
@Composable
private fun DashboardQuickLinks(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Quick Access",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = White
        )

        // Row 1: Primary Hubs (Monitoring & Community)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HubCard(
                modifier = Modifier.weight(1f),
                title = "Monitoring",
                subtitle = "Crop Health & Timers",
                icon = Icons.Default.Assessment,
                iconTint = ForestGreen,
                onClick = { navController.navigate(Routes.FARMS) }
            )
            HubCard(
                modifier = Modifier.weight(1f),
                title = "Community Hub",
                subtitle = "Farmer Feed & Chat",
                icon = Icons.Default.Groups,
                iconTint = MonitoringGreenLight,
                onClick = { navController.navigate(Routes.COMMUNITY) }
            )
        }

        // Row 2: Secondary Tools (Calendar, Library, Edit Farm)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickLinkButton(
                modifier = Modifier.weight(1f),
                title = "Calendar",
                icon = Icons.Default.CalendarMonth,
                iconTint = ForestGreen,
                onClick = { navController.navigate(Routes.CALENDAR) }
            )
            QuickLinkButton(
                modifier = Modifier.weight(1f),
                title = "Crop Library",
                icon = Icons.Default.LocalFlorist,
                iconTint = MonitoringGreenLight,
                onClick = { navController.navigate(Routes.LIBRARY) }
            )
            QuickLinkButton(
                modifier = Modifier.weight(1f),
                title = "Edit Farm",
                icon = Icons.Default.Edit,
                iconTint = Sunlight,
                onClick = { navController.navigate(Routes.EDIT) }
            )
        }
    }
}

@Composable
private fun HubCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(82.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MonitoringCard,
        border = BorderStroke(1.dp, MonitoringBorder),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconTint.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = White.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun QuickLinkButton(
    modifier: Modifier = Modifier,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(82.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MonitoringCard,
        border = BorderStroke(1.dp, MonitoringBorder),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = White,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

// ─── 8. Bottom Navigation Bar (Monitoring Dark Surface) ─────────────────────
@Composable
private fun MainBottomNavBar(
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
            NavigationBarItem(
                selected = selectedRoute == Routes.HOME,
                onClick = { onNavigate(Routes.HOME) },
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ForestGreen,
                    selectedTextColor = ForestGreen,
                    unselectedIconColor = White.copy(alpha = 0.5f),
                    unselectedTextColor = White.copy(alpha = 0.5f),
                    indicatorColor = ForestGreen.copy(alpha = 0.25f)
                )
            )

            NavigationBarItem(
                selected = selectedRoute == Routes.FARMS,
                onClick = { onNavigate(Routes.FARMS) },
                icon = { Icon(Icons.Default.Assessment, contentDescription = "Monitoring") },
                label = { Text("Monitoring", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ForestGreen,
                    selectedTextColor = ForestGreen,
                    unselectedIconColor = White.copy(alpha = 0.5f),
                    unselectedTextColor = White.copy(alpha = 0.5f),
                    indicatorColor = ForestGreen.copy(alpha = 0.25f)
                )
            )

            NavigationBarItem(
                selected = selectedRoute == Routes.COMMUNITY,
                onClick = { onNavigate(Routes.COMMUNITY) },
                icon = { Icon(Icons.Default.Groups, contentDescription = "Community") },
                label = { Text("Community", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ForestGreen,
                    selectedTextColor = ForestGreen,
                    unselectedIconColor = White.copy(alpha = 0.5f),
                    unselectedTextColor = White.copy(alpha = 0.5f),
                    indicatorColor = ForestGreen.copy(alpha = 0.25f)
                )
            )

            NavigationBarItem(
                selected = selectedRoute == Routes.CALENDAR,
                onClick = { onNavigate(Routes.CALENDAR) },
                icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar") },
                label = { Text("Calendar", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ForestGreen,
                    selectedTextColor = ForestGreen,
                    unselectedIconColor = White.copy(alpha = 0.5f),
                    unselectedTextColor = White.copy(alpha = 0.5f),
                    indicatorColor = ForestGreen.copy(alpha = 0.25f)
                )
            )

            NavigationBarItem(
                selected = selectedRoute == Routes.LIBRARY,
                onClick = { onNavigate(Routes.LIBRARY) },
                icon = { Icon(Icons.Default.LocalFlorist, contentDescription = "Library") },
                label = { Text("Library", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
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
