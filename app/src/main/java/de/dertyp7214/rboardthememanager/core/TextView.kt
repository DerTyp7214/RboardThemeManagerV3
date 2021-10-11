package de.dertyp7214.rboardthememanager.core

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.widget.TextView

fun TextView.setDrawableTint(color: Int) {
    compoundDrawables.forEach { drawable ->
        drawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
    }
}