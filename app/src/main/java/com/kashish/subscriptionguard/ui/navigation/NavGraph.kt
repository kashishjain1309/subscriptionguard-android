package com.kashish.subscriptionguard.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kashish.subscriptionguard.ui.component.BottomNavBar
import com.kashish.subscriptionguard.ui.screen.AddEditSubscriptionScreen
import com.kashish.subscriptionguard.ui.screen.HomeScreen
import com.kashish.subscriptionguard.ui.screen.LoginScreen
import com.kashish.subscriptionguard.ui.screen.ProfileScreen
import com.kashish.subscriptionguard.ui.screen.SplashScreen
import com.kashish.subscriptionguard.ui.screen.SubscriptionDetailScreen

@Composable
fun NavGraph() {
    val navController: NavHostController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute == Screen.Home.route || currentRoute == Screen.Profile.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onHomeClick = { navController.navigate(Screen.Home.route) { launchSingleTop = true } },
                    onProfileClick = { navController.navigate(Screen.Profile.route) { launchSingleTop = true } }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onAddSubscription = { navController.navigate(Screen.AddEdit.route) },
                    onSubscriptionClick = { id -> navController.navigate("${Screen.Detail.route}/$id") }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onLogOut = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable("${Screen.Detail.route}/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: return@composable
                SubscriptionDetailScreen(
                    subscriptionId = id,
                    onBack = { navController.popBackStack() },
                    onEdit = { subId -> navController.navigate("${Screen.AddEdit.route}?id=$subId") },
                    onCancelled = { navController.popBackStack() }
                )
            }

            composable(
                route = "${Screen.AddEdit.route}?id={id}",
                arguments = listOf(navArgument("id") { nullable = true; defaultValue = null })
            ) { backStackEntry ->
                AddEditSubscriptionScreen(
                    subscriptionId = backStackEntry.arguments?.getString("id"),
                    onSaved = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}