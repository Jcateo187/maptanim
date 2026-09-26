package com.maptanim.app.ui.screens.vegetables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.maptanim.app.data.datasource.CropMetadataAssetDataSource
import com.maptanim.app.domain.model.Crop
import com.maptanim.app.navigation.Routes
import com.maptanim.app.ui.screens.home.MainBottomNavBar
import com.maptanim.app.ui.theme.ForestGreen
import com.maptanim.app.ui.theme.White

// ─── Monitoring Dark Forest Colors ──────────────────────────────────────────
private val MonitoringBg = Color(0xFF10160F)
private val MonitoringSurface = Color(0xFF131D15)
private val MonitoringCard = Color(0xFF1B2317)
private val MonitoringCardElevated = Color(0xFF222F1F)
private val MonitoringBorder = Color(0xFF2E4D3E)
private val MonitoringGreenLight = Color(0xFF81C784)
private val Sunlight = Color(0xFFFFD54F)

@Composable
fun VegetablesScreen(
    navController: NavController,
    viewModel: VegetablesViewModel = viewModel()
) {
    val allCrops by viewModel.crops.collectAsState()
    val context = LocalContext.current

    var selectedCrop by remember { mutableStateOf<Crop?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }

    val categories = listOf("ALL", "FRUIT", "CUCURBIT", "LEAFY", "LEGUME", "ROOT")

    val filteredCrops = remember(allCrops, searchQuery, selectedCategory) {
        allCrops.filter { crop ->
            val matchesCategory = when (selectedCategory) {
                "ALL" -> true
                "FRUIT" -> crop.category.contains("FRUIT", ignoreCase = true) || crop.category.contains("SOLANACEOUS", ignoreCase = true)
                "CUCURBIT" -> crop.category.contains("CUCURBIT", ignoreCase = true) || crop.name.contains("Ampalaya", ignoreCase = true) || crop.name.contains("Pipino", ignoreCase = true) || crop.name.contains("Kalabasa", ignoreCase = true) || crop.name.contains("Pumpkin", ignoreCase = true)
                "LEAFY" -> crop.category.contains("LEAFY", ignoreCase = true) || crop.name.contains("Pechay", ignoreCase = true) || crop.name.contains("Kangkong", ignoreCase = true) || crop.name.contains("Lettuce", ignoreCase = true) || crop.name.contains("Cabbage", ignoreCase = true)
                "LEGUME" -> crop.category.contains("LEGUME", ignoreCase = true) || crop.name.contains("Sitaw", ignoreCase = true) || crop.name.contains("Bean", ignoreCase = true)
                "ROOT" -> crop.category.contains("ROOT", ignoreCase = true) || crop.name.contains("Carrot", ignoreCase = true) || crop.name.contains("Radish", ignoreCase = true) || crop.name.contains("Onion", ignoreCase = true)
                else -> crop.category.contains(selectedCategory, ignoreCase = true)
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
                selectedRoute = Routes.VEGETABLES,
                onNavigate = { route ->
                    if (route != Routes.VEGETABLES && route != Routes.LIBRARY) {
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
                // ─── 1. BROWSE VEGETABLES VIEW ──────────────────────────────
                VegetablesCatalogView(
                    crops = filteredCrops,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onSelectCategory = { selectedCategory = it },
                    onSelectCrop = { selectedCrop = it }
                )
            } else {
                // ─── 2. VEGETABLE FULL-SCREEN DETAIL GUIDE (13 SECTIONS) ────
                val guide = remember(selectedCrop) {
                    VegetableGuideProvider.getGuideForCrop(selectedCrop!!, context)
                }
                VegetableDetailGuideView(
                    guide = guide,
                    onBack = { selectedCrop = null }
                )
            }
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// ─── CATALOG VIEW ───────────────────────────────────────────────────────────
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun VegetablesCatalogView(
    crops: List<Crop>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    categories: List<String>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    onSelectCrop: (Crop) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Title & Subtitle Header
        Text(
            text = "Vegetables",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = White
        )
        Text(
            text = "Explore agricultural information about vegetables.",
            fontSize = 13.sp,
            color = White.copy(alpha = 0.65f),
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
        )

        // Search Bar
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MonitoringCard,
            border = BorderStroke(1.dp, MonitoringBorder),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MonitoringGreenLight,
                    modifier = Modifier.size(18.dp)
                )
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Search vegetables (e.g. Tomato, Pechay, Eggplant)...",
                            fontSize = 12.sp,
                            color = White.copy(alpha = 0.45f)
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        singleLine = true,
                        textStyle = TextStyle(color = White, fontSize = 13.sp),
                        cursorBrush = SolidColor(MonitoringGreenLight),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchQueryChange("") },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = White.copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Category Filter Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = cat == selectedCategory
                val label = when (cat) {
                    "ALL" -> "All Vegetables"
                    "FRUIT" -> "Solanaceous"
                    "CUCURBIT" -> "Cucurbits / Gourds"
                    "LEAFY" -> "Leafy Greens"
                    "LEGUME" -> "Legumes / Beans"
                    "ROOT" -> "Root Crops"
                    else -> cat
                }
                Surface(
                    onClick = { onSelectCategory(cat) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) ForestGreen else MonitoringCard,
                    border = BorderStroke(1.dp, if (isSelected) MonitoringGreenLight else MonitoringBorder.copy(alpha = 0.6f))
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) White else White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Vegetables Grid
        if (crops.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LocalFlorist,
                        contentDescription = null,
                        tint = White.copy(alpha = 0.3f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No vegetables match your search.",
                        color = White.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(crops, key = { it.id }) { crop ->
                    VegetableCardItem(crop = crop, onClick = { onSelectCrop(crop) })
                }
            }
        }
    }
}

@Composable
private fun VegetableCardItem(
    crop: Crop,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MonitoringCard,
        border = BorderStroke(1.dp, MonitoringBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // Vegetable Thumbnail & Category Pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0D160E)),
                contentAlignment = Alignment.Center
            ) {
                val imageUrl = CropMetadataAssetDataSource.resolveCropImage(crop)
                AsyncImage(
                    model = imageUrl,
                    contentDescription = crop.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                )

                // Category pill on top right
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                ) {
                    Text(
                        text = "${crop.daysToHarvest}d",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Sunlight,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Common Name & Local Name
            Text(
                text = crop.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = White,
                maxLines = 1
            )
            crop.localName?.let { local ->
                if (local.isNotBlank() && !local.equals(crop.name, ignoreCase = true)) {
                    Text(
                        text = local,
                        fontSize = 11.sp,
                        color = MonitoringGreenLight,
                        fontStyle = FontStyle.Italic,
                        maxLines = 1
                    )
                }
            }

            // Quick Parameters
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = Color(0xFF64B5F6),
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = "Every ${crop.wateringIntervalDays}d",
                        fontSize = 10.sp,
                        color = White.copy(alpha = 0.7f)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Guide",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MonitoringGreenLight
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MonitoringGreenLight,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// ─── 13-SECTION FULL-SCREEN DETAIL GUIDE VIEW ──────────────────────────────
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun VegetableDetailGuideView(
    guide: VegetableAgronomicGuide,
    onBack: () -> Unit
) {
    val crop = guide.crop
    val sections = listOf(
        "Overview" to "📋",
        "Varieties" to "🧬",
        "Growing Season" to "☀️",
        "Soil" to "🪴",
        "Planting" to "🌱",
        "Watering" to "💧",
        "Fertilization" to "🧪",
        "Growth Stages" to "📈",
        "Pests & Diseases" to "🐛",
        "Companion Plants" to "🤝",
        "Intercropping" to "🌾",
        "Harvest" to "🧺",
        "Post-Harvest" to "📦"
    )
    var selectedSectionIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // ── Header Navigation Bar (Back + Title) ────────────────────────────
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
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Vegetables",
                        tint = White
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${crop.name} ${if (!crop.localName.isNullOrBlank()) "(${crop.localName})" else ""}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Text(
                        text = "Comprehensive Agricultural Profile",
                        fontSize = 10.sp,
                        color = MonitoringGreenLight
                    )
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ForestGreen.copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, ForestGreen)
                ) {
                    Text(
                        text = crop.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MonitoringGreenLight,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // ── 13-Section Horizontal Quick Switcher ────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MonitoringSurface)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            sections.forEachIndexed { index, (title, emoji) ->
                val isSelected = index == selectedSectionIndex
                Surface(
                    onClick = { selectedSectionIndex = index },
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) ForestGreen else MonitoringCard,
                    border = BorderStroke(1.dp, if (isSelected) MonitoringGreenLight else MonitoringBorder.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(emoji, fontSize = 11.sp)
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) White else White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // ── Content Area with Hero + Selected Section ───────────────────────
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Profile Card
            item {
                VegetableHeroProfileCard(guide = guide)
            }

            // Display Selected Section Content
            item {
                when (selectedSectionIndex) {
                    0 -> SectionOverview(guide = guide)
                    1 -> SectionVarieties(guide = guide)
                    2 -> SectionGrowingSeason(guide = guide)
                    3 -> SectionSoil(guide = guide)
                    4 -> SectionPlanting(guide = guide)
                    5 -> SectionWatering(guide = guide)
                    6 -> SectionFertilization(guide = guide)
                    7 -> SectionGrowthStages(guide = guide)
                    8 -> SectionPestsAndDiseases(guide = guide)
                    9 -> SectionCompanionPlants(guide = guide)
                    10 -> SectionIntercropping(guide = guide)
                    11 -> SectionHarvest(guide = guide)
                    12 -> SectionPostHarvest(guide = guide)
                }
            }
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// ─── HERO PROFILE CARD ──────────────────────────────────────────────────────
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun VegetableHeroProfileCard(guide: VegetableAgronomicGuide) {
    val crop = guide.crop
    val imageUrl = CropMetadataAssetDataSource.resolveCropImage(crop)

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MonitoringCard,
        border = BorderStroke(1.dp, MonitoringBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0D160E))
                        .border(1.dp, MonitoringBorder, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = crop.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = crop.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    crop.localName?.let { local ->
                        Text(
                            text = "Local: $local",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MonitoringGreenLight
                        )
                    }
                    Text(
                        text = guide.overview.botanicalName,
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic,
                        color = White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = guide.overview.family,
                        fontSize = 10.sp,
                        color = White.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4 Key Quick Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickMetricBadge(
                    modifier = Modifier.weight(1f),
                    label = "Harvest",
                    value = "${crop.daysToHarvest} Days",
                    icon = Icons.Default.Timer,
                    tint = Sunlight
                )
                QuickMetricBadge(
                    modifier = Modifier.weight(1f),
                    label = "Watering",
                    value = "Every ${crop.wateringIntervalDays}d",
                    icon = Icons.Default.WaterDrop,
                    tint = Color(0xFF64B5F6)
                )
                QuickMetricBadge(
                    modifier = Modifier.weight(1f),
                    label = "Soil pH",
                    value = "${crop.optimalPhMin} – ${crop.optimalPhMax}",
                    icon = Icons.Default.Terrain,
                    tint = MonitoringGreenLight
                )
            }
        }
    }
}

@Composable
private fun QuickMetricBadge(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MonitoringCardElevated,
        border = BorderStroke(1.dp, MonitoringBorder.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = label, fontSize = 9.sp, color = White.copy(alpha = 0.5f))
            Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = White)
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// ─── 13 SECTION DETAIL COMPOSABLES ──────────────────────────────────────────
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun SectionOverview(guide: VegetableAgronomicGuide) {
    SectionCardWrapper(title = "Overview", icon = "📋") {
        Text(text = guide.overview.summary, fontSize = 13.sp, color = White.copy(alpha = 0.9f), lineHeight = 18.sp)
        Spacer(modifier = Modifier.height(10.dp))
        DetailFactRow(label = "Botanical Name", value = guide.overview.botanicalName)
        DetailFactRow(label = "Family", value = guide.overview.family)
        DetailFactRow(label = "Culinary Uses", value = guide.overview.culinaryUses)
        DetailFactRow(label = "Regional Suitability", value = guide.overview.regionalSuitability)
        DetailFactRow(label = "Agricultural Significance", value = guide.overview.agriculturalImportance)
    }
}

@Composable
private fun SectionVarieties(guide: VegetableAgronomicGuide) {
    SectionCardWrapper(title = "Cultivars & Varieties", icon = "🧬") {
        Text(
            text = "Recommended commercial hybrid and open-pollinated varieties adapted for Philippine cultivation:",
            fontSize = 12.sp,
            color = White.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        guide.varieties.forEach { variety ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MonitoringCardElevated,
                border = BorderStroke(1.dp, MonitoringBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = variety.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = White)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ForestGreen.copy(alpha = 0.3f),
                            border = BorderStroke(1.dp, ForestGreen)
                        ) {
                            Text(
                                text = "${variety.daysToHarvest} days",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Sunlight,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(text = "Local: ${variety.localName}", fontSize = 11.sp, color = MonitoringGreenLight)
                    Text(text = variety.characteristics, fontSize = 11.sp, color = White.copy(alpha = 0.8f), modifier = Modifier.padding(top = 4.dp))
                    Text(text = "🛡️ ${variety.diseaseResistance}", fontSize = 10.sp, color = Color(0xFFA5D6A7), modifier = Modifier.padding(top = 2.dp))
                    Text(text = "☀️ Season: ${variety.optimalSeason}", fontSize = 10.sp, color = White.copy(alpha = 0.6f), modifier = Modifier.padding(top = 2.dp))
                }
            }
        }
    }
}

@Composable
private fun SectionGrowingSeason(guide: VegetableAgronomicGuide) {
    val season = guide.growingSeason
    SectionCardWrapper(title = "Growing Season & Climate", icon = "☀️") {
        DetailFactRow(label = "Dry Season (Tag-araw)", value = season.drySeasonStatus)
        DetailFactRow(label = "Wet Season (Tag-ulan)", value = season.wetSeasonStatus)
        DetailFactRow(label = "Optimal Temperature", value = season.optimalTemperature)
        DetailFactRow(label = "Peak Planting Window", value = season.peakMonths)
        DetailFactRow(label = "Climatic Vulnerabilities", value = season.climateRisks)

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Agronomic Weather Advice:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Sunlight)
        season.weatherTips.forEach { tip ->
            BulletPoint(text = tip)
        }
    }
}

@Composable
private fun SectionSoil(guide: VegetableAgronomicGuide) {
    val soil = guide.soil
    SectionCardWrapper(title = "Soil Requirements & Bed Prep", icon = "🪴") {
        DetailFactRow(label = "Ideal Soil Textures", value = soil.idealSoilTypes)
        DetailFactRow(label = "Optimal pH Range", value = soil.optimalPh)
        DetailFactRow(label = "Drainage Quality", value = soil.drainage)
        DetailFactRow(label = "Land Preparation", value = soil.landPrep)
        DetailFactRow(label = "Organic Compost", value = soil.organicMatter)

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Soil Management Best Practices:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Sunlight)
        soil.recommendations.forEach { rec ->
            BulletPoint(text = rec)
        }
    }
}

@Composable
private fun SectionPlanting(guide: VegetableAgronomicGuide) {
    val p = guide.planting
    SectionCardWrapper(title = "Planting & Spacing Guide", icon = "🌱") {
        DetailFactRow(label = "Establishment Method", value = p.method)
        DetailFactRow(label = "Germination Period", value = p.germinationDays)
        DetailFactRow(label = "Transplanting Age", value = p.transplantAge)
        DetailFactRow(label = "In-Row Spacing", value = p.plantSpacing)
        DetailFactRow(label = "Between-Row Spacing", value = p.rowSpacing)
        DetailFactRow(label = "Planting Depth", value = p.plantingDepth)

        if (p.trellisingNeeded && p.trellisingAdvice != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = ForestGreen.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = "🪵 Trellis & Staking Required", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Sunlight)
                    Text(text = p.trellisingAdvice, fontSize = 11.sp, color = White.copy(alpha = 0.85f))
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        p.tips.forEach { tip ->
            BulletPoint(text = tip)
        }
    }
}

@Composable
private fun SectionWatering(guide: VegetableAgronomicGuide) {
    val w = guide.watering
    SectionCardWrapper(title = "Watering Schedule & Irrigation", icon = "💧") {
        DetailFactRow(label = "Watering Frequency", value = w.frequency)
        DetailFactRow(label = "Best Time of Day", value = w.bestTime)
        DetailFactRow(label = "Critical Moisture Stages", value = w.criticalStages)
        DetailFactRow(label = "Irrigation System", value = w.irrigationType)
        DetailFactRow(label = "Moisture Retention", value = w.moistureConservation)

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Irrigation Warnings:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF8A80))
        w.warnings.forEach { warning ->
            BulletPoint(text = warning, bulletColor = Color(0xFFFF8A80))
        }
    }
}

@Composable
private fun SectionFertilization(guide: VegetableAgronomicGuide) {
    val f = guide.fertilization
    SectionCardWrapper(title = "Fertilization & Nutrient Plan", icon = "🧪") {
        DetailFactRow(label = "NPK Nutrient Ratio", value = f.npkRatio)
        DetailFactRow(label = "Basal Application", value = f.basalApplication)
        DetailFactRow(label = "Side-Dressing Program", value = f.sideDressing)
        DetailFactRow(label = "Organic Bio-Fertilizers", value = f.organicOptions)
        DetailFactRow(label = "Micronutrient Supplements", value = f.micronutrients)

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Recommended Application Schedule:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Sunlight)
        f.schedule.forEach { step ->
            BulletPoint(text = step)
        }
    }
}

@Composable
private fun SectionGrowthStages(guide: VegetableAgronomicGuide) {
    SectionCardWrapper(title = "Growth Stages & Timeline", icon = "📈") {
        guide.growthStages.forEach { stage ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MonitoringCardElevated,
                border = BorderStroke(1.dp, MonitoringBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Stage ${stage.stageNumber}: ${stage.stageName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MonitoringGreenLight
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.4f)
                        ) {
                            Text(
                                text = stage.durationDays,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Sunlight,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(text = stage.description, fontSize = 11.sp, color = White.copy(alpha = 0.85f), modifier = Modifier.padding(top = 4.dp))
                    Text(text = "👉 Action: ${stage.farmerAction}", fontSize = 10.sp, color = Color(0xFFA5D6A7), modifier = Modifier.padding(top = 2.dp))
                }
            }
        }
    }
}

@Composable
private fun SectionPestsAndDiseases(guide: VegetableAgronomicGuide) {
    SectionCardWrapper(title = "Pests & Disease Management", icon = "🐛") {
        guide.pestsAndDiseases.forEach { pd ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MonitoringCardElevated,
                border = BorderStroke(1.dp, MonitoringBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = pd.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = White)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (pd.type.contains("Pest")) Color(0xFFE65100).copy(alpha = 0.3f) else Color(0xFFB71C1C).copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = pd.type,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (pd.type.contains("Pest")) Color(0xFFFFB74D) else Color(0xFFFF8A80),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(text = "🔍 Symptoms: ${pd.symptoms}", fontSize = 11.sp, color = White.copy(alpha = 0.85f), modifier = Modifier.padding(top = 4.dp))
                    Text(text = "🌿 Organic Control: ${pd.organicControl}", fontSize = 10.sp, color = Color(0xFFA5D6A7), modifier = Modifier.padding(top = 2.dp))
                    Text(text = "🛡️ Prevention: ${pd.prevention}", fontSize = 10.sp, color = White.copy(alpha = 0.65f), modifier = Modifier.padding(top = 2.dp))
                }
            }
        }
    }
}

@Composable
private fun SectionCompanionPlants(guide: VegetableAgronomicGuide) {
    val comp = guide.companionPlants
    SectionCardWrapper(title = "Companion Plants", icon = "🤝") {
        Text(text = "Beneficial Companions:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MonitoringGreenLight)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            comp.beneficialCompanions.forEach { companion ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ForestGreen.copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, ForestGreen)
                ) {
                    Text(
                        text = "🌿 $companion",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
        Text(text = comp.companionBenefits, fontSize = 11.sp, color = White.copy(alpha = 0.85f), modifier = Modifier.padding(vertical = 4.dp))

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Plants to Avoid:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF8A80))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            comp.plantsToAvoid.forEach { avoid ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFB71C1C).copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, Color(0xFFFF8A80).copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "🚫 $avoid",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF8A80),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
        Text(text = comp.avoidReasons, fontSize = 11.sp, color = White.copy(alpha = 0.75f))
    }
}

@Composable
private fun SectionIntercropping(guide: VegetableAgronomicGuide) {
    val inter = guide.intercropping
    SectionCardWrapper(title = "Intercropping Systems", icon = "🌾") {
        DetailFactRow(label = "Recommended Crops", value = inter.recommendedCrops.joinToString(", "))
        DetailFactRow(label = "Spatial Configuration", value = inter.spatialLayout)
        DetailFactRow(label = "Agronomic Benefits", value = inter.benefits)
        DetailFactRow(label = "Field Management", value = inter.managementAdvice)
    }
}

@Composable
private fun SectionHarvest(guide: VegetableAgronomicGuide) {
    val h = guide.harvest
    SectionCardWrapper(title = "Harvest Maturity & Technique", icon = "🧺") {
        DetailFactRow(label = "Maturity Indicators", value = h.maturityIndicators)
        DetailFactRow(label = "Harvest Timing", value = h.daysRange)
        DetailFactRow(label = "Harvesting Method", value = h.harvestingMethod)
        DetailFactRow(label = "Best Time of Day", value = h.timeOfDay)
        DetailFactRow(label = "Picking Frequency", value = h.frequency)

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Key Signs of Maturity:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Sunlight)
        h.indicatorsList.forEach { ind ->
            BulletPoint(text = ind)
        }
    }
}

@Composable
private fun SectionPostHarvest(guide: VegetableAgronomicGuide) {
    val ph = guide.postHarvest
    SectionCardWrapper(title = "Post-Harvest Handling & Storage", icon = "📦") {
        DetailFactRow(label = "Sorting & Grading", value = ph.sortingGrading)
        DetailFactRow(label = "Washing & Cleaning", value = ph.washingCleaning)
        DetailFactRow(label = "Storage Environment", value = ph.storageConditions)
        DetailFactRow(label = "Optimal Temperature", value = ph.optimalTemperature)
        DetailFactRow(label = "Relative Humidity", value = ph.relativeHumidity)
        DetailFactRow(label = "Packaging & Transport", value = ph.packagingTransport)
        DetailFactRow(label = "Estimated Shelf Life", value = ph.shelfLife)
    }
}

// ────────────────────────────────────────────────────────────────────────────
// ─── REUSABLE UI HELPERS ────────────────────────────────────────────────────
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun SectionCardWrapper(
    title: String,
    icon: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MonitoringCard,
        border = BorderStroke(1.dp, MonitoringBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Text(icon, fontSize = 16.sp)
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }
            HorizontalDivider(
                color = MonitoringBorder.copy(alpha = 0.6f),
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            content()
        }
    }
}

@Composable
private fun DetailFactRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = White.copy(alpha = 0.55f),
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = White.copy(alpha = 0.95f),
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Composable
private fun BulletPoint(
    text: String,
    bulletColor: Color = MonitoringGreenLight
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(4.dp)
                .clip(CircleShape)
                .background(bulletColor)
        )
        Text(
            text = text,
            fontSize = 11.sp,
            color = White.copy(alpha = 0.85f),
            lineHeight = 16.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
