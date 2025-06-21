package com.example.project1.settings.terms

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.project1.home.BottomNavigationBar

@Composable
fun TermsScreen(
    navController: NavController
) {
    val darkNavy = Color(0xFF1C2431)
    val containerColor = Color(0xFF232B3D)
    val scrollState = rememberScrollState()
    val headTextPadd =  (LocalConfiguration.current.screenWidthDp)/10

    androidx.compose.material3.Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                selectedScreen = Screen.Terms.route
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1C2431))
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Top Bar with Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { navController.navigate(Screen.Settings.route) }
                )
                Spacer(modifier = Modifier.weight(0.69f))
                androidx.compose.material.Text(
                    "Terms and Policies",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            androidx.compose.material3.Divider(color = Color.Gray.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(20.dp))

            // Purpose Section
            PolicySection(
                title = "Purpose of the Application",
                content = "This application is designed as part of a cybersecurity awareness initiative to help non-technical users recognize and avoid phishing threats. It educates users on identifying fraudulent links and suspicious SMS messages while providing insights based on AI-driven threat analysis. Additionally, the app includes interactive educational content and security tools to enhance user awareness."
            )

            // Data Collection
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
                content = "We do not guarantee absolute accuracy in detecting phishing threats, and we are not responsible for any damages, losses, or security breaches resulting from reliance on the app’s recommendations. The application is provided \"as is,\" without any express or implied warranties. Users assume full responsibility for their cybersecurity decisions and actions."
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TermsAndPoliciesScreenPreview() {
    TermsScreen(navController = NavController(LocalContext.current))
}
