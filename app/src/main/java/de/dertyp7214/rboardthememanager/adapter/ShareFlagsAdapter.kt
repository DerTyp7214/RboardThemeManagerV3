package de.dertyp7214.rboardthememanager.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import de.dertyp7214.rboardthememanager.R
import java.util.*

class ShareFlagsAdapter(
    private val context: Context, val flags: List<String>, private val onClick: (key: String) -> Unit) :
    RecyclerView.Adapter<ShareFlagsAdapter.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    private val states: HashMap<String, Boolean> by lazy {
        HashMap(flags.associateWith { false })
    }

    fun getSelectedFlags(): List<String> {
        return states.filter { it.value }.map { it.key }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectAll() {
        states.forEach { (s, _) -> states[s] = true }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearSelection() {
        states.forEach { (s, _) -> states[s] = false }
        notifyDataSetChanged()
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val main: View = v
        val title: TextView = v.findViewById(R.id.title)
        val summary: TextView = v.findViewById(R.id.summary)
        val checkBox: MaterialCheckBox = v.findViewById(R.id.checkBox)
        val shareFlagsCard: MaterialCardView = v.findViewById(R.id.share_flags_card)
    }

    override fun getItemId(position: Int): Long {
        return flags[position].hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.share_flag_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val flag = flags[position]
        when (position) {
            0 -> {
                if (flags.size == 1){
                    holder.shareFlagsCard.background = ContextCompat.getDrawable(context, R.drawable.color_surface_overlay_background_rounded)
                }
                else{
                    holder.shareFlagsCard.background = ContextCompat.getDrawable(context, R.drawable.color_surface_overlay_background_top)
                }
            }
            flags.lastIndex -> {
                holder.shareFlagsCard.background = ContextCompat.getDrawable(context, R.drawable.color_surface_overlay_background_bottom)
            }
            else -> {
                holder.shareFlagsCard.background = ContextCompat.getDrawable(context, R.drawable.color_surface_overlay_background)
            }
        }

        holder.checkBox.isChecked = states[flag] == true
        holder.checkBox.setOnClickListener {
            states[flag] = states[flag] == false
            onClick(flag)
        }
        holder.main.setOnClickListener {
            states[flag] = states[flag] == false
            onClick(flag)
            holder.checkBox.isChecked = states[flag] == true
        }

        holder.title.text = flag.split("_").joinToString(" ") { s ->
            s.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.ROOT
                ) else it.toString()
            }
        }
        holder.summary.text = flag
    }

    override fun getItemCount(): Int = flags.size
}