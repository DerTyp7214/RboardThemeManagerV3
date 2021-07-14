package de.dertyp7214.rboardthememanager.core

import com.dertyp7214.logs.helpers.Logger
import com.topjohnwu.superuser.Shell

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