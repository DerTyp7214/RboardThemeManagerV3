package de.dertyp7214.rboardthememanager.core

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.Button
import androidx.core.graphics.ColorUtils

fun Button.enable(activeColor: Int) {
    isEnabled = true
    ValueAnimator.ofArgb(activeColor).apply {
        addUpdateListener {
            val color = it.animatedValue as Int
            this@enable.backgroundTintList = ColorStateList.valueOf(color)
        }
        duration = 300
    }.start()
}

fun Button.disable(activeColor: Int) {
    isEnabled = false
    ValueAnimator.ofArgb(ColorUtils.blendARGB(Color.GRAY, activeColor, .1F)).apply {
        addUpdateListener {
            val color = it.animatedValue as Int
            this@disable.backgroundTintList = ColorStateList.valueOf(color)
        }
        duration = 300
    }.start()
}