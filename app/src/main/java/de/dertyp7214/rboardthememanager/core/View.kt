package de.dertyp7214.rboardthememanager.core

import android.graphics.Rect
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

fun View.calculateBottomInParent(): Int {
    return Rect().also { copyBoundsInWindow(it) }.bottom.let { bottom ->
        parent.let { parent ->
            if (parent is View) Rect().also { parent.copyBoundsInWindow(it) }.bottom - bottom
            else bottom
        }
    }
}

fun View.copyBoundsInWindow(rect: Rect) {
    if (isLaidOut && isAttachedToWindow) {
        rect.set(0, 0, width, height)
        val tmpIntArr = intArrayOf(0, 0)
        getLocationInWindow(tmpIntArr)
        rect.offset(tmpIntArr[0], tmpIntArr[1])
    } else {
        throw IllegalArgumentException(
            "Can not copy bounds as view is not laid out" +
                    " or attached to window"
        )
    }
} 