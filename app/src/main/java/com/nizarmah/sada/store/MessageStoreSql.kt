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
        db.rawQuery("SELECT * FROM $tableName", null).use { c ->
            buildList {
                while (c.moveToNext()) add(
                    Message.fromCursor(c)
                )
            }
        }

    // ExportDump returns a NL-JSON dump of all messages in the database.
    fun exportDump(): String = buildString {
        all().forEach { append(gson.toJson(it)).append('\n') }
    }

    // ImportDump imports a NL-JSON dump into the database.
    fun importDump(nljson: String) =
        nljson.lineSequence().filter { it.isNotBlank() }.forEach {
            add(gson.fromJson(it, Message::class.java))
        }
}
