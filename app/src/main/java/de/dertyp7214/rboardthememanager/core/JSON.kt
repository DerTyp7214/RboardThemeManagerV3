@file:Suppress("BlockingMethodInNonBlockingContext")

package de.dertyp7214.rboardthememanager.core

import org.json.JSONArray
import org.json.JSONObject

fun JSONObject.safeParse(string: String): JSONObject {
    return try {
        JSONObject(string)
    } catch (e: Exception) {
        this
    }
}

fun JSONArray.safeParse(string: String): JSONArray {
    return try {
        JSONArray(string)
    } catch (e: Exception) {
        this
    }
}

fun JSONArray.forEach(run: (o: Any, index: Int) -> Unit) {
    for (i in 0 until length()) run(get(i), i)
}

@Suppress("UNCHECKED_CAST")
fun <V> JSONArray.toList(): ArrayList<V> {
    val list = ArrayList<V>()
    forEach { o, _ -> list += o as V }
    return list
}

fun <V> JSONObject.getList(key: String): ArrayList<V> {
    return try {
        getJSONArray(key).toList()
    } catch (e: Exception) {
        ArrayList()
    }
}