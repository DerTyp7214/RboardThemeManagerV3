package de.dertyp7214.rboardthememanager.preferences

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
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
import de.Maxr1998.modernpreferences.helpers.pref
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.SafeJSON
import de.dertyp7214.rboardthememanager.core.openUrl

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
            useTint = false
            icon = glide
                .load(activity.getString(R.string.rkbdi_image))
                .apply(options)
                .submit(100, 100).get()
            onClick {
                activity.openUrl(activity.getString(R.string.rkbdi_github))
                false
            }
        }
        builder.pref("akos") {
            titleRes = R.string.akos_paha
            summaryRes = R.string.developer_themer
            useTint = false
            icon = glide
                .load(activity.getString(R.string.akos_image))
                .apply(options)
                .submit(100, 100).get()
            onClick {
                activity.openUrl(activity.getString(R.string.akos_github))
                false
            }
        }
        builder.pref("dertyp") {
            titleRes = R.string.dertyp
            summaryRes = R.string.developer
            useTint = false
            icon = glide
                .load(activity.getString(R.string.dertyp_image))
                .apply(options)
                .submit(100, 100).get()
            onClick {
                activity.openUrl(activity.getString(R.string.dertyp_github))
                false
            }
        }
        builder.pref("dan") {
            titleRes = R.string.dan
            summaryRes = R.string.moderator
            useTint = false
            icon = glide
                .load(activity.getString(R.string.dan_image))
                .apply(options)
                .submit(100, 100).get()
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
        builder.categoryHeader("translators") {
            titleRes = R.string.translators
        }
        builder.pref("fr") {
            titleRes = R.string.french
            summaryRes = R.string.akos_paha
            useTint = false
            iconRes = FlagKit.getResId(activity, "fr")
        }
        builder.pref("hu") {
            titleRes = R.string.hungarian
            summaryRes = R.string.yoanndp
            useTint = false
            iconRes = FlagKit.getResId(activity, "hu")
        }
        builder.pref("it") {
            titleRes = R.string.italian
            summaryRes = R.string.alessandro
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
        builder.pref("vi") {
            titleRes = R.string.vietnamese
            summaryRes = R.string.primal_pea
            useTint = false
            iconRes = FlagKit.getResId(activity, "vi")
        }
        builder.pref("zh") {
            titleRes = R.string.chinese
            summaryRes = R.string.contingency
            useTint = false
            iconRes = FlagKit.getResId(activity, "cn")
        }
    }
}
