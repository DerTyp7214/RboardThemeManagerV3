package de.dertyp7214.rboardthememanager.widgets

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.dpToPx
import de.dertyp7214.rboardthememanager.core.roundCorners
import de.dertyp7214.rboardthememanager.utils.ThemeUtils
import de.dertyp7214.rboardthememanager.utils.applyTheme

class SwitchKeyboardWidget : AppWidgetProvider() {

    @Suppress("MemberVisibilityCanBePrivate")
    companion object {
        private const val PREFS_NAME =
            "de.dertyp7214.rboardthememanager.widgets.SwitchKeyboardWidget"
        private const val PREF_PREFIX_KEY = "appwidget_"

        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            loadThemePath(context, appWidgetId)?.let { themeName ->
                val theme = ThemeUtils.getThemeData(themeName)
                val themeImage = theme.image?.roundCorners(
                    context.resources.getDimension(android.R.dimen.system_app_widget_background_radius)
                        .dpToPx(context).toInt(), theme.colorFilter
                )

                val pendingIntent =
                    Intent(context, SwitchKeyboardWidget::class.java).let { intent ->
                        intent.putExtra("themePath", theme.path)
                        PendingIntent.getBroadcast(
                            context,
                            appWidgetId,
                            intent,
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    }

                val views = RemoteViews(context.packageName, R.layout.switch_keyboard_widget)
                views.setImageViewBitmap(R.id.theme_image, themeImage)
                views.setOnClickPendingIntent(R.id.theme_image, pendingIntent)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }

        internal fun saveThemePath(context: Context, appWidgetId: Int, path: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.putString(PREF_PREFIX_KEY + appWidgetId, path)
            prefs.apply()
        }

        internal fun loadThemePath(context: Context, appWidgetId: Int): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            return prefs.getString(PREF_PREFIX_KEY + appWidgetId, null)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {

    }

    override fun onDisabled(context: Context) {

    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        intent.getStringExtra("themePath")?.let { themePath ->
            val theme = ThemeUtils.getThemeData(themePath)
            if (applyTheme(theme, true))
                Toast.makeText(context, R.string.applied, Toast.LENGTH_SHORT).show()
        }

        AppWidgetManager.getInstance(context).let { appWidgetManager ->
            onUpdate(
                context,
                appWidgetManager,
                appWidgetManager.getAppWidgetIds(
                    ComponentName(
                        context,
                        SwitchKeyboardWidget::class.java
                    )
                )
            )
        }
    }
}