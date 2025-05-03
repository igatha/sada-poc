package com.nizarmah.sada

import android.app.Application

import com.nizarmah.sada.util.PermissionsManager

class SadaApp : Application() {
    override fun onCreate() {
        super.onCreate()

        PermissionsManager.init(this)
    }
}