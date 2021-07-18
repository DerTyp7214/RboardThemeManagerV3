package de.dertyp7214.rboardthememanager.preferences

import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.EXTRA_TEXT
import android.os.Build
import android.os.Handler
import android.view.View
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.dertyp7214.logs.helpers.DogbinUtils
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.helpers.onClick
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

class Preferences(private val activity: Activity, intent: Intent, onRequestReload: () -> Unit) :
    AbstractPreference() {
    private val type by lazy { intent.getStringExtra("type") }

    private val usingModule = MagiskUtils.getModules().any { it.id == Config.MODULE_ID }
    private val infoData = mapOf(
        "theme_count" to (Config.themeCount?.toString() ?: "0"),
        "theme_path" to (if (!Config.useMagisk) Config.MAGISK_THEME_LOC else Config.THEME_LOCATION),
        "installation_method" to (if (!Config.useMagisk) R.string.pref_gboard else if (usingModule) R.string.magisk else R.string.other),
        "rboard_app_version" to "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
        "phone_model" to Build.MODEL,
        "android_version" to Build.VERSION.RELEASE,
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
                if (Flags.applyChanges()) {
                    Toast.makeText(activity, R.string.flags_applied, Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
            }
        }
    }

    val preferences: PreferenceScreen
        @RequiresApi(Build.VERSION_CODES.R)
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

    fun loadMenu(menuInflater: MenuInflater, menu: Menu?) {
        when (type) {
            "info" -> {
                if (menu != null)
                    menuInflater.inflate(R.menu.share, menu)
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
            "settings", "flags", "all_flags" -> false
            else -> false
        }
    }


    val extraView: View? = preference.getExtraView()

    @RequiresApi(Build.VERSION_CODES.R)
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
                    Toast.makeText(Application.context, R.string.easter_egg, Toast.LENGTH_SHORT)
                        .show()
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
}