package com.maptanim.app.ui.screens.loading

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maptanim.app.navigation.Routes
import com.maptanim.app.viewmodel.LoadingDestination
import com.maptanim.app.viewmodel.LoadingViewModel

@Composable
fun LoadingScreen(
    navController: NavController
) {

    val loadingViewModel: LoadingViewModel = viewModel()

    val destination by loadingViewModel.destination.collectAsState()

    LaunchedEffect(destination) {

        when (destination) {

            is LoadingDestination.Welcome -> {
                navController.navigate(Routes.WELCOME) {
                    popUpTo(Routes.LOADING) { inclusive = true }
                }
            }

            is LoadingDestination.WelcomeGuide -> {
                navController.navigate(Routes.WELCOME_GUIDE) {
                    popUpTo(Routes.LOADING) { inclusive = true }
                }
            }

            is LoadingDestination.Home -> {
                navController.navigate(Routes.HOME) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }

            is LoadingDestination.None -> {
                // Still initializing — stay on loading screen
            }

        }

    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        CircularProgressIndicator()

    }

}