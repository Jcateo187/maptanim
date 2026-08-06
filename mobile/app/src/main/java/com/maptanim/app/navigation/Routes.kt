package com.maptanim.app.navigation

object Routes {
    const val COMPANY = "company"
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val FORGOT_PASSWORD = "forgot_password"
    const val LOADING = "loading"
    const val WELCOME_GUIDE = "welcome_guide"

    // 5-Tab Navigation Routes
    const val HOME = "home"
    const val FARMS = "farms"
    const val CALENDAR = "calendar"
    const val LIBRARY = "library"
    const val COMMUNITY = "community"
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