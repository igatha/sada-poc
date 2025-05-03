package com.nizarmah.sada.sync

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

// RelayManager is used to sync dumps between towers.
object RelayManager {

    // We sync using full dumps (read all, write all).
    // See: https://softwareengineering.stackexchange.com/a/348894

    // PullDump pulls dump from a tower into a temporary file.
    suspend fun pullDump(url: String, buffer: File): Boolean = withContext(Dispatchers.IO) {
        try {
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connect()

            if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                Log.e("RelayManager", "Failed to pull dump from $url with response code: ${conn.responseCode}")
                return@withContext false
            }

            conn.inputStream.use { input ->
                buffer.writeBytes(input.readBytes())
            }

            return@withContext true
        } catch (e: Exception) {
            Log.e("RelayManager", "Error pulling dump from $url", e)
            return@withContext false
        }
    }

    // PushDump pushes dump to a tower from a temporary file.
    suspend fun pushDump(url: String, buffer: File): Boolean = withContext(Dispatchers.IO) {
        try {
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.requestMethod = "POST"

            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            conn.outputStream.use { os ->
                buffer.inputStream().use { input ->
                    input.copyTo(os)
                }
            }

            if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                Log.e("RelayManager", "Failed to push dump to $url with response code: ${conn.responseCode}")
                return@withContext false
            }

            return@withContext true
        } catch (e: Exception) {
            Log.e("RelayManager", "Error pushing dump to $url", e)
            return@withContext false
        }
    }
}
