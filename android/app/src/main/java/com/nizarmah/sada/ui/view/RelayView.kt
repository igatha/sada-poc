package com.nizarmah.sada.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nizarmah.sada.ui.component.PermissionsHandler

import com.nizarmah.sada.viewmodel.RelayState
import com.nizarmah.sada.util.PermissionsHelper

@Composable
fun RelayView(
    state: RelayState,
    pullUrl: String,
    pushUrl: String,
    permissions: Array<String>,
    permissionsGranted: Boolean,
    onPullUrlChange: (String) -> Unit,
    onPushUrlChange: (String) -> Unit,
    onPull: () -> Unit,
    onPush: () -> Unit,
    modifier: Modifier = Modifier
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
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Title and Description
            Text(
                text = "Relay",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Message sync between towers, offline or online. " +
                       "Requires manually switching to the tower's WiFi.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Pull Section
            Text(
                text = "Pull Data",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = pullUrl,
                onValueChange = onPullUrlChange,
                placeholder = {
                    Text(
                        text = "Pull URL",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                maxLines = 1,
                textStyle = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onPull,
                enabled = state == RelayState.IDLE && pullUrl.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Pull",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Push Section
            Text(
                text = "Push Data",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = pushUrl,
                onValueChange = onPushUrlChange,
                placeholder = {
                    Text(
                        text = "Push URL",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                maxLines = 1,
                textStyle = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onPush,
                enabled = state == RelayState.IDLE && pushUrl.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Push",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            // State Indicator
            when (state) {
                RelayState.PULLING -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pulling data...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                RelayState.PUSHING -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pushing data...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                else -> {}
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RelayViewPreview() {
    RelayView(
        state = RelayState.IDLE,
        pullUrl = "http://example.com/pull",
        pushUrl = "http://example.com/push",
        permissions = PermissionsHelper.getRelayPermissions(),
        permissionsGranted = true,
        onPullUrlChange = {},
        onPushUrlChange = {},
        onPull = {},
        onPush = {}
    )
}
