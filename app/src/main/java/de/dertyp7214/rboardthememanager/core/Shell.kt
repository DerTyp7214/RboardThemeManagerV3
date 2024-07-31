package de.dertyp7214.rboardthememanager.core

import android.app.Activity
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.Shell.isAppGrantedRoot
import java.io.IOException

fun hasRoot(): Boolean {
    return try {
        isAppGrantedRoot() == true
    } catch (e: IOException) {
        false
    }
}

@Suppress("DEPRECATION", "UNUSED_PARAMETER")
fun hasRoot(activity: Activity): Boolean = try {
    Shell.cmd().exec()
    hasRoot()
} catch (_: Exception) {
    false
}