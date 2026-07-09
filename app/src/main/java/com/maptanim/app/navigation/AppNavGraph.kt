package com.maptanim.app.navigation

import Routes
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.maptanim.app.ui.screens.guest.GuestScreen
import com.maptanim.app.ui.screens.onboarding.OnboardingScreen
import com.maptanim.app.ui.screens.splash.CompanyLogoScreen
import com.maptanim.app.ui.screens.loading.LoadingScreen
import com.maptanim.app.ui.screens.home.HomeScreen

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()

    NavHost(

        navController = navController,

        startDestination = Routes.COMPANY

    ) {

        composable(Routes.COMPANY) {

            CompanyLogoScreen(navController)

        }

        composable(Routes.LOADING) {

            LoadingScreen(navController)

        }

        composable(Routes.GUEST) {

            GuestScreen()

        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(navController)
        }

        composable(Routes.HOME) {
            HomeScreen()
        }
    }

}


