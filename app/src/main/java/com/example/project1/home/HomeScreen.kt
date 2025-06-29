package com.example.project1.home

import Screen
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.project1.DataStoreManager
import com.example.project1.R
import com.example.project1.statistics.StatisticsManager
import com.example.project1.statistics.UserStatistics
import com.example.project1.ui.theme.Project1Theme
import com.example.project1.ui.theme.customColors

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    var userName: String? by remember { mutableStateOf(null) }

    // Fetch username from DataStore
    LaunchedEffect(Unit) {
        DataStoreManager.getUsername(context).collect { savedUsername ->
            userName = savedUsername
        }
    }

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
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            // Welcome Text
            Text(
                text = if (!userName.isNullOrEmpty()) "Hi, $userName 👋" else "Welcome!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Subtitle Text
            Text(
                text = "Welcome to PhishAware! Stay alert, stay safe.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.customColors.secondaryText
            )

            // Tip Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(
                    text = "Always verify links and messages before sharing your info.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Main Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // First Row of Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    HomeButtonsWithChart(
                        title = "URL Stats",
                        isUrlChart = true,
                        onClick = { navigateTo(navController, Screen.URL.route) }
                    )
                    HomeButtonsWithChart(
                        title = "SMS Stats",
                        isUrlChart = false,
                        onClick = { navigateTo(navController, Screen.SMS.route) }
                    )
                }

                // Second Row of Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    HomeButtons(
                        title = "Chat with AI",
                        icon = R.drawable.ic_chat,
                        onClick = { navigateTo(navController, Screen.Chat.route) }
                    )
                    HomeButtons(
                        title = "Learn & Protect",
                        icon = R.drawable.ic_education,
                        onClick = { navigateTo(navController, Screen.SecurityTips.route) }
                    )
                }
            }

            // Join Community Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clickable { navController.navigate(Screen.Community.route) },
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.customColors.communityCard,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🤝 Join the Community",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun HomeButtons(title: String, icon: Int, iconSize: Int = 60, onClick: () -> Unit) {
    Card(
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.customColors.cardBackground),
        modifier = Modifier
            .size(width = 150.dp, height = 180.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(iconSize.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun HomeButtonsWithChart(title: String, isUrlChart: Boolean, onClick: () -> Unit) {
    val context = LocalContext.current
    val statisticsManager = StatisticsManager.getInstance(context)
    val statistics by statisticsManager.statistics.collectAsState(initial = null)

    Card(
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.customColors.cardBackground),
        modifier = Modifier
            .size(width = 150.dp, height = 180.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                PieChart(statistics = statistics, isUrlChart = isUrlChart)

                val (safeCount, total) = if (isUrlChart) {
                    (statistics?.safeUrls ?: 0) to (statistics?.totalUrls ?: 0)
                } else {
                    (statistics?.safeSms ?: 0) to (statistics?.totalSms ?: 0)
                }
                val ratioText = if (total > 0) "$safeCount/$total" else "N/A"

                Text(
                    text = ratioText,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun PieChart(statistics: UserStatistics?, isUrlChart: Boolean) {
    val safeColor = MaterialTheme.customColors.success
    val unsafeColor = MaterialTheme.customColors.danger
    val emptyColor = MaterialTheme.colorScheme.surfaceVariant
    val donutHoleColor = MaterialTheme.customColors.cardBackground

    val (safe, total) = if (isUrlChart) {
        (statistics?.safeUrls ?: 0) to (statistics?.totalUrls ?: 0)
    } else {
        (statistics?.safeSms ?: 0) to (statistics?.totalSms ?: 0)
    }

    Canvas(modifier = Modifier.size(100.dp)) {
        if (total == 0) {
            drawCircle(color = emptyColor, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 20f))
        } else {
            val safeAngle = 360f * safe / total
            val unsafeAngle = 360f - safeAngle

            drawArc(
                color = unsafeColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 20f)
            )
            drawArc(
                color = safeColor,
                startAngle = -90f,
                sweepAngle = safeAngle,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 20f)
            )
        }
    }
}

private fun navigateTo(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(Screen.Home.route) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun PreviewHomeScreenLight() {
    Project1Theme {
        HomeScreen(navController = rememberNavController())
    }
}