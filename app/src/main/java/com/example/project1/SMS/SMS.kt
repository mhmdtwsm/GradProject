package com.example.project1

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project1.SMS.SMSData.SMSHistoryItem
import com.example.project1.home.BottomNavigationBar
//import com.example.project1.home.Screen
import com.example.project1.viewmodel.SMSViewModel

@Composable
fun SMSScreen(navController: NavController) {
    val viewModel: SMSViewModel = viewModel()
    val smsText by viewModel.smsText.collectAsState()
    val smsHistory by viewModel.smsHistory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    androidx.compose.material3.Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                selectedScreen = Screen.SMS.route
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
            // Header
            androidx.compose.material3.Text(
                text = "SMS Scan",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            androidx.compose.material3.Divider(color = Color.Gray.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(24.dp))

            // SMS Input
            SMSInputField(
                value = smsText,
                onValueChange = { viewModel.onSMSTextChange(it) },
                onPasteClick = {
                    val clipboardManager =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = clipboardManager.primaryClip
                    if (clipData != null && clipData.itemCount > 0) {
                        val text = clipData.getItemAt(0).text.toString()
                        viewModel.pasteFromClipboard(text)
                    }
                }
            )

            // Check Button
            Button(
                onClick = { viewModel.checkSMS() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Gray,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Check",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // SMS History
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(smsHistory) { smsItem ->
                    SMSHistoryCard(smsItem = smsItem)
                }
            }
        }
    }
}

@Composable
fun SMSInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onPasteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text("message") },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black,
                    textColor = Color.Black
                ),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )

            IconButton(
                onClick = onPasteClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_link), // Using link icon as clipboard
                    contentDescription = "Paste from clipboard",
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
fun SMSHistoryCard(smsItem: SMSHistoryItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.LightGray.copy(alpha = 0.8f))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Display the actual message text
            Text(
                text = smsItem.message,
                color = Color.Black,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Status indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                val statusText = when {
                    smsItem.isSafe == true -> "Safe"
                    smsItem.isSafe == false -> "Unsafe"
                    else -> "No connection"
                }

                Text(
                    text = statusText,
                    color = Color.Black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}