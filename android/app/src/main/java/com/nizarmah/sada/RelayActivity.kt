package com.nizarmah.sada

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import com.nizarmah.sada.ui.screen.RelayScreen
import com.nizarmah.sada.ui.theme.SadaTheme
import com.nizarmah.sada.util.PermissionsManager

class RelayActivity : ComponentActivity() {

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        setContent {
            SadaTheme {
                RelayScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        PermissionsManager.refreshPermissions(this)
    }
}
