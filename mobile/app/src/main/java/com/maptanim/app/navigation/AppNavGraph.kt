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
import com.maptanim.app.ui.screens.about.AboutScreen
import com.maptanim.app.ui.screens.auth.ForgotPasswordScreen
import com.maptanim.app.ui.screens.auth.LoginScreen
import com.maptanim.app.ui.screens.auth.WelcomeScreen
import com.maptanim.app.ui.screens.community.CommunityScreen
import com.maptanim.app.ui.screens.edit.FarmEditorScreen
import com.maptanim.app.ui.screens.farm.FarmScreen
import com.maptanim.app.ui.screens.home.MainHomeScreen
import com.maptanim.app.ui.screens.loading.LoadingScreen
import com.maptanim.app.ui.screens.profile.ProfileScreen
import com.maptanim.app.ui.screens.reports.ReportsScreen
import com.maptanim.app.ui.screens.splash.CompanyLogoScreen
import com.maptanim.app.ui.screens.vegetables.VegetablesScreen

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
            route = Routes.EDIT,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            FarmEditorScreen(navController)
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
            route = Routes.LIBRARY,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            VegetablesScreen(navController = navController)
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
            com.maptanim.app.ui.screens.notifications.NotificationsScreen(navController = navController)
        }

        composable(
            route = Routes.FARMS,
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