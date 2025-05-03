package com.example.project1

import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.project1.home.BottomNavigationBar
import com.example.project1.qrscanner.QRCodeScannerScreen
import com.example.project1.viewmodel.ScanResult
import com.example.project1.viewmodel.URLViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun URLScreen(
    navController: NavController,
    viewModel: URLViewModel = viewModel(),
    autoScan: Boolean = true
) {
    val context = LocalContext.current
    val urlText = remember { mutableStateOf("") }
    val scanResult by viewModel.scanResult.collectAsState()
    val urlHistory by viewModel.urlHistory.collectAsState(initial = emptyList())

    // State to control showing the QR scanner
    var showQrScanner by remember { mutableStateOf(false) }

    // Load history when the screen is first displayed
    LaunchedEffect(key1 = true) {
        try {
            viewModel.loadUrlHistory()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Auto-paste and scan if coming from notification
    LaunchedEffect(key1 = true) {
        if (autoScan) {
            try {
                // Get clipboard content
                val clipboardContent = getClipboardText(context)
                println("Clipboard content: '$clipboardContent'")

                // Check if clipboard has content
                if (clipboardContent.isNotEmpty()) {
                    println("Clipboard is not empty")

                    // Check if it starts with http
                    if (clipboardContent.startsWith("http")) {
                        println("Clipboard content starts with http")

                        // Update the text field
                        urlText.value = clipboardContent
                        println("Set urlText.value to: ${urlText.value}")

                        // Small delay to ensure UI is updated
                        kotlinx.coroutines.delay(300)

                        // Scan the URL
                        println("Scanning URL: ${urlText.value}")
                        viewModel.scanUrl(clipboardContent)
                    } else {
                        println("Clipboard content does not start with http")
                    }
                } else {
                    println("Clipboard is empty")
                }
            } catch (e: Exception) {
                println("Error accessing clipboard: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    // Show QR Scanner if activated
    if (showQrScanner) {
        QRCodeScannerScreen(
            onUrlDetected = { detectedUrl ->
                // Update the text field with detected URL
                urlText.value = detectedUrl

                // Scan the URL
                viewModel.scanUrl(detectedUrl)

                // Close the scanner
                showQrScanner = false
            },
            onClose = {
                // Close the scanner without scanning
                showQrScanner = false
            }
        )
    } else {
        // Show regular URL screen
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    selectedScreen = Screen.URL.route
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1C2431)) // Dark blue background
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "URL Scan",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Divider(color = Color.Gray.copy(alpha = 0.5f))

                Spacer(modifier = Modifier.height(24.dp))

                // Status indicator
                when (val result = scanResult) {
                    is ScanResult.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(25.dp))
                                .background(Color.Gray.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }

                    is ScanResult.Safe -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(25.dp))
                                .background(Color.Gray.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "SAFE",
                                color = Color.Green,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    is ScanResult.Unsafe -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(25.dp))
                                .background(Color.Gray.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "UNSAFE",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    is ScanResult.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(25.dp))
                                .background(Color.Gray.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ERROR: ${result.message}",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    null -> {
                        // Show nothing if no scan has been performed
                        Spacer(modifier = Modifier.height(50.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // URL Input field with clipboard and QR code icons
                OutlinedTextField(
                    value = urlText.value,
                    onValueChange = { urlText.value = it },
                    placeholder = { Text("Enter URL to scan") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(25.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White,
                        cursorColor = Color.Black,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    trailingIcon = {
                        Row {
                            IconButton(onClick = {
                                // Open QR code scanner
                                showQrScanner = true
                            }) {
                                Icon(
                                    painterResource(id = R.drawable.qr),
                                    contentDescription = "Scan QR Code",
                                    tint = Color.Black
                                )
                            }

                            IconButton(onClick = {
                                try {
                                    val clipboardText = getClipboardText(context)
                                    println("Clipboard button clicked, content: '$clipboardText'")
                                    if (clipboardText.isNotEmpty()) {
                                        urlText.value = clipboardText
                                        println("Set urlText.value from button: ${urlText.value}")
                                    }
                                } catch (e: Exception) {
                                    println("Error accessing clipboard from button: ${e.message}")
                                    e.printStackTrace()
                                }
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.clipboard),
                                    contentDescription = "Paste from clipboard",
                                    tint = Color.Black
                                )
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Check button
                Button(
                    onClick = {
                        if (urlText.value.isNotEmpty()) {
                            println("Check button clicked, scanning: ${urlText.value}")
                            viewModel.scanUrl(urlText.value)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray
                    )
                ) {
                    Text(
                        text = "Check",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // History section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Gray.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // History header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "History",
                                color = Color.LightGray,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Status",
                                color = Color.LightGray,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // History list
                        LazyColumn {
                            items(urlHistory) { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.url,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        modifier = Modifier.weight(0.7f)
                                    )

                                    Text(
                                        text = if (item.isSafe) "safe" else "unsafe",
                                        color = if (item.isSafe) Color.Green else Color.Red,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.weight(0.3f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Helper function to get text from clipboard
private fun getClipboardText(context: Context): String {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    return if (clipboard.hasPrimaryClip() && clipboard.primaryClip?.itemCount ?: 0 > 0) {
        clipboard.primaryClip?.getItemAt(0)?.text.toString()
    } else {
        ""
    }
}