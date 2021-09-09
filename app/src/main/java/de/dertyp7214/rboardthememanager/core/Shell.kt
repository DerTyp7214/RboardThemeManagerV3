package de.dertyp7214.rboardthememanager.core

import com.topjohnwu.superuser.Shell.rootAccess
import java.io.IOException

fun hasRoot(): Boolean {
    return try {
        /*ProcessBuilder().run {
            command("su")
            start()
        }.inputStream.let {
            Log.d("Check Root", it.bufferedReader().readText())
        }*/
        rootAccess()
    }catch (e: IOException) {
        false
    }
}