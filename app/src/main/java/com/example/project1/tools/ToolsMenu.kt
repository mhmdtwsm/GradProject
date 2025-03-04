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

            Cardy(onClick = { navController.navigate(Screen.PasswordTest.route) })
            Cardy(onClick = { navController.navigate(Screen.PasswordTest.route) })
            Cardy(onClick = { navController.navigate(Screen.PasswordTest.route) })
            Cardy(onClick = { navController.navigate(Screen.PasswordTest.route) })

        }
    }

}

@Composable
fun Cardy(onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 0.dp, // Remove any shadow that might look like a border
        backgroundColor = Color(0xFF1C2431), // Ensure background matches parent
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .border(3.dp, Color.Gray, RoundedCornerShape(12.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chat),
                contentDescription = "Password Test",
                tint = Color.White,
                modifier = Modifier
                    .size(70.dp)
                    .padding(vertical = 10.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Password Test",
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier
            )
        }
    }
}


@Preview
@Composable
fun ToolsMenuPreview() {
    ToolsMenu(navController = NavController(LocalContext.current))
}