package com.example.project1.URL.URLData

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class URLRepository(context: Context) {
    // Use singleton database instance
    private val database = URLDatabase.getInstance(context)

    suspend fun saveUrl(url: String, isSafe: Boolean) = withContext(Dispatchers.IO) {
        val db = database.writableDatabase
        val values = ContentValues().apply {
            put(URLDatabase.COLUMN_URL, url)
            put(URLDatabase.COLUMN_IS_SAFE, if (isSafe) 1 else 0)
            put(URLDatabase.COLUMN_TIMESTAMP, System.currentTimeMillis())
        }

        db.insert(URLDatabase.TABLE_URL_HISTORY, null, values)
        // Don't close the database here
    }

    suspend fun getRecentUrls(limit: Int = 12): List<URLHistoryItem> = withContext(Dispatchers.IO) {
        val urls = mutableListOf<URLHistoryItem>()
        val db = database.readableDatabase

        var cursor: Cursor? = null
        try {
            cursor = db.query(
                URLDatabase.TABLE_URL_HISTORY,
                null,
                null,
                null,
                null,
                null,
                "${URLDatabase.COLUMN_TIMESTAMP} DESC",
                limit.toString()
            )

            while (cursor.moveToNext()) {
                urls.add(createUrlHistoryItemFromCursor(cursor))
            }
        } finally {
            cursor?.close()
            // Don't close the database here
        }

        urls
    }

    private fun createUrlHistoryItemFromCursor(cursor: Cursor): URLHistoryItem {
        val idIndex = cursor.getColumnIndex(URLDatabase.COLUMN_ID)
        val urlIndex = cursor.getColumnIndex(URLDatabase.COLUMN_URL)
        val isSafeIndex = cursor.getColumnIndex(URLDatabase.COLUMN_IS_SAFE)
        val timestampIndex = cursor.getColumnIndex(URLDatabase.COLUMN_TIMESTAMP)

        return URLHistoryItem(
            id = cursor.getLong(idIndex),
            url = cursor.getString(urlIndex),
            isSafe = cursor.getInt(isSafeIndex) == 1,
            timestamp = cursor.getLong(timestampIndex)
        )
    }
}

