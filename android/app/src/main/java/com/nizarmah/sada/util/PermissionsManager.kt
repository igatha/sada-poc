package com.nizarmah.sada.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

object PermissionsManager {
    private val _internetPermitted = MutableStateFlow(false)
    val internetPermitted: StateFlow<Boolean> = _internetPermitted.asStateFlow()

    private val _wifiStatePermitted = MutableStateFlow(false)
    val wifiStatePermitted: StateFlow<Boolean> = _wifiStatePermitted.asStateFlow()

    private val _wifiHoppingPermitted = MutableStateFlow(false)
    val wifiHoppingPermitted: StateFlow<Boolean> = _wifiHoppingPermitted.asStateFlow()

    private val _smsPermitted = MutableStateFlow(false)
    val smsPermitted: StateFlow<Boolean> = _smsPermitted

    private val _relayPermitted = MutableStateFlow(false)
    val relayPermitted: StateFlow<Boolean> = _relayPermitted

    private val _towerPermitted = MutableStateFlow(false)
    val towerPermitted: StateFlow<Boolean> = _towerPermitted

    @OptIn(DelicateCoroutinesApi::class)
    val permissionsGranted: StateFlow<Boolean> = combine(
        smsPermitted,
        relayPermitted,
        towerPermitted
    ) { tower, relay, sms ->
        tower && relay && sms
    }.stateIn(GlobalScope, SharingStarted.Eagerly, false)

    // Init initializes by checking current permissions.
    fun init(ctx: Context) {
        refreshPermissions(ctx)
    }

    // RefreshPermissions updates the current permission states.
    fun refreshPermissions(ctx: Context) {
        _internetPermitted.value = hasPermissions(ctx, PermissionsHelper.getInternetPermissions())
        _wifiStatePermitted.value = hasPermissions(ctx, PermissionsHelper.getWifiStatePermissions())
        _wifiHoppingPermitted.value = hasPermissions(ctx, PermissionsHelper.getWifiHoppingPermissions())

        _smsPermitted.value = hasPermissions(ctx, PermissionsHelper.getSmsPermissions())
        _relayPermitted.value = hasPermissions(ctx, PermissionsHelper.getRelayPermissions())
        _towerPermitted.value = hasPermissions(ctx, PermissionsHelper.getTowerPermissions())
    }

    // HasPermissions checks if permissions were granted.
    private fun hasPermissions(ctx: Context, permissions: Array<String>): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(ctx, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
}