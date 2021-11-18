package de.dertyp7214.rboardthememanager.core

import com.dertyp7214.logs.helpers.Logger
import com.topjohnwu.superuser.Shell
import de.dertyp7214.rboardthememanager.Application
import de.dertyp7214.rboardthememanager.R
import org.xml.sax.InputSource
import java.io.StringReader
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

fun String.runAsCommand(callback: (result: Array<String>) -> Unit = {}): Boolean {
    return Shell.su(this).exec().apply {
        if (err.size > 0) Logger.log(
            Logger.Companion.Type.ERROR, "RUN COMMAND",
            err.toTypedArray().apply { callback(this) }.contentToString()
        )
        if (out.size > 0) Logger.log(
            Logger.Companion.Type.DEBUG, "RUN COMMAND",
            out.toTypedArray().apply { callback(this) }.contentToString()
        )
    }.isSuccess.apply {
        Logger.log(Logger.Companion.Type.INFO, "RUN COMMAND", "${this@runAsCommand} -> $this")
    }
}
fun String.capitalize() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }


fun String.booleanOrNull(): Boolean? {
    return if (this == "true" || this == "false") toBoolean() else null
}

fun String.getSystemProperty(): String {
    return try {
        Runtime.getRuntime().exec("getprop $this").inputStream.bufferedReader().readLine()
    } catch (e: Exception) {
        ""
    }
}
fun String.setSystemProperty(value: String = "") =
    "resetprop ${if (value.isEmpty()) "--delete " else ""}$this $value".runAsCommand()


fun List<String>.runAsCommand(callback: (result: Array<String>) -> Unit = {}): Boolean {
    return Shell.su(*this.toTypedArray()).exec().apply {
        if (err.size > 0) Logger.log(
            Logger.Companion.Type.ERROR, "RUN COMMAND",
            err.toTypedArray().apply { callback(this) }.contentToString()
        )
        if (out.size > 0) Logger.log(
            Logger.Companion.Type.DEBUG, "RUN COMMAND",
            out.toTypedArray().apply { callback(this) }.contentToString()
        )
    }.isSuccess.apply {
        Logger.log(Logger.Companion.Type.INFO, "RUN COMMAND", "${this@runAsCommand} -> $this")
    }
}


fun <T> String.setXmlValue(value: T, key: String): String {
    return this.let { fileText ->
        val type = when (value) {
            is Boolean -> "boolean"
            is Int -> "integer"
            is Long -> "long"
            is Float -> "float"
            else -> "string"
        }
        Logger.log(
            Logger.Companion.Type.DEBUG,
            "CHANGE FLAG",
            "value: $value key: $key type: $type"
        )
        if (type != "string") {
            when {
                "<$type name=\"$key\"" in fileText -> fileText.replace(
                    """<$type name="$key" value=".*" />""".toRegex(),
                    """<$type name="$key" value="$value" />"""
                )
                Regex("<map( |)/>") in fileText -> fileText.replace(
                    Regex("<map( |)/>"),
                    """<map><$type name="$key" value="$value" /></map>"""
                )
                else -> fileText.replace(
                    "<map>",
                    """<map><$type name="$key" value="$value" />"""
                )
            }
        } else {
            when {
                "<$type name\"$key\"" in fileText -> fileText.replace(
                    """<$type name="$key">.*</$type>""".toRegex(),
                    """<$type name="$key">$value</$type>"""
                )
                Regex("<map( |)/>") in fileText -> fileText.replace(
                    Regex("<map( |)/>"),
                    """<map><$type name="$key">$value</$type></map>"""
                )
                else -> fileText.replace(
                    "<map>",
                    """<map><$type name="$key">$value</$type>"""
                )
            }
        }
    }
}

fun String.readXML(): Map<String, Any> {
    val output = HashMap<String, Any>()

    val map = try {
        DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
            InputSource(StringReader(this))
        ).getElementsByTagName("map")
    } catch (e: Exception) {
        e.printStackTrace()
        return output
    }

    try {
        if (map.length > 0)
            for (item in map.item(0).childNodes) {
                if (item.nodeName != "set" && !item.nodeName.startsWith("#")) {
                    val name = item.attributes?.getNamedItem("name")?.nodeValue
                    val value = item.attributes?.getNamedItem("value")?.nodeValue?.let {
                        when (item.nodeName) {
                            "long" -> it.toLong()
                            "boolean" -> it.toBooleanStrict()
                            "float" -> it.toFloat()
                            "double" -> it.toDouble()
                            "integer", "int" -> it.toInt()
                            else -> it
                        }
                    }
                    if (name != null) output[name] = value ?: item.textContent ?: ""
                } else if (item.nodeName == "set") {
                    val name = item.attributes?.getNamedItem("name")?.nodeValue
                    val value = item.childNodes?.toList()?.filter { !it.nodeName.startsWith("#") }
                        ?.map {
                            if (it.nodeName == "string") "<string>${it.textContent}</string>" else "<${it.nodeName} value=\"${
                                item.attributes?.getNamedItem("value")?.nodeValue
                            }\" />"
                        }?.toSet() ?: setOf()
                    if (name != null) output[name] = value
                }
            }
    } catch (e: Exception) {
        e.printStackTrace()
        Application.getTopActivity()?.openDialog(R.string.try_fix_flags, R.string.flags_corrupted) {
            it.dismiss()
        }
    }

    return output
}