package de.dertyp7214.rboardthememanager.core

import android.app.Activity
import android.os.Build
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.data.SoundPack
import de.dertyp7214.rboardthememanager.utils.ZipHelper
import java.io.File

fun SoundPack.download(activity: Activity, result: (sounds: List<String>) -> Unit) {
    val dialog = activity.openLoadingDialog(R.string.downloading_pack)
    val name = title.replace(" ", "_")
    PRDownloader.download(url,if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){activity.cacheDir.absolutePath}else{
        activity.externalCacheDir?.absolutePath
    }, "$name.zip").build()
        .setOnStartOrResumeListener { }
        .setOnCancelListener { }
        .setOnProgressListener { }
        .start(object : OnDownloadListener {
            override fun onDownloadComplete() {
                val pack = File(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){activity.cacheDir}else{
                    activity.externalCacheDir
                }, "$name.zip")
                val destination = File(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){activity.cacheDir}else{
                    activity.externalCacheDir
                }, name)
                dialog.dismiss()
                if (ZipHelper().unpackZip(destination.absolutePath, pack.absolutePath))
                    result(destination.listFiles()?.map { it.absolutePath } ?: listOf())
                else result(listOf())
            }

            override fun onError(error: Error?) {
                dialog.dismiss()
                result(listOf())
            }
        })
}