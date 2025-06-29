package com.example.project1

import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.project1.SMS.SMSData.SMSHistoryItem
import com.example.project1.home.BottomNavigationBar
import com.example.project1.ui.theme.customColors
import com.example.project1.viewmodel.SMSViewModel

@Composable
fun SMSScreen(navController: NavController) {
    val viewModel: SMSViewModel = viewModel()
    val smsText by viewModel.smsText.collectAsState()
    val smsHistory by viewModel.smsHistory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    Scaffold(
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
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "SMS Scan",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

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

            Spacer(modifier = Modifier.height(16.dp))

            // Check Button
            Button(
                onClick = { viewModel.checkSMS() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Check",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "History",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.customColors.historyHeader,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // SMS History
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(smsHistory) { smsItem ->
                    SMSHistoryCard(smsItem = smsItem)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SMSInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onPasteClick: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Enter message to scan", color = MaterialTheme.customColors.hintText) },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp), // Increased height for multi-line input
        shape = RoundedCornerShape(16.dp),
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
            IconButton(
                onClick = onPasteClick,
                modifier = Modifier
                    .padding(top = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.clipboard),
                    contentDescription = "Paste from clipboard",
                    tint = MaterialTheme.customColors.onInputBackground.copy(alpha = 0.7f)
                )
            }
        },
        singleLine = false
    )
}

@Composable
fun SMSHistoryCard(smsItem: SMSHistoryItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.customColors.historyCard
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = smsItem.message,
                color = MaterialTheme.customColors.onHistoryCard,
                fontSize = 14.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            )

            val statusText: String
            val statusColor: androidx.compose.ui.graphics.Color

            when (smsItem.isSafe) {
                true -> {
                    statusText = "Safe"
                    statusColor = MaterialTheme.customColors.success
                }
                false -> {
                    statusText = "Unsafe"
                    statusColor = MaterialTheme.customColors.danger
                }
                null -> {
                    statusText = "No Connection"
                    statusColor = MaterialTheme.colorScheme.onSurfaceVariant
                }
            }

            Text(
                text = statusText,
                color = statusColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SMSPreview() {
    SMSScreen(navController = rememberNavController())
}