package com.example.project1.settings

import Screen
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavController
import androidx.preference.PreferenceManager
import com.example.project1.DataStoreManager.USERNAME_KEY
import com.example.project1.SMS.SMSData.SMSRepository
import com.example.project1.URL.URLData.URLRepository
import com.example.project1.dataStore
import com.example.project1.home.BottomNavigationBar
import com.example.project1.statistics.StatisticsManager
import com.example.project1.viewmodel.SMSViewModel
import com.example.project1.viewmodel.URLViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun SettingsScreen(navController: NavController) {
    val darkModeEnabled = remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()
    val darkNavy = Color(0xFF1C2431)

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                selectedScreen = Screen.Settings.route
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(android.graphics.Color.parseColor("#101F31")))
                .verticalScroll(scrollState)
                .padding(innerPadding) // Avoid overlapping with bottom bar
                .padding(16.dp)
        ) {
            Text(
                text = "Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 16.dp)
            )

            androidx.compose.material3.Divider(color = Color.Gray.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(24.dp))

            // Account & Privacy Section
            SectionTitle("Account")

            SettingsItem(
                icon = Icons.Default.Person,
                title = "Edit profile",
                onClick = { navController.navigate(Screen.Profile.route) }
            )

//            SettingsItem(
//                icon = Icons.Default.Warning,
//                title = "Permissions",
//                onClick = { /** TODO Handle notifications & overlay action **/ }
//            )

            Spacer(modifier = Modifier.height(24.dp))

//             Theme Section
            SectionTitle("Theme")
            ThemeSwitch(darkModeEnabled = darkModeEnabled)


            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = Color.White.copy(alpha = 0.2f)
            )

            // Support & About Section
            SectionTitle("Support & About")

            SettingsItem(
                icon = Icons.Default.Info,
                title = "Help & Support",
                onClick = {
                    navController.navigate(Screen.Help.route)
                }
            )
            SettingsItem(
                icon = Icons.Default.Info,
                title = "Terms and Policies",
                onClick = {
                    navController.navigate(Screen.Terms.route)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Language Section
//            SectionTitle("Language")
//            LanguageSelector()

            Spacer(modifier = Modifier.height(32.dp))

            // Sign Out Button
            SignOutButton(context = LocalContext.current, navController = navController)

            Spacer(modifier = Modifier.height(32.dp)) // Extra space to prevent UI cutoff
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        color = Color.White,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun LanguageSelector() {
    val darkNavy = Color(0xFF1C2431)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(darkNavy)
            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .clickable { /* Open language selector */ }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "English", fontSize = 16.sp, color = Color.White)
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Select language",
                tint = Color.White
            )
        }
    }
}

@Composable
fun SignOutButton(context: Context, navController: NavController) {
    val darkNavy = Color(0xFF1C2431)

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Button(
            onClick = {
                signOutUser(context = context, navController = navController)
            },
            modifier = Modifier
                .width(200.dp)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = darkNavy,
                contentColor = Color.White
            ),
            border = BorderStroke(1.dp, Color.White)
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
                Text(text = "Sign Out", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun ThemeSwitch(darkModeEnabled: MutableState<Boolean>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Dark Mode", fontSize = 16.sp, color = Color.White)
        Switch(
            checked = darkModeEnabled.value,
            onCheckedChange = { darkModeEnabled.value = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF3D5AFE),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.Gray
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
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            color = Color.White
        )
    }
}

fun signOutUser(context: Context, navController: NavController) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    // 1. Clear SharedPreferences
    prefs.edit()
        .remove("AUTH_TOKEN")
        .remove("VERIFY_EMAIL")
        .remove("PROFILE_PICTURE_URI")
        .remove("PROFILE_PICTURE_TIMESTAMP")
        .apply()

    // 2. Clear DataStore & onboarding status
    CoroutineScope(Dispatchers.IO).launch {
        context.dataStore.edit { preferences ->
            preferences.remove(USERNAME_KEY)
        }

        // 3. Delete profile picture file
        val fileName = "profile_picture.jpg"
        val imageFile = File(context.filesDir, fileName)
        if (imageFile.exists()) {
            imageFile.delete()
        }

        // 4. Delete statistics.json file
        val statisticsFile = File(context.filesDir, StatisticsManager.STATISTICS_JSON_FILE)
        if (statisticsFile.exists()) {
            statisticsFile.delete()
            android.util.Log.d("SettingsScreen", "Deleted statistics.json file")
        }

        // 5. Delete url_history.json file
        val urlHistoryFile = File(context.filesDir, URLViewModel.HISTORY_JSON_FILE)
        if (urlHistoryFile.exists()) {
            urlHistoryFile.delete()
            android.util.Log.d("SettingsScreen", "Deleted url_history.json file")
        }

        // 6. Delete sms_history.json file
        val smsHistoryFile = File(context.filesDir, SMSViewModel.HISTORY_JSON_FILE)
        StatisticsManager.getInstance(context).clearStatistics()
        if (smsHistoryFile.exists()) {
            smsHistoryFile.delete()
            android.util.Log.d("SettingsScreen", "Deleted sms_history.json file")
        }

        // 7. Clear local databases
        URLRepository(context).clearAll()
        SMSRepository(context).clearAll()

        withContext(Dispatchers.Main) {
            navController.navigate(Screen.Login.route) {
                // Optional: Clear back stack so user can't go back
                popUpTo(0) {
                    inclusive = true  // This will remove the start destination too
                }

                // Prevent multiple login screens if back button is pressed
                launchSingleTop = true
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    SettingsScreen(navController = NavController(LocalContext.current))
}