package de.dertyp7214.rboardthememanager.utils

import androidx.core.graphics.ColorUtils

object ColorUtils {
    fun isColorLight(color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) > .4
    }
}