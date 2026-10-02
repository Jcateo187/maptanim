package com.maptanim.app.ui.components.editcomponents.croptray

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.maptanim.app.domain.model.Crop

data class CropOption(
    val id: String,
    val name: String,
    val localName: String? = null,
    val emoji: String = "🌱",
    val category: String,
    val lifeType: String = "Seasonal",
    val imageFileName: String = "${id}.png",
    val imageUrl: String? = null,
    val hasAsset: Boolean = true,
    val isBed: Boolean = false,
    val bedColor: Color? = null
)

fun Crop.toCropOption(): CropOption {
    val cleanId = id.lowercase().replace(" ", "_")
    val defaultAssetFileName = when (cleanId) {
        "stringbeans", "sitaw", "string_beans" -> "sitaw.png"
        "eggplant", "talong" -> "eggplant.png"
        "tomato", "kamatis" -> "tomato.png"
        "onion", "sibuyas" -> "onion.png"
        "pumpkin", "squash", "kalabasa" -> "pumpkin.png"
        "corn", "mais" -> "corn.png"
        "cabbage", "repolyo" -> "cabbage.png"
        "pechay" -> "pechay.png"
        "ampalaya", "bittergourd", "bitter_gourd" -> "ampalaya.png"
        "okra" -> "okra.png"
        "sili", "chili", "chili_pepper", "pepper" -> "sili.png"
        "cucumber", "pipino" -> "pipino.png"
        "kangkong", "water_spinach" -> "kangkong.png"
        "lettuce", "litsugas" -> "lettuce.png"
        "carrot", "karot" -> "carrot.png"
        else -> "${cleanId}.png"
    }

    val emojiIcon = when (category.lowercase()) {
        "root" -> "🥕"
        "fruit" -> "🍅"
        "leafy" -> "🥬"
        "podded" -> "🫘"
        "bulb" -> "🧅"
        "stem" -> "🌽"
        else -> "🌱"
    }

    return CropOption(
        id = id,
        name = name,
        localName = localName,
        emoji = emojiIcon,
        category = category.ifBlank { "Vegetable" },
        lifeType = "Seasonal",
        imageFileName = defaultAssetFileName,
        imageUrl = imageUrl,
        hasAsset = !imageUrl.isNullOrBlank()
    )
}

val AVAILABLE_CROP_CATALOG = listOf(
    // ── Garden Bed (Color Only) ──────────────────────────────────────────
    CropOption(
        id = "bed",
        name = "Bed",
        localName = "Kama ng Halaman",
        emoji = "🟫",
        category = "Bed",
        lifeType = "Permanent",
        imageFileName = "",
        isBed = true,
        bedColor = Color(0xFF6D4C41)
    ),
    // ── Vegetables ────────────────────────────────────────────────────────
    CropOption("carrot", "Carrot", "Karot", "🥕", "Root", lifeType = "Seasonal", imageFileName = "carrot.png"),
    CropOption("stringbeans", "String Beans", "Sitaw", "🫘", "Podded", lifeType = "Seasonal", imageFileName = "sitaw.png"),
    CropOption("eggplant", "Eggplant", "Talong", "🍆", "Fruit", lifeType = "Permanent", imageFileName = "eggplant.png"),
    CropOption("tomato", "Tomato", "Kamatis", "🍅", "Fruit", lifeType = "Semi Permanent", imageFileName = "tomato.png"),
    CropOption("onion", "Onion", "Sibuyas", "🧅", "Bulb", lifeType = "Seasonal", imageFileName = "onion.png"),
    CropOption("pumpkin", "Squash", "Kalabasa", "🎃", "Fruit", lifeType = "Seasonal", imageFileName = "pumpkin.png"),
    CropOption("corn", "Corn", "Mais", "🌽", "Stem", lifeType = "Seasonal", imageFileName = "corn.png"),
    CropOption("cabbage", "Cabbage", "Repolyo", "🥬", "Leafy", lifeType = "Seasonal", imageFileName = "cabbage.png"),
    CropOption("pechay", "Pechay", "Pechay", "🥬", "Leafy", lifeType = "Seasonal", imageFileName = "pechay.png"),
    CropOption("ampalaya", "Ampalaya", "Ampalaya", "🥒", "Fruit", lifeType = "Seasonal", imageFileName = "ampalaya.png"),
    CropOption("okra", "Okra", "Okra", "🌿", "Fruit", lifeType = "Seasonal", imageFileName = "okra.png"),
    CropOption("sili", "Chili Pepper", "Sili", "🌶️", "Fruit", lifeType = "Permanent", imageFileName = "sili.png"),
    CropOption("cucumber", "Cucumber", "Pipino", "🥒", "Fruit", lifeType = "Seasonal", imageFileName = "pipino.png"),
    CropOption("kangkong", "Kangkong", "Kangkong", "🥬", "Leafy", lifeType = "Seasonal", imageFileName = "kangkong.png"),
    CropOption("lettuce", "Lettuce", "Litsugas", "🥗", "Leafy", lifeType = "Seasonal", imageFileName = "lettuce.png")
)

val CATEGORY_OPTIONS = listOf(
    "All", "Leafy", "Root", "Bulb", "Stem", "Flower", "Podded", "Tuber", "Fruit"
)

enum class TrayTab { BED, CROPS }

/**
 * CropTray — Right-side crop selection panel with CoC-style Drag & Drop support.
 * Dynamically populated from local Room database / Supabase Storage sync.
 */
@Composable
fun CropTray(
    modifier: Modifier = Modifier,
    selectedCropName: String? = null,
    availableCrops: List<CropOption> = AVAILABLE_CROP_CATALOG,
    isSyncing: Boolean = false,
    onSyncRequested: (() -> Unit)? = null,
    onCropSelected: (cropName: String, cropId: String) -> Unit = { _, _ -> },
    onCropDragStart: (cropName: String, cropId: String, imageUrl: String?, screenOffset: Offset) -> Unit = { _, _, _, _ -> },
    onCropDragging: (screenOffset: Offset) -> Unit = { _ -> },
    onCropDragEnd: (screenOffset: Offset) -> Unit = { _ -> },
    onClose: () -> Unit = {}
) {
    var activeTrayTab by remember {
        mutableStateOf(if (selectedCropName.equals("Bed", ignoreCase = true)) TrayTab.BED else TrayTab.CROPS)
    }

    LaunchedEffect(selectedCropName) {
        if (selectedCropName.equals("Bed", ignoreCase = true)) {
            activeTrayTab = TrayTab.BED
        } else if (!selectedCropName.isNullOrBlank()) {
            activeTrayTab = TrayTab.CROPS
        }
    }

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

    val filteredCrops = remember(activeTrayTab, selectedCategory, activeSearchQuery, availableCrops) {
        if (activeTrayTab == TrayTab.BED) {
            availableCrops.filter { it.isBed || it.id.equals("bed", ignoreCase = true) }
        } else {
            availableCrops.filter { !it.isBed && !it.id.equals("bed", ignoreCase = true) }.filter { crop ->
                val categoryMatch = selectedCategory == "All" || crop.category.equals(selectedCategory, ignoreCase = true)
                val searchMatch = activeSearchQuery.isBlank() ||
                        crop.name.contains(activeSearchQuery, ignoreCase = true) ||
                        (!crop.localName.isNullOrBlank() && crop.localName.contains(activeSearchQuery, ignoreCase = true))

                categoryMatch && searchMatch
            }
        }
    }

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val trayShape = if (isLandscape) {
        RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    }

    val trayModifier = if (isLandscape) {
        modifier.fillMaxHeight().width(230.dp)
    } else {
        val trayHeight = if (activeTrayTab == TrayTab.BED) 130.dp else 215.dp
        modifier.fillMaxWidth().height(trayHeight)
    }

    Surface(
        shape = trayShape,
        color = Color.White.copy(alpha = 0.98f),
        shadowElevation = 10.dp,
        modifier = trayModifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // ── Combined Slim Header & Tabs Bar ──────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Segmented Pill for Bed | Crops
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF1F8E9))
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Bed Tab Button
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (activeTrayTab == TrayTab.BED) Color(0xFF6D4C41) else Color.Transparent,
                        modifier = Modifier.clickable { activeTrayTab = TrayTab.BED }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(11.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (activeTrayTab == TrayTab.BED) Color.White else Color(0xFF6D4C41))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Bed",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (activeTrayTab == TrayTab.BED) Color.White else Color(0xFF4E342E)
                            )
                        }
                    }

                    // Crops Tab Button
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (activeTrayTab == TrayTab.CROPS) Color(0xFF2E7D32) else Color.Transparent,
                        modifier = Modifier.clickable { activeTrayTab = TrayTab.CROPS }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🌱", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Crops",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (activeTrayTab == TrayTab.CROPS) Color.White else Color(0xFF1B5E20)
                            )
                        }
                    }
                }

                // Header Actions: Count Badge, Sync Button, Close Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "(${filteredCrops.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeTrayTab == TrayTab.BED) Color(0xFF8D6E63) else Color(0xFF43A047)
                    )
                    if (onSyncRequested != null && activeTrayTab == TrayTab.CROPS) {
                        IconButton(
                            onClick = onSyncRequested,
                            enabled = !isSyncing,
                            modifier = Modifier.size(24.dp)
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    strokeWidth = 1.5.dp,
                                    color = Color(0xFF1B5E20)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Sync crops",
                                    tint = Color(0xFF1B5E20),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Panel",
                            tint = Color.DarkGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Category & Search (Only shown for Crops tab)
            if (activeTrayTab == TrayTab.CROPS) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Category Dropdown Filter
                    Box {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (selectedCategory != "All") Color(0xFF1B5E20) else Color(0xFFE8E8E8),
                            modifier = Modifier
                                .height(30.dp)
                                .clickable { categoryMenuExpanded = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Category Filter",
                                    tint = if (selectedCategory != "All") Color.White else Color.Black,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = if (selectedCategory == "All") "Cat." else selectedCategory,
                                    fontSize = 10.sp,
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
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }

                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

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
                                            fontSize = 12.sp
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

                    // Compact Search Bar using BasicTextField
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF5F5F5),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSearchFocused) Color(0xFF1B5E20) else Color(0xFFE0E0E0)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color(0xFF1B5E20),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Search crops...",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = {
                                        searchQuery = it
                                        activeSearchQuery = it
                                    },
                                    singleLine = true,
                                    textStyle = LocalTextStyle.current.copy(
                                        color = Color.Black,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .focusRequester(searchFocusRequester)
                                        .onFocusChanged { focusState ->
                                            isSearchFocused = focusState.isFocused
                                        }
                                )
                            }
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        searchQuery = ""
                                        activeSearchQuery = ""
                                    },
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Concise Hint Text
            Text(
                text = if (activeTrayTab == TrayTab.BED) {
                    "💡 Tap bed to select, then drag or tap to place"
                } else {
                    "💡 Tap crop to select, then drag onto a Bed"
                },
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Medium,
                color = if (activeTrayTab == TrayTab.BED) Color(0xFF5D4037) else Color(0xFF2E7D32),
                modifier = Modifier.padding(horizontal = 2.dp)
            )

            // Cards Grid
            if (filteredCrops.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No crops found",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = if (isLandscape) GridCells.Fixed(2) else GridCells.Adaptive(minSize = 95.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredCrops) { crop ->
                        val isSelected = selectedCropName.equals(crop.name, ignoreCase = true)
                        CropChipCard(
                            crop = crop,
                            isSelected = isSelected,
                            onClick = { onCropSelected(crop.name, crop.id) },
                            onDragStart = { offset -> onCropDragStart(crop.name, crop.id, crop.imageUrl, offset) },
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

    // CRITICAL: Only attach detectDragGestures when the card is already selected.
    // When unselected (default), touch events are not consumed, allowing LazyVerticalGrid
    // to scroll vertically without any accidental drag interceptions or stuttering.
    val dragModifier = if (isSelected) {
        Modifier.pointerInput(crop.id, isSelected) {
            detectDragGestures(
                onDragStart = { localOffset ->
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
    } else {
        Modifier
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(if (isSelected) 1.5.dp else 0.8.dp, borderColor),
        shadowElevation = if (isSelected) 3.dp else 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                cardRootOffset = coordinates.positionInRoot()
            }
            .then(dragModifier)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Compact Crop Image Box (34dp)
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) Color(0xFFC8E6C9) else Color(0xFFE8F5E9).copy(alpha = 0.7f))
                    .border(
                        1.dp,
                        if (isSelected) Color(0xFF388E3C) else Color(0xFF81C784).copy(alpha = 0.35f),
                        RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (crop.isBed && crop.bedColor != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(3.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(crop.bedColor)
                            .border(1.dp, Color.White.copy(alpha = 0.45f), RoundedCornerShape(4.dp))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val diamond = Path().apply {
                                moveTo(w / 2f, 2f)
                                lineTo(w - 2f, h / 2f)
                                lineTo(w / 2f, h - 2f)
                                lineTo(2f, h / 2f)
                                close()
                            }
                            drawPath(
                                path = diamond,
                                color = Color.White.copy(alpha = 0.35f),
                                style = Stroke(width = 1.dp.toPx())
                            )
                        }
                    }
                } else {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    ) {
                        com.maptanim.app.renderer.canvas.CropSvgRenderer.drawCropSvg(
                            drawScope = this,
                            cropName = crop.name,
                            center = Offset(size.width / 2f, size.height / 2f),
                            sizePx = size.width * 0.85f
                        )
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                val displayName = if (!crop.localName.isNullOrBlank() && !crop.name.contains(crop.localName, ignoreCase = true)) {
                    "${crop.name} (${crop.localName})"
                } else {
                    crop.name
                }
                Text(
                    text = displayName,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 10.sp,
                    color = if (isSelected) Color(0xFF1B5E20) else Color.Black,
                    maxLines = 1
                )
                Text(
                    text = if (isSelected) {
                        if (crop.isBed) "Bed • Drag to Map" else "${crop.category} • Drag to Bed"
                    } else {
                        if (crop.isBed) "Garden Bed" else crop.category
                    },
                    fontSize = 8.sp,
                    color = if (isSelected) Color(0xFF1B5E20) else Color.Gray,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1
                )
            }
        }
    }
}
