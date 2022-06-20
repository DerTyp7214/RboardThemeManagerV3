@file:Suppress("unused")

package de.dertyp7214.rboardthememanager.core

import android.content.Context
import de.dertyp7214.colorutilsc.ColorUtilsC
import de.dertyp7214.rboardthememanager.R

fun Int.changeAlpha(alpha: Int): Int {
    return ColorUtilsC.setAlphaComponent(this, alpha)
}

fun Int.dp(context: Context): Int {
    val scale = context.resources.displayMetrics.density
    return (this * scale + 0.5f).toInt()
}

val Int.safeString: Int
    get() {
        return if (this == 0x0) R.string.none else this
    }

val Int.safeIcon: Int
    get() {
        return if (this == 0x0) R.drawable.empty else this
    }