package com.nizarmah.sada.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import java.net.Inet4Address
import java.net.NetworkInterface
import java.nio.ByteOrder

// NetworkUtils is just a helper class for network related operations.
object NetworkUtils {

    // GetLocalIpAddress returns the local IP address of the device.
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

    // GetGatewayIpAddress returns the gateway IP address of the current network.
    fun getGatewayIpAddress(ctx: Context): String? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            doGetGatewayIp(ctx)
        } else {
            doGetGatewayIpLegacy(ctx)
        }

    // DoGetGatewayIp returns the gateway IP address of the current network for API >= 29.
    private fun doGetGatewayIp(ctx: Context): String? {
        val cm = ctx.getSystemService(ConnectivityManager::class.java) ?: return null
        val active = cm.activeNetwork ?: return null
        val lp = cm.getLinkProperties(active) ?: return null

        val route = lp.routes
            .firstOrNull {
                it.destination.toString() == "0.0.0.0/0"
                        && it.gateway is Inet4Address
            }

        return route?.gateway?.hostAddress
    }

    // DoGetGatewayIpLegacy returns the gateway IP address of the current network for API < 29.
    private fun doGetGatewayIpLegacy(ctx: Context): String? {
        val wifi = ctx.applicationContext.getSystemService(WifiManager::class.java)
            ?: return null

        val dhcp = wifi.dhcpInfo ?: return null

        // Convert little-endian int → dot-decimal string
        val raw = if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
            Integer.reverseBytes(dhcp.gateway)
        else dhcp.gateway

        return ((raw shr 24) and 0xFF).toString() + "." +
                ((raw shr 16) and 0xFF) + "." +
                ((raw shr 8)  and 0xFF) + "." +
                (raw and 0xFF)
    }
}
