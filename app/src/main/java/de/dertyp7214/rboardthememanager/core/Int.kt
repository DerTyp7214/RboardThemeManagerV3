@file:Suppress("unused")

package de.dertyp7214.rboardthememanager.core

import android.graphics.Color
import de.dertyp7214.rboardthememanager.R

fun Int.changeAlpha(alpha: Int): Int {
    return Color.argb(
        alpha,
        Color.red(this),
        Color.green(this),
        Color.blue(this)
    )
}

fun lerp(a: Int, b: Int, f: Float): Float = a + (f * (a - b).toFloat())

val Int.safeString: Int
    get() {
        return if (this == 0x0) R.string.none else this
    }

val Int.safeIcon: Int
    get() {
        return if (this == 0x0) R.drawable.empty else this
    }