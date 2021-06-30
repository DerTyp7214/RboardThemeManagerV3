package de.dertyp7214.rboardthememanager.core

import android.content.Context
import android.graphics.Color
import android.util.DisplayMetrics

fun Int.changeAlpha(alpha: Int): Int {
    return Color.argb(
        alpha,
        Color.red(this),
        Color.green(this),
        Color.blue(this)
    )
}