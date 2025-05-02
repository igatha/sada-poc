package com.nizarmah.sada.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import com.google.gson.Gson

import com.nizarmah.sada.model.Message
import com.nizarmah.sada.util.NetworkUtils
import com.nizarmah.sada.util.PermissionsHelper
import com.nizarmah.sada.util.PermissionsManager
import java.util.UUID

private const val TOWER_PORT = 8080

class SmsViewModel(app: Application) : AndroidViewModel(app) {

    private val gson = Gson()

    val permissions = PermissionsHelper.getSmsPermissions()
    val permissionsGranted = PermissionsManager.smsPermitted

    private val _deviceIp = MutableStateFlow<String>("")
    val deviceIp: StateFlow<String> = _deviceIp

    private val _towerIp = MutableStateFlow<String>("")
    val towerIp: StateFlow<String> = _towerIp

    private val _towerOnline = MutableStateFlow<Boolean>(false)
    val towerOnline: StateFlow<Boolean> = _towerOnline

    private val _sendError = MutableStateFlow(false)
    val sendError: StateFlow<Boolean> = _sendError.asStateFlow()

    private val _messageText = MutableStateFlow("")
    val messageText: StateFlow<String> = _messageText.asStateFlow()

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    init {
        viewModelScope.launch {
            _deviceIp.value = NetworkUtils.getLocalIpAddress().orEmpty()
            _towerIp.value = NetworkUtils.getGatewayIpAddress(app).orEmpty()
            _towerOnline.value = isTowerOnline()
        }
    }

    private suspend fun isTowerOnline(): Boolean = withContext(Dispatchers.IO) {
        if (_towerIp.value.isEmpty()) {
            return@withContext false
        }

        try {
            val url = URL("http://${_towerIp.value}:$TOWER_PORT/tower/healthcheck")

            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            connection.responseCode == HttpURLConnection.HTTP_OK
        } catch (e: Exception) {
            Log.e("SmsViewModel", "Error checking tower online", e)

            false
        }
    }

    fun updateUsername(text: String) {
        _username.value = text
    }

    fun updateMessageText(text: String) {
        if (text.length <= 280) {
            _messageText.value = text
        }
    }

    fun sendMessage() {
        val message = Message(
            messageId = UUID.randomUUID().toString(),
            username = _username.value,
            content = _messageText.value,
            timestamp = System.currentTimeMillis().toString()
        )

        viewModelScope.launch {
            var success = false
            try {
                success = sendMessageToServer(message)
            } catch (e: Exception) {
                Log.e("SmsViewModel", "Error sending message", e)
            }

            _sendError.value = !success
            if (success) {
                _messageText.value = ""
                refreshInbox()
            }
        }
    }

    private suspend fun sendMessageToServer(message: Message): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("http://${_towerIp.value}:$TOWER_PORT/send")

            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"

            val jsonMessage = gson.toJson(message)

            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            conn.outputStream.use { os ->
                os.write(jsonMessage.toByteArray())
            }

            Log.d("SmsViewModel", "Message (${message.content}) response code: ${conn.responseCode}")

            return@withContext conn.responseCode == HttpURLConnection.HTTP_OK
        } catch (e: Exception) {
            Log.e("SmsViewModel", "Failed to send message", e)

            return@withContext false
        }
    }

    fun refreshInbox() {
        viewModelScope.launch {
            var newMessages = emptyList<Message>()
            try {
                newMessages = fetchInbox()
            } catch (e: Exception) {
                Log.e("SmsViewModel", "Error refreshing inbox", e)
            }

            _messages.value = newMessages
        }
    }

    private suspend fun fetchInbox(): List<Message> = withContext(Dispatchers.IO) {
        try {
            val url = URL("http://${_towerIp.value}:$TOWER_PORT/inbox")

            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connect()

            if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                Log.e("SmsViewModel", "Failed to fetch inbox: ${conn.responseCode}")
                return@withContext emptyList()
            }

            val response = conn.inputStream.bufferedReader().use { it.readText() }

            Log.d("SmsViewModel", "Inbox response (${conn.responseCode}): $response")

            // Parse the response as a list of Message objects
            val type = object : com.google.gson.reflect.TypeToken<List<Message>>() {}.type
            return@withContext gson.fromJson<List<Message>>(response, type)
        } catch (e: Exception) {
            Log.e("SmsViewModel", "Error fetching inbox", e)

            return@withContext emptyList()
        }
    }
}