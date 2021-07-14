package de.dertyp7214.rboardthememanager.preferences

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.dertyp7214.logs.helpers.Logger
import com.topjohnwu.superuser.io.SuFile
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.helpers.onCheckedChange
import de.Maxr1998.modernpreferences.helpers.onClick
import de.Maxr1998.modernpreferences.helpers.pref
import de.Maxr1998.modernpreferences.helpers.switch
import de.Maxr1998.modernpreferences.preferences.CategoryHeader
import de.Maxr1998.modernpreferences.preferences.SwitchPreference
import de.dertyp7214.rboardthememanager.Application
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.components.SearchBar
import de.dertyp7214.rboardthememanager.core.*
import de.dertyp7214.rboardthememanager.screens.PreferencesActivity
import de.dertyp7214.rboardthememanager.utils.FileUtils
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class Flags(val activity: Activity) : AbstractPreference() {
    enum class FILES(val filePath: String) {
        @SuppressLint("SdCardPath")
        FLAGS("/data/data/${Config.GBOARD_PACKAGE_NAME}/shared_prefs/flag_value.xml"),
        GBOARD_PREFERENCES(Config.GBOARD_PREFS_PATH),
        NONE("")
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
        val file: FILES = FILES.NONE,
        val valueMap: Map<Any?, Any?>? = null,
        val visible: Boolean = true,
        val linkedKeys: List<String> = listOf(),
        val onClick: () -> Unit = {}
    ) {
        SHOW_ALL_FLAGS(
            "show_all_flags",
            R.string.show_all_flags,
            R.string.show_all_flags_long,
            -1,
            "",
            TYPE.JUST_CLICK,
            FILES.NONE,
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
        ),
        EMPTY2(
            "empty2",
            -1,
            -1,
            -1,
            "",
            TYPE.GROUP,
            FILES.NONE
        );
    }

    data class FlagItem(
        val key: String,
        @StringRes val title: Int,
        @StringRes val summary: Int,
        @DrawableRes val icon: Int,
        val defaultValue: Any,
        val type: TYPE,
        val file: FILES = FILES.NONE,
        val valueMap: Map<Any?, Any?>? = null,
        val visible: Boolean = true,
        val linkedKeys: List<String> = listOf(),
        val onClick: () -> Unit = {}
    ) {
        constructor(enum: FLAGS) : this(
            enum.key,
            enum.title,
            enum.summary,
            enum.icon,
            enum.defaultValue,
            enum.type,
            enum.file,
            enum.valueMap,
            enum.visible,
            enum.linkedKeys,
            enum.onClick
        )

        @Suppress("UNCHECKED_CAST")
        fun <T> getValue(
            context: Context,
            defaultValue: T? = null,
            values: Map<String, Any> = Companion.values
        ): T? {
            fun <E> getVal(defaultValue: E? = null, valueMap: Map<Any?, Any?>?): E? {
                return if (valueMap != null) valueMap.filter {
                    if (it.isNumber()) it.equalsNumber(values[key])
                    else it.value == values[key]
                }.entries.firstOrNull()?.key as? E ?: defaultValue
                else values[key] as? E ?: defaultValue
            }
            return if (linkedKeys.isEmpty()) getVal(defaultValue, valueMap) else {
                val flags = getFlagItems(context)
                !linkedKeys.map { key ->
                    flags.find { flag -> flag.key == key }?.let {
                        it.getValue(context, it.defaultValue, values)
                    } ?: false
                }.contains(false) as? T ?: defaultValue
            }
        }

        @SuppressLint("SdCardPath")
        fun <T> setValue(v: T) =
            setValue(if (valueMap != null) valueMap[v] else v, key, file)
    }

    override fun getExtraView(): View? = null

    override fun preferences(builder: PreferenceScreen.Builder) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val preferences = hashMapOf<String, Preference>()
        val allFlags = ArrayList(getFlagItems(activity, true))
        val values = values
        allFlags.addAll(FLAGS.values().map { FlagItem(it) })
        allFlags.forEach { item ->
            prefs.edit { remove(item.key) }
            val pref: Preference = when (item.type) {
                TYPE.BOOLEAN -> SwitchPreference(item.key).apply {
                    defaultValue =
                        item.getValue(activity, item.defaultValue, values) as? Boolean ?: false
                    onCheckedChange {
                        if (!item.setValue(it)) Toast.makeText(
                            activity,
                            R.string.error,
                            Toast.LENGTH_SHORT
                        ).show()
                        item.linkedKeys.forEach { key ->
                            preferences[key]?.let { preference ->
                                if (preference is SwitchPreference) preference.checked = it
                            }
                        }
                        true
                    }
                }
                TYPE.INT, TYPE.LONG, TYPE.FLOAT -> Preference(item.key)
                TYPE.GROUP -> CategoryHeader(item.key)
                TYPE.STRING -> Preference(item.key).apply {}
                TYPE.JUST_CLICK -> Preference(item.key).apply {
                    onClick { item.onClick(); false }
                }
            }
            pref.apply {
                titleRes = item.title
                summaryRes = item.summary
                iconRes = item.icon
                visible = item.visible
                if (item.summary == -1) summary = item.key
            }
            preferences[item.key] = pref
            builder.addPreferenceItem(pref)
        }
    }

    class AllFlags(
        private val activity: Activity,
        private val requestReload: () -> Unit
    ) : AbstractPreference() {

        private var filter: String = ""

        private val searchBar = SearchBar(activity).apply {
            setOnSearchListener {
                filter = it
                requestReload()
            }
            setOnCloseListener {
                filter = ""
                requestReload()
            }
        }

        override fun getExtraView(): View = searchBar

        override fun preferences(builder: PreferenceScreen.Builder) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            getCurrentXmlValues(FILES.FLAGS.filePath, true).filter {
                filter.isEmpty() || it.key.contains(
                    filter,
                    true
                )
            }.forEach { entry ->
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
                        if (!setValue(it, entry.key, FILES.FLAGS)) Toast.makeText(
                            activity,
                            R.string.error,
                            Toast.LENGTH_SHORT
                        ).show()
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
    }

    companion object {
        private val flags = arrayListOf<FlagItem>()
        private var flagsString: EnumMap<FILES, String> = EnumMap(FILES::class.java)

        fun getFlagItems(context: Context, refresh: Boolean = false): List<FlagItem> {
            if (!refresh && flags.isNotEmpty()) return flags
            flags.clear()

            val json = File(context.applicationInfo.dataDir, "flags.json").let {
                if (!it.exists()) null
                else SafeJSON(JSONObject(it.readText())).getJSONArray("flags")
            }

            json?.forEach<JSONObject> { obj, _ ->
                val o = SafeJSON(obj)
                flags.add(
                    FlagItem(
                        o.getString("key"),
                        FileUtils.getResourceId(
                            context,
                            o.getString("title"),
                            "string",
                            context.packageName
                        ),
                        FileUtils.getResourceId(
                            context,
                            o.getString("summary"),
                            "string",
                            context.packageName
                        ),
                        FileUtils.getResourceId(
                            context,
                            o.getString("icon"),
                            "drawable",
                            context.packageName
                        ),
                        o.get("defaultValue"),
                        TYPE.valueOf(o.getString("type").uppercase()),
                        o.getString("file").let { file ->
                            if (file.isNotEmpty()) FILES.valueOf(file.uppercase())
                            else FILES.NONE
                        },
                        o.getJSONArray("valueMap").map<JSONObject, Pair<Any?, Any?>> { ob ->
                            Pair(ob.get("first").let {
                                when (SafeJSON(ob).getString("type1")) {
                                    "long" -> it.toString().toLong()
                                    "boolean" -> it.toString().toBooleanStrict()
                                    "float" -> it.toString().toFloat()
                                    "integer" -> it.toString().toInt()
                                    else -> it
                                }
                            }, ob.get("second").let {
                                when (SafeJSON(ob).getString("type2")) {
                                    "long" -> it.toString().toLong()
                                    "boolean" -> it.toString().toBooleanStrict()
                                    "float" -> it.toString().toFloat()
                                    "integer" -> it.toString().toInt()
                                    else -> it
                                }
                            })
                        }.toMap().let { map -> if (map.isEmpty()) null else map },
                        o.getBoolean("visible", true),
                        o.getJSONArray("linkedKeys").toList()
                    )
                )
            }

            return flags
        }

        val values: Map<String, Any>
            get() {
                return getCurrentXmlValues(FILES.GBOARD_PREFERENCES.filePath) +
                        getCurrentXmlValues(FILES.FLAGS.filePath)
            }

        @SuppressLint("SdCardPath")
        private fun getCurrentXmlValues(file: String, cached: Boolean = false): Map<String, Any> {
            return SuFile(file).readXML(
                if (cached) FILES.values().find { it.filePath == file }?.let { flagsString[it] }
                else null
            )
        }

        @SuppressLint("SdCardPath")
        fun applyChanges(): Boolean {
            FILES.values().filter { it != FILES.NONE }.forEach { file ->
                val fileName = file.filePath
                flagsString[file]?.let {
                    SuFile(fileName).writeFile(it.trim())
                }
            }

            return "am force-stop ${Config.GBOARD_PACKAGE_NAME}".runAsCommand()
        }

        @SuppressLint("SdCardPath")
        fun setUpFlags() {
            FILES.values().filter { it != FILES.NONE }.forEach { file ->
                val fileName = file.filePath
                flagsString[file] = SuFile(fileName).openStream()?.use {
                    it.bufferedReader().readText()
                } ?: "<map></map>"
            }
        }

        fun <T> setValue(value: T, key: String, file: FILES): Boolean {
            if (file == FILES.NONE) return true
            flagsString[file] = flagsString[file]?.let { fileText ->
                val type = when (value) {
                    is Boolean -> "boolean"
                    is Int -> "integer"
                    is Long -> "long"
                    is Float -> "float"
                    else -> "string"
                }
                Logger.log(
                    Logger.Companion.Type.DEBUG,
                    "CHANGE FLAG",
                    "value: $value key: $key type: $type"
                )
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

            return true
        }
    }
}