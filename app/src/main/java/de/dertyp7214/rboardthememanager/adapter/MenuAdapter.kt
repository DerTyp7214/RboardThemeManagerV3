package de.dertyp7214.rboardthememanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.data.MenuItem

class MenuAdapter(private val items: List<MenuItem>, private val context: Context) :
    RecyclerView.Adapter<MenuAdapter.ViewHolder>() {
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val textView: TextView = v.findViewById(R.id.text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.menu_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.textView.setText(item.title)
        holder.textView.setCompoundDrawablesWithIntrinsicBounds(item.icon, 0, 0, 0)
        holder.textView.setOnClickListener { item.onClick() }
    }

    override fun getItemCount(): Int = items.size
}