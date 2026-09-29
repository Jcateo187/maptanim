package com.maptanim.app.navigation

object Routes {
    const val COMPANY = "company"
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val FORGOT_PASSWORD = "forgot_password"
    const val LOADING = "loading"

    // 5-Tab Navigation Routes: Home, Farm, Community, Vegetables, Profile
    const val HOME = "home"
    const val FARM = "farms"
    const val FARMS = "farms"
    const val MONITORING = "farms"
    const val COMMUNITY = "community"
    const val VEGETABLES = "library"
    const val KNOWLEDGE = "library"
    const val LIBRARY = "library"
    const val PROFILE = "profile"
    const val PROFILE_WITH_TAB = "profile?tab={tab}"

    // Edit Mode, Settings & Notifications
    const val EDIT = "edit"
    const val SETTINGS = "settings"
    const val NOTIFICATIONS = "notifications"
    const val ABOUT = "about"
    const val REPORTS = "reports"

    fun profileRoute(tab: Int = 0) = "profile?tab=$tab"
}