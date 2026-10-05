package com.maptanim.app.features.library.screen

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maptanim.app.features.library.viewmodel.VegetablesViewModel

/**
 * VegetablesScreen — Backward compatibility delegate.
 * The implementation has been decomposed into modular components in [LibraryScreen].
 */
@Composable
fun VegetablesScreen(
    navController: NavController,
    initialCropName: String? = null,
    viewModel: VegetablesViewModel = viewModel()
) {
    LibraryScreen(
        navController = navController,
        initialCropName = initialCropName,
        viewModel = viewModel
    )
}
