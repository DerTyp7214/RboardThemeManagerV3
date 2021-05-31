package de.dertyp7214.rboardthememanager.core

import android.view.View
import android.view.ViewGroup

fun View.setMargin(
    leftMargin: Int? = null, topMargin: Int? = null,
    rightMargin: Int? = null, bottomMargin: Int? = null
) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(
            leftMargin ?: params.leftMargin,
            topMargin ?: params.topMargin,
            rightMargin ?: params.rightMargin,
            bottomMargin ?: params.bottomMargin
        )
        layoutParams = params
    }
}

fun View.parent(max: Int = Int.MAX_VALUE): View {
    var p = this
    var index = 0
    while (p.parent is View && index++ < max) p = p.parent as View
    return p
}

