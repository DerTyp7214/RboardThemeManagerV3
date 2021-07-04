package de.dertyp7214.rboardthememanager.core

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.dertyp7214.rboardthememanager.R

inline val Activity.content: View
    get() {
        return findViewById(android.R.id.content)
    }

operator fun <T : ViewModel> FragmentActivity.get(modelClass: Class<T>): T =
    run(::ViewModelProvider)[modelClass]

fun Activity.openUrl(url: String) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}

fun Activity.openDialog(
    message: String,
    title: String,
    cancelable: Boolean = false,
    negative: ((dialogInterface: DialogInterface) -> Unit)? = { it.dismiss() },
    positive: (dialogInterface: DialogInterface) -> Unit
): AlertDialog {
    content.setRenderEffect(RenderEffect.createBlurEffect(10F, 10F, Shader.TileMode.REPEAT))
    return MaterialAlertDialogBuilder(this, R.style.Dialog_RboardThemeManagerV3_MaterialAlertDialog)
        .setCancelable(cancelable)
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

fun Activity.openDialog(
    @StringRes message: Int,
    @StringRes title: Int,
    cancelable: Boolean = false,
    negative: ((dialogInterface: DialogInterface) -> Unit)? = { it.dismiss() },
    positive: (dialogInterface: DialogInterface) -> Unit
): AlertDialog = openDialog(getString(message), getString(title), cancelable, negative, positive)

fun Activity.openShareThemeDialog(
    negative: ((dialogInterface: DialogInterface) -> Unit) = { it.dismiss() },
    positive: (dialogInterface: DialogInterface, name: String, author: String) -> Unit
): AlertDialog {
    content.setRenderEffect(RenderEffect.createBlurEffect(10F, 10F, Shader.TileMode.REPEAT))
    val view = layoutInflater.inflate(R.layout.share_popup, null)
    return MaterialAlertDialogBuilder(this, R.style.Dialog_RboardThemeManagerV3_MaterialAlertDialog)
        .setCancelable(false)
        .setView(view)
        .setOnDismissListener { content.setRenderEffect(null) }
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

@SuppressLint("InflateParams")
fun Activity.openInputDialog(
    @StringRes hint: Int,
    negative: ((dialogInterface: DialogInterface) -> Unit) = { it.dismiss() },
    positive: (dialogInterface: DialogInterface, text: String) -> Unit
): AlertDialog {
    content.setRenderEffect(RenderEffect.createBlurEffect(10F, 10F, Shader.TileMode.REPEAT))
    val view = layoutInflater.inflate(R.layout.input_dialog, null)
    return MaterialAlertDialogBuilder(this, R.style.Dialog_RboardThemeManagerV3_MaterialAlertDialog)
        .setCancelable(false)
        .setView(view)
        .setOnDismissListener { content.setRenderEffect(null) }
        .create().also { dialog ->
            val input = view.findViewById<EditText>(R.id.editText)
            input.setHint(hint)

            view.findViewById<Button>(R.id.ok)?.setOnClickListener {
                positive(dialog, input?.text?.toString() ?: "")
            }
            view.findViewById<Button>(R.id.cancel)?.setOnClickListener { negative(dialog) }
            dialog.show()
        }
}

fun Activity.openLoadingDialog(@StringRes message: Int): AlertDialog {
    content.setRenderEffect(RenderEffect.createBlurEffect(10F, 10F, Shader.TileMode.REPEAT))
    val view = layoutInflater.inflate(R.layout.loading_dialog, null)
    return MaterialAlertDialogBuilder(this, R.style.Dialog_RboardThemeManagerV3_MaterialAlertDialog)
        .setCancelable(false)
        .setView(view)
        .setOnDismissListener { content.setRenderEffect(null) }
        .create().also { dialog ->
            view.findViewById<TextView>(R.id.message).setText(message)
            dialog.show()
        }
}