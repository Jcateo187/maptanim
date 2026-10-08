package com.maptanim.app.features.library.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maptanim.app.features.farm.dialogs.CropInformationDialog
import com.maptanim.app.features.library.components.CropCatalogGrid
import com.maptanim.app.features.library.model.LibraryCategory
import com.maptanim.app.features.library.viewmodel.LibraryViewModel
import com.maptanim.app.navigation.MainBottomNavBar
import com.maptanim.app.navigation.Routes

/**
 * LibraryScreen — Main crop knowledge catalog & encyclopedia screen.
 * Uses CropCatalogGrid for 3-column visual browsing and CropInformationDialog
 * for detailed agronomic and encyclopedic dossiers on each crop.
 */
@Composable
fun LibraryScreen(
    navController: NavController,
    initialCropName: String? = null,
    viewModel: LibraryViewModel = viewModel()
) {
    val allCrops by viewModel.crops.collectAsState()
    val selectedCrop by viewModel.selectedCrop.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(LibraryCategory.ALL) }

    // Direct deep-link: If initialCropName is provided, open that crop's dialog
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
            val family = com.maptanim.app.features.farm.components.getCropFamily(crop)
            val matchesCategory = when (selectedCategory) {
                LibraryCategory.ALL -> true
                LibraryCategory.FRUIT -> crop.category.contains("FRUIT", ignoreCase = true) || crop.category.contains("SOLANACEOUS", ignoreCase = true) || family.scientificName == "Solanaceae"
                LibraryCategory.CUCURBIT -> crop.category.contains("CUCURBIT", ignoreCase = true) || family.scientificName == "Cucurbitaceae"
                LibraryCategory.LEAFY -> crop.category.contains("LEAFY", ignoreCase = true) || family.scientificName in listOf("Brassicaceae", "Convolvulaceae", "Asteraceae")
                LibraryCategory.LEGUME -> crop.category.contains("LEGUME", ignoreCase = true) || family.scientificName == "Fabaceae"
                LibraryCategory.ROOT -> crop.category.contains("ROOT", ignoreCase = true) || family.scientificName == "Apiaceae"
                LibraryCategory.BULB -> crop.category.contains("BULB", ignoreCase = true) || family.scientificName in listOf("Amaryllidaceae", "Poaceae")
            }
            val matchesQuery = searchQuery.isBlank() ||
                    crop.name.contains(searchQuery, ignoreCase = true) ||
                    (crop.localName?.contains(searchQuery, ignoreCase = true) == true) ||
                    (crop.botanicalName?.contains(searchQuery, ignoreCase = true) == true) ||
                    family.scientificName.contains(searchQuery, ignoreCase = true) ||
                    family.commonName.contains(searchQuery, ignoreCase = true)

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
            // ── 1. Visual Catalog Grid ───────────────────────────────────
            CropCatalogGrid(
                crops = filteredCrops,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                selectedCategory = selectedCategory,
                onSelectCategory = { selectedCategory = it },
                onSelectCrop = { viewModel.selectCrop(it) }
            )

            // ── 2. Unified Crop Information Dialog for Selected Crop ──────
            selectedCrop?.let { crop ->
                CropInformationDialog(
                    cropName = crop.name,
                    onDismiss = { viewModel.selectCrop(null) }
                )
            }
        }
    }
}
