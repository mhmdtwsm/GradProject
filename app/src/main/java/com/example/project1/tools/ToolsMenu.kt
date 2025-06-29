package com.example.project1.tools

import Screen
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.project1.R
import com.example.project1.home.BottomNavigationBar
import com.example.project1.ui.theme.Project1Theme
import com.example.project1.ui.theme.customColors

@Composable
fun ToolsMenu(modifier: Modifier = Modifier, navController: NavController) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                selectedScreen = Screen.ToolsMenu.route
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Tools",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            Spacer(modifier = Modifier.height(32.dp))

            // Cards
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ToolCard(
                    iconId = R.drawable.ic_link,
                    title = "URL Analyzer",
                    onClick = { navController.navigate(Screen.UrlAnalyzer.route) }
                )
                ToolCard(
                    iconId = R.drawable.passwordgenerate,
                    title = "Password Generate",
                    onClick = { navController.navigate(Screen.PasswordGenerate.route) }
                )
                ToolCard(
                    iconId = R.drawable.passwordcheck,
                    title = "Password Test",
                    onClick = { navController.navigate(Screen.PasswordTest.route) }
                )
                ToolCard(
                    iconId = R.drawable.ic_education,
                    title = "Security Tips",
                    onClick = { navController.navigate(Screen.SecurityTips.route) }
                )
                ToolCard(
                    iconId = R.drawable.ic_chat,
                    title = "Chat with AI",
                    onClick = { navController.navigate(Screen.Chat.route) }
                )
            }
        }
    }
}

@Composable
fun ToolCard(iconId: Int, title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.customColors.cardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Preview(showBackground = true, name = "Tools Menu Light")
@Composable
fun ToolsMenuPreviewLight() {
    Project1Theme { // Wrapping with your theme for preview
        ToolsMenu(navController = rememberNavController())
    }
}

@Preview(showBackground = true, name = "Tools Menu Dark")
@Composable
fun ToolsMenuPreviewDark() {
    Project1Theme { // You would need to update your preview to handle dark mode state
        ToolsMenu(navController = rememberNavController())
    }
}