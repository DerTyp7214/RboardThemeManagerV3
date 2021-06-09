package de.dertyp7214.rboardthememanager.core

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import de.dertyp7214.rboardthememanager.R

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
    content
    return AlertDialog.Builder(this)
        .setCancelable(false)
        .setMessage(message)
        .setTitle(title)
        .setPositiveButton(android.R.string.ok) { dialogInterface, _ -> positive(dialogInterface) }
        .setOnDismissListener { content }
        .apply {
            if (negative != null) setNegativeButton(android.R.string.cancel) { dialogInterface, _ ->
                negative.invoke(
                    dialogInterface
                )
            }
        }
        .create().also { it.show() }
}

fun Activity.openShareThemeDialog(
    negative: ((dialogInterface: DialogInterface) -> Unit) = { it.dismiss() },
    positive: (dialogInterface: DialogInterface, name: String, author: String) -> Unit
): AlertDialog {
    val content = findViewById<View>(android.R.id.content)
    content
    val view = layoutInflater.inflate(R.layout.share_popup, null)
    return AlertDialog.Builder(this)
        .setCancelable(false)
        .setView(view)
        .setOnDismissListener { content }
        .create().also { dialog ->
            val nameInput = view.findViewById<EditText>(R.id.editTextName)
            val authorInput = view.findViewById<EditText>(R.id.editTextAuthor)

            view.findViewById<Button>(R.id.ok)?.setOnClickListener {
                positive(
                    dialog,
                    nameInput?.text?.toString() ?: "Shared Pack",
                    authorInput?.text?.toString() ?: "Rboard Theme Manager"
                )
            }
            view.findViewById<Button>(R.id.cancel)?.setOnClickListener {
                negative(dialog)
            }
            dialog.show()
        }
}

fun Activity.openLoadingDialog(@StringRes message: Int): AlertDialog {
    val content = findViewById<View>(android.R.id.content)
    content
    val view = layoutInflater.inflate(R.layout.loading_dialog, null)
    return AlertDialog.Builder(this)
        .setCancelable(false)
        .setView(view)
        .setOnDismissListener { content }
        .create().also { dialog ->
            view.findViewById<TextView>(R.id.message).setText(message)
            dialog.show()
        }
}