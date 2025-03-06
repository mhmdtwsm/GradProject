package com.example.project1

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.project1.home.BottomNavigationBar

@Composable
fun URLScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                selectedScreen = Screen.URL.route
            )
        }
    ) { innerPadding ->

        Text(text = "URL Screen")
    }
}