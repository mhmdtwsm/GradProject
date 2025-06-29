package com.example.project1.home

import Screen // Make sure to import your Screen sealed class
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.project1.R
import com.example.project1.ui.theme.Project1Theme

@Composable
fun BottomNavigationBar(navController: NavController, selectedScreen: String) {
    val drawlineColor = MaterialTheme.colorScheme.outline
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .zIndex(1f)
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                drawLine(
                    color = drawlineColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = strokeWidth
                )
            },
    ) {
        fun navigateIfNotCurrent(route: String) {
            if (selectedScreen != route) {
                navController.navigate(route) {
                    launchSingleTop = true
                    // This logic restores the back stack state, which is good practice
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    restoreState = true
                }
            }
        }

        // Common colors for all navigation items
        val itemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedTextColor = MaterialTheme.colorScheme.onSurface,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            indicatorColor = MaterialTheme.colorScheme.secondaryContainer
        )

        // Home Item
        NavigationBarItem(
            selected = selectedScreen == Screen.Home.route,
            onClick = { navigateIfNotCurrent(Screen.Home.route) },
            icon = { Icon(painterResource(id = R.drawable.ic_home), "Home", Modifier.size(28.dp)) },
            label = { Text("Home", style = MaterialTheme.typography.labelSmall) },
            colors = itemColors
        )

        // URL Item
        NavigationBarItem(
            selected = selectedScreen == Screen.URL.route,
            onClick = { navigateIfNotCurrent(Screen.URL.route) },
            icon = { Icon(painterResource(id = R.drawable.ic_link), "URL", Modifier.size(28.dp)) },
            label = { Text("URL", style = MaterialTheme.typography.labelSmall) },
            colors = itemColors
        )

        // SMS Item
        NavigationBarItem(
            selected = selectedScreen == Screen.SMS.route,
            onClick = { navigateIfNotCurrent(Screen.SMS.route) },
            icon = { Icon(painterResource(id = R.drawable.ic_message), "SMS", Modifier.size(28.dp)) },
            label = { Text("SMS", style = MaterialTheme.typography.labelSmall) },
            colors = itemColors
        )

        // Tools Item - Using the CORRECT route
        NavigationBarItem(
            selected = selectedScreen == Screen.ToolsMenu.route,
            onClick = { navigateIfNotCurrent(Screen.ToolsMenu.route) }, // CORRECTED
            icon = { Icon(painterResource(id = R.drawable.ic_tools), "Tools", Modifier.size(28.dp)) },
            label = { Text("Tools", style = MaterialTheme.typography.labelSmall) },
            colors = itemColors
        )

        // Settings Item
        NavigationBarItem(
            selected = selectedScreen == Screen.Settings.route,
            onClick = { navigateIfNotCurrent(Screen.Settings.route) },
            icon = { Icon(painterResource(id = R.drawable.ic_settings), "Settings", Modifier.size(28.dp)) },
            label = { Text("Settings", style = MaterialTheme.typography.labelSmall) },
            colors = itemColors
        )
    }
}


@Preview(name = "BottomNav Dark")
@Composable
fun BottomNavigationBarDarkPreview() {
    Project1Theme() {
        // We pass the route string from our sealed class for the preview
        BottomNavigationBar(
            navController = rememberNavController(),
            selectedScreen = Screen.Home.route
        )
    }
}

@Preview(name = "BottomNav Light")
@Composable
fun BottomNavigationBarLightPreview() {
    Project1Theme() {
        // We pass the route string from our sealed class for the preview
        BottomNavigationBar(
            navController = rememberNavController(),
            selectedScreen = Screen.Home.route
        )
    }
}