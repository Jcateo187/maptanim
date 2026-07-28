# 11. Navigation — Jetpack Compose Navigation Graph

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
    object Profile   : Screen("profile")

    // Detail routes with args
    object BedDetail : Screen("bed_detail/{bedId}") {
        fun route(bedId: String) = "bed_detail/$bedId"
    }
    object CropDetail : Screen("crop_detail/{cropName}") {
        fun route(cropName: String) = "crop_detail/$cropName"
    }
    object Notifications : Screen("notifications")
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
                    EditBottomBar(...)
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
            route = Screen.BedDetail.route,
            arguments = listOf(navArgument("bedId") { type = NavType.StringType })
        ) { backStackEntry ->
            BedDetailScreen(bedId = backStackEntry.arguments?.getString("bedId") ?: "")
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
