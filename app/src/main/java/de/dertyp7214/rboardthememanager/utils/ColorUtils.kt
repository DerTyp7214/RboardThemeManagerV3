package de.dertyp7214.rboardthememanager.utils

import android.graphics.Bitmap
import androidx.core.graphics.ColorUtils
import de.dertyp7214.rboardthememanager.core.getDominantColor

object ColorUtils {
    fun dominantColor(image: Bitmap): Int {
        return image.getDominantColor()
    }

    fun isColorLight(color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) > .5
    }
}