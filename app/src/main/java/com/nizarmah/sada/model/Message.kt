package com.nizarmah.sada.model

// Message represents the messages received by the TowerServer.
data class Message(
    val messageId: String,
    val from: String,
    val to: String?,
    val content: String,
    val timestamp: String
)
