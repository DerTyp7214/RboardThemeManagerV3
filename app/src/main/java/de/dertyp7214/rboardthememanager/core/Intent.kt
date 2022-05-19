package de.dertyp7214.rboardthememanager.core

import android.content.Intent
import android.os.Build

@Suppress("UNCHECKED_CAST", "DEPRECATION")
inline fun <reified T> Intent.getArrayExtra(name: String): Array<T> {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            getSerializableExtra(name, Array<T>::class.java) ?: arrayOf()
        else getSerializableExtra(name) as Array<T>
    } catch (e: Exception) {
        arrayOf()
    }
}

fun Intent.putExtra(name: String, value: Map<*, *>) = putExtra(name, value.toList().toTypedArray())
fun <K, V> Intent.getMapExtra(name: String): Map<K, V> = getArrayExtra<Pair<K, V>>(name).toMap()