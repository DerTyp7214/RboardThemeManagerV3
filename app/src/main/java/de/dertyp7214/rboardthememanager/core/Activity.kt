package de.dertyp7214.rboardthememanager.core

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.RenderEffect
import android.graphics.Shader
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.dertyp7214.rboardthememanager.R

val Activity.preferences: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(this)

inline val Activity.content: View
    get() {
        return findViewById(android.R.id.content)
    }

inline val Activity.enableBlur: Boolean
    get() {
        return preferences.getBoolean("useBlur", true)
    }

operator fun <T : ViewModel> FragmentActivity.get(modelClass: Class<T>): T =
    run(::ViewModelProvider)[modelClass]

fun Activity.openUrl(url: String) {
    CustomTabsIntent.Builder()
        .setShowTitle(true)
        .setDefaultColorSchemeParams(
            CustomTabColorSchemeParams
                .Builder()
                .setToolbarColor(getAttr(R.attr.colorBackgroundFloating))
                .setNavigationBarColor(getAttr(R.attr.colorBackgroundFloating))
                .setSecondaryToolbarColor(getAttr(R.attr.colorBackgroundFloating))
                .setNavigationBarDividerColor(getAttr(R.attr.colorBackgroundFloating))
                .build()
        )
        .build()
        .launchUrl(this, Uri.parse(url))
}

fun Activity.openDialog(
    message: String,
    title: String,
    positiveText: String,
    negativeText: String,
    cancelable: Boolean = false,
    negative: ((dialogInterface: DialogInterface) -> Unit)? = { it.dismiss() },
    positive: (dialogInterface: DialogInterface) -> Unit
): AlertDialog {
    if (enableBlur) content.setRenderEffect(
        RenderEffect.createBlurEffect(
            10F,
            10F,
            Shader.TileMode.REPEAT
        )
    )
    return MaterialAlertDialogBuilder(this)
        .setCancelable(cancelable)
        .setMessage(message)
        .setTitle(title)
        .setPositiveButton(positiveText) { dialogInterface, _ -> positive(dialogInterface) }
        .setOnDismissListener { if (enableBlur) content.setRenderEffect(null) }
        .apply {
            if (negative != null) setNegativeButton(negativeText) { dialogInterface, _ ->
                negative.invoke(
                    dialogInterface
                )
            }
        }
        .create().also { it.show() }
}

fun Activity.openDialog(
    message: String,
    title: String,
    cancelable: Boolean = false,
    negative: ((dialogInterface: DialogInterface) -> Unit)? = { it.dismiss() },
    positive: (dialogInterface: DialogInterface) -> Unit
): AlertDialog = openDialog(
    message,
    title,
    getString(android.R.string.ok),
    getString(android.R.string.cancel),
    cancelable,
    negative,
    positive
)

fun Activity.openDialog(
    @StringRes message: Int,
    @StringRes title: Int,
    cancelable: Boolean = false,
    negative: ((dialogInterface: DialogInterface) -> Unit)? = { it.dismiss() },
    positive: (dialogInterface: DialogInterface) -> Unit
): AlertDialog = openDialog(getString(message), getString(title), cancelable, negative, positive)

fun Activity.openDialog(
    @StringRes message: Int,
    @StringRes title: Int,
    @StringRes positiveText: Int,
    @StringRes negativeText: Int,
    cancelable: Boolean = false,
    negative: ((dialogInterface: DialogInterface) -> Unit)? = { it.dismiss() },
    positive: (dialogInterface: DialogInterface) -> Unit
): AlertDialog = openDialog(
    getString(message),
    getString(title),
    getString(positiveText),
    getString(negativeText),
    cancelable,
    negative,
    positive
)

fun Activity.openShareThemeDialog(
    negative: ((dialogInterface: DialogInterface) -> Unit) = { it.dismiss() },
    positive: (dialogInterface: DialogInterface, name: String, author: String) -> Unit
) = openDialog(R.layout.share_popup, false) { dialog ->
    val nameInput = findViewById<EditText>(R.id.editTextName)
    val authorInput = findViewById<EditText>(R.id.editTextAuthor)

    findViewById<Button>(R.id.ok)?.setOnClickListener {
        positive(
            dialog,
            nameInput?.text?.toString() ?: "Shared Pack",
            authorInput?.text?.toString() ?: "Rboard Theme Manager"
        )
    }
    findViewById<Button>(R.id.cancel)?.setOnClickListener {
        negative(dialog)
    }
}

@SuppressLint("InflateParams")
fun Activity.openInputDialog(
    @StringRes hint: Int,
    value: String? = null,
    negative: ((dialogInterface: DialogInterface) -> Unit) = { it.dismiss() },
    positive: (dialogInterface: DialogInterface, text: String) -> Unit
) = openDialog(R.layout.input_dialog, false) { dialog ->
    val input = findViewById<EditText>(R.id.editText)
    input.setHint(hint)
    if (value != null) input.setText(value)

    findViewById<Button>(R.id.ok)?.setOnClickListener {
        positive(dialog, input?.text?.toString() ?: "")
    }
    findViewById<Button>(R.id.cancel)?.setOnClickListener { negative(dialog) }
}

fun Activity.openLoadingDialog(@StringRes message: Int) =
    openDialog(R.layout.loading_dialog, false) {
        findViewById<TextView>(R.id.message).setText(message)
    }

fun Activity.openDialog(
    @LayoutRes layout: Int,
    cancelable: Boolean = true,
    block: View.(DialogInterface) -> Unit
): AlertDialog {
    if (enableBlur) content.setRenderEffect(
        RenderEffect.createBlurEffect(
            10F,
            10F,
            Shader.TileMode.REPEAT
        )
    )
    val view = layoutInflater.inflate(layout, null)
    return MaterialAlertDialogBuilder(this)
        .setCancelable(cancelable)
        .setView(view)
        .setOnDismissListener { if (enableBlur) content.setRenderEffect(null) }
        .create().also { dialog ->
            block(view, dialog)
            dialog.show()
        }
}