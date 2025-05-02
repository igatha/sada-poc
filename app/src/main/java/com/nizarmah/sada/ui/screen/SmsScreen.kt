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
import com.nizarmah.sada.viewmodel.SmsViewModel
import java.time.format.DateTimeFormatter

@Composable
fun SmsScreen() {
    val viewModel: SmsViewModel = viewModel()
    val listState = rememberLazyListState()

    val permissions = viewModel.permissions
    val permissionsGranted by viewModel.permissionsGranted.collectAsState()

    val deviceIp by viewModel.deviceIp.collectAsState()
    val towerIp by viewModel.towerIp.collectAsState()
    val towerOnline by viewModel.towerOnline.collectAsState()

    val username by viewModel.username.collectAsState()
    val messageText by viewModel.messageText.collectAsState()
    val sendError by viewModel.sendError.collectAsState()

    val messages by viewModel.messages.collectAsState()

    // Auto-scroll to bottom when messages change
    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        bottomBar = {
            PermissionsHandler(
                permissions = permissions,
                permissionsGranted = permissionsGranted,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Title and Description
            Text(
                text = "SMS",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Send and receive messages while connected to a tower.",
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Connection Info
            Text(
                text = "Device IP: $deviceIp",
                fontSize = 14.sp
            )
            Text(
                text = "Gateway IP: $towerIp",
                fontSize = 14.sp
            )
            Text(
                text = "Tower Status: ${if (towerOnline) "✅ Online" else "❌ Offline"}",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Username Input
            OutlinedTextField(
                value = username,
                onValueChange = { viewModel.updateUsername(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter your username...") },
                singleLine = true,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Message Input Area
            Column {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { viewModel.updateMessageText(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Type your message...") },
                    singleLine = true,
                    maxLines = 1
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (sendError) {
                        Text(
                            text = "Failed to send message",
                            fontSize = 12.sp,
                            color = Color.Red,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Text(
                        text = "${messageText.length}/280",
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.sendMessage() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Inbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Inbox",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = { viewModel.refreshInbox() }
                ) {
                    Text("Refresh")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Messages List
            LazyColumn(
                state = listState
            ) {
                items(messages) { message ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "From: ${message.username}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = message.content,
                            fontSize = 14.sp
                        )
                        Text(
                            text = message.timestamp.format(
                                DateTimeFormatter.ofPattern("MMM dd, HH:mm")
                            ),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SmsScreenPreview() {
    SmsScreen()
}
