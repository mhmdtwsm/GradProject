import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.passwordmanager.screens.*
import com.example.project1.*
import com.example.project1.authentication.login.LoginScreen
import com.example.project1.authentication.passwordreset.ForgotPasswordScreen
import com.example.project1.authentication.passwordreset.OTP.VerifyCodeScreen
import com.example.project1.authentication.passwordreset.ResetPasswordScreen
import com.example.project1.authentication.passwordreset.VerifyEmailScreen
import com.example.project1.authentication.register.RegisterScreen
import com.example.project1.authentication.resetpassword.ChangePasswordScreen
import com.example.project1.chat.ChatScreen
import com.example.project1.home.Community
import com.example.project1.home.HomeScreen
import com.example.project1.onboard.OnboardingScreen
import com.example.project1.settings.*
import com.example.project1.tools.*
import com.example.project1.tools.passwordgenerate.PasswordGenerate
import com.example.project1.tools.passwordtest.PasswordTest
import com.example.project1.tools.tips.SecurityTipsScreen
import com.example.project1.tools.urlanalyzer.UrlAnalyzerScreen
import com.example.project1.settings.terms.TermsScreen
import com.example.project1.settings.help.HelpScreen
import com.example.project1.settings.profile.EditProfileScreen


// Sealed class to define all routes
sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object URL : Screen("url")
    object SMS : Screen("sms")
    object Community : Screen("community")

    // Password Reset Flow
    object ForgotPassword : Screen("forgotPassword")
    object VerifyCode : Screen("verify_code/{fromLogin}")
    object ResetPassword : Screen("resetPassword/{email}")
    object VerifyEmail : Screen("verifyEmail/{fromLogin}")

    // Settings
    object Settings : Screen("settings")
    object Terms : Screen("terms")
    object Help : Screen("help")
    object Profile : Screen("profile")
    object ChangePassword : Screen("changePassword")

    // Tools
    object ToolsMenu : Screen("toolsMenu")
    object PasswordTest : Screen("passwordTest")
    object PasswordGenerate : Screen("passwordGenerate")
    object SecurityTips : Screen("securityTips")
    object Chat : Screen("chat")
    object UrlAnalyzer : Screen("url_analyzer")

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
    ) {
        // Onboarding
        composable(Screen.Onboarding.route) {
            OnboardingScreen {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            }
        }

        // Login
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                },
                viewModel = viewModel(),
                navController = navController
            )
        }

        // Register
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onNavigateToVerifyOTP = {
                    navController.navigate("verify_code/false") {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // Forgot Password
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                navController = navController,
                onResetPasswordClick = {
                    navController.navigate("verifyEmail/false")
                }
            )
        }

        // Verify Email
        composable(
            route = Screen.VerifyEmail.route,
            arguments = listOf(navArgument("fromLogin") { type = NavType.BoolType })
        ) {
            val fromLogin = it.arguments?.getBoolean("fromLogin") ?: false
            VerifyEmailScreen(navController = navController, fromLogin = fromLogin)
        }

        // Verify Code
        composable(
            route = Screen.VerifyCode.route,
            arguments = listOf(navArgument("fromLogin") { type = NavType.BoolType })
        ) {
            val fromLogin = it.arguments?.getBoolean("fromLogin") ?: false
            Log.d("Navigation", "Navigating to VerifyCode with fromLogin: $fromLogin")
            VerifyCodeScreen(navController = navController, fromLogin = fromLogin)
        }

        // Reset Password
        composable(
            route = Screen.ResetPassword.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) {
            val email = it.arguments?.getString("email") ?: ""
            ResetPasswordScreen(navController = navController, onBackClick = {}, email = email)
        }

        // Community
        composable(Screen.Community.route) {
            Community(navController)
        }

        // Home
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        // SMS & URL
        composable(Screen.URL.route) {
            URLScreen(navController = navController, autoScan = autoScan)
        }

        composable(Screen.SMS.route) {
            SMSScreen(navController = navController)
        }

        // Settings
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

        // Tools
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
        composable(Screen.UrlAnalyzer.route) {
            UrlAnalyzerScreen(navController = navController)
        }

        // Chat
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
