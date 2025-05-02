package com.nizarmah.sada.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nizarmah.sada.ui.component.PermissionsHandler
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
