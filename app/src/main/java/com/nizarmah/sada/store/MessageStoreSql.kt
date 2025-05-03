package com.nizarmah.sada.store

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE

import com.google.gson.Gson

import com.nizarmah.sada.model.Message

class MessageStoreSql(
    val db: SQLiteDatabase,
    val tableName: String
) {

    private val gson = Gson()

    // Add adds a message to the database, skipping duplicates.
    fun add(m: Message) =
        db.insertWithOnConflict(
            tableName, null, m.toContentValues(), CONFLICT_IGNORE)

    // All returns all messages in the database.
    fun all(): List<Message> =
        db.rawQuery("SELECT * FROM $tableName ORDER BY timestamp ASC", null).use { c ->
            buildList {
                while (c.moveToNext()) add(
                    Message.fromCursor(c)
                )
            }
        }

    // ImportDump imports a JSON dump into the database.
    fun importDump(json: String) {
        // Parse the response as a list of Message objects
        val type = object : com.google.gson.reflect.TypeToken<List<Message>>() {}.type
        val messages = gson.fromJson<List<Message>>(json, type)

        messages.forEach { add(it) }
    }
}
