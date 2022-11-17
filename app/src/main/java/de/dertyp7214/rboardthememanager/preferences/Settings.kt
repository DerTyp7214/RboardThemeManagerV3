package de.dertyp7214.rboardthememanager.preferences

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.*
import de.Maxr1998.modernpreferences.preferences.choice.SelectionItem
import de.dertyp7214.rboardcomponents.utils.ThemeUtils
import de.dertyp7214.rboardthememanager.Application
import de.dertyp7214.rboardthememanager.Application.Companion.context
import de.dertyp7214.rboardthememanager.BuildConfig
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.Config.FLAG_PATH
import de.dertyp7214.rboardthememanager.Config.GBOARD_VERSION_CODE
import de.dertyp7214.rboardthememanager.Config.MODULE_ID
import de.dertyp7214.rboardthememanager.Config.PLAY_URL
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.*
import de.dertyp7214.rboardthememanager.screens.Logs
import de.dertyp7214.rboardthememanager.screens.MainActivity
import de.dertyp7214.rboardthememanager.screens.PreferencesActivity
import de.dertyp7214.rboardthememanager.utils.GboardUtils
import de.dertyp7214.rboardthememanager.utils.MagiskUtils
import de.dertyp7214.rboardthememanager.utils.PackageUtils

class Settings(private val activity: Activity, private val args: SafeJSON) : AbstractPreference() {
    enum class FILES(val Path: String) {
        @SuppressLint("SdCardPath")
        CACHE("/data/user${if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) "_de" else ""}/0/${Config.GBOARD_PACKAGE_NAME}/cache/auto_clean/"),

        @SuppressLint("SdCardPath")
        EMOJIS("/data/data/${Config.GBOARD_PACKAGE_NAME}/databases/expression-history.db")

    }

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
        val onClick: Activity.(Boolean) -> Unit = {},
        val visible: Boolean = true
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
                    PreferencesActivity::class.java[it] = {
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
                    PreferencesActivity::class.java[it] = {
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
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                listOf(
                    SelectionItem("dark", R.string.dark, -1),
                    SelectionItem("light", R.string.light, -1),
                    SelectionItem("system_theme", R.string.system_theme, -1)
                )
            } else {
                listOf(
                    SelectionItem("dark", R.string.dark, -1),
                    SelectionItem("light", R.string.light, -1)
                )
            }
        ),
        APP_STYLE(
            "app_style",
            R.string.app_style,
            -1,
            R.drawable.ic_theme_settings,
            "default",
            TYPE.SELECT,
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                listOf(
                    SelectionItem("blue_style", R.string.blue_style, -1),
                    SelectionItem("green_style", R.string.green_style, -1),
                    SelectionItem("red_style", R.string.red_style, -1),
                    SelectionItem("yellow_style", R.string.yellow_style, -1),
                    SelectionItem("orange_style", R.string.orange_style, -1),
                    SelectionItem("pink_style", R.string.pink_style, -1),
                    SelectionItem("lime_style", R.string.lime_style,-1),
                    SelectionItem("default_style", R.string.default_style, -1)
                )
            } else {
                ThemeUtils.APP_THEMES.map {
                    SelectionItem(it.value, it.key, -1)
                }
            }
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
                    "rm \"$FLAG_PATH\"",
                    "rm \"${Flags.FILES.FLAGS.filePath}\"",
                    "am force-stop ${Config.GBOARD_PACKAGE_NAME}"
                ).runAsCommand()
                GboardUtils.updateCurrentFlags("")
                Toast.makeText(this, R.string.flags_fixed, Toast.LENGTH_LONG).show()
            }
        ),
        COPY_FLAGS(
            "copy_flags",
            R.string.copy_flags,
            R.string.copy_flags_long,
            R.drawable.ic_flag_copy,
            "",
            TYPE.STRING,
            listOf(),
            {
                listOf(
                    "\\cp \"$FLAG_PATH\" \"${Flags.FILES.FLAGS.filePath}\"",
                    "am force-stop ${Config.GBOARD_PACKAGE_NAME}"
                ).runAsCommand()
                Toast.makeText(this, R.string.flags_copied, Toast.LENGTH_LONG).show()
            }
        ),
        GBOARD_CACHE_CLEAR(
            "gboard_cache_clear",
            R.string.gboard_cache_clear,
            R.string.gboard_cache_clear_long,
            R.drawable.ic_keyboard_theme,
            "",
            TYPE.STRING,
            listOf(),
            {
                listOf(
                    "rm -r \"${FILES.CACHE.Path}\"",
                    "am force-stop ${Config.GBOARD_PACKAGE_NAME}"
                ).runAsCommand()
                Toast.makeText(this, R.string.gboard_cache_cleared, Toast.LENGTH_LONG).show()
            }
        ),
        CLEAR_RECENT_EMOJIS(
            "clear_recent_emojis",
            R.string.clear_recent_emojis,
            R.string.clear_recent_emojis_long,
            R.drawable.ic_emoji,
            "",
            TYPE.STRING,
            listOf(),
            {
                listOf(
                    "rm \"${FILES.EMOJIS.Path}\"",
                    "am force-stop ${Config.GBOARD_PACKAGE_NAME}"
                ).runAsCommand()
                Toast.makeText(this, R.string.recent_emojis_cleared, Toast.LENGTH_LONG).show()
            }
        ),
        DEEP_LINK(
            "deep_link",
            R.string.deep_link,
            R.string.deep_link,
            R.drawable.ic_link,
            "",
            TYPE.STRING,
            listOf(),
            {
                packageManager.getLaunchIntentForPackage("de.dertyp7214.deeplinkrboard")
                    ?.let(::startActivity)
                    ?: openUrl("https://github.com/DerTyp7214/DeepLinkRboard")
            },
            BuildConfig.DEBUG
        ),
        IME_TEST(
            "ime_test",
            R.string.ime_test,
            R.string.ime_test_long,
            R.drawable.ic_ime_tester,
            "",
            TYPE.STRING,
            listOf(),
            {
                packageManager.getLaunchIntentForPackage("de.dertyp7214.rboardimetester")
                    ?.let(::startActivity)
                    ?: openUrl(PLAY_URL("de.dertyp7214.rboardimetester"))
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
                        de.dertyp7214.rboardthememanager.utils.RootUtils.reboot()
                    }, 500)
                }
            }
        ),
        LOGS(
            "LOGS",
            R.string.logs,
            R.string.logs_long,
            -1,
            "",
            TYPE.STRING,
            listOf(),
            {
                Logs::class.java[this]
            },
            BuildConfig.DEBUG
        );

        inline fun <reified T> getValue(context: Context, defaultValue: T? = null): T? {
            return PreferenceManager.getDefaultSharedPreferences(context).all[key].let {
                if (it is T) it
                else defaultValue
            }
        }
    }

    override fun onStart(recyclerView: RecyclerView, adapter: PreferencesAdapter) {
        adapter.currentScreen.indexOf(args.getString("highlight"))
            .let { if (it >= 0) recyclerView.scrollToPosition(it) }
    }

    override fun getExtraView(): View? = null

    override fun onBackPressed(callback: () -> Unit) {
        callback()
    }

    override fun preferences(builder: PreferenceScreen.Builder) {
        SETTINGS.values().filter { it.visible }
            .filter {
                !(it == SETTINGS.SHOW_SYSTEM_THEME && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) &&
                        !(it == SETTINGS.USE_BLUR && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) &&
                        !(it == SETTINGS.FIX_FLAGS && BuildConfig.DEBUG && context?.let { it1 ->
                            PackageUtils.getAppVersionCode(
                                Config.GBOARD_PACKAGE_NAME,
                                it1.packageManager
                            )
                        }!! > GBOARD_VERSION_CODE) &&
                        !(it == SETTINGS.COPY_FLAGS && BuildConfig.DEBUG && context?.let { it1 ->
                            PackageUtils.getAppVersionCode(
                                Config.GBOARD_PACKAGE_NAME,
                                it1.packageManager
                            )
                        }!! > GBOARD_VERSION_CODE)
            }
            .forEach { item ->
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
                            when (item.key) {
                                "app_theme" -> when (it) {
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
                                "app_style" -> {
                                    if (item.getValue(activity, "") != it) {
                                        MainActivity.clearInstances()
                                        MainActivity::class.java[activity]
                                        PreferencesActivity::class.java[activity] = {
                                            putExtra("type", "settings")
                                        }
                                        activity.finish()
                                    }
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
