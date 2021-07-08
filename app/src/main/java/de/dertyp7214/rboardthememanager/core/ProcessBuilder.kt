package de.dertyp7214.rboardthememanager.core

import java.io.File

fun ProcessBuilder.su(command: String): Process {
    redirectError(File("/dev/null"))
    command("su", "--mount-master", "-c", command)
    return start()
}