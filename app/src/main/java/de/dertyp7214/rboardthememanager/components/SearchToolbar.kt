@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package de.dertyp7214.rboardthememanager.components

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Menu
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.children
import com.google.android.material.appbar.MaterialToolbar
import de.dertyp7214.colorutilsc.ColorUtilsC
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.dpToPx
import de.dertyp7214.rboardthememanager.core.getAttr
import de.dertyp7214.rboardthememanager.core.setMargin
import kotlin.math.roundToInt

@SuppressLint("ResourceType")
class SearchToolbar(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    val searchBar: SearchBar

    private val toolbar: MaterialToolbar

    private val toolbarColor: Int
    private val cornerRadius = context.resources.getDimension(R.dimen.roundCorners)
    private val margin = 8.dpToPx(context)

    var searchOpen = false
        set(value) {
            openSearch(value)
            field = value
        }

    var animationDuration = context.resources.getInteger(android.R.integer.config_mediumAnimTime)

    init {
        inflate(context, R.layout.search_toolbar, this)

        toolbar = findViewById(R.id.searchToolbar_toolbar)
        searchBar = findViewById(R.id.searchToolbar_searchBar)

        val typedArray = context.obtainStyledAttributes(
            attrs,
            intArrayOf(R.attr.menu, android.R.attr.background, R.attr.searchOpen)
        )
        typedArray.getResourceId(0, -1).let { if (it != -1) toolbar.inflateMenu(it) }
        toolbarColor = typedArray.getColor(1, context.getAttr(R.attr.colorSurface))
        searchOpen = typedArray.getBoolean(2, false)
        typedArray.recycle()

        toolbar.setBackgroundColor(toolbarColor)
        setBackgroundColor(Color.TRANSPARENT)

        openSearch(searchOpen, false)
    }

    fun inflateMenu(resId: Int) = toolbar.inflateMenu(resId)
    fun getMenu(): Menu = toolbar.menu
    override fun showContextMenu() = toolbar.showContextMenu()
    fun setOnMenuItemClickListener(listener: Toolbar.OnMenuItemClickListener?) =
        toolbar.setOnMenuItemClickListener(listener)

    override fun setOnClickListener(l: OnClickListener?) = toolbar.setOnClickListener(l)
    fun setNavigationOnClickListener(listener: OnClickListener?) =
        toolbar.setNavigationOnClickListener(listener)

    fun setNavigationIcon(drawable: Int) = toolbar.setNavigationIcon(drawable)

    var navigationIcon: Drawable? = null
        set(value) {
            toolbar.navigationIcon = value
            field = value
        }
        get() = toolbar.navigationIcon

    private fun openSearch(open: Boolean, animated: Boolean = true) {
        val searchBarColor = ColorUtilsC.overlayColors(
            context.getColor(R.color.color_primary_0_10_overlay),
            context.getAttr(R.attr.colorSurface)
        )

        val duration: Long = if (animated) animationDuration.toLong() else 0

        if (open) {
            searchBar.visibility = VISIBLE

            searchBar.searchBar.apply {
                setMargin(all = 0)
                radius = 0F
                setCardBackgroundColor(toolbarColor)
                children.iterator().forEach { child -> child.alpha = 0F }
                toolbar.setBackgroundColor(Color.TRANSPARENT)

                ObjectAnimator.ofFloat(toolbar, ALPHA, 1F, 0F).apply {
                    this.duration = duration / 8
                    doOnEnd {
                        toolbar.visibility = GONE
                        toolbar.setBackgroundColor(toolbarColor)
                    }
                    start()
                }

                ValueAnimator.ofFloat(0F, 1F).also { animator ->
                    animator.duration = duration
                    animator.addUpdateListener {
                        val ratio = it.animatedValue as Float

                        setMargin(all = (margin * ratio).roundToInt())
                        radius = cornerRadius * ratio
                        setCardBackgroundColor(
                            ColorUtilsC.blendARGB(
                                toolbarColor,
                                searchBarColor,
                                ratio
                            )
                        )
                        children.iterator().forEach { child -> child.alpha = ratio }
                    }
                    animator.start()
                }
            }
        } else {
            searchBar.searchBar.apply {
                setMargin(all = margin.roundToInt())
                radius = cornerRadius
                children.iterator().forEach { child -> child.alpha = 1F }

                ObjectAnimator.ofFloat(toolbar, ALPHA, 0F, 1F).apply {
                    this.duration = duration / 8
                    doOnStart {
                        toolbar.setBackgroundColor(Color.TRANSPARENT)
                        toolbar.visibility = VISIBLE
                    }
                    doOnEnd {
                        toolbar.setBackgroundColor(toolbarColor)
                        searchBar.visibility = GONE
                    }
                    startDelay = duration - this.duration
                    start()
                }

                ValueAnimator.ofFloat(1F, 0F).also { animator ->
                    animator.duration = duration
                    animator.addUpdateListener {
                        val ratio = it.animatedValue as Float

                        setMargin(all = (margin * ratio).roundToInt())
                        radius = cornerRadius * ratio
                        setCardBackgroundColor(
                            ColorUtilsC.blendARGB(
                                toolbarColor,
                                searchBarColor,
                                ratio
                            )
                        )
                        children.iterator().forEach { child -> child.alpha = ratio }
                    }
                    animator.start()
                }
            }
        }
    }
}