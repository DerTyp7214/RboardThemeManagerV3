package de.dertyp7214.rboardthememanager.core

import com.dertyp7214.logs.helpers.Logger

fun log(type: Logger.Companion.Type, tag: String, body: Any?) {
    try {
        Logger.log(type, tag, body, Logger.context)
    } catch (_: Exception) {
    }
}