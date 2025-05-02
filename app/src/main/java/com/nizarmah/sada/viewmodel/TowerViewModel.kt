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
import com.google.gson.reflect.TypeToken

import com.nizarmah.sada.model.Message
import com.nizarmah.sada.util.NetworkUtils
import com.nizarmah.sada.util.PermissionsHelper
import com.nizarmah.sada.util.PermissionsManager
import com.nizarmah.sada.db.MessageDbHelper
import com.nizarmah.sada.store.MessageStoreSql
import com.nizarmah.sada.server.TowerServer

private const val TOWER_PORT = 8080
private const val DB_NAME = "sada_messages.db"
private const val TBL_NAME_MESSAGES = "messages"

class TowerViewModel(app: Application) : AndroidViewModel(app) {

    private val gson = Gson()

    private lateinit var messageDb: MessageDbHelper
    private lateinit var messageStore: MessageStoreSql
    private lateinit var tower: TowerServer

    val permissions = PermissionsHelper.getTowerPermissions()
    val permissionsGranted = PermissionsManager.towerPermitted

    private val _towerIp = MutableStateFlow<String>("")
    val towerIp: StateFlow<String> = _towerIp

    private val _messageCount = MutableStateFlow(0)
    val messageCount: StateFlow<Int> = _messageCount

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    init {
        viewModelScope.launch {
            setupTower()
            refreshMessages()
        }
    }

    private fun setupTower() {
        val ip = NetworkUtils.getLocalIpAddress()
        if (ip == null) {
            Log.e("TowerViewModel", "Failed to get local IP address")
            return
        }

        _towerIp.value = ip

        // Setup database
        messageDb = MessageDbHelper(getApplication(), DB_NAME, 1, TBL_NAME_MESSAGES)
        messageStore = MessageStoreSql(messageDb.writableDatabase, TBL_NAME_MESSAGES)

        // Start the tower
        tower = TowerServer(port = TOWER_PORT, messageStore)
        tower.startTower()

        Log.d("TowerViewModel", "Tower running at http://$ip:$TOWER_PORT")
    }

    override fun onCleared() {
        super.onCleared()

        tower.stopTower()
    }

    fun refreshMessages() {
        viewModelScope.launch {
            try {
                val newMessages = fetchMessages()
                _messages.value = newMessages
                _messageCount.value = newMessages.size
            } catch (e: Exception) {
                Log.e("TowerViewModel", "Error refreshing messages", e)
            }
        }
    }

    private suspend fun fetchMessages(): List<Message> = withContext(Dispatchers.IO) {
        try {
            val url = URL("http://${_towerIp.value}:$TOWER_PORT/inbox")

            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connect()

            if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                Log.e("TowerViewModel", "Failed to fetch messages: ${conn.responseCode}")
                return@withContext emptyList()
            }

            val response = conn.inputStream.bufferedReader().use { it.readText() }

            Log.d("TowerViewModel", "Messages response (${conn.responseCode}): $response")

            val type = object : TypeToken<List<Message>>() {}.type
            return@withContext gson.fromJson<List<Message>>(response, type)
        } catch (e: Exception) {
            Log.e("TowerViewModel", "Error fetching messages", e)
            return@withContext emptyList()
        }
    }
}