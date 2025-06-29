package com.example.project1.settings.help

import Screen
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.project1.R
import com.example.project1.ui.theme.Project1Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(navController: NavController) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & Support", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // WhatsApp Button
            SocialButton(
                icon = R.drawable.ic_whatsapp,
                text = "Contact on WhatsApp",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/201009725548"))
                    context.startActivity(intent)
                },
                // Specific brand color for WhatsApp
                tint = Color(0xFF25D366)
            )

            // Facebook Button
            SocialButton(
                icon = R.drawable.ic_facebook,
                text = "Find us on Facebook",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/mohamed.ahmed.558343/"))
                    context.startActivity(intent)
                }
            )

            // Website Button
            SocialButton(
                icon = R.drawable.ic_globe,
                text = "Visit our Website",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://bio.link/phishaware"))
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun SocialButton(
    icon: Int,
    text: String,
    onClick: () -> Unit,
    tint: Color = LocalContentColor.current // Use local content color by default
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground // Set default content color
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
                modifier = Modifier.size(24.dp),
                tint = tint // Apply the provided tint
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
                // Color is inherited from the button's contentColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HelpScreenPreview() {
    Project1Theme {
        HelpScreen(navController = rememberNavController())
    }
}