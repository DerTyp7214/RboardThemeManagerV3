package de.dertyp7214.rboardthememanager.utils

import de.dertyp7214.colorutilsc.ColorUtilsC

object ColorUtils {
    fun isColorLight(color: Int): Boolean {
        return ColorUtilsC.calculateLuminance(color) > .4
    }
}