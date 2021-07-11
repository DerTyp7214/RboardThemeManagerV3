package de.dertyp7214.rboardthememanager.preferences

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.helpers.categoryHeader
import de.Maxr1998.modernpreferences.helpers.onClick
import de.Maxr1998.modernpreferences.helpers.pref
import de.Maxr1998.modernpreferences.helpers.screen
import de.dertyp7214.rboardthememanager.Application
import de.dertyp7214.rboardthememanager.BuildConfig
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.start
import de.dertyp7214.rboardthememanager.screens.ReadMoreReadFast
import de.dertyp7214.rboardthememanager.utils.GboardUtils
import de.dertyp7214.rboardthememanager.utils.MagiskUtils
import de.dertyp7214.rboardthememanager.widgets.FlagsWidget

class Preferences(private val activity: Activity, intent: Intent, onRequestReload: () -> Unit) :
    AbstractPreference() {

    private val type by lazy { intent.getStringExtra("type") }

    private val preference: AbstractPreference

    init {
        when (type) {
            "info" -> {
                preference = this
            }
            "settings" -> {
                preference = Settings(activity)
            }
            "flags" -> {
                Flags.setUpFlags()
                preference = Flags(activity)
            }
            "all_flags" -> {
                Flags.setUpFlags()
                preference = Flags.AllFlags(activity, onRequestReload)
            }
            else -> {
                preference = this
            }
        }
    }

    fun onBackPressed() {
        when (type) {
            "info" -> {
            }
            "settings" -> {
            }
            "flags", "all_flags" -> {
                Flags.applyChanges()
                Toast.makeText(activity, R.string.flags_applied, Toast.LENGTH_SHORT).show()
                AppWidgetManager.getInstance(activity).let { appWidgetManager ->
                    appWidgetManager.getAppWidgetIds(
                        ComponentName(activity, FlagsWidget::class.java)
                    ).forEach { id ->
                        FlagsWidget.updateAppWidget(activity, appWidgetManager, id)
                    }
                }
            }
            else -> {
            }
        }
    }

    val preferences: PreferenceScreen
        get() {
            return when (type) {
                "info", "settings", "flags", "all_flags" -> screen(
                    activity,
                    preference::preferences
                )
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

    val extraView: View? = preference.getExtraView()

    override fun getExtraView(): View? = null

    override fun preferences(builder: PreferenceScreen.Builder) {
        val usingModule = MagiskUtils.getModules().any { it.id == Config.MODULE_ID }
        builder.apply {
            pref("theme_count") {
                titleRes = R.string.theme_count
                summary = Config.themeCount?.toString() ?: "0"
                iconRes = R.drawable.ic_themes
            }
            pref("theme_path") {
                titleRes = R.string.theme_path
                summary = if (!Config.useMagisk) Config.MAGISK_THEME_LOC else Config.THEME_LOCATION
                iconRes = R.drawable.ic_folder_open
            }
            pref("installation_method") {
                titleRes = R.string.installation_method
                summaryRes =
                    if (!Config.useMagisk) R.string.pref_gboard else if (usingModule) R.string.magisk else R.string.other
                iconRes = R.drawable.ic_download
            }
            pref("rboard_app_version") {
                titleRes = R.string.rboard_app_version
                summary = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                iconRes = R.drawable.ic_rboard
                var count = 0
                onClick {
                    count++
                    Handler(Looper.getMainLooper()).postDelayed({
                        count--
                    }, 2000)
                    if (count == 8) {
                        Toast.makeText(Application.context, R.string.easter_egg, Toast.LENGTH_SHORT)
                            .show()
                        Application.context?.let {
                            ReadMoreReadFast::class.java.start(it) {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                        }
                    }
                    false
                }
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

abstract class AbstractPreference {
    internal abstract fun preferences(builder: PreferenceScreen.Builder)
    internal abstract fun getExtraView(): View?
}