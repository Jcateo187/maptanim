package com.maptanim.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.maptanim.app.features.about.screen.AboutScreen
import com.maptanim.app.features.auth.screen.ForgotPasswordScreen
import com.maptanim.app.features.auth.screen.LoginScreen
import com.maptanim.app.features.auth.screen.WelcomeScreen
import com.maptanim.app.features.community.screen.CommunityScreen
import com.maptanim.app.features.farm.screen.FarmScreen
import com.maptanim.app.features.home.screen.MainHomeScreen
import com.maptanim.app.features.splash.screen.LoadingScreen
import com.maptanim.app.features.profile.ProfileScreen
import com.maptanim.app.features.reports.screen.ReportsScreen
import com.maptanim.app.features.splash.screen.CompanyLogoScreen
import com.maptanim.app.features.library.screen.VegetablesScreen
import com.maptanim.app.features.notifications.screen.NotificationsScreen

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

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(navController)
        }

        composable(Routes.LOADING) {
            LoadingScreen(navController)
        }

        composable(
            route = Routes.HOME,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            MainHomeScreen(navController)
        }

        composable(
            route = Routes.PROFILE
        ) {
            ProfileScreen(navController = navController, initialTab = 0)
        }

        composable(
            route = Routes.PROFILE_WITH_TAB,
            arguments = listOf(navArgument("tab") { type = NavType.IntType; defaultValue = 0 })
        ) { backStackEntry ->
            val tab = backStackEntry.arguments?.getInt("tab") ?: 0
            ProfileScreen(navController = navController, initialTab = tab)
        }

        composable(
            route = "${Routes.LIBRARY}?cropName={cropName}",
            arguments = listOf(
                navArgument("cropName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) { backStackEntry ->
            val cropName = backStackEntry.arguments?.getString("cropName")
            VegetablesScreen(navController = navController, initialCropName = cropName)
        }

        composable(
            route = Routes.COMMUNITY
        ) {
            CommunityScreen(navController = navController)
        }

        composable(
            route = Routes.SETTINGS
        ) {
            ProfileScreen(navController = navController, initialTab = 1)
        }

        composable(
            route = Routes.NOTIFICATIONS
        ) {
            NotificationsScreen(navController = navController)
        }

        composable(
            route = Routes.FARM,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    tween(300)
                )
            }
        ) {
            FarmScreen(navController = navController)
        }

        composable(
            route = Routes.ABOUT,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    tween(300)
                )
            }
        ) {
            AboutScreen(navController = navController)
        }

        composable(
            route = Routes.REPORTS,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    tween(300)
                )
            }
        ) {
            ReportsScreen(navController = navController)
        }
    }
}