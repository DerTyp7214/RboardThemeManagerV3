package de.dertyp7214.rboardthememanager.preferences

import android.os.Build
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.murgupluoglu.flagkit.FlagKit
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.categoryHeader
import de.Maxr1998.modernpreferences.helpers.onClick
import de.Maxr1998.modernpreferences.helpers.onClickView
import de.Maxr1998.modernpreferences.helpers.pref
import de.dertyp7214.rboardthememanager.Config.PLAY_URL
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.SafeJSON
import de.dertyp7214.rboardthememanager.core.openUrl
import de.dertyp7214.rboardthememanager.core.isReachable
import de.dertyp7214.rboardthememanager.core.openDialog
import java.net.URL

class About(private val activity: AppCompatActivity, private val args: SafeJSON) :
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
        val options = RequestOptions
            .bitmapTransform(RoundedCorners(25))
            .placeholder(R.drawable.ic_person)
            .fallback(R.drawable.ic_person)
            .error(R.drawable.ic_person)
        val glide = Glide.with(activity)

        builder.categoryHeader("team") {
            titleRes = R.string.team
        }
        builder.pref("rkbdi") {
            titleRes = R.string.rkbdi
            summaryRes = R.string.owner_themer
            if (!URL(activity.getString(R.string.rkbdi_image)).isReachable()) {
                iconRes = R.drawable.ic_person
            }
            else{
                useTint = false
                icon = glide
                    .load(activity.getString(R.string.rkbdi_image))
                    .apply(options)
                    .submit(100, 100).get()
            }
            onClick {
                activity.openUrl(activity.getString(R.string.rkbdi_github))
                false
            }
        }
        builder.pref("dertyp") {
            titleRes = R.string.dertyp
            summaryRes = R.string.developer
            if (!URL(activity.getString(R.string.dertyp_image)).isReachable()) {
                iconRes = R.drawable.ic_person
            }
            else{
                useTint = false
                icon = glide
                    .load(activity.getString(R.string.dertyp_image))
                    .apply(options)
                    .submit(100, 100).get()
            }
            onClick {
                activity.openUrl(activity.getString(R.string.dertyp_github))
                false
            }
        }
        builder.pref("dan") {
            titleRes = R.string.dan
            summaryRes = R.string.moderator
            if (!URL(activity.getString(R.string.dan_image)).isReachable()) {
                iconRes = R.drawable.ic_person
            }
            else{
                useTint = false
                icon = glide
                    .load(activity.getString(R.string.dan_image))
                    .apply(options)
                    .submit(100, 100).get()
            }
            onClick {
                activity.openUrl(activity.getString(R.string.dan_github))
                false
            }
        }
        builder.categoryHeader("links") {
            titleRes = R.string.links
        }
        builder.pref("github_repo") {
            titleRes = R.string.github_repo
            iconRes = R.drawable.ic_github
            onClick {
                activity.openUrl(activity.getString(R.string.github_repo_url))
                false
            }
        }
        builder.pref("telegram_channel") {
            titleRes = R.string.telegram_channel
            iconRes = R.drawable.ic_tg
            onClick {
                activity.openUrl(activity.getString(R.string.telegram_channel_url))
                false
            }
        }
        builder.pref("telegram_group") {
            titleRes = R.string.telegram_group
            iconRes = R.drawable.ic_groups
            onClick {
                activity.openUrl(activity.getString(R.string.telegram_group_url))
                false
            }
        }
        builder.pref("xda_thread") {
            titleRes = R.string.xda_thread
            iconRes = R.drawable.ic_xda
            onClick {
                activity.openUrl(activity.getString(R.string.xda_thread_url))
                false
            }
        }
        builder.categoryHeader("rboard_apps") {
            titleRes = R.string.rboard_apps
        }
        builder.pref("more_rboard_apps") {
            titleRes = R.string.more_rboard_apps
            summaryRes = R.string.more_rboard_apps_desc
            iconRes = R.drawable.ic_rboard
            onClickView {
                activity.openDialog(R.layout.more_rboard_apps_dialog) { dialog ->
                    val title = findViewById<TextView>(R.id.TextViewApps)
                    title.text = activity.getString(R.string.more_rboard_apps)
                    val rboardThemePatcher = findViewById<TextView>(R.id.rboard_theme_patcher)
                    val rboardThemeCreator = findViewById<TextView>(R.id.rboard_theme_creator)
                    val rboardDeepLink = findViewById<TextView>(R.id.rboard_deep_link)
                    val rboardImeTester = findViewById<TextView>(R.id.rboard_ime_tester)

                    rboardDeepLink.setOnClickListener {
                        activity.packageManager.getLaunchIntentForPackage(
                            "de.dertyp7214.deeplinkrboard"
                        )?.let(activity::startActivity)
                            ?: activity.openUrl(activity.getString(R.string.rboard_deep_link))
                    }
                    rboardThemePatcher.setOnClickListener {
                        activity.packageManager.getLaunchIntentForPackage(
                            "de.dertyp7214.rboardpatcher"
                        )?.let(activity::startActivity)
                            ?: activity.openUrl(PLAY_URL("de.dertyp7214.rboardpatcher"))
                    }
                    rboardThemeCreator.setOnClickListener {
                        activity.packageManager.getLaunchIntentForPackage(
                            "de.dertyp7214.rboardthemecreator"
                        )?.let(activity::startActivity)
                            ?: activity.openUrl(PLAY_URL("de.dertyp7214.rboardthemecreator"))
                    }
                    rboardImeTester.setOnClickListener {
                        activity.packageManager.getLaunchIntentForPackage(
                            "de.dertyp7214.rboardimetester"
                        )?.let(activity::startActivity)
                            ?: activity.openUrl(PLAY_URL("de.dertyp7214.rboardimetester"))
                    }

                    findViewById<Button>(R.id.close)?.setOnClickListener { dialog.dismiss() }
                }
            }
        }
        builder.categoryHeader("translators") {
            titleRes = R.string.translators
        }
        builder.pref("ar") {
            titleRes = R.string.arabic
            summaryRes = R.string.muhammadbahaa2001
            useTint = false
            iconRes = FlagKit.getResId(activity, "arab")
        }
        builder.pref("pt-rBR") {
            titleRes = R.string.portuguese_brazilian
            summaryRes = R.string.igormiguell
            useTint = false
            iconRes = FlagKit.getResId(activity, "br")
        }
        builder.pref("zh")
        {
            titleRes = R.string.chinese
            summaryRes = R.string.contingency
            useTint = false
            iconRes = FlagKit.getResId(activity, "cn")
        }
        builder.pref("fr") {
            titleRes = R.string.french
            summaryRes = R.string.yoanndp
            useTint = false
            iconRes = FlagKit.getResId(activity, "fr")
        }
        builder.pref("it") {
            titleRes = R.string.italian
            summaryRes = R.string.alpha4041
            useTint = false
            iconRes = FlagKit.getResId(activity, "it")
        }
        builder.pref("pl") {
            titleRes = R.string.polish
            summaryRes = R.string.rkbdi
            useTint = false
            iconRes = FlagKit.getResId(activity, "pl")
        }
        builder.pref("ru") {
            titleRes = R.string.russian
            summaryRes = R.string.ruslan
            useTint = false
            iconRes = FlagKit.getResId(activity, "ru")
        }
        builder.pref("vn") {
            titleRes = R.string.vietnamese
            summaryRes = R.string.primal_pea
            useTint = false
            iconRes = FlagKit.getResId(activity, "vn")
        }
    }
}