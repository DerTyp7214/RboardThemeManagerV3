package de.dertyp7214.rboardthememanager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.data.ThemePack

class ManageRepoThemePackAdapter(private val items: List<ThemePack>) :
    RecyclerView.Adapter<ManageRepoThemePackAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.title)
        val author: TextView = v.findViewById(R.id.author)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.manage_repo_theme_pack, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.title.text = item.name
        holder.author.text = item.author
    }

    override fun getItemCount(): Int = items.size
}