package com.example.project1.settings.terms

import Screen
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.project1.authentication.CommonComponents.GeneralHeader // Make sure this import is correct
import com.example.project1.ui.theme.Project1Theme
import com.example.project1.ui.theme.customColors

@Composable
fun TermsScreen(navController: NavController) {
    Project1Theme {
        Scaffold(
            topBar = {
                // Using the reusable, theme-aware header
                GeneralHeader(
                    title = "Terms and Policies",
                    showBackButton = true,
                    onBackClick = { navController.popBackStack() }
                )
            },
            // The container color is now handled by the theme
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(1.dp)) // Spacer to push content down from header

                PolicySection(
                    title = "Purpose of the Application",
                    content = "This application is designed as part of a cybersecurity awareness initiative to help non-technical users recognize and avoid phishing threats. It educates users on identifying fraudulent links and suspicious SMS messages while providing insights based on AI-driven threat analysis. Additionally, the app includes interactive educational content and security tools to enhance user awareness."
                )

                PolicySection(
                    title = "Data Collection",
                    content = "To improve the accuracy of phishing detection, the app may analyze URLs and SMS message patterns. However, we do not collect or store any personally identifiable information, such as names, phone numbers, or email addresses. All processing occurs locally or through secure API interactions, ensuring user privacy and data protection."
                )

                PolicySection(
                    title = "User Responsibility",
                    content = "This application is an educational and awareness tool designed to assist users in recognizing phishing threats. It does not provide foolproof protection against cyberattacks. Users are responsible for verifying information and taking necessary precautions when interacting with links and messages. The app should be used as a supplementary security resource, not as a replacement for cybersecurity best practices."
                )

                PolicySection(
                    title = "Limitation of Liability",
                    content = "We do not guarantee absolute accuracy in detecting phishing threats, and we are not responsible for any damages, losses, or security breaches resulting from reliance on the app's recommendations. The application is provided \"as is,\" without any express or implied warranties. Users assume full responsibility for their cybersecurity decisions and actions."
                )
                Spacer(modifier = Modifier.height(1.dp))
            }
        }
    }
}

@Composable
fun PolicySection(title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        // The card now correctly uses the theme's custom colors
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.customColors.cardBackground
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface // Theme-aware color
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant, // Theme-aware color
                lineHeight = MaterialTheme.typography.bodyMedium.fontSize * 1.5
            )
        }
    }
}

@Preview
@Composable
fun TermsScreenPreview() {
    Project1Theme {
        TermsScreen(navController = rememberNavController())
    }
}