package de.dertyp7214.rboardthememanager.preferences

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.helpers.*
import de.Maxr1998.modernpreferences.preferences.choice.SelectionItem
import de.dertyp7214.rboardthememanager.Application
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.Config.MODULE_ID
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.openDialog
import de.dertyp7214.rboardthememanager.core.runAsCommand
import de.dertyp7214.rboardthememanager.core.start
import de.dertyp7214.rboardthememanager.screens.PreferencesActivity
import de.dertyp7214.rboardthememanager.utils.GboardUtils
import de.dertyp7214.rboardthememanager.utils.MagiskUtils

class Settings(private val activity: Activity) : AbstractPreference() {
    enum class TYPE {
        BOOLEAN,
        STRING,
        INT,
        LONG,
        FLOAT,
        GROUP,
        SELECT
    }

    @Suppress("UNCHECKED_CAST")
    enum class SETTINGS(
        val key: String,
        @StringRes val title: Int,
        @StringRes val summary: Int,
        @DrawableRes val icon: Int,
        val defaultValue: Any,
        val type: TYPE,
        val items: List<SelectionItem> = listOf(),
        val onClick: Activity.(Boolean) -> Unit = {}
    ) {
        THEMES_HEADER(
            "themes_header",
            R.string.themes_header,
            -1,
            -1,
            "",
            TYPE.GROUP
        ),
        SHOW_SYSTEM_THEME(
            "show_system_theme",
            R.string.show_system_theme,
            R.string.show_system_theme_long,
            R.drawable.ic_keyboard_theme,
            true,
            TYPE.BOOLEAN
        ),
        SHOW_PREINSTALLED_THEMES(
            "show_preinstalled_themes",
            R.string.show_preinstalled_themes,
            R.string.show_preinstalled_themes_long,
            R.drawable.ic_keyboard_theme,
            true,
            TYPE.BOOLEAN
        ),
        USE_MAGISK(
            "useMagisk",
            R.string.use_magisk,
            R.string.gboard_magisk,
            R.drawable.ic_magisk,
            false,
            TYPE.BOOLEAN,
            listOf(),
            {
                Config.useMagisk =
                    PreferenceManager.getDefaultSharedPreferences(this)
                        .getBoolean("useMagisk", false)
                if (Config.useMagisk && !MagiskUtils.isModuleInstalled(MODULE_ID))
                    MagiskUtils.installModule(this)
            }
        ),
        DOWNLOAD(
            "download_header",
            R.string.download_header,
            -1,
            -1,
            "",
            TYPE.GROUP
        ),
        REPOS(
            "repos",
            R.string.repos,
            R.string.repos_long,
            R.drawable.ic_repo,
            "",
            TYPE.STRING,
            listOf(),
            {
                Application.context?.let {
                    PreferencesActivity::class.java.start(it) {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra("type", "repos")
                    }
                }
            }
        ),
        MANAGER(
            "manager",
            R.string.manager,
            -1,
            -1,
            "",
            TYPE.GROUP
        ),
        INFO(
            "info",
            R.string.info,
            -1,
            R.drawable.ic_info,
            "",
            TYPE.STRING,
            listOf(), {
                Application.context?.let {
                    PreferencesActivity::class.java.start(it) {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra("type", "info")
                    }
                }
            }
        ),
        APP_THEME(
            "app_theme",
            R.string.app_theme,
            -1,
            R.drawable.ic_app_theme,
            "",
            TYPE.SELECT,
            listOf(
                SelectionItem("dark", R.string.dark, -1),
                SelectionItem("light", R.string.light, -1),
                SelectionItem("system_theme", R.string.system_theme, -1)
            )
        ),
        USE_BLUR(
            "useBlur",
            R.string.use_blur,
            R.string.use_blur_long,
            R.drawable.ic_blur,
            true,
            TYPE.BOOLEAN
        ),
        FIX_FLAGS(
            "fix_flags",
            R.string.fix_flags,
            R.string.fix_flags_long,
            R.drawable.ic_flag_remove_outline,
            "",
            TYPE.STRING,
            listOf(),
            {
                listOf(
                    "rm \"${Flags.FILES.FLAGS.filePath}\"",
                    "am force-stop ${Config.GBOARD_PACKAGE_NAME}"
                ).runAsCommand()
                GboardUtils.updateCurrentFlags("")
                Toast.makeText(this, R.string.flags_fixed, Toast.LENGTH_LONG).show()
            }
        ),
        UNINSTALL(
            "uninstall",
            R.string.uninstall,
            R.string.uninstall_long,
            R.drawable.ic_trash,
            "",
            TYPE.STRING,
            listOf(),
            {
                openDialog(R.string.uninstall_long, R.string.uninstall, false) {
                    MagiskUtils.uninstallModule(MODULE_ID)
                    Handler(Looper.getMainLooper()).postDelayed({
                        "reboot".runAsCommand()
                    }, 500)
                }
            }
        );

        inline fun <reified T> getValue(context: Context, defaultValue: T? = null): T? {
            return PreferenceManager.getDefaultSharedPreferences(context).all[key].let {
                if (it is T) it
                else defaultValue
            }
        }
    }

    override fun getExtraView(): View? = null

    override fun onBackPressed(callback: () -> Unit) {
        callback()
    }

    override fun preferences(builder: PreferenceScreen.Builder) {
        SETTINGS.values().forEach { item ->
            val pref: Preference = when (item.type) {
                TYPE.BOOLEAN -> builder.switch(item.key) {
                    defaultValue = item.defaultValue as Boolean
                    onCheckedChange {
                        item.onClick(activity, it)
                        true
                    }
                }
                TYPE.INT, TYPE.LONG, TYPE.FLOAT -> builder.pref(item.key) {}
                TYPE.GROUP -> builder.categoryHeader(item.key) {
                    titleRes = item.title
                    summaryRes = item.summary
                    iconRes = item.icon
                }.let { Preference("") }
                TYPE.STRING -> builder.pref(item.key) {}
                TYPE.SELECT -> builder.singleChoice(item.key, item.items) {
                    initialSelection = item.items.last().key
                    onSelectionChange {
                        when (it) {
                            "dark" -> {
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            }
                            "light" -> {
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            }
                            "system_auto" -> {
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_UNSPECIFIED)
                            }
                        }
                        true
                    }
                }
            }
            pref.apply {
                titleRes = item.title
                summaryRes = item.summary
                iconRes = item.icon
                onClick { item.onClick(activity, false); false }
            }
        }
    }
}