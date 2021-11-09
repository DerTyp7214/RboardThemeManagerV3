package de.dertyp7214.rboardthememanager.core

import android.os.Build
import com.dertyp7214.logs.helpers.Logger
import java.io.File

fun ProcessBuilder.su(vararg command: String): Process {
    Logger.log(Logger.Companion.Type.INFO, "[ProcessBuilder.su]", command.joinToString(", "))
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        redirectError(File("/dev/null"))
    }else {
        setOutputFile("/dev/null")
    }
    command("su", "--mount-master", "-c", command.joinToString("; "))
    return start()
}

private fun ProcessBuilder.setOutputFile(vararg command: String): Process {
    Logger.log(Logger.Companion.Type.INFO, "[ProcessBuilder.su]", command.joinToString(", "))
       command("su", "--mount-master", "-c", command.joinToString("; "))
    return start()
}