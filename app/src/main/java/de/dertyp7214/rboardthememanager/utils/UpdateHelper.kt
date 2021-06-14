@file:Suppress("SameParameterValue")

package de.dertyp7214.rboardthememanager.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import de.dertyp7214.rboardthememanager.BuildConfig
import java.io.File
import kotlin.math.roundToLong

class UpdateHelper(
    private val url: String,
    private val context: Context,
    private val path: String = getPath(context, "updater")
) {
    companion object {
        private fun getPath(context: Context, folder: String): String {
            return File(context.getExternalFilesDir("."), folder).absolutePath
        }
    }

    private val listeners = ArrayList<(progress: Long, bytes: Long, total: Long) -> Unit>()
    private var finishListener: (path: String, duration: Long) -> Unit = { _, _ -> }
    private var errorListener: (error: Error?) -> Unit = {}

    fun addOnProgressListener(listener: (progress: Long, bytes: Long, total: Long) -> Unit): UpdateHelper {
        listeners.add(listener)
        return this
    }

    fun setFinishListener(listener: (path: String, duration: Long) -> Unit): UpdateHelper {
        finishListener = listener
        return this
    }

    fun setErrorListener(listener: (error: Error?) -> Unit): UpdateHelper {
        errorListener = listener
        return this
    }

    fun start() {
        val startTime = System.currentTimeMillis()
        PRDownloader.download(url, path, "update.apk").build()
            .setOnStartOrResumeListener { }
            .setOnProgressListener { progress ->
                listeners.forEach {
                    val percentage =
                        ((progress.currentBytes.toFloat() / progress.totalBytes.toFloat()) * 100F).roundToLong()
                    it(percentage, progress.currentBytes, progress.totalBytes)
                }
            }
            .setOnCancelListener { }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    finishListener("$path/update.apk", System.currentTimeMillis() - startTime)
                    val toInstall: File = File(path, "$path/update.apk")
                    val intent: Intent
                    val apkUri = FileProvider.getUriForFile(
                        context,
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        toInstall
                    )
                    intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
                    intent.data = apkUri
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }

                override fun onError(error: Error?) {
                    errorListener(error)
                }
            })
    }
}