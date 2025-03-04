package com.example.project1.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.project1.R
import com.example.project1.Screen

@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomNavigation(
        backgroundColor = Color(0xFF1C2431),
        contentColor = Color.White,
        modifier = Modifier
            .padding(vertical = 5.dp)
            .border(2.dp, Color.White, RoundedCornerShape(12.dp))
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
            modifier = Modifier.padding(vertical = 5.dp),
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
            modifier = Modifier.padding(vertical = 5.dp),
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
            modifier = Modifier.padding(vertical = 5.dp),

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
            modifier = Modifier.padding(vertical = 5.dp),

            onClick = {
                navController.navigate(Screen.ToolsMenu.route)
            }
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
            modifier = Modifier.padding(vertical = 5.dp),

            onClick = {}
        )
    }
}