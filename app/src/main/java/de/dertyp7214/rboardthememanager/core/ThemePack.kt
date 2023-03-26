package de.dertyp7214.rboardthememanager.core

import androidx.fragment.app.FragmentActivity
import com.dertyp7214.logs.helpers.Logger
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.topjohnwu.superuser.io.SuFile
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.data.ThemeDataClass
import de.dertyp7214.rboardthememanager.data.ThemePack
import de.dertyp7214.rboardthememanager.utils.ZipHelper
import java.io.File

fun ThemePack.download(activity: FragmentActivity, result: (themes: List<String>) -> Unit) {
    val dialog = activity.openLoadingDialog(R.string.downloading_pack)
    val name = name.replace(" ", "_")
    Logger.log(Logger.Companion.Type.DEBUG, "Download", "Downloading $url ${url.let {
        if (it.startsWith("http")) it else "${Config.RAW_PREFIX}/$it"
    }} -> $name")
    PRDownloader.download(url.let {
        if (it.startsWith("http")) it else "${Config.RAW_PREFIX}/$it"
    }, activity.cacheDir.absolutePath, "$name.pack").build()
        .setOnStartOrResumeListener { }
        .setOnCancelListener { }
        .setOnProgressListener { }
        .start(object : OnDownloadListener {
            override fun onDownloadComplete() {
                val pack = File(activity.cacheDir, "$name.pack")

                if (this@download.hash != null && pack.hash() != this@download.hash) {
                    dialog.dismiss()
                    result(listOf())

                    activity.openDialog(R.string.error, R.string.hash_mismatch, negative = null) {
                        it.dismiss()
                    }

                    return
                }

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

operator fun List<ThemePack>.get(theme: ThemeDataClass) =
    find { it.themes?.contains(theme.name) == true }