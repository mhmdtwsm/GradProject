package com.example.project1.tools.tips

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.project1.R
import com.example.project1.home.BottomNavigationBar
import com.example.project1.viewmodel.SecurityTipsViewModel

@Composable
fun SecurityTipsScreen(
    navController: NavController,
    viewModel: SecurityTipsViewModel = viewModel(),
) {
    val context = LocalContext.current

    // Fetch security tip when the screen is first displayed
    DisposableEffect(key1 = viewModel) {
        viewModel.fetchSecurityTip(context)
        onDispose { }
    }

    androidx.compose.material3.Scaffold() { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1C2431))
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
                        .clickable { navController.popBackStack() }
                )
                Spacer(modifier = Modifier.weight(0.69f))
                androidx.compose.material.Text(
                    "Cyber Tips",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))
            androidx.compose.material3.Divider(color = Color.Gray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(20.dp))

            // Main content
            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 100.dp)
                )
            } else if (viewModel.errorMessage.isNotEmpty()) {
                Text(
                    text = viewModel.errorMessage,
                    color = Color.Red,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 100.dp)
                )
            } else {
                viewModel.securityTip?.let { tip ->
                    // Image card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .padding(8.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(tip.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Security Tip Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tip text
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE2E2E2)
                        )
                    ) {
                        Text(
                            text = tip.tipText,
                            fontSize = 16.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Refresh button
            IconButton(
                onClick = { viewModel.refreshSecurityTip(context) },
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2C3E50))
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun SecurityTipsScreenPreview() {
    SecurityTipsScreen(navController = rememberNavController())
}
