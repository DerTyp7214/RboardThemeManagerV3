package de.dertyp7214.rboardthememanager.core

import android.graphics.Bitmap
import com.topjohnwu.superuser.io.SuFile
import com.topjohnwu.superuser.io.SuFileOutputStream
import de.dertyp7214.rboard.RboardTheme
import de.dertyp7214.rboardthememanager.Config
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

fun RboardTheme.install(overrideTheme: Boolean = true, recycle: Boolean = false): Boolean {

    "mkdir -p ${Config.MAGISK_THEME_LOC}".runAsCommand()

    val installPath =
        SuFile(Config.MAGISK_THEME_LOC, "${name.replace(" ", "_").removeSuffix(".zip")}.zip")
    val imageFile = SuFile(installPath.absolutePath.removeSuffix(".zip"))

    val zipOutputStream = ZipOutputStream(SuFileOutputStream.open(installPath))

    zipOutputStream.putNextEntry(ZipEntry("metadata.json"))
    zipOutputStream.write(metadata.toString().toByteArray())

    css.forEach {
        zipOutputStream.putNextEntry(ZipEntry(it.name))
        zipOutputStream.write(it.content.toByteArray())
    }

    images.forEach {
        zipOutputStream.putNextEntry(ZipEntry(it.name))
        val stream = ByteArrayOutputStream()
        it.content.compress(Bitmap.CompressFormat.PNG, 100, stream)
        zipOutputStream.write(stream.toByteArray())
    }

    zipOutputStream.close()

    if (preview != null) {
        SuFileOutputStream.open(imageFile).use { output ->
            preview?.compress(Bitmap.CompressFormat.PNG, 100, output)
        }
    }

    return arrayListOf(
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