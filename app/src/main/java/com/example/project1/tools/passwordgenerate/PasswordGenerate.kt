package com.example.project1.tools.passwordgenerate

import Screen
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.project1.R
import com.example.project1.ui.theme.Project1Theme
import com.example.project1.ui.theme.customColors
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordGenerateScreen(navController: NavController) {
    var password by remember { mutableStateOf("P@ssw0rd!123") }
    var passwordLength by remember { mutableStateOf(12f) }
    var upperCase by remember { mutableStateOf(true) }
    var lowerCase by remember { mutableStateOf(true) }
    var numbers by remember { mutableStateOf(true) }
    var symbols by remember { mutableStateOf(true) }

    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Password Generator") },
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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Password Display
            OutlinedTextField(
                value = password,
                onValueChange = { },
                readOnly = true,
                label = { Text("Generated Password") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.customColors.inputBackground,
                    unfocusedContainerColor = MaterialTheme.customColors.inputBackground,
                    disabledContainerColor = MaterialTheme.customColors.inputBackground,
                    focusedTextColor = MaterialTheme.customColors.onInputBackground,
                    unfocusedTextColor = MaterialTheme.customColors.onInputBackground,
                    disabledTextColor = MaterialTheme.customColors.onInputBackground,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                ActionButton(icon = R.drawable.ic_copy, description = "Copy") {
                    clipboardManager.setText(AnnotatedString(password))
                }
                ActionButton(icon = R.drawable.ic_delete, description = "Clear") {
                    password = ""
                }
                ActionButton(icon = R.drawable.ic_refresh, description = "Refresh") {
                    password = generatePassword(
                        length = passwordLength.toInt(),
                        useUpperCase = upperCase,
                        useLowerCase = lowerCase,
                        useNumbers = numbers,
                        useSymbols = symbols
                    )
                }
            }

            // Password Length Slider
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Length: ${passwordLength.toInt()}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Slider(
                    value = passwordLength,
                    onValueChange = { passwordLength = it },
                    valueRange = 4f..32f,
                    steps = 27, // (32 - 4) - 1
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }

            // Password Options
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.customColors.cardBackground,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PasswordOption(
                        text = "Upper case (A-Z)",
                        isChecked = upperCase,
                        onCheckedChange = { upperCase = it }
                    )
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    PasswordOption(
                        text = "Lower case (a-z)",
                        isChecked = lowerCase,
                        onCheckedChange = { lowerCase = it }
                    )
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    PasswordOption(
                        text = "Numbers (0-9)",
                        isChecked = numbers,
                        onCheckedChange = { numbers = it }
                    )
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    PasswordOption(
                        text = "Symbols (@#&$)",
                        isChecked = symbols,
                        onCheckedChange = { symbols = it }
                    )
                }
            }
        }
    }
}

@Composable
fun ActionButton(icon: Int, description: String, onClick: () -> Unit) {
    FilledTonalIconButton(
        onClick = onClick,
        modifier = Modifier.size(50.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = description,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun PasswordOption(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

private fun generatePassword(
    length: Int,
    useUpperCase: Boolean,
    useLowerCase: Boolean,
    useNumbers: Boolean,
    useSymbols: Boolean
): String {
    val upperCaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val lowerCaseChars = "abcdefghijklmnopqrstuvwxyz"
    val numberChars = "0123456789"
    val symbolChars = "@#$%&*"

    var allowedChars = ""
    if (useUpperCase) allowedChars += upperCaseChars
    if (useLowerCase) allowedChars += lowerCaseChars
    if (useNumbers) allowedChars += numberChars
    if (useSymbols) allowedChars += symbolChars

    if (allowedChars.isEmpty()) return "Please select an option"

    return (1..length)
        .map { allowedChars[Random.nextInt(0, allowedChars.length)] }
        .joinToString("")
}

@Preview(showBackground = true)
@Composable
fun PasswordGeneratePreview() {
    Project1Theme {
        PasswordGenerateScreen(navController = rememberNavController())
    }
}