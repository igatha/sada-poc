package com.nizarmah.sada.ui.view


import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.nizarmah.sada.model.Message
import com.nizarmah.sada.ui.component.PermissionsHandler
import com.nizarmah.sada.util.PermissionsHelper
import java.util.UUID

@Composable
fun TowerView(
    towerUrl: String,
    messageCount: Int,
    messages: List<Message>,
    permissions: Array<String>,
    permissionsGranted: Boolean,
    onRefresh: () -> Unit
) {
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
            Text(
                text = "Tower",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Local messaging server synced with relays. " +
                       "Requires an open hotspot named SADA-[ID], eg. SADA-XYZ.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tower Info
            Text(
                text = "Tower URL: $towerUrl",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Message count: $messageCount",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Messages List
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Messages",
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
            LazyColumn {
                items(messages) { message ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = message.username,
                            style = MaterialTheme.typography.bodySmall,
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = message.content,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = message.messageId,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )

                                Text(
                                    text = message.timestamp,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TowerViewPreview() {
    TowerView(
        towerUrl = "http://192.168.1.100:8080",
        messageCount = 3,
        messages = listOf(
            Message(
                messageId = UUID.randomUUID().toString(),
                username = "user1",
                content = "Hello world!",
                timestamp = System.currentTimeMillis().toString()
            ),
            Message(
                messageId = UUID.randomUUID().toString(),
                username = "user2",
                content = "How are you?",
                timestamp = System.currentTimeMillis().toString()
            ),
        ),
        permissions = PermissionsHelper.getTowerPermissions(),
        permissionsGranted = true,
        onRefresh = {}
    )
}
