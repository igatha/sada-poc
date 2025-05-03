package com.nizarmah.sada.util

import android.Manifest
import android.os.Build

object PermissionsHelper {

    fun getSmsPermissions(): Array<String> {
        var permissions = getWifiStatePermissions()

        permissions += getInternetPermissions()

        return permissions
    }

    fun getRelayPermissions():Array<String> {
        var permissions = getInternetPermissions()

        permissions += getWifiStatePermissions()

        permissions += getWifiHoppingPermissions()

        return permissions
    }

    fun getTowerPermissions(): Array<String> {
        var permissions = getInternetPermissions()

        return permissions
    }

    fun getInternetPermissions(): Array<String> {
        var permissions = arrayOf(
            Manifest.permission.INTERNET
        )

        return permissions
    }

    fun getWifiStatePermissions(): Array<String> {
       var permissions = arrayOf(
           Manifest.permission.ACCESS_WIFI_STATE,
       )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions += arrayOf(
                Manifest.permission.ACCESS_NETWORK_STATE,
            )
        }

        return permissions
    }

    fun getWifiHoppingPermissions(): Array<String> {
        var permissions = getWifiStatePermissions()

        permissions += arrayOf(
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions += arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

        return permissions
    }
}
