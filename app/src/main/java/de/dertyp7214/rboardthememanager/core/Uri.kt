package de.dertyp7214.rboardthememanager.core

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

@SuppressLint("Recycle", "Range")
fun Uri.getFileName(activity: Activity): String {
    var result: String? = null
    if (scheme == "content") {
        val cursor = activity.contentResolver.query(this, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        } finally {
            cursor!!.close()
        }
    }
    if (result == null) {
        result = path
        val cut = result!!.lastIndexOf('/')
        if (cut != -1) {
            result = result.substring(cut + 1)
        }
    }
    return result
}

fun Uri.writeToFile(context: Context, file: File): Boolean {
    if (!file.exists()) file.createNewFile()
    return context.contentResolver.openInputStream(this)?.let {
        file.copyInputStreamToFile(it)
        true
    } ?: false
}