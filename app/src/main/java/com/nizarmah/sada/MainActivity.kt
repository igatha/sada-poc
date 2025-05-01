package com.nizarmah.sada

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity

import com.nizarmah.sada.server.TowerServer
import com.nizarmah.sada.store.MessageStore
import com.nizarmah.sada.util.NetworkUtils

// MainActivity is the entry-point for our app.
class MainActivity : ComponentActivity() {

    // MessageStore is needed by the Tower to store messages.
    private lateinit var messageStore: MessageStore
    // Tower is the server the handles local network requests.
    private lateinit var tower: TowerServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTower()
    }

    override fun onDestroy() {
        super.onDestroy()
        tower.stopTower()
    }

    // SetupTower starts the tower if the device has an IP.
    private fun setupTower() {
        // Ensure the device is on a network.
        // The Tower should be on a hotspot network, ideally.
        val ip = NetworkUtils.getLocalIpAddress()
        if (ip == null) {
            return
        }

        // Create the message store.
        messageStore = MessageStore()

        // Start the tower.
        tower = TowerServer(port=8080, messageStore)
        tower.startTower()

        Log.d("Tower", "Tower running at http://$ip:8080")
    }
}
