package de.dertyp7214.rboardthememanager.preferences

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.categoryHeader
import de.Maxr1998.modernpreferences.helpers.onClick
import de.Maxr1998.modernpreferences.helpers.pref
import de.dertyp7214.rboardthememanager.Application
import de.dertyp7214.rboardthememanager.Config.GBOARD_PACKAGE_NAME
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.SafeJSON
import de.dertyp7214.rboardthememanager.core.getSystemProperty
import de.dertyp7214.rboardthememanager.core.openInputDialogFlag
import de.dertyp7214.rboardthememanager.core.runAsCommand
import de.dertyp7214.rboardthememanager.core.safeIcon
import de.dertyp7214.rboardthememanager.core.safeString
import de.dertyp7214.rboardthememanager.core.setSystemProperty
import de.dertyp7214.rboardthememanager.utils.FileUtils
import de.dertyp7214.rboardthememanager.utils.GboardUtils

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

    override fun preferences(builder: PreferenceScreen.Builder) {
        val prefs = if (Application.context?.resources?.getBoolean(R.bool.isTab_or_fold) == true && GboardUtils.getGboardVersionCode(
                activity) < 153612662){
            listOf(
                "ch_margins",

                "kb_pad_port_b",
                "kb_pad_port_l",
                "kb_pad_port_r",

                "ch_margins_landscape",

                "kb_pad_land_b",
                "kb_pad_land_l",
                "kb_pad_land_r",

                "ch_margins_fold",

                "kbp_fport_b",
                "kbp_fport_l",
                "kbp_fport_r",

                "ch_margins_landscape_fold",

                "kbp_fland_b",
                "kbp_fland_l",
                "kbp_fland_r",

                "ch_radius",

                "corner_key_r"
            )}
        else if (Application.context?.resources?.getBoolean(R.bool.isTab_or_fold) == true && GboardUtils.getGboardVersionCode(
                activity) >= 153612662){
            listOf(
                "ch_margins",

                "kb_pad_port_b",
                "kb_pad_port_l",
                "kb_pad_port_r",

                "ch_margins_landscape",

                "kb_pad_land_b",
                "kb_pad_land_l",
                "kb_pad_land_r",

                "ch_margins_fold",

                "kbp_fport_b",
                "kbp_fport_l",
                "kbp_fport_r",

                "ch_margins_landscape_fold",

                "kbp_fland_b",
                "kbp_fland_l",
                "kbp_fland_r"
            )}
        else if (Application.context?.resources?.getBoolean(R.bool.isTab_or_fold) == false && GboardUtils.getGboardVersionCode(activity)  >= 153612662 ){
            listOf(
                "ch_margins",

                "kb_pad_port_b",
                "kb_pad_port_l",
                "kb_pad_port_r",

                "ch_margins_landscape",

                "kb_pad_land_b",
                "kb_pad_land_l",
                "kb_pad_land_r"
            )}
        else {
            listOf(
                "ch_margins",

                "kb_pad_port_b",
                "kb_pad_port_l",
                "kb_pad_port_r",

                "ch_margins_landscape",

                "kb_pad_land_b",
                "kb_pad_land_l",
                "kb_pad_land_r",

                "ch_radius",

                "corner_key_r"
            )
        }

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

        prefs.forEach { key ->
            if (key.startsWith("ch_")) builder.categoryHeader(key) {
                titleRes = getString(key.removePrefix("ch_")).safeString
            }
            else builder.pref(key) {
                val value = "ro.com.google.ime.$key".getSystemProperty()
                titleRes = getString(key).safeString
                summary = value.ifBlank { activity.getString(R.string.not_set) }
                iconRes = getIcon("ic_$key").safeIcon
                onClick {
                    activity.openInputDialogFlag(
                        activity.getResources().getString(getString(key).safeString),
                        R.string.nothing,
                        "ro.com.google.ime.$key".getSystemProperty(),
                        R.string.reset,
                        {
                            it.dismiss()
                            "ro.com.google.ime.$key".setSystemProperty(saveToModule = true)
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
                        "ro.com.google.ime.$key".setSystemProperty(text, true)
                        if ("am force-stop $GBOARD_PACKAGE_NAME".runAsCommand()) {
                            Toast.makeText(
                                activity,
                                R.string.prop_changed,
                                Toast.LENGTH_SHORT
                            ).show()
                            summary = text
                            requestRebind()
                        } else Toast.makeText(activity, R.string.error, Toast.LENGTH_SHORT).show()
                    }
                    false
                }
            }
        }
    }
}
