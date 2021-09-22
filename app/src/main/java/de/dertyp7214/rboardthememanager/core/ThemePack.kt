package de.dertyp7214.rboardthememanager.core

import android.app.Activity
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.topjohnwu.superuser.io.SuFile
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.data.ThemePack
import de.dertyp7214.rboardthememanager.utils.ZipHelper
import java.io.File

fun ThemePack.download(activity: Activity, result: (themes: List<String>) -> Unit) {
    val dialog = activity.openLoadingDialog(R.string.downloading_pack)
    val name = name.replace(" ", "_")
    PRDownloader.download(url, activity.cacheDir.absolutePath, "$name.pack").build()
        .setOnStartOrResumeListener { }
        .setOnCancelListener { }
        .setOnProgressListener { }
        .start(object : OnDownloadListener {
            override fun onDownloadComplete() {
                val pack = File(activity.cacheDir, "$name.pack")
                val destination = File(activity.cacheDir, name)
                dialog.dismiss()
                if (ZipHelper().unpackZip(destination.absolutePath, pack.absolutePath)) {
                    SuFile(destination, "pack.meta").writeFile(
                        "name=$name\nauthor=$author\n"
                    )
                    result(destination.listFiles { file -> file.extension == "zip" }
                        ?.map { it.absolutePath } ?: listOf())
                } else result(listOf())
            }

            override fun onError(error: Error?) {
                dialog.dismiss()
                result(listOf())
            }
        })
}