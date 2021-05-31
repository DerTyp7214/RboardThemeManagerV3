package de.dertyp7214.rboardthememanager.utils

import android.content.Context
import android.os.Environment
import java.io.File

object FileUtils {
    fun getThemePacksPath(context: Context): File {
        return File(
            context.getExternalFilesDirs(Environment.DIRECTORY_NOTIFICATIONS)[0].absolutePath.removeSuffix(
                "Notifications"
            ), "ThemePacks"
        ).apply { if (!exists()) mkdirs() }
    }

    fun getSoundPacksPath(context: Context): File {
        return File(
            context.getExternalFilesDirs(Environment.DIRECTORY_NOTIFICATIONS)[0].absolutePath.removeSuffix(
                "Notifications"
            ), "SoundPacks"
        ).apply { if (!exists()) mkdirs() }
    }

    fun getResourceId(context: Context, variableName: String, resourceName: String, packageName: String): Int {
        return try {
            context.resources.getIdentifier(variableName, resourceName, packageName)
        } catch (e: Exception) {
            -1
        }
    }
}