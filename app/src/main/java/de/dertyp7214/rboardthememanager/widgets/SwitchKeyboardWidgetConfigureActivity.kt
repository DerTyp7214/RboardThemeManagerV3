@file:Suppress("DEPRECATION")

package de.dertyp7214.rboardthememanager.widgets

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.dertyp7214.rboardcomponents.utils.doAsync
import de.dertyp7214.rboardthememanager.adapter.ThemeAdapter
import de.dertyp7214.rboardthememanager.components.MarginItemDecoration
import de.dertyp7214.rboardthememanager.core.applyTheme
import de.dertyp7214.rboardthememanager.core.dpToPxRounded
import de.dertyp7214.rboardthememanager.data.ThemeDataClass
import de.dertyp7214.rboardthememanager.databinding.SwitchKeyboardWidgetConfigureBinding
import de.dertyp7214.rboardthememanager.utils.ThemeUtils

class SwitchKeyboardWidgetConfigureActivity : AppCompatActivity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: SwitchKeyboardWidgetConfigureBinding

    public override fun onCreate(icicle: Bundle?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            applyTheme(main = true)
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        }

        val view: View = window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.navigationBarColor = Color.TRANSPARENT
        }
        super.onCreate(icicle)

        setResult(RESULT_CANCELED)

        binding = SwitchKeyboardWidgetConfigureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val list = ArrayList<ThemeDataClass>()

        val adapter =
            ThemeAdapter(this, list, ThemeAdapter.SelectionState.NEVER, { _, _ -> }) { theme ->
                SwitchKeyboardWidget.saveThemePath(applicationContext, appWidgetId, theme.path)
                SwitchKeyboardWidget.updateAppWidget(
                    this,
                    AppWidgetManager.getInstance(this),
                    appWidgetId
                )
                setResult(
                    RESULT_OK,
                    Intent().apply { putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId) })
                finish()
            }

        recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(MarginItemDecoration(2.1.dpToPxRounded(this)))

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