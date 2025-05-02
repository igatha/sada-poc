package com.nizarmah.sada.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import kotlin.collections.isNotEmpty

import com.nizarmah.sada.model.Message
import com.nizarmah.sada.ui.component.PermissionsHandler
import java.util.UUID

@Composable
fun SmsView(
    deviceIp: String,
    towerIp: String,
    towerOnline: Boolean,
    username: String,
    messageText: String,
    sendError: Boolean,
    messages: List<Message>,
    permissions: Array<String>,
    permissionsGranted: Boolean,
    onSend: () -> Unit,
    onRefresh: () -> Unit,
    onUsernameChange: (String) -> Unit,
    onMessageTextChange: (String) -> Unit,
) {
    val listState = rememberLazyListState()

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
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Messaging service via towers. " +
                        "Requires an active WiFi connection to a tower, eg. SADA-XYZ.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Connection Info
            Text(
                text = "Device IP: $deviceIp",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Gateway IP: $towerIp",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Tower Status: ${if (towerOnline) "✅" else "❌"}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Username Input
            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Enter your username...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                singleLine = true,
                maxLines = 1,
                textStyle = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Message Input Area
            Column {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = onMessageTextChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Type your message...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    singleLine = true,
                    maxLines = 1,
                    textStyle = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (sendError) {
                        Text(
                            text = "Failed to send message",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Red,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Text(
                        text = "${messageText.length}/280",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSend,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Send",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Inbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Inbox",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = onRefresh
                ) {
                    Text(
                        text = "Refresh",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

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
                            text = message.username,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = message.content,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SmsViewPreview() {
    SmsView(
        deviceIp = "192.168.1.100",
        towerIp = "192.168.1.101",
        towerOnline = true,
        username = "John Doe",
        messageText = "Hello, world!",
        sendError = false,
        messages = listOf(
            Message(
                messageId = UUID.randomUUID().toString(),
                username = "John Doe",
                content = "Hello, world!",
                timestamp = System.currentTimeMillis().toString()
            )
        ),
        permissions = arrayOf("android.permission.INTERNET"),
        permissionsGranted = true,
        onSend = {},
        onRefresh = {},
        onUsernameChange = {},
        onMessageTextChange = {}
    )
}
