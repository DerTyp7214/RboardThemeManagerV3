package de.dertyp7214.rboardthememanager.utils

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_META_DATA
import android.content.pm.PackageManager.NameNotFoundException
import com.dertyp7214.logs.helpers.Logger
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
}