package de.dertyp7214.rboardthememanager.core

import android.content.Context
import android.net.Uri
import java.io.File

fun Uri.writeToFile(context: Context, file: File): Boolean {
    if (!file.exists()) file.createNewFile()
    return context.contentResolver.openInputStream(this)?.let {
        file.copyInputStreamToFile(it)
        it.close()
        true
    } ?: false
}