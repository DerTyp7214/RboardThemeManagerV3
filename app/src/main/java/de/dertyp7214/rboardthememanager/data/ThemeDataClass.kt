package de.dertyp7214.rboardthememanager.data

import android.graphics.Bitmap
import android.graphics.ColorFilter

data class ThemeDataClass(
    val image: Bitmap? = null,
    val name: String,
    val path: String,
    val colorFilter: ColorFilter? = null,
    var installed: Boolean = false
)