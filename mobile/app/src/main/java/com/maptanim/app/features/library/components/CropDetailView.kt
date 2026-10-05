package com.maptanim.app.features.library.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.features.library.model.AgronomicTab

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * CropDetailView — Deep agronomic reference view with hero identity, commercial cultivars,
 * companion planting, phenology stages, and 4 numbered agronomic protocols.
 * Adheres strictly to the Daylight High-Contrast Theme.
 */
@Composable
fun CropDetailView(
    guide: VegetableAgronomicGuide,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
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
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Compact Top Bar with Back Button
        Surface(
            color = Color.White,
            border = BorderStroke(1.dp, CardBorderColor),
            shadowElevation = 2.dp,
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
                        tint = DeepBlack,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${crop.name} ${if (!crop.localName.isNullOrBlank() && !crop.localName.equals(crop.name, ignoreCase = true)) "(${crop.localName})" else ""}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = LushGreen,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = guide.overview.botanicalName,
                        fontSize = 10.sp,
                        fontStyle = FontStyle.Italic,
                        color = MutedText
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFE8F5E9),
                    border = BorderStroke(1.dp, LushGreen.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = crop.category,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = LushGreen,
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
            // ── Section 1: Hero Identity Card ────────────────────────────────
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

            // ── Section 2: Commercial Cultivars ──────────────────────────────
            item {
                CropVarietiesSection(
                    varieties = guide.varieties,
                    selectedIndex = selectedVarietyIndex,
                    onSelectVariety = { selectedVarietyIndex = it }
                )
            }

            // ── Section 3: Companion Matrix ──────────────────────────────────
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

            // ── Section 4: Phenology Growth Stages ───────────────────────────
            item {
                GrowthStageTimeline(
                    stages = guide.growthStages,
                    selectedIndex = selectedStageIndex,
                    onSelectStage = { selectedStageIndex = it }
                )
            }

            // ── Section 5: Soil Compatibility & Amendments ───────────────────
            item {
                SoilCompatibilityCard(soilInfo = guide.soil)
            }

            // ── Section 6: Number-Based Agronomic Protocols Tabs (1 to 4) ────
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "AGRONOMIC PROTOCOLS & FIELD PRACTICES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlack,
                        letterSpacing = 0.5.sp
                    )

                    // 4 Numbered Protocol Tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AgronomicTab.entries.forEach { tab ->
                            val isSel = tab == selectedTab
                            Surface(
                                onClick = { selectedTab = tab },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) LushGreen else LightSurface,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSel) LushGreen else CardBorderColor
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "${tab.tabNumber}.",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else LushGreen
                                    )
                                    Text(
                                        text = tab.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSel) Color.White else DeepBlack
                                    )
                                }
                            }
                        }
                    }

                    // Tab Content
                    when (selectedTab) {
                        AgronomicTab.PREPARATION -> TabPreparationContent(guide, selectedApproach == "Organic")
                        AgronomicTab.PLANTING -> TabPlantingContent(guide, selectedMethod)
                        AgronomicTab.CARE_MAINTENANCE -> TabCareMaintenanceContent(
                            guide = guide,
                            isOrganic = selectedApproach == "Organic",
                            expandedPestIndex = expandedPestIndex,
                            onTogglePest = { idx ->
                                expandedPestIndex = if (expandedPestIndex == idx) null else idx
                            }
                        )
                        AgronomicTab.HARVEST_METHOD -> TabHarvestMethodContent(guide)
                    }
                }
            }
        }
    }

    // Quick Fact Dialog
    activeFactDialog?.let { (title, content) ->
        AlertDialog(
            onDismissRequest = { activeFactDialog = null },
            title = {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = LushGreen
                )
            },
            text = {
                Text(
                    text = content,
                    fontSize = 13.sp,
                    color = DeepBlack,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { activeFactDialog = null }) {
                    Text("Close", color = LushGreen, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
private fun CropVarietiesSection(
    varieties: List<VarietyDetail>,
    selectedIndex: Int,
    onSelectVariety: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorderColor),
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
                    color = DeepBlack,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Tap to inspect traits",
                    fontSize = 10.sp,
                    color = LushGreen,
                    fontWeight = FontWeight.SemiBold
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
                        color = if (isSelected) Color(0xFFF1F8E9) else LightSurface,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) LushGreen else CardBorderColor
                        ),
                        modifier = Modifier.width(170.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = v.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) LushGreen else DeepBlack
                            )
                            Text(
                                text = "${v.daysToHarvest} days · ${v.optimalSeason}",
                                fontSize = 10.sp,
                                color = LushGreen,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                            Text(
                                text = v.diseaseResistance,
                                fontSize = 9.sp,
                                color = MutedText,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Selected Cultivar Profile
            val active = varieties.getOrNull(selectedIndex) ?: varieties.firstOrNull()
            if (active != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor),
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
                                color = LushGreen
                            )
                            Text(
                                text = "Maturity: ${active.daysToHarvest} DAT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepBlack
                            )
                        }

                        Text(
                            text = "Fruit & Market Traits: ${active.characteristics}",
                            fontSize = 11.sp,
                            color = DeepBlack,
                            lineHeight = 15.sp
                        )

                        Text(
                            text = "Disease Tolerance: ${active.diseaseResistance}",
                            fontSize = 11.sp,
                            color = MutedText,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CropCompanionsAndIntercroppingSection(
    companionInfo: CompanionInfo,
    intercroppingInfo: IntercroppingInfo,
    expandedCompanion: String?,
    onToggleCompanion: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorderColor),
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
                    text = "COMPANION PLANTING MATRIX",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepBlack,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Click to inspect",
                    fontSize = 10.sp,
                    color = LushGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Two Columns: Beneficial vs Incompatible
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Beneficial Column
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF1F8E9),
                    border = BorderStroke(1.dp, LushGreen.copy(alpha = 0.3f)),
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
                            Icon(Icons.Default.Check, contentDescription = null, tint = LushGreen, modifier = Modifier.size(14.dp))
                            Text("BENEFICIAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LushGreen)
                        }

                        companionInfo.beneficialCompanions.forEach { comp ->
                            val isExp = expandedCompanion == comp
                            Surface(
                                onClick = { onToggleCompanion(comp) },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isExp) Color(0xFFE8F5E9) else Color.White,
                                border = BorderStroke(1.dp, CardBorderColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Spa, contentDescription = null, tint = LushGreen, modifier = Modifier.size(14.dp))
                                    Text(
                                        text = comp,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DeepBlack
                                    )
                                }
                            }
                        }
                    }
                }

                // Incompatible Column
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFEBEE),
                    border = BorderStroke(1.dp, Color(0xFFEF9A9A)),
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
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(14.dp))
                            Text("INCOMPATIBLE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                        }

                        companionInfo.plantsToAvoid.forEach { comp ->
                            val isExp = expandedCompanion == comp
                            Surface(
                                onClick = { onToggleCompanion(comp) },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isExp) Color(0xFFFFCDD2) else Color.White,
                                border = BorderStroke(1.dp, CardBorderColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(14.dp))
                                    Text(
                                        text = comp,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DeepBlack
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
