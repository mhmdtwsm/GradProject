import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project1.SMSScreen
import com.example.project1.URLScreen
import com.example.project1.onboard.OnboardingScreen
import com.example.project1.register.LoginScreen
import com.example.project1.register.RegisterScreen
import com.example.project1.home.HomeScreen
import com.example.project1.settings.terms.TermsScreen
import com.example.project1.tools.passwordtest.PasswordTest
import com.example.project1.tools.ToolsMenu
import com.example.project1.tools.passwordgenerate.PasswordGenerate
import com.example.project1.settings.SettingsScreen
import com.example.project1.settings.help.HelpScreen

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object URL : Screen("url")
    object SMS : Screen("sms")

    // Settings Navigations
    object Settings : Screen("settings")
    object Terms : Screen("terms")
    object Help : Screen("help")

    // Tools Menu Navigations
    object ToolsMenu : Screen("toolsMenu")
    object PasswordTest : Screen("passwordTest")
    object PasswordGenerate : Screen("passwordGenerate")
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

        composable(Screen.URL.route) {
            URLScreen(navController = navController)
        }

        composable(Screen.SMS.route) {
            SMSScreen(navController = navController)
        }

        // Settings Screen
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(Screen.Terms.route) {
            TermsScreen(navController = navController)
        }
        composable(Screen.Help.route) {
            HelpScreen(navController = navController)
        }

        // Tools Menu
        composable(Screen.ToolsMenu.route) {
            ToolsMenu(navController = navController)
        }

        composable(Screen.PasswordTest.route) {
            PasswordTest(navController = navController)
        }

        composable(Screen.PasswordGenerate.route) {
            PasswordGenerate(navController = navController)
        }
    }
}