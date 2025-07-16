package de.dertyp7214.rboardthememanager.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.tbuonomo.viewpagerdotsindicator.setBackgroundCompat
import de.dertyp7214.rboardcomponents.utils.doAsync
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.components.NewsCards
import de.dertyp7214.rboardthememanager.core.download
import de.dertyp7214.rboardthememanager.core.dpToPxRounded
import de.dertyp7214.rboardthememanager.core.fontSize
import de.dertyp7214.rboardthememanager.core.format
import de.dertyp7214.rboardthememanager.core.openDialog
import de.dertyp7214.rboardthememanager.core.openUrl
import de.dertyp7214.rboardthememanager.core.parseRepo
import de.dertyp7214.rboardthememanager.core.preferences
import de.dertyp7214.rboardthememanager.core.setMargin
import de.dertyp7214.rboardthememanager.core.toHumanReadableBytes
import de.dertyp7214.rboardthememanager.core.zeroOrNull
import de.dertyp7214.rboardthememanager.data.ThemePack
import de.dertyp7214.rboardthememanager.screens.InstallPackActivity

class ThemePackAdapter(
    private val context: Context,
    private val list: List<ThemePack>,
    private val activity: FragmentActivity,
    private val resultLauncher: ActivityResultLauncher<Intent>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    private val previousVisitThemes =
        context.preferences.getLong("previousVisitThemes", System.currentTimeMillis())

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val root: View = v.findViewById(R.id.root)
        val size: TextView = v.findViewById(R.id.size)
        val title: TextView = v.findViewById(R.id.title)
        val author: TextView = v.findViewById(R.id.author)
        val lastUpdate: TextView = v.findViewById(R.id.lastUpdate)
        val newTagLinearLayout: LinearLayout = v.findViewById(R.id.newTagLinearLayout)
        val card: MaterialCardView = v.findViewById(R.id.card)
    }

    class NewsViewHolder(v: NewsCards) : RecyclerView.ViewHolder(v) {
        val newsCards: NewsCards = v
    }

    override fun getItemId(position: Int): Long {
        return list[position].hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0.dpToPxRounded(context)) return NewsViewHolder(NewsCards(activity))
        return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.pack_item, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val themePack = list[position]

        if (holder is ViewHolder) {
            when (position) {
                1 -> {
                    if (list.size == 2){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                            holder.card.setBackgroundResource(R.drawable.color_surface_overlay_background_rounded)
                            val param = holder.card.layoutParams as ViewGroup.MarginLayoutParams
                            param.setMargins(16.dpToPxRounded(context), 0.dpToPxRounded(context), 16.dpToPxRounded(context), 0.dpToPxRounded(context))
                            holder.card.layoutParams = param

                        }
                        else{
                            holder.card.setBackgroundCompat(ContextCompat.getDrawable(context,R.drawable.color_surface_overlay_background_rounded))
                            val param = holder.card.layoutParams as ViewGroup.MarginLayoutParams
                            param.setMargins(16.dpToPxRounded(context), 0.dpToPxRounded(context), 16.dpToPxRounded(context), 0.dpToPxRounded(context))
                            holder.card.layoutParams = param

                        }
                    }
                    else{
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                            holder.card.setBackgroundResource(R.drawable.color_surface_overlay_background_top)
                            val param = holder.card.layoutParams as ViewGroup.MarginLayoutParams
                            param.setMargins(16.dpToPxRounded(context), 0.dpToPxRounded(context), 16.dpToPxRounded(context), 0.dpToPxRounded(context))
                            holder.card.layoutParams = param

                        }
                        else{
                            holder.card.setBackgroundCompat(ContextCompat.getDrawable(context,R.drawable.color_surface_overlay_background_top))
                            val param = holder.card.layoutParams as ViewGroup.MarginLayoutParams
                            param.setMargins(16.dpToPxRounded(context), 0.dpToPxRounded(context), 16.dpToPxRounded(context), 0.dpToPxRounded(context))
                            holder.card.layoutParams = param

                        }
                    }
                }
                list.lastIndex -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        holder.card.setBackgroundResource(R.drawable.color_surface_overlay_background_bottom)
                        val param = holder.card.layoutParams as ViewGroup.MarginLayoutParams
                        param.setMargins(16.dpToPxRounded(context), 0.dpToPxRounded(context), 16.dpToPxRounded(context), 4.dpToPxRounded(context))
                        holder.card.layoutParams = param
                    }
                    else{
                        holder.card.setBackgroundCompat(ContextCompat.getDrawable(context,R.drawable.color_surface_overlay_background_bottom))
                        val param = holder.card.layoutParams as ViewGroup.MarginLayoutParams
                        param.setMargins(16.dpToPxRounded(context), 0.dpToPxRounded(context), 16.dpToPxRounded(context), 4.dpToPxRounded(context))
                        holder.card.layoutParams = param
                    }
                }
                else -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        holder.card.setBackgroundResource(R.drawable.color_surface_overlay_background)
                        val param = holder.card.layoutParams as ViewGroup.MarginLayoutParams
                        param.setMargins(16.dpToPxRounded(context), 0.dpToPxRounded(context), 16.dpToPxRounded(context), 0.dpToPxRounded(context))
                        holder.card.layoutParams = param
                    }
                    else{
                        holder.card.setBackgroundCompat(ContextCompat.getDrawable(context,R.drawable.color_surface_overlay_background))
                        val param = holder.card.layoutParams as ViewGroup.MarginLayoutParams
                        param.setMargins(16.dpToPxRounded(context), 0.dpToPxRounded(context), 16.dpToPxRounded(context), 0.dpToPxRounded(context))
                        holder.card.layoutParams = param
                    }
                }
            }
            holder.size.text =
                "${themePack.themes?.size?.let { "($it)" } ?: ""} ${
                    themePack.size.zeroOrNull {
                        it.toHumanReadableBytes(
                            activity
                        )
                    } ?: ""
                }"
            holder.title.text = themePack.name
            holder.author.text = themePack.author
            holder.lastUpdate.text = themePack.date.format(System.currentTimeMillis())
            holder.newTagLinearLayout.visibility = if (themePack.date > previousVisitThemes) View.VISIBLE else View.GONE
            holder.lastUpdate.visibility = if (holder.newTagLinearLayout.isVisible) View.GONE else View.VISIBLE
            holder.size.visibility = if (holder.newTagLinearLayout.isVisible) View.GONE else View.VISIBLE

            holder.root.setOnClickListener {
                themePack.download(activity) {
                    resultLauncher.launch(
                        Intent(
                            activity,
                            InstallPackActivity::class.java
                        ).putStringArrayListExtra("themes", ArrayList(it))
                    )
                }
            }

            holder.root.setOnLongClickListener {
                activity.run {
                    openDialog(
                        TextUtils.concat(
                            themePack.description ?: getString(R.string.theme_pack),
                            "\n\n\n",
                            "Unknown Repo".fontSize(.6f)
                        ),
                        getString(R.string.description),
                        true,
                        R.string.download,
                        {
                            it.dismiss()
                            openUrl(themePack.url.let { url ->
                                if (url.startsWith("http")) url else "${Config.RAW_PREFIX}/$url"
                            })
                        }
                    ) {
                        it.dismiss()
                    }.apply {
                        doAsync(themePack.repoUrl::parseRepo) { repo ->
                            if (repo.meta?.name != null) {
                                val mainMessage =
                                    themePack.description ?: getString(R.string.theme_pack)
                                val footerSpan = repo.meta.name.fontSize(.6f)
                                setMessage(TextUtils.concat(mainMessage, "\n\n\n", footerSpan))
                            }
                        }
                    }
                }
                false
            }
        } else if (holder is NewsViewHolder) {
            holder.newsCards.setClickNewsListener { pack ->
                pack.download(activity) {
                    resultLauncher.launch(
                        Intent(
                            activity,
                            InstallPackActivity::class.java
                        ).putStringArrayListExtra("themes", ArrayList(it))
                    )
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int = if (list[position].none) 0 else 1

    override fun getItemCount(): Int = list.size
}