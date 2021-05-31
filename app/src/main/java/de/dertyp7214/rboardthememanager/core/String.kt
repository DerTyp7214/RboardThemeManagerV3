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