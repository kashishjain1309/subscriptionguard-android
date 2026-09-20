package com.kashish.subscriptionguard.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object AddEdit : Screen("addEdit")
    data object Detail : Screen("detail")
    data object Profile : Screen("profile")
}