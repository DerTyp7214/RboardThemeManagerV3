package de.dertyp7214.rboardthememanager.preferences

import android.content.Context
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.categoryHeader
import de.Maxr1998.modernpreferences.helpers.onClick
import de.Maxr1998.modernpreferences.helpers.pref
import de.dertyp7214.rboardthememanager.Config.GBOARD_PACKAGE_NAME
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.SafeJSON
import de.dertyp7214.rboardthememanager.core.forEach
import de.dertyp7214.rboardthememanager.core.getSystemProperty
import de.dertyp7214.rboardthememanager.core.openInputDialogFlag
import de.dertyp7214.rboardthememanager.core.runAsCommand
import de.dertyp7214.rboardthememanager.core.safeIcon
import de.dertyp7214.rboardthememanager.core.safeString
import de.dertyp7214.rboardthememanager.core.setSystemProperty
import de.dertyp7214.rboardthememanager.utils.FileUtils
import org.json.JSONObject
import java.io.File

class Props(private val activity: AppCompatActivity, private val args: SafeJSON) :
    AbstractMenuPreference() {
    override fun loadMenu(menuInflater: MenuInflater, menu: Menu?) {}
    override fun onMenuClick(menuItem: MenuItem): Boolean = false
    override fun onBackPressed(callback: () -> Unit) = callback()
    override fun getExtraView(): View? = null

    override fun onStart(recyclerView: RecyclerView, adapter: PreferencesAdapter) {
        adapter.currentScreen.indexOf(args.getString("highlight"))
            .let { if (it >= 0) recyclerView.scrollToPosition(it) }
    }

    enum class TYPE {
        GROUP,
        STRING
    }

    enum class PROPS(
        val key: String,
        val title: String,
        @StringRes val summary: Int,
        @DrawableRes val icon: Int,
        val defaultValue: Any,
        val type: TYPE,
        val visible: Boolean = true,
        val onClick: () -> Unit = {}
    )

    data class PropItem(
        val key: String,
        val title: String,
        @StringRes val summary: Int,
        @DrawableRes val icon: Int,
        val defaultValue: Any,
        val type: TYPE,
        val visible: Boolean = true,
        val onClick: () -> Unit = {}
    ) {
        constructor(enum: PROPS) : this(
            enum.key,
            enum.title,
            enum.summary,
            enum.icon,
            enum.defaultValue,
            enum.type,
            enum.visible,
            enum.onClick
        )
    }

    companion object {
        var changes: Boolean = false

        val props = arrayListOf<PropItem>()

        fun getPropItems(context: Context, refresh: Boolean = false): ArrayList<PropItem> {
            if (!refresh && props.isNotEmpty()) return props
            props.clear()

            val json = File(context.applicationInfo.dataDir, "props.json").let {
                if (!it.exists()) null
                else SafeJSON(JSONObject(it.readText())).getJSONArray("props")
            }

            json?.forEach<JSONObject> { obj, _ ->
                val o = SafeJSON(obj)
                props.add(
                    PropItem(
                        o.getString("key"),
                        o.getString("title"),
                        FileUtils.getResourceId(
                            context,
                            o.getString("summary").toString(),
                            "string",
                            context.packageName
                        ),
                        FileUtils.getResourceId(
                            context,
                            o.getString("icon"),
                            "drawable",
                            context.packageName
                        ),
                        o["defaultValue"],
                        TYPE.valueOf(o.getString("type").uppercase()),
                        o.getBoolean("visible", true)
                    )
                )
            }

            return props
        }
    }

    override fun preferences(builder: PreferenceScreen.Builder) {
        val getString = { key: String ->
            FileUtils.getResourceId(
                activity,
                key,
                "string",
                activity.packageName
            )
        }
        val getIcon = { key: String ->
            FileUtils.getResourceId(
                activity,
                key,
                "drawable",
                activity.packageName
            )
        }

        val prefs = ArrayList(getPropItems(activity, true))
        prefs.addAll(PROPS.entries.map { PropItem(it) })
        prefs.forEach { key ->
            if (key.title.startsWith("ch_")) builder.categoryHeader(key.title) {
                titleRes = getString(key.title.removePrefix("ch_")).safeString
                visible = key.visible
            }
            else builder.pref(key.toString()) {
                val value = "ro.com.google.ime.${key.key}".getSystemProperty()
                summary = value.ifBlank { activity.getString(R.string.not_set) }
                iconRes = getIcon(key.icon.toString()).safeIcon
                titleRes = getString(key.title.toString()).safeString
                visible = key.visible
                onClick {
                    activity.openInputDialogFlag(
                        activity.resources.getString(getString(key.title).safeString),
                        if (key.title.toString() == "top_icon_num") {
                            R.string.top_icon_num_long
                        } else {
                            R.string.not_set
                        },
                        "ro.com.google.ime.${key.key}".getSystemProperty(),
                        R.string.reset,
                        {
                            it.dismiss()
                            "ro.com.google.ime.${key.key}".setSystemProperty(saveToModule = true)
                            if ("am force-stop $GBOARD_PACKAGE_NAME".runAsCommand()) {
                                Toast.makeText(
                                    activity,
                                    R.string.prop_changed,
                                    Toast.LENGTH_SHORT
                                ).show()
                                summary = activity.getString(R.string.not_set)
                                requestRebind()
                            } else Toast.makeText(activity, R.string.error, Toast.LENGTH_SHORT)
                                .show()
                        }
                    ) { dialogInterface, text ->
                        dialogInterface.dismiss()
                        "ro.com.google.ime.${key.key}".setSystemProperty(text, true)
                        if ("am force-stop $GBOARD_PACKAGE_NAME".runAsCommand()) {
                            Toast.makeText(
                                activity,
                                R.string.prop_changed,
                                Toast.LENGTH_SHORT
                            ).show()
                            summary = text
                            requestRebind()
                        } else Toast.makeText(activity, R.string.error, Toast.LENGTH_SHORT)
                            .show()
                    }
                    false
                }
            }
        }
    }
}