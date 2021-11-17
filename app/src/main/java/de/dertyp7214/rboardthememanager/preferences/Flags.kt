package de.dertyp7214.rboardthememanager.preferences

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.*
import de.Maxr1998.modernpreferences.preferences.CategoryHeader
import de.Maxr1998.modernpreferences.preferences.SwitchPreference
import de.Maxr1998.modernpreferences.preferences.choice.SelectionItem
import de.Maxr1998.modernpreferences.preferences.choice.SingleChoiceDialogPreference
import de.dertyp7214.rboardthememanager.Application
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.components.SearchBar
import de.dertyp7214.rboardthememanager.components.XMLEntry
import de.dertyp7214.rboardthememanager.components.XMLFile
import de.dertyp7214.rboardthememanager.core.*
import de.dertyp7214.rboardthememanager.screens.PreferencesActivity
import de.dertyp7214.rboardthememanager.screens.ShareFlags
import de.dertyp7214.rboardthememanager.utils.FileUtils
import de.dertyp7214.rboardthememanager.utils.GboardUtils
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

@SuppressLint("ShowToast")
class Flags(val activity: Activity, private val args: SafeJSON) : AbstractPreference() {
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
        SELECT,
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
        val minSdk: Int = 0,
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
        SHOW_ALL_PREFERENCES(
            "show_all_preferences",
            R.string.show_all_preferences,
            R.string.show_all_preferences_long,
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
                        putExtra("type", "all_preferences")
                    }
                }
            }
        ),
        OTHER_PROPS(
            "other_props",
            R.string.other_props,
            R.string.other_props_long,
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
                        putExtra("type", "props")
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
        val minSdk: Int = 0,
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
            enum.minSdk,
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

    override fun onStart(recyclerView: RecyclerView, adapter: PreferencesAdapter) {
        adapter.currentScreen.indexOf(args.getString("highlight"))
            .let { if (it >= 0) recyclerView.scrollToPosition(it) }
    }

    override fun onBackPressed(callback: () -> Unit) {
        callback()
    }


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
                        else Snackbar.make(
                            activity.findViewById(android.R.id.content),
                            R.string.press_back_to_apply,
                            Snackbar.LENGTH_LONG
                        ).showMaterial()
                        item.linkedKeys.forEach { key ->
                            preferences[key]?.let { preference ->
                                if (preference is SwitchPreference) preference.checked = it
                            }
                        }
                        true
                    }
                }
                TYPE.SELECT -> {
                    SingleChoiceDialogPreference(item.key, item.valueMap?.map { (key, _) ->
                        if (key is String) SelectionItem(
                            key,
                            FileUtils.getResourceId(
                                activity,
                                key,
                                "string",
                                activity.packageName
                            ).safeString
                        )
                        else SelectionItem("", "")
                    } ?: listOf()).apply {
                        item.valueMap?.let { map ->
                            map.flip()[flagsString[item.file]?.getValue(item.key)?.value
                                ?: item.defaultValue].toString()
                        }?.let { initialSelection = it }
                        defaultOnSelectionChange(item::setValue)
                    }
                }
                TYPE.INT, TYPE.LONG, TYPE.FLOAT -> Preference(item.key)
                TYPE.GROUP -> CategoryHeader(item.key)
                TYPE.STRING -> Preference(item.key)
                TYPE.JUST_CLICK -> Preference(item.key).apply {
                    onClick { item.onClick(); false }
                }
            }
            pref.apply {
                titleRes = item.title.safeString
                summaryRes = item.summary.safeString
                iconRes = item.icon.safeIcon
                visible = item.visible && item.minSdk <= Build.VERSION.SDK_INT
                if (item.summary == -1) summary = item.key
                if (listOf(TYPE.INT, TYPE.LONG, TYPE.FLOAT, TYPE.STRING).contains(item.type))
                    onClick {
                        activity.openInputDialog(
                            R.string.nothing,
                            item.defaultValue.toString()
                        ) { dialogInterface, text ->
                            dialogInterface.dismiss()
                            summary = text
                            requestRebind()
                            val xmlValue = flagsString[FILES.FLAGS]?.getValue(item.key)
                            if (xmlValue != null) flagsString[FILES.FLAGS]?.setValue(
                                XMLEntry(
                                    xmlValue.name,
                                    text,
                                    xmlValue.type
                                )
                            )?.also {
                                changes = true
                                Snackbar.make(
                                    activity.findViewById(android.R.id.content),
                                    R.string.press_back_to_apply,
                                    Snackbar.LENGTH_LONG
                                ).showMaterial()
                            }
                        }
                        false
                    }
            }
            preferences[item.key] = pref
            builder.addPreferenceItem(pref)
        }
    }

    class AllFlags(
        private val activity: Activity,
        private val args: SafeJSON,
        private val requestReload: () -> Unit
    ) : AbstractPreference() {

        private var filter: String = ""
        private var onlyDisabled: Boolean = false

        private val searchBar = SearchBar(activity).apply {
            id = R.id.search_bar
            setOnSearchListener {
                filter = it
                requestReload()
            }
            setOnCloseListener {
                filter = ""
                requestReload()
            }
            setMenu(R.menu.all_flags) {
                when (it.itemId) {
                    R.id.only_disabled -> {
                        it.isChecked = !it.isChecked
                        onlyDisabled = it.isChecked
                        requestReload()
                    }
                    R.id.share_flags -> ShareFlags::class.java.start(activity)
                }
                true
            }
        }

        override fun getExtraView(): View = searchBar

        override fun onStart(recyclerView: RecyclerView, adapter: PreferencesAdapter) {
            args.getString("input").let { input ->
                if (input.isNotEmpty()) activity.findViewById<SearchBar>(R.id.search_bar).apply {
                    text = input
                    search()
                }
            }

            adapter.currentScreen.indexOf(args.getString("highlight"))
                .let { if (it >= 0) recyclerView.scrollToPosition(it) }
        }

        override fun onBackPressed(callback: () -> Unit) {
            callback()
        }

        override fun preferences(builder: PreferenceScreen.Builder) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            getCurrentXmlValues(FILES.FLAGS.filePath, true).filter {
                (filter.isEmpty() || it.key.contains(
                    filter,
                    true
                )) && onlyDisabled.let { b -> if (b) it.value is Boolean && it.value == false else true }
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
                        else Snackbar.make(
                            activity.findViewById(android.R.id.content),
                            R.string.press_back_to_apply,
                            Snackbar.LENGTH_LONG
                        ).showMaterial()
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
                        onClick {
                            activity.openInputDialog(
                                R.string.nothing,
                                summary?.toString()
                            ) { dialogInterface, text ->
                                dialogInterface.dismiss()
                                summary = text
                                requestRebind()
                                val xmlValue = flagsString[FILES.FLAGS]?.getValue(entry.key)
                                if (xmlValue != null) flagsString[FILES.FLAGS]?.setValue(
                                    XMLEntry(
                                        xmlValue.name,
                                        text,
                                        xmlValue.type
                                    ).setValue(text)
                                )?.also {
                                    changes = true
                                    Snackbar.make(
                                        activity.findViewById(android.R.id.content),
                                        R.string.press_back_to_apply,
                                        Snackbar.LENGTH_LONG
                                    ).showMaterial()
                                }
                            }
                            false
                        }
                    }
                }
            }
        }
    }

    class AllPreferences(
        private val activity: Activity,
        private val args: SafeJSON,
        private val requestReload: () -> Unit
    ) : AbstractPreference() {

        private var filter: String = ""
        private var onlyDisabled: Boolean = false

        private val searchBar = SearchBar(activity).apply {
            id = R.id.search_bar
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

        override fun onStart(recyclerView: RecyclerView, adapter: PreferencesAdapter) {
            args.getString("input").let { input ->
                if (input.isNotEmpty()) activity.findViewById<SearchBar>(R.id.search_bar).apply {
                    text = input
                    search()
                }
            }

            adapter.currentScreen.indexOf(args.getString("highlight"))
                .let { if (it >= 0) recyclerView.scrollToPosition(it) }
        }

        override fun onBackPressed(callback: () -> Unit) {
            callback()
        }

        override fun preferences(builder: PreferenceScreen.Builder) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            getCurrentXmlValues(FILES.GBOARD_PREFERENCES.filePath, true).filter {
                (filter.isEmpty() || it.key.contains(
                    filter,
                    true
                )) && onlyDisabled.let { b -> if (b) it.value is Boolean && it.value == false else true }
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
                        if (!setValue(it, entry.key, FILES.GBOARD_PREFERENCES)) Toast.makeText(
                            activity,
                            R.string.error,
                            Toast.LENGTH_SHORT
                        ).show()
                        else Snackbar.make(
                            activity.findViewById(android.R.id.content),
                            R.string.press_back_to_apply,
                            Snackbar.LENGTH_LONG
                        ).showMaterial()
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
                        onClick {
                            activity.openInputDialog(
                                R.string.nothing,
                                summary?.toString()
                            ) { dialogInterface, text ->
                                dialogInterface.dismiss()
                                summary = text
                                requestRebind()
                                val xmlValue =
                                    flagsString[FILES.GBOARD_PREFERENCES]?.getValue(entry.key)
                                if (xmlValue != null) flagsString[FILES.GBOARD_PREFERENCES]?.setValue(
                                    XMLEntry(
                                        xmlValue.name,
                                        text,
                                        xmlValue.type
                                    ).setValue(text)
                                )?.also {
                                    changes = true
                                    Snackbar.make(
                                        activity.findViewById(android.R.id.content),
                                        R.string.press_back_to_apply,
                                        Snackbar.LENGTH_LONG
                                    ).showMaterial()
                                }
                            }
                            false
                        }
                    }
                }
            }
        }
    }

    companion object {
        private var changes: Boolean = false
        private val flags = arrayListOf<FlagItem>()
        private var flagsString: EnumMap<FILES, XMLFile> = EnumMap(FILES::class.java)

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
                                    "string" -> it.toString()
                                    else -> it
                                }
                            }, ob.get("second").let {
                                when (SafeJSON(ob).getString("type2")) {
                                    "long" -> it.toString().toLong()
                                    "boolean" -> it.toString().toBooleanStrict()
                                    "float" -> it.toString().toFloat()
                                    "integer" -> it.toString().toInt()
                                    "string" -> it.toString()
                                    else -> it
                                }
                            })
                        }.toMap().ifEmpty { null },
                        o.getBoolean("visible", true),
                        o.getInt("minSdk", 0),
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

        val flagValues: Map<String, Any>
            get() {
                return getCurrentXmlValues(FILES.FLAGS.filePath)
            }


        @SuppressLint("SdCardPath")
        private fun getCurrentXmlValues(file: String, cached: Boolean = false): Map<String, Any> {
            val xmlFileName = FILES.values().find { it.filePath == file }
            val xmlFile = flagsString[xmlFileName]
            return if (cached && xmlFile != null) xmlFile.simpleMap()
            else XMLFile(file).also { flagsString[xmlFileName] = it }.simpleMap()
        }

        @SuppressLint("SdCardPath")
        fun applyChanges(): Boolean {
            if (!changes) return false
            changes = false
            FILES.values().filter { it != FILES.NONE }.forEach { file ->
                flagsString[file]?.let {
                    if (file == FILES.FLAGS) GboardUtils.updateCurrentFlags(it.toString())
                    it.writeFile()
                }
            }

            return "am force-stop ${Config.GBOARD_PACKAGE_NAME}".runAsCommand()
        }

        @SuppressLint("SdCardPath")
        fun setUpFlags() {
            changes = false
            FILES.values().filter { it != FILES.NONE }.forEach { file ->
                flagsString[file] = XMLFile(file.filePath)
            }
        }

        fun <T> setValue(value: T, key: String, file: FILES): Boolean {
            if (file == FILES.NONE) return true
            changes = true
            if (value != null) flagsString[file]?.setValue(XMLEntry.parse(key, value))
            return true
        }
    }
}