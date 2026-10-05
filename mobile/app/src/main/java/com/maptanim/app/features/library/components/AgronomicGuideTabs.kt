package com.maptanim.app.features.library.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * TabPreparationContent — Bed preparation, basal fertilization, and soil profile specs.
 */
@Composable
fun TabPreparationContent(guide: VegetableAgronomicGuide, isOrganic: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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

        // Soil Profile Card
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, CardBorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val soilImg = AgronomicAssetHelper.resolveSoilImage(guide.soil.idealSoilTypes)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor),
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
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LushGreen
                    )
                    Text(
                        text = "Drainage: ${guide.soil.drainage}",
                        fontSize = 11.sp,
                        color = DeepBlack,
                        lineHeight = 15.sp
                    )
                    Text(
                        text = "Organic Matter: ${guide.soil.organicMatter}",
                        fontSize = 11.sp,
                        color = MutedText,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

/**
 * TabPlantingContent — In-row spacing, row spacing, planting depth, and execution checklist.
 */
@Composable
fun TabPlantingContent(guide: VegetableAgronomicGuide, plantingMethod: String) {
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
                color = LightSurface,
                border = BorderStroke(1.dp, CardBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "TRELLISING & STAKING REQUIREMENT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LushGreen
                    )
                    Text(
                        text = guide.planting.trellisingAdvice ?: "Erect bamboo stakes or A-frame trellis 2–3 weeks after transplanting. Tie main stems loosely with soft twine.",
                        fontSize = 11.sp,
                        color = DeepBlack,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

/**
 * TabCareMaintenanceContent — Watering frequency, IPM pest defense guide, and fertilization protocols.
 */
@Composable
fun TabCareMaintenanceContent(
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
                "Irrigation: Apply water at root base early morning; avoid splashing lower foliage to stop fungal blight.",
                if (isOrganic) {
                    "Fertilization: Drench with Fermented Plant Juice (FPJ) or vermitea every 10–14 days during vegetative growth."
                } else {
                    "Fertilization: Side-dress with 46-0-0 (Urea) at 21 days after planting; apply 0-0-60 during flowering set."
                },
                "Staking & Pruning: Secure main stem to bamboo trellises; pinch off lower auxiliary suckers to concentrate fruit size.",
                "Weed Control: Maintain 3–5 cm rice straw mulch to shade out weed seedlings and conserve soil moisture."
            )
        )

        // Pests & Diseases Defense Guide
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, CardBorderColor),
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
                        Icon(Icons.Default.BugReport, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
                        Text(
                            text = "PESTS & DISEASES DEFENSE GUIDE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBlack
                        )
                    }
                    Text(
                        text = "Tap to expand diagnosis",
                        fontSize = 10.sp,
                        color = LushGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                guide.pestsAndDiseases.forEachIndexed { idx, pest ->
                    val isExpanded = expandedPestIndex == idx
                    val pestImg = AgronomicAssetHelper.resolvePestImage(pest.name, pest.imageAsset)

                    Surface(
                        onClick = { onTogglePest(idx) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isExpanded) Color(0xFFF1F8E9) else LightSurface,
                        border = BorderStroke(
                            1.dp,
                            if (isExpanded) LushGreen else CardBorderColor
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
                                        color = Color.White,
                                        border = BorderStroke(1.dp, CardBorderColor),
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
                                        color = Color(0xFFFFEBEE),
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.BugReport, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
                                        }
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = pest.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepBlack
                                    )
                                    Text(
                                        text = pest.type,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (pest.type.contains("Insect", ignoreCase = true)) Color(0xFFE65100) else Color(0xFFC62828)
                                    )
                                }

                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = DeepBlack,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            if (isExpanded) {
                                HorizontalDivider(color = CardBorderColor, thickness = 1.dp)

                                Text(
                                    text = "Visual Symptoms: ${pest.symptoms}",
                                    fontSize = 11.sp,
                                    color = DeepBlack,
                                    lineHeight = 15.sp
                                )

                                Text(
                                    text = "Organic Control: ${pest.organicControl}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = LushGreen,
                                    lineHeight = 15.sp
                                )

                                pest.chemicalControl?.let { chem ->
                                    Text(
                                        text = "Conventional IPM: $chem",
                                        fontSize = 11.sp,
                                        color = Color(0xFF1565C0),
                                        lineHeight = 15.sp
                                    )
                                }

                                Text(
                                    text = "Cultural Prevention: ${pest.prevention}",
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
    }
}

/**
 * TabHarvestMethodContent — Harvest indicators, cutting techniques, and storage specs.
 */
@Composable
fun TabHarvestMethodContent(guide: VegetableAgronomicGuide) {
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

        // Maturity Indicators List
        if (guide.harvest.indicatorsList.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = LightSurface,
                border = BorderStroke(1.dp, CardBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "MATURITY INDICATOR STAGES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LushGreen
                    )
                    guide.harvest.indicatorsList.forEach { ind ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("•", color = LushGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = ind,
                                fontSize = 11.sp,
                                color = DeepBlack,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Post-Harvest & Storage Advisory
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = LightSurface,
            border = BorderStroke(1.dp, CardBorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "POST-HARVEST HANDLING & PACKAGING",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LushGreen
                )
                Text(
                    text = "Sorting & Grading: ${guide.postHarvest.sortingGrading}",
                    fontSize = 11.sp,
                    color = DeepBlack,
                    lineHeight = 15.sp
                )
                Text(
                    text = "Storage RH: ${guide.postHarvest.relativeHumidity} · Shelf Life: ${guide.postHarvest.shelfLife}",
                    fontSize = 11.sp,
                    color = MutedText,
                    lineHeight = 15.sp
                )
                Text(
                    text = "Packaging: ${guide.postHarvest.packagingTransport}",
                    fontSize = 11.sp,
                    color = MutedText,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

/**
 * SpecMetricCard — Compact specification tile with circular icon badge.
 */
@Composable
fun SpecMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFE8F5E9),
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = LushGreen,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = MutedText,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = value,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepBlack,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * ProtocolChecklistCard — Numbered step-by-step agronomic action checklist.
 */
@Composable
fun ProtocolChecklistCard(
    title: String,
    steps: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorderColor),
        modifier = modifier.fillMaxWidth()
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
                color = LushGreen,
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
                        color = LushGreen,
                        modifier = Modifier.width(16.dp)
                    )
                    Text(
                        text = step,
                        fontSize = 11.sp,
                        color = DeepBlack,
                        lineHeight = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
