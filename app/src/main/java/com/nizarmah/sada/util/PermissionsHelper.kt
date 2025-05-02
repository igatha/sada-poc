package com.nizarmah.sada.util

import android.Manifest
import android.os.Build

object PermissionsHelper {
    fun getTowerPermissions(): Array<String> {
        var permissions = arrayOf(
            // Tower server for requests and responses.
            Manifest.permission.INTERNET
        )

        return permissions
    }

    fun getSmsPermissions(): Array<String> {
        var permissions = arrayOf(
            // Tower server requests.
            Manifest.permission.INTERNET,
            // Tower gateway ip retrieval.
            Manifest.permission.ACCESS_WIFI_STATE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions += arrayOf(
                // Network util to get tower gateway ip.
                Manifest.permission.ACCESS_NETWORK_STATE,
                // Location settings to access network info.
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

        return permissions
    }
}
