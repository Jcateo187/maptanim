package com.maptanim.app.features.library.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.maptanim.app.data.datasource.CropMetadataAssetDataSource
import com.maptanim.app.domain.model.Crop
import com.maptanim.app.features.library.model.LibraryCategory

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * CropCatalogGrid — 3-column vegetable library catalog with live search and category filters.
 * Adheres strictly to the Daylight High-Contrast Theme.
 */
@Composable
fun CropCatalogGrid(
    crops: List<Crop>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: LibraryCategory,
    onSelectCategory: (LibraryCategory) -> Unit,
    onSelectCrop: (Crop) -> Unit,
    modifier: Modifier = Modifier
) {
    var isCategoryDropdownOpen by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        // Clean Title Header
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
                    color = DeepBlack
                )
                Text(
                    text = "Agronomic reference catalog & management protocols",
                    fontSize = 11.sp,
                    color = MutedText
                )
            }

            // Category Dropdown Button
            Box {
                Surface(
                    onClick = { isCategoryDropdownOpen = !isCategoryDropdownOpen },
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = selectedCategory.icon,
                            contentDescription = null,
                            tint = LushGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = selectedCategory.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepBlack
                        )
                        Icon(
                            imageVector = if (isCategoryDropdownOpen) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = MutedText,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = isCategoryDropdownOpen,
                    onDismissRequest = { isCategoryDropdownOpen = false },
                    modifier = Modifier.background(Color.White)
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
                                        tint = if (cat == selectedCategory) LushGreen else MutedText,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = cat.label,
                                        color = if (cat == selectedCategory) LushGreen else DeepBlack,
                                        fontSize = 12.sp,
                                        fontWeight = if (cat == selectedCategory) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            },
                            onClick = {
                                onSelectCategory(cat)
                                isCategoryDropdownOpen = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Search Bar
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = LightSurface,
            border = BorderStroke(1.dp, CardBorderColor),
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
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
                    tint = LushGreen,
                    modifier = Modifier.size(18.dp)
                )
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Search crops by name or variety...",
                            fontSize = 12.sp,
                            color = MutedText
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        singleLine = true,
                        textStyle = TextStyle(color = DeepBlack, fontSize = 12.sp),
                        cursorBrush = SolidColor(LushGreen),
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
                            tint = MutedText,
                            modifier = Modifier.size(16.dp)
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
                    color = MutedText,
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
 * Adheres strictly to the Daylight High-Contrast Theme.
 */
@Composable
fun Crop3ColumnCardItem(
    crop: Crop,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor),
        modifier = modifier.fillMaxWidth()
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
                    .background(Color(0xFFF1F8E9)),
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

            // Crop Name (Lush Green Bold, 12sp)
            Text(
                text = crop.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = LushGreen,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Local Name (Charcoal Italic, 10sp)
            val local = crop.localName
            if (!local.isNullOrBlank() && !local.equals(crop.name, ignoreCase = true)) {
                Text(
                    text = local,
                    fontSize = 10.sp,
                    color = MutedText,
                    fontStyle = FontStyle.Italic,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Clean Category Pill
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFFE8F5E9)
            ) {
                Text(
                    text = crop.category.take(8).uppercase(),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = LushGreen,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}
