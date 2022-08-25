package de.dertyp7214.rboardthememanager.core

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.postDelayed

fun delayed(delay: Long, callback: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed(delay) { callback() }
}

fun Context.getAttr(@AttrRes attr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.data
}

@SuppressLint("DiscouragedApi", "InternalInsetResource")
fun Context.getNavigationBarHeight(): Int {
    val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else 0
}

fun Context.notify(
    notificationManager: NotificationManagerCompat,
    notificationId: Int,
    notification: Notification
) {
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) notificationManager.notify(notificationId, notification)
}

fun Context.applyThemeOverlay(@StyleRes theme: Int) {
    this.theme.applyStyle(theme, true)

    if (this is Activity) {
        val windowDecorViewTheme = getWindowDecorViewTheme()
        windowDecorViewTheme?.applyStyle(theme, true)
    }
}