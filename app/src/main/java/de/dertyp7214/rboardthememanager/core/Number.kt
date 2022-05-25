@file:Suppress("unused")

package de.dertyp7214.rboardthememanager.core

import android.content.Context
import android.util.DisplayMetrics
import java.lang.Double.parseDouble
import kotlin.math.roundToInt

fun Any.isNumber(): Boolean {
    return try {
        parseDouble(this as String)
        true
    } catch (e: Exception) {
        false
    }
}

fun Any.equalsNumber(second: Any?): Boolean {
    return try {
        parseDouble(this as String) == parseDouble(second as String)
    } catch (e: Exception) {
        false
    }
}

fun <E> Int.zeroOrNull(block: (Int) -> E): E? {
    return if (this > 0) block(this) else null
}

fun <E> Long.zeroOrNull(block: (Long) -> E): E? {
    return if (this > 0) block(this) else null
}

fun Number.dpToPx(context: Context): Float {
    return this.toFloat() * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Number.dpToPxRounded(context: Context): Int {
    return dpToPx(context).roundToInt()
}

@Suppress("unused")
fun Number.pxToDp(context: Context): Float {
    return this.toFloat() / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Long.format(currentDate: Long): String {
    val diff = currentDate - this
    return (diff / (1000 * 60 * 60 * 24)).toInt().let {
        when (it) {
            0 -> "today"
            1 -> "$it day ago"
            else -> "$it days ago"
        }
    }
}