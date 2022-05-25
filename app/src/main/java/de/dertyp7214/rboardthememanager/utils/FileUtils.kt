package de.dertyp7214.rboardthememanager.utils

import android.annotation.SuppressLint
import android.content.Context

object FileUtils {
    @SuppressLint("DiscouragedApi")
    fun getResourceId(
        context: Context,
        variableName: String,
        resourceName: String,
        packageName: String
    ): Int {
        return try {
            if (variableName.isBlank()) -1
            else context.resources.getIdentifier(variableName, resourceName, packageName)
        } catch (e: Exception) {
            -1
        }
    }
}