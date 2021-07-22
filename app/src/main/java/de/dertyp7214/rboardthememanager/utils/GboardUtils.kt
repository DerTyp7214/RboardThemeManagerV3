package de.dertyp7214.rboardthememanager.utils

import android.content.Context
import android.content.pm.PackageManager.GET_META_DATA
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.dertyp7214.logs.helpers.Logger
import de.dertyp7214.rboardthememanager.Application
import de.dertyp7214.rboardthememanager.Config.GBOARD_PACKAGE_NAME

object GboardUtils {
    fun getGboardVersion(context: Context): String {
        return try {
            context.packageManager.getPackageInfo(GBOARD_PACKAGE_NAME, GET_META_DATA).versionName
        } catch (error: Exception) {
            Logger.log(
                Logger.Companion.Type.ERROR,
                "GboardVersion",
                "$GBOARD_PACKAGE_NAME: ${error.stackTraceToString()}"
            )
            ""
        }
    }

    fun getGboardVersionCode(context: Context): Long {
        return PackageUtils.getAppVersionCode(
            GBOARD_PACKAGE_NAME,
            context.packageManager
        )
    }

    fun updateCurrentFlags(flags: String) {
        Application.context?.let { context ->
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val currentGboardVersion = getGboardVersionCode(context)
            preferences.edit {
                putLong("gboardVersion", currentGboardVersion)
                putString("flags", flags)
            }
        }
    }

    fun loadBackupFlags(callback: (String) -> Unit) {
        Application.context?.let { context ->
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val currentGboardVersion = getGboardVersionCode(context)
            val lastGboardVersion = preferences.getLong("gboardVersion", currentGboardVersion)
            if (lastGboardVersion < currentGboardVersion)
                preferences.getString("flags", null)?.let(callback)
        }
    }
}