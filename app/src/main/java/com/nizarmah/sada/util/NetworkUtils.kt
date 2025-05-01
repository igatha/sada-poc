package com.nizarmah.sada.util

import java.net.Inet4Address
import java.net.NetworkInterface

// NetworkUtils is just a helper class for network related operations.
object NetworkUtils {

    // getLocalIpAddress returns the local IP address of the device.
    fun getLocalIpAddress(): String? {
        return try {
            NetworkInterface.getNetworkInterfaces().toList().flatMap { it.inetAddresses.toList() }
                .firstOrNull { !it.isLoopbackAddress && it is Inet4Address }
                ?.hostAddress
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
