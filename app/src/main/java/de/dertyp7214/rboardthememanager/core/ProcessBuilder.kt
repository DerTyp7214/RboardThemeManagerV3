package de.dertyp7214.rboardthememanager.core

import com.dertyp7214.logs.helpers.Logger
import java.io.File

fun ProcessBuilder.su(vararg command: String): Process {
    Logger.log(Logger.Companion.Type.INFO, "[ProcessBuilder.su]", command.joinToString(", "))
    redirectError(File("/dev/null"))
    command("su", "--mount-master", "-c", command.joinToString(" && "))
    return start()
}