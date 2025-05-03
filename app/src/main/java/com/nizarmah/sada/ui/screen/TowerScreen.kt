package com.nizarmah.sada.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

import com.nizarmah.sada.ui.view.TowerView
import com.nizarmah.sada.viewmodel.TowerViewModel

@Composable
fun TowerScreen() {
    val viewModel: TowerViewModel = viewModel()

    val permissions = viewModel.permissions
    val permissionsGranted by viewModel.permissionsGranted.collectAsState()

    val towerUrl by viewModel.towerUrl.collectAsState()
    val messageCount by viewModel.messageCount.collectAsState()

    val messages by viewModel.messages.collectAsState()

    TowerView(
        towerUrl = towerUrl,
        messageCount = messageCount,
        messages = messages,
        permissions = permissions,
        permissionsGranted = permissionsGranted,
        onRefresh = { viewModel.refreshMessages() }
    )
}