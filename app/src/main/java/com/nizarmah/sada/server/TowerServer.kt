package com.nizarmah.sada.server

import android.util.Log

import com.google.gson.Gson
import fi.iki.elonen.*

import com.nizarmah.sada.model.Message
import com.nizarmah.sada.store.MessageStoreSql

// TowerServer handles requests over a local network.
class TowerServer(
    port: Int,
    private val store: MessageStoreSql,
    private val socketReadTimeout: Int = SOCKET_READ_TIMEOUT
) : NanoHTTPD(port) {

    private val gson = Gson()

    // StartTower starts the server.
    fun startTower() = this.start(socketReadTimeout, false)
    // StopTower stops the server.
    fun stopTower() = this.stop()

    // Serve is called when a request is received.
    override fun serve(s: IHTTPSession): Response {
        return try {
            when (s.uri) {
                // Messaging endpoints.
                "/send" -> handleSend(s)
                "/inbox" -> handleInbox(s)

                // Dump endpoints.
                "/dump/export" -> handleDumpExport(s)
                "/dump/import" -> handleDumpImport(s)

                // Healthcheck endpoints.
                "/tower/healthcheck" -> handleTowerHealthcheck(s)

                else -> notFound()
            }
        } catch (e: Exception) {
            err(e)
        }
    }

    /** Endpoints */

    // HandleSend sends a message across the network.
    private fun handleSend(s: IHTTPSession): Response {
        if (s.method != Method.POST) return badRequest()

        val msg = gson.fromJson(s.getBody(), Message::class.java)
        store.add(msg)

        return ok()
    }

    // HandleInbox returns a list of messages.
    private fun handleInbox(s: IHTTPSession): Response {
        if (s.method != Method.GET) return badRequest()

        return json(store.all())
    }

    // HandleDumpImport imports a dump of messages.
    private fun handleDumpImport(s: IHTTPSession): Response {
        if (s.method != Method.POST) return badRequest()

        store.importDump(s.getBody())

        return ok()
    }

    // HandleDumpExport exports a dump of messages.
    private fun handleDumpExport(s: IHTTPSession): Response {
        if (s.method != Method.GET) return badRequest()

        return json(store.exportDump())
    }

    // HandleTowerHealthcheck returns 200 to indicate tower is up.
    private fun handleTowerHealthcheck(s: IHTTPSession): Response {
        if (s.method != Method.GET) return badRequest()

        return ok()
    }

    /** Helpers */

    // Text returns a plain text response.
    private fun text(status: Response.IStatus,  t: String) = newFixedLengthResponse(
        status, "text/plain", t)
    // JSON returns a JSON response.
    private fun json(obj: Any) = newFixedLengthResponse(
        Response.Status.OK, "application/json", gson.toJson(obj))

    // Ok returns a 200 OK response.
    private fun ok() = text(Response.Status.OK, "ok")
    // NotFound returns a 404 NOT FOUND response.
    private fun notFound() = text(Response.Status.NOT_FOUND, "not found")
    // BadRequest returns a 400 BAD REQUEST response.
    private fun badRequest() = text(Response.Status.BAD_REQUEST, "bad request")

    // Err returns a 500 INTERNAL SERVER response.
    private fun err(e: Exception) : Response {
        val msg = e.message.orEmpty()

        Log.e("TowerServer", msg, e)

        return text(
            Response.Status.INTERNAL_ERROR,
            "500 internal server error ($msg)"
        )
    }

    // GetBody returns the body of the request.
    private fun IHTTPSession.getBody(): String {
        val files = HashMap<String, String>()
        this.parseBody(files)

        return files["postData"].orEmpty()
    }
}
