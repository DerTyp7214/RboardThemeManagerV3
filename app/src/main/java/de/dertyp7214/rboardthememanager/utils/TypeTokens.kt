package de.dertyp7214.rboardthememanager.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object TypeTokens {
    operator fun <T> invoke(list: Boolean = false): Type =
        if (list) object : TypeToken<List<T>>() {}.type else object : TypeToken<T>() {}.type
}

fun <T> Gson.fromJsonList(json: String): List<T> = fromJson(json, TypeTokens<T>(true))