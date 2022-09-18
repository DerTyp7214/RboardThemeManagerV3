package de.dertyp7214.rboardthememanager.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.components.NewsCards
import de.dertyp7214.rboardthememanager.core.*
import de.dertyp7214.rboardthememanager.data.ThemePack
import de.dertyp7214.rboardthememanager.screens.InstallPackActivity
import de.dertyp7214.rboardcomponents.utils.doAsync

class ThemePackAdapter(
    private val list: List<ThemePack>,
    private val activity: Activity,
    private val resultLauncher: ActivityResultLauncher<Intent>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val root: View = v.findViewById(R.id.root)
        val size: TextView = v.findViewById(R.id.size)
        val title: TextView = v.findViewById(R.id.title)
        val author: TextView = v.findViewById(R.id.author)
        val lastUpdate: TextView = v.findViewById(R.id.lastUpdate)
    }

    class NewsViewHolder(v: NewsCards) : RecyclerView.ViewHolder(v) {
        val newsCards: NewsCards = v
    }

    override fun getItemId(position: Int): Long {
        return list[position].hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) return NewsViewHolder(NewsCards(activity))
        return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.pack_item, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val themePack = list[position]

        if (holder is ViewHolder) {
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
                            openUrl(themePack.url)
                        }
                    ) {
                        it.dismiss()
                    }.apply {
                        doAsync(themePack.repoUrl::parseRepo) { repo ->
                            if (repo?.meta?.name != null) {
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