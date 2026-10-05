package com.maptanim.app.features.farm.components

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.maptanim.app.data.datasource.CropMetadataAssetDataSource
import com.maptanim.app.features.farm.renderer.model.PlotRenderData
import com.maptanim.app.features.farm.tabs.plan.AVAILABLE_CROP_CATALOG
import com.maptanim.app.features.farm.viewmodel.CropPlantingDraft
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

/**
 * Categorized variety data structure.
 */
data class CategorizedVarietyGroup(
    val categoryName: String,
    val iconEmoji: String,
    val varieties: List<String>
)

/**
 * Returns the botanical/culinary category for a given crop name or crop id.
 */
fun getCropCategoryName(cropName: String, cropId: String): String {
    val catalog = AVAILABLE_CROP_CATALOG.firstOrNull {
        it.id.equals(cropId, ignoreCase = true) ||
        it.name.equals(cropName, ignoreCase = true) ||
        it.localName.equals(cropName, ignoreCase = true)
    }
    if (catalog != null) return catalog.category
    val lower = cropName.lowercase()
    return when {
        lower.contains("carrot") || lower.contains("karot") || lower.contains("radish") -> "Root"
        lower.contains("onion") || lower.contains("sibuyas") || lower.contains("garlic") -> "Bulb"
        lower.contains("cabbage") || lower.contains("repolyo") || lower.contains("pechay") ||
        lower.contains("kangkong") || lower.contains("lettuce") || lower.contains("mustasa") || lower.contains("spinach") -> "Leafy"
        lower.contains("tomato") || lower.contains("kamatis") || lower.contains("eggplant") || lower.contains("talong") ||
        lower.contains("squash") || lower.contains("kalabasa") || lower.contains("pumpkin") || lower.contains("okra") ||
        lower.contains("ampalaya") || lower.contains("sili") || lower.contains("pepper") || lower.contains("cucumber") || lower.contains("pipino") -> "Fruit"
        lower.contains("corn") || lower.contains("mais") -> "Stem"
        lower.contains("bean") || lower.contains("sitaw") || lower.contains("pea") || lower.contains("mung") -> "Podded"
        lower.contains("potato") || lower.contains("patatas") || lower.contains("kamote") || lower.contains("cassava") -> "Tuber"
        else -> "Vegetable"
    }
}

/**
 * Returns standard category emoji.
 */
fun getCategoryEmoji(category: String): String {
    return when (category.lowercase()) {
        "all" -> "🌱"
        "leafy" -> "🥬"
        "root" -> "🥕"
        "fruit" -> "🍅"
        "bulb" -> "🧅"
        "stem" -> "🌽"
        "podded" -> "🫘"
        "tuber" -> "🥔"
        else -> "🌱"
    }
}

/**
 * CropsSummaryOverlay — Full in-composition overlay for reviewing planting dates, methods, and varieties.
 *
 * Features:
 * - Common Settings dialog triggered from top settings icon/button with:
 *     - Planting date (Today / Choose date)
 *     - Planting method (Direct Seeding / Transplanting)
 *     - Growing approach (Organic / Conventional)
 *     - [ Apply to all ] button
 * - Horizontal bed selector (1 row 5 columns proportion, small cards, horizontally scrollable)
 * - Plantings section with vegetable category dropdown (small icon-only trigger, no name)
 * - 2 columns × 3 rows per page with horizontal scroll and pagination tabs in bottom (1, 2, 3...)
 * - Consistent MapTanim theme colors (ForestGreen #1E6E38, #EAF5EE, #E2E8F0)
 */
@Composable
fun CropsSummaryOverlay(
    farmName: String,
    cropPlantings: List<CropPlantingDraft>,
    errorMessage: String? = null,
    isSaving: Boolean = false,
    onCancel: () -> Unit,
    onSave: (List<CropPlantingDraft>) -> Unit
) {
    var drafts by remember(cropPlantings) {
        mutableStateOf(cropPlantings)
    }

    var localValidationError by remember { mutableStateOf<String?>(null) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var calendarTargetDraftId by remember { mutableStateOf<String?>(null) }
    var calendarTargetBedId by remember { mutableStateOf<String?>(null) }
    var varietyTargetDraftId by remember { mutableStateOf<String?>(null) }

    // Common Settings state
    var showCommonSettingsDialog by remember { mutableStateOf(false) }
    var showCalendarForCommonSettings by remember { mutableStateOf(false) }
    var commonSettingsDateOption by remember { mutableStateOf("Today") }
    var commonSettingsChosenDate by remember { mutableStateOf(LocalDate.now().toString()) }
    var commonSettingsMethod by remember { mutableStateOf("Direct Seeding") }
    var commonSettingsApproach by remember { mutableStateOf("Organic") }

    // Vegetable category filter state (small icon only trigger)
    var selectedCategory by remember { mutableStateOf("All") }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }

    val bedGroups = remember(drafts) {
        drafts.groupBy { it.bedId }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val coroutineScope = rememberCoroutineScope()
    var selectedBedId by remember(bedGroups.keys) {
        mutableStateOf("ALL")
    }

    // Filter crops based on selected bed and selected vegetable category
    val bedFilteredCrops = remember(drafts, selectedBedId) {
        if (selectedBedId == "ALL" || selectedBedId.isBlank()) {
            drafts
        } else {
            drafts.filter { it.bedId == selectedBedId }
        }
    }

    val displayCrops = remember(bedFilteredCrops, selectedCategory) {
        if (selectedCategory.equals("All", ignoreCase = true)) {
            bedFilteredCrops
        } else {
            bedFilteredCrops.filter { draft ->
                val cat = getCropCategoryName(draft.cropName, draft.cropId)
                cat.equals(selectedCategory, ignoreCase = true)
            }
        }
    }

    // 2 columns × 3 rows = 6 items per page
    val cropPages = remember(displayCrops) { displayCrops.chunked(6) }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { cropPages.size.coerceAtLeast(1) })

    LaunchedEffect(selectedBedId, selectedCategory) {
        if (cropPages.isNotEmpty()) {
            pagerState.scrollToPage(0)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xDD000000))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { /* block outside clicks */ }
            .padding(
                horizontal = if (isLandscape) 16.dp else 8.dp,
                vertical = if (isLandscape) 8.dp else 12.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(if (isLandscape) 0.90f else 0.98f)
                .widthIn(max = 760.dp)
                .fillMaxHeight(if (isLandscape) 0.98f else 0.96f)
                .clip(RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = 16.dp
        ) {
            Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
                // ── Top Header Banner with Settings icon named "Common Settings" ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFF1E6E38), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Spa,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Crop Summary",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = Color(0xFF0F1E2A)
                                )
                                Text(
                                    text = "$farmName • ${drafts.size} Pananim sa ${bedGroups.size} Bed",
                                    fontSize = 12.sp,
                                    color = Color(0xFF5B6976)
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Top settings button with icon named "Common Settings"
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFEAF5EE),
                                border = BorderStroke(1.dp, Color(0xFF1E6E38).copy(alpha = 0.3f)),
                                modifier = Modifier.clickable { showCommonSettingsDialog = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Common Settings",
                                        tint = Color(0xFF1E6E38),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Common Settings",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E6E38)
                                    )
                                }
                            }

                            IconButton(
                                onClick = onCancel,
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color.White, CircleShape)
                                    .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                            ) {
                                Icon(Icons.Default.Close, "Close", tint = Color(0xFF1E6E38), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

                // Feedback Banner
                if (!feedbackMessage.isNullOrBlank()) {
                    Surface(
                        color = Color(0xFFEAF5EE),
                        border = BorderStroke(1.dp, Color(0xFF1E6E38).copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF1E6E38), modifier = Modifier.size(15.dp))
                                Text(feedbackMessage ?: "", color = Color(0xFF1E6E38), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            IconButton(onClick = { feedbackMessage = null }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Close, null, tint = Color(0xFF1E6E38), modifier = Modifier.size(13.dp))
                            }
                        }
                    }
                }

                // Error or Validation Banner
                val activeError = localValidationError ?: errorMessage
                if (!activeError.isNullOrBlank()) {
                    Surface(
                        color = Color(0xFFFEF2F2),
                        border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Warning, null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                            Text(activeError, color = Color(0xFF991B1B), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // ── Bed Selection: 1 row 5 column, small only, horizontal scroll ──
                if (drafts.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // "All Beds" chip
                        item(key = "bed-tab-all") {
                            val isAllSelected = selectedBedId == "ALL" || selectedBedId.isBlank()
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isAllSelected) Color(0xFF1E6E38) else Color(0xFFF8FAFC),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (isAllSelected) Color(0xFF1E6E38) else Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier
                                    .width(96.dp)
                                    .height(46.dp)
                                    .clickable { selectedBedId = "ALL" }
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "All Beds",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isAllSelected) Color.White else Color(0xFF0F1E2A),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${drafts.size} crops",
                                        fontSize = 10.sp,
                                        color = if (isAllSelected) Color.White.copy(alpha = 0.85f) else Color(0xFF64748B),
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        // Individual Bed Cards (Bed Name ID & Number of crops, 1 row 5 column proportion, small)
                        bedGroups.forEach { (bedId, bedCrops) ->
                            val isSelected = bedId == selectedBedId
                            val bedLabel = bedCrops.firstOrNull()?.bedLabel?.ifBlank { null } ?: "Bed $bedId"
                            val cropCount = bedCrops.size

                            item(key = "bed-tab-$bedId") {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) Color(0xFF1E6E38) else Color(0xFFF8FAFC),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = if (isSelected) Color(0xFF1E6E38) else Color(0xFFE2E8F0)
                                    ),
                                    modifier = Modifier
                                        .width(96.dp)
                                        .height(46.dp)
                                        .clickable { selectedBedId = bedId }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = bedLabel,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else Color(0xFF0F1E2A),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "$cropCount crops",
                                            fontSize = 10.sp,
                                            color = if (isSelected) Color.White.copy(alpha = 0.85f) else Color(0xFF64748B),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ── Plantings Section Header with Category Dropdown (small only icon no name) ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Plantings",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F1E2A)
                        )
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFEAF5EE),
                            border = BorderStroke(0.5.dp, Color(0xFF1E6E38).copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "${displayCrops.size}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E6E38),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Vegetable category dropdown: small only icon no name!
                    Box {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .height(30.dp)
                                .clickable { categoryDropdownExpanded = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = getCategoryEmoji(selectedCategory),
                                    fontSize = 15.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Category Filter",
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false }
                        ) {
                            listOf(
                                "All" to "🌱 All Categories",
                                "Leafy" to "🥬 Leafy Vegetables",
                                "Root" to "🥕 Root Crops",
                                "Fruit" to "🍅 Fruit Vegetables",
                                "Bulb" to "🧅 Bulb Crops",
                                "Stem" to "🌽 Stem Crops",
                                "Podded" to "🫘 Podded Legumes",
                                "Tuber" to "🥔 Tubers"
                            ).forEach { (catKey, catLabel) ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = catLabel,
                                            fontSize = 13.sp,
                                            fontWeight = if (selectedCategory.equals(catKey, ignoreCase = true)) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selectedCategory.equals(catKey, ignoreCase = true)) Color(0xFF1E6E38) else Color(0xFF0F1E2A)
                                        )
                                    },
                                    onClick = {
                                        selectedCategory = catKey
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // ── 2 Columns × 3 Rows Paginated Container (can horizontal scroll) ──
                if (displayCrops.isEmpty()) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Yard, null, tint = Color(0xFFCBD5E1), modifier = Modifier.size(40.dp))
                            Text(
                                text = if (selectedCategory != "All") "Walang pananim sa kategoryang ${selectedCategory}." else "Walang pananim sa napiling bed.",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                            if (selectedCategory != "All") {
                                OutlinedButton(
                                    onClick = { selectedCategory = "All" },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFF1E6E38))
                                ) {
                                    Text("Ipakita ang Lahat", color = Color(0xFF1E6E38), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 2.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) { pageIndex ->
                            val pageCrops = cropPages.getOrNull(pageIndex) ?: emptyList()
                            val rows = remember(pageCrops) { pageCrops.chunked(2) }

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rows.forEach { rowCrops ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        rowCrops.forEach { cropDraft ->
                                            Box(modifier = Modifier.weight(1f)) {
                                                CompactCropGridCard(
                                                    draft = cropDraft,
                                                    dateFormatter = dateFormatter,
                                                    onOpenCalendar = { calendarTargetDraftId = cropDraft.id },
                                                    onOpenVarietyDropdown = { varietyTargetDraftId = cropDraft.id },
                                                    onPlantToday = {
                                                        val todayStr = LocalDate.now().toString()
                                                        drafts = drafts.map { if (it.id == cropDraft.id) it.copy(plantingDate = todayStr) else it }
                                                    },
                                                    onNotesChanged = { newNotes ->
                                                        drafts = drafts.map { if (it.id == cropDraft.id) it.copy(notes = newNotes) else it }
                                                    },
                                                    onToggleMethod = {
                                                        val newMethod = if (cropDraft.plantingMethod.contains("Direct", ignoreCase = true)) "Transplanting" else "Direct Seeding"
                                                        drafts = drafts.map { if (it.id == cropDraft.id) it.copy(plantingMethod = newMethod) else it }
                                                    },
                                                    onToggleApproach = {
                                                        val newApproach = if (cropDraft.growingApproach.contains("Organic", ignoreCase = true)) "Conventional" else "Organic"
                                                        drafts = drafts.map { if (it.id == cropDraft.id) it.copy(growingApproach = newApproach) else it }
                                                    }
                                                )
                                            }
                                        }
                                        if (rowCrops.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }

                        // ── Crop Pagination Tabs: [ 1 ] [ 2 ] [ 3 ] ──────────────
                        if (cropPages.size > 1) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SwapHoriz,
                                            contentDescription = null,
                                            tint = Color(0xFF64748B),
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Text(
                                            text = "Pahina ${pagerState.currentPage + 1} ng ${cropPages.size} (${displayCrops.size} Pananim)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF475569)
                                        )
                                    }

                                    // Tab 1, 2, 3 ... buttons
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        cropPages.indices.forEach { pageIdx ->
                                            val isPageActive = pagerState.currentPage == pageIdx
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (isPageActive) Color(0xFF1E6E38) else Color(0xFFF1F5F9),
                                                border = BorderStroke(
                                                    width = 1.dp,
                                                    color = if (isPageActive) Color(0xFF1E6E38) else Color(0xFFCBD5E1)
                                                ),
                                                modifier = Modifier.clickable {
                                                    coroutineScope.launch {
                                                        pagerState.animateScrollToPage(pageIdx)
                                                    }
                                                }
                                            ) {
                                                Box(
                                                    modifier = Modifier.size(width = 30.dp, height = 26.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "${pageIdx + 1}",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isPageActive) Color.White else Color(0xFF334155)
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

                // Fixed Bottom Actions
                Surface(
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onCancel,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFEF4444)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                            modifier = Modifier.weight(1f).height(40.dp)
                        ) {
                            Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Cancel", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Button(
                            onClick = {
                                val missing = mutableListOf<String>()
                                drafts.forEach { d ->
                                    if (d.cropName.isBlank()) missing.add("Crop")
                                    if (d.variety.isBlank()) missing.add("Variety (${d.cropName})")
                                    if (d.plantingDate.isBlank()) {
                                        missing.add("Petsa (${d.cropName})")
                                    } else {
                                        val valid = try { LocalDate.parse(d.plantingDate.take(10)) != null } catch (_: Exception) { false }
                                        if (!valid) missing.add("Wastong Petsa (${d.cropName})")
                                    }
                                    if (d.plantCount <= 0) missing.add("Bilang (${d.cropName})")
                                    if (d.bedId.isBlank()) missing.add("Bed ID (${d.cropName})")
                                }
                                if (missing.isNotEmpty()) {
                                    localValidationError = "⚠️ Pakiusap punan ang lahat ng kinakailangang impormasyon: ${missing.distinct().joinToString(", ")}"
                                } else {
                                    localValidationError = null
                                    onSave(drafts)
                                }
                            },
                            enabled = !isSaving,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E6E38)),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                            modifier = Modifier.weight(1.3f).height(40.dp)
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(Modifier.width(6.dp))
                                Text("Saving...", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                            } else {
                                Icon(Icons.Default.Save, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Save Plantings", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Common Settings Dialog ──────────────────────────────────────
    if (showCommonSettingsDialog) {
        CommonSettingsDialog(
            initialDateOption = commonSettingsDateOption,
            initialChosenDate = commonSettingsChosenDate,
            initialMethod = commonSettingsMethod,
            initialApproach = commonSettingsApproach,
            onDismiss = { showCommonSettingsDialog = false },
            onOpenCalendar = { showCalendarForCommonSettings = true },
            onApplyToAll = { dateOption, chosenDate, method, approach ->
                commonSettingsDateOption = dateOption
                commonSettingsChosenDate = chosenDate
                commonSettingsMethod = method
                commonSettingsApproach = approach

                val finalDate = if (dateOption == "Today") LocalDate.now().toString() else chosenDate
                drafts = drafts.map {
                    it.copy(
                        plantingDate = finalDate,
                        plantingMethod = method,
                        growingApproach = approach
                    )
                }
                feedbackMessage = "✓ Inilapat ang Common Settings sa lahat ng ${drafts.size} pananim."
                showCommonSettingsDialog = false
            }
        )
    }

    if (showCalendarForCommonSettings) {
        PlantingCalendarModal(
            cropName = "Lahat ng Pananim",
            plotLabel = "Common Settings",
            initialDateStr = commonSettingsChosenDate,
            onDismiss = { showCalendarForCommonSettings = false },
            onDateSelected = { newDate ->
                commonSettingsChosenDate = newDate
                commonSettingsDateOption = "Choose date"
                showCalendarForCommonSettings = false
            }
        )
    }

    calendarTargetDraftId?.let { targetId ->
        val target = drafts.firstOrNull { it.id == targetId }
        if (target != null) {
            val initialDateStr = target.plantingDate
            PlantingCalendarModal(
                cropName = target.cropName,
                plotLabel = target.bedLabel,
                initialDateStr = initialDateStr,
                onDismiss = { calendarTargetDraftId = null },
                onDateSelected = { newDate ->
                    val isValid = try {
                        val parsed = LocalDate.parse(newDate.take(10))
                        !parsed.isBefore(LocalDate.now().minusYears(1)) && !parsed.isAfter(LocalDate.now().plusYears(1))
                    } catch (_: Exception) {
                        false
                    }
                    if (isValid) {
                        drafts = drafts.map { if (it.id == targetId) it.copy(plantingDate = newDate) else it }
                        localValidationError = null
                        calendarTargetDraftId = null
                    } else {
                        localValidationError = "⚠️ Hindi wasto ang napiling petsa ng pagtatanim para sa ${target.cropName}. Pakiusap pumili ng wastong petsa."
                    }
                }
            )
        }
    }

    calendarTargetBedId?.let { targetBedId ->
        val bedDrafts = drafts.filter { it.bedId == targetBedId }
        val bedLabel = bedDrafts.firstOrNull()?.bedLabel ?: "Bed"
        val initialDateStr = bedDrafts.firstOrNull()?.plantingDate ?: LocalDate.now().toString()
        PlantingCalendarModal(
            cropName = "Lahat sa $bedLabel",
            plotLabel = bedLabel,
            initialDateStr = initialDateStr,
            onDismiss = { calendarTargetBedId = null },
            onDateSelected = { newDate ->
                drafts = drafts.map { if (it.bedId == targetBedId) it.copy(plantingDate = newDate) else it }
                calendarTargetBedId = null
            }
        )
    }

    varietyTargetDraftId?.let { targetId ->
        val target = drafts.firstOrNull { it.id == targetId }
        if (target != null) {
            val categorizedGroups = remember(target.cropName) {
                getCategorizedVarietiesForCrop(target.cropName)
            }
            CropVarietyPickerModal(
                cropName = target.cropName,
                plotLabel = target.bedLabel,
                currentVariety = target.variety,
                categorizedGroups = categorizedGroups,
                onDismiss = { varietyTargetDraftId = null },
                onVarietySelected = { newVariety ->
                    drafts = drafts.map { if (it.id == targetId) it.copy(variety = newVariety, varietyId = newVariety.lowercase().replace(" ", "_")) else it }
                    varietyTargetDraftId = null
                }
            )
        }
    }
}

/**
 * CommonSettingsDialog — Overlay dialog for bulk-configuring planting parameters.
 * Exactly follows the requested format:
 * COMMON SETTINGS
 * ────────────────────────
 * Planting date
 * ● Today
 * ○ Choose date
 *
 * Planting method
 * ○ Direct Seeding
 * ○ Transplanting
 *
 * Growing approach
 * ○ Organic
 * ○ Conventional
 *
 * [ Apply to all ]
 */
@Composable
private fun CommonSettingsDialog(
    initialDateOption: String,
    initialChosenDate: String,
    initialMethod: String,
    initialApproach: String,
    onDismiss: () -> Unit,
    onOpenCalendar: () -> Unit,
    onApplyToAll: (dateOption: String, chosenDate: String, method: String, approach: String) -> Unit
) {
    var dateOption by remember { mutableStateOf(initialDateOption) }
    var method by remember { mutableStateOf(initialMethod) }
    var approach by remember { mutableStateOf(initialApproach) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 420.dp),
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header: COMMON SETTINGS
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
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Common Settings",
                            tint = Color(0xFF1E6E38),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "COMMON SETTINGS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF0F1E2A)
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

                // ── Planting date section ──
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Planting date",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F1E2A)
                    )
                    // Today
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { dateOption = "Today" }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (dateOption == "Today"),
                            onClick = { dateOption = "Today" },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF1E6E38),
                                unselectedColor = Color(0xFF94A3B8)
                            )
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Today (${LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))})",
                            fontSize = 13.sp,
                            fontWeight = if (dateOption == "Today") FontWeight.SemiBold else FontWeight.Normal,
                            color = Color(0xFF0F1E2A)
                        )
                    }
                    // Choose date
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                dateOption = "Choose date"
                                onOpenCalendar()
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (dateOption == "Choose date"),
                            onClick = {
                                dateOption = "Choose date"
                                onOpenCalendar()
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF1E6E38),
                                unselectedColor = Color(0xFF94A3B8)
                            )
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Choose date",
                            fontSize = 13.sp,
                            fontWeight = if (dateOption == "Choose date") FontWeight.SemiBold else FontWeight.Normal,
                            color = Color(0xFF0F1E2A)
                        )
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (dateOption == "Choose date") Color(0xFFEAF5EE) else Color(0xFFF1F5F9),
                            border = BorderStroke(1.dp, if (dateOption == "Choose date") Color(0xFF1E6E38) else Color(0xFFE2E8F0)),
                            modifier = Modifier.clickable {
                                dateOption = "Choose date"
                                onOpenCalendar()
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.CalendarMonth, null, tint = Color(0xFF1E6E38), modifier = Modifier.size(13.dp))
                                Text(
                                    text = initialChosenDate,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (dateOption == "Choose date") Color(0xFF1E6E38) else Color(0xFF475569)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                // ── Planting method section ──
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Planting method",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F1E2A)
                    )
                    // Direct Seeding
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { method = "Direct Seeding" }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (method == "Direct Seeding"),
                            onClick = { method = "Direct Seeding" },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF1E6E38),
                                unselectedColor = Color(0xFF94A3B8)
                            )
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Direct Seeding",
                            fontSize = 13.sp,
                            fontWeight = if (method == "Direct Seeding") FontWeight.SemiBold else FontWeight.Normal,
                            color = Color(0xFF0F1E2A)
                        )
                    }
                    // Transplanting
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { method = "Transplanting" }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (method == "Transplanting"),
                            onClick = { method = "Transplanting" },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF1E6E38),
                                unselectedColor = Color(0xFF94A3B8)
                            )
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Transplanting",
                            fontSize = 13.sp,
                            fontWeight = if (method == "Transplanting") FontWeight.SemiBold else FontWeight.Normal,
                            color = Color(0xFF0F1E2A)
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                // ── Growing approach section ──
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Growing approach",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F1E2A)
                    )
                    // Organic
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { approach = "Organic" }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (approach == "Organic"),
                            onClick = { approach = "Organic" },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF1E6E38),
                                unselectedColor = Color(0xFF94A3B8)
                            )
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Organic",
                            fontSize = 13.sp,
                            fontWeight = if (approach == "Organic") FontWeight.SemiBold else FontWeight.Normal,
                            color = Color(0xFF0F1E2A)
                        )
                    }
                    // Conventional
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { approach = "Conventional" }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (approach == "Conventional"),
                            onClick = { approach = "Conventional" },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF1E6E38),
                                unselectedColor = Color(0xFF94A3B8)
                            )
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Conventional",
                            fontSize = 13.sp,
                            fontWeight = if (approach == "Conventional") FontWeight.SemiBold else FontWeight.Normal,
                            color = Color(0xFF0F1E2A)
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                // [ Apply to all ] Button
                Button(
                    onClick = {
                        onApplyToAll(dateOption, initialChosenDate, method, approach)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E6E38)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Apply to all",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * CompactCropGridCard — Individual crop card designed for the 2 columns × 3 rows grid view:
 * - Crop thumbnail on left with crop name and bed / plant count
 * - Variety selector chip (clickable)
 * - Planting date chip (clickable)
 * - Method ("Direct" / "Transplant") and Approach ("Organic" / "Conv.") clickable badges
 * - 3-dots more menu with quick actions
 */
@Composable
private fun CompactCropGridCard(
    draft: CropPlantingDraft,
    dateFormatter: DateTimeFormatter,
    onOpenCalendar: () -> Unit,
    onOpenVarietyDropdown: () -> Unit,
    onPlantToday: () -> Unit,
    onNotesChanged: (String) -> Unit,
    onToggleMethod: () -> Unit,
    onToggleApproach: () -> Unit
) {
    val cropName = draft.cropName
    val cropId = draft.cropId
    val imageUri = remember(cropId, cropName) {
        CropMetadataAssetDataSource.resolveCropImage(cropId, cropName)
    }
    val currentDateStr = draft.plantingDate
    val currentLocalDate = remember(currentDateStr) {
        try { LocalDate.parse(currentDateStr.take(10)) } catch (_: Exception) { LocalDate.now() }
    }
    var showEditNoteDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // ── Top Row: Image, Name & More Menu ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = cropName,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Column {
                        Text(
                            text = cropName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF0F1E2A),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${draft.bedLabel} • ${draft.plantCount} pcs",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B),
                            maxLines = 1
                        )
                    }
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Itanim Ngayon (Today)", fontSize = 12.sp) },
                            onClick = {
                                showMenu = false
                                onPlantToday()
                            },
                            leadingIcon = { Icon(Icons.Default.Today, null, tint = Color(0xFF1E6E38), modifier = Modifier.size(16.dp)) }
                        )
                        DropdownMenuItem(
                            text = { Text("Pumili ng Petsa", fontSize = 12.sp) },
                            onClick = {
                                showMenu = false
                                onOpenCalendar()
                            },
                            leadingIcon = { Icon(Icons.Default.CalendarMonth, null, tint = Color(0xFF1E6E38), modifier = Modifier.size(16.dp)) }
                        )
                        DropdownMenuItem(
                            text = { Text("Palitan ang Variety", fontSize = 12.sp) },
                            onClick = {
                                showMenu = false
                                onOpenVarietyDropdown()
                            },
                            leadingIcon = { Icon(Icons.Default.Spa, null, tint = Color(0xFF1E6E38), modifier = Modifier.size(16.dp)) }
                        )
                        DropdownMenuItem(
                            text = { Text("Palitan ang Method (${draft.plantingMethod})", fontSize = 12.sp) },
                            onClick = {
                                showMenu = false
                                onToggleMethod()
                            },
                            leadingIcon = { Icon(Icons.Default.SwapHoriz, null, tint = Color(0xFF1E6E38), modifier = Modifier.size(16.dp)) }
                        )
                        DropdownMenuItem(
                            text = { Text("Palitan ang Approach (${draft.growingApproach})", fontSize = 12.sp) },
                            onClick = {
                                showMenu = false
                                onToggleApproach()
                            },
                            leadingIcon = { Icon(Icons.Default.Science, null, tint = Color(0xFF1E6E38), modifier = Modifier.size(16.dp)) }
                        )
                        DropdownMenuItem(
                            text = { Text("Tala / Note", fontSize = 12.sp) },
                            onClick = {
                                showMenu = false
                                showEditNoteDialog = true
                            },
                            leadingIcon = { Icon(Icons.Default.Description, null, tint = Color(0xFF1E6E38), modifier = Modifier.size(16.dp)) }
                        )
                    }
                }
            }

            // ── Variety Row ──
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenVarietyDropdown() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null,
                        tint = Color(0xFF1E6E38),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = draft.variety.ifBlank { "Select Variety..." },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E293B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            // ── Date Row ──
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenCalendar() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = Color(0xFF1E6E38),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = currentLocalDate.format(dateFormatter),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F1E2A),
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            // ── Badges Row: Method & Approach ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Planting method badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFEAF5EE),
                    border = BorderStroke(0.5.dp, Color(0xFF1E6E38).copy(alpha = 0.3f)),
                    modifier = Modifier.clickable { onToggleMethod() }
                ) {
                    Text(
                        text = "🌱 ${if (draft.plantingMethod.contains("Direct", true)) "Direct" else "Transplant"}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E6E38),
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }

                // Growing approach badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (draft.growingApproach.contains("Organic", true)) Color(0xFFEAF5EE) else Color(0xFFF1F5F9),
                    border = BorderStroke(0.5.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.clickable { onToggleApproach() }
                ) {
                    Text(
                        text = "🌿 ${if (draft.growingApproach.contains("Organic", true)) "Organic" else "Conv."}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (draft.growingApproach.contains("Organic", true)) Color(0xFF1E6E38) else Color(0xFF475569),
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }

                if (draft.notes.isNotBlank()) {
                    Spacer(Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = "Notes",
                        tint = Color(0xFF1E6E38),
                        modifier = Modifier
                            .size(14.dp)
                            .clickable { showEditNoteDialog = true }
                    )
                }
            }
        }
    }

    if (showEditNoteDialog) {
        NoteEditorDialog(
            cropName = cropName,
            initialNote = draft.notes,
            onDismiss = { showEditNoteDialog = false },
            onSave = {
                onNotesChanged(it)
                showEditNoteDialog = false
            }
        )
    }
}

/**
 * Legacy overload for CropsSummaryOverlay for backwards compatibility.
 */
@Composable
fun CropsSummaryOverlay(
    farmName: String,
    plots: List<PlotRenderData>,
    onCancel: () -> Unit,
    onSave: (updatedPlantedDates: Map<String, String>, updatedVarieties: Map<String, String>) -> Unit
) {
    val drafts = remember(plots) {
        plots.filter { !it.cropName.isNullOrBlank() }.map { plot ->
            val cropName = plot.cropName ?: "Vegetable"
            CropPlantingDraft(
                id = plot.id,
                cropName = cropName,
                cropId = plot.cropId ?: cropName.lowercase(),
                bedId = plot.id,
                bedLabel = plot.plotLabel,
                variety = plot.cropVariety?.ifBlank { null } ?: getDefaultVariety(cropName),
                varietyId = (plot.cropVariety?.ifBlank { null } ?: getDefaultVariety(cropName)).lowercase().replace(" ", "_"),
                plantingDate = plot.plantedDate?.take(10)?.ifBlank { null } ?: LocalDate.now().toString(),
                plantCount = (plot.widthM * plot.heightM).toInt().coerceAtLeast(1),
                notes = "",
                isNew = false,
                isChanged = true
            )
        }
    }
    CropsSummaryOverlay(
        farmName = farmName,
        cropPlantings = drafts,
        onCancel = onCancel,
        onSave = { updatedDrafts ->
            val dates = updatedDrafts.associate { it.id to it.plantingDate }
            val varieties = updatedDrafts.associate { it.id to it.variety }
            onSave(dates, varieties)
        }
    )
}


/**
 * CropSummaryRow — Individual crop card matching the user's design mockup:
 * - Rounded white card with soft border
 * - Crop photo thumbnail on left
 * - Title + Leaf icon + "Variety" dropdown box
 * - Top-right "Nakatakda" status pill + 3-dots more menu
 * - Horizontal divider
 * - Bottom row: Petsa ng Pagtatanim (left) | Tala / Note (right)
 */
@Composable
private fun CropSummaryRow(
    draft: CropPlantingDraft,
    dateFormatter: DateTimeFormatter,
    onOpenCalendar: () -> Unit,
    onOpenVarietyDropdown: () -> Unit,
    onPlantToday: () -> Unit,
    onNotesChanged: (String) -> Unit
) {
    val cropName = draft.cropName
    val cropId = draft.cropId
    val imageUri = remember(cropId, cropName) {
        CropMetadataAssetDataSource.resolveCropImage(cropId, cropName)
    }
    val currentDateStr = draft.plantingDate
    val currentLocalDate = remember(currentDateStr) {
        try { LocalDate.parse(currentDateStr.take(10)) } catch (_: Exception) { LocalDate.now() }
    }
    var showEditNoteDialog by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ── Top Row ─────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Crop Thumbnail
                    AsyncImage(
                        model = imageUri,
                        contentDescription = cropName,
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = cropName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF0F1E2A)
                        )

                        // Variety Selector Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Spa,
                                contentDescription = null,
                                tint = Color(0xFF1E6E38),
                                modifier = Modifier.size(16.dp)
                            )
                            Column {
                                Text(
                                    text = "Variety",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.Medium
                                )
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFF1F5F9),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier.clickable { onOpenVarietyDropdown() }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = draft.variety.ifBlank { "Pumili ng Variety..." },
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E293B),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Select Variety",
                                            tint = Color(0xFF64748B),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Top Right: More Options Menu
                Box {
                    var showMenu by remember { mutableStateOf(false) }
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options",
                            tint = Color(0xFF334155),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Itanim Ngayon (Day 1)") },
                            onClick = {
                                showMenu = false
                                onPlantToday()
                            },
                            leadingIcon = { Icon(Icons.Default.Today, null, tint = Color(0xFF1E6E38)) }
                        )
                        DropdownMenuItem(
                            text = { Text("Baguhin ang Petsa") },
                            onClick = {
                                showMenu = false
                                onOpenCalendar()
                            },
                            leadingIcon = { Icon(Icons.Default.CalendarMonth, null, tint = Color(0xFF1E6E38)) }
                        )
                        DropdownMenuItem(
                            text = { Text("Palitan ang Variety") },
                            onClick = {
                                showMenu = false
                                onOpenVarietyDropdown()
                            },
                            leadingIcon = { Icon(Icons.Default.Spa, null, tint = Color(0xFF1E6E38)) }
                        )
                    }
                }
            }

            // Divider
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // ── Bottom Row: Date & Note ─────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column: Petsa ng Pagtatanim
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onOpenCalendar() }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFFEAF5EE), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Color(0xFF1E6E38),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Petsa ng Pagtatanim",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = currentLocalDate.format(dateFormatter),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F1E2A)
                        )
                    }
                }

                // Center Vertical Divider
                Box(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .width(1.dp)
                        .height(34.dp)
                        .background(Color(0xFFE2E8F0))
                )

                // Right Column: Tala / Note
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showEditNoteDialog = true }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFFEAF5EE), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = Color(0xFF1E6E38),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tala / Note",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = draft.notes.ifBlank { "Magdagdag ng tala..." },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (draft.notes.isBlank()) Color(0xFF94A3B8) else Color(0xFF0F1E2A),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }

    if (showEditNoteDialog) {
        NoteEditorDialog(
            cropName = cropName,
            initialNote = draft.notes,
            onDismiss = { showEditNoteDialog = false },
            onSave = {
                onNotesChanged(it)
                showEditNoteDialog = false
            }
        )
    }
}

/**
 * NoteEditorDialog — Clean dialog for editing crop notes.
 */
@Composable
private fun NoteEditorDialog(
    cropName: String,
    initialNote: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var noteText by remember { mutableStateOf(initialNote) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = 16.dp,
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Tala / Note para sa $cropName",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF0F1E2A)
                )

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    placeholder = {
                        Text(
                            "Maglagay ng tala / note para sa pananim na ito...",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E6E38),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC),
                        focusedTextColor = Color(0xFF0F172A),
                        unfocusedTextColor = Color(0xFF0F172A)
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Kanselahin", color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onSave(noteText) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E6E38)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("I-save", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Interactive Monthly Calendar Modal (Matching the Monitoring Reschedule Flow).
 */
@Composable
private fun PlantingCalendarModal(
    cropName: String,
    plotLabel: String,
    initialDateStr: String,
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    val initialDate = remember(initialDateStr) {
        try {
            LocalDate.parse(initialDateStr.take(10))
        } catch (e: Exception) {
            LocalDate.now()
        }
    }

    var selectedDate by remember { mutableStateOf(initialDate) }
    var currentYearMonth by remember { mutableStateOf(YearMonth.from(initialDate)) }
    val today = LocalDate.now()

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
                    .widthIn(min = 320.dp, max = 390.dp)
                    .fillMaxWidth(0.82f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* block touch */ },
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
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
                                text = "Petsa ng Pagtatanim",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "$cropName • Plot $plotLabel",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    // Month Navigation Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { currentYearMonth = currentYearMonth.minusMonths(1) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month", tint = Color(0xFF475569), modifier = Modifier.size(18.dp))
                        }

                        Text(
                            text = "${currentYearMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${currentYearMonth.year}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A)
                        )

                        IconButton(
                            onClick = { currentYearMonth = currentYearMonth.plusMonths(1) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = Color(0xFF475569), modifier = Modifier.size(18.dp))
                        }
                    }

                    // Days of Week Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                            Text(day, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                        }
                    }

                    // Calendar Days Grid
                    val firstDayOfMonth = currentYearMonth.atDay(1)
                    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
                    val daysInMonth = currentYearMonth.lengthOfMonth()
                    val totalCells = firstDayOfWeek + daysInMonth
                    val rows = (totalCells + 6) / 7

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (r in 0 until rows) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                for (c in 0..6) {
                                    val dayNum = r * 7 + c - firstDayOfWeek + 1
                                    if (dayNum in 1..daysInMonth) {
                                        val dateObj = currentYearMonth.atDay(dayNum)
                                        val isSelected = dateObj == selectedDate
                                        val isToday = dateObj == today

                                        val bgColor = when {
                                            isSelected -> Color(0xFF1E6E38)
                                            isToday -> Color(0xFFEAF5EE)
                                            else -> Color.Transparent
                                        }

                                        val textColor = when {
                                            isSelected -> Color.White
                                            isToday -> Color(0xFF1E6E38)
                                            else -> Color(0xFF0F172A)
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(bgColor)
                                                .border(
                                                    width = if (isToday && !isSelected) 1.dp else 0.dp,
                                                    color = if (isToday) Color(0xFF1E6E38) else Color.Transparent,
                                                    shape = CircleShape
                                                )
                                                .clickable {
                                                    selectedDate = dateObj
                                                    onDateSelected(dateObj.toString())
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$dayNum",
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                                color = textColor
                                            )
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.size(28.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Quick Shortcut Chips (Exact Labels: Today, Tomorrow, 3 Days, 7 Days)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            "Today" to today,
                            "Tomorrow" to today.plusDays(1),
                            "3 Days" to today.plusDays(3),
                            "7 Days" to today.plusDays(7)
                        ).forEach { (label, dVal) ->
                            val isSel = selectedDate == dVal
                            Surface(
                                onClick = {
                                    selectedDate = dVal
                                    currentYearMonth = YearMonth.from(dVal)
                                    onDateSelected(dVal.toString())
                                },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) Color(0xFF1E6E38) else Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, if (isSel) Color(0xFF1E6E38) else Color(0xFFE2E8F0)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        label,
                                        fontSize = 10.sp,
                                        color = if (isSel) Color.White else Color(0xFF475569),
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    // Footer Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF475569)),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                        ) {
                            Text("Cancel", fontSize = 12.sp, color = Color(0xFF475569))
                        }

                        Button(
                            onClick = { onDateSelected(selectedDate.toString()) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E6E38)),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                        ) {
                            Text("Confirm", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Categorized Crop Variety Picker Modal Overlay.
 */
@Composable
private fun CropVarietyPickerModal(
    cropName: String,
    plotLabel: String,
    currentVariety: String,
    categorizedGroups: List<CategorizedVarietyGroup>,
    onDismiss: () -> Unit,
    onVarietySelected: (String) -> Unit
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
                    .widthIn(min = 320.dp, max = 420.dp)
                    .fillMaxWidth(0.85f)
                    .fillMaxHeight(0.85f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* block touch */ },
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
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
                                text = "Pumili ng Variety",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "$cropName • Plot $plotLabel",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    // Grouped Variety List
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(categorizedGroups) { group ->
                            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(group.iconEmoji, fontSize = 13.sp)
                                    Text(
                                        text = group.categoryName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E6E38)
                                    )
                                }

                                group.varieties.forEach { varietyName ->
                                    val isSelected = varietyName == currentVariety
                                    Surface(
                                        onClick = {
                                            onVarietySelected(varietyName)
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) Color(0xFFEAF5EE) else Color.White,
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) Color(0xFF1E6E38) else Color(0xFFE2E8F0)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = varietyName,
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color(0xFF1E6E38) else Color(0xFF0F172A)
                                            )
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = Color(0xFF1E6E38),
                                                    modifier = Modifier.size(16.dp)
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
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    ) {
                        Text("Isara", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                    }
                }
            }
        }
    }
}

internal fun getCategorizedVarietiesForCrop(cropName: String): List<CategorizedVarietyGroup> {
    val clean = cropName.lowercase()
    return when {
        clean.contains("ampalaya") || clean.contains("bitter") -> listOf(
            CategorizedVarietyGroup("Commercial F1 Hybrids (High Yield)", "🌟", listOf("Galaxy Max F1", "Jade Star XL F1", "Trident F1")),
            CategorizedVarietyGroup("Open Pollinated / Traditional", "🌾", listOf("Bonito F1", "Sta. Rita", "Pinakbet Special"))
        )
        clean.contains("tomato") || clean.contains("kamatis") -> listOf(
            CategorizedVarietyGroup("Commercial F1 Hybrids (High Yield)", "🌟", listOf("Diamante Max F1", "Avatar F1", "Marimar F1")),
            CategorizedVarietyGroup("Open Pollinated / Traditional", "🌾", listOf("Rosanna", "Apollo", "Kamatis Tagalog"))
        )
        clean.contains("eggplant") || clean.contains("talong") -> listOf(
            CategorizedVarietyGroup("Commercial F1 Hybrids (High Yield)", "🌟", listOf("Fortuner F1", "Morena F1", "Banate King F1")),
            CategorizedVarietyGroup("Open Pollinated / Traditional", "🌾", listOf("Dumaguete Long Purple", "Dingras Multiple Purple"))
        )
        clean.contains("carrot") || clean.contains("karot") -> listOf(
            CategorizedVarietyGroup("Commercial F1 Hybrids (High Yield)", "🌟", listOf("Terracotta F1", "Chantenay Supreme")),
            CategorizedVarietyGroup("Open Pollinated / Traditional", "🌾", listOf("Kuroda Improved", "Early Nantes"))
        )
        clean.contains("cabbage") || clean.contains("repolyo") -> listOf(
            CategorizedVarietyGroup("Commercial F1 Hybrids (High Yield)", "🌟", listOf("Rare Ball F1", "K-S Cross F1", "Kyross F1")),
            CategorizedVarietyGroup("Open Pollinated / Traditional", "🌾", listOf("Scorpio", "Golden Acre"))
        )
        clean.contains("pechay") || clean.contains("bokchoy") -> listOf(
            CategorizedVarietyGroup("Commercial & Popular", "🌟", listOf("Black Behi", "Pavon", "Ching-Chiang")),
            CategorizedVarietyGroup("Open Pollinated / Local", "🌾", listOf("Baby Bokchoy Local", "Native Pechay"))
        )
        clean.contains("onion") || clean.contains("sibuyas") -> listOf(
            CategorizedVarietyGroup("Commercial F1 Hybrids (High Yield)", "🌟", listOf("Red Pinoy F1", "Yellow Granex F1", "Superpex F1")),
            CategorizedVarietyGroup("Open Pollinated / Local", "🌾", listOf("Batanes Red", "Tanduyong Red Shallot"))
        )
        clean.contains("pumpkin") || clean.contains("squash") || clean.contains("kalabasa") -> listOf(
            CategorizedVarietyGroup("Commercial F1 Hybrids (High Yield)", "🌟", listOf("Suprema F1", "Horizon F1")),
            CategorizedVarietyGroup("Open Pollinated / Local", "🌾", listOf("Rizalina", "Native Tagalog Kalabasa"))
        )
        clean.contains("corn") || clean.contains("mais") -> listOf(
            CategorizedVarietyGroup("Commercial F1 Hybrids (High Yield)", "🌟", listOf("Macho Sweet F1", "Machismo F1", "Sweet Pearl F1")),
            CategorizedVarietyGroup("Open Pollinated / Traditional", "🌾", listOf("IPB Var 6 (White)", "Lagkitan Glutinous"))
        )
        clean.contains("okra") -> listOf(
            CategorizedVarietyGroup("Commercial & Popular", "🌟", listOf("Smooth Green F1", "Kamiling Green")),
            CategorizedVarietyGroup("Open Pollinated / Local", "🌾", listOf("Native Deep Green", "Clemson Spineless"))
        )
        clean.contains("sili") || clean.contains("chili") || clean.contains("pepper") -> listOf(
            CategorizedVarietyGroup("Commercial F1 Hybrids (High Yield)", "🌟", listOf("Django F1 (Siling Haba)", "Hot Pepper F1")),
            CategorizedVarietyGroup("Open Pollinated / Local", "🌾", listOf("Siling Labuyo Native", "Taiwan Hot"))
        )
        clean.contains("sitaw") || clean.contains("stringbean") || clean.contains("beans") -> listOf(
            CategorizedVarietyGroup("Commercial F1 Hybrids (High Yield)", "🌟", listOf("Sandigan F1", "Galante F1", "Negros Dark Green")),
            CategorizedVarietyGroup("Open Pollinated / Traditional", "🌾", listOf("UPLB Green", "Bongabon Striped"))
        )
        else -> listOf(
            CategorizedVarietyGroup("Standard Cultivar", "🌟", listOf("East-West Standard F1", "Local Standard Cultivar"))
        )
    }
}

/**
 * Returns standard Philippine agricultural variety recommendation.
 */
private fun getDefaultVariety(cropName: String): String {
    val clean = cropName.lowercase()
    return when {
        clean.contains("ampalaya") || clean.contains("bitter") -> "Galaxy Max F1"
        clean.contains("tomato") || clean.contains("kamatis") -> "Diamante Max F1"
        clean.contains("eggplant") || clean.contains("talong") -> "Fortuner F1"
        clean.contains("carrot") || clean.contains("karot") -> "Terracotta F1"
        clean.contains("cabbage") || clean.contains("repolyo") -> "Rare Ball F1"
        clean.contains("pechay") || clean.contains("bokchoy") -> "Black Behi"
        clean.contains("onion") || clean.contains("sibuyas") -> "Red Pinoy F1"
        clean.contains("pumpkin") || clean.contains("squash") || clean.contains("kalabasa") -> "Suprema F1"
        clean.contains("corn") || clean.contains("mais") -> "Macho Sweet F1"
        clean.contains("okra") -> "Smooth Green F1"
        clean.contains("sili") || clean.contains("chili") || clean.contains("pepper") -> "Django F1"
        clean.contains("sitaw") || clean.contains("stringbean") || clean.contains("beans") -> "Sandigan F1"
        clean.contains("lettuce") -> "General F1"
        clean.contains("kangkong") -> "Upland Green"
        else -> "East-West Standard F1"
    }
}

/**
 * Returns estimated days to maturity / harvest for the crop.
 */
internal fun getDaysToHarvestEstimate(cropName: String): Int {
    val clean = cropName.lowercase()
    return when {
        clean.contains("pechay") || clean.contains("kangkong") -> 30
        clean.contains("lettuce") -> 45
        clean.contains("okra") -> 50
        clean.contains("sitaw") || clean.contains("stringbean") -> 55
        clean.contains("ampalaya") || clean.contains("bitter") -> 60
        clean.contains("tomato") || clean.contains("kamatis") -> 65
        clean.contains("eggplant") || clean.contains("talong") -> 70
        clean.contains("corn") || clean.contains("mais") -> 75
        clean.contains("carrot") || clean.contains("karot") -> 85
        clean.contains("cabbage") || clean.contains("repolyo") -> 85
        clean.contains("pumpkin") || clean.contains("squash") -> 90
        clean.contains("onion") || clean.contains("sibuyas") -> 100
        clean.contains("sili") || clean.contains("chili") -> 75
        else -> 60
    }
}
