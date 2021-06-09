package de.dertyp7214.rboardthememanager.utils

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.preference.PreferenceManager
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.helpers.categoryHeader
import de.Maxr1998.modernpreferences.helpers.pref
import de.Maxr1998.modernpreferences.helpers.switch
import de.dertyp7214.rboardthememanager.R

class Settings {
    enum class TYPE {
        BOOLEAN,
        STRING,
        INT,
        LONG,
        FLOAT,
        GROUP
    }

    enum class SETTINGS(
        val key: String,
        @StringRes val title: Int,
        @StringRes val summary: Int,
        @DrawableRes val icon: Int,
        val defaultValue: Any,
        val type: TYPE
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
            -1,
            R.drawable.ic_themes,
            true,
            TYPE.BOOLEAN
        ),
        SHOW_PREINSTALLED_THEMES(
            "show_preinstalled_themes",
            R.string.show_preinstalled_themes,
            -1,
            R.drawable.ic_themes,
            true,
            TYPE.BOOLEAN
        );

        inline fun <reified T> getValue(context: Context, defaultValue: T? = null): T? {
            return PreferenceManager.getDefaultSharedPreferences(context).all[key].let {
                if (it is T) it
                else defaultValue
            }
        }
    }

    fun preferences(builder: PreferenceScreen.Builder) {
        SETTINGS.values().forEach { item ->
            val pref: Preference = when (item.type) {
                TYPE.BOOLEAN -> builder.switch(item.key) {
                    defaultValue = item.defaultValue as Boolean
                }
                TYPE.INT, TYPE.LONG, TYPE.FLOAT -> builder.pref(item.key) {}
                TYPE.GROUP -> builder.categoryHeader(item.key) {
                    titleRes = item.title
                    summaryRes = item.summary
                    iconRes = item.icon
                }.let { Preference("") }
                TYPE.STRING -> builder.pref(item.key) {}
            }
            pref.apply {
                titleRes = item.title
                summaryRes = item.summary
                iconRes = item.icon
            }
        }
    }
}