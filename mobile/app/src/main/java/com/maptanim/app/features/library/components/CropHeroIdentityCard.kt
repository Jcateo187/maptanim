package com.maptanim.app.features.library.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.maptanim.app.data.datasource.CropMetadataAssetDataSource
import com.maptanim.app.domain.model.Crop
import com.maptanim.app.features.library.model.*

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * CropHeroIdentityCard — Hero header displaying crop title, scientific taxon,
 * agricultural summary, and interactive cultivation approach selectors.
 */
@Composable
fun CropHeroIdentityCard(
    crop: Crop,
    guide: VegetableAgronomicGuide,
    selectedMethod: String,
    onSelectMethod: (String) -> Unit,
    selectedApproach: String,
    onSelectApproach: (String) -> Unit,
    onFactClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorderColor),
        modifier = modifier.fillMaxWidth()
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
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = crop.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = LushGreen
                    )
                    Text(
                        text = "Local: ${crop.localName ?: crop.name} · ${guide.overview.family}",
                        fontSize = 11.sp,
                        color = MutedText
                    )
                    Text(
                        text = "Scientific: ${guide.overview.botanicalName}",
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic,
                        color = DeepBlack
                    )
                }

                val imageUrl = CropMetadataAssetDataSource.resolveCropImage(crop)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor),
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

            HorizontalDivider(color = CardBorderColor, thickness = 1.dp)

            Text(
                text = guide.overview.summary,
                fontSize = 12.sp,
                color = DeepBlack,
                lineHeight = 17.sp
            )

            // Clickable Fact Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    onClick = { onFactClick("Regional Suitability", guide.overview.regionalSuitability) },
                    shape = RoundedCornerShape(6.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = LushGreen, modifier = Modifier.size(12.dp))
                        Text("Regions", fontSize = 10.sp, color = DeepBlack, fontWeight = FontWeight.SemiBold)
                    }
                }

                Surface(
                    onClick = { onFactClick("Culinary & Cultural Uses", guide.overview.culinaryUses) },
                    shape = RoundedCornerShape(6.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Restaurant, contentDescription = null, tint = LushGreen, modifier = Modifier.size(12.dp))
                        Text("Culinary Uses", fontSize = 10.sp, color = DeepBlack, fontWeight = FontWeight.SemiBold)
                    }
                }

                Surface(
                    onClick = { onFactClick("Agricultural Importance", guide.overview.agriculturalImportance) },
                    shape = RoundedCornerShape(6.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = LushGreen, modifier = Modifier.size(12.dp))
                        Text("Economics", fontSize = 10.sp, color = DeepBlack, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Interactive Method & Approach Selector
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "CUSTOMIZE PROTOCOL SPECIFICATIONS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MutedText,
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
                            .background(LightSurface, RoundedCornerShape(8.dp))
                            .padding(2.dp)
                    ) {
                        listOf("Transplanting", "Direct Seeding").forEach { m ->
                            val isSel = m == selectedMethod
                            Surface(
                                onClick = { onSelectMethod(m) },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) LushGreen else Color.Transparent,
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
                                        color = if (isSel) Color.White else DeepBlack
                                    )
                                }
                            }
                        }
                    }

                    // Approach Toggle
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .background(LightSurface, RoundedCornerShape(8.dp))
                            .padding(2.dp)
                    ) {
                        listOf("Organic", "Conventional").forEach { a ->
                            val isSel = a == selectedApproach
                            Surface(
                                onClick = { onSelectApproach(a) },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) LushGreen else Color.Transparent,
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
                                        color = if (isSel) Color.White else DeepBlack
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
