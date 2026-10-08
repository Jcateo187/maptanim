package com.maptanim.app.features.library.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import com.maptanim.app.domain.model.Crop
import com.maptanim.app.features.farm.components.CropFamilyInfo
import com.maptanim.app.features.farm.components.getCropFamily
import com.maptanim.app.features.farm.renderer.canvas.CropSvgRenderer
import com.maptanim.app.features.library.model.LibraryCategory

private val LushGreen = Color(0xFF2E7D32)
private val DeepBlack = Color(0xFF111813)
private val CardBorderColor = Color(0xFFE0E0E0)
private val MutedText = Color(0xFF555555)
private val LightSurface = Color(0xFFF9FAF8)

/**
 * CropCatalogGrid — 3-column vegetable library catalog grouped by botanical family type.
 * Includes live search and category filters adhering strictly to the Daylight High-Contrast Theme.
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

    // Group crops by their botanical family type and sort by canonical family order
    val groupedCrops = remember(crops) {
        crops.groupBy { getCropFamily(it) }
            .toSortedMap(compareBy { it.sortOrder })
    }

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
                    text = "Vegetables",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepBlack
                )
                Text(
                    text = "Agronomic reference catalog organized by botanical family",
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

        // Search Bar Row: Input (left) and Search Button (right)
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = LightSurface,
                border = BorderStroke(1.dp, CardBorderColor),
                modifier = Modifier
                    .weight(1f)
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
                        contentDescription = null,
                        tint = LushGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search vegetables...",
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
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }),
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

            // Search Button on Right Side
            Surface(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                },
                shape = RoundedCornerShape(10.dp),
                color = LushGreen,
                border = BorderStroke(1.dp, LushGreen),
                shadowElevation = 1.dp,
                modifier = Modifier.height(42.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Search",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Crops 3-Column Grid Grouped by Family Type ────────────────────────
        if (crops.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No vegetables match your filter.",
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
                contentPadding = PaddingValues(top = 4.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                groupedCrops.forEach { (family, familyCrops) ->
                    // Section Heading for Botanical Family Type
                    item(
                        span = { GridItemSpan(maxLineSpan) },
                        key = "family_heading_${family.scientificName}"
                    ) {
                        CropFamilyHeading(
                            family = family,
                            cropCount = familyCrops.size
                        )
                    }

                    // Crop Cards under this Family
                    items(familyCrops, key = { it.id }) { crop ->
                        Crop3ColumnCardItem(
                            crop = crop,
                            onClick = { onSelectCrop(crop) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * CropFamilyHeading — Full-width section heading for each botanical crop family in Crop Library.
 * Displays the family botanical name, common family type name, emoji icon, and crop count badge.
 */
@Composable
fun CropFamilyHeading(
    family: CropFamilyInfo,
    cropCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Family Icon Badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFE8F5E9),
                border = BorderStroke(1.dp, Color(0xFFC8E6C9))
            ) {
                Box(
                    modifier = Modifier.size(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = family.icon,
                        fontSize = 15.sp
                    )
                }
            }

            // Family Heading Names
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = family.scientificName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepBlack
                )
                Text(
                    text = family.commonName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MutedText
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        HorizontalDivider(
            color = CardBorderColor,
            thickness = 1.dp
        )
    }
}

/**
 * 3-Column Clean Crop Card Item
 * The image takes the whole card with high-contrast bottom scrim.
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
        color = Color(0xFFF1F8E9),
        border = BorderStroke(1.dp, CardBorderColor),
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Whole card SVG illustration
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 10.dp)
            ) {
                CropSvgRenderer.drawCropSvg(
                    drawScope = this,
                    cropName = crop.name,
                    center = Offset(size.width / 2f, (size.height / 2f) - 10.dp.toPx()),
                    sizePx = minOf(size.width, size.height) * 0.88f
                )
            }

            // Bottom Gradient Scrim Overlay with Crop Name & Local Name
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
                        text = crop.name,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    val local = crop.localName
                    if (!local.isNullOrBlank() && !local.equals(crop.name, ignoreCase = true)) {
                        Text(
                            text = local,
                            fontSize = 9.5.sp,
                            color = Color(0xFFD4E6D2),
                            fontStyle = FontStyle.Italic,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
