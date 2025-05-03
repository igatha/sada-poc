package com.nizarmah.sada.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

import com.nizarmah.sada.ui.view.RelayView
import com.nizarmah.sada.viewmodel.RelayViewModel

@Composable
fun RelayScreen() {

    val viewModel: RelayViewModel = viewModel()

    val permissions = viewModel.permissions
    val permissionsGranted by viewModel.permissionsGranted.collectAsState()

    val state by viewModel.state.collectAsState()

    val pullUrl by viewModel.pullUrl.collectAsState()
    val pushUrl by viewModel.pushUrl.collectAsState()

    RelayView(
        state = state,
        pullUrl = pullUrl,
        pushUrl = pushUrl,
        permissions = permissions,
        permissionsGranted = permissionsGranted,
        onPullUrlChange = { viewModel.updatePullUrl(it) },
        onPushUrlChange = { viewModel.updatePushUrl(it) },
        onPull = { viewModel.pull() },
        onPush = { viewModel.push() }
    )
}
