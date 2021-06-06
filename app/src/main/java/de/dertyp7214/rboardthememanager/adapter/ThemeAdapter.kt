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
import de.dertyp7214.rboardthememanager.core.setAll
import de.dertyp7214.rboardthememanager.data.ThemeDataClass
import de.dertyp7214.rboardthememanager.utils.ColorUtils
import de.dertyp7214.rboardthememanager.utils.getActiveTheme
import java.util.*
import kotlin.collections.ArrayList

class ThemeAdapter(
    private val context: Context,
    private val themes: List<ThemeDataClass>,
    private val forcedSelectionState: SelectionState? = null,
    private val onSelectionStateChange: (selectionState: SelectionState, adapter: ThemeAdapter) -> Unit,
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

    private val selected: ArrayListWrapper<Boolean> = ArrayListWrapper(themes.map { false }) {
        if (oldSelectionState != selectionState) {
            oldSelectionState = selectionState
            onSelectionStateChange(selectionState, this)
        }
    }
    private var oldSelectionState: SelectionState? = null
    private val selectionState: SelectionState
        get() = forcedSelectionState
            ?: if (selected.any { it }) SelectionState.SELECTING else SelectionState.NONE

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        activeTheme = getActiveTheme()
            .removePrefix("assets:theme_package_metadata_")
            .removeSuffix(".binarypb").let {
                if (it.isBlank()) "system_auto" else it
            }
    }

    private class ArrayListWrapper<E>(
        private val list: ArrayList<E>,
        private val onSet: (items: ArrayList<E>) -> Unit
    ) {
        constructor(list: List<E>, onSet: (items: ArrayList<E>) -> Unit) : this(
            ArrayList(list),
            onSet
        )

        val size: Int
            get() = list.size

        fun filter(predicate: (e: E) -> Boolean) = list.filter(predicate)
        fun <T> mapIndexed(transform: (index: Int, e: E) -> T) = list.mapIndexed(transform)
        fun clear() = list.clear()
        fun addAll(items: List<E>) = list.addAll(items)
        fun any(predicate: (e: E) -> Boolean) = list.any(predicate)
        operator fun get(index: Int) = list[index]
        operator fun set(index: Int, e: E) {
            list[index] = e
            onSet(list)
        }

        fun setAll(e: E) {
            list.setAll(e)
            onSet(list)
        }
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

    enum class SelectionState {
        SELECTING,
        NONE
    }

    fun clearSelection() {
        selected.setAll(false)
        notifyDataChanged()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun getSelected(): List<ThemeDataClass> {
        return selected.mapIndexed { index, value -> Pair(themes[index], value) }
            .filter { it.second }.map { it.first }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyDataChanged() {
        if (selected.size != themes.size) selected.apply {
            clear()
            addAll(themes.map { false })
        }
        notifyDataSetChanged()
        activeTheme = getActiveTheme()
            .removePrefix("assets:theme_package_metadata_")
            .removeSuffix(".binarypb").let {
                if (it.isBlank()) "system_auto" else it
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
        val dataClass = themes[position]

        holder.themeImage.setImageBitmap(dataClass.image ?: default)
        holder.themeImage.colorFilter = dataClass.colorFilter
        holder.themeImage.alpha = if (dataClass.image != null) 1F else .3F

        val color = ColorUtils.dominantColor(holder.themeImage.drawable.getBitmap())

        if (holder.gradient != null) {
            val gradient = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(color, Color.TRANSPARENT)
            )
            holder.gradient.background = gradient
        }

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

        holder.selectOverlay.background.alpha = 187

        if (selected[position])
            holder.selectOverlay.alpha = 1F
        else
            holder.selectOverlay.alpha = 0F

        holder.card.setCardBackgroundColor(color)

        holder.card.setOnClickListener {
            if (selectionState == SelectionState.SELECTING) {
                selected[position] = !selected[position]
                holder.selectOverlay.animate().alpha(if (selected[position]) 1F else 0F)
                    .setDuration(200).withEndAction {
                        notifyDataChanged()
                    }
            } else onClickTheme(dataClass)
        }

        holder.card.setOnLongClickListener {
            selected[position] = true
            holder.selectOverlay.animate().alpha(1F).setDuration(200).withEndAction {
                notifyDataChanged()
            }
            true
        }

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