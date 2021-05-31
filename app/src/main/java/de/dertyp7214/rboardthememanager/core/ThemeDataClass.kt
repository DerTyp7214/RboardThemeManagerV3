package de.dertyp7214.rboardthememanager.core

import android.content.Context
import com.dertyp7214.logs.helpers.Logger
import com.topjohnwu.superuser.io.SuFile
import de.dertyp7214.rboardthememanager.data.ThemeDataClass

fun ThemeDataClass.delete(): Boolean {
    if (image != null) "rm -rf ${path.removeSuffix(".zip")}".runAsCommand { image ->
        Logger.log(Logger.Companion.Type.INFO, "DELETE THEME IMAGE", image.contentToString())
    }
    return "rm -rf $path".runAsCommand {
        Logger.log(Logger.Companion.Type.INFO, "DELETE THEME", it.contentToString())
    }
}

fun ThemeDataClass.moveToCache(context: Context): ThemeDataClass {
    val zip = SuFile(path)
    val newZip = SuFile(context.cacheDir, zip.name)
    val imageFile = SuFile(path.removeSuffix(".zip"))
    val newImage = SuFile(context.cacheDir, imageFile.name)
    zip.copy(newZip)
    if (imageFile.exists()) imageFile.copy(newImage)
    return ThemeDataClass(image, name, newZip.absolutePath, selected)
}