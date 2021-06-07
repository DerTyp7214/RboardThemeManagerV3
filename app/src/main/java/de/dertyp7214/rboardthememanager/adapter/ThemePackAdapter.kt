package de.dertyp7214.rboardthememanager.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.download
import de.dertyp7214.rboardthememanager.data.ThemePack
import de.dertyp7214.rboardthememanager.screens.InstallPackActivity

class ThemePackAdapter(
    private val list: List<ThemePack>,
    private val activity: Activity,
    private val resultLauncher: ActivityResultLauncher<Intent>
) :
    RecyclerView.Adapter<ThemePackAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val root: View = v.findViewById(R.id.root)
        val title: TextView = v.findViewById(R.id.title)
        val author: TextView = v.findViewById(R.id.author)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.pack_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val themePack = list[position]

        holder.title.text = themePack.title
        holder.author.text = themePack.author

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
    }

    override fun getItemCount(): Int = list.size
}