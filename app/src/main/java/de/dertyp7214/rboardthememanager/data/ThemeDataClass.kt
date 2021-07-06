package de.dertyp7214.rboardthememanager.data

import android.graphics.Bitmap
import android.graphics.ColorFilter
import java.io.File
import java.util.*

data class ThemeDataClass(
    val image: Bitmap? = null,
    val name: String,
    val path: String,
    val colorFilter: ColorFilter? = null,
    var installed: Boolean = false
) {
    val readableName = name.split("_").joinToString(" ") { s ->
        s.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.ROOT
            ) else it.toString()
        }
    }.removeSuffix(":")
    val fileName = File(path).name
        .removeSuffix(".binarypb")
        .removeSuffix(":")
}