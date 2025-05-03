package com.nizarmah.sada.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import com.nizarmah.sada.model.Message

class MessageDbHelper(
    ctx: Context,
    dbName: String,
    dbVersion: Int,
    val tableName: String
) : SQLiteOpenHelper(ctx, dbName, null, dbVersion) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $tableName (
                ${Message.MESSAGE_ID} TEXT PRIMARY KEY,
                ${Message.USERNAME}    TEXT,
                ${Message.CONTENT}    TEXT,
                ${Message.TIMESTAMP}  TEXT
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldV: Int, newV: Int) {
      /* none */
    }
}
