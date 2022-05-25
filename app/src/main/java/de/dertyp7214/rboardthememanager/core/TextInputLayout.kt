package de.dertyp7214.rboardthememanager.core

import com.google.android.material.textfield.TextInputLayout

val TextInputLayout.value: String?
    get() = try {
        editText?.text?.toString()
    } catch (_: Exception) {
        null
    }