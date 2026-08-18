package com.maptanim.app.ui.screens.knowledgebase

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.maptanim.app.domain.model.Crop
import com.maptanim.app.domain.model.PestGuide
import com.maptanim.app.domain.model.SeasonalWindowInfo
import com.maptanim.app.domain.model.SoilGuide

@Composable
fun LibraryScreen(
    navController: NavHostController,
    viewModel: LibraryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showPestWarning by remember { mutableStateOf(false) }
    var isPestImagesHidden by remember { mutableStateOf(false) }

    val view = LocalView.current
    DisposableEffect(view) {
        val window = (view.parent as? DialogWindowProvider)?.window
            ?: (view.context as? Activity)?.window
        window?.let { win ->
            WindowCompat.setDecorFitsSystemWindows(win, false)
            win.setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT
            )
            WindowInsetsControllerCompat(win, win.decorView).apply {
                hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
        onDispose {}
    }

    // Fullscreen edge-to-edge black transparent scrim (same as Profile/Notifications/Settings/Monitoring)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                navController.popBackStack()
            },
        contentAlignment = Alignment.Center
    ) {
        // Main Landscape Overlay Frame (matches Profile, Notification, Setting, Monitoring)
        Card(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .fillMaxHeight(0.88f)
                .padding(vertical = 12.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {},
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D15)),
            border = BorderStroke(1.5.dp, Color(0xFF2E4D3E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // ─── LEFT SIDE NAVIGATION BAR ─────────────────────────────────────────────
                SideNavBar(
                    activeTab = uiState.activeTab,
                    onTabSelect = { tab ->
                        if (tab == LibraryTab.PESTS && uiState.activeTab != LibraryTab.PESTS) {
                            showPestWarning = true
                        } else {
                            viewModel.selectTab(tab)
                        }
                    }
                )

                // ─── RIGHT SIDE CONTENT AREA ─────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(14.dp)
                ) {
                    // Top Control Bar (Search, Crop Category Filters on Right, Eye Hide/Unhide, Close 'X' Button)
                    TopControlBar(
                        activeTab = uiState.activeTab,
                        searchQuery = uiState.searchQuery,
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        selectedCategory = uiState.selectedCategoryFilter,
                        onCategorySelect = { viewModel.selectCategoryFilter(it) },
                        isPestImagesHidden = isPestImagesHidden,
                        onTogglePestImages = { isPestImagesHidden = !isPestImagesHidden },
                        onCloseClick = { navController.popBackStack() }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Active Tab Content View
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        when (uiState.activeTab) {
                            LibraryTab.CROPS -> CropCatalogView(
                                crops = uiState.crops,
                                onCropClick = { viewModel.selectCrop(it) }
                            )
                            LibraryTab.PESTS -> PestCatalogView(
                                pests = uiState.pests,
                                isImagesHidden = isPestImagesHidden,
                                onPestClick = { viewModel.selectPest(it) }
                            )
                            LibraryTab.SOILS -> SoilGuideView(
                                soils = uiState.soils,
                                onSoilClick = { viewModel.selectSoil(it) }
                            )
                            LibraryTab.CALENDAR -> SeasonalCalendarView(
                                windows = uiState.seasonalWindows
                            )
                        }
                    }
                }
            }
        }

        // Modal Dialog Overlays
        uiState.selectedCrop?.let { crop ->
            CropDetailDialog(crop = crop, onDismiss = { viewModel.selectCrop(null) })
        }

        uiState.selectedPest?.let { pest ->
            PestDetailDialog(
                pest = pest,
                isImageHidden = isPestImagesHidden,
                onDismiss = { viewModel.selectPest(null) }
            )
        }

        uiState.selectedSoil?.let { soil ->
            SoilDetailDialog(soil = soil, onDismiss = { viewModel.selectSoil(null) })
        }

        if (showPestWarning) {
            PestGraphicWarningDialog(
                onDismiss = { showPestWarning = false },
                onContinue = {
                    showPestWarning = false
                    isPestImagesHidden = false
                    viewModel.selectTab(LibraryTab.PESTS)
                },
                onHideImage = {
                    showPestWarning = false
                    isPestImagesHidden = true
                    viewModel.selectTab(LibraryTab.PESTS)
                }
            )
        }
    }
}

@Composable
private fun PestGraphicWarningDialog(
    onDismiss: () -> Unit,
    onContinue: () -> Unit,
    onHideImage: () -> Unit
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .widthIn(max = 460.dp)
                    .padding(24.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {},
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1814)),
                border = BorderStroke(1.5.dp, Color(0xFFE5A024).copy(alpha = 0.7f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Warning Icon
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE5A024).copy(alpha = 0.15f))
                            .border(1.5.dp, Color(0xFFE5A024).copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚠️", fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Warning",
                        color = Color(0xFFFFD54F),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "The next screen contains graphic images of pests that may be disturbing to some viewers.",
                        color = Color.White.copy(alpha = 0.88f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. Back Button
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Text(
                                text = "Back",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }

                        // 2. Hide Image Button
                        OutlinedButton(
                            onClick = onHideImage,
                            modifier = Modifier
                                .weight(1.3f)
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFE5A024).copy(alpha = 0.6f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFF2C241B),
                                contentColor = Color(0xFFFFD54F)
                            ),
                            contentPadding = PaddingValues(horizontal = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VisibilityOff,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Hide Image",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }

                        // 3. Continue Button
                        Button(
                            onClick = onContinue,
                            modifier = Modifier
                                .weight(1.2f)
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFD97706),
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Continue",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Left Side Navigation Bar ──────────────────────────────────────────────────

@Composable
private fun SideNavBar(
    activeTab: LibraryTab,
    onTabSelect: (LibraryTab) -> Unit
) {
    Column(
        modifier = Modifier
            .width(210.dp)
            .fillMaxHeight()
            .background(Color(0xFF192A20))
            .border(width = 1.dp, color = Color(0xFF263C2E))
            .padding(vertical = 20.dp, horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LibraryTab.entries.forEach { tab ->
            val isSelected = tab == activeTab
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) Color(0xFF2E7D32) else Color.Transparent)
                    .clickable { onTabSelect(tab) }
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = tab.iconEmoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = tab.title,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

// ─── Top Control Bar Component ─────────────────────────────────────────────────

@Composable
private fun TopControlBar(
    activeTab: LibraryTab,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    isPestImagesHidden: Boolean = false,
    onTogglePestImages: () -> Unit = {},
    onCloseClick: () -> Unit
) {
    val categories = listOf("ALL", "CUCURBIT / VINE", "FRUIT", "LEAFY", "ROOT", "BULB", "PODDED", "TUBER", "GRAIN")
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1B2317),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Search Bar Box
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF2A3424),
                border = BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.6f)),
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    androidx.compose.foundation.text.BasicTextField(
                        value = searchQuery,
                        onValueChange = onQueryChange,
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(Color(0xFF4CAF50)),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search crops, pests, soils, calendar...",
                                    color = Color.White.copy(alpha = 0.45f),
                                    fontSize = 12.sp
                                )
                            }
                            innerTextField()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 2. Crop Category Dropdown Button (Only in Crops Tab)
            if (activeTab == LibraryTab.CROPS) {
                Box {
                    Surface(
                        onClick = { isCategoryDropdownExpanded = true },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF2A3424),
                        border = BorderStroke(1.dp, Color(0xFF4CAF50))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (selectedCategory == "ALL") "All Categories" else selectedCategory,
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = isCategoryDropdownExpanded,
                        onDismissRequest = { isCategoryDropdownExpanded = false },
                        modifier = Modifier
                            .background(Color(0xFF1B2A20))
                            .border(1.dp, Color(0xFF4CAF50).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    ) {
                        categories.forEach { category ->
                            val isSelected = category == selectedCategory
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (category == "ALL") "All Crops" else category,
                                        color = if (isSelected) Color(0xFF81C784) else Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    onCategorySelect(category)
                                    isCategoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // 3. Eye Hide / Unhide Toggle Button (In Pests Tab)
            if (activeTab == LibraryTab.PESTS) {
                Surface(
                    onClick = onTogglePestImages,
                    shape = RoundedCornerShape(8.dp),
                    color = if (isPestImagesHidden) Color(0xFF2C241B) else Color(0xFF2A3424),
                    border = BorderStroke(
                        1.dp,
                        if (isPestImagesHidden) Color(0xFFE5A024).copy(alpha = 0.7f) else Color(0xFF4CAF50).copy(alpha = 0.6f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isPestImagesHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (isPestImagesHidden) "Unhide Images" else "Hide Images",
                            tint = if (isPestImagesHidden) Color(0xFFFFD54F) else Color(0xFFA5D6A7),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isPestImagesHidden) "Images Hidden" else "Images Shown",
                            fontSize = 11.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // 4. Right-Side Close 'X' Button
            IconButton(
                onClick = onCloseClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Library",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ─── Tab 1: Crop Catalog View ────────────────────────────────────────────────

@Composable
private fun CropCatalogView(
    crops: List<Crop>,
    onCropClick: (Crop) -> Unit
) {
    if (crops.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No crops found matching your search.", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(crops) { crop ->
                CropCard(crop = crop, onClick = { onCropClick(crop) })
            }
        }
    }
}

@Composable
private fun CropCard(
    crop: Crop,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(16.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(185.dp)
            .border(1.dp, Color(0xFF4CAF50).copy(alpha = 0.35f), cardShape)
            .clickable { onClick() },
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3326))
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Crop image fitting the entire card with corner radius clipping
            val cropImg = com.maptanim.app.data.datasource.CropMetadataAssetDataSource.getCropAssetImagePath(crop.id, crop.name)
            AsyncImage(
                model = cropImg,
                contentDescription = crop.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(cardShape)
            )

            // Gradient scrim for badge & text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.35f),
                                Color.Transparent,
                                Color(0xFF0A140E).copy(alpha = 0.85f),
                                Color(0xFF070E0A)
                            )
                        )
                    )
            )

            // Category badge at top-right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF14241B).copy(alpha = 0.9f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = crop.category,
                    color = Color(0xFFA5D6A7),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Name inside the image with smooth bottom alignment
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = crop.name,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    crop.localName?.let { local ->
                        if (local.isNotBlank() && !local.equals(crop.name, ignoreCase = true)) {
                            Text(
                                text = "($local)",
                                color = Color(0xFF81C784),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

internal fun formatImageUrl(url: String?): String? {
    if (url.isNullorblank()) return null
    val trimmed = url!!.trim()
    if (trimmed.startsWith("http://") || trimmed.startsWith("https://") || trimmed.startsWith("file://") || trimmed.startsWith("content://")) {
        return trimmed
    }
    val cleanPath = trimmed.trimStart('/')
    return "file:///android_asset/$cleanPath"
}

private fun String?.isNullorblank(): Boolean = this == null || this.trim().isEmpty()

// ─── Tab 2: Pests & Diseases View ────────────────────────────────────────────

@Composable
private fun PestCatalogView(
    pests: List<PestGuide>,
    isImagesHidden: Boolean = false,
    onPestClick: (PestGuide) -> Unit
) {
    if (pests.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No pests found matching your search.", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(pests) { pest ->
                PestCard(
                    pest = pest,
                    isImageHidden = isImagesHidden,
                    onClick = { onPestClick(pest) }
                )
            }
        }
    }
}

@Composable
private fun PestCard(
    pest: PestGuide,
    isImageHidden: Boolean = false,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(16.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(185.dp)
            .border(1.dp, Color(0xFFE53935).copy(alpha = 0.35f), cardShape)
            .clickable { onClick() },
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF26191B))
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (isImageHidden) {
                // Eye close placeholder when images are hidden
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF201618)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 28.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VisibilityOff,
                                contentDescription = "Image Hidden",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "Image Hidden",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                // Pest Image fitting the entire card with corner radius clipping
                val pestImg = com.maptanim.app.data.repository.getPestAssetImagePath(pest.id, pest.name)
                AsyncImage(
                    model = pestImg,
                    contentDescription = pest.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(cardShape)
                )

                // Gradient scrim for badge & text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.35f),
                                    Color.Transparent,
                                    Color(0xFF1A0F11).copy(alpha = 0.85f),
                                    Color(0xFF140B0D)
                                )
                            )
                        )
                )
            }

            // Category badge at top-right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1F1214).copy(alpha = 0.9f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = pest.category,
                    color = Color(0xFFFF8A80),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Name inside the image with bottom alignment
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = pest.name,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    if (pest.localName.isNotBlank() && !pest.localName.equals(pest.name, ignoreCase = true)) {
                        Text(
                            text = "(${pest.localName})",
                            color = Color(0xFFFF8A80),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// ─── Tab 3: Soil Guides View ─────────────────────────────────────────────────

@Composable
private fun SoilGuideView(
    soils: List<SoilGuide>,
    onSoilClick: (SoilGuide) -> Unit
) {
    if (soils.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No soil guides found matching your search.", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(soils) { soil ->
                SoilCard(soil = soil, onClick = { onSoilClick(soil) })
            }
        }
    }
}

@Composable
private fun SoilCard(
    soil: SoilGuide,
    onClick: () -> Unit
) {
    val soilColor = getSoilColor(soil.soilType)
    val cardShape = RoundedCornerShape(16.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(185.dp)
            .border(1.dp, soilColor.copy(alpha = 0.4f), cardShape)
            .clickable { onClick() },
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2820))
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Soil Image fitting the entire card with corner radius clipping
            val soilImg = com.maptanim.app.data.repository.getSoilAssetImagePath(soil.soilType, soil.title)
            AsyncImage(
                model = soilImg,
                contentDescription = soil.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(cardShape)
            )

            // Gradient scrim for badge & text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.35f),
                                Color.Transparent,
                                Color(0xFF121A13).copy(alpha = 0.85f),
                                Color(0xFF0C120D)
                            )
                        )
                    )
            )

            // Soil Type badge at top-right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF141F16).copy(alpha = 0.9f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = soil.soilType.name,
                    color = Color(0xFFFFD54F),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Name inside the image with bottom alignment
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = soil.title,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    if (soil.localName.isNotBlank() && !soil.localName.equals(soil.title, ignoreCase = true)) {
                        Text(
                            text = "(${soil.localName})",
                            color = Color(0xFFA5D6A7),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// ─── Tab 4: Seasonal Planting Calendar View ──────────────────────────────────

@Composable
private fun SeasonalCalendarView(
    windows: List<SeasonalWindowInfo>
) {
    if (windows.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No calendar windows found.", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(windows) { info ->
                SeasonalWindowCard(info = info)
            }
        }
    }
}

@Composable
private fun SeasonalWindowCard(info: SeasonalWindowInfo) {
    val cardShape = RoundedCornerShape(16.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(185.dp)
            .border(1.dp, Color(0xFF4CAF50).copy(alpha = 0.35f), cardShape),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3326))
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Crop image fitting the entire card with corner radius clipping
            val cropImg = com.maptanim.app.data.datasource.CropMetadataAssetDataSource.getCropAssetImagePath(info.cropName, info.cropName)
            AsyncImage(
                model = cropImg,
                contentDescription = info.cropName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(cardShape)
            )

            // Gradient scrim for badges & text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.35f),
                                Color.Transparent,
                                Color(0xFF0F1E15).copy(alpha = 0.85f),
                                Color(0xFF0B1710)
                            )
                        )
                    )
            )

            // Season Badges at top-right
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CompactSeasonBadge("☀️", info.drySeasonStatus)
                CompactSeasonBadge("🌧️", info.wetSeasonStatus)
            }

            // Name & Peak months inside the image with bottom alignment
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${info.cropName} (${info.localName})",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Text(
                        text = "Peak: ${info.peakMonths}",
                        color = Color(0xFFFFD54F),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactSeasonBadge(seasonEmoji: String, status: String) {
    val (bgColor, textColor) = when (status) {
        "OPTIMAL" -> Color(0xFF2E7D32) to Color.White
        "ACCEPTABLE" -> Color(0xFFF59E0B) to Color.Black
        else -> Color(0xFFD32F2F) to Color.White
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Text(text = "$seasonEmoji $status", color = textColor, fontSize = 8.sp, fontWeight = FontWeight.Bold)
    }
}
