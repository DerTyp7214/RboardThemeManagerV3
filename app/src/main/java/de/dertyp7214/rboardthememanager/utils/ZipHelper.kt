package de.dertyp7214.rboardthememanager.utils

import com.dertyp7214.logs.helpers.Logger
import com.topjohnwu.superuser.io.SuFile
import com.topjohnwu.superuser.io.SuFileInputStream
import com.topjohnwu.superuser.io.SuFileOutputStream
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class ZipHelper {
    private val buffer = 80000
    fun zip(_files: List<String>, zipFileName: String) {
        try {
            var origin: BufferedInputStream?
            val dest = SuFileOutputStream.open(zipFileName)
            val out = ZipOutputStream(
                BufferedOutputStream(
                    dest
                )
            )
            val data = ByteArray(buffer)
            for (i in _files.indices) {
                val fi = SuFileInputStream.open(_files[i])
                origin = BufferedInputStream(fi, buffer)
                val entry =
                    ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1))
                out.putNextEntry(entry)
                var count: Int
                while (origin.read(data, 0, buffer).also { count = it } != -1) {
                    out.write(data, 0, count)
                }
                origin.close()
            }
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun unpackZip(path: String, zipName: String): Boolean {
        ZipFile(zipName).use { zip ->
            SuFile(path).mkdirs()
            zip.entries().asSequence().forEach { entry ->
                zip.getInputStream(entry).use { input ->
                    SuFileOutputStream.open(SuFile(path, entry.name)).use { output ->
                        Logger.log(
                            Logger.Companion.Type.DEBUG,
                            "OUTPUT",
                            SuFile(path, entry.name).absolutePath
                        )
                        input.copyTo(output)
                    }
                }
            }
        }
        return true
    }
}