package de.dertyp7214.rboardthememanager.core

import android.app.Activity
import android.content.Context
import android.content.Intent


fun <T : Activity> Class<T>.start(context: Context) {
    context.startActivity(Intent(context, this))
}