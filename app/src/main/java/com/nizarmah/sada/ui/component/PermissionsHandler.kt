package com.nizarmah.sada.ui.component

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

import com.nizarmah.sada.util.PermissionsManager

@Composable
fun PermissionsHandler(
    permissions: Array<String>,
    permissionsGranted: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Launcher to request multiple permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            PermissionsManager.refreshPermissions(context)
        }
    )

    // Request permissions on first launch
    LaunchedEffect(Unit) {
        if (!permissionsGranted) {
            permissionLauncher.launch(permissions)
        }
    }

    Column(
        modifier = modifier
    ) {
        if (!permissionsGranted) {
            PersistentBanner(
                message = "Permissions are missing.",
                actionLabel = "Settings",
                onActionClick = {
                    // Open app settings
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }

                    context.startActivity(intent)
                }
            )
        }
    }
}
