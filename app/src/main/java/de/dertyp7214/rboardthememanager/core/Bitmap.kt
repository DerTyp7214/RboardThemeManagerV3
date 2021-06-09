package de.dertyp7214.rboardthememanager.core

import android.graphics.Bitmap
import android.graphics.Color

fun Bitmap.getDominantColor(): Int {
    val newBitmap = Bitmap.createScaledBitmap(
        copy(
            Bitmap.Config.ARGB_8888,
            true
        ), 1, 1, true
    )
    val color = try {
        newBitmap.getPixel(0, 0)
    } catch (e: Exception) {
        e.printStackTrace()
        Color.BLACK
    }
    newBitmap.recycle()
    return color
}