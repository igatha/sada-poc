package com.nizarmah.sada.store

import com.nizarmah.sada.model.Message

// MessageStore is a simple in-memory store for messages.
class MessageStore {
    private val messages = mutableListOf<Message>()

    // Add adds a message to the store if it doesn't already exist.
    fun add(message: Message) {
        if (messages.none { it.messageId == message.messageId }) {
            messages.add(message)
        }
    }

    // GetSince returns all messages after the given timestamp.
    fun getSince(timestamp: String?): List<Message> {
        return if (timestamp == null) {
            messages
        } else {
            messages.filter { it.timestamp > timestamp }
        }
    }
}
