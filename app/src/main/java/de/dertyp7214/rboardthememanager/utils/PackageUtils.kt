package de.dertyp7214.rboardthememanager.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.runAsCommand
import java.io.File

object PackageUtils {
    @SuppressLint("SdCardPath")
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
                    when {
                        Build.VERSION.SDK_INT > Build.VERSION_CODES.O -> {
                            intent = Intent(Intent.ACTION_VIEW).setDataAndType(
                                downloadedApk,
                                "application/vnd.android.package-archive"
                            )
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        else -> {
                            intent = null
                            if ("chmod 775 ${"/data/user/0/de.dertyp7214.rboardthememanager.debug/files/updater"}".runAsCommand() && "chmod 775 ${file.absolutePath}".runAsCommand() && "pm install ${file.absolutePath}".runAsCommand() || " pm install -r ${file.absolutePath}".runAsCommand()) Toast.makeText(
                                context,
                                R.string.app_updated,
                                Toast.LENGTH_SHORT
                            ).show()
                            else error()
                        }
                    }
                    intent?.let {
                        resultLauncher?.launch(it) ?: context.startActivity(it)
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

    fun getAppVersionCode(packageName: String, packageManager: PackageManager): Long{
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