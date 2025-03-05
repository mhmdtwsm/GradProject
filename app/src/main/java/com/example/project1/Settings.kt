package com.example.project1

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontVariation
import androidx.navigation.NavController
import com.example.project1.home.BottomNavigationBar

@Composable
fun SettingsScreen(navController: NavController) {
    Text(text = "URL Screen")
    BottomNavigationBar(navController = navController, selectedScreen = Screen.Settings.route)

}