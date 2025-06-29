package com.example.project1.settings

import Screen
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.preference.PreferenceManager
import com.example.project1.DataStoreManager.USERNAME_KEY
import com.example.project1.SMS.SMSData.SMSRepository
import com.example.project1.URL.URLData.URLRepository
import com.example.project1.dataStore
import com.example.project1.home.BottomNavigationBar
import com.example.project1.statistics.StatisticsManager
import com.example.project1.ui.theme.Project1Theme
import com.example.project1.ui.theme.ThemeManager
import com.example.project1.ui.theme.customColors
import com.example.project1.viewmodel.SMSViewModel
import com.example.project1.viewmodel.URLViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val isDarkMode by ThemeManager.isDarkMode(context).collectAsState(initial = false)

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                selectedScreen = Screen.Settings.route
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Account Section
            SettingsGroup(title = "Account") {
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "Edit Profile",
                    onClick = { navController.navigate(Screen.Profile.route) }
                )
            }

            // Theme Section
            SettingsGroup(title = "Theme") {
                ThemeSwitch(
                    isDarkMode = isDarkMode,
                    onThemeChange = { newDarkMode ->
                        coroutineScope.launch {
                            ThemeManager.setDarkMode(context, newDarkMode)
                        }
                    }
                )
            }

            // Support & About Section
            SettingsGroup(title = "Support & About") {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Help & Support",
                    onClick = { navController.navigate(Screen.Help.route) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Terms and Policies",
                    onClick = { navController.navigate(Screen.Terms.route) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign Out Button
            SignOutButton(
                context = context,
                navController = navController
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.customColors.cardBackground,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SignOutButton(context: Context, navController: NavController) {
    OutlinedButton(
        onClick = { signOutUser(context = context, navController = navController) },
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(50.dp),
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Sign Out",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Sign Out",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ThemeSwitch(
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Dark Mode",
            style = MaterialTheme.typography.bodyLarge,
        )
        Switch(
            checked = isDarkMode,
            onCheckedChange = onThemeChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

fun signOutUser(context: Context, navController: NavController) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    CoroutineScope(Dispatchers.IO).launch {
        // Clear SharedPreferences
        prefs.edit()
            .remove("AUTH_TOKEN")
            .remove("VERIFY_EMAIL")
            .remove("PROFILE_PICTURE_URI")
            .remove("PROFILE_PICTURE_TIMESTAMP")
            .apply()

        // Clear DataStore
        context.dataStore.edit { preferences ->
            preferences.remove(USERNAME_KEY)
        }

        // Delete local files
        val filesToDelete = listOf(
            "profile_picture.jpg",
            StatisticsManager.STATISTICS_JSON_FILE,
            URLViewModel.HISTORY_JSON_FILE,
            SMSViewModel.HISTORY_JSON_FILE
        )
        filesToDelete.forEach { fileName ->
            val file = File(context.filesDir, fileName)
            if (file.exists()) {
                file.delete()
            }
        }

        // Clear local databases and managers
        StatisticsManager.getInstance(context).clearStatistics()
        URLRepository(context).clearAll()
        SMSRepository(context).clearAll()

        // Navigate to Login screen
        withContext(Dispatchers.Main) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    Project1Theme {
        SettingsScreen(navController = rememberNavController())
    }
}