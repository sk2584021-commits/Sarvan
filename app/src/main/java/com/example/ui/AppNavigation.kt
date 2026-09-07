package com.example.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "auth_check"
    ) {
        composable("auth_check") {
            AuthCheckScreen(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                },
                onNavigateToHome = {
                    navController.navigate("main") {
                        popUpTo(0)
                    }
                }
            )
        }
        composable("login") {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate("signup") },
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo(0)
                    }
                }
            )
        }
        composable("signup") {
            SignUpScreen(
                onNavigateBack = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.navigate("main") {
                        popUpTo(0)
                    }
                }
            )
        }
        composable("main") {
            MainContainerScreen(rootNavController = navController)
        }
        composable("messages") {
            MessagesScreen(
                onBack = { navController.popBackStack() },
                onChatClick = { userId ->
                    navController.navigate("chat/$userId")
                }
            )
        }
        composable("chat/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            ChatScreen(
                otherUserId = userId,
                onBack = { navController.popBackStack() }
            )
        }
        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate("login") { popUpTo(0) }
                },
                onNavigateToCreator = {
                    navController.navigate("creator_dashboard")
                }
            )
        }
        composable("creator_dashboard") {
            CreatorDashboardScreen(
                onBack = { navController.popBackStack() },
                onNavigateToMonetization = {
                    navController.navigate("monetization_app")
                }
            )
        }
        composable("monetization_app") {
            MonetizationAppScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
