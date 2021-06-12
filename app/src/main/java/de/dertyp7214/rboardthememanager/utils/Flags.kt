package de.dertyp7214.rboardthememanager.utils

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.topjohnwu.superuser.io.SuFile
import com.topjohnwu.superuser.io.SuFileInputStream
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.helpers.*
import de.dertyp7214.rboardthememanager.Application
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.*
import de.dertyp7214.rboardthememanager.screens.PreferencesActivity
import org.xml.sax.InputSource
import java.io.StringReader
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.collections.HashMap

class Flags {
    enum class FILES(val fileName: String) {
        FLAGS("flag_value.xml"),
        GBOARD_PREFERENCES("${Config.GBOARD_PACKAGE_NAME}_preferences.xml")
    }

    enum class TYPE {
        BOOLEAN,
        STRING,
        INT,
        LONG,
        FLOAT,
        GROUP,
        JUST_CLICK
    }

    enum class FLAGS(
        val key: String,
        @StringRes val title: Int,
        @StringRes val summary: Int,
        @DrawableRes val icon: Int,
        val defaultValue: Any,
        val type: TYPE,
        val file: FILES,
        val valueMap: Map<Any?, Any?>? = null,
        val visible: Boolean = true,
        val onClick: () -> Unit = {}
    ) {
        GENERAL(
            "general",
            R.string.general,
            -1,
            -1,
            "",
            TYPE.GROUP,
            FILES.FLAGS
        ),
        EMOJI_COMPAT_APP_WHITELIST(
            "emoji_compat_app_whitelist",
            R.string.emoji_compat_app_whitelist,
            -1,
            R.drawable.ic_emoji_compat,
            false,
            TYPE.BOOLEAN,
            FILES.FLAGS,
            mapOf(
                Pair(true, "*"),
                Pair(false, "disabled"),
                Pair(null, "disabled")
            )
        ),
        ENABLE_JOYSTICK_DELETE(
            "enable_joystick_delete",
            R.string.enable_joystick_delete,
            -1,
            R.drawable.ic_backspace,
            false,
            TYPE.BOOLEAN,
            FILES.FLAGS
        ),
        DEPRECATE_SEARCH(
            "deprecate_search",
            R.string.deprecate_search,
            -1,
            R.drawable.ic_google_logo,
            false,
            TYPE.BOOLEAN,
            FILES.FLAGS,
            mapOf(
                Pair(false, second = true),
                Pair(null, false),
                Pair(true, second = false)
            ),
            false
        ),
        ENABLE_SHARING(
            "enable_sharing",
            R.string.enable_sharing,
            -1,
            R.drawable.ic_baseline_share_24,
            false,
            TYPE.BOOLEAN,
            FILES.FLAGS
        ),
        THEMED_NAVBAR_STYLE(
            "themed_nav_bar_style",
            R.string.themed_nav_bar_style,
            -1,
            R.drawable.ic_navbar,
            false,
            TYPE.BOOLEAN,
            FILES.FLAGS,
            mapOf(
                Pair(true, 2L),
                Pair(false, 1L),
                Pair(null, 1L)
            )
        ),
        ENABLE_EMAIL_PROVIDER_COMPLETION(
            "enable_email_provider_completion",
            R.string.enable_email_provider_completion,
            -1,
            R.drawable.ic_email,
            false,
            TYPE.BOOLEAN,
            FILES.FLAGS
        ),
        ENABLE_POPUP_VIEW_V2(
            "enable_popup_view_v2",
            R.string.enable_popup_view_v2,
            -1,
            R.drawable.ic_popup_v2,
            false,
            TYPE.BOOLEAN,
            FILES.FLAGS
        ),
        ENABLE_KEY_BORDER(
            "enable_key_border",
            R.string.enable_key_border,
            -1,
            R.drawable.ic_key_boarder,
            false,
            TYPE.BOOLEAN,
            FILES.GBOARD_PREFERENCES
        ),
        ENABLE_SECONDARY_SYMBOLS(
            "enable_secondary_symbols",
            R.string.enable_secondary_symbols,
            -1,
            R.drawable.ic_numeric,
            false,
            TYPE.BOOLEAN,
            FILES.GBOARD_PREFERENCES
        ),
        SHOW_SUGGESTIONS(
            "show_suggestions",
            R.string.show_suggestions,
            -1,
            R.drawable.ic_alphabetical,
            false,
            TYPE.BOOLEAN,
            FILES.GBOARD_PREFERENCES
        ),
        SHOW_BRANDING_ON_SPACE(
            "show_branding_on_space",
            R.string.show_branding_on_space,
            -1,
            -1,
            false,
            TYPE.BOOLEAN,
            FILES.FLAGS
        ),
        ENABLE_AUTO_FLOAT_KEYBOARD_IN_MULTI_WINDOW(
            "enable_auto_float_keyboard_in_multi_window",
            R.string.enable_auto_float_keyboard_in_multi_window,
            -1,
            -1,
            false,
            TYPE.BOOLEAN,
            FILES.FLAGS
        ),
        ENABLE_CLIPBOARD_SCREENSHOT_PASTE(
            "enable_clipboard_screenshot_paste",
            R.string.enable_clipboard_screenshot_paste,
            -1,
            -1,
            false,
            TYPE.BOOLEAN,
            FILES.FLAGS
        ),
        ENABLE_LENS(
            "enable_lens",
            R.string.enable_lens,
            -1,
            -1,
            false,
            TYPE.BOOLEAN,
            FILES.FLAGS
        ),
        ANDROID_12(
            "android_12",
            R.string.android_12,
            -1,
            -1,
            "",
            TYPE.GROUP,
            FILES.FLAGS
        ),
        PILL_SHAPED_KEY(
            "pill_shaped_key",
            R.string.pill_shaped_key,
            -1,
            -1,
            false,
            TYPE.BOOLEAN,
            FILES.FLAGS
        ),
        MONET(
            "monet",
            R.string.monet,
            -1,
            -1,
            "",
            TYPE.GROUP,
            FILES.FLAGS
        ),
        USE_SILK_THEME_BY_DEFAULT(
            "use_silk_theme_by_default",
            R.string.use_silk_theme_by_default,
            -1,
            R.drawable.ic_themes,
            false,
            TYPE.BOOLEAN,
            FILES.FLAGS
        ),
        SILK_ON_ALL_PIXEL(
            "silk_on_all_pixel",
            R.string.silk_on_all_pixel,
            -1,
            R.drawable.ic_themes,
            false,
            TYPE.BOOLEAN,
            FILES.FLAGS
        ),
        SILK_THEME(
            "silk_theme",
            R.string.silk_theme,
            -1,
            R.drawable.ic_themes,
            false,
            TYPE.BOOLEAN,
            FILES.FLAGS
        ),
        EMPTY(
            "empty",
            -1,
            -1,
            -1,
            "",
            TYPE.GROUP,
            FILES.FLAGS
        ),
        SHOW_ALL_FLAGS(
            "show_all_flags",
            R.string.show_all_flags,
            R.string.show_all_flags_long,
            -1,
            "",
            TYPE.JUST_CLICK,
            FILES.FLAGS,
            onClick = {
                Application.context?.let {
                    PreferencesActivity::class.java.start(
                        it
                    ) {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra("type", "all_flags")
                    }
                }
            }
        );

        inline fun <reified T> getValue(defaultValue: T? = null): T? {
            return if (valueMap != null) valueMap.filter {
                it.value == values[key]
            }.entries.firstOrNull()?.key as? T ?: defaultValue else values[key] as? T
                ?: defaultValue
        }

        @SuppressLint("SdCardPath")
        fun <T> setValue(v: T) = setValue(if (valueMap != null) valueMap[v] else v, key, file)
    }

    fun preferences(builder: PreferenceScreen.Builder) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(Application.context)
        FLAGS.values().forEach { item ->
            if (item.visible) {
                prefs.edit { remove(item.key) }
                val pref: Preference = when (item.type) {
                    TYPE.BOOLEAN -> builder.switch(item.key) {
                        defaultValue = item.getValue(item.defaultValue) as? Boolean ?: false
                        onCheckedChange {
                            if (!item.setValue(it)) Application.context?.let { context ->
                                Toast.makeText(
                                    context,
                                    R.string.error,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            true
                        }
                    }
                    TYPE.INT, TYPE.LONG, TYPE.FLOAT -> builder.pref(item.key) {}
                    TYPE.GROUP -> builder.categoryHeader(item.key) {
                        titleRes = item.title
                        summaryRes = item.summary
                    }.let { Preference("") }
                    TYPE.STRING -> builder.pref(item.key) {}
                    TYPE.JUST_CLICK -> builder.pref(item.key) {
                        onClick { item.onClick(); false }
                    }
                }
                pref.apply {
                    titleRes = item.title
                    summaryRes = item.summary
                    iconRes = item.icon
                    if (item.summary == -1) summary = item.key
                }
            }
        }
    }

    fun allFlagsPreferences(builder: PreferenceScreen.Builder) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(Application.context)
        getCurrentXmlValues(FILES.FLAGS.fileName).forEach { entry ->
            prefs.edit { remove(entry.key) }
            if (entry.value is Boolean) builder.switch(entry.key) {
                title = entry.key.split("_").joinToString(" ") {
                    it.replaceFirstChar { char ->
                        if (char.isLowerCase()) char.titlecase(
                            Locale.getDefault()
                        ) else char.toString()
                    }
                }
                summary = entry.key
                defaultValue = entry.value as Boolean
                onCheckedChange {
                    if (!setValue(it, entry.key, FILES.FLAGS)) Application.context?.let { context ->
                        Toast.makeText(
                            context,
                            R.string.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    true
                }
            } else {
                builder.pref(entry.key) {
                    title = entry.key.split("_").joinToString(" ") {
                        it.replaceFirstChar { char ->
                            if (char.isLowerCase()) char.titlecase(
                                Locale.getDefault()
                            ) else char.toString()
                        }
                    }
                    summary = entry.value.toString()
                }
            }
        }
    }

    companion object {
        val values: Map<String, Any>
            get() {
                return getCurrentXmlValues(FILES.GBOARD_PREFERENCES.fileName) +
                        getCurrentXmlValues(FILES.FLAGS.fileName)
            }

        @SuppressLint("SdCardPath")
        private fun getCurrentXmlValues(file: String): Map<String, Any> {
            val output = HashMap<String, Any>()

            val fileName = "/data/data/${Config.GBOARD_PACKAGE_NAME}/shared_prefs/$file"
            val xmlFile = SuFile(fileName)
            if (!xmlFile.exists()) return output

            val map = try {
                DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                    InputSource(
                        StringReader(
                            SuFileInputStream.open(xmlFile).bufferedReader().readText()
                        )
                    )
                ).getElementsByTagName("map")
            } catch (e: Exception) {
                return output
            }

            for (item in map.item(0).childNodes) {
                val name = item.attributes?.getNamedItem("name")?.nodeValue
                val value = item.attributes?.getNamedItem("value")?.nodeValue
                if (name != null) output[name] =
                    (value?.booleanOrNull() ?: value) ?: item.textContent ?: ""
            }

            return output
        }

        @SuppressLint("SdCardPath")
        private fun <T> setValue(value: T, key: String, file: FILES): Boolean {
            val fileName = "/data/data/${Config.GBOARD_PACKAGE_NAME}/shared_prefs/${file.fileName}"
            val content = SuFileInputStream.open(SuFile(fileName)).use {
                it.bufferedReader().readText()
            }.let { fileText ->
                val type = when (value) {
                    is Boolean -> "boolean"
                    is Int -> "integer"
                    is Long -> "long"
                    is Float -> "float"
                    else -> "string"
                }
                if (type != "string") {
                    when {
                        "<$type name=\"$key\"" in fileText -> fileText.replace(
                            """<$type name="$key" value=".*" />""".toRegex(),
                            """<$type name="$key" value="$value" />"""
                        )
                        Regex("<map[ |]/>") in fileText -> fileText.replace(
                            Regex("<map[ |]/>"),
                            """<map><$type name="$key" value="$value" /></map>"""
                        )
                        else -> fileText.replace(
                            "<map>",
                            """<map><$type name="$key" value="$value" />"""
                        )
                    }
                } else {
                    when {
                        "<$type name\"$key\"" in fileText -> fileText.replace(
                            """<$type name="$key">.*</$type>""".toRegex(),
                            """<$type name="$key">$value</$type>"""
                        )
                        Regex("<map[ |]>") in fileText -> fileText.replace(
                            Regex("<map[ |]>"),
                            """<map><$type name="$key">$value</$type></map>"""
                        )
                        else -> fileText.replace(
                            "<map>",
                            """<map><$type name="$key">$value</$type>"""
                        )
                    }
                }
            }

            SuFile(fileName).writeFile(content)

            return "am force-stop ${Config.GBOARD_PACKAGE_NAME}".runAsCommand()
        }
    }
}