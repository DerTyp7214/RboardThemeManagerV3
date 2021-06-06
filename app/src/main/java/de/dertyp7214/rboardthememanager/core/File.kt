package de.dertyp7214.rboardthememanager.core

import android.widget.ImageView
import com.topjohnwu.superuser.io.SuFile
import com.topjohnwu.superuser.io.SuFileInputStream
import de.dertyp7214.rboardthememanager.data.ModuleMeta
import de.dertyp7214.rboardthememanager.utils.doAsync
import java.io.File
import java.io.InputStream
import kotlin.text.Charsets.UTF_8

fun File.parseModuleMeta(): ModuleMeta {
    val text = SuFileInputStream.open(this).readBytes().toString(UTF_8)
    val map = HashMap<String, Any>()
    text.split("\n").forEach {
        val line = it.split("=")
        if (line.size > 1) map[line[0]] = line[1]
    }
    return ModuleMeta(
        (map["id"] ?: "") as String,
        (map["name"] ?: "") as String,
        (map["version"] ?: "") as String,
        (map["versionCode"] ?: "") as String,
        (map["author"] ?: "") as String,
        (map["description"] ?: "") as String,
        map
    )
}

fun File.copyInputStreamToFile(inputStream: InputStream) {
    this.outputStream().use { fileOut ->
        inputStream.copyTo(fileOut)
    }
}