package com.nizarmah.sada.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nizarmah.sada.sync.RelayManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

import com.nizarmah.sada.util.PermissionsHelper
import com.nizarmah.sada.util.PermissionsManager

enum class RelayState {
    IDLE,
    PULLING,
    PUSHING
}

class RelayViewModel(app: Application) : AndroidViewModel(app) {
    val permissions = PermissionsHelper.getRelayPermissions()
    val permissionsGranted = PermissionsManager.relayPermitted

    private val _state = MutableStateFlow(RelayState.IDLE)
    val state: StateFlow<RelayState> = _state.asStateFlow()

    private val _pullUrl = MutableStateFlow("")
    val pullUrl: StateFlow<String> = _pullUrl.asStateFlow()

    private val _pushUrl = MutableStateFlow("")
    val pushUrl: StateFlow<String> = _pushUrl.asStateFlow()

    private val buffer = File.createTempFile("relay", "json")

    fun updatePullUrl(url: String) {
        _pullUrl.value = url
    }

    fun updatePushUrl(url: String) {
        _pushUrl.value = url
    }

    fun pull() {
        val url = _pullUrl.value.trim()
        if (url.isBlank()) return

        viewModelScope.launch {
            _state.value = RelayState.PULLING

            RelayManager.pullDump(url, buffer)

            _state.value = RelayState.IDLE
        }
    }

    fun push() {
        val url = _pushUrl.value.trim()
        if (url.isBlank()) {
            Log.e("RelayViewModel", "Push URL cannot be empty")
            return
        }

        if (buffer == null) {
            Log.e("RelayViewModel", "No data to push. Pull data first.")
            return
        }

        viewModelScope.launch {
            _state.value = RelayState.PUSHING

            RelayManager.pushDump(url, buffer)

            _state.value = RelayState.IDLE
        }
    }
}
