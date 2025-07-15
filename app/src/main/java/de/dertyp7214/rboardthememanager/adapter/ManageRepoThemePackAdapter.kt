package de.dertyp7214.rboardthememanager.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.tbuonomo.viewpagerdotsindicator.setBackgroundCompat
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.dpToPxRounded
import de.dertyp7214.rboardthememanager.data.ThemePack

class ManageRepoThemePackAdapter(private val context: Context,private val items: List<ThemePack>) :
    RecyclerView.Adapter<ManageRepoThemePackAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.title)
        val author: TextView = v.findViewById(R.id.author)
        val card: MaterialCardView = v.findViewById(R.id.card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.manage_repo_theme_pack, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        when (position) {
            0 -> {
                if (items.size == 1){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        holder.card.setBackgroundResource(R.drawable.color_surface_overlay_background_rounded)
                    }
                    else{
                        holder.card.setBackgroundCompat(ContextCompat.getDrawable(context,R.drawable.color_surface_overlay_background_rounded))
                    }
                }
                else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        holder.card.setBackgroundResource(R.drawable.color_surface_overlay_background_top)
                    }
                    else{
                        holder.card.setBackgroundCompat(ContextCompat.getDrawable(context,R.drawable.color_surface_overlay_background_top))
                    }
                }
            }
            items.lastIndex -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    holder.card.setBackgroundResource(R.drawable.color_surface_overlay_background_bottom)
                }
                else{
                    holder.card.setBackgroundCompat(ContextCompat.getDrawable(context,R.drawable.color_surface_overlay_background_bottom))
                }
            }
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    holder.card.setBackgroundResource(R.drawable.color_surface_overlay_background)
                }
                else{
                    holder.card.setBackgroundCompat(ContextCompat.getDrawable(context,R.drawable.color_surface_overlay_background))
                }
            }
        }
        holder.title.text = item.name
        holder.author.text = item.author
    }

    override fun getItemCount(): Int = items.size
}