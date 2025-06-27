package com.example.project1.home

import Screen
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.preference.PreferenceManager
import com.example.project1.DataStoreManager
import com.example.project1.R
import com.example.project1.statistics.StatisticsManager

@Composable
fun HomeScreen(navController: NavController, message: String? = null) {

    val context = LocalContext.current
    var userName: String? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        DataStoreManager.getUsername(context).collect { savedUsername ->
            userName = savedUsername
        }
    }

    val emailauth =
        PreferenceManager.getDefaultSharedPreferences(context).getString("VERIFY_EMAIL", null)
    val authtoken =
        PreferenceManager.getDefaultSharedPreferences(context).getString("AUTH_TOKEN", null)

    val statisticsManager = StatisticsManager.getInstance(context)
    val statsString = statisticsManager.getStatisticsAsString()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                selectedScreen = Screen.Home.route
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(android.graphics.Color.parseColor("#101F31")))
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = if (userName != null) "Hi, $userName 👋" else "Welcome!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Welcome to PhishAware! Stay alert, stay safe.",
                fontSize = 16.sp,
                color = Color(0xFFB0BEC5)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF263238), RoundedCornerShape(12.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                    .padding(16.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Always verify links before sharing your info.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                // الصف الأول
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    HomeButtonsWithChart(
                        title = "Probability URL",
                        icon = R.drawable.ic_link,
                        isUrlChart = true
                    ) {
                        navController.navigate(Screen.URL.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }

                    HomeButtonsWithChart(
                        title = "Probability SMS",
                        icon = R.drawable.ic_message,
                        isUrlChart = false
                    ) {
                        navController.navigate(Screen.SMS.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }

                // الصف الثاني
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    HomeButtons("Chat\nwith AI", R.drawable.ic_chat) {
                        navController.navigate(Screen.Chat.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }

                    HomeButtons("Learn &\nProtect", R.drawable.ic_education) {
                        navController.navigate(Screen.SecurityTips.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }

                // 🔹 كارت الانضمام إلى الكوميونتي 🔹
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clickable {
                            navController.navigate(Screen.Community.route)
                        },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E3A5F)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "💬 Join the Community",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen(navController = rememberNavController())
}
