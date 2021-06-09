package de.dertyp7214.rboardthememanager.components

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.LinearLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.dpToPx
import de.dertyp7214.rboardthememanager.core.getAttrColor
import java.util.*

class ChipContainer(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private val chipGroup: ChipGroup
    private val clearTags: Chip

    private var filterToggleListener: (filters: List<String>) -> Unit = {}
    private val chips = arrayListOf<ChipData>()

    init {
        inflate(context, R.layout.chip_container, this)

        chipGroup = findViewById(R.id.chipGroup)
        clearTags = findViewById(R.id.clear_tags)

        clearTags.setOnClickListener {
            chips.forEachIndexed { index, _ ->
                chips[index].selected = false
            }
            refreshChips()
            filterToggleListener(chips.map { it.text })
        }
    }

    fun setChips(chips: List<String>) {
        this.chips.clear()
        this.chips.addAll(chips.map { ChipData(it, false) }
            .sortedBy { it.text.lowercase(Locale.getDefault()) })
        refreshChips()
    }

    fun setOnFilterToggle(listener: (filters: List<String>) -> Unit) {
        filterToggleListener = listener
    }

    val filters: List<String>
        get() {
            return chips.filter { it.selected }.map { it.text }
        }

    private fun refreshChips() {
        chipGroup.removeAllViews()

        val backgroundColor = context.getAttrColor(android.R.attr.colorBackground)
        val strokeColor = context.getAttrColor(R.attr.colorBackgroundFloating)
        val rippleColor = context.getAttrColor(R.attr.colorBackgroundFloating)
        val textColor = context.getAttrColor(R.attr.colorOnPrimary)

        val backgroundColorSelected = context.getAttrColor(R.attr.colorBackgroundFloating)
        val strokeColorSelected = context.getAttrColor(R.attr.colorOnPrimary)
        val rippleColorSelected = context.getAttrColor(R.attr.colorOnPrimary)

        chips.forEachIndexed { index, it ->
            val chip = Chip(context)
            chip.rippleColor =
                ColorStateList.valueOf(if (it.selected) rippleColorSelected else rippleColor)
            chip.chipBackgroundColor =
                ColorStateList.valueOf(if (it.selected) backgroundColorSelected else backgroundColor)
            chip.chipStrokeColor =
                ColorStateList.valueOf(if (it.selected) strokeColorSelected else strokeColor)
            chip.chipStrokeWidth = 1.dpToPx(context)
            chip.text = it.text
            chip.setTextColor(textColor)
            chip.setOnClickListener {
                toggleChip(index)
            }

            chipGroup.addView(chip)
        }
    }

    private fun toggleChip(index: Int) {
        chips[index].selected = !chips[index].selected
        refreshChips()

        filterToggleListener(chips.filter { it.selected }.map { it.text })
    }

    private data class ChipData(val text: String, var selected: Boolean)
}