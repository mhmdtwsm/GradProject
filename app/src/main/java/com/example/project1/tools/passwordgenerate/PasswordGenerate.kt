package com.example.project1.tools.passwordgenerate

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.project1.R
import kotlin.random.Random

@Composable
fun PasswordGenerate(navController: NavController) {
    var password by remember { mutableStateOf("pass@#12WORD") }
    var passwordLength by remember { mutableStateOf(14f) }
    var upperCase by remember { mutableStateOf(true) }
    var lowerCase by remember { mutableStateOf(true) }
    var numbers by remember { mutableStateOf(true) }
    var symbols by remember { mutableStateOf(true) }

    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C2431))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar with Back Button and Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "Password Generator",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Image(
            painter = painterResource(id = R.drawable.line),
            contentDescription = "line",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(width = 300.dp, height = 4.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Password Display
        TextField(
            value = password,
            onValueChange = { },
            readOnly = true,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color(0xFF1C2431),
                textColor = Color.White,
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.Gray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Password Length Slider
        Text(
            text = "Length: ${passwordLength.toInt()}",
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Slider(
            value = passwordLength,
            onValueChange = { passwordLength = it },
            valueRange = 4f..32f,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Options",
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Password Options
        PasswordOption(
            text = "Upper case (A-Z)",
            icon = R.drawable.ic_uppercase,
            isChecked = upperCase,
            onCheckedChange = { upperCase = it }
        )

        PasswordOption(
            text = "Lower case (a-z)",
            icon = R.drawable.ic_lowercase,
            isChecked = lowerCase,
            onCheckedChange = { lowerCase = it }
        )

        PasswordOption(
            text = "Numbers",
            icon = R.drawable.ic_numbers,
            isChecked = numbers,
            onCheckedChange = { numbers = it }
        )

        PasswordOption(
            text = "Symbols(@#&$)",
            icon = R.drawable.ic_symbols,
            isChecked = symbols,
            onCheckedChange = { symbols = it }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Generate Button
//        Button(
//            onClick = {
//                password = generatePassword(
//                    length = passwordLength.toInt(),
//                    useUpperCase = upperCase,
//                    useLowerCase = lowerCase,
//                    useNumbers = numbers,
//                    useSymbols = symbols
//                )
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp),
//            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF2E3B4E))
//        ) {
//            Text("Generate", color = Color.White)
//        }


        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = { clipboardManager.setText(AnnotatedString(password)) },
                modifier = Modifier
                    .background(Color(0xFF2E3B4E), RoundedCornerShape(15.dp))
                    .padding(5.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_copy),
                    contentDescription = "Copy",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = { password = "" },
                modifier = Modifier
                    .background(Color(0xFF2E3B4E), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "Clear",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = {
                    password = generatePassword(
                        length = passwordLength.toInt(),
                        useUpperCase = upperCase,
                        useLowerCase = lowerCase,
                        useNumbers = numbers,
                        useSymbols = symbols
                    )
                },
                modifier = Modifier
                    .background(Color(0xFF2E3B4E), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_refresh),
                    contentDescription = "Refresh",
                    tint = Color.White
                )
            }
        }
    }
}


private fun generatePassword(
    length: Int,
    useUpperCase: Boolean,
    useLowerCase: Boolean,
    useNumbers: Boolean,
    useSymbols: Boolean
): String {
    val upperCaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val lowerCaseChars = "abcdefghijklmnopqrstuvwxyz"
    val numberChars = "0123456789"
    val symbolChars = "@#$%&*"

    var allowedChars = ""
    if (useUpperCase) allowedChars += upperCaseChars
    if (useLowerCase) allowedChars += lowerCaseChars
    if (useNumbers) allowedChars += numberChars
    if (useSymbols) allowedChars += symbolChars

    if (allowedChars.isEmpty()) return ""

    return (1..length)
        .map { allowedChars[Random.nextInt(0, allowedChars.length)] }
        .joinToString("")
}

@Preview(showBackground = true)
@Composable
fun PasswordGeneratePreview() {
    PasswordGenerate(navController = NavController(LocalContext.current))
}