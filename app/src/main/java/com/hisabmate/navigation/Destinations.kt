package com.hisabmate.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object AddEditRecord : Screen("add_edit_record/{dateMillis}") {
        fun createRoute(dateMillis: Long) = "add_edit_record/$dateMillis"
    }
    object Calendar : Screen("calendar")
    object Summary : Screen("summary")
    object Streaks : Screen("streaks")
}
