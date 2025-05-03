package com.example.project1.SMS.SMSData

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SMSRepository(context: Context) {
    private val database = SMSDatabase.getInstance(context)

    suspend fun saveSMS(message: String, isSafe: Boolean?) = withContext(Dispatchers.IO) {
        val db = database.writableDatabase
        val values = ContentValues().apply {
            put(SMSDatabase.COLUMN_MESSAGE, message)
            // Store -1 for null (no connection)
            put(
                SMSDatabase.COLUMN_IS_SAFE, when (isSafe) {
                    true -> 1
                    false -> 0
                    null -> -1
                }
            )
            put(SMSDatabase.COLUMN_TIMESTAMP, System.currentTimeMillis())
        }

        db.insert(SMSDatabase.TABLE_SMS_HISTORY, null, values)
    }

    suspend fun getRecentSMS(limit: Int = 10): List<SMSHistoryItem> = withContext(Dispatchers.IO) {
        val messages = mutableListOf<SMSHistoryItem>()
        val db = database.readableDatabase

        var cursor: Cursor? = null
        try {
            cursor = db.query(
                SMSDatabase.TABLE_SMS_HISTORY,
                null,
                null,
                null,
                null,
                null,
                "${SMSDatabase.COLUMN_TIMESTAMP} DESC",
                limit.toString()
            )

            while (cursor.moveToNext()) {
                messages.add(createSMSHistoryItemFromCursor(cursor))
            }
        } finally {
            cursor?.close()
        }

        messages
    }

    private fun createSMSHistoryItemFromCursor(cursor: Cursor): SMSHistoryItem {
        val idIndex = cursor.getColumnIndex(SMSDatabase.COLUMN_ID)
        val messageIndex = cursor.getColumnIndex(SMSDatabase.COLUMN_MESSAGE)
        val isSafeIndex = cursor.getColumnIndex(SMSDatabase.COLUMN_IS_SAFE)
        val timestampIndex = cursor.getColumnIndex(SMSDatabase.COLUMN_TIMESTAMP)

        val safetyValue = cursor.getInt(isSafeIndex)
        val isSafe = when (safetyValue) {
            1 -> true
            0 -> false
            else -> null // -1 represents no connection
        }

        return SMSHistoryItem(
            id = cursor.getLong(idIndex),
            message = cursor.getString(messageIndex),
            isSafe = isSafe,
            timestamp = cursor.getLong(timestampIndex)
        )
    }

    suspend fun clearAll() = withContext(Dispatchers.IO) {
        val db = database.writableDatabase
        db.delete(SMSDatabase.TABLE_SMS_HISTORY, null, null)
    }
    
}