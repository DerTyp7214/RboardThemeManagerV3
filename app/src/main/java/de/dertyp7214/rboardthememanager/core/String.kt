package de.dertyp7214.rboardthememanager.core

import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import com.dertyp7214.logs.helpers.Logger
import com.google.gson.Gson
import com.topjohnwu.superuser.Shell
import de.dertyp7214.rboardthememanager.Application
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.data.RboardRepo
import de.dertyp7214.rboardthememanager.data.RboardRepoMeta
import de.dertyp7214.rboardthememanager.utils.MagiskUtils
import org.xml.sax.InputSource
import java.io.IOException
import java.io.StringReader
import java.net.URL
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

typealias RepoUrl = String

fun RepoUrl.parseRepo(): RboardRepo {
    val url = let {
        when {
            contains(Config.GITHUB_REPO_PREFIX) -> replace(
                Config.GITHUB_REPO_PREFIX,
                Config.REPO_PREFIX
            )

            contains(Config.GITLAB_REPO_PREFIX) -> replace(
                Config.GITLAB_REPO_PREFIX,
                Config.REPO_PREFIX
            )

            else -> this
        }
    }.removePrefix("true:").removePrefix("false:")
    val meta = try {
        Gson().fromJson(
            URL(url.replace("list.json", "meta.json")).readText(),
            RboardRepoMeta::class.java
        )
    } catch (e: IOException) {
        null
    } catch (e: Exception) {
        null
    }
    return RboardRepo(url, startsWith("true:"), meta)
}

val String.escapePath: String
    get() = replace("(", "\\(")
        .replace(")", "\\)")

fun String.fontSize(relative: Float): CharSequence {
    return SpannableString(this).apply {
        setSpan(
            RelativeSizeSpan(relative),
            0,
            length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
    }
}

@Suppress("DEPRECATION")
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

fun String.getSystemProperty(): String {
    return try {
        Runtime.getRuntime().exec("getprop $this").inputStream.bufferedReader().readLine()
    } catch (e: Exception) {
        ""
    }
}

fun String.setSystemProperty(value: String = "", saveToModule: Boolean = false): Boolean {
    if (saveToModule) {
        val files = mapOf(
            Pair(
                "system.prop",
                "$this=$value"
            )
        )
        MagiskUtils.updateModule(Config.MODULE_META, files)
    }
    return "resetprop ${if (value.isEmpty()) "--delete " else ""}$this $value".runAsCommand()
}

@Suppress("DEPRECATION")
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

operator fun String.times(other: Number): String = repeat(other.toInt())