package de.dertyp7214.rboardthememanager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.dertyp7214.rboardthememanager.R

class ReposAdapter(private val repos: List<String>, private val onRemove: (repo: String) -> Unit) :
    RecyclerView.Adapter<ReposAdapter.ViewHolder>() {
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val text: TextView = v.findViewById(R.id.repo)
        val remove: ImageView = v.findViewById(R.id.remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.repo_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.text = repos[position]
        holder.remove.setOnClickListener { onRemove(repos[position]) }
    }

    override fun getItemCount(): Int = repos.size
}