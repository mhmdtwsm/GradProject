package com.example.project1

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(userName: String = "User_1") {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C2431)) // Dark background color
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Hi, $userName",
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
                NavButton("Probability URL", R.drawable.ic_link)
                Spacer(modifier = Modifier.width(16.dp)) // Add spacing between buttons()
                NavButton("Probability SMS", R.drawable.ic_message)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NavButton("Chat with AI", R.drawable.ic_chat)
                Spacer(modifier = Modifier.width(16.dp)) // Add spacing between buttons()
                NavButton("Learn & Protect", R.drawable.ic_education)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Navigation
        BottomNavigationBar()
    }
}

@Composable
fun NavButton(title: String, icon: Int, iconSize: Int = 70) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 8.dp,
        modifier = Modifier
            .size(width = 150.dp, height = 180.dp)
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .clickable(onClick = { /*TODO add the navigation to the onclick*/

            })
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1C2431))
                .border(3.dp, Color.Gray, RoundedCornerShape(12.dp)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(iconSize.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BottomNavigationBar() {
    BottomNavigation(
        backgroundColor = Color(0xFF1C2431),
        contentColor = Color.White
    ) {
        BottomNavigationItem(
            icon = {
                Icon(
                    painterResource(
                        id = R.drawable.ic_home
                    ),
                    contentDescription = "Home",
                    modifier = Modifier.size(40.dp),
                )
            },
            label = { Text("Home") },
            selected = true,
            onClick = {}
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    painterResource(
                        id = R.drawable.ic_link
                    ),
                    contentDescription = "URL",
                    modifier = Modifier.size(40.dp),
                )
            },
            label = { Text("URL") },
            selected = false,
            onClick = {}
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    painterResource(id = R.drawable.ic_message),
                    contentDescription = "SMS",
                    modifier = Modifier.size(40.dp),

                    )
            },
            label = { Text("SMS") },
            selected = false,
            onClick = {}
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    painterResource(id = R.drawable.ic_tools),
                    contentDescription = "Tools",
                    modifier = Modifier.size(37.dp),
                )
            },
            label = { Text("Tools") },
            selected = false,
            onClick = {}
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    painterResource(id = R.drawable.ic_settings),
                    contentDescription = "Settings",
                    modifier = Modifier.size(38.dp),
                )
            },
            label = { Text("Setting") },
            selected = false,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen()
}
