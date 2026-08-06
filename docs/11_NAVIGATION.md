# 11. Navigation — Jetpack Compose Navigation Graph

> 📌 **Navigation**: [◀ 10. API Documentation](file:///d:/Development/MapTanim/docs/10_API_DOCUMENTATION.md) | [🏠 Master Index](file:///d:/Development/MapTanim/docs/README.md) | [12. UI/UX Guidelines ▶](file:///d:/Development/MapTanim/docs/12_UI_UX_GUIDELINES.md)

---
## 📌 Overview
MapTanim uses Jetpack Compose Navigation (`androidx.navigation:navigation-compose`) with a sealed class route system. The app has a single `NavHost` with all routes defined in `AppNavGraph.kt`.

---

## 🔹 Navigation Flow

```
Splash Screen
      │
      ▼
Loading Screen (check session)
      │
   ┌──┴──────────────────┐
   │                     │
   ▼                     ▼
Auth Screen         Home Screen (if session exists)
(Login/OTP/Guest)
   │
   ├──▶ Verified ──────▶ Home Screen
   └──▶ Guest ─────────▶ Home Screen
```

---

## 🔹 Screen Routes

```kotlin
// Routes.kt
sealed class Screen(val route: String) {
    object Splash    : Screen("splash")
    object Loading   : Screen("loading")
    object Auth      : Screen("auth")
    object Home      : Screen("home")
    object Farms     : Screen("farms")
    object Calendar  : Screen("calendar")
    object Library   : Screen("library")
    object Profile       : Screen("profile")
    object Settings      : Screen("settings")
    object Notifications : Screen("notifications")

    // Detail routes with args
    object PlotDetail : Screen("plot_detail/{plotId}") {
        fun route(plotId: String) = "plot_detail/$plotId"
    }
    object CropDetail : Screen("crop_detail/{cropName}") {
        fun route(cropName: String) = "crop_detail/$cropName"
    }
}
```

---

## 🔹 Bottom Navigation Tabs

The 5-tab bottom navigation corresponds to the exact tabs visible in both PNG screenshots:

```kotlin
// BottomNavItem.kt
sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home     : BottomNavItem("home",     Icons.Filled.Home,          "Home")
    object Farms    : BottomNavItem("farms",    Icons.Filled.Grass,         "Farms")
    object Calendar : BottomNavItem("calendar", Icons.Filled.CalendarMonth,  "Calendar")
    object Library  : BottomNavItem("library",  Icons.Filled.MenuBook,      "Library")
    object Profile  : BottomNavItem("profile",  Icons.Filled.Person,        "Profile")
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Farms,
    BottomNavItem.Calendar,
    BottomNavItem.Library,
    BottomNavItem.Profile
)
```

---

## 🔹 Edit Mode Navigation

**Edit Mode is NOT a separate navigation route.** It is a UI state toggled within `HomeScreen`:

```kotlin
// HomeScreen.kt
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isEditMode = uiState.isEditMode

    Scaffold(
        topBar = {
            MapTanimTopBar(
                isEditMode = isEditMode,
                ...
            )
        },
        bottomBar = {
            if (isEditMode) {
                Column {
                    EditBottomLayout(...)
                    BottomNavBar(...)
                }
            } else {
                BottomNavBar(...)
            }
        }
    ) { ... }
}
```

---

## 🔹 AppNavGraph.kt

```kotlin
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onNavigate = {
                navController.navigate(Screen.Loading.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Loading.route) {
            LoadingScreen(
                onNavigateToAuth = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Loading.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Loading.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Auth.route) {
            AuthScreen(onAuthenticated = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Auth.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Farms.route) { FarmsScreen(navController) }
        composable(Screen.Calendar.route) { CalendarScreen(navController) }
        composable(Screen.Library.route) { LibraryScreen(navController) }
        composable(Screen.Profile.route) { ProfileScreen(navController) }

        composable(
            route = Screen.PlotDetail.route,
            arguments = listOf(navArgument("plotId") { type = NavType.StringType })
        ) { backStackEntry ->
            PlotDetailScreen(plotId = backStackEntry.arguments?.getString("plotId") ?: "")
        }

        composable(Screen.Notifications.route) { NotificationsScreen(navController) }
    }
}
```

---

## 🔹 Navigation State Management

- `NavController` is provided by Hilt as a scoped dependency where needed.
- Back stack: pressing Back in Home exits the app (no further back).
- Auth screen: popped from back stack after login (can't navigate back to login).
- Edit Mode: managed via `HomeViewModel.toggleEditMode()`, no nav change.

---

## 🔹 Notification Bell Navigation

Tapping the `🔔` notification bell in the top bar (both View and Edit Mode) navigates to:
```kotlin
navController.navigate(Screen.Notifications.route)
```

This is accessible from all screens that show the TopBar.

---

## 📚 Related Documentation & Cross References
- 📄 [Master Documentation Hub](file:///d:/Development/MapTanim/docs/README.md)
- 📄 [00. Getting Started Guide](file:///d:/Development/MapTanim/docs/00_GETTING_STARTED.md)
- 📄 [03. System Architecture](file:///d:/Development/MapTanim/docs/03_SYSTEM_ARCHITECTURE.md)
- 📄 [12. UI/UX Guidelines](file:///d:/Development/MapTanim/docs/12_UI_UX_GUIDELINES.md)
- 📄 [13. Design System](file:///d:/Development/MapTanim/docs/13_DESIGN_SYSTEM.md)
- 📄 [14. Component Library](file:///d:/Development/MapTanim/docs/14_COMPONENT_LIBRARY.md)
- 📄 [15. Render Engine](file:///d:/Development/MapTanim/docs/15_RENDER_ENGINE.md)
- 📄 [16. Interactive Plot Mapping](file:///d:/Development/MapTanim/docs/16_INTERACTIVE_PLOT_MAPPING.md)
- 📄 [18. View Mode](file:///d:/Development/MapTanim/docs/18_VIEW_MODE.md)
- 📄 [19. Edit Mode](file:///d:/Development/MapTanim/docs/19_EDIT_MODE.md)
- 📄 [34. Direct Soil Crop Planting & Resize System](file:///d:/Development/MapTanim/docs/34_CROP_PLANTING_AND_RESIZE_SYSTEM.md)
- 📄 [35. Asset Planning & Sprites](file:///d:/Development/MapTanim/docs/35_ASSETS_PLANNING.md)
- 📄 [38. Audio & Sound Assets Planning](file:///d:/Development/MapTanim/docs/38_AUDIO_AND_SOUND_ASSETS_PLANNING.md)
- 📄 [39. Crop View Interaction & Variety Simulation](file:///d:/Development/MapTanim/docs/39_CROP_VIEW_INTERACTION_AND_VARIETY_SIMULATION.md)
