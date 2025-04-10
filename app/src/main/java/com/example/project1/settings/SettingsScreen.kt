package com.example.project1.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.project1.R
import com.example.project1.home.BottomNavigationBar

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
                .background(Color(0xFF1C2431))
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
            SectionTitle("Account & Privacy")

            SettingsItem(
                icon = Icons.Default.Person,
                title = "Edit profile",
                onClick = { /** TODO Handle edit profile action **/ }
            )
            SettingsItem(
                icon = Icons.Default.Info,
                title = "Security & Privacy",
                onClick = { /** TODO Handle security & privacy action **/ }
            )
            SettingsItem(
                icon = Icons.Default.Notifications,
                title = "Notifications & Overlay",
                onClick = { /** TODO Handle notifications & overlay action **/ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Theme Section
            SectionTitle("Theme")

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
            SectionTitle("Language")

            LanguageSelector()

            Spacer(modifier = Modifier.height(32.dp))

            // Sign Out Button
            SignOutButton({ /** TODO Handle sign out action **/ })

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
fun SignOutButton(onSignOut: () -> Unit) {
    val darkNavy = Color(0xFF1C2431)

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Button(
            onClick = onSignOut,
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

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    SettingsScreen(navController = NavController(LocalContext.current))
}