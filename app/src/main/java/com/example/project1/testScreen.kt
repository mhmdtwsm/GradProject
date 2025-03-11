package com.example.project1.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ClipboardScreen(context: Context) {
    var clipboardText by remember { mutableStateOf("No clipboard data") }

    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    LaunchedEffect(Unit) {
        val clip: ClipData? = clipboardManager.primaryClip
        clipboardText = if (clip != null && clip.itemCount > 0) {
            clip.getItemAt(0).text.toString()
        } else {
            "No clipboard data"
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text("Clipboard Content:", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(clipboardText, fontSize = 16.sp)
    }
}
