package com.example.project1.authentication.passwordreset.OTP

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.preference.PreferenceManager
import com.example.project1.ui.theme.Project1Theme
import com.example.project1.ui.theme.customColors
import com.example.project1.viewmodel.VerifyCodeUiState
import com.example.project1.viewmodel.VerifyCodeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyCodeScreen(
    navController: NavController,
    fromLogin: Boolean,
    viewModel: VerifyCodeViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val otpValue by viewModel.otpValue.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val savedEmail = sharedPrefs.getString("VERIFY_EMAIL", "") ?: ""
        val savedToken = sharedPrefs.getString("VERIFY_TOKEN", "") ?: ""
        if (savedEmail.isNotEmpty() && savedToken.isNotEmpty()) {
            viewModel.setEmailAndToken(savedEmail, savedToken)
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is VerifyCodeUiState.Success) {
            delay(500)
            if (!fromLogin) {
                navController.navigate("login") { popUpTo("verify_code") { inclusive = true } }
            }
        } else if (uiState is VerifyCodeUiState.ResendSuccess) {
            delay(3000)
            viewModel.resetUiState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Check Your Email") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                val displayEmail = viewModel.getEmail()
                Text(
                    text = "We sent a verification code to:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = displayEmail,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                OtpInputField(
                    otpValue = otpValue,
                    onOtpValueChange = { viewModel.updateOtpValue(it) },
                    isEnabled = uiState !is VerifyCodeUiState.Loading && uiState !is VerifyCodeUiState.ResendLoading
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.verifyOtp(navController, context, fromLogin) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = uiState !is VerifyCodeUiState.Loading && uiState !is VerifyCodeUiState.ResendLoading && otpValue.length == 6
                ) {
                    Text("Verify Code")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Didn't get the code?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(
                        onClick = { viewModel.resendOtp() },
                        enabled = uiState !is VerifyCodeUiState.Loading && uiState !is VerifyCodeUiState.ResendLoading
                    ) {
                        Text("Resend")
                    }
                }

                if (uiState is VerifyCodeUiState.Error) {
                    Text(
                        text = (uiState as VerifyCodeUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                if (uiState is VerifyCodeUiState.ResendSuccess) {
                    Text(
                        text = "OTP resent successfully!",
                        color = MaterialTheme.customColors.success,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }

            if (uiState is VerifyCodeUiState.Loading || uiState is VerifyCodeUiState.ResendLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun OtpInputField(
    otpValue: String,
    onOtpValueChange: (String) -> Unit,
    isEnabled: Boolean,
    otpLength: Int = 6
) {
    val focusRequesters = remember { List(otpLength) { FocusRequester() } }

    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        for (i in 0 until otpLength) {
            OutlinedTextField(
                value = otpValue.getOrNull(i)?.toString() ?: "",
                onValueChange = { newValue ->
                    if (newValue.length <= 1) {
                        val newOtp = otpValue.toMutableList()
                        if (newValue.isEmpty()) {
                            if (i < otpValue.length) {
                                newOtp.removeAt(i)
                                onOtpValueChange(newOtp.joinToString(""))
                                if (i > 0) focusRequesters[i - 1].requestFocus()
                            }
                        } else {
                            if (i < otpValue.length) newOtp[i] = newValue.first() else newOtp.add(newValue.first())
                            onOtpValueChange(newOtp.joinToString("").take(otpLength))
                            if (i < otpLength - 1) focusRequesters[i + 1].requestFocus()
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequesters[i]),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                enabled = isEnabled
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerifyCodeScreenPreview() {
    Project1Theme {
        VerifyCodeScreen(
            navController = rememberNavController(),
            fromLogin = false
        )
    }
}