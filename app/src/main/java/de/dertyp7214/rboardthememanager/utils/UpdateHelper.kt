@file:Suppress("SameParameterValue")

package de.dertyp7214.rboardthememanager.utils

import android.content.Context
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import java.io.File
import kotlin.math.roundToLong

class UpdateHelper(
    private val url: String,
    private val context: Context,
    private val path: String = getPath(context, "updater")
) {
    companion object {
        private fun getPath(context: Context, folder: String): String {
            return File(context.filesDir, folder).absolutePath
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
                }

                override fun onError(error: Error?) {
                    errorListener(error)
                }
            })
    }
}