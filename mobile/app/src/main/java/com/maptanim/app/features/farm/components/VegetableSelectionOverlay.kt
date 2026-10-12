package com.maptanim.app.features.farm.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.maptanim.app.R
import com.maptanim.app.features.farm.renderer.canvas.CropSvgRenderer
import com.maptanim.app.domain.model.Crop
import com.maptanim.app.features.farm.tabs.plan.AVAILABLE_CROP_CATALOG
import com.maptanim.app.features.farm.tabs.plan.CropOption

// Anti-glare ergonomic light theme: soothing herbal surface, crisp cards, high-contrast ink
private val LushGreen = Color(0xFF1B5E20)
private val DeepBlack = Color(0xFF141915)
private val Charcoal = Color(0xFF28302A)
private val MutedGreen = Color(0xFF386641)
private val CardBorderColor = Color(0xFFCFD8CB)
private val CardBg = Color(0xFFFFFFFF)
private val SurfaceBg = Color(0xFFF2F5F0)

val CROP_PREVIEW_IMAGES: Map<String, Int> = mapOf(
    "Tomato" to R.drawable.ph_tomato_hero
)

val DEFAULT_CROP_VARIETIES: Map<String, List<String>> = mapOf(
    "Tomato" to listOf("Diamante Max F1", "Cherry", "Apollo", "Roma VF", "Native Kamatis"),
    "Eggplant" to listOf("Fortuner F1", "Casino F1", "Long Purple", "Mistisa F1"),
    "Carrot" to listOf("Terracotta F1", "New Kuroda", "Chantenay"),
    "Lettuce" to listOf("General F1", "Grand Rapids", "Lollo Rossa"),
    "Chili Pepper" to listOf("Django F1", "Red Hot F1", "Siling Labuyo"),
    "Cabbage" to listOf("Rare Ball F1", "Scorpio F1", "K-K Cross"),
    "Pechay" to listOf("Black Behi", "Pavito", "Baby Bok Choy"),
    "Cucumber" to listOf("Pilgrim F1", "Green Magic", "Native Pipino"),
    "Okra" to listOf("Smooth Green F1", "Clemson Spineless"),
    "Onion" to listOf("Red Pinoy F1", "Yellow Granex", "Red Creole"),
    "Squash" to listOf("Suprema F1", "San Mateo"),
    "Corn" to listOf("Macho Sweet F1", "Sweet Pearl"),
    "String Beans" to listOf("Sandigan F1", "Negro F1"),
    "Ampalaya" to listOf("Galaxy Max F1", "Jade Star"),
    "Kangkong" to listOf("Upland Green", "Bonsai")
)

/**
 * Botanical crop family metadata for classification and section headings
 */
data class CropFamilyInfo(
    val scientificName: String,
    val commonName: String,
    val icon: String,
    val sortOrder: Int
)

fun getCropFamily(crop: Crop): CropFamilyInfo {
    val searchTerms = "${crop.name} ${crop.localName.orEmpty()} ${crop.botanicalName.orEmpty()}"
    return getCropFamily(searchTerms)
}

fun getCropFamily(cropName: String): CropFamilyInfo {
    val lower = cropName.lowercase()
    return when {
        lower.contains("tomato") || lower.contains("eggplant") || lower.contains("chili") || lower.contains("sili") || lower.contains("pepper") || lower.contains("kamatis") || lower.contains("talong") || lower.contains("solan") || lower.contains("capsicum") ->
            CropFamilyInfo("Solanaceae", "Nightshade Family", "🍅", 1)

        lower.contains("squash") || lower.contains("cucumber") || lower.contains("ampalaya") || lower.contains("pipino") || lower.contains("kalabasa") || lower.contains("pumpkin") || lower.contains("gourd") || lower.contains("cucurb") || lower.contains("momordica") || lower.contains("patola") || lower.contains("upo") ->
            CropFamilyInfo("Cucurbitaceae", "Gourd Family", "🥒", 2)

        lower.contains("cabbage") || lower.contains("pechay") || lower.contains("repolyo") || lower.contains("bok choy") || lower.contains("brassica") || lower.contains("mustasa") || lower.contains("radish") || lower.contains("labanos") ->
            CropFamilyInfo("Brassicaceae", "Mustard / Cabbage Family", "🥬", 3)

        lower.contains("string beans") || lower.contains("sitaw") || lower.contains("bean") || lower.contains("vigna") || lower.contains("legume") || lower.contains("munggo") || lower.contains("sigarilyas") || lower.contains("bataw") ->
            CropFamilyInfo("Fabaceae", "Legume Family", "🫘", 4)

        lower.contains("onion") || lower.contains("sibuyas") || lower.contains("garlic") || lower.contains("bawang") || lower.contains("allium") || lower.contains("leek") ->
            CropFamilyInfo("Amaryllidaceae", "Allium Family", "🧅", 5)

        lower.contains("carrot") || lower.contains("karot") || lower.contains("daucus") || lower.contains("celery") || lower.contains("kintsay") ->
            CropFamilyInfo("Apiaceae", "Carrot Family", "🥕", 6)

        lower.contains("okra") || lower.contains("abelmoschus") || lower.contains("malva") ->
            CropFamilyInfo("Malvaceae", "Mallow Family", "🌿", 7)

        lower.contains("corn") || lower.contains("mais") || lower.contains("zea") || lower.contains("poac") ->
            CropFamilyInfo("Poaceae", "Grass / Cereal Family", "🌽", 8)

        lower.contains("kangkong") || lower.contains("spinach") || lower.contains("water spinach") || lower.contains("ipomoea") || lower.contains("kamote") || lower.contains("sweet potato") ->
            CropFamilyInfo("Convolvulaceae", "Morning Glory Family", "🥗", 9)

        lower.contains("lettuce") || lower.contains("litsugas") || lower.contains("lactuca") || lower.contains("aster") ->
            CropFamilyInfo("Asteraceae", "Daisy / Aster Family", "🥗", 10)

        else ->
            CropFamilyInfo("Horticultural", "Vegetable Family", "🌱", 99)
    }
}

data class CatalogCategoryChip(
    val name: String,
    val icon: String
)

val CATALOG_CATEGORY_CHIPS = listOf(
    CatalogCategoryChip("All", "🌱"),
    CatalogCategoryChip("Fruit", "🍅"),
    CatalogCategoryChip("Leafy", "🥬"),
    CatalogCategoryChip("Root", "🥕"),
    CatalogCategoryChip("Bulb", "🧅"),
    CatalogCategoryChip("Podded", "🫘"),
    CatalogCategoryChip("Stem", "🌽")
)

/**
 * VegetableSelectionOverlay — Bottom overlay that appears when the user clicks "+ ADD VEGETABLES".
 * Shows cards of vegetables categorized with icons, grouped by crop family headings,
 * with customizable variety modal selection and drag-or-tap planting.
 */
@Composable
fun VegetableSelectionOverlay(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onOpenCropInfo: (cropName: String) -> Unit,
    onCropDragStart: (cropName: String, cropId: String, variety: String?, screenOffset: Offset) -> Unit,
    onCropDragging: (screenOffset: Offset) -> Unit,
    onCropDragEnd: (screenOffset: Offset) -> Unit,
    onPlantDirectly: (cropName: String, cropId: String, variety: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showGuide by remember { mutableStateOf(true) }
    val selectedVarieties = remember { mutableStateMapOf<String, String>() }

    val crops = remember(searchQuery, selectedCategory) {
        AVAILABLE_CROP_CATALOG.filter { !it.isBed }
            .filter { crop ->
                if (selectedCategory == "All") true
                else crop.category.equals(selectedCategory, ignoreCase = true)
            }
            .filter { crop ->
                if (searchQuery.isBlank()) true
                else crop.name.contains(searchQuery, ignoreCase = true) ||
                     (crop.localName?.contains(searchQuery, ignoreCase = true) == true) ||
                     getCropFamily(crop.name).scientificName.contains(searchQuery, ignoreCase = true) ||
                     getCropFamily(crop.name).commonName.contains(searchQuery, ignoreCase = true)
            }
    }

    val groupedCrops = remember(crops) {
        crops.groupBy { getCropFamily(it.name) }
            .toSortedMap(compareBy { it.sortOrder })
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.60f)
                .shadow(16.dp, RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)),
            shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
            color = SurfaceBg,
            border = BorderStroke(1.2.dp, CardBorderColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                // Header Bar (Subtitle 'Drag Vegetable to 2D Canvas' deleted)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "VEGETABLE CATALOG",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = LushGreen,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Select & plant vegetable varieties",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = Charcoal.copy(alpha = 0.75f)
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Charcoal)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar with Guide Toggle Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                "Search vegetable, variety, family...",
                                fontSize = 13.sp,
                                color = Charcoal.copy(alpha = 0.6f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = LushGreen
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = Charcoal,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DeepBlack,
                            unfocusedTextColor = DeepBlack,
                            focusedContainerColor = CardBg,
                            unfocusedContainerColor = CardBg,
                            focusedBorderColor = LushGreen,
                            unfocusedBorderColor = CardBorderColor
                        ),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    // Button beside search bar to show/hide the guide
                    IconButton(
                        onClick = { showGuide = !showGuide },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (showGuide) LushGreen.copy(alpha = 0.12f) else CardBg,
                                RoundedCornerShape(10.dp)
                            )
                            .border(
                                1.dp,
                                if (showGuide) LushGreen else CardBorderColor,
                                RoundedCornerShape(10.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = if (showGuide) "Hide Guide" else "Show Guide",
                            tint = if (showGuide) LushGreen else Charcoal,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Top dismissible guide banner for users
                AnimatedVisibility(visible = showGuide) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFE8F5E9),
                        border = BorderStroke(1.dp, Color(0xFFA5D6A7)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = "💡", fontSize = 15.sp)
                                Text(
                                    text = "Drag vegetable to 2D canvas, or tap card to plant directly into active bed.",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = DeepBlack,
                                    lineHeight = 15.sp
                                )
                            }
                            IconButton(
                                onClick = { showGuide = false },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Hide Guide",
                                    tint = Charcoal,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Crop Category Chips with Icons
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(CATALOG_CATEGORY_CHIPS) { cat ->
                        val isSelected = (selectedCategory == cat.name)
                        Surface(
                            onClick = { selectedCategory = cat.name },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) LushGreen else CardBg,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) LushGreen else CardBorderColor
                            ),
                            shadowElevation = if (isSelected) 2.dp else 0.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(text = cat.icon, fontSize = 13.sp)
                                Text(
                                    text = cat.name,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else DeepBlack
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Grid of Vegetable Cards grouped by Crop Family
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    if (crops.isEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No vegetables found matching \"$searchQuery\"",
                                    fontSize = 13.5.sp,
                                    color = Charcoal.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    groupedCrops.forEach { (family, familyCrops) ->
                        // Heading Name based on Crop Family
                        item(span = { GridItemSpan(2) }, key = "family_${family.scientificName}") {
                            CropFamilyHeader(family = family)
                        }

                        // Cards under this Crop Family
                        items(familyCrops, key = { it.id }) { crop ->
                            val currentVariety = selectedVarieties[crop.name]
                                ?: DEFAULT_CROP_VARIETIES[crop.name]?.firstOrNull()
                                ?: "Standard F1"

                            VegetableCardItem(
                                crop = crop,
                                currentVariety = currentVariety,
                                onVarietySelected = { selectedVarieties[crop.name] = it },
                                onInfoClick = { onOpenCropInfo(crop.name) },
                                onDragStart = { offset ->
                                    onCropDragStart(crop.name, crop.id, currentVariety, offset)
                                },
                                onDragging = onCropDragging,
                                onDragEnd = onCropDragEnd,
                                onPlantDirectly = {
                                    onPlantDirectly(crop.name, crop.id, currentVariety)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Full-width section heading for each taxonomic crop family
 */
@Composable
private fun CropFamilyHeader(family: CropFamilyInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(text = family.icon, fontSize = 14.sp)
        Text(
            text = family.scientificName,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = DeepBlack
        )
        Text(
            text = "(${family.commonName})",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Medium,
            color = MutedGreen
        )
        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = CardBorderColor.copy(alpha = 0.6f),
            thickness = 1.dp
        )
    }
}

@Composable
private fun VegetableCardItem(
    crop: CropOption,
    currentVariety: String,
    onVarietySelected: (String) -> Unit,
    onInfoClick: () -> Unit,
    onDragStart: (Offset) -> Unit,
    onDragging: (Offset) -> Unit,
    onDragEnd: (Offset) -> Unit,
    onPlantDirectly: () -> Unit
) {
    var cardRootOffset by remember { mutableStateOf(Offset.Zero) }
    var currentTouchOffset by remember { mutableStateOf(Offset.Zero) }
    var showVarietyDialog by remember { mutableStateOf(false) }

    val varietiesList = DEFAULT_CROP_VARIETIES[crop.name] ?: listOf("Standard F1", "Native Variety", "Open-Pollinated")

    val dragModifier = Modifier.pointerInput(crop.id, currentVariety) {
        detectDragGesturesAfterLongPress(
            onDragStart = { localOffset ->
                currentTouchOffset = cardRootOffset + localOffset
                onDragStart(currentTouchOffset)
            },
            onDrag = { change, _ ->
                change.consume()
                currentTouchOffset = cardRootOffset + change.position
                onDragging(currentTouchOffset)
            },
            onDragEnd = { onDragEnd(currentTouchOffset) },
            onDragCancel = { onDragEnd(currentTouchOffset) }
        )
    }

    // Variety Selection Modal Dialog (Clean Crisp White Surface — Not Black)
    if (showVarietyDialog) {
        Dialog(
            onDismissRequest = { showVarietyDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.2.dp, CardBorderColor),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "SELECT VARIETY",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = LushGreen,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = crop.name,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepBlack
                            )
                            if (!crop.localName.isNullOrBlank() && !crop.name.equals(crop.localName, ignoreCase = true)) {
                                Text(
                                    text = "(${crop.localName})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Charcoal
                                )
                            }
                        }

                        IconButton(
                            onClick = { showVarietyDialog = false },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Charcoal)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFE5E9E2))
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        varietiesList.forEach { v ->
                            val isSelected = (v == currentVariety)
                            Surface(
                                onClick = {
                                    onVarietySelected(v)
                                    showVarietyDialog = false
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0xFFE8F5E9) else Color(0xFFF9FBF8),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) LushGreen else Color(0xFFDDE3DA)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 11.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = v,
                                        fontSize = 13.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) LushGreen else DeepBlack
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = LushGreen,
                                            modifier = Modifier.size(18.dp)
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

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CardBg,
        border = BorderStroke(1.2.dp, CardBorderColor),
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(175.dp)
            .onGloballyPositioned { coordinates ->
                cardRootOffset = coordinates.positionInRoot()
            }
            .then(dragModifier)
            .clickable { onPlantDirectly() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background SVG or Image taking the WHOLE card
            val previewImageRes = CROP_PREVIEW_IMAGES[crop.name]
            if (previewImageRes != null) {
                Image(
                    painter = painterResource(id = previewImageRes),
                    contentDescription = crop.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF1F8E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 14.dp)
                    ) {
                        CropSvgRenderer.drawCropSvg(
                            drawScope = this,
                            cropName = crop.name,
                            center = Offset(size.width / 2f, (size.height / 2f) - 16.dp.toPx()),
                            sizePx = minOf(size.width, size.height) * 0.85f
                        )
                    }
                }
            }

            // Top-End Info Action Button
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.9f),
                border = BorderStroke(0.5.dp, CardBorderColor),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(28.dp)
                    .clickable { onInfoClick() }
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Crop Info",
                        tint = LushGreen,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            // Bottom Gradient Scrim with Crop Name, Local Name & Variety Selector
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
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    // Crop Name & Local Name
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = crop.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (!crop.localName.isNullOrBlank() && !crop.name.equals(crop.localName, ignoreCase = true)) {
                            Text(
                                text = "(${crop.localName})",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.85f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Variety Selector Trigger
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.White.copy(alpha = 0.92f),
                        border = BorderStroke(0.5.dp, Color(0xFFC8E6C9)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showVarietyDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentVariety,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LushGreen,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Variety",
                                tint = LushGreen,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
