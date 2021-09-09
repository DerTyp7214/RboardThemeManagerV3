package de.dertyp7214.rboardthememanager.core

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.core.os.postDelayed

fun delayed(delay: Long, callback: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed(delay) { callback() }
}

fun Context.getAttr(@AttrRes attr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.data
}

fun Context.getNavigationBarHeight(): Int {
    val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else 0
}