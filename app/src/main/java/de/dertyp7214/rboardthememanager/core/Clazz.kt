package de.dertyp7214.rboardthememanager.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

fun <T : Activity> Class<T>.start(context: Context, block: Intent.() -> Unit = {}) {
    context.startActivity(Intent(context, this).apply(block))
}

fun <T : Activity> Class<T>.start(
    context: Context,
    launcher: ActivityResultLauncher<Intent>,
    block: Intent.() -> Unit = {}
) {
    launcher.launch(Intent(context, this).apply(block))
}