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
import android.widget.ImageView
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.components.XMLEntry
import de.dertyp7214.rboardthememanager.components.XMLType
import de.dertyp7214.rboardthememanager.data.ThemeDataClass
import androidx.core.net.toUri

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

fun Activity.verifyInstallerId(): Boolean {
    val validInstallers: List<String> =
        listOf("com.android.vending", "com.google.android.feedback")

    val installer = packageManager.getInstallSourceInfo(packageName).installingPackageName

    return installer != null && validInstallers.contains(installer)
}

fun Activity.openUrl(url: String) {
    val color = getAttr(com.google.android.material.R.attr.colorSurface)
    CustomTabsIntent.Builder()
        .setShowTitle(true)
        .setDefaultColorSchemeParams(
            CustomTabColorSchemeParams
                .Builder()
                .setToolbarColor(color)
                .setNavigationBarColor(color)
                .setSecondaryToolbarColor(color)
                .setNavigationBarDividerColor(color)
                .build()
        )
        .build()
        .launchUrl(this, url.toUri())
}

fun Activity.openDialog(
    message: CharSequence,
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
    message: CharSequence,
    title: String,
    cancelable: Boolean = false,
    @StringRes negativeText: Int = android.R.string.cancel,
    negative: ((dialogInterface: DialogInterface) -> Unit)? = { it.dismiss() },
    positive: (dialogInterface: DialogInterface) -> Unit
): AlertDialog = openDialog(
    message,
    title,
    getString(android.R.string.ok),
    getString(negativeText),
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
): AlertDialog = openDialog(
    getString(message),
    getString(title),
    cancelable,
    negative = negative,
    positive = positive
)

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

fun Activity.openPreviewDialog(theme: ThemeDataClass): AlertDialog =
    openDialog(R.layout.preview_dialog, true) {
        findViewById<ImageView>(R.id.preview)?.setImageBitmap(theme.image)
    }

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
fun Activity.openInputDialogFlag(
    name: String,
    @StringRes hint: Int,
    value: String? = null,
    @StringRes negativeText: Int = android.R.string.cancel,
    negative: ((dialogInterface: DialogInterface) -> Unit) = { it.dismiss() },
    positive: (dialogInterface: DialogInterface, text: String) -> Unit
) = openDialog(R.layout.input_dialog_with_title, false) { dialog ->
    val flagnames = findViewById<TextView>(R.id.TextViewFlag)
    val input = findViewById<EditText>(R.id.editText)
    flagnames.text = name
    input.setHint(hint)
    if (value != null) input.setText(value)

    findViewById<Button>(R.id.ok)?.setOnClickListener {
        positive(dialog, (if (input.text.isNullOrEmpty()){
            input.error = getString(R.string.not_empty)
            return@setOnClickListener;
        } else{
            input?.text?.toString()
        }).toString()
        )
    }
    findViewById<Button>(R.id.cancel)?.let { button ->
        button.setOnClickListener { negative(dialog) }
        button.setText(negativeText)
    }
}

fun Activity.openLoadingDialog(@StringRes message: Int) =
    openDialog(R.layout.loading_dialog, false) {
        findViewById<TextView>(R.id.message).setText(message)
    }

fun Activity.openXMLEntryDialog(block: DialogInterface.(XMLEntry) -> Unit) =
    openDialog(R.layout.xml_entry_dialog_layout) { dialog ->
        val xmlNameInput = findViewById<TextInputLayout>(R.id.xmlName)
        val xmlValueInput = findViewById<TextInputLayout>(R.id.xmlValue)
        val xmlTypeInput = findViewById<TextInputLayout>(R.id.xmlType)

        val positiveButton = findViewById<MaterialButton>(R.id.positive)
        val negativeButton = findViewById<MaterialButton>(R.id.negative)

        fun validate(inputLayout: TextInputLayout) =
            if (inputLayout.value.isNullOrEmpty()) {
                inputLayout.error = getString(R.string.not_empty)
                false
            } else true

        fun getValues(inputLayouts: Map<String, TextInputLayout>): Map<String, String>? =
            if (inputLayouts.all { validate(it.value) }) inputLayouts.map {
                Pair(
                    it.key,
                    it.value.value ?: ""
                )
            }.toMap()
            else null

        positiveButton.setOnClickListener {
            getValues(
                mapOf(
                    "name" to xmlNameInput,
                    "value" to xmlValueInput,
                    "type" to xmlTypeInput
                )
            )?.let {
                block(
                    dialog,
                    XMLEntry(
                        it["name"] ?: "",
                        it["value"] ?: "",
                        XMLType.parseType(it["type"]) ?: XMLType.STRING
                    )
                )
                dialog.dismiss()
            }
        }

        negativeButton.setOnClickListener { dialog.dismiss() }
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