package de.dertyp7214.rboardthememanager.core

import android.graphics.*


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

fun Bitmap.resize(width: Int? = null, height: Int? = null): Bitmap {
    if (width == null && height == null) return this
    val w = width ?: (this.height / (height ?: 0)) * this.width
    val h = height ?: (this.width / (width ?: 0)) * this.height
    return Bitmap.createScaledBitmap(this, w, h, false)
}

fun Bitmap.roundCorners(pixels: Int, colorFilter: ColorFilter? = null): Bitmap {
    val output = Bitmap.createBitmap(
        width, height, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(output)
    val color = -0xbdbdbe
    val paint = Paint()
    val rect = Rect(0, 0, width, height)
    val rectF = RectF(rect)
    val roundPx = pixels.toFloat()
    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    paint.color = color
    canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    colorFilter?.let { paint.setColorFilter(it) }
    canvas.drawBitmap(this, rect, rect, paint)
    return output
}