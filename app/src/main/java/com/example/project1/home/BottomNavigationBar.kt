package com.example.project1.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.project1.R
import com.example.project1.Screen

@Composable
fun BottomNavigationBar(navController: NavController, selectedScreen: String, txtSize: Int = 10) {

    BottomNavigation(
        backgroundColor = Color(0xFF1C2431),
        contentColor = Color.White,
        modifier = Modifier
            .padding(vertical = 10.dp)
            .zIndex(1f)
    ) {
        BottomNavigationItem(
            icon = {
                Icon(
                    painterResource(id = R.drawable.ic_home),
                    contentDescription = "Home",
                    modifier = Modifier.size(40.dp),
                    tint = if (selectedScreen == Screen.Home.route) Color.White else Color.Gray
                )
            },
            label = {
                Text(
                    fontWeight = FontWeight.Bold,
                    text = "Home",
                    fontSize = txtSize.sp
                )
            }, selected = selectedScreen == Screen.Home.route,
            modifier = Modifier.padding(vertical = 5.dp),
            onClick = { navController.navigate(Screen.Home.route) }
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    painterResource(id = R.drawable.ic_link),
                    contentDescription = "URL",
                    modifier = Modifier.size(40.dp),
                    tint = if (selectedScreen == Screen.URL.route) Color.White else Color.Gray
                )
            },
            label = {
                Text(
                    fontWeight = FontWeight.Bold,
                    text = "URL",
                    fontSize = txtSize.sp
                )
            },
            selected = selectedScreen == Screen.URL.route,
            modifier = Modifier.padding(vertical = 5.dp),
            onClick = {
                navController.navigate(Screen.URL.route)
            }
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    painterResource(id = R.drawable.ic_message),
                    contentDescription = "SMS",
                    modifier = Modifier.size(40.dp),
                    tint = if (selectedScreen == Screen.SMS.route) Color.White else Color.Gray
                )
            },
            label = {
                Text(
                    fontWeight = FontWeight.Bold,
                    text = "SMS",
                    fontSize = txtSize.sp
                )
            }, selected = selectedScreen == Screen.SMS.route,
            modifier = Modifier.padding(vertical = 5.dp),
            onClick = {
                navController.navigate(Screen.SMS.route)
            }
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    painterResource(id = R.drawable.ic_tools),
                    contentDescription = "Tools",
                    modifier = Modifier.size(40.dp),
                    tint = if (selectedScreen == Screen.ToolsMenu.route) Color.White else Color.Gray
                )
            },
            label = {
                Text(
                    text = "Tools",
                    fontSize = txtSize.sp,
                    fontWeight = FontWeight.Bold,

                    )
            },
            selected = selectedScreen == Screen.ToolsMenu.route,
            modifier = Modifier.padding(vertical = 5.dp),
            onClick = { navController.navigate(Screen.ToolsMenu.route) }
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    painterResource(id = R.drawable.ic_settings),
                    contentDescription = "Settings",
                    modifier = Modifier.size(40.dp),
                    tint = if (selectedScreen == Screen.Settings.route) Color.White else Color.Gray
                )
            },
            label = {
                Text(
                    text = "Settings",
                    fontSize = txtSize.sp,
                    fontWeight = FontWeight.Bold,
                )
            }, selected = selectedScreen == Screen.Settings.route,
            modifier = Modifier.padding(vertical = 5.dp),
            onClick = {
                navController.navigate(Screen.Settings.route)
            }
        )
    }
}
