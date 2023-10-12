package de.dertyp7214.rboardthememanager.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import de.dertyp7214.rboard.IAppTheme
import de.dertyp7214.rboard.RboardTheme
import de.dertyp7214.rboardcomponents.utils.ThemeUtils
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.core.install
import de.dertyp7214.rboardthememanager.core.toRboardTheme
import de.dertyp7214.rboardthememanager.data.ThemeDataClass
import de.dertyp7214.rboardthememanager.utils.ThemeUtils as ThemeHelper

class AppThemeService : Service() {
    private val binder = object : IAppTheme.Stub() {
        override fun getAidlVersion(): Int {
            return Config.AIDL_VERSION
        }

        override fun getAppTheme(): String {
            return ThemeUtils.getStyleName(this@AppThemeService)
        }

        override fun getRboardThemes(): Array<String> {
            return ThemeHelper.loadThemes().map { it.name }.toTypedArray()
        }

        override fun getRboardTheme(name: String?): RboardTheme {
            return ThemeHelper.getThemeData(name ?: "").toRboardTheme()
        }

        override fun installRboardTheme(theme: RboardTheme?): Boolean {
            return theme?.install() ?: false
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
}