package de.dertyp7214.rboardthememanager.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.openDialog
import de.dertyp7214.rboardthememanager.data.SoundPack
import java.io.File

class SoundPackAdapter(
    private val list: List<SoundPack>,
    private val activity: Activity
) : RecyclerView.Adapter<SoundPackAdapter.ViewHolder>() {
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val root: View = v.findViewById(R.id.root)
        val title: TextView = v.findViewById(R.id.title)
        val image: ImageView = v.findViewById(R.id.image)
        val author: TextView = v.findViewById(R.id.author)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.pack_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.title.text = item.title
        holder.author.text = item.author

        holder.image.setImageResource(R.drawable.ic_sounds)

        holder.root.setOnClickListener {
            TODO("DOWNLOAD SOUNDS")
            val sounds = arrayListOf<File>()
            activity.openDialog(R.layout.soundpack_dialog) { dialog ->
                val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

                recyclerView.setHasFixedSize(true)
                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                    override fun onCreateViewHolder(
                        parent: ViewGroup,
                        viewType: Int
                    ): ViewHolder {
                        return ViewHolder(TextView(activity))
                    }

                    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                        val sound = sounds[position]

                        holder.itemView.let {
                            if (it is TextView) it.text = sound.name
                        }
                    }

                    override fun getItemCount(): Int = sounds.size

                }

                findViewById<Button>(R.id.ok)?.setOnClickListener {
                    dialog.dismiss()
                }
                findViewById<Button>(R.id.cancel)?.setOnClickListener { dialog.dismiss() }
            }
        }
    }

    override fun getItemCount(): Int = list.size
}