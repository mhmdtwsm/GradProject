package com.example.project1.tools.passwordtest

import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.project1.R
import java.util.regex.Pattern

@Composable
fun PasswordTest(navController: NavController) {
    var password by remember { mutableStateOf("") }
    var passwordStrength by remember { mutableStateOf("Your Password is weak\nUse 8+ characters.\nAdd Aa, 123, @#!.\nAvoid names/dates.") }
    var backgroundColor by remember { mutableStateOf(Color.Gray) }

    fun checkPasswordStrength(input: String) {
        var score = 0

        // 1. Length Check
        when {
            input.length < 8 -> score += 0
            input.length in 8..11 -> score += 5
            input.length in 12..15 -> score += 15
            input.length >= 16 -> score += 20
        }

        // 2. Character Variety Check
        val hasLower = input.any { it.isLowerCase() }
        val hasUpper = input.any { it.isUpperCase() }
        val hasDigit = input.any { it.isDigit() }
        val hasSymbol = input.any { !it.isLetterOrDigit() }

        when {
            hasLower && hasUpper && hasDigit && hasSymbol -> score += 20
            hasLower && hasUpper && hasDigit -> score += 10
            hasLower && hasUpper -> score += 5
            else -> score += 0
        }

        // 3. Repetitions & Patterns Check
        val repeatedPattern = Pattern.compile("(.)\\1{3,}") // Detects repeated characters
        val commonPatterns =
            listOf("123456", "qwerty", "abcdef", "111111", "password", "admin", "letmein")

        if (repeatedPattern.matcher(input).find()) score -= 10
        if (commonPatterns.any { input.contains(it, ignoreCase = true) }) score -= 10

        // 4. Dictionary & Common Words Check
        val weakWords = listOf("password", "admin", "welcome", "hello", "letmein")
        if (weakWords.any { input.contains(it, ignoreCase = true) }) score -= 15

        // Determine Strength
        when {
            score <= 10 -> {
                passwordStrength = "❌ Weak\nUse more variety and avoid common words."
                backgroundColor = Color.Red
            }

            score in 11..30 -> {
                passwordStrength = "⚠️ Moderate\nTry adding symbols and increasing length."
                backgroundColor = Color.Yellow
            }

            score in 31..50 -> {
                passwordStrength = "✅ Strong\nGood job!"
                backgroundColor = Color.Green
            }

            else -> {
                passwordStrength = "🏆 Very Strong!\nYour password is well-protected."
                backgroundColor = Color.Blue
            }
        }
    }

    fun pasteFromClipboard(context: Context, onPaste: (String) -> Unit) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboard.primaryClip
        if (clipData != null && clipData.itemCount > 0) {
            val pastedText = clipData.getItemAt(0).text.toString()
            onPaste(pastedText)
        } else {
            Toast.makeText(context, "Clipboard is empty!", Toast.LENGTH_SHORT).show()
        }
    }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C2431))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Top Bar with Back Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp),
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
            Text(
                "Password Test",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(20.dp))
        Image(
            painter = painterResource(id = R.drawable.line),
            contentDescription = "line",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(width = 300.dp, height = 4.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 100.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = passwordStrength,
                    style = TextStyle(fontSize = 16.sp, color = Color.Black),
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray, shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        textColor = Color.White
                    )
                )
                IconButton(onClick = { pasteFromClipboard(context) { password = it } }) {
                    Icon(
                        painter = painterResource(id = R.drawable.clipboard),
                        contentDescription = "Paste",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { checkPasswordStrength(password) },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Test", color = Color.Black)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordTestPreview() {
    PasswordTest(navController = NavController(LocalContext.current))
}
