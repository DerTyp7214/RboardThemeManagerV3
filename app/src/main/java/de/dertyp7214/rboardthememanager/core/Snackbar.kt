package de.dertyp7214.rboardthememanager.core

import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import de.dertyp7214.rboardthememanager.R

fun Snackbar.showMaterial(): Snackbar {
    view.background = ContextCompat.getDrawable(context, R.drawable.snackbar_background)
    show()
    return this
}