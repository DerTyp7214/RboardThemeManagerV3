package de.dertyp7214.rboardthememanager.core

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.topjohnwu.superuser.io.SuFile
import com.topjohnwu.superuser.io.SuFileInputStream
import com.topjohnwu.superuser.io.SuFileOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

fun SuFile.copy(newFile: File): Boolean {
    return "\\cp $absolutePath ${newFile.absolutePath}".runAsCommand()
}

fun SuFile.copyRecursively(newFile: File): Boolean {
    if (!newFile.exists()) newFile.mkdirs()
    return "\\cp -a $absolutePath/. ${newFile.absolutePath}".runAsCommand()
}

fun SuFile.decodeBitmap(opts: BitmapFactory.Options? = null): Bitmap? {
    val pathName = absolutePath
    var bm: Bitmap? = null
    var stream: InputStream? = null
    try {
        stream = SuFileInputStream.open(pathName)
        bm = BitmapFactory.decodeStream(stream, null, opts)
    } catch (e: Exception) {
        /*  do nothing.
                If the exception happened on open, bm will be null.
            */
        Log.e("BitmapFactory", "Unable to decode stream: $e")
    } finally {
        if (stream != null) {
            try {
                stream.close()
            } catch (e: IOException) {
                // do nothing here
            }
        }
    }
    return bm
}

fun SuFile.tar(zip: File): Boolean {
    return listOf("cd $absolutePath", "tar -cf ${zip.absolutePath} .").runAsCommand()
}

fun SuFile.writeFile(content: String) {
    SuFileOutputStream.open(this).writer(Charset.defaultCharset())
        .use { outputStreamWriter ->
            outputStreamWriter.write(content)
        }
}
