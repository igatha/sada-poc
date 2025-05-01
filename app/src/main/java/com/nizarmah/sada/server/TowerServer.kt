package com.nizarmah.sada.server

import android.util.Log

import com.google.gson.Gson
import fi.iki.elonen.*

import com.nizarmah.sada.model.Message
import com.nizarmah.sada.store.MessageStore

// TowerServer handles requests over a local network.
class TowerServer(
    port: Int,
    private val store: MessageStore
) : NanoHTTPD(port) {

    private val gson = Gson()

    // Serve is called when a request is received.
    override fun serve(session: IHTTPSession): Response {
        return try {
            when (session.uri) {
                "/send" -> handleSend(session)
                "/inbox" -> handleInbox(session)

                else -> newFixedLengthResponse(
                    Response.Status.NOT_FOUND,
                    "application/json",
                    """{"error":"Not Found"}"""
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()

            newFixedLengthResponse(
                Response.Status.INTERNAL_ERROR,
                "application/json",
                """{"error":"${e.message}"}"""
            )
        }
    }

    // HandleSend sends a message across the network.
    private fun handleSend(session: IHTTPSession): Response {
        // Ensure the request is a POST request.
        if (session.method != Method.POST) {
            return newFixedLengthResponse(
                Response.Status.METHOD_NOT_ALLOWED,
                "application/json",
                """{"error":"Method not allowed"}"""
            )
        }

        // Prepare the request body.
        val files = HashMap<String, String>()
        session.parseBody(files)

        // Retrieve the post data.
        val body = files["postData"]
        if (body == null) {
            return newFixedLengthResponse(
                Response.Status.BAD_REQUEST,
                "application/json",
                """{"error":"Bad Request"}"""
            )
        }

        // Create message.
        val msg = gson.fromJson(body, Message::class.java)

        Log.d("TowerServer", "Received message: $msg")

        store.add(msg)

        return newFixedLengthResponse(
            Response.Status.OK,
            "application/json",
            """{"status":"ok"}"""
        )
    }

    // HandleInbox returns the messages sent since a given timestamp.
    private fun handleInbox(session: IHTTPSession): Response {
        // If the request is a GET request, return the messages.
        if (session.method != Method.GET) {
            return newFixedLengthResponse(
                Response.Status.METHOD_NOT_ALLOWED,
                "application/json",
                """{"error":"Method not allowed"}"""
            )
        }

        // Get the messages since the given timestamp.
        val since = session.parameters["since"]?.firstOrNull()
        val result = store.getSince(since)
        val json = gson.toJson(result)

        return newFixedLengthResponse(
            Response.Status.OK,
            "application/json",
            json
        )
    }

    // StartTower starts the server.
    fun startTower() = this.start(SOCKET_READ_TIMEOUT, false)

    // StopTower stops the server.
    fun stopTower() = this.stop()
}
