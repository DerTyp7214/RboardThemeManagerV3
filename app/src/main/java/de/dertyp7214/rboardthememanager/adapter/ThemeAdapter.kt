package de.dertyp7214.rboardthememanager.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.getBitmap
import de.dertyp7214.rboardthememanager.data.ThemeDataClass
import de.dertyp7214.rboardthememanager.utils.ColorUtils
import de.dertyp7214.rboardthememanager.utils.getActiveTheme
import java.util.*

class ThemeAdapter(
    private val context: Context,
    private val themes: List<ThemeDataClass>,
    private val onClickTheme: (theme: ThemeDataClass) -> Unit
) :
    RecyclerView.Adapter<ThemeAdapter.ViewHolder>() {

    private var recyclerView: RecyclerView? = null
    private var lastPosition =
        recyclerView?.layoutManager?.let { (it as GridLayoutManager).findLastVisibleItemPosition() }
            ?: 0

    private var activeTheme = ""
    private val default = ContextCompat.getDrawable(
        context,
        R.drawable.ic_keyboard
    )!!.getBitmap()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        activeTheme = getActiveTheme()
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val themeImage: ImageView = v.findViewById(R.id.theme_image)
        val themeName: TextView = v.findViewById(R.id.theme_name)
        val themeNameSelect: TextView = v.findViewById(R.id.theme_name_selected)
        val selectOverlay: ViewGroup = v.findViewById(R.id.select_overlay)
        val card: CardView = v.findViewById(R.id.card)
        val gradient: View? = try {
            v.findViewById(R.id.gradient)
        } catch (e: Exception) {
            null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.theme_grid_item_single, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val selection = true in themes.map { it.selected }
        val dataClass = themes[position]

        val color = ColorUtils.dominantColor(dataClass.image ?: default)

        if (holder.gradient != null) {
            val gradient = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(color, Color.TRANSPARENT)
            )
            holder.gradient.background = gradient
        }

        holder.themeImage.setImageBitmap(dataClass.image ?: default)
        holder.themeImage.alpha = if (dataClass.image != null) 1F else .3F

        holder.themeName.text =
            "${
                dataClass.name.split("_").joinToString(" ") { s ->
                    s.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.ROOT
                        ) else it.toString()
                    }
                }
            } ${if (dataClass.name == activeTheme) "(applied)" else ""}"
        holder.themeNameSelect.text =
            "${
                dataClass.name.split("_").joinToString(" ") { s ->
                    s.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.ROOT
                        ) else it.toString()
                    }
                }
            } ${if (dataClass.name == activeTheme) "(applied)" else ""}"

        holder.themeName.setTextColor(if (ColorUtils.isColorLight(color)) Color.BLACK else Color.WHITE)

        if (dataClass.selected)
            holder.selectOverlay.alpha = 1F
        else
            holder.selectOverlay.alpha = 0F

        holder.card.setCardBackgroundColor(color)

        holder.card.setOnClickListener {
            onClickTheme(dataClass)
        }

        /*holder.card.setOnClickListener {
            if (selection) {
                list[position].selected = !list[position].selected
                holder.selectOverlay.animate().alpha(1F - holder.selectOverlay.alpha)
                    .setDuration(200).withEndAction {
                        notifyDataSetChanged()
                        if (list[position].selected) addItemSelect(dataClass, position)
                        if (!list[position].selected) removeItemSelect(dataClass, position)
                        if (true !in list.map { it.selected }) selectToggle(false)
                    }.start()
            } else {
                SelectedThemeBottomSheet(dataClass, default, color, isColorLight(color), context) {
                    homeViewModel.setRefetch(true)
                }.show(
                    context.supportFragmentManager,
                    ""
                )
            }
        }

        holder.card.setOnLongClickListener {
            list[position].selected = true
            holder.selectOverlay.animate().alpha(1F).setDuration(200).withEndAction {
                notifyDataSetChanged()
                selectToggle(true)
            }
            true
        }*/

        setAnimation(holder.card, position)
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation =
                AnimationUtils.loadAnimation(context, R.anim.item_animation_fall_down)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    override fun getItemCount(): Int = themes.size
}