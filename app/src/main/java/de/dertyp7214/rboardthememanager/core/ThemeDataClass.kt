package de.dertyp7214.rboardthememanager.core

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import com.dertyp7214.logs.helpers.Logger
import com.google.gson.Gson
import com.topjohnwu.superuser.io.SuFile
import de.dertyp7214.rboard.CssFile
import de.dertyp7214.rboard.ImageFile
import de.dertyp7214.rboard.RboardTheme
import de.dertyp7214.rboard.ThemeMetadata
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.data.ThemeDataClass
import java.io.File
import java.util.zip.ZipFile

fun ThemeDataClass.delete(): Boolean {
    if (image != null) "rm -rf \"${path.removeSuffix(".zip")}\"".runAsCommand { image ->
        Logger.log(Logger.Companion.Type.INFO, "DELETE THEME IMAGE", image.contentToString())
    }
    return "rm -rf \"$path\"".runAsCommand {
        Logger.log(Logger.Companion.Type.INFO, "DELETE THEME", it.contentToString())
    }
}

fun ThemeDataClass.moveToCache(context: Context): ThemeDataClass {
    val zip = SuFile(path)
    val newZip = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        SuFile(context.cacheDir, zip.name)
    } else {
        SuFile(context.externalCacheDir, zip.name)
    }
    val imageFile = SuFile(path.removeSuffix(".zip"))
    val newImage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        SuFile(context.cacheDir, imageFile.name)
    } else {
        SuFile(context.externalCacheDir, imageFile.name)
    }
    zip.copy(newZip)
    if (imageFile.exists()) imageFile.copy(newImage)
    return ThemeDataClass(image, name, newZip.absolutePath)
}

fun ThemeDataClass.install(overrideTheme: Boolean = true, recycle: Boolean = false): Boolean {
    val file = SuFile(path)
    val imageFile = SuFile(file.absolutePath.removeSuffix(".zip"))
    val installPath = SuFile(Config.MAGISK_THEME_LOC, file.name)
    if (recycle) image?.recycle()
    return if (installPath.exists() && !overrideTheme) true
    else arrayListOf(
        "mkdir -p ${Config.MAGISK_THEME_LOC}",
        "cp \"$path\" \"${installPath.absolutePath}\"",
        "chmod 644 \"${installPath.absolutePath}\""
    ).apply {
        if (imageFile.exists()) addAll(
            listOf(
                "cp \"${imageFile.absolutePath}\" \"${installPath.absolutePath.removeSuffix(".zip")}\"",
                "chmod 644 \"${installPath.absolutePath.removeSuffix(".zip")}\""
            )
        )
    }.runAsCommand()
}

fun ThemeDataClass.toRboardTheme(context: Context): RboardTheme {
    val tmpPath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        File(context.cacheDir, "rboard")
    } else {
        File(context.externalCacheDir, "rboard")
    }
    if (!tmpPath.exists()) tmpPath.mkdirs()

    listOf(
        "cp \"$path\" \"${tmpPath.absolutePath}\"",
        "chmod 777 \"${tmpPath.absolutePath}\"",
    ).runAsCommand()

    val tmpFilePath = File(tmpPath, File(path).name)

    val zip = ZipFile(tmpFilePath)
    val metadata = Gson().fromJson(
        zip.getInputStream(zip.getEntry("metadata.json")).use { it.bufferedReader().readText() },
        ThemeMetadata::class.java
    )
    val cssFiles = metadata.styleSheets.map { Pair(it, zip.getInputStream(zip.getEntry(it))) }
        .map { CssFile(it.first, it.second.use { stream -> stream.bufferedReader().readText() }) }
        .let { cssFiles ->
            val arrayList: ArrayList<CssFile> = ArrayList(cssFiles)
            if (metadata.flavors.isNotEmpty()) {
                arrayList.addAll(metadata.flavors.flatMap { flavor ->
                    flavor.styleSheets.map { Pair(it, zip.getInputStream(zip.getEntry(it))) }
                        .map {
                            CssFile(
                                it.first,
                                it.second.use { stream -> stream.bufferedReader().readText() })
                        }
                })
            }
            arrayList
        }
    val imageFiles = zip.entries().toList().filter { it.name.endsWith(".png") }
    val images =
        if (imageFiles.isNotEmpty()) imageFiles.map { Pair(it.name, zip.getInputStream(it)) }
            .map {
                ImageFile(
                    it.first,
                    it.second.use { stream -> BitmapFactory.decodeStream(stream) })
            } else listOf()

    zip.close()

    if (!tmpFilePath.deleteRecursively())
        "rm -rf \"${tmpFilePath.absolutePath}\"".runAsCommand()

    return RboardTheme(
        name,
        metadata,
        image,
        cssFiles,
        images
    )
}

fun ThemeDataClass.isInstalled() = SuFile(Config.MAGISK_THEME_LOC, File(path).name).exists()
fun ThemeDataClass.getLocalTime(): Long =
    if (isInstalled) SuFile(
        Config.MAGISK_THEME_LOC,
        File(path).name
    ).lastModified() else Long.MAX_VALUE