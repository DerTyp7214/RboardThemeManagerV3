package de.dertyp7214.rboardthememanager.core

import java.lang.Double.parseDouble

fun Any.isNumber(): Boolean {
    return try {
        parseDouble(this as String)
        true
    } catch (e: Exception) {
        false
    }
}

fun Any.equalsNumber(second: Any?): Boolean {
    return try {
        parseDouble(this as String) == parseDouble(second as String)
    } catch (e: Exception) {
        false
    }
}