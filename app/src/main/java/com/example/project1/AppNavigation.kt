import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.passwordmanager.screens.AccountDetailsScreen
import com.example.passwordmanager.screens.AddAccountScreen
import com.example.passwordmanager.screens.DashboardScreen
import com.example.passwordmanager.screens.VaultScreen
import com.example.project1.SMSScreen
import com.example.project1.URLScreen
import com.example.project1.authentication.login.LoginScreen
import com.example.project1.authentication.passwordreset.ForgotPasswordScreen
import com.example.project1.authentication.passwordreset.OTP.VerifyCodeScreen
import com.example.project1.authentication.passwordreset.ResetPasswordScreen
import com.example.project1.authentication.passwordreset.VerifyEmailScreen
import com.example.project1.authentication.register.RegisterScreen
import com.example.project1.authentication.resetpassword.ChangePasswordScreen
import com.example.project1.chat.ChatScreen
import com.example.project1.home.HomeScreen
import com.example.project1.onboard.OnboardingScreen
import com.example.project1.settings.SettingsScreen
import com.example.project1.settings.help.HelpScreen
import com.example.project1.settings.profile.EditProfileScreen
import com.example.project1.settings.terms.TermsScreen
import com.example.project1.tools.ToolsMenu
import com.example.project1.tools.passwordgenerate.PasswordGenerate
import com.example.project1.tools.passwordtest.PasswordTest
import com.example.project1.tools.tips.SecurityTipsScreen


sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object URL : Screen("url")
    object SMS : Screen("sms")

    // Password Reset Flow
    object ForgotPassword : Screen("forgotPassword")

    // Define VerifyCode with proper route pattern for query parameters
    object VerifyCode : Screen("verify_code")

    object ResetPassword : Screen("resetPassword")
    object VerifyEmail : Screen("verifyEmail")

    // Settings Navigations
    object Settings : Screen("settings")
    object Terms : Screen("terms")
    object Help : Screen("help")
    object Profile : Screen("profile")
    object ChangePassword : Screen("changePassword")

    // Tools Menu Navigations
    object ToolsMenu : Screen("toolsMenu")
    object PasswordTest : Screen("passwordTest")
    object PasswordGenerate : Screen("passwordGenerate")
    object SecurityTips : Screen("securityTips")
    object Chat : Screen("chat")

    // Password Manager
    object Dashboard : Screen("dashboard")
    object Vault : Screen("vault/{vaultId}")
    object AddAccount : Screen("add_account/{vaultId}")
    object AccountDetails : Screen("account_details/{vaultId}/{accountId}")

}

@Composable
fun AppNavigation(
    startDestination: String = Screen.Onboarding.route,
    autoScan: Boolean = false
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        // Onboarding Screen
        composable(Screen.Onboarding.route) {
            OnboardingScreen {
                // Navigate to Login screen when onboarding is finished
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            }
        }

//        composable(
//            route = "home/{message}",
//            arguments = listOf(
//                navArgument("message") {
//                    type = NavType.StringType
//                    defaultValue = ""
//                    nullable = true
//                }
//            )
//        ) { backStackEntry ->
//            val message = backStackEntry.arguments?.getString("message") ?: ""
//            HomeScreen(navController = navController, message = message)
//        }


        // IMPORTANT: Only define the VerifyCode route once with proper arguments
        composable(
            route = "verify_code/{fromLogin}",
            arguments = listOf(
                navArgument("fromLogin") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            // Extract parameters from backStackEntry
            val fromLogin = backStackEntry.arguments?.getBoolean("fromLogin") ?: false

            // Log the extracted values
            Log.d(
                "Navigation",
                "Navigating to VerifyCode with fromLogin: $fromLogin"
            )

            // Pass the extracted parameters to the VerifyCodeScreen
            VerifyCodeScreen(
                navController = navController,
                fromLogin = fromLogin,
            )
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
                },
                onNavigateToForgotPassword = {
                    // Add this to your LoginScreen
                    navController.navigate(Screen.ForgotPassword.route)
                },
                viewModel = viewModel(),
                navController = navController
            )
        }

        // Register Screen
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    // Navigate back to Login screen
                    navController.popBackStack()
                },
                onNavigateToVerifyOTP = {
                    // Navigate to Home screen
                    navController.navigate(Screen.VerifyEmail.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // Password Reset Flow
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                navController = navController,
                onResetPasswordClick = {
                    navController.navigate(Screen.VerifyEmail.route)
                })
        }

        composable(Screen.VerifyEmail.route) {
            VerifyEmailScreen(
                navController = navController, fromLogin = false,
            )
        }

        composable(
            route = "resetPassword/{email}",
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ResetPasswordScreen(
                navController = navController,
                onBackClick = { },
                email = email,
            )
        }

        // Pass The Info that user Coming from Login to Reset Password Screen
        composable(
            route = "verifyEmail/{fromLogin}",
            arguments = listOf(
                navArgument("fromLogin") {
                    type = NavType.BoolType
                    defaultValue = true
                }
            )
        ) {
            val fromLogin = it.arguments?.getBoolean("fromLogin") ?: false
            VerifyEmailScreen(
                navController = navController, fromLogin = fromLogin,
            )
        }

        // Home Screen
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        // Rest of your existing routes...
        composable(Screen.URL.route) {
            URLScreen(
                navController = navController,
                autoScan = autoScan
            )
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
        composable(Screen.Profile.route) {
            EditProfileScreen(
                onNavigateToPasswordChange = {
                    navController.navigate(Screen.ChangePassword.route)
                },
                navController = navController,
                viewModel = viewModel()
            )
        }

        composable(Screen.ChangePassword.route) {
            ChangePasswordScreen(navController = navController)
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

        composable(Screen.SecurityTips.route) {
            SecurityTipsScreen(navController = navController)
        }

        composable(Screen.Chat.route) {
            ChatScreen(navController = navController)
        }

        // Password Manager

        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }
        composable(Screen.Vault.route) { backStackEntry ->
            val vaultId = backStackEntry.arguments?.getString("vaultId") ?: ""
            VaultScreen(navController = navController, vaultId = vaultId)
        }
        composable(Screen.AddAccount.route) { backStackEntry ->
            val vaultId = backStackEntry.arguments?.getString("vaultId") ?: ""
            AddAccountScreen(navController = navController, vaultId = vaultId)
        }
        composable(Screen.AccountDetails.route) { backStackEntry ->
            val vaultId = backStackEntry.arguments?.getString("vaultId") ?: ""
            val accountId = backStackEntry.arguments?.getString("accountId") ?: ""
            AccountDetailsScreen(
                navController = navController,
                vaultId = vaultId,
                accountId = accountId
            )
        }

    }
}
