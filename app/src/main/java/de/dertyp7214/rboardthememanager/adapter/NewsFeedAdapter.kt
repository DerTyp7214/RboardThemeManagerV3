package de.dertyp7214.rboardthememanager.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.components.NewsCards
import de.dertyp7214.rboardthememanager.data.ThemePack
import de.dertyp7214.rboardthememanager.utils.asyncInto

class NewsFeedAdapter(
    private val news: List<NewsCards.CardElement>,
    private val onCardClicked: (pack: ThemePack) -> Unit
) :
    RecyclerView.Adapter<NewsFeedAdapter.ViewHolder>() {
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val card: CardView = v.findViewById(R.id.card)
        val imageView: ImageView = v.findViewById(R.id.image)
        val name: TextView = v.findViewById(R.id.name)
        val author: TextView = v.findViewById(R.id.author)
    }

    private var defaultBg: Drawable? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        defaultBg = ContextCompat.getDrawable(
            parent.context,
            R.drawable.news_bg_default
        )
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = news[position]

        holder.imageView.setImageDrawable(defaultBg)

        item::bitmap asyncInto holder.imageView::setImageBitmap

        holder.name.text = item.title
        holder.author.text = "by ${item.author}"

        holder.card.setOnClickListener {
            onCardClicked(ThemePack(item.author, item.url, item.title, listOf()))
        }
    }

    override fun getItemCount() = news.size
}