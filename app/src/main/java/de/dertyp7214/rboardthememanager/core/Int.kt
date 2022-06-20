@file:Suppress("unused")

package de.dertyp7214.rboardthememanager.core

import de.dertyp7214.colorutilsc.ColorUtilsC
import de.dertyp7214.rboardthememanager.R

fun Int.changeAlpha(alpha: Int): Int {
    return ColorUtilsC.setAlphaComponent(this, alpha)
}

val Int.safeString: Int
    get() {
        return if (this == 0x0) R.string.none else this
    }

val Int.safeIcon: Int
    get() {
        return if (this == 0x0) R.drawable.empty else this
    }