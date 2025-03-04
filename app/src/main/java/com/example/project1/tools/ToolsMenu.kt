package com.example.project1.tools

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.project1.R
import com.example.project1.Screen

@Composable
fun ToolsMenu(modifier: Modifier = Modifier, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C2431))
    ) {
        Text(
            text = "Tools",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 40.dp, bottom = 20.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.line),
            contentDescription = "line",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(width = 270.dp, height = 4.dp)
        )
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Cards(onClick = { navController.navigate(Screen.PasswordTest.route) })
            Cards(onClick = { navController.navigate(Screen.PasswordTest.route) })
            Cards(onClick = { navController.navigate(Screen.PasswordTest.route) })
            Cards(onClick = { navController.navigate(Screen.PasswordTest.route) })

        }
    }
}


@Preview
@Composable
fun ToolsMenuPreview() {
    ToolsMenu(navController = NavController(LocalContext.current))
}