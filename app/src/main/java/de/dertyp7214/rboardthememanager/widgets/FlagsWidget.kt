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
import de.dertyp7214.rboardthememanager.core.border
import de.dertyp7214.rboardthememanager.core.logo
import de.dertyp7214.rboardthememanager.core.monet
import de.dertyp7214.rboardthememanager.preferences.Flags
import kotlin.math.roundToInt

class FlagsWidget : AppWidgetProvider() {

    @Suppress("MemberVisibilityCanBePrivate")
    companion object {
        internal enum class FLAGS(val key: String) {
            MONET("monet"),
            G_LOGO("g_logo"),
            KEY_BORDER("key_border");
        }

        @SuppressLint("RemoteViewLayout")
        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            loadFlags().let { flags ->
                val pendingIntent = { flag: FLAGS ->
                    Intent(context, FlagsWidget::class.java).let { intent ->
                        intent.putExtra("flag", flag.key)
                        PendingIntent.getBroadcast(
                            context,
                            (Math.random() * 100).roundToInt(),
                            intent,
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    }
                }

                val views = RemoteViews(context.packageName, R.layout.flags_widget)

                views.setCompoundButtonChecked(R.id.monetCheckbox, flags[FLAGS.MONET] ?: false)
                views.setOnClickPendingIntent(R.id.monetCheckbox, pendingIntent(FLAGS.MONET))

                views.setCompoundButtonChecked(R.id.gLogoCheckbox, flags[FLAGS.G_LOGO] ?: false)
                views.setOnClickPendingIntent(R.id.gLogoCheckbox, pendingIntent(FLAGS.G_LOGO))

                views.setCompoundButtonChecked(
                    R.id.keyBorderCheckbox,
                    flags[FLAGS.KEY_BORDER] ?: false
                )
                views.setOnClickPendingIntent(
                    R.id.keyBorderCheckbox,
                    pendingIntent(FLAGS.KEY_BORDER)
                )

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }

        internal fun loadFlags(): Map<FLAGS, Boolean> {
            val values = Flags.values

            return mapOf(
                Pair(FLAGS.MONET, values.monet),
                Pair(FLAGS.G_LOGO, values.logo),
                Pair(FLAGS.KEY_BORDER, values.border)
            )
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

    override fun onEnabled(context: Context) {}
    override fun onDisabled(context: Context) {}

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        intent.getStringExtra("flag")?.let { flag ->
            Flags.setUpFlags()
            val values = Flags.values
            when (flag) {
                FLAGS.MONET.key -> {
                    val bool = !values.monet
                    Flags.setValue(bool, "use_silk_theme_by_default", Flags.FILES.FLAGS)
                    Flags.setValue(bool, "silk_on_all_pixel", Flags.FILES.FLAGS)
                    Flags.setValue(bool, "silk_theme", Flags.FILES.FLAGS)
                }
                FLAGS.G_LOGO.key -> {
                    if (values.logo) {
                        Flags.setValue(
                            false, "show_branding_on_space", Flags.FILES.FLAGS
                        )
                        Flags.setValue(
                            86400000L, "show_branding_interval_seconds", Flags.FILES.FLAGS
                        )
                        Flags.setValue(
                            900L, "branding_fadeout_delay_ms", Flags.FILES.FLAGS
                        )
                    } else {
                        Flags.setValue(
                            true, "show_branding_on_space", Flags.FILES.FLAGS
                        )
                        Flags.setValue(
                            0L, "show_branding_interval_seconds", Flags.FILES.FLAGS
                        )
                        Flags.setValue(
                            2147483647L, "branding_fadeout_delay_ms", Flags.FILES.FLAGS
                        )
                    }
                }
                FLAGS.KEY_BORDER.key -> {
                    Flags.setValue(
                        !values.border,
                        "enable_key_border",
                        Flags.FILES.GBOARD_PREFERENCES
                    )
                }
            }
            if (Flags.applyChanges())
                Toast.makeText(context, R.string.flag_applied, Toast.LENGTH_SHORT).show()
        }
        AppWidgetManager.getInstance(context).let { appWidgetManager ->
            onUpdate(
                context,
                appWidgetManager,
                appWidgetManager.getAppWidgetIds(ComponentName(context, FlagsWidget::class.java))
            )
        }
    }
}