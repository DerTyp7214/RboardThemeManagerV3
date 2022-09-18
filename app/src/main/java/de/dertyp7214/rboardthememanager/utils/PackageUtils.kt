package de.dertyp7214.rboardthememanager.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import java.io.File

object PackageUtils {
    @SuppressLint("SdCardPath", "SetWorldReadable")
    fun install(
        context: FragmentActivity,
        file: File,
        resultLauncher: ActivityResultLauncher<Intent>? = null,
        error: () -> Unit = {}
    ) {
        try {
            if (file.exists()) {
                if (file.extension == "apk") {
                    val intent: Intent?
                    val downloadedApk = getFileUri(context, file)
                    val newFile = File(context.getExternalFilesDir(""), "updater" + "/update.apk")
                    when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                            intent = Intent(Intent.ACTION_VIEW).setDataAndType(
                                downloadedApk,
                                "application/vnd.android.package-archive"
                            )
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            context.startActivity(intent)
                        }
                        else -> {
                            intent = Intent(Intent.ACTION_VIEW).setDataAndType(
                                Uri.fromFile(newFile),
                                "application/vnd.android.package-archive"
                            );
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            context.startActivity(intent)
                        }
                    }
                } else error()
            } else error()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("install", e.message!!)
            error()
        }
    }

    fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun getAppVersionCode(packageName: String, packageManager: PackageManager): Long {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageManager.getPackageInfo(packageName, 0).longVersionCode
            } else {
                packageManager.getPackageInfo(packageName, 0).versionCode.toLong()

            }
        } catch (e: PackageManager.NameNotFoundException) {
            -1
        }
    }


    private fun getFileUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName, file
        )
    }
}