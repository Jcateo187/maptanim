package com.maptanim.app.navigation

/**
 * Single source of truth for all application routes in MapTanim.
 * Zero duplicate aliases.
 */
object Routes {
    // Auth & Splash Flow
    const val COMPANY = "company"
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val FORGOT_PASSWORD = "forgot_password"
    const val LOADING = "loading"

    // 5 Primary Navigation Tabs (Single Source of Truth)
    const val HOME = "home"
    const val FARM = "farms"            // Unified Single-Screen Farm Hub (Canvas + Tabs)
    const val COMMUNITY = "community"
    const val LIBRARY = "library"       // Replaces legacy VEGETABLES and KNOWLEDGE
    const val PROFILE = "profile"
    const val PROFILE_WITH_TAB = "profile?tab={tab}"

    // Secondary / Modal Routes
    const val SETTINGS = "settings"
    const val NOTIFICATIONS = "notifications"
    const val ABOUT = "about"

    fun profileRoute(tab: Int = 0) = "profile?tab=$tab"
    fun libraryRoute(cropName: String? = null) = if (cropName.isNullOrBlank()) LIBRARY else "$LIBRARY?cropName=$cropName"
}