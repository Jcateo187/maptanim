package com.maptanim.app.features.library.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.maptanim.app.features.library.model.*

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

@Composable
fun CropVarietiesSection(
    varieties: List<VarietyDetail>,
    selectedIndex: Int,
    onSelectVariety: (Int) -> Unit
) = CommercialCultivarsSection(varieties, selectedIndex, onSelectVariety)

@Composable
fun CommercialCultivarsSection(
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
                    text = "Commercial Cultivars & Seeds",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DeepBlack
                )
                Text(
                    text = "${varieties.size} Variants Available",
                    fontSize = 10.sp,
                    color = MutedText
                )
            }

            // Variety selector chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                varieties.forEachIndexed { index, variety ->
                    val isSelected = selectedIndex == index
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) LushGreen else LightSurface,
                        border = BorderStroke(1.dp, if (isSelected) LushGreen else CardBorderColor),
                        modifier = Modifier.clickable { onSelectVariety(index) }
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Text(
                                text = variety.name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else DeepBlack
                            )
                            Text(
                                text = variety.localName,
                                fontSize = 9.sp,
                                color = if (isSelected) Color.White.copy(alpha = 0.8f) else MutedText
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
fun CropCompanionsAndIntercroppingSection(
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
                    text = "Companion Planting & Intercropping",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DeepBlack
                )
                Text(
                    text = "Synergy & Antagonism",
                    fontSize = 10.sp,
                    color = LushGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Beneficial Companions
            Text("Beneficial Crop Partners:", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = DeepBlack)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                companionInfo.beneficialCompanions.forEach { companion ->
                    val isSelected = expandedCompanion == companion
                    val imgUrl = AgronomicAssetHelper.resolveCompanionCropImage(companion)

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) Color(0xFFE8F5E9) else LightSurface,
                        border = BorderStroke(1.dp, if (isSelected) LushGreen else CardBorderColor),
                        modifier = Modifier.clickable { onToggleCompanion(companion) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            if (imgUrl != null) {
                                AsyncImage(
                                    model = imgUrl,
                                    contentDescription = companion,
                                    modifier = Modifier.size(18.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                            Text(
                                text = companion,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = DeepBlack
                            )
                        }
                    }
                }
            }

            Text(
                text = companionInfo.companionBenefits,
                fontSize = 11.sp,
                color = MutedText,
                lineHeight = 15.sp
            )

            // Plants to Avoid
            if (companionInfo.plantsToAvoid.isNotEmpty()) {
                HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                Text(
                    text = "Antagonistic Plants (Avoid Planting Nearby):",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = Color(0xFFC62828)
                )
                companionInfo.plantsToAvoid.forEach { antagonist ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(shape = CircleShape, color = Color(0xFFC62828), modifier = Modifier.size(5.dp)) {}
                        Text(text = antagonist, fontSize = 11.sp, color = DeepBlack)
                    }
                }
                Text(
                    text = "Reason: ${companionInfo.avoidReasons}",
                    fontSize = 10.sp,
                    color = MutedText,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun AgronomicFactDialog(
    title: String,
    fact: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorderColor),
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = LushGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DeepBlack
                    )
                }

                Text(
                    text = fact,
                    fontSize = 12.sp,
                    color = DeepBlack,
                    lineHeight = 17.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LushGreen)
                    ) {
                        Text("Understood", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
