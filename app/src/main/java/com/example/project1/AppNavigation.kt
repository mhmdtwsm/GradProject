package com.example.project1

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project1.onboard.OnboardingScreen
import com.example.project1.Register.LoginScreen
import com.example.project1.Register.RegisterScreen
import com.example.project1.home.HomeScreen
import com.example.project1.tools.passwordtest.PasswordTest
import com.example.project1.tools.ToolsMenu

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object ToolsMenu : Screen("toolsMenu")
    object PasswordTest : Screen("passwordTest")
}

@Composable
fun AppNavigation(startDestination: String = Screen.Onboarding.route) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        // Onboarding Screen
        composable(Screen.Onboarding.route) {
            OnboardingScreen {
                // Navigate to Login screen when onboarding is finished
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            }
        }

        // Login Screen
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    // Navigate to Register screen
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToHome = {
                    // Navigate to Home screen
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Register Screen
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    // Navigate back to Login screen
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    // Navigate to Home screen
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Home Screen
        composable(Screen.Home.route) {
            HomeScreen(userName = "User_1", navController = navController)
        }

        composable(Screen.ToolsMenu.route) {
            ToolsMenu(navController = navController)
        }

        composable(Screen.PasswordTest.route) {
            PasswordTest()
        }
    }
}