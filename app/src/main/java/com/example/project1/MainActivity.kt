package com.example.project1

import AppNavigation
import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import com.example.project1.service.ClipboardService
import com.example.project1.ui.theme.Project1Theme

class MainActivity : ComponentActivity() {
    private var shouldNavigateToPasswordTest = false

    // Permission launcher for notification permission
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startClipboardService()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemNavigationBar()

        // Check if we should navigate to password test
        shouldNavigateToPasswordTest = intent?.action == "OPEN_PASSWORD_TEST"

        // Request notification permission for Android 13+ at app start
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            startClipboardService()
        }

        setContent {
            Project1Theme {
                AppNavigation(
                    startDestination = if (shouldNavigateToPasswordTest) "passwordTest" else "home"
                )
            }
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

    private fun hideSystemNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Modern approach for Android 11+
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.decorView.windowInsetsController?.let { controller ->
                controller.hide(android.view.WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // Legacy approach for older Android versions
            window.decorView.systemUiVisibility = (
                    SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                            View.SYSTEM_UI_FLAG_FULLSCREEN
                    )
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // Re-hide the navigation bar when the window gains focus
            hideSystemNavigationBar()
        }
    }
}