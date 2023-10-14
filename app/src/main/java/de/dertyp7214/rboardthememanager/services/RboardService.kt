package de.dertyp7214.rboardthememanager.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import de.dertyp7214.rboard.IRboard
import de.dertyp7214.rboard.RboardTheme
import de.dertyp7214.rboardcomponents.core.preferences
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.core.install
import de.dertyp7214.rboardthememanager.core.toRboardTheme
import de.dertyp7214.rboardthememanager.utils.MagiskUtils.buildShell
import de.dertyp7214.rboardthememanager.utils.ThemeUtils as ThemeHelper

class RboardService : Service() {
    private val binder = object : IRboard.Stub() {
        override fun getAidlVersion(): Int {
            return Config.AIDL_VERSION
        }

        override fun getRboardThemes(): Array<String> {
            Config.useMagisk = preferences.getBoolean("useMagisk", false)

            buildShell()

            return ThemeHelper.loadThemesCtx(this@RboardService).map { it.name }.toTypedArray()
        }

        override fun getRboardTheme(name: String?): RboardTheme {
            Config.useMagisk = preferences.getBoolean("useMagisk", false)

            buildShell()

            return ThemeHelper.getThemeData(name?.let {
                if (it.endsWith(".zip")) it else "$it.zip"
            } ?: "", this@RboardService).toRboardTheme(this@RboardService)
        }

        override fun installRboardTheme(theme: RboardTheme?): Boolean {
            Config.useMagisk = preferences.getBoolean("useMagisk", false)

            buildShell()

            return theme?.install() ?: false
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
}