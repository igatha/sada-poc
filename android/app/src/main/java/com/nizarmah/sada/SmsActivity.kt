package com.nizarmah.sada

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

import com.nizarmah.sada.ui.screen.SmsScreen
import com.nizarmah.sada.ui.theme.SadaTheme
import com.nizarmah.sada.util.PermissionsManager

class SmsActivity : ComponentActivity() {

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        setContent {
            SadaTheme {
                SmsScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        PermissionsManager.refreshPermissions(this)
    }
}

@Preview(showBackground = true)
@Composable
fun SmsActivityPreview() {
    SmsActivity()
}
