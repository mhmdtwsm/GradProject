package com.example.project1.tools.passwordtest

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.project1.R
import com.example.project1.ui.theme.Project1Theme
import com.example.project1.ui.theme.customColors
import java.util.regex.Pattern

// --- Logic Layer (No UI dependencies) ---

enum class StrengthLevel {
    NEUTRAL, WEAK, MODERATE, STRONG, VERY_STRONG
}

data class StrengthResult(
    val message: String,
    val level: StrengthLevel
)

fun checkPasswordStrength(input: String): StrengthResult {
    if (input.isEmpty()) {
        return StrengthResult("Use 8+ characters.\nAdd Aa, 123, @#!.\nAvoid names/dates.", StrengthLevel.NEUTRAL)
    }

    var score = 0
    // Length Check
    when {
        input.length < 8 -> score += 0
        input.length in 8..11 -> score += 10
        input.length >= 12 -> score += 20
    }

    // Character Variety Check
    val hasLower = input.any { it.isLowerCase() }
    val hasUpper = input.any { it.isUpperCase() }
    val hasDigit = input.any { it.isDigit() }
    val hasSymbol = input.any { !it.isLetterOrDigit() }
    if (listOf(hasLower, hasUpper, hasDigit, hasSymbol).count { it } >= 3) {
        score += 20
    }

    // Patterns and Common Words Check
    if (Pattern.compile("(.)\\1{2,}").matcher(input).find()) score -= 10
    val commonPatterns = listOf("123", "abc", "qwerty", "password", "admin")
    if (commonPatterns.any { input.contains(it, ignoreCase = true) }) score -= 15

    // Determine Strength
    return when {
        score < 15 -> StrengthResult("Weak\nMore variety & length needed.", StrengthLevel.WEAK)
        score < 30 -> StrengthResult("Moderate\nGood, but can be stronger.", StrengthLevel.MODERATE)
        score < 40 -> StrengthResult("Strong\nThis is a solid password.", StrengthLevel.STRONG)
        else -> StrengthResult("Very Strong!\nExcellent protection.", StrengthLevel.VERY_STRONG)
    }
}

// --- UI Layer ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordTestScreen(navController: NavController) {
    var password by remember { mutableStateOf("") }
    var strengthResult by remember { mutableStateOf(checkPasswordStrength("")) }
    val context = LocalContext.current

    val strengthColors = when (strengthResult.level) {
        StrengthLevel.NEUTRAL -> CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
        StrengthLevel.WEAK -> CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
        StrengthLevel.MODERATE -> CardDefaults.cardColors(
            containerColor = MaterialTheme.customColors.warningContainer,
            contentColor = MaterialTheme.customColors.onWarningContainer
        )
        StrengthLevel.STRONG -> CardDefaults.cardColors(
            containerColor = MaterialTheme.customColors.successContainer,
            contentColor = MaterialTheme.customColors.onSuccessContainer
        )
        StrengthLevel.VERY_STRONG -> CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Password Strength Test") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Strength Indicator Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 120.dp),
                shape = MaterialTheme.shapes.large,
                colors = strengthColors
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = strengthResult.message,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Password Input Field
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    strengthResult = checkPasswordStrength(it)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Enter password to test") },
                placeholder = { Text("P@ssw0rd!123") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.customColors.inputBackground,
                    unfocusedContainerColor = MaterialTheme.customColors.inputBackground,
                    focusedTextColor = MaterialTheme.customColors.onInputBackground,
                    unfocusedTextColor = MaterialTheme.customColors.onInputBackground,
                    cursorColor = MaterialTheme.colorScheme.primary,
                ),
                trailingIcon = {
                    IconButton(onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.primaryClip?.getItemAt(0)?.text?.toString()?.let {
                            password = it
                            strengthResult = checkPasswordStrength(it)
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.clipboard),
                            contentDescription = "Paste",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordTestPreview() {
    Project1Theme {
        PasswordTestScreen(navController = rememberNavController())
    }
}