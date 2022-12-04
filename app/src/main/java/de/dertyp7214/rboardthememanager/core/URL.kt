package de.dertyp7214.rboardthememanager.core

import java.net.HttpURLConnection
import java.net.URL

fun URL.getTextFromUrl(catch: () -> String = { "" }): String {
    return try {
        readText()
    } catch (e: Exception) {
        catch()
    }
}

fun URL.isReachable(): Boolean {
    return try {
        val responseCode = (openConnection() as HttpURLConnection).responseCode

        responseCode == 200 || responseCode == 301 || responseCode == 302 || responseCode == 303 || responseCode == 307 || responseCode == 308 || responseCode == 404 || responseCode == 405 || responseCode == 400
    } catch (e: Exception) {
        false
    }
}