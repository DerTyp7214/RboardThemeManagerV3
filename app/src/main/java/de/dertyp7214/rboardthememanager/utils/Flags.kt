package de.dertyp7214.rboardthememanager.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.dertyp7214.logs.helpers.Logger
import com.topjohnwu.superuser.io.SuFile
import com.topjohnwu.superuser.io.SuFileInputStream
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
import de.dertyp7214.rboardthememanager.core.*
import de.dertyp7214.rboardthememanager.screens.PreferencesActivity
import org.json.JSONArray
import org.json.JSONObject
import org.xml.sax.InputSource
import java.io.File
import java.io.StringReader
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Flags(val context: Context) {
    enum class FILES(val fileName: String) {
        FLAGS("flag_value.xml"),
        GBOARD_PREFERENCES("${Config.GBOARD_PACKAGE_NAME}_preferences.xml"),
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
        );

        @Suppress("UNCHECKED_CAST")
        fun <T> getValue(defaultValue: T? = null): T? {
            fun <E> getVal(defaultValue: E? = null, valueMap: Map<Any?, Any?>?): E? {
                return if (valueMap != null) valueMap.filter {
                    if (it.isNumber()) it.equalsNumber(values[key])
                    else it.value == values[key]
                }.entries.firstOrNull()?.key as? E ?: defaultValue else values[key] as? E
                    ?: defaultValue
            }
            return if (linkedKeys.isEmpty()) getVal(defaultValue, valueMap) else {
                !linkedKeys.map { key ->
                    values().find { flag -> flag.key == key }?.let {
                        it.getValue(it.defaultValue)
                    } ?: false
                }.contains(false) as? T ?: defaultValue
            }
        }

        @SuppressLint("SdCardPath")
        fun <T> setValue(v: T) = setValue(if (valueMap != null) valueMap[v] else v, key, file)
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
        fun <T> getValue(context: Context, defaultValue: T? = null): T? {
            fun <E> getVal(defaultValue: E? = null, valueMap: Map<Any?, Any?>?): E? {
                return if (valueMap != null) valueMap.filter {
                    if (it.isNumber()) it.equalsNumber(values[key])
                    else it.value == values[key]
                }.entries.firstOrNull()?.key as? E ?: defaultValue else values[key] as? E
                    ?: defaultValue
            }
            return if (linkedKeys.isEmpty()) getVal(defaultValue, valueMap) else {
                !linkedKeys.map { key ->
                    getFlagItems(context).find { flag -> flag.key == key }?.let {
                        it.getValue(context, it.defaultValue)
                    } ?: false
                }.contains(false) as? T ?: defaultValue
            }
        }

        @SuppressLint("SdCardPath")
        fun <T> setValue(v: T) =
            setValue(if (valueMap != null) valueMap[v] else v, key, file)
    }

    fun preferences(builder: PreferenceScreen.Builder) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val preferences = hashMapOf<String, Preference>()
        val allFlags = ArrayList(getFlagItems(context, true))
        allFlags.addAll(FLAGS.values().map { FlagItem(it) })
        allFlags.forEach { item ->
            prefs.edit { remove(item.key) }
            val pref: Preference = when (item.type) {
                TYPE.BOOLEAN -> SwitchPreference(item.key).apply {
                    defaultValue = item.getValue(context, item.defaultValue) as? Boolean ?: false
                    onCheckedChange {
                        if (!item.setValue(it)) Toast.makeText(
                            context,
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

    fun allFlagsPreferences(builder: PreferenceScreen.Builder) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
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
                    if (!setValue(it, entry.key, FILES.FLAGS)) Toast.makeText(
                        context,
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

    companion object {
        private val flags = arrayListOf<FlagItem>()

        fun getFlagItems(context: Context, refresh: Boolean = false): List<FlagItem> {
            if (!refresh && flags.isNotEmpty()) return flags
            flags.clear()

            val json = File(context.applicationInfo.dataDir, "flags.json").let {
                if (!it.exists()) null
                else JSONArray(it.readText())
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
                val value = item.attributes?.getNamedItem("value")?.nodeValue?.let {
                    when (item.nodeName) {
                        "long" -> it.toLong()
                        "boolean" -> it.toBooleanStrict()
                        "float" -> it.toFloat()
                        "integer" -> it.toInt()
                        else -> it
                    }
                }
                if (name != null) output[name] = value ?: item.textContent ?: ""
            }

            return output
        }

        @SuppressLint("SdCardPath")
        private fun <T> setValue(value: T, key: String, file: FILES): Boolean {
            Log.d("VALUE", "$key $value $file")
            if (file == FILES.NONE) return true
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

            SuFile(fileName).writeFile(content)

            return "am force-stop ${Config.GBOARD_PACKAGE_NAME}".runAsCommand()
        }
    }
}