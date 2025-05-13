package com.example.project1.test

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class SmsItem(
    val smS_ID: Int,
    val smS_text: String,
    val scanResult: String,
    val checkDate: String,
    val identityUserID: String,
    val user: Any?
)

fun main() {
    val apiUrl = "http://phishaware.runasp.net/api/Prediction/all_sms"
    val authToken =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIyYjMzYmE2ZS01NTM0LTRkZGYtYWM2OS1lNDk5ZjhiNGQ5NTUiLCJlbWFpbCI6Im1vaGFlZHRvcmVzQGdtYWlsLmNvbSIsImp0aSI6ImNmOGM1N2RjLTBlMTEtNGU5Yy04YjVlLTc1NjNkNjc0MzA4MSIsImh0dHA6Ly9zY2hlbWFzLnhtbHNvYXAub3JnL3dzLzIwMDUvMDUvaWRlbnRpdHkvY2xhaW1zL25hbWVpZGVudGlmaWVyIjoiMmIzM2JhNmUtNTUzNC00ZGRmLWFjNjktZTQ5OWY4YjRkOTU1IiwiaHR0cDovL3NjaGVtYXMubWljcm9zb2Z0LmNvbS93cy8yMDA4LzA2L2lkZW50aXR5L2NsYWltcy9yb2xlIjoiVXNlciIsImV4cCI6MTc0NjkzMjAyOCwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdCIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3QifQ.iwAqvcErl97pf71sHscpFfvs_vV6oRNuoo3qFzuzoX0" // Replace with your actual token

    try {
        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection

        // Set up the connection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Authorization", "Bearer $authToken")
        connection.setRequestProperty("Accept", "application/json")

        // Get the response code
        val responseCode = connection.responseCode
        println("Response Code: $responseCode\n")

        // Read the response
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            reader.close()

            // Parse JSON response
            val gson = Gson()
            val smsListType = object : TypeToken<List<SmsItem>>() {}.type
            val smsList: List<SmsItem> = gson.fromJson(response.toString(), smsListType)

            // Display formatted output
            println("=== SMS Messages (${smsList.size} items) ===")
            smsList.forEachIndexed { index, sms ->
                println("${sms.smS_ID}")
                println("${sms.smS_text}")
                println("${sms.scanResult}")
                println("${sms.checkDate}")
                println("${sms.identityUserID}")
                println("----------------------------------------")
            }
        } else {
            val errorStream = BufferedReader(InputStreamReader(connection.errorStream))
            val errorResponse = StringBuilder()
            var line: String?

            while (errorStream.readLine().also { line = it } != null) {
                errorResponse.append(line)
            }
            errorStream.close()

            println("Error Response:")
            println(errorResponse.toString())
        }

        connection.disconnect()
    } catch (e: Exception) {
        println("An error occurred: ${e.message}")
        e.printStackTrace()
    }
}