package de.dertyp7214.rboardthememanager.core

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
import android.net.Uri
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog


fun Activity.openUrl(url: String) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}

fun Activity.openDialog(
    @StringRes message: Int,
    @StringRes title: Int,
    negative: ((dialogInterface: DialogInterface) -> Unit)? = { it.dismiss() },
    positive: (dialogInterface: DialogInterface) -> Unit
): AlertDialog {
    val content = findViewById<View>(android.R.id.content)
    content.setRenderEffect(RenderEffect.createBlurEffect(10F, 10F, Shader.TileMode.REPEAT))
    return AlertDialog.Builder(this)
        .setCancelable(false)
        .setMessage(message)
        .setTitle(title)
        .setPositiveButton(android.R.string.ok) { dialogInterface, _ -> positive(dialogInterface) }
        .setOnDismissListener { content.setRenderEffect(null) }
        .apply {
            if (negative != null) setNegativeButton(android.R.string.cancel) { dialogInterface, _ ->
                negative.invoke(
                    dialogInterface
                )
            }
        }
        .create().also { it.show() }
}