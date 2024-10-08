package de.dertyp7214.rboardthememanager.preferences

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.RecyclerView
import com.topjohnwu.superuser.io.SuFile
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.categoryHeader
import de.Maxr1998.modernpreferences.helpers.onCheckedChange
import de.Maxr1998.modernpreferences.helpers.onClick
import de.Maxr1998.modernpreferences.helpers.onSelectionChange
import de.Maxr1998.modernpreferences.helpers.pref
import de.Maxr1998.modernpreferences.helpers.singleChoice
import de.Maxr1998.modernpreferences.helpers.switch
import de.Maxr1998.modernpreferences.preferences.choice.SelectionItem
import de.dertyp7214.rboardcomponents.utils.ThemeUtils
import de.dertyp7214.rboardthememanager.BuildConfig
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.Config.FLAG_PATH
import de.dertyp7214.rboardthememanager.Config.MODULE_ID
import de.dertyp7214.rboardthememanager.Config.PLAY_URL
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.components.XMLEntry
import de.dertyp7214.rboardthememanager.components.XMLFile
import de.dertyp7214.rboardthememanager.components.XMLType
import de.dertyp7214.rboardthememanager.core.SafeJSON
import de.dertyp7214.rboardthememanager.core.get
import de.dertyp7214.rboardthememanager.core.openDialog
import de.dertyp7214.rboardthememanager.core.openUrl
import de.dertyp7214.rboardthememanager.core.runAsCommand
import de.dertyp7214.rboardthememanager.core.set
import de.dertyp7214.rboardthememanager.core.writeFile
import de.dertyp7214.rboardthememanager.screens.Logs
import de.dertyp7214.rboardthememanager.screens.PreferencesActivity
import de.dertyp7214.rboardthememanager.screens.ThemeChangerActivity
import de.dertyp7214.rboardthememanager.utils.GboardUtils
import de.dertyp7214.rboardthememanager.utils.MagiskUtils
import de.dertyp7214.rboardthememanager.utils.PackageUtils.getPackageUid

class Settings(private val activity: Activity, private val args: SafeJSON) : AbstractPreference() {

    enum class FILES(val Path: String) {
        @SuppressLint("SdCardPath")
        CACHE("/data/user_de/0/${Config.GBOARD_PACKAGE_NAME}/cache/auto_clean/"),

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
                PreferencesActivity::class.java[this] = {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra("type", "repos")
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
                PreferencesActivity::class.java[this] = {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra("type", "info")
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
        APP_STYLE(
            "app_style",
            R.string.app_style,
            -1,
            R.drawable.ic_theme_settings,
            "default",
            TYPE.STRING,
            listOf(), {
                ThemeChangerActivity::class.java[this]
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
                val xmlFile = XMLFile(path = FLAG_PATH)
                xmlFile.setValue(XMLEntry("crowdsource_uri", "", XMLType.STRING))
                SuFile(Flags.FILES.FLAGS.filePath).writeFile(xmlFile.toString())
                val uid = getPackageUid(
                    Config.GBOARD_PACKAGE_NAME,
                    packageManager
                )
                val gids = packageManager.getPackageGids(Config.GBOARD_PACKAGE_NAME)
                listOf(
                    "chmod 660 \"${Flags.FILES.FLAGS.filePath}\"",
                    if (uid != null && gids != null) "chown ${uid}:${gids.first()} \"${Flags.FILES.FLAGS.filePath}\"" else "",
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
                openDialog(R.string.gboard_cache_clear_long_question, R.string.gboard_cache_clear_question, false) {
                    listOf(
                        "rm -r \"${FILES.CACHE.Path}\"",
                        "am force-stop ${Config.GBOARD_PACKAGE_NAME}"
                    ).runAsCommand()
                    Toast.makeText(this, R.string.gboard_cache_cleared, Toast.LENGTH_LONG).show()
                }
            }
        ),
        CLEAR_RECENT_EMOJIS(
            "clear_recent_emojis",
            R.string.clear_recent_emojis,
            R.string.clear_recent_emojis_long,
            R.drawable.ic_emoji_clear,
            "",
            TYPE.STRING,
            listOf(),
            {
                openDialog(R.string.clear_recent_emojis_question, R.string.clear_recent_emojis, false) {
                    listOf(
                        "rm \"${FILES.EMOJIS.Path}\"",
                        "am force-stop ${Config.GBOARD_PACKAGE_NAME}"
                    ).runAsCommand()
                    Toast.makeText(this, R.string.recent_emojis_cleared, Toast.LENGTH_LONG).show()
                }
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
            R.drawable.ic_logs,
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
        SETTINGS.entries.filter { it.visible }.forEach { item ->
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

                TYPE.STRING -> builder.pref(item.key) {
                    when (item.key) {
                        "app_style" -> {
                            summaryRes =
                                ThemeUtils.APP_THEMES.toList()
                                    .first {
                                        it.second == ThemeUtils.getStyleName(activity)
                                    }.first
                        }
                    }
                }

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
                        }
                        true
                    }
                }
            }
            pref.apply {
                if (titleRes == -1) titleRes = item.title
                if (summaryRes == -1) summaryRes = item.summary
                iconRes = item.icon
                onClick { item.onClick(activity, false); false }
            }
        }
    }
}