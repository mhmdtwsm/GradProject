package com.example.project1

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.project1.home.BottomNavigationBar

@Composable
fun SMSScreen(navController: NavController) {
    Text(text = "SMS Screen")
    BottomNavigationBar(navController = navController, selectedScreen = Screen.SMS.route)

}