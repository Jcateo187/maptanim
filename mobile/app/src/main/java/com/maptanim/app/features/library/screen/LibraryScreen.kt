package com.maptanim.app.features.library.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maptanim.app.features.library.components.CropCatalogGrid
import com.maptanim.app.features.library.components.CropDetailView
import com.maptanim.app.features.library.model.LibraryCategory
import com.maptanim.app.features.library.viewmodel.LibraryViewModel
import com.maptanim.app.navigation.MainBottomNavBar
import com.maptanim.app.navigation.Routes

private val LushGreen = Color(0xFF2E7D32)

/**
 * LibraryScreen — Main crop knowledge catalog & agronomic reference screen.
 * Decomposed from the legacy 2,132-line VegetablesScreen monolith into modular components:
 * 1. CropCatalogGrid (3-column visual catalog + search & category filters)
 * 2. CropDetailView (hero identity, cultivars, companion matrix, phenology timeline, soil cards, 4 numbered protocols)
 *
 * Adheres strictly to the Daylight High-Contrast Theme (Pure White background, Lush Green buttons/tabs/names, Deep Black text).
 */
@Composable
fun LibraryScreen(
    navController: NavController,
    initialCropName: String? = null,
    viewModel: LibraryViewModel = viewModel()
) {
    val allCrops by viewModel.crops.collectAsState()
    val selectedCrop by viewModel.selectedCrop.collectAsState()
    val selectedCropGuide by viewModel.selectedCropGuide.collectAsState()
    val isLoadingGuide by viewModel.isLoadingGuide.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(LibraryCategory.ALL) }

    // Direct deep-link: If initialCropName is provided, open that crop's detail view
    LaunchedEffect(allCrops, initialCropName) {
        if (!initialCropName.isNullOrBlank() && allCrops.isNotEmpty()) {
            val matching = allCrops.firstOrNull {
                it.name.equals(initialCropName, ignoreCase = true) ||
                it.localName?.equals(initialCropName, ignoreCase = true) == true
            }
            if (matching != null) {
                viewModel.selectCrop(matching)
            }
        }
    }

    val filteredCrops = remember(allCrops, searchQuery, selectedCategory) {
        allCrops.filter { crop ->
            val matchesCategory = when (selectedCategory) {
                LibraryCategory.ALL -> true
                LibraryCategory.FRUIT -> crop.category.contains("FRUIT", ignoreCase = true) || crop.category.contains("SOLANACEOUS", ignoreCase = true)
                LibraryCategory.CUCURBIT -> crop.category.contains("CUCURBIT", ignoreCase = true) || crop.name.contains("Ampalaya", ignoreCase = true) || crop.name.contains("Pipino", ignoreCase = true) || crop.name.contains("Kalabasa", ignoreCase = true)
                LibraryCategory.LEAFY -> crop.category.contains("LEAFY", ignoreCase = true) || crop.name.contains("Pechay", ignoreCase = true) || crop.name.contains("Kangkong", ignoreCase = true) || crop.name.contains("Lettuce", ignoreCase = true)
                LibraryCategory.LEGUME -> crop.category.contains("LEGUME", ignoreCase = true) || crop.name.contains("Sitaw", ignoreCase = true) || crop.name.contains("Bean", ignoreCase = true)
                LibraryCategory.ROOT -> crop.category.contains("ROOT", ignoreCase = true) || crop.name.contains("Carrot", ignoreCase = true) || crop.name.contains("Radish", ignoreCase = true)
                LibraryCategory.BULB -> crop.category.contains("BULB", ignoreCase = true) || crop.name.contains("Onion", ignoreCase = true) || crop.name.contains("Garlic", ignoreCase = true)
            }
            val matchesQuery = searchQuery.isBlank() ||
                    crop.name.contains(searchQuery, ignoreCase = true) ||
                    (crop.localName?.contains(searchQuery, ignoreCase = true) == true) ||
                    (crop.botanicalName?.contains(searchQuery, ignoreCase = true) == true)

            matchesCategory && matchesQuery
        }
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            MainBottomNavBar(
                selectedRoute = Routes.LIBRARY,
                onNavigate = { route ->
                    if (route != Routes.LIBRARY) {
                        navController.navigate(route) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            if (selectedCrop == null) {
                // ── 1. Visual Catalog Grid ───────────────────────────────────
                CropCatalogGrid(
                    crops = filteredCrops,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    selectedCategory = selectedCategory,
                    onSelectCategory = { selectedCategory = it },
                    onSelectCrop = { viewModel.selectCrop(it) }
                )
            } else if (isLoadingGuide) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = LushGreen,
                        modifier = Modifier.size(36.dp),
                        strokeWidth = 3.dp
                    )
                }
            } else if (selectedCropGuide != null) {
                // ── 2. Deep Agronomic Detail View ────────────────────────────
                CropDetailView(
                    guide = selectedCropGuide!!,
                    onBack = { viewModel.selectCrop(null) }
                )
            }
        }
    }
}
