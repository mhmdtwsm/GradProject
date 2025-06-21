package com.example.project1.URL.URLData

data class URLHistoryItem(
    val id: Long = 0,
    val url: String,
    val isSafe: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)