package de.dertyp7214.rboardthememanager.utils

import android.app.Activity
import android.content.Intent
import android.os.Build
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.helpers.categoryHeader
import de.Maxr1998.modernpreferences.helpers.pref
import de.Maxr1998.modernpreferences.helpers.screen
import de.dertyp7214.rboardthememanager.BuildConfig
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.R

class Preferences(private val activity: Activity, intent: Intent) {

    private val type = intent.getStringExtra("type")

    init {
        when (type) {
            "info" -> {
            }
            "settings" -> {
            }
            "flags" -> Flags.setUpFlags()
            "all_flags" -> {
            }
            else -> {
            }
        }
    }

    fun onBackPressed() {
        when (type) {
            "info" -> {
            }
            "settings" -> {
            }
            "flags" -> Flags.applyChanges()
            "all_flags" -> {
            }
            else -> {
            }
        }
    }

    val preferences: PreferenceScreen
        get() {
            return when (type) {
                "info" -> getInfoPreferences()
                "settings" -> getSettingsPreferences()
                "flags" -> getFlagsPreferences()
                "all_flags" -> getAllFlagsPreferences()
                else -> screen(activity) {}
            }
        }

    val title: String
        get() {
            return when (type) {
                "info" -> activity.getString(R.string.info)
                "settings" -> activity.getString(R.string.settings)
                "flags" -> activity.getString(R.string.flags)
                "all_flags" -> activity.getString(R.string.all_flags)
                else -> ""
            }
        }

    private fun getSettingsPreferences() = screen(activity, Settings()::preferences)
    private fun getFlagsPreferences() = screen(activity, Flags(activity)::preferences)
    private fun getAllFlagsPreferences() = screen(activity, Flags(activity)::allFlagsPreferences)

    private fun getInfoPreferences(): PreferenceScreen {
        val usingModule = MagiskUtils.getModules().any { it.id == Config.MODULE_ID }
        return screen(activity) {
            pref("theme_count") {
                titleRes = R.string.theme_count
                summary = Config.themeCount?.toString() ?: "0"
                iconRes = R.drawable.ic_themes
            }
            pref("theme_path") {
                titleRes = R.string.theme_path
                summary = Config.THEME_LOCATION
                iconRes = R.drawable.ic_folder_open
            }
            pref("installation_method") {
                titleRes = R.string.installation_method
                summaryRes = if (usingModule) R.string.magisk else R.string.other
                iconRes = R.drawable.ic_download
            }
            pref("rboard_app_version") {
                titleRes = R.string.rboard_app_version
                summary = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                iconRes = R.drawable.ic_rboard
            }
            categoryHeader("device") {
                titleRes = R.string.device
            }
            pref("android_version") {
                titleRes = R.string.android_version
                summary = Build.VERSION.RELEASE_OR_CODENAME
                iconRes = R.drawable.ic_android
            }
            if (MagiskUtils.isMagiskInstalled())
                pref("root_version") {
                    titleRes = R.string.root_version
                    summary =
                        "Magisk: ${MagiskUtils.getMagiskVersionString().removeSuffix(":MAGISK")}"
                    iconRes = R.drawable.ic_root
                }
            pref("gboard_version") {
                titleRes = R.string.gboard_version
                summary = GboardUtils.getGboardVersion(activity).split("-").first()
                iconRes = R.drawable.ic_gboard
            }
            pref("unsupported_oem") {
                titleRes = R.string.unsupported_oem
                summaryRes = if (Config.IS_MIUI) R.string.yes else R.string.no
                iconRes = R.drawable.ic_trash
            }
        }
    }
}