package de.dertyp7214.rboardthememanager.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.topjohnwu.superuser.io.SuFile
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.copyRecursively
import de.dertyp7214.rboardthememanager.core.download
import de.dertyp7214.rboardthememanager.core.format
import de.dertyp7214.rboardthememanager.core.openDialog
import de.dertyp7214.rboardthememanager.core.showMaterial
import de.dertyp7214.rboardthememanager.core.toHumanReadableBytes
import de.dertyp7214.rboardthememanager.core.zeroOrNull
import de.dertyp7214.rboardthememanager.data.SoundPack
import de.dertyp7214.rboardthememanager.utils.RootUtils
import de.dertyp7214.rboardthememanager.utils.getSoundsDirectory
import java.io.File
import java.io.FileInputStream

class SoundPackAdapter(
    private val list: List<SoundPack>,
    private val activity: Activity
) : RecyclerView.Adapter<SoundPackAdapter.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val root: View = v.findViewById(R.id.root)
        val size: TextView = v.findViewById(R.id.size)
        val title: TextView = v.findViewById(R.id.title)
        val image: ImageView = v.findViewById(R.id.image)
        val author: TextView = v.findViewById(R.id.author)
        val lastUpdate: TextView = v.findViewById(R.id.lastUpdate)
    }

    override fun getItemId(position: Int): Long {
        return list[position].hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.pack_item, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val soundPack = list[position]

        holder.size.text =
            soundPack.size.zeroOrNull {
                it.toHumanReadableBytes(
                    activity
                )
            } ?: ""
        holder.title.text = soundPack.title
        holder.author.text = soundPack.author

        holder.image.setImageResource(R.drawable.ic_sounds)

        holder.lastUpdate.text = soundPack.date.zeroOrNull{it.format(System.currentTimeMillis())} ?: ""
        holder.root.setOnClickListener {
            soundPack.download(activity) { sounds ->
                activity.openDialog(R.layout.soundpack_dialog) { dialog ->
                    val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
                    val textView = findViewById<TextView>(R.id.TextViewSound)

                    recyclerView.setHasFixedSize(true)
                    recyclerView.layoutManager = LinearLayoutManager(activity)
                    recyclerView.adapter = SoundAdapter(sounds, activity)
                    textView.text = soundPack.title
                    findViewById<Button>(R.id.ok)?.setOnClickListener {
                        getSoundsDirectory()?.path?.let { path ->
                            SuFile("${Config.MODULE_PATH}$path/audio/ui").apply {
                                deleteRecursive()
                                SuFile(sounds.first()).parentFile
                                    ?.copyRecursively(this)
                            }
                            activity.findViewById<View>(R.id.bottom_bar)?.let { view ->
                                Snackbar.make(
                                    activity.window.decorView,
                                    R.string.sounds_applied,
                                    Snackbar.LENGTH_LONG
                                ).setAnchorView(view).setAction(
                                    R.string.reboot
                                ) {
                                    RootUtils.reboot()
                                }.showMaterial()
                            }
                        }
                        dialog.dismiss()
                    }
                    findViewById<Button>(R.id.cancel)?.setOnClickListener { dialog.dismiss() }
                }
            }
        }
    }

    override fun getItemCount(): Int = list.size

    private class SoundAdapter(
        private val sounds: List<String>,
        private val activity: Activity
    ) :
        RecyclerView.Adapter<SoundAdapter.ViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(activity)
                    .inflate(android.R.layout.simple_list_item_1, parent, false)
            )
        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val textView = v as TextView
        }

        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int
        ) {
            val sound = sounds[position]
            val file = File(sound)

            holder.textView.text = file.name
            holder.textView.setOnClickListener {
                MediaPlayer().apply {
                    try {
                        setDataSource(FileInputStream(file).fd)
                        prepare()
                        start()
                        setOnCompletionListener {
                            release()
                        }
                    } catch (e: Exception) {
                        release()
                    }
                }
            }
        }

        override fun getItemCount(): Int = sounds.size
    }
}