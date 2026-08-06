package com.maptanim.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.maptanim.app.ui.screens.auth.ForgotPasswordScreen
import com.maptanim.app.ui.screens.auth.LoginScreen
import com.maptanim.app.ui.screens.auth.WelcomeGuideScreen
import com.maptanim.app.ui.screens.auth.WelcomeScreen
import com.maptanim.app.ui.screens.about.AboutScreen
import com.maptanim.app.ui.screens.calendar.CalendarScreen
import com.maptanim.app.ui.screens.community.CommunityScreen
import com.maptanim.app.ui.screens.edit.FarmEditorScreen
import com.maptanim.app.ui.screens.home.HomeScreen
import com.maptanim.app.ui.screens.knowledgebase.LibraryScreen
import com.maptanim.app.ui.screens.loading.LoadingScreen
import com.maptanim.app.ui.screens.monitoring.MonitoringScreen
import com.maptanim.app.ui.screens.profile.ProfileScreen
import com.maptanim.app.ui.screens.reports.ReportsScreen
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

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(navController)
        }

        composable(Routes.LOADING) {
            LoadingScreen(navController)
        }

        composable(Routes.WELCOME_GUIDE) {
            WelcomeGuideScreen(navController)
        }

        composable(
            route = Routes.HOME,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    tween(300)
                )
            }
        ) {
            HomeScreen(navController)
        }

        composable(
            route = Routes.EDIT,
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
            FarmEditorScreen(navController)
        }

        composable(
            route = Routes.PROFILE,
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
            ProfileScreen(navController = navController, initialTab = 0)
        }

        composable(
            route = Routes.PROFILE_WITH_TAB,
            arguments = listOf(navArgument("tab") { type = NavType.IntType; defaultValue = 0 }),
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
        ) { backStackEntry ->
            val tab = backStackEntry.arguments?.getInt("tab") ?: 0
            ProfileScreen(navController = navController, initialTab = tab)
        }

        composable(
            route = Routes.LIBRARY,
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
            LibraryScreen(navController = navController)
        }

        composable(
            route = Routes.COMMUNITY,
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
            CommunityScreen(navController = navController)
        }

        composable(
            route = Routes.SETTINGS,
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
            com.maptanim.app.ui.screens.settings.SettingsScreen(navController = navController)
        }

        composable(
            route = Routes.NOTIFICATIONS,
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
            ProfileScreen(navController = navController, initialTab = 1)
        }

        composable(
            route = Routes.CALENDAR,
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
            CalendarScreen(navController = navController)
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
            MonitoringScreen(navController = navController)
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