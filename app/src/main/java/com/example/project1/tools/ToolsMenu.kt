package com.example.project1.tools

import Screen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.project1.R
import com.example.project1.home.BottomNavigationBar

@Composable
fun ToolsMenu(modifier: Modifier = Modifier, navController: NavController) {
    androidx.compose.material3.Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                selectedScreen = Screen.ToolsMenu.route
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(android.graphics.Color.parseColor("#101F31")))
                .padding(innerPadding)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            androidx.compose.material3.Text(
                text = "Tools",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            androidx.compose.material3.Divider(color = Color.White.copy(alpha = 1f))

            Spacer(modifier = Modifier.height(40.dp))

            // Cards
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Cards(
                    iconId = R.drawable.ic_link,
                    title = "URL Analyzer",
                    onClick = { navController.navigate(Screen.UrlAnalyzer.route) }
                )

                Cards(
                    iconId = R.drawable.passwordgenerate,
                    title = "Password Generate",
                    onClick = { navController.navigate(Screen.PasswordGenerate.route) }
                )

                Cards(
                    iconId = R.drawable.passwordcheck,
                    title = "Password Test",
                    onClick = { navController.navigate(Screen.PasswordTest.route) }
                )

                Cards(
                    iconId = R.drawable.ic_education,
                    title = "Security Tips",
                    onClick = { navController.navigate(Screen.SecurityTips.route) }
                )

                Cards(
                    iconId = R.drawable.ic_chat,
                    title = "Chat with AI",
                    onClick = { navController.navigate(Screen.Chat.route) }
                )
            }
        }
    }
}

@Preview
@Composable
fun ToolsMenuPreview() {
    ToolsMenu(navController = NavController(LocalContext.current))
}
