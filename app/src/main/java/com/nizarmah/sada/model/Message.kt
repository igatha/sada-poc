package com.nizarmah.sada.model

import android.content.ContentValues
import android.database.Cursor

import com.google.gson.annotations.SerializedName

// Message represents the messages received by the TowerServer.
data class Message(
    @SerializedName(MESSAGE_ID)
    val messageId: String,
    @SerializedName(FROM_ID)
    val from: String,
    @SerializedName(TO_ID)
    val to: String?,
    val content: String,
    val timestamp: String
) {
    // ToContentValues converts a Message to a ContentValues.
    fun toContentValues(): ContentValues = ContentValues().apply {
        put(MESSAGE_ID, messageId)
        put(FROM_ID,    from)
        put(TO_ID,      to)
        put(CONTENT,    content)
        put(TIMESTAMP,  timestamp)
    }

    companion object {
        const val MESSAGE_ID = "message_id"
        const val FROM_ID = "from_id"
        const val TO_ID = "to_id"
        const val CONTENT = "content"
        const val TIMESTAMP = "timestamp"

        // FromCursor converts a Cursor to a Message.
        fun fromCursor(c: Cursor): Message = Message(
            c.getString(c.getColumnIndexOrThrow(MESSAGE_ID)),
            c.getString(c.getColumnIndexOrThrow(FROM_ID)),
            c.getString(c.getColumnIndexOrThrow(TO_ID)),
            c.getString(c.getColumnIndexOrThrow(CONTENT)),
            c.getString(c.getColumnIndexOrThrow(TIMESTAMP))
        )
    }
}
