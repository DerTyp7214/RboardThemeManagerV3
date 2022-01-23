package de.dertyp7214.rboardthememanager.utils

import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object TypeTokens {
    inline operator fun <reified T> invoke(): Type = object : TypeToken<T>() {}.type
}