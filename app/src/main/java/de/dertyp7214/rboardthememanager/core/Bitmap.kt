package de.dertyp7214.rboardthememanager.core

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build

fun Bitmap.getDominantColor(): Int {
    val newBitmap = Bitmap.createScaledBitmap(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && config == Bitmap.Config.HARDWARE) this.copy(
            Bitmap.Config.ARGB_8888,
            true
        )
        else this, 1, 1, true
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