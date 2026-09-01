package com.maptanim.app.ui.components.monitoring

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.maptanim.app.data.datasource.CropMetadataAssetDataSource
import com.maptanim.app.data.repository.getPestAssetImagePath
import com.maptanim.app.domain.model.CompanionRelation
import com.maptanim.app.domain.model.PestGuide
import com.maptanim.app.domain.model.Season
import com.maptanim.app.domain.model.SoilType
import com.maptanim.app.domain.model.TaskType
import com.maptanim.app.dss.knowledgebase.CompanionEntry
import com.maptanim.app.dss.knowledgebase.GrowingTip
import com.maptanim.app.ui.components.editcomponents.summary.CategorizedVarietyGroup
import com.maptanim.app.ui.components.editcomponents.summary.getCategorizedVarietiesForCrop
import com.maptanim.app.ui.screens.monitoring.*
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitoringDashboardOverlay(
    onDismiss: () -> Unit,
    onNavigateToLibrary: () -> Unit = {},
    viewModel: MonitoringViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredCrops = viewModel.getFilteredCrops()

    // Active selected crop ID for Screen 2 (enables real-time live timer updates)
    var activeSelectedPlantId by remember { mutableStateOf<String?>(null) }
    var selectedCropForVarietyPicker by remember { mutableStateOf<MonitoredPlant?>(null) }
    var selectedCropForHarvest by remember { mutableStateOf<MonitoredPlant?>(null) }
    var cropAndDateForConfirmation by remember { mutableStateOf<Pair<MonitoredPlant, String>?>(null) }

    val activeSelectedPlant = uiState.plantedCrops.firstOrNull { it.id == activeSelectedPlantId }
        ?: filteredCrops.firstOrNull { it.id == activeSelectedPlantId }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        val view = LocalView.current
        DisposableEffect(view) {
            val window = (view.parent as? DialogWindowProvider)?.window
                ?: (view.context as? Activity)?.window
            window?.let { win ->
                WindowCompat.setDecorFitsSystemWindows(win, false)
                win.setLayout(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
                )
                WindowInsetsControllerCompat(win, win.decorView).apply {
                    hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
                    systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
            onDispose {}
        }

        // Fullscreen edge-to-edge transparent black scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onDismiss()
                },
            contentAlignment = Alignment.Center
        ) {
            // Main Landscape Overlay Frame
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .fillMaxHeight(0.90f)
                    .padding(vertical = 8.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {},
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131D15)),
                border = BorderStroke(1.5.dp, Color(0xFF2E4D3E)),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    // Task completion snackbar toast
                    uiState.completedTaskMessage?.let { msg ->
                        LaunchedEffect(msg) {
                            kotlinx.coroutines.delay(3000)
                            viewModel.clearTaskMessage()
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ForestGreen,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(msg, color = White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                IconButton(onClick = { viewModel.clearTaskMessage() }, modifier = Modifier.size(18.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = White, modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                    }

                    if (activeSelectedPlant == null) {
                        // ── SCREEN 1: Crop Selection with 6 Soil Types & Seasonal Side Nav ──
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // ── LEFT SIDE NAV: Soil Types & Seasons ──────────────────────
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFF1B2317),
                                border = BorderStroke(1.dp, Color(0xFF2E4D3E)),
                                modifier = Modifier
                                    .width(190.dp)
                                    .fillMaxHeight()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // 1. My Farm Crops Mode
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (uiState.filterMode == MonitoringFilterMode.ALL_FARM_CROPS) ForestGreen else Color(0xFF243020),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.selectNavSection(MonitoringNavSection.OVERVIEW) }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Park, contentDescription = null, tint = White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("🌱 Farm Plants (${uiState.plantedCrops.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = White)
                                        }
                                    }

                                    HorizontalDivider(color = Color(0xFF2E4D3E), thickness = 0.8.dp, modifier = Modifier.padding(vertical = 2.dp))

                                    // 2. 6 Soil Types Section Header
                                    Text(
                                        text = "6 SOIL TYPES",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ForestGreen,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )

                                    val soilTypesList = listOf(
                                        Triple(SoilType.LOAM, "Loam Soil", Color(0xFF8D6E63)),
                                        Triple(SoilType.CLAY, "Clay Soil", Color(0xFFD84315)),
                                        Triple(SoilType.SANDY, "Sandy Soil", Color(0xFFFDD835)),
                                        Triple(SoilType.SILTY, "Silty Soil", Color(0xFF78909C)),
                                        Triple(SoilType.PEATY, "Peaty Soil", Color(0xFF4E342E)),
                                        Triple(SoilType.CHALKY, "Chalky Soil", Color(0xFFECEFF1))
                                    )

                                    soilTypesList.forEach { (soil, label, dotColor) ->
                                        val isSelected = uiState.filterMode == MonitoringFilterMode.BY_SOIL_TYPE && uiState.selectedSoilType == soil
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) ForestGreen else Color(0xFF243020),
                                            border = if (isSelected) BorderStroke(1.dp, White.copy(alpha = 0.5f)) else null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { viewModel.selectSoilType(soil) }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(dotColor)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = label,
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = White
                                                )
                                            }
                                        }
                                    }

                                    HorizontalDivider(color = Color(0xFF2E4D3E), thickness = 0.8.dp, modifier = Modifier.padding(vertical = 2.dp))

                                    // 3. Seasonal Section Header
                                    Text(
                                        text = "SEASONAL WINDOWS",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ForestGreen,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )

                                    val seasonsList = listOf(
                                        Pair(Season.DRY, "☀️ Dry Season (Tag-araw)"),
                                        Pair(Season.WET, "🌧️ Wet Season (Tag-ulan)"),
                                        Pair(Season.YEAR_ROUND, "🔄 Year-Round (Buong Taon)")
                                    )

                                    seasonsList.forEach { (season, label) ->
                                        val isSelected = uiState.filterMode == MonitoringFilterMode.BY_SEASON && uiState.selectedSeason == season
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) ForestGreen else Color(0xFF243020),
                                            border = if (isSelected) BorderStroke(1.dp, White.copy(alpha = 0.5f)) else null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { viewModel.selectSeason(season) }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = label,
                                                    fontSize = 10.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = White
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // ── RIGHT MAIN PANE: Search, Category Filter & Crops Grid ──────
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f)
                            ) {
                                // Top Filter Bar
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF1B2317),
                                    border = BorderStroke(1.dp, Color(0xFF2E4D3E)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Active Mode Badge
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = ForestGreen.copy(alpha = 0.25f),
                                            border = BorderStroke(1.dp, ForestGreen)
                                        ) {
                                            val badgeText = when (uiState.filterMode) {
                                                MonitoringFilterMode.BY_SOIL_TYPE -> "Soil: ${uiState.selectedSoilType.name}"
                                                MonitoringFilterMode.BY_SEASON -> "Season: ${uiState.selectedSeason.name}"
                                                MonitoringFilterMode.ALL_FARM_CROPS -> "My Farm Crops"
                                            }
                                            Text(
                                                text = badgeText,
                                                color = White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }

                                        // Search Bar
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFF2A3424),
                                            border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.6f)),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Search,
                                                    contentDescription = null,
                                                    tint = White.copy(alpha = 0.5f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                BasicTextField(
                                                    value = uiState.searchQuery,
                                                    onValueChange = { viewModel.updateSearchQuery(it) },
                                                    singleLine = true,
                                                    textStyle = TextStyle(
                                                        color = White,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Medium
                                                    ),
                                                    cursorBrush = SolidColor(ForestGreen),
                                                    decorationBox = { innerTextField ->
                                                        if (uiState.searchQuery.isEmpty()) {
                                                            Text(
                                                                text = "Search crops in this section...",
                                                                color = White.copy(alpha = 0.45f),
                                                                fontSize = 12.sp
                                                            )
                                                        }
                                                        innerTextField()
                                                    },
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                        }

                                        // Category Dropdown
                                        Box {
                                            Surface(
                                                onClick = { viewModel.toggleCategoryDropdown(true) },
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color(0xFF2A3424),
                                                border = BorderStroke(1.dp, ForestGreen)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(Icons.Default.Category, contentDescription = null, tint = White, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(uiState.selectedCategory.label, fontSize = 11.sp, color = White, fontWeight = FontWeight.SemiBold)
                                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = White, modifier = Modifier.size(16.dp))
                                                }
                                            }

                                            DropdownMenu(
                                                expanded = uiState.isCategoryDropdownExpanded,
                                                onDismissRequest = { viewModel.toggleCategoryDropdown(false) },
                                                modifier = Modifier.background(Color(0xFF1B2317))
                                            ) {
                                                CropCategoryFilter.values().forEach { category ->
                                                    DropdownMenuItem(
                                                        text = { Text(category.label, color = White, fontSize = 12.sp) },
                                                        onClick = { viewModel.selectCategory(category) }
                                                    )
                                                }
                                            }
                                        }

                                        // Close Button
                                        IconButton(
                                            onClick = onDismiss,
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Close Monitoring", tint = White, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // 4-Column Landscape Crop Cards Grid
                                if (filteredCrops.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(Icons.Default.Park, contentDescription = null, tint = White.copy(alpha = 0.3f), modifier = Modifier.size(48.dp))
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = when (uiState.filterMode) {
                                                    MonitoringFilterMode.BY_SOIL_TYPE -> "No crops found for ${uiState.selectedSoilType.name} with category ${uiState.selectedCategory.label}"
                                                    MonitoringFilterMode.BY_SEASON -> "No crops found for ${uiState.selectedSeason.name} Season with category ${uiState.selectedCategory.label}"
                                                    MonitoringFilterMode.ALL_FARM_CROPS -> "No monitored farm crops found"
                                                },
                                                color = White.copy(alpha = 0.6f),
                                                fontSize = 13.sp,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                } else {
                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(4),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        items(filteredCrops) { crop ->
                                            CropSelectionGridCard(
                                                crop = crop,
                                                onSelect = { activeSelectedPlantId = crop.id }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // ── SCREEN 2: Selected Crop Detail View (Dual Pane with 6 Panels) ──
                        activeSelectedPlant?.let { crop ->
                            // Header bar with Back button & Crop Name
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { activeSelectedPlantId = null },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to List", tint = White, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(getCropEmoji(crop.cropName), fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${crop.cropName.uppercase()} (${crop.plotLabel})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = White
                                    )
                                }

                                // Close Button (top-right)
                                IconButton(
                                    onClick = onDismiss,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Close Monitoring", tint = White, modifier = Modifier.size(20.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Dual Pane Layout for Screen 2
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // 6-Section Side Navigation (Overview, Timeline, Calendar, Companions, Growing Tips, Pest & Disease)
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF1B2317),
                                    modifier = Modifier
                                        .width(160.dp)
                                        .fillMaxHeight()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .padding(6.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(
                                                text = "DSS PANELS",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = White.copy(alpha = 0.45f),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )

                                            val navSections = listOf(
                                                MonitoringNavSection.OVERVIEW,
                                                MonitoringNavSection.TIMELINE,
                                                MonitoringNavSection.CALENDAR,
                                                MonitoringNavSection.COMPANIONS,
                                                MonitoringNavSection.GROWING_TIPS,
                                                MonitoringNavSection.PEST_DISEASE
                                            )

                                            navSections.forEach { section ->
                                                NavSectionItem(
                                                    section = section,
                                                    isSelected = uiState.selectedNavSection == section,
                                                    onClick = { viewModel.selectNavSection(section) }
                                                )
                                            }
                                        }
                                    }
                                }

                                // Right Main Content Section (Switch among 6 DSS Panels)
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .weight(1f)
                                ) {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        item {
                                            when (uiState.selectedNavSection) {
                                                MonitoringNavSection.OVERVIEW -> MyPlantsTabSection(
                                                    crop = crop,
                                                    onHarvestClick = { selectedCropForHarvest = crop },
                                                    onCompleteTask = { taskType ->
                                                        viewModel.completeDssTask(crop.id, crop.farmId, taskType)
                                                    }
                                                )
                                                MonitoringNavSection.TIMELINE -> TimelineCard(crop = crop)
                                                MonitoringNavSection.CALENDAR -> CalendarCard(
                                                    crop = crop,
                                                    onVarietyClick = { selectedCropForVarietyPicker = crop },
                                                    onRescheduleWithDate = { clickedDate ->
                                                        cropAndDateForConfirmation = crop to clickedDate
                                                    },
                                                    onCancelPlanClick = { viewModel.cancelMonitoringSchedule(crop.id) }
                                                )
                                                MonitoringNavSection.COMPANIONS -> CompanionsTabSection(crop = crop)
                                                MonitoringNavSection.GROWING_TIPS -> GrowingTipsTabSection(crop = crop)
                                                MonitoringNavSection.PEST_DISEASE -> PestDiseaseTabSection(
                                                    crop = crop,
                                                    onNavigateToLibrary = {
                                                        onDismiss()
                                                        onNavigateToLibrary()
                                                    }
                                                )
                                                else -> MyPlantsTabSection(
                                                    crop = crop,
                                                    onHarvestClick = { selectedCropForHarvest = crop },
                                                    onCompleteTask = { taskType ->
                                                        viewModel.completeDssTask(crop.id, crop.farmId, taskType)
                                                    }
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
        }
    }

    // Modal Variety Picker Dropdown / Selector
    selectedCropForVarietyPicker?.let { crop ->
        MonitoringVarietyDropdownModal(
            crop = crop,
            onDismiss = { selectedCropForVarietyPicker = null },
            onConfirmVariety = { chosenVariety ->
                viewModel.startMonitoring(crop.id, crop.rawPlantedDate ?: java.time.LocalDate.now().toString(), chosenVariety)
                selectedCropForVarietyPicker = null
            }
        )
    }

    // Direct Date Change Confirmation Modal from Main Calendar Page
    cropAndDateForConfirmation?.let { (crop, newDate) ->
        DateChangeConfirmModal(
            cropName = crop.cropName,
            plotLabel = crop.plotLabel,
            newDateStr = newDate,
            onDismiss = { cropAndDateForConfirmation = null },
            onConfirm = {
                viewModel.startMonitoring(crop.id, newDate, crop.cropVariety ?: "Standard Variety")
                cropAndDateForConfirmation = null
            }
        )
    }

    // Modal Harvest Completion Selector
    selectedCropForHarvest?.let { crop ->
        HarvestCompletionModal(
            crop = crop,
            onDismiss = { selectedCropForHarvest = null },
            onConfirmHarvest = { yieldKg, notes ->
                viewModel.completeHarvest(crop.id, yieldKg, notes)
                selectedCropForHarvest = null
                activeSelectedPlantId = null
            }
        )
    }
}

// ── Crop Selection Grid Card (4 Columns per Row) ─────────────────────────────
@Composable
private fun CropSelectionGridCard(
    crop: MonitoredPlant,
    onSelect: () -> Unit
) {
    val isSim = crop.cropName.lowercase().contains("ampalaya") || crop.cropVariety?.contains("10s", ignoreCase = true) == true
    val progressRatio = if (isSim) (crop.daysPlanted.toFloat() / 10f).coerceIn(0f, 1f) else if (crop.daysToHarvest > 0) (crop.daysPlanted.toFloat() / crop.daysToHarvest).coerceIn(0f, 1f) else 0f

    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1B2317),
        border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF2A3424)),
                contentAlignment = Alignment.Center
            ) {
                val cropImg = CropMetadataAssetDataSource.getCropAssetImagePath(crop.cropName, crop.cropName)
                AsyncImage(
                    model = cropImg,
                    contentDescription = crop.cropName,
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            }

            Text(
                text = crop.cropName,
                fontWeight = FontWeight.Bold,
                color = White,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = "${crop.plotLabel} • ${crop.category.label}",
                fontSize = 9.sp,
                color = White.copy(alpha = 0.65f),
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isSim) "${crop.daysPlanted}s / 10s" else "Day ${crop.daysPlanted}/${crop.daysToHarvest}",
                    fontSize = 9.sp,
                    color = ForestGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${(progressRatio * 100).toInt()}%",
                    fontSize = 8.sp,
                    color = White.copy(alpha = 0.5f)
                )
            }

            LinearProgressIndicator(
                progress = progressRatio,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = ForestGreen,
                trackColor = Color.Gray.copy(alpha = 0.25f)
            )

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF2A3424)
            ) {
                val label = crop.cropVariety?.let { "$it • ${crop.stageName}" } ?: crop.stageName
                Text(
                    text = label,
                    fontSize = 8.sp,
                    color = White,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
            }
        }
    }
}

// ── Left Nav Item for Screen 2 (6 Panels) ───────────────────────────────────
@Composable
private fun NavSectionItem(
    section: MonitoringNavSection,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) ForestGreen else Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = when (section) {
                MonitoringNavSection.OVERVIEW -> Icons.Default.Dashboard
                MonitoringNavSection.SOIL_TYPES -> Icons.Default.Terrain
                MonitoringNavSection.SEASONAL -> Icons.Default.WbSunny
                MonitoringNavSection.TIMELINE -> Icons.Default.Timeline
                MonitoringNavSection.CALENDAR -> Icons.Default.CalendarMonth
                MonitoringNavSection.COMPANIONS -> Icons.Default.Groups
                MonitoringNavSection.GROWING_TIPS -> Icons.Default.Lightbulb
                MonitoringNavSection.PEST_DISEASE -> Icons.Default.BugReport
            }
            Icon(icon, contentDescription = null, tint = White, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = section.title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = White
            )
        }
    }
}

// ── 1. PANEL: MY PLANTS (Crop State + Live DSS Tasks) ────────────────────────
@Composable
private fun MyPlantsTabSection(
    crop: MonitoredPlant,
    onHarvestClick: () -> Unit = {},
    onCompleteTask: (TaskType) -> Unit = {}
) {
    val isSim = crop.cropName.lowercase().contains("ampalaya") || crop.cropVariety?.contains("10s", ignoreCase = true) == true
    val remainingDays = (crop.daysToHarvest - crop.daysPlanted).coerceAtLeast(0)
    val progressRatio = if (isSim) (crop.daysPlanted.toFloat() / 10f).coerceIn(0f, 1f) else if (crop.daysToHarvest > 0) (crop.daysPlanted.toFloat() / crop.daysToHarvest).coerceIn(0f, 1f) else 0f
    val isHarvestable = crop.currentStageIndex == 4 || crop.stageName.contains("Harvest", ignoreCase = true) || crop.healthStatus.contains("HARVEST", ignoreCase = true) || remainingDays == 0

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Current Crop Status & Simulation Progress Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1B2317),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Top row: Simulation Day & Crop Status Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSim) "SIMULATION TIMER: ${crop.daysPlanted}s / 10s" else "DAY ${crop.daysPlanted} / ${crop.daysToHarvest}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isHarvestable) Color(0xFFD48806).copy(alpha = 0.35f) else ForestGreen.copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, if (isHarvestable) Color(0xFFD48806) else ForestGreen)
                    ) {
                        Text(
                            text = if (crop.stageName.contains("Overdue", ignoreCase = true)) "HARVEST OVERDUE ⚠️" else if (isHarvestable) "HARVEST READY 🌾" else "ACTIVE MONITORING",
                            color = White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Current Growth Stage Title
                Text(
                    text = crop.stageName.uppercase(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )

                // Progress Bar
                LinearProgressIndicator(
                    progress = progressRatio,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (isHarvestable) Color(0xFFD48806) else ForestGreen,
                    trackColor = Color.Gray.copy(alpha = 0.25f)
                )

                // Days Until Harvest
                Text(
                    text = if (isHarvestable) "READY FOR HARVEST NOW" else "$remainingDays DAYS TO HARVEST",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isHarvestable) Color(0xFFD48806) else White.copy(alpha = 0.9f)
                )

                if (isHarvestable) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Button(
                        onClick = onHarvestClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD48806)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Agriculture, contentDescription = null, tint = White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Harvest Now 🌾 (Complete Cycle & Record Yield)",
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Today's DSS Care Tasks Section
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1B2317),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's DSS Care Tasks",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                    Text(
                        text = "Tap action to record activity",
                        fontSize = 9.sp,
                        color = White.copy(alpha = 0.45f)
                    )
                }

                if (crop.dssTasks.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        crop.dssTasks.forEach { task ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF2A3424),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        val icon = when (task.taskType) {
                                            TaskType.WATER -> "💧"
                                            TaskType.FERTILIZE -> "🌿"
                                            TaskType.HARVEST -> "🌾"
                                            TaskType.PEST_ALERT -> "🐛"
                                            else -> "🔎"
                                        }
                                        Text(icon, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(task.title, fontSize = 11.sp, color = White, fontWeight = FontWeight.Bold)
                                            Text("Due: ${task.dueDate}", fontSize = 9.sp, color = White.copy(alpha = 0.5f))
                                        }
                                    }

                                    Button(
                                        onClick = { onCompleteTask(task.taskType) },
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Mark Done ✓", fontSize = 10.sp, color = White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Quick Action Logging Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            onClick = { onCompleteTask(TaskType.WATER) },
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF2A3424),
                            border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.4f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text("💧", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Log Water", fontSize = 11.sp, color = White, fontWeight = FontWeight.Bold)
                            }
                        }

                        Surface(
                            onClick = { onCompleteTask(TaskType.FERTILIZE) },
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF2A3424),
                            border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.4f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text("🌿", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Log Fertilize", fontSize = 11.sp, color = White, fontWeight = FontWeight.Bold)
                            }
                        }

                        Surface(
                            onClick = { onCompleteTask(TaskType.PEST_ALERT) },
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF2A3424),
                            border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.4f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text("🔎", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Log Inspect", fontSize = 11.sp, color = White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Plot & Soil Characteristics Summary Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1B2317),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Plot & Soil Profile",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Plot Soil Type", fontSize = 9.sp, color = White.copy(alpha = 0.5f))
                        Text(crop.soilType.name, fontSize = 11.sp, color = White, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Soil Match", fontSize = 9.sp, color = White.copy(alpha = 0.5f))
                        val scoreLabel = when {
                            crop.soilScore == null -> "—"
                            crop.soilScore >= 1.0f -> "Optimal (100%)"
                            crop.soilScore >= 0.75f -> "Suitable (75%)"
                            crop.soilScore >= 0.50f -> "Marginal (50%)"
                            else -> "Poor (25%)"
                        }
                        Text(scoreLabel, fontSize = 11.sp, color = if ((crop.soilScore ?: 0f) >= 0.75f) ForestGreen else Color(0xFFD48806), fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("NPK Ratio", fontSize = 9.sp, color = White.copy(alpha = 0.5f))
                        Text("${crop.nRatio.toInt()}:${crop.pRatio.toInt()}:${crop.kRatio.toInt()}", fontSize = 11.sp, color = White, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Optimal pH", fontSize = 9.sp, color = White.copy(alpha = 0.5f))
                        Text("${crop.optimalPhMin} – ${crop.optimalPhMax}", fontSize = 11.sp, color = White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ── 2. PANEL: TIMELINE CARD ──────────────────────────────────────────────────
@Composable
private fun TimelineCard(crop: MonitoredPlant) {
    val stages = listOf("Sprout", "Seedling", "Vegetative", "Flowering/Podding", "Harvest Ready")
    val remainingDays = (crop.daysToHarvest - crop.daysPlanted).coerceAtLeast(0)
    val progressRatio = if (crop.daysToHarvest > 0) (crop.daysPlanted.toFloat() / crop.daysToHarvest).coerceIn(0f, 1f) else 0f

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1B2317),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(getCropEmoji(crop.cropName), fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${crop.cropName} — Day ${crop.daysPlanted} / ${crop.daysToHarvest}",
                        fontWeight = FontWeight.Bold,
                        color = White,
                        fontSize = 13.sp
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ForestGreen
                ) {
                    Text(
                        text = "Current: ${crop.stageName}",
                        fontSize = 10.sp,
                        color = White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            HorizontalDivider(color = White.copy(alpha = 0.1f))

            // Visual Growth Progression Stepper
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                stages.forEachIndexed { idx, stageTitle ->
                    val isDone = idx < crop.currentStageIndex
                    val isCurrent = idx == crop.currentStageIndex

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    when {
                                        isCurrent -> ForestGreen
                                        isDone -> Color(0xFF43A047)
                                        else -> Color.Gray.copy(alpha = 0.25f)
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isDone) "✓ $stageTitle" else if (isCurrent) "● $stageTitle" else "○ $stageTitle",
                            fontSize = 8.sp,
                            color = when {
                                isCurrent -> White
                                isDone -> ForestGreen
                                else -> White.copy(alpha = 0.4f)
                            },
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Variety-Specific Stage Durations (when stageDays info available)
            crop.stageDays?.let { sDays ->
                HorizontalDivider(color = White.copy(alpha = 0.1f))
                Text("Variety Stage Breakdown (${crop.cropVariety ?: "Standard"})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Sprout", fontSize = 9.sp, color = White.copy(alpha = 0.5f))
                        Text("${sDays.stage1Sprout}d", fontSize = 10.sp, color = White, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Seedling", fontSize = 9.sp, color = White.copy(alpha = 0.5f))
                        Text("${sDays.stage2Seedling}d", fontSize = 10.sp, color = White, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Vegetative", fontSize = 9.sp, color = White.copy(alpha = 0.5f))
                        Text("${sDays.stage3Vegetative}d", fontSize = 10.sp, color = White, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Flowering", fontSize = 9.sp, color = White.copy(alpha = 0.5f))
                        Text("${sDays.stage4Flowering}d", fontSize = 10.sp, color = White, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Harvest", fontSize = 9.sp, color = White.copy(alpha = 0.5f))
                        Text("${sDays.stage5Harvest}d", fontSize = 10.sp, color = Color(0xFFD48806), fontWeight = FontWeight.Bold)
                    }
                }
            }

            HorizontalDivider(color = White.copy(alpha = 0.1f))

            // Prominent Stage Metrics Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Highlighted Stage", fontSize = 9.sp, color = White.copy(alpha = 0.5f))
                    Text(crop.stageName, fontSize = 10.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Simulation Day", fontSize = 9.sp, color = White.copy(alpha = 0.5f))
                    Text("Day ${crop.daysPlanted} of ${crop.daysToHarvest}", fontSize = 10.sp, color = White, fontWeight = FontWeight.SemiBold)
                }
                Column {
                    Text("Days Remaining", fontSize = 9.sp, color = White.copy(alpha = 0.5f))
                    Text("$remainingDays days", fontSize = 10.sp, color = Color(0xFFD48806), fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Total Progress", fontSize = 9.sp, color = White.copy(alpha = 0.5f))
                    Text("${(progressRatio * 100).toInt()}%", fontSize = 10.sp, color = Color(0xFF1E88E5), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ── 3. PANEL: CALENDAR CARD ──────────────────────────────────────────────────
@Composable
private fun CalendarCard(
    crop: MonitoredPlant,
    onVarietyClick: () -> Unit = {},
    onRescheduleWithDate: (String) -> Unit = {},
    onCancelPlanClick: () -> Unit = {}
) {
    val actualDate = crop.rawPlantedDate?.take(10) ?: "Not Confirmed"
    val plannedDate = crop.rawPlantedDate?.take(10) ?: java.time.LocalDate.now().toString()
    val harvestTargetDate = try {
        val baseDate = if (!crop.rawPlantedDate.isNullOrBlank()) java.time.LocalDate.parse(crop.rawPlantedDate.take(10)) else java.time.LocalDate.now()
        baseDate.plusDays(crop.daysToHarvest.toLong()).toString()
    } catch (e: Exception) { "TBD" }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1B2317),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row with Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Planting & Schedule Lifecycle",
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen,
                    fontSize = 12.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        onClick = onVarietyClick,
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF203625),
                        border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.8f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "🌱 Variety: ${crop.cropVariety ?: "Select"}",
                                fontSize = 9.sp,
                                color = White,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Variety",
                                tint = ForestGreen,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Surface(
                        onClick = onCancelPlanClick,
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF3B1E1E),
                        border = BorderStroke(1.dp, Color(0xFFE53935))
                    ) {
                        Text(
                            text = "🚫 Cancel Plan",
                            fontSize = 9.sp,
                            color = Color(0xFFEF5350),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = White.copy(alpha = 0.1f))

            // Lifecycle Date Metadata Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Planned Date", fontSize = 9.sp, color = White.copy(alpha = 0.5f))
                    Text(plannedDate, fontSize = 10.sp, color = White, fontWeight = FontWeight.SemiBold)
                }
                Column {
                    Text("Actual Planting (Day 0)", fontSize = 9.sp, color = White.copy(alpha = 0.5f))
                    Text(actualDate, fontSize = 10.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Expected Harvest", fontSize = 9.sp, color = White.copy(alpha = 0.5f))
                    Text(harvestTargetDate, fontSize = 10.sp, color = Color(0xFFD48806), fontWeight = FontWeight.Bold)
                }
            }

            HorizontalDivider(color = White.copy(alpha = 0.1f))

            // Interactive Monthly Calendar Grid
            MonthlyCalendarView(
                plantedDateStr = crop.rawPlantedDate,
                daysToHarvest = crop.daysToHarvest,
                onSelectPlantingDate = { clickedDate ->
                    onRescheduleWithDate(clickedDate)
                }
            )
        }
    }
}

@Composable
private fun MonthlyCalendarView(
    plantedDateStr: String?,
    daysToHarvest: Int,
    onSelectPlantingDate: (String) -> Unit
) {
    val initialDate = try {
        if (!plantedDateStr.isNullOrBlank()) java.time.LocalDate.parse(plantedDateStr.take(10)) else java.time.LocalDate.now()
    } catch (e: Exception) {
        java.time.LocalDate.now()
    }

    var currentYearMonth by remember(plantedDateStr) { mutableStateOf(java.time.YearMonth.from(initialDate)) }
    val plantingDate = initialDate
    val harvestDate = initialDate.plusDays(daysToHarvest.toLong())
    val today = java.time.LocalDate.now()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { currentYearMonth = currentYearMonth.minusMonths(1) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month", tint = White, modifier = Modifier.size(16.dp))
            }

            Text(
                text = "${currentYearMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${currentYearMonth.year}",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = White
            )

            IconButton(
                onClick = { currentYearMonth = currentYearMonth.plusMonths(1) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = White, modifier = Modifier.size(16.dp))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                Text(day, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = White.copy(alpha = 0.5f))
            }
        }

        val firstDayOfMonth = currentYearMonth.atDay(1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
        val daysInMonth = currentYearMonth.lengthOfMonth()
        val totalCells = firstDayOfWeek + daysInMonth
        val rows = (totalCells + 6) / 7

        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            for (r in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    for (c in 0..6) {
                        val dayNum = r * 7 + c - firstDayOfWeek + 1
                        if (dayNum in 1..daysInMonth) {
                            val dateObj = currentYearMonth.atDay(dayNum)
                            val isPlantingDay = dateObj == plantingDate
                            val isHarvestDay = dateObj == harvestDate
                            val isToday = dateObj == today

                            val fertDate = initialDate.plusDays((daysToHarvest * 0.35f).toLong())
                            val scoutDate = initialDate.plusDays(7)
                            val isFertilizationDay = dateObj == fertDate
                            val isInspectionDay = dateObj == scoutDate

                            val bgColor = when {
                                isPlantingDay -> ForestGreen
                                isHarvestDay -> Color(0xFFD48806)
                                isFertilizationDay -> Color(0xFF43A047)
                                isInspectionDay -> Color(0xFF1E88E5)
                                isToday -> Color(0xFF2A3424)
                                else -> Color.Transparent
                            }

                            val textColor = when {
                                isPlantingDay || isHarvestDay || isFertilizationDay || isInspectionDay -> White
                                isToday -> ForestGreen
                                else -> White.copy(alpha = 0.85f)
                            }

                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(bgColor)
                                    .border(
                                        width = if (isToday && !isPlantingDay && !isHarvestDay) 1.dp else 0.dp,
                                        color = if (isToday) ForestGreen else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { onSelectPlantingDate(dateObj.toString()) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$dayNum",
                                    fontSize = 9.sp,
                                    fontWeight = if (isPlantingDay || isHarvestDay || isFertilizationDay || isToday) FontWeight.Bold else FontWeight.Normal,
                                    color = textColor
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(ForestGreen))
                Spacer(modifier = Modifier.width(3.dp))
                Text("Planting", fontSize = 8.sp, color = White.copy(alpha = 0.7f))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFF43A047)))
                Spacer(modifier = Modifier.width(3.dp))
                Text("Fertilize", fontSize = 8.sp, color = White.copy(alpha = 0.7f))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFFD48806)))
                Spacer(modifier = Modifier.width(3.dp))
                Text("Harvest", fontSize = 8.sp, color = White.copy(alpha = 0.7f))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(7.dp).clip(CircleShape).border(1.dp, ForestGreen, CircleShape))
                Spacer(modifier = Modifier.width(3.dp))
                Text("Today", fontSize = 8.sp, color = White.copy(alpha = 0.7f))
            }
        }
    }
}

// ── 4. PANEL: COMPANION PLANTING ─────────────────────────────────────────────
@Composable
private fun CompanionsTabSection(crop: MonitoredPlant) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Active Neighbors Evaluation Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1B2317),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Farm Companion Status",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when {
                            crop.companionAlerts.isNotEmpty() -> Color(0xFFE53935).copy(alpha = 0.25f)
                            crop.activeCompanionEvaluations.any { it.relationship == CompanionRelation.BENEFICIAL } -> ForestGreen.copy(alpha = 0.25f)
                            else -> Color.Gray.copy(alpha = 0.25f)
                        },
                        border = BorderStroke(1.dp, when {
                            crop.companionAlerts.isNotEmpty() -> Color(0xFFE53935)
                            crop.activeCompanionEvaluations.any { it.relationship == CompanionRelation.BENEFICIAL } -> ForestGreen
                            else -> Color.Gray.copy(alpha = 0.4f)
                        })
                    ) {
                        Text(
                            text = when {
                                crop.companionAlerts.isNotEmpty() -> "ANTAGONIST ALERT ⚠️"
                                crop.activeCompanionEvaluations.any { it.relationship == CompanionRelation.BENEFICIAL } -> "BENEFICIAL NEIGHBOR 🌿"
                                crop.activeCompanionEvaluations.isNotEmpty() -> "NEUTRAL COEXISTENCE"
                                else -> "NO COMPANION ASSIGNED"
                            },
                            color = White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                if (crop.companionAlerts.isNotEmpty()) {
                    crop.companionAlerts.forEach { alert ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF3B1E1E),
                            border = BorderStroke(1.dp, Color(0xFFE53935)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("⚠️", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("${alert.cropA} (${alert.plotALabel}) ✕ ${alert.cropB} (${alert.plotBLabel})", fontSize = 11.sp, color = Color(0xFFEF5350), fontWeight = FontWeight.Bold)
                                    Text(alert.message, fontSize = 10.sp, color = White.copy(alpha = 0.8f))
                                }
                            }
                        }
                    }
                } else if (crop.activeCompanionEvaluations.isNotEmpty()) {
                    crop.activeCompanionEvaluations.forEach { entry ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF2A3424),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${entry.cropA} + ${entry.cropB}", fontSize = 11.sp, color = White, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = entry.relationship.name,
                                        fontSize = 9.sp,
                                        color = if (entry.relationship == CompanionRelation.BENEFICIAL) ForestGreen else White.copy(alpha = 0.7f),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(entry.reason, fontSize = 10.sp, color = White.copy(alpha = 0.75f))
                            }
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF2A3424),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("ℹ️ No companion assigned", fontSize = 11.sp, color = White, fontWeight = FontWeight.Bold)
                            Text(
                                text = "There are currently no adjacent crops assigned to neighboring plots. \"No companion assigned\" does not imply compatibility.",
                                fontSize = 10.sp,
                                color = White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }

        // Recommended Beneficial Companions Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1B2317),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ThumbUp, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Recommended Beneficial Companions for ${crop.cropName}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = White)
                }

                if (crop.beneficialCompanions.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        crop.beneficialCompanions.take(4).forEach { companion ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ForestGreen.copy(alpha = 0.25f),
                                border = BorderStroke(1.dp, ForestGreen)
                            ) {
                                Text(
                                    text = "🌿 $companion",
                                    fontSize = 10.sp,
                                    color = White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                } else {
                    Text("No specific beneficial pairs cataloged.", fontSize = 10.sp, color = White.copy(alpha = 0.6f))
                }
            }
        }

        // Antagonist Crops to Avoid Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1B2317),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Antagonist Crops to Avoid Planting Adjacent", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = White)
                }

                if (crop.antagonistCompanions.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        crop.antagonistCompanions.take(4).forEach { antagonist ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF3B1E1E),
                                border = BorderStroke(1.dp, Color(0xFFE53935))
                            ) {
                                Text(
                                    text = "🚫 $antagonist",
                                    fontSize = 10.sp,
                                    color = Color(0xFFEF5350),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                } else {
                    Text("No strong antagonist pairs recorded.", fontSize = 10.sp, color = White.copy(alpha = 0.6f))
                }
            }
        }
    }
}

// ── 5. PANEL: GROWING TIPS (Stage-Specific + Agronomic Specs) ────────────────
@Composable
private fun GrowingTipsTabSection(crop: MonitoredPlant) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Stage-Specific Growing Tips
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1B2317),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFFFA000), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Stage Guidance: ${crop.stageName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ForestGreen
                    ) {
                        Text(
                            text = "DA-BPI STANDARD",
                            fontSize = 8.sp,
                            color = White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                if (crop.growingTipsList.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        crop.growingTipsList.forEach { tip ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF2A3424),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(tip.icon, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(tip.title, fontSize = 11.sp, color = White, fontWeight = FontWeight.Bold)
                                        Text(tip.description, fontSize = 10.sp, color = White.copy(alpha = 0.8f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Agronomic & General Care Reference
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1B2317),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Agronomic & Soil Care Reference",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    crop.generalCareTips.forEach { tip ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF2A3424),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(tip.icon, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(tip.title, fontSize = 11.sp, color = White, fontWeight = FontWeight.Bold)
                                    Text(tip.description, fontSize = 10.sp, color = White.copy(alpha = 0.8f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── 6. PANEL: PEST & DISEASE CONTROL ─────────────────────────────────────────
@Composable
private fun PestDiseaseTabSection(
    crop: MonitoredPlant,
    onNavigateToLibrary: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Summary Header Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1B2317),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.BugReport, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${crop.cropName} — Pest & Disease Management",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFE53935).copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, Color(0xFFE53935))
                    ) {
                        Text(
                            text = "${crop.affectedPests.size} Risks Cataloged",
                            fontSize = 9.sp,
                            color = White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "Reference-based IPM (Integrated Pest Management) guidance. Scouting is recommended weekly.",
                    fontSize = 10.sp,
                    color = White.copy(alpha = 0.7f)
                )
            }
        }

        // List of Matching Pest Guides
        if (crop.affectedPests.isNotEmpty()) {
            crop.affectedPests.forEach { pest ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1B2317),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val pestImg = getPestAssetImagePath(pest.id, pest.name)
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF2A3424)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = pestImg,
                                        contentDescription = pest.name,
                                        contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(pest.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = White)
                                    Text("${pest.localName} • ${pest.scientificName}", fontSize = 9.sp, color = White.copy(alpha = 0.6f))
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF2A3424)
                            ) {
                                Text(pest.category, fontSize = 8.sp, color = ForestGreen, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }

                        HorizontalDivider(color = White.copy(alpha = 0.1f))

                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text("🌿 Organic Intervention:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                            Text(pest.organicControl, fontSize = 9.sp, color = White.copy(alpha = 0.85f))

                            Text("🧪 Chemical Control (DA Approved):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFA000))
                            Text(pest.chemicalControl, fontSize = 9.sp, color = White.copy(alpha = 0.85f))

                            Text("🛡️ Prevention Practice:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E88E5))
                            Text(pest.preventionTips, fontSize = 9.sp, color = White.copy(alpha = 0.85f))
                        }
                    }
                }
            }
        } else {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1B2317),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("🔎 Routine Scouting Guidance", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = White)
                    Text("• Inspect undersides of leaves early morning for aphids or whiteflies.", fontSize = 10.sp, color = White.copy(alpha = 0.8f))
                    Text("• Check soil base for cutworms or stem borer entrance holes.", fontSize = 10.sp, color = White.copy(alpha = 0.8f))
                    Text("• Remove infected or yellowing lower leaves to prevent fungal spread.", fontSize = 10.sp, color = White.copy(alpha = 0.8f))
                }
            }
        }

        // Action Button: Direct Navigation to LibraryScreen
        Surface(
            onClick = onNavigateToLibrary,
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF2A3424),
            border = BorderStroke(1.dp, ForestGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "📚 View Complete Pest & Crop Library in Knowledge Base",
                    color = White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ── CATEGORIZED VARIETY PICKER DROPDOWN MODAL ────────────────────────────────
@Composable
private fun MonitoringVarietyDropdownModal(
    crop: MonitoredPlant,
    onDismiss: () -> Unit,
    onConfirmVariety: (varietyName: String) -> Unit
) {
    val categorizedGroups = remember(crop.cropName) {
        getCategorizedVarietiesForCrop(crop.cropName)
    }
    val currentVariety = crop.cropVariety ?: ""

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .widthIn(min = 360.dp, max = 500.dp)
                    .fillMaxWidth(0.85f)
                    .fillMaxHeight(0.85f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* block touch */ },
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF102014).copy(alpha = 0.95f),
                border = BorderStroke(1.2.dp, ForestGreen.copy(alpha = 0.85f)),
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "🌱 Select Crop Variety",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = White
                            )
                            Text(
                                text = "${crop.cropName} • Plot ${crop.plotLabel}",
                                fontSize = 11.sp,
                                color = ForestGreen
                            )
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = White)
                        }
                    }

                    HorizontalDivider(color = White.copy(alpha = 0.1f))

                    // Grouped Variety List
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(categorizedGroups) { group ->
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(group.iconEmoji, fontSize = 12.sp)
                                    Text(
                                        text = group.categoryName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFD54F)
                                    )
                                }

                                group.varieties.forEach { varietyName ->
                                    val isSelected = varietyName.equals(currentVariety, ignoreCase = true)
                                    Surface(
                                        onClick = {
                                            onConfirmVariety(varietyName)
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) ForestGreen else Color(0xFF1E3323),
                                        border = BorderStroke(
                                            0.8.dp,
                                            if (isSelected) Color(0xFF81C784) else ForestGreen.copy(alpha = 0.3f)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = varietyName,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = White
                                            )
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = White,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                    ) {
                        Text("Close", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = White)
                    }
                }
            }
        }
    }
}

// ── DATE CHANGE CONFIRMATION MODAL ───────────────────────────────────────────
@Composable
private fun DateChangeConfirmModal(
    cropName: String,
    plotLabel: String,
    newDateStr: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .widthIn(min = 320.dp, max = 440.dp)
                    .fillMaxWidth(0.85f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* block touch */ },
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF102014).copy(alpha = 0.95f),
                border = BorderStroke(1.2.dp, ForestGreen.copy(alpha = 0.85f)),
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = ForestGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Change Planting Date?",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = White
                            )
                        }
                        IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = White)
                        }
                    }

                    HorizontalDivider(color = White.copy(alpha = 0.1f))

                    Text(
                        text = "Are you sure you want to change the planting date for $cropName (Plot $plotLabel) to $newDateStr?\n\nThe monitoring timeline and growth timer will restart from this date.",
                        fontSize = 12.sp,
                        color = White.copy(alpha = 0.85f),
                        lineHeight = 17.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, White.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                        ) {
                            Text("Cancel", fontSize = 12.sp, color = White)
                        }

                        Button(
                            onClick = onConfirm,
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(36.dp)
                        ) {
                            Text("Yes, Change Date", fontWeight = FontWeight.Bold, color = White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// ── Crop Emoji Helper ────────────────────────────────────────────────────────
private fun getCropEmoji(name: String): String = when (name.lowercase().replace(" ", "")) {
    "carrot", "karot" -> "🥕"
    "stringbeans", "sitaw", "beans" -> "🫘"
    "eggplant", "talong" -> "🍆"
    "tomato", "kamatis" -> "🍅"
    "onion", "sibuyas" -> "🧅"
    "pumpkin", "squash", "kalabasa" -> "🎃"
    "corn", "mais" -> "🌽"
    "cabbage", "repolyo" -> "🥬"
    "pechay" -> "🥬"
    "ampalaya" -> "🥒"
    "okra" -> "🌿"
    "sili", "chilipepper", "chili" -> "🌶️"
    "cucumber", "pipino" -> "🥒"
    "kangkong" -> "🥬"
    "lettuce" -> "🥗"
    else -> "🌱"
}

// ── HARVEST COMPLETION MODAL ────────────────────────────────────────────────
@Composable
private fun HarvestCompletionModal(
    crop: MonitoredPlant,
    onDismiss: () -> Unit,
    onConfirmHarvest: (yieldKg: Float?, notes: String?) -> Unit
) {
    var yieldText by remember { mutableStateOf("") }
    var notesText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1B2317),
            border = BorderStroke(1.5.dp, Color(0xFFD48806)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🌾 Record Harvest: ${crop.cropName}",
                        fontWeight = FontWeight.Bold,
                        color = White,
                        fontSize = 15.sp
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = White)
                    }
                }

                Text(
                    text = "Confirm actual crop harvest for ${crop.plotLabel}. This action will record your harvest history and clear the plot for your next planting cycle.",
                    fontSize = 11.sp,
                    color = White.copy(alpha = 0.7f)
                )

                // Yield (kg) Input
                Text("Recorded Yield (kg):", fontSize = 11.sp, color = White, fontWeight = FontWeight.Bold)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF2A3424),
                    border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BasicTextField(
                        value = yieldText,
                        onValueChange = { yieldText = it },
                        singleLine = true,
                        textStyle = TextStyle(color = White, fontSize = 12.sp),
                        cursorBrush = SolidColor(ForestGreen),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.padding(10.dp)) {
                                if (yieldText.isEmpty()) {
                                    Text("Enter yield in kilograms (e.g., 25.5)", color = White.copy(alpha = 0.45f), fontSize = 12.sp)
                                }
                                innerTextField()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Notes Input
                Text("Harvest Notes (Optional):", fontSize = 11.sp, color = White, fontWeight = FontWeight.Bold)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF2A3424),
                    border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BasicTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        singleLine = false,
                        textStyle = TextStyle(color = White, fontSize = 12.sp),
                        cursorBrush = SolidColor(ForestGreen),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.padding(10.dp)) {
                                if (notesText.isEmpty()) {
                                    Text("Enter notes (e.g., First batch high quality)", color = White.copy(alpha = 0.45f), fontSize = 12.sp)
                                }
                                innerTextField()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = White.copy(alpha = 0.7f))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val yield = yieldText.toFloatOrNull()
                            onConfirmHarvest(yield, notesText)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD48806)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Confirm & Record Harvest 🌾", fontWeight = FontWeight.Bold, color = White, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
