package de.dertyp7214.rboardthememanager.utils

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_META_DATA
import android.os.Build
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.dertyp7214.logs.helpers.Logger
import com.topjohnwu.superuser.io.SuFile
import de.dertyp7214.rboardthememanager.Application
import de.dertyp7214.rboardthememanager.Config.GBOARD_PACKAGE_NAME
import de.dertyp7214.rboardthememanager.core.openStream
import de.dertyp7214.rboardthememanager.core.readXML
import de.dertyp7214.rboardthememanager.preferences.Flags

object GboardUtils {
    @Suppress("DEPRECATION")
    fun getGboardVersion(context: Context): String {
        return try {
            if (Build.VERSION.SDK_INT >= 33) {
                context.packageManager.getPackageInfo(
                    GBOARD_PACKAGE_NAME, PackageManager.PackageInfoFlags.of(
                        GET_META_DATA.toLong()
                    )
                ).versionName
            } else {
                context.packageManager.getPackageInfo(
                    GBOARD_PACKAGE_NAME,
                    GET_META_DATA
                ).versionName
            }
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

    @Suppress("unused")
    fun loadBackupFlags(callback: (String) -> Unit) {
        Application.context?.let { context ->
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val currentGboardVersion = getGboardVersionCode(context)
            val lastGboardVersion = preferences.getLong("gboardVersion", currentGboardVersion)
            if (lastGboardVersion < currentGboardVersion)
                preferences.getString("flags", null)?.let(callback)
        }
    }

    fun flagsChanged(callback: (String) -> Unit) {
        Application.context?.let { context ->
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val oldFlags = preferences.getString("flags", null)
            val newFlags = SuFile(Flags.FILES.FLAGS.filePath).openStream()?.use {
                it.bufferedReader().readText()
            }
            if (oldFlags != null && newFlags != null && oldFlags.readXML() != newFlags.readXML())
                callback(newFlags)
        }
    }
}