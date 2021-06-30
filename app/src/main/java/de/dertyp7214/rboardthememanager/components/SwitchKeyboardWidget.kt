package de.dertyp7214.rboardthememanager.components

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
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
        val themePath = intent.getStringExtra("themePath")
        if (themePath != null) {
            val theme = ThemeUtils.getThemeData(themePath)
            if (applyTheme(theme, true, context))
                Toast.makeText(context, R.string.applied, Toast.LENGTH_SHORT).show()
        }
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val themeName = loadThemePath(context, appWidgetId)
    if (themeName != null) {
        val theme = ThemeUtils.getThemeData(themeName)
        val themeImage = theme.image?.roundCorners(
            context.resources.getDimension(android.R.dimen.system_app_widget_background_radius)
                .dpToPx(context).toInt(), theme.colorFilter
        )

        val pendingIntent = Intent(context, SwitchKeyboardWidget::class.java).let { intent ->
            intent.putExtra("themePath", theme.path)
            PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        val views = RemoteViews(context.packageName, R.layout.switch_keyboard_widget)
        views.setImageViewBitmap(R.id.theme_image, themeImage)
        views.setOnClickPendingIntent(R.id.theme_image, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}