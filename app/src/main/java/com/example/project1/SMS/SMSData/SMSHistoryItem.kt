package com.example.project1.SMS.SMSData

data class SMSHistoryItem(
    val id: Long = 0,
    val message: String,
    val isSafe: Boolean?, // Nullable to represent "No connection" state
    val timestamp: Long = System.currentTimeMillis()
)