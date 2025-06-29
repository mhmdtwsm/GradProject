package com.example.project1.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    scrim = DarkScrim,
    inverseSurface = DarkInverseSurface,
    inverseOnSurface = DarkInverseOnSurface,
    inversePrimary = DarkInversePrimary,
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    scrim = LightScrim,
    inverseSurface = LightInverseSurface,
    inverseOnSurface = LightInverseOnSurface,
    inversePrimary = LightInversePrimary,
)

@Composable
fun Project1Theme(
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val isDarkMode by ThemeManager.isDarkMode(context).collectAsState(initial = false)

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDarkMode) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDarkMode -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkMode
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

val MaterialTheme.customColors: CustomColors
    @Composable
    get() {
        val context = LocalContext.current
        val isDarkMode by ThemeManager.isDarkMode(context).collectAsState(initial = false)

        return if (isDarkMode) {
            CustomColors(
                cardBackground = DarkCardBackground,
                communityCard = DarkCommunityCard,
                secondaryText = DarkSecondaryText,
                hintText = DarkHintText,
                success = DarkSuccess,
                onSuccess = DarkOnSuccess,
                successContainer = DarkSuccessContainer,
                onSuccessContainer = DarkOnSuccessContainer, // Added
                warning = DarkWarning,
                onWarning = DarkOnWarning,
                warningContainer = DarkWarningContainer,
                onWarningContainer = DarkOnWarningContainer, // Added
                danger = DarkDanger,
                onDanger = DarkOnDanger,
                dangerContainer = DarkDangerContainer,
                onDangerContainer = DarkOnDangerContainer,   // Added
                inputBackground = DarkInputBackground,
                onInputBackground = DarkOnInputBackground,
                inputBorder = DarkInputBorder,
                buttonSecondary = DarkButtonSecondary,
                onButtonSecondary = DarkOnButtonSecondary,
                historyCard = DarkHistoryCard,
                onHistoryCard = DarkOnHistoryCard,
                historyHeader = DarkHistoryHeader,
                semiTransparent = DarkSemiTransparent,
            )
        } else {
            CustomColors(
                cardBackground = LightCardBackground,
                communityCard = LightCommunityCard,
                secondaryText = LightSecondaryText,
                hintText = LightHintText,
                success = LightSuccess,
                onSuccess = LightOnSuccess,
                successContainer = LightSuccessContainer,
                onSuccessContainer = LightOnSuccessContainer, // Added
                warning = LightWarning,
                onWarning = LightOnWarning,
                warningContainer = LightWarningContainer,
                onWarningContainer = LightOnWarningContainer, // Added
                danger = LightDanger,
                onDanger = LightOnDanger,
                dangerContainer = LightDangerContainer,
                onDangerContainer = LightOnDangerContainer,   // Added
                inputBackground = LightInputBackground,
                onInputBackground = LightOnInputBackground,
                inputBorder = LightInputBorder,
                buttonSecondary = LightButtonSecondary,
                onButtonSecondary = LightOnButtonSecondary,
                historyCard = LightHistoryCard,
                onHistoryCard = LightOnHistoryCard,
                historyHeader = LightHistoryHeader,
                semiTransparent = LightSemiTransparent,
            )
        }
    }

// --- DATA CLASS CORRECTED ---
data class CustomColors(
    val cardBackground: androidx.compose.ui.graphics.Color,
    val communityCard: androidx.compose.ui.graphics.Color,
    val secondaryText: androidx.compose.ui.graphics.Color,
    val hintText: androidx.compose.ui.graphics.Color,
    val success: androidx.compose.ui.graphics.Color,
    val onSuccess: androidx.compose.ui.graphics.Color,
    val successContainer: androidx.compose.ui.graphics.Color,
    val onSuccessContainer: androidx.compose.ui.graphics.Color, // Added
    val warning: androidx.compose.ui.graphics.Color,
    val onWarning: androidx.compose.ui.graphics.Color,
    val warningContainer: androidx.compose.ui.graphics.Color,
    val onWarningContainer: androidx.compose.ui.graphics.Color, // Added
    val danger: androidx.compose.ui.graphics.Color,
    val onDanger: androidx.compose.ui.graphics.Color,
    val dangerContainer: androidx.compose.ui.graphics.Color,
    val onDangerContainer: androidx.compose.ui.graphics.Color,   // Added
    val inputBackground: androidx.compose.ui.graphics.Color,
    val onInputBackground: androidx.compose.ui.graphics.Color,
    val inputBorder: androidx.compose.ui.graphics.Color,
    val buttonSecondary: androidx.compose.ui.graphics.Color,
    val onButtonSecondary: androidx.compose.ui.graphics.Color,
    val historyCard: androidx.compose.ui.graphics.Color,
    val onHistoryCard: androidx.compose.ui.graphics.Color,
    val historyHeader: androidx.compose.ui.graphics.Color,
    val semiTransparent: androidx.compose.ui.graphics.Color,
)