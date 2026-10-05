package com.maptanim.app.features.library.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.maptanim.app.data.datasource.CropMetadataAssetDataSource
import com.maptanim.app.domain.model.Crop
import com.maptanim.app.navigation.Routes
import com.maptanim.app.features.home.screen.MainBottomNavBar
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.White

// ─── Theme Colors ──────────────────────────────────────────────────────────
private val MonitoringBg = Color(0xFF10160F)
private val MonitoringSurface = Color(0xFF141C12)
private val MonitoringCard = Color(0xFF182216)
private val MonitoringCardAlt = Color(0xFF1F2B1C)
private val MonitoringBorder = Color(0xFF2B3A26)
private val MonitoringBorderLight = Color(0xFF3C5035)
private val MonitoringGreenLight = Color(0xFF81C784)
private val AccentGold = Color(0xFFFFB74D)

enum class LibraryCategory(val id: String, val label: String, val icon: ImageVector) {
    ALL("ALL", "All Vegetables", Icons.Default.LocalFlorist),
    FRUIT("FRUIT", "Solanaceous & Fruit", Icons.Default.Spa),
    CUCURBIT("CUCURBIT", "Cucurbits & Gourds", Icons.Default.Eco),
    LEAFY("LEAFY", "Leafy Greens", Icons.Default.Grass),
    LEGUME("LEGUME", "Legumes & Beans", Icons.Default.Yard),
    ROOT("ROOT", "Root & Tubers", Icons.Default.Park),
    BULB("BULB", "Bulb & Stem", Icons.Default.Agriculture)
}

enum class AgronomicTab(val tabNumber: Int, val title: String, val icon: ImageVector) {
    PREPARATION(1, "Preparation", Icons.Default.Landscape),
    PLANTING(2, "Planting", Icons.Default.Spa),
    CARE_MAINTENANCE(3, "Care & Maintenance", Icons.Default.WaterDrop),
    HARVEST_METHOD(4, "Harvest Method", Icons.Default.ShoppingBasket)
}

@Composable
fun VegetablesScreen(
    navController: NavController,
    initialCropName: String? = null,
    viewModel: VegetablesViewModel = viewModel()
) {
    val allCrops by viewModel.crops.collectAsState()
    val context = LocalContext.current

    var selectedCrop by remember { mutableStateOf<Crop?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(LibraryCategory.ALL) }
    var isCategoryDropdownOpen by remember { mutableStateOf(false) }

    // Direct deep-link: If initialCropName is provided, open that crop's overview dialog/view
    LaunchedEffect(allCrops, initialCropName) {
        if (!initialCropName.isNullOrBlank() && allCrops.isNotEmpty()) {
            val matching = allCrops.firstOrNull {
                it.name.equals(initialCropName, ignoreCase = true) ||
                it.localName?.equals(initialCropName, ignoreCase = true) == true
            }
            if (matching != null) {
                selectedCrop = matching
            }
        }
    }

    val filteredCrops = remember(allCrops, searchQuery, selectedCategory) {
        allCrops.filter { crop ->
            val matchesCategory = when (selectedCategory) {
                LibraryCategory.ALL -> true
                LibraryCategory.FRUIT -> crop.category.contains("FRUIT", ignoreCase = true) || crop.category.contains("SOLANACEOUS", ignoreCase = true)
                LibraryCategory.CUCURBIT -> crop.category.contains("CUCURBIT", ignoreCase = true) || crop.name.contains("Ampalaya", ignoreCase = true) || crop.name.contains("Pipino", ignoreCase = true) || crop.name.contains("Kalabasa", ignoreCase = true)
                LibraryCategory.LEAFY -> crop.category.contains("LEAFY", ignoreCase = true) || crop.name.contains("Pechay", ignoreCase = true) || crop.name.contains("Kangkong", ignoreCase = true) || crop.name.contains("Lettuce", ignoreCase = true)
                LibraryCategory.LEGUME -> crop.category.contains("LEGUME", ignoreCase = true) || crop.name.contains("Sitaw", ignoreCase = true) || crop.name.contains("Bean", ignoreCase = true)
                LibraryCategory.ROOT -> crop.category.contains("ROOT", ignoreCase = true) || crop.name.contains("Carrot", ignoreCase = true) || crop.name.contains("Radish", ignoreCase = true)
                LibraryCategory.BULB -> crop.category.contains("BULB", ignoreCase = true) || crop.name.contains("Onion", ignoreCase = true) || crop.name.contains("Garlic", ignoreCase = true)
            }
            val matchesQuery = searchQuery.isBlank() ||
                    crop.name.contains(searchQuery, ignoreCase = true) ||
                    (crop.localName?.contains(searchQuery, ignoreCase = true) == true) ||
                    (crop.botanicalName?.contains(searchQuery, ignoreCase = true) == true)

            matchesCategory && matchesQuery
        }
    }

    Scaffold(
        containerColor = MonitoringBg,
        bottomBar = {
            MainBottomNavBar(
                selectedRoute = Routes.LIBRARY,
                onNavigate = { route ->
                    if (route != Routes.LIBRARY) {
                        navController.navigate(route) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedCrop == null) {
                // ─── 1. BROWSE CROP LIBRARY (3-COLUMN GRID) ──────────────────
                CropLibraryCatalogView(
                    crops = filteredCrops,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    selectedCategory = selectedCategory,
                    isCategoryDropdownOpen = isCategoryDropdownOpen,
                    onToggleDropdown = { isCategoryDropdownOpen = !isCategoryDropdownOpen },
                    onSelectCategory = {
                        selectedCategory = it
                        isCategoryDropdownOpen = false
                    },
                    onSelectCrop = { selectedCrop = it }
                )
            } else {
                // ─── 2. CROP OVERVIEW & 4-TAB AGRONOMIC DETAIL VIEW ─────────
                val guide = remember(selectedCrop) {
                    VegetableGuideProvider.getGuideForCrop(selectedCrop!!, context)
                }
                CropOverviewDetailView(
                    guide = guide,
                    onBack = { selectedCrop = null }
                )
            }
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// ─── 1. CROP LIBRARY CATALOG VIEW (3 COLUMNS, NO READING ONLY / DATES) ──────
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun CropLibraryCatalogView(
    crops: List<Crop>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: LibraryCategory,
    isCategoryDropdownOpen: Boolean,
    onToggleDropdown: () -> Unit,
    onSelectCategory: (LibraryCategory) -> Unit,
    onSelectCrop: (Crop) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        // Clean Title Header (NO "Reading Only" badge)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Crop Library",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Text(
                    text = "Agronomic reference catalog & management protocols",
                    fontSize = 11.sp,
                    color = Color(0xFFA0B09A)
                )
            }

            // Category Dropdown Button with Icon
            Box {
                Surface(
                    onClick = onToggleDropdown,
                    shape = RoundedCornerShape(8.dp),
                    color = MonitoringCard,
                    border = BorderStroke(1.dp, MonitoringBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = selectedCategory.icon,
                            contentDescription = null,
                            tint = MonitoringGreenLight,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = selectedCategory.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = White
                        )
                        Icon(
                            imageVector = if (isCategoryDropdownOpen) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color(0xFFA0B09A),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = isCategoryDropdownOpen,
                    onDismissRequest = onToggleDropdown,
                    modifier = Modifier.background(MonitoringCard)
                ) {
                    LibraryCategory.entries.forEach { cat ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = cat.icon,
                                        contentDescription = null,
                                        tint = if (cat == selectedCategory) MonitoringGreenLight else Color(0xFFA0B09A),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = cat.label,
                                        color = if (cat == selectedCategory) MonitoringGreenLight else White,
                                        fontSize = 12.sp,
                                        fontWeight = if (cat == selectedCategory) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            },
                            onClick = { onSelectCategory(cat) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Search Bar
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MonitoringCard,
            border = BorderStroke(1.dp, MonitoringBorder),
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MonitoringGreenLight,
                    modifier = Modifier.size(16.dp)
                )
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Search crops by name or local name...",
                            fontSize = 12.sp,
                            color = Color(0xFF6E7E6A)
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        singleLine = true,
                        textStyle = TextStyle(color = White, fontSize = 12.sp),
                        cursorBrush = SolidColor(MonitoringGreenLight),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchQueryChange("") },
                        modifier = Modifier.size(18.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = Color(0xFFA0B09A),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Crops 3-Column Grid ─────────────────────────────────────────────
        if (crops.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No crops match your filter.",
                    color = Color(0xFFA0B09A),
                    fontSize = 13.sp
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(crops, key = { it.id }) { crop ->
                    Crop3ColumnCardItem(crop = crop, onClick = { onSelectCrop(crop) })
                }
            }
        }
    }
}

/**
 * 3-Column Clean Crop Card Item
 * Omits harvest dates and "Every 2d" watering per user request.
 */
@Composable
private fun Crop3ColumnCardItem(
    crop: Crop,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = MonitoringCard,
        border = BorderStroke(1.dp, MonitoringBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            // Crop Thumbnail Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0C130B)),
                contentAlignment = Alignment.Center
            ) {
                val imageUrl = CropMetadataAssetDataSource.resolveCropImage(crop)
                AsyncImage(
                    model = imageUrl,
                    contentDescription = crop.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Crop Name (Bold, 12sp)
            Text(
                text = crop.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Local Name (Italic, 10sp)
            val local = crop.localName
            if (!local.isNullOrBlank() && !local.equals(crop.name, ignoreCase = true)) {
                Text(
                    text = local,
                    fontSize = 10.sp,
                    color = MonitoringGreenLight,
                    fontStyle = FontStyle.Italic,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Clean Category Pill
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFF141E12)
            ) {
                Text(
                    text = crop.category.take(8).uppercase(),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFA0B09A),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// ─── 2. CROP OVERVIEW & DEEP AGRONOMIC DETAIL VIEW ─────────────────────────
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun CropOverviewDetailView(
    guide: VegetableAgronomicGuide,
    onBack: () -> Unit
) {
    val crop = guide.crop

    // Interactive agronomic configuration states
    var selectedMethod by remember { mutableStateOf("Transplanting") }
    var selectedApproach by remember { mutableStateOf("Organic") }
    var selectedTab by remember { mutableStateOf(AgronomicTab.PREPARATION) }
    var selectedVarietyIndex by remember { mutableIntStateOf(0) }
    var selectedStageIndex by remember { mutableIntStateOf(0) }
    var expandedCompanion by remember { mutableStateOf<String?>(null) }
    var expandedPestIndex by remember { mutableStateOf<Int?>(0) }
    var activeFactDialog by remember { mutableStateOf<Pair<String, String>?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MonitoringBg)
    ) {
        // ── Compact Top Bar with Back Button (NO extra statusBarsPadding) ──
        Surface(
            color = MonitoringSurface,
            border = BorderStroke(1.dp, MonitoringBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Library",
                        tint = White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${crop.name} ${if (!crop.localName.isNullOrBlank() && !crop.localName.equals(crop.name, ignoreCase = true)) "(${crop.localName})" else ""}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = guide.overview.botanicalName,
                        fontSize = 10.sp,
                        fontStyle = FontStyle.Italic,
                        color = MonitoringGreenLight
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ForestGreen.copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, ForestGreen)
                ) {
                    Text(
                        text = crop.category,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MonitoringGreenLight,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Section 1: Hero Identity & Method/Approach Configuration ───
            item {
                CropHeroIdentityCard(
                    crop = crop,
                    guide = guide,
                    selectedMethod = selectedMethod,
                    onSelectMethod = { selectedMethod = it },
                    selectedApproach = selectedApproach,
                    onSelectApproach = { selectedApproach = it },
                    onFactClick = { title, content -> activeFactDialog = title to content }
                )
            }

            // ── Section 2: Philippine Commercial Varieties (Clickable) ──────
            item {
                CropVarietiesSection(
                    varieties = guide.varieties,
                    selectedIndex = selectedVarietyIndex,
                    onSelectVariety = { selectedVarietyIndex = it }
                )
            }

            // ── Section 3: Companion Planting Matrix & Intercropping ────────
            item {
                CropCompanionsAndIntercroppingSection(
                    companionInfo = guide.companionPlants,
                    intercroppingInfo = guide.intercropping,
                    expandedCompanion = expandedCompanion,
                    onToggleCompanion = { name ->
                        expandedCompanion = if (expandedCompanion == name) null else name
                    }
                )
            }

            // ── Section 4: Growth Stages & Phenology Timeline (Clickable) ───
            item {
                CropGrowthStagesTimelineSection(
                    stages = guide.growthStages,
                    selectedIndex = selectedStageIndex,
                    onSelectStage = { selectedStageIndex = it }
                )
            }

            // ── Section 5: Number-Based Agronomic Protocols Tabs (1 to 4) ───
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "AGRONOMIC PROTOCOLS & FIELD PRACTICES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA0B09A),
                        letterSpacing = 0.5.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AgronomicTab.entries.forEach { tab ->
                            val isSelected = tab == selectedTab
                            Surface(
                                onClick = { selectedTab = tab },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) ForestGreen else MonitoringCard,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) MonitoringGreenLight else MonitoringBorder
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isSelected) White else Color(0xFF243020),
                                        modifier = Modifier.size(18.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${tab.tabNumber}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) ForestGreen else MonitoringGreenLight
                                            )
                                        }
                                    }

                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) White else Color(0xFFA0B09A),
                                        modifier = Modifier.size(13.dp)
                                    )

                                    Text(
                                        text = tab.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) White else Color(0xFFA0B09A)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Section 6: Protocol Content Depending on Selected Tab ───────
            item {
                when (selectedTab) {
                    AgronomicTab.PREPARATION -> TabPreparationContent(
                        guide = guide,
                        isOrganic = selectedApproach == "Organic"
                    )
                    AgronomicTab.PLANTING -> TabPlantingContent(
                        guide = guide,
                        plantingMethod = selectedMethod
                    )
                    AgronomicTab.CARE_MAINTENANCE -> TabCareMaintenanceContent(
                        guide = guide,
                        isOrganic = selectedApproach == "Organic",
                        expandedPestIndex = expandedPestIndex,
                        onTogglePest = { idx ->
                            expandedPestIndex = if (expandedPestIndex == idx) null else idx
                        }
                    )
                    AgronomicTab.HARVEST_METHOD -> TabHarvestMethodContent(
                        guide = guide
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Modal Dialog for Quick Facts (Regional, Culinary, Economics)
    activeFactDialog?.let { (title, content) ->
        AlertDialog(
            onDismissRequest = { activeFactDialog = null },
            containerColor = MonitoringCard,
            title = {
                Text(
                    text = title,
                    color = White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = content,
                    color = Color(0xFFD0DDD0),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { activeFactDialog = null }) {
                    Text("Close", color = MonitoringGreenLight, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// ────────────────────────────────────────────────────────────────────────────
// ─── CROP HERO IDENTITY CARD ────────────────────────────────────────────────
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun CropHeroIdentityCard(
    crop: Crop,
    guide: VegetableAgronomicGuide,
    selectedMethod: String,
    onSelectMethod: (String) -> Unit,
    selectedApproach: String,
    onSelectApproach: (String) -> Unit,
    onFactClick: (String, String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MonitoringCard),
        border = BorderStroke(1.dp, MonitoringBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row 1: Names & Thumbnail
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = crop.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Text(
                        text = "Local: ${crop.localName ?: crop.name} · ${guide.overview.family}",
                        fontSize = 11.sp,
                        color = Color(0xFFA0B09A)
                    )
                    Text(
                        text = "Scientific: ${guide.overview.botanicalName}",
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic,
                        color = MonitoringGreenLight
                    )
                }

                val imageUrl = CropMetadataAssetDataSource.resolveCropImage(crop)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF0C130B),
                    border = BorderStroke(1.dp, MonitoringBorderLight),
                    modifier = Modifier.size(58.dp)
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = crop.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    )
                }
            }

            HorizontalDivider(color = MonitoringBorder, thickness = 0.8.dp)

            // Agricultural Summary
            Text(
                text = guide.overview.summary,
                fontSize = 12.sp,
                color = Color(0xFFC8D6C4),
                lineHeight = 17.sp
            )

            // Clickable Quick Fact Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    onClick = { onFactClick("Regional Suitability", guide.overview.regionalSuitability) },
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF142014),
                    border = BorderStroke(1.dp, Color(0xFF284826))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = MonitoringGreenLight, modifier = Modifier.size(12.dp))
                        Text("Regions", fontSize = 10.sp, color = White, fontWeight = FontWeight.SemiBold)
                    }
                }

                Surface(
                    onClick = { onFactClick("Culinary & Cultural Uses", guide.overview.culinaryUses) },
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF241C14),
                    border = BorderStroke(1.dp, Color(0xFF503824))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Restaurant, contentDescription = null, tint = AccentGold, modifier = Modifier.size(12.dp))
                        Text("Culinary Uses", fontSize = 10.sp, color = White, fontWeight = FontWeight.SemiBold)
                    }
                }

                Surface(
                    onClick = { onFactClick("Agricultural Importance", guide.overview.agriculturalImportance) },
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF161E28),
                    border = BorderStroke(1.dp, Color(0xFF2A3E54))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFF90CAF9), modifier = Modifier.size(12.dp))
                        Text("Economics", fontSize = 10.sp, color = White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Interactive Method & Approach Selector
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "CUSTOMIZE PROTOCOL SPECIFICATIONS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA0B09A),
                    letterSpacing = 0.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Method Toggle
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFF121910), RoundedCornerShape(8.dp))
                            .padding(2.dp)
                    ) {
                        listOf("Transplanting", "Direct Seeding").forEach { m ->
                            val isSel = m == selectedMethod
                            Surface(
                                onClick = { onSelectMethod(m) },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) ForestGreen else Color.Transparent,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 5.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = m,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSel) White else Color(0xFFA0B09A)
                                    )
                                }
                            }
                        }
                    }

                    // Approach Toggle
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFF121910), RoundedCornerShape(8.dp))
                            .padding(2.dp)
                    ) {
                        listOf("Organic", "Conventional").forEach { a ->
                            val isSel = a == selectedApproach
                            Surface(
                                onClick = { onSelectApproach(a) },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) ForestGreen else Color.Transparent,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 5.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = a,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSel) White else Color(0xFFA0B09A)
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

// ────────────────────────────────────────────────────────────────────────────
// ─── COMMERCIAL VARIETIES SECTION (INTERACTIVE & CLICKABLE) ─────────────────
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun CropVarietiesSection(
    varieties: List<VarietyDetail>,
    selectedIndex: Int,
    onSelectVariety: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MonitoringCard),
        border = BorderStroke(1.dp, MonitoringBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "COMMERCIAL VARIETIES (PHILIPPINES)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = White,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Tap to inspect traits",
                    fontSize = 10.sp,
                    color = MonitoringGreenLight
                )
            }

            // Variety Cards Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                varieties.forEachIndexed { idx, v ->
                    val isSelected = idx == selectedIndex
                    Surface(
                        onClick = { onSelectVariety(idx) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) MonitoringCardAlt else Color(0xFF141C12),
                        border = BorderStroke(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) MonitoringGreenLight else MonitoringBorder
                        ),
                        modifier = Modifier.width(170.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = v.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) White else Color(0xFFC0CDC0)
                            )
                            Text(
                                text = "${v.daysToHarvest} days · ${v.optimalSeason}",
                                fontSize = 10.sp,
                                color = AccentGold,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                            Text(
                                text = v.diseaseResistance,
                                fontSize = 9.sp,
                                color = Color(0xFFA0B09A),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Selected Variety Detailed Profile
            val active = varieties.getOrNull(selectedIndex) ?: varieties.firstOrNull()
            if (active != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF131D12),
                    border = BorderStroke(1.dp, Color(0xFF263C24)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Cultivar Profile: ${active.name}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MonitoringGreenLight
                            )
                            Text(
                                text = "Maturity: ${active.daysToHarvest} DAT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentGold
                            )
                        }

                        Text(
                            text = "Fruit & Market Traits: ${active.characteristics}",
                            fontSize = 11.sp,
                            color = Color(0xFFD0DDD0),
                            lineHeight = 15.sp
                        )

                        Text(
                            text = "Disease Tolerance: ${active.diseaseResistance}",
                            fontSize = 11.sp,
                            color = Color(0xFFB0C4AE),
                            lineHeight = 15.sp
                        )

                        // Phenology Stage Days Breakdown (if available)
                        active.stageDays?.let { stages ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 2.dp)
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                stages.forEach { (stg, days) ->
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF1E2C1C)
                                    ) {
                                        Text(
                                            text = "$stg: ${days}d",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MonitoringGreenLight,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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

// ────────────────────────────────────────────────────────────────────────────
// ─── COMPANION PLANTING & INTERCROPPING MATRIX ───────────────────────────────
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun CropCompanionsAndIntercroppingSection(
    companionInfo: CompanionInfo,
    intercroppingInfo: IntercroppingInfo,
    expandedCompanion: String?,
    onToggleCompanion: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MonitoringCard),
        border = BorderStroke(1.dp, MonitoringBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "COMPANION PLANTING & BIO-DEFENSE MATRIX",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = White,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Click to inspect",
                    fontSize = 10.sp,
                    color = MonitoringGreenLight
                )
            }

            // Two Columns: Beneficial vs Avoid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Beneficial Column
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF142416),
                    border = BorderStroke(1.dp, Color(0xFF2E5432)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = MonitoringGreenLight, modifier = Modifier.size(12.dp))
                            Text("BENEFICIAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MonitoringGreenLight)
                        }

                        companionInfo.beneficialCompanions.forEach { comp ->
                            val isExp = expandedCompanion == comp
                            Surface(
                                onClick = { onToggleCompanion(comp) },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isExp) Color(0xFF1F3A22) else Color(0xFF182C1A),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val img = AgronomicAssetHelper.resolveCompanionCropImage(comp)
                                    if (img != null) {
                                        AsyncImage(
                                            model = img,
                                            contentDescription = comp,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else {
                                        Icon(Icons.Default.Spa, contentDescription = null, tint = MonitoringGreenLight, modifier = Modifier.size(14.dp))
                                    }
                                    Text(
                                        text = comp,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = White
                                    )
                                }
                            }
                        }
                    }
                }

                // Avoid / Incompatible Column
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF281C16),
                    border = BorderStroke(1.dp, Color(0xFF5E3A24)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFFF8A65), modifier = Modifier.size(12.dp))
                            Text("INCOMPATIBLE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF8A65))
                        }

                        companionInfo.plantsToAvoid.forEach { comp ->
                            val isExp = expandedCompanion == comp
                            Surface(
                                onClick = { onToggleCompanion(comp) },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isExp) Color(0xFF3E281C) else Color(0xFF322018),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val img = AgronomicAssetHelper.resolveCompanionCropImage(comp)
                                    if (img != null) {
                                        AsyncImage(
                                            model = img,
                                            contentDescription = comp,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF8A65), modifier = Modifier.size(14.dp))
                                    }
                                    Text(
                                        text = comp,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Agronomic Mechanism Explanation (Appears when user clicks a companion or shows overall)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF131B12),
                border = BorderStroke(1.dp, MonitoringBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (expandedCompanion != null) "Rationale for $expandedCompanion:" else "Biological Synergy Rationale:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MonitoringGreenLight
                    )
                    Text(
                        text = "• Beneficial: ${companionInfo.companionBenefits}",
                        fontSize = 10.sp,
                        color = Color(0xFFC0CDC0),
                        lineHeight = 14.sp
                    )
                    Text(
                        text = "• Risks: ${companionInfo.avoidReasons}",
                        fontSize = 10.sp,
                        color = Color(0xFFCCA898),
                        lineHeight = 14.sp
                    )
                }
            }

            // Intercropping Strategy Card
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF162015),
                border = BorderStroke(1.dp, Color(0xFF2E462B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Yard, contentDescription = null, tint = MonitoringGreenLight, modifier = Modifier.size(12.dp))
                        Text(
                            text = "INTERCROPPING OPPORTUNITY: ${intercroppingInfo.recommendedCrops.joinToString(", ")}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    }
                    Text(
                        text = "Spatial Arrangement: ${intercroppingInfo.spatialLayout}",
                        fontSize = 10.sp,
                        color = Color(0xFFB0C4AE),
                        lineHeight = 14.sp
                    )
                    Text(
                        text = "Management: ${intercroppingInfo.managementAdvice}",
                        fontSize = 10.sp,
                        color = Color(0xFFA0B49E),
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// ─── GROWTH STAGES & PHENOLOGY TIMELINE SECTION ─────────────────────────────
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun CropGrowthStagesTimelineSection(
    stages: List<GrowthStageItem>,
    selectedIndex: Int,
    onSelectStage: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MonitoringCard),
        border = BorderStroke(1.dp, MonitoringBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GROWTH STAGES & PHENOLOGY TIMELINE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = White,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Tap stage",
                    fontSize = 10.sp,
                    color = MonitoringGreenLight
                )
            }

            // Horizontal Stage Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                stages.forEachIndexed { idx, stage ->
                    val isSel = idx == selectedIndex
                    Surface(
                        onClick = { onSelectStage(idx) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSel) ForestGreen else MonitoringCardAlt,
                        border = BorderStroke(
                            1.dp,
                            if (isSel) MonitoringGreenLight else MonitoringBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "${stage.stageNumber}.",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) White else MonitoringGreenLight
                            )
                            Text(
                                text = stage.stageName.take(16),
                                fontSize = 10.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) White else Color(0xFFA0B09A)
                            )
                        }
                    }
                }
            }

            // Expanded Active Stage Card
            val activeStage = stages.getOrNull(selectedIndex) ?: stages.firstOrNull()
            if (activeStage != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF131D12),
                    border = BorderStroke(1.dp, Color(0xFF263C24)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Stage ${activeStage.stageNumber}: ${activeStage.stageName}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MonitoringGreenLight
                            )
                            Text(
                                text = activeStage.durationDays,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentGold
                            )
                        }
                        Text(
                            text = "Phenology: ${activeStage.description}",
                            fontSize = 11.sp,
                            color = Color(0xFFD0DDD0),
                            lineHeight = 15.sp
                        )
                        Text(
                            text = "Farmer Priority: ${activeStage.farmerAction}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF81C784),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// ─── 4 TAB SPECIFICATION & PROTOCOL CONTENTS ────────────────────────────────
// ────────────────────────────────────────────────────────────────────────────

// ── Tab 1: Preparation ──────────────────────────────────────────────────────
@Composable
private fun TabPreparationContent(guide: VegetableAgronomicGuide, isOrganic: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Specifications Grid (2 Columns, 2 Rows)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SpecMetricCard(
                title = "SOIL TYPE",
                value = guide.soil.idealSoilTypes,
                icon = Icons.Default.Terrain,
                modifier = Modifier.weight(1f)
            )
            SpecMetricCard(
                title = "OPTIMAL PH",
                value = "pH ${guide.soil.optimalPh}",
                icon = Icons.Default.Science,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SpecMetricCard(
                title = "BED DIMENSIONS",
                value = "1.0m width × 20–30cm ht",
                icon = Icons.Default.Straighten,
                modifier = Modifier.weight(1f)
            )
            SpecMetricCard(
                title = "BASAL FERTILIZATION",
                value = if (isOrganic) "2–3 kg/m² compost" else guide.fertilization.basalApplication.take(24),
                icon = Icons.Default.Eco,
                modifier = Modifier.weight(1f)
            )
        }

        // Verified Local Soil Asset Reference Card
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MonitoringCard),
            border = BorderStroke(1.dp, MonitoringBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val soilImg = AgronomicAssetHelper.resolveSoilImage(guide.soil.idealSoilTypes)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0C130B),
                    border = BorderStroke(1.dp, MonitoringBorderLight),
                    modifier = Modifier.size(54.dp)
                ) {
                    AsyncImage(
                        model = soilImg,
                        contentDescription = "Ideal Soil Type",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Recommended Soil Profile",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MonitoringGreenLight
                    )
                    Text(
                        text = "Drainage: ${guide.soil.drainage}",
                        fontSize = 10.sp,
                        color = Color(0xFFD0DDD0),
                        lineHeight = 14.sp
                    )
                    Text(
                        text = "Organic Matter: ${guide.soil.organicMatter}",
                        fontSize = 10.sp,
                        color = Color(0xFFA0B09A),
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // Protocol Checklist
        ProtocolChecklistCard(
            title = "SOIL PREPARATION & FIELD LAYOUT PROTOCOL",
            steps = listOf(
                "Plow and rotovate bed soil to 20–30 cm depth to allow unrestricted root aeration.",
                "Incorporate ${if (isOrganic) "well-cured compost or vermicast at 2–3 kg/m²" else "complete 14-14-14 fertilizer with basal organic matter"} 10–14 days prior to planting.",
                "Ensure drainage canals between raised beds are at least 30 cm deep to prevent waterlogging during monsoon showers.",
                "Level bed surface and apply silver-black plastic mulch or rice straw to suppress weeds and conserve soil moisture."
            )
        )

        // Weather & Seasonal Risk Card
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF131D12),
            border = BorderStroke(1.dp, MonitoringBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "CLIMATE & SEASONAL PROFILE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGold
                )
                Text(
                    text = "Optimal Temperature: ${guide.growingSeason.optimalTemperature} · Peak: ${guide.growingSeason.peakMonths}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = White
                )
                Text(
                    text = "Weather Precautions: ${guide.growingSeason.climateRisks}",
                    fontSize = 10.sp,
                    color = Color(0xFFC0CDC0),
                    lineHeight = 14.sp
                )
            }
        }
    }
}

// ── Tab 2: Planting ─────────────────────────────────────────────────────────
@Composable
private fun TabPlantingContent(guide: VegetableAgronomicGuide, plantingMethod: String) {
    val isDirect = plantingMethod == "Direct Seeding"

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SpecMetricCard(
                title = "IN-ROW SPACING",
                value = guide.planting.plantSpacing,
                icon = Icons.Default.FormatLineSpacing,
                modifier = Modifier.weight(1f)
            )
            SpecMetricCard(
                title = "BETWEEN-ROW",
                value = guide.planting.rowSpacing,
                icon = Icons.Default.ViewColumn,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SpecMetricCard(
                title = "SEEDING DEPTH",
                value = guide.planting.plantingDepth,
                icon = Icons.Default.VerticalAlignBottom,
                modifier = Modifier.weight(1f)
            )
            SpecMetricCard(
                title = if (isDirect) "GERMINATION" else "TRANSPLANT AGE",
                value = if (isDirect) guide.planting.germinationDays else guide.planting.transplantAge,
                icon = Icons.Default.Schedule,
                modifier = Modifier.weight(1f)
            )
        }

        ProtocolChecklistCard(
            title = "PLANTING EXECUTION (${plantingMethod.uppercase()})",
            steps = if (isDirect) listOf(
                "Create shallow seeding furrows at ${guide.planting.plantingDepth} depth.",
                "Sow 2–3 seeds per hill at ${guide.planting.plantSpacing} intervals.",
                "Cover lightly with fine compost-soil mix and firm gently.",
                "Water lightly with fine mist; thin to 1 vigorous seedling per hill after 10–14 days."
            ) else listOf(
                "Harden nursery seedlings 5–7 days prior by gradually increasing sunlight and reducing water.",
                "Transplant during late afternoon (4:00 PM – 6:00 PM) to avoid direct sun wilt and transplant shock.",
                "Plant seedling root ball flush with soil level; avoid burying stems too deeply.",
                "Water immediately around each base with 250–500 mL water to settle roots."
            )
        )

        // Trellising & Staking Guide
        if (guide.planting.trellisingNeeded) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF131D12),
                border = BorderStroke(1.dp, MonitoringBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "TRELLISING & STAKING REQUIREMENT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MonitoringGreenLight
                    )
                    Text(
                        text = guide.planting.trellisingAdvice ?: "Erect bamboo stakes or A-frame trellis 2–3 weeks after transplanting. Tie main stems loosely with soft twine.",
                        fontSize = 10.sp,
                        color = Color(0xFFD0DDD0),
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

// ── Tab 3: Care & Maintenance ───────────────────────────────────────────────
@Composable
private fun TabCareMaintenanceContent(
    guide: VegetableAgronomicGuide,
    isOrganic: Boolean,
    expandedPestIndex: Int?,
    onTogglePest: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SpecMetricCard(
                title = "WATER FREQUENCY",
                value = guide.watering.frequency,
                icon = Icons.Default.WaterDrop,
                modifier = Modifier.weight(1f)
            )
            SpecMetricCard(
                title = "BEST TIME",
                value = guide.watering.bestTime,
                icon = Icons.Default.WbSunny,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SpecMetricCard(
                title = "TRELLIS / STAKING",
                value = if (guide.planting.trellisingNeeded) "Required (A-frame / Poles)" else "Not required",
                icon = Icons.Default.Straighten,
                modifier = Modifier.weight(1f)
            )
            SpecMetricCard(
                title = "APPROACH",
                value = if (isOrganic) "Bio-repellents & Compost" else "Synthetic IPM & Urea",
                icon = Icons.Default.HealthAndSafety,
                modifier = Modifier.weight(1f)
            )
        }

        ProtocolChecklistCard(
            title = "CROP CARE & MAINTENANCE PROTOCOL",
            steps = listOf(
                "Irrigation: Apply water at root base early morning; avoid splashing lower foliage to stop early fungal blight.",
                if (isOrganic) {
                    "Fertilization: Drench with Fermented Plant Juice (FPJ) or vermitea every 10–14 days during vegetative growth."
                } else {
                    "Fertilization: Side-dress with 46-0-0 (Urea) at 21 days after planting; apply 0-0-60 during flowering set."
                },
                "Staking & Pruning: Secure main stem to bamboo trellises; pinch off lower auxiliary suckers to concentrate fruit size.",
                "Weed Control: Maintain 3–5 cm rice straw mulch to shade out weed seedlings and conserve soil moisture."
            )
        )

        // ── PESTS & DISEASES DEFENSE (CLICKABLE WITH LOCAL ASSET IMAGES) ───
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MonitoringCard),
            border = BorderStroke(1.dp, MonitoringBorder),
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.BugReport, contentDescription = null, tint = Color(0xFFFF8A65), modifier = Modifier.size(14.dp))
                        Text(
                            text = "PESTS & DISEASES DEFENSE GUIDE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    }
                    Text(
                        text = "Tap to expand diagnosis",
                        fontSize = 9.sp,
                        color = MonitoringGreenLight
                    )
                }

                guide.pestsAndDiseases.forEachIndexed { idx, pest ->
                    val isExpanded = expandedPestIndex == idx
                    val pestImg = AgronomicAssetHelper.resolvePestImage(pest.name, pest.imageAsset)

                    Surface(
                        onClick = { onTogglePest(idx) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isExpanded) MonitoringCardAlt else Color(0xFF141C12),
                        border = BorderStroke(
                            1.dp,
                            if (isExpanded) MonitoringGreenLight else MonitoringBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (pestImg != null) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF0C130B),
                                        border = BorderStroke(1.dp, MonitoringBorderLight),
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        AsyncImage(
                                            model = pestImg,
                                            contentDescription = pest.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                } else {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF281C16),
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.BugReport, contentDescription = null, tint = Color(0xFFFF8A65), modifier = Modifier.size(20.dp))
                                        }
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = pest.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = White
                                    )
                                    Text(
                                        text = pest.type,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (pest.type.contains("Insect", ignoreCase = true)) AccentGold else Color(0xFFFF8A65)
                                    )
                                }

                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = Color(0xFFA0B09A),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Expanded Detailed Diagnosis & Management
                            if (isExpanded) {
                                HorizontalDivider(color = MonitoringBorder, thickness = 0.8.dp)

                                Text(
                                    text = "Visual Symptoms: ${pest.symptoms}",
                                    fontSize = 10.sp,
                                    color = Color(0xFFD0DDD0),
                                    lineHeight = 14.sp
                                )

                                Text(
                                    text = "Organic Control: ${pest.organicControl}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MonitoringGreenLight,
                                    lineHeight = 14.sp
                                )

                                pest.chemicalControl?.let { chem ->
                                    Text(
                                        text = "Conventional IPM: $chem",
                                        fontSize = 10.sp,
                                        color = Color(0xFF90CAF9),
                                        lineHeight = 14.sp
                                    )
                                }

                                Text(
                                    text = "Cultural Prevention: ${pest.prevention}",
                                    fontSize = 10.sp,
                                    color = Color(0xFFA0B09A),
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Tab 4: Harvest Method ───────────────────────────────────────────────────
@Composable
private fun TabHarvestMethodContent(guide: VegetableAgronomicGuide) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SpecMetricCard(
                title = "HARVEST TIME",
                value = guide.harvest.timeOfDay,
                icon = Icons.Default.WbTwilight,
                modifier = Modifier.weight(1f)
            )
            SpecMetricCard(
                title = "FREQUENCY",
                value = guide.harvest.frequency,
                icon = Icons.Default.EventRepeat,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SpecMetricCard(
                title = "TOOL",
                value = "Clean pruning shears",
                icon = Icons.Default.ContentCut,
                modifier = Modifier.weight(1f)
            )
            SpecMetricCard(
                title = "STORAGE TEMP",
                value = guide.postHarvest.optimalTemperature,
                icon = Icons.Default.Thermostat,
                modifier = Modifier.weight(1f)
            )
        }

        ProtocolChecklistCard(
            title = "HARVEST & POST-HARVEST PROTOCOL",
            steps = listOf(
                "Visual Maturity: ${guide.harvest.maturityIndicators}",
                "Harvesting Technique: Cut pedicel cleanly leaving 1 cm stem attached; do not yank fruits to avoid vine tearing.",
                "Timing: Harvest between 6:00 AM – 9:00 AM before midday field heat causes produce dehydration.",
                "Handling & Curing: Place produce directly in shaded crates; grade and sort marketable yields by size and firmness."
            )
        )

        // Visual Maturity Indicators List (Clickable / Illustrated)
        if (guide.harvest.indicatorsList.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF131D12),
                border = BorderStroke(1.dp, MonitoringBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "MATURITY INDICATOR STAGES",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold
                    )
                    guide.harvest.indicatorsList.forEach { ind ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("•", color = MonitoringGreenLight, fontSize = 11.sp)
                            Text(
                                text = ind,
                                fontSize = 10.sp,
                                color = Color(0xFFD0DDD0),
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Post-Harvest & Storage Advisory
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF151F28),
            border = BorderStroke(1.dp, Color(0xFF2B4054)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "POST-HARVEST HANDLING & PACKAGING",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF90CAF9)
                )
                Text(
                    text = "Sorting & Grading: ${guide.postHarvest.sortingGrading}",
                    fontSize = 10.sp,
                    color = Color(0xFFD0DDD0),
                    lineHeight = 14.sp
                )
                Text(
                    text = "Storage RH: ${guide.postHarvest.relativeHumidity} · Shelf Life: ${guide.postHarvest.shelfLife}",
                    fontSize = 10.sp,
                    color = Color(0xFFA0B4C8),
                    lineHeight = 14.sp
                )
                Text(
                    text = "Packaging: ${guide.postHarvest.packagingTransport}",
                    fontSize = 10.sp,
                    color = Color(0xFFA0B4C8),
                    lineHeight = 14.sp
                )
            }
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// ─── REUSABLE SPECIFICATION AND PROTOCOL COMPONENTS ─────────────────────────
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun SpecMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MonitoringCard,
        border = BorderStroke(1.dp, MonitoringBorder),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF1B2818),
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MonitoringGreenLight,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA0B09A),
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = value,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ProtocolChecklistCard(
    title: String,
    steps: List<String>
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MonitoringCard),
        border = BorderStroke(1.dp, MonitoringBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MonitoringGreenLight,
                letterSpacing = 0.5.sp
            )

            steps.forEachIndexed { idx, step ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "${idx + 1}.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MonitoringGreenLight,
                        modifier = Modifier.width(16.dp)
                    )
                    Text(
                        text = step,
                        fontSize = 11.sp,
                        color = Color(0xFFD0DDD0),
                        lineHeight = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

