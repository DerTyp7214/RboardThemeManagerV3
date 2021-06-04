package de.dertyp7214.rboardthememanager.core

import java.net.URL

fun URL.getTextFromUrl(): String {
    return try {
        readText()
    } catch (e: Exception) {
        ""
    }
}