package com.nizarmah.sada

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.nizarmah.sada.util.PermissionsManager

class RelayActivity : ComponentActivity() {

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
    }

    override fun onResume() {
        super.onResume()
        PermissionsManager.refreshPermissions(this)
    }
}
