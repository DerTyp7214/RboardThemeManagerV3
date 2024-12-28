package de.dertyp7214.rboardthememanager

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.os.LocaleListCompat
import androidx.preference.PreferenceManager
import com.dertyp7214.logs.helpers.Logger
import com.downloader.PRDownloader
import de.dertyp7214.colorutilsc.ColorUtilsC
import de.dertyp7214.rboardcomponents.utils.ThemeUtils
import de.dertyp7214.rboardcomponents.utils.doInBackground
import de.dertyp7214.rboardthememanager.core.getAttr
import de.dertyp7214.rboardthememanager.core.hasRoot
import de.dertyp7214.rboardthememanager.core.isReachable
import de.dertyp7214.rboardthememanager.utils.GboardUtils
import de.dertyp7214.rboardthememanager.utils.MagiskUtils
import java.net.URL

class Application : Application() {

    init {
        ColorUtilsC.init()
    }

    companion object {
        var context: Application? = null
            private set

        var uiHandler: Handler? = null
            private set

        fun getTopActivity(context: Context? = this.context): Activity? {
            return if (context is ContextWrapper) {
                context as? Activity ?: getTopActivity(context.baseContext)
            } else null
        }
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.getDefault())
        ThemeUtils.registerActivityLifecycleCallbacks(this)
        PRDownloader.initialize(this)
        doInBackground {
            if (!URL(Config.GITHUB_REPO_PREFIX).isReachable()) {
                Config.REPO_PREFIX = Config.GITLAB_REPO_PREFIX
                Config.RAW_PREFIX = Config.GITLAB_RAW_PREFIX
            }
            if (!URL("https://bin.utwitch.net").isReachable())
                Logger.customBin = "bin.dertyp.dev"
        }
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit { putString("logMode", if (BuildConfig.DEBUG) "VERBOSE" else "ERROR") }
        Logger.init(
            this,
            getAttr(com.google.android.material.R.attr.colorPrimary),
            getAttr(com.google.android.material.R.attr.colorAccent)
        )
        Logger.extraData = {
            StringBuilder("Rooted: ")
                .append(if (hasRoot()) "yes" else "no").append("\n")
                .append("Version-Code: ").append(BuildConfig.VERSION_CODE).append("\n")
                .append("Gboard-Version-Code: ").append(GboardUtils.getGboardVersionCode(this))
                .append("\n")
                .append("Gboard-Version-Name: ").append(GboardUtils.getGboardVersion(this))
                .append("\n")
                .append("Version-Name: ").append(BuildConfig.VERSION_NAME).append("\n\n")
                .apply {
                    val magisk = MagiskUtils.isMagiskInstalled()
                    append("Magisk: ").append(if (magisk) "yes" else "no")
                        .append("\n")
                    if (magisk) {
                        append("Magisk-Version: ")
                            .append(MagiskUtils.getMagiskVersionFullString()).append("\n")
                        append("Magisk Modules:\n")
                        MagiskUtils.getModules().forEach { module ->
                            append("\t\t").append(module.meta.name).append("\n")
                        }
                    }
                }.toString()
        }
        context = this
        uiHandler = Handler(Looper.getMainLooper())
        PreferenceManager.getDefaultSharedPreferences(this).apply {
            when (getString("app_theme", "system_theme")) {
                "dark" -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }

                "light" -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                "system_theme" -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
        }
    }
}