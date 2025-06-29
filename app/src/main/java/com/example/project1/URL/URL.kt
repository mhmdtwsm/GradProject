package com.example.project1

import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project1.home.BottomNavigationBar
import com.example.project1.qrscanner.QRCodeScannerScreen
import com.example.project1.ui.theme.customColors
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
    LaunchedEffect(key1 = autoScan) {
        if (autoScan) {
            try {
                val clipboardContent = getClipboardText(context)
                if (clipboardContent.isNotEmpty() && clipboardContent.startsWith("http")) {
                    urlText.value = clipboardContent
                    kotlinx.coroutines.delay(300) // Ensure UI updates
                    viewModel.scanUrl(clipboardContent)
                }
            } catch (e: Exception) {
                Log.e("URLScreen", "Error auto-scanning from clipboard: ${e.message}")
            }
        }
    }

    if (showQrScanner) {
        QRCodeScannerScreen(
            onUrlDetected = { detectedUrl ->
                urlText.value = detectedUrl
                viewModel.scanUrl(detectedUrl)
                showQrScanner = false
            },
            onClose = {
                showQrScanner = false
            }
        )
    } else {
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
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "URL Scan",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                Spacer(modifier = Modifier.height(24.dp))

                // Status indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    when (val result = scanResult) {
                        is ScanResult.Loading -> {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                        is ScanResult.Safe -> {
                            Text(
                                text = "SAFE",
                                color = MaterialTheme.customColors.success,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        is ScanResult.Unsafe -> {
                            Text(
                                text = "UNSAFE",
                                color = MaterialTheme.customColors.danger,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        is ScanResult.Error -> {
                            Text(
                                text = "ERROR",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        null -> {
                            // Show nothing, but keep the space
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // URL Input field
                OutlinedTextField(
                    value = urlText.value,
                    onValueChange = { urlText.value = it },
                    placeholder = { Text("Enter URL to scan", color = MaterialTheme.customColors.hintText) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp, max = 56.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.customColors.inputBackground,
                        unfocusedContainerColor = MaterialTheme.customColors.inputBackground,
                        cursorColor = MaterialTheme.customColors.onInputBackground,
                        focusedTextColor = MaterialTheme.customColors.onInputBackground,
                        unfocusedTextColor = MaterialTheme.customColors.onInputBackground,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.customColors.inputBorder,
                    ),
                    trailingIcon = {
                        Row {
                            IconButton(onClick = { showQrScanner = true }) {
                                Icon(
                                    painterResource(id = R.drawable.qr),
                                    contentDescription = "Scan QR Code",
                                    tint = MaterialTheme.customColors.onInputBackground
                                )
                            }
                            IconButton(onClick = {
                                urlText.value = getClipboardText(context)
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.clipboard),
                                    contentDescription = "Paste from clipboard",
                                    tint = MaterialTheme.customColors.onInputBackground
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
                            viewModel.scanUrl(urlText.value)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Check",
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
                        containerColor = MaterialTheme.customColors.historyCard
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
                                color = MaterialTheme.customColors.historyHeader,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Status",
                                color = MaterialTheme.customColors.historyHeader,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                        // History list
                        LazyColumn {
                            items(urlHistory) { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.url,
                                        color = MaterialTheme.customColors.onHistoryCard,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(0.7f)
                                    )
                                    Text(
                                        text = if (item.isSafe) "Safe" else "Unsafe",
                                        color = if (item.isSafe) MaterialTheme.customColors.success else MaterialTheme.customColors.danger,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
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

private fun getClipboardText(context: Context): String {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    return clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
}