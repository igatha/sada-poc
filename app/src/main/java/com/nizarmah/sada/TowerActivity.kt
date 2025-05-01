package com.nizarmah.sada

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity

import com.nizarmah.sada.server.TowerServer
import com.nizarmah.sada.db.MessageDbHelper
import com.nizarmah.sada.store.MessageStoreSql
import com.nizarmah.sada.util.NetworkUtils

// Constants for the database.
private const val DB_NAME = "sada_messages.db"
private const val TBL_NAME_MESSAGES = "messages"

// Constants for the Tower server.
private const val TOWER_PORT = 8080

// TowerActivity is the entry-point for our app.
class TowerActivity : ComponentActivity() {

    // MessageStoreSql is needed by the Tower to store messages.
    private lateinit var messageDb: MessageDbHelper
    private lateinit var messageStore: MessageStoreSql
    // Tower is the server the handles local network requests.
    private lateinit var tower: TowerServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTower(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        tower.stopTower()
    }

    // SetupTower starts the tower if the device has an IP.
    private fun setupTower(ctx: Context): String {
        // Ensure the device is on a network.
        // The Tower should be on a hotspot network, ideally.
        val ip = NetworkUtils.getLocalIpAddress()
        if (ip == null) {
            return ""
        }

        messageDb = MessageDbHelper(ctx, DB_NAME, 1, TBL_NAME_MESSAGES)
        messageStore = MessageStoreSql(messageDb.writableDatabase, TBL_NAME_MESSAGES)

        // Start the tower.
        tower = TowerServer(port=TOWER_PORT, messageStore)
        tower.startTower()

        val url = "http://$ip:$TOWER_PORT"
        Log.d("Tower", "Tower running at $url")

        return url
    }
}
