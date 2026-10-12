package com.maptanim.app.features.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptanim.app.features.farm.dialogs.CropEncyclopediaRegistry
import com.maptanim.app.features.farm.renderer.canvas.CropSvgRenderer

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)

/**
 * UserFavoriteCropsSection — Displays a horizontal scroll row of favorite crop cards
 * directly beneath the avatar card on the Profile screen.
 * Shows crops that the user favorited in CropInformationDialog.
 */
@Composable
fun UserFavoriteCropsSection(
    favoriteCrops: List<String>,
    onCropClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Heading with Favorite icon and Favorite title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Color(0xFFD32F2F),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Favorite",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DeepBlack
            )
        }

        if (favoriteCrops.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFD32F2F).copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "No favorite vegetables yet",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepBlack
                        )
                        Text(
                            text = "Tap the heart icon in Vegetable Info to bookmark your favorite vegetables here.",
                            fontSize = 11.sp,
                            color = MutedText
                        )
                    }
                }
            }
        } else {
            // 1 row horizontal scroll
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(favoriteCrops, key = { it }) { cropName ->
                    val data = remember(cropName) { CropEncyclopediaRegistry.getCropEncyclopedia(cropName) }
                    FavoriteCropCardItem(
                        cropName = data.cropName,
                        localName = data.localName,
                        onClick = { onCropClick(data.cropName) }
                    )
                }
            }
        }
    }
}

/**
 * Single Crop Card Item in the Favorite Crops horizontal list.
 */
@Composable
private fun FavoriteCropCardItem(
    cropName: String,
    localName: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF1F8E9),
        border = BorderStroke(1.dp, CardBorderColor),
        shadowElevation = 1.dp,
        modifier = Modifier
            .width(115.dp)
            .height(132.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // SVG vector illustration in center
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                CropSvgRenderer.drawCropSvg(
                    drawScope = this,
                    cropName = cropName,
                    center = Offset(size.width / 2f, (size.height / 2f) - 10.dp.toPx()),
                    sizePx = minOf(size.width, size.height) * 0.85f
                )
            }

            // Small red heart badge at top right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorited",
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(12.dp)
                )
            }

            // Bottom gradient overlay with crop title and tagalog name
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xB3000000),
                                Color(0xE6000000)
                            )
                        )
                    )
                    .padding(horizontal = 6.dp, vertical = 5.dp)
            ) {
                Column {
                    Text(
                        text = cropName,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    val local = localName.substringBefore('(').trim()
                    if (local.isNotBlank() && !local.equals(cropName, ignoreCase = true)) {
                        Text(
                            text = local,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFC8E6C9),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
