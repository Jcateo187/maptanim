package com.maptanim.app.features.library.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.maptanim.app.features.library.model.VegetableAgronomicGuide

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

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
                icon = Icons.Default.AccessTime,
                modifier = Modifier.weight(1f)
            )
        }

        // Irrigation Protocol
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, CardBorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Irrigation & Moisture Management",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DeepBlack
                )
                Text(
                    text = "Method: ${guide.watering.irrigationType}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = LushGreen
                )
                Text(
                    text = "Critical Stages: ${guide.watering.criticalStages}",
                    fontSize = 11.sp,
                    color = DeepBlack
                )
                Text(
                    text = "Conservation: ${guide.watering.moistureConservation}",
                    fontSize = 11.sp,
                    color = MutedText
                )

                if (guide.watering.warnings.isNotEmpty()) {
                    HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                    Text("Moisture Warnings:", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Color(0xFFC62828))
                    guide.watering.warnings.forEach { warning ->
                        BulletItem(text = warning, dotColor = Color(0xFFC62828))
                    }
                }
            }
        }

        // Nutrition & Fertilization Schedule
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, CardBorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isOrganic) "Organic Nutrition Protocol" else "Standard Fertilizer Protocol",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = DeepBlack
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Text(
                            text = "NPK: ${guide.fertilization.npkRatio}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = LushGreen,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "Side-Dressing: ${guide.fertilization.sideDressing}",
                    fontSize = 11.sp,
                    color = DeepBlack,
                    lineHeight = 15.sp
                )

                if (isOrganic) {
                    Text(
                        text = "Organic Amendments: ${guide.fertilization.organicOptions}",
                        fontSize = 11.sp,
                        color = LushGreen,
                        lineHeight = 15.sp
                    )
                }

                HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                Text("Application Schedule:", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = DeepBlack)
                guide.fertilization.schedule.forEachIndexed { idx, sched ->
                    ProcedureCard(
                        stepNumber = idx + 1,
                        title = sched.substringBefore(":"),
                        instruction = sched.substringAfter(":").trim()
                    )
                }
            }
        }

        // Integrated Pest Management (IPM) Defense Guide
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, CardBorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Integrated Pest Management (IPM)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = DeepBlack
                    )
                    Text(
                        text = "${guide.pestsAndDiseases.size} Monitored Threats",
                        fontSize = 10.sp,
                        color = MutedText
                    )
                }

                guide.pestsAndDiseases.forEachIndexed { index, pest ->
                    val isExpanded = expandedPestIndex == index

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isExpanded) LightSurface else Color.White,
                        border = BorderStroke(1.dp, if (isExpanded) LushGreen else CardBorderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTogglePest(index) }
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    if (!pest.imageAsset.isNullOrBlank()) {
                                        AsyncImage(
                                            model = pest.imageAsset,
                                            contentDescription = pest.name,
                                            modifier = Modifier.size(24.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    } else {
                                        Surface(
                                            shape = CircleShape,
                                            color = if (pest.type == "Insect Pest") Color(0xFFFFF3E0) else Color(0xFFFFEBEE),
                                            modifier = Modifier.size(22.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = if (pest.type == "Insect Pest") Icons.Default.PestControl else Icons.Default.Coronavirus,
                                                    contentDescription = null,
                                                    tint = if (pest.type == "Insect Pest") Color(0xFFE65100) else Color(0xFFC62828),
                                                    modifier = Modifier.size(13.dp)
                                                )
                                            }
                                        }
                                    }

                                    Column {
                                        Text(
                                            text = pest.name,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DeepBlack,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = pest.type,
                                            fontSize = 9.sp,
                                            color = MutedText
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = MutedText,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            if (isExpanded) {
                                HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                                Text(
                                    text = "Symptoms: ${pest.symptoms}",
                                    fontSize = 10.sp,
                                    color = DeepBlack,
                                    lineHeight = 14.sp
                                )
                                Text(
                                    text = "Bio-Control: ${pest.organicControl}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = LushGreen,
                                    lineHeight = 14.sp
                                )
                                Text(
                                    text = "Cultural Prevention: ${pest.prevention}",
                                    fontSize = 10.sp,
                                    color = MutedText,
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
