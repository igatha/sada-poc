package com.nizarmah.sada

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable

import com.nizarmah.sada.ui.screen.TowerScreen
import com.nizarmah.sada.ui.theme.SadaTheme
import com.nizarmah.sada.util.PermissionsManager

class TowerActivity : ComponentActivity() {

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        setContent {
            SadaTheme {
                TowerScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        PermissionsManager.refreshPermissions(this)
    }
}
