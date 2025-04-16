package com.example.project1.authentication.passwordreset

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

// Create a function to make the API call
fun resetPassword(
    email: String,
    newPassword: String,
    context: Context,
    onSuccess: () -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val url = URL("http://phishaware.runasp.net/api/auth/reset-password")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            // Create JSON payload
            val jsonPayload = JSONObject()
            jsonPayload.put("email", email)
            jsonPayload.put("newPassword", newPassword)

            // Write to the connection
            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(jsonPayload.toString())
                writer.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Password reset successful
                CoroutineScope(Dispatchers.Main).launch {
                    onSuccess()
                    Toast.makeText(
                        context,
                        "Password has been reset successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                // Handle error
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        context,
                        "Failed to reset password. Error code: $responseCode",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            connection.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}