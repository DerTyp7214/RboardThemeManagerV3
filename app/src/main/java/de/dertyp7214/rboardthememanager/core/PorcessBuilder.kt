package de.dertyp7214.rboardthememanager.core

import android.os.Build
import com.dertyp7214.logs.helpers.Logger
import java.io.File

fun ProcessBuilder.su(vararg command: String): Process {
    Logger.log(Logger.Companion.Type.INFO, "[ProcessBuilder.su]", command.joinToString(", "))
    // Remove the Android Version check if old Android Versions are no longer supported on the Gboard side.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) redirectError(File("/dev/null"))
    command("su", "--mount-master", "-c", command.joinToString("; "))
    return start()
}