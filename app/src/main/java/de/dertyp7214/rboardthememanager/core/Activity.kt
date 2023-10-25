package de.dertyp7214.rboardthememanager.core

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.RenderEffect
import android.graphics.Shader
import android.net.Uri
import android.os.Build
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
        .launchUrl(this, Uri.parse(url))
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
    if (enableBlur) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        content.setRenderEffect(
            RenderEffect.createBlurEffect(
                10F,
                10F,
                Shader.TileMode.REPEAT
            )
        )
    }
    return MaterialAlertDialogBuilder(this)
        .setCancelable(cancelable)
        .setMessage(message)
        .setTitle(title)
        .setPositiveButton(positiveText) { dialogInterface, _ -> positive(dialogInterface) }
        .setOnDismissListener {
            if (enableBlur) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                content.setRenderEffect(null)
            }
        }
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
fun Activity.openInputDialog(
    @StringRes hint: Int,
    value: String? = null,
    @StringRes negativeText: Int = android.R.string.cancel,
    negative: ((dialogInterface: DialogInterface) -> Unit) = { it.dismiss() },
    positive: (dialogInterface: DialogInterface, text: String) -> Unit
) = openDialog(R.layout.input_dialog, false) { dialog ->
    val input = findViewById<EditText>(R.id.editText)
    input.setHint(hint)
    if (value != null) input.setText(value)

    findViewById<Button>(R.id.ok)?.setOnClickListener {
        positive(dialog, input?.text?.toString() ?: "")
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
fun Activity.applyTheme(
    main: Boolean = false,
    info: Boolean = false,
    shareFlags: Boolean = false,
    installPack: Boolean = false
) {
    val style = preferences.getString("app_style", "default_style")
    setTheme(
        when (style) {
            "apocyan_style" -> when {
                main -> R.style.Theme_RboardThemeManagerV3_Main_apocyan
                info -> R.style.Theme_RboardThemeManagerV3_Info_apocyan
                shareFlags -> R.style.Theme_RboardThemeManagerV3_ShareFlags_apocyan
                installPack -> R.style.Theme_RboardThemeManagerV3_InstallPack_apocyan
                else -> R.style.Theme_RboardThemeManagerV3_apocyan
            }
            "blue_style" -> when {
                main -> R.style.Theme_RboardThemeManagerV3_Main_Blue
                info -> R.style.Theme_RboardThemeManagerV3_Info_Blue
                shareFlags -> R.style.Theme_RboardThemeManagerV3_ShareFlags_Blue
                installPack -> R.style.Theme_RboardThemeManagerV3_InstallPack_Blue
                else -> R.style.Theme_RboardThemeManagerV3_Blue
            }
            "brown_blue_style" -> when {
                main -> R.style.Theme_RboardThemeManagerV3_Main_brown_blue
                info -> R.style.Theme_RboardThemeManagerV3_Info_brown_blue
                shareFlags -> R.style.Theme_RboardThemeManagerV3_ShareFlags_brown_blue
                installPack -> R.style.Theme_RboardThemeManagerV3_InstallPack_brown_blue
                else -> R.style.Theme_RboardThemeManagerV3_brown_blue
            }
            "green_style" -> when {
                main -> R.style.Theme_RboardThemeManagerV3_Main_Green
                info -> R.style.Theme_RboardThemeManagerV3_Info_Green
                shareFlags -> R.style.Theme_RboardThemeManagerV3_ShareFlags_Green
                installPack -> R.style.Theme_RboardThemeManagerV3_InstallPack_Green
                else -> R.style.Theme_RboardThemeManagerV3_Green
            }
            "green_brown_style" -> when {
                main -> R.style.Theme_RboardThemeManagerV3_Main_green_brown
                info -> R.style.Theme_RboardThemeManagerV3_Info_green_brown
                shareFlags -> R.style.Theme_RboardThemeManagerV3_ShareFlags_green_brown
                installPack -> R.style.Theme_RboardThemeManagerV3_InstallPack_green_brown
                else -> R.style.Theme_RboardThemeManagerV3_green_brown
            }
            "lavender_tonic_style" -> when {
                main -> R.style.Theme_RboardThemeManagerV3_Main_lavender_tonic
                info -> R.style.Theme_RboardThemeManagerV3_Info_lavender_tonic
                shareFlags -> R.style.Theme_RboardThemeManagerV3_ShareFlags_lavender_tonic
                installPack -> R.style.Theme_RboardThemeManagerV3_InstallPack_lavender_tonic
                else -> R.style.Theme_RboardThemeManagerV3_lavender_tonic
            }
            "lime_style" -> when {
                main -> R.style.Theme_RboardThemeManagerV3_Main_Lime
                info -> R.style.Theme_RboardThemeManagerV3_Info_Lime
                shareFlags -> R.style.Theme_RboardThemeManagerV3_ShareFlags_Lime
                installPack -> R.style.Theme_RboardThemeManagerV3_InstallPack_Lime
                else -> R.style.Theme_RboardThemeManagerV3_Lime
            }
            "monochrome_style" -> when {
                main -> R.style.Theme_RboardThemeManagerV3_Main_monochrome
                info -> R.style.Theme_RboardThemeManagerV3_Info_monochrome
                shareFlags -> R.style.Theme_RboardThemeManagerV3_ShareFlags_monochrome
                installPack -> R.style.Theme_RboardThemeManagerV3_InstallPack_monochrome
                else -> R.style.Theme_RboardThemeManagerV3_monochrome
            }
            "orange_style" -> when {
                main -> R.style.Theme_RboardThemeManagerV3_Main_Orange
                info -> R.style.Theme_RboardThemeManagerV3_Info_Orange
                shareFlags -> R.style.Theme_RboardThemeManagerV3_ShareFlags_Orange
                installPack -> R.style.Theme_RboardThemeManagerV3_InstallPack_Orange
                else -> R.style.Theme_RboardThemeManagerV3_Orange
            }
            "pink_style" -> when {
                main -> R.style.Theme_RboardThemeManagerV3_Main_Pink
                info -> R.style.Theme_RboardThemeManagerV3_Info_Pink
                shareFlags -> R.style.Theme_RboardThemeManagerV3_ShareFlags_Pink
                installPack -> R.style.Theme_RboardThemeManagerV3_InstallPack_Pink
                else -> R.style.Theme_RboardThemeManagerV3_Pink
            }
            "peach_pearl_style" -> when {
                main -> R.style.Theme_RboardThemeManagerV3_Main_peach_pearl
                info -> R.style.Theme_RboardThemeManagerV3_Info_peach_pearl
                shareFlags -> R.style.Theme_RboardThemeManagerV3_ShareFlags_peach_pearl
                installPack -> R.style.Theme_RboardThemeManagerV3_InstallPack_peach_pearl
                else -> R.style.Theme_RboardThemeManagerV3_peach_pearl
            }
            "red_style" -> when {
                main -> R.style.Theme_RboardThemeManagerV3_Main_Red
                info -> R.style.Theme_RboardThemeManagerV3_Info_Red
                shareFlags -> R.style.Theme_RboardThemeManagerV3_ShareFlags_Red
                installPack -> R.style.Theme_RboardThemeManagerV3_InstallPack_Red
                else -> R.style.Theme_RboardThemeManagerV3_Red
            }
            "samoan_sun_style" -> when {
                main -> R.style.Theme_RboardThemeManagerV3_Main_samoan_sun
                info -> R.style.Theme_RboardThemeManagerV3_Info_samoan_sun
                shareFlags -> R.style.Theme_RboardThemeManagerV3_ShareFlags_samoan_sun
                installPack -> R.style.Theme_RboardThemeManagerV3_InstallPack_samoan_sun
                else -> R.style.Theme_RboardThemeManagerV3_samoan_sun
            }
            "yellow_style" -> when {
                main -> R.style.Theme_RboardThemeManagerV3_Main_Yellow
                info -> R.style.Theme_RboardThemeManagerV3_Info_Yellow
                shareFlags -> R.style.Theme_RboardThemeManagerV3_ShareFlags_Yellow
                installPack -> R.style.Theme_RboardThemeManagerV3_InstallPack_Yellow
                else -> R.style.Theme_RboardThemeManagerV3_Yellow
            }
            "yellow_blue_style" -> when {
                main -> R.style.Theme_RboardThemeManagerV3_Main_yellow_blue
                info -> R.style.Theme_RboardThemeManagerV3_Info_yellow_blue
                shareFlags -> R.style.Theme_RboardThemeManagerV3_ShareFlags_yellow_blue
                installPack -> R.style.Theme_RboardThemeManagerV3_InstallPack_yellow_blue
                else -> R.style.Theme_RboardThemeManagerV3_yellow_blue
            }
            else -> when {
                main -> R.style.Theme_RboardThemeManagerV3_Main
                info -> R.style.Theme_RboardThemeManagerV3_Info
                shareFlags -> R.style.Theme_RboardThemeManagerV3_ShareFlags
                installPack -> R.style.Theme_RboardThemeManagerV3_InstallPack
                else -> R.style.Theme_RboardThemeManagerV3
            }
        }
    )
}
fun Activity.openDialog(
    @LayoutRes layout: Int,
    cancelable: Boolean = true,
    block: View.(DialogInterface) -> Unit
): AlertDialog {
    if (enableBlur) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        content.setRenderEffect(
            RenderEffect.createBlurEffect(
                10F,
                10F,
                Shader.TileMode.REPEAT
            )
        )
    }
    val view = layoutInflater.inflate(layout, null)
    return MaterialAlertDialogBuilder(this)
        .setCancelable(cancelable)
        .setView(view)
        .setOnDismissListener {
            if (enableBlur) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                content.setRenderEffect(null)
            }
        }
        .create().also { dialog ->
            block(view, dialog)
            dialog.show()
        }
}