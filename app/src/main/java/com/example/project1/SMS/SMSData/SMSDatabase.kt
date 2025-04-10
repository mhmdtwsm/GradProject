package com.example.project1.SMS.SMSData

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SMSDatabase private constructor(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_NAME = "sms_history.db"
        private const val DATABASE_VERSION = 1

        // Table and column names
        const val TABLE_SMS_HISTORY = "sms_history"
        const val COLUMN_ID = "id"
        const val COLUMN_MESSAGE = "message"
        const val COLUMN_IS_SAFE = "is_safe"
        const val COLUMN_TIMESTAMP = "timestamp"

        @Volatile
        private var INSTANCE: SMSDatabase? = null

        fun getInstance(context: Context): SMSDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SMSDatabase(context).also { INSTANCE = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_SMS_HISTORY (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_MESSAGE TEXT NOT NULL,
                $COLUMN_IS_SAFE INTEGER NOT NULL,
                $COLUMN_TIMESTAMP INTEGER NOT NULL
            )
        """.trimIndent()

        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SMS_HISTORY")
        onCreate(db)
    }
}