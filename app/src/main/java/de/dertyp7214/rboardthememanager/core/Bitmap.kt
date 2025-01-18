package de.dertyp7214.rboardthememanager.core

import android.graphics.*
import androidx.core.graphics.scale
import androidx.core.graphics.createBitmap

fun Bitmap.resize(width: Int? = null, height: Int? = null): Bitmap {
    if (width == null && height == null) return this
    if (width == this.width && height == this.height) return this
    if ((width ?: 0) > this.width || (height ?: 0) > this.height) return this
    val w = width ?: (((height ?: 0).toFloat() / this.height.toFloat()) * this.width.toFloat())
    val h = height ?: (((width ?: 0).toFloat() / this.width.toFloat()) * this.height.toFloat())
    return scale(w.toInt(), h.toInt(), false)
}

fun Bitmap.roundCorners(pixels: Int, colorFilter: ColorFilter? = null): Bitmap {
    val output = createBitmap(width, height, Bitmap.Config.ARGB_8888)
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