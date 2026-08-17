package com.maptanim.app.ui.components.editcomponents.croptray

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

data class CropOption(
    val id: String,
    val name: String,
    val emoji: String,
    val category: String,
    val lifeType: String = "Seasonal",
    val imageFileName: String = "${id}.png",
    val hasAsset: Boolean = true
)

val AVAILABLE_CROP_CATALOG = listOf(
    CropOption("carrot", "Carrot", "🥕", "Root", lifeType = "Seasonal", imageFileName = "carrot.png"),
    CropOption("stringbeans", "String Beans", "🫘", "Podded", lifeType = "Seasonal", imageFileName = "sitaw.png"),
    CropOption("eggplant", "Eggplant", "🍆", "Fruit", lifeType = "Permanent", imageFileName = "eggplant.png"),
    CropOption("tomato", "Tomato", "🍅", "Fruit", lifeType = "Semi Permanent", imageFileName = "tomato.png"),
    CropOption("onion", "Onion", "🧅", "Bulb", lifeType = "Seasonal", imageFileName = "onion.png"),
    CropOption("pumpkin", "Squash", "🎃", "Fruit", lifeType = "Seasonal", imageFileName = "pumpkin.png"),
    CropOption("corn", "Corn", "🌽", "Stem", lifeType = "Seasonal", imageFileName = "corn.png"),
    CropOption("cabbage", "Cabbage", "🥬", "Leafy", lifeType = "Seasonal", imageFileName = "cabbage.png"),
    CropOption("pechay", "Pechay", "🥬", "Leafy", lifeType = "Seasonal", imageFileName = "pechay.png"),
    CropOption("ampalaya", "Ampalaya", "🥒", "Fruit", lifeType = "Seasonal", imageFileName = "ampalaya.png"),
    CropOption("okra", "Okra", "🌿", "Fruit", lifeType = "Seasonal", imageFileName = "okra.png"),
    CropOption("sili", "Chili Pepper", "🌶️", "Fruit", lifeType = "Permanent", imageFileName = "sili.png"),
    CropOption("cucumber", "Cucumber", "🥒", "Fruit", lifeType = "Seasonal", imageFileName = "pipino.png"),
    CropOption("kangkong", "Kangkong", "🥬", "Leafy", lifeType = "Seasonal", imageFileName = "kangkong.png"),
    CropOption("lettuce", "Lettuce", "🥗", "Leafy", lifeType = "Seasonal", imageFileName = "lettuce.png")
)

val CATEGORY_OPTIONS = listOf(
    "All", "Leafy", "Root", "Bulb", "Stem", "Flower", "Podded", "Tuber", "Fruit"
)

/**
 * CropTray — Right-side crop selection panel with CoC-style Drag & Drop support.
 */
@Composable
fun CropTray(
    modifier: Modifier = Modifier,
    selectedCropName: String? = null,
    availableCrops: List<CropOption> = AVAILABLE_CROP_CATALOG,
    onCropSelected: (cropName: String, cropId: String) -> Unit = { _, _ -> },
    onCropDragStart: (cropName: String, cropId: String, screenOffset: Offset) -> Unit = { _, _, _ -> },
    onCropDragging: (screenOffset: Offset) -> Unit = { _ -> },
    onCropDragEnd: (screenOffset: Offset) -> Unit = { _ -> },
    onClose: () -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var categoryMenuExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var activeSearchQuery by remember { mutableStateOf("") }

    var isSearchFocused by remember { mutableStateOf(false) }
    val searchFocusRequester = remember { FocusRequester() }

    val searchScale by animateFloatAsState(
        targetValue = if (isSearchFocused) 1.05f else 1.0f,
        label = "searchScale"
    )
    val searchElevation by animateDpAsState(
        targetValue = if (isSearchFocused) 6.dp else 1.dp,
        label = "searchElevation"
    )

    val filteredCrops = remember(selectedCategory, activeSearchQuery, availableCrops) {
        availableCrops.filter { crop ->
            val categoryMatch = selectedCategory == "All" || crop.category.equals(selectedCategory, ignoreCase = true)
            val searchMatch = activeSearchQuery.isBlank() || crop.name.contains(activeSearchQuery, ignoreCase = true)

            categoryMatch && searchMatch
        }
    }

    Surface(
        shape = RoundedCornerShape(topStart = 30.dp, bottomStart = 30.dp),
        color = Color.White.copy(alpha = 0.98f),
        shadowElevation = 12.dp,
        modifier = modifier
            .fillMaxHeight()
            .width(300.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "SELECT CROPS",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = "(${filteredCrops.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF43A047)
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Panel",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Category Dropdown & Search Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedCategory != "All") Color(0xFF1B5E20) else Color(0xFFE0E0E0),
                        modifier = Modifier
                            .height(40.dp)
                            .clickable { categoryMenuExpanded = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Category Filter",
                                tint = if (selectedCategory != "All") Color.White else Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (selectedCategory == "All") "Cat." else selectedCategory,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedCategory != "All") Color.White else Color.Black
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = categoryMenuExpanded,
                        onDismissRequest = { categoryMenuExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        Surface(
                            color = Color(0xFFE8F5E9),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "PLANT CATEGORIES",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1B5E20),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }

                        Divider(color = Color.LightGray.copy(alpha = 0.5f))

                        CATEGORY_OPTIONS.forEach { category ->
                            val isCatSelected = selectedCategory == category
                            DropdownMenuItem(
                                modifier = Modifier.background(
                                    if (isCatSelected) Color(0xFFE8F5E9) else Color.White
                                ),
                                text = {
                                    Text(
                                        text = category,
                                        fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isCatSelected) Color(0xFF1B5E20) else Color(0xFF212121),
                                        fontSize = 13.sp
                                    )
                                },
                                onClick = {
                                    selectedCategory = category
                                    categoryMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = searchElevation,
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            scaleX = searchScale
                            scaleY = searchScale
                        }
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            activeSearchQuery = it
                        },
                        textStyle = LocalTextStyle.current.copy(
                            color = Color.Black,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 16.sp
                        ),
                        placeholder = {
                            Text(
                                text = "Search crops...",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 48.dp)
                            .focusRequester(searchFocusRequester)
                            .onFocusChanged { focusState ->
                                isSearchFocused = focusState.isFocused
                            },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color(0xFFFAFAFA),
                            focusedBorderColor = Color(0xFF1B5E20),
                            unfocusedBorderColor = Color.LightGray,
                            cursorColor = Color(0xFF1B5E20)
                        ),
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        searchQuery = ""
                                        activeSearchQuery = ""
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear search",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else {
                                IconButton(
                                    onClick = {
                                        searchFocusRequester.requestFocus()
                                        activeSearchQuery = searchQuery
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = Color(0xFF1B5E20),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    )
                }
            }

            // Drag Subtitle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Text(
                    text = "💡 Tap a crop to select it, then drag or tap farm map to plant",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2E7D32)
                )
            }

            // Plant Cards Grid
            if (filteredCrops.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No crops found",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredCrops) { crop ->
                        val isSelected = selectedCropName.equals(crop.name, ignoreCase = true)
                        CropChipCard(
                            crop = crop,
                            isSelected = isSelected,
                            onClick = { onCropSelected(crop.name, crop.id) },
                            onDragStart = { offset -> onCropDragStart(crop.name, crop.id, offset) },
                            onDragging = onCropDragging,
                            onDragEnd = onCropDragEnd
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CropChipCard(
    crop: CropOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDragStart: (Offset) -> Unit,
    onDragging: (Offset) -> Unit,
    onDragEnd: (Offset) -> Unit
) {
    val bgColor = if (isSelected) Color(0xFFE8F5E9) else Color(0xFFF8F9FA)
    val borderColor = if (isSelected) Color(0xFF1B5E20) else Color.LightGray.copy(alpha = 0.5f)
    var cardRootOffset by remember { mutableStateOf(Offset.Zero) }
    var currentTouchOffset by remember { mutableStateOf(Offset.Zero) }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        shadowElevation = if (isSelected) 4.dp else 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                cardRootOffset = coordinates.positionInRoot()
            }
            .pointerInput(crop.id) {
                detectDragGestures(
                    onDragStart = { localOffset ->
                        onClick()
                        currentTouchOffset = cardRootOffset + localOffset
                        onDragStart(currentTouchOffset)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        currentTouchOffset = cardRootOffset + change.position
                        onDragging(currentTouchOffset)
                    },
                    onDragEnd = {
                        onDragEnd(currentTouchOffset)
                    },
                    onDragCancel = {
                        onDragEnd(currentTouchOffset)
                    }
                )
            }
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Crop image from assets/metadata/crops_images
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) Color(0xFFC8E6C9) else Color(0xFFE8F5E9).copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                val assetUri = "file:///android_asset/metadata/crops_images/${crop.imageFileName}"
                AsyncImage(
                    model = assetUri,
                    contentDescription = crop.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = crop.name,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = if (isSelected) Color(0xFF1B5E20) else Color.Black,
                    maxLines = 1
                )
                Text(
                    text = if (isSelected) "${crop.category} • Drag or Tap Map" else "${crop.category} • Tap to Select",
                    fontSize = 8.sp,
                    color = if (isSelected) Color(0xFF1B5E20) else Color.Gray,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1
                )
            }
        }
    }
}