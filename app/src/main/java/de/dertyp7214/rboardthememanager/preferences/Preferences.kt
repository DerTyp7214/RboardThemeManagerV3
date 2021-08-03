package de.dertyp7214.rboardthememanager.preferences

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.EXTRA_TEXT
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dertyp7214.logs.helpers.DogbinUtils
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

class Preferences(
    private val activity: AppCompatActivity,
    intent: Intent,
    onRequestReload: () -> Unit
) :
    AbstractPreference() {

    private val type by lazy { intent.getStringExtra("type") }

    private val usingModule = MagiskUtils.getModules().any { it.id == Config.MODULE_ID }
    private val infoData = mapOf(
        "theme_count" to (Config.themeCount?.toString() ?: "0"),
        "theme_path" to (if (!Config.useMagisk) Config.MAGISK_THEME_LOC else Config.THEME_LOCATION),
        "installation_method" to (if (!Config.useMagisk) R.string.pref_gboard else if (usingModule) R.string.magisk else R.string.other),
        "rboard_app_version" to "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
        "phone_model" to Build.MODEL,
        "android_version" to Build.VERSION.RELEASE_OR_CODENAME,
        "root_version" to "Magisk: ${MagiskUtils.getMagiskVersionString().removeSuffix(":MAGISK")}",
        "gboard_version" to "${
            GboardUtils.getGboardVersion(activity).split("-").first()
        } (${GboardUtils.getGboardVersionCode(activity)})",
        "unsupported_oem" to if (Config.IS_MIUI) R.string.yes else R.string.no
    )

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
            "repos" -> {
                preference = Repos(activity, onRequestReload)
            }
            else -> {
                preference = this
            }
        }
    }

    override fun onBackPressed(callback: () -> Unit) {
        when (type) {
            "info" -> callback()
            "settings" -> callback()
            "repos" -> preference.onBackPressed(callback)
            "flags", "all_flags" -> {
                if (Flags.applyChanges()) {
                    Toast.makeText(activity, R.string.flags_applied, Toast.LENGTH_SHORT).show()
                    AppWidgetManager.getInstance(activity).let { appWidgetManager ->
                        appWidgetManager.getAppWidgetIds(
                            ComponentName(activity, FlagsWidget::class.java)
                        ).forEach { id ->
                            FlagsWidget.updateAppWidget(activity, appWidgetManager, id)
                        }
                    }
                }
                callback()
            }
            else -> callback()
        }
    }

    val preferences: PreferenceScreen
        get() {
            return when (type) {
                "info", "settings", "flags", "all_flags", "repos" -> screen(
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
                "repos" -> activity.getString(R.string.repos)
                else -> ""
            }
        }

    fun loadMenu(menuInflater: MenuInflater, menu: Menu?) {
        when (type) {
            "info" -> {
                if (menu != null)
                    menuInflater.inflate(R.menu.share, menu)
            }
            "repos" -> {
                if (preference is AbstractMenuPreference) preference.loadMenu(menuInflater, menu)
            }
            "settings", "flags", "all_flags" -> {
            }
        }
    }

    fun onMenuClick(menuItem: MenuItem): Boolean {
        return when (type) {
            "info" -> when (menuItem.itemId) {
                R.id.share -> {
                    val stringBuilder = StringBuilder()
                    infoData.forEach {
                        stringBuilder.append(
                            "${it.key}: ${
                                it.value.let { value ->
                                    if (value is Int) activity.getString(value)
                                    else value
                                }
                            }\n"
                        )
                    }
                    DogbinUtils.upload(
                        stringBuilder.toString(),
                        object : DogbinUtils.UploadResultCallback {
                            override fun onSuccess(url: String) {
                                Intent().apply {
                                    action = ACTION_SEND
                                    putExtra(EXTRA_TEXT, url)
                                    type = "text/plain"
                                }.let {
                                    Intent.createChooser(it, activity.getString(R.string.share))
                                }.also(activity::startActivity)
                            }

                            override fun onFail(message: String, e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(activity, R.string.error, Toast.LENGTH_SHORT).show()
                            }
                        })
                    true
                }
                else -> false
            }
            "repos" -> {
                if (preference is AbstractMenuPreference) preference.onMenuClick(menuItem)
                else false
            }
            "settings", "flags", "all_flags" -> false
            else -> false
        }
    }

    val extraView: View? = preference.getExtraView()

    override fun getExtraView(): View? = null

    override fun preferences(builder: PreferenceScreen.Builder) {
        builder.apply {
            pref("theme_count") {
                titleRes = R.string.theme_count
                summary = infoData[key] as String
                iconRes = R.drawable.ic_themes
            }
            pref("theme_path") {
                titleRes = R.string.theme_path
                summary = infoData[key] as String
                iconRes = R.drawable.ic_folder_open
            }
            pref("installation_method") {
                titleRes = R.string.installation_method
                summaryRes = infoData[key] as Int
                iconRes = R.drawable.ic_download
            }
            pref("rboard_app_version") {
                titleRes = R.string.rboard_app_version
                summary = infoData[key] as String
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
            pref("phone_model") {
                titleRes = R.string.model
                summary = infoData[key] as String
                iconRes = R.drawable.ic_model
            }
            pref("android_version") {
                titleRes = R.string.android_version
                summary = infoData[key] as String
                iconRes = R.drawable.ic_android
            }
            if (MagiskUtils.isMagiskInstalled())
                pref("root_version") {
                    titleRes = R.string.root_version
                    summary = infoData[key] as String
                    iconRes = R.drawable.ic_root
                }
            pref("gboard_version") {
                titleRes = R.string.gboard_version
                summary = infoData[key] as String
                iconRes = R.drawable.ic_gboard
            }
            pref("unsupported_oem") {
                titleRes = R.string.unsupported_oem
                summaryRes = infoData[key] as Int
                iconRes = R.drawable.ic_trash
            }
        }
    }
}

abstract class AbstractPreference {
    internal abstract fun preferences(builder: PreferenceScreen.Builder)
    internal abstract fun getExtraView(): View?
    internal abstract fun onBackPressed(callback: () -> Unit)
}

abstract class AbstractMenuPreference : AbstractPreference() {
    internal abstract fun loadMenu(menuInflater: MenuInflater, menu: Menu?)
    internal abstract fun onMenuClick(menuItem: MenuItem): Boolean
}