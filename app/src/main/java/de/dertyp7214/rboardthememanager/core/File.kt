package de.dertyp7214.rboardthememanager.core

import com.topjohnwu.superuser.io.SuFile
import com.topjohnwu.superuser.io.SuFileInputStream
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