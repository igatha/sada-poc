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
    private val _towerPermitted = MutableStateFlow(false)
    val towerPermitted: StateFlow<Boolean> = _towerPermitted.asStateFlow()

    private val _smsPermitted = MutableStateFlow(false)
    val smsPermitted: StateFlow<Boolean> = _smsPermitted.asStateFlow()

    @OptIn(DelicateCoroutinesApi::class)
    val permissionsGranted: StateFlow<Boolean> = combine(
        towerPermitted,
        smsPermitted
    ) { tower, sms ->
        tower && sms
    }.stateIn(GlobalScope, SharingStarted.Eagerly, false)

    // Init initializes by checking current permissions.
    fun init(ctx: Context) {
        refreshPermissions(ctx)
    }

    // RefreshPermissions updates the current permission states.
    fun refreshPermissions(ctx: Context) {
        _towerPermitted.value = hasPermissions(ctx, PermissionsHelper.getTowerPermissions())
        _smsPermitted.value = hasPermissions(ctx, PermissionsHelper.getSmsPermissions())
    }

    // HasPermissions checks if permissions were granted.
    private fun hasPermissions(ctx: Context, permissions: Array<String>): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(ctx, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
}