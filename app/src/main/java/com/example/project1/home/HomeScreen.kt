package com.example.project1.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.project1.DataStoreManager
import com.example.project1.R
import kotlinx.coroutines.flow.first

@Composable
fun HomeScreen(navController: NavController, message: String? = null) {

    // Retrieve username
    val context = LocalContext.current
    var userName: String? by remember { mutableStateOf(null) }
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
                .background(Color(0xFF1C2431)) // Dark background color
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (userName != null) "Hi, $userName" else "",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Welcome to Phishaware!",
                fontSize = 16.sp,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Warning Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color.Transparent, RoundedCornerShape(10.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Always verify links before sharing your info.",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Buttons Grid
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    HomeButtons("Probability URL", R.drawable.ic_link, onClick = {
                        navController.navigate(Screen.URL.route)
                    })
                    Spacer(modifier = Modifier.width(16.dp)) // Add spacing between buttons()
                    HomeButtons("Probability SMS", R.drawable.ic_message, onClick = {
                        navController.navigate(Screen.SMS.route)
                    })
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    HomeButtons("Chat with AI", R.drawable.ic_chat, onClick = {

                    })
                    Spacer(modifier = Modifier.width(16.dp)) // Add spacing between buttons()
                    HomeButtons("Learn & Protect", R.drawable.ic_education, onClick = {

                    })
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
