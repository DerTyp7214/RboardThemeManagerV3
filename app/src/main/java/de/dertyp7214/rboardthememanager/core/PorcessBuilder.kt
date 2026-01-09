package de.dertyp7214.rboardthememanager.core

import com.dertyp7214.logs.helpers.Logger
import java.io.File
import java.io.OutputStreamWriter

fun ProcessBuilder.su(vararg command: String): Process {
    Logger.log(Logger.Companion.Type.INFO, "[ProcessBuilder.su]", command.joinToString(", "))
    redirectError(File("/dev/null"))

    // Start su without -c to avoid ARG_MAX overflow
    command("su", "--mount-master")
    val proc = start()

    // Write commands through stdin instead of argv
    OutputStreamWriter(proc.outputStream).use { w ->
        command.forEach { w.appendLine(it) }
        w.appendLine("exit")
    }

    return proc
}
