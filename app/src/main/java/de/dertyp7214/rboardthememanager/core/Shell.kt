package de.dertyp7214.rboardthememanager.core

import com.topjohnwu.superuser.Shell.isAppGrantedRoot
import java.io.IOException

fun hasRoot(): Boolean {
    return try {
        isAppGrantedRoot() == true
    }catch (e: IOException) {
        false
    }
}