package de.dertyp7214.rboardthememanager.core

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.topjohnwu.superuser.io.SuFile
import com.topjohnwu.superuser.io.SuFileInputStream
import com.topjohnwu.superuser.io.SuFileOutputStream
import de.dertyp7214.rboardthememanager.BuildConfig
import org.apache.commons.text.StringEscapeUtils
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets.UTF_8

fun SuFile.copy(newFile: File) = "\\cp $absolutePath ${newFile.absolutePath}".runAsCommand()

fun SuFile.copyRecursively(newFile: File): Boolean {
    if (!newFile.exists()) newFile.mkdirs()
    return "\\cp -a $absolutePath/. ${newFile.absolutePath}".runAsCommand()
}

@SuppressLint("SdCardPath")
fun SuFile.decodeBitmap(opts: BitmapFactory.Options? = null): Bitmap? {
    val pathName = absolutePath
    var bm: Bitmap? = null
    var stream: InputStream? = null
    try {
        stream = SuFileInputStream.open(pathName)
        bm = BitmapFactory.decodeStream(stream, null, opts)
    } catch (e: Exception) {
        Log.e("BitmapFactory", "Unable to decode stream: $e")
        try {
            val file = File("/data/data/${BuildConfig.APPLICATION_ID}")
            ProcessBuilder().su("cp $pathName ${file.absolutePath}").apply {
                errorStream.bufferedReader().readText()
                    .let { text -> Log.d("BitmapFactory", "Error: $text") }
            }
            stream = SuFileInputStream.open(file)
            bm = BitmapFactory.decodeStream(stream, null, opts)
        } catch (e: Exception) {
            Log.e("BitmapFactory", "Unable to decode stream: $e")
        }
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

fun SuFile.writeFile(content: String) {
    if (exists()) SuFileOutputStream.open(this).writer(UTF_8)
        .use { outputStreamWriter ->
            outputStreamWriter.write(content)
        }
    else ProcessBuilder().su("echo \"${StringEscapeUtils.escapeJava(content)}\" > '$absolutePath'")
        .logs("APPLY", true)
}

fun SuFile.openStream(): InputStream? {
    return if (exists()) SuFileInputStream.open(this)
    else ProcessBuilder().su("cat $absolutePath").logs("READ", true).inputStream
}