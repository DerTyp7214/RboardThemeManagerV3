@file:Suppress("BlockingMethodInNonBlockingContext")

package de.dertyp7214.rboardthememanager.core

import org.json.JSONArray
import org.json.JSONObject

class SafeJSON(private val json: JSONObject) {
    fun getBoolean(name: String, defaultValue: Boolean = false): Boolean {
        return try {
            json.getBoolean(name)
        } catch (e: Exception) {
            defaultValue
        }
    }

    fun getString(name: String, defaultValue: String = ""): String {
        return try {
            json.getString(name)
        } catch (e: Exception) {
            defaultValue
        }
    }

    fun getLong(name: String, defaultValue: Long = 0): Long {
        return try {
            json.getLong(name)
        } catch (e: Exception) {
            defaultValue
        }
    }

    fun getInt(name: String, defaultValue: Int = 0): Int {
        return try {
            json.getInt(name)
        } catch (e: Exception) {
            defaultValue
        }
    }

    fun getJSONArray(name: String, defaultValue: JSONArray = JSONArray()): JSONArray {
        return try {
            json.getJSONArray(name)
        } catch (e: Exception) {
            defaultValue
        }
    }

    fun getJSONObject(name: String, defaultValue: JSONObject = JSONObject()): JSONObject {
        return try {
            json.getJSONObject(name)
        } catch (e: Exception) {
            defaultValue
        }
    }

    operator fun get(name: String, defaultValue: Any = ""): Any {
        return try {
            json.get(name)
        } catch (e: Exception) {
            defaultValue
        }
    }

    override fun toString(): String {
        return try {
            json.toString(2)
        } catch (e: Exception) {
            ""
        }
    }
}

fun JSONObject.safeParse(string: String): SafeJSON {
    return try {
        SafeJSON(JSONObject(string))
    } catch (e: Exception) {
        SafeJSON(this)
    }
}

fun JSONArray.safeParse(string: String): JSONArray {
    return try {
        JSONArray(string)
    } catch (e: Exception) {
        this
    }
}

inline fun <reified E> JSONArray.toList(): List<E> {
    return map<Any, E> { it as E }
}

inline fun <reified E> JSONArray.forEach(run: (o: E, index: Int) -> Unit) {
    for (i in 0 until length()) if (get(i) is E) run(get(i) as E, i)
}

inline fun <reified E, B> JSONArray.map(run: (o: E) -> B): List<B> {
    val list = arrayListOf<B>()
    forEach<E> { o, _ -> list.add(run(o)) }
    return list
}