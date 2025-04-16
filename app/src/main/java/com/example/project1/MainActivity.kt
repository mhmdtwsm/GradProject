package com.example.project1

import AppNavigation
import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.project1.DataStoreManager
import com.example.project1.service.ClipboardService
import com.example.project1.ui.theme.Project1Theme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) startClipboardService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureEdgeToEdge()

        lifecycleScope.launch {
            // Initialize navigation state
            val (startDestination, autoScan) = determineInitialDestination()

            setContent {
                Project1Theme {
                    val rememberedAutoScan = remember { autoScan }
                    AppNavigation(
                        startDestination = startDestination,
                        autoScan = rememberedAutoScan
                    )
                }
            }
        }

        handleNotificationPermission()
    }

    private suspend fun determineInitialDestination(): Pair<String, Boolean> {
        // Check for deep links first
        return when (intent?.action) {
            "OPEN_PASSWORD_TEST" -> Screen.PasswordTest.route to false
            "OPEN_URL_SCAN" -> Screen.URL.route to intent.getBooleanExtra("AUTO_SCAN", false)
            else -> {
                // Default navigation flow
                val isOnboardingCompleted = DataStoreManager
                    .isOnboardingCompleted(applicationContext)
                    .first()

                if (isOnboardingCompleted) Screen.Home.route to false
                else Screen.Onboarding.route to false
            }
        }
    }

    private fun handleNotificationPermission() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }

            else -> startClipboardService()
        }
    }

    private fun startClipboardService() {
        Intent(this, ClipboardService::class.java).also { intent ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }
    }

    private fun configureEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController?.let { controller ->
                controller.hide(android.view.WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                            View.SYSTEM_UI_FLAG_FULLSCREEN
                    )
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) configureEdgeToEdge()
    }
}