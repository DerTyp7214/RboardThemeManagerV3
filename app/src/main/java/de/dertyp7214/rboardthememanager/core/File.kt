package de.dertyp7214.rboardthememanager.core

import android.app.Activity
import android.content.Intent
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import com.topjohnwu.superuser.io.SuFile
import com.topjohnwu.superuser.io.SuFileInputStream
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.data.ModuleMeta
import org.xml.sax.InputSource
import java.io.File
import java.io.InputStream
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
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

fun File.readXML(string: String? = null): Map<String, Any> {
    val output = HashMap<String, Any>()

    val fileName = absolutePath
    val content = string ?: SuFile(fileName).openStream()?.use {
        it.bufferedReader().readText()
    }

    val map = try {
        DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
            InputSource(StringReader(content))
        ).getElementsByTagName("map")
    } catch (e: Exception) {
        e.printStackTrace()
        return output
    }

    if (map.length > 0)
        for (item in map.item(0).childNodes) {
            if (item.nodeName != "set" && !item.nodeName.startsWith("#")) {
                val name = item.attributes?.getNamedItem("name")?.nodeValue
                val value = item.attributes?.getNamedItem("value")?.nodeValue?.let {
                    when (item.nodeName) {
                        "long" -> it.toLong()
                        "boolean" -> it.toBooleanStrict()
                        "float" -> it.toFloat()
                        "integer" -> it.toInt()
                        else -> it
                    }
                }
                if (name != null) output[name] = value ?: item.textContent ?: ""
            }
        }

    return output
}

fun File.share(
    activity: Activity,
    type: String = "*/*",
    action: String = Intent.ACTION_SEND,
    shareText: Int = R.string.share
) {
    val uri = FileProvider.getUriForFile(activity, activity.packageName, this)
    ShareCompat.IntentBuilder(activity)
        .setStream(uri)
        .setType(type)
        .intent.setAction(action)
        .setDataAndType(uri, type)
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION).apply {
            activity.startActivity(
                Intent.createChooser(
                    this,
                    activity.getString(shareText)
                )
            )
        }
}