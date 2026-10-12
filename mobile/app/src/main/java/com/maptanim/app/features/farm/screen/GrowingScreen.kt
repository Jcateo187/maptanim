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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maptanim.app.R
import com.maptanim.app.domain.model.ManagementStage
import com.maptanim.app.features.farm.components.BackyardCropAgroData
import com.maptanim.app.features.farm.dialogs.CalendarScreenTab
import com.maptanim.app.features.farm.dialogs.CropCareDssDialog
import com.maptanim.app.features.farm.dialogs.CropScheduleCalendarScreen
import com.maptanim.app.features.farm.renderer.canvas.CropSvgRenderer
import com.maptanim.app.features.home.viewmodel.HomeViewModel
import com.maptanim.app.navigation.Routes
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
    val plotLabel: String
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
    val stage: ManagementStage
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

    var activeCalendarTarget by remember { mutableStateOf<CalendarTarget?>(null) }
    var activeCheckUpTarget by remember { mutableStateOf<CheckUpTarget?>(null) }
    var activeGrowingTab by remember { mutableStateOf(GrowingScreenTab.GROWING) }

    val today = remember { LocalDate.now() }

    // Build garden items and categorize crops into Growing vs Planning
    val gardens = remember(uiState.plots, today) {
        val plots = uiState.plots

        if (plots.isEmpty()) {
            listOf(
                GrowingGardenItem(
                    id = "garden_default",
                    name = "Main Garden",
                    allCrops = emptyList(),
                    growingCrops = emptyList(),
                    planningCrops = emptyList()
                )
            )
        } else {
            val grouped = plots.groupBy { it.plotLabel.substringBefore(" (Z") }
            grouped.map { (gardenLabel, plotList) ->
                val allVegetables = plotList.mapNotNull { p ->
                    val name = p.cropName?.trim()
                    if (!name.isNullOrBlank() && !name.equals("Bed", ignoreCase = true)) {
                        val parsedPlantDate = try {
                            if (!p.plantedDate.isNullOrBlank()) LocalDate.parse(p.plantedDate.take(10)) else null
                        } catch (_: Exception) { null }

                        val isFuture = parsedPlantDate != null && parsedPlantDate.isAfter(today)
                        // Only crops that have started a schedule on or before this day belong in Growing
                        val isGrowingOnThisDay = p.isMonitoringStarted && parsedPlantDate != null && !isFuture

                        GrowingVegetableItem(
                            plotId = p.id,
                            cropName = name,
                            cropVariety = p.cropVariety,
                            daysPlanted = p.daysPlanted,
                            growthStage = p.growthStage,
                            stage = ManagementStage.fromIndex(p.growthStage - 1),
                            plantedDateStr = p.plantedDate,
                            isCalendarStarted = isGrowingOnThisDay,
                            isFutureDate = isFuture,
                            targetPlantingDate = parsedPlantDate,
                            plotLabel = p.plotLabel
                        )
                    } else null
                }

                val growing = allVegetables.filter { it.isCalendarStarted }
                val planning = allVegetables.filter { !it.isCalendarStarted }

                GrowingGardenItem(
                    id = plotList.first().id,
                    name = gardenLabel,
                    allCrops = allVegetables,
                    growingCrops = growing,
                    planningCrops = planning
                )
            }
        }
    }

    // Active selected garden (defaults to initialGardenId or the first garden)
    var selectedGardenId by remember(gardens, initialGardenId) {
        mutableStateOf(
            gardens.firstOrNull { it.id == initialGardenId }?.id
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
                        Text(
                            text = "${selectedGarden?.name ?: "Hardin"} • ${gardens.size} Hardin",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MutedText
                        )
                    }
                }
            }
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
                                onClick = { selectedGardenId = garden.id },
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
                        text = selectedGarden?.name ?: "Hardin",
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
                                text = if (activeGrowingTab == GrowingScreenTab.GROWING)
                                    "Walang gulay na nagsimula na ng kalendaryo sa hardin na ito ngayong araw. Maaari kang pumunta sa tab na 'Planning' upang simulan ang kalendaryo o magtanim sa Farm Canvas."
                                else
                                    "Lahat ng nakatanim sa hardin na ito ay aktibo nang lumalaki ngayong araw, o maaari kang mag-draft ng bagong pananim sa Farm Canvas 2D.",
                                fontSize = 12.sp,
                                color = MutedText,
                                lineHeight = 17.sp
                            )
                            Button(
                                onClick = { navController.navigate(Routes.FARM) },
                                colors = ButtonDefaults.buttonColors(containerColor = LushGreen),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Pumunta sa Farm Canvas", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                items(displayedCrops, key = { it.plotId }) { crop ->
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

                    Surface(
                        onClick = {
                            // User clicks Card of vegetable -> go to Calendar screen
                            activeCalendarTarget = CalendarTarget(
                                cropName = crop.cropName,
                                varietyName = crop.cropVariety,
                                gardenLabel = selectedGarden?.name ?: "Garden",
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
                                    Text(
                                        text = crop.cropName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepBlack
                                    )

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

                            // Right side: Action Button only for Growing tab
                            if (activeGrowingTab == GrowingScreenTab.GROWING) {
                                FilledTonalButton(
                                    onClick = {
                                        // User clicks Check Up button -> go to Crop Check Up
                                        activeCheckUpTarget = CheckUpTarget(
                                            cropName = crop.cropName,
                                            varietyName = crop.cropVariety ?: "",
                                            gardenLabel = selectedGarden?.name ?: "Garden",
                                            plotId = crop.plotId,
                                            stage = crop.stage
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
                homeViewModel.startPlantingSchedule(target.plotId, LocalDate.now())
                activeCalendarTarget = null
            },
            onDismiss = { activeCalendarTarget = null },
            onReschedule = { /* Kept resilient */ }
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
            onDismiss = { activeCheckUpTarget = null }
        )
    }
}
