package de.dertyp7214.rboardthememanager.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import java.io.File

object PackageUtils {
    fun install(
        context: FragmentActivity,
        file: File,
        resultLauncher: ActivityResultLauncher<Intent>? = null,
        error: () -> Unit = {}
    ) {
        try {
            if (file.exists()) {
                if (file.extension == "apk") {
                    val downloadedApk = getFileUri(context, file)
                    val intent = Intent(Intent.ACTION_VIEW).setDataAndType(
                        downloadedApk,
                        "application/vnd.android.package-archive"
                    )
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    resultLauncher?.launch(intent) ?: context.startActivity(intent)
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

    private fun getFileUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName, file
        )
    }
}