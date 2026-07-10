package com.maptanim.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.maptanim.app.ui.screens.auth.LoginScreen
import com.maptanim.app.ui.screens.auth.WelcomeGuideScreen
import com.maptanim.app.ui.screens.auth.WelcomeScreen
import com.maptanim.app.ui.screens.home.HomeScreen
import com.maptanim.app.ui.screens.loading.LoadingScreen
import com.maptanim.app.ui.screens.splash.CompanyLogoScreen

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

        composable(Routes.WELCOME) {
            WelcomeScreen(navController)
        }



        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }

        composable(Routes.LOADING) {
            LoadingScreen(navController)
        }

        composable(Routes.WELCOME_GUIDE) {
            WelcomeGuideScreen(navController)
        }

        composable(Routes.HOME) {
            HomeScreen()
        }

    }
}