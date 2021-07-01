package de.dertyp7214.rboardthememanager.core

import com.dertyp7214.logs.helpers.Logger

fun Process.logs(tag: String = "Process", errorsOnly: Boolean = false): Process {
    errorStream.bufferedReader().readText().let { error ->
        Logger.log(
            Logger.Companion.Type.INFO,
            tag,
            "[Error]: $error"
        )
    }
    if (!errorsOnly) inputStream.bufferedReader().readText().let { error ->
        Logger.log(
            Logger.Companion.Type.INFO,
            tag,
            "[Response]: $error"
        )
    }
    return this
}