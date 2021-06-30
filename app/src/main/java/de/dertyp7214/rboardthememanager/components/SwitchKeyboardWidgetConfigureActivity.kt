package de.dertyp7214.rboardthememanager.components

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.dertyp7214.rboardthememanager.adapter.ThemeAdapter
import de.dertyp7214.rboardthememanager.data.ThemeDataClass
import de.dertyp7214.rboardthememanager.databinding.SwitchKeyboardWidgetConfigureBinding
import de.dertyp7214.rboardthememanager.utils.ThemeUtils
import de.dertyp7214.rboardthememanager.utils.doAsync

class SwitchKeyboardWidgetConfigureActivity : Activity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: SwitchKeyboardWidgetConfigureBinding

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        setResult(RESULT_CANCELED)

        binding = SwitchKeyboardWidgetConfigureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val list = ArrayList<ThemeDataClass>()

        val adapter =
            ThemeAdapter(this, list, ThemeAdapter.SelectionState.NEVER, { _, _ -> }) { theme ->
                saveThemePath(applicationContext, appWidgetId, theme.path)
                updateAppWidget(this, AppWidgetManager.getInstance(this), appWidgetId)
                setResult(
                    RESULT_OK,
                    Intent().apply { putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId) })
                finish()
            }

        recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        doAsync(ThemeUtils::loadThemes) { themes ->
            list.addAll(themes)
            adapter.notifyDataChanged()
        }

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
    }

}

private const val PREFS_NAME = "de.dertyp7214.rboardthememanager.components.SwitchKeyboardWidget"
private const val PREF_PREFIX_KEY = "appwidget_"

internal fun saveThemePath(context: Context, appWidgetId: Int, path: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.putString(PREF_PREFIX_KEY + appWidgetId, path)
    prefs.apply()
}

internal fun loadThemePath(context: Context, appWidgetId: Int): String? {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0)
    return prefs.getString(PREF_PREFIX_KEY + appWidgetId, null)
}

internal fun deleteThemePath(context: Context, appWidgetId: Int) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.remove(PREF_PREFIX_KEY + appWidgetId)
    prefs.apply()
}