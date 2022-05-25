package de.dertyp7214.rboardthememanager.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

operator fun <T : Activity> Class<T>.get(context: Context) =
    context.startActivity(Intent(context, this))

operator fun <T : Activity> Class<T>.set(context: Context, block: Intent.() -> Unit) =
    context.startActivity(Intent(context, this).apply(block))

operator fun <T : Activity> Class<T>.set(
    context: Context,
    launcher: ActivityResultLauncher<Intent>,
    block: Intent.() -> Unit
) = launcher.launch(Intent(context, this).apply(block))