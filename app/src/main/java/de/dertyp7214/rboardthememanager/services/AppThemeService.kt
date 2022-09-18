package de.dertyp7214.rboardthememanager.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import de.dertyp7214.rboard.IAppTheme
import de.dertyp7214.rboardcomponents.utils.ThemeUtils

class AppThemeService : Service() {
    private val binder = object : IAppTheme.Stub() {
        override fun getAppTheme(): String {
            return ThemeUtils.getStyleName(this@AppThemeService)
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
}