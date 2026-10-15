package com.maptanim.app.features.farm.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maptanim.app.R
import com.maptanim.app.domain.model.ManagementStage
import com.maptanim.app.features.farm.components.BackyardCropAgroData
import com.maptanim.app.features.farm.schedule.CalendarScreenTab
import com.maptanim.app.features.farm.dialogs.CropCareDssDialog
import com.maptanim.app.features.farm.dialogs.CropScheduleCalendarScreen
import com.maptanim.app.features.farm.dialogs.SetPlantingDateDialog
import com.maptanim.app.data.repository.RepositoryProvider
import com.maptanim.app.features.farm.schedule.CheckUpRecorder
import com.maptanim.app.features.farm.schedule.CropScheduleStatus
import com.maptanim.app.features.farm.renderer.canvas.CropSvgRenderer
import com.maptanim.app.navigation.MainBottomNavBar
import com.maptanim.app.navigation.Routes
import com.maptanim.app.features.farm.schedule.resolveScheduleStatus
import com.maptanim.app.features.home.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ForestGreen = Color(0xFF1B5E20)
private val LushGreen = Color(0xFF2E7D32)
private val LightGreenBg = Color(0xFFE8F5E9)
private val DeepBlack = Color(0xFF111813)
private val Charcoal = Color(0xFF28302A)
private val MutedText = Color(0xFF555555)
private val CardBorderColor = Color(0xFFD0D8CC)
private val CardBg = Color(0xFFFFFFFF)
private val SurfaceBg = Color(0xFFF2F5F0)
private val AmberAlert = Color(0xFFE65100)

private val GardenImages = listOf(
    R.drawable.garden_card_1,
    R.drawable.garden_card_2,
    R.drawable.garden_card_3,
    R.drawable.garden_card_4,
    R.drawable.garden_card_5
)

enum class GrowingScreenTab(val label: String, val icon: ImageVector) {
    GROWING("Growing", Icons.Default.Spa),
    PLANNING("Planning", Icons.Default.Architecture)
}

data class GrowingGardenItem(
    val id: String,
    val name: String,
    val allCrops: List<GrowingVegetableItem>,
    val growingCrops: List<GrowingVegetableItem>,
    val planningCrops: List<GrowingVegetableItem>
)

data class GrowingVegetableItem(
    val plotId: String,
    val cropName: String,
    val cropVariety: String?,
    val daysPlanted: Int,
    val growthStage: Int,
    val stage: ManagementStage,
    val plantedDateStr: String?,
    val isCalendarStarted: Boolean,
    val isFutureDate: Boolean,
    val targetPlantingDate: LocalDate?,
    val plotLabel: String,
    val scheduleStatus: CropScheduleStatus = CropScheduleStatus.NoSchedule
)

private data class CalendarTarget(
    val cropName: String,
    val varietyName: String?,
    val gardenLabel: String,
    val plotId: String,
    val plantedDateStr: String,
    val isPlanningMode: Boolean = false
)

private data class CheckUpTarget(
    val cropName: String,
    val varietyName: String,
    val gardenLabel: String,
    val plotId: String,
    val stage: ManagementStage,
    val scheduleStatus: CropScheduleStatus? = null,
    val plantedDateStr: String? = null
)

/**
 * Growing Screen — Dedicated screen for tracking garden crops:
 * 1. Cards at the top for Garden name (switch between gardens)
 * 2. Two distinct tabs:
 *    - "Growing": crops that have already started a calendar planting schedule on or before today.
 *    - "Planning": crops recorded in 2D Canvas that have not yet started the calendar or are scheduled for other/future days.
 * 3. Tapping a vegetable card opens the Calendar Screen defaulted to the Today's Task tab.
 * 4. Tapping "Check Up" button opens the Crop Check Up (CropCareDssDialog).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrowingScreen(
    navController: NavController,
    initialGardenId: String? = null,
    homeViewModel: HomeViewModel = viewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()

    val allLogsFlow = remember { RepositoryProvider.cropLogRepository.observeAllLogs() }
    val allLogs by allLogsFlow.collectAsState(initial = emptyList())

    var activeCalendarTarget by remember { mutableStateOf<CalendarTarget?>(null) }
    var activeCheckUpTarget by remember { mutableStateOf<CheckUpTarget?>(null) }
    var showSetPlantingDateForTarget by remember { mutableStateOf<CalendarTarget?>(null) }
    var activeGrowingTab by remember { mutableStateOf(GrowingScreenTab.GROWING) }

    val today = remember { LocalDate.now() }

    // Build garden items and categorize crops into Growing vs Planning
    val gardens = remember(uiState.plots, today, uiState.activeFarm?.farmName) {
        val plots = uiState.plots

        if (plots.isEmpty()) {
            listOf(
                GrowingGardenItem(
                    id = "garden_default",
                    name = uiState.activeFarm?.farmName?.takeIf { it.isNotBlank() } ?: "Aking Hardin",
                    allCrops = emptyList(),
                    growingCrops = emptyList(),
                    planningCrops = emptyList()
                )
            )
        } else {
            val grouped = plots.groupBy { it.plotLabel.substringBefore(" (Z") }
            val individualGardens = grouped.map { (gardenLabel, plotList) ->
                val allVegetables = plotList.mapNotNull { p ->
                    val name = p.cropName?.trim()
                    if (!name.isNullOrBlank() && !name.equals("Bed", ignoreCase = true) && !name.equals("Garden", ignoreCase = true)) {
                        val parsedPlantDate = try {
                            if (!p.plantedDate.isNullOrBlank()) LocalDate.parse(p.plantedDate.take(10)) else null
                        } catch (_: Exception) { null }

                        val schedStatus = resolveScheduleStatus(
                            plantedDate = p.plantedDate,
                            cropName = name,
                            isMonitoringStarted = p.isMonitoringStarted && !p.plantedDate.isNullOrBlank(),
                            today = today
                        )
                        val isFuture = schedStatus is CropScheduleStatus.Upcoming
                        val isGrowingOnThisDay = schedStatus.canCheckUp

                        GrowingVegetableItem(
                            plotId = p.id,
                            cropName = name,
                            cropVariety = p.cropVariety,
                            daysPlanted = p.daysPlanted,
                            growthStage = p.growthStage,
                            stage = schedStatus.stageOrNull ?: ManagementStage.fromIndex(p.growthStage - 1),
                            plantedDateStr = p.plantedDate,
                            isCalendarStarted = isGrowingOnThisDay,
                            isFutureDate = isFuture,
                            targetPlantingDate = parsedPlantDate,
                            plotLabel = p.plotLabel,
                            scheduleStatus = schedStatus
                        )
                    } else null
                }

                val growing = allVegetables.filter { it.isCalendarStarted }
                val planning = allVegetables.filter { !it.isCalendarStarted }

                GrowingGardenItem(
                    id = plotList.first().id.substringBefore("_zone_"),
                    name = gardenLabel,
                    allCrops = allVegetables,
                    growingCrops = growing,
                    planningCrops = planning
                )
            }

            if (individualGardens.isNotEmpty()) {
                val allCropsList = individualGardens.flatMap { it.allCrops }
                val allGrowingList = individualGardens.flatMap { it.growingCrops }
                val allPlanningList = individualGardens.flatMap { it.planningCrops }
                val allGardensItem = GrowingGardenItem(
                    id = "all_gardens",
                    name = "Lahat (All)",
                    allCrops = allCropsList,
                    growingCrops = allGrowingList,
                    planningCrops = allPlanningList
                )
                listOf(allGardensItem) + individualGardens
            } else {
                individualGardens
            }
        }
    }

    // Active selected garden (defaults to initialGardenId, or "all_gardens" if available, or first garden)
    var selectedGardenId by remember(gardens, initialGardenId) {
        mutableStateOf(
            gardens.firstOrNull { it.id == initialGardenId }?.id
                ?: gardens.firstOrNull { it.id == "all_gardens" }?.id
                ?: gardens.firstOrNull { it.growingCrops.isNotEmpty() || it.planningCrops.isNotEmpty() }?.id
                ?: gardens.firstOrNull()?.id
                ?: ""
        )
    }

    val selectedGarden = remember(gardens, selectedGardenId) {
        gardens.firstOrNull { it.id == selectedGardenId } ?: gardens.firstOrNull()
    }

    Scaffold(
        topBar = {
            Surface(
                color = CardBg,
                border = BorderStroke(1.dp, CardBorderColor),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Bumalik sa Home",
                            tint = ForestGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Growing",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DeepBlack
                        )
                        val subtitleText = if (selectedGarden?.id == "all_gardens") {
                            "Lahat ng Pananim • ${gardens.size - 1} Hardin"
                        } else {
                            "${selectedGarden?.name ?: "Hardin"} • ${gardens.size} Hardin"
                        }
                        Text(
                            text = subtitleText,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MutedText
                        )
                    }
                }
            }
        },
        bottomBar = {
            MainBottomNavBar(
                selectedRoute = Routes.GROWING,
                onNavigate = { route ->
                    if (route == Routes.HOME) {
                        val popped = navController.popBackStack(Routes.HOME, inclusive = false)
                        if (!popped) {
                            navController.navigate(Routes.HOME) {
                                popUpTo(0) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    } else if (route != Routes.GROWING) {
                        navController.navigate(route) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        },
        containerColor = SurfaceBg
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
        ) {
            // ── 1. Top Section: Cards for Garden Name ────────────────────────
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "MGA HARDIN (GARDENS)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.8.sp,
                        color = MutedText,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        itemsIndexed(gardens, key = { _, garden -> garden.id }) { index, garden ->
                            val isSelected = garden.id == selectedGarden?.id
                            val gardenImageRes = GardenImages[(index % GardenImages.size + GardenImages.size) % GardenImages.size]
                            val badgeCount = if (activeGrowingTab == GrowingScreenTab.GROWING) {
                                garden.growingCrops.size
                            } else {
                                garden.planningCrops.size
                            }

                            Surface(
                                onClick = {
                                    selectedGardenId = garden.id
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                shadowElevation = if (isSelected) 4.dp else 1.5.dp,
                                border = BorderStroke(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) LushGreen else Color(0xFFE0E0E0)
                                ),
                                modifier = Modifier
                                    .width(98.dp)
                                    .height(108.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(12.dp))
                                ) {
                                    // Background garden photo
                                    Image(
                                        painter = painterResource(id = gardenImageRes),
                                        contentDescription = garden.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    // Badge with number of vegetables for current active tab
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSelected) ForestGreen else Color.White,
                                        shadowElevation = 2.dp,
                                        border = BorderStroke(0.5.dp, Color(0xFFE0E0E0)),
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(5.dp)
                                    ) {
                                        Text(
                                            text = "$badgeCount",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isSelected) Color.White else DeepBlack,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }

                                    // Garden name label at bottom
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .fillMaxWidth()
                                            .background(if (isSelected) LightGreenBg else Color.White)
                                            .padding(horizontal = 6.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = garden.name,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) ForestGreen else DeepBlack,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── 2. Segmented Navigation Tabs: Growing vs Planning ────────────
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = CardBg,
                    border = BorderStroke(1.2.dp, CardBorderColor),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GrowingScreenTab.values().forEach { tab ->
                            val isSelected = activeGrowingTab == tab
                            val tabBgColor by animateColorAsState(
                                targetValue = if (isSelected) ForestGreen else Color.Transparent,
                                label = "growingTabBg"
                            )
                            val iconColor by animateColorAsState(
                                targetValue = if (isSelected) Color.White else ForestGreen,
                                label = "growingTabIcon"
                            )
                            val textColor by animateColorAsState(
                                targetValue = if (isSelected) Color.White else DeepBlack,
                                label = "growingTabText"
                            )
                            val tabCount = if (tab == GrowingScreenTab.GROWING) {
                                selectedGarden?.growingCrops?.size ?: 0
                            } else {
                                selectedGarden?.planningCrops?.size ?: 0
                            }

                            Surface(
                                onClick = { activeGrowingTab = tab },
                                shape = RoundedCornerShape(10.dp),
                                color = tabBgColor,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = null,
                                        tint = iconColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${tab.label} ($tabCount)",
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                        color = textColor,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── 3. Section Header for Vegetables List ────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val count = if (activeGrowingTab == GrowingScreenTab.GROWING) {
                        selectedGarden?.growingCrops?.size ?: 0
                    } else {
                        selectedGarden?.planningCrops?.size ?: 0
                    }
                    val gardenTitle = if (selectedGarden?.id == "all_gardens") "Lahat ng Hardin" else (selectedGarden?.name ?: "Hardin")
                    Text(
                        text = if (activeGrowingTab == GrowingScreenTab.GROWING)
                            "MGA NAKATANIM NA GULAY ($count)"
                        else
                            "MGA NAKAPLANO SA CANVAS ($count)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.8.sp,
                        color = MutedText
                    )
                    Text(
                        text = gardenTitle,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = ForestGreen
                    )
                }
            }

            // ── 4. Crops List rendering according to active tab ──────────────
            val displayedCrops = if (activeGrowingTab == GrowingScreenTab.GROWING) {
                selectedGarden?.growingCrops ?: emptyList()
            } else {
                selectedGarden?.planningCrops ?: emptyList()
            }

            if (displayedCrops.isEmpty()) {
                item {
                    val isAllGardens = selectedGarden?.id == "all_gardens"
                    val currentGardenName = selectedGarden?.name ?: "Hardin"
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CardBg,
                        border = BorderStroke(1.dp, CardBorderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(26.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = if (activeGrowingTab == GrowingScreenTab.GROWING)
                                    Icons.Default.Spa
                                else
                                    Icons.Default.Architecture,
                                contentDescription = null,
                                tint = LushGreen,
                                modifier = Modifier.size(38.dp)
                            )
                            Text(
                                text = if (activeGrowingTab == GrowingScreenTab.GROWING)
                                    "Walang Kasalukuyang Lumalaki"
                                else
                                    "Walang Gulay sa Pagpaplano",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepBlack
                            )
                            Text(
                                text = if (isAllGardens) {
                                    if (activeGrowingTab == GrowingScreenTab.GROWING)
                                        "Walang gulay na kasalukuyang lumalaki sa anumang hardin. Maaari kang pumunta sa Hardin Screen upang magdagdag o mamahala ng iyong mga hardin."
                                    else
                                        "Walang nakaplanong pananim sa anumang hardin. Pumunta sa Hardin Screen upang mag-draft ng bagong pananim sa Farm Canvas."
                                } else {
                                    if (activeGrowingTab == GrowingScreenTab.GROWING)
                                        "Walang gulay na kasalukuyang lumalaki sa $currentGardenName. Buksan ang 2D Canvas upang magtanim o mag-ayos ng mga gulay."
                                    else
                                        "Walang nakaplanong gulay sa $currentGardenName. Buksan ang 2D Canvas upang maglagay ng bagong pananim."
                                },
                                fontSize = 12.sp,
                                color = MutedText,
                                lineHeight = 17.sp,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = {
                                    if (isAllGardens) {
                                        navController.navigate(Routes.FARM)
                                    } else {
                                        navController.navigate(Routes.farmRoute(selectedGarden?.id))
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (isAllGardens) {
                                        "Pumunta sa Hardin Screen"
                                    } else {
                                        "Pumunta sa Farm Canvas 2D ($currentGardenName)"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                items(displayedCrops, key = { "${it.plotId}_${it.cropName}_${it.plotLabel}" }) { crop ->
                    val profile = remember(crop.cropName) { BackyardCropAgroData.getProfile(crop.cropName) }
                    val totalDays = profile.baseDaysToHarvest

                    val plantedDate = remember(crop.plantedDateStr) {
                        try {
                            if (!crop.plantedDateStr.isNullOrBlank()) {
                                LocalDate.parse(crop.plantedDateStr.take(10))
                            } else {
                                LocalDate.now()
                            }
                        } catch (_: Exception) {
                            LocalDate.now()
                        }
                    }

                    val expectedHarvestDate = plantedDate.plusDays(totalDays.toLong())
                    val formattedHarvestDate = remember(expectedHarvestDate) {
                        try {
                            expectedHarvestDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.forLanguageTag("fil-PH")))
                        } catch (_: Exception) {
                            expectedHarvestDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH))
                        }
                    }

                    val formattedPlantDate = remember(crop.targetPlantingDate) {
                        crop.targetPlantingDate?.let { date ->
                            try {
                                date.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.forLanguageTag("fil-PH")))
                            } catch (_: Exception) {
                                date.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH))
                            }
                        } ?: "Hindi pa itinakda"
                    }

                    val cropGardenLabel = remember(crop.plotLabel, selectedGarden?.name) {
                        crop.plotLabel.substringBefore(" (Z").takeIf { it.isNotBlank() } ?: (selectedGarden?.name ?: "Garden")
                    }

                    Surface(
                        onClick = {
                            // User clicks Card of vegetable -> go to Calendar screen
                            activeCalendarTarget = CalendarTarget(
                                cropName = crop.cropName,
                                varietyName = crop.cropVariety,
                                gardenLabel = cropGardenLabel,
                                plotId = crop.plotId,
                                plantedDateStr = crop.plantedDateStr ?: LocalDate.now().toString(),
                                isPlanningMode = (activeGrowingTab == GrowingScreenTab.PLANNING) || !crop.isCalendarStarted
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = CardBg,
                        border = BorderStroke(1.dp, CardBorderColor),
                        shadowElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Left: Vegetable SVG + Details
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = LightGreenBg,
                                    border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.35f)),
                                    modifier = Modifier.size(54.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Canvas(modifier = Modifier.size(38.dp)) {
                                            CropSvgRenderer.drawCropSvg(
                                                drawScope = this,
                                                cropName = crop.cropName,
                                                center = Offset(size.width / 2f, size.height / 2f),
                                                sizePx = size.width * 0.95f
                                            )
                                        }
                                    }
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = crop.cropName,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DeepBlack
                                        )
                                        if (selectedGarden?.id == "all_gardens") {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = SurfaceBg,
                                                border = BorderStroke(0.6.dp, CardBorderColor)
                                            ) {
                                                Text(
                                                    text = cropGardenLabel,
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = ForestGreen,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }

                                    if (activeGrowingTab == GrowingScreenTab.GROWING) {
                                        // Growth stage badge for Growing
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = LightGreenBg
                                        ) {
                                            Text(
                                                text = "Yugto ${crop.growthStage}: ${crop.stage.label}",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ForestGreen,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        // Expected harvest date
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Event,
                                                contentDescription = null,
                                                tint = MutedText,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = "Expected Harvest: $formattedHarvestDate",
                                                fontSize = 11.sp,
                                                color = Charcoal,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        // Last Check-up info for Growing tab
                                        val latestLog = remember(allLogs, crop.cropName, crop.plotId) {
                                            allLogs.firstOrNull {
                                                it.cropName.equals(crop.cropName, ignoreCase = true) || it.cropPlantingId == crop.plotId
                                            }
                                        }
                                        if (latestLog != null) {
                                            val summary = remember(latestLog) { CheckUpRecorder.summarize(latestLog) }
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = summary.severity.color.copy(alpha = 0.09f),
                                                border = BorderStroke(0.6.dp, summary.severity.color.copy(alpha = 0.4f))
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(5.dp)
                                                            .clip(CircleShape)
                                                            .background(summary.severity.color)
                                                    )
                                                    Text(
                                                        text = "Huling check-up: ${latestLog.date} • ${summary.severity.label}",
                                                        fontSize = 10.5.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = summary.severity.color
                                                    )
                                                }
                                            }
                                        } else {
                                            Text(
                                                text = "Wala pang check-up ngayon",
                                                fontSize = 10.5.sp,
                                                color = MutedText.copy(alpha = 0.7f),
                                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                            )
                                        }
                                    } else {
                                        // Planning Tab: see ONLY svg, vegetable name, variety, started in this date and not started
                                        val displayVariety = crop.cropVariety?.takeIf { it.isNotBlank() }
                                            ?: "Lokal / Karaniwan"

                                        Text(
                                            text = "Variety: $displayVariety",
                                            fontSize = 11.5.sp,
                                            color = Charcoal,
                                            fontWeight = FontWeight.Medium
                                        )

                                        if (crop.targetPlantingDate != null) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFFFFF3E0),
                                                border = BorderStroke(0.6.dp, AmberAlert.copy(alpha = 0.4f))
                                            ) {
                                                Text(
                                                    text = "Started in this date: $formattedPlantDate",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = AmberAlert,
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                                )
                                            }
                                        } else {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFFECEFF1),
                                                border = BorderStroke(0.6.dp, Color(0xFFB0BEC5))
                                            ) {
                                                Text(
                                                    text = "Not started",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF546E7A),
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Right side: Action Button (Check Up) only for Growing tab
                            if (activeGrowingTab == GrowingScreenTab.GROWING) {
                                FilledTonalButton(
                                    onClick = {
                                        // User clicks Check Up button -> go to Crop Check Up
                                        activeCheckUpTarget = CheckUpTarget(
                                            cropName = crop.cropName,
                                            varietyName = crop.cropVariety ?: "",
                                            gardenLabel = cropGardenLabel,
                                            plotId = crop.plotId,
                                            stage = crop.scheduleStatus.stageOrNull ?: crop.stage,
                                            scheduleStatus = crop.scheduleStatus,
                                            plantedDateStr = crop.plantedDateStr
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = LightGreenBg,
                                        contentColor = ForestGreen
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MedicalServices,
                                            contentDescription = null,
                                            tint = ForestGreen,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Text(
                                            text = "Check Up",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ForestGreen
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal CropScheduleCalendarScreen opened from Vegetable Card
    activeCalendarTarget?.let { target ->
        CropScheduleCalendarScreen(
            cropName = target.cropName,
            varietyName = target.varietyName,
            gardenLabel = target.gardenLabel,
            plotId = target.plotId,
            plantedDateStr = target.plantedDateStr,
            initialTab = if (target.isPlanningMode) CalendarScreenTab.CALENDAR else CalendarScreenTab.TODAYS_TASK,
            isPlanningMode = target.isPlanningMode,
            onStartSchedule = {
                homeViewModel.startPlantingSchedule(target.plotId, target.cropName, LocalDate.now())
                activeCalendarTarget = null
            },
            onNavigateToCanvas = { targetPlotId ->
                activeCalendarTarget = null
                navController.navigate(Routes.farmRoute(targetPlotId))
            },
            onDismiss = { activeCalendarTarget = null },
            onReschedule = {
                showSetPlantingDateForTarget = target
            }
        )
    }

    // Modal SetPlantingDateDialog opened from Reschedule action in Calendar
    showSetPlantingDateForTarget?.let { target ->
        SetPlantingDateDialog(
            cropName = target.cropName,
            varietyName = target.varietyName,
            gardenLabel = target.gardenLabel,
            initialDate = target.plantedDateStr,
            isReschedule = true,
            onDismiss = { showSetPlantingDateForTarget = null },
            onSaveDate = { chosenDate ->
                homeViewModel.startPlantingSchedule(target.plotId, target.cropName, LocalDate.parse(chosenDate.take(10)))
                showSetPlantingDateForTarget = null
                activeCalendarTarget = target.copy(plantedDateStr = chosenDate, isPlanningMode = false)
            }
        )
    }

    // Modal CropCareDssDialog opened from Check Up button
    activeCheckUpTarget?.let { checkUp ->
        CropCareDssDialog(
            cropName = checkUp.cropName,
            varietyName = checkUp.varietyName,
            gardenLabel = checkUp.gardenLabel,
            plotId = checkUp.plotId,
            currentStage = checkUp.stage,
            scheduleStatus = checkUp.scheduleStatus,
            onOpenSchedule = {
                activeCheckUpTarget = null
                activeCalendarTarget = CalendarTarget(
                    cropName = checkUp.cropName,
                    varietyName = checkUp.varietyName,
                    gardenLabel = checkUp.gardenLabel,
                    plotId = checkUp.plotId,
                    plantedDateStr = checkUp.plantedDateStr ?: "",
                    isPlanningMode = false
                )
            },
            onDismiss = { activeCheckUpTarget = null }
        )
    }
}
