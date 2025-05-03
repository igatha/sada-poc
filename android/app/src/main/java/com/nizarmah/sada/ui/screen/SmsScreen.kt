package com.nizarmah.sada.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nizarmah.sada.ui.view.SmsView
import com.nizarmah.sada.viewmodel.SmsViewModel

@Composable
fun SmsScreen() {
    val viewModel: SmsViewModel = viewModel()

    val permissions = viewModel.permissions
    val permissionsGranted by viewModel.permissionsGranted.collectAsState()

    val deviceIp by viewModel.deviceIp.collectAsState()
    val towerIp by viewModel.towerIp.collectAsState()
    val towerOnline by viewModel.towerOnline.collectAsState()

    val username by viewModel.username.collectAsState()
    val messageText by viewModel.messageText.collectAsState()
    val sendError by viewModel.sendError.collectAsState()

    val messages by viewModel.messages.collectAsState()

    SmsView(
        deviceIp = deviceIp,
        towerIp = towerIp,
        towerOnline = towerOnline,
        username = username,
        messageText = messageText,
        sendError = sendError,
        messages = messages,
        permissions = permissions,
        permissionsGranted = permissionsGranted,
        onSend = { viewModel.sendMessage() },
        onRefresh = { viewModel.refreshMessages() },
        onUsernameChange = { viewModel.updateUsername(it) },
        onMessageTextChange = { viewModel.updateMessageText(it) }
    )
}
