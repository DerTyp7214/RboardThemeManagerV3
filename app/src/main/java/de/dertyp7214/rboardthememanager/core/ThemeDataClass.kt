package de.dertyp7214.rboardthememanager.core

import android.content.Context
import com.dertyp7214.logs.helpers.Logger
import com.topjohnwu.superuser.io.SuFile
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.data.ThemeDataClass
import java.io.File

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
    return ThemeDataClass(image, name, newZip.absolutePath)
}

fun ThemeDataClass.install(overrideTheme: Boolean = true): Boolean {
    val file = File(path)
    val imageFile = File(file.absolutePath.removeSuffix(".zip"))
    val installPath = SuFile(Config.MAGISK_THEME_LOC, file.name)
    return if (installPath.exists() && !overrideTheme) true
    else arrayListOf(
        "mkdir -p ${Config.MAGISK_THEME_LOC}",
        "cp $path ${installPath.absolutePath}",
        "chmod 644 ${installPath.absolutePath}"
    ).apply {
        if (imageFile.exists()) addAll(
            listOf(
                "cp ${imageFile.absolutePath} ${installPath.absolutePath.removeSuffix(".zip")}",
                "chmod 644 ${installPath.absolutePath.removeSuffix(".zip")}"
            )
        )
    }.runAsCommand()
}

fun ThemeDataClass.isInstalled(): Boolean {
    val installedPath = SuFile(Config.MAGISK_THEME_LOC, File(path).name)
    return installedPath.exists()
}