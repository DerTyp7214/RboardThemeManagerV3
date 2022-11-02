package de.dertyp7214.rboardthememanager.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.color.MaterialColors
import de.dertyp7214.colorutilsc.ColorUtilsC
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.getAttr
import de.dertyp7214.rboardthememanager.core.setAll
import de.dertyp7214.rboardthememanager.data.ThemeDataClass
import de.dertyp7214.rboardthememanager.utils.TraceWrapper
import de.dertyp7214.rboardcomponents.utils.doAsync
import de.dertyp7214.rboardthememanager.utils.getActiveTheme

class ThemeAdapter(
    private val context: Context,
    private val themes: List<ThemeDataClass>,
    private val forcedSelectionState: SelectionState? = null,
    private val onSelectionStateChange: (selectionState: SelectionState, adapter: ThemeAdapter) -> Unit,
    private val customLongClickListener: ((theme: ThemeDataClass) -> Unit)? = null,
    private val onClickTheme: (theme: ThemeDataClass) -> Unit
) :
    RecyclerView.Adapter<ThemeAdapter.ViewHolder>() {

    private var recyclerView: RecyclerView? = null
    private var lastPosition =
        recyclerView?.layoutManager?.let { (it as GridLayoutManager).findLastVisibleItemPosition() }
            ?: 0

    private var activeTheme = ""
    private val default by lazy {
        ContextCompat.getDrawable(
            context,
            R.drawable.ic_keyboard
        )!!.toBitmap()
    }
    private val selectedBackground by lazy {
        ColorDrawable(context.getAttr(R.attr.colorBackgroundFloating)).apply { alpha = 187 }
    }

    private val selected: ArrayListWrapper<Boolean> = ArrayListWrapper(themes.map { false }) {
        if (oldSelectionState != selectionState || forcedSelectionState == SelectionState.SELECTING) {
            oldSelectionState = selectionState
            onSelectionStateChange(selectionState, this)
        }
    }
    private var oldSelectionState: SelectionState? = null
    private val selectionState: SelectionState
        get() = forcedSelectionState
            ?: if (selected.any { it }) SelectionState.SELECTING else SelectionState.NONE

    init {
        setHasStableIds(true)
        cacheColor()
    }

    override fun getItemId(position: Int): Long {
        return themes[position].hashCode().toLong()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
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
        val updateAvailable: TextView = v.findViewById(R.id.update_available)
        val selectOverlay: ViewGroup = v.findViewById(R.id.select_overlay)
        val card: MaterialCardView = v.findViewById(R.id.card)
        val gradient: View? = try {
            v.findViewById(R.id.gradient)
        } catch (e: Exception) {
            null
        }
    }

    enum class SelectionState {
        SELECTING,
        NONE,
        NEVER
    }

    fun clearSelection() {
        selected.setAll(false)
        notifyDataChanged()
    }

    fun selectAll() {
        selected.setAll(true)
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
            colorCache.clear()
            cacheColor()
        }
        notifyDataSetChanged()
        activeTheme = getActiveTheme()
            .removePrefix("assets:theme_package_metadata_")
            .removeSuffix(".binarypb").ifBlank { "dynamic_color" }
    }

    operator fun set(index: Int, value: Boolean) {
        selected[index] = value
    }

    private fun cacheColor() {
        themes.forEachIndexed { index, themeDataClass ->
            doAsync({
                ImageView(context).let { view ->
                    view.setImageBitmap(themeDataClass.image ?: default)
                    view.colorFilter = themeDataClass.colorFilter
                    val color = MaterialColors.harmonizeWithPrimary(
                        context, view.drawable.toBitmap().let {
                            it[0, it.height / 2]
                        }
                    )
                    Pair(color, ColorUtilsC.calculateLuminance(color) > .4)
                }
            }, { colorCache[index] = it })
        }
    }

    private fun getColorAndCache(dataClass: ThemeDataClass, position: Int): Int {
        return (selected.size != themes.size).let {
            if (it) null
            else colorCache[position]?.first
        } ?: ImageView(context).let { view ->
            view.setImageBitmap(dataClass.image ?: default)
            view.colorFilter = dataClass.colorFilter
            val color = MaterialColors.harmonizeWithPrimary(
                context, view.drawable.toBitmap().let {
                    it[0, it.height / 2]
                }
            )
            colorCache[position] = Pair(color, ColorUtilsC.calculateLuminance(color) > .4)
            color
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.theme_grid_item_single, parent, false)
        )
    }

    private val colorCache: HashMap<Int, Pair<Int, Boolean>> = hashMapOf()

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataClass = themes[position]

        val trace = TraceWrapper("ADAPTER $position", false)
        trace.addSplit("IMAGE")

        holder.themeImage.setImageBitmap(dataClass.image ?: default)
        holder.themeImage.colorFilter = dataClass.colorFilter
        holder.themeImage.alpha = if (dataClass.image != null) 1F else .3F

        holder.updateAvailable.text =
            context.getString(R.string.update_available, dataClass.packName)
        holder.updateAvailable.visibility =
            if (dataClass.updateAvailable && dataClass.isInstalled) VISIBLE else INVISIBLE


        trace.addSplit("CALCULATE COLOR")

        val color = getColorAndCache(dataClass, position)

        trace.addSplit("BACKGROUND")

        if (holder.gradient != null) {
            val gradient = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(color, Color.TRANSPARENT)
            )
            holder.gradient.background = gradient
        }

        holder.selectOverlay.background = selectedBackground

        holder.card.setCardBackgroundColor(color)

        trace.addSplit("TEXT")

        "${dataClass.readableName} ${if (dataClass.name == activeTheme) context.getString(R.string.applied_theme) else ""}".let {
            holder.themeName.text = it
            holder.themeNameSelect.text = it
        }

        holder.themeName.setTextColor(
            if (colorCache[position]?.second == true) Color.BLACK else Color.WHITE
        )

        holder.updateAvailable.setTextColor(
            if (colorCache[position]?.second == true) Color.BLACK else Color.WHITE
        )

        trace.addSplit("CLICK")

        if (selected[position])
            holder.selectOverlay.alpha = 1F
        else
            holder.selectOverlay.alpha = 0F

        holder.card.setOnClickListener {
            if (selectionState == SelectionState.SELECTING) {
                selected[position] = !selected[position]
                holder.selectOverlay.animate().alpha(if (selected[position]) 1F else 0F)
                    .setDuration(200).withEndAction {
                        notifyDataChanged()
                    }
            } else onClickTheme(dataClass)
        }

        if (selectionState != SelectionState.NEVER || customLongClickListener != null)
            holder.card.setOnLongClickListener {
                customLongClickListener?.invoke(dataClass) ?: run {
                    selected[position] = true
                    holder.selectOverlay.animate().alpha(1F).setDuration(200).withEndAction {
                        notifyDataChanged()
                    }
                }
                true
            }

        trace.addSplit("ANIMATION")
        setAnimation(holder.card, position)
        trace.end()
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