package com.example.project1.URL.URLData

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class URLDatabase private constructor(context: Context) : SQLiteOpenHelper(
    context.applicationContext, // Use application context to prevent leaks
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_NAME = "url_history.db"
        private const val DATABASE_VERSION = 1

        // Table and column names
        const val TABLE_URL_HISTORY = "url_history"
        const val COLUMN_ID = "id"
        const val COLUMN_URL = "url"
        const val COLUMN_IS_SAFE = "is_safe"
        const val COLUMN_TIMESTAMP = "timestamp"

        @Volatile
        private var INSTANCE: URLDatabase? = null

        fun getInstance(context: Context): URLDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: URLDatabase(context).also { INSTANCE = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_URL_HISTORY (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_URL TEXT NOT NULL,
                $COLUMN_IS_SAFE INTEGER NOT NULL,
                $COLUMN_TIMESTAMP INTEGER NOT NULL
            )
        """.trimIndent()

        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_URL_HISTORY")
        onCreate(db)
    }
}

