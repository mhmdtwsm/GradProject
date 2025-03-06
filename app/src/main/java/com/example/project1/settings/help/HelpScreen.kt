package com.example.project1.settings.help

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.project1.R

@Composable
fun HelpScreen(navController: NavController) {
    val darkNavy = Color(0xFF1E293D)
    val headTextPadd = (LocalConfiguration.current.screenWidthDp) / 10

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkNavy)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow),
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Help & Support",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = headTextPadd.dp)
                )
            }
            Image(
                painter = painterResource(id = R.drawable.line),
                contentDescription = "line",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(width = 300.dp, height = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Spacer(modifier = Modifier.height(16.dp))

            // Social Media Buttons
            SocialButton(
                icon = R.drawable.ic_whatsapp,
                text = "WhatsApp",
                onClick = { /* TODO Handle WhatsApp click */ },
            )

            Spacer(modifier = Modifier.height(16.dp))

            SocialButton(
                icon = R.drawable.ic_facebook,
                text = "Facebook",
                onClick = { /* TODO Handle website click */ },
            )

            Spacer(modifier = Modifier.height(16.dp))

            SocialButton(
                icon = R.drawable.ic_globe,
                text = "Website",
                onClick = { /* TODO Handle Facebook click */ },
            )
        }
    }
}

@Composable
fun SocialButton(
    icon: Int,
    text: String,
    onClick: () -> Unit,
    tint: Color = Color.White
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = text,
                modifier = Modifier
                    .size(24.dp),
                tint = tint
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HelpScreenPreview() {
    HelpScreen( navController = NavController(LocalContext.current))
}