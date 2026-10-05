package com.maptanim.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * BottomNavItem — Defines the primary 5-tab navigation items for MapTanim:
 * Home, Farm, Community, Library, Profile.
 */
sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem(Routes.HOME, Icons.Default.Home, "Home")
    object Farm : BottomNavItem(Routes.FARM, Icons.Default.Agriculture, "Farm")
    object Community : BottomNavItem(Routes.COMMUNITY, Icons.Default.Groups, "Community")
    object Vegetables : BottomNavItem(Routes.LIBRARY, Icons.AutoMirrored.Filled.MenuBook, "Library")
    object Profile : BottomNavItem(Routes.PROFILE, Icons.Default.Person, "Profile")

    companion object {
        val items = listOf(Home, Farm, Community, Vegetables, Profile)
    }
}
