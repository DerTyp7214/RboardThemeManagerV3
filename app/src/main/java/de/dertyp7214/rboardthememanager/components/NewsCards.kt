package de.dertyp7214.rboardthememanager.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.adapter.NewsFeedAdapter
import de.dertyp7214.rboardthememanager.data.ThemePack
import de.dertyp7214.rboardthememanager.utils.doAsync
import java.net.URL

class NewsCards(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private val newsFeedUrl = "https://raw.githubusercontent.com/GboardThemes/Packs/master/top.json"

    companion object {
        val cards: ArrayList<CardElement> = arrayListOf()
    }

    private val recyclerView: RecyclerView
    private val newsFeedAdapter: NewsFeedAdapter

    private var clickNewsListener: (pack: ThemePack) -> Unit = {}

    init {
        inflate(context, R.layout.news_card, this)

        newsFeedAdapter = NewsFeedAdapter(cards) {
            clickNewsListener(it)
        }
        recyclerView = findViewById(R.id.recyclerViewNews)

        val snapHelper = LinearSnapHelper()
        recyclerView.adapter = newsFeedAdapter
        snapHelper.attachToRecyclerView(recyclerView)

        if (cards.isEmpty())
            doAsync(::fetchNewsFeed) {
                cards.addAll(it)
                setUpViews()
            }
        else setUpViews()
    }

    fun setClickNewsListener(listener: (pack: ThemePack) -> Unit) {
        clickNewsListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setUpViews() {
        newsFeedAdapter.notifyDataSetChanged()
    }

    private fun fetchNewsFeed(): List<CardElement> {
        return try {
            Gson().fromJson(
                URL(newsFeedUrl).readText(),
                object : TypeToken<List<CardElement>>() {}.type
            )
        } catch (e: Exception) {
            listOf()
        }
    }

    data class CardElement(
        val image: String,
        val author: String,
        val title: String,
        val url: String
    ) {
        var bitmap: Bitmap? = null
            private set
            get() {
                if (field == null)
                    field = BitmapFactory.decodeStream(URL(image).openConnection().getInputStream())
                return field
            }
    }
}